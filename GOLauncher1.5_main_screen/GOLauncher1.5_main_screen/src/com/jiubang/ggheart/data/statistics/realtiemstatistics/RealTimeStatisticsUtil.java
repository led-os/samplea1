package com.jiubang.ggheart.data.statistics.realtiemstatistics;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.AppUtils;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.data.statistics.Statistics;

/**
 * 实时统计辅助类
 * 
 * @author zhouzefeng
 * 
 */
public class RealTimeStatisticsUtil {
	/**
	 * !!!提交和发包之前要写false
	 * 打开统计测试服务器开关,true:测试服务器
	 */
	public static boolean sDebugMode = false;
	/**
	 * 保存预安装的分割符
	 */
	private static final String STATISTICS_DATA_SEPARATE_ITEM = "#";
	/**
	 * 统计数据之间的分割符
	 */
	private static final String STATISTICS_ITEM_SEPARATE = "||";
	/**
	 * 缓存预安装包信息上传统计各字段的key值
	 */
	public static final int TARGET_ID = 0;
	public static final int ENTRANCE = TARGET_ID + 1;
	public static final int CATEGORY = ENTRANCE + 1;
	public static final int ACTION = CATEGORY + 1;
	public static final int POSITION = ACTION + 1;
	public static final int KEYWORD = POSITION + 1;
	
	/**
	 * 缓存预安装的信息
	 */
	private static Map<Integer, String> sMPreInstallMap = new HashMap<Integer, String>();

	/**
	 * 保存当前实时统计的入口值
	 * 
	 * @param entrance
	 */
	public static void saveCurrnetEntrance(Context context, int entrance) {
		PreferencesManager pm = new PreferencesManager(context,
				RealTimeStatisticsContants.REALTIME_STATISTICS_APPGAME_DATA,
				Context.MODE_PRIVATE);
		if (pm != null) {
			pm.putInt(
					RealTimeStatisticsContants.REALTIME_STATISTICS_CURENT_ENTARNCE_KEY,
					entrance);
			pm.commit();
		}
	}
	
	/**
	 * 搜索的基本统计
	 * @param context
	 * @param funid 	功能id
	 * @param entryid 	搜索入口id
	 * @param actionid 	行为id
	 * @param keyword	关键字
	 */
	public static void upLoadSearchBase(Context context, int funid, int entryid, String actionid, String keyword) {
		StringBuffer staticData = getSearchBaseData(context, funid, entryid, actionid, keyword);
		staticData.append("").append(STATISTICS_ITEM_SEPARATE);
		staticData.append("").append(STATISTICS_ITEM_SEPARATE);
		staticData.append("").append(STATISTICS_ITEM_SEPARATE);
		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadStaticData(staticData.toString());
	}

	private static StringBuffer getSearchBaseData(Context context, int funid, int entryid,
			String actionid, String keyword) {
		//日志序列||日志打印时间||Android ID||goid||国家||渠道||版本号||版本名||功能点ID||入口||关键字||操作代码||操作结果||应用id||包名||应用名称
		StringBuffer staticData = new StringBuffer();
		staticData.append("25").append(STATISTICS_ITEM_SEPARATE);
		staticData.append(getTime()).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Machine.getAndroidId(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(StatisticsManager.getGOID(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Statistics.getSimCountry(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Statistics.getUid(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(AppUtils.getVersionCodeByPkgName(context, "com.gau.go.launcherex")).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(context.getString(R.string.curVersion)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(funid).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(entryid).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(keyword).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(actionid).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(1).append(STATISTICS_ITEM_SEPARATE);
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		return staticData;
	}
	
	public static void upLoadAppDrawerSearch(String... params) {
		String keyWord = params.length > 1 ? params[1] : null;
		if (keyWord == null) {
			keyWord = "";
		}
		StringBuffer staticData = getSearchBaseData(ApplicationProxy.getContext(), 33, 4, params[0],
				keyWord);
		String appid = params.length > 2 ? params[2] : null;
		if (appid == null) {
			appid = "";
		}
		staticData.append(appid).append(STATISTICS_ITEM_SEPARATE);
		String pkgName = params.length > 3 ? params[3] : null;
		if (pkgName == null) {
			pkgName = "";
		}
		staticData.append(pkgName).append(STATISTICS_ITEM_SEPARATE);
		String appName = params.length > 4 ? params[4] : null;
		if (appName == null) {
			appName = "";
		}
		staticData.append(appName).append(STATISTICS_ITEM_SEPARATE);
		String haveWebResult = params.length > 5 ? params[5] : null;
		if (haveWebResult != null) {
			staticData.append(haveWebResult).append(STATISTICS_ITEM_SEPARATE);
		}
		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadStaticData(staticData.toString());
	}
	
	/**
	 * 统计搜索下载点击数
	 * @param context
	 * @param appId　			应用id
	 * @param operateCode		行为id
	 * @param pkgName			包名
	 * @param funid				功能id
	 * @param keyword			关键字
	 * @param searchEntryId		搜索入口id
	 */
	public static void upLoadSearchDownload(Context context, String appId,
			String operateCode, String pkgName, int funid, String keyword, String searchEntryId) {
		//日志序列||日志打印时间||Android ID||goid||国家||渠道||版本号||版本名||功能点ID||入口||关键字||操作代码||操作结果||应用id||包名||应用名称
		StringBuffer staticData = new StringBuffer();
		staticData.append("25").append(STATISTICS_ITEM_SEPARATE);
		staticData.append(getTime()).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Machine.getAndroidId(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(StatisticsManager.getGOID(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Statistics.getSimCountry(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Statistics.getUid(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(AppUtils.getVersionCodeByPkgName(context, "com.gau.go.launcherex")).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(context.getString(R.string.curVersion)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(funid).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(searchEntryId).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(keyword).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(operateCode).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(1).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(appId).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(pkgName).append(STATISTICS_ITEM_SEPARATE);
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadStaticData(staticData.toString());
		// 保存预安装信息，安装后判断是否上传统计
		RealTimeStatisticsUtil.saveInstallList(context, appId,
				operateCode, searchEntryId,
				String.valueOf(funid), pkgName, 0, keyword);
	}
	
	public static String getTime() {
		String time = "0";
		try {
			Calendar curDate = Calendar.getInstance();
			curDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			
			int year = curDate.get(Calendar.YEAR);
			int month = curDate.get(Calendar.MONTH) + 1;
			int day = curDate.get(Calendar.DAY_OF_MONTH);
			int hour = curDate.get(Calendar.HOUR_OF_DAY);
			int minute = curDate.get(Calendar.MINUTE);
			int sec = curDate.get(Calendar.SECOND);
			time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + sec;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return time;
	}

	/**
	 * 获取保存当前实时统计的入口值
	 * 
	 * @param entrance
	 */
	public static int getCurrnetEntrance(Context context) {
		int entrance = -1;
		PreferencesManager pm = new PreferencesManager(context,
				RealTimeStatisticsContants.REALTIME_STATISTICS_APPGAME_DATA,
				Context.MODE_PRIVATE);
		if (pm != null) {
			entrance = pm
					.getInt(RealTimeStatisticsContants.REALTIME_STATISTICS_CURENT_ENTARNCE_KEY,
							-1);
		}
		return entrance;
	}
	
	/**
	 * 统计详情点击
	 * @param context
	 * @param appId
	 * @param operateCode
	 * @param categoryId
	 * @param position
	 */
	public static void upLoadDetailClick(Context context, int appId, int funid,
			 String categoryId, int position) {
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		StatisticsManager.getInstance(context).upLoadBasicOptionStaticData(
				funid, String.valueOf(appId),
				RealTimeStatisticsContants.OPERATE_DETAIL_CLICK, RealTimeStatisticsContants.OPERATE_SUCCESS,
				Statistics.getUid(context),
				String.valueOf(getCurrnetEntrance(context)), categoryId,
				position);
	}

	/**
	 * 获取桌面渠道号的方法
	 * 
	 * @param context
	 * @return
	 */
//	public static String getUid(Context context) {
//		try {
//			String uid = null;
//			PreferencesManager sharedPreferences = new PreferencesManager(
//					context, IPreferencesIds.UID_CONFIG, Context.MODE_PRIVATE);
//			uid = sharedPreferences.getString(IPreferencesIds.UID_CONFIG_KEY,
//					"").trim();
//			if (!uid.equals("")) {
//				return uid;
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		final String defaultUid = "1";
//		// 从资源获取流
//		InputStream is = null;
//		is = context.getResources().openRawResource(R.raw.uid);
//		// 读取流内容
//		byte[] buffer = new byte[1024];
//		try {
//			int len = is.read(buffer);
//			if (len <= 0) {
//				// 避免文件为空，不能正常返回值
//				return defaultUid;
//			}
//			byte[] data = new byte[len];
//			for (int i = 0; i < len; i++) {
//				data[i] = buffer[i];
//			}
//			// 生成字符串
//			String dataStr = new String(data);
//			dataStr.trim();
//			if (data != null && dataStr.contains("\r\n")) {
//				// 去掉回车键
//				dataStr = dataStr.replaceAll("\r\n", "");
//			}
//			return dataStr;
//		} catch (IOException e) {
//			e.printStackTrace();
//			// IO异常
//		} finally {
//			try {
//				is.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return defaultUid;
//	}

	/**
	 * 上传下载(更新下载)，并缓存预安装信息 以便安装成功后，做安装统计
	 * 
	 * @param context
	 * @param functionId
	 * @param sender
	 * @param optionCode
	 * @param typeID
	 * @param position
	 * @param pkgName
	 */
	public static void upLoadDownloadStaticData(Context context,
			int functionId, String sender, String optionCode, String typeID,
			int position, String pkgName) {
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		// 上传下载或更新信息
		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadBasicOptionStaticData(
				functionId, sender, optionCode,
				RealTimeStatisticsContants.OPERATE_SUCCESS, Statistics.getUid(context),
				String.valueOf(getCurrnetEntrance(context)), typeID, position);
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		try {
			StatisticsManager.getInstance(ApplicationProxy.getContext()).addDownLoadRecord(pkgName, "1");
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("GoLauncherEX addDownLoadRecord error", e.toString());
		}
		
		// 保存预安装信息，安装后判断是否上传统计
		saveInstallList(context, sender, optionCode,
				String.valueOf(getCurrnetEntrance(context)), typeID, pkgName,
				position, "");
	}
	
	/**
	 * 上传搜索安装统计
	 * @param context
	 * @param appId　		应用id
	 * @param operateCode 	操作码
	 * @param pkgName　		包名
	 * @param funid　		功能id
	 * @param keyword 		关键字
	 * @param searchEntryId	搜索入口
	 */
	public static void upLoadSearchInstall(Context context, String appId,
			String operateCode, String pkgName, String funid, String keyword, String searchEntryId) {
		//日志序列||日志打印时间||Android ID||goid||国家||渠道||版本号||版本名||功能点ID||入口||关键字||操作代码||操作结果||应用id||包名||应用名称
		StringBuffer staticData = new StringBuffer();
		staticData.append("25").append(STATISTICS_ITEM_SEPARATE);
		staticData.append(getTime()).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Machine.getAndroidId(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(StatisticsManager.getGOID(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Statistics.getSimCountry(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(Statistics.getUid(context)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(AppUtils.getVersionCodeByPkgName(context, "com.gau.go.launcherex")).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(context.getString(R.string.curVersion)).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(funid).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(searchEntryId).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(keyword).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(operateCode).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(1).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(appId).append(STATISTICS_ITEM_SEPARATE);
		staticData.append(pkgName).append(STATISTICS_ITEM_SEPARATE);
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadStaticData(staticData.toString());
	}

	/**
	 * 下载或更新时候，保存预安装信息
	 * 
	 * @param context
	 * @param target
	 * @param entry
	 * @param categoryID
	 * @param pkgName
	 */
	public static void saveInstallList(Context context, String target,
			String action, String entry, String categoryID, String pkgName,
			int position, String keyword) {
		PreferencesManager pm = new PreferencesManager(context,
				RealTimeStatisticsContants.REALTIME_STATISTICS_APPGAME_DATA,
				Context.MODE_PRIVATE);
		String data = pm.getString(pkgName, "");
		
		if (data == null || data.equals("")) {
			StringBuffer sb = new StringBuffer();
			sb.append(target).append(STATISTICS_DATA_SEPARATE_ITEM);
			sb.append(entry).append(STATISTICS_DATA_SEPARATE_ITEM);
			sb.append(categoryID).append(STATISTICS_DATA_SEPARATE_ITEM);
			sb.append(String.valueOf(action)).append(STATISTICS_DATA_SEPARATE_ITEM);
			sb.append(String.valueOf(position)).append(STATISTICS_DATA_SEPARATE_ITEM);
			sb.append(String.valueOf(keyword));
			
			if (pm != null) {
				pm.putString(pkgName, sb.toString());
				pm.commit();
			}
		}
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
			sMPreInstallMap.put(TARGET_ID, data[0]);
			sMPreInstallMap.put(ENTRANCE, data[1]);
			sMPreInstallMap.put(CATEGORY, data[2]);
			sMPreInstallMap.put(ACTION, data[3]);
			sMPreInstallMap.put(POSITION, data[4]);
			if (data.length >= 6) {
				sMPreInstallMap.put(KEYWORD, data[5]);
			}
		}
		return flag;
	}

	/**
	 * 获取安装信息
	 * 
	 * @param context
	 * @param pkgName
	 * @return
	 */
	public static String[] getInstallData(Context context, String pkgName) {
		String[] datas = null;
		String strData = null;
		PreferencesManager pm = new PreferencesManager(context,
				RealTimeStatisticsContants.REALTIME_STATISTICS_APPGAME_DATA,
				Context.MODE_PRIVATE);
		if (pm != null) {
			strData = pm.getString(pkgName, "");
			// Log.v("zzf", "-pkgName-----"+pkgName+"--------"+strData);
			if (null != strData && 0 != strData.length()) {
				datas = strData.split(STATISTICS_DATA_SEPARATE_ITEM);
				pm.remove(pkgName);
				pm.commit();
			}
		}

		return datas;
	}

	/**
	 * 获取缓存的预安装包信息
	 * 
	 * @return
	 */
	public static Map<Integer, String> getPreHashMap() {
		if (sMPreInstallMap.size() < 1) {
			return null;
		}
		return sMPreInstallMap;
	}

	/**
	 * 判断是否有预安装信息 有的话上传安装（更新安装）统计
	 * 
	 * @param context
	 * @param packageName
	 */
	public static void upInstalledStaticData(Context context, String packageName) {
		// 测试服务器
		if (isPreInstallState(context, packageName)) {
			if (null != sMPreInstallMap) {
				String targetId = sMPreInstallMap
						.get(RealTimeStatisticsUtil.TARGET_ID);
				String stattisticsAction = sMPreInstallMap
						.get(RealTimeStatisticsUtil.ACTION);
				// 区分是更新安装 还是 下载 安装
				if (stattisticsAction
						.equals(RealTimeStatisticsContants.OPERATE_UPDATE_CLICK)) {
					stattisticsAction = RealTimeStatisticsContants.OPERATE_UPDATE_INSTALLED;
				} else {
					stattisticsAction = RealTimeStatisticsContants.OPERATE_DOWNLAOD_INSTALLED;
				}
				String entrance = sMPreInstallMap.get(ENTRANCE);
				String position = sMPreInstallMap.get(POSITION);
				String category = sMPreInstallMap.get(CATEGORY);
				String keyword = sMPreInstallMap.get(RealTimeStatisticsUtil.KEYWORD);
				
				if (keyword != null && !keyword.equals("")) {
					// 如果有关键字,说明是搜索安装,这时入口id作为搜索入口id,分类id作为功能id
					upLoadSearchInstall(context, targetId, stattisticsAction, packageName, category, keyword, entrance);
				} else {
					int mPostion = -1;
					if (position != null) {
						mPostion = Integer.valueOf(position);
					}
					if (sDebugMode) {
						StatisticsManager.getInstance(context).setDebugMode();
					}
					StatisticsManager.getInstance(ApplicationProxy.getContext())
							.upLoadBasicOptionStaticData(
									RealTimeStatisticsContants.FUN_ID_APPGAME,
									targetId, stattisticsAction,
									RealTimeStatisticsContants.OPERATE_SUCCESS,
									Statistics.getUid(context),
									String.valueOf(getCurrnetEntrance(context)),
									category, mPostion);
				}
			}
				
		}
	}

	/**
	 * 统计应用中心的登录
	 * 
	 * @param context
	 * @param entrance
	 */
	public static void upLoadLoginOptionStaticData(Context context, int entrance) {
		// 测试服务器
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		// 统计登录情况
		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadBasicOptionStaticData(
				RealTimeStatisticsContants.FUN_ID_APPGAME, null,
				RealTimeStatisticsContants.OPERATE_LOGIN,
				RealTimeStatisticsContants.OPERATE_SUCCESS, Statistics.getUid(context),
				String.valueOf(entrance), null, -1);
		// 保存当前登录的入口值
		saveCurrnetEntrance(context, entrance);
	}

	/**
	 * 统计Tab栏点击
	 * 
	 * @param context
	 * @param typeId
	 */
	public static void upLoadTabClickOptionStaticData(Context context,
			int typeId) {
		// 测试服务器
		if (sDebugMode) {
			StatisticsManager.getInstance(context).setDebugMode();
		}
		// tab栏统计
		StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadBasicOptionStaticData(
				RealTimeStatisticsContants.FUN_ID_APPGAME, null,
				RealTimeStatisticsContants.OPERATE_TAB_CLICK,
				RealTimeStatisticsContants.OPERATE_SUCCESS, Statistics.getUid(context),
				String.valueOf(getCurrnetEntrance(context)),
				String.valueOf(typeId), -1);
	}

}
