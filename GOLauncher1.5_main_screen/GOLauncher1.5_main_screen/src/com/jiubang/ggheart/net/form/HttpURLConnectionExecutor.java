package com.jiubang.ggheart.net.form;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;

/**
 * 
 * <br>类描述: 使用HttpURLConnection方式联网
 * 
 * @author  liuwenqin
 * @date  [2012-10-22]
 */
public class HttpURLConnectionExecutor extends HttpExecutor {
	private HttpURLConnection mHttpURLConnection;
	private String mProxyHost;
	
	private static final String POST_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";

	public HttpURLConnectionExecutor() {
		super();
	}

	/** {@inheritDoc} */

	@Override
	public InputStream doRefresh(String requestUrl, Request request, Result result) {
		InputStream inputStream = null;
		if (requestUrl != null && !requestUrl.trim().equals("") && requestUrl.startsWith("http://")) {
			URL url = null;
			try {
				url = new URL(requestUrl);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				// 请求URL不合法
				result.setStatus(HttpRequestStatus.REQUEST_FAILED);
				result.setErrorType(HttpRequestStatus.REQUEST_INVALID_URL);
			}
			if (url != null) {
				if (TextUtils.isEmpty(mProxyHost)) {
					try {
						mHttpURLConnection = (HttpURLConnection) url.openConnection();
					} catch (IOException e) {
						e.printStackTrace();
						result.setStatus(HttpRequestStatus.REQUEST_FAILED);
						result.setErrorType(HttpRequestStatus.REQUEST_IO_EXCEPTION);
					}
				} else {
					java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
							new InetSocketAddress(android.net.Proxy.getDefaultHost(),
									android.net.Proxy.getDefaultPort()));
					try {
						mHttpURLConnection = (HttpURLConnection) url.openConnection(proxy);
					} catch (IOException e) {
						e.printStackTrace();
						result.setStatus(HttpRequestStatus.REQUEST_FAILED);
						result.setErrorType(HttpRequestStatus.REQUEST_IO_EXCEPTION);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						result.setStatus(HttpRequestStatus.REQUEST_FAILED);
						result.setErrorType(HttpRequestStatus.REQUEST_PROTOCOL_EXCEPTION);
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
						result.setStatus(HttpRequestStatus.REQUEST_FAILED);
						result.setErrorType(HttpRequestStatus.REQUEST_PROTOCOL_EXCEPTION);
					}
				}
				if (mHttpURLConnection != null) {
					if (request.isUseAgent()) {
						mHttpURLConnection.setRequestProperty("Use-Agent", HttpUtil.USER_AGENT);
					}
					if (!result.getBGzip()) {
						mHttpURLConnection.setRequestProperty("Accept-Encoding", "identity");
					} else {
						mHttpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
					}
					if (request.getPostData() == null && request.getPostParams() != null) {
						//设置请求头为表单数据（重要）
						mHttpURLConnection.setRequestProperty("Content-Type", POST_CONTENT_TYPE);
					}
					mHttpURLConnection.setConnectTimeout(HttpUtil.CONNECTION_TIMEOUT);
					mHttpURLConnection.setReadTimeout(HttpUtil.READ_TIMEOUT);
					mHttpURLConnection.setDoInput(true);
					if (request.getMethod().equalsIgnoreCase("POST")) {
						mHttpURLConnection.setDoOutput(true);
						mHttpURLConnection.setChunkedStreamingMode(0);
						OutputStream outputStream = null;
						try {
							outputStream = new BufferedOutputStream(
									mHttpURLConnection.getOutputStream());
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (outputStream != null) {
							boolean postDataNormal = true; //上传数据正常
							byte[] data = request.getPostData();
							if (data == null) {
								List<NameValuePair> pairs = request.getPostParams();
								try {
									data = parseParamsToByte(pairs);
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}
							try {
								outputStream.write(data); //包含网络连接
								outputStream.flush();
							} catch (IOException e) {
								e.printStackTrace();
								postDataNormal = false;
							}
							if (postDataNormal) {
								try {
									inputStream = new BufferedInputStream(
											mHttpURLConnection.getInputStream());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					} else if (request.getMethod().equalsIgnoreCase("GET")) {
						boolean connectionNormal = true; //连接是否正常
						try {
							mHttpURLConnection.connect();
						} catch (IOException e) {
							e.printStackTrace();
							result.setStatus(HttpRequestStatus.REQUEST_FAILED);
							result.setErrorType(HttpRequestStatus.REQUEST_TIMEOUT);
							connectionNormal = false;
						}
						if (connectionNormal) {
							try {
								inputStream = mHttpURLConnection.getInputStream();
							} catch (IOException e) {
								e.printStackTrace();
								result.setStatus(HttpRequestStatus.REQUEST_FAILED);
								result.setErrorType(HttpRequestStatus.REQUEST_IO_EXCEPTION);
							}
							if (inputStream != null) {
								if (!result.getBGzip()) {
									//检查是否需要解压
									Map<String, List<String>> headerFields = mHttpURLConnection
											.getHeaderFields();
									if (headerFields != null) {
										for (String key : headerFields.keySet()) {
											if (key != null
													&& (key.equalsIgnoreCase("Content-Encoding") || key
															.equalsIgnoreCase("X_Enc"))) {
												List<String> vals = headerFields.get(key);
												if (vals.indexOf("gzip") != -1
														|| vals.indexOf("x-gzip") != -1) {
													result.setBGzip(true);
												}
											}
										}
									}
								}
								if (result.getBGzip()) {
									try {
										inputStream = new GZIPInputStream(inputStream);
									} catch (IOException e) {
										//解压数据流出错，关闭输入流，置空
										e.printStackTrace();
										try {
											inputStream.close();
										} catch (IOException e1) {
											e1.printStackTrace();
										}
										inputStream = null;
										result.setStatus(HttpRequestStatus.REQUEST_FAILED);
										result.setErrorType(HttpRequestStatus.REQUEST_ZIP_ERROR);
									}
								}
							}
						}
					}
				}
			}
		}
		return inputStream;
	}

	/**
	 * 将NameValuePair的数据转为byte数组
	 * @param pairs
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private byte[] parseParamsToByte(List<NameValuePair> pairs) throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();
		final int length = pairs.size();
		for (int i = 0; i < length; i++) {
			NameValuePair pair = pairs.get(i);
			buffer.append(pair.getName());
			buffer.append("=");
			buffer.append(pair.getValue());
			if (i != length - 1) {
				buffer.append("&");
			}
		}
		String string = buffer.toString();
		return buffer.toString().getBytes(HTTP.UTF_8);
	}
	
	public void release() {
		if (mHttpURLConnection != null) {
			mHttpURLConnection.disconnect();
		}
	}

	@Override
	public String executeConnectNetwork(String requestUrl, Request request, Result result) {
		// TODO Auto-generated method stub
		return null;
	}
}