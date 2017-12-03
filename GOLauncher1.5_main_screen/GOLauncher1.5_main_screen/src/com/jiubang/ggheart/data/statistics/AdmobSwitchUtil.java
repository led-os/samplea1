package com.jiubang.ggheart.data.statistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.launcherex.R;
import com.go.util.ServerUtils;
import com.jiubang.ggheart.admob.AdController;
import com.jiubang.ggheart.admob.AdInfo;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemePurchaseManager;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * Admob广告开关工具类
 * @author zhouxuewen
 *
 */
public class AdmobSwitchUtil {
	
	private final static boolean DEBUG = false;

	private static String sUrl = LauncherEnv.Server.ADMOB_BASE_HOSTURL_PRO; 	// 正式admob地址
	
	//　本地ＩＰ
//	private final static String URL = "http://192.168.214.48:9014/gostore/webcontent/function/getAdmobCon.jsp?";
	
	static {
		if (ServerUtils.isUseTestServer(LauncherEnv.Server.ADMOB_CONFIG_USE_TEST_SERVER)) {
			sUrl = LauncherEnv.Server.ADMOB_BASE_HOSTURL_SIT;
		}
	}
	
	/**
	 * 连网更新开关状态
	 * @param context
	 */
	public static void updataAdmobSwitch(final Context context) {
		if (context == null) {
			return;
		}
		
		try {
			StringBuilder sb = new StringBuilder(sUrl);
			sb.append("country=" + Statistics.simlanguage(context));
			sb.append("&channel=" + Statistics.getUid(context));
			sb.append("&goid=" + StatisticsManager.getGOID(context));
			sb.append("&vercode=" + Statistics.buildVersionCode(context));
			sb.append("&vername=" + context.getString(R.string.curVersion));
			sb.append("&isvip=" + ((ThemePurchaseManager.getCustomerLevel(context) == 1 || ThemePurchaseManager.getCustomerLevel(context) == 2) ? "1" : "0"));
			sb.append("&ispremium=" + (DeskSettingUtils.isNoAdvert() ? "1" : "0"));
			sb.append("&timestamp=" + getTimestamp(context));
			sb.append("&pid=" + "1,2,3");
			final String url = sb.toString();
			
			if (DEBUG) {
				Log.d("zxw", "url:" + url);
			}
			new Thread() {
				public void run() {
					HttpGet httpRequest = new HttpGet(url);
					httpRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
					httpRequest.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);

					try {
						HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);   
						/*若状态码为200 ok*/  
						if (httpResponse.getStatusLine().getStatusCode() == 200) {   
							String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
							/*取出响应字符串*/  
							JSONObject obj = new JSONObject(content);
							if (DEBUG) {
								Log.d("zxw", "obj:" + obj.toString());
							}
							JSONArray array = obj.optJSONArray("switches");
							if (array != null) {
								int size = array.length();
								List<AdInfo> infos = new ArrayList<AdInfo>();
								for (int i = 0; i < size; ++i) {
									JSONObject info = array.optJSONObject(i);
									if (info == null) {
										continue;
									}
									AdInfo item = new AdInfo();
									item.mPosId = info.optInt("adpos");
									item.mSwitchState = info.optInt("swich") == 2;
									item.mPid = info.optInt("pid");
									item.mProvity = info.optInt("gjfirst");
									item.mGetJarEnable = info.optInt("isgj") == 1;
									infos.add(item);
								}
								int adlimit = obj.optInt("adlimit"); 
								int pertime = obj.optInt("pertime");
								long timestamp = obj.optLong("timestamp", 0);
								saveTimestamp(context, timestamp);
								AdController.getController(context).updateAdInfos(infos, adlimit, pertime);
							}
						}     
					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				};
			}.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static void saveTimestamp(Context context, long timestamp) {
		try {
			PrivatePreference pref = PrivatePreference.getPreference(context);
			pref.putLong(PrefConst.KEY_TIMESTAMP, timestamp);
			pref.commit();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static long getTimestamp(Context context) {
		long timestamp = 0;
		try {
			PrivatePreference pref = PrivatePreference.getPreference(context);
			timestamp = pref.getLong(PrefConst.KEY_TIMESTAMP, 0);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return timestamp;
	}
}
