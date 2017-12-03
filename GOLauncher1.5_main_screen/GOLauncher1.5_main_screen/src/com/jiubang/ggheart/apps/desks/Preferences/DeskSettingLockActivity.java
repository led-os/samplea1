package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.appfunc.LockAppActivity;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.common.controler.InvokeLockControler;
import com.jiubang.ggheart.common.password.PasswordActivity.ActionResultCallBack;
import com.jiubang.ggheart.data.info.DeskLockSettingInfo;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * <br>
 * 类描述:桌面设置-安全锁Activity <br>
 * 功能详细描述:
 * 
 * @author zhujian
 */
public class DeskSettingLockActivity extends DeskSettingBaseActivity {

	/**
	 * 应用锁
	 */
	private DeskSettingItemCheckBoxView mSettingAppLocked;

	/**
	 * 选择应用程序
	 */
	private DeskSettingItemBaseView mSettingSelectApp;

	/**
	 * 锁定隐藏程序
	 */
	private DeskSettingItemCheckBoxView mSettingLockedHideApp;

	/**
	 * 锁定恢复备份与恢复默认以及备份整合为一项
	 */
	private DeskSettingItemCheckBoxView mSettingBak;

	/**
	 * 锁定恢复默认
	 */
	//private DeskSettingItemCheckBoxView mSettingDefault;

	/**
	 *  修改邮箱
	 */
	private DeskSettingItemBaseView mSettingChangeEmail;

	/**
	 * 修改密码
	 */
	private DeskSettingItemBaseView mSettingChangePassword;
	
	/**
	 * 付费广告条
	 */
	private View mSettingPrimeBanner;
	
	/**
	 * 付费广告条容器
	 */
	private View mSettingPrimeBannerContainer;

	private DeskLockSettingInfo mDeskLockSettingInfo;

	public final static int APPLOCKED = 0;

	public final static int HIDEAPPLOCKED = 1;

	public final static int RESTORESETTING = 2;

	public final static int RESTOREDEFAULT = 3;

	public final static int SELECTAPP = 4;

	public final static int CHANGEEMAIL = 5;

	public final static int CHANGEPASSWORD = 6;

	private long mLastClickTime; // 最后一次点击时间
	private static final long CLICK_TIME = 400; // 每次点击间隔时间
	/**
	 * 是否点击了付费广告条
	 */
	private boolean mIsClickPrimeBanner = false;
	
	private int mEntranceid = 13;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_lock);
		mEntranceid = getIntent().getIntExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, 13);
		mDeskLockSettingInfo = SettingProxy.getDeskLockSettingInfo();
		initViews();
		load();
	}

	/**
	 * <br>
	 * 功能简述:初始化View <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void initViews() {
		mSettingAppLocked = (DeskSettingItemCheckBoxView) findViewById(R.id.app_lock);
		mSettingAppLocked.setOnValueChangeListener(this);

		mSettingSelectApp = (DeskSettingItemBaseView) findViewById(R.id.select_apps);
		mSettingSelectApp.setOnClickListener(this);

		mSettingLockedHideApp = (DeskSettingItemCheckBoxView) findViewById(R.id.hide_app_locked);
		mSettingLockedHideApp.setOnValueChangeListener(this);

		mSettingBak = (DeskSettingItemCheckBoxView) findViewById(R.id.lock_restore_settings);
		mSettingBak.setOnValueChangeListener(this);

//		mSettingDefault = (DeskSettingItemCheckBoxView) findViewById(R.id.lock_restore_default);
//		mSettingDefault.setOnValueChangeListener(this);

		mSettingChangeEmail = (DeskSettingItemBaseView) findViewById(R.id.change_email);
		mSettingChangeEmail.setOnClickListener(this);

		mSettingChangePassword = (DeskSettingItemBaseView) findViewById(R.id.change_password);
		mSettingChangePassword.setOnClickListener(this);
		
		mSettingPrimeBanner = findViewById(R.id.setting_prime_banner);
		mSettingPrimeBanner.setOnClickListener(this);
		
		mSettingPrimeBannerContainer = findViewById(R.id.setting_prime_banner_container);
		mSettingPrimeBannerContainer.setOnClickListener(this);
	}

	@Override
	public void load() {
		super.load();
		if (FunctionPurchaseManager.getInstance(getApplicationContext())
				.getPayFunctionState(
						FunctionPurchaseManager.PURCHASE_ITEM_SECURITY) == FunctionPurchaseManager.STATE_CAN_USE) {
			mSettingPrimeBannerContainer.setVisibility(View.GONE);
			View scrollView = findViewById(R.id.desk_setting_lock_scrollview);
			LayoutParams params = (LayoutParams) scrollView.getLayoutParams();
			params.topMargin = 0;
			scrollView.setLayoutParams(params);
			
			if (mDeskLockSettingInfo != null) {
				mSettingAppLocked.setIsCheck(mDeskLockSettingInfo.mAppLock);
				mSettingLockedHideApp
						.setIsCheck(mDeskLockSettingInfo.mLockHideApp);
				if (mDeskLockSettingInfo.mRestoreSetting
						|| mDeskLockSettingInfo.mRestoreDefault) {
					mSettingBak.setIsCheck(true);
				} else {
					mSettingBak.setIsCheck(false);
				}
				// mSettingDefault.setIsCheck(mDeskLockSettingInfo.mRestoreDefault);
				if (!mDeskLockSettingInfo.mAppLock) {
					mSettingSelectApp.setEnabled(mDeskLockSettingInfo.mAppLock);
				}
			}
		} else {
			mSettingPrimeBannerContainer.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void save() {
		super.save();
		if (mDeskLockSettingInfo != null) {
			boolean isChange = false;
			if (mDeskLockSettingInfo.mAppLock != mSettingAppLocked.getIsCheck()) {
				mDeskLockSettingInfo.mAppLock = mSettingAppLocked.getIsCheck();
				isChange = true;
				CommonControler controler = CommonControler.getInstance(getApplicationContext());
				if (mDeskLockSettingInfo.mAppLock) {
					controler.startLockAppMonitor();
				} else {
					controler.stopLockAppMonitor();
				}
			}

			if (mDeskLockSettingInfo.mLockHideApp != mSettingLockedHideApp.getIsCheck()) {
				mDeskLockSettingInfo.mLockHideApp = mSettingLockedHideApp.getIsCheck();
				isChange = true;
			}

			if (mDeskLockSettingInfo.mRestoreSetting != mSettingBak.getIsCheck()) {
				mDeskLockSettingInfo.mRestoreSetting = mSettingBak.getIsCheck();
				isChange = true;
			}

			if (mDeskLockSettingInfo.mRestoreDefault != mSettingBak.getIsCheck()) {
				mDeskLockSettingInfo.mRestoreDefault = mSettingBak.getIsCheck();
				isChange = true;
			}

			if (isChange) {
				SettingProxy.updateDesLockSettingInfo(mDeskLockSettingInfo);
			}
		}
	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
		if (baseView == mSettingAppLocked) {
			if (mSettingAppLocked.getIsCheck()) {
				mSettingSelectApp.setEnabled(true);
			} else {
				mSettingSelectApp.setEnabled(false);
			}

						}
//		else if (baseView == mSettingLockedHideApp) {
//			if (mSettingLockedHideApp.getIsCheck() && AppConfigControler.getInstance(this).getHideApps().isEmpty()) {
//					mSettingLockedHideApp.setIsCheck(false);
//					final DialogConfirm dialog = new DialogConfirm(this);
//					dialog.show();
//					dialog.setTitle(getString(R.string.desksetting_lock_hide_app));
//					dialog.setMessage(getString(R.string.desksetting_hide_app_tips));
//					dialog.setNegativeButtonVisible(View.GONE);
//					dialog.setPositiveButton(getString(R.string.sure), new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//						}
//					});
//					return false;
//			}
//			
//		}
					return false;
			
		}

	@Override
	public void onClick(View v) {
		long curTime = System.currentTimeMillis();
		if (curTime - mLastClickTime < CLICK_TIME) {
			return;
		}
		mLastClickTime = curTime;
		final InvokeLockControler controler = InvokeLockControler.getInstance(this);
		switch (v.getId()) {
			case R.id.select_apps :
				Intent lockAppIntent = new Intent(getBaseContext(), LockAppActivity.class);
				startActivity(lockAppIntent);
				break;
			case R.id.change_email :
				controler.startLockAction(CHANGEEMAIL, new ActionResultCallBack() {

					@Override
					public void onUnlockSuccess(int actionId) {
						controler.setupEmail(DeskSettingLockActivity.this);
					}

					@Override
					public void onUnlockFail(int actionId) {
						// TODO Auto-generated method stub

					}
				}, DeskSettingLockActivity.this, R.drawable.safe_lock_icon, DeskSettingLockActivity.this.getString(R.string.desksetting_change_email));
				break;
			case R.id.change_password :
				controler.startLockAction(CHANGEPASSWORD, new ActionResultCallBack() {

					@Override
					public void onUnlockSuccess(int actionId) {
						controler.setupPassWord(CHANGEPASSWORD, new ActionResultCallBack() {
							
							@Override
							public void onUnlockSuccess(int actionId) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onUnlockFail(int actionId) {
								// TODO Auto-generated method stub
								
							}
						});
					}

					@Override
					public void onUnlockFail(int actionId) {
						// TODO Auto-generated method stub

					}
				}, DeskSettingLockActivity.this, R.drawable.safe_lock_icon, DeskSettingLockActivity.this.getString(R.string.desksetting_change_password));
				break;
			case R.id.setting_prime_banner :
			case R.id.setting_prime_banner_container : 
				mIsClickPrimeBanner = true;
				if (GoAppUtils.isAppExist(this, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
					FunctionPurchaseManager.getInstance(getApplicationContext()).startItemPayPage(
							mEntranceid + "");
				} else {
					DeskSettingUtils.showPayDialog(this, 501); //显示付费对话框
				}
				break;
			default :
				break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (FunctionPurchaseManager.getInstance(getApplicationContext())
				.getPayFunctionState(
						FunctionPurchaseManager.PURCHASE_ITEM_SECURITY) == FunctionPurchaseManager.STATE_CAN_USE
				&& mIsClickPrimeBanner) {
			mIsClickPrimeBanner = false;
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}

}
