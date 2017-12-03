package com.jiubang.ggheart.apps.desks.diy;

import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.commomidentify.IOpenAppAnimationType;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.GoLauncherLogicProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.proxy.VersionControl;
import com.go.util.AppUtils;
import com.go.util.androidsys.CheckAFA;
import com.go.util.androidsys.ClearDefaultIntent;
import com.go.util.androidsys.HardWareProxy;
import com.go.util.device.Machine;
import com.go.util.device.meizu.SmartBarUtil;
import com.go.util.graphics.DrawUtils;
import com.go.util.graphics.HolographicOutlineHelper;
import com.go.util.graphics.effector.united.EffectorControler;
import com.go.util.log.Duration;
import com.go.util.log.LogUnit;
import com.go.util.window.WindowControl;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenFrameMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.core.message.MessageManager;
import com.jiubang.ggheart.analytic.Analyst;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.apps.appfunc.setting.AppFuncAutoFitManager;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.Preferences.SliderSettings;
import com.jiubang.ggheart.apps.desks.Preferences.ViewResponAreaInfo;
import com.jiubang.ggheart.apps.desks.diy.broadcastReceiver.NotificationReceiver;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.CellUtils;
import com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl.GuideControler;
import com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl.GuideForGlFrame;
import com.jiubang.ggheart.apps.desks.diy.guide.RateGuideTask;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.desks.settings.AutoFitDeviceManager;
import com.jiubang.ggheart.apps.font.FontControler;
import com.jiubang.ggheart.common.controler.AppInvokeMonitor;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.components.DeskAlertDialog;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;
import com.jiubang.ggheart.components.advert.AdvertDialogCenter;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.DatabaseHelper;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.theme.broadcastReceiver.MyThemeReceiver;
import com.jiubang.ggheart.iconconfig.AppIconConfigController;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.launcher.ThreadName;
import com.jiubang.ggheart.plugin.shell.ShellPluginFactory;
import com.jiubang.ggheart.themeicon.ThemeIconManager;

/**
 * DIY桌面主Activity,DIY入口
 * 
 * @author yuankai
 * @version 1.0
 */
public class GoLauncher extends DeskActivity {
	
	static final int WALLPAPER_SCREENS_SPAN = 2;
	static final boolean LOG_TO_SD = false;

	protected FrameLayout mMainLayout;
	FrameLayout mFrameLayout;
	private FrameControl mFrameControl;
	private DiyScheduler mScheduler;

	// 用于在其他桌面点击通讯统计图标时，intent中包含的信息字段
	//	private static final String GOTO_NOTIFICATION_SETTING_DETAIL = "goto_notification_setting_detail";

	private Analyst mAnalyst = null;

	private int mOpenAppType; // 打开app的动画方式
//	private int mLastVersionCode = -1; //更新前的版本号，只在初次启动时有效，以后启动该值与当前版本号相同
	
	// Handler消息
	private final static int GOOGLE_ANALYTICS = 1;
	private MyThemeReceiver mReceiver_screenedit;  //添加界面桌面主题设置广播
	private NotificationReceiver mNotificationReceiver;
	
	private boolean mInit3DModeSuccess = true;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GOOGLE_ANALYTICS :
					try {
						if (null != mAnalyst) {
							mAnalyst.stopAnalysation();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default :
					break;
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Duration.setStart("golauncher start");
		super.onCreate(savedInstanceState);
		GoLauncherActivityProxy.init(this);
		if (!ShellPluginFactory.isSupportShellPlugin(getApplicationContext())) {
			openDeliverDialog();
			return;
		}
//		Debug.startMethodTracing("golauncher-tv");
		// 设置Window层级的硬件加速
		
		HardWareProxy.hardwareAcceleratedByWindow(this);
		
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 未初始化完前强制为竖屏
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 未初始化完前强制为横屏
		}

//		BetaController.getInstance().setContext(this);
		// 初始化桌面程序本身的语言资源信息
		DeskResourcesConfiguration.createInstance(this);
		// 初始化log
		LogUnit.initLog(this, LOG_TO_SD);

		// 设置density, 全局使用
		DrawUtils.resetDensity(this);
		HolographicOutlineHelper.resetDensity(DrawUtils.sDensity);

		mFrameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.main, null);

		MessageManager msgManager = MsgMgrProxy.getMsgMgr();
		mFrameControl = new FrameControl(this, mFrameLayout, msgManager);

		// 第一次运行，必要在AppCore.build之前检测，否则一些自适应的设置会失效
		VersionControl.checkFirstRun();
		
		// 新版本第一次运行
		VersionControl.checkNewVersionFirstRun();
		
//		AppUtils.initRandomAd(this); // 去广告测试
		// 切换3DCore
//		changeEngine();
		
		// 当前设备设置自适应
		autoFitDeviceSetting();
		
		// 功能表模块设置自适应
		autoFitAppFuncSetting();
		
		// 构造AppCore
		AppCore.build();
		
		// 设置全局消息过滤器
		MsgMgrProxy.setFilter(AppCore.getInstance());

//		checkNewVersionFirstRun();
		
		// 第一次运行检查设置信息
		checkSettingInfoFirstRun();

		// 设置壁纸分辨率
		// setWallpaperDimension();
		// setWallpaperDimensionWithDefaultPicSize();

		//在启动引导页面前面显示已付费的界面
		DeskSettingUtils.gotoHadPayView(this);
		
		//保存上一次的versionCode
		saveLastVersionCode();
		
		//第一次运行检查
		brocastSideBarClearDate();
		//添加调度层，由调度层去根据后台数据不同，进行不同的加载动作
		mScheduler = new DiyScheduler(this, mFrameControl);
		if (!mScheduler.onCreate(savedInstanceState)) {
			openDeliverDialog();
			return;
		}
		
		// 设置壁纸分辨率
		WallpaperDensityUtil.setWallpaperDimension(this);

		// 通知周边插件桌面启动
		//		sendBroadcast(new Intent(ICustomAction.ACTION_LAUNCHER_START));

		startGoogleAnalysation();
		//针对OneX手机进行一些特殊的提示
		try {
			if (Machine.isONE_X() && !Machine.IS_ICS_MR1) {
				// 恢复默认或者初次安装则弹窗
				if (VersionControl.getFirstRun()
						|| VersionControl.getNewVeriosnFirstRun()) {
					guideNotify();
				}
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}
		//检查桌面启动的Intent是否带有某些数据，需要完成某些操作
		checkIntentDetailData(getIntent());
		//清除默认桌面
		ClearDefaultIntent.clDH(this);
		
		// //检测不保留后台活动
		CheckAFA.checkAFA(this);
		
		// 升级或全新安装，检测到语言匹配，则弹出提示框。
		checkLanguage();
		
		// 弹出语言设置通知栏
		showLanguageSetting();

		//		Intent in = new Intent();
		//		in.setClass(ApplicationProxy.getContext(), DownloadService.class);
		//		ApplicationProxy.getContext().startService(in);
		//		ApplicationProxy.getApplication().bindDownloadService();

		//为添加界面的主题设置增加动态广播接收,防止广播接收延迟
		mReceiver_screenedit = new MyThemeReceiver();
		IntentFilter filter = new IntentFilter(ICustomAction.ACTION_THEME_BROADCAST_SCREENEDIT);
		this.registerReceiver(mReceiver_screenedit, filter);
		
		//添加清除2D转3D引导通知栏的广播接收
		mNotificationReceiver = new NotificationReceiver();
		IntentFilter notificationFilter = new IntentFilter(ICustomAction.ACTION_CLEAN_NOTIFICATION_FOR_RECOMMEND_3DCORE);
		notificationFilter.addAction(ICustomAction.ACTION_APPDRAWER_LOCATE_APP);
		registerReceiver(mNotificationReceiver, notificationFilter);

		//Guide导航页初始化
		GuideControler.getInstance(this);
		//從4.01版開始將放在DB中的已使用字體設置放到preference中
		syncFontCfg();
		
		// LH TODO:
		// 数据库与特效XML比较，查看是否有新的特效加入
		EffectorControler.getInstance().compareHasNewEffector();
		
		// 启动侧边栏
		startSideBar();
		//活动叫停，可能12月份会继续
//		if (VersionControl.getNewVeriosnFirstRun()) {
//			//弹通知栏,送手机活动
//			showWinGalaxyNotification();
//
//			// 更多加红点
//			PreferencesManager preferencesManager = new PreferencesManager(getContext(),
//					IPreferencesIds.FEATUREDTHEME_CONFIG, Context.MODE_PRIVATE);
//			preferencesManager.putBoolean(IPreferencesIds.NEED_TO_SHOW_MORE_TIP_POINT, true);
//			preferencesManager.commit();
//		}
		
		
		
	}

	private void openDeliverDialog() {
		mInit3DModeSuccess = false;
		Intent intent = new Intent(getApplicationContext(), GoLauncherDeliverActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(intent);
		finish();
	}

	private void startGoogleAnalysation() {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// 启动统计.发送信息给Analysation
				new Thread(ThreadName.START_GOOGLE_ANALYTICS) {
					@Override
					public void run() {
						try {
							mAnalyst = new Analyst(ApplicationProxy.getContext(), R.raw.ua_number);
							mAnalyst.startAnalysation();
							mAnalyst.uploadReferrerInfo();
							mHandler.sendEmptyMessageDelayed(GOOGLE_ANALYTICS, 20 * 60 * 1000);
						} catch (Throwable e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}.start();
			}
		});
	}
	
	private void saveLastVersionCode() {
		int lastVersionCode = VersionControl.getLastVersionCode();
//		Log.d("version", "lastVersionCode= " + lastVersionCode);
		
		if (lastVersionCode < DeskSettingUtils.REOPEN_NEW_FLAG_VERSION_CODE) {
			PreferencesManager ps = new PreferencesManager(ApplicationProxy.getContext());
			ps.putInt(IPreferencesIds.PREFERENCE_LAST_VERSION_CODE, lastVersionCode);
			ps.commit();
		} 
	}
	
	/**
	 * <br>功能简述:送手机活动，
	 * <br>功能详细描述:2013.11.12-2013.11.23期间，国外第一次安装或升级都会弹出消息到通知栏
	 * <br>注意:
	 */
//	private void showWinGalaxyNotification() {
//		Calendar nowData = Calendar.getInstance();
//		nowData.get(Calendar.YEAR);
//		
//		Calendar beginData = Calendar.getInstance();
//		beginData.set(2013, 10, 12, 0, 0);
//		Calendar endData = Calendar.getInstance();
//		endData.set(2013, 10, 23, 0, 0);
//		if (nowData.after(beginData) && nowData.before(endData)) {
//			Intent webViewIntent = new Intent(this, ThemeWebViewActivity.class);
//			String url = "http://gotest.3g.net.cn/gostore/webcontent/activity/20131015/us/introduction.jsp?"
//					+ "imei="
//					+ Statistics.getVirtualIMEI(getApplicationContext())
//					+ "&goid="
//					+ StatisticsManager.getGOID(getApplicationContext())
//					+ "&channel="
//					+ Statistics.getUid(getApplicationContext())
//					+ "&country=us&language="
//					+ Machine.getLanguage(getApplicationContext());
//			webViewIntent.putExtra("url", url);
//			webViewIntent.putExtra("entry", GuiThemeStatistics.ENTRY_NOTIFICATION);
//			String title = "送手机活动"; //通知栏出现时的标题
//			String tickerText = "升级到最新版，赢大奖"; //展开后的标题
//			String content = "送每人一台手机"; //展开后的内容
//
//			GoAppUtils.sendNotification(this, webViewIntent, R.drawable.menuitem_win_award,
//					tickerText, title, content, INotificationId.GOTO_THEME_WEBVIEW);
//			//加统计，弹出通知栏的统计
//			GuiThemeStatistics.winGalaxyStaticData("", GuiThemeStatistics.WIN_GALAXY_NOTIFICATION,
//					1, GuiThemeStatistics.ENTRY_NOTIFICATION + "", "0", "0", "0", "");
//		}
//		
//	}

	public void startSideBar() {
		DesktopSettingInfo desktopSettingInfo = SettingProxy.getDesktopSettingInfo();
		SliderSettings sliderSettings = SliderSettings.getInstence(this);
		ViewResponAreaInfo leftInfo = sliderSettings.getResponAreaInfoLeft();
		ViewResponAreaInfo rightInfo = sliderSettings.getResponAreaInfoRight();
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT >= 12) {
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		}
		intent.setAction(ICustomAction.ACTION_SYNC_SIDEBAR);
		//右侧数据
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAX,
				rightInfo.getRightAreaX());
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAY,
				rightInfo.getRightAreaY());
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAWIDTH,
				rightInfo.getRightAreaWidth());
		intent.putExtra(IPreferencesIds.PREFERENCE_RIGHT_AREAINFO_RIGHTAREAHEIGHT,
				rightInfo.getRightAreaHeight());
		//左侧数据
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAX, leftInfo.getLeftAreaX());
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAY, leftInfo.getLeftAreaY());
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAWIDTH,
				leftInfo.getLeftAreaWidth());
		intent.putExtra(IPreferencesIds.PREFERENCE_LEFT_AREAINFO_LEFTAREAHEIGHT,
				leftInfo.getLeftAreaHeight());
		FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(getApplicationContext());
		boolean hasPay = purchaseManager
				.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS);

		//是否开启 true开启 false 关闭
		if (desktopSettingInfo.mEnableSideDock && hasPay) {
			intent.putExtra(ICustomAction.ACTION_START_SIDEBAR, true);
		} else {
			intent.putExtra(ICustomAction.ACTION_START_SIDEBAR, false);
		}
		//向左向右 true左边 false 右边
		if (desktopSettingInfo.mSideDockPosition == 0) {
			intent.putExtra(ICustomAction.ACTION_START_LEFT_SIDEBAR, true);
		} else {
			intent.putExtra(ICustomAction.ACTION_START_LEFT_SIDEBAR, false);
		}

		this.sendBroadcast(intent);
		// 启动侧边栏
		if (hasPay
				&& SettingProxy.getDesktopSettingInfo().mEnableSideDock) {
			//			Intent intent = new Intent(ICustomAction.ACTION_START_SIDEBAR);
			//			if (Build.VERSION.SDK_INT >= 12) {
			//				intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			//			}
			//			getContext().sendBroadcast(intent);
			GoAppUtils.startSlideBar(this);
		} else {
			GoAppUtils.stopSlideBar(this);
		}
	}
	
	/**
	 * <br>功能简述:检查3D引擎是否要打开的方法
	 * <br>功能详细描述:
	 * <br>注意:
	 */
//	private void changeEngine() {
//		PreferencesManager sharedPreferences = new PreferencesManager(this,
//				IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
//		// 是否之前已经初始化失败过
//		boolean initFailed = sharedPreferences.getBoolean(
//				IPreferencesIds.PREFERENCE_ENGINE_INITED_FAILED, false);
//		boolean isUsedShellEngine = false;
//		if (initFailed) {
//			// 如果之前初始化失败过，就弹出Toast提示
//			DeskToast.makeText(this, R.string.shellengine_init_failed, Toast.LENGTH_LONG).show();
//			// 并把失败的字段和选择的字段重置，下次会再尝试初始化
//			sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_INITED_FAILED, false);
//			sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_SELECTED, false);
//			sharedPreferences.commit();
//		} else {
//			
//			if (Machine.needToOpen3DCore()
//					&& ShellPluginFactory.isSupportShellPlugin(this)) {
//				//手机支持3D引擎
//				//新用户并第一次运行桌面
//				if (VersionControl.getFirstRun()) {
//					// 默认使用3D引擎
//					isUsedShellEngine = true;
//					sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_SELECTED,
//							isUsedShellEngine);
//					sharedPreferences.commit();
//				} else {
//					// 如果不是第一次运行桌面
//					// 取出上版本3D引擎是否选择打开的值
//					boolean lastEngineSelected = sharedPreferences.getBoolean(
//							IPreferencesIds.PREFERENCE_LAST_ENGINE_SELECTED, true);
//					// 是否弹过推荐切换到3D
//					// 如果之前我们已经帮用户自动转成3D，就会弹出通知栏，如果用户清除通知栏或者手工把3D又转成2D，就会标志成True
//					boolean hasRecommendShellEngine = sharedPreferences.getBoolean(
//							IPreferencesIds.PREFERENCE_RECOMMEND_SHELL_ENGINE, false);
//					
//					Log.d("3DCode", "isNewUser= " + VersionControl.isNewUser() + " lastEngineSelected= " + lastEngineSelected);
//					Log.d("3DCode", "hasRecommendShellEngine= " + hasRecommendShellEngine + " SDK_INT= " + Build.VERSION.SDK_INT);
//					//对特定手机，升级后2D自动转为3D
//					if (!VersionControl.isNewUser()  		//不是全新用户，是升级用户
//							&& !lastEngineSelected  				//之前使用2D模式
//							&& !hasRecommendShellEngine 			//未弹过推荐通知栏
//					) {
//						//上面的代码已经判断了用户的手机是否支持3D引擎，现在要把所有支持3D引擎的用户，升级后自动转成使用3D
//						isUsedShellEngine = true;
//						sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_SELECTED,
//								isUsedShellEngine);
//						sharedPreferences.putBoolean(
//								IPreferencesIds.PREFERENCE_RECOMMEND_SHELL_ENGINE_NOTIFIY_EXIST,
//								true);
//						sharedPreferences.putBoolean(
//								IPreferencesIds.PREFERENCE_LAST_ENGINE_SELECTED, true);
//						sharedPreferences.commit();
//						// 2D转3D后，弹出通知栏，点击跳转设置
//						recommend3DCoreNotifiy();
//						
//						//添加2D转3D统计
//						GuiThemeStatistics.guiStaticData("41", 99, "", "3D", 1, "", "", "", "", "");
//					} else {
//						// 直接取出设置值
//						isUsedShellEngine = sharedPreferences.getBoolean(
//								IPreferencesIds.PREFERENCE_ENGINE_SELECTED, false);
//					}
//				}
//			} else {
//				//如果用户不支持3D引擎
//				if (Machine.IS_FROYO) {
//					//如果是2.2或者以上的用户，上传信息，统计一下情况
//					StringBuffer stringBuffer = new StringBuffer();
//					stringBuffer.append("AndroidSDKVersion:" + Machine.getAndroidSDKVersion() + "\n");
//					stringBuffer.append("isSupportGLES20:" + Machine.isSupportGLES20(this) + "\n");
//					stringBuffer.append("isShellPluginExist:" + ShellPluginFactory.isShellPluginExist(this) + "\n");
//					GoAppUtils.postLogInfo("NotSupport_3D", stringBuffer.toString());
//				}
//				// 如果不支持打开3D引擎,直接取出设置值
//				isUsedShellEngine = sharedPreferences.getBoolean(
//						IPreferencesIds.PREFERENCE_ENGINE_SELECTED, false);
//			}
//			
////			if (GOLauncherApp.getFirstRun() && Machine.needToOpen3DCore()
////					&& ShellPluginFactory.isSupportShellPlugin(this)) {
////				isUsedShellEngine = true;
////				sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_SELECTED,
////						isUsedShellEngine);
////				sharedPreferences.commit();
////			} else {
////				isUsedShellEngine = sharedPreferences.getBoolean(
////						IPreferencesIds.PREFERENCE_ENGINE_SELECTED, false);
////			}
//		}
//		
//		Log.d("3DCode", "Build.MODEL= " + Build.MODEL + " Build.PRODUCT= " + Build.PRODUCT); 
//		ShellPluginFactory.sUseEngineFlag = isUsedShellEngine;
//	}
	
	/**
	 * 判断桌面intent是否有携带自定义数据
	 * 
	 * @param intent
	 */
	private void checkIntentDetailData(Intent intent) {
		if (null != intent) {
//			final String pkg = null;
			// String gotoNotification = null;
			try {
				final String pkg = intent.getStringExtra(ICustomAction.DATA_PKG_GOTO_SPECIFICK_WIDGET_DETAIL);
				// gotoNotification =
				// intent.getStringExtra(GOTO_NOTIFICATION_SETTING_DETAIL);
				
				if (null != pkg) {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
							IScreenFrameMsgId.GOTO_SCREEN_EDIT_TAB, -1, pkg, null);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}

			
			// if (null != gotoNotification) {
			// MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
			// IDiyMsgIds.SCREEN_OPEN_NOTIFICATION_SETTING, -1, null, null);
			// }
			String keyPackage = null;
			try {
				keyPackage = intent.getStringExtra(ICustomAction.DATA_PKG_GOTO_HADPAY_VIEW);
				if (null != keyPackage) {
					 //Log.e(keyPackage, "------------");
					 DeskSettingUtils.gotoHadPayView(this);
				}
				//To do go to had pay view
			} catch (Exception e) {
				// e.printStackTrace();
			}
			
			changeTheme(intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		LogUnit.destory(this, LOG_TO_SD);
		if (!mInit3DModeSuccess) {
			GoLauncherActivityProxy.destory();
			return;
		}
		// 先执行mScheduler的清理，依赖于mFrameManager的数据
		mScheduler.onDestroy();
		mFrameControl.cleanup();
		AppCore.destroy();
//		BetaController.getInstance().cleanUp();
		MsgMgrProxy.cleanup();
		
		GoLauncherActivityProxy.destory();

		// 通知周边插件桌面退出
		sendBroadcast(new Intent(ICustomAction.ACTION_LAUNCHER_EXIT));

		// 设置壁纸分辨率
		setWallpaperDimension();

		AppUtils.cancelNotificaiton(this, INotificationId.ONE_X_GUIDE);
		AppUtils.cancelNotificaiton(this, INotificationId.LANGUAGE_START_GGMENU);
		AppUtils.cancelNotificaiton(this, INotificationId.MIGRATE_TIP);

		//反注册添加界面桌面主题设置广播
		unregisterReceiver(mReceiver_screenedit);
		
		SettingProxy.getInstance(this).doUnbindService();
		
		// 结束自己
		AppUtils.killProcess();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DrawUtils.resetDensity(this);
		CellUtils.init(getBaseContext());
		// 向下传
		mScheduler.onConfigurationChanged(newConfig);

	}

	@Override
	protected void onStart() {
		// 通知周边插件桌面onStart
		sendBroadcast(new Intent(ICustomAction.ACTION_LAUNCHER_ONSTART));
		super.onStart();
		mScheduler.onStart();
		
	}

	@Override
	protected void onRestart() {
		GoLauncherActivityProxy.onRestart();
		if (isUnderSDK14() && mOpenAppType == IOpenAppAnimationType.TYPE_OPEN_APP_SMALL2BIG) {
			overridePendingTransition(R.anim.zoom_golauncher_enter, R.anim.zoom_exit);
		}
		super.onRestart();
	}

	@Override
	protected void onStop() {
		GoLauncherActivityProxy.onStop();
		
		if (isUnderSDK14() && mOpenAppType == IOpenAppAnimationType.TYPE_OPEN_APP_SMALL2BIG) {
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_golauncher_exit);
		}
		super.onStop();
		// 通知周边插件桌面onStop
		sendBroadcast(new Intent(ICustomAction.ACTION_LAUNCHER_STOP));
		mScheduler.onStop();
	}

	@Override
	protected void onPause() {
		if (isUnderSDK14() && mOpenAppType == IOpenAppAnimationType.TYPE_OPEN_APP_SMALL2BIG) {
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_golauncher_exit);
		}
		super.onPause();
		GoLauncherActivityProxy.onPause();
		mScheduler.onPause();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mScheduler.dispatchKeyEvent(event)) {
			return true;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (mScheduler.dispatchTouchEvent(event)) {
			return true;
		} else {
			return super.dispatchTouchEvent(event);
		}
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		if (mScheduler.dispatchTrackballEvent(event)) {
			return true;
		} else {
			return super.dispatchTrackballEvent(event);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (mScheduler.onKeyDown(keyCode, event)) {
//			return true;
//		} else {
//			return super.onKeyDown(keyCode, event);
//		}

		boolean ret = ShellPluginFactory.getShellManager().onKeyDown(keyCode, event);
		if (!ret) {
			ret = super.onKeyDown(keyCode, event);
		}
		return ret;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (mScheduler.onKeyUp(keyCode, event)) {
//			return true;
//		} else {
//			return super.onKeyUp(keyCode, event);
//		}
		
		boolean ret = ShellPluginFactory.getShellManager().onKeyUp(keyCode, event);
		if (!ret) {
			ret = super.onKeyUp(keyCode, event);
		}
		return ret;
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean ret = ShellPluginFactory.getShellManager().onKeyLongPress(keyCode, event);
		if (!ret) {
			ret = super.onKeyLongPress(keyCode, event);
		}
		return ret;
	}
	
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		boolean ret = ShellPluginFactory.getShellManager().onKeyMultiple(keyCode, repeatCount, event);
		if (!ret) {
			ret = super.onKeyMultiple(keyCode, repeatCount, event);
		}
		return ret;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mScheduler.onNewIntent(intent);

		checkIntentDetailData(intent);
		showLanguageGGMenu(intent);
	}

	@Override
	protected void onResume() {
		// 隐藏魅族的smartbar
		SmartBarUtil.hideSmartBar(getWindow());
		if (mScheduler.isNeedRestart()) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
		}
		if (isUnderSDK14() && mOpenAppType == IOpenAppAnimationType.TYPE_OPEN_APP_SMALL2BIG) {
			overridePendingTransition(R.anim.zoom_golauncher_enter, R.anim.zoom_exit);
		}
		super.onResume();
		GoLauncherActivityProxy.init(this);
		GoLauncherActivityProxy.onResume();

		mScheduler.onResume();
		int startTutorialMask = GoLauncherLogicProxy.getTutorialMask();
		if (startTutorialMask != LauncherEnv.MASK_TUTORIAL_NONE) {
			startTutorialFrame();
			GoLauncherLogicProxy.setTutorialMask(LauncherEnv.MASK_TUTORIAL_NONE);
		}
		// 壁纸滚动
		if (SettingProxy.getScreenSettingInfo().mWallpaperScroll) {
			// M9
			if (Machine.isM9()) {
				WallpaperDensityUtil.setWallpaperDimension(this);
			}
		}
		// 是否显示提醒评分对话框
		if (!Machine.isKorea(this)) {
			RateGuideTask.getInstacne(this).checkUnExcutedRate();
			com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager.getMessageManager(this).checkUnExcutedShowDialogMsg();
		}
		// UI3.0主题下发，检查是否有缓存的通知
		ThemeIconManager.getInstance(this).checkNotificationWaiting();
		
		AppInvokeMonitor monitor = AppInvokeMonitor.getInstance(this);
		monitor.stopMonitor(CommonControler.MONITOR_BROWSER_INVOKE_TASK_ID);
		// 同学，按照传统惯例继续读sp下去吧～good luck
		for (int i = 305; i <= 308; i++) {
			DeskSettingUtils.showGuidePrimeDialog(this, i);
		}
		
		//判断点击浏览器次数
		checkShowBroswer();
		
		//update by caoyaming 2014-04-03  请求应用图标配置数据
		AppIconConfigController.getInstance(this).requestIconConfigData();
		//update by caoyaming end
		
		//		Debug.stopMethodTracing();
		LogUnit.i("Test", "golauncher start: " + Duration.getDuration("golauncher start"));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			try {
					boolean isFullScreen = !SettingProxy.getDesktopSettingInfo().mShowStatusbar;
					// if(Machine.isTablet(sContext))
					// {
					// isFullScreen = true;
					// }
					if (isFullScreen) {
						WindowControl.setIsFullScreen(this, isFullScreen, true);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mScheduler.onActivityResult(requestCode, resultCode, data);
	}

	// private void setWallpaperDimension()
	// {
	// WallpaperManager wpm = (WallpaperManager)
	// getSystemService(WALLPAPER_SERVICE);
	//
	// Display display = getWindowManager().getDefaultDisplay();
	// boolean isPortrait = display.getWidth() < display.getHeight();
	//
	// final int width = isPortrait ? display.getWidth() : display.getHeight();
	// final int height = isPortrait ? display.getHeight() : display.getWidth();
	// try
	// {
	// wpm.suggestDesiredDimensions(width * WALLPAPER_SCREENS_SPAN, height);
	// }
	// catch (Exception e)
	// {
	// Log.i(LogConstants.HEART_TAG, "fail to setWallpaperDimension");
	// }
	// }

	/**
	 * 为特殊机型设置壁纸原始宽高，避免出现壁纸缩放问题
	 */
	@SuppressLint("ServiceCast")
	private void setWallpaperDimensionWithDefaultPicSize() {
		WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
		try {
			/**
			 * 把建议宽高设为<0,系统会返回不经任何缩放的壁纸图片 NOTE：一般的Launcher是在onCreate时把这个size设死为
			 * （宽*2，高） 这样会使不为（宽*2，高）的壁纸有缩放，如i9000原生ROM可以剪切（宽，高）的壁纸，
			 * 这时出来的效果就是放大了一倍。
			 */
			wpm.suggestDesiredDimensions(-1, -1);
		} catch (IllegalArgumentException e) {
			// NOTE:目前只发现在三星原生ROM时设置为<0不报这个异常，所以我们做法是：
			// 如果可以设置<0来拿不缩放壁纸，就这样做，如果报异常，就不改设置，用原来的（宽*2，高）
		}
	}

	@Override
	public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData,
			boolean globalSearch) {
		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(initialQuery);
		objs.add((Boolean) selectInitialQuery);
		objs.add(appSearchData);
		objs.add((Boolean) globalSearch);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, ICommonMsgId.SHELL_START_SEARCH, -1,
				null, objs);

	}

	// private void checkFirstRun()
	// {
	// boolean firstRun = false;
	//
	// GoSettingControler controler = GoSettingControler.getInstance(ApplicationProxy.getContext());
	// if (null != controler)
	// {
	// ThemeSettingInfo info = controler.getThemeSettingInfo();
	// if (null != info && info.mFirstRun)
	// {
	// info.mFirstRun = false;
	// // 如果是高级配置则开启常驻内存和高质量绘图
	// final int deviceLevel = ConfigurationInfo.getDeviceLevel();
	// if(deviceLevel == ConfigurationInfo.HIGH_DEVICE){
	// info.mIsPemanentMemory = true;
	// }
	// controler.updateThemeSettingInfo(info);
	// firstRun = true;
	// }
	// }
	//
	// mFirstRun = firstRun;
	// }
	
	private void checkSettingInfoFirstRun() {
		// SettingInfo settingInfo =
		// GoSettingControler.getInstance(sContext).getSettingInfo();
		// mOpenAppType = settingInfo.mAppOpenType;
		mOpenAppType = DataProvider.getInstance(this).getAppOpenType();

	}

	

	@SuppressLint("ServiceCast")
	private void setWallpaperDimension() {
		WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);

		Display display = getWindowManager().getDefaultDisplay();
		boolean isPortrait = display.getWidth() < display.getHeight();

		final int width = isPortrait ? display.getWidth() : display.getHeight();
		final int height = isPortrait ? display.getHeight() : display.getWidth();
		try {
			wpm.suggestDesiredDimensions(width * WALLPAPER_SCREENS_SPAN, height);
		} catch (Exception e) {
			Log.i("", "fail to setWallpaperDimension");
		}
	}

	private int startTutorialFrame() {
		int frameId = -1;
		int startTutorialMask = GoLauncherLogicProxy.getTutorialMask();
		switch (startTutorialMask) {
			case LauncherEnv.MASK_TUTORIAL_PREVIEW :
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.SCREEN_SHOW_PREVIEW, -1, null, null);
				break;
			case LauncherEnv.MASK_TUTORIAL_CREATE_FOLDER :
				GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_FUNC_FOLDER);
				break;
			case LauncherEnv.MASK_TUTORIAL_CUSTOM_GESTURE :
				GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_CUSTOM_GESTURE);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
				break;
			case LauncherEnv.MASK_TUTORIAL_DOCK_BAR_ICON :
				GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_DOCK_BAR_ICON_GESTURE);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
				break;
			default :
				break;
		}
		return frameId;
	}

	private void guideNotify() {
		final DeskAlertDialog deskAlertDialog = new DeskAlertDialog(GoLauncher.this);
		String title = GoLauncher.this.getString(R.string.one_x_guide_dialogtitle);
		String content = getResources().getString(R.string.one_x_guide_dialogcontent);
		TextView text = new TextView(this);
		text.setText(content);
		text.setTextSize(18);
		String url = "http://golauncher.goforandroid.com/2012/10/htc-one-xs-update-guide/";
		if (Locale.getDefault().getLanguage().equals("zh")) {
			url = "http://golauncher.goforandroid.com/zh/2012/10/htc-one-xs-update-guide/";
		}
		deskAlertDialog.setTitle(title);
		deskAlertDialog.setView(text);
		final String goUrl = url;
		deskAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Check",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse(goUrl);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						try {
							startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		deskAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				deskAlertDialog.selfDestruct();
			}
		});
		deskAlertDialog.show();
	}

	/***
	 * 判断是否低于4.0
	 * 
	 * @return
	 */
	private boolean isUnderSDK14() {
		//避免ANR return android.os.Build.VERSION.SDK_INT < 14;
		return !Machine.IS_ICS;
	}

	/*
	 * 自适应当前设备
	 */
	private void autoFitDeviceSetting() {
		// 如果不是第一次执行GO桌面则不需要执行以下的适配
		if (VersionControl.getFirstRun()) {
			AutoFitDeviceManager.autoFit();
		}
	}

	/*
	 *桌面第一次运行通知侧边栏插件清空数据 266为有侧边栏设置的版本
	 */
	private void brocastSideBarClearDate() {
		// 如果不是第一次执行GO桌面则不需要执行
		if (VersionControl.getFirstRun()
				|| (VersionControl.getNewVeriosnFirstRun() && 
						VersionControl.getLastVersionCode() < VersionControl.VERSION_SIDEBAR_SETTING_SUPPORT)) {
			Intent intent = new Intent();
			if (Build.VERSION.SDK_INT >= 12) {
				intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			}
			intent.setAction(ICustomAction.ACTION_RESTORE_SIDEBAR);
			getBaseContext().sendBroadcast(intent);
		}
	}
	
	private void checkShowBroswer() {
	    
	    //判断是否存在电子市场，如果存在电子市场就推。不存在就不推。
        if (!GoAppUtils.isMarketExist(ApplicationProxy.getContext())) {
            return;
        }
	    
	    String curCountry = Machine.getCountry(ApplicationProxy.getContext());
        boolean isNewUser = VersionControl.isNewUser();
        
        boolean isAllowCounry = isNewUser || (!isNewUser && Machine.isMathonCountory(curCountry));
        
	    //判断是否新用户以及 4.15对老用户的具体国家开放。
//	    if (VersionControl.isNewUser() && DeskSettingUtils.isPrimeAd(ApplicationProxy.getContext()) && !AppUtils.isAppExist(ApplicationProxy.getContext(), PackageName.MAXTHON_PACKAGE)) {
	    if (isAllowCounry && DeskSettingUtils.isPrimeAd(ApplicationProxy.getContext()) && !AppUtils.isAppExist(ApplicationProxy.getContext(), PackageName.MAXTHON_PACKAGE)) {
	        PreferencesManager ps = new PreferencesManager(ApplicationProxy.getContext());
            int openCount = ps
                    .getInt(IPreferencesIds.PREFERENCES_OPEN_DOCK_BROWSER_COUNT, 0);
            boolean isNeedDisplay = ps.getBoolean(IPreferencesIds.PREFERENCES_HAD_OPEN_BROWSER, true);
	        if (isNeedDisplay && openCount == 2 && Statistics.is200ChannelUid(ApplicationProxy.getContext()) && !GoAppUtils.isCnUser(ApplicationProxy.getContext())) {
	            GuiThemeStatistics.guiStaticDataFor360Mathon(40, "5380697", "f000",
                        1, "-1", "-1", "-1", "-1", "571");
	            AdvertDialogCenter.showMaxthonDialog(GoLauncherActivityProxy.getActivity());
	            ps.putBoolean(IPreferencesIds.PREFERENCES_HAD_OPEN_BROWSER, false);
	            ps.commit();
	        }
	    }
	    
	}
	
	private void autoFitAppFuncSetting() {
		if (VersionControl.getFirstRun()) {
			AppFuncAutoFitManager.getInstance(ApplicationProxy.getContext()).autoFitDeviceSetting();
		}
	}

	/**
	 * 清除其他桌面
	 */
	// private void killOtherLauncher() {
	// try {
	// ActivityManager activityManager = (ActivityManager) GoLauncher.this
	// .getSystemService(Context.ACTIVITY_SERVICE);
	// List<ResolveInfo> infos = null;
	// PackageManager packageMgr = GoLauncher.this.getPackageManager();
	// Intent intent = new Intent("android.intent.action.MAIN");
	// intent.addCategory("android.intent.category.HOME");
	// infos = packageMgr.queryIntentActivities(intent, 0);
	// boolean sdk_init = Build.VERSION.SDK_INT >= 8;
	// if (infos != null && activityManager != null) {
	// for (ResolveInfo info : infos) {
	// if(info != null){
	// String packageName = info.activityInfo.packageName;
	// if (packageName != null) {
	// if(packageName.startsWith(LauncherEnv.PACKAGE_NAME) ||
	// packageName.startsWith("com.jiubang.goscreenlock")){
	// continue;
	// }
	// }
	// if (sdk_init) {
	// activityManager.killBackgroundProcesses(packageName);
	// } else {
	// activityManager.restartPackage(packageName);
	// }
	// }
	// }
	// }
	// } catch (Throwable t) {
	// t.printStackTrace();
	// }
	// }
	// 检测语言是否匹配
	private void checkLanguage() {
		if (VersionControl.getFirstRun()
				|| VersionControl.getNewVeriosnFirstRun()
				&& !(Machine.isONE_X() && !Machine.IS_ICS_MR1)) {
			String language = Locale.getDefault().getLanguage();
			if (language.equals("zh")) {
				language = language + "_" + Locale.getDefault().getCountry();
			}
			String[] languageName = { "tr", "cs", "pt", "el", "pl", "ar", "it", "fr", "ja", "de",
					"zh_HK" };
			for (String lang : languageName) {
				if (lang.contains(language)) {
					// 如果末安装则弹出提示框
					if (!GoAppUtils.isAppExist(GoLauncher.this, PackageName.LANGUAGE_PACKAGE
							+ "." + language)) {
						showDownLanguageDialog(this, language);
						break;
					}
				}
			}
		}
	}

	/**
	 * 下载语言包
	 */
	private void showDownLanguageDialog(Context context, String language) {
		final Context downcontext = context;
		final String downlanguage = language;
		final DeskAlertDialog deskAlertDialog = new DeskAlertDialog(GoLauncher.this);
		String title = GoLauncher.this.getString(R.string.downlanguage_title);
		deskAlertDialog.setTitle(title);
		String content1 = GoLauncher.this.getString(R.string.downlanguage_content1);
		String content2 = GoLauncher.this.getString(R.string.downlanguage_content2);
		String content3 = GoLauncher.this.getString(R.string.downlanguage_content3);
		String content4 = GoLauncher.this.getString(R.string.downlanguage_content4);
		String languageName = this.getResources().getConfiguration().locale.getDisplayLanguage();

		String content = getLanguageContent(language);
		if (null == content) {
			content = content1 + " " + languageName + " " + content2 + "\n" + content3 + " "
					+ languageName + " " + content4;
		}
		String positiveBtnText = GoLauncher.this.getString(R.string.install_language_tip_postive);
		deskAlertDialog.setMessage(content);

		deskAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveBtnText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						String packageName = PackageName.LANGUAGE_PACKAGE + "."
								+ downlanguage;
						//						GoStoreOperatorUtil.gotoStoreDetailDirectly(
						//								downcontext, packageName);
						AppsDetail.gotoDetailDirectly(downcontext,
								AppsDetail.START_TYPE_APPRECOMMENDED, packageName);
					}
				});
		deskAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				deskAlertDialog.selfDestruct();
			}
		});
		deskAlertDialog.show();
	}

	private String getLanguageContent(String language) {
		String content = null;
		if ("ru".equals(language)) {
			content = this.getString(R.string.russian_dialogcontent);
		} else if ("iw".equals(language)) {
			content = this.getString(R.string.hebrew_dialogcontent);
		} else if ("ro".equals(language)) {
			content = this.getString(R.string.romanian_dialogcontent);
		} else if ("es".equals(language)) {
			content = this.getString(R.string.spanish_dialogcontent);
		} else if ("it".equals(language)) {
			content = this.getString(R.string.italiano_dialogcontent);
		} else if ("de".equals(language)) {
			content = this.getString(R.string.german_dialogcontent);
		} else if ("tr".equals(language)) {
			content = this.getString(R.string.turkish_dialogcontent);
		} else if ("pl".equals(language)) {
			content = this.getString(R.string.polish_dialogcontent);
		} else if ("pt".equals(language)) {
			content = this.getString(R.string.portugu_dialogcontent);
		} else if ("fr".equals(language)) {
			content = this.getString(R.string.french_dialogcontent);
		} else if ("ja".equals(language)) {
			content = this.getString(R.string.japanese_dialogcontent);
		} else if ("vi".equals(language)) {
			content = this.getString(R.string.vietnamese_dialogcontent);
		} else if ("in".equals(language)) {
			content = this.getString(R.string.indonesia_dialogcontent);
		} else if ("sv".equals(language)) {
			content = this.getString(R.string.swedish_dialogcontent);
		} else if ("ir".equals(language)) {
			content = this.getString(R.string.persian_dialogcontent);
		} else if ("hr".equals(language)) {
			content = this.getString(R.string.croatian_dialogcontent);
		} else if ("sr".equals(language)) {
			content = this.getString(R.string.serbian_dialogcontent);
		} else if ("zh_HK".equals(language)) {
			content = this.getString(R.string.hongkong_dialogcontent);
		}
		return content;
	}

	private String getLanguageNotification(String language) {
		String content = null;
		if ("ru".equals(language)) {
			content = this.getString(R.string.russian_notification);
		} else if ("iw".equals(language)) {
			content = this.getString(R.string.hebrew_notification);
		} else if ("ro".equals(language)) {
			content = this.getString(R.string.romanian_notification);
		} else if ("es".equals(language)) {
			content = this.getString(R.string.spanish_notification);
		} else if ("it".equals(language)) {
			content = this.getString(R.string.italiano_notification);
		} else if ("de".equals(language)) {
			content = this.getString(R.string.german_notification);
		} else if ("tr".equals(language)) {
			content = this.getString(R.string.turkish_notification);
		} else if ("pl".equals(language)) {
			content = this.getString(R.string.polish_notification);
		} else if ("pt".equals(language)) {
			content = this.getString(R.string.portugu_notification);
		} else if ("fr".equals(language)) {
			content = this.getString(R.string.french_notification);
		} else if ("ja".equals(language)) {
			content = this.getString(R.string.japanese_notification);
		} else if ("vi".equals(language)) {
			content = this.getString(R.string.vietnamese_notification);
		} else if ("in".equals(language)) {
			content = this.getString(R.string.indonesia_notification);
		} else if ("sv".equals(language)) {
			content = this.getString(R.string.swedish_notification);
		} else if ("ir".equals(language)) {
			content = this.getString(R.string.persian_notification);
		} else if ("hr".equals(language)) {
			content = this.getString(R.string.croatian_notification);
		} else if ("sr".equals(language)) {
			content = this.getString(R.string.serbian_notification);
		} else if ("zh-HK".equals(language)) {
			content = this.getString(R.string.hongkong_notification);
		}
		return content;
	}

	// 检测语言是否匹配
	private void showLanguageSetting() {
		try {
			PreferencesManager preferences = new PreferencesManager(this,
					IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
			int goDbVersion = preferences.getInt(IPreferencesIds.GO_DB_VERSION, 0);
			int currentDbVersion = DatabaseHelper.getDB_CUR_VERSION();
			boolean isShowLanguage = false;
			if (goDbVersion == 0) {
				preferences.putInt(IPreferencesIds.GO_DB_VERSION, currentDbVersion);
				preferences.commit();
				isShowLanguage = true;

			} else if (goDbVersion < currentDbVersion) {
				preferences.putInt(IPreferencesIds.GO_DB_VERSION, currentDbVersion);
				preferences.commit();
				isShowLanguage = true;
			}
			if (isShowLanguage) {
				String language = Locale.getDefault().getLanguage();
				if (language.equals("zh")) {
					language = language + "-" + Locale.getDefault().getCountry();
				}
				String[] supportLanguages = this.getResources().getStringArray(
						R.array.support_language);
				for (String lang : supportLanguages) {
					if (language.contains(lang)) {
						// 如果末安装则弹出提示框
						if (!GoAppUtils.isAppExist(GoLauncher.this,
								PackageName.LANGUAGE_PACKAGE + "." + language)) {
							// 发通知广播
							Intent intent = new Intent();
							intent.setClassName(this, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
							intent.setAction(ICustomAction.ACTION_LANGUAGE_START_GGMENU);

							String title = this.getString(R.string.notification_language_title);
							String noteTitle = this
									.getString(R.string.notification_language_notetitle);
							String noteText = getLanguageNotification(lang);
							if (null == noteText) {
								noteText = this.getString(R.string.notification_language_context1)
										+ " " + language + " "
										+ this.getString(R.string.notification_language_context2);
							}
							GoAppUtils.sendNotification(this, intent, R.drawable.icon, title,
									noteTitle, noteText, INotificationId.LANGUAGE_START_GGMENU);
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showLanguageGGMenu(Intent intent) {
		if (intent == null) {
			return;
		}
		try {
			String action = intent.getAction();
			if (ICustomAction.ACTION_LANGUAGE_START_GGMENU.equals(action)) {
				//发消息通知GGMENU启动
				mScheduler.handleMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.SHOW_MENU,
						2, null, null);
			}
		} catch (Throwable t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}
	} // end showLanguageGGMenu


	@Override
	public void startActivityForResult(final Intent intent, final int requestCode) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, ICommonMsgId.START_ACTIVITY, 0,
				new Runnable() {

					@Override
					public void run() {
						try {
							GoLauncher.super.startActivityForResult(intent, requestCode);
						} catch (ActivityNotFoundException e) {
							e.printStackTrace();
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
									ICommonMsgId.ACTIVITY_NOT_FOUND_EXCEPTION, requestCode, intent,
									null);
						} catch (SecurityException e) {
							e.printStackTrace();
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
									ICommonMsgId.SECURITY_EXCEPTION, requestCode, intent, null);
						} catch (Throwable e) {
							e.printStackTrace();
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
									ICommonMsgId.OTHER_EXCEPTION, requestCode, intent, null);
						}
					}
				}, null);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try {
			super.onRestoreInstanceState(savedInstanceState);
		} catch (Exception e) {
			Log.i("GoLauncher", "onRestoreInstanceState has exception " + e.getMessage());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		try {
			super.onSaveInstanceState(outState);
		} catch (Exception e) {
			Log.i("GoLauncher", "onSaveInstanceState has exception " + e.getMessage());
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		try {
			super.onBackPressed();
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("GoLauncher", "onBackPressed has exception " + e.getMessage());
		}
	}
	
	/**
	 * <br>功能简述:
	 * <br>功能详细描述://因为部分机型上面广播接收问题，主题管理应用主题时不发广播改为启动桌面，在桌面中完成
	 * <br>注意:
	 */
	private void changeTheme(Intent intent) {

		if (intent != null
				&& intent.getIntExtra(MyThemeReceiver.ACTION_TYPE_STRING, -1) == MyThemeReceiver.CHANGE_THEME) {
			String pkgName = intent.getStringExtra(MyThemeReceiver.PKGNAME_STRING);
			if (pkgName != null) {
				Intent themeIntent = new Intent(ICustomAction.ACTION_THEME_BROADCAST_SCREENEDIT);
				themeIntent.putExtra(MyThemeReceiver.ACTION_TYPE_STRING,
						MyThemeReceiver.CHANGE_THEME);
				themeIntent.putExtra(MyThemeReceiver.PKGNAME_STRING, pkgName);
				sendBroadcast(themeIntent);
			}
		}
	}
	/**
	 * <br>功能简述:從4.01版開始將放在DB中的已使用字體設置放到preference中
	 * <br>功能详细描述:爲了解決多進程DextTextView關聯DB問題
	 * <br>注意:
	 */
	private void syncFontCfg() {
		if (VersionControl.getNewVeriosnFirstRun()) {
			FontControler.getInstance(ApplicationProxy.getContext()).syncFontCfgFromDB();
		}
	}
}