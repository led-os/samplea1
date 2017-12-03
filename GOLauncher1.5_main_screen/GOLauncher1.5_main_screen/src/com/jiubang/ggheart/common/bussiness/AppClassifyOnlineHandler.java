package com.jiubang.ggheart.common.bussiness;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.proxy.ApplicationProxy;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.jiubang.ggheart.appgame.appcenter.help.AppsNetConstant;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.appgame.base.data.AppJsonOperator;
import com.jiubang.ggheart.appgame.base.net.AppGameNetRecord;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreAppInforUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.launcher.PackageName;
/**
 * 
 * @author wuziyi
 *
 */
public class AppClassifyOnlineHandler implements IConnectListener {
	
	private static final String HOSTURL_BASE = "http://goappfloder.3g.cn/appfloder/common?";

	public static final int UPDATE_DB_FUN_ID = 1;

	public static final int ARRANGE_APP_FUN_ID = 2;

	private static final String TAG = "AppClassifyRequestor";
	
	private static boolean sDebug = false;
	
	private RequestAppClassifyDataListener mRequestArrangeAppListener;

	private int mEntrance = -1;

	public void requestAppClassifyData(int funid, HashMap<String, String> paramMap, int entrance) {
		mEntrance = entrance;
		String url = getUrl(funid);
		try {
			// 具体的网络链接逻辑，APPGAME记录的引用问题，超时过短问题
			THttpRequest httpRequest = new THttpRequest(url, this);
			paramMap.put("handle", "0");
			paramMap.put("shandle", "1");
			JSONObject pheadJson = getPheadJson();
			paramMap.put("data", pheadJson.toString());
			log("data---->" + pheadJson.toString());
			httpRequest.setParamMap(paramMap);
			httpRequest.setProtocol(THttpRequest.PROTOCOL_POST);
			httpRequest.setOperator(new AppJsonOperator());
			httpRequest.setNetRecord(new AppGameNetRecord(ApplicationProxy.getContext(), false));
			httpRequest.setRetryTime(1);
			httpRequest.setTimeoutValue(6 * 1000);
			SimpleHttpAdapter httpAdapter = SimpleHttpAdapter.getInstance(ApplicationProxy
					.getContext());
			httpAdapter.addTask(httpRequest);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	/**
	 * <br>功能简述:设置参数Phead信息
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	private JSONObject getPheadJson() {
		Context context = ApplicationProxy.getContext();
		JSONObject phead = new JSONObject();
		JSONObject pheadJson = new JSONObject();
		if (context != null) {
			try {
				String imei = GoStorePhoneStateUtil.getVirtualIMEI(context);

				pheadJson.put("uid", imei); //桌面id

				pheadJson.put("lang", RecommAppsUtils.language(context)); //所在国家的语言

				pheadJson.put("local", Machine.getCountry(context)); //先取sim卡.没有然后再去本地

				pheadJson.put("channel", GoStorePhoneStateUtil.getUid(context)); // 渠道号

				pheadJson.put("imsi", RecommAppsUtils.getCnUser(context));

				int hasMarket = GoStoreAppInforUtil.isExistGoogleMarket(context) ? 1 : 0;
				pheadJson.put("hasmarket", hasMarket); //判断是否有google市场

				pheadJson.put("sys", Build.MODEL);

				pheadJson.put("sdk", Build.VERSION.SDK_INT); //sdklevel

				pheadJson.put("dpi", RecommAppsUtils.getDisplay(context));

				pheadJson.put("pversion", AppsNetConstant.CLASSIFICATION_INFO_PVERSION); //协议版本

				pheadJson.put("net", RecommAppsUtils.buildNetworkState(context)); //协议版本

				pheadJson.put("aid", Machine.getAndroidId()); //androidid

				pheadJson.put("cversion", RecommAppsUtils.buildVersion(context)); //桌面的版本号, String例如：3.16

				pheadJson.put("cid", "1");

				pheadJson.put("goid", StatisticsManager.getGOID(context));
				
				pheadJson.put("vcode", AppUtils.getVersionCodeByPkgName(ApplicationProxy.getContext(), PackageName.PACKAGE_NAME));
				
				pheadJson.put("ts", System.currentTimeMillis());

				phead.put("phead", pheadJson);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return phead;
	}

	/**
	 * <br>功能简述:获取URL地址
	 * <br>功能详细描述:funid=x&rd=1234;其中rd是系统时间毫秒数，防止网关cache.
	 * @return
	 */
	private String getUrl(int funId) {
		StringBuffer buffer = new StringBuffer(HOSTURL_BASE);
		buffer.append("funid=" + funId + "&rd=" + System.currentTimeMillis());
		return buffer.toString();
	}
	
	private HashMap<String, String> getUrlParama(URI uri) {
		HashMap<String, String> map = new HashMap<String, String>();
		String urlString = uri.toString();
		String[] strs = urlString.split("\\?");
		if (strs.length > 1) {
			String params = strs[1];
			String[] splitParam = params.split("\\&");
			for (int i = 0; i < splitParam.length; i++) {
				String param = splitParam[i];
				String[] keyValue = param.split("\\=");
				map.put(keyValue[0], keyValue[1]);
			}
		}
		return map;
	}
	
	public static void log(String message) {
		if (sDebug) {
			Log.i(TAG, message);
		}
	}

	public void setRequestArrangeAppListener(RequestAppClassifyDataListener requestArrangeAppListener) {
		mRequestArrangeAppListener = requestArrangeAppListener;
	}

	@Override
	public void onException(THttpRequest arg0, int errorCode) {
		log("error---->" + "网络请求异常---->" + errorCode);
		HashMap<String, String> paramaMap = getUrlParama(arg0.getUrl());
		int requestFunid = Integer.valueOf(paramaMap.get("funid"));
		if (mRequestArrangeAppListener != null) {
			mRequestArrangeAppListener.onRequestException(mEntrance, requestFunid);
		}
	}

	@Override
	public void onFinish(THttpRequest arg0, IResponse response) {
		HashMap<String, String> paramaMap = getUrlParama(arg0.getUrl());
		int requestFunid = Integer.valueOf(paramaMap.get("funid"));
		try {
			JSONObject json = (JSONObject) response.getResponse();
			log("ResponseJson---->" + json.toString());
			JSONObject result;
			result = json.getJSONObject(MessageListBean.TAG_RESULT);
			int status = result.getInt(MessageListBean.TAG_STATUS);
			//是否链接服务器成功
			switch (status) {
				case ConstValue.STATTUS_OK :
					JSONArray appInfos = json.getJSONArray("appinfo");
					HashMap<String, Integer> appsMap = new HashMap<String, Integer>();
					for (int i = 0; i < appInfos.length(); i++) {
						JSONObject appinfo = appInfos.getJSONObject(i);
						appsMap.put(appinfo.getString("packagename"), appinfo.getInt("tid"));
					}
					JSONArray typeinfos = json.getJSONArray("typeinfo");
					HashMap<Integer, HashMap<String, String>> folderMap = new HashMap<Integer, HashMap<String, String>>();
					for (int i = 0; i < typeinfos.length(); i++) {
						JSONObject typeinfo = typeinfos.getJSONObject(i);
						Iterator<String> it = typeinfo.keys();
						HashMap<String, String> nameMap = new HashMap<String, String>();
						while (it.hasNext()) {
							String key = it.next();
							if (key.equals("tid")) {
								int type = typeinfo.getInt(key);
								folderMap.put(type, nameMap);
							} else {
								String language = typeinfo.getString(key);
								log("ResponseJson typeinfo---->" + key);
								log("ResponseJson typeinfo---->" + language);
								nameMap.put(key, language);
							}
						}
					}
					if (mRequestArrangeAppListener != null) {
						mRequestArrangeAppListener.onRequsetFinish(appsMap, folderMap, mEntrance, requestFunid);
					}
					break;

				default :
					break;
			}
		} catch (Exception e) {
			Log.i("dzj", "arrangeApp onFinish but has exception");
			e.printStackTrace();
			handleRequestException(requestFunid);
		}
	} 
	
	private void handleRequestException(int requestFunid) {
		if (mRequestArrangeAppListener != null) {
			mRequestArrangeAppListener.onRequestException(mEntrance, requestFunid);
		}
	}

	@Override
	public void onStart(THttpRequest arg0) {

	}
	
	/**
	 * 
	 * @author wuziyi
	 * 
	 */
	public interface RequestAppClassifyDataListener {
		public void onRequsetFinish(HashMap<String, Integer> appInfos, HashMap<Integer, HashMap<String, String>> folderMap, int entrance, int funId);

		public void onRequestException(int entrance, int funId);
	}
}
