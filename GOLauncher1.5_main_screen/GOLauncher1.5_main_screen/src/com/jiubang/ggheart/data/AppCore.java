package com.jiubang.ggheart.data;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.launcherex.R;
import com.gau.utils.net.HttpAdapter;
import com.go.launcher.taskmanager.TaskMgrControler;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.DataChangeListenerProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.file.FileUtil;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IAppDrawerMsgId;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IWidgetMsgId;
import com.jiubang.core.framework.CleanManager;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.core.message.IMessageFilter;
import com.jiubang.ggheart.apps.appmanagement.controler.ApplicationManager;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.appfunc.appsupdate.AppsListUpdateManager;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.dock.DockStyleIconManager;
import com.jiubang.ggheart.apps.desks.net.VersionManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager;
import com.jiubang.ggheart.apps.gowidget.GoWidgetManager;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;
import com.jiubang.ggheart.components.sidemenuadvert.SideAdvertControl;
import com.jiubang.ggheart.data.statistics.FunctionalStatistic;
import com.jiubang.ggheart.data.statistics.SearchStatisticSaver;
import com.jiubang.ggheart.data.theme.DeskThemeControler;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.launcher.PreferenceConfiguration;
import com.jiubang.ggheart.plugin.migrate.MigrateControler;
import com.jiubang.ggheart.plugin.notification.NotificationControler;
import com.jiubang.ggheart.themeicon.ThemeIconManager;

/**
 * UI交互接口，参与业务逻辑的处理，为UI层提供数据及数据持久化的接口。
 * 
 * @author huyong
 * 
 */
public class AppCore implements ICleanable, IMessageFilter {
	// 单例
	private static/* volatile */AppCore sSelfInstance = null;

	private Context mContext = null;

	// 框架基础服务
	private AppDataEngine mAppDataEngine; // 原始数据，与系统的一致
	private HttpAdapter mHttpAdapter = null;
	private VersionManager mVersionManager = null;

	private TaskMgrControler mTaskMgrControler; // 进程管理 // 功能表设置

	private SysShortCutControler mSysShortcutControler;
	private SelfAppItemInfoControler mSelfAppItemControler;
	private DockItemControler mDockItemControler;

	private ThemeManager mThemeManager = null; // 主题模块
	private ImageExplorer mImageExplorer = null;
	private DeskThemeControler mDeskThemeControler;

	private NotificationControler mNotificationControler; // 统计模块
	private MigrateControler mMigrateControler; // 桌面搬家模块

	private DockStyleIconManager mDockStyleIconManager; // dock风格图标安装包管理器

	private GoWidgetManager mGoWidgetManager;
	private CleanManager mCleanManager = null;

	private FunctionalStatistic mFunctionalStatistic; // 收索统计

	private ScheduleTaskHandler mTaskHandler = null;

	//	private DownloadManager mDownloadManager = null; // 下载管理

	private AppsListUpdateManager mAppsListUpdateManager = null; // 程序更新管理

	private ApplicationManager mApplicationManager; // 应用程序管理

	private CommonControler mCommonControler;
	/**
	 * 获取Core实例 在不同的Activity之间共享
	 * 
	 * @param context
	 *            构造所需上下文
	 * @return AppCore的一个实例
	 */
	static public/* synchronized */AppCore getInstance() {
		if (sSelfInstance == null) {
			build();
		}
		return sSelfInstance;
	}

	static public void build() {
		sSelfInstance = new AppCore(ApplicationProxy.getApplication());
		sSelfInstance.construct();
		// 打开主题资源下发功能
		ThemeIconManager.getInstance(ApplicationProxy.getApplication()).startThemeIconIssued();
	}

	static public void destroy() {
		if (sSelfInstance != null) {
			sSelfInstance.cleanup();
		}
		//反注册key接收广播
		DeskSettingUtils.unregisteKeyReceiver();
		// 关闭主题资源下发功能
		ThemeIconManager.getInstance(ApplicationProxy.getApplication()).stopThemeIconIssued();
	}

	private AppCore(Context context) {
		mContext = context;
		mCleanManager = new CleanManager();

		// 创建字体文件夹
		FileUtil.mkDir(LauncherEnv.Path.SDCARD + LauncherEnv.Path.FONT_PATH);
		// 尝试影藏GO桌面小图片
		FileUtil.hideMedia(LauncherEnv.Path.SDCARD + LauncherEnv.Path.LAUNCHER_DIR);
	}

	private void init() {

//		DeskResourcesConfiguration.createInstance(mContext);

		// 数据库模块最先初始化，后面有对数据库是否首次创建有依赖
		// DataModelTmp.getDataModel(mContext);

		DataProvider.getInstance(mContext);

		mHttpAdapter = new HttpAdapter(mContext);
		// mExportDataBase = new ExportDataBase(mContext);

		mThemeManager = ThemeManager.getInstance(mContext);

		mImageExplorer = ImageExplorer.getInstance(mContext);

		// mAppDataEngine = new AppDataEngine(mContext);
		mAppDataEngine = AppDataEngine.getInstance(mContext);

		mDeskThemeControler = new DeskThemeControler(mContext);
		mCleanManager.add(mDeskThemeControler);

		mDockStyleIconManager = DockStyleIconManager.getInstance(mContext);

		// GoWidgetManager
		mGoWidgetManager = new GoWidgetManager(mContext);
		mCleanManager.add(mGoWidgetManager);

		// 下载管理
		//		mDownloadManager = DownloadManager.getInstance(mContext);

		mApplicationManager = ApplicationManager.getInstance(mContext);
		
		//3D拉取侧边栏广告数据
		initSidemenuAdData();

	}

	/**
	 * @author zhangxi
	 * @date 2013-09-22
	 * @describe:侧边栏广告拉数据
	 */
	private void initSidemenuAdData() {
		SideAdvertControl sideAdvertControl = SideAdvertControl.getAdvertControlInstance(mContext);
	}

	private void models() {
		mSysShortcutControler = new SysShortCutControler(mContext);
		mCleanManager.add(mSysShortcutControler);


		mSelfAppItemControler = new SelfAppItemInfoControler(mContext);
		mCleanManager.add(mSelfAppItemControler);

		mDockItemControler = new DockItemControler(mContext);
		mCleanManager.add(mDockItemControler);
		

		mVersionManager = new VersionManager(mContext);
		mCommonControler = CommonControler.getInstance(mContext);

		// 监控
		mNotificationControler = new NotificationControler(mContext, mAppDataEngine);
		mMigrateControler = new MigrateControler(mContext);

		// 收费Ｋey包接收广播
		DeskSettingUtils.registeKeyReceiver();
		
		//begin 原来在GOLancuncherApp的代码移到这里 因为DB Locked问题
		boolean hasPay = DeskSettingUtils.checkHadPay(mContext);
		if (hasPay) {
			DeskSettingUtils.onlyFirstTimeSetNoAdvert(mContext);
		} else {
			DeskSettingUtils.sendBroadCastRecheck(mContext);
		}
		FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(ApplicationProxy.getApplication());
		purchaseManager.registerObserver(SettingProxy.getInstance(ApplicationProxy.getContext()));
		// end
		PreferenceConfiguration.createInstance(mContext);

		// 判断Bata版本
		String preferenceString = mContext.getResources().getString(R.string.curVersion);
		if (preferenceString.contains("Beta") || preferenceString.contains("beta")
				|| preferenceString.contains("BETA")) {
			mFunctionalStatistic = new FunctionalStatistic();
			SearchStatisticSaver.initStatistic(mContext, mFunctionalStatistic.getContent());
		}
	}

	private void construct() {
		// 基本初始化
		init();
		//
		models();
		// 启动定时任务
		startSchedulTask();
	}

	public Context getContext() {
		return mContext;
	}

	public SysShortCutControler getSysShortCutControler() {
		return mSysShortcutControler;
	}

	public SelfAppItemInfoControler getSelfAppItemInfoControler() {
		return mSelfAppItemControler;
	}

	public DockItemControler getDockItemControler() {
		return mDockItemControler;
	}

	public DeskThemeControler getDeskThemeControler() {
		return mDeskThemeControler;
	}

	public NotificationControler getNotificationControler() {
		return mNotificationControler;
	}

	public FunctionalStatistic getFunctionalStatistic() {
		return mFunctionalStatistic;
	}

	// TODO:整理Explorer
	public ImageExplorer getImageExplorer() {
		return mImageExplorer;
	}

	// 获取goWidgetmanager
	public GoWidgetManager getGoWidgetManager() {
		return mGoWidgetManager;
	}

	/**
	 * 获取进程管理控制器
	 * 
	 * @return
	 */
	public TaskMgrControler getTaskMgrControler() {
		// 进程管理
		if (null == mTaskMgrControler) {
			mTaskMgrControler = new TaskMgrControler(mContext, mAppDataEngine);
		}
		return mTaskMgrControler;
	}

	/**
	 * 释放进程管理控制器
	 */
	public void releaseTaskMgrControler() {
		mTaskMgrControler = null;
	}

	/**
	 * http事务处理
	 * 
	 * @author huyong
	 * @return
	 */
	public final HttpAdapter getHttpAdapter() {
		return mHttpAdapter;
	}

	public final VersionManager getVersionManager() {
		if (mVersionManager == null) {
			mVersionManager = new VersionManager(mContext);
		}
		return mVersionManager;
	}

	public final AppsListUpdateManager getAppsListUpdateManager() {
		if (mAppsListUpdateManager == null) {
			mAppsListUpdateManager = AppsListUpdateManager.getInstance(mContext);
		}
		return mAppsListUpdateManager;
	}

	public final ApplicationManager getApplicationManager() {
		if (mApplicationManager == null) {
			mApplicationManager = ApplicationManager.getInstance(mContext);
		}
		return mApplicationManager;
	}

	private void startSchedulTask() {
		if (mTaskHandler == null) {
			mTaskHandler = new ScheduleTaskHandler(mContext);
		}
		mTaskHandler.startScanAppTask();
		mTaskHandler.startCheckUpdateTask();
//		 开启统计任务
		mTaskHandler.startStatisticsTask();
//		 开启定时弹出评分对话框任务
		mTaskHandler.startRateDialogTask();
//		DB自动备份
		mTaskHandler.startDBBackUpTask();
		//提示用户卸载不常用应用
		mTaskHandler.startNotifyUninstallTask();
		//开启定时内测功能
		mTaskHandler.startBetaTask();

		//开启定时更新侧边栏广告
		mTaskHandler.startUpSideAdData();
	}

	private void cancelSchedulTask() {
		if (mTaskHandler != null) {
			mTaskHandler.cancel();
			mTaskHandler = null;
		}
	}

	/**
	 * 清除后台内存中数据
	 * 
	 * @author huyong
	 */

	@Override
	public void cleanup() {
		// 取消定时任务
		cancelSchedulTask();
		DataChangeListenerProxy.getInstance().setDataChangeListener(null);
		StatisticsManager.getInstance(ApplicationProxy.getContext()).destory();
		mGoWidgetManager.onDestory();
		mCleanManager.cleanup();

		SettingProxy.cleanup();

		if (null != mThemeManager) {
			mThemeManager.cleanup();
		}

		// 清理监控
		if (null != mNotificationControler) {
			mNotificationControler.selfDestruct();
			mNotificationControler = null;
		}

		// 桌面搬家
		if (null != mMigrateControler) {
			mMigrateControler.selfDestruct();
			mMigrateControler = null;
		}

		// 设置配置
		PreferenceConfiguration.destroyInstance();

		DeskResourcesConfiguration.destroyInstance();

		// 功能统计
		if (null != mFunctionalStatistic) {
			SearchStatisticSaver.saveStatistic(mContext, mFunctionalStatistic.getContent());
		}
		// 下载管理
		//		DownloadManager.destroy();
		DataProvider.destroy();
		
		//清理AdverControl
		SideAdvertControl.destroy();

		FunctionPurchaseManager.destory();
		GetJarManager.destory();
	}

	// @Override
	// public boolean isConsumed(int msgId)
	// {
	// // TODO 在此处对关心的消息返回true
	// boolean ret = false;
	// switch (msgId)
	// {
	// case IDiyMsgIds.EVENT_UNINSTALL_INTENT:
	// case IDiyMsgIds.EVENT_REFLUSH_SDCARD_IS_OK:
	// case IDiyMsgIds.EVENT_REFLUSH_TIME_IS_UP:
	// case IDiyMsgIds.EVENT_UNINSTALL_APP:
	// case IDiyMsgIds.EVENT_UNINSTALL_APPS:
	// case IDiyMsgIds.EVENT_THEME_CHANGED:
	// ret = true;
	// break;
	//
	// default:
	// break;
	// }
	// return ret;
	// }

	@Override
	public int getMsgHandlerId() {
		return IDiyFrameIds.APPCORE;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		switch (msgId) {
			case IFrameworkMsgId.SYSTEM_ON_DESTROY : {
				mGoWidgetManager.onDestory();
			}
				break;
			case IAppCoreMsgId.EVENT_SD_MOUNT :
			case IAppCoreMsgId.EVENT_REFLUSH_SDCARD_IS_OK :
			case IAppCoreMsgId.EVENT_REFLUSH_TIME_IS_UP : {
				mThemeManager.onBCChange(msgId, param, obj);
				// 通知功能表重新检查是否需要冲洗加载主题
				MsgMgrProxy.sendBroadcastHandler(this, IAppDrawerMsgId.APPDRAWER_INDICATOR_THEME_CHANGE,
						-1, null, null);
				MsgMgrProxy.sendBroadcastHandler(this, IAppDrawerMsgId.APPDRAWER_TAB_HOME_THEME_CHANGE,
						-1, null, null);

			}
				break;

			case IAppCoreMsgId.EVENT_INSTALL_PACKAGE :
			case IAppCoreMsgId.EVENT_INSTALL_APP : {
				mThemeManager.onBCChange(msgId, param, obj);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, IWidgetMsgId.UPDATE_GOWIDGET,
						-1, obj[0], null);
			}
				String packageName = (String) obj[0];
				if (packageName.startsWith(PackageName.LANGUAGE_PACKAGE)) {
					String currentLanguage = packageName.substring(
							PackageName.LANGUAGE_PACKAGE.length() + 1, packageName.length());
					String language = Locale.getDefault().toString();
					if (language.contains(currentLanguage)) {
						PreferencesManager preferences = new PreferencesManager(mContext,
								IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
						preferences.putString(IPreferencesIds.CURRENT_SELECTED_LANGUAGE,
								currentLanguage);
						preferences.commit();
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
								ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
					}

					if (packageName.equals(PackageName.PRIME_KEY)
							|| packageName.equals(LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
						if (packageName.equals(LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
							FunctionPurchaseManager.getInstance(mContext).onPrimeKeyInstall();
						}
					}

				}
				break;

			case IAppCoreMsgId.EVENT_UNINSTALL_PACKAGE : {
				mThemeManager.onBCChange(msgId, param, obj);
				mNotificationControler.handleLauncherEvent(msgId, param, obj);
//				if (ShellPluginFactory.isUseShellPlugin(mContext)) {
//					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
//							IDiyMsgIds.UPDATE_GOWIDGET, -1, obj[0], null);
//				} else {
//					mGoWidgetManager.updateGoWidget((String) obj[0]);
//				}
			}
				break;
			case IAppCoreMsgId.EVENT_UPDATE_APP : 
			case IAppCoreMsgId.EVENT_UPDATE_PACKAGE : {
				mThemeManager.onBCChange(msgId, param, obj);
				// widget升级后，重启桌面
				final String pkgName = (String) obj[0];
				final GoWidgetManager widgetManager = getGoWidgetManager();
				if (widgetManager.containsPackage(pkgName)) {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
							ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
				}
				// 如果语言包升级后。重启桌面
				String language = mContext.getResources().getConfiguration().locale.getLanguage();
				String languagePackage = PackageName.LANGUAGE_PACKAGE + "." + language;
				if (languagePackage.equals(pkgName)) {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
							ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
				} else if (pkgName.equals(PackageName.NOTIFICATION_PACKAGE_NAME)) {
					if (getNotificationControler() != null) {
						getNotificationControler().resetFlags();
						getNotificationControler().checkNotification();
					}
				}
			}
				break;

			case IAppCoreMsgId.EVENT_UNINSTALL_APP : {
				mThemeManager.onBCChange(msgId, param, obj[0], obj[1]);
				mNotificationControler.handleLauncherEvent(msgId, param, obj[0], obj[1]);
//				if (ShellPluginFactory.isUseShellPlugin(mContext)) {
//					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
//							IDiyMsgIds.UPDATE_GOWIDGET, -1, obj[0], null);
//				} else {
//					mGoWidgetManager.updateGoWidget((String) obj[0]);
//				}
			}
				break;

			case IAppCoreMsgId.EVENT_UPDATE_EXTERNAL_PACKAGES : {
				mGoWidgetManager.refreshExternalWidget((ArrayList<String>) obj[0]);
			}
				break;

			case IAppCoreMsgId.EVENT_THEME_CHANGED : {
				SettingProxy.getInstance(mContext).onBCChange(msgId, param, obj);
				packageName = null;
				if (null != obj && obj.length > 0) {
					packageName = (String) obj[0];
				}
				SettingProxy.addScreenStyleSetting(packageName);
				mDeskThemeControler.handleLauncherEvent(msgId, param, obj);
				mAppDataEngine.onHandleThemeIconStyleChanged();
				// edit by chenguanyu：将widget应用皮肤抽离到case
				// IDiyMsgIds.EVENT_CHANGE_WIDGET_THEME
				// mGoWidgetManager.startApplyWidgetTheme((String)obj[0]);
				// end edit

				if (null != PreferenceConfiguration.getInstance()) {
					PreferenceConfiguration.getInstance().onBCChange(msgId, param, obj);
				}
				// 自定义应用换肤
				mSelfAppItemControler.handleMessage(who, msgId, param, obj);
			}
				break;

			case IAppCoreMsgId.EVENT_CHANGE_WIDGET_THEME : {
				mGoWidgetManager.startApplyWidgetTheme((String) obj[0]);
			}
				break;

			case IAppCoreMsgId.EVENT_CHECK_THEME_ICON : {
				mAppDataEngine.onHandleThemeIconStyleChanged();
			}
				break;
			case IAppCoreMsgId.REFRESH_SCREENICON_THEME : {
				mAppDataEngine.onHandleScreenThemeIconStyleChanged();
			}
				break;
			case IAppCoreMsgId.REFRESH_FOLDER_THEME : {
				mAppDataEngine.onHandleFolderThemeIconStyleChanged();
			}
				break;
			case IAppCoreMsgId.REFRESH_SCREENINDICATOR_THEME : {
				mAppDataEngine.onHandleScreenIndicatorStyleChanged();
			}
				break;

			case IAppCoreMsgId.EVENT_SHOW_OR_HIDE_ICON_BASE : {
				boolean showBase = param == 1 ? true : false;
				mAppDataEngine.onHandleShowIconBaseChanged(showBase);
			}
				break;

			default :
				break;
		}
		return false;
	}

	public ScheduleTaskHandler getmTaskHandler() {
		return mTaskHandler;
	}
	
}
