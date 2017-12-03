package com.jiubang.ggheart.net.form;

import java.io.InputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *  
 * <br>类描述: 网络操作类
 * 
 * @author  liuwenqin
 * @date  [2012-9-17]
 */
public abstract class HttpExecutor {
	/**网络代理地址*/
	private String mProxyHost;

	public HttpExecutor() {
	}

	/**
	 * <br>功能简述: 检查网络状态
	 * @param result
	 * @return
	 */
	public boolean checkNetwork(Result result, Context context) {
		boolean isNetworkAvailable = false;
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = null;
		info = mgr.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			int type = info.getType();
			if (type == ConnectivityManager.TYPE_MOBILE) {
				mProxyHost = android.net.Proxy.getDefaultHost();
				if (mProxyHost != null && mProxyHost.length() > 0) {
					result.setNetType(Result.TYPE_MOBILE_PROXY);
				} else {
					result.setNetType(Result.TYPE_MOBILE);
				}
			} else if (type == ConnectivityManager.TYPE_WIFI) {
				result.setNetType(Result.TYPE_WIFI);
				mProxyHost = null;
			} else if (type == ConnectivityManager.TYPE_WIMAX) {
				result.setNetType(Result.TYPE_WIMAX);
				mProxyHost = null;
			} else if (type == ConnectivityManager.TYPE_ETHERNET) {
				result.setNetType(Result.TYPE_ETHERNET);
				mProxyHost = null;
			} else if (type == ConnectivityManager.TYPE_MOBILE_DUN) {
				result.setNetType(Result.TYPE_MOBILE_DUN);
				mProxyHost = null;
			} else {
				result.setNetType(Result.TYPE_UNKNOW);
			}
			isNetworkAvailable = true;
		} else {
			result.setNetType(Result.TYPE_NETWORK_DOWN);
			mProxyHost = null;
			isNetworkAvailable = false;
		}
		return isNetworkAvailable;
	}

	/**
	 * <br>功能简述: 执行网络请求
	 * @param requestUrl 请求URL
	 * @param request 请求对象
	 * @param result 请求结果对象
	 */
	public abstract InputStream doRefresh(String requestUrl, Request request, Result result);
	
	/**
	 * <br>功能简述: 执行网络请求
	 * @param requestUrl 请求URL
	 * @param request 请求对象
	 * @param result 请求结果对象
	 * @return 网络回应的数据文本，如果连接出错时，返回值为null
	 */
	public abstract String executeConnectNetwork(String requestUrl, Request request, Result result);

	/**
	 * <br>功能简述: 关闭连接对象，释放资源
	 */
	public abstract void release();

}