package com.jiubang.ggheart.gorecomm.net;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

/**
 * 使用Android SDK默认方式发送Http请求
 * @author caoyaming
 *
 */
public class HttpRequestUtils {
	//请求网络连接超时时间
	private static final int NETWORK_CONNECTION_TIMEOUT = 30 * 1000;
	//读取网络数据超时时间
	private static final int NETWORK_SO_TIMEOUT = 30 * 1000;
	/**
	 * 执行Http请求(重载方法)
	 * @param httpUri 请求Uri
	 * @return httpResponse 请求相应对象
	 * @throws IOException 
	 * @throws Exception
	 */
	public static HttpResponse executeHttpRequest(String httpUri) throws IOException, Exception {
		return executeHttpRequest(httpUri, NETWORK_CONNECTION_TIMEOUT, NETWORK_SO_TIMEOUT);
	}
	/**
	 * 执行Http请求
	 * @param httpUri 请求Uri
	 * @param connectionTimeout 连接超时时长
	 * @param soTimeout 读取超时时长
	 * @return httpResponse 请求相应对象
	 * @throws IOException 
	 * @throws Exception
	 */
	public static HttpResponse executeHttpRequest(String httpUri, int connectionTimeout, int soTimeout) throws IOException, Exception {
		HttpResponse httpResponse = null;
		HttpGet httpRequest = new HttpGet(httpUri);
		try {
			BasicHttpParams httpParameters = new BasicHttpParams();
			//设置请求超时时间(毫秒)
			HttpConnectionParams.setConnectionTimeout(httpParameters, NETWORK_CONNECTION_TIMEOUT);   
			//设置读取数据超时时间(毫秒)
			HttpConnectionParams.setSoTimeout(httpParameters, NETWORK_CONNECTION_TIMEOUT); 
			//默认采用证书认证方式
			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			try {
				//执行请求
				httpResponse = httpClient.execute(httpRequest);
			} catch (Exception e) {
				//不采用证书认证方式
				httpClient = ConnectionManager.getNewHttpClient(httpParameters);
				//执行请求
				httpResponse = httpClient.execute(httpRequest);
			}
			//if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				//请求成功
			//}
		} catch (Exception e) {
			//其它错误
			throw e;
		}
		return httpResponse;
	}
}
