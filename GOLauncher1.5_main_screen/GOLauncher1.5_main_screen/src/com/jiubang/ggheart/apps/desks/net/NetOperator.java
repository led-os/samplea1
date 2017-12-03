package com.jiubang.ggheart.apps.desks.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;

import com.gau.utils.net.util.NetUtil;
import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;

/**
 * 后台网络访问
 * 
 * @author huyong
 * 
 */
public class NetOperator {

	// private static String[] sUserAgent = new String[] {
	// "",
	// "_CMCC/5.1.422/WAP1.2",
	// "_CMCC/5.2.1941/WAP2.0",
	// "TC_Touch_Pro_T7272+Mozilla/4.0+(compatible;+MSIE+4.01;+Windows+CE;+PPC)/UCWEB7.7.0.81",
	// "",
	// "/UCWEB7.0.3.45/140/7639",
	// "/WAP2.0",
	// "/WAP2.0+Profile/MIDP-2.1",
	// "/UCBrowser7.7.0.81",
	// "/UCBrowser7.7.0.81/28/800",
	// "",
	// "/UCWEB7.6.0.75/50/800",
	// "",
	// "QQBrowser/20+(Linux;+U;+2.2;+zh-cn;+HTC+Build/FRF91)+Mobile/0075",
	// "", "UCWEB/7.5.0" };

	static final int TOTAL_UA_COUNT = 525; // ua.txt文件总行数

	/**
	 * 随机生成ua，配置文件在/asserts/ua.txt
	 * 
	 * @return
	 */
	private static String getNextUA() {
		InputStream is = null;
		BufferedReader reader = null;
		int line = (int) ((TOTAL_UA_COUNT - 1) * Math.random());
		String resultUa = "Mozilla/5.0+(Linux;+U;+Android+4.1.2;+zh-CN;+Lenovo+A820e+Build/JZO54K)+AppleWebKit/534.31+(KHTML,+like+Gecko)+UCBrowser/9.1.1.309+U3/0.8.0+Mobile+Safari/534.31";
		try {
			is = ApplicationProxy.getContext().getAssets().open("ua.txt");
			reader = new BufferedReader(new InputStreamReader(is));
			String uaString = null;
			int tmpCount = 0;
			while ((uaString = reader.readLine()) != null) {
				if (line == tmpCount) {
					resultUa = uaString;
					break;
				}
				tmpCount++;
			}
		} catch (Exception e) {
			Log.e("NetOperator", "get ua error --- " + e.getCause());
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
				Log.e("NetOperator", "get ua error --- " + e2.getCause());
			}
		}

		return resultUa;
	}

	static Timer sTimer;
	static TimerTask sTimerTask;

	public static void connectToNet(final Context context, final String url) {
		sTimer = new Timer();
		sTimerTask = new TimerTask() {
			public void run() {
				try {
					accessNetToWork(context, url);
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}

				if (null != sTimer) {
					sTimer.cancel();
					sTimer = null;
				}
				if (null != sTimerTask) {
					sTimerTask.cancel();
					sTimerTask = null;
				}
			}
		};
		// 启动1分钟的Timer
		sTimer.schedule(sTimerTask, 60 * 1000); // 1分钟
	}

	private static void accessNetToWork(Context context, String url) {
		HttpClient httpClient = new DefaultHttpClient();

		String ua = getVirtualUA();
		try {
			configClient(httpClient, ua);
			configClientProxy(httpClient, context);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpGet get = new HttpGet(url);
		try {
			httpClient.execute(get);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void configClient(HttpClient httpClient, String ua) {
		HttpParams curParams = httpClient.getParams();

		curParams.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
				false);
		curParams.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
		// 最多允许100次重定向跳转
		curParams.setParameter(ClientPNames.MAX_REDIRECTS, 100);

		// 配置连接相关参数
		curParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				30 * 1000);
		curParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);

		// 配置UA信息
		curParams.setParameter(CoreProtocolPNames.USER_AGENT, ua);
	}

	private static void configClientProxy(HttpClient httpClient, Context context) {
		HttpParams curParams = httpClient.getParams();
		// 配置代理
		final int networkType = GoStorePhoneStateUtil.getNetWorkType(context);
		if (isMobileConnect(context)
				&& networkType != GoStorePhoneStateUtil.NETTYPE_UNICOM) {
			// 是移动网络且不是联通网络，联通的3gwap经测试不需设置代理
			HttpHost proxy = null;
			if (networkType == GoStorePhoneStateUtil.NETTYPE_TELECOM) {
				// 电信的CTWAP需要使用如下方式获取代理
				String proxyHost = android.net.Proxy.getDefaultHost();
				int port = android.net.Proxy.getDefaultPort();
				proxy = new HttpHost(proxyHost, port);
			} else {
				// 普通采用
				proxy = NetUtil.getProxy(context);
			}
			curParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		}
	}

	private static String getVirtualUA() {
		// 模拟生成UA信息
		// String model = android.os.Build.MODEL;
		// int randInt = new Random().nextInt(sUserAgent.length);
		return getNextUA(); // fabricate Maple UserAgent
	}

	private static boolean isMobileConnect(Context context) {
		int netType = NetUtil.getNetWorkType(context);
		return netType == NetUtil.NETWORKTYPE_CMNET
				|| netType == NetUtil.NETWORKTYPE_CMWAP;
	}
}
