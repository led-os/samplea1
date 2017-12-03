package com.jiubang.ggheart.apps.desks.purchase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.utils.cache.utils.MD5;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * <br>类描述:收费功能促销等活动实现
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年12月13日]
 */
public class PromotionsControl {

	//	private static final String LIMIT_FREE_CHECK_URL= "http://gotest.3g.net.cn/guiActivity/webcontent/function/validatedt.jsp?";
	private static final String LIMIT_FREE_CHECK_URL = "http://guiActivity.3g.cn/guiActivity/webcontent/function/validatedt.jsp?";

	private static final String ACTIVE_CODE_CHECK_URL = "http://api.goforandroid.com/activationcode/nlValifyclient.do"; //正式
//	private static final String ACTIVE_CODE_CHECK_URL = "http://indchat.3g.cn:8080/activationcode/nlValifyclient.do"; // 测试
	private Context mContext;
	public static final int CHECK_LIMIT_FREE_OK = 1; //限免有效
	public static final int CHECK_LIMIT_FREE_INVALID = 2; //限免无效

	public static final int ACTIVE_SUCCESS = 200;
	public static final int ACTIVE_INVALID_CODE = 401;
	public static final int ACTIVE_LIMIT = 402; //超出限额
	public static final int ACTIVE_INVALID_DATE = 405; //不在活动日期

	public PromotionsControl(Context context) {
		mContext = context;
	}

	/**
	 * <br>功能简述:圣诞限免检查
	 * <br>功能详细描述:
	 * <br>注意:使用广播发送的原因在于FunctionPurchaseManager会在多个进程中有实例，需要通知所有的实例
	 */
	public void checkLimitFreemValid() {
		//先允许用户使用，和服务器确认后再校正结果
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				Intent intent = new Intent(FunctionPurchaseManager.ACTION_PURCHASE_LIMIT_FREEM);
				intent.putExtra("saveResult", false);
				mContext.sendBroadcast(intent);
				try {
					StringBuilder sb = new StringBuilder(LIMIT_FREE_CHECK_URL);
					String st = "20131217000000";
					String et = "20131217200000";
					sb.append("st=" + URLEncoder.encode(st, "UTF-8"));
					sb.append("&et=" + URLEncoder.encode(et, "UTF-8"));
					sb.append("&country="
							+ URLEncoder.encode(Machine.getLanguage(mContext) + "-"
									+ Machine.getCountry(mContext).toUpperCase(), "UTF-8"));
					sb.append("&channel=" + Statistics.getUid(mContext));
					sb.append("&goid="
							+ URLEncoder.encode(StatisticsManager.getGOID(mContext), "UTF-8"));
					sb.append("&vercode=" + Statistics.buildVersionCode(mContext));
					sb.append("&vername="
							+ URLEncoder.encode(AppUtils.getVersionNameByPkgName(mContext,
							        PackageName.PACKAGE_NAME), "UTF-8"));
					sb.append("&md5=" + MD5.encode(st + et + "jiubang.org"));
					HttpGet httpRequest = new HttpGet(sb.toString().trim());
					httpRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							15000);
					httpRequest.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15000);
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
						/*取出响应字符串*/
						JSONObject reponse = null;
						if (content != null) {
							reponse = new JSONObject(content);
							if (reponse != null) {
								int state = reponse.optInt("state");
								intent.putExtra("result", state);
								intent.putExtra("saveResult", true);
								mContext.sendBroadcast(intent);
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();;
	}

	public void checkActiveCode(final String code, final IActiveListener listener) {
		new Thread() {
			public void run() {
				JSONObject data = new JSONObject();
				int responseCode = ACTIVE_LIMIT;
				HttpURLConnection conn = null;
				try {
					data.put("pver", 1.0);
					data.put("version",
							AppUtils.getVersionCodeByPkgName(mContext, PackageName.PACKAGE_NAME));
					data.put("lang", Machine.getCountry(mContext));
					data.put("channel", Statistics.getUid(mContext));
					data.put("androidid", Machine.getAndroidId());
					data.put("code", code);
					data.put("pid", 1);
					URL url = new URL(ACTIVE_CODE_CHECK_URL);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("Accept-Encoding", "");
					((HttpURLConnection) conn).setRequestMethod("POST");
					conn.setDoOutput(true); //允许输出数据
					conn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded;charset=UTF-8");
					conn.setConnectTimeout(15000);
					conn.setReadTimeout(15000);
					OutputStream outStream = conn.getOutputStream();
					outStream.write(data.toString().getBytes("UTF-8"));
					outStream.flush();
					outStream.close();
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						try {
							BufferedReader reader = new BufferedReader(new InputStreamReader(
									conn.getInputStream()));
							String result = reader.readLine();
							if (result != null) {
								responseCode = Integer.valueOf(result);
							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
					if (listener != null) {
						listener.onResponse(responseCode);
					}
				}
			}
		}.start();
	}

	/**
	 * 
	 * <br>类描述:激活回调
	 * <br>功能详细描述:
	 * 
	 * @author  rongjinsong
	 * @date  [2014年1月7日]
	 */
	public interface IActiveListener {
		public void onResponse(int responseCode);
	}
}
