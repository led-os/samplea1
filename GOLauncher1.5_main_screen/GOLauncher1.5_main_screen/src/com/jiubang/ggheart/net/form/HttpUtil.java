package com.jiubang.ggheart.net.form;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;

import com.gau.utils.cache.compress.ZipUtils;
import com.go.util.StringUtil;
import com.go.util.file.FileUtil;

/**
 * 工具方法
 * @author laojiale
 *
 */
public final class HttpUtil {
	//是否压缩请求参数
	public static final int COMPRESSION_REQUEST_DATA = 1; //压缩数据
	public static final int NOT_COMPRESSION_REQUEST_DATA = 0; //不压缩数据
	//是否对提交的数据进行压缩 默认0:无处理;1:采用gzip压缩;
	public final static String KEY_HANDLE = "handle";
	//提交的请求数据将需要提交的json数据进行如下处理:.toString().getBytes("UTF-8")；然后进行压缩处理(根据需要而定)；最后对byte数据采用ISO-8859-1编码成字符串.
	public final static String KEY_DATA = "data";
	private static final int STRING_LENGTH = 0xFF;
	
	public static final String STR_API_EXTRA_DATE = "date";
	public static final String STR_API_EXTRA_LAUGUAGE = "lang";
	public static final String STR_API_EXTRA_ALPHABET = "alphabet";
	public static final String STR_API_EXTRA_CONTINENTID = "continentid";
	public static final String STR_API_EXTRA_COUNTRYID = "countryid";
	public static final String STR_API_EXTRA_STATEID = "stateid";
	public static final String STR_API_EXTRA_PROTOCOL_VERSION = "ps";
	public static final String STR_API_EXTRA_SYSTEM_VERSION = "sys";
	public static final String STR_API_EXTRA_KEYWORD = "k";
	//连接超时时间
	public static final int CONNECTION_TIMEOUT = 25000;
	//读取超时时间
	public static final int READ_TIMEOUT = 25000;
	//将客户端伪装成浏览器
	public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
	/**
	 * 打包请求的数据头信息(json对象)的数据
	 * 参看 {@link com.gau.go.launcherex.gowidget.weather.globaltheme.http.util.HttpConstant.HttpRequestHeadJson } 中的相关常量
	 * @param context
	 * @param pheadBean 
	 * @return JSONObject 出错时返回null
	 */
	/*public static JSONObject packagePheadJson(HttpRequestHeadBean pheadBean) {
		JSONObject pheadJson = new JSONObject();
		try {
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_LAUNCHERID, pheadBean.mLauncherid);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_LANG, pheadBean.mLang);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_LOCAL, pheadBean.mLocal);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_CHANNEL, pheadBean.mChannel);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_IMSI, pheadBean.mImsi);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_HASMARKET, pheadBean.mHasmarket);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_SYS, pheadBean.mSys);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_SDK, pheadBean.mSdk);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_DPI, pheadBean.mDpi);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_PVERSION, pheadBean.mPversion);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_NET, pheadBean.mNet);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_ANDROIDID, pheadBean.mAndroidid);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_CVERSION, pheadBean.mCversion);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_CVNUM, pheadBean.mCvnum);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_CLIENTID, pheadBean.mClientid);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_SBUY, pheadBean.mSbuy);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_OFFICICAL, pheadBean.mOfficial);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_VIP, pheadBean.mVip);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_MGTOKEN, pheadBean.mMgtoken);
			pheadJson.put(HttpConstant.HttpRequestHeadJson.KEY_NET_LOG, pheadBean.mNetlog);
		} catch (JSONException e) {
			pheadJson = null;
			if (Loger.isD()) {
				e.printStackTrace();
			}
		}

		return pheadJson;
	}*/

	/**
	 * 组装列表请求参数单元
	 * @param context
	 * @param httpRequestListBean
	 * @return
	 */
	/*public static JSONObject packageListRequestJson(HttpRequestListBean httpRequestListBean) {
		JSONObject listRequset = new JSONObject();
		try {
			listRequset.put(HttpConstant.HttpRequestListJson.KEY_TYPE_ID, httpRequestListBean.mTypeid);
			listRequset.put(HttpConstant.HttpRequestListJson.KEY_ITP, httpRequestListBean.mItp);
			listRequset.put(HttpConstant.HttpRequestListJson.KEY_MUST, httpRequestListBean.mMust);
			listRequset.put(HttpConstant.HttpRequestListJson.KEY_MARK, httpRequestListBean.mMark);
			listRequset.put(HttpConstant.HttpRequestListJson.KEY_PAGE_ID, httpRequestListBean.mPageid);
			listRequset.put(HttpConstant.HttpRequestListJson.KEY_ACCESS, httpRequestListBean.mAccess);
		} catch (JSONException e) {
			listRequset = null;
			if (Loger.isD()) {
				e.printStackTrace();
			}
		}
		return listRequset;
	}*/
	/**
	 * 向服务器获取数据,并返回json数据
	 * @param url 请求地址接口
	 * @param handle 是否压缩
	 * @param postDataString 需要提交的数据
	 * @return json数据，出错时返回null
	 */
	public static void postRequest(final Context context, final String url, final int handle, final String postDataString, final Request.RequestListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject resultJson = null;
				try {
					resultJson = postRequest(context, url, handle, postDataString);
					if (listener != null) {
						listener.onFinish(resultJson);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null) {
						listener.onException(e, resultJson);
					}
				}
			}
		}).start();
	}
	/**
	 * 向服务器获取数据,并返回json数据
	 * @param url 请求地址接口
	 * @param handle 是否压缩
	 * @param postDataString 需要提交的数据
	 * @return json数据，出错时返回null
	 */
	public static JSONObject postRequest(Context context, String url, int handle, String postDataString) throws Exception {
		String jsonString = null;
		if (handle == COMPRESSION_REQUEST_DATA) {
			try {
				//压缩请求数据(暂时不支持)
				postDataString = ZipUtils.compress(postDataString, HTTP.UTF_8, HTTP.ISO_8859_1);
			} catch (Exception e) {
				postDataString = null;
				throw e;
			}
		}
		if (null != postDataString) {
			Request request = new Request(url, "POST");
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair(KEY_HANDLE, handle + ""));
			postParams.add(new BasicNameValuePair(KEY_DATA, postDataString));
			request.setPostParams(postParams);
			// 开始请求网络数据
			Result result = new Result();
			HttpExecutor httpExecutor = HttpExecutorContext.getHttpExecutor();
			if (httpExecutor.checkNetwork(result, context)) {
				InputStream inputStream = null;
				inputStream = httpExecutor.doRefresh(url, request, result);
				if (inputStream != null) {
					try {
						jsonString = FileUtil.readInputStream(inputStream, HTTP.ISO_8859_1);
						jsonString = ZipUtils.unCompress(jsonString, HTTP.ISO_8859_1, HTTP.UTF_8);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			httpExecutor.release();
		}
		return new JSONObject(StringUtil.toString(jsonString));
	}
	
	/**
	 * 产生MD5加密串
	 * 
	 * @author huyong
	 * @param plainText
	 * @param charset
	 * @return
	 */
	private static String to32BitString(String plainText, String charset) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			if (charset != null) {
				md.update(plainText.getBytes(charset));
			} else {
				md.update(plainText.getBytes());
			}
			byte[] byteArray = md.digest();
			StringBuffer md5Buf = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(STRING_LENGTH & byteArray[i]).length() == 1) {
					md5Buf.append("0").append(Integer.toHexString(STRING_LENGTH & byteArray[i]));
				} else {
					md5Buf.append(Integer.toHexString(STRING_LENGTH & byteArray[i]));
				}
			}
			return md5Buf.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 产生32位md5加密字符串
	 * 
	 * @param s
	 *            待加密的字符串
	 * @return md5加密处理后的字符串
	 */
	public final static String mD5generator(String s) {
		// String charset =System.getProperties()
		return to32BitString(s, null);
	}
}
