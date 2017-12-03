package com.jiubang.ggheart.common.bussiness;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.proxy.ApplicationProxy;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
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
 * http://gowiki.3g.net.cn/pages/viewpage.action?pageId=3801116
 */
public class LightAppConnector implements IConnectListener {
	
	private static final String HOSTURL_BASE = "http://gotest.3g.net.cn/lightapp/common?";
	
	public static final int SMART_CARD_DATA = 1;

	public static final int APPDRAWER_TOOLS_DATA = 2;
	
	private RequestLightAppDataListener mRequestLightAppListener;

	public void requestLightAppData(int funid, HashMap<String, JSONArray> ExtarMap) {
		String url = getUrl(funid);
		try {
			// 具体的网络链接逻辑，APPGAME记录的引用问题，超时过短问题
			THttpRequest httpRequest = new THttpRequest(url, this);
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("handle", "0");
			paramMap.put("shandle", "1");
			JSONObject pheadJson = getPheadJson(ExtarMap);
			paramMap.put("data", pheadJson.toString());
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
	private JSONObject getPheadJson(HashMap<String, JSONArray> ExtarMap) {
		Context context = ApplicationProxy.getContext();
		JSONObject phead = new JSONObject();
		JSONObject pheadJson = new JSONObject();
		if (context != null) {
			try {
				pheadJson.put("imei", GoStorePhoneStateUtil.getVirtualIMEI(context)); //桌面id

				pheadJson.put("lang", RecommAppsUtils.language(context).split("_")[0]); //所在国家的语言

				pheadJson.put("local", Machine.getCountry(context).toLowerCase(Locale.getDefault())); //先取sim卡.没有然后再去本地

				pheadJson.put("channel", GoStorePhoneStateUtil.getUid(context)); // 渠道号

				pheadJson.put("imsi", RecommAppsUtils.getCnUser(context));

				int hasMarket = GoStoreAppInforUtil.isExistGoogleMarket(context) ? 1 : 0;
				pheadJson.put("hasmarket", hasMarket); //判断是否有google市场

				pheadJson.put("sys", Build.VERSION.RELEASE);
				
				pheadJson.put("model", Build.MODEL);

				pheadJson.put("sdk", Build.VERSION.SDK_INT); //sdklevel

				pheadJson.put("dpi", RecommAppsUtils.getDisplay(context));

				pheadJson.put("pversion", 1); //协议版本

				pheadJson.put("net", RecommAppsUtils.buildNetworkState(context)); 

				pheadJson.put("aid", Machine.getAndroidId()); //androidid

				pheadJson.put("cversion", RecommAppsUtils.getGoLocherVersion(context)); //桌面的版本号, String例如：3.16

				pheadJson.put("cid", "1");

				pheadJson.put("goid", StatisticsManager.getGOID(context));
				
				pheadJson.put("vcode", AppUtils.getVersionCodeByPkgName(ApplicationProxy.getContext(), PackageName.PACKAGE_NAME));
				
				pheadJson.put("ts", System.currentTimeMillis());

				phead.put("phead", pheadJson);
				
				Set<Entry<String, JSONArray>> set = ExtarMap.entrySet();
				for (Entry<String, JSONArray> entry : set) {
					phead.put(entry.getKey(), entry.getValue());
				}
				
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
	
	public void setRequestLightAppListener(RequestLightAppDataListener requestLightAppListener) {
		mRequestLightAppListener = requestLightAppListener;
	}

	@Override
	public void onException(THttpRequest arg0, int errorCode) {
		HashMap<String, String> paramaMap = getUrlParama(arg0.getUrl());
		int requestFunid = Integer.valueOf(paramaMap.get("funid"));
		if (mRequestLightAppListener != null) {
			mRequestLightAppListener.onRequestException(requestFunid);
		}
	}

	@Override
	public void onFinish(THttpRequest arg0, IResponse response) {
		HashMap<String, String> paramaMap = getUrlParama(arg0.getUrl());
		int requestFunid = Integer.valueOf(paramaMap.get("funid"));
		try {
			JSONObject json = (JSONObject) response.getResponse();
			JSONObject result;
			result = json.getJSONObject(MessageListBean.TAG_RESULT);
			int status = result.getInt(MessageListBean.TAG_STATUS);
			//是否链接服务器成功
			switch (status) {
				case ConstValue.STATTUS_OK :
					if (mRequestLightAppListener != null) {
						mRequestLightAppListener.onRequsetFinish(json, requestFunid);
					}
					break;

				default :
					handleRequestException(requestFunid);
					break;
			}
		} catch (Exception e) {
			handleRequestException(requestFunid);
		}
	} 
	
	private void handleRequestException(int requestFunid) {
		if (mRequestLightAppListener != null) {
			mRequestLightAppListener.onRequestException(requestFunid);
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
	public interface RequestLightAppDataListener {
		public void onRequsetFinish(JSONObject json, int funId);

		public void onRequestException(int funId);
	}
}
