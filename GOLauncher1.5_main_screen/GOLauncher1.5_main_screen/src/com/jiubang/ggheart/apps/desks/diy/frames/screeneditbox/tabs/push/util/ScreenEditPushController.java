package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.util;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.gau.utils.cache.utils.CacheUtil;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.INetRecord;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.WallpaperOperation;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.gohandbook.SharedPreferencesUtil;
import com.jiubang.ggheart.components.gohandbook.StringGzipOperator;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 壁纸,主题,widget推送请求工具
 * @author zouguiquan
 *
 */
public class ScreenEditPushController {

	private CacheManager mCacheManager;
	protected Context mContext;
	private SharedPreferencesUtil mSharedPreferencesUtil;
	private PackageManager mPackageManager;

	private boolean mRequesting;
	private long mRequestInterval = 8 * 60 * 60 * 1000;
	
	private static final int CONNECT_TIME_OUT = 10000;
	private static final int READ_TIME_OUT = 30000;

	public ScreenEditPushController(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
		mCacheManager = new CacheManager(new FileCacheImpl(
				LauncherEnv.Path.SCREEN_EDIT_PUSH_CACHEDATA_PATH));
		mSharedPreferencesUtil = new SharedPreferencesUtil(context);
	}
	
	private Intent searchWallpaperListIntent() {
		Intent result = null;
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER, null);
		List<ResolveInfo> list = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : list) {
			if (resolveInfo.activityInfo == null) {
				continue;
			}
			
			String pkgName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;
			if (pkgName != null && pkgName.equals(WallpaperOperation.LIVE_WALLPAPER_PACKAGENAME)) {
				if (className != null) {
					result = new Intent();
					result.setComponent(new ComponentName(pkgName, className));
				}
			}
		}
		return result;
	}
	
	public Drawable getAppIcon(String pkgName) {
		Drawable icon = null;
		try {
			ApplicationInfo info = mPackageManager.getApplicationInfo(pkgName, 0);
			if (info != null) {
				icon = info.loadIcon(mPackageManager);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return icon;
	}
	
	public String getAppLable(String packageName) {
		if (packageName == null) {
			return null;
		}
		
		String label = null;
		
		try {
			ApplicationInfo info = mPackageManager.getApplicationInfo(packageName, 0);
			if (info != null) {
				label = mPackageManager.getApplicationLabel(info).toString();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return label;
	}

	public Bitmap loadImage(String imgPath, String imgName, String imgUrl) {
		Bitmap result = null;
		result = loadImgFromSD(imgPath, imgName);
		
		if (result == null) {
			result = loadImgFromNetwork(imgUrl);
			if (result != null) {
				if (FileUtil.isSDCardAvaiable()) {
					boolean b = FileUtil.saveBitmapToSDFile(result, imgPath + imgName, Bitmap.CompressFormat.PNG);
				}
			}
		}
		return result;
	}
	
	public Bitmap loadImgFromSD(String imgPath, String imgName) {
		Bitmap result = null;
		try {
			if (FileUtil.isSDCardAvaiable()) {
				File file = new File(imgPath + imgName);
				if (file.exists()) {
					result = BitmapFactory.decodeFile(imgPath + imgName);
				}
			}
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public Bitmap loadImgFromNetwork(String imgUrl) {
		if (imgUrl == null || imgUrl.equals("") || !Machine.isNetworkOK(mContext)) {
			return null;
		}
		
		Bitmap result = null;
		InputStream inputStream = null;
		HttpURLConnection urlCon = null;
		try {
			urlCon = createURLConnection(imgUrl);
			inputStream = (InputStream) urlCon.getContent();
			if (inputStream != null) {
				result = BitmapFactory.decodeStream(inputStream);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (urlCon != null) {
				urlCon.disconnect();
			}
		}
		return result;
	}

	/**
	 * 功能简述: 根据URL生成HttpURLConnection的方法 功能详细描述: 注意:
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public HttpURLConnection createURLConnection(String url) throws Exception {
		HttpURLConnection urlCon = null;
		urlCon = (HttpURLConnection) new URL(url).openConnection();
		urlCon.setConnectTimeout(CONNECT_TIME_OUT);
		urlCon.setReadTimeout(READ_TIME_OUT);
		return urlCon;
	}

	/**
	 * 
	 * @param context
	 * @param type  602代表"动态壁纸列表"　603代表"widget列表"
	 * @param handler
	 */
	public void requestPushData(final int typeId, final String saveTimeKey) {

		if (mContext == null || mRequesting || !Machine.isNetworkOK(mContext)) {
			return;
		}

		String url = ScreenEditPushConstants.getUrl(mContext, "8"); //获取URL地址

		JSONObject requestJson = getRequestUrlJson(typeId);
//		log("请求参数1：" + requestJson.toString());
		try {
			THttpRequest request = new THttpRequest(url, requestJson.toString().getBytes(),
					new IConnectListener() {
						@Override
						public void onStart(THttpRequest request) {
						}

						@Override
						public void onFinish(THttpRequest request, IResponse response) {
							
							mRequesting = false;
							if (response != null && response.getResponse() != null
									&& (response.getResponse() instanceof String)) {
								try {
									String responseString = response.getResponse().toString();
									JSONObject json = new JSONObject(responseString);
									JSONObject result = json.getJSONObject(MessageListBean.TAG_RESULT);
									int status = result.getInt(MessageListBean.TAG_STATUS);

									//请求成功
									if (status == ConstValue.STATTUS_OK) {
										
										JSONArray pushArray = json.getJSONArray("apps");
										log("请求成功! responseString = " + responseString);
										savePushData(pushArray, typeId, saveTimeKey);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onException(THttpRequest arg0, int arg1) {
						}
					});

			request.setOperator(new StringGzipOperator()); //设置返回数据类型-字符串

			//设置报错提示
			request.setNetRecord(new INetRecord() {

				@Override
				public void onTransFinish(THttpRequest arg0, Object arg1, Object arg2) {
				}

				@Override
				public void onStartConnect(THttpRequest arg0, Object arg1, Object arg2) {
				}

				@Override
				public void onException(Exception e, Object arg1, Object arg2) {
					mRequesting = false;
					e.printStackTrace(); //打印出HTTP请求真实的错误信息
				}

				@Override
				public void onConnectSuccess(THttpRequest arg0, Object arg1, Object arg2) {
				}
			});
			SimpleHttpAdapter httpAdapter = SimpleHttpAdapter.getInstance(mContext);
			httpAdapter.addTask(request);
			mRequesting = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 缓存推荐列表
	 * @param jsonArray
	 * @param typeId
	 */
	public void savePushData(JSONArray jsonArray, int typeId, String saveTimeKey) {
		if (jsonArray != null) {
//			mCacheManager.saveCacheAsync(ScreenEditPushConstants.sSaveCachePerfix + typeId,
//					CacheUtil.jsonArrayToByteArray(jsonArray), this);
			mCacheManager.saveCache(ScreenEditPushConstants.sNewPushAppPerfix + typeId,
					CacheUtil.jsonArrayToByteArray(jsonArray));
			saveLtsString(saveTimeKey);
		}
	}
	
	public int saveInstalledPackage(String pkgName) {
		if (pkgName == null || pkgName.equals("")) {
			return -1;
		}
		
		int count = ScreenEditPushConstants.sPushKeyArray.length;
		for (int i = 0; i < count; i++) {
			int key = ScreenEditPushConstants.sPushKeyArray[i];
			
			List<String> currentPushApp = getCurrentPushApp(key);
			if (currentPushApp != null && currentPushApp.contains(pkgName)) {
				JSONArray installArray = getJSONArray(ScreenEditPushConstants.sInstalledAppPerfix + key);

				if (installArray == null) {
					installArray = new JSONArray();
					log("saveInstalledPackage installArray.length= " + installArray.length());
				} else {
					log("saveInstalledPackage installArray.length= " + installArray.length());
				}
				
				int length = installArray.length();
				try {
					for (int j = 0; j < length; j++) {
						JSONObject msgJsonObject = (JSONObject) installArray.get(j);
						String packageName = msgJsonObject.optString(ScreenEditPushConstants.sJsonPackage);
						if (packageName.equals(pkgName)) {
							return key;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				JSONObject jsonObject = createJSONObject(pkgName);
				installArray.put(jsonObject);
				mCacheManager.saveCache(ScreenEditPushConstants.sInstalledAppPerfix + key,
						CacheUtil.jsonArrayToByteArray(installArray));
				return key;
			}
		}
		
		return -1;
	}
	
	public void saveCurrentPushApp(JSONArray jsonArray, String key) {
		mCacheManager.saveCache(key, CacheUtil.jsonArrayToByteArray(jsonArray));
	}
	
	public List<String> getCurrentPushApp(int key) {
		List<String> result = null;
		JSONArray jsonArray = getJSONArray(ScreenEditPushConstants.sCurrentPushAppPerfix + key);
		if (jsonArray != null) {
			result = new ArrayList<String>();
			int length = jsonArray.length();
			try {
				for (int i = 0; i < length; i++) {
					JSONObject msgJsonObject = (JSONObject) jsonArray.get(i);
					String packageName = msgJsonObject.optString(ScreenEditPushConstants.sJsonPackage);
					result.add(packageName);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public JSONArray getJSONArray(String key) {
		JSONArray jsonArray = null;
		byte[] data = mCacheManager.loadCache(key);
		
		if (data != null) {
			jsonArray = CacheUtil.byteArrayToJsonArray(data);
		}
		return jsonArray;
	}
	
	public JSONObject createJSONObject(String pkgName) {
		if (pkgName == null || pkgName.equals("")) {
			return null;
		}
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(ScreenEditPushConstants.sJsonPackage, pkgName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObject;
	}
	
	public void cleanPushData(int typeId) {
//		mCacheManager.clearCache(ScreenEditPushConstants.sSaveCachePerfix + typeId);
//		mCacheManager.clearCache(ScreenEditPushConstants.sInstalledAppPerfix + typeId);
//		FileUtil.deleteDirectory(LauncherEnv.Path.SCREEN_EDIT_PUSH_CACHEICON_PATH);
	}
	
	/**
	 * 记录数据更新的时间
	 * @param context
	 * @param ltsString
	 */
	private void saveLtsString(String key) {
		if (mSharedPreferencesUtil != null) {
			long now = System.currentTimeMillis();
			mSharedPreferencesUtil.saveLong(key, now);
		}
	}
	
	public void cleanLtsString(String key) {
		if (mSharedPreferencesUtil != null) {
			mSharedPreferencesUtil.saveLong(key, -1);
		}
	}
	
	/**
	 * 超过８小时，重新请求数据
	 * @return
	 */
	public boolean checkToRequset(String key) {
		long now = System.currentTimeMillis();
		long before = mSharedPreferencesUtil.getLong(key, -1l);
		if (before != -1l) {
			return (now - before) > mRequestInterval;
		}
		return true;
	}

	/**
	 * <br>功能简述:设置请求参数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param type  602代表"动态壁纸列表"　603代表"widget列表"
	 * @return
	 */
	public JSONObject getRequestUrlJson(int type) {
		JSONObject pheadJson = getPheadJson();
		JSONObject request = new JSONObject();
		try {
			request.put("phead", pheadJson);
			request.put("typeid", type);
			request.put("pageid", 0);		//固定值
			request.put("itp", 2);			//固定值
			request.put("must", 1);			//固定值
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
		JSONObject pheadJson = new JSONObject();
		if (mContext != null) {
			String imei = GoStorePhoneStateUtil.getVirtualIMEI(mContext);
			
			try {
				pheadJson.put("hasmarket", 0);
				pheadJson.put("launcherid", imei); 								  //桌面id
				pheadJson.put("local", Machine.getCountry(mContext)); 		      //先取sim卡.没有然后再去本地
				pheadJson.put("channel", GoStorePhoneStateUtil.getUid(mContext)); //渠道号

				Locale locale = Locale.getDefault();
				String language = String.format("%s_%s", locale.getLanguage().toLowerCase(), locale
						.getCountry().toLowerCase());
				pheadJson.put("lang", language);								  //语言

				pheadJson.put("pversion", "4.3"); 								  //协议版本号,固定值
				pheadJson.put("cversion", "3.19"); 								  //客户端软件版本号,固定值
				pheadJson.put("androidid", Machine.getAndroidId()); 			  //androidid
				pheadJson.put("clientid", 21);								      //访问方id,固定值
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pheadJson;
	}

	public void log(String content) {
		if (true) {
			Log.d("widgetpush", content);
		}
	}
}
