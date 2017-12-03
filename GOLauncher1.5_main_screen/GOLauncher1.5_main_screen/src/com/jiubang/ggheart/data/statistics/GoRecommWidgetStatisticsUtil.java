package com.jiubang.ggheart.data.statistics;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.go.util.AppUtils;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.appgame.base.net.DownloadUtil;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsContants;

/**
 * Go推荐Widget数据统计工具类
 * @author caoyaming
 *
 */
@SuppressLint("UseSparseArrays")
public class GoRecommWidgetStatisticsUtil {
	//LOG_TAG
	private static final String LOG_TAG = GoRecommWidgetStatisticsUtil.class.getName();
	//是否链接测试服务器   false:正式服务器   true:测试服务器
	public static final boolean DEBUG = false;
	//下载与安装成功的最大间隔时间(2小时)
	private static final long DOWNLOAD_TO_INSTALL_MAX_INTERVAL_TIME = 1 * 60 * 60 * 1000;
	//Go推荐Widget统计数据保存在sharedpreference中的名称
	public static final String PREFERENCE_GORECOMM_WIDGET_STATISTICS = "preference_gorecomm_widget_statistics";
	//拼装上传参数分隔符
	private static final String STATISTICS_DATA_SEPARATE_STRING = "||";
	//操作统计-日志序列
	public static final int OPERATION_LOG_SEQ = 41;
	//功能点ID
	public static final int FUNCTION_ID = 100;
	
	//=========统计项的功能ID============
	//广告展示(桌面给用户展示广告时或者用户切换广告时上传)
	public static final String ACTION_DISPLAY_ADVERTISING = "f000";
	//专区点击(用户点击图片-跳专题专区)---暂不使用
	public static final String ACTION_CLICK_ZONE = "h000";
	//下载点击(跳单个主题或应用)
	public static final String ACTION_CLICK_DOWNLOAD = "a000";
	//url点击(用户点击图片)---跳网页
	public static final String ACTION_CLICK_URL = "url";
	//下载安装(用户在应用下载完成后,点击确认安装或者自动安装成功)
	public static final String ACTION_DOWNLOAD_INSTALL = "b000";	
	//渠道号
	private static String sChannel = null;
	//当前应用版本号
	private static Integer sAppVersionCode = null;
	//当前应用版本名称
	private static String sAppVersionName = null;
	//Go Id
	private static String sGoId = null;
	//保存统计数据的分割符
	private static final String STATISTICS_DATA_SEPARATE_ITEM = "#";
	//缓存预安装包信息key值
	public static final int SENDER = 0;
	public static final int PACKAGENAME = SENDER + 1;
	public static final int OPTION_CODE = PACKAGENAME + 1;
	public static final int ICBACK_URL = OPTION_CODE + 1;
	public static final int DOWNLOAD_TIME = ICBACK_URL + 1;
	private static Map<Integer, String> sMPreInstallMap = new HashMap<Integer, String>();
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
	 * 上传广告展示统计信息---桌面给用户展示广告时或者用户切换广告时上传
	 * @param context
	 * @param sender 统计对象ID---如果是Groupon数据,则该值填写Groupon Url
	 */
	public static void uploadDisplayAdvertisingStatistic(Context context, String sender) {
		if (TextUtils.isEmpty(sender)) {
			return;
		} 
		isDebug(context);
		uploadOperationStatisticData(context, sender, ACTION_DISPLAY_ADVERTISING);
	}
	
	/**
	 * 上传下载点击统计信息---跳单个主题或应用
	 * @param context
	 * @param sender 统计ID
	 */
	public static void uploadClickDownloadStatistic(Context context, String sender, String packageName, String cbackUrl, String iCbackUrl) {
		if (TextUtils.isEmpty(sender)) {
			return;
		} 
		isDebug(context);
		uploadOperationStatisticData(context, sender, ACTION_CLICK_DOWNLOAD);
		if (!TextUtils.isEmpty(packageName)) {
			//如果PackageName不为空,则说明是应用,保存预安装信息,安装后判断是否上传统计
			GoRecommWidgetStatisticsUtil.saveReadyInstallList(context, sender, packageName, ACTION_CLICK_DOWNLOAD, iCbackUrl);
		}
		if (!TextUtils.isEmpty(cbackUrl)) {
			//回调接口
			DownloadUtil.sendCBackUrl(cbackUrl + "&androidid=" + Machine.getAndroidId(context));
		}
	}
	/**
	 * 上传url点击统计信息---用户点击图片(跳网页),暂时是统计Groupon
	 * @param context
	 * @param sender 统计ID
	 */
	public static void uploadClickUrlStatistic(Context context, String sender) {
		if (TextUtils.isEmpty(sender)) {
			return;
		} 
		isDebug(context);
		uploadOperationStatisticData(context, sender, ACTION_CLICK_URL);
	}
	/**
	 * 上传下载安装统计信息---用户在应用下载完成后,点击确认安装或者自动安装成功
	 * @param context
	 * @param sender 统计ID
	 */
	public static void uploadDownloadInstallStatistic(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return;
		} 
		isDebug(context);
		if (isPreInstallState(context, packageName) && sMPreInstallMap != null && !TextUtils.isEmpty(sMPreInstallMap.get(SENDER))) {
			//获取下载的时间,如果下载时间离安装时间超过2小时,则不认为是通过Go推荐下载安装的.
			long downloadTime = -1;
			if (TextUtils.isEmpty(sMPreInstallMap.get(DOWNLOAD_TIME))) {
				return;
			}
			try {
				downloadTime = Long.parseLong(sMPreInstallMap.get(DOWNLOAD_TIME));
			} catch (Exception e) {
				downloadTime = -1;
			}
			//如果在有效的时间内安装,则认为是通过GO推荐Widget下载安装的,上传安装统计
			if (System.currentTimeMillis() - downloadTime <= DOWNLOAD_TO_INSTALL_MAX_INTERVAL_TIME) {
				//下载安装
				uploadOperationStatisticData(context, sMPreInstallMap.get(SENDER), ACTION_DOWNLOAD_INSTALL);
				//判断回调地址是否为空,如果不为空,则回调.
				if (!TextUtils.isEmpty(sMPreInstallMap.get(ICBACK_URL))) {
					//回调接口
					DownloadUtil.sendCBackUrl(sMPreInstallMap.get(ICBACK_URL) + "&ts=" + System.currentTimeMillis());
				}
			}
		}
	}
	/**
	 * 上传用户操作行为统计数据
	 * 日志序列||Android ID||日志打印时间||功能点ID||统计对象||操作代码||操作结果||国家||渠道||版本号||版本名||入口||tab分类||位置||imei||goid||关联对象||备注 
	 * 
	 * @param context
	 * @param sender 统计对象(操作对象的ID,例如应用ID、专区ID、链接)
	 * @param optionCode 操作代码
	 */
	private static void uploadOperationStatisticData(Context context, String sender, String optionCode) {
		if (context == null || TextUtils.isEmpty(sender) || TextUtils.isEmpty(optionCode)) {
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
		buffer.append(sender);
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
		//入口-暂时为空
		buffer.append("0");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//tab分类
		buffer.append("0");
		buffer.append(STATISTICS_DATA_SEPARATE_STRING);
		//位置--统计对象(AppID)的所在位置.-暂时为0
		buffer.append("0");
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
	 * 保存预安装信息
	 * 
	 * @param context
	 * @param sender 统计对象Id
	 * @param packageName 应用包名
	 * @param optionCode 统计操作码
	 */
	public static void saveReadyInstallList(Context context, String sender, String packageName, String optionCode, String icbackUrl) {
		StringBuffer sb = new StringBuffer();
		sb.append(sender).append(STATISTICS_DATA_SEPARATE_ITEM);
		sb.append(packageName).append(STATISTICS_DATA_SEPARATE_ITEM);
		sb.append(optionCode).append(STATISTICS_DATA_SEPARATE_ITEM);
		sb.append(icbackUrl).append(STATISTICS_DATA_SEPARATE_ITEM); //安装回调地址
		sb.append(System.currentTimeMillis()); //进入下载页的时间
		//保存数据
		PreferencesManager pm = new PreferencesManager(context, PREFERENCE_GORECOMM_WIDGET_STATISTICS, Context.MODE_PRIVATE);
		pm.putString(packageName, sb.toString());
		pm.commit();
	}
	/**
	 * 获取安装信息
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static String[] getInstallData(Context context, String packageName) {
		String[] datas = null;
		String strData = null;
		PreferencesManager pm = new PreferencesManager(context, PREFERENCE_GORECOMM_WIDGET_STATISTICS, Context.MODE_PRIVATE);
		strData = pm.getString(packageName, "");
		if (null != strData && 0 != strData.length()) {
			datas = strData.split(STATISTICS_DATA_SEPARATE_ITEM);
			pm.remove(packageName);
			pm.commit();
		}
		return datas;
	}
	/**
	 * 是否是预安装状态
	 * 
	 * @param context
	 * @param pkgName
	 * @return
	 */
	public static boolean isPreInstallState(Context context, String pkgName) {
		boolean flag = false;
		String[] data = getInstallData(context, pkgName);
		if (null != data && data.length > 1) {
			flag = true;
			// 清除 缓存数据
			sMPreInstallMap.clear();
			sMPreInstallMap.put(SENDER, data[0]);
			sMPreInstallMap.put(PACKAGENAME, data[1]);
			sMPreInstallMap.put(OPTION_CODE, data[2]);
			sMPreInstallMap.put(ICBACK_URL, data[3]);
			sMPreInstallMap.put(DOWNLOAD_TIME, data[4]);
		}
		return flag;
	}
}
