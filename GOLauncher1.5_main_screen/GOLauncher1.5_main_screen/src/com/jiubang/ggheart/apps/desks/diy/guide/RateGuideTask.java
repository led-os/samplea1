package com.jiubang.ggheart.apps.desks.diy.guide;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.go.util.log.LogConstants;
import com.go.util.market.MarketConstant;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.RateDialogContentActivity;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 处理评分引导的业务
 * @author zouguiquan
 *
 */
public class RateGuideTask {
	
	private static final String TAG = "RateGuideTask";
	private boolean mDebug = true;

	private static RateGuideTask sInstance;
	private AlarmManager mAlarmManager;
	private Context mContext;
	
	public static final int EVENT_UNKONW = 0;
	public static final int EVENT_GUIDEFRAME = 1;
	public static final int EVENT_RUNNING_CLEAN = 2;
	public static final int EVENT_RECENTLY_CLEAN = 3;
	public static final int EVENT_APPLY_THEME = 4;
	
	private static final long NINE_SECOND_MILLSECOND = 12 * 1000;
	private static final long THREE_SECOND_MILLSECOND = 3 * 1000;
	
	// 4.12需求，两次评分引导时间间隔最小值
	public static final long REGUIDE_TIME_DELAY = 1000 * 60 * 60 * 24 * 7 * 3;
	// 4.12需求， 只针对以下高分国家进行二次评分引导
	public static final String[] AVAILABLE_COUNTRYS_REGUIDE = new String[] { "mx", //墨西哥
			"au", //澳大利亚
			"de", //德国
			"fr", //法国
			"it", //意大利
			"ph", //菲律宾
			"us", //美国
			"ru"  //俄罗斯
	};
	// 评分功能被屏蔽的国际或地区
	public static final String[] SHIELDED_COUNTRYS = new String[] { "kr", //韩国
		"br", //巴西
		"tw" //台湾
	};
	
	public static RateGuideTask getInstacne(Context context) {
		if (sInstance == null) {
			sInstance = new RateGuideTask(context);
		}
		return sInstance;
	}
	
	private RateGuideTask(Context context) {
		mContext = context;
		mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	/**
	 * <br>功能简述: 定时任务，用于启动评分引导
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param event
	 * @param params
	 */
	public void scheduleShowRateDialog(int event, String... params) {
		
		if (mDebug) {
			Log.d(TAG, "showRateDialog event = " + event);
		}
		// 韩国、台湾、巴西不弹评分弹框
		if (!isInCountryShielded()) {
			try {

				//使用新的sharepreferences，DESK_SHAREPREFERENCES_FILE恢复默认或一些操作后会被clear。
				//DESK_SHAREPREFERENCES_FILE暂时还保留，迭代几个版本后去掉
				PreferencesManager ratePreferences = new PreferencesManager(mContext,
						IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
				boolean hasShow = ratePreferences.getBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG,
						false);
				
				if (mDebug) {
					Log.d(TAG, "---showRateDialog hasShow = " + hasShow);
				}
				
				if (hasShow) {
					return;
				}
				
				long tiggerTime = 0;
				long now = System.currentTimeMillis();
				if (event == EVENT_APPLY_THEME) {
					tiggerTime = now + NINE_SECOND_MILLSECOND;
				} else if (event == EVENT_RUNNING_CLEAN || event == EVENT_RECENTLY_CLEAN) {
					tiggerTime = now + THREE_SECOND_MILLSECOND;
				}
				
				if (tiggerTime > 0) {
					Intent rateAppIntent = new Intent(ICustomAction.ACTION_SHOW_RATE_DIALOG);
					rateAppIntent.putExtra(RateDialogContentActivity.EXTRA_EVENT, event);
					rateAppIntent.putExtra(RateDialogContentActivity.EXTRA_PARAMS, params);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
							rateAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggerTime, pendingIntent);
					rateAppIntent = null;
					pendingIntent = null;
				}
			} catch (Exception e) {
				Log.i(LogConstants.HEART_TAG, "showRateDialog error");
			}
		}
	}
	
	/**
	 * <br>功能简述: 显示评分引导弹框
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param event
	 * @param params
	 */
	public void showRateDialog(Intent intent) {
		long now = System.currentTimeMillis();
		PreferencesManager preferencesManager = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		// ----------------将shareprenece转移到PREFERENCE_RATE_CONFIG上----------------//
		PreferencesManager ratePreferences = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
		boolean hasShow = ratePreferences.getBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG, false);
		if (hasShow) {
			return;
		}
		boolean isGooglePlayExist = GoAppUtils.isMarketExist(mContext);
		if (!isGooglePlayExist) {
			return;
		}
		// -------------------------------------------------------------------------//
		boolean remind_rate = ratePreferences.getBoolean(IPreferencesIds.REMIND_RATE, true);
		boolean rate_guideframe = ratePreferences.getBoolean(IPreferencesIds.RATE_GUIDEFRAME, false);
		// boolean launcherIsTop =
		// Machine.isTopActivity(GOLauncherApp.getContext(),
		// LauncherEnv.PACKAGE_NAME);
		boolean launcherIsTop = GoLauncherActivityProxy.isActivity();

		boolean isNeedShowRate = false; // golauncher resume时查看
		boolean showDialog = false;

		long lastRateShowTimeMsc = ratePreferences.getLong(IPreferencesIds.RATE_LAST_SHOW_TIME, -1);
		boolean isTimeRight = false;
		// 判断评分引导时机是否成熟：
		// 1.上次评分引导时间未记录（默认为是从未弹过评分引导的升级用户 或者 新用户）
		// 2.上次评分引导时间有记录(默认为是进行二次评分引导)，与当前时间差值大于限定值
		// 3.满足1或者2，并且用户当前国家在指定国家范围内（4.12需求）（4.15需求，去掉二次评分引导的国家限制）
		if (lastRateShowTimeMsc == -1
				|| (now - lastRateShowTimeMsc >= RateGuideTask.REGUIDE_TIME_DELAY)) {
			isTimeRight = true;
		}
		try {
			if (remind_rate && isTimeRight && !rate_guideframe) {
				// 桌面在前端并且网络可用，则立即弹出评分引导框，否则，延后处理
				if (launcherIsTop && Machine.isNetworkOK(mContext)) {
					showDialog = true;
					Intent newIntent = new Intent(ApplicationProxy.getContext(),
							RateDialogContentActivity.class);
					newIntent.fillIn(intent, Intent.FILL_IN_DATA);
					newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					GoLauncherActivityProxy.getActivity().startActivity(newIntent);
					// 注释下面不再提醒
					// // 启动下一次定时弹出评分对话框
					// scheduleNextRate(SHOW_RATE_DIALOG);
				} else {
					// 当前桌面不在顶层，下次启动桌面一份钟后再弹
					isNeedShowRate = true;
					// 注释下面不再提醒
					// scheduleNextRate(SHOW_RATE_DIALOG);
				}
			}
		} finally {
			// *********************************************************************************/
			if (showDialog) {
				ratePreferences.putBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG, showDialog);
				ratePreferences.putLong(IPreferencesIds.RATE_LAST_SHOW_TIME,
						System.currentTimeMillis());
			}
			int event = intent.getIntExtra(RateDialogContentActivity.EXTRA_EVENT,
					RateGuideTask.EVENT_UNKONW);
			String[] params = intent.getStringArrayExtra(RateDialogContentActivity.EXTRA_PARAMS);
			String memCleaned = (params != null && params.length > 0)
					? params[0]
					: RateDialogContentActivity.MEM_CLEANED_DEFAULT;

			ratePreferences.putInt(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG_EXTRA_EVENT, event);
			ratePreferences.putString(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG_EXTRA_MEMCLEANED,
					memCleaned);
			ratePreferences.putBoolean(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG, isNeedShowRate);
			ratePreferences.commit();
			// *********************************************************************************//
			preferencesManager.putInt(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG_EXTRA_EVENT, event);
			preferencesManager.putString(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG_EXTRA_MEMCLEANED,
					memCleaned);
			preferencesManager.putBoolean(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG, isNeedShowRate);
			preferencesManager.commit();
		}
	}

	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void sendMail() {
		Context context = mContext.getApplicationContext();
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String[] receiver = new String[] { "golauncher@goforandroid.com" };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receiver);

		String suggestionForMailString = context.getResources().getString(
				R.string.feedback_select_type_suggestion_for_mail);
	
		String subject = "GO Launcher EX(v" + context.getString(R.string.curVersion)
				+ ") Feedback(" + suggestionForMailString + ")";
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		String content = context.getString(R.string.rate_go_launcher_mail_content) + "\n\n";
		StringBuffer body = new StringBuffer(content);
		body.append("\nProduct=" + android.os.Build.PRODUCT);
		body.append("\nPhoneModel=" + android.os.Build.MODEL);
		body.append("\nROM=" + android.os.Build.DISPLAY);
		body.append("\nBoard=" + android.os.Build.BOARD);
		body.append("\nDevice=" + android.os.Build.DEVICE);
		body.append("\nDensity="
				+ String.valueOf(context.getResources().getDisplayMetrics().density));
		body.append("\nPackageName=" + context.getPackageName());
		body.append("\nAndroidVersion=" + android.os.Build.VERSION.RELEASE);
		body.append("\nTotalMemSize="
				+ (AppUtils.getTotalInternalMemorySize() / 1024 / 1024) + "MB");
		body.append("\nFreeMemSize="
				+ (AppUtils.getAvailableInternalMemorySize() / 1024 / 1024) + "MB");
		body.append("\nRom App Heap Size="
				+ Integer.toString((int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L))
				+ "MB");
		emailIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
		emailIntent.setType("plain/text");
		try {
			context.startActivity(emailIntent);
		} catch (Exception e) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://golauncher.goforandroid.com"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClassName("com.android.browser",
					"com.android.browser.BrowserActivity");
			context.startActivity(intent);
		}
	}
	
	/**
	 * <br>功能简述: 通讯统计入口
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param eventType 触发评分引导事件类型
	 * @param isSecondRate 是否为二次评分引导
	 * @param isCancelRate 用户是否点击取消评分
	 */
	public void rateStatistics(int eventType, boolean isSecondRate, boolean isCancelRate) {
		//TODO 统计入口
		String rateTimes = isSecondRate ? "2" : "1";
		String optionCode = isCancelRate ? "cancel" : "score";
		String entrance = getStatisticsEntrance(eventType);
		GuiThemeStatistics.ratingDialogStaticData(optionCode, entrance, rateTimes);
	}


	/**
	 * <br>功能简述: 根据事件类型获取统计入口标识
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param eventType
	 * @return
	 */
	public String getStatisticsEntrance(int eventType) {
		String entrance = "";
		switch (eventType) {
			case RateGuideTask.EVENT_GUIDEFRAME:
				entrance = "6";
				break;
			case RateGuideTask.EVENT_RUNNING_CLEAN :
				entrance = "2";
				break;
			case RateGuideTask.EVENT_RECENTLY_CLEAN :
				entrance = "3";
				break;
			case RateGuideTask.EVENT_APPLY_THEME :
				entrance = "4";
				break;
			case RateGuideTask.EVENT_UNKONW :
				entrance = "5";
			default :
				break;
		}
		return entrance;
	}
	
	/**
	 * <br>功能简述: 检查上一个版本是否弹过评分引导弹框
	 * <br>功能详细描述: 用于判断当前弹框是否为再次弹框
	 * <br>注意: 
	 * @return
	 */
	public boolean hasShowLastVersion() {
		PreferencesManager ratePreferences = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
		return ratePreferences.getBoolean(IPreferencesIds.HAS_SHOW_RATE_DIALOG_LASTVERSION, false);
	}
	
	/**
	 * <br>功能简述: 由版本更新页触发的评分操作
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param mEvent
	 */
	public void rateFromeGuideFrame(){
		directGooglePlayRate();
		PreferencesManager ratePreferences = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
		ratePreferences.putBoolean(IPreferencesIds.RATE_GUIDEFRAME, true);
		ratePreferences.commit();
		//TODO 数据统计，待优化
		rateStatistics(EVENT_GUIDEFRAME, hasShowLastVersion(), false);
	}
	
	/**
	 * <br>功能简述: 版本更新页显示时候调用的统计接口
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void guideStatistics() {
		//TODO 次数统计细节待确认
		String rateTimes = hasShowLastVersion() ? "2" : "1";
		String optionCode = "guide";
		String entrance = getStatisticsEntrance(EVENT_GUIDEFRAME);
		GuiThemeStatistics.ratingDialogStaticData(optionCode, entrance, rateTimes);
	}
	
	/**
	 * <br>功能简述: 当前是否可以进行评分引导
	 * <br>功能详细描述: 外部调用，例如更新引导页
	 * <br>注意:
	 * @return
	 */
	public boolean isRateEnvOk(){
		return !isInCountryShielded() && Machine.isNetworkOK(mContext) && GoAppUtils.isMarketExist(mContext);
	}
	
	/**
	 * <br>功能简述: 当前国家或地区是否在屏蔽范围之内
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private boolean isInCountryShielded(){
		boolean retval = false;
		String country = Machine.getCountry(mContext);
		for(String code : SHIELDED_COUNTRYS) {
			if (code.equals(country)) {
				retval = true;
				break;
			}
		}
		return retval;
	}
	
	/**
	 * <br>功能简述: 导向电子市场进行评分操作
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void directGooglePlayRate() {
		String packageName = mContext.getApplicationInfo().packageName;
		if (GoAppUtils.isMarketExist(mContext)) {
			GoAppUtils.gotoMarket(mContext, MarketConstant.APP_DETAIL
					+ packageName);
		} else {
			AppUtils.gotoBrowser(mContext, MarketConstant.APP_DETAIL + packageName);
		}
	}
	
	/**
	 * <br>功能简述: 检查未被执行的评分弹框任务
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void checkUnExcutedRate() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				PreferencesManager preferencesManager = new PreferencesManager(mContext,
						IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
				boolean needToShowRate = preferencesManager.getBoolean(
						IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG, false);
				AlarmManager alarmManager = null;
				if (needToShowRate) {
					int event = preferencesManager.getInt(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG_EXTRA_EVENT, RateGuideTask.EVENT_UNKONW);
					String memCleaned = preferencesManager.getString(IPreferencesIds.IS_NEED_SHOW_RATE_DIALOG_EXTRA_MEMCLEANED, RateDialogContentActivity.MEM_CLEANED_DEFAULT);
					alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
					Intent rateAppIntent = new Intent(ICustomAction.ACTION_SHOW_RATE_DIALOG);
					rateAppIntent.putExtra(RateDialogContentActivity.EXTRA_EVENT, event);
					if (event == RateGuideTask.EVENT_RUNNING_CLEAN) {
						rateAppIntent.putExtra(RateDialogContentActivity.EXTRA_PARAMS, new String[]{memCleaned});
					}
					PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
							rateAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					alarmManager.set(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis() + 10 * 1000, pendingIntent);
				}
			}

		}.start();
	}
}
