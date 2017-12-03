package com.jiubang.ggheart.themeicon;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 数据提供者，对外提供主题下发相关所有数据
 * @author caoyaming
 *
 */
public class ThemeIconDataProvider {
	//下发状态
	public static final int ISSUED_STATE_IDLE = 0x00;
	public static final int ISSUED_STATE_ING = 0x01;
	public static final int ISSUED_STATE_FAILED = 0x02;
	public static final int ISSUED_STATE_SUCCESS = 0x03;
	public static final int ISSUED_STATE_FORBID = 0x04;

	//sharedPreference文件名
	private static final String SP_FILENAME = "themeicon";
	//是否已经通知过用户更新成功
	private static final String SP_KEY_HAS_NOTIFIED = "sp_key_has_notified_";
	//主题资源下发状态标识key
	private static final String SP_KEY_THEME_ISSUED_STATE = "sp_key_theme_issued_state_";
	//最后一次成功更新Icon时间的Key
	private static final String SP_KEY_TIME_NEXT_REQUEST_REFRESH = "sp_key_time_next_request_refresh_";
	//上次成功更新数据列表时间戳的Key
	private static final String SP_KEY_TIME_LAST_UPDATE_DATALIST_SUCCESS = "sp_time_last_update_datalist_success_";
	//是否需要重新请求数据列表
	private static final String SP_KEY_NEED_RECONNECT_DATALIST = "sp_key_need_reconnect_datalist_";
	//下载环境正常情况下，请求数据列表失败重连次数
	private static final String SP_KEY_RECONNECTION_TIMES_REQUEST_DATALIST = "sp_key_reconnection_times_request_datalist_";
	//是否存在等待中的通知（唯一的通知，用于提示用户手动替换新主题资源）
	private static final String SP_KEY_HAS_NOTIFICATION_WAITING = "sp_key_has_notification_waiting_";

	//Context 
	private Context mContext;
	//SharedPreferences
	private SharedPreferences mSharedPreferences;
	//下载状态缓存值， key：主题包名 value：下载状态值
	private HashMap<String, Integer> mDldStateMap = new HashMap<String, Integer>();
	//当前类对象
	private static ThemeIconDataProvider sSelf;

	private ThemeIconDataProvider(Context context) {
		mContext = context;
		mSharedPreferences = mContext.getSharedPreferences(SP_FILENAME, Context.MODE_PRIVATE);
	}
	/**
	 * 获取当前类单例对象
	 * @return
	 */
	public static ThemeIconDataProvider getInstance(Context context) {
		if (sSelf == null) {
			sSelf = new ThemeIconDataProvider(context);
		}
		return sSelf;
	}

	/**
	 * 获取当前主题下一次向服务器请求刷新的时间
	 * @param themePackageName 主题包名
	 * @return 时间戳(毫秒数)
	 */
	long getTimeNextRequestRefresh(String themePackageName) {
		return mSharedPreferences.getLong(SP_KEY_TIME_NEXT_REQUEST_REFRESH + themePackageName, 0L);
	}

	/**
	 * <br>功能简述:保存当前主题下一次向服务器请求刷新的时间
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackageName 主题包名
	 * @param timeMsc 时间值（单位毫秒）
	 */
	void setTimeNextRequestRefresh(String themePackageName, long timeMsc) {
		mSharedPreferences.edit()
				.putLong(SP_KEY_TIME_NEXT_REQUEST_REFRESH + themePackageName, timeMsc).commit();
	}

	/**
	 * <br>功能简述: 上次更新数据列表的时间戳
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackageName
	 * @return
	 */
	long getTimeLastUpdateDatalistSuccess(String themePackageName) {
		return mSharedPreferences.getLong(SP_KEY_TIME_LAST_UPDATE_DATALIST_SUCCESS
				+ themePackageName, 0);
	}

	/**
	 * <br>功能简述: 上次更新数据列表的时间戳
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackageName
	 * @param timeMsc
	 */
	void setTimeLastUpdateDatalistSucess(String themePackageName, long timeMsc) {
		mSharedPreferences.edit()
				.putLong(SP_KEY_TIME_LAST_UPDATE_DATALIST_SUCCESS + themePackageName, timeMsc)
				.commit();
	}

	/**
	 * <br>功能简述: 获取数据库中主题资源下载列表
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePkgName
	 * @return
	 */
	ArrayList<ThemeIconDataBean> getThemeIconDataList(String themePkgName, String componentName) {
		ThemeIconDataBean whereThemeIconDataBean = new ThemeIconDataBean();
		whereThemeIconDataBean.mThemePackageName = themePkgName;
		whereThemeIconDataBean.mComponentName = componentName;
		ArrayList<ThemeIconDataBean> themeIconDataList = ThemeIconDataModel.getInstance(mContext)
				.queryThemeIconData(whereThemeIconDataBean);
		return themeIconDataList;
	}

	/**
	 * <br>功能简述: 获取下载环境正常情况下请求数据列表的失败重连次数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackageName
	 * @return
	 */
	int getReconnectionTimesRequestDataList(String themePackageName) {
		return mSharedPreferences.getInt(SP_KEY_RECONNECTION_TIMES_REQUEST_DATALIST + themePackageName, 0);
	}

	/**
	 * <br>功能简述:记录下载环境正常情况下的请求数据列表失败重连次数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackageName
	 * @param times
	 */
	void setReconnectionTimesRequestDataList(String themePackageName, int times) {
		mSharedPreferences.edit().putInt(SP_KEY_RECONNECTION_TIMES_REQUEST_DATALIST + themePackageName, times)
				.commit();
	}
	
	/**
	 * <br>功能简述: 是否需要重新请求数据列表
	 * <br>功能详细描述: 用于上次异常重连
	 * <br>注意:
	 * @param themePackageName
	 */
	boolean getNeedReconnectDatalist(String themePackageName) {
		return mSharedPreferences.getBoolean(SP_KEY_NEED_RECONNECT_DATALIST + themePackageName, false);
	}

	/**
	 * <br>功能简述: 记录是否需要重新请求数据列表
	 * <br>功能详细描述: 用于上次异常重连
	 * <br>注意:
	 * @param themePackagename
	 * @param value
	 */
	void setNeedReconnectDatalist(String themePackagename, boolean value) {
		mSharedPreferences.edit()
				.putBoolean(SP_KEY_NEED_RECONNECT_DATALIST + themePackagename, value).commit();
	}
	
	/**
	 * <br>功能简述: 是否存在等待中的通知
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackagename
	 * @return
	 */
	boolean getHasNotificationWaiting(String themePackagename) {
		return mSharedPreferences.getBoolean(SP_KEY_HAS_NOTIFICATION_WAITING + themePackagename,
				false);
	}

	/**
	 * <br>功能简述: 缓存等待中的通知
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePackagename
	 * @param value
	 */
	void setHasNotificationWaiting(String themePackagename, boolean value) {
		mSharedPreferences.edit()
				.putBoolean(SP_KEY_HAS_NOTIFICATION_WAITING + themePackagename, value).commit();
	}

	/**
	 * <br>功能简述: 获取对应主题图标资源的下载状态
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePkgName
	 * @return
	 */
	synchronized int getIssuedState(String themePkgName) {
		int state = ISSUED_STATE_IDLE;
		if (mDldStateMap.get(themePkgName) == null) {
			state = mSharedPreferences
					.getInt(SP_KEY_THEME_ISSUED_STATE + themePkgName, ISSUED_STATE_IDLE);
			mDldStateMap.put(themePkgName, state);
		} else {
			state = mDldStateMap.get(themePkgName);
		}
		return state;
	}

	/**
	 * <br>功能简述: 保存对应主题图标资源的下载状态
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePkgName
	 * @param state
	 */
	synchronized void setIssuedState(String themePkgName, int state) {
		mDldStateMap.put(themePkgName, state);
		mSharedPreferences.edit().putInt(SP_KEY_THEME_ISSUED_STATE + themePkgName, state).commit();
	}

	/**
	 * <br>功能简述: 对应主题资源下载完成后是否已经通知用户
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePkgName
	 * @return
	 */
	synchronized boolean getHasNotified(String themePkgName) {
		return mSharedPreferences.getBoolean(SP_KEY_HAS_NOTIFIED + themePkgName, false);
	}

	/**
	 * <br>功能简述: 保存状态，用于记录对应主题资源下载完成后是否已经通知用户
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param themePkgName
	 * @param hasNotified 是否已经通知
	 */
	synchronized void setHasNotified(String themePkgName, boolean hasNotified) {
		mSharedPreferences.edit().putBoolean(SP_KEY_HAS_NOTIFIED + themePkgName, hasNotified)
				.commit();
	}
}
