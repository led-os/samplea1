package com.jiubang.ggheart.common.controler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.SingleThreadProxy;
import com.go.util.file.media.ThumbnailManager;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.EmailInputDialog;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.common.bussiness.LockInfoBussiness;
import com.jiubang.ggheart.common.bussiness.LockInfoBussiness.LockInfo;
import com.jiubang.ggheart.common.controler.AppInvokeMonitor.MonitorTaskObserver;
import com.jiubang.ggheart.common.password.PasswordActivity;
import com.jiubang.ggheart.common.password.PasswordActivity.ActionResultCallBack;
import com.jiubang.ggheart.common.password.PasswordInputActivity;
import com.jiubang.ggheart.common.password.PasswordInputCoverActivity;
import com.jiubang.ggheart.common.password.SetupPasswordActivity;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.Controler;
import com.jiubang.ggheart.data.info.AppItemInfo;

/**
 * 这玩意要负责，启动解锁界面，判断付费跳转，找回密码，功能模块锁开关，设置密码
 * @author wuziyi
 *
 */
public class InvokeLockControler extends Controler implements MonitorTaskObserver {
	private static InvokeLockControler sSelfObject;
//	private SparseArray<ActionResultCallBack> mResultListners;
	private ActionResultCallBack mResultListner;
	//锁的信息
	private LockInfoBussiness mLockInfoBussiness;
	
	private static final int MESSAGE_COUNT = 1;
	private int mCurrentCount = 0;
	private CountDownListener mCountListener;
	private IconInfo mIconInfo;
	
	public static final int MAX_ERROR_TIME = 5;
	public static final int COOL_DOWN_TIME = 30;
	
	public static final int ACTION_ID_APPLOCK = 1;
	public static final int ACTION_ID_LOCKHIDEAPP = 2;
	public final static int ACTION_ID_RESTORDB = 3;
	/**
	 * 第一套密码
	 */
	public static final int PASSWORD_SET_ONE = 0;
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_COUNT :
					mCurrentCount --;
					if (null != mCountListener) {
						mCountListener.updateCount(mCurrentCount);
					}
					if (mCurrentCount > 0) {
						sendEmptyMessageDelayed(MESSAGE_COUNT, 1000);
					}
					break;
			}
		};
	};
	
	private InvokeLockControler(Context context) {
		super(context);
		mLockInfoBussiness = new LockInfoBussiness(context);
		mIconInfo = new IconInfo();
//		mResultListners = new SparseArray<ActionResultCallBack>();
	}
	
	public synchronized static InvokeLockControler getInstance(Context context) {
		if (sSelfObject == null) {
			sSelfObject = new InvokeLockControler(context);
		}
		return sSelfObject;
	}
	
	public void startLockAction(int actionId, ActionResultCallBack callback, Context context, Bitmap topIcon, String title) {
		startLockAction(actionId, callback, context, topIcon, -1, title, false);
	}
	
	public void startLockAction(int actionId, ActionResultCallBack callback, Context context, int resouceID, String title) {
		startLockAction(actionId, callback, context, null, resouceID, title, false);
	}
	/**
	 * 该方法会在引起解锁界面出现的地方调用(使用默认的图标启动解锁界面)
	 * @param actionId 该id会在回调的方法中返回
	 * @param callback
	 */
	public void startLockAction(int actionId, ActionResultCallBack callback, Context context, String title) {
		startLockAction(actionId, callback, context, null, -1, title, false);
	}
	
	/**
	 * 该方法会在引起解锁界面出现的地方调用
	 * @param actionId 该id会在回调的方法中返回
	 * @param callback
	 */
	public void startLockAction(final int actionId, ActionResultCallBack callback,
			final Context context, Bitmap topIcon, int resouceId, String title,
			boolean needWallPaper) {
		Context appContext = context.getApplicationContext();
		if (FunctionPurchaseManager.getInstance(appContext)
				.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_SECURITY) == FunctionPurchaseManager.STATE_CAN_USE) {
			mResultListner = callback;
			if (getPassWord() != null && getEmail() != null) {
				Intent intent;
				// 进入解锁界面
				if (needWallPaper) {
					// 这玩意是singleInstance，跟下面那个是不同档次的
					intent = new Intent(appContext, PasswordInputCoverActivity.class);
				} else {
					// 改用Activity方式启动解锁界面
					intent = new Intent(appContext, PasswordInputActivity.class);
				}
				intent.putExtra(PasswordInputActivity.ACTION_ID, actionId);
				//				intent.putExtra(PasswordInputActivity.EXTRA_TOP_ICON_BITMAP, topIcon);
				//				intent.putExtra(PasswordInputActivity.EXTRA_TOP_ICON_RESID, resouceId);
				//				intent.putExtra(PasswordInputActivity.EXTRA_TOP_ICON_TEXT, title);
				mIconInfo.mIcon = topIcon;
				mIconInfo.mResourceId = resouceId;
				mIconInfo.mTitle = title;
				Log.i("wuziyi", "startLockAction:" + mIconInfo);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} else {
				if (null == getPassWord()) {
					// 设置密码和邮箱
					setupEmailAndPassword(actionId, context, callback);
				} else if (null == getEmail()) {
					// 有些扯淡的人，设了密码之后，不设置邮箱，这里必须再要他设置
					setupEmail(context, actionId, callback);
				} else {
					throw new IllegalAccessError("Is any conditions error?");
				}
			}
		} else {
			if (actionId == ACTION_ID_APPLOCK) {
				if (!FunctionPurchaseManager.getInstance(mContext).showItemExpiredNotPayPaye("14",
						FunctionPurchaseManager.UNPAY_TIP_LOCKAPP)) {
					callback.onUnlockSuccess(actionId);
				}
			} else if (actionId == ACTION_ID_LOCKHIDEAPP) {
				callback.onUnlockSuccess(actionId);
				FunctionPurchaseManager.getInstance(mContext).showItemExpiredNotPayPaye("14",
						FunctionPurchaseManager.UNPAY_TIP_LOCKHIDEAPP);
			} else if (actionId == ACTION_ID_RESTORDB) {
				callback.onUnlockSuccess(actionId);
				FunctionPurchaseManager.getInstance(mContext).showItemExpiredNotPayPaye("14",
						FunctionPurchaseManager.UNPAY_TIP_BACKUP);
			} else {
				callback.onUnlockSuccess(actionId);
			}
		}

	}
	
	public IconInfo getIconInfo() {
		return mIconInfo;
	}
	
	/**
	 * 弹出邮箱设置，不需要结果回调
	 * @param context
	 */
	public void setupEmail(Context context) {
		setupEmail(context, -1, null);
	}
	
	/**
	 * 弹出邮箱设置
	 * @param context
	 */
	public void setupEmail(final Context context, final int actionId, final ActionResultCallBack callback) {
		final EmailInputDialog editTextDialog = new EmailInputDialog(context);
		editTextDialog.show();
		editTextDialog.setDefaultText(getEmail());
		editTextDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editTextDialog.isEmailAddress()) {
					setEmail(PASSWORD_SET_ONE, editTextDialog.getEditText());
					DeskToast.makeText(context, R.string.lockpattern_email_setup_ok, Toast.LENGTH_SHORT).show();
					if (callback != null) {
						callback.onUnlockSuccess(actionId);
					}
					editTextDialog.dismiss();
				} else {
					editTextDialog.showErrorSummary();
				}
			}
		});
		editTextDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (callback != null) {
					callback.onUnlockFail(actionId);
				}
				editTextDialog.dismiss();
			}
		});
		editTextDialog.setTitle(R.string.lockpattern_set_email_title);
		editTextDialog.setMessage(R.string.lockpattern_email_dialog_message);
	}

	/**
	 * 先弹出密码设置，后弹出邮箱设置
	 * @param actionId
	 * @param context
	 * @param callback
	 */
	public void setupEmailAndPassword(final int actionId, final Context context, final ActionResultCallBack callback) {
		if (getPassWord() == null) {
			setupPassWord(actionId, new ActionResultCallBack() {
				
				@Override
				public void onUnlockSuccess(final int actionId) {
					setupEmail(context, actionId, callback);
				}
				
				@Override
				public void onUnlockFail(int actionId) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	}
	
	public void setConutDownListener(CountDownListener listener) {
		mCountListener = listener;
	}
	
	public void startCountDown() {
		mHandler.sendEmptyMessage(MESSAGE_COUNT);
	}
	
	public int getCountNumber() {
		return mCurrentCount;
	}

	public void setCountNumber(int number) {
		mCurrentCount = number;
	}
	
	/**
	 * 由于启动一个层没办法带参数，解锁成功失败的观察者只有这样拿出去了
	 * @return
	 */
	public ActionResultCallBack getResultListner() {
		return mResultListner;
	}
	
	/**
	 * 设置密码(加密后本地保存)
	 * @param password
	 */
	public boolean setupPassWord(int actionId, ActionResultCallBack callback) {
		mResultListner = callback;
		// 改用Activity方式启动解锁界面
		Intent intent = new Intent(mContext, SetupPasswordActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(PasswordActivity.ACTION_ID, actionId);
		mContext.startActivity(intent);
		return true;
	}
	
	public void setEmail(int keyId, String email) {
		LockInfo info = mLockInfoBussiness.getLockInfo(keyId);
		if (info != null) {
			mLockInfoBussiness.updataEmail(keyId, email);
		} else {
			LockInfo lockInfo = new LockInfo();
			lockInfo.setEmail(email);
			mLockInfoBussiness.addLockInfo(keyId, lockInfo);
		}
	}
	
	public void setPassWord(int keyId, String password) {
		LockInfo info = mLockInfoBussiness.getLockInfo(keyId);
		if (info != null) {
			mLockInfoBussiness.updataPassword(keyId, password);
		} else {
			LockInfo lockInfo = new LockInfo();
			lockInfo.setPasswrod(password);
			mLockInfoBussiness.addLockInfo(keyId, lockInfo);
		}
	}
	
	/**
	 * 获取密码
	 * @return
	 */
	public String getPassWord() {
		return mLockInfoBussiness.getPassword(PASSWORD_SET_ONE);
	}
	
	/**
	 * 
	 * @author zouguiquan
	 *
	 */
	public static interface CountDownListener {
		public void updateCount(int number);
	}

	/**
	 * 获取密码
	 * @return
	 */
	public String getEmail() {
		return mLockInfoBussiness.getEmail(PASSWORD_SET_ONE);
	}
	/**
	 *  删除密码
	 * @return
	 */
	public void delPassword() {
		mLockInfoBussiness.delPassword(PASSWORD_SET_ONE);
	}
	/**
	 * 删除邮箱
	 * @return
	 */
	public void delEmail() {
		 mLockInfoBussiness.delEmail(PASSWORD_SET_ONE);
	}
	
	public final static String FIND_PASSWORD_URL = "http://configbak.goforandroid.com/configbak/entrance";

	public void sendData(final String email, final String password) {
		SingleThreadProxy.postRunable(new Runnable() {
			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("funid", "4"));
				params.add(new BasicNameValuePair("email", email));
				params.add(new BasicNameValuePair("pass", password));
				params.add(new BasicNameValuePair("lang", language(mContext)));
				try {
					HttpPost hp = new HttpPost(FIND_PASSWORD_URL);
					hp.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse httpResponse = new DefaultHttpClient().execute(hp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 获取语言和国家地区的方法 格式: SIM卡方式：cn 系统语言方式：zh-CN
	 * 
	 * @return
	 */
	private static String language(Context context) {

		String ret = null;

		try {
			TelephonyManager telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telManager != null) {
				ret = telManager.getSimCountryIso();
				if (ret != null && !ret.equals("")) {
					ret = String.format("%s_%s", Locale.getDefault().getLanguage().toLowerCase(),
							ret.toLowerCase());
				}
			}
		} catch (Throwable e) {
			// e.printStackTrace();
		}

		if (ret == null || ret.equals("")) {
			ret = String.format("%s_%s", Locale.getDefault().getLanguage().toLowerCase(), Locale
					.getDefault().getCountry().toLowerCase());
		}
		return null == ret ? "error" : ret;
	}

	private String mIgnorePkg = mContext.getPackageName();
	public void ignorePkg(Intent launchIntent) {
		if (launchIntent != null) {
			mIgnorePkg = launchIntent.getComponent().getPackageName();
		} else {
			mIgnorePkg = mContext.getPackageName();
		}
	}

	@Override
	public void onRunningTasksChanged(String lastPkg, final String curTopPkg) {
//		final String curTopPkg = curTopCn.getPackageName();
		if (curTopPkg.equals(mIgnorePkg)) {
			
		} else {
			final CommonControler controler = CommonControler.getInstance(mContext);
			final AppItemInfo launchInfo = controler.getLockAppInfo(curTopPkg);
			mIgnorePkg = curTopPkg;
			if (launchInfo != null) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						startLockAction(0, new ActionResultCallBack() {
							
							@Override
							public void onUnlockSuccess(int actionId) {
								mIgnorePkg = curTopPkg;
//								startAppByPkg(curTopPkg);
							}
							
							@Override
							public void onUnlockFail(int actionId) {
								startAppByPkg(mContext.getPackageName());
//								controler.stopMonitor();
							}
						}, mContext, ThumbnailManager.getInstance(mContext).getParcelableBitmap(launchInfo.mIcon.getBitmap()), -1, launchInfo.mTitle, true);
						
					}
				});
			}
		}
	}
	
	/**
	 * <br>功能简述:开启应用
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param packageName
	 */
	private void startAppByPkg(String packageName) {
//		PackageManager packageMgr = mContext.getPackageManager();
//
//		Intent launchIntent = packageMgr.getLaunchIntentForPackage(packageName);
//		if (null != launchIntent) {
//			try {
//				launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(launchIntent);
//			} catch (ActivityNotFoundException e) {
//				e.printStackTrace();
//			}
//		} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，这个地方最重要，关于解释，自己google吧
				intent.addCategory(Intent.CATEGORY_HOME);
				try {
					mContext.startActivity(intent);
				} catch (Throwable e) {
					e.printStackTrace();
				}
//		}
	}
	
	/**
	 * 解锁界面顶部图标文字info
	 * @author wuziyi
	 *
	 */
	public class IconInfo {
		protected Bitmap mIcon;
		protected String mTitle;
		protected int mResourceId;
		
		public Bitmap getIcon() {
			return mIcon;
		}
		public void setIcon(Bitmap icon) {
			mIcon = icon;
		}
		public String getTitle() {
			return mTitle;
		}
		public void setTitle(String title) {
			mTitle = title;
		}
		public int getResourceId() {
			return mResourceId;
		}
		public void setResourceId(int resourceId) {
			mResourceId = resourceId;
		}
		
		
	}

}
