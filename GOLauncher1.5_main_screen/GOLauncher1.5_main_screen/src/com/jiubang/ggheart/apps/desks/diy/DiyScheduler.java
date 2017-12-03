package com.jiubang.ggheart.apps.desks.diy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.LiveFolders;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.DataChangeListenerProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.proxy.ValueReturned;
import com.go.proxy.VersionControl;
import com.go.util.AppUtils;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.device.Machine;
import com.go.util.log.LogConstants;
import com.go.util.window.OrientationControl;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.ICoverFrameMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.golauncher.message.IFolderMsgId;
import com.golauncher.message.IMediaManagementMsgId;
import com.golauncher.message.IScreenAdvertMsgId;
import com.golauncher.message.IScreenEditMsgId;
import com.golauncher.message.IScreenFrameMsgId;
import com.golauncher.message.IWidgetMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.framework.IDispatchEventHandler;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.apps.appfunc.controler.AppDrawerControler;
import com.jiubang.ggheart.apps.appfunc.controler.SwitchControler;
import com.jiubang.ggheart.apps.config.GOLauncherConfig;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingMainActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogLanguageChoice;
import com.jiubang.ggheart.apps.desks.diy.frames.cover.CoverFrame;
import com.jiubang.ggheart.apps.desks.diy.frames.cover.CoverMonitor.ICoverCallback;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenModifyFolderActivity;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.Search;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.ScreenEditConstants;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.ScreenEditStatistics;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.util.ScreenEditPushConstants;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.util.ScreenEditPushController;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.apps.desks.imagepreview.ChangeIconPreviewActivity;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.desks.settings.AppList;
import com.jiubang.ggheart.apps.desks.settings.AppListMultiple;
import com.jiubang.ggheart.apps.systemwidget.SysSubWidgetInfo;
import com.jiubang.ggheart.billing.AppInBillingActivity;
import com.jiubang.ggheart.billing.PurchaseSupportedManager;
import com.jiubang.ggheart.billing.base.Consts;
import com.jiubang.ggheart.billing.base.IabHelper;
import com.jiubang.ggheart.billing.base.IabResult;
import com.jiubang.ggheart.components.DeskBuilder;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.components.advert.AdvertDialogCenter;
import com.jiubang.ggheart.components.renamewindow.RenameActivity;
import com.jiubang.ggheart.components.sidemenuadvert.SideAdvertControl;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.AppService;
import com.jiubang.ggheart.data.DataChangeListener;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.DataType;
import com.jiubang.ggheart.data.DBImport.DBImporter;
import com.jiubang.ggheart.data.DBImport.LauncherSelectorActivity;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.SysAppInfo;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.data.statistics.FunctionalStatistic;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;
import com.jiubang.ggheart.launcher.CheckApplication;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.IconUtilities;
import com.jiubang.ggheart.launcher.InstallShortcutReceiver;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.LauncherWidgetHost;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.launcher.ThreadName;
import com.jiubang.ggheart.plugin.PluginClassLoader;
import com.jiubang.ggheart.plugin.theme.ThemeLauncherProxy;
import com.jiubang.ggheart.plugin.theme.inf.IThemeLauncherProxy;
import com.jiubang.ggheart.recommend.localxml.XmlRecommendedApp;
import com.jiubang.ggheart.uninstallcheck.UninstallBussinessCheck;

/**
 * DIY桌面调度器 负责各层的创建和销毁等工作 负责与Activity外部的消息交互,并进行传递 负责与后台主题数据进行交互的层
 * 
 * @author yuankai
 * @version 1.0
 */
public class DiyScheduler
		implements
			IMessageHandler,
			KeyEvent.Callback,
			IDispatchEventHandler,
			BroadCasterObserver,
			WallpaperControler.IWallpaperChangeListener,
			ICoverCallback {
	public static final String LAST_MOTION_X = "lastMotionX";
	public static final String LAST_MOTION_Y = "lastMotionY";

	private static final String START_NOTIFICATION_THREAD_NAME = "start_notification_thread_name";
	
	private static final int TIME_INTERVAL = 30 * 60 * 1000; // 30分钟

	private Activity mActivity;
	private StatusBarHandler mStatusBarHandler; // 状态栏管理器
	private AppInvoker mAppInvoker; // 应用启动模块
	private FrameControl mFrameControl; // 帧管理器

	private boolean mIsNewIntent; // 是否是新的Intent, 用于接收home事件

	public static final String EXTRA_CUSTOM_WIDGET = "custom_widget";
	public static final String SEARCH_WIDGET = "search_widget";
	public final static String FIELD_INSERT_INDEX = "insert_index";
	static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

	private final static String SCREEN_LOCKED = "screenlocked";

	private int mLastOrientation; // 最近一次的屏幕方向
	private InstallShortcutReceiver mInstallShortcutReceiver; // 添加会计方式receiver

	private Locale mLastLocale; // 当前语言

	/**
	 * 请求添加快捷方式的操作，请求码在类IShortcutHolderIds里定义
	 */
	private int mShortcutHolerId = IShortcutHolderIds.SHORTCUT_SCREEN;

	// 正在添加桌面组件时，不处理横竖屏事件
	private boolean mAddingComponent = false;

	// 判断是否从其他Activity返回
	private boolean mIsOut = false;

	private WallpaperControler mWallpaperControler = null;

	private boolean mIsKeyDown = false;
	/** 区分进入快捷方式的发起者 */
	private static final int DOCK_ACTION = 0x1;
	private static final int SCREEN_ACTION = 0x2;
	private static final int DOCK_GESTURE = 0x3;
	private int mShortcutSource = SCREEN_ACTION;
	// add by yangbing 2012-06-20
	private boolean mIsMusicBarIntent;

//	private ArrayList<String> mApkLanguageArrayList;

	// private final static boolean ENABLE_SCREEN_CAPTURE =
	// DebugState.isScreenCaptureEnable();
	// 语言选择列表对话框
	private DialogLanguageChoice mLanguageChoiceDialog;
	// 罩子层
	private CoverFrame mCoverFrame;

	private int mPaddingAddWidgetId;
	
	private LauncherWidgetHost mWidgetHost;
	
	private ScreenEditPushController mPushController;
	
	//private int mChildProcessId = 0;
	
	/**
	 * 调度器构造方法
	 * 
	 * @param activity
	 *            UI活动
	 * @param frameManager
	 *            帧管理器
	 * @param id
	 *            惟一ID标识
	 */
	public DiyScheduler(Activity activity, FrameControl frameControl) {
		mWallpaperControler = WallpaperControler.buildInstance(activity, this);
		mStatusBarHandler = new StatusBarHandler();
		mActivity = activity;
		// 添加消息接收者
		MsgMgrProxy.registMsgHandler(this);
		mFrameControl = frameControl;
		// 或者程序支持的语言集合
//		mApkLanguageArrayList = new ArrayList<String>();
//		String[] supportLanguages = mActivity.getResources().getStringArray(
//				R.array.support_language);
//		for (String apkLanguage : supportLanguages) {
//			mApkLanguageArrayList.add(apkLanguage);
//		}
		
		mWidgetHost = new LauncherWidgetHost(activity, LauncherEnv.APP_WIDGET_HOST_ID);
		mPushController = new ScreenEditPushController(activity);
	}

	private void exit(boolean restart) {
		// 通知周边插件桌面退出
		mActivity.sendBroadcast(new Intent(ICustomAction.ACTION_LAUNCHER_EXIT));
		// 无论是重启还是退出，都把下载管理器销毁
		// DownloadManager.destroy();
		DataProvider.destroy();
		AppUtils.cancelNotificaiton(mActivity, INotificationId.ONE_X_GUIDE);
		AppUtils.cancelNotificaiton(mActivity, INotificationId.LANGUAGE_START_GGMENU);
		AppUtils.cancelNotificaiton(mActivity, INotificationId.MIGRATE_TIP);
		AppUtils.cancelNotificaiton(mActivity, INotificationId.SNAPSHOT_NOTIFICATION);
		/*if (mChildProcessId != Integer.MIN_VALUE) {
			try {
				AppUtils.killProcess(mChildProcessId);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}*/
		
		if (!restart) {
			mActivity.finish();
			// 停止服务
			stopAppService();
			// stopMessageService();
			AppCore.destroy();
		}
		
		//当是重启以及设置项中没有勾选常驻进程的时候。
		if (restart && !SettingProxy.getThemeSettingInfo().mIsPemanentMemory) {
		    // 停止服务
            stopAppService();
		}
		
		AppUtils.killProcess();
	}

	private void listenDataChanged() {
		DataChangeListener listener = new DataChangeListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void dataChanged(int dataType, int param, Object object, List objects1) {
				if (DataType.DATATYPE_THEMESETTING == dataType) {
					switch (param) {
						case DataType.APPLY_CHANGE : // 设置更改
							checkPersistenceAndTransparentStatusbar();
							break;

						case DataType.EXIT_GOLAUNCHER :
							exit(false);
							break;

						case DataType.RESTART_GOLAUNCHER :
							exit(true);
							break;

						default :
							break;
					}
				}

				MsgMgrProxy.sendBroadcast(this, IAppCoreMsgId.APPCORE_DATACHANGE, dataType, null,
						object, objects1);
			}
		};

		if (null != ApplicationProxy.getApplication()) {
			DataChangeListenerProxy.getInstance().setDataChangeListener(listener);
		}

		SettingProxy.getInstance(ApplicationProxy.getContext()).registerObserver(this);
	}

	private void initAppCore() {
		new Thread(ThreadName.SCHEDULER_INITIALIZE_APPCORE) {
			@Override
			public void run() {
				Looper.prepare();
				{
					// 加载配置文件数据
					GOLauncherConfig.getInstance(ApplicationProxy.getContext()).loadConfig();
					//					ChannelConfig channelConfig = GOLauncherConfig.getInstance(ApplicationProxy.getContext()).getChannelConfig();
					//					if (channelConfig != null && channelConfig.isNeedDownloadService()) {
					//						DownloadControllerProxy.getInstance().getDownloadController();
					//					}
					// 启动桌面时，在其他桌面添加应用中心快捷方式，只会添加一次
//					AppsManagementActivity.addShortcut(mActivity);
					// 暂时监听 TODO:AppCore完善后去除此处
					listenDataChanged();
					// 加载GoWidget信息
					AppCore.getInstance().getGoWidgetManager().prepareGoWidgetInfo();
					// 加载应用程序信息
					if (null != AppDataEngine.getInstance(ApplicationProxy.getContext())) {
						AppDataEngine.getInstance(ApplicationProxy.getContext()).scanInitAllAppItems();
					}

					// 保存当前语言TODO:与DataModel的checkLanguage()统一
					PrivatePreference pref = PrivatePreference.getPreference(mActivity);
					Locale locale = Locale.getDefault();
					String language = String.format(LauncherEnv.LANGUAGE_FORMAT,
							locale.getLanguage(), locale.getCountry());
					pref.putString(PrefConst.KEY_LANGUAGE, language);
					pref.commit();
				}
			}
		}.start();
	}

	/**
	 * 负责所有组件的初始化工作,统一地方
	 */
	private void init() {

		// 应用启动模块
		mAppInvoker = new AppInvoker(mActivity, AppDrawerControler.getInstance(mActivity), AppCore
				.getInstance().getNotificationControler());

		// 加载数据库数据 TODO:改名 不叫initAppCore
		initAppCore();

		registReceivers();

		// 取消默认
		// showCancelDefaultLauncher();

		// 设置是否支持版本间数据共享
		if (VersionControl.getNewVeriosnFirstRun()) {
			new Thread(ThreadName.DIY_INIT_DBIMPORTER) {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					PreferencesManager sharedPreferences = new PreferencesManager(mActivity,
							IPreferencesIds.DB_PROVIDER_SUPPORT, Context.MODE_WORLD_READABLE);

					if (!sharedPreferences.getBoolean(IPreferencesIds.IMPORT_SUPPORT, false)) {
						sharedPreferences.putBoolean(IPreferencesIds.IMPORT_SUPPORT, true);
						sharedPreferences.commit();
					}
					boolean hasimport = sharedPreferences.getBoolean(IPreferencesIds.HAS_IMPORTED,
							false);
					if (!hasimport) {
						DBImporter.sendNotify(mActivity);
						sharedPreferences.putBoolean(IPreferencesIds.HAS_IMPORTED, true);
						sharedPreferences.commit();
					}
				}

			}.start();
		}
	}

	// private void showCancelDefaultLauncher()
	// {
	// GoSettingControler controler = GoSettingControler.getInstance(ApplicationProxy.getContext());
	// if (null != controler)
	// {
	// ThemeSettingInfo info = controler.getThemeSettingInfo();
	// if (null != info && !info.mTipCancelDefaultDesk)
	// {
	// return;
	// }
	// }
	//
	// final String packageStr = AppUtils.getDefaultLauncherPackage(mActivity);
	//
	// // 没有设置
	// // 设置为Go桌面
	// // 设置为GO锁屏
	// if (null == packageStr
	// || packageStr.equals(LauncherEnv.PACKAGE_NAME)
	// || packageStr.equals(LauncherEnv.GO_LOCK_PACKAGE_NAME))
	// {
	// return;
	// }
	//
	// DeskBuilder builder = new DeskBuilder(mActivity);
	// builder.setTitle(mActivity.getString(R.string.cancel_default_tip_title));
	// final View view =
	// ((LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cancel_default_tip,
	// null);
	// CheckBox box = null;
	// if (null != view)
	// {
	// builder.setView(view);
	// box = (CheckBox)view.findViewById(R.id.cancel_default_no_tip);
	// }
	// else
	// {
	// builder.setMessage(mActivity.getString(R.string.cancel_default_tip_message));
	// }
	// final CheckBox checkBox = box;
	// builder.setPositiveButton(mActivity.getString(R.string.ok), new
	// DialogInterface.OnClickListener()
	// {
	// @Override
	// public void onClick(DialogInterface dialog, int which)
	// {
	// AppUtils.showAppDetails(mActivity, packageStr);
	// Toast.makeText(mActivity,
	// mActivity.getString(R.string.cancel_default_toast_message),
	// Toast.LENGTH_LONG).show();
	//
	// if (null != checkBox && checkBox.isChecked())
	// {
	// noTipCancelDefaultDesk();
	// }
	// }
	// });
	// builder.setNegativeButton(mActivity.getString(R.string.cancel), new
	// DialogInterface.OnClickListener()
	// {
	// @Override
	// public void onClick(DialogInterface dialog, int which)
	// {
	// if (null != checkBox && checkBox.isChecked())
	// {
	// noTipCancelDefaultDesk();
	// }
	// }
	// });
	// AlertDialog dialog = builder.create();
	// dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
	// {
	// @Override
	// public void onDismiss(DialogInterface dialog)
	// {
	// if (null != checkBox && checkBox.isChecked())
	// {
	// noTipCancelDefaultDesk();
	// }
	// }
	// });
	// dialog.show();
	//
	// try {
	// Vibrator vibrator = (Vibrator)
	// mActivity.getSystemService(Context.VIBRATOR_SERVICE);
	// long pattern[] = { 0, 150 };
	// vibrator.vibrate(pattern, -1);
	// } catch (Throwable e) {
	// // TODO Auto-generated catch block
	// Log.e("Vibrator error"," in showCancelDefaultLauncher");
	// }
	// }

	private void noTipCancelDefaultDesk() {
		ThemeSettingInfo info = SettingProxy.getThemeSettingInfo();
		if (null != info && info.mTipCancelDefaultDesk) {
			info.mTipCancelDefaultDesk = false;
			SettingProxy.updateThemeSettingInfo(info);
		}
	}

	private void showMigrateTip() {
		DeskBuilder builder = new DeskBuilder(mActivity);
		builder.setTitle(R.string.migrate_tip_title);
		builder.setMessage(R.string.migrate_tip_message);
		builder.setPositiveButton(mActivity.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestMigrateDesk();
					}
				});
		builder.setNegativeButton(mActivity.getString(R.string.cancel), null);
		builder.create().show();
	}

	private void requestMigrateDesk() {
		Intent intent = new Intent("com.ma.deskmigrate.DeskMigrate");
		Bundle bundle = new Bundle();
		bundle.putInt("code", IRequestCodeIds.REQUEST_MIGRATE_DESK);
		intent.putExtras(bundle);
		try {
			mActivity.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unRegistReceivers() {
		mActivity.unregisterReceiver(mInstallShortcutReceiver);
		// 反注册壁纸更新事件
		mWallpaperControler.unRegistWallpaperReceiver();
	}

	private void registReceivers() {
		mInstallShortcutReceiver = new InstallShortcutReceiver();
		IntentFilter filter = new IntentFilter(ICustomAction.ACTION_INSTALL_SHORTCUT);
		mActivity.registerReceiver(mInstallShortcutReceiver, filter);
		// 注册壁纸更新事件
		mWallpaperControler.registWallpaperReceiver();
	}

	/**
	 * 处理Activity的onNewIntent方法
	 * 
	 * @param intent
	 *            意图
	 */
	public void onNewIntent(Intent intent) {

		// 从音乐播放通知栏回来，add by yangbing 2012-06-20
		if (intent != null
				&& intent.getStringExtra(ICustomAction.DESTINATION_KEY) != null
				&& ICustomAction.DESTINATION_RETURN_TO_GOMUSIC.equals(intent
						.getStringExtra(ICustomAction.DESTINATION_KEY))) {
			// 回到播放界面
			SwitchControler.getInstance(mActivity).showMediaManagementMusicPlayerContent();
			mIsMusicBarIntent = true;
			return;
		}
		// 关闭提示
		if (MsgMgrProxy.getTopFrameId() == IDiyFrameIds.GUIDE_GL_FRAME) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
					IDiyFrameIds.GUIDE_GL_FRAME, null, null);
		}
		if (intent != null && !onNewIntentFilter(intent)) {
			// 广播给下层
			MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_ON_NEW_INTENT, -1, intent,
					null);
		}

		if (intent != null) {
			// add by jiang 用于屏幕锁定 从通知栏 跳菜单
			String s = intent.getStringExtra(SCREEN_LOCKED);
			if (s != null && s.equals(SCREEN_LOCKED)) {
				Intent it = new Intent();;
				it.setAction(ICustomAction.ACTION_SHOW_MENU);
				it.putExtra(SCREEN_LOCKED, SCREEN_LOCKED);
				mAppInvoker.invokeApp(it);
			}
		}
	}

	/**
	 * 过滤操作
	 * 
	 * @param intent
	 *            intent对象
	 * @return 是否处理
	 */
	private boolean onNewIntentFilter(Intent intent) {
		// 关闭系统所有弹出框
		closeSystemDialogs();
		mIsNewIntent = true;

		// 过滤HOME事件
		String action = intent.getAction();
		if (Intent.ACTION_MAIN.equals(action)) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
					IFrameworkMsgId.SYSTEM_HOME_CLICK, 0, intent, null);
			return true;
		} else if (ICustomAction.ACTION_SHOW_THEME_PREVIEW.equals(action)) {
			return true;
		}

		return false;
	}

	/**
	 * 处理恢复事件
	 */
	public void onResume() {
		// LogUnit.i("onResume", "onResume");
		// 从音乐播放通知栏恢复，进入功能表音乐播放器界面 add by yangbing 2012-06-20
		if (mIsMusicBarIntent) {
			//			if (mFrameControl.getTopFrame() != null
			//					&& mFrameControl.getTopFrame().getId() == IDiyFrameIds.APPFUNC_FRAME) {
			//				// 已经在功能表界面了,先调用AppFuncFrame的onResume()，重启绘画引擎
			//				AppFuncFrame appFuncFrame = (AppFuncFrame) mFrameControl.getTopFrame();
			//				appFuncFrame.onResume();
			//				// 发消息切换回所有程序tab页
			//				DeliverMsgManager.getInstance().onChange(AppFuncConstants.TABCOMPONENT,
			//						AppFuncConstants.RETURN_MUSIC_PLAY, null);
			//				// 回到播放界面
			//				SwitchControler.getInstance(mActivity).showMediaManagementMusicPlayerContent();
			//			}
			// 回到播放界面
			SwitchControler.getInstance(mActivity).showMediaManagementMusicPlayerContent();
			mIsMusicBarIntent = false;
			//			return;
		}
		// 检查状态栏状态
		mStatusBarHandler.checkForStatusBar();

		// 如果桌面加载完成设置方向 更新壁纸
		if (null != AppDataEngine.getInstance(ApplicationProxy.getContext())
				&& AppDataEngine.getInstance(ApplicationProxy.getContext()).isLoadData()) {
			setOrientation();
		}
		mWallpaperControler.updateWallpaper(false);
		//		mWallpaperControler.updateWallpaperInBackground(false);

		if (mIsNewIntent) {
			// Post to a handler so that this happens after the search dialog
			// tries to open
			// itself again.
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SearchManager searchManagerService = (SearchManager) mActivity
							.getSystemService(Context.SEARCH_SERVICE);
					try {
						if (searchManagerService != null) {
							searchManagerService.stopSearch();
						}
					} catch (Exception e) {
					}
				}
			});

		}

		mIsNewIntent = false;
		if (mFrameControl == null) {
			return;
		}

		// 广播resume事件
		MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_ON_RESUME, -1, null, null);
		if (mCoverFrame != null) {
			mCoverFrame.onResume();
		}
	}

	/**
	 * 系统pause事件回调
	 */
	public void onPause() {
		// 进入pause状态时的时间
		WallpaperControler.sStopedTime = System.currentTimeMillis();
		// 保存上次的屏幕方向
		Configuration configuration = mActivity.getResources().getConfiguration();
		mLastLocale = configuration.locale;
		mLastOrientation = configuration.orientation;
		MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_ON_PAUSE, -1, null, null);
		if (mCoverFrame != null) {
			mCoverFrame.onPause();
		}
	}

	/**
	 * 系统start事件回调
	 */
	public void onStart() {
		Log.i("DemoService", "DiyScheduer.onStart");
		// LogUnit.i("onStart", "onStart");
		Log.i(LogConstants.HEART_TAG, "onStart");
		MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_ON_START, -1, null, null);
	}

	/**
	 * 系统stop事件回调
	 */
	public void onStop() {
		// LogUnit.i("onStop", "onStop");
		Log.i(LogConstants.HEART_TAG, "onStop");
		// 进入stop状态时的时间
		WallpaperControler.sStopedTime = System.currentTimeMillis();
		MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_ON_STOP, -1, null, null);

		if (mCoverFrame != null) {
			mCoverFrame.onPause();
		}
//		stopUninstallService();
	}

	/**
	 * 系统create事件回调
	 */
	public boolean onCreate(Bundle savedInstance) {
		Log.i("DemoService", "DiyScheduler onCreate");
		// 初始化页面
		if (!mFrameControl.initCachedFrame()) {
			return false;
		}
		// LogUnit.i("onCreate", "onCreate");

		// 初始化
		init();
		checkPersistenceAndTransparentStatusbar();
		// mWallpaperControler.updateWallpaper(false);//第一次壁纸设置提前到此处
		startAppService();
		startNotificationService();
		// startMessageCenterService();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					UninstallBussinessCheck.recommendNextLauncher(mActivity);
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("DemoService", "Failed to load the uninstall service module");
				}
			}
		}, "UninstallBussinessCheckThread").start();
		return true;
	}

	/**
	 * 启动后台服务
	 * 
	 * @author huyong
	 */
	private void startAppService() {
		Intent intent = new Intent();
		intent.setClass(mActivity.getApplicationContext(), AppService.class);
		Log.i("DemoService", "Before startAppService");
		mActivity.getApplicationContext().startService(intent);
		Log.i("DemoService", "End startAppService");
	}

	// private void startMessageCenterService()
	// {
	// Intent intent = new Intent();
	// intent.setClass(mActivity.getApplicationContext(),
	// MessageCenterService.class);
	// mActivity.getApplicationContext().startService(intent);
	// }

	private void startNotificationService() {
		if (GoAppUtils.isAppExist(mActivity, ICustomAction.NOTIFICATION_PACKAGE)) {
			new Thread(START_NOTIFICATION_THREAD_NAME) {
				@Override
				public void run() {
					Intent it = new Intent(ICustomAction.ACTION_NOTIFICATIONACTION_START_SERVICE);
					mActivity.startService(it);
					super.run();
				}
			}.start();
		}
	}

	// private void stopNotificationService(){
	// if (GoAppUtils.isAppExist(mActivity, InnerAction.NOTIFICATION_PACKAGE)){
	// Intent it = new Intent(InnerAction.NOTIFICATIONACTION_START_SERVICE);
	// mActivity.stopService(it);
	// }
	// }
	/**
	 * 停止后台服务
	 * 
	 * @author huyong
	 */
	private void stopAppService() {
		Intent intent = new Intent();
		intent.setClass(mActivity.getApplicationContext(), AppService.class);
		Log.i("DemoService", "Before stopAppService");
		mActivity.getApplicationContext().stopService(intent);
		Log.i("DemoService", "End stopAppService");
	}

	// private void stopMessageService()
	// {
	// Intent intent = new Intent();
	// intent.setClass(mActivity.getApplicationContext(),
	// MessageCenterService.class);
	// mActivity.getApplicationContext().stopService(intent);
	// }
	/**
	 * 系统destroy事件回调
	 */
	public void onDestroy() {
		//关闭掉内购相关的服务
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
		// LogUnit.i("onDestroy", "onDestroy");
		MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_ON_DESTROY, -1, null, null);
		unRegistReceivers();

		// 清理框架代码,后面会做杀进程的事情，所以此处不需要作回收
		// mFrameManager.cleanFrameWork();
		// 前台清理工作
		mFrameControl.cleanup();
		if (mCoverFrame != null) {
			mCoverFrame.cleanup();
			mCoverFrame = null;
		}
	}

	/**
	 * 设置屏幕翻转
	 */
	private void setOrientation() {
		// 检查屏幕翻转设置，并应用
		int oriType = SettingProxy.getGravitySettingInfo().mOrientationType;
		OrientationControl.setOrientation(mActivity, oriType);
	}

	private void closeSystemDialogs() {
		try {
			mActivity.removeDialog(IDialogIds.DIALOG_DIY);
			mActivity.getWindow().closeAllPanels();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	//	private void gotoWallpaperSelect() {
	//		if (!checkShowWallpapersettingGuide()) {
	//			final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
	//			Bundle bundle = new Bundle();
	//			bundle.putString(ChooseWallpaper.CHOOSERTYPE, ChooseWallpaper.TYPE_WALLPAPERCHOOSER);
	//			pickWallpaper.putExtras(bundle);
	//
	//			Intent chooser = Intent.createChooser(pickWallpaper,
	//					mActivity.getText(R.string.chooser_wallpaper));
	//
	//			WallpaperManager wm = (WallpaperManager) mActivity
	//					.getSystemService(Context.WALLPAPER_SERVICE);
	//			WallpaperInfo wi = wm.getWallpaperInfo();
	//			if (wi != null && wi.getSettingsActivity() != null) {
	//				LabeledIntent li = new LabeledIntent(mActivity.getPackageName(),
	//						R.string.configure_wallpaper, 0);
	//				li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
	//				chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
	//			}
	//
	//			startActivity(chooser, null);
	//		}
	//	}

	private boolean checkShowWallpapersettingGuide() {
		// SharedPreferences sharedPreferences = mActivity
		// .getSharedPreferences(IPreferencesIds.USERTUTORIALCONFIG,
		// Context.MODE_PRIVATE);
		// boolean shouldShowGuide =
		// sharedPreferences.getBoolean(LauncherEnv.SHOULD_SHOW_WAPAPERSETTING_GUIDE,
		// true);
		// if (shouldShowGuide)
		// {
		// 新加了设置按钮，改为每次点击都弹用户指引
		// GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_WALLPAPER_SETTING);
		// handleMessage(this,
		// IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
		// }
		// SharedPreferences.Editor editor = sharedPreferences.edit();
		// editor.putBoolean(LauncherEnv.SHOULD_SHOW_WAPAPERSETTING_GUIDE,
		// false);
		// editor.commit();

		return true;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case IFrameworkMsgId.REMOVE_FRAME :
					mFrameControl.hideFrame(msg.arg1);
					break;

				case IFrameworkMsgId.SHOW_FRAME :
					mFrameControl.showFrame(msg.arg1);
					break;
				default :
					break;
			}
		};
	};
	private boolean mNeedRestart;

	void checkPersistenceAndTransparentStatusbar() {
		final ThemeSettingInfo themeSettingInfo = SettingProxy.getThemeSettingInfo();
		if (themeSettingInfo != null) {
//			boolean isPemanentMemory = themeSettingInfo.mIsPemanentMemory;
//			setPersistent(mActivity, isPemanentMemory);
			if (mWallpaperControler != null) {
				mWallpaperControler
						.setTransparentStatusbarSupport(themeSettingInfo.mTransparentStatusbar);
			}
		}
	}

//	private void setPersistent(final Activity activity, final boolean isPemanentMemory) {
//		if (activity == null) {
//			return;
//		}
//		// 为兼容4.0以上采用反射机制
//		try {
//			Method setPersistent = activity.getClass().getMethod("setPersistent",
//					boolean.class);
//			setPersistent.invoke(activity, isPemanentMemory);
//		} catch (Throwable e) {
//			Log.e("DiyScheduler", "setPersistent" + e.getMessage());
//		}
//	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean handleMessage(Object who, int msgId, final int param, Object ...obj) {
		boolean ret = false;
		switch (msgId) {
			case IFrameworkMsgId.SHOW_FRAME : {
				if (param == IDiyFrameIds.SCREEN || param == IDiyFrameIds.DOCK
						|| param == IDiyFrameIds.APP_DRAWER) {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, IFrameworkMsgId.SHOW_FRAME,
							param, obj[0], obj[1]);
					ret = true;
				}

				if (param == IDiyFrameIds.APP_DRAWER_SEARCH
						|| param == IDiyFrameIds.MEDIA_MANAGEMENT_FRAME) {
					if (obj[0] instanceof Boolean && (Boolean) obj[0] == true) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
								IFrameworkMsgId.SHOW_FRAME, param, obj[0], obj[1]);
						ret = (Boolean) obj[0];
					}
				}

				if (!ret) {
					ret = mFrameControl.showFrame(param);
					if (param == IDiyFrameIds.MEDIA_MANAGEMENT_FRAME) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
								IMediaManagementMsgId.MEDIA_MANAGEMENT_3D_DRAWER_BG, param, obj[0],
								obj[1]);
					}
				}
			}
				break;

			case IFrameworkMsgId.REMOVE_FRAME : {
				if (param == IDiyFrameIds.SCREEN || param == IDiyFrameIds.DOCK
						|| param == IDiyFrameIds.APP_DRAWER) {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
							IFrameworkMsgId.REMOVE_FRAME, param, obj[0], obj[1]);
					ret = true;
				}
				if (param == IDiyFrameIds.APP_DRAWER_SEARCH) {
					if (obj[0] instanceof Object[]) {
						Object[] objs = (Object[]) obj[0];
						if ((Boolean) objs[0] == true) {
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
									IFrameworkMsgId.REMOVE_FRAME, param, obj[0], obj[1]);
							ret = (Boolean) objs[0];
						}
					}
				}

				if (!ret) {
					mHandler.sendMessage(mHandler.obtainMessage(msgId, param, -1));
				}
			}
				break;

			case IFrameworkMsgId.HIDE_FRAME : {
				if (param == IDiyFrameIds.MEDIA_MANAGEMENT_FRAME) {
					if (obj[0] instanceof Boolean) {
						if ((Boolean) obj[0]) {
							MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
									IFrameworkMsgId.HIDE_FRAME, param, obj[0], obj[1]);
							ret = true;
						}
					}
				}

				if (!ret) {
					mFrameControl.hide(param);
					ret = true;
				}
			}
				break;

			case ICommonMsgId.SHOW_APP_DRAWER : {
					// 进入功能表
					//					mFrameControl.showFrame(IDiyFrameIds.APPFUNC_FRAME);
					handleMessage(this, IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.APP_DRAWER,
							null, null);
				ret = true;
			}
				break;

			case IFrameworkMsgId.SYSTEM_HOME_CLICK : {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.IMAGE_BROWSER_FRAME,
						IFrameworkMsgId.SYSTEM_HOME_CLICK, 0, null, null);
			}
				break;
			case ICommonMsgId.SHOW_HIDE_STATUSBAR : {
//				if (ShellPluginFactory.isUseShellPlugin(mActivity)) {
//					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
//							IDiyMsgIds.SHOW_HIDE_STATUSBAR, param, object, null);
//				} else {
					if (param == -1) {
						setFullScreen(!mStatusBarHandler.isFullScreen(GoLauncherActivityProxy.getActivity()));
					} else if (param == -2) {
						if (obj[0] != null) {
							boolean fullScreen = (Boolean) obj[0];
							setFullScreen(fullScreen, false);
						}
					}
					// else if(param == -3){ //与-1区别：不保存数据库
					// setFullScreen(!mStatusBarHandler.isFullScreen(), false);
					// }
				}
				ret = true;
//			}
				break;

			case ICommonMsgId.SHOW_SEARCH_DIALOG : {
				if (obj[0] != null && obj[0] instanceof Bundle) {
					final Bundle bundle = (Bundle) obj[0];
					final String initialQuery = bundle.getString(Search.FIELD_INITIAL_QUERY);
					final boolean selectInitialQuery = bundle
							.getBoolean(Search.FIELD_SELECT_INITIAL_QUERY);
					final Bundle searchData = bundle.getBundle(Search.FIELD_SEARCH_DATA);
					final boolean globalSearch = bundle.getBoolean(Search.FIELD_GLOBAL_SEARCH);
					showSearchDialog(initialQuery, selectInitialQuery, searchData, globalSearch);
					ret = true;
				}
			}
				break;

			case ICommonMsgId.START_ACTIVITY : {
				if (obj[0] != null && obj[0] instanceof Intent) {
					synchronized (this) {
						Rect rect = null;
						boolean[] result = null;
						if (obj[1] != null && ((List) obj[1]).size() > 0) {
							rect = (Rect) ((List) obj[1]).get(0);
							if (((List) obj[1]).size() > 1) {
								result = (boolean[]) ((List) obj[1]).get(1);
							}
						}
						startActivity((Intent) obj[0], rect, result, param);
					}
				}
			}
				break;

			case ICommonMsgId.START_ACTIVITY_FOR_RESULT : {
				if (obj[0] != null && obj[0] instanceof Intent) {
					ret = mAppInvoker.invokeAppForResult(param, (Intent) obj[0]);
				}
			}
				break;

			case IDockMsgId.DOCK_ENTER_SHORTCUT_SELECT : {
				mShortcutHolerId = IShortcutHolderIds.SHORTCUT_DOCK_CLICK;
				mShortcutSource = DOCK_ACTION;
				pickShortcut(IRequestCodeIds.REQUEST_PICK_SHORTCUT, R.string.select_app_icon, param);
			}
				break;

			case IDockMsgId.DOCK_ENTER_SHORTCUT_SELECT_FOR_GESTURE : {
				mShortcutHolerId = IShortcutHolderIds.SHORTCUT_DOCK_GESTURE;
				mShortcutSource = DOCK_GESTURE;
				pickShortcut(IRequestCodeIds.REQUEST_DOCK_PICK_SHORTCUT_FOR_GESTURE,
						R.string.select_app_icon, param);
			}
				break;

			case ICommonMsgId.SEND_WALLPAPER_COMMAND : {
				mWallpaperControler.sendWallpaperCommand(mFrameControl.getRootView(),
						(Bundle) obj[0]);
				ret = true;
			}
				break;

			case ICommonMsgId.UPDATE_WALLPAPER_OFFSET : // 壁纸背景偏移
			{
				mWallpaperControler.updateWallpaperOffset(mFrameControl.getRootView(),
						(Bundle) obj[0]);
				ret = true;
				break;
			}

			case IScreenFrameMsgId.SCREEN_FINISH_LOADING : {
				
				setOrientation(); // 初始化成之后设置为正确的横竖屏模式
				
				if (null != AppDataEngine.getInstance(ApplicationProxy.getContext())) {
					AppDataEngine.getInstance(ApplicationProxy.getContext()).startLoadCompletedData();
				}
				// add by zhoujun
				// 当桌面数据加载完毕时，在application中初始化其他一些操作，比如绑定付费的管理的service
				DataChangeListenerProxy.getInstance().dataLoadFinish();
				// add by zhoujun 2012-09-10 end
				PreferencesManager sharedPreferences = new PreferencesManager(mActivity,
						IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE
								| Context.MODE_MULTI_PROCESS);
				if (sharedPreferences.getBoolean(
						IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_CHECKDB, false)) {
					mActivity.sendBroadcast(new Intent(ICustomAction.ACTION_CHECK_NEW_DB));
					sharedPreferences.putBoolean(
							IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_CHECKDB, false);
					sharedPreferences.commit();
				}
				getSidemenuAdData();
			}
				break;

			case IAppCoreMsgId.EVENT_THEME_CHANGED : {
				if (obj[0] != null && obj[0] instanceof String) {
					String curPackageName = (String) obj[0];
					ThemeInfoBean infoBean = ThemeManager.getInstance(mActivity)
							.getCurThemeInfoBean();
					if (infoBean != null) {
						initLauncherProxy(infoBean);
						if (infoBean.isMaskView()) {
							handleMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
									ICoverFrameMsgId.COVER_FRAME_ADD_VIEW, CoverFrame.COVER_VIEW_THEME,
									curPackageName, null);
						} else {
							handleMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
									ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW,
									CoverFrame.COVER_VIEW_THEME, null, null);
							handleMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
									ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW,
									CoverFrame.COVER_VIEW_HOLIDAY, false, null);
						}
					}

				}
				break;
			}
			case ICommonMsgId.EVENT_INIT_THEME_LAUNCHER_PROXY :
				ThemeInfoBean infoBean = ThemeManager.getInstance(mActivity).getCurThemeInfoBean();
				if (infoBean != null) {
					initLauncherProxy(infoBean);
				}
				break;
			case ICommonMsgId.RESTART_GOLAUNCHER : {
				exit(true);
				break;
			}

			case IAppCoreMsgId.EVENT_INSTALL_APP :
			case IAppCoreMsgId.EVENT_INSTALL_PACKAGE : {
				if (null != obj[0] && obj[0] instanceof String) {
					String objectString = (String) obj[0];
					if (XmlRecommendedApp.getRecommededAppInfoByPackage(objectString) != null) {
						replaceRecommandIcon(obj[0]);
					}
					if (PackageName.DESKMIGRATE_PACKAGE_NAME.equals(objectString)) {
						showMigrateTip();
					} else if (PackageName.LOCKER_PACKAGE.equals(objectString)
							|| PackageName.LOCKER_PRO_PACKAGE.equals(objectString)) {
						replaceRecommandIcon(obj[0]);
					} else if (PackageName.NOTIFICATION_PACKAGE_NAME.equals(objectString)) {
						startNotificationService();
					} else if (PackageName.RECOMMEND_NETQIN_PACKAGE.equals(objectString)
							|| PackageName.RECOMMEND_EVERNOTE_PACKAGE.equals(objectString)
							|| PackageName.RECOMMEND_NEXTLAUNCHER_PACKAGE
									.equals(objectString)
							|| PackageName.MEDIA_PLUGIN.equals(objectString)
							|| PackageName.RECOMMEND_BAIDUBROWSER_PACKAGE
									.equals(objectString)
							|| PackageName.RECOMMEND_BAIDUBATTERSAVER_PACKAGE
									.equals(objectString)) {
						replaceRecommandIcon(obj[0]);
					} else {
						ValueReturned value = new ValueReturned();
						MsgMgrProxy
								.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
										IScreenAdvertMsgId.CHECK_IS_IN_ADVERT_LIST, -1, value,
										objectString);
						if (value.mConmused) {
							replaceRecommandIcon(obj[0]);
						} else {
							// 推荐图标处理
							handleRecommandIcon(objectString);
						}
					}
					
					//-----for slide menu clean master and du speed booster promotion-----
					PrivatePreference pref = PrivatePreference.getPreference(ApplicationProxy.getContext());
				    long cmTime = pref.getLong(PrefConst.KEY_SLIDEMENU_SHOW_CLEAN_MASTER_AD_TIMESTAMP, 0);
				    long duTime = pref.getLong(PrefConst.KEY_SLIDEMENU_SHOW_DU_SPEED_TIMESTAMP, 0);
					if (objectString.equals(PackageName.CLEAN_MASTER_PACKAGE)
							&& (System.currentTimeMillis() - cmTime <= TIME_INTERVAL)) {
						GuiThemeStatistics.guiStaticData("20", 40, "2463865", "b000", 1, "", "", "", "", "601");
					} else if (objectString.equals(PackageName.DU_SPEED_BOOSTER)
							&& (System.currentTimeMillis() - duTime <= TIME_INTERVAL)) {
						GuiThemeStatistics.guiStaticData("20", 40, "5377557", "b000", 1, "", "", "", "", "602");
					} 
					
					
					if (objectString.equals(PackageName.CLEAN_MASTER_PACKAGE)
							&& (System.currentTimeMillis() - AdvertDialogCenter.sDownloadCleanMasterMark <= TIME_INTERVAL)) {
						GuiThemeStatistics.guiStaticData(40, "2463865", "b000", 1, "-1", "-1",
								"-1", "-1");
					} else if (objectString.equals(PackageName.NEXT_BROWSER_PACKAGE_NAME)
							&& (System.currentTimeMillis() - AdvertDialogCenter.sDownloadNextBrowserMark <= TIME_INTERVAL)) {
						GuiThemeStatistics.guiStaticData(51,
								PackageName.NEXT_BROWSER_PACKAGE_NAME, "b000", -1, "-1",
								"-1", "-1", "-1");
					} else if (objectString.equals(PackageName.CLEAN_MASTER_PACKAGE)
							&& (System.currentTimeMillis() - AdvertDialogCenter.sDownloadCleanMasterInAppManager <= TIME_INTERVAL)) {
						GuiThemeStatistics.appManagerAdStaticData("2463865", "b000", 1, "-1");
					} else if (objectString.equals(PackageName.MAXTHON_PACKAGE)
                            && (System.currentTimeMillis() - AdvertDialogCenter.sDownloadMathonBrowserMark <= TIME_INTERVAL)) {
					    //统计安装了遨游浏览器
					    GuiThemeStatistics.guiStaticDataFor360Mathon(40, "5380697", "b000",
		                        1, "-1", "-1", "-1", "-1", "571");
                    } else if (objectString.equals(PackageName.SECURITY_GUARDS_PACKAGE)
                            && (System.currentTimeMillis() - AdvertDialogCenter.sDownloadScurityMark <= TIME_INTERVAL)) {
                        //统计安装了360
                        GuiThemeStatistics.guiStaticDataFor360Mathon(40, "5371909", "b000", 1,
                                "-1", "-1", "-1", "-1", "570");
                    } else if (objectString.equals(PackageName.DU_SPEED_BOOSTER)
                            && (System.currentTimeMillis() - AdvertDialogCenter.sDownloadDuSpeedBoosterMark <= TIME_INTERVAL)) {
                    	//统计安装了安卓优化大师
                    	String aId = GuiThemeStatistics.getAdvertId(mActivity, "5377557");
                        GuiThemeStatistics.guiStaticDataWithAId(40, "5377557", "b000", 1,
                                "-1", "-1", "-1", "-1", aId);
                    }

					//判断是否是go动态壁纸推荐的应用,如果是把包名保存下来,并做统计
					int state = mPushController.saveInstalledPackage(objectString);
					if (state == ScreenEditPushConstants.REQUEST_ID_FOR_WALLPAPER) {
						if (ScreenEditStatistics.sStatisticDebug) {
							StatisticsManager.getInstance(ApplicationProxy.getContext()).setDebugMode();
						}
						StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadBasicOptionStaticData(
								ScreenEditStatistics.STATISTICS_FUN_ID, objectString, 
								ScreenEditStatistics.STATISTICS_OPERATE_APP_INSTALLED, 
								ScreenEditStatistics.STATISTICS_OPERATE_SUCCESS, 
								Statistics.getUid(ApplicationProxy.getContext()), 
								ScreenEditStatistics.STATISTICS_ENTER, 
								ScreenEditStatistics.STATISTICS_TYPE_DYNAMICA_WALLPAPER, -1);	
					}
				}
			}
				break;
			case IAppCoreMsgId.EVENT_UNINSTALL_APP :
			case IAppCoreMsgId.EVENT_UNINSTALL_PACKAGE : {
				if (null != obj[0] && obj[0] instanceof String) {
					String objectString = (String) obj[0];
					if (PackageName.LOCKER_PACKAGE.equals(objectString)) {
					} else if (objectString.contains(PackageName.LANGUAGE_PACKAGE)) {
						String language = mActivity.getResources()
								.getConfiguration().locale.getLanguage();
						language = PackageName.LANGUAGE_PACKAGE + "." + language;
						if (obj[0].equals(language)) {
							exit(true);
						}
					} else if (ICustomAction.NOTIFICATION_PACKAGE
							.equals(objectString)) {
						// stopNotificationService();
					} else if (objectString
							.equals(LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
						FunctionPurchaseManager.getInstance(mActivity)
								.sendTrialExpiredBroadCast(0);
					}

					PreferencesManager pre = new PreferencesManager(mActivity);
					PreferencesManager pm = new PreferencesManager(mActivity,
							IPreferencesIds.PREFERENCE_APPFUNC_CLEANMASTER_NEW,
							Context.MODE_PRIVATE);
					boolean isRecommendDuSpeedBooster = pre.getBoolean(
							IPreferencesIds.PREFERENCE_IS_RECOMMEND_DUSPEEDBOOSTER,
							false);
					boolean hasShow = false;
						// 如果在弹框前卸载了clean master，则不再弹框
					hasShow = pm.getBoolean(IPreferencesIds.PREFERENCE_HAS_SHOW_CLEAN_MASTER_DIALOG,
									false);
					if (((PackageName.CLEAN_MASTER_PACKAGE.equals(objectString) && !isRecommendDuSpeedBooster) || (PackageName.DU_SPEED_BOOSTER
							.equals(objectString) && isRecommendDuSpeedBooster)) && !hasShow) {
						pm.putBoolean(IPreferencesIds.PREFERENCE_HAS_SHOW_CLEAN_MASTER_DIALOG, true);
						pm.commit();
						hasShow = true;
					}
					// 卸载3次后，弹出推荐clean master的对话框，只弹一次 add by Ryan 2013.07.08
					boolean isRecommend = true;
					if (GoAppUtils.isMarketNotExitAnd200Channel(mActivity)) {
						isRecommend = false;
					}

					// V4.12 原来：卸载 3 个应用,弹出对话框推荐 CM，已安装 CM 时不推荐 ； 现在：
					// 保留现有规则的前提下,若用户未安装电子市场,针对200渠道包,则不推荐 2014.01.10
					if (isPrimeAd() && !hasShow && isRecommend) {
						int count = pm.getInt(
								IPreferencesIds.PREFERENCE_UNINSTALL_COUNT_FOR_CM,
								1);
						if (count >= 3) {
							if (isRecommendDuSpeedBooster) {
								if (!AppUtils.isAppExist(mActivity,
										PackageName.DU_SPEED_BOOSTER)) {
									// 如果已经安装了CM，则推荐下载安卓优化大师
									String mapId = "5377557";
									String aId = "592";
									GuiThemeStatistics.guiStaticData(40, mapId,
											"f000", 1, "-1", "-1", "-1", "-1");
									AdvertDialogCenter.showDuSpeedBoosterDialog(
											mActivity, mapId, aId,
											LauncherEnv.Url.DU_SPEED_BOOSTER_URL);

								}
							} else {
								if (!AppUtils.isAppExist(mActivity,
										PackageName.CLEAN_MASTER_PACKAGE)) {
									GuiThemeStatistics.guiStaticData(40, "2463865",
											"f000", 1, "-1", "-1", "-1", "-1");
									AdvertDialogCenter.showCleanMasterDialog(mActivity);
								}
							}
							pm.putBoolean(
									IPreferencesIds.PREFERENCE_HAS_SHOW_CLEAN_MASTER_DIALOG,
									true);
							pm.commit();
						}
					}
					// end add
				}
			}
				break;

			case ICommonMsgId.RESET_ORIENTATION : {
				setOrientation();
			}
				break;

			case ICommonMsgId.SET_ORIENTATION : {
				OrientationControl.setOrientation(mActivity, param);
			}
				break;

			case ICommonMsgId.PREFERENCES : {
				startActivity(new Intent(mActivity, DeskSettingMainActivity.class), null);
			}
				break;

			case IWidgetMsgId.GOWIDGET_UNINSTALL_GOWIDGET_SWITCH : {
				// 卸载开关gowidget
				Uri packageURI = Uri.parse("package:" + ICustomAction.PKG_GOWIDGET_SWITCH);
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				mActivity.startActivityForResult(uninstallIntent,
						IRequestCodeIds.REQUEST_GOWIDGET_UNINSTALL_SWITCH);
			}
				break;

			case ICommonMsgId.ENABLE_KEYGUARD : {
				Intent loackIntent = new Intent(ICustomAction.ACTION_ENABLE_SCREEN_GUARD);
				startActivity(loackIntent, null);
			}
				break;

			case IWidgetMsgId.SHOW_DOWNLOAD_GOSWITCHWIDGET_DIALOG : {
				int resId = R.string.no_install_goswitchwidget_tips;
				if (param == 1) {
					resId = R.string.update_goswitchwidget_tips;
				}
				// showInstallSwitchWidgetDialog(resId);
				// add by Ryan 2012.7.11
				String title = mActivity.getResources().getString(R.string.onoff);
				String content = mActivity.getResources().getString(resId);
				String[] linkArray = new String[] { ICustomAction.PKG_GOWIDGET_SWITCH,
						LauncherEnv.Url.GOSWITCHWIDGET_FTP_URL };
				CheckApplication.downloadAppFromMarketFTPGostore(mActivity, content, linkArray,
						LauncherEnv.GOLAUNCHER_GOOGLE_REFERRAL_LINK, title,
						System.currentTimeMillis(), GoAppUtils.isCnUser(mActivity),
						CheckApplication.FROM_SCREENEDIT, 0, null);
			}
				break;

			case IScreenEditMsgId.SCREEN_EDIT_ADD_SYSTEM_SHORTCUT : {
//				mAddingComponent = true;
//				mShortcutSource = SCREEN_ACTION;
				// 添加应用程序图标
//				pickShortcut(IRequestCodeIds.REQUEST_PICK_SHORTCUT, R.string.select_app_icon, param);
				
				//让屏幕可以滚动
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.ALLOW_SCREEN_TO_SCROLL, 0, null, null);
				
				mShortcutHolerId = IShortcutHolderIds.SHORTCUT_SCREEN;
				
				ResolveInfo info = (ResolveInfo) obj[0];
		        Intent intent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		        String pkg = info.activityInfo.packageName;
		        String cls = info.activityInfo.name;
		        ComponentName componentName = new ComponentName(pkg, cls);
		        intent.setComponent(componentName);
				processShortcut(intent, IRequestCodeIds.REQUEST_CREATE_SHORTCUT);
			}
				break;
				
			case IAppCoreMsgId.ADD_SYSTEM_ITEM_TO_SCREEN : {
				
				//让屏幕可以滚动
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.ALLOW_SCREEN_TO_SCROLL, 0, null, null);
				
				SysSubWidgetInfo info = null;
				if (obj[0] != null && obj[0] instanceof SysSubWidgetInfo) {
					info = (SysSubWidgetInfo) obj[0];
				}
				
				if (info != null) {
					int appWidgetId = mWidgetHost.allocateAppWidgetId();
					
					if (info.getCustomerInfo() != null 
							&& info.getCustomerInfo().equals(ScreenEditConstants.SEARCH_WIDGET_TAG)) {
						Intent intent = new Intent();
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
						intent.putExtra(DiyScheduler.EXTRA_CUSTOM_WIDGET, DiyScheduler.SEARCH_WIDGET);

						processPickAppwidget(Activity.RESULT_OK, intent);
						return true;
					}
					
					boolean allow = false;
					try {
						Method method = AppWidgetManager.class.getMethod("bindAppWidgetIdIfAllowed", int.class, ComponentName.class);
						allow = (Boolean) method.invoke(AppWidgetManager.getInstance(mActivity), 
								appWidgetId, info.getProviderInfo().provider);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} 
					
					if (allow) {
						
						Intent intent = new Intent();
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
						
						if (info.getCustomerInfo() != null && 
								info.getCustomerInfo().equals(ScreenEditConstants.SEARCH_WIDGET_TAG)) {
							intent.putExtra(DiyScheduler.EXTRA_CUSTOM_WIDGET, DiyScheduler.SEARCH_WIDGET);
						}
						
						processPickAppwidget(Activity.RESULT_OK, intent);
					} else {
						
						mPaddingAddWidgetId = appWidgetId;
						Intent intent = new Intent("android.appwidget.action.APPWIDGET_BIND");
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
						intent.putExtra("appWidgetProvider", info.getProviderInfo().provider);
						startActivityForResult(intent, IRequestCodeIds.BIND_SYSTEM_WIDGET);
					}
				}
			}
			 	break;
			 	
			case IWidgetMsgId.PICK_WIDGET : {
				mAddingComponent = true;
				pickWidget();
			}
				break;
				
			case IAppCoreMsgId.BIND_SYSTEM_WIDGET: {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.ALLOW_SCREEN_TO_SCROLL, 0, null, null);
				
				mPaddingAddWidgetId = param;
				SysSubWidgetInfo info = (SysSubWidgetInfo) obj[0];
				Intent intent = new Intent("android.appwidget.action.APPWIDGET_BIND");
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, param);
				intent.putExtra("appWidgetProvider", info.getProviderInfo().provider);
				startActivityForResult(intent, IRequestCodeIds.BIND_SYSTEM_WIDGET);
			}
				break;
				
			case IScreenFrameMsgId.CUSTOMER_PICK_WIDGET : {
				//让屏幕可以滚动
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.ALLOW_SCREEN_TO_SCROLL, 0, null, null);
				
				Intent data = (Intent) obj[0];
				processPickAppwidget(param, data);
			}
				break;
				
			case ICommonMsgId.OPEN_NOTIFICATION_SETTING : {
				AppCore.getInstance().getNotificationControler().openNotification();
			}
				break;
			case ICommonMsgId.IMPORT_OTHER_DB : {
				if (obj[0] != null) {
					importDBFromOtherLauncher((String) obj[0]);
				}
			}
				break;

			case ICoverFrameMsgId.COVER_FRAME_ADD_VIEW :
			if (mActivity instanceof GoLauncher) {
				if (mCoverFrame == null) {
					mCoverFrame = new CoverFrame(mActivity);
					mCoverFrame.registerCoverCallback(this);
					mActivity.addContentView(mCoverFrame, new FrameLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				}
			}
				// 不需要break;
			case ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW :
			case ICoverFrameMsgId.COVER_FRAME_HIDE_ALL :
			case ICoverFrameMsgId.COVER_FRAME_SHOW_ALL :
			case ICoverFrameMsgId.COVER_FRAME_REMOVE_ALL : {
				coverFrameHandle(msgId, param, obj[0]);
			}
				break;
				
			case IAppCoreMsgId.REQUEST_SCREEN_EDIT_PUSH_LIST : {
				if (mPushController != null) {
					String saveTimeKey = (String) obj[0];
					mPushController.requestPushData(param, saveTimeKey);
				}
			}
				break;
				
			case ICommonMsgId.UPDATE_WALLPAPER : {
				MsgMgrProxy.postUiRunnable(this, new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mWallpaperControler.updateWallpaper(true);
						
					}
				}, false);
			}
				break;
			case IScreenFrameMsgId.SCREEN_ADD_SHORTCUT_COMPLETE:
				switch (mShortcutHolerId) {
					case IShortcutHolderIds.SHORTCUT_SCREEN :
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
								IScreenFrameMsgId.SCREEN_ADD_SHORTCUT_COMPLETE, -1, obj[0], null);
						break;
					default :
						ShortCutInfo cutInfo = (ShortCutInfo) obj[0];
						notifyShortCutHolder(cutInfo);
						break;
				}
				mShortcutHolerId = IShortcutHolderIds.SHORTCUT_SCREEN;
				break;
			case ICommonMsgId.ACTIVITY_NEED_RESTART: {
				mNeedRestart = param == 1;
			}
				break;
			case ICommonMsgId.CHECK_BILLING_SUPPORT: {
				checkBillingSupported();
			}
				break;
			case ICommonMsgId.RETRIEVE_SEARCH_WIDGET_VIEW: {
				LayoutInflater inflater = LayoutInflater.from(ApplicationProxy.getContext());
				Search search = (Search) inflater.inflate(R.layout.widget_search,
						null);
				ValueReturned vr = (ValueReturned) obj[0];
				vr.mValue = search;
			}
				break;
			default :
				ret = false;
		}

		return ret;
	} // end handleMessage
	
	/**
	 * 替换桌面和桌面文件夹中的推荐图标为真图标
	 */
	private void replaceRecommandIcon(Object object) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.REPLACE_RECOMMAND_ICON_IN_FOLDER, -1, object, null);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_REPLACE_RECOMMEND_ICON, -1, object, null);
	}
	
	/**
	 * 设置全屏，但不更改数据库的设置信息
	 * 
	 * @param isFullScreen
	 * @param updateData
	 */
	private void setFullScreen(final boolean isFullScreen, boolean updateData) {
		mStatusBarHandler.setFullScreen(isFullScreen, updateData);
	}

	private void setFullScreen(final boolean isFullScreen) {
		setFullScreen(isFullScreen, true);
	}

	/**
	 * 进入快捷方式选择界面
	 * 
	 * @param requestCode
	 *            请求码
	 * @param title
	 * @param source
	 *            发起者
	 */
	private void pickShortcut(int requestCode, int title, int type) {
		Bundle bundle = null;
		if (DOCK_GESTURE != mShortcutSource) {
			bundle = new Bundle();
			if (mShortcutSource == DOCK_ACTION) {
				//判断是否DOCK自适应模式。，自适应模式没有GO快捷方式
				if (type != 1) {
					ArrayList<String> shortcutNames = new ArrayList<String>();
					shortcutNames.add(mActivity.getString(R.string.launcher_action));
					bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
					ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
					shortcutIcons.add(ShortcutIconResource.fromContext(mActivity,
							R.drawable.screen_edit_go_shortcut));
					bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
							shortcutIcons);
				}

			} else {
				if (type != -10) {
					//3D模式下不支持系统文件夹
					ArrayList<String> shortcutNames = new ArrayList<String>();
					shortcutNames.add(mActivity.getString(R.string.dialog_name_system_folder));
					bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
					
					ArrayList<ShortcutIconResource> shortcutIcons = new ArrayList<ShortcutIconResource>();
					shortcutIcons.add(ShortcutIconResource.fromContext(mActivity,
							R.drawable.ic_launcher_add_folder));
					bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);
				}
			}
		}

		Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
		pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
		pickIntent.putExtra(Intent.EXTRA_TITLE, mActivity.getText(title));
		if (null != bundle) {
			pickIntent.putExtras(bundle);
		}

		startActivityForResult(pickIntent, requestCode);
	}

	private void processShortcut(Intent intent, int requestCodeShortcut) {
//		String applicationName = mActivity.getResources().getString(R.string.launcher_action);
//		String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
//		String systemFolderName = mActivity.getResources().getString(
//				R.string.dialog_name_system_folder);
//
//		if (applicationName != null && applicationName.equals(shortcutName)) {
//			if (mShortcutSource == SCREEN_ACTION) {
//				Intent laIntent = new Intent(mActivity, LauncherActionList.class);
//				startActivityForResult(laIntent,
//						IRequestCodeIds.REQUEST_SCREEN_ADD_GOLAUNCHER_SHORTCUT);
//			} else if (mShortcutSource == DOCK_ACTION) {
//				DockAddLauncherActionDialog aDialog = DockAddLauncherActionDialog
//						.getDialog(mActivity);
//				aDialog.show();
//			}
//		} else if (systemFolderName != null && systemFolderName.equals(shortcutName)) {
//			pickFolder();
//		} else {
			startActivityForResult(intent, requestCodeShortcut);
//		}
	}

	// 通过GO快捷方式启动的操作（可能是程序或者GGMenu等，也要返回一个开启成功的标识）
	private boolean startActivity(Intent intent, Rect rect, boolean[] result, int from) {
		if (intent != null) {
			// Home键跳转修改
			return mAppInvoker.invokeApp(intent, rect, result, from);
		}
		return false;
	}

	private boolean startActivity(Intent intent, Rect rect) {
		if (intent != null) {
			mIsOut = mAppInvoker.invokeApp(intent, rect, null);
			return mIsOut;
		}
		return false;
	}

	private void startActivityForResult(Intent intent, int requestCode) {
		if (intent == null) {
			return;
		}
		try {
			mActivity.startActivityForResult(intent, requestCode);
			mIsOut = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (!mFrameControl.onKeyLongPress(keyCode, event)) {
			// 如果所有frame不处理该key事件，则过滤之
			return false;
		}
		return true;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		if (!mFrameControl.onKeyMultiple(keyCode, repeatCount, event)) {
			// 如果所有frame不处理该key事件，则过滤之
			return false;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Log.i(LogConstants.HEART_TAG, "onKeyDown keyCode = " + keyCode);
		boolean handled = mFrameControl.onKeyDown(keyCode, event);
		if (!handled) {
			boolean searchFilter = false;

			if (!searchFilter) {
				// 如果所有frame不处理该key事件，则过滤之
				if (keyCode == KeyEvent.KEYCODE_MENU) {
					/**
					 * NOTICE: 对KEYCODE_MENU事件处理不能返回true， 否则系统认为已经被处理就不会弹出软键盘
					 */
					if (event.isLongPress()) {
						FunctionalStatistic st = AppCore.getInstance().getFunctionalStatistic();
						if (null != st) {
							st.statistic(FunctionalStatistic.Rule.RULE_ADDVALUE, "long_menu_key", 1);
						}
					}
				} else if (keyCode == KeyEvent.KEYCODE_BACK) {
					// 屏蔽返回键
					handled = true;
				}
			}
		}
		mIsKeyDown = true;
		return handled;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// Log.i(LogConstants.HEART_TAG, "onKeyUp keyCode = " + keyCode);
		mIsKeyDown = false;
		// TODO 后面考虑做成独立一个按键过滤模块
		if (!mFrameControl.onKeyUp(keyCode, event)) {
			// if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
			// || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			// {
			// if (ENABLE_SCREEN_CAPTURE)
			// {
			// captureScreen();
			// return true;
			// }
			// }

			// 如果所有frame不处理该key事件，则过滤之
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				closeSystemDialogs();
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
				// 统计
				FunctionalStatistic st = AppCore.getInstance().getFunctionalStatistic();
				if (null != st) {
					st.statistic(FunctionalStatistic.Rule.RULE_ADDVALUE, "search_key", 1);
				}
			}

			return false;
		}
		return true;
	}

	@Override
	public int getMsgHandlerId() {
		return IDiyFrameIds.SCHEDULE_FRAME;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (!mFrameControl.dispatchKeyEvent(event)) {
			// 过滤处理
		}
		return false;
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		if (!mFrameControl.dispatchTrackballEvent(event)) {
			// 过滤处理
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (mCoverFrame != null && mCoverFrame.dispatchTouchEvent(event)) {
			return true;
		} else if (!mFrameControl.dispatchTouchEvent(event)) {
			// 过滤处理
		}
		return false;
	}

	/**
	 * 处理Activity的返回事件
	 * 
	 * @param requestCode
	 *            返回码
	 * @param resultCode
	 *            结果码
	 * @param data
	 *            数据
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mAddingComponent = false;
		switch (requestCode) {
			case IRequestCodeIds.REQUEST_THEME_SCAN : {
				// 主题管理来的
				handleThemeScanResult(resultCode, data);
			}
				break;

			case IRequestCodeIds.REQUEST_PICK_APPWIDGET : {
				processPickAppwidget(resultCode, data);
			}
				break;

			case IRequestCodeIds.REQUEST_CREATE_APPWIDGET : {
				completeAddAppWidget(data, resultCode);
			}
				break;

			case IRequestCodeIds.REQUEST_PICK_SHORTCUT : {
				if (resultCode == Activity.RESULT_OK) {
					processShortcut(data,
					/* IRequestCodeIds.REQUEST_PICK_APPLICATION, */
					IRequestCodeIds.REQUEST_CREATE_SHORTCUT);
				}
			}
				break;

			// case IRequestCodeIds.REQUEST_PICK_APPLICATION:
			// {
			// if (resultCode == Activity.RESULT_OK)
			// {
			// completeAddApplication(data);
			// }
			// }
			// break;

			case IRequestCodeIds.REQUEST_CREATE_SHORTCUT : {
				completeAddShortcut(data);
			}
				break;

//			case IRequestCodeIds.REQUEST_PICK_LIVE_FOLDER : {
//				if (resultCode == Activity.RESULT_OK) {
//					addLiveFolder(data);
//				}
//			}
//				break;

//			case IRequestCodeIds.REQUEST_CREATE_LIVE_FOLDER : {
//				if (resultCode == Activity.RESULT_OK) {
//					completeAddLiveFolder(data);
//				}
//			}
//				break;

//			case IRequestCodeIds.REQUEST_CREATE_APPDRAWER_FOLDER : {
//				if (resultCode == Activity.RESULT_OK) {
//					completeAddAppDrawerFolder(data);
//				}
//			}
//				break;

			case IRequestCodeIds.REQUEST_DOCK_PICK_SHORTCUT_FOR_GESTURE : {
				if (resultCode == Activity.RESULT_OK) {
					processShortcut(data,
					/* IRequestCodeIds.REQUEST_PICK_APPLICATION_IN_DOCK_GESTURE, */
					IRequestCodeIds.REQUEST_CREAT_SHORTCUT_IN_DOCK_GESTURE);
				}
			}
				break;

			case IRequestCodeIds.REQUEST_PICK_APPLICATION_IN_DOCK_GESTURE : {
				if (resultCode == Activity.RESULT_OK) {
					Bundle bundle = data.getExtras();
					Intent intent = bundle.getParcelable(ScreenModifyFolderActivity.INTENT_STRING);
					// mApplicationHolerId =
					// IShortcutHolderIds.SHORTCUT_DOCK_GESTURE;
					completeAddApplication(intent);
				}
			}
				break;

			case IRequestCodeIds.REQUEST_CREAT_SHORTCUT_IN_DOCK_GESTURE : {
				if (resultCode == Activity.RESULT_OK) {
					completeAddShortcut(data);
				}
			}
				break;

			// case IRequestCodeIds.REQUEST_DOCK_CHANGE_APP_STYLE_ICON:
			// {
			// dockChangeIcon(requestCode, resultCode, data);
			// }
			// break;

			case IRequestCodeIds.REQUEST_PREVIEWIMAGE_FORICON : {
				screenChangeIcon(requestCode, resultCode, data);
			}
				break;

			case IRequestCodeIds.REQUEST_THEME_FORICON : {
				if (resultCode == Activity.RESULT_OK) {
					int frameid = ChangeIconPreviewActivity.sFromWhatRequester;
					if (frameid == ChangeIconPreviewActivity.DOCK_STYLE_FROM_EDIT
							|| frameid == ChangeIconPreviewActivity.DOCK_FOLDER_STYLE) {
						dockChangeIcon(requestCode, resultCode, data);
					} else if (frameid == ChangeIconPreviewActivity.SCREEN_STYLE
							|| frameid == ChangeIconPreviewActivity.USER_FOLDER_STYLE) {
						screenChangeIcon(requestCode, resultCode, data);
					} else if (frameid == ChangeIconPreviewActivity.SCREEN_FOLDER_ITEM_STYLE
							|| frameid == ChangeIconPreviewActivity.DOCK_FOLDER_ITEM_STYLE) {
						Bundle bundle = data.getExtras();
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_FOLDER,
								ICommonMsgId.CHANGE_ICON_STYLE, -1, bundle, null);
					}
				}
			}
				break;

			case IRequestCodeIds.REQUEST_DOCK_CHANGE_APP_ICON : {
				if (resultCode == Activity.RESULT_OK) {
					Bundle bundle = data.getExtras();
					Intent intent = bundle.getParcelable(AppList.INTENT_STRING);

					if (intent == null) {
						return;
					}

					// 获取出intent中包含的应用
					final ShortCutInfo info = infoFromApplicationIntent2(mActivity, intent);
					if (info == null) {
						// 取不到快捷方式，大多情况出现在用户使用磁盘模式
						DeskToast.makeText(mActivity,
								mActivity.getString(R.string.cannot_read_app), Toast.LENGTH_LONG)
								.show();
						return;
					}

					MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
							IDockMsgId.DOCK_ADD_APPLICATION, -1, info, null);
				}
			}
				break;

			case IRequestCodeIds.REQUEST_SCREEN_ADD_APP : {
				if (resultCode == Activity.RESULT_OK) {
					Bundle bundle = data.getExtras();
					ArrayList<Intent> intents = bundle
							.getParcelableArrayList(AppListMultiple.INTENT_LIST_STRING);
					// mApplicationHolerId = IShortcutHolderIds.SHORTCUT_SCREEN;
					completeAddApplications(intents);
				}
			}
				break;

			case IRequestCodeIds.REQUEST_THEME_SCAN_VIEW_REFRESH : {
			}
				break;

			case IRequestCodeIds.REQUEST_RENAME : {
				if (resultCode == Activity.RESULT_OK) {
					String name = data.getStringExtra(RenameActivity.NAME);
					int handlerid = data.getIntExtra(RenameActivity.HANDLERID, -1);
					long itemid = data.getLongExtra(RenameActivity.ITEMID, -1);
					ArrayList<String> list = new ArrayList<String>();
					list.add(name);
					MsgMgrProxy.sendMessage(this, handlerid, ICommonMsgId.ICON_RENAME, -1, itemid, list);

					//					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
					//							IDiyMsgIds.QUICKACTION_EVENT, IQuickActionId.RENAME, itemid, list);
					list.clear();
					list = null;
				}
			}
				break;
			case IRequestCodeIds.REQUEST_MODIFY_FOLDER : {
				if (resultCode == Activity.RESULT_OK) {
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_FOLDER,
							IFolderMsgId.MODIFY_FOLDER_COMPELETE, -1, data, null);
				}
			}
				break;
				
			case IRequestCodeIds.BIND_SYSTEM_WIDGET: {
				if (resultCode == Activity.RESULT_OK) {
					int widgetId = data != null ? data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
					if (widgetId > 0) {
						Intent intent = new Intent();
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
						processPickAppwidget(Activity.RESULT_OK, intent);
					} else {
						
						if (mPaddingAddWidgetId == 0) {
							return;
						}
						
						Intent intent = new Intent();
						intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mPaddingAddWidgetId);
						processPickAppwidget(Activity.RESULT_OK, intent);
					}
				} else if (resultCode == Activity.RESULT_CANCELED) {
					int widgetId = data != null ? data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
					if (widgetId > 0) {
						mWidgetHost.deleteAppWidgetId(widgetId);
					}
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN, IScreenFrameMsgId.SCREEN_CLEAN_RESTOREAPPWIDGET_INFO, 0, null, null);
				}
			}
				break;
			default :
				break;
		}
	}

	private void dockChangeIcon(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			bundle.putInt("OkOrCancle", 1);
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
					ICommonMsgId.CHANGE_ICON_STYLE, -1, bundle, null);
		}
	}

	private void screenChangeIcon(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();

			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN, ICommonMsgId.CHANGE_ICON_STYLE, -1,
					bundle, null);
		}
	}

	void showSearchDialog(String initialQuery, boolean selectInitialQuery, Bundle appSearchData,
			boolean globalSearch) {
		if (appSearchData == null) {
			appSearchData = new Bundle();
			appSearchData.putString(SearchManager.QUERY, "launcher-search");
		}

		final SearchManager searchManager = (SearchManager) mActivity
				.getSystemService(Context.SEARCH_SERVICE);

		try {
			searchManager.startSearch(initialQuery, selectInitialQuery,
					mActivity.getComponentName(), appSearchData, globalSearch);
		} catch (Exception e) {
			// NO permission: android.permission.GLOBAL_SEARCH_CONTROL
			Log.i(LogConstants.HEART_TAG, "showSearchDialog error");
			// e.printStackTrace();
		}
	}

	private void processPickAppwidget(final int resultCode, final Intent data) {
		if (data == null) {
			return;
		}

		int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

		if (resultCode == Activity.RESULT_OK) {
			String customWidget = data.getStringExtra(EXTRA_CUSTOM_WIDGET);
			if (SEARCH_WIDGET.equals(customWidget)) {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.SCREEN_ADD_SEARCH_WIDGET, -1, null, null);
			} else {
				// 获取到的AppwidgetID
				configureOrAddAppWidget(data);
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {			
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
					IScreenFrameMsgId.SCREEN_DEL_APPWIDGET_ID, appWidgetId, null, null);
		}
	}

	private void configureOrAddAppWidget(Intent data) {
		int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		AppWidgetProviderInfo appWidget = AppWidgetManager.getInstance(mActivity).getAppWidgetInfo(
				appWidgetId);
		if (appWidget == null) {
			return;
		}
		if (appWidget.configure != null) {
			// Launch over to configure widget, if needed
			Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.setComponent(appWidget.configure);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

			startActivityForResult(intent, IRequestCodeIds.REQUEST_CREATE_APPWIDGET);
		} else {
			// Otherwise just add it
			onActivityResult(IRequestCodeIds.REQUEST_CREATE_APPWIDGET, Activity.RESULT_OK, data);
		}
	}

	/**
	 * 添加快捷方式到对应UI层
	 * 
	 * @param data
	 *            The intent describing the shortcut.
	 */
	private void completeAddShortcut(Intent data) {
		if (data == null) {
			return;
		}

		// 获取出intent中包含的应用
		final ShortCutInfo info = infoFromShortcutIntent(mActivity, data);

		if (info == null) {
			// 取不到快捷方式，大多情况出现在用户使用磁盘模式
			DeskToast.makeText(mActivity, mActivity.getString(R.string.cannot_read_app),
					Toast.LENGTH_LONG).show();
			return;
		}

		notifyShortCutHolder(info);
	}

	private void notifyShortCutHolder(final ShortCutInfo info) {
		// 将本应用发送给屏幕层进行添加工作
		switch (mShortcutHolerId) {
			case IShortcutHolderIds.SHORTCUT_SCREEN : {
				//3D
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IScreenFrameMsgId.SCREEN_ADD_SHORTCUT, -1, info, null);

			}
				break;

			case IShortcutHolderIds.SHORTCUT_DOCK_CLICK : {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
						IDockMsgId.DOCK_ADD_SHORTCUT, -1, info, null);
			}
				break;

			case IShortcutHolderIds.SHORTCUT_DOCK_GESTURE : {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
						IDockMsgId.DOCK_ADD_SHORTCUT_FOR_GESTURE, -1, info, null);
			}
				break;

			default :
				break;
		}
	}

	/**
	 * 添加widget到屏幕层
	 * 
	 * @param data
	 *            The intent describing the shortcut.
	 */
	private void completeAddAppWidget(Intent data, int resultCode) {		
		if (data == null) {
			return;
		}

		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				final int appWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

				// 将本应用发送给屏幕层进行添加工作
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN, IScreenFrameMsgId.SCREEN_ADD_APPWIDGET,
						appWidgetId, null, null);
			}
		} 
	}

	/**
	 * 添加应用程序到DOCK层
	 * 
	 * @param data
	 *            Intent数据
	 */
	private void completeAddApplication(Intent data) {
		if (data == null) {
			return;
		}

		// 获取出intent中包含的应用
		// final ShortCutInfo info = infoFromApplicationIntent(mActivity, data);
		final ShortCutInfo info = infoFromApplicationIntent2(mActivity, data);
		if (info == null) {
			// 取不到快捷方式，大多情况出现在用户使用磁盘模式
			DeskToast.makeText(mActivity, mActivity.getString(R.string.cannot_read_app),
					Toast.LENGTH_LONG).show();
			return;
		}

		MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
				IDockMsgId.DOCK_ADD_APPLICATION_GESTURE, -1, info, null);
	}

	/**
	 * 添加应用程序合集到屏幕
	 * 
	 * @param intents
	 */
	private void completeAddApplications(ArrayList<Intent> intents) {
		if (intents == null) {
			return;
		}

		// 获取出intent中包含的应用
		ArrayList<ShortCutInfo> infos = new ArrayList<ShortCutInfo>();
		int size = intents.size();
		for (int i = 0; i < size; i++) {
			final ShortCutInfo info = infoFromApplicationIntent2(mActivity, intents.get(i));
			if (info == null) {
				// 取不到快捷方式，大多情况出现在用户使用磁盘模式
				Toast.makeText(mActivity, mActivity.getString(R.string.cannot_read_app),
						Toast.LENGTH_SHORT).show();
			} else {
				infos.add(info);
			}
		}
		infos.clear();
		infos = null;
	}

	private ShortCutInfo infoFromApplicationIntent2(Context context, Intent data) {
		AppItemInfo appItemInfo = AppDataEngine.getInstance(ApplicationProxy.getContext()).getAppItem(data);
		if (appItemInfo == null) {
			// 异常情况
			Log.i(LogConstants.HEART_TAG, "infoFromApplicationIntent2 appItemInfo = null ");
			return infoFromApplicationIntent(context, data);
		}

		if (ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE.equals(data.getAction())
				|| ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME.equals(data.getAction())
				|| ICustomAction.ACTION_FUNC_SPECIAL_APP_GOWIDGET.equals(data.getAction())
				|| ICustomAction.ACTION_FUNC_SHOW_RECOMMENDCENTER.equals(data.getAction())
				|| ICustomAction.ACTION_FUNC_SHOW_GAMECENTER.equals(data.getAction())
				|| ICustomAction.ACTION_FREE_THEME_ICON.equals(data.getAction())) {
			ShortCutInfo shortCutInfo = new ShortCutInfo();
			shortCutInfo.mIcon = appItemInfo.mIcon;
			shortCutInfo.mItemType = IItemType.ITEM_TYPE_APPLICATION;
			shortCutInfo.mFeatureTitle = appItemInfo.mTitle;
			shortCutInfo.mIntent = appItemInfo.mIntent;
			shortCutInfo.mTitle = appItemInfo.mTitle;
			shortCutInfo.setRelativeItemInfo(appItemInfo);
			return shortCutInfo;
		} else {
			ComponentName component = data.getComponent();
			PackageManager packageManager = context.getPackageManager();
			ActivityInfo activityInfo = null;
			try {
				activityInfo = packageManager.getActivityInfo(component, 0);
			} catch (NameNotFoundException e) {
				Log.i(LogConstants.HEART_TAG,
						"Couldn't find ActivityInfo for selected application", e);
			}

			if (activityInfo != null) {
				ShortCutInfo itemInfo = new ShortCutInfo();
				// 注册
				// appItemInfo.registerObserver(itemInfo);

				itemInfo.mTitle = appItemInfo.getTitle();
				if (itemInfo.mTitle == null) {
					itemInfo.mTitle = activityInfo.name;
				}
				itemInfo.setActivity(component, Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

				itemInfo.mIcon = appItemInfo.getIcon();
				return itemInfo;
			}
		}
		return null;
	}

	private ShortCutInfo infoFromApplicationIntent(Context context, Intent data) {
		ComponentName component = data.getComponent();
		PackageManager packageManager = context.getPackageManager();
		ActivityInfo activityInfo = null;
		try {
			activityInfo = packageManager.getActivityInfo(component, 0 /*
																		 * no
																		 * flags
																		 */);
		} catch (NameNotFoundException e) {
			Log.i(LogConstants.HEART_TAG, "Couldn't find ActivityInfo for selected application", e);
		}

		if (activityInfo != null) {
			ShortCutInfo itemInfo = new ShortCutInfo();

			itemInfo.mTitle = activityInfo.loadLabel(packageManager);
			if (itemInfo.mTitle == null) {
				itemInfo.mTitle = activityInfo.name;
			}

			itemInfo.setActivity(component, Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

			itemInfo.mIcon = IconUtilities.createIconThumbnail(activityInfo.loadIcon(packageManager),
					context);
			return itemInfo;
		}

		return null;
	}

	private ShortCutInfo infoFromShortcutIntent(Context context, Intent data) {
		return SysAppInfo.createFromShortcut(context, data);
	}

	private void handleThemeScanResult(int resultCode, Intent data) {
		// String key =
		// mActivity.getResources().getString(R.string.needthemeid);
		if (data == null) {
			// 自动返回，则不处理
			return;
		}
	}

	/**
	 * 系统配置改变事件
	 * 
	 * @param newConfig
	 *            新的配置
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		PreferencesManager preferences = new PreferencesManager(mActivity,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		String currentlanguage = preferences.getString(IPreferencesIds.CURRENT_SELECTED_LANGUAGE, "");
		// 用户更换语言，重启桌面
		if ((null != mLastLocale) && !newConfig.locale.equals(mLastLocale)
				&& "".equals(currentlanguage)) {
			exit(true);
			//			AppUtils.killProcess();
		}

		//		if (MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_FRAME, IDiyMsgIds.PREVIEW_SHOWING, 0,
		//				null, null)) {
		//			return;
		//		}

		final boolean isChange = mLastOrientation != newConfig.orientation;
		// 正在添加桌面组件 时不广播屏幕切换事件
		if (isChange && !mAddingComponent) {
			MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_CONFIGURATION_CHANGED,
					newConfig.orientation, newConfig, null);
		}

		int oriType = SettingProxy.getGravitySettingInfo().mOrientationType;
		// 实体键盘处于推出状态，在此处添加额外的处理代码
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			OrientationControl.changeOrientationByKeyboard(mActivity, true, newConfig, oriType);
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			OrientationControl.changeOrientationByKeyboard(mActivity, false, newConfig, oriType);
		}

		mLastOrientation = newConfig.orientation;

		mLastLocale = newConfig.locale;
		// 更新壁纸
		mWallpaperControler.updateWallpaper(true);
		//		mWallpaperControler.updateWallpaperInBackground(true);
		//		// 横竖屏切换，语言选择列表对话框消失
		//		if (mSingleChoiceDialog != null && mSingleChoiceDialog.isShowing()) {
		//			mSingleChoiceDialog.dismiss();
		//		}
		if (mCoverFrame != null) {
			mCoverFrame.onConfigurationChange(newConfig);
		}

		if (Machine.IS_SDK_ABOVE_KITKAT) {
			StatusBarHandler.setStatusBarTransparentKitKat(mActivity.getWindow(), true);
			if (Machine.canHideNavBar()) {
				StatusBarHandler.setNavBarTransparentKitKat(mActivity.getWindow(), true);
			}
		}

		if (mWallpaperControler.isSysDrawSupport()) {
			WallpaperDensityUtil.setWallpaperDimension(mActivity);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
			case IAppCoreMsgId.APPCORE_DATACHANGE : {
				if (param == DataType.DATATYPE_THEMESETTING) {
					checkPersistenceAndTransparentStatusbar();
				}
			}
				break;

			default :
				break;
		}
	}

	private void pickWidget() {
		// 添加widget
		Bundle bundle = new Bundle();
		bundle.putInt("id", -1);

		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_GET_ALLOCATE_APPWIDGET_ID, -1, bundle, null);

		int allocateAppWidget = bundle.getInt("id");
		if (allocateAppWidget > -1) {
			Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
			pickIntent.putExtra(Intent.EXTRA_TITLE, mActivity.getText(R.string.select_widget_app));
			pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, allocateAppWidget);

			// add the search widget
			ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
			AppWidgetProviderInfo info = new AppWidgetProviderInfo();
			info.provider = new ComponentName(mActivity.getPackageName(), "XXX.YYY");
			info.label = mActivity.getString(R.string.group_search);
			info.icon = R.drawable.ic_search_widget;
			customInfo.add(info);
			pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);

			// 添加搜索widget
			ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
			Bundle b = new Bundle();
			b.putString(EXTRA_CUSTOM_WIDGET, SEARCH_WIDGET);
			customExtras.add(b);
			pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS,
					customExtras);

			// start the pick activity
			startActivityForResult(pickIntent, IRequestCodeIds.REQUEST_PICK_APPWIDGET);
		}
	}

	private void pickFolder() {
		// Insert extra item to handle inserting folder
		Bundle bundle = new Bundle();

		// ArrayList<String> shortcutNames = new ArrayList<String>();
		// shortcutNames.add(mActivity.getResources().getString(R.string.group_folder));
		// shortcutNames.add(mActivity.getResources().getString(R.string.app_drawer_folder));
		// bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME,
		// shortcutNames);

		// ArrayList<ShortcutIconResource> shortcutIcons = new
		// ArrayList<ShortcutIconResource>();
		// shortcutIcons.add(ShortcutIconResource.fromContext(
		// mActivity, R.drawable.ic_launcher_add_folder));
		// shortcutIcons.add(ShortcutIconResource.fromContext(
		// mActivity, R.drawable.ic_launcher_add_folder));
		// bundle.putParcelableArrayList(
		// Intent.EXTRA_SHORTCUT_ICON_RESOURCE, shortcutIcons);

		Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
		pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(LiveFolders.ACTION_CREATE_LIVE_FOLDER));
		pickIntent.putExtra(Intent.EXTRA_TITLE,
				mActivity.getText(R.string.title_select_live_folder));
		pickIntent.putExtras(bundle);

		startActivityForResult(pickIntent, IRequestCodeIds.REQUEST_PICK_LIVE_FOLDER);
	}

	@Override
	public void onWallpaperChange(Drawable wallpaperDrawable) {
		final int yOffset = mStatusBarHandler.isFullScreen(GoLauncherActivityProxy.getActivity()) ? 0 : StatusBarHandler
				.getStatusbarHeight();
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN, IScreenFrameMsgId.SET_WALLPAPER_DRAWABLE,
				yOffset, wallpaperDrawable, null);
	}

	/**
	 * 弹出语言列表
	 */
	public void showInstallLanguageTip(final Context context) {
		mLanguageChoiceDialog = new DialogLanguageChoice(mActivity);
		mLanguageChoiceDialog.show();
	}

	private void handleRecommandIcon(String pkgName) {
		if (PackageName.RECOMMAND_GOSMS_PACKAGE.equals(pkgName)
				|| PackageName.RECOMMAND_GOPOWERMASTER_PACKAGE.equals(pkgName)
				|| PackageName.RECOMMAND_GOTASKMANAGER_PACKAGE.equals(pkgName)
				|| PackageName.RECOMMAND_GOKEYBOARD_PACKAGE.equals(pkgName)
				|| PackageName.RECOMMAND_GOBACKUPEX_PACKAGE.equals(pkgName)
				|| PackageName.RECOMMAND_GOWEATHEREX_PACKAGE.equals(pkgName)
				|| PackageName.RECOMMAND_LOCKSCREEN_PACKAGE.equals(pkgName)) {
			// go短信、go省电、go任务管理器、go输入法、go备份、go天气,一键锁屏
			replaceRecommandIcon(pkgName);
		}
	}


	/**
	 * <br>功能简述:从其他版本go桌面导入数据库
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param pkg 源数据库的包名
	 */
	public void importDBFromOtherLauncher(final String pkg) {

		new Thread(ThreadName.MIGRATEINTODESK_STARTMIGRARE) {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				DBImporter.importDB(mActivity, pkg, mActivity.getPackageName());
				DataProvider.getInstance(mActivity).checkPkgNameForImportDB(pkg);
				Intent it = new Intent(ICustomAction.ACTION_REMOVE_DB_READ_PERMISSION);
				it.putExtra("pkg", pkg);
				mActivity.sendBroadcast(it);
				LauncherSelectorActivity.sImportDB = false;
				exit(true);
			}
		}.start();

	}

	/**
	 * 罩子层消息处理
	 */
	private void coverFrameHandle(int msgId, int viewId, Object object) {
		if (mCoverFrame != null) {
			mCoverFrame.handleMessages(msgId, viewId, object);
			if (mCoverFrame != null) { //handleMessages时可能会调用removeCoverFrame()，所以要多一次判空 
				mCoverFrame.requestLayout(); //该requestLayout()用于解决3D模式下，从功能表返回桌面罩子层元素无法绘制的问题
			}
		}
	} // end showCoverFrame

	/**
	 * 移除罩子层
	 */
	private void removeCoverFrame() {
		if (mCoverFrame != null) {
			//			mFrameControl.removeCoverFrame();
			mCoverFrame = null;
		}
	} // end showCoverFrame

	@Override
	public void handleRemoveCoverView() {
		removeCoverFrame();
	}

	@Override
	public void handleHideMaskView() {
		if (mCoverFrame != null) {
			mCoverFrame.hideMaskView();
		}
	}

	@Override
	public void handleShowMaskView() {
		if (mCoverFrame != null) {
			mCoverFrame.onWakeUp();
		}
	}

	@Override
	public void handleRemoveHolidayView() {
		coverFrameHandle(ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW, CoverFrame.COVER_VIEW_HOLIDAY, null);
	}

	private boolean isPrimeAd() {
		FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(mActivity
				.getApplicationContext());
		boolean hasPayAd = purchaseManager
				.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_AD);
		if (!hasPayAd || hasPayAd && !DeskSettingUtils.isNoAdvert()) {
			return true;
		}
		return false;
	}

	/**
	 * @author zhangxi
	 * @date 2013-09-22
	 * @describe:侧边栏广告拉数据
	 */
	private void getSidemenuAdData() {
		SideAdvertControl sideAdvertControl = SideAdvertControl
				.getAdvertControlInstance(mActivity);
//		sideAdvertControl.requestAdvertData();
		sideAdvertControl.requestToolsData();
		sideAdvertControl.requestWidgetData();
	}
	
	private void initLauncherProxy(ThemeInfoBean infoBean) {
		ThemeInfoBean.MiddleViewBean middleViewBean = infoBean.getMiddleViewBean();
		if (infoBean.isMaskView() || (middleViewBean != null && middleViewBean.mHasMiddleView)) {
			try {
				Context remoteContext = ApplicationProxy.getApplication().createPackageContext(
						infoBean.getPackageName(),
						Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
				PluginClassLoader loader = new PluginClassLoader(
						remoteContext.getClassLoader(), getClass().getClassLoader());
				Class<?> managerClass = loader
						.loadClass("com.gau.go.launcherex.theme.common.LauncherProxyManager");
				Method method = managerClass.getMethod("setLauncherProxy",
						IThemeLauncherProxy.class);
				method.invoke(null, new ThemeLauncherProxy());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	

	private IabHelper mHelper = null; //内购三版付费相关启动类
	private boolean mHasAlreadyCheckBillingSupported = false; //由于桌面启动后会调用两次checkBillingSupported方法，加个变量判断是否已检测过
	private void checkBillingSupported() {
		if (!mHasAlreadyCheckBillingSupported) {
			mHelper = new IabHelper(mActivity, AppInBillingActivity.LICENSEKEY);
			
			mHelper.enableDebugLogging(false);
			new Thread(ThreadName.CHECK_BILLING_SUPPORTED) {
				public void run() {
					mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
						public void onIabSetupFinished(IabResult result) {
							if (Consts.DEBUG) {
								Log.d(Consts.DEBUG_TAG, "Setup finished.");
							}
							if (!result.isSuccess()) {
								PurchaseSupportedManager.saveSupported(mActivity, false);
								Log.d(Consts.DEBUG_TAG, "Problem setting up in-app billing: "
										+ result);
							} else {
								PurchaseSupportedManager.saveSupported(mActivity, true);
								if (Consts.DEBUG) {
									Log.d(Consts.DEBUG_TAG, "Setup successful. ");
								}
							}
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									if (mHelper != null) {
										mHelper.dispose();
										mHelper = null;
									}
								}
							});
						}
					});
					mHasAlreadyCheckBillingSupported = true;
				};
			}.start();
		}
	}
	
	public boolean isNeedRestart() {
		return mNeedRestart;
	}
}
