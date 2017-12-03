package com.jiubang.ggheart.iconconfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * Icon未读数字配置数据提供者  BusinessCooperationController
 * 
 * @author caoyaming
 *
 */
public class AppIconConfigDataProvider {
	//LOG_TAG
	private static final String LOG_TAG = AppIconConfigDataProvider.class.getName();
	//服务器地址
	//private static String sHosturlBase = ServerUtils.isUseTestServer(LauncherEnv.Server.SCREEN_ICON_CONFIG_TEST_SERVER) ? LauncherEnv.Server.SCREEN_ICON_CONFIG_URL_SIT : LauncherEnv.Server.SCREEN_ICON_CONFIG_URL_PRO;
	private static String sHosturlBase = LauncherEnv.Server.APP_ICON_CONFIG_URL_SIT;
	//Context
	private Context mContext;
	//PreferencesManager
	private PreferencesManager mPreferencesManager = null;
	//当前类对象
	private static AppIconConfigDataProvider sInstance = null;
	public AppIconConfigDataProvider(Context context) {
		mContext = context;
		//获取PreferencesManager对象
		mPreferencesManager = new PreferencesManager(mContext, PrefConst.SP_UNBAKUP_PREFERENCE_APP_ICON_CONFIG, Context.MODE_PRIVATE);
		
		// 这两个controller需要及早初始化，不然收不到更新xml的广播
		/*IconClickInterceptController.getInstance(mContext);
		PromotionDialogController.getInstance(mContext);
		IconControllableManager.getInstance(mContext);*/
	}
	/**
	 * 获取当前类单例对象
	 * @param context 
	 * @return
	 */
	public static synchronized AppIconConfigDataProvider getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new AppIconConfigDataProvider(context);
		}
		return sInstance;
	}
	/**
	 * 请求屏幕图标对应的数字数据
	 */
	public synchronized void requestIconUnreadDigitalData(final ReuqestDataListener listener) {
		//如果监听器为空,则直接返回,不需要请求.
		if (listener == null) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				HttpURLConnection mUrlConn = null;
				InputStream input = null;
				try {
					//创建URL
					URL url = new URL(getRequestUrl());
					//打开连接
					mUrlConn = (HttpURLConnection) url.openConnection();
					//设置读取数据超时时间
					mUrlConn.setReadTimeout(30000);
					//设置连接超时时间
					mUrlConn.setConnectTimeout(30000);
					//读取返回的数据
					input = mUrlConn.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					//解析并返回数据
					listener.onFinish(parseScreenIconConfigData(sb.toString()));
				} catch (Exception e) {
					e.printStackTrace();
					//返回请求数据失败
					listener.onException(e.getMessage());
				} finally {
					try {
						if (input != null) {
							input.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (mUrlConn != null) {
						mUrlConn.disconnect();
					}
				}
			}
		}.start();
	}
	/**
	 * 获取请求屏幕图标未读数字信息服务器地址
	 * @return 地址(已带请求参数)
	 */
	private String getRequestUrl() {
		StringBuilder sb = new StringBuilder(sHosturlBase);
		//产品id(1:Go桌面)
		sb.append("?pid=1");
		//产品渠道号
		sb.append("&channel=" + Statistics.getUid(mContext));
		//go系列统一id
		sb.append("&goid=" + StatisticsManager.getGOID(mContext));
		try {
			//获取当前应用信息
			PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			//版本编号,整型数
			sb.append("&vercode=" + info.versionCode);
			//版本号,字符串
			sb.append("&vername=" + info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		//客户端上次请求时间戳,首次请求为0---用于控制用户收到消息的时长
		sb.append("&timestamp=" + getLastServerTimeStamp());
		Log.e(LOG_TAG, "请求屏幕图标未读数字信息服务器地址 ： " + sb.toString());
		return sb.toString();
	}
	
	/**
	 * 解析屏幕图标配置相关数据
	 * @param dataStr 配置相关数据
	 * @return
	 */
	private List<AppIconConfigBean> parseScreenIconConfigData(String dataStr) {
		try {
			//转换为Json格式
			JSONObject iconDataJsonObject = new JSONObject(dataStr);
			//获取产品ID--暂未使用
			//int productId = iconDataJsonObject.optLong("pid", 0);
			//获取数据更新时间戳
			long timeStamp = iconDataJsonObject.optLong("timestamp", 0);
			//获取配置信息
			JSONArray dataJsonArray = iconDataJsonObject.optJSONArray("data");
			if (dataJsonArray == null || dataJsonArray.length() <= 0) {
				return null;
			}
			//解析配置信息
			List<AppIconConfigBean> iconConfigDataList = new ArrayList<AppIconConfigBean>();
			JSONObject dataJsonObject;
			AppIconConfigBean iconConfigBean;
			for (int index = 0; index < dataJsonArray.length(); index++) {
				dataJsonObject = dataJsonArray.optJSONObject(index);
				if (dataJsonObject == null) {
					continue;
				}
				iconConfigBean = new AppIconConfigBean();
				//应用标识ComponentName
				iconConfigBean.setmComponentName(dataJsonObject.optString("pkgename", ""));
				//显示未读数字,默认0
				iconConfigBean.setmShowNumber(dataJsonObject.optInt("shownumber", 0));
				//开始版本(如:4.14)
				iconConfigBean.setmStartVersion(dataJsonObject.optString("startversion", ""));
				//结束版本(如:4.14)
				iconConfigBean.setmEndVersion(dataJsonObject.optString("endverstion", ""));
				//开始时间(yyyyMMddHHmmss)
				iconConfigBean.setmValidStartTime(dataJsonObject.optLong("validstarttime", 0));
				//结束时间(yyyyMMddHHmmss)
				iconConfigBean.setmValidEndTime(dataJsonObject.optLong("validendtime", 0));
				
				iconConfigDataList.add(iconConfigBean);
			}
			//解析数据成功,保存更新时间戳
			if (iconConfigDataList.size() > 0 && timeStamp > 0) {
				savetLastServerTimeStamp(timeStamp);
			}
			return iconConfigDataList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取最后一次获取的数据中服务端的时间戳
	 * @return
	 */
	private long getLastServerTimeStamp() {
		return mPreferencesManager.getLong(PrefConst.KEY_APP_ICON_CONFIG_SERVER_TIME_STAMP, 0);
	}
	/**
	 * 保存最后一次获取的数据中服务端的时间戳
	 * @param time 时间戳
	 */
	private void savetLastServerTimeStamp(long time) {
		mPreferencesManager.putLong(PrefConst.KEY_APP_ICON_CONFIG_SERVER_TIME_STAMP, time);
		mPreferencesManager.commit();
	}
	
	/**
	 * 请求数据监听器
	 * @author caoyaming
	 *
	 */
	public interface ReuqestDataListener {
		/**
		 * 请求数据完成		
		 * @param iconConfigDataList 返回的数据集合
		 */
		public void onFinish(List<AppIconConfigBean> iconConfigDataList);
		/**
		 * 请求数据失败
		 * @param errorMessage 错误消息
		 */
		public void onException(String errorMessage);
	}
}
