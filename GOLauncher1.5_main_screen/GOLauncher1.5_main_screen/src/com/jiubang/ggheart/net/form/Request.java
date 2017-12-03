package com.jiubang.ggheart.net.form;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.Context;

import com.jiubang.ggheart.appgame.appcenter.help.AppsNetConstant;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;

/**
 * 
 * <br>类描述: 业务请求
 * 
 * @author  liuwenqin
 * @date  [2012-9-17]
 */
public class Request {

	/**请求链接的地址*/
	private final String mUrl;
	/**以Get方式获取数据时的多个请求参数键值对*/
	private final ArrayList<Header> mHeaders;
	/**Post请求方式的上传数据 */
	private byte[] mPostData;
	/**Post请求方式是提交的表单数据**/
	private List<NameValuePair> mPostParams;
	/**数据请求方式。Get/Post*/
	private String mMethod;
	/**代表当前请求的ID，可以用在网络请求任务队列中，标识某个请求任务。暂时没用到*/
	@SuppressWarnings("unused")
	private final int mTransactionID;
	/**标识客户端是否需要伪装成浏览器。防止访问第三方服务器时，客户端被屏蔽*/
	private boolean mIsUseAgent;
	/**链接超时时间*/
	private int mConnectionTimeOut = HttpUtil.CONNECTION_TIMEOUT;
	/**读取超时时间*/
	private int mReadTimeOut = HttpUtil.READ_TIMEOUT;
	/**以Get方式获取数据时的多个请求参数键值对*/
	private final HashMap<String, String> mHttpHeaders;

	public Request(String url) {
		this.mUrl = url;
		this.mMethod = "GET";
		this.mHeaders = new ArrayList<Header>();
		this.mTransactionID = -1;
		this.mIsUseAgent = false;
		mHttpHeaders = new HashMap<String, String>();
	}

	public Request(String url, String method) {
		this.mUrl = url;
		this.mMethod = method;
		this.mHeaders = new ArrayList<Header>();
		this.mTransactionID = -1;
		this.mIsUseAgent = false;
		mHttpHeaders = new HashMap<String, String>();
	}

	public Request(String url, int connectionTimeOut, int readTimeOut) {
		this.mUrl = url;
		this.mMethod = "GET";
		this.mHeaders = new ArrayList<Header>();
		this.mTransactionID = -1;
		this.mIsUseAgent = false;
		if (connectionTimeOut > 0) {
			this.mConnectionTimeOut = connectionTimeOut;
		}
		if (readTimeOut > 0) {
			this.mReadTimeOut = readTimeOut;
		}
		mHttpHeaders = new HashMap<String, String>();
	}

	public boolean isUseAgent() {
		return mIsUseAgent;
	}

	public void setUseAgent(boolean isUseAgent) {
		this.mIsUseAgent = isUseAgent;
	}

	public int getConnectionTimeOut() {
		return mConnectionTimeOut;
	}

	public int getReadTimeOut() {
		return mReadTimeOut;
	}

	/**
	 * <br>功能简述: 添加请求参数
	 * @param key 请求参数键
	 * @param val 请求参数值
	 * @return 请求参数键值对
	 */
	public Header addHeader(String key, String val) {
		Header header = new Header(key, val);
		this.mHeaders.add(header);
		return header;
	}

	/**
	 * 添加Http请求的头参数
	 * @param key
	 * @param val
	 */
	public void addHttpHeader(String key, String val) {
		mHttpHeaders.put(key, val);
	}

	public HashMap<String, String> getHttpHeader() {
		return mHttpHeaders;
	}

	public void setPostData(byte[] postData) {
		this.mPostData = postData;
	}

	public byte[] getPostData() {
		return this.mPostData;
	}

	public List<NameValuePair> getPostParams() {
		return mPostParams;
	}

	public void setPostParams(List<NameValuePair> postParams) {
		this.mPostParams = postParams;
	}

	public void setMethod(String method) {
		this.mMethod = method;
	}

	public String getMethod() {
		return this.mMethod;
	}

	/**
	 * <br>功能简述: 组成完整的请求路径
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String composeCompleteURL() throws UnsupportedEncodingException {
		StringBuilder requestURL = new StringBuilder();
		requestURL.append(mUrl);
		int size = mHeaders.size();
		if (size > 0) {
			requestURL.append("?");
		}
		Header header = null;
		for (int i = 0; i < size; i++) {
			header = mHeaders.get(i);
			requestURL.append(URLEncoder.encode(header.getKey(), "utf-8"));
			requestURL.append("=");
			requestURL.append(URLEncoder.encode(header.getValue(), "utf-8"));
			if (i + 1 < size) { //当前参数不是最后一个
				requestURL.append("&");
			}
		}
		return requestURL.toString();
	}
	public void addDefaultHeader(Context context) {
		this.addHeader(HttpUtil.STR_API_EXTRA_LAUGUAGE, RecommAppsUtils.language(context));
		this.addHeader(HttpUtil.STR_API_EXTRA_SYSTEM_VERSION, android.os.Build.VERSION.RELEASE);
		this.addHeader(HttpUtil.STR_API_EXTRA_PROTOCOL_VERSION, String.valueOf(AppsNetConstant.CLASSIFICATION_INFO_PVERSION));
	}
	/**
	 * 
	 * @author caoyaming
	 *
	 */
	public static abstract class RequestListener {
		/**
		 * 请求完成 
		 * @param resultJson 返回的Json数据
		 */
		public abstract void onFinish(JSONObject resultJson);
		/**
		 * 请求失败
		 * @param resultJson 返回的Json数据
		 */
		public abstract void onException(Exception e, JSONObject resultJson);
	}
}