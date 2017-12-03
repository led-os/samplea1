package com.jiubang.ggheart.smartcard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.SparseArray;

import com.jiubang.ggheart.gorecomm.net.HttpRequestUtils;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * @author guoyiqing
 * 
 */
public class RecommIconPreLoader {

	private static final int CONNECT_TIME_OUT = 10000;
	private static final int READ_TIME_OUT = 10000;
	private static final String RECOMM_ICON_LOAD = "recomm_icon_load";
	private static RecommIconPreLoader sInstance;
	private Context mContext;
	private boolean mIsLoading;

	private RecommIconPreLoader(Context context) {
		mContext = context;
	}

	public static synchronized RecommIconPreLoader getServer(Context context) {
		if (sInstance == null) {
			sInstance = new RecommIconPreLoader(context);
		}
		return sInstance;
	}
	
	private boolean isWIFINetwork(Context context) {
		boolean result = false;
		if (context != null) {
			try {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm != null) {
					NetworkInfo networkInfo = cm.getActiveNetworkInfo();
					if (networkInfo != null
							&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI
							&& networkInfo.isConnected()) {
						result = true;
					}
				}
			} catch (NoSuchFieldError e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void preLoadIconAsync() {
		if (mIsLoading) {
			return;
		}
		new Thread(RECOMM_ICON_LOAD) {
			public void run() {
				List<Recommanditem> items = RecommInfoLoader.getLoader(mContext).loadLightGamesInfo();
				String url = null;
				for (Recommanditem recommanditem : items) {
					url = recommanditem.getIconUrl();
					loadFile(url, url2LocalPath(url));
				}
				SparseArray<List<Recommanditem>> folds = RecommInfoLoader.getLoader(mContext).loadFoldsInfo();
				int size = folds.size();
				for (int i = 0; i < size; i++) {
					items = folds.valueAt(i);
					for (Recommanditem recommanditem : items) {
						if (!isWIFINetwork(mContext)) {
							continue;          // 只有WIFI才会Load
						}
						url = recommanditem.getIconUrl();
						loadFile(url, url2LocalPath(url));
					}
				}
				mIsLoading = false;
			};
		}.start();
	}
	
	public boolean loadFile(String imgUrl, String destPath) {
		if (imgUrl == null || imgUrl.equals("")) {
			return false;
		}
		File file = new File(destPath);
		if (file.exists()) {
			return true;
		}
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
			return loadImagFromHttpClient(imgUrl, destPath);
		} else {
			return loadFromHttpConnection(imgUrl, destPath);
		}
	}

	private boolean loadFromHttpConnection(String imgUrl, String destPath) {
		InputStream inputStream;
		HttpURLConnection urlCon = null;
		OutputStream outputStream = null;
		try {
			urlCon = (HttpURLConnection) new URL(imgUrl).openConnection();
			urlCon.setConnectTimeout(CONNECT_TIME_OUT);
			urlCon.setReadTimeout(READ_TIME_OUT);
			inputStream = (InputStream) urlCon.getContent();
			outputStream = new FileOutputStream(destPath);
			int length = -1;
			byte[] bytes = new byte[1024];
			while ((length = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, length);
			}
			outputStream.flush();
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (urlCon != null) {
				urlCon.disconnect();
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 使用HttpClient方式从网络加载图片
	 * 
	 * @param imgUrl
	 *            图片下载地址
	 * @param destPath
	 * @return
	 */
	private boolean loadImagFromHttpClient(String imgUrl, String destPath) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			HttpResponse httpResponse = HttpRequestUtils
					.executeHttpRequest(imgUrl);
			if (httpResponse != null
					&& httpResponse.getStatusLine() != null
					&& (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK || httpResponse
							.getStatusLine().getStatusCode() == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION)
					&& httpResponse.getEntity() != null) {
				inputStream = httpResponse.getEntity().getContent();
				outputStream = new FileOutputStream(destPath);
				int length = -1;
				byte[] bytes = new byte[1024];
				while ((length = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, length);
				}
				outputStream.flush();
				return true;
			}
		} catch (IOException e) {
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
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public String url2LocalPath(String url) {
		if (url == null || url.equals("")) {
			return "";
		}
		String target = null;
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(url);
		target = m.replaceAll("").trim();
		int hashCode = target.hashCode();
		return LauncherEnv.Path.SMARTCARD_RECOMMEND_ICON_PATH + "/" + hashCode;
	}

}
