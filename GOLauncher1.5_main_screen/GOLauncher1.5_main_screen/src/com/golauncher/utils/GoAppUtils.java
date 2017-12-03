package com.golauncher.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.AppUtils;
import com.go.util.GotoMarketIgnoreBrowserTask;
import com.go.util.device.Machine;
import com.go.util.log.LogConstants;
import com.go.util.market.MarketConstant;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenAdvertMsgId;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.apps.desks.diy.INotificationId;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.QuestionnaireActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.BannerDetailActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeWebViewActivity;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreAppInforUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.DeskBuilder;
import com.jiubang.ggheart.components.advert.AdvertConstants;
import com.jiubang.ggheart.components.advert.AdvertDialogCenter;
import com.jiubang.ggheart.components.advert.untils.NoAdvertCheckReceiver;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.info.FavoriteInfo;
import com.jiubang.ggheart.data.info.FunTaskItemInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.CheckApplication;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * @author liuheng
 *
 */
public class GoAppUtils {

	public static final int WIDGET_CLICK_NONE = 0;  //widget点击事件不做处理
	public static final int WIDGET_CLICK_RETURN = 1; //widget点击事件拦截
	public static final int WIDGET_CLICK_PICK_APP_WIDGET = 2; //widget点击事件打开go系统小部件并拦截
	
	/**
	 * <br>功能简述:跳转主题专题页
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param id 专题ID
	 */
	public static void goToThemeSpec(Activity context, int id, boolean fromMsg) {
		try {
			Intent intent = new Intent();
			intent.putExtra("ty", id);
			intent.putExtra("entrance", fromMsg);
			intent.setClass(context, BannerDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 调起clean master
	 * 如果没有安装，则跳转电子市场
	 * @param context
	 * @param aId
	 * @param ga
	 */
	public static void launchCleanMaster(Context context) {
		if (context == null) {
			return;
		}
		PackageManager pm = context.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(PackageName.CLEAN_MASTER_PACKAGE);
		context.startActivity(intent);
	}
	
	/**
	 * <br>功能简述:使用GALINK打开电子市场的详情页面。
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param packageName
	 * @param aId
	 * @param mapId
	 * @param gaLink
	 * @return
	 */
	public static boolean openDetailsWithGALink(Context context, String packageName, String aId,
			String mapId, String gaLink) {
		if (context == null) {
			return false;
		}
		if (!isAppExist(context, packageName)) {
			StatisticsData.updateAppClickData(context, packageName,
					AdvertConstants.ADVERT_STATISTICS_TYPE, mapId, aId);
			MsgMgrProxy.sendMessage(null, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
					IScreenAdvertMsgId.REQUEST_ADVERT_STAT_CLICK_ACTION, -1, packageName, aId, null, mapId);

			if (isMarketExist(context)) {
				GoAppUtils.gotoMarket(context, MarketConstant.APP_DETAIL + packageName + gaLink);
			} else {
				AppUtils.gotoBrowser(context, MarketConstant.BROWSER_APP_DETAIL + packageName
						+ gaLink);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * <br>功能简述:传统的URL打开电子市场的详情页面
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param packageName
	 * @param aId
	 * @param mapId
	 * @param url
	 * @return
	 */
	public static boolean openDetailsWithUrl(Context context, String packageName, String aId,
			String mapId, String url) {
		if (context == null) {
			return false;
		}
		if (!isAppExist(context, packageName)) {
			// 实时统计
			// 展示统计
			GuiThemeStatistics.guiStaticDataWithAId(40, mapId, "f000", 1, "-1", "-1", "-1",
					"-1", aId);
			//点击统计
			GuiThemeStatistics.guiStaticDataWithAId(40, mapId, "a000", 1, "-1", "-1", "-1",
					"-1", aId);
			AdvertDialogCenter.sDownloadDuSpeedBoosterMark = System.currentTimeMillis();
			GuiThemeStatistics.recordAdvertId(context, mapId, aId);
//			StatisticsData.updateAppClickData(context, packageName,
//					AdvertConstants.ADVERT_STATISTICS_TYPE, mapId, aId);
//			AdvertControl advertControl = AdvertControl.getAdvertControlInstance(context);
//			advertControl.requestAdvertStatOnClick(packageName, aId, null, mapId);
			GotoMarketIgnoreBrowserTask.startExecuteTask(context, url);
			return true;
		}
		return false;
	}
	
	public static int widgetOperate(FavoriteInfo info, String packageName) {
		return WIDGET_CLICK_NONE;
	}

	public static void gotoBrowserInRunTask(Context context, String url) {
		if (url == null) {
			return;
		}
		Uri uri = Uri.parse(url);
		Intent myIntent = new Intent(Intent.ACTION_VIEW, uri);
		try {

			// 1:已安装的浏览器列表
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> resolveList = pm.queryIntentActivities(myIntent, 0);
			resolveList = AppUtils.filterPkgs(resolveList, PackageName.PKGS_BROWSER_NOTNEEDHANDLED);
			if (resolveList != null && !resolveList.isEmpty()) {
				// 2:获取当前运行程序列表
				ArrayList<FunTaskItemInfo> curRunList = null;
				try {
					curRunList = AppCore.getInstance().getTaskMgrControler().getProgresses();
				} catch (Throwable e) {
				}
				int curRunSize = (curRunList != null) ? curRunList.size() : 0;

				// 两个列表循环遍历
				for (int i = curRunSize - 1; i > 0; i--) {
					FunTaskItemInfo funTaskItemInfo = curRunList.get(i);
					Intent funIntent = funTaskItemInfo.getAppItemInfo().mIntent;
					ComponentName funComponentName = funIntent.getComponent();
					for (ResolveInfo resolveInfo : resolveList) {
						if (resolveInfo.activityInfo.packageName != null
								&& resolveInfo.activityInfo.packageName.equals(funComponentName
										.getPackageName())) {
							// 找到正在运行的浏览器，直接拉起
							if (funIntent.getComponent() != null) {
								String pkgString = funIntent.getComponent().getPackageName();
								if (pkgString != null) {
									funIntent.setAction(Intent.ACTION_VIEW);
									funIntent.setData(uri);
									funIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
									context.startActivity(funIntent);
									return;
								}
							}
						}
					}
				}
				ResolveInfo resolveInfo = resolveList.get(0);
				String pkgString = resolveInfo.activityInfo.packageName;
				String activityName = resolveInfo.activityInfo.name;
				myIntent.setClassName(pkgString, activityName);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(myIntent);
				return;
			}
		} catch (Throwable e) {
			//有错，则不进行intent过滤
		}
	}

	private static void gotoDownloadGolocker(final Context context) {
		DeskBuilder builder = new DeskBuilder(context);
		builder.setTitle(context.getString(R.string.locker_tip_title));
		builder.setMessage(context.getString(R.string.locker_tip_message));
		builder.setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!GoAppUtils.gotoMarket(context, MarketConstant.BY_PKGNAME
						+ PackageName.LOCKER_PACKAGE)) {
					AppUtils.gotoBrowser(context, LauncherEnv.Url.GOLOCKER_DOWNLOAD_URL);
				}
			}
		});
		builder.setNegativeButton(context.getString(R.string.cancel), null);
		builder.create().show();
	}

	/**
	 * 打开golocker
	 */
	public static void gotoGolocker(final Context context) {
		if (isGoLockerExist(context)) {
			PackageManager pm = context.getPackageManager();
			try {
				Intent tmpIntent = pm.getLaunchIntentForPackage(ICustomAction.ACTION_LOCKER);
				AppUtils.safeStartActivity(context, tmpIntent);
			} catch (Exception e) {
			}
		} else {
			gotoDownloadGolocker(context);
		}
	}

	/**
	 * 打开golocker
	 */
	public static void gotoGolockerSetting(final Context context) {
		if (isGoLockerExist(context)) {
			AppUtils.safeStartActivity(context, new Intent(ICustomAction.ACTION_LOCKER_SETTING));
		} else {
			gotoDownloadGolocker(context);
		}
	}	

	/**
	 * 打开gobackup
	 */
	public static void gotoGobackup(final Context context) {
		if (isAppExist(context, PackageName.RECOMMAND_GOBACKUPEX_PACKAGE)) {
			PackageManager pm = context.getPackageManager();
			try {
				Intent tmpIntent = pm
						.getLaunchIntentForPackage(PackageName.RECOMMAND_GOBACKUPEX_PACKAGE);
				AppUtils.safeStartActivity(context, tmpIntent);
			} catch (Exception e) {
			}
		} else {
			// gotoDownloadGobackup(context);
			// add by chenguanyu 2012.7.10
			String title = context.getString(R.string.recommand_gobackup);
			String content = context.getString(R.string.fav_app);
			String[] linkArray = new String[] { PackageName.RECOMMAND_GOBACKUPEX_PACKAGE,
					LauncherEnv.Url.GOBACKUP_EX_FTP_URL };
			CheckApplication.downloadAppFromMarketFTPGostore(context, content, linkArray,
					LauncherEnv.GOBACKUP_GOOGLE_REFERRAL_LINK, title, System.currentTimeMillis(),
					isCnUser(context), CheckApplication.FROM_MENU, 0, null);
		}
	}

	private static void gotoDownloadGobackup(final Context context) {
		DeskBuilder builder = new DeskBuilder(context);
		builder.setTitle(context.getString(R.string.attention_title));
		builder.setMessage(context.getString(R.string.backup_tip_message));
		builder.setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 跳到GO Store上的GO备份界面
				AppsDetail.gotoDetailDirectly(context, AppsDetail.START_TYPE_APPRECOMMENDED,
						PackageName.GOBACKUP_PACKAGE);
				//				GoStoreOperatorUtil.gotoStoreDetailDirectly(context,
				//						PackageName.GOBACKUP_PACKAGE);
			}
		});
		builder.setNegativeButton(context.getString(R.string.cancel), null);
		builder.create().show();
	}
	
	/**
	 * 跳转到美化中心，如果没有安装则跳转电子市场
	 * @param context
	 * @param entranceId 入口id（桌面主题、锁屏主题等）
	 */
	public static void gotoKittyPlay(Context context, int entranceId, String ga) {
		if (context == null) {
			return;
		}
		
		if (isAppExist(context, PackageName.KITTY_PLAY_PACKAGE_EX_NAME)) {
			Intent intent = null;
			try {
				intent = new Intent(ICustomAction.KITTYPLAY_ACTION_NEW_ENTRANCE);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.putExtra(ThemeManager.KITTYPLAY_ENTRANCE_KEY, entranceId);
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				//如果根据action查找不了，再根据包名启动
				intent = context.getPackageManager().getLaunchIntentForPackage(
						PackageName.KITTY_PLAY_PACKAGE_EX_NAME);
				intent.putExtra(ThemeManager.KITTYPLAY_ENTRANCE_KEY, entranceId);
				context.startActivity(intent);
			}
		} else {
			if (isMarketExist(context)) {
				GoAppUtils.gotoMarket(context, MarketConstant.APP_DETAIL + PackageName.KITTY_PLAY_PACKAGE_EX_NAME + ga);
			} else {
				AppUtils.gotoBrowser(context, MarketConstant.BROWSER_APP_DETAIL + PackageName.KITTY_PLAY_PACKAGE_EX_NAME + ga);
			}
		}
	}
	/**
	 * 卸载程序时判断是否要显示调查问卷
	 * @param uri
	 * @return
	 */
	public static boolean goToQuestionnaire(Context context, String pkgName) {
		if (context == null || pkgName == null) {
			return false;
		}
		try {
			if (Machine.isNetworkOK(context) && 
					pkgName.equals(context.getPackageName())) {
				Intent intent = new Intent();
				intent.setClass(context, QuestionnaireActivity.class);
				context.startActivity(intent);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ArrayList<String> getLockerPkgName(Context context) {
		Intent golockerIntent = new Intent(ICustomAction.ACTION_LOCKER);
		ArrayList<String> packageNames = null;
		List<ResolveInfo> infos = null;
		infos = context.getPackageManager().queryIntentActivities(golockerIntent, 0);
		if (infos != null && !infos.isEmpty()) {
			int size = infos.size();
			packageNames = new ArrayList<String>(size);
			for (ResolveInfo resolveInfo : infos) {
				if (resolveInfo.activityInfo.packageName.equals(PackageName.GO_LOCK_PACKAGE_NAME)) {
					packageNames.add(resolveInfo.activityInfo.packageName);
				} else {
					packageNames.add(0, resolveInfo.activityInfo.packageName);
				}
			}
		}
		return packageNames;
	}
	
	/**
	 * 判断用户安装的锁屏版本是否为普通版本，即跟默认包名一致
	 * @param context
	 * @return
	 */
	public static boolean isDefGolockerExist(Context context) {
		List<String> lockPkgName = getLockerPkgName(context);
		for (String pkgName : lockPkgName) {
			if (pkgName.equals(PackageName.GO_LOCK_PACKAGE_NAME)) {
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * 返回当前可使用的锁屏包名
	 * @param context
	 * @return
	 */
	public static String getCurLockerPkgName(Context context) {
		if (isGoLockerExist(context) && !isDefGolockerExist(context)) {
			return getLockerPkgName(context).get(0);
		} else {
			return PackageName.GO_LOCK_PACKAGE_NAME;
		}
	}

	/**
	 * 下载HD版本
	 */
	public static void gotoHDLauncher(final Context context) {
		PackageManager pa = context.getPackageManager();
		List<PackageInfo> packages = pa.getInstalledPackages(0);
		for (PackageInfo info : packages) {
			if (info.packageName.equals(PackageName.GOHD_LAUNCHER_PACKAGE)) {
				Intent intent = pa
						.getLaunchIntentForPackage(PackageName.GOHD_LAUNCHER_PACKAGE);
				context.startActivity(intent);
				return;
			}
		}
		if (GoStorePhoneStateUtil.is200ChannelUid(context)) {
			if (!GoAppUtils.gotoMarket(context, MarketConstant.BY_PKGNAME
					+ PackageName.GOHD_LAUNCHER_PACKAGE)) {
				// 如果没有电子市场跳转到网页版电子市场
				AppUtils.gotoBrowser(context, LauncherEnv.Url.GOHDLAUNCHER_WEB_URL);
			}
		} else {
			gotoDownloadHDLauncher(context);
		}
	}
	
	/**
	 * <br>功能简述:用webview打开
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param mContext
	 * @param url
	 */
	public static void gotoWebView(Context mContext, String url) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(mContext, ThemeWebViewActivity.class);
		intent.putExtra("url", url);
		// 送手机活动，统计点击banner
		intent.putExtra("entry", GuiThemeStatistics.ENTRY_BANNER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
	

	/**
	 * 跳转到Android Market
	 * 
	 * @param uriString
	 *            market的uri
	 * @return 成功打开返回true
	 */
	public static boolean gotoMarket(Context context, String uriString) {
		boolean ret = false;
		Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
		marketIntent.setPackage(MarketConstant.PACKAGE);
		if (context instanceof Activity) {
			marketIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		} else {
			marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		try {
			context.startActivity(marketIntent);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(LogConstants.HEART_TAG, "gotoMarketForAPK error, uri = " + uriString);
			Toast.makeText(context, R.string.no_googlemarket_tip, Toast.LENGTH_SHORT).show();
		}
		return ret;
	}

	public static void sendIconNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId,
			int flags, RemoteViews contentView) {
		try {
			Notification notification = new Notification(iconId, tickerText,
					System.currentTimeMillis());
			
			PendingIntent contentIntent = null;
			if (notificationId == INotificationId.GOTO_THEME_PREVIEW
					|| notificationId == INotificationId.GOTO_LOCKERTHEME_PREVIEW) {
				contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				//手动清除时发送广播,引导用户购买付费版
				Intent delIntent = new Intent(NoAdvertCheckReceiver.NOADVERT_CHECK_ACTION);
				notification.deleteIntent = PendingIntent.getBroadcast(context, 0, delIntent, 0);
			} else {
				contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
			}
			notification.setLatestEventInfo(context, title, content, contentIntent);
			if (contentView != null) {
				notification.contentIntent = contentIntent;
				notification.contentView = contentView;
			}
			// 设置标志
			notification.flags |= flags;
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(notificationId, notification);
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "start notification error id = " + notificationId);
		}
	}

	/**
	 * 发送通知栏信息(点击后打开Activity)
	 * 
	 * @param context
	 * @param intent
	 * @param iconId
	 *            图标id
	 * @param tickerText
	 *            通知栏显示的信息
	 * @param title
	 *            展开通知栏后显示的标题
	 * @param content
	 *            展开通知栏显示的文字
	 * @param notificationId
	 *            通知消息id {@link INotificationId}, 全局唯一
	 * @param flags
	 * 			  标志
	 */
	public static void sendNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId,
			int flags) {
		sendIconNotification(context, intent, iconId, tickerText, title, content, notificationId,
				flags, null);
	}
	
	/**
	 * 自定义图片发送通知栏信息(点击后打开Activity)
	 * @param context
	 * @param intent
	 * @param iconId
	 * @param tickerText
	 * @param title
	 * @param content
	 * @param notificationId
	 * @param icon 
	 * 			展示图片
	 */
	public static void sendIconNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId,
			Bitmap icon) {
		if (icon != null) {
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.msg_center_noitify_content);
			contentView.setTextViewText(R.id.theme_title, title);
			contentView.setTextViewText(R.id.theme_content, content);
			contentView.setImageViewBitmap(R.id.theme_view_image, icon);
			sendIconNotification(context, intent, iconId, tickerText, title, content,
					notificationId, Notification.FLAG_AUTO_CANCEL, contentView);
		} else {
			sendIconNotification(context, intent, iconId, tickerText, title, content,
					notificationId, Notification.FLAG_AUTO_CANCEL, null);
		}
	}

	/**
	 * 发送通知栏信息(点击后打开Activity)
	 * 
	 * @param context
	 * @param intent
	 * @param iconId
	 *            图标id
	 * @param tickerText
	 *            通知栏显示的信息
	 * @param title
	 *            展开通知栏后显示的标题
	 * @param content
	 *            展开通知栏显示的文字
	 * @param notificationId
	 *            通知消息id {@link INotificationId}, 全局唯一
	 */
	public static void sendNotification(Context context, Intent intent, int iconId,
			CharSequence tickerText, CharSequence title, CharSequence content, int notificationId) {
		sendNotification(context, intent, iconId, tickerText, title, content, notificationId,
				Notification.FLAG_AUTO_CANCEL);
	}
	
	/**
	 * 优先跳转到market，如果失败则转到浏览器
	 * 
	 * @param context
	 * @param marketUrl
	 *            market地址
	 * @param browserUrl
	 *            浏览器地址
	 */
	public static void gotoBrowserIfFailtoMarket(Context context, String marketUrl,
			String browserUrl) {
		boolean toMarket = gotoMarket(context, marketUrl);
		if (!toMarket) {
			AppUtils.gotoBrowser(context, browserUrl);
		}
	}

	/**
	 * 发送短暂显示的通知栏信息
	 * 
	 * @param context
	 * @param iconId
	 *            　图标id
	 * @param tickerText
	 *            　通知栏显示的信息
	 * @param notificationId
	 *            　通知消息id {@link INotificationId}, 全局唯一
	 */
	public static void sendNotificationDisplaySeconds(Context context, int iconId,
			CharSequence tickerText, int notificationId) {
		sendNotification(context, null, iconId, tickerText, null, null, notificationId);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(notificationId);
	}
	
	/**
	 * 检查是安装某包,排除默认主题
	 * 
	 * @param context
	 * @param packageName
	 *            包名
	 * @return
	 */
	public static boolean isAppExist(final Context context, final String packageName) {
		if (null == packageName) {
			return false;
		}
		
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(packageName)) {
			return true;
		}
		
		return AppUtils.isAppExist(context, packageName);
	}


	public static void startSlideBar(Context context) {
		Intent serviceIntent = null;
		if (isAppExist(context, PackageName.PRIME_KEY)) {
			serviceIntent = new Intent();
			ComponentName com = new ComponentName(PackageName.PRIME_KEY,
					"com.jiubang.plugin.controller.AppService");
			serviceIntent.setComponent(com);
		} else if (isAppExist(context, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
			serviceIntent = new Intent();
			ComponentName com = new ComponentName(LauncherEnv.Plugin.PRIME_GETJAR_KEY,
					"com.jiubang.plugin.controller.AppService");
			serviceIntent.setComponent(com);
		}
		if (serviceIntent != null) {
			if (Build.VERSION.SDK_INT >= 12) {
				serviceIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			}
			context.startService(serviceIntent);
		}
	}

	
	public static void stopSlideBar(Context context) {
		Intent serviceIntent = null;
		if (isAppExist(context, PackageName.PRIME_KEY)) {
			serviceIntent = new Intent();
			ComponentName com = new ComponentName(PackageName.PRIME_KEY,
					"com.jiubang.plugin.controller.AppService");
			serviceIntent.setComponent(com);
		} else if (isAppExist(context, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
			serviceIntent = new Intent();
			ComponentName com = new ComponentName(LauncherEnv.Plugin.PRIME_GETJAR_KEY,
					"com.jiubang.plugin.controller.AppService");
			serviceIntent.setComponent(com);
		}
		if (serviceIntent != null) {
			context.stopService(serviceIntent);
		}
	}
	
	public static boolean isKittyplayExist(Context context) {
		return isAppExist(context, PackageName.KITTY_PLAY_PACKAGE_EX_NAME);
	}
	
	/**
	 * 是否存在老版的clean master
	 */
	public static boolean isOldCMExist(Context context) {
		boolean isOldCMExist = false;
		if (isAppExist(context, PackageName.CLEAN_MASTER_PACKAGE)) {
			int verCode = AppUtils.getVersionCodeByPkgName(context, PackageName.CLEAN_MASTER_PACKAGE);
			if (30500237 >= verCode) {
				// 30500237是CM提供的不带gowidget的versioncode基准
				isOldCMExist = true;
			}
		}
		return isOldCMExist;
	}
	
	/**
	 * 手机上是否有电子市场
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMarketExist(final Context context) {
		return isAppExist(context, MarketConstant.PACKAGE);
	}
	
	/**
	 * 是否国外用户或国内的有电子市场的用户，true for yes，or false for no
	 * 
	 * @author huyong
	 * @param context
	 * @return
	 */
	public static boolean isOverSeaOrExistMarket(Context context) {
		boolean result = false;
		boolean isCnUser = isCnUser(context);
		// 全部的国外用户 + 有电子市场的国内用户
		if (isCnUser) {
			// 是国内用户，则进一步判断是否有电子市场
			result = isMarketExist(context);
		} else {
			// 是国外用户
			result = true;
		}
		return result;
	}
	
	/**
	 * 判断是否为非中国大陆用户 针对GOStore而起的判断，必须要在拿到SIM的情况下，而且移动国家号不属于中国大陆
	 * 
	 * @return
	 */
	public static boolean isNotCnUser() {
		return !isCnUser(ApplicationProxy.getContext());
	}

	/**
	 * 判断是否中国大陆用户 该接口不推荐使用，请使用{@link #isCnUser(Context)}代替
	 * 
	 * @return
	 */
	@Deprecated
	public static boolean isCnUser() {
		return isCnUser(ApplicationProxy.getContext());
	}
	
	/**
	 * 因为主题2.0新起进程，无法获取GoLauncher.getContext()， 所以重载此方法，以便主题2.0调用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isCnUser(Context context) {
		boolean result = false;

		if (context != null) {
			// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// SIM卡状态
			boolean simCardUnable = manager.getSimState() != TelephonyManager.SIM_STATE_READY;
			String simOperator = manager.getSimOperator();

			if (simCardUnable || TextUtils.isEmpty(simOperator)) {
				// 如果没有SIM卡的话simOperator为null，然后获取本地信息进行判断处理
				// 获取当前国家或地区，如果当前手机设置为简体中文-中国，则使用此方法返回CN
				String curCountry = Locale.getDefault().getCountry();
				if (curCountry != null && curCountry.contains("CN")) {
					// 如果获取的国家信息是CN，则返回TRUE
					result = true;
				} else {
					// 如果获取不到国家信息，或者国家信息不是CN
					result = false;
				}
			} else if (simOperator.startsWith("460")) {
				// 如果有SIM卡，并且获取到simOperator信息。
				/**
				 * 中国大陆的前5位是(46000) 中国移动：46000、46002 中国联通：46001 中国电信：46003
				 */
				result = true;
			}
		}

		return result;
	}

	/**
	 * 用Intent方法判断GO锁屏是否存在
	 * @param context
	 * @return
	 */
	public static boolean isGoLockerExist(Context context) {
		Intent golockerIntent = new Intent(ICustomAction.ACTION_LOCKER);
		boolean isExist = false;
		if (AppUtils.isAppExist(context, golockerIntent)) {
			isExist = true;
		}
		return isExist;
	}
	
	/**
	 * 是否为GO桌面主题
	 * @param packageName
	 * @return
	 */
	public static boolean isGOTheme(String packageName) {
		return packageName != null
				&& (packageName.startsWith(IGoLauncherClassName.MAIN_THEME_PACKAGE) || packageName
						.contains(IGoLauncherClassName.OLD_THEME_PACKAGE));
	}
	
	/**
	 * 是否为GO桌面美化版
	 * @param packageName
	 * @return
	 */
	public static boolean isGOLauncherReplaceTheme(Context context, String packageName) {
		if (isGOTheme(packageName)) {
			Intent intentTheme = new Intent(IGoLauncherClassName.MAIN_THEME_PACKAGE);
			intentTheme.addCategory(ThemeManager.THEME_CATEGORY);
			intentTheme.setPackage(packageName);
			List<ResolveInfo> themes = context.getPackageManager().queryIntentActivities(
					intentTheme, 0);
			return themes.isEmpty();
		} else {
			return false;
		}
	}

	// 跳转FTP下载
	private static void gotoDownloadHDLauncher(final Context context) {
		DeskBuilder builder = new DeskBuilder(context);
		builder.setTitle(context.getString(R.string.attention_title));
		builder.setMessage(context.getString(R.string.hdlauncher_tip_message));
		builder.setPositiveButton(context.getString(R.string.ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AppUtils.gotoBrowser(context, LauncherEnv.Url.GOHDLAUNCHER_DOWNLOAD_URL);
			}
		});
		builder.setNegativeButton(context.getString(R.string.cancel), null);
		builder.create().show();
	}

	/**
	 * 获取安装在手机内所有GO桌面的包名
	 * @param context
	 * @return
	 */
	public static List<String> getAllGoLauncherPackageNames(Context context) {
		List<String> packageNames = null;
		Intent intent = new Intent(ICustomAction.ACTION_GOLAUNCHER);
		List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent,
				0);
		if (resolveInfos != null && !resolveInfos.isEmpty()) {
			int size = resolveInfos.size();
			packageNames = new ArrayList<String>(size);
			for (ResolveInfo resolveInfo : resolveInfos) {
				packageNames.add(resolveInfo.activityInfo.packageName);
			}
		}
		return packageNames;
	}
	
	/**
	 * 
	 * 后台自动上传log信息到服务器
	 * @param tag 分类信息，可用来做信息分类
	 * @param logInfo 具体log信息，全部的自定义与堆栈信息用该字段输出
	 */
	public static void postLogInfo(final String tag, final String logInfo) {
		try {
			String baseInfo = AppUtils.getBaseDeviceInfo();
			String postInfo = baseInfo + "\nlogtag=" + tag + "\nlog=" + logInfo;
			String postInfo2 = postInfo.replaceAll("\r\n", "==").replaceAll("\n\t", "@@").replaceAll("\n", "#");
			GuiThemeStatistics.autoPostLogInfo(tag, postInfo2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 后台自动上传log信息到服务器，将当前调用堆栈信息捕获出来
	 * @param tag 分类信息，可用来做信息分类，便于查找
	 * @param tag
	 */
	public static void postLogWhoCallme(final String tag) {
		Exception p = new Exception("whoCallme");
		postLogInfo("autoPostWhoCallme", Log.getStackTraceString(p));
	}

	/**
	 * <br>功能简述:200渠道同时没有电子市场
	 * <br>功能详细描述:用来判断是否需要推荐广告。
	 * <br>注意:
	 * @param context
	 * @return
	 */
    public static boolean isMarketNotExitAnd200Channel(Context context) {

        boolean result = false;
        if (!GoStoreAppInforUtil.isExistGoogleMarket(context)
                && GoStorePhoneStateUtil.is200ChannelUid(context)) {
            result = true;
        }

        return result;
    }
    
    /**
	 * <br>功能简述:跳转主题专题页
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param id 专题ID
	 */
	public static void goToThemeSpec(Context context, int id, boolean fromMsg) {
		try {
			Intent intent = new Intent();
			intent.putExtra("ty", id);
			intent.putExtra("entrance", fromMsg);
			intent.setClass(context, BannerDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <br>功能简述:不弹浏览器选择框，直接开网页
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param mContext
	 * @param uriString
	 * @param filterPkgs 可以为null
	 */
	public static void gotoDefaultBrowser(Context mContext, String uriString, String[] filterPkgs) {
	}
	
	public static int getShowZeroScreenTimes(Context context) {
		return 0;
	}


	public static void setShowZeroScreenTimes(Context context, int count) {
	}
	
	/**
	 * 卸载程序
	 * 
	 * @param context
	 *            上下文
	 * @param packageURI
	 *            需要卸载的程序的Uri
	 */
	public static void uninstallApp(Context context, Uri packageURI) {
		// 记录由桌面产生的卸载次数，用于clean master弹框
		PreferencesManager pm = new PreferencesManager(context, IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_NEW, Context.MODE_PRIVATE);
		int count = pm.getInt(IPreferencesIds.PREFERENCE_UNINSTALL_COUNT_FOR_CM, 0);
		if (count < 3) {
			count = count + 1;
			pm.putInt(IPreferencesIds.PREFERENCE_UNINSTALL_COUNT_FOR_CM, count);
			pm.commit();
		}
		pm = null;
		
		AppUtils.uninstallApp(context, packageURI);
	}
}
