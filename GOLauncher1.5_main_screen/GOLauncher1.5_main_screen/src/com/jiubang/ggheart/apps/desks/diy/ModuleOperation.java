package com.jiubang.ggheart.apps.desks.diy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.AppUtils;
import com.go.util.GotoMarketIgnoreBrowserTask;
import com.go.util.file.FileUtil;
import com.go.util.log.LogConstants;
import com.go.util.market.MarketConstant;
import com.go.util.window.WindowControl;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.golauncher.message.IScreenAdvertMsgId;
import com.golauncher.message.IScreenFrameMsgId;
import com.golauncher.message.IWidgetMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.component.AppsManagementActivity;
import com.jiubang.ggheart.appgame.base.component.MainViewGroup;
import com.jiubang.ggheart.appgame.recommend.AppKitsActivity;
import com.jiubang.ggheart.apps.appfunc.controler.SwitchControler;
import com.jiubang.ggheart.apps.config.ChannelConfig;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncUtils;
import com.jiubang.ggheart.apps.desks.diy.AppInvoker.IAppInvokerListener;
import com.jiubang.ggheart.apps.desks.diy.frames.dock.DockConstants;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.controller.ScreenControler;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeManageActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeManageView;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.components.advert.AdvertConstants;
import com.jiubang.ggheart.components.diygesture.gesturemanageview.DiyGestureRecogniser;
import com.jiubang.ggheart.components.diygesture.model.DiyGestureModelImpl;
import com.jiubang.ggheart.components.gohandbook.GoHandBookMainActivity;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.statistics.AppManagementStatisticsUtil;
import com.jiubang.ggheart.data.statistics.AppRecommendedStatisticsUtil;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.StatisticsAppsInfoData;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsContants;
import com.jiubang.ggheart.launcher.CheckApplication;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.mediamanagement.MediaPluginFactory;
import com.jiubang.ggheart.plugin.notification.NotificationControler;

/**
 * define MessageOperation one method represent one module, which can be
 * abstract in future
 * 
 * @author Hanson
 * 
 */

public class ModuleOperation implements IOperation {

	private Intent mLaunchIntent;
	ArrayList<IAppInvokerListener> mListener;
	int mFrom;
	boolean mIsCnUser;

	public void setmCommandParam(CommandParam param) {
		this.mLaunchIntent = param.getmLaunchIntent();
		this.mListener = param.getmListener();
		this.mFrom = param.getmFrom();
		this.mIsCnUser = param.mIsCnUser;
	}

	/*
	 * do not revise the following name as it is the method name invoked by
	 * Command class
	 */
	public static final String MOPENFUNCTIONMENU = "openFunctionMenu";
	public static final String MOPENNOTHING = "openNothing";
	public static final String MOPENSHOWHIDESTATUSBAR = "openShowHideStatusBar";
	public static final String MOPENSHOWMAINSCREENORPREVIEW = "openShowMainScreenOrPreview";
	public static final String MOPENSHOWMAINSCREEN = "openShowMainScreen";
	public static final String MOPENSHOWPREVIEW = "openShowPreview";
	public static final String MOPENSHOWMENU = "openShowMenu";
	public static final String MOPENTURNSCREEN = "openTurnScreen";
	public static final String MOPENEXPENDBAR = "openExpendBar";
	public static final String MOPENSPECIALAPPGOTHEME = "openSpecialAppGoTheme";
	public static final String MOPENSPECIALAPPGOSTORE = "openSpecialAppGoStore";
	public static final String MOPENSPECIALAPPGOWIDGET = "openSpecialAppGoWidget";
	public static final String MOPENSHOWPREFERENCES = "openShowPreferences";
	public static final String MOPENSHOWFUNCTIONMENUFORLAUNCHER = "openShowFunctionMenuForLauncher";
	public static final String MOPENSHOWLOCKERSETTING = "openShowLockerSetting";
	public static final String MOPENSCREENGUARD = "openScreenGuard";
	public static final String MOPENSHOWDOCK = "openShowDock";
	public static final String MOPENSCREENGOLOCKERTHEME = "openScreenGoLockerTheme";
	public static final String MOPENRECOMMENDDOWNLOAD = "openRecommendDownLoad";
	public static final String MOPENFUNCSPECIALAPPGOSTORETHEME = "openFuncSpecialAppGoStoreTheme";
	public static final String MOPENSHOWRECOMMENDLIST = "openShowRecommendList";
	public static final String MOPENSHOWRECOMMENDGAME = "openShowRecommendGame";
	public static final String MOPENSHOWRECOMMENDCENTER = "openShowRecommendCenter";
	public static final String MOPENSHOWEVERNOTE = "openShowEverNote";
	public static final String MOPENRECOMMANDGOSMS = "openRecommandGoSMS";
	public static final String MOPENRECOMMANDGOPOWERMASTER = "openRecommandGoPowerMaster";
	public static final String MOPENRECOMMANDGOTASKMANAGER = "openRecommandGoTaskManager";
	public static final String MOPENRECOMMANDGOKEYBOARD = "openRecommandGoKeyBoard";
	public static final String MOPENRECOMMANDGOLOCKER = "openRecommandGoLocker";
	public static final String MOPENRECOMMANDGOBACKUP = "openRecommandGoBackup";
	public static final String MOPENRECOMMANDGOWEATHER = "openRecommandGoWeather";
	public static final String MOPENSHOWDIYGESTURE = "openShowDIYGesture";
	public static final String MOPENSHOWGOHANDBOOK = "openShowGOHandBook";
	public static final String MOPENSHOWPHOTO = "openShowPhoto";
	public static final String MOPENSHOWMUSIC = "openShowMusic";
	public static final String MOPENSHOWVIDEO = "openShowVideo";
	public static final String MOPENRECOMMANDLOCKSCREEN = "openRecommandLockScreen";
	public static final String MOPENRECOMMANDMEDIAPLUGIN = "openRecommandMediaPlugin";
	public static final String MOPENFREETHEMEICON = "openFreeThemeIcon";
	public static final String MOPENSCREENADVERT = "openScreenAdvert";
	public static final String MOPENSHOWBAIDUBROWSER = "openShowBaiduBrowser";
	public static final String MOPENSHOWBAIDUBATTERYSAVER = "openShowBaiduBatterySave";
//	public static final String MOPENPROGMANAGE = "openProManage";
//	public static final String MOPENRECENTAPP = "openRecentApp";

	public ModuleOperation() {

	}

	public ModuleOperation(Intent launchIntent,
			ArrayList<IAppInvokerListener> mListener, int from, boolean isCnUser) {

		this.mLaunchIntent = launchIntent;
		this.mListener = mListener;
		this.mFrom = from;
		this.mIsCnUser = isCnUser;
	}

	public boolean openFunctionMenu(Activity activity) {
		Log.i("Test", "openFunctionMenu");
		// 打开功能表
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
				ICommonMsgId.SHOW_APP_DRAWER, -1, null, null);
		return true;
	}

	public boolean openNothing(Activity mActivity) {
		// do nothing
		return true;
	}

	public boolean openShowHideStatusBar(Activity mActivity) {
		// 显示/隐藏状态栏
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
				ICommonMsgId.SHOW_HIDE_STATUSBAR, -1, null, null);
		return true;
	}

	public boolean openShowMainScreenOrPreview(Activity mActivity) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_SHOW_MAIN_SCREEN_OR_PREVIEW, -1, null, null);
		return true;
	}

	public boolean openShowMainScreen(Activity mActivity) {
		// 防止go快捷方式图标,DOCKgo快捷方式,Dock手势,自定义手势 中相应跳主屏幕功能失效
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_SHOW_HOME, -1, null, null);
		return true;
	}

	public boolean openShowPreview(Activity mActivity) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_SHOW_PREVIEW, -1, false, null);
		return true;
	}

	public boolean openExpendBar(Activity activity) {
		try {
			// 判断是否启动了3D引擎
			WindowControl.setIsFullScreen(activity, false, true);
			WindowControl.expendNotification(activity);
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, e.toString());
		}
		return true;
	}

	public boolean openSpecialAppGoTheme(Activity mActivity) {
		// 功能表中go主题假图标被点击
		// MyThemes Process
		NotificationControler control = AppCore.getInstance()
				.getNotificationControler();
		control.updataGoTheme(ThemeConstants.LAUNCHER_FEATURED_THEME_ID, 0);
		Intent mythemesIntent = new Intent();
		mythemesIntent.putExtra("entrance",
				ThemeManageView.LAUNCHER_THEME_VIEW_ID);
		mythemesIntent.setClass(mActivity, ThemeManageActivity.class);
		mActivity.startActivity(mythemesIntent);
		addAppActiveCount(mLaunchIntent, mActivity);
		// GuiThemeStatistics.setCurrentEntry(GuiThemeStatistics.ENTRY_DESK_ICON,
		// mActivity);
		for (IAppInvokerListener listener : mListener) {
			if (listener instanceof NotificationControler) {
				listener.onInvokeApp(mLaunchIntent);
			}
		}
		GuiThemeStatistics.guiStaticData("",
				GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
				String.valueOf(GuiThemeStatistics.ENTRY_DESK_ICON), "", "", "");
		return true;
	}

	public boolean openSpecialAppGoStore(Activity mActivity) {
		// 功能表中go精品假图标被点击
		// Intent intent = new Intent();
		// intent.setClass(mActivity, GoStore.class);
		// mActivity.startActivity(intent);
		int currententer = mLaunchIntent.getByteExtra("currententer",
				AppRecommendedStatisticsUtil.ENTRY_TYPE_APPFUNC_ICO_GOSTORE);
		int entrance = mLaunchIntent.getIntExtra("entrance",
				RealTimeStatisticsContants.AppgameEntrance.FUNC_GOSTORE);
		AppRecommendedStatisticsUtil.getInstance().saveCurrentEnter(
				ApplicationProxy.getContext(), currententer);
		if (entrance == RealTimeStatisticsContants.AppgameEntrance.FUNC_TOP_BAR_ICON) {
			AppsManagementActivity.startAppCenter(GoLauncherActivityProxy.getActivity(),
					MainViewGroup.ACCESS_FOR_SHORTCUT, false, entrance);
		} else if (ChannelConfig.getInstance(mActivity).isNeedAppCenter()) {
			AppsManagementActivity.startAppCenter(GoLauncherActivityProxy.getActivity(),
					MainViewGroup.ACCESS_FOR_APPCENTER_THEME, false, entrance);
		} else {
			AppsManagementActivity.startAppCenter(GoLauncherActivityProxy.getActivity(),
					MainViewGroup.ACCESS_FOR_APPCENTER_RECOMMEND, false,
					entrance);
		}
		addAppActiveCount(mLaunchIntent, mActivity);

		return true;
	}

	public boolean openShowPreferences(Activity mActivity) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.PREFERENCES, -1,
				null, null);
		return true;
	}

	public boolean openShowFunctionMenuForLauncher(Activity mActivity) {

		// 打开功能表
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
				ICommonMsgId.SHOW_APP_DRAWER, -1, null, null);
		return true;
	}

	public boolean openShowLockerSetting(Activity mActivity) {
		// GO锁屏设置
		try {
			mActivity.startActivity(new Intent(
					ICustomAction.ACTION_LOCKER_SETTING));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}

	public boolean openShowDock(Activity mActivity) {
		// 显示隐藏DOCK
		if (ShortCutSettingInfo.sEnable) {
			final int type = DiyGestureModelImpl.sOpeningActivityFlag == 0
					? DockConstants.HIDE_ANIMATION
					: DockConstants.HIDE_ANIMATION_NO;
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, IDockMsgId.DOCK_HIDE, type,
					null, null);
		} else {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, IDockMsgId.DOCK_SHOW,
					DockConstants.HIDE_ANIMATION, null, null);
		}
		return true;
	}

	public boolean openScreenGuard(Activity mActivity) {
		// 锁屏
		if (AppUtils.isAppExist(mActivity, ICustomAction.PKG_GOWIDGET_SWITCH)) {
			PackageManager manager = mActivity.getPackageManager();
			try {
				PackageInfo info = manager.getPackageInfo(
						ICustomAction.PKG_GOWIDGET_SWITCH, 0);
				if (info != null && info.versionCode >= 18) {
					Intent it = new Intent(ICustomAction.ACTION_SWITCH_SERVICE);
					Bundle bundle = new Bundle();
					bundle.putInt("switchId", 12);
					it.putExtras(bundle);
					// 启动锁屏开关
					mActivity.startService(it);
				} else {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
							IWidgetMsgId.SHOW_DOWNLOAD_GOSWITCHWIDGET_DIALOG, 1,
							null, null);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					IWidgetMsgId.SHOW_DOWNLOAD_GOSWITCHWIDGET_DIALOG, 0, null,
					null);
		}

		// GoLauncher.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
		// IDiyMsgIds.ENABLE_KEYGUARD, -1, null, null);
		return true;
	}

	public boolean openScreenGoLockerTheme(Activity mActivity) {
		// golocker主题
		GoAppUtils.gotoGolocker(mActivity);
		return true;
	}

	public boolean openRecommendDownLoad(Activity mActivity) {
		StatisticsData.updateAppClickData(mActivity.getApplicationContext(),
				PackageName.RECOMMEND_NETQIN_PACKAGE);
		if (GoAppUtils.isMarketExist(mActivity)) {
			GoAppUtils.gotoMarket(mActivity, MarketConstant.APP_DETAIL
					+ PackageName.RECOMMEND_NETQIN_PACKAGE
					+ LauncherEnv.Plugin.RECOMMEND_GOOGLE_REFERRAL_LINK);
		} else {
			AppUtils.gotoBrowser(mActivity,
					MarketConstant.BROWSER_APP_DETAIL
							+ PackageName.RECOMMEND_NETQIN_PACKAGE);
		}
		return true;
	}

	public boolean openFuncSpecialAppGoStoreTheme(Activity mActivity) {
		// if
		// (ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE_THEME.equals(action))
		// {
		// Intent it = new Intent(mActivity, GoStore.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("sort", String.valueOf(SortsBean.SORT_THEME));
		// it.putExtras(bundle);
		// mActivity.startActivity(it);
		// StatisticsData.countStatData(mActivity,
		// StatisticsData.ENTRY_KEY_GOFOLDER);
		// GoStoreStatisticsUtil
		// .setCurrentEntry(GoStoreStatisticsUtil.ENTRY_TYPE_THEME, mActivity);
		AppsManagementActivity.startAppCenter(GoLauncherActivityProxy.getActivity(),
				MainViewGroup.ACCESS_FOR_APPCENTER_THEME, false);
		StatisticsData.countStatData(mActivity,
				StatisticsData.ENTRY_KEY_GOFOLDER);
		StatisticsAppsInfoData.addAppInfoClickedCount(mLaunchIntent, mActivity);
		return true;
	}

	public boolean openShowRecommendList(Activity activity) {
		// if (ICustomAction.ACTION_SHOW_RECOMMENDLIST.equals(action)
		// || ICustomAction.ACTION_FUNC_SHOW_RECOMMENDLIST.equals(action)) {
		// 桌面快捷方式点击进入装机必备
		ChannelConfig channelConfig = ChannelConfig.getInstance(activity);
		if (channelConfig != null && channelConfig.isNeedAppsKit()) {
			// 根据渠道配置信息，如果本渠道需要有装机必备
			AppManagementStatisticsUtil.getInstance().saveCurrentEnter(
					activity, AppManagementStatisticsUtil.ENTRY_TYPE_DESK);
			StatisticsData.updateAppClickData(activity,
					mLaunchIntent.getAction());
			Intent intent = new Intent(activity, AppKitsActivity.class);
			intent.putExtra(AppKitsActivity.ENTRANCE_KEY,
					AppKitsActivity.ENTRANCE_ID_SHORTCUTS);
			activity.startActivity(intent);
			// 一键装机增加点击数
			StatisticsAppsInfoData.addAppInfoClickedCount(mLaunchIntent,
					activity);
		} else {
			// 如果本渠道不需要装机必备，提示找不到该应用
			// 主要是针对从有用户从有装机/玩机必备的渠道包，升级到没有装机/玩机必备的渠道包
			// 这时不能让它启动
			// Add by wangzhuobin 2012.08.10
			DeskToast.makeText(activity, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	public boolean openShowRecommendGame(Activity activity) {
		// if (ICustomAction.ACTION_SHOW_RECOMMENDGAME.equals(action)
		// || ICustomAction.ACTION_FUNC_SHOW_RECOMMENDGAME.equals(action)) {
		// 桌面或者功能表点击进入应用中心
		ChannelConfig channelConfig = ChannelConfig.getInstance(activity);
		if (channelConfig != null && channelConfig.isNeedAppCenter()) {
			// 根据渠道配置信息，如果本渠道需要有应用中心
			AppManagementStatisticsUtil.getInstance().saveCurrentEnter(
					activity,
					AppManagementStatisticsUtil.ENTRY_TYPE_APPFUNC_ICON);
			int entrance = -1;
			// 需要区分桌面进来的还是功能表
			if (ICustomAction.ACTION_FUNC_SHOW_RECOMMENDGAME
					.equals(mLaunchIntent.getAction())) {
				entrance = RealTimeStatisticsContants.AppgameEntrance.FUNC_ICON;
			} else {
				entrance = RealTimeStatisticsContants.AppgameEntrance.DESK_ICON;
			}
			AppsManagementActivity.startAppCenter(activity,
					MainViewGroup.ACCESS_FOR_SHORTCUT, true, entrance);

			// 应用中心增加点击数
			StatisticsAppsInfoData.addAppInfoClickedCount(mLaunchIntent,
					activity);
		} else {
			// 如果本渠道不需要应用中心，提示找不到该应用
			// 主要是针对从有用户从有应用中心的渠道包，升级到没有应用中心的渠道包
			// 这时不能让它启动
			// Add by wangzhuobin 2012.08.18
			DeskToast.makeText(activity, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	public boolean openShowRecommendCenter(Activity activity) {
		// if (ICustomAction.ACTION_SHOW_RECOMMENDCENTER.equals(action)
		// || ICustomAction.ACTION_FUNC_SHOW_RECOMMENDCENTER.equals(action)) {
		// 桌面或者功能表点击进入应用中心
		ChannelConfig channelConfig = ChannelConfig.getInstance(activity);
		if (channelConfig != null && channelConfig.isNeedAppCenter()) {
			// 根据渠道配置信息，如果本渠道需要有应用中心
			AppManagementStatisticsUtil.getInstance().saveCurrentEnter(
					activity,
					AppManagementStatisticsUtil.ENTRY_TYPE_APPFUNC_ICON);
			int entrance = -1;
			// 需要区分桌面进来的还是功能表
			if (mFrom == AppInvoker.TYPE_SCREEN) {
				// 桌面打开
				entrance = RealTimeStatisticsContants.AppgameEntrance.DESK_APP_GOMARKET_ICON;
			} else if (mFrom == AppInvoker.TYPE_APPDRAWER) {
				// 功能表打开
				entrance = RealTimeStatisticsContants.AppgameEntrance.FUNC_ICON;
			}
			AppsManagementActivity.startAppCenter(activity,
					MainViewGroup.ACCESS_FOR_SHORTCUT, true, entrance);

			// 应用中心增加点击数
			StatisticsAppsInfoData.addAppInfoClickedCount(mLaunchIntent,
					activity);
		} else {
			// 如果本渠道不需要应用中心，提示找不到该应用
			// 主要是针对从有用户从有应用中心的渠道包，升级到没有应用中心的渠道包
			// 这时不能让它启动
			// Add by wangzhuobin 2012.08.18
			DeskToast.makeText(activity, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	public boolean openShowEverNote(Activity mActivity) {
		StatisticsData.updateAppClickData(mActivity.getApplicationContext(),
				PackageName.RECOMMEND_EVERNOTE_PACKAGE);
		FileUtil.saveByteToSDFile(LauncherEnv.EVERNOTE_STRING.getBytes(),
				LauncherEnv.EVERNOTE_FILE_NAME);

		if (GoAppUtils.isMarketExist(mActivity)) {
			GoAppUtils.gotoMarket(mActivity, MarketConstant.APP_DETAIL
					+ PackageName.RECOMMEND_EVERNOTE_PACKAGE
					+ LauncherEnv.GOLAUNCHER_FOREVERNOTE_GOOGLE_REFERRAL_LINK);
		} else {
			AppUtils.gotoBrowser(
					mActivity,
					MarketConstant.BROWSER_APP_DETAIL
							+ PackageName.RECOMMEND_EVERNOTE_PACKAGE
							+ LauncherEnv.GOLAUNCHER_FOREVERNOTE_GOOGLE_REFERRAL_LINK);
		}
		return true;
	}

	public boolean openRecommandGoSMS(Activity mActivity) {
		// go短信
		String title = mActivity.getString(R.string.recommand_gosms);
		String content = mActivity.getString(R.string.recommend_gosms_detail);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openRecommandGoPowerMaster(Activity mActivity) {
		// go省电
		String title = mActivity.getString(R.string.recommand_gopowermaster);
		String content = mActivity
				.getString(R.string.recommend_gopowermaster_detail);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openRecommandGoTaskManager(Activity mActivity) {
		// go任务管理器
		String title = mActivity.getString(R.string.recommand_gotaskmanager);
		String content = mActivity
				.getString(R.string.recommend_gotaskmanager_detail);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openRecommandGoKeyBoard(Activity mActivity) {
		// go输入法
		String title = mActivity.getString(R.string.recommand_gokeyboard);
		String content = mActivity
				.getString(R.string.recommend_gokeyboard_detail);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openRecommandGoLocker(Activity mActivity) {
		// go锁屏
		String title = mActivity.getString(R.string.customname_golocker);
		String content = mActivity
				.getString(R.string.recommend_golocker_detail);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}
	
	public boolean openRecommandGoBackup(Activity mActivity) {
		// go备份
		String title = mActivity.getString(R.string.recommand_gobackup);
		String content = mActivity
				.getString(R.string.recommend_gobackup_detail);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openRecommandGoWeather(Activity mActivity) {
		// go天气
		String title = mActivity.getString(R.string.recommand_goweatherex);
		String content = mActivity
				.getString(R.string.recommend_goweatherex_detail);
		if (AppUtils.isAppExist(mActivity,
				PackageName.RECOMMAND_GOWEATHEREX_PACKAGE)
				&& (AppUtils.getVersionCodeByPkgName(mActivity,
						PackageName.RECOMMAND_GOWEATHEREX_PACKAGE) < 10)) {
			// 如果go天气已安装且版本号低于10，点击图标后，提示用户要可更新
			content = mActivity.getString(R.string.fav_update);
		}
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openShowDIYGesture(Activity mActivity) {
		Intent intent = new Intent(mActivity, DiyGestureRecogniser.class);
		mActivity.startActivity(intent);

		return true;
	}

	public boolean openShowGOHandBook(Activity mActivity) {
		Intent intent = new Intent(mActivity, GoHandBookMainActivity.class);
		mActivity.startActivity(intent);
		return true;
	}

	public boolean openShowPhoto(Activity mActivity) {
		if (MediaPluginFactory.isMediaPluginExist(mActivity)) {
			if (AppFuncUtils.getInstance(mActivity).isMediaPluginCompatible()) {
				// 打开功能表并进入图片管理界面
				StatisticsData.countMenuData(mActivity,
						StatisticsData.FUNTAB_KEY_IMAGE);
				SwitchControler.getInstance(mActivity)
						.showMediaManagementImageContent();
			}
		} else {
			showMedPlugDownDialog(mActivity);
		}
		return true;
	}

	public boolean openShowMusic(Activity mActivity) {
		if (MediaPluginFactory.isMediaPluginExist(mActivity)) {
			if (AppFuncUtils.getInstance(mActivity).isMediaPluginCompatible()) {
				// 打开功能表并进入音乐管理界面
				StatisticsData.countMenuData(mActivity,
						StatisticsData.FUNTAB_KEY_AUDIO);
				SwitchControler.getInstance(mActivity)
						.showMediaManagementMusicContent();
			}
		} else {
			showMedPlugDownDialog(mActivity);
		}
		return true;
	}

	public boolean openShowVideo(Activity mActivity) {
		if (MediaPluginFactory.isMediaPluginExist(mActivity)) {
			if (AppFuncUtils.getInstance(mActivity).isMediaPluginCompatible()) {
				// 打开功能表并进入视频管理界面
				StatisticsData.countMenuData(mActivity,
						StatisticsData.FUNTAB_KEY_VIDEO);
				SwitchControler.getInstance(mActivity)
						.showMediaManagementVideoContent();
			}
		} else {
			showMedPlugDownDialog(mActivity);
		}
		return true;
	}

	public boolean openRecommandLockScreen(Activity mActivity) {
		// 一键锁屏
		String title = mActivity.getString(R.string.recommand_lockscreen);
		String content = mActivity
				.getString(R.string.recommend_lockscreen_detail);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray, LauncherEnv.LOCK_SCREEN_REFERRAL_LINK, title,
				System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openRecommandMediaPlugin(Activity mActivity) {
		// 资源管理插件
		String title = mActivity.getString(R.string.recommend_media_plugin);
		String content = mActivity.getString(R.string.fav_app);
		String[] linkArray = {
				mLaunchIntent.getComponent().getPackageName(),
				mLaunchIntent
						.getStringExtra(ScreenControler.RECOMMAND_APP_FTP_URL_KEY) };
		CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content,
				linkArray,
				LauncherEnv.Plugin.RECOMMEND_MEDIA_PLUGIN_GOOGLE_REFERRAL_LINK,
				title, System.currentTimeMillis(), mIsCnUser,
				CheckApplication.FROM_GO_FOLDER, 0, null);
		return true;
	}

	public boolean openFreeThemeIcon(Activity mActivity) {
		GoAppUtils.goToThemeSpec(mActivity, 274, false);
		// Intent intent = new Intent();
		// intent.putExtra("ty", Integer.valueOf("274"));//專題id
		// intent.putExtra("entrance", true);//專題名稱
		// intent.setClass(mActivity, BannerDetailActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// mActivity.startActivity(intent);
		return true;
	}

	private void addAppActiveCount(final Intent intent, Activity mActivity) {
		AppItemInfo info = AppDataEngine.getInstance(mActivity).getAppItem(
				intent);
		if (info != null) {
			info.addActiveCount(mActivity, 1);
		}
		StatisticsAppsInfoData.addAppInfoClickedCount(intent, mActivity);
	}	
	
	// 15屏的广告
	public boolean openScreenAdvert(Activity mActivity) {
		String packageNameString = mLaunchIntent
				.getStringExtra(AdvertConstants.ADVERT_PACK_NAME);

		if (packageNameString == null) {
			return false;
		}

		String idString = mLaunchIntent
				.getStringExtra(AdvertConstants.ADVERT_ID);
		String clickUrl = mLaunchIntent
				.getStringExtra(AdvertConstants.ADVERT_CLICK_URL);
		String mapId = mLaunchIntent
				.getStringExtra(AdvertConstants.ADVERT_MAPID);

		// 统计
		if (packageNameString != null && !packageNameString.equals("")) {
			StatisticsData.updateAppClickData(mActivity, packageNameString,
					AdvertConstants.ADVERT_STATISTICS_TYPE, mapId, idString);
		}

		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
				IScreenAdvertMsgId.REQUEST_ADVERT_STAT_CLICK_ACTION, -1, packageNameString, idString,
				clickUrl, mapId);
		
		String title = mLaunchIntent
				.getStringExtra(AdvertConstants.ADVERT_TITLE);
		// 增加detail描述 add by Ryan
		String detail = mLaunchIntent
				.getStringExtra(AdvertConstants.ADVERT_DETAIL);
		if (detail == null || detail.equals("")) {
			detail = mActivity.getString(R.string.fav_app);
		}
		// end
		int actType = mLaunchIntent.getIntExtra(AdvertConstants.ADVERT_ACTTYPE,
				-1);
		String downloadUrl = mLaunchIntent
				.getStringExtra(AdvertConstants.ADVERT_ACTVALUE);
		String[] linkArray = new String[2];
		String googleLink = null; // google分析链接
		linkArray[0] = packageNameString;

		if (downloadUrl != null && !downloadUrl.equals("")) {
			if (downloadUrl.startsWith(ConstValue.PREFIX_MARKET)) {
				String subString = ConstValue.PREFIX_MARKET + packageNameString;
				googleLink = downloadUrl.substring(subString.length());
			} else if (downloadUrl.startsWith(MarketConstant.APP_DETAIL)) {
				String subString = MarketConstant.APP_DETAIL + packageNameString;
				googleLink = downloadUrl.substring(subString.length());
			}

			if (downloadUrl.startsWith(ConstValue.PREFIX_HTTP)) {
				linkArray[1] = downloadUrl;
			} else {
				linkArray[1] = null;
			}

			// 根据类型判断，wap和webview的跳转浏览器，FTP直接下载
			if (actType == AdvertConstants.ACTTYPE_WAP
					|| actType == AdvertConstants.ACTTYPE_WEBVIEW) {
				// V4.15需求，先判断URL是否为302跳转类型地址
				String url = linkArray[1];
				if (GotoMarketIgnoreBrowserTask.isRedirectUrl(url)) {
					GotoMarketIgnoreBrowserTask.startExecuteTask(mActivity, url);
				} else {
					GoAppUtils.gotoBrowserInRunTask(mActivity, url);
//					startBrowser(mActivity, url);
				}
//				startBrowser(mActivity, linkArray[1]);
			} else {
				CheckApplication.downloadAppFromMarketFTPGostore(mActivity,
						detail, linkArray, googleLink, title,
						System.currentTimeMillis(), mIsCnUser,
						CheckApplication.FROM_RECOMMEND_APP, 0, null);
			}
		}

		return true;
	}

	public boolean openShowBaiduBrowser(Activity mActivity) {
		// 百度浏览器相关的实时统计信息
		String id = "85"; // 广告id
		String mapId = "2369953"; // 统计id
		// 回调url
		String clickUrl = "http://69.28.52.42:8090/recommendedapp/manage/appcallback.action?cburl=&ctype=0&pname=com.baidu.browser.inter&uid=&aid=&from=golaunchermsgAdv&mapid=2369953&corpid=2&referrer=utm_source%3Dgo_launcher%26utm_medium%3Dhyperlink%26utm_campaign%3Dhomescreen_ads";

		StatisticsData.updateAppClickData(mActivity.getApplicationContext(),
				PackageName.RECOMMEND_BAIDUBROWSER_PACKAGE,
				AdvertConstants.ADVERT_STATISTICS_TYPE, mapId, id);

		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
				IScreenAdvertMsgId.REQUEST_ADVERT_STAT_CLICK_ACTION, -1, 
				PackageName.RECOMMEND_BAIDUBROWSER_PACKAGE, id,
				clickUrl, mapId);
		AppUtils.gotoBrowser(mActivity, LauncherEnv.Url.BAIDU_BROWSER_URL);

		return true;
	}

	public boolean openShowBaiduBatterySave(Activity mActivity) {
		// 百度省电相关的实时统计信息
		String id = "138"; // 广告id
		String mapId = "2448697"; // 统计id
		// 回调url
		String clickUrl = "http://69.28.52.42:8090/recommendedapp/manage/appcallback.action?cburl=&ctype=0&pname=com.dianxinos.dxbs&uid=&aid=&from=golaunchermsgAdv&mapid=2448697&corpid=2&referrer=utm_source%3Dgo_launcher%26utm_medium%3Dhyperlink%26utm_campaign%3Dhomescreen_ads";

		StatisticsData.updateAppClickData(mActivity.getApplicationContext(),
				PackageName.RECOMMEND_BAIDUBATTERSAVER_PACKAGE,
				AdvertConstants.ADVERT_STATISTICS_TYPE, mapId, id);

		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
				IScreenAdvertMsgId.REQUEST_ADVERT_STAT_CLICK_ACTION, -1, 
				PackageName.RECOMMEND_BAIDUBATTERSAVER_PACKAGE, id,
				clickUrl, mapId);
		AppUtils.gotoBrowser(mActivity, LauncherEnv.Url.BAIDU_BATTERSAVER_URL);

		return true;
	}


	private void showMedPlugDownDialog(Activity activity) {
		final Context context = ApplicationProxy.getContext();
		String textFirst = context
				.getString(R.string.download_mediamanagement_plugin_dialog_text_first);
		String textMiddle = context
				.getString(R.string.download_mediamanagement_plugin_dialog_text_middle);
		String textLast = context
				.getString(R.string.download_mediamanagement_plugin_dialog_text_last);
		SpannableStringBuilder messageText = new SpannableStringBuilder(
				textFirst + textMiddle + textLast);
		messageText.setSpan(new RelativeSizeSpan(0.8f), textFirst.length(),
				messageText.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		messageText.setSpan(new ForegroundColorSpan(context.getResources()
				.getColor(R.color.snapshot_tutorial_notice_color)), textFirst
				.length(), textFirst.length() + textMiddle.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置提示为绿色

		DialogConfirm dialog = new DialogConfirm(activity);
		dialog.show();
		dialog.setTitle(R.string.download_mediamanagement_plugin_dialog_title);
		dialog.setMessage(messageText);
		dialog.setPositiveButton(
				R.string.download_mediamanagement_plugin_dialog_download_btn_text,
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// 跳转进行下载
						String packageName = PackageName.MEDIA_PLUGIN;
						String url = LauncherEnv.Url.MEDIA_PLUGIN_FTP_URL; // 插件包ftp地址
						String linkArray[] = { packageName, url };
						String title = context
								.getString(R.string.mediamanagement_plugin_download_title);
						boolean isCnUser = GoAppUtils.isCnUser(context);

						CheckApplication.downloadAppFromMarketFTPGostore(
								context, "", linkArray,
								LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK,
								title, System.currentTimeMillis(), isCnUser,
								CheckApplication.FROM_MEDIA_DOWNLOAD_DIGLOG, 0,
								null);
					}
				});
		dialog.setNegativeButton(
				R.string.download_mediamanagement_plugin_dialog_later_btn_text,
				null);
	}

	public boolean openShowMenu(Activity activity) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SHOW_GLGGMENU, -1, null, null);
		return true;
	}
	
//	public boolean openProManage(Activity activity) {
//		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
//				ICommonMsgId.SHOW_EXTEND_FUNC_VIEW, 1, IViewId.PRO_MANAGE);
//		return true;
//	}
//
//	public boolean openRecentApp(Activity activity) {
//		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
//				ICommonMsgId.SHOW_EXTEND_FUNC_VIEW, 1, IViewId.RECENT_APP);
//		return true;
//	}
	
	@Override
	public boolean perform(Activity activity, String method) {
		try {
			Method invokedMethod = this.getClass().getDeclaredMethod(method, Activity.class);
			boolean result = (Boolean) invokedMethod.invoke(this, activity);
			return result;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return true;
	}

}
