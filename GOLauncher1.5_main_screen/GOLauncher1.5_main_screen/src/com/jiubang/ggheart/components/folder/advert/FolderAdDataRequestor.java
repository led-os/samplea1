package com.jiubang.ggheart.components.folder.advert;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.gau.utils.cache.utils.CacheUtil;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.help.AppsNetConstant;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.appgame.base.data.AppJsonOperator;
import com.jiubang.ggheart.appgame.base.net.AppGameNetRecord;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreAppInforUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.common.bussiness.AppClassifyBussiness;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderInfo;

/**
 * 
 * @author dingzijian
 *
 */
public class FolderAdDataRequestor {

	//中国地区正式地址
	private static final String HOSTURL_BASE_CN = "http://goappcenter.3g.net.cn/recommendedapp/common.do?";
	//非中国地区正式地址
		private static final String HOSTURL_BASE = "http://goappcenter.goforandroid.com/recommendedapp/common.do?";

//	private static final String HOSTURL_BASE = "http://183.61.112.38:8011/recommendedapp/common.do?";

	private static final String FUN_ID = "15";

	public static final int TYPE_ID_GAME = 1;

	private static final String MAP_KEY_PKG_NAME = "key_pkg_name";

	private static final String MAP_KEY_APP_TITLE = "key_app_title";

	//	public static final String KEY_MARK = "key_mark";

	private FolderAdDataRequestFinishListener mAdDataRequestFinishListener;

	private CacheManager mCacheManager;

	public static final long TWENTY_FOUR_HOUR = 1000 * 24 * 60 * 60;
	public static final long EIGHT_HOUR = 1000 * 8 * 60 * 60;
	
	public FolderAdDataRequestor() {
		mCacheManager = new CacheManager(new FileCacheImpl(
				LauncherEnv.Path.FOLDER_AD_DATA_CACHE_PATH));
	}
	public void requestFolderAdData(final int tid) {
	    
	    //原来(没有以下这个判断)智能整理后,在游戏,文件夹的“猜你喜欢”中推送游戏应用。只推送未安装的游戏应用,已安装的不推；
	    //现在(加上下面这段代码) : 保留现有规则的前提下,如果用户未安装电子市场,则不推送游戏应用广告
        if (GoAppUtils.isMarketNotExitAnd200Channel(ApplicationProxy.getContext())
                && GLAppFolderInfo.TYPE_RECOMMAND_GAME == tid) {
            return;
        }
	    
	    
		boolean useCache = checkDataCache(tid);
		boolean overEightHour = System.currentTimeMillis() - getLastRequestTime() > EIGHT_HOUR;
		if (useCache || !(Machine.isNetworkOK(ApplicationProxy.getContext()) && overEightHour)) {
			return;
		}
		String hostUrl = HOSTURL_BASE;
		if (GoAppUtils.isCnUser(ApplicationProxy.getContext())) {
			hostUrl = HOSTURL_BASE_CN;
		}
		String url = getUrl(hostUrl);
		Log.i("dzj", "URL---->" + url);
		JSONObject requestJson = getRequestUrlJson(tid);
		Log.i("dzj", "RequestUrlJson---->" + requestJson.toString());
		try {
			THttpRequest httpRequest = new THttpRequest(url, requestJson.toString().getBytes(),
					new IConnectListener() {

						@Override
						public void onStart(THttpRequest arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFinish(THttpRequest arg0, IResponse response) {
							if (response != null && response.getResponse() != null
									&& (response.getResponse() instanceof JSONObject)) {
								JSONObject json = (JSONObject) response.getResponse();
								Log.i("dzj", "ResponseJson---->" + json.toString());
								try {
									JSONObject result = json
											.getJSONObject(MessageListBean.TAG_RESULT);
									int status = result.getInt(MessageListBean.TAG_STATUS);
									//TODO 是否链接服务器成功，才进行广告推送???
									if (status == ConstValue.STATTUS_OK) {
										int hasNew = json.getInt("hasnew");
										JSONArray array = null;
										String cacheKey = tid + "";
										switch (hasNew) {
											case 1 :
												array = json.getJSONArray("apps");
												if (mCacheManager.isCacheExist(cacheKey)) {
													// 如果缓存已经存在则清除旧的缓存
													mCacheManager.clearCache(cacheKey);
												}
												mCacheManager.saveCache(cacheKey, CacheUtil
														.stringToByteArray(array.toString()));
												Log.i("dzj", "appsJson---->" + array.toString());
												break;
											default :
												array = getCacheData(cacheKey);
												Log.i("dzj",
														"appsJsonCache---->" + array.toString());
												break;
										}
										if (mAdDataRequestFinishListener != null) {
											mAdDataRequestFinishListener.onAdDataRequestFinish(
													array, tid);
										}
										saveLastRequestTime();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						}

						@Override
						public void onException(THttpRequest arg0, int errorCode) {
							Log.i("dzj", "error---->" + "网络请求异常---->" + errorCode);
						}
					});
			httpRequest.setOperator(new AppJsonOperator());
			httpRequest.setNetRecord(new AppGameNetRecord(ApplicationProxy.getContext(), false));
			SimpleHttpAdapter httpAdapter = SimpleHttpAdapter.getInstance(ApplicationProxy.getContext());
			httpAdapter.addTask(httpRequest);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (URISyntaxException e) {

			e.printStackTrace();
		}
	}
	private boolean checkDataCache(final int tid) {
		boolean hasCache = mCacheManager.isCacheExist(tid + "");
		if (hasCache) {
			long lastRequestTime = getLastRequestTime();
			boolean needUpdate = System.currentTimeMillis() - lastRequestTime > TWENTY_FOUR_HOUR
					? true
					: false;
			if (!needUpdate) {
				JSONArray array = getCacheData(tid + "");
				if (mAdDataRequestFinishListener != null && array != null) {
					//					Log.i("dzj", "don't need update appsJsonCache---->" + array.toString());
					mAdDataRequestFinishListener.onAdDataRequestFinish(array, tid);
					return true;
				}
			}
		}
		return false;
	}
	private long getLastRequestTime() {
		PreferencesManager manager = new PreferencesManager(ApplicationProxy.getContext(),
				IPreferencesIds.FOLDER_AD_PREFERENCES, Context.MODE_PRIVATE);
		long lastRequestTime = manager.getLong(IPreferencesIds.FOLDER_AD_LAST_REQUEST_TIME, 0);
		return lastRequestTime;
	}

	private void saveLastRequestTime() {
		PreferencesManager manager = new PreferencesManager(ApplicationProxy.getContext(),
				IPreferencesIds.FOLDER_AD_PREFERENCES, Context.MODE_PRIVATE);
		manager.putLong(IPreferencesIds.FOLDER_AD_LAST_REQUEST_TIME, System.currentTimeMillis());
		manager.commit();
	}
/**
 * 
 * @author root
 *
 */
	public interface FolderAdDataRequestFinishListener {
		public void onAdDataRequestFinish(JSONArray adDataJsonArray, int folderTypeId);
	}

	public void setAdDataRequestFinishListener(
			FolderAdDataRequestFinishListener AdDataRequestFinishListener) {
		this.mAdDataRequestFinishListener = AdDataRequestFinishListener;
	}

	/**
	 * <br>功能简述:获取URL地址
	 * <br>功能详细描述:funid=x&rd=1234;其中rd是随机数，防止网关cache.
	 * <br>注意:1:服务器请求，2：统计
	 * @return
	 */
	private String getUrl(String url) {
		StringBuffer buffer = new StringBuffer(url);
		Random random = new Random(new Date().getTime()); //随机数
		buffer.append("funid=" + FUN_ID + "&rd=" + random.nextLong());
		random = null;
		return buffer.toString();
	}

	/**
	 * <br>功能简述:设置请求参数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	private JSONObject getRequestUrlJson(int tid) {
		JSONObject request = new JSONObject();
		JSONObject pheadJson = getPheadJson();
		HashMap<String, JSONArray> map = getAppPkgNamesAndTitle(tid);
		JSONArray packageNames = map.get(MAP_KEY_PKG_NAME);
		JSONArray appTitles = map.get(MAP_KEY_APP_TITLE);
		//是否强制刷新
		int must = mCacheManager.isCacheExist(tid + "") ? 1 : 0;
		//		PreferencesManager preferencesManager = new PreferencesManager(ApplicationProxy.getContext(),
		//				IPreferencesIds.FOLDER_AD_PREFERENCES, Context.MODE_PRIVATE);
		//		String mark = preferencesManager.getString(KEY_MARK, null);
		try {
			request.put("phead", pheadJson);
			request.put("tid", tid);
			request.put("packnames", packageNames);
			request.put("appnames", appTitles);
			request.put("must", must);
			//			if (mark != null) {
			//				request.put("mark", mark);
			//			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return request;
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
		JSONObject pheadJson = new JSONObject();
		if (context != null) {
			try {
				String imei = GoStorePhoneStateUtil.getVirtualIMEI(context);

				pheadJson.put("launcherid", imei); //桌面id

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

				pheadJson.put("androidid", Machine.getAndroidId()); //androidid

				pheadJson.put("cversion", RecommAppsUtils.buildVersion(context)); //桌面的版本号, String例如：3.16

				pheadJson.put("clientid", "21");

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pheadJson;
	}

	private HashMap<String, JSONArray> getAppPkgNamesAndTitle(int tid) {
		HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
		JSONArray pkgNameArray = new JSONArray();
		JSONArray appTitleArray = new JSONArray();
		CommonControler commonControler = CommonControler.getInstance(ApplicationProxy.getContext());
		int classification = AppClassifyBussiness.NO_CLASSIFY_APP;
		switch (tid) {
			case TYPE_ID_GAME :
				classification = AppClassifyBussiness.GAMES;
				break;

			default :
				break;
		}
		ArrayList<AppItemInfo> appItemInfos = (ArrayList<AppItemInfo>) commonControler
				.getClassifyList(classification,
						AppDataEngine.getInstance(ApplicationProxy.getContext()).getAllAppItemInfos());
		if (appItemInfos != null && !appItemInfos.isEmpty()) {
			for (AppItemInfo info : appItemInfos) {
				pkgNameArray.put(info.getAppPackageName());
				appTitleArray.put(info.mTitle);
			}
			map.put(MAP_KEY_PKG_NAME, pkgNameArray);
			map.put(MAP_KEY_APP_TITLE, appTitleArray);
		}
		return map;
	}

	private JSONArray getCacheData(String cacheKey) {
		JSONArray jsonArray = null;
		if (mCacheManager.isCacheExist(cacheKey)) {
			byte[] cacheData = mCacheManager.loadCache(cacheKey);
			if (cacheData == null) {
				return null;
			}
			String appInfo = CacheUtil.byteArrayToString(cacheData);
			if (appInfo == null) {
				return null;
			}
			try {
				jsonArray = new JSONArray(appInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
	}

}
