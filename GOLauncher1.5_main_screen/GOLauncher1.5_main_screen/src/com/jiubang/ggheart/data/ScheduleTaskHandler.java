package com.jiubang.ggheart.data;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.launcherex.R;
import com.gau.utils.net.HttpAdapter;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.VersionControl;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.ConvertUtils;
import com.go.util.device.Machine;
import com.go.util.log.LogConstants;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IAppDrawerMsgId;
import com.golauncher.message.IAppManagerMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenAdvertMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.component.AppsManageView;
import com.jiubang.ggheart.appgame.appcenter.help.AppsManagementConstants;
import com.jiubang.ggheart.appgame.base.component.MainViewGroup;
import com.jiubang.ggheart.apps.config.ChannelConfig;
import com.jiubang.ggheart.apps.config.GOLauncherConfig;
import com.jiubang.ggheart.apps.desks.appfunc.appsupdate.AppsListUpdateManager;
import com.jiubang.ggheart.apps.desks.appfunc.handler.FuncAppDataHandler;
import com.jiubang.ggheart.apps.desks.backup.AutoBackUpTool;
import com.jiubang.ggheart.apps.desks.diy.GoLauncher;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.RateDialogContentActivity;
import com.jiubang.ggheart.apps.desks.diy.RestoryBackupDialogActivity;
import com.jiubang.ggheart.apps.desks.diy.guide.RateGuideTask;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.HttpUtil;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageCenterActivity;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageBaseBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean.MessageHeadBean;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.net.VersionManager;
import com.jiubang.ggheart.apps.desks.purchase.getjar.GetJarManager;
import com.jiubang.ggheart.apps.desks.settings.BackUpSettingReceiver;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean.AppBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.BaseBean;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.appmanager.AppManagerUtils;
import com.jiubang.ggheart.components.appmanager.SimpleAppManagerActivity;
import com.jiubang.ggheart.components.folder.advert.FolderAdController;
import com.jiubang.ggheart.components.sidemenuadvert.SideAdvertControl;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.data.statistics.StatisticsAppsInfoData;
import com.jiubang.ggheart.data.statistics.StatisticsData;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsContants;
import com.jiubang.ggheart.data.theme.LockerManager;
import com.jiubang.ggheart.data.theme.OnlineThemeGetter;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeNotifyBean;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.ThreadName;
import com.jiubang.ggheart.plugin.shell.ShellPluginFactory;
import com.jiubang.ggheart.smartcard.RecommInfoServer;

/**
 * 定时任务管理者
 * @author yangguanxiang
 *
 */
public final class ScheduleTaskHandler implements BroadCasterObserver {
	// 将时间统一抽至最前
	private final static long AUTO_CHECK_DELAY = 3 * 60 * 1000; // 启动后3mins后检查一次更新
	//		private final static long AUTO_CHECK_DELAY = 60 * 1000;  						// 检测时为10秒
	private final static long SCAN_APPS_DELAY = (int) (2.5 * 60 * 1000); // < 2.2 version 启动2.5mins后开始扫描
	private final static long SCAN_APPS_DELAY_FROYO = (int) (2.5 * 60 * 1000); // 2.2 version启动2.5mins后开始扫描
//	private final static long SHOW_RATE_DIALOG = 24 * 60 * 60 * 1000; // 新安装24小时提醒用户进行评分
	//	private final static long SCAN_APPS_DELAY = 25 * 1000;  						// < 2.2 version 启动25s后开始扫描
	//	private final static long SCAN_APPS_DELAY_FROYO = 25 * 1000 ;  					// 2.2 version启动25s后开始扫描
	private final static long UPDATE_INTERVAL = 8 * 60 * 60 * 1000; // 每隔 8小时检查一次更新
	//		private final static long UPDATE_INTERVAL = 60 * 1000;						// 每隔 10s检查一次更新
	// 请注意，上述代码涉及到统计上传
	
	//	private final static long AUTO_CHECK_DELAY = 30 * 1000; 						//检测时为30秒
	//	private final static long AUTO_CHECK_DELAY = 3* 60 * 1000;  					// 启动后3mins后检查一次更新
	//	private final static long AUTO_CHECK_DELAY = AlarmManager.INTERVAL_DAY;  		// 启动ngwe后24小时后检查一次更新
	//	private final static long SCAN_APPS_DELAY = (int)(2.5 * 60  * 1000);  			// < 2.2 version 启动2.5mins后开始扫描
	//	private final static long SCAN_APPS_DELAY_FROYO = (int)(2.5 * 60 * 1000) ;  	// 2.2 version启动2.5mins后开始扫描
	//	private final static long UPDATE_INTERVAL = AlarmManager.INTERVAL_HALF_DAY;		// 每隔 12小时检查一次更新
	//	private final static long UPDATE_INTERVAL = 55 * 1000;							//检测时为55秒
	//	private final static long UPDATE_INTERVAL = AlarmManager.INTERVAL_DAY;		// 每隔 24小时检查一次更新
	//	private final static long UPDATE_INTERVAL = 2*60*1000;		// 每隔 12小时检查一次更新，测试时使用4分钟

	// 2012-2-3 间隔时间从3天更改为2天 by 敖日明
	// edit by 敖日明 2013-03-22
	// edit by yejijiong 2013-05-15
	private final static int UPLOAD_DELAY_DAY_FOR_NORMAL = 3 * 15; //一般更新提示间隔时间，由于每次是8h，因此用3*n表示间隔n天	
	private final static int UPLOAD_DELAY_DAY_FOR_NOTIFICATION = 3 * 7; //通知栏更新提示间隔时间，由于每次是8h，因此用3*n表示间隔n天
	
	private final static long NOTIFY_USER_TO_UNINSTALL = 7 * 24 * 60 * 60 * 1000; //提示用户卸载少用应用的间隔
	
	public static final long ONE_DAY = AlarmManager.INTERVAL_DAY;
	public static final long SIDEAD_REQUEST_DELAY = ONE_DAY;

	private final HashMap<String, PendingIntent> mPendingHashMap = new HashMap<String, PendingIntent>(
			2);

	private Context mContext;
	private AlarmManager mAlarmManager;
	private TaskReceiver mReceiver;
	private MessageManager mMsgManager;
	//	private long mLastCheckUpdateTime = 0;

	public final static long ONE_MINUTE = 60 * 1000; // 1min
	public final static long HALF_AN_HOUR = 30 * ONE_MINUTE; // 30min
	public final static String SHAREDPREFERENCES_MSG_UPDATE = "msg_update";
	public final static String SHAREDPREFERENCES_CHECK_MSG = "msg_autocheck";
	public final static String SHAREDPREFERENCES_NEED_START_UPDATE = "start"; //网络连接上时是否需要更新
	public final static String SHAREDPREFERENCES_READED_LIST = "readed_msg_list";
	private static final int MSG_SHOW_DIALOG = 0X01;
	private static final int MSG_ADD_VIEW_COERFRAME = 0X02;
	private static final int MSG_PULL_NEW_THEME = 0X03;
	private static final int MSG_UPLOAD_BASICINFO = 0X04;
	private static final int MSG_UPLOAD_3D_STATE = 0X05;  //上传3D插件开关状态
	private static final int MSG_INIT_GETJAR = 0X06;  //初始化getjar

	public Handler mNotifyHandler = new Handler();

	private static final int MAX_AUTO_BACKUP_TRY_TIMES = 5;
	private int mAutoBackUpReTryTimes = 0;
	//	/**
	//	 * 点击notification的更新信息，跳转到应用中心的action
	//	 */
	//	private static final String ACTION_APP_MANAGEMENT = "com.gau.go.launcherex.appmanagement";

	public ScheduleTaskHandler(Context context) {
		mContext = context;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ICustomAction.ACTION_SCAN_APPS);
		filter.addAction(ICustomAction.ACTION_AUTO_CHECK_UPDATE);
		filter.addAction(ICustomAction.ACTION_TIDY_DATA);
		filter.addAction(ICustomAction.ACTION_MSGCENTER_SHOWMSG);
		filter.addAction(ICustomAction.ACTION_MSGCENTER_REMOVEMSG);
		filter.addAction(ICustomAction.ACTION_CHECK_NEWTHEME_NOTIFY);
		filter.addAction(ICustomAction.ACTION_AUTO_BACKUP_DB);
		filter.addAction(ICustomAction.ACTION_BACKUP);
		filter.addAction(ICustomAction.ACTION_CHECK_NEW_DB);
		filter.addAction(ICustomAction.ACTION_NOTIFY_UNINSTALL);
		filter.addAction(ICustomAction.ACTION_UPDATE_ADVERT_SIDEBAR);
		filter.addAction(ICustomAction.ACTION_BETA_NORMAL_INTERVAL);
		filter.addAction(ICustomAction.ACTION_BETA_CHECK_TIME);
		filter.addAction(ICustomAction.ACTION_BETA_CHECK_TIME_INTERVAL);
		filter.addAction(ICustomAction.ACTION_SHOW_RATE_DIALOG);

//		if (VersionControl.getFirstRun()) {
//			setFirstRunForRate(VersionControl.getFirstRun());
//		}
//		if (getFirstRunForRate()) {
//			filter.addAction(ICustomAction.ACTION_SHOW_RATE_DIALOG);
//		}

		mReceiver = new TaskReceiver();
		mContext.registerReceiver(mReceiver, filter);
		mMsgManager = MessageManager.getMessageManager(ApplicationProxy.getContext());
		mMsgManager.registerObserver(this);

		//注册网络状态监听
		filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(mReceiver, filter);
	}
	/**
	 * 启动统计桌面数据的任务
	 */
	public void startStatisticsTask() {
		scheduleNextStatistics(Statistics.STATICTISC_USEDHOURS_FREQUENCY);
		startUpload3DInstructStatistics();
	}
	
	private void scheduleNextStatistics(long time) {
		try {
			final long tiggertTime = System.currentTimeMillis() + time;
			Intent intent = new Intent(ICustomAction.ACTION_TIDY_DATA);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_TIDY_DATA, pendingIntent);
			intent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "startStatisticsTask error");
		}
	}

	/**
	 * @author zhangxi
	 * @date 2013-09-23
	 */
	public void startUpSideAdData() {
		scheduleNextUpSideAdData(SIDEAD_REQUEST_DELAY);
	}

	/**
	 * 
	 * @param strKey
	 *            action String
	 * @param time
	 *            triggertime
	 * @author zhangxi
	 * @data 2013-09-22
	 */
	private void scheduleNextUpSideAdData(long time) {
		try {
			final long tiggertTime = System.currentTimeMillis() + time;
			Intent intent = new Intent(
					ICustomAction.ACTION_UPDATE_ADVERT_SIDEBAR);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
					0, intent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime,
					pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_UPDATE_ADVERT_SIDEBAR,
					pendingIntent);
			intent = null;
			pendingIntent = null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(LogConstants.HEART_TAG, "scheduleNextAction error");
		}
	}

	//	private void scheduleNextRate(long time) {
	//		try {
	//			final long tiggertTime = System.currentTimeMillis() + time;
	//			Intent intent = new Intent(ICustomAction.ACTION_SHOW_RATE_DIALOG);
	//			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
	//			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
	//			mPendingHashMap.put(ICustomAction.ACTION_SHOW_RATE_DIALOG, pendingIntent);
	//			intent = null;
	//			pendingIntent = null;
	//		} catch (Exception e) {
	//			Log.i(LogConstants.HEART_TAG, "startRateTask error");
	//		}
	//	}

	public void startBetaTask() {
//		BetaController.getInstance().startBetaTask();
	}
	
	/**
	 * 检查更新
	 */
	public void startCheckUpdateTask() {
		scheduleNextCheck(AUTO_CHECK_DELAY);
	}

	private void scheduleNextCheck(long time) {
		try {
			final long tiggertTime = System.currentTimeMillis() + time;
			Intent updateIntent = new Intent(ICustomAction.ACTION_AUTO_CHECK_UPDATE);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, updateIntent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_AUTO_CHECK_UPDATE, pendingIntent);
			updateIntent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "startCheckUpdateTask error");
		}
	}
	
	public void startNotifyUninstallTask() {
		try {
			PreferencesManager sharedPreferences = new PreferencesManager(mContext,
					IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
			
			long tiggerTime = 0;
			long now = System.currentTimeMillis();
			long firstStartTime = sharedPreferences.getLong(IPreferencesIds.REMIND_NOTIFY_UNINSTALL_TIME, 0L);
			
			if (firstStartTime <= 0) {
				tiggerTime = now + NOTIFY_USER_TO_UNINSTALL;
				sharedPreferences.putLong(IPreferencesIds.REMIND_NOTIFY_UNINSTALL_TIME, now);
				sharedPreferences.commit();
			} else if (now - firstStartTime >= NOTIFY_USER_TO_UNINSTALL) {
				tiggerTime = now;
				sharedPreferences.putLong(IPreferencesIds.REMIND_NOTIFY_UNINSTALL_TIME, now);
				sharedPreferences.commit();
			} else {
				tiggerTime = NOTIFY_USER_TO_UNINSTALL + firstStartTime;
			}
						
			Intent scanAppIntent = new Intent(ICustomAction.ACTION_NOTIFY_UNINSTALL);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, scanAppIntent, 0);
			mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP
		            , tiggerTime, NOTIFY_USER_TO_UNINSTALL, pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_NOTIFY_UNINSTALL, pendingIntent);
			scanAppIntent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "startNotifyUninstallTask error");
		}
	}

	public void startScanAppTask() {
		try {
			final int apiLevel = Build.VERSION.SDK_INT;
			long tiggerTime = System.currentTimeMillis();
			if (apiLevel >= 8) {
				tiggerTime += SCAN_APPS_DELAY_FROYO;
			} else {
				tiggerTime += SCAN_APPS_DELAY;
			}

			Intent scanAppIntent = new Intent(ICustomAction.ACTION_SCAN_APPS);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, scanAppIntent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggerTime, pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_SCAN_APPS, pendingIntent);
			scanAppIntent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "startScanAppTask error");
		}
	}

	public void startRateDialogTask() {
		long showRateTime;
		PreferencesManager ratePreferences = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		
		clearHasShowRatePref(ratePreferences, sharedPreferences);

		// 4.11需求，无论新旧用户，定时时间统一为8小时
		showRateTime = 8 * 60 * 60 * 1000;
		
		String country = Machine.getCountry(mContext);
		// 韩国、台湾、巴西不弹评分弹框
		if (!Machine.isKorea(mContext) && !country.equals("tw") && !country.equals("br")) {
			try {
				//使用新的sharepreferences，DESK_SHAREPREFERENCES_FILE恢复默认或一些操作后会被clear。
				//DESK_SHAREPREFERENCES_FILE暂时还保留，迭代几个版本后去掉
//				PreferencesManager ratePreferences = new PreferencesManager(mContext,
//						IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
				boolean hasShow = ratePreferences.getBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG,
						false);
				if (hasShow) {
					return;
				}
				long firstCheckTime = 0L;
				firstCheckTime = ratePreferences.getLong(IPreferencesIds.RATE_FIRST_CHECK_TIME, 0L);
				//*****************************************************************************//
				if (sharedPreferences != null && firstCheckTime == 0) {
					firstCheckTime = sharedPreferences
							.getLong(IPreferencesIds.RATE_FIRST_CHECK_TIME, 0L);
				}
				long now = System.currentTimeMillis();
				long nextCheckTime = 0L; //下一次上传间隔时间
				if (firstCheckTime == 0L) {
					nextCheckTime = showRateTime + now;
					//				sharedPreferences.putLong(IPreferencesIds.REMIND_RATE_TIME, now);
					//				sharedPreferences.commit();

					ratePreferences.putLong(IPreferencesIds.RATE_FIRST_CHECK_TIME, now);
					ratePreferences.commit();
				} else if (showRateTime <= now - firstCheckTime) {
					nextCheckTime = now;
				} else {
					nextCheckTime = showRateTime + firstCheckTime;
				}
				Intent rateAppIntent = new Intent(ICustomAction.ACTION_SHOW_RATE_DIALOG);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
						rateAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				mAlarmManager.set(AlarmManager.RTC_WAKEUP, nextCheckTime, pendingIntent);
				mPendingHashMap.put(ICustomAction.ACTION_SHOW_RATE_DIALOG, pendingIntent);
				rateAppIntent = null;
				pendingIntent = null;
			} catch (Exception e) {
				Log.i(LogConstants.HEART_TAG, "startShowRateDialogTask error");
			}

		}
	}
	
	/**
	 * 清除已经触发评分框的标记 判断条件：1.如果是更新版本后初次使用, 且上一版本是v4.10以前的版本（旧的判断逻辑）
	 * 2.如果是更新版本后初次使用，且上一版本是v4.10以后的版本 整合后，更新版本后初次使用，一定将重置标记，弹框首次展示逻辑，将由
	 * {@link IPreferencesIds#REMIND_RATE}决定 注释：v4.10的版本号为305 v4.11的版本号为306
	 */
	private void clearHasShowRatePref(PreferencesManager ratePreferences, PreferencesManager sharedPreferences) {		
		if (VersionControl.getNewVeriosnFirstRun()) {
			boolean hasShowLoastVersion = ratePreferences.getBoolean(
					IPreferencesIds.HAS_SHOW_RATE_DIALOG, false);
			if (isLastVersion410()) {
				// 上一个版本如果是4.10, 如果有弹过弹框，无论做什么操作，以后版本都不再弹
				if (hasShowLoastVersion) {
					ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, false);
				}
			} else {
				// 重置缓存
				// 由于4.12之前版本代码没有记录RATE_LAST_SHOW_TIME这个字段，因此4.12初始化该字段，会得到默认值-1，导致国家过滤和间隔判断失效
				if (isLastVersionBefore412()) {
					// 如果上一版本有显示过 则虚拟一个上次显示时间，为当前系统时间
					if (hasShowLoastVersion) {
						ratePreferences.putLong(IPreferencesIds.RATE_LAST_SHOW_TIME,
								System.currentTimeMillis());
					}
					// 由于4.12版本需要对评分引导相关字段进行备份处理，而4.12之前版本REMIND_RATE字段保存在桌面pref文件
					// 4.12及以后的版本，将以ratePreferences中的REMIND_RATE字段值为准，故此在新版本初次运行时候进行字段转移
					boolean remind_rate = sharedPreferences.getBoolean(IPreferencesIds.REMIND_RATE,
							true);
					sharedPreferences.remove(IPreferencesIds.REMIND_RATE);
					ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, remind_rate);
				}
				ratePreferences.putBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG, false);
				ratePreferences.putLong(IPreferencesIds.RATE_FIRST_CHECK_TIME, 0L);
				sharedPreferences.putLong(IPreferencesIds.RATE_FIRST_CHECK_TIME, 0L);
			}
			ratePreferences.putBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG_LASTVERSION,
					hasShowLoastVersion);
				ratePreferences.commit();
				sharedPreferences.commit();
			}
		// 注释旧代码
		// if (GOLauncherApp.getApplication().getNewVeriosnFirstRun()
		// && GOLauncherApp.getLastVersionCode() < 305) {
		// ratePreferences.putBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG,
		// false);
		// ratePreferences.putLong(IPreferencesIds.REMIND_RATE_TIME, 0L);
		// ratePreferences.commit();
		// sharedPreferences.putLong(IPreferencesIds.REMIND_RATE_TIME, 0L);
		// sharedPreferences.commit();
		// }
	}
	
	private boolean isLastVersion410() {
		return VersionControl.getLastVersionCode() == 305
				|| VersionControl.getLastVersionCode() == 306;
	}
	
	private boolean isLastVersionBefore412() {
		return VersionControl.getLastVersionCode() < RateDialogContentActivity.VERSIONCODE_412;
	}

	/**
	 * 上传3D插件统计数据
	 */
	public void startUpload3DInstructStatistics() {
		PreferencesManager sharedPreferences = new PreferencesManager(
				mContext, IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
		boolean show = sharedPreferences.getBoolean(IPreferencesIds.PREFERENCE_ENGINE_CHANGED_NEED_UPLOAD_STATISTICS, false);
		if (show) { //需要上传3D开关控制数据
			mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_3D_STATE, 5000);
//			mMsgManager.upload3DInstructStatistics();
		}
	}
	
	public synchronized void cancel() {
		Collection<PendingIntent> collection = mPendingHashMap.values();
		for (PendingIntent pendingIntent : collection) {
			mAlarmManager.cancel(pendingIntent);
		}
		mPendingHashMap.clear();
		mContext.unregisterReceiver(mReceiver);
	}

	private void doAutoCheck() {
		long now = System.currentTimeMillis();
		long nextCheckTime = UPDATE_INTERVAL; //下一次上传间隔时间
		long lastCheckUpdate = getLastCheckedTime(); //上一次的检查时间
		if (lastCheckUpdate == 0L || (now - lastCheckUpdate >= UPDATE_INTERVAL)
				|| (now - lastCheckUpdate <= 0L)) {
			final VersionManager versionManager = AppCore.getInstance().getVersionManager();
			//检查更新同时上传统计数据
			versionManager.startCheckUpdate();
			RecommInfoServer.getServer(mContext).loadDataAysnc();
			//检查程序列表更新
			int tryUpload = getLastTryUpload();
			if (tryUpload % 3 == 0) {
				//每隔24h上传一次，此处需要间隔一次。
				AppsListUpdateManager appsListUpdateManager = AppCore.getInstance()
						.getAppsListUpdateManager();
				HttpAdapter httpAdapter = AppCore.getInstance().getHttpAdapter();
				IConnectListener receiver = getUpdateConnectListener();
				appsListUpdateManager.startCheckUpdate(httpAdapter, receiver, true, 0);
				
				//交给统计sdk去上传用户列表数据信息
				appsListUpdateManager.autoPostAppListData();
				//交给统计sdk去上传用户数据后，即可清理掉本地的统计数据，因为统计sdk会保证上传到达
//				StatisticsAppsInfoData.resetStatisticsAllDataInfos(mContext);
				
				if (ShellPluginFactory.isSupportShellPlugin(mContext)) {
					//仅支持3D的用户上传数据
					mMsgManager.upload3DInstructStatistics();
				}
			}
			setLastTryUpload(++tryUpload);

			//保存本次检查的时长
			setLastCheckedTime(now);
			//检查新主题
			mHandler.sendEmptyMessageDelayed(MSG_PULL_NEW_THEME, 600);
			mHandler.sendEmptyMessageDelayed(MSG_UPLOAD_BASICINFO, 1000);

		} else {
			//动态调整下一次的间隔时间
			nextCheckTime = UPDATE_INTERVAL - (now - lastCheckUpdate);
		}

		// 启动下一次定时检查
		scheduleNextCheck(nextCheckTime);
	}

	/**
	 * 更新连接监听
	 * @return
	 */
	private IConnectListener getUpdateConnectListener() {
		final IConnectListener receiver = new IConnectListener() {

			@Override
			public void onStart(THttpRequest arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinish(THttpRequest arg0, IResponse arg1) {
				// TODO Auto-generated method stub
				//1、清理统计数据
				StatisticsAppsInfoData.resetStatisticsAllDataInfos(mContext);

				//2、可以更新的数据bean
				if (arg1 != null) {
					ArrayList<BaseBean> listBeans = (ArrayList<BaseBean>) arg1.getResponse();
					if (listBeans != null && listBeans.size() > 0) {
						final AppsBean appsBean = (AppsBean) listBeans.get(0);
						if (appsBean != null && appsBean.mListBeans != null
								&& appsBean.mListBeans.size() > 0) {
							//取出渠道配置信息，后面更新处理要根据这个而定
							//Add by wangzhuobin 2012.07.28
							final ChannelConfig channelConfig = GOLauncherConfig.getInstance(ApplicationProxy.getContext()).getChannelConfig();

							int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

							int tryUpload = getLastTryUpload();

							boolean hasHandledUpdateMsg = getHandledAppUpdateMsg();
							if (!hasHandledUpdateMsg) {
								setHandledAppUpdateMsg(true);
							}
							FuncAppDataHandler handler = FuncAppDataHandler.getInstance(mContext);
							if (tryUpload % UPLOAD_DELAY_DAY_FOR_NORMAL == 1) {
								//相隔7d，是海外用户								
								if (handler != null && handler.isShowAppUpdate()) {
									//从服务器得到新的AppsBean时,把ClickAppupdate设为false.
									handler.setmClickAppupdate(false);
									if (currentHour >= 8 && currentHour < 23) {

										if (VersionControl.getFirstRun() && !hasHandledUpdateMsg) {
											//如果是第一次安装运行则延迟8小时后通知
											mNotifyHandler.postDelayed(new Runnable() {

												@Override
												public void run() {
													sendUpdateMsgToAppFunc(channelConfig, appsBean);
												}
											}, 8 * 60 * 60 * 1000);
										} else {
											sendUpdateMsgToAppFunc(channelConfig, appsBean);
										}

									} else {
										//延迟10小时通知
										mNotifyHandler.postDelayed(new Runnable() {

											@Override
											public void run() {
												sendUpdateMsgToAppFunc(channelConfig, appsBean);
											}
										}, 10 * 60 * 60 * 1000);
									}

								}
							}
							if (tryUpload % UPLOAD_DELAY_DAY_FOR_NOTIFICATION == 1) {
								if (handler != null && handler.isShowAppUpdate()) {
									if (currentHour >= 8 && currentHour < 23) {
										if (VersionControl.getFirstRun()
												&& !hasHandledUpdateMsg) {
											//如果是第一次安装运行则延迟8小时后通知
											mNotifyHandler.postDelayed(new Runnable() {

												@Override
												public void run() {
													sendUpdateMsgToNotification(channelConfig,
															appsBean);
												}
											}, 8 * 60 * 60 * 1000);
										} else {
											sendUpdateMsgToNotification(channelConfig, appsBean);
										}
									} else {
										// 延迟10小时通知

										mNotifyHandler.postDelayed(new Runnable() {

											@Override
											public void run() {
												sendUpdateMsgToNotification(channelConfig, appsBean);
											}
										}, 10 * 60 * 60 * 1000);
									}
								}
							}
						}
					}

				}

			}

			@Override
			public void onException(THttpRequest arg0, int arg1) {
				// TODO Auto-generated method stub
				StatisticsData.saveHttpExceptionDate(mContext, arg0, arg1);
			}
		};

		return receiver;
	}

	private boolean isNeedToCheckWhenNetWorked() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		boolean result = false;
		if (pref != null) {
			result = pref.getBoolean(PrefConst.KEY_CHECK, false);
		}
		return result;
	}

	private void setNeedToCheckWhenNetWorked(boolean checked) {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		if (pref != null) {
			pref.putBoolean(PrefConst.KEY_CHECK, checked);
			pref.commit();
		}
	}

	private int getLastTryUpload() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		int lastTryUpload = 0;
		if (pref != null) {
			lastTryUpload = pref.getInt(PrefConst.KEY_UPLOAD, 0);
		}
		return lastTryUpload;
	}

	private void setLastTryUpload(int tryUploadTime) {
		if (tryUploadTime >= Integer.MAX_VALUE) {
			tryUploadTime = 0;
		}
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		if (pref != null) {
			pref.putInt(PrefConst.KEY_UPLOAD, tryUploadTime);
			pref.commit();
		}
	}

	private long getLastCheckedTime() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		long lastCheckedTime = 0L;
		if (pref != null) {
			lastCheckedTime = pref.getLong(PrefConst.KEY_CHECK_TIME, 0L);
		}
		return lastCheckedTime;
	}

	private void setLastCheckedTime(long checkedTime) {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		if (pref != null) {
			pref.putLong(PrefConst.KEY_CHECK_TIME, checkedTime);
			pref.commit();
		}
	}

	/**
	 * 任务广播接受者
	 * @author yangguanxiang
	 *
	 */
	private class TaskReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			//			Log.i("TaskReceiver", "action = " + action);
			try {
				if (action.equals(ICustomAction.ACTION_SCAN_APPS)) {
					mPendingHashMap.remove(action);
					// 超时开始扫描app
					final AppDataEngine dataEngine = AppDataEngine.getInstance(ApplicationProxy.getContext());
					dataEngine.asynReScanSysApp(IAppCoreMsgId.EVENT_REFLUSH_TIME_IS_UP, null);
				}
				//检查更新同时上传统计数据
				else if (action.equals(ICustomAction.ACTION_AUTO_CHECK_UPDATE)) {
					if (Machine.isNetworkOK(mContext)) {
						mPendingHashMap.remove(action);

						doAutoCheckMsg(); //消息中心更新
						//						doAutoGetUrl();	
						
						//内测的定时统计,因为周期都是８小时,所以放在这里
//						BetaController.getInstance().uploadStatisticsData();

						//当前网络可用，直接检查更新及上传统计
						doAutoCheck();

					} else {
						//当前网络不可用，则保存当前状态值，以便网络状态OK时，再次进行上传统计
						setNeedToCheckWhenNetWorked(true);
					}
					saveStartTime();
					//由于卡顿，getjar初始化延迟到三分钟后，以后的每8小时也会走到这里，但getjar是单例不会被多次实例化的情况。
					mHandler.sendEmptyMessageDelayed(MSG_INIT_GETJAR, ONE_MINUTE * 2);
				}
				//监控网络更改状态
				else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					//1.网络可用，且上次未上传成功，则上传统计，并修改状态值
					if (Machine.isNetworkOK(mContext)) {
						if (isNeedToCheckWhenNetWorked()) {
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									doAutoCheckMsg(); //消息中心更新

								}
							}, 60000);
							//						doAutoGetUrl();	
							mHandler.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									//当前网络可用，直接检查更新及上传统计
									doAutoCheck();
								}
							}, 30000);
							//修改状态值
							setNeedToCheckWhenNetWorked(false);
						}
						FolderAdController.getInstance().checkFolderAdDataUpdate();
						// 检查是否需要弹出评分引导弹框
						RateGuideTask.getInstacne(mContext).checkUnExcutedRate();
						RecommInfoServer.getServer(mContext).loadDataAysnc();
					}
				}
				//统计桌面数据
				else if (action.equals(ICustomAction.ACTION_TIDY_DATA)) {
					mPendingHashMap.remove(action);
					VersionManager versionManager = AppCore.getInstance().getVersionManager();
					if (versionManager != null) {
						Statistics statistics = versionManager.getStatistics();
						if (statistics != null) {
							//更新使用时间
							statistics.increaseUseTime();
						}
					}

					// 保存用户使用信息
					StatisticsData.saveNoUploadDate(mContext);

					// 启动下一次定时统计
					scheduleNextStatistics(Statistics.STATICTISC_USEDHOURS_FREQUENCY);
					//广告8小时判断一下是否进行通知栏提示
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
							IScreenAdvertMsgId.CHECK_IS_NOT_OPEN, -1);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
							IScreenAdvertMsgId.CHECK_REQUEST_AGAIN, -1);
					FolderAdController.getInstance().checkFolderAdDataUpdate();
				}
				else if (action.equals(ICustomAction.ACTION_UPDATE_ADVERT_SIDEBAR)) {
					//请求更新数据
					SideAdvertControl sideAdvertControl = SideAdvertControl.getAdvertControlInstance(mContext);
//					sideAdvertControl.requestAdvertData();
					sideAdvertControl.requestToolsData();
					sideAdvertControl.requestWidgetData();
					
					//设置新的更新时间
					scheduleNextUpSideAdData(SIDEAD_REQUEST_DELAY);
				}
				//弹出评分对话框
				else if (action.equals(ICustomAction.ACTION_SHOW_RATE_DIALOG)) {
					mPendingHashMap.remove(action);
					RateGuideTask.getInstacne(mContext).showRateDialog(intent);
				} else if (action.equals(ICustomAction.ACTION_MSGCENTER_SHOWMSG)) {
					checkViewType();
				} else if (action.equals(ICustomAction.ACTION_MSGCENTER_REMOVEMSG)) {
					removeCoverMsg();
				} else if (action.equals(ICustomAction.ACTION_CHECK_NEWTHEME_NOTIFY)) {
					checkThemeNotify();
				} else if (action.equals(ICustomAction.ACTION_AUTO_BACKUP_DB)) {
					AutoBackUpTool.backUpSettings(mContext);
				} else if (action.equals(ICustomAction.ACTION_BACKUP)) {
					handleDBBackCB(intent);
				} else if (action.equals(ICustomAction.ACTION_CHECK_NEW_DB)) {
					showRestoreDBDialog();
				} else if (action.equals(ICustomAction.ACTION_NOTIFY_UNINSTALL)) {
					
					PreferencesManager sharedPreferences = new PreferencesManager(mContext,
							IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
					boolean allowFlag = sharedPreferences.getBoolean(IPreferencesIds.ALLOW_NOTIFY_UNINSTALL_LESSUSE_APP, true);
					
					long now = System.currentTimeMillis();
					sharedPreferences.putLong(IPreferencesIds.REMIND_NOTIFY_UNINSTALL_TIME, now);
					sharedPreferences.commit();
					
					if (!allowFlag) {
						return;
					}

					List<?> lessUseApp = AppManagerUtils.lessUsedApp(mContext);
					if (lessUseApp != null && lessUseApp.size() > 10) {
						NotificationManager notificationManager = 
							(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
						Intent target = new Intent(mContext, SimpleAppManagerActivity.class);

						String title = mContext.getResources().getString(R.string.notify_user_uninstall_lessused_app_title);
						String content = mContext.getResources().getString(R.string.notify_user_uninstall_lessused_app_content);
						
						Notification notification = new Notification();
						notification.icon = R.drawable.icon;
						notification.tickerText = title;
						notification.defaults = Notification.DEFAULT_SOUND;
						notification.flags |= Notification.FLAG_AUTO_CANCEL; // 点击后自动消失
						
						PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, target, PendingIntent.FLAG_UPDATE_CURRENT);

						notification.setLatestEventInfo(mContext, title, content, pendingIntent);
						notificationManager.notify(1, notification);
					}
				} else if (action.equals(ICustomAction.ACTION_BETA_NORMAL_INTERVAL)) {
//					Log.d("beta", "ICustomAction.ACTION_BETA_NORMAL_INTERVAL");
//					BetaController.getInstance().requestBetaInfo();
				} else if (action.equals(ICustomAction.ACTION_BETA_CHECK_TIME)) {
//					Log.d("beta", "ICustomAction.ACTION_BETA_CHECK_TIME");
//					BetaController.getInstance().startCheckoutInterval();
				} else if (action.equals(ICustomAction.ACTION_BETA_CHECK_TIME_INTERVAL)) {
//					Log.d("beta", "ICustomAction.ACTION_BETA_CHECK_TIME_INTERVAL");
//					BetaController betaController = BetaController.getInstance();
//					if (betaController.isInCheckTimeInterval()) {
//						betaController.requestBetaInfo();
//					} else {
//						betaController.startNormalInterval();
//					}					
				}

			} catch (Exception e) {
				Log.i(LogConstants.HEART_TAG, "TaskReceiver onReceive err, action = " + action);
			}
		}
	}

	private void saveStartTime() {
		try {
			Context context = ApplicationProxy.getContext();
			PrivatePreference pref = PrivatePreference.getPreference(context);
			pref.putLong(PrefConst.KEY_STARTTIME, System.currentTimeMillis());
			pref.commit();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void doAutoCheckMsg() {
		long now = System.currentTimeMillis();
		long lastCheckUpdate = HttpUtil.getLastUpdateMsgTime(mContext); //上一次的检查时间
		if (lastCheckUpdate == 0L || (now - lastCheckUpdate >= UPDATE_INTERVAL)
				|| (now - lastCheckUpdate <= 0L)) {
			mMsgManager.postUpdateRequest(1);
			mMsgManager.postGetUrlRequest();
		} else {
			checkViewType();
		}
	}

	//	private void doAutoGetUrl() {
	//		long now = System.currentTimeMillis();
	//		long lastCheckUpdate = HttpUtil.getLastUpdateMsgTime(mContext); //上一次的检查时间
	//		if (lastCheckUpdate == 0L || (now - lastCheckUpdate >= UPDATE_INTERVAL)
	//				|| (now - lastCheckUpdate <= 0L)) {
	//			mMsgManager.postGetUrlRequest();
	//		}
	//	}

	/**
	 * <br>功能简述:检查消息中心中是否有未读需要主动显示的消息
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void checkViewType() {
		Vector<MessageHeadBean> msgs = mMsgManager.getMessageList();
		boolean hasDialogMsg = false;
		boolean hasStatusBarMsg = false;
		boolean hasCoverFrameMsg = false;
		long nextScheduleTime = Long.MAX_VALUE;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		for (int i = 0; msgs != null && i < msgs.size(); i++) {
			MessageHeadBean bean = msgs.get(i);
			if (!bean.misReaded && (bean.mViewType & MessageBaseBean.VIEWTYPE_DIALOG) != 0
					&& !hasDialogMsg) {
				String hms = getCurrentHMS();
				if (bean.mEndTime != null
						&& bean.mStartTime != null
						&& isInTime(hms, bean.mStartTime, bean.mEndTime)
						|| (bean.mEndTime == null || bean.mStartTime == null
								|| bean.mEndTime.equals("") || bean.mStartTime.equals(""))) {
					if (!mMsgManager.isInMessageCenter()) {
						if (bean.mIsWifi == 1 && !mMsgManager.isWifiConnected()) {
							mMsgManager.saveDataInfoAndRegisterWifiReceiver(bean);
						} else {
							if (Machine.isNetworkOK(mContext)) { //网络状态正常才弹窗
								mMsgManager.showDialogMessage(bean.mId);
								hasDialogMsg = true;
							}
						}
					}
				} else if (bean.mStartTime != null && !bean.mStartTime.equals("")) {
					try {
						Date d1 = sdf.parse(hms);
						Date d2 = sdf.parse(bean.mStartTime);
						nextScheduleTime = Math.abs(d1.getTime() - d2.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (Exception e) {

					}
				}
			}

			if (!bean.misReaded && !bean.mIsRemoved
					&& (bean.mViewType & MessageBaseBean.VIEWTYPE_STATUS_BAR) != 0
					&& !hasStatusBarMsg) {
				String hms = getCurrentHMS();
				if (bean.mEndTime != null
						&& bean.mStartTime != null
						&& isInTime(hms, bean.mStartTime, bean.mEndTime)
						|| (bean.mEndTime == null || bean.mStartTime == null
								|| bean.mEndTime.equals("") || bean.mStartTime.equals(""))) {
					if (!mMsgManager.isInMessageCenter()) {
						mMsgManager.sendNotify(bean);
						hasStatusBarMsg = true;
					}
				} else if (bean.mStartTime != null && !bean.mStartTime.equals("")) {
					try {
						Date d1 = sdf.parse(hms);
						Date d2 = sdf.parse(bean.mStartTime);
						long time = Math.abs(d1.getTime() - d2.getTime());
						if (nextScheduleTime > time) {
							nextScheduleTime = time;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (Exception e) {

					}
				}
			}

			String hms = getCurrentHMS();
			if (!bean.misReaded && (bean.mViewType & MessageBaseBean.VIEWTYPE_DESK_TOP) != 0
					&& !hasCoverFrameMsg && !bean.mClickClosed) {

				if (bean.mEndTime != null
						&& bean.mStartTime != null
						&& isInTime(hms, bean.mStartTime, bean.mEndTime)
						|| (bean.mEndTime == null || bean.mStartTime == null
								|| bean.mEndTime.equals("") || bean.mStartTime.equals(""))) {
					if (bean.mIsWifi == 1 && !mMsgManager.isWifiConnected()) {
						mMsgManager.saveDataInfoAndRegisterWifiReceiver(bean);
					} else { //不受wifi控制，直接
						Message msg = Message.obtain();
						msg.what = MSG_ADD_VIEW_COERFRAME;
						msg.obj = bean;
						mHandler.sendMessage(msg);
						hasCoverFrameMsg = true;
						if (bean.mEndTime != null && !bean.mEndTime.equals("")) {
							try {
								Date d1 = sdf.parse(hms);
								Date d2 = sdf.parse(bean.mEndTime);
								long removeT = Math.abs(d1.getTime() - d2.getTime());
								scheduleRemoveCoverMsg(removeT); //超出显示时间移除视图
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				} else if (bean.mStartTime != null && !bean.mStartTime.equals("")) {
					try {
						Date d1 = sdf.parse(hms);
						Date d2 = sdf.parse(bean.mStartTime);
						long time = Math.abs(d1.getTime() - d2.getTime());
						if (nextScheduleTime > time) {
							nextScheduleTime = time;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (Exception e) {

					}
				}
			}
			if (nextScheduleTime != Long.MAX_VALUE) {
				scheduleNextMsgCheck(nextScheduleTime);
				nextScheduleTime = Long.MAX_VALUE;
			}
		}

	}
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_ADD_VIEW_COERFRAME :
					if (msg.obj != null) {
						mMsgManager.prepareZipRes((MessageHeadBean) msg.obj);
					}
					break;
				case MSG_PULL_NEW_THEME :
					doPullNewTheme();
					break;
				case MSG_UPLOAD_BASICINFO :
					String uid = GoStorePhoneStateUtil.getUid(mContext);
					//					StatisticsManager.getInstance(mContext).enableLog(true);
					boolean isnew = VersionControl.getFirstRun();
					StatisticsManager.getInstance(ApplicationProxy.getContext())
							.upLoadBasicInfoStaticData("1", uid, false, false, "-1", isnew);
					//使用新的SDK统计用户行为
					GuiThemeStatistics.updateLoadGoLauncherUserBehaviorStaticData();
					break;
				case MSG_UPLOAD_3D_STATE :
					mMsgManager.upload3DInstructStatistics();
					break;
				case MSG_INIT_GETJAR :
					initGetjar();
					break;
				default :
					break;
			}
		}

	};

	/**
	 * 用notification显示应用更新提示
	 * @param appsBean
	 * @author zhoujun
	 */
	private void sendUpdateInfoToNotification(AppsBean appsBean) {
		if (appsBean != null && !appsBean.mListBeans.isEmpty()) {
			//add by zhoujun  2010--0-14, 控制是否显示  
			if (appsBean.mControlcontrolMap != null && !appsBean.mControlcontrolMap.isEmpty()
					&& appsBean.mControlcontrolMap.get(1) == 0) {
				return;
			}
			List<AppBean> appBeans = appsBean.mListBeans;
			Resources res = mContext.getResources();
			NotificationManager notificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(AppsManagementConstants.NOTIFY_TAG,
					AppsManagementConstants.NOTIFY_UPDATE_INFO_ID);

			Intent intent = new Intent(ICustomAction.ACTION_APP_MANAGEMENT);

			//下载进度点击时，目前不做任何处理 
			//		intent.putExtra(AppsManagementConstants.APPS_MANAGEMENT_VIEW_KEY,
			//				AppsManageView.APPS_UPDATE_VIEW_ID);

			intent.putExtra(AppsManagementConstants.APPS_MANAGEMENT_START_TYPE_KEY,
					AppsManageView.APPS_START_TYPE);
			intent.putExtra(AppsManagementConstants.APPS_MANAGEMENT_ENTRANCE_KEY,
					MainViewGroup.ACCESS_FOR_UPDATE_NOTIFACTION);
			intent.putExtra(AppsManagementConstants.APPS_MANAGEMENT_SHOW_FRONTCOVER, false);
			//add by zzf 通知栏跳转应用更新界面 Intent 携带入口值
			intent.putExtra(RealTimeStatisticsContants.ENTRANCE_KEY,
					RealTimeStatisticsContants.AppgameEntrance.NOTIFICATION);

			PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);

			String updateInfo = appBeans.size() + " "
					+ res.getString(R.string.apps_management_notification_title_update_info_suffix);
			Notification notification = new Notification(R.drawable.notification_update_icon,
					updateInfo, System.currentTimeMillis());
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			PackageManager pkgMgr = mContext.getPackageManager();
			StringBuilder sb = new StringBuilder();
			for (AppBean appBean : appBeans) {
				sb.append(appBean.getAppName(pkgMgr)).append(", ");
			}
			sb.delete(sb.lastIndexOf(","), sb.length());
			notification.setLatestEventInfo(mContext, updateInfo, sb.toString(), pendingIntent);
			notificationManager.notify(AppsManagementConstants.NOTIFY_TAG,
					AppsManagementConstants.NOTIFY_UPDATE_INFO_ID, notification);
		}
	}
	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		// TODO Auto-generated method stub
		switch (msgId) {
			case MessageCenterActivity.GET_MSG_LIST_FINISH :
				boolean bool = ConvertUtils.int2boolean(param);
				if (bool) {
					if (mMsgManager.getUnreadedCnt() > 0) {
						checkViewType();
					}
				}
				break;
			default :
				break;
		}
	}

	private void sendUpdateMsgToAppFunc(ChannelConfig channelConfig, AppsBean appsBean) {
		//在8:00到23:00之间，则通知
		// LH TODO:这里是不是不需要了？发到2D的
		
		MsgMgrProxy.sendHandler(this, IDiyFrameIds.APP_DRAWER, IAppDrawerMsgId.EVENT_APPS_LIST_UPDATE,
				0, appsBean, null);
		if (!channelConfig.isNeedAppCenter()) {
			//如果本渠道没有应用中心，则要交给应用管理来处理
			//Add by wangzhuobin 2012.07.28
			MsgMgrProxy.sendHandler(null, IDiyFrameIds.APP_MANAGER,
					IAppDrawerMsgId.EVENT_APPS_LIST_UPDATE, 0, appsBean, null);
		}
	}

	private void sendUpdateMsgToNotification(ChannelConfig channelConfig, AppsBean appsBean) {
		// 在8:00到23:00之间，则通知
		if (!channelConfig.isNeedAppCenter()) {
			//如果本渠道没有应用中心，则要交给应用管理来处理
			//Add by wangzhuobin 2012.07.28
			MsgMgrProxy.sendHandler(null, IDiyFrameIds.APP_MANAGER,
					IAppManagerMsgId.EVENT_APPS_LIST_UPDATE_NOTIFICATION, 0, appsBean, null);
		} else {
			sendUpdateInfoToNotification(appsBean);
		}
	}

	/**
	 * 是否已经处理过一次应用程序更新消息
	 * @return
	 */
	private boolean getHandledAppUpdateMsg() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		boolean result = false;
		if (pref != null) {
			result = pref.getBoolean(PrefConst.KEY_FIRST_HANDLE_APPUPDATE_MSG,
					false);
		}
		return result;
	}

	private void setHandledAppUpdateMsg(boolean handled) {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		if (pref != null) {
			pref.putBoolean(PrefConst.KEY_FIRST_HANDLE_APPUPDATE_MSG, handled);
			pref.commit();
		}
	}
	/**
	 * <br>功能简述:检查是否有新的主题
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void doPullNewTheme() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					checkThemeNotify();
					new OnlineThemeGetter(mContext).getFeaturedThemeInfoBeans(ThemeManager
							.getInstance(mContext).getAllThemeInfosWithoutDefaultTheme(),
							ThemeConstants.LAUNCHER_FEATURED_THEME_ID, true, null);
					if (GoAppUtils.isGoLockerExist(mContext)) {
						new OnlineThemeGetter(mContext).getFeaturedThemeInfoBeans(LockerManager
								.getInstance(mContext).getInstallThemeInfoBean(),
								ThemeConstants.LOCKER_FEATURED_THEME_ID, true, null);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}.start();
	}
	
	

	/**
	 * <br>功能简述:获得当前HH:MM:SS
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private String getCurrentHMS() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			long t = System.currentTimeMillis();
			Date date = new Date(t);
			String result = sdf.format(date);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * <br>功能简述:时间dstTime是否在begin~end区间内
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param dstTime
	 * @param begin
	 * @param end
	 * @return
	 */
	private boolean isInTime(String dstTime, String begin, String end) {
		if (dstTime == null || begin == null || end == null
				|| (dstTime.compareTo(begin) >= 0 && dstTime.compareTo(end) < 0)) {
			return true;
		}
		return false;
	}

	/**
	 * <br>功能简述:启动下一次检查是否有主动显示型消息需要显示
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param nextTime
	 */
	private void scheduleNextMsgCheck(long nextTime) {
		try {
			final long tiggertTime = System.currentTimeMillis() + nextTime;
			Intent intent = new Intent(ICustomAction.ACTION_MSGCENTER_SHOWMSG);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_MSGCENTER_SHOWMSG, pendingIntent);
			intent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "scheduleNextMsgCheck error");
		}
	}

	/**
	 * <br>功能简述:定时移除罩子层视图
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param nextTime
	 */
	private void scheduleRemoveCoverMsg(long nextTime) {
		try {
			final long tiggertTime = System.currentTimeMillis() + nextTime;
			Intent intent = new Intent(ICustomAction.ACTION_MSGCENTER_REMOVEMSG);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_MSGCENTER_REMOVEMSG, pendingIntent);
			intent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "scheduleNextMsgCheck error");
		}
	}
	/**
	 * <br>功能简述:移除罩子层消息视图
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void removeCoverMsg() {
		if (mMsgManager != null) {
			mMsgManager.removeCoverFrameView();
		}
	}

	/**
	 * <br>功能简述:主题通知栏
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void checkThemeNotify() {
		ArrayList<ThemeNotifyBean> beans = ThemeManager.getInstance(mContext).getNotifyBean();
		if (beans != null && !beans.isEmpty()) {
			for (int i = 0; i < beans.size(); i++) {
				ThemeNotifyBean bean = beans.get(i);
				if (bean != null) {
					long stime = bean.getShowStatTime();
					long etime = bean.getShowEndTime();
					long now = System.currentTimeMillis();
					if (now >= stime && now <= etime) {
						PreferencesManager sp = new PreferencesManager(mContext,
								IPreferencesIds.FEATUREDTHEME_CONFIG, Context.MODE_PRIVATE);
						long lastShowTime = sp.getLong(IPreferencesIds.LAST_SHOW_NEWTHEME_STAMP, 0);
						if (lastShowTime > 0) {
							long diff = now - lastShowTime;
							if (diff < ONE_DAY) {
								ThemeManager.getInstance(mContext).removeNotifyBean(bean.getType());
								return;
							}
						}
						int type = bean.getType();
//						String id = null;
						String configId = null;
						if (bean.getType() == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
//							id = IPreferencesIds.HASSHOWFEATURENOTIFY;
							configId = IPreferencesIds.SHAREDPREFERENCES_MSG_THEME_NOTIFY_SHOW_STATICS_DATA;
						} else if (bean.getType() == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
//							id = IPreferencesIds.HASSHOWHOTNOTIFY;
							configId = IPreferencesIds.SHAREDPREFERENCES_MSG_THEME_NOTIFY_SHOW_STATICS_DATA;

						} else {
//							id = IPreferencesIds.HASSHOWLOCKERNOTIFY;
							configId = IPreferencesIds.SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_SHOW_STATICS_DATA;

						}
						sp.putLong(IPreferencesIds.LAST_SHOW_NEWTHEME_STAMP, now);
//						sp.putBoolean(id, true);
						sp.commit();
						new OnlineThemeGetter(mContext).sendAsynNewThemesNotification(type,
								bean.getShowContent(), bean.getShowIconUrl());
						PreferencesManager manager = new PreferencesManager(mContext, configId,
								Context.MODE_PRIVATE);
						int cnt = manager.getInt(configId, 0);
						manager.putInt(configId, cnt + 1);
						manager.commit();
						int staticsId = ThemeConstants.STATICS_ID_FEATURED_NOTIFY;
						if (type == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
							staticsId = ThemeConstants.STATICS_ID_LOCKER_NOTIFY;
						}
						mMsgManager.updateThemeNotifyStatisticsData(staticsId, 0, true);
						ThemeManager.getInstance(mContext).removeNotifyBean(bean.getType());

					} else if (stime > now) {
						scheduleNextThemeNotifyCheck(stime - now);
					}
				}
			}

		}
	}

	/**
	 * <br>功能简述:启动下一次检查是否有主动显示型消息需要显示
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param nextTime
	 */
	private void scheduleNextThemeNotifyCheck(long nextTime) {
		try {
			final long tiggertTime = System.currentTimeMillis() + nextTime;
			Intent intent = new Intent(ICustomAction.ACTION_CHECK_NEWTHEME_NOTIFY);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_CHECK_NEWTHEME_NOTIFY, pendingIntent);
			intent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "scheduleNextMsgCheck error");
		}
	}

	
	public void startDBBackUpTask() {
		try {
			PreferencesManager sharedPreferences = new PreferencesManager(
					mContext, IPreferencesIds.PREFERENCE_SETTING_CFG,
					Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);

			long firstCheckTime = 0L;
			firstCheckTime = sharedPreferences
					.getLong(
							IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_TIMESTAMP,
							0L);
			long now = System.currentTimeMillis();
			long nextCheckTime = 0L; // 下一次间隔时间
			if (firstCheckTime == 0 || ONE_DAY <= now - firstCheckTime) {
				if (firstCheckTime == 0
						&& DataProvider.getInstance(mContext).isNewDB()) {
					nextCheckTime = now + ONE_DAY; // 新DB没有必要备份
				} else {
					nextCheckTime = now;
				}
			} else {
				nextCheckTime = ONE_DAY + firstCheckTime;
			}
			Intent rateAppIntent = new Intent(
					ICustomAction.ACTION_AUTO_BACKUP_DB);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
					0, rateAppIntent, 0);
			mAlarmManager.set(AlarmManager.RTC_WAKEUP, nextCheckTime,
					pendingIntent);
			mPendingHashMap.put(ICustomAction.ACTION_AUTO_BACKUP_DB,
					pendingIntent);
			rateAppIntent = null;
			pendingIntent = null;
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "startDBBackUpDBTask error");
		}
	}
	
	/**
	 * 备份DB后的回调处理
	 * @param intent
	 */
	private void handleDBBackCB(Intent intent) {
		String result = intent.getStringExtra(BackUpSettingReceiver.BACKINFO);
		String path = intent.getStringExtra(BackUpSettingReceiver.BACKPATH);
		if (path != null && path.contains(LauncherEnv.Path.DB_AUTO_BACKUP_PATH)) { // 成功，记录时间启动下次定时器
			if (result != null
					&& result.equals(BackUpSettingReceiver.EXPORT_SUCCESS)) {
				PreferencesManager sharedPreferences = new PreferencesManager(
						mContext, IPreferencesIds.PREFERENCE_SETTING_CFG,
						Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
				sharedPreferences
						.putLong(
								IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_TIMESTAMP,
								System.currentTimeMillis());
				sharedPreferences.commit();
				startDBBackUpTask();
				mAutoBackUpReTryTimes = 0;
			} else {
				if (mAutoBackUpReTryTimes < MAX_AUTO_BACKUP_TRY_TIMES) { // 失败，未超过5次隔5s再尝试
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mAutoBackUpReTryTimes++;
							Intent bkIntent = new Intent(
									ICustomAction.ACTION_AUTO_BACKUP_DB);
							mContext.sendBroadcast(bkIntent);
						}
					}, 1000 * 5);
				} else { // 失败，超过5次，下个24小时再备份
					mAutoBackUpReTryTimes = 0;
					PreferencesManager sharedPreferences = new PreferencesManager(
							mContext, IPreferencesIds.PREFERENCE_SETTING_CFG,
							Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
					sharedPreferences
							.putLong(
									IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_TIMESTAMP,
									System.currentTimeMillis());
					sharedPreferences.commit();
					startDBBackUpTask();
				}
			}
		}
	}
	
	
	private void showRestoreDBDialog() {
		String backDB = LauncherEnv.Path.DB_AUTO_BACKUP_PATH + "/db"
				+ "/androidheart.db";
		File dbFile = new File(backDB);
		if (dbFile.exists()) {
			Intent intent = new Intent(mContext,
					RestoryBackupDialogActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}
	}
	
	private void initGetjar() {
		new Thread(ThreadName.GET_JAR_INIT) {
			public void run() {
				GetJarManager.buildInstance(new Intent(mContext,
						GoLauncher.class));
			};
		}.start();
	}
}
