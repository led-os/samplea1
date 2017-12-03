/*
 * 文 件 名:  ThemePurchaseManager.java
 * 版    权:  3G
 * 描    述:  <描述>
 * 修 改 人:  rongjinsong
 * 修改时间:  2012-8-21
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.jiubang.ggheart.apps.desks.diy.themescan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.encrypt.CryptTool;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.gau.utils.cache.utils.CacheUtil;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.DownloadControllerProxy;
import com.go.util.BroadCaster;
import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.go.util.market.MarketConstant;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.appgame.download.DownloadTask;
import com.jiubang.ggheart.appgame.download.IDownloadService;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.CouponSelectDialog;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreOperatorUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.billing.IPurchaseStateListener;
import com.jiubang.ggheart.billing.PurchaseStateManager;
import com.jiubang.ggheart.billing.PurchaseSupportedManager;
import com.jiubang.ggheart.billing.ThemeAppInBillingManager;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.components.SingleChoiceDialog;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.data.theme.broadcastReceiver.MyThemeReceiver;
import com.jiubang.ggheart.data.theme.zip.ZipResources;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 * 
 * @author rongjinsong
 * @date [2012-8-21]
 */
public class ThemePurchaseManager extends BroadCaster implements IPurchaseStateListener {

	private final static String TAG_LEVEL = "level";
	private ThemeAppInBillingManager mInBillingManager;
	private Context mContext;
	private static ThemePurchaseManager sSelf;
	private static boolean sDeBug = ThemeManager.SDEBUG;
	private CacheManager mCacheManager;
	private boolean mHasBindService;
	private ArrayList<Runnable> mDownLoadList;
	private ThemePurchaseManager(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mDownLoadList = new ArrayList<Runnable>();
		mInBillingManager = ThemeAppInBillingManager.getInstance(context);
		mCacheManager = new CacheManager(new FileCacheImpl(LauncherEnv.Path.GOTHEMES_PATH));
	}

	public synchronized static ThemePurchaseManager getInstance(Context context) {
		if (sSelf == null) {
			sSelf = new ThemePurchaseManager(context);
		}
		return sSelf;
	}

	private synchronized static void clearSelf() {
		sSelf = null;
	}

	/**
	 * 根据主题包名，检查付费状态
	 * 
	 * @param packageName
	 * @return 返回主题付费后，下载的zip文件名称
	 */
	public static String queryPurchaseState(Context context, String packageName) {
		//		if (sDeBug) {
		//			if (mTestPaidPks.contains(packageName)) {
		//				return packageName;
		//			}
		//			return null;
		//		}
		return PurchaseStateManager.query(context, packageName);
		//		return mInBillingManager.checkBillingState(packageName);
	}

	public void requestPurchase(Activity activity, ThemeInfoBean bean, int position) {
		//		int staticsType;
		//		if (bean.getBeanType() == ThemeConstants.LAUNCHER_FEATURED_THEME_ID
		//				|| bean.getBeanType() == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
		//			staticsType = GuiThemeStatistics.THEME_LAUNCHER_TYPE;
		//
		//		} else {
		//			staticsType = GuiThemeStatistics.THEME_LOCKER_TYPE;
		//		}
		int tabId = 0;
		if (bean.getBeanType() == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
			tabId = ThemeConstants.STATICS_ID_FEATURED;
		} else if (bean.getBeanType() == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
			tabId = ThemeConstants.STATICS_ID_HOT;
		} else if (bean.getBeanType() == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
			tabId = bean.getSortId();
		} else {
			tabId = ThemeConstants.STATICS_ID_LOCKER;
		}
		if (activity instanceof ThemeDetailActivity) {
			GuiThemeStatistics.guiStaticData(
					String.valueOf(bean.getFeaturedId()),
					GuiThemeStatistics.OPTION_CODE_DETAIL_BTN_CLICK, 1, "", String.valueOf(tabId),
					String.valueOf(position), "");
		} else {
			GuiThemeStatistics.guiStaticData(
					String.valueOf(bean.getFeaturedId()),
					GuiThemeStatistics.OPTION_CODE_PREVIEW_BTN_CLICK, 1, "", String.valueOf(tabId),
					String.valueOf(position), "");
		}
//		mInBillingManager.requestPurchase(bean.getPackageName(), bean.getPayId(), this);
		showPurchaseDialog(activity, bean);
	}

	private void showPurchaseDialog(Activity activity, final ThemeInfoBean bean) {
		final SingleChoiceDialog alertDialog = new SingleChoiceDialog(activity);
		alertDialog.show();
		alertDialog.setTitle(R.string.coupon_choose_purchase_type);

		String inBilling = mContext.getString(R.string.coupon_choose_purchase_type_inbilling);
		String couspon = mContext.getString(R.string.coupon_choose_purchase_type_coupon);
		int imgIds[] = { R.drawable.theme_pay_icon_market, R.drawable.theme_pay_icon_coupon };
		final CharSequence[] items = { inBilling, couspon };
		alertDialog.setItemData(items, null, imgIds, -1, false);
		alertDialog.setOnItemClickListener(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (item == 1) {
					//优惠券付费
					Intent it = new Intent(mContext.getApplicationContext(),
							CouponSelectDialog.class);
					it.putExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY, bean.getPackageName());
					it.putExtra(MyThemeReceiver.THEME_PRICE_INAPPBILLING, 1.99f);
					it.putExtra(MyThemeReceiver.THEME_TYPE, "common");
					it.putExtra(MyThemeReceiver.USE_COUPON_THEME_INFO, bean);
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(it);
				} else {
					mInBillingManager.requestPurchase(bean.getPackageName(), bean.getPayId(),
							ThemePurchaseManager.this);
				}
			}
		});
	}
	
	/**
	 * <br>
	 * 功能简述:付费选择：getjar或内付费 <br>
	 * 功能详细描述: <br>
	 * 注意:如果不支持内付费则只显示getjar
	 * 
	 * @param activity
	 * @param infoBean
	 * @param purchaseStateListener
	 */
	public void selectPayType(final Activity activity, final ThemeInfoBean infoBean,
			final int position) {

		if (!GoAppUtils.isCnUser(activity) && PurchaseSupportedManager.checkBillingSupported(activity)) {
			requestPurchase(activity, infoBean, position);
		} else {
			handleNormalFeaturedClickEvent(activity, infoBean, position);
		}
	}

	/**
	 * <br>
	 * 功能简述:主题包是否已下载 <br>
	 * 功能详细描述:如果文件存在但未付费也算未下载 <br>
	 * 注意:
	 * 
	 * @param themeName
	 * @return
	 */
	public boolean hasDownloaded(String themeName, String packageName) {
		File file = new File(LauncherEnv.Path.GOT_ZIP_HEMES_PATH + themeName
				+ ZipResources.ZIP_POSTFIX);
		if (file.exists() && queryPurchaseState(mContext, packageName) != null) {
			return true;
		}
		return false;
	}

	/**
	 * <br>
	 * 功能简述:下载内付费的主题包 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param infoBean
	 */
	public void startDownload(final ThemeInfoBean infoBean) {
		if(mHasBindService){
			handleDownLoad(infoBean);
		}else{
			synchronized (mDownLoadList) {
				mDownLoadList.add(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						handleDownLoad(infoBean);
					}
				});
				bindService();
			}
		}
	}

	private void handleDownLoad(ThemeInfoBean infoBean){
		if (infoBean == null) {
			return;
		}
		String url = infoBean.getDownLoadUrl();
		if (url == null || url.equals("")) {
			return;
		}
		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			DeskToast.makeText(mContext, R.string.import_export_sdcard_unmounted,
					Toast.LENGTH_SHORT).show();
			return;
		}
		String name = infoBean.getThemeName();
		if (name == null || name.equals("")) {
			name = infoBean.getPackageName();
		}

		String path = null;
		if (infoBean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
			path = LauncherEnv.Path.GOLOCKER_ZIP_HEMES_PATH;
		} else {
			path = LauncherEnv.Path.GOT_ZIP_HEMES_PATH;
		}
		
		GoStoreOperatorUtil.downloadFileDirectly(ApplicationProxy.getContext(), name, path,
				infoBean.getDownLoadUrl(), infoBean.getFeaturedId(), name
						+ ZipResources.ZIP_POSTFIX, false, AppsDetail.START_TYPE_DOWNLOAD_GO);
	}
	
	/**
	 * <br>
	 * 功能简述:处理推荐主题点击事件 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param bean
	 */
	protected void handleNormalFeaturedClickEvent(Context context, final ThemeInfoBean bean, int position) {
		HashMap<Integer, String> urlMap = bean.getUrlMap();
		String sendId = null;
		if (bean.getFeaturedId() != 0) {
			sendId = String.valueOf(bean.getFeaturedId());
		} else {
			sendId = bean.getPackageName();
		}
		GuiThemeStatistics.getInstance(mContext).savePaidThemePkg(bean.getPackageName(),
				bean.getFeaturedId());
		int tabId = 0;
		if (bean.getBeanType() == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
			tabId = ThemeConstants.STATICS_ID_FEATURED;
		} else if (bean.getBeanType() == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
			tabId = ThemeConstants.STATICS_ID_HOT;
		} else if (bean.getBeanType() == ThemeConstants.LAUNCHER_SPEC_THEME_ID) {
			tabId = bean.getSortId();
		} else {
			tabId = ThemeConstants.STATICS_ID_LOCKER;
		}
		if (context instanceof ThemeDetailActivity) {
			GuiThemeStatistics.guiStaticData(
					sendId,
					GuiThemeStatistics.OPTION_CODE_DETAIL_BTN_CLICK, 1, "", String.valueOf(tabId),
					String.valueOf(position), "");
			//			GuiThemeStatistics.getInstance(mContext).saveUserDetailGet(context,
			//					bean.getPackageName(), position, staticsType, "0", String.valueOf(tabId));
		} else {
			GuiThemeStatistics.guiStaticData(
					sendId,
					GuiThemeStatistics.OPTION_CODE_PREVIEW_BTN_CLICK, 1, "", String.valueOf(tabId),
					String.valueOf(position), "");
			//			GuiThemeStatistics.getInstance(mContext).saveUserTouch(context, bean.getPackageName(),
			//					position, staticsType, "0", String.valueOf(tabId));
		}
		if (urlMap == null) {
			if (bean.getBeanType() == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
				deskFeaturedClickEvent(bean.getPackageName());
			} else if (bean.getBeanType() == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
				lockerFeaturedClickEvent(bean.getPackageName());
			}
			return;
		}
		String url = urlMap.get(ThemeInfoBean.URL_KEY_GOSTORE);
		if (null != url) {
			AppsDetail.gotoDetailDirectly(mContext, AppsDetail.START_TYPE_APPRECOMMENDED,
					bean.getFeaturedId(), bean.getPackageName());
			//			Intent intent = new Intent();
			//			intent.setClass(mContext, ItemDetailActivity.class);
			//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//			intent.putExtra(GoStorePublicDefine.ITEM_ID_KEY, String.valueOf(bean.getFeaturedId()));
			//			intent.putExtra(GoStorePublicDefine.ITEM_PKG_NAME, bean.getPackageName());
			//			mContext.startActivity(intent);
			return;
		}
		url = urlMap.get(ThemeInfoBean.URL_KEY_FTP);
		if (null != url) {
			if (mHasBindService) {
				GoStoreOperatorUtil.downloadFileDirectly(mContext, bean.getThemeName(), url,
						bean.getFeaturedId(), bean.getPackageName(), null, DownloadTask.ICON_TYPE_ID,
						bean.getFirstPreViewDrawableName());
			}else{
				synchronized (mDownLoadList) {
					mDownLoadList.add(new Runnable() {
						public void run() {
							GoStoreOperatorUtil.downloadFileDirectly(mContext, bean.getThemeName(),
									bean.getUrlMap().get(ThemeInfoBean.URL_KEY_FTP),
									bean.getFeaturedId(), bean.getPackageName(), null,
									DownloadTask.ICON_TYPE_ID, bean.getFirstPreViewDrawableName());
						}
					});
					bindService();
				}
			}
			return;
		}
		url = urlMap.get(ThemeInfoBean.URL_KEY_GOOGLEMARKET);
		if (url != null) {
			gtotoMarketByurl(url);
			return;
		}
		url = urlMap.get(ThemeInfoBean.URL_KEY_WEB_GOOGLEMARKET);
		if (url != null) {
			GoStoreOperatorUtil.gotoBrowser(mContext, url);
			return;
		}
		url = urlMap.get(ThemeInfoBean.URL_KEY_OTHER);
		if (url != null) {
			GoStoreOperatorUtil.gotoBrowser(mContext, url);
			return;
		}
	}

	/**
	 * 跳转到电子市场
	 * 
	 * @param pkgName
	 */
	private void gotoMarket(String pkgName) {
		if (GoAppUtils.isMarketExist(mContext)) {
			// 跳转到market
			GoAppUtils.gotoMarket(mContext, MarketConstant.APP_DETAIL + pkgName
					+ LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK);
		} else {
			// 跳转到网页版market
			GoStoreOperatorUtil.gotoBrowser(mContext, MarketConstant.BROWSER_APP_DETAIL
					+ pkgName + LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK);
		}
	}

	private void gtotoMarketByurl(String url) {
		if (GoAppUtils.isMarketExist(mContext)) {
			// 跳转到market
			GoAppUtils.gotoMarket(mContext, url);
		} else {
			// 跳转到网页版market
			GoStoreOperatorUtil.gotoBrowser(mContext, url);
		}
	}

	@Override
	public void purchaseState(int purchaseState, String packageName) {
		// TODO Auto-generated method stub
		broadCast(IAppCoreMsgId.THEME_INAPP_PAID_FINISHED, purchaseState, packageName, null);
	}

	public void handleInAppClick(ThemeInfoBean infoBean, Activity activity, int position) {
		GuiThemeStatistics.getInstance(mContext).savePaidThemePkg(infoBean.getPackageName(),
				infoBean.getFeaturedId());
		if (infoBean.getPrice() != null && infoBean.getPrice().endsWith("0.0")) {
			startDownload(infoBean);
		} else if (queryPurchaseState(mContext, infoBean.getPackageName()) != null) {
			if (hasDownloaded(infoBean.getThemeName(), infoBean.getPackageName())) {
				applyTheme(activity, infoBean.getPackageName());
			} else {
				new PurchaseStateManager(mContext).save(infoBean.getPackageName(),
						LauncherEnv.Path.GOT_ZIP_HEMES_PATH);
				startDownload(infoBean);
			}
		} else {
			requestPurchase(activity, infoBean, position);
		}
	}

	public void destory() {
		if (mHasBindService) {
			ApplicationProxy.getContext().unbindService(mConnenction);
			DownloadControllerProxy.getInstance().setDownloadController(null);
			mHasBindService = false;
		}
		clearSelf();
		clearAllObserver();
		if (mInBillingManager != null) {
			mInBillingManager.destory();
		}
	}

	public void applyTheme(Activity activity, String packageName) {
		Intent intentGoLauncher = new Intent();
		intentGoLauncher.setClassName(activity, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
		activity.startActivity(intentGoLauncher);
		Intent intent = new Intent(ICustomAction.ACTION_THEME_BROADCAST);
		intent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING, MyThemeReceiver.CHANGE_THEME);
		intent.putExtra(MyThemeReceiver.PKGNAME_STRING, packageName);
		activity.sendBroadcast(intent);
		ThemeDetailActivity.exit();
	}

	/**
	 * 默认推荐主题跳转使用 桌面精选主题点击事件
	 * */
	protected void deskFeaturedClickEvent(String packageName) {

		if (GoStorePhoneStateUtil.is200ChannelUid(mContext) || !GoAppUtils.isCnUser(mContext)) { // 国外
			gotoMarket(packageName);

		} else { // 国内
			gotoGostore(packageName);
		}
	}

	/**
	 * 跳转到GO精品
	 * 
	 * @param pkgName
	 */
	private void gotoGostore(String pkgName) {
		AppsDetail.gotoDetailDirectly(mContext, AppsDetail.START_TYPE_APPRECOMMENDED, pkgName);
		//		Intent it = new Intent(mContext, ItemDetailActivity.class);
		//		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//		it.putExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY, pkgName);
		//		try {
		//			mContext.startActivity(it);
		//		} catch (ActivityNotFoundException e) {
		//			e.printStackTrace();
		//		}
	}

	/**
	 * 默认锁屏主题 锁屏精选主题点击事件 包名第一个字母 s代表收费 n代表免费
	 * */
	protected void lockerFeaturedClickEvent(String packageName) {

		// 真实包名
		//		String pkgName = packageName.substring(1);
		if (packageName.startsWith("n")) { // 免费
			if (GoStorePhoneStateUtil.is200ChannelUid(mContext) || !GoAppUtils.isCnUser(mContext)) { // 国外
				gotoMarket(packageName);

			} else { // 国内
				gotoGostore(packageName);
			}
		} else { // 收费
			gotoMarket(packageName);
		}

	}
	public void payForVip(int level) {
		String payId = null;
		// 生成一个加密的付费包名,查询是根据这个来查
		String id = Machine.getAndroidId();
		String payName = null;
		if (level == ThemeConstants.CUSTOMER_LEVEL1) {
			payName = CryptTool.encrypt(IPreferencesIds.THEME_CUSTOMER_LEVEL_1, id);
			payId = ThemeConstants.VIP_LEVE1_PAY_ID;
			if (sDeBug) {
				payId = "android.test.purchased";
			}
		} else if (level == ThemeConstants.CUSTOMER_LEVEL2) {
			payName = CryptTool.encrypt(IPreferencesIds.THEME_CUSTOMER_LEVEL_2, id);
			payId = ThemeConstants.VIP_LEVE2_PAY_ID;
			if (sDeBug) {
				payId = "android.test.purchased";
			}
		} else if (level == ThemeConstants.CUSTOMER_LEVEL1_UPGRADE) {
			payName = CryptTool.encrypt(IPreferencesIds.THEME_CUSTOMER_LEVEL_2, id);
			payId = ThemeConstants.VIP_LEVE1_UPGRADE_PAY_ID;
			if (sDeBug) {
				payId = "android.test.purchased";
			}
		}
		mInBillingManager.requestPurchase(payName, payId, this);
	}
	/**
	 * <br>功能简述:获取桌面主题vip或者锁屏主题vip
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param themeType 
	 * @return
	 */
	public static int getCustomerLevel(Context context, int themeType) {
		if (themeType == ThemeManageView.LAUNCHER_THEME_VIEW_ID) {
			// 走回原来获取桌面vip逻辑
			return getLauncherVip(context);
		} else if (themeType == ThemeManageView.LOCKER_THEME_VIEW_ID) {
			return getLockerVip(context);
		}
		return ThemeConstants.CUSTOMER_LEVEL0;

	}
	/**
	 * <br>功能简述:获取桌面主题vip
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param themeType 
	 * @return
	 */
	public static int getCustomerLevel(Context context) {
			// 走回原来获取桌面vip逻辑
			return getLauncherVip(context);
//			return 2;
	}
	
	private static int getLauncherVip(Context context) {
		String id = Machine.getAndroidId();
		String levelKey = CryptTool.encrypt(IPreferencesIds.THEME_CUSTOMER_LEVEL_2, id);
		//				new PurchaseStateManager(context).save(levelKey, levelKey);
		int level = ThemeConstants.CUSTOMER_LEVEL0;
		if (queryPurchaseState(context, levelKey) != null) {
			level = ThemeConstants.CUSTOMER_LEVEL2;
		}
		if (level == ThemeConstants.CUSTOMER_LEVEL0) {
			levelKey = CryptTool.encrypt(IPreferencesIds.THEME_CUSTOMER_LEVEL_1, id);
			if (queryPurchaseState(context, levelKey) != null) {
				level = ThemeConstants.CUSTOMER_LEVEL1;
			}

		}
		int l = verifyLevelFromSD(level, context);
		if (l > level) {
			if (l == ThemeConstants.CUSTOMER_LEVEL2) {
				levelKey = CryptTool.encrypt(IPreferencesIds.THEME_CUSTOMER_LEVEL_2, id);
				new PurchaseStateManager(context).save(levelKey, levelKey);
			} else if (l == ThemeConstants.CUSTOMER_LEVEL1) {
				levelKey = CryptTool.encrypt(IPreferencesIds.THEME_CUSTOMER_LEVEL_1, id);
				new PurchaseStateManager(context).save(levelKey, levelKey);
			}
		}
		return l;
	}
	
	private static int getLockerVip(Context context) {
		int vipLevel;
		int levelTemp = getLauncherVip(context);
		if (levelTemp != ThemeConstants.CUSTOMER_LEVEL0) {
			// 是桌面vip，下面要区分就新的还是旧的
			if (!hasNewVipFromSD(context)) {
				// 是旧vip，拥有锁屏vip的特权
				if (levelTemp == ThemeConstants.CUSTOMER_LEVEL1) {
					vipLevel = ThemeConstants.CUSTOMER_LEVEL1;
				} else {
					vipLevel = ThemeConstants.CUSTOMER_LEVEL2;
				}
			} else if (isLockerVip(context)) {
				vipLevel = ThemeConstants.LOCKER_VIP_LEVEL1;
			} else {
				vipLevel = ThemeConstants.CUSTOMER_LEVEL0;
			}
		} else {
			if (isLockerVip(context)) {
				vipLevel = ThemeConstants.LOCKER_VIP_LEVEL1;
			} else {
				vipLevel = ThemeConstants.CUSTOMER_LEVEL0;
			}
		}
		return vipLevel;
	}
	private static boolean isLockerVip(Context context) {
		String res = "";
		boolean result = false;
		// 用provider查询

		Uri uri = Uri.parse(ThemeConstants.LOCKER_VIP_CONTENT_UEL);
		String[] projection = new String[] { "is_golocker_vip" };
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, projection, null, null, null);
		//判断游标是否为空

		if (cursor != null) {
			try {
				cursor.moveToFirst();
				do {
					res = cursor.getString(0);
					// 输出日记
					if (res.equals("1")) {
						result = true;
					}
				} while (cursor.moveToNext());
				cursor.close();
				cursor = null;
			} catch (Exception e) {
				// TODO: handle exception
				if (cursor != null) {
					cursor.close();
				}
			}

		} else {
			String deviceId = Secure.getString(resolver, Secure.ANDROID_ID);
			String md5 = CryptTool.mD5generator(deviceId);
			String key = CryptTool.encrypt(md5, ThemeConstants.CRYPT_VIP_KEY);
			//            Log.d("zyz", "文件名 " + key);
			//            Log.d("zyz", "deviceId " + deviceId);
			String dir = LauncherEnv.Path.THEMES_PATH + key;
			File file = new File(dir);
			if (file.exists()) {
				result = true;
			}
		}
		//		}

		return result;
	}
	

	public static void clearPaidThemePkgList() {
		FileUtil.deleteFile(LauncherEnv.Path.GOTHEMES_PATH + ThemeConstants.CLICK_THEME_PKGNAME);
	}

	public void savePaidThemePkg(String pkg) {
		try {
			JSONObject obj = getPaidThemePkg();
			ArrayList<String> list = parsePaidInfo(obj);
			if (list != null && list.contains(pkg)) {
				return;
			}
			JSONArray jsonArray = null;

			if (obj != null) {
				jsonArray = obj.getJSONArray("pkgs");
			} else {
				obj = new JSONObject();
			}

			if (jsonArray == null) {
				jsonArray = new JSONArray();
				obj.put("pkgs", jsonArray);
			}

			JSONObject json = new JSONObject();
			json.put("pkg", pkg);

			jsonArray.put(json);
			mCacheManager.saveCache(ThemeConstants.CLICK_THEME_PKGNAME, obj.toString().getBytes());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * <br>功能简述:校验SD卡上保存的等级信息与sharepreference中是否一致
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param level
	 * @return
	 */
	private static int verifyLevelFromSD(int level, Context context) {
		int l = ThemeConstants.CUSTOMER_LEVEL0;
		String fileName = CryptTool.encrypt(ThemeConstants.FILE_CUSTOMER_LEVEL,
				GoStorePhoneStateUtil.getVirtualIMEI(context)); //保证每个手机不一样
		CacheManager cacheManager = new CacheManager(new FileCacheImpl(
				LauncherEnv.Path.GOTHEMES_PATH));
		byte[] cacheData = null;
		if (cacheManager.isCacheExist(fileName)) {
			cacheData = cacheManager.loadCache(fileName);
		}
		if (cacheData == null && level == ThemeConstants.CUSTOMER_LEVEL0) {
			return ThemeConstants.CUSTOMER_LEVEL0;
		}
		JSONObject obj = null;
		try {
			if (cacheData != null) {
				obj = CacheUtil.byteArrayToJson(cacheData);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (obj != null) {
			l = obj.optInt(TAG_LEVEL);
		}
		if (level < l) { //SD卡中的等级大于sharepreference：桌面恢复了默认
			level = l;
		} else if (level > l) { //SD卡中的等级小于sharepreference：未保存到sd卡，现在保存
			obj = new JSONObject();
			try {
				obj.put(TAG_LEVEL, level);
				cacheManager.saveCache(fileName, obj.toString().getBytes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return level;
	}
	/**
	 * <br>功能简述:记录新vip标记
	 * <br>功能详细描述:新VIP只出现在4.12以后，新VIP将无法使用锁屏
	 * <br>注意:
	 * @param newVIP
	 * @param context
	 * @return
	 */
	public static void writeNewLauncherVipToSD(String newVIP, Context context) {
		String vipTemp = null;
		String fileName = CryptTool.encrypt(ThemeConstants.FILE_LOCKER_LEVEL,
				GoStorePhoneStateUtil.getVirtualIMEI(context)); //保证每个手机不一样
		CacheManager cacheManager = new CacheManager(new FileCacheImpl(
				LauncherEnv.Path.GOTHEMES_PATH));
		byte[] cacheData = null;
		if (cacheManager.isCacheExist(fileName)) {
			cacheData = cacheManager.loadCache(fileName);
		}
		JSONObject obj = null;
		try {
			if (cacheData != null) {
				obj = CacheUtil.byteArrayToJson(cacheData);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (obj != null) {
			vipTemp = obj.optString(newVIP);
		}
		if (vipTemp == null || vipTemp.equals("")) {
			obj = new JSONObject();
			try {
				obj.put(newVIP, newVIP);
				cacheManager.saveCache(fileName, obj.toString().getBytes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * <br>功能简述:查询有无新vip标记
	 * <br>功能详细描述:新VIP只出现在4.12以后，新VIP将无法使用锁屏vip的特权
	 * <br>注意:
	 * @param newVIP
	 * @param context
	 * @return
	 */
	public static boolean hasNewVipFromSD(Context context) {
		boolean res = false;
		String vipTemp = null;
		String fileName = CryptTool.encrypt(ThemeConstants.FILE_LOCKER_LEVEL,
				GoStorePhoneStateUtil.getVirtualIMEI(context)); //保证每个手机不一样
		CacheManager cacheManager = new CacheManager(new FileCacheImpl(
				LauncherEnv.Path.GOTHEMES_PATH));
		byte[] cacheData = null;
		if (cacheManager.isCacheExist(fileName)) {
			cacheData = cacheManager.loadCache(fileName);
		}
		JSONObject obj = null;
		try {
			if (cacheData != null) {
				obj = CacheUtil.byteArrayToJson(cacheData);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (obj != null) {
			vipTemp = obj.optString(IPreferencesIds.CUSTOMER_VIP_NEW);
		}
		if (vipTemp != null && !vipTemp.equals("")) {
			res = true;
			if (queryPurchaseState(context, IPreferencesIds.CUSTOMER_VIP_NEW) == null) {
				new PurchaseStateManager(context).save(IPreferencesIds.CUSTOMER_VIP_NEW, IPreferencesIds.CUSTOMER_VIP_NEW);
			}
		}
		return res;
	}

	private JSONObject getPaidThemePkg() {
		byte[] cacheData = mCacheManager.loadCache(ThemeConstants.CLICK_THEME_PKGNAME);
		if (cacheData == null) {
			return null;
		}
		JSONObject obj = CacheUtil.byteArrayToJson(cacheData);
		return obj;
	}
	public ArrayList<String> getPaidPkgs() {
		return parsePaidInfo(getPaidThemePkg());
	}

	private ArrayList<String> parsePaidInfo(JSONObject paidJson) {
		if (paidJson != null) {
			try {
				JSONArray jsonArray = paidJson.getJSONArray("pkgs");
				if (jsonArray != null && jsonArray.length() > 0) {
					int length = jsonArray.length();
					ArrayList<String> paidInfoList = new ArrayList<String>(length);
					for (int i = 0; i < length; i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						String pkg = json.optString("pkg", "");
						if (!pkg.equals("")) {
							paidInfoList.add(pkg);
						}
					}
					return paidInfoList;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	private void bindService() {
		ApplicationProxy.getContext().startService(new Intent(ICustomAction.ACTION_DOWNLOAD_SERVICE));
		if (!mHasBindService) {
			mHasBindService = ApplicationProxy.getContext().bindService(
					new Intent(ICustomAction.ACTION_DOWNLOAD_SERVICE), mConnenction,
					Context.BIND_AUTO_CREATE);
		}
	}
	
	/**
	 * 下载服务的控制接口Connector
	 */
	private ServiceConnection mConnenction = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			IDownloadService mController = IDownloadService.Stub.asInterface(service);
			// 设置整个进程通用的下载控制接口
			DownloadControllerProxy.getInstance().setDownloadController(mController);
			synchronized (mDownLoadList) {
				for(Runnable runnable :mDownLoadList){
					runnable.run();
				}
				mDownLoadList.clear();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("", "Theme onServiceDisconnected");
			DownloadControllerProxy.getInstance().setDownloadController(null);
		}
	};
}
