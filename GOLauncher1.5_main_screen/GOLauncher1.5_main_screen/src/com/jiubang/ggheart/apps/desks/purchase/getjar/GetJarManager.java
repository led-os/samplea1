package com.jiubang.ggheart.apps.desks.purchase.getjar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.gau.go.launcherex.R;
import com.getjar.sdk.Account;
import com.getjar.sdk.Ad;
import com.getjar.sdk.GetjarClient;
import com.getjar.sdk.GetjarConnectionCallbacks;
import com.getjar.sdk.GetjarConnectionResult;
import com.getjar.sdk.GetjarConstants;
import com.getjar.sdk.GetjarLicense.Scope;
import com.getjar.sdk.ImageAsset;
import com.getjar.sdk.OnAdAvailableListener;
import com.getjar.sdk.OnGetjarLicensesReceivedListener;
import com.getjar.sdk.OnGetjarVoucherRedeemedListener;
import com.getjar.sdk.OnGetjarWorkFinishedListener;
import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogSingleChoice;
import com.jiubang.ggheart.apps.desks.diy.GoLauncher;
import com.jiubang.ggheart.data.ContentProvider.GoContentProvider;
import com.jiubang.ggheart.data.tables.PurchaseTable;

/**
 * 
 * <br>
 * 类描述:getJar付费管理类 <br>
 * 功能详细描述:
 * 
 * @author rongjinsong
 * @date [2014年3月20日]
 */
public class GetJarManager implements OnGetjarWorkFinishedListener,
		GetjarConnectionCallbacks {

	public static final String TAG = "GetJarManager";
	private static final String TOKEN = "d16fa6fb-b09a-4a54-a750-52ea9b203b32";
	private static final String NATIVE_AD_TAG = "go_launcher_native_AD";
	private GetjarClient mClient;
	private Context mContext;
	private static GetJarManager sSelf;
	private BroadcastReceiver mReceiver;
	private ConcurrentHashMap<String, IGetJarPurchaseListener> mRequestMap;
	private ArrayList<IConnectCallBack> mConnectCallBacks;

	private GetJarManager(Context context, Intent intent) {
		mContext = context;
		try {
			mClient = new GetjarClient.Builder(TOKEN, context, intent, this,
					this).create();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initReceiver();
		mRequestMap = new ConcurrentHashMap<String, IGetJarPurchaseListener>();
		mConnectCallBacks = new ArrayList<GetJarManager.IConnectCallBack>();
	}

	public static synchronized GetJarManager buildInstance() {
		return buildInstance(new Intent(ApplicationProxy.getContext(), GoLauncher.class));

	}

	public static synchronized GetJarManager buildInstance(Intent intent) {
		if (sSelf == null) {
			sSelf = new GetJarManager(ApplicationProxy.getContext(), intent);
		}
		/**
		 * 这里增加这个的原因是目前getjar sdk存在db locked异常，发生的场景是在桌面刚起动过程进入主题预览或精品，
		 * 在构造函数了catch住了异常，但mClient会为空，这里在做一次build,待getjar修复了db locked后再去除这段
		 */
		if (sSelf.mClient == null) {
			try {
				sSelf.mClient = new GetjarClient.Builder(TOKEN, ApplicationProxy.getContext(),
						intent, sSelf, sSelf).create();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sSelf;
	}

	public void connect(IConnectCallBack callBack) {
		if (callBack != null) {
			if (mClient != null) {
				synchronized (mConnectCallBacks) {
					if (callBack != null
							&& !mConnectCallBacks.contains(callBack)) {
						mConnectCallBacks.add(callBack);
					}
				}
				mClient.connect();
			} else {
				callBack.onConnect(false, null);
			}
		}
	}

	public void addConnectCallBack(IConnectCallBack callBack) {
		if (callBack != null) {
			synchronized (mConnectCallBacks) {
				if (callBack != null && !mConnectCallBacks.contains(callBack)) {
					mConnectCallBacks.add(callBack);
				}
			}
		}
	}
	
	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		Log.d(TAG, "getJar onConnected");
		sendConnectBroadcast(true, null);
	}

	@Override
	public void onConnectionFailed(GetjarConnectionResult arg0) {
		// TODO Auto-generated method stub
		Intent resolution = null;
		if (arg0.hasResolution() && getAvailableAccounts() != null && getAvailableAccounts().size() > 0) {
			resolution = arg0.getResolutionIntent();
		}
		sendConnectBroadcast(false, resolution);
		Log.d(TAG, "onConnectionFailed ErrorCode：" + arg0.getErrorCode());
	}

	private void sendConnectBroadcast(boolean connect, Intent resolution) {
		synchronized (mConnectCallBacks) {
			for (IConnectCallBack callBack : mConnectCallBacks) {
				if (callBack != null) {
					callBack.onConnect(connect, resolution);
				}
			}
			mConnectCallBacks.clear();
		}
	}

	@Override
	public void onWorkFinished(Intent arg0) {
		// TODO Auto-generated method stub
	}

	public void queryPurchaseState(String productId,
			OnGetjarLicensesReceivedListener listener) {
		try {
			if (mClient != null) {
				mClient.getLicense(productId, listener);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <br>
	 * 功能简述:付费接口 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param producdId
	 *            商品ID
	 * @param productName
	 *            商品名称
	 * @param description
	 *            商品描述
	 * @param price
	 */
	public void requestPurchase(String producdId, String productName,
			String description, int price, IGetJarPurchaseListener listener) {
		if (listener != null && producdId != null) {
			mRequestMap.put(producdId, listener);
		}
		Intent purchaseActivity = new Intent(mContext,
				GetJarPurchaseActivity.class);
		purchaseActivity.putExtra(GetJarPurchaseActivity.PRODUCT_ID, producdId);
		purchaseActivity.putExtra(GetJarPurchaseActivity.PRODUCT_NAME,
				productName);
		purchaseActivity.putExtra(GetJarPurchaseActivity.PRODUCT_INFO,
				description);
		purchaseActivity.putExtra(GetJarPurchaseActivity.PRODUCT_PRICE, price);
		purchaseActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(purchaseActivity);

	}

	private void initReceiver() {
		if (mContext == null) {
			return;
		}
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals(
						GetJarPurchaseActivity.PURCHASE_REQUEST_ACTION)) {
					intent.setClass(mContext, GetJarPurchaseActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				} else if (intent.getAction().equals(
						GetJarPurchaseActivity.PURCHASE_RESULT_ACTION)) {
					final String id = intent
							.getStringExtra(GetJarPurchaseActivity.PRODUCT_ID);
					final int result = intent.getIntExtra(
							GetJarPurchaseActivity.PURCHSE_RESULT,
							GetJarPurchaseActivity.PURCHSE_RESULT_FAILE);
					if (id != null) {
						new Thread() {
							public void run() {
								final IGetJarPurchaseListener listener = mRequestMap
										.get(id);
								mRequestMap.remove(id);
								if (listener != null) {
									listener.handlePurchaseResult(result);
								}
							};
						}.start();
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(GetJarPurchaseActivity.PURCHASE_REQUEST_ACTION);
		filter.addAction(GetJarPurchaseActivity.PURCHASE_RESULT_ACTION);
		mContext.registerReceiver(mReceiver, filter);
	}

	public static synchronized void destory() {
		if (sSelf != null) {
			try {
				if (sSelf != null && sSelf.mContext != null) {
					sSelf.mContext.unregisterReceiver(sSelf.mReceiver);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sSelf.mRequestMap != null) {
				sSelf.mRequestMap.clear();
			}
		}
		sSelf = null;
	}

	public boolean isPurchased(String productId) {
		ContentResolver resolver = mContext.getContentResolver();
		String selection = PurchaseTable.PRODUCT_ID + " ='" + productId + "'";
		Cursor cursor = resolver.query(GoContentProvider.CONTENT_PURCHASE_URI,
				null, selection, null, null);
		return cursor != null && cursor.getCount() > 0;
	}

	public void prepareAd(OnAdAvailableListener listener) {
		try {
			Log.d(TAG, "prepare Ad");
			if (mClient != null) {
				mClient.prepareAd(GetjarConstants.CURRENCY_KEY_NONE, listener);
			} else if (listener != null) {
				listener.onAdAvailable(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Intent getInterstitialIntent() {
		if (mClient != null) {
			try {
				return mClient.getInterstitialIntent(
						"go_launcher_Interstitial_AD",
						GetjarConstants.CURRENCY_KEY_NONE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public Ad getNativeAd() {
		if (mClient != null) {
			try {
				Ad ad = mClient.getAd(NATIVE_AD_TAG,
						GetjarConstants.CURRENCY_KEY_NONE);
				while ((ad != null)
						&& (!ad.hasImageType(ImageAsset.INTERSTITIAL_IMAGE_TYPE))) {
					ad = mClient.getAd(NATIVE_AD_TAG,
							GetjarConstants.CURRENCY_KEY_NONE);
				}
				return ad;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public Uri getNativeAdImageUrl(Ad ad) {
		if (ad != null) {
			// Get some values from the ad that we will need
			ImageAsset adImage = null;
			for (ImageAsset image : ad.getAdImages()) {
				if ((ImageAsset.ICON_IMAGE_TYPE.equals(image.getType()))
						&& (image.getUrl() != null)) {
					adImage = image;
				}
			}
			String uri = adImage.getUrl();
			if (uri != null) {
				return Uri.parse(adImage.getUrl());
			}
		}
		return null;
	}

	/**
	 * 
	 * <br>
	 * 类描述:connect结果回调 <br>
	 * 功能详细描述:
	 * 
	 * @author rongjinsong
	 * @date [2014年3月26日]
	 */
	public interface IConnectCallBack {
		public void onConnect(boolean connect, Intent resolution);
	}

	public Account getCurrentAccount() {
		if (mClient != null) {
			try {
				return mClient.getCurrentAccount();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<Account> getAvailableAccounts() {
		if (mClient != null) {
			try {
				return mClient.getAvailableAccounts();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void setCurrentAccount(String account) {
		if (mClient != null) {
			try {
				mClient.setCurrentAccount(account,
						GetjarConstants.ANDROID_ACCOUNT_PROVIDER_KEY);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void getLicense(String productid,
			OnGetjarLicensesReceivedListener listener) {
		try {
			if (mClient != null) {
				mClient.getLicense(productid, listener);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Intent getPurchaseIntent(String productId, String productName,
			String poductInfo, long productPrice) {
		try {
			if (mClient != null) {
				return mClient.getPurchaseIntent(productId, productName,
						poductInfo, productPrice, Scope.USER);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void confirmVoucher(String voucherToken) {
		try {
			if (mClient != null) {
				mClient.confirmVoucher(voucherToken);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void redeemVoucher(String voucherToken, String developerPayload,
			OnGetjarVoucherRedeemedListener redeemListener) {
		try {
			if (mClient != null) {
				mClient.redeemVoucher(voucherToken, developerPayload,
						redeemListener);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * <br>功能简述:弹出getjar帐号选择框
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param activity
	 */
	public static void showSelectAccountDialog(Activity activity) {
		List<com.getjar.sdk.Account> accounts = GetJarManager.buildInstance()
				.getAvailableAccounts();
		// 有多个帐号时弹框
		if (accounts != null) {
			if (accounts.size() > 1) {
				showSelectAccountDialog(accounts, activity);
			} else if (accounts.size() == 1) {
				GetJarManager.buildInstance().setCurrentAccount(accounts.get(0).getName());
			}
		}

	}

	private static void showSelectAccountDialog(List<Account> accounts, Activity activity) {
		final CharSequence[] entries = new String[accounts.size()];
		for (int i = 0; i < accounts.size(); i++) {
			entries[i] = accounts.get(i).getName();
		}
		DialogSingleChoice mDialog = new DialogSingleChoice(activity);
		mDialog.show();
		mDialog.setTitle(activity.getResources().getString(
				R.string.theme_getjar_select_account_doalog_title));
		mDialog.setItemData(entries, 0, true);
		mDialog.setNegativeButtonVisible(View.GONE);
		mDialog.setCancelable(false);
		mDialog.setOnItemClickListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				GetJarManager.buildInstance().setCurrentAccount(entries[item].toString());
			}
		});
	}

}
