package com.jiubang.ggheart.data.statistics;

import android.content.Context;
import android.text.TextUtils;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.go.util.AppUtils;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;

/**
 * Go内置天气Widget数据统计工具类
 * @author wangzhuobin
 *
 */
public class GLGoWeatherStatisticsUtil {
	//LOG_TAG
	private static final String LOG_TAG = GLGoWeatherStatisticsUtil.class.getName();
	//是否链接测试服务器   false:正式服务器   true:测试服务器
	public static final boolean DEBUG = false;
	//拼装上传参数分隔符
	private static final String STATISTICS_DATA_SEPARATE_STRING = "||";
	//操作统计-日志序列
	public static final int OPERATION_LOG_SEQ = 41;
	//功能点ID
	public static final int FUNCTION_ID = 152;
	//统计包名
	public static final String STATISTICS_PACKGENAME = "com.gau.go.launcherex.gowidget.goweather";

	//=========操作代码============
	//城市编辑点击
	public static final String ACTION_EDIT_CITY = "edit_city";
	//主题商店点击
	public static final String ACTION_THEME_STORE = "theme_store";
	//24小时天气点击
	public static final String ACTION_HOURLY_FORECAST = "hourly_forecast";
	//未来天气
	public static final String ACTION_FUTURE_WEATHER = "future_weather";
	//下载完整版点击
	public static final String ACTION_DOWN_FULL_VERSION = "down_full_version";
	//弹框（下载）点击
	public static final String ACTION_DIALOG_DOWN = "a000";
	//弹框（取消）点击人数
	public static final String ACTION_DIALOG_CANCEL = "cancel";
	//添加（more）点击
	public static final String ACTION_EDIT_MORE = "more";
	//服务器请求成功
	public static final String ACTION_REQUEST_SUCCESS = "request_succ";
	//服务器请求失败
	public static final String ACTION_REQUEST_FAIL = "request_fail";
	//Widget点击(需要上传统计对象)
	public static final String ACTION_WIDGET_CLICK = "widget_click";
	//长按菜单->设置点击
	public static final String ACTION_MENU_SETTINGS = "menu_set";
	//长按菜单 ->主题点击
	public static final String ACTION_MENU_THEME = "menu_theme";

	//渠道号
	private static String sChannel = null;
	//当前应用版本号
	private static Integer sAppVersionCode = null;
	//当前应用版本名称
	private static String sAppVersionName = null;
	//Go Id
	private static String sGoId = null;

	/**
	 * 上传用户操作行为统计数据
	 * 日志序列||Android ID||日志打印时间||功能点ID||统计对象||操作代码||操作结果||国家||渠道||版本号||版本名||入口||tab分类||位置||imei||goid||关联对象||备注 
	 * 
	 * @param context
	 * @param sender 统计对象(操作对象的ID,例如应用ID、专区ID、链接)
	 * @param optionCode 操作代码
	 */

	public static void uploadOperationStatisticData(Context context, String optionCode) {
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
		//统计对象
		buffer.append(STATISTICS_PACKGENAME);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//操作代码
		buffer.append(optionCode);
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//操作次数（默认1）
		buffer.append(1);
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
		//入口（本次需求为空）
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//tab分类（本次需求为空）
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//位置--（本次需求为空）
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
		//关联对象（本次需求为空）
		buffer.append("");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//备注（本次需求为空）
		buffer.append("");
		UtilTool.log(LOG_TAG, buffer.toString());
		if (DEBUG) {
			StatisticsManager.getInstance(context).setDebugMode();
			StatisticsManager.getInstance(context).enableLog(true);
		}
		//上传统计数据
		StatisticsManager.getInstance(context).upLoadStaticData(buffer.toString());
	}
}
