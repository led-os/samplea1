package com.jiubang.ggheart.components.sidemenuadvert.utils;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreAppInforUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.sidemenuadvert.SideAdvertConstants;

/**
 * 
 * @author zhangxi
 *
 */
public class SideWidgetJsonUtil {

	// 应用中心根据分类id取数据的协议版本 固定标识
	public final static String CLASSIFICATION_INFO_PVERSION = "4.7";

	public final static int POST_TYPEID_GOSIDEWIDGET = 604; // 固定标识
	public final static int POST_ITP_GOSIDEWIDGET = 2; // 固定标识
	
	public final static int SIDEWIDGET_NET_PROTOCOL = 21; //固定标识
	
	public static JSONObject getRequestUrlJson(Context context) {
		JSONObject request = new JSONObject();
		JSONObject pheadJson = getPheadJson(context,
				CLASSIFICATION_INFO_PVERSION, SIDEWIDGET_NET_PROTOCOL);
		try {
			request.put("phead", pheadJson);

			JSONArray reqs = new JSONArray();
			JSONObject req1 = new JSONObject();
			req1.put("typeid", POST_TYPEID_GOSIDEWIDGET); // 固定值,写死
			req1.put("itp", POST_ITP_GOSIDEWIDGET); // 固定值,写死
			req1.put("must", 1); // 固定值,写死
			req1.put("pageid", 0); // 固定值,写死

			// 获取缓存中mark
			try {
				JSONObject infoBean = getSideWidgetCache();
				if (infoBean != null) {
					JSONObject typeInfo = infoBean.getJSONObject("types");
					JSONObject sideWidgetInfo = typeInfo.getJSONObject(String
							.valueOf(POST_TYPEID_GOSIDEWIDGET));
					String markString = sideWidgetInfo.optString("mark");
					if (markString != null && markString.length() > 0) {
						req1.put("mark", markString); 
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			
			reqs.put(req1);
			request.put("reqs", reqs);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
	}

	public static JSONObject getPheadJson(Context context, String pversion,
			int clientId) {
		JSONObject pheadJson = new JSONObject();
		if (context != null) {
			try {
				String imei = GoStorePhoneStateUtil.getVirtualIMEI(context);
				pheadJson.put("launcherid", imei); // 桌面id
				Locale locale = Locale.getDefault();
				pheadJson.put("sys", Build.MODEL); // 必填
				String language = String.format("%s_%s", locale.getLanguage()
						.toLowerCase(), locale.getCountry().toLowerCase());
				pheadJson.put("lang", language);
				pheadJson.put("local", Machine.getCountry(context).toUpperCase()); // 先取sim卡.没有然后再去本地
				pheadJson
						.put("channel", GoStorePhoneStateUtil.getUid(context)); // 渠道号
				pheadJson.put("imsi", RecommAppsUtils.getCnUser(context));
				pheadJson.put("hasmarket", GoStoreAppInforUtil
						.isExistGoogleMarket(context) ? 1 : 0);
				pheadJson.put("sdk", Build.VERSION.SDK_INT); // sdklevel
				pheadJson.put("dpi", RecommAppsUtils.getDisplay(context));
				pheadJson.put("pversion", pversion); // 协议版本
				pheadJson.put("net",
						RecommAppsUtils.buildNetworkState(context)); // 协议版本
				pheadJson.put("androidid", Machine.getAndroidId()); // androidid
				String curVersion = ApplicationProxy.getApplication()
						.getResources().getString(R.string.curVersion);
				pheadJson.put("cversion", curVersion); // 桌面的版本号, String例如：3.16
				pheadJson.put("clientid", clientId); // 死值
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pheadJson;
	}
	
	public static JSONArray getGoWidgetArray(JSONObject json) {
		JSONArray outArray = null;
		try {
			JSONObject result = json.getJSONObject("types");
			String keyString;
			keyString = String.valueOf(SideWidgetJsonUtil.POST_TYPEID_GOSIDEWIDGET);
			JSONObject valueJsonObject = result.getJSONObject(keyString);

			if (valueJsonObject != null) {
				outArray = valueJsonObject.getJSONArray("appdata");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		return outArray;
	}
	
	public static JSONArray getGoToolsArray(JSONObject json) {
		JSONArray outArray = null;
		try {
			JSONArray result = json.getJSONArray("types");
			if (result != null) {
				JSONObject obj = result.getJSONObject(0);
				outArray = obj.getJSONArray("apps");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		return outArray;
	}

	public static boolean saveSideWidgetCache(byte[] jsonbytes) {

		return SideAdvertUtils.saveSideCache(
				SideAdvertConstants.SIDE_CACHE_TYPE_WIDGET, jsonbytes);
	}
	
	public static boolean saveSideToolsCache(byte[] jsonbytes) {

		return SideAdvertUtils.saveSideCache(
				SideAdvertConstants.SIDE_CACHE_TYPE_TOOLS, jsonbytes);
	}

	public static JSONObject getSideWidgetCache() {
		JSONObject appInfo = null;
		try {
			String localStr = SideAdvertUtils
					.getSideCache(SideAdvertConstants.SIDE_CACHE_TYPE_WIDGET);
			if (localStr != null) {
				appInfo = new JSONObject(localStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null; 
		}
		return appInfo;
	}
	
	public static JSONObject getSideToolsCache() {
		JSONObject appInfo = null;
		try {
			String localStr = SideAdvertUtils
					.getSideCache(SideAdvertConstants.SIDE_CACHE_TYPE_TOOLS);
			if (localStr != null) {
				appInfo = new JSONObject(localStr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null; 
		}
		return appInfo;
	}
	
}
