package com.jiubang.ggheart.net;

import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
/**
 * 该类是为了解决在http请求时报javax.net.ssl.SSLPeerUnverifiedException: No peer certificate(无效证书)的异常
 * @author caoyaming
 *
 */
public class ConnectionManager {
	public static HttpClient getNewHttpClient(HttpParams params) {
    	try {
    		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    		trustStore.load(null, null);
    		SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
    		//允许所有主机的验证
    		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    		if (params == null) {
    			params = new BasicHttpParams();
    		}
    		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
    		//设置http https支持
    		SchemeRegistry registry = new SchemeRegistry();
    		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    		registry.register(new Scheme("https", sf, 443));

    		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
    		return new DefaultHttpClient(ccm, params);
    	} catch (Exception e) {
    		return new DefaultHttpClient();
    	}
	} 
}
