package com.jiubang.ggheart.zeroscreen.navigation.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.zeroscreen.navigation.bean.ZeroScreenAdSuggestSiteInfo;

/**
 * @author zhangkai 从服务器获取热门网址更新到数据库
 */
public class SuggestSiteObtain {
	private static SuggestSiteObtain sInstance = new SuggestSiteObtain();

	private final String mTestServer = "http://lauhotwebs.3g.cn:8012";
	private final String mAddr = "/gobrowser/common?funid=1&handle=0&shandle=1";

	private String mURL = null;
	private boolean mIsRunning = false;
	private Context mContext = null;
	private String mWebUrl;

	private ArrayList<ZeroScreenAdSuggestSiteInfo> mInfoList;

	private SuggestSiteObtain() {
		/*
		 * if (ToolUtil.isTestServer()) { mURL = mTestServer.concat(mAddr); }
		 * else { mURL = BrowserEnv.Server.getServerDomain().concat(mAddr); }
		 */
		mURL = mTestServer.concat(mAddr);
	}

	public static SuggestSiteObtain getInstance() {
		return sInstance;
	}

	public synchronized void startgetSuggestSites(final Context context,
			final Handler handler) {
		mContext = context;
		if (mIsRunning) {
			return;
		}

		// 首先获取热门网址时间间隔是否已足够
		if (!isTimeIntervalEnough(context)) {
			return;
		}
		if (!Machine.isNetworkOK(context)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				mIsRunning = true;

				String localVersion = getLocalSuggestVersion(context);
				JSONObject postdata = null;

				try {
					JSONObject phead = ToolUtil.getJSONhead(mContext);
					postdata = new JSONObject();
					postdata.put("phead", phead);
					postdata.put("datasVer", localVersion);
					postdata.put("pid", "lanucher");

				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				if (postdata == null) {
					mIsRunning = false;
					return;
				}

				String rd = "&rd=" + System.currentTimeMillis();

				// 创建POST请求
				HttpPost request = new HttpPost(mURL.concat(rd));
				List<BasicNameValuePair> nameValuePair = new ArrayList<BasicNameValuePair>();
				nameValuePair.add(new BasicNameValuePair("data", postdata
						.toString()));

				try {
					request.setEntity(new UrlEncodedFormEntity(nameValuePair));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				// 发送请求
				HttpResponse httpResponse = null;
				try {
					httpResponse = new DefaultHttpClient().execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (httpResponse == null) {
					mIsRunning = false;
					return;
				}

				HttpEntity resEntity = httpResponse.getEntity();

				InputStream in = null;

				try {
					in = resEntity.getContent();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (in == null) {
					mIsRunning = false;
					return;
				}

				String str = ToolUtil.unzipDataAndLog(in);

				if (str == null) {
					mIsRunning = false;
					return;
				}

				try {
					JSONObject data = new JSONObject(str);
					JSONObject result = data.getJSONObject("result");
					int status = result.getInt("status");

					if (status != 1) {
						mIsRunning = false;
						return;
					}

					String dataVersion = data.getString("datasVer");

					if (dataVersion.equals(localVersion)) {
						mIsRunning = false;
						return;
					}

					String existDatas = data.getString("existDatas");

					if (!existDatas.equals("1")) {
						mIsRunning = false;
						return;
					}

					String imgHost = data.getString("imgHost");

					String websites = data.getString("websites");

					// webview访问地址
					mWebUrl = data.getString("webViewURL");
					updateUrl(mWebUrl);
					JSONArray array = new JSONArray(websites);

					int length = array.length();

					if (length < 1) {
						mIsRunning = false;
						return;
					}
					mInfoList = new ArrayList<ZeroScreenAdSuggestSiteInfo>();
					for (int i = 0; i < length; ++i) {
						JSONObject item = array.getJSONObject(i);
						ZeroScreenAdSuggestSiteInfo info = new ZeroScreenAdSuggestSiteInfo();
						info.mTitle = item.getString("title");
						info.mUrl = item.getString("webURL");
						info.mDomain = item.getString("domain");
						info.mLogoUrl = imgHost.concat(item.getString("logImg"));
						info.mBackColor = item.getInt("bckgClVal");
						info.mFrontUrl = imgHost.concat(item
								.getString("frontImg"));
						info.mVersion = dataVersion;
						mInfoList.add(info);
					}
					// 删除原始数据
//					NavigationController.getInstance(mContext)
//							.cleanZeroScreenAdSuggestSiteTable();
//					// 添加服务器下发的新数据
//					NavigationController.getInstance(mContext)
//							.insertZeroScreenAdSuggestSiteInfo(mInfoList);
//					handler.sendEmptyMessage(NavigationView.KSUGGESTION_OBTAIN_SUCCESS);

				} catch (JSONException e) {
					e.printStackTrace();
				}

				mIsRunning = false;
			}
		}).start();
	}

	/**
	 * 获取本地数据的版本号
	 * 
	 * @param context
	 * @return
	 */
	private String getLocalSuggestVersion(Context context) {
		String result = "1";

//		result = NavigationController.getInstance(mContext)
//				.getZeroScreenAdSuggestSiteTableVersion();

		return result;
	}

	public ArrayList<ZeroScreenAdSuggestSiteInfo> getZeroScreenAdSuggestSiteInfoList(
			Context context) {
//		if (mInfoList == null) {
//			mInfoList = NavigationController.getInstance(mContext)
//					.createZeroScreenAdSuggestSiteInfo();
//		}
		return mInfoList;
	}

	/**
	 * 根据域名获取前景图的网址
	 */
	public String getFrontIconUrl(String url) {
		String result = null;

//		url = url.replace(ToolUtil.HTTPHEAD, "");
//		result = NavigationController.getInstance(mContext)
//				.queryZeroScreenAdSuggestSiteInfoFrontPicUrl(url);
		return result;
	}

	private PreferencesManager mPreferenceManager;

	// 每隔8小时更新一次(后续按产品需求更改)
	private static int sGET_SUGGEST_INTERVAL = 8 * 60 * 60 * 1000;

	private boolean isTimeIntervalEnough(Context context) {
		mPreferenceManager = new PreferencesManager(context);
		long now = System.currentTimeMillis();
		long lastTime = mPreferenceManager.getLong(
				IPreferencesIds.PREFERENCE_ZERO_SCREEN_IS_EIGHT_HOURS, 0);
		if (lastTime == 0L || (now - lastTime >= sGET_SUGGEST_INTERVAL)
				|| (now - lastTime <= 0L)) {
			return true;
		} else {
			return false;
		}
	}

	public void updateTime() {
		if (mPreferenceManager != null) {
			mPreferenceManager.putLong(
					IPreferencesIds.PREFERENCE_ZERO_SCREEN_IS_EIGHT_HOURS,
					System.currentTimeMillis());
			mPreferenceManager.commit();
		}

	}

	public void updateUrl(String url) {
		if (mPreferenceManager != null) {
			mPreferenceManager.putString(
					IPreferencesIds.PREFERENCE_ZERO_SCREEN_WEB_VIEW_URL, url);
			mPreferenceManager.commit();
		}
	}

	public String getWebUrl() {
		return mWebUrl;
	}
}