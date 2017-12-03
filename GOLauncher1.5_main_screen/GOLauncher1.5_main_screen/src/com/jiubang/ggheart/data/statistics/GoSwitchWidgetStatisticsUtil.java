package com.jiubang.ggheart.data.statistics;

import android.content.Context;
import android.text.TextUtils;
import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.go.util.AppUtils;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsContants;
import com.jiubang.ggheart.switchwidget.GoSwitchWidgetUtils;

/**
 * 内置Go开关Widget数据统计工具类
 * @author caoyaming
 *
 */
public class GoSwitchWidgetStatisticsUtil {
	//LOG_TAG
	private static final String LOG_TAG = GoSwitchWidgetStatisticsUtil.class.getName();
	//是否链接测试服务器   false:正式服务器   true:测试服务器
	public static final boolean DEBUG = false;
	//下载与安装成功的最大间隔时间(2小时)
	private static final long DOWNLOAD_TO_INSTALL_MAX_INTERVAL_TIME = 1 * 60 * 60 * 1000;
	//Go开关Widget统计数据保存在sharedpreference中的名称
	public static final String PREFERENCE_GOSWITCH_WIDGET = "preference_goswitch_widget";
	//拼装上传参数分隔符
	private static final String STATISTICS_DATA_SEPARATE_STRING = "||";
	//操作统计-日志序列
	public static final int OPERATION_LOG_SEQ = 41;
	//功能点ID--内置开关widget
	public static final int FUNCTION_ID = 114;
	
	//=========统计项的功能ID============
	//用户移除widget
	public static final String ACTION_CANCEL_WIDGET = "cancel_widget";	
	//用户点击wifi开关
	public static final String ACTION_CLICK_WIFI = "click_wifi";	
	//用户点击移动网络开关
	public static final String ACTION_CLICK_NETWORK = "click_network";	
	//用户点击铃声开关
	public static final String ACTION_CLICK_SOUND = "click_sound";	
	//用户点击亮度开关
	public static final String ACTION_CLICK_DISPLAY = "click_display";	
	//用户点击蓝牙按钮
	public static final String ACTION_CLICK_BLUETOOTH = "click_bluetooth";	
	//用户点击更多按钮
	public static final String ACTION_CLICK_MORE = "click_more";	
	//用户点击提示区域
	public static final String ACTION_CLICK_REMINDER = "click_reminder";	
	//用户通过提示引导，下载weight，并安装成功
	public static final String ACTION_DOWNLOAD_INSTALL = "b000";	
	
	//渠道号
	private static String sChannel = null;
	//当前应用版本号
	private static Integer sAppVersionCode = null;
	//当前应用版本名称
	private static String sAppVersionName = null;
	//Go Id
	private static String sGoId = null;
	/**
	 * 是否设置上传到测试服务器
	 * @param context
	 */
	public static void isDebug(Context context) {
		if (DEBUG) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
	}
	/**
	 * 用户移除widget
	 * @param context
	 */
	public static void uploadCancelWidgetStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CANCEL_WIDGET);
	}
	/**
	 * 用户点击wifi开关
	 * @param context
	 */
	public static void uploadClickWiFiStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CLICK_WIFI);
	}
	/**
	 * 用户点击移动网络开关
	 * @param context
	 */
	public static void uploadClickNetworkStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CLICK_NETWORK);
	}
	/**
	 * 用户点击铃声开关
	 * @param context
	 */
	public static void uploadClickSoundStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CLICK_SOUND);
	}
	/**
	 * 用户点击亮度开关
	 * @param context
	 */
	public static void uploadClickDisplayStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CLICK_DISPLAY);
	}
	/**
	 * 用户点击蓝牙开关
	 * @param context
	 */
	public static void uploadClickBluetoothStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CLICK_BLUETOOTH);
	}
	/**
	 * 用户点击更多按钮
	 * @param context
	 */
	public static void uploadClickMoreStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CLICK_MORE);
	}
	/**
	 * 用户点击提示区域
	 * @param context
	 */
	public static void uploadClickReminderStatistic(Context context) {
		isDebug(context);
		uploadOperationStatisticData(context, ACTION_CLICK_REMINDER);
		//判断Go开关Widget是否已安装
		if (!GoSwitchWidgetUtils.isInstallGoSwitchWidget(context)) {
			//未安装,保存点击时间.
			saveClickReminderTime(context);
		}
	}
	/**
	 * 用户通过提示引导,下载Widget,并安装成功
	 * @param context    
	 */
	public static void uploadDownloadInstallStatistic(Context context) {
		isDebug(context);
		//获取点击提示区域时间
		long clickReminderTime = getClickReminderTime(context);
		if (GoSwitchWidgetUtils.isInstallGoSwitchWidget(context) && clickReminderTime > 0 && (System.currentTimeMillis() - clickReminderTime) <= DOWNLOAD_TO_INSTALL_MAX_INTERVAL_TIME) {
			//如果点击提示区域的时间离当前时间在2小时内,则认为是点击了提示区域引导下载安装的，即上传安装统计数据.
			uploadOperationStatisticData(context, ACTION_DOWNLOAD_INSTALL);
		}
	}
	/**
	 * 上传用户操作行为统计数据
	 * 日志序列||Android ID||日志打印时间||功能点ID||统计对象||操作代码||操作结果||国家||渠道||版本号||版本名||入口||tab分类||位置||imei||goid||关联对象||备注 
	 * 
	 * @param context
	 * @param optionCode 操作代码
	 */
	private static void uploadOperationStatisticData(Context context, String optionCode) {
		if (context == null || TextUtils.isEmpty(optionCode)) {
			return;
		}
		StringBuffer buffer = new StringBuffer();
		//日志序列号
		buffer.append(OPERATION_LOG_SEQ);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//Android ID
		buffer.append(Machine.getAndroidId(context));
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//日志打印时间
		buffer.append(UtilTool.getBeiJinTime());
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//功能点ID
		buffer.append(FUNCTION_ID);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//统计对象(本次需求为空)
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//操作代码
		buffer.append(optionCode);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//操作结果-----0:未成功,1:成功(默认成功)
		buffer.append(RealTimeStatisticsContants.OPERATE_SUCCESS);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//国家
		buffer.append(RecommAppsUtils.local(context));
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//渠道
		if (sChannel == null) {
			sChannel = GoStorePhoneStateUtil.getUid(context);
			if (!TextUtils.isEmpty(sChannel)) {
				sChannel = sChannel.replaceAll("\r\n", "");
				sChannel = sChannel.replaceAll("\n", "");
			}
		}
		buffer.append(sChannel);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//版本号
		if (sAppVersionCode == null || sAppVersionCode <= 0 || TextUtils.isEmpty(sAppVersionName)) {
			//当前应用版本号
			sAppVersionCode = AppUtils.getVersionCodeByPkgName(context, context.getPackageName());
			//当前应用版本名称
			sAppVersionName = AppUtils.getVersionNameByPkgName(context, context.getPackageName());
		}
		buffer.append(sAppVersionCode);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//版本名
		buffer.append(sAppVersionName);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//入口-本次需求为空
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//tab分类--本次需求为空
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//位置--统计对象(AppID)的所在位置.-本次需求为空
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//IMEI
		buffer.append(GoStorePhoneStateUtil.getVirtualIMEI(context));
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//goid
		if (TextUtils.isEmpty(sGoId)) {
			sGoId = UtilTool.getGOId(context);
		}
		buffer.append(sGoId);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//关联对象(本次需求留空)
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//备注(广告id,上传服务器下发的广告id.)
		buffer.append(""); 
		UtilTool.log(LOG_TAG, buffer.toString());
		//上传统计数据
		StatisticsManager.getInstance(context).upLoadStaticData(buffer.toString());
	}
	/**
	 * 保存点击提示区域时间
	 * @param context
	 */
	public static void saveClickReminderTime(Context context) {
		//保存数据
		PreferencesManager pm = new PreferencesManager(context, PREFERENCE_GOSWITCH_WIDGET, Context.MODE_PRIVATE);
		pm.putLong("CLICK_REMINDER_TIME_KEY", System.currentTimeMillis());
		pm.commit();
	}
	/**
	 * 获取点击提示区域时间
	 * @param context
	 * @return
	 */
	public static Long getClickReminderTime(Context context) {
		PreferencesManager pm = new PreferencesManager(context, PREFERENCE_GOSWITCH_WIDGET, Context.MODE_PRIVATE);
		Long clickReminderTime = pm.getLong("CLICK_REMINDER_TIME_KEY", -1L);
		if (clickReminderTime > 0) {
			pm.remove("CLICK_REMINDER_TIME_KEY");
			pm.commit();
		}
		return clickReminderTime;
	}
}
