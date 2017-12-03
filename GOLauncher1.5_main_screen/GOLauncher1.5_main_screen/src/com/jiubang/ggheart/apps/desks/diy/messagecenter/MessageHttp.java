package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.gau.go.launcherex.R;
import com.gau.utils.cache.encrypt.CryptTool;
import com.gau.utils.cache.utils.CacheFileUtils;
import com.go.proxy.ApplicationProxy;
import com.go.util.AppUtils;
import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.go.util.log.LogUnit;
import com.go.util.window.WindowControl;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageBaseBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageContentBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean.MessageHeadBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageStatisticsBean;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.CouponBean;
import com.jiubang.ggheart.apps.desks.net.NetOperator;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * 类描述: MessageHttp类
 * 功能详细描述: 主要处理一些网络请求和解析JSon数据的操作
 * @date  [2012-9-28]
 */
public class MessageHttp {

	private static final String SUPER_THEME_MSG_ITEM_SPITE = "super_theme_msg_item";
	protected static final String THEME_MSG_COMING_ACTION = "com.gau.go.launcherex.theme.msg.coming.action";
	public static final String PVERSION = "3.4"; // 请求协议版本号
	private Context mContext;
	private PraseListener mListener;
	private static final int POST_TYPE_GET_LIST = 1; // 获取消息列表
	private static final int POST_TYPE_GET_MSG = 2; // 获取一个消息内容
	private static final int POST_TYPE_GET_URL = 3; //获取桌面后台链接
	private static final int POST_TYPE_GET_COUPON = 4; //获取一个优惠券

	public static final int STATUS_IDLE = 0;
	public static final int STATUS_POSTING = 1;
	public static final int STATUS_GETING_MSG = 2;
	public static final int STATUS_GETING_URL = 3;
	private HttpPost mHttpPost;
	private int mStatus;

	private static final int REQUEST_TIMEOUT = 7 * 1000; //设置请求超时7秒钟  
	private static final int RESPONSE_TIMEOUT = 7 * 1000;  //设置等待数据超时时间7秒钟  
	private static final String SUPER_THEME_MSG_EXTRA = "super_theme_msg_extra";
	private static String sCHANNAEL = null;
	private static String sURL = ConstValue.sHosturlBase;
	private byte[] mLock = new byte[0];
	
	/**
	 * 推荐第三方应用的消息id以及统计id
	 */
	private String mMsgId = null;
	private String mMapId = null;

	public MessageHttp(Context context) {
		mContext = context;
	}

	private void compoundNameValuePairs(Context context, JSONObject data, int auto) {
		if (context != null) {
			if (data == null) {
				data = new JSONObject();
			}
			// vps
			String imei = GoStorePhoneStateUtil.getVirtualIMEI(context);
			try {
				data.put("vps", HttpUtil.getVps(context, imei));
				data.put("launcherid", imei);
				// channel
				sCHANNAEL = GoStorePhoneStateUtil.getUid(context);
				if (ConstValue.DEBUG
						&& FileUtil.isFileExist(LauncherEnv.Path.MESSAGECENTER_PATH
								+ "properties.txt")) {
					String info = EncodingUtils.getString(
							FileUtil.getByteFromSDFile(LauncherEnv.Path.MESSAGECENTER_PATH
									+ "properties.txt"), "UTF-8");
					if (info != null && info.split("#").length == 2) {
						String[] properties = info.split("#");
						sCHANNAEL = properties[0];
						sURL = properties[1];
					}
				}

				if (ConstValue.DEBUG) {
					LogUnit.diyInfo(ConstValue.MSG_TAG, "uid = " + sCHANNAEL);
					LogUnit.diyInfo(ConstValue.MSG_TAG, "url = " + sURL);
					LogUnit.diyInfo(ConstValue.MSG_TAG,
							"屏幕密度： " + WindowControl.getDensity(context));
				}
				data.put("channel", sCHANNAEL);
				// lang 带上区域信息，如zh_cn,en_us
				Locale locale = Locale.getDefault();
				String language = String.format("%s_%s", locale.getLanguage().toLowerCase(), locale
						.getCountry().toLowerCase());
				data.put("lang", language);
				//				data.put("local", locale.getCountry().toLowerCase());
				data.put("local", Machine.getCountry(context));
				// pversion 协议版本号
				data.put("pversion", PVERSION);
				// isfee
				String isFee = "1";
				if (GoStorePhoneStateUtil.isCnUser(context) || !GoAppUtils.isMarketExist(context)) {
					// 若是中国用户，或是未安装电子市场，则均为不能收费用户。
					isFee = "0";
				}
				data.put("isfee", isFee);
				String curVersion = context.getString(R.string.curVersion);
				int index = curVersion.indexOf("beta");

				if (index > 0) {
					curVersion = curVersion.substring(0, index);
				} else if ((index = curVersion.indexOf("Beta")) > 0) {
					curVersion = curVersion.substring(0, index);
				}
				data.put("cversion", curVersion);

				PackageManager pm = context.getPackageManager();
				PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
				data.put("vcode", info.versionCode);
				data.put("Isauto", auto);
				//				data.put("pid", "d6addc867c35e17ece2d286d892e9890");
				data.put("pid", null);
				data.put("spappcenter", 1);
				data.put("density", WindowControl.getDensity(context));
				String androidId = getAndroidId(context);
				data.put("androidid", androidId);
				boolean requestOrNot = getFirstRequest();
				if (requestOrNot) { //不是第一次请求
					data.put("isfirst", 0);
				} else { //第一次请求
					saveFirstRequest();
					data.put("isfirst", 1);
				}
				data.put("goid", StatisticsManager.getGOID(context));
				
				//判断是否收费用户，是否选择去广告功能不发消息通知
				if (DeskSettingUtils.isNoAdvert()) {
					data.put("ispay", 1);
				} else {
					data.put("ispay", 2);
				}
				
				data.put("imsi", getSimOperator(context));
				data.put("regtime", Machine.checkInstallStamp(context));
				
				//4.13版本新增sdklevel字段
//				Log.d("TAG", "--------sdklevel : " + Machine.getAndroidSDKVersion() + "; android id : " + androidId);
				data.put("sdklevel", Machine.getAndroidSDKVersion());
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	private static String getAndroidId(Context context) {
		String androidId = null;
		if (context != null) {
			androidId = Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
		}
		return androidId;

	}
	
	/**
	 * 获取运营商编码
	 * @param context
	 * @return
	 */
	private String getSimOperator(Context context) {
		// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
		String simOperator = "000";
		try {
			if (context != null) {
				// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
				TelephonyManager manager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				simOperator = manager.getSimOperator();
			}
		} catch (Throwable e) {
			// TODO: handle exception
		}

		return simOperator;
	}

	public synchronized void postUpdateRequest(int auto) {
		if (mStatus != STATUS_IDLE) {
			return;
		}
		if (null != mContext) {
			mStatus = STATUS_POSTING;
			JSONObject phead = getPhead(mContext, null, auto);
			JSONObject postdataJsonObject = getPostJson(mContext, phead, POST_TYPE_GET_LIST, null);
			String url = HttpUtil.getUrl(ConstValue.URL_GET_MSG_LIST, sURL);
			mHttpPost = new HttpPost(url);
			// 绑定到请求 Entry
			StringEntity se;
			JSONObject msgList = null;
			MessageListBean msgsBean = null;
			try {
				se = new StringEntity(postdataJsonObject.toString());
				mHttpPost.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(mHttpPost);

				msgList = parseMsgListStreamData(httpResponse.getEntity().getContent());
				msgsBean = new MessageListBean();

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} finally {
				if (mHttpPost != null) {
					mHttpPost = null;
				}
			}
			if (parseMsgList(msgList, msgsBean) && mListener != null) {
				mListener.listParseFinish(true, msgsBean);
			} else if (mListener != null) {
				mListener.listParseFinish(false, null);
			}
		}
		mStatus = STATUS_IDLE;
	}

	public synchronized void postGetUrlRequest() {
		if (mStatus != STATUS_IDLE) {
			return;
		}
		if (null != mContext) {
			mStatus = STATUS_GETING_URL;
			JSONObject head = getPhead(mContext, null, 0);
			JSONObject postdataJson = getPostJson(mContext, head, POST_TYPE_GET_URL, null);
			if (ConstValue.DEBUG) {
				Log.i(ConstValue.MSG_TAG, postdataJson.toString());
			}
			String url = HttpUtil.getUrl(ConstValue.URL_GET_URL, sURL);
			if (ConstValue.DEBUG) {
				LogUnit.diyInfo(ConstValue.MSG_TAG, "postGetUrlRequest url = " + url);
			}
			mHttpPost = new HttpPost(url);
			StringEntity se;
			JSONObject msg = null;
			JSONObject sUrls = null;
			try {
				se = new StringEntity(postdataJson.toString());
				mHttpPost.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(mHttpPost);

				msg = parseMsgContentStreamData(httpResponse.getEntity().getContent());
				if (msg != null && msg.has("surls")) {
					sUrls = msg.getJSONObject("surls");
					if (sUrls != null) {
						if (sUrls.has("2")) {
							String getUrl = sUrls.getString("2");
							if (getUrl != null && !getUrl.equals("")) {
								NetOperator.connectToNet(ApplicationProxy.getContext(), getUrl);
							}
						}
						if (sUrls.has("3")) {
							String getFacebookInfo = sUrls.getString("3");
							saveFacebookInfo(getFacebookInfo);
						}
					}
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} finally {
				if (mHttpPost != null) {
					mHttpPost = null;
				}
			}
		}
		mStatus = STATUS_IDLE;
	}
	
	/**
	 * 根据优惠券号获取优惠券
	 */
	public synchronized void postGetCouponRequest(String couponId) {
		if (mStatus != STATUS_IDLE) {
			return;
		}
		if (null != mContext) {
			CouponBean coupon = null;
			boolean bool = false;
			int status = -1;
			mStatus = STATUS_GETING_URL;
			JSONObject head = getPhead(mContext, null, 0);
			JSONObject postdataJson = getPostJson(mContext, head, POST_TYPE_GET_COUPON, couponId);
			if (ConstValue.DEBUG) {
				Log.i(ConstValue.MSG_TAG, postdataJson.toString());
			}
			String url = HttpUtil.getUrl(ConstValue.URL_GET_COUPON, sURL);
			if (ConstValue.DEBUG) {
				LogUnit.diyInfo(ConstValue.MSG_TAG, "postGetCouponRequest url = " + url);
			}
			mHttpPost = new HttpPost(url);
			StringEntity se;
			JSONObject msg = null;
			try {
				se = new StringEntity(postdataJson.toString());
				mHttpPost.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(mHttpPost);

				msg = parseMsgContentStreamData(httpResponse.getEntity().getContent());
				if (msg != null) {
					status = msg.getInt(MessageListBean.TAG_STATUS);
					if (status == 1) { //获取优惠券成功
						JSONObject yhj = msg.getJSONObject(MessageBaseBean.TAG_MSG_COUPON);
						String ycode = yhj.getString(MessageBaseBean.TAG_MSG_COUPON_CODE);
						int value = yhj.getInt(MessageBaseBean.TAG_MSG_COUPON_VALUE);
						String eTime = yhj.getString(MessageBaseBean.TAG_MSG_PROMOTION_ETIME);
						String sTime = yhj.getString(MessageBaseBean.TAG_MSG_PROMOTION_STIME);
						coupon = new CouponBean(ycode, value, sTime, eTime);
					}
					bool = true;
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} finally {
				if (mHttpPost != null) {
					mHttpPost = null;
				}
				if (mListener != null) {
					mListener.couponParseFinish(bool, status, coupon);
				}
			}
		}
		mStatus = STATUS_IDLE;
	}
	
	/**
	 * 功能预告请求
	 * @return 返回功能预告的链接
	 */
	public synchronized String postGetFuncForeUrlRequest() {
		if (mStatus != STATUS_IDLE) {
			return null;
		}
		
		String funcForeUrl = null;
		if (null != mContext) {
			mStatus = STATUS_GETING_URL;
			JSONObject head = getPhead(mContext, null, 0);
			JSONObject postdataJson = getPostJson(mContext, head, POST_TYPE_GET_URL, null);
			if (ConstValue.DEBUG) {
				Log.i(ConstValue.MSG_TAG, postdataJson.toString());
			}
			String url = HttpUtil.getUrl(ConstValue.URL_GET_URL, sURL);
			if (ConstValue.DEBUG) {
				LogUnit.diyInfo(ConstValue.MSG_TAG, "postGetUrlRequest url = " + url);
			}
			mHttpPost = new HttpPost(url);
			StringEntity se;
			JSONObject msg = null;
			JSONObject sUrls = null;
			try {
				se = new StringEntity(postdataJson.toString());
				mHttpPost.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(mHttpPost);

				msg = parseMsgContentStreamData(httpResponse.getEntity().getContent());
				if (msg != null && msg.has("surls")) {
					sUrls = msg.getJSONObject("surls");
					if (sUrls != null) {
						if (sUrls.has("4")) {
							String getUrl = sUrls.getString("4");
							if (getUrl != null && !getUrl.equals("")) {
								funcForeUrl = getUrl;
							}
						}
					}
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} finally {
				if (mHttpPost != null) {
					mHttpPost = null;
				}
			}
		}
		mStatus = STATUS_IDLE;
		return funcForeUrl;
	}

	// public void postUpdateRequest(){
	// if( mStatus != STATUS_IDLE )
	// {
	// return;
	// }
	// if(null != mContext){
	// JSONObject obj = new JSONObject();
	//
	// byte[] postData = getPostData(mContext, obj,POST_TYPE_GET_LIST,null);
	// THttpRequest request;
	// try {
	// String url = HttpUtil.getUrl(ConstValue.URL_GET_MSG_LIST);
	// request = new THttpRequest(url, postData, new IConnectListener() {
	//
	// @Override
	// public void onStart(THttpRequest request) {
	// // TODO Auto-generated method stub
	// mStatus = STATUS_POSTING;
	// }
	//
	// @Override
	// public void onFinish(THttpRequest request, IResponse response) {
	// // TODO Auto-generated method stub
	// if(response.getResponseType() == IResponse.RESPONSE_TYPE_BYTEARRAY){
	// JSONObject jsonObject = (JSONObject) response.getResponse();
	// MessageListBean msgsBean = new MessageListBean();
	// if(praseMsgList(jsonObject,msgsBean) && mListener != null)
	// {
	// mListener.listPraseFinish(true,msgsBean);
	// }
	// }
	// mStatus = STATUS_IDLE;
	// }
	//
	// @Override
	// public void onException(THttpRequest request, int reason) {
	// // TODO Auto-generated method stub
	// if(mListener != null)
	// {
	// mListener.listPraseFinish(false,null);
	// }
	// mStatus = STATUS_IDLE;
	//
	// }
	// });
	// MessageListStreamHttpOperator operator = new
	// MessageListStreamHttpOperator();
	// request.setOperator(operator);
	// SimpleHttpAdapter.getInstance(mContext).addTask(request);
	// } catch (IllegalArgumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (URISyntaxException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	// /**
	// *
	// * @param context
	// * @param nameValuePairs
	// * @param msgId 消息ID
	// * @return
	// */
	public byte[] getPostData(Context context, JSONObject phead, int type, String msgId) {
		if (phead == null) {
			phead = new JSONObject();
		}
		compoundNameValuePairs(context, phead, 0);
		byte[] postData = null;
		try {
			JSONObject request = new JSONObject();
			request.put("phead", phead);
			if (type == POST_TYPE_GET_MSG) {
				request.put("id", msgId);
			} else {
				long timeStamp = HttpUtil.getLastUpdateMsgTime(context);
				request.put("lts", timeStamp);
			}
			postData = request.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return postData;
	}

	/**
	 * <br>功能简述:头信息
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param phead
	 * @param auto
	 * @return
	 */
	public JSONObject getPhead(Context context, JSONObject phead, int auto) {
		if (phead == null) {
			phead = new JSONObject();
		}
		compoundNameValuePairs(context, phead, auto);
		return phead;
	}

	public static JSONObject getPostJson(Context context, JSONObject phead, int type, String msgId) {
		JSONObject request = new JSONObject();
		// byte[] postData = null;
		try {
			request.put("phead", phead);
			if (type == POST_TYPE_GET_MSG) {
				request.put("id", msgId);
			} else if (type == POST_TYPE_GET_URL) {
				request.put("types", "2#3#4");
			} else if (type == POST_TYPE_GET_COUPON) {
				request.put("ycode", msgId);
			} else {
				long timeStamp = HttpUtil.getLastUpdateMsgTime(context);
				request.put("lts", timeStamp);
			}
			if (ConstValue.DEBUG) {
				//				Log.i(ConstValue.MSG_TAG, "request = " + request.toString());
				LogUnit.diyInfo(ConstValue.MSG_TAG, "requestToServer : " + request.toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return request;
	}

	private boolean parseMsgList(JSONObject obj, MessageListBean bean) {
		boolean ret = false;
		if (obj == null) {
			return ret;
		}

		try {
			long lts = obj.getLong(MessageListBean.TAG_LTS);
			JSONArray array = obj.getJSONArray(MessageListBean.TAG_MSGS);
			HttpUtil.saveLastUpdateMsgTime(mContext, lts);
			List<JSONObject> themeJsonObjects = new ArrayList<JSONObject>();
			bean.praseMsgsHead(array, themeJsonObjects);
			handleThemeMsg(themeJsonObjects);
			ret = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private void handleThemeMsg(List<JSONObject> themeMsgs) {
		if (themeMsgs.size() > 0) {
			StringBuilder sBuilder = new StringBuilder();
			for (JSONObject object : themeMsgs) {
				sBuilder.append(object.toString());
				sBuilder.append(SUPER_THEME_MSG_ITEM_SPITE);
			}
			CacheFileUtils.appendToFile(LauncherEnv.Path.MSG_THEME_PATH,
					sBuilder.toString());
			if (mContext != null) {
				Intent intent = new Intent(THEME_MSG_COMING_ACTION);
				intent.putExtra(SUPER_THEME_MSG_EXTRA, sBuilder.toString());
				mContext.sendBroadcast(intent);
			}
		}
	}
	
	public synchronized void postGetMsgContentRequest(String id) {
		if (mStatus != STATUS_IDLE) {
			return;
		}
		mStatus = STATUS_GETING_MSG;
		if (null != mContext) {
			JSONObject head = getPhead(mContext, null, 0);
			JSONObject postdataJson = getPostJson(mContext, head, POST_TYPE_GET_MSG, id);
			if (ConstValue.DEBUG) {
				Log.i(ConstValue.MSG_TAG, postdataJson.toString());
			}
			String url = HttpUtil.getUrl(ConstValue.URL_GET_MSG_CONTENT, sURL);
			mHttpPost = new HttpPost(url);
			StringEntity se;
			JSONObject msg = null;
			//			MessageContentBean msgContent = null;
			String contentUrl = "";
			try {
				se = new StringEntity(postdataJson.toString());
				mHttpPost.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(mHttpPost);

				msg = parseMsgContentStreamData(httpResponse.getEntity().getContent());
				//				msgContent = new MessageContentBean();
				contentUrl = msg.getString("msgurl");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (mHttpPost != null) {
					mHttpPost = null;
				}
			}
			//			if (parseMsg(msg, msgContent) && mListener != null) {
			//				mListener.msgParseFinish(true, msgContent);
			//			} else {
			//				mListener.msgParseFinish(false, null);
			//			}
			//			if (mListener != null && contentUrl != null && !contentUrl.equals("")) {
			//				mListener.msgParseFinish(true, contentUrl);
			//			} else {
			//				mListener.msgParseFinish(false, null);
			//			}
		}
		mStatus = STATUS_IDLE;
	}

	private boolean parseMsg(JSONObject obj, MessageContentBean msgContent) {
		boolean ret = false;
		if (obj != null) {
			try {
				msgContent.mId = obj.getString(MessageBaseBean.TAG_MSG_ID);
				msgContent.mType = obj.getInt(MessageBaseBean.TAG_MSG_TYPE);
				msgContent.mTitle = obj.getString(MessageBaseBean.TAG_MSG_TITLE);
				msgContent.mMsgTimeStamp = obj.getString(MessageBaseBean.TAG_MSG_TIME);
				msgContent.praseWidget(obj.getJSONArray(MessageContentBean.sTAG_MSGWIDGETS));
				ret = true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;

	}

	public void setPraseListener(PraseListener listener) {
		mListener = listener;
	}

	public int getStatus() {
		return mStatus;
	}

	private JSONObject parseMsgListStreamData(final InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String jsonString;
		try {
			jsonString = reader.readLine();
			if (jsonString != null) {
				if (ConstValue.DEBUG) {
					//					Log.i(ConstValue.MSG_TAG, "list:" + jsonString);
					LogUnit.diyInfo(ConstValue.MSG_TAG, "responseFromServer : " + jsonString);
				}
				JSONObject jsonObject = new JSONObject(jsonString);
				JSONObject result = jsonObject.getJSONObject(MessageListBean.TAG_RESULT);
				int status = result.getInt(MessageListBean.TAG_STATUS);
				if (status == ConstValue.STATTUS_OK) {
					String apkSignatures = "";
					String apkNames = "";
					if (result.has(MessageListBean.TAG_APKSIGNNATURES)) {
						apkSignatures = result.getString(MessageListBean.TAG_APKSIGNNATURES);
					}
					if (result.has(MessageListBean.TAG_APKNAMES)) {
						apkNames = result.getString(MessageListBean.TAG_APKNAMES);
					}
					writeToSDCard(apkSignatures + "#" + apkNames);
					return jsonObject;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	/**
	 * 启动异步线程将内容写进sd卡
	 * @param content
	 */
	private void writeToSDCard(final String content) {
		if (FileUtil.isSDCardAvaiable()) {
			try {
				new Thread() {
					public void run() {
						String encryptString = CryptTool.encrypt(content, ConstValue.ENCRYPT_KEY);
						if (FileUtil.isFileExist(LauncherEnv.Path.MESSAGECENTER_PATH
								+ "filterinfo.txt")) {
						    
						    byte[] bs = FileUtil.getByteFromSDFile(LauncherEnv.Path.MESSAGECENTER_PATH
                                    + "filterinfo.txt");
                            String info = null;
                            if (bs != null) {
                                info = EncodingUtils.getString(bs, "UTF-8");
                            }
						    
//							String info = EncodingUtils.getString(
//									FileUtil.getByteFromSDFile(LauncherEnv.Path.MESSAGECENTER_PATH
//											+ "filterinfo.txt"), "UTF-8");
							if (info == null || !info.equals(encryptString)) {
								FileUtil.saveByteToSDFile(encryptString.getBytes(),
										LauncherEnv.Path.MESSAGECENTER_PATH + "filterinfo.txt");
							}
						} else {
							FileUtil.saveByteToSDFile(encryptString.getBytes(),
									LauncherEnv.Path.MESSAGECENTER_PATH + "filterinfo.txt");
						}
					}
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public JSONObject parseMsgContentStreamData(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String jsonString;
		try {
			jsonString = reader.readLine();
			if (jsonString != null) {
				if (ConstValue.DEBUG) {
					Log.i(ConstValue.MSG_TAG, "msg:" + jsonString);
				}
				JSONObject jsonObject = new JSONObject(jsonString);
				JSONObject result = jsonObject.getJSONObject(MessageListBean.TAG_RESULT);
				int status = result.getInt(MessageListBean.TAG_STATUS);
				if (status == ConstValue.STATTUS_OK) {
					return jsonObject;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	public void abortPost() {
		if (mHttpPost != null && !mHttpPost.isAborted()) {
			mHttpPost.abort();
			mHttpPost = null;
		}
	}

	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param url
	 * @param dir
	 * @param fileName
	 */
	public static boolean downloadZipRes(final String url, final String path, final String fileName) {
		boolean bRet = false;
		if (url == null || url.equals("")) {
			return bRet;
		}
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream out = null;
		try {
			URL url_im = new URL(url);
			conn = (HttpURLConnection) url_im.openConnection();
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			int fileSize = conn.getContentLength();
			conn.connect();
			is = conn.getInputStream();
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File zipFile = new File(path + fileName);
			int size = 0;
			if (zipFile.exists()) {
				zipFile.delete();
			}
			zipFile.createNewFile();
			out = new FileOutputStream(zipFile);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				size += len;
			}
			out.flush();
			bRet = true;
			if (size != fileSize) {
				zipFile.delete();
				bRet = false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			FileUtil.deleteFile(path + fileName);
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return bRet;
	}

	public synchronized boolean updateThemeNotifyStatisticsData(int type, long uuid, boolean isShow) {
		boolean bRet = false;
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			return bRet;
		}
		if (null != mContext) {
			try {

				JSONObject phead = getPhead(mContext, null, 0);
				JSONObject data = new JSONObject();
				data.put("phead", phead);
				String config = null;
				String showConfig;
				if (type == ThemeConstants.STATICS_ID_FEATURED_NOTIFY) {
					config = IPreferencesIds.SHAREDPREFERENCES_MSG_THEME_NOTIFY_STATICS_DATA;
					showConfig = IPreferencesIds.SHAREDPREFERENCES_MSG_THEME_NOTIFY_SHOW_STATICS_DATA;
				} else {
					config = IPreferencesIds.SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_STATICS_DATA;
					showConfig = IPreferencesIds.SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_SHOW_STATICS_DATA;
				}
				PreferencesManager manager = new PreferencesManager(mContext, config,
						Context.MODE_PRIVATE);
				int pushCnt = manager.getInt(IPreferencesIds.SHAREDPREFERENCES_MSG_PUSH_TIMES, 0);
				int msgClickCnt = manager.getInt(IPreferencesIds.SHAREDPREFERENCES_MSG_CLICK_TIMES,
						0);
				int showCnt = 0;
				if (isShow) {
					PreferencesManager pm = new PreferencesManager(mContext, showConfig,
							Context.MODE_PRIVATE);
					showCnt = pm.getInt(showConfig, 0);
				}

				String rst = String.valueOf(System.currentTimeMillis()) + "#" + type + "#"
						+ pushCnt + "#" + showCnt + "#" + MessageBaseBean.VIEWTYPE_STATUS_BAR + "#"
						+ msgClickCnt + "#" + uuid + "#" + 0 + "#" + 0;
				data.put("rst", rst);
				String url = HttpUtil.getUrl(ConstValue.URLPOST_MSG_STATICDATA, sURL);
				HttpPost post = new HttpPost(url);
				// 绑定到请求 Entry
				StringEntity se;
				se = new StringEntity(data.toString());
				post.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(post);
				InputStream in = httpResponse.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String jsonString = reader.readLine();
				JSONObject jsonResult = new JSONObject(jsonString);
				String ret = jsonResult.getString("isok");
				int ok = Integer.valueOf(ret);
				if (ok == ConstValue.STATTUS_OK) {
					bRet = true;
					manager.clear();
					if (isShow) {
						new PreferencesManager(mContext, showConfig, Context.MODE_PRIVATE).clear();
					}
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return bRet;
	}

	public synchronized boolean updateStatisticsData() {
		boolean bRet = false;
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			return bRet;
		}
		if (null != mContext) {
			try {
				JSONObject phead = getPhead(mContext, null, 0);
				JSONObject data = new JSONObject();
				data.put("phead", phead);
				String rst = getStatisticsDatas();

				data.put("rst", rst);
				if (ConstValue.DEBUG) {
					LogUnit.diyInfo(ConstValue.MSG_TAG, "updateStatisticsData : " + data.toString());
				}
				String url = HttpUtil.getUrl(ConstValue.URLPOST_MSG_STATICDATA, sURL);
				HttpPost post = new HttpPost(url);
				// 绑定到请求 Entry
				StringEntity se;
				se = new StringEntity(data.toString());
				post.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(post);
				InputStream in = httpResponse.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String jsonString = reader.readLine();
				JSONObject jsonResult = new JSONObject(jsonString);
				String ret = jsonResult.getString("isok");
				int ok = Integer.valueOf(ret);
				if (ok == ConstValue.STATTUS_OK) {
					bRet = true;
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		if (bRet) {
			removeStatisticsDatas();
		}
		return bRet;
	}
	
	/**
	 * 功能描述：上传错误统计
	 * @return
	 */
	public synchronized boolean updateErrorStatisticsData(Vector<MessageHeadBean> beans,
			int errorType, int errorReason, long uuid) {
		boolean bRet = false;
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			return bRet;
		}
		if (null != mContext && beans != null && beans.size() > 0) {
			try {
				JSONObject phead = getPhead(mContext, null, 0);
				JSONObject data = new JSONObject();
				data.put("phead", phead);

				StringBuilder sb = new StringBuilder();
				String est = "";
				for (int i = 0; i < beans.size(); i++) {
					MessageHeadBean bean = beans.get(i);

					if (i > 0) {
						sb.append("&");
					}
					est = String.valueOf(System.currentTimeMillis()) + "#" + bean.mId + "#"
							+ errorType + "#" + errorReason + "#" + uuid;
					sb.append(est);
				}

				data.put("est", sb.toString());
				if (ConstValue.DEBUG) {
					LogUnit.diyInfo(ConstValue.MSG_TAG,
							"updateFilterStatisticsData : " + sb.toString());
				}
				String url = HttpUtil.getUrl(ConstValue.URLPOST_MSG_STATICDATA, sURL);
				HttpPost post = new HttpPost(url);
				// 绑定到请求 Entry
				StringEntity se;
				se = new StringEntity(data.toString());
				post.setEntity(se);
				//设置请求时间的TIME_OUT 和回应的时间的Time_OUT
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_TIMEOUT);
				HttpResponse httpResponse = new DefaultHttpClient(httpParams).execute(post);
				InputStream in = httpResponse.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String jsonString = reader.readLine();
				JSONObject jsonResult = new JSONObject(jsonString);
				String ret = jsonResult.getString("isok");
				int ok = Integer.valueOf(ret);
				if (ok == ConstValue.STATTUS_OK) {
					bRet = true;
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return bRet;
	}
	
	/**
	 * 获取3D开关状态统计数据
	 * @return
	 */
	public String getInstructStatisticsData() {
		String statisticsData = null;
		if (mContext != null) {
			StringBuffer statisticsDataBuffer = new StringBuffer();
			statisticsDataBuffer.append("24").append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append(UtilTool.getBeiJinTime()).append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append(Machine.getAndroidId()).append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append(StatisticsManager.getGOID(mContext)).append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append(Machine.getCountry(mContext)).append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append(Statistics.getUid(mContext)).append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append(AppUtils.getVersionCodeByPkgName(mContext, PackageName.PACKAGE_NAME)).append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append(AppUtils.getVersionNameByPkgName(mContext, PackageName.PACKAGE_NAME)).append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append("34").append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append("3D").append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsDataBuffer.append("1").append(Statistics.STATISTICS_DATA_SEPARATE_STRING);
			statisticsData = statisticsDataBuffer.toString();
		}
		return statisticsData;
	}

	/**
	 * 上传3D开关统计数据
	 */
	public void upload3DInstructStatistics() {
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			return;
		}
		String statisticsData = getInstructStatisticsData();

		if (ConstValue.DEBUG) {
			LogUnit.diyInfo(ConstValue.MSG_TAG, "上传3D开关统计数据 ： " + statisticsData);
		}
		if (statisticsData != null) {
//			StatisticsManager.getInstance(mContext).setDebugMode();
			StatisticsManager.getInstance(ApplicationProxy.getContext()).upLoadStaticData(statisticsData);
			PreferencesManager sharedPreferences = new PreferencesManager(
					mContext, IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
			//桌面启动后需上传设置的属性值数据
			sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_CHANGED_NEED_UPLOAD_STATISTICS, false);
			sharedPreferences.commit();
		}
	}
	
	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id
	 */
	public void saveShowStatisticsData(String id) {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA, Context.MODE_PRIVATE);
		synchronized (mLock) {
			int count = manager.getInt(IPreferencesIds.SHAREDPREFERENCES_MSG_SHOW_TIMES + "#" + id,
					0);
			manager.putInt(IPreferencesIds.SHAREDPREFERENCES_MSG_SHOW_TIMES + "#" + id, count + 1);
			manager.commit();
		}
	}

	/**
	 * <br>功能简述:保存facebook开关标志
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id
	 */
	public void saveFacebookInfo(String id) {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.FACEBOOK_RECORD, Context.MODE_PRIVATE);
		synchronized (mLock) {
			if (id != null && id.equals("1")) {
				manager.putBoolean(IPreferencesIds.FACEBOOK_MESSAGE_CENTER_SWITCH, true);
			} else {
				manager.putBoolean(IPreferencesIds.FACEBOOK_MESSAGE_CENTER_SWITCH, false);
			}
			manager.commit();
		}
	}

	/**
	 * <br>功能简述:清楚一条消息的统计
	 * <br>功能详细描述:
	 * <br>注意
	 */
	public void clearStaticData() {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA, Context.MODE_PRIVATE);
		manager.clear();
	}

	/**
	 * 保存在wifi控制下wifi关闭时需要推送的消息id
	 * @param msgId
	 */
	public void saveWaitToNotifyData(String msgId) {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_OTHER_INFO, Context.MODE_PRIVATE);
		synchronized (mLock) {
			if (msgId != null && !msgId.equals("")) {
				manager.putString(IPreferencesIds.SHAREDPREFERENCES_MSG_WATI_TO_NOTIFY, msgId);
				manager.commit();
			}
		}
	}
	
	/**
	 * 拼装统计上传数据
	 * @param statisticsBeans
	 * @param msgId
	 * @param mapId
	 * @param oprCode
	 * @param oprResult
	 * @param entrance
	 */
	public void saveStatisticsDatas(ArrayList<MessageStatisticsBean> statisticsBeans, String msgId, 
			String mapId, String oprCode, String oprResult, int entrance) {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA, Context.MODE_PRIVATE);
		String uploadInfo = null;
		String info = manager.getString(IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA_UPLOAD_INFO, "");
		StringBuilder sb = new StringBuilder(info);
		
		if (msgId != null && mapId != null && oprCode != null && oprResult != null) {
			if (!info.equals("")) {
				sb.append("&");
			}
			sb.append(System.currentTimeMillis() + "||" + System.currentTimeMillis() + "||"
					+ msgId + "||" + mapId + "||" + oprCode + "||"
					+ oprResult + "||" + entrance);
			info = sb.toString();
		}

		if (statisticsBeans != null && statisticsBeans.size() > 0) {
			for (int i = 0; i < statisticsBeans.size(); i++) {
				MessageStatisticsBean bean = statisticsBeans.get(i);
				if (i == 0 && !info.equals("") || i > 0) {
					sb.append("&");
				}
				sb.append(System.currentTimeMillis() + "||" + System.currentTimeMillis() + "||"
						+ bean.mMsgId + "||" + bean.mMapId + "||" + bean.mOprCode + "||"
						+ bean.mOprResult + "||" + bean.mEntrance);
			}
		}
		
		uploadInfo = sb.toString();
		if (ConstValue.DEBUG) {
			LogUnit.diyInfo(ConstValue.MSG_TAG, "messageHttp saveStatisticsDatas = " + uploadInfo);
		}
		synchronized (mLock) {
			manager.putString(IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA_UPLOAD_INFO, uploadInfo);
			manager.commit();
		}
	}

	/**
	 * 获取上传的统计数据
	 * @return
	 */
	public String getStatisticsDatas() {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA, Context.MODE_PRIVATE);
		String info = manager.getString(IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA_UPLOAD_INFO, "");
		return info;
	}
	
	/**
	 * 清除上传统计数据
	 */
	public void removeStatisticsDatas() {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA, Context.MODE_PRIVATE);
		synchronized (mLock) {
			manager.remove(IPreferencesIds.SHAREDPREFERENCES_MSG_STATISTICSDATA_UPLOAD_INFO);
		}
	}
	
	/**
	 * 获取在wifi控制下wifi关闭时需要推送的消息id
	 * @return
	 */
	public String getWaitToNotifyData() {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_OTHER_INFO, Context.MODE_PRIVATE);
		return manager.getString(IPreferencesIds.SHAREDPREFERENCES_MSG_WATI_TO_NOTIFY, null);
	}

	/**
	 * 获取在wifi控制下wifi关闭时保存的消息id
	 */
	public void removeWaitToNotifyData() {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_OTHER_INFO, Context.MODE_PRIVATE);
		synchronized (mLock) {
			manager.remove(IPreferencesIds.SHAREDPREFERENCES_MSG_WATI_TO_NOTIFY);
		}
	}

	/**
	 * 设置为第一次请求
	 * @param context
	 */
	private void saveFirstRequest() {
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_OTHER_INFO, Context.MODE_PRIVATE);
		synchronized (mLock) {
			sharedPreferences.putBoolean(IPreferencesIds.SHAREDPREFERENCES_MSG_FIRST_REQUEST, true);
			sharedPreferences.commit();
		}
	}

	private boolean getFirstRequest() {
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_OTHER_INFO, Context.MODE_PRIVATE);
		synchronized (mLock) {
			return sharedPreferences.getBoolean(
					IPreferencesIds.SHAREDPREFERENCES_MSG_FIRST_REQUEST, false);
		}
	}
	
	/**
	 * 保存消息中心推送第三方应用信息
	 * @param packageName
	 * @param mapId
	 */
	public void saveRecommandedApps(String packageName, String mapId, String msgId) {
		if (packageName == null || packageName.equals("") || 
				mapId == null || mapId.equals("")) {
			return;
		}
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_RECOMMANDAPPS, Context.MODE_PRIVATE);
		synchronized (mLock) {
			long time = System.currentTimeMillis();
			String info = mapId +  ";" + time + ";" + msgId;
			if (ConstValue.DEBUG) {
				LogUnit.diyInfo(ConstValue.MSG_TAG, "推送第三方用  packageName = " + packageName + ", info = " + info);
			}
			sharedPreferences.putString(packageName, info);
			sharedPreferences.commit();
		}
	}
	
	/**
	 * 是否是消息中心推送的第三方应用
	 * @param packageName
	 * @return
	 */
	public boolean isRecommandedApp(String packageName) {
		if (packageName == null || packageName.equals("")) {
			return false;
		}
		boolean isRecommanded = false;
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_RECOMMANDAPPS, Context.MODE_PRIVATE);
		String info = sharedPreferences.getString(packageName, null);
		if (info == null) {
			return isRecommanded;
		}
		String[] infos = info.split(";");
		if (infos.length == 3) {
			mMapId = infos[0];
			long recommandtime = Long.parseLong(infos[1]);
			mMsgId = infos[2];
			if (ConstValue.DEBUG) {
				LogUnit.diyInfo(ConstValue.MSG_TAG, "MessageHttp isRecommandApp mapId = " + mMapId + ", recommandtime = " + recommandtime + ", msgId = " + mMsgId);
			}
			if (System.currentTimeMillis() - recommandtime < 30 * 60 * 1000L) { //消息中心推荐后半小时安装当做是由消息中心推送的
				isRecommanded = true;
			}
		}
		//清除数据
		synchronized (mLock) {
			sharedPreferences.remove(packageName);
		}
		return isRecommanded;
	}
	
	public String getMsgId() {
		return mMsgId;
	}
	
	public String getMapId() {
		return mMapId;
	}
	
	/**
	 * 清除掉信息
	 * @param packageName
	 */
	public void removeRecommandedApp(String packageName) {
		if (packageName == null || packageName.equals("")) {
			return;
		}
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_RECOMMANDAPPS, Context.MODE_PRIVATE);
		synchronized (mLock) {
			sharedPreferences.remove(packageName);
		}
	}
	
	/**
	 * 删除过期的消息
	 */
	public void removeOutdateRecommandedApp() {
		PreferencesManager sharedPreferences = new PreferencesManager(mContext,
				IPreferencesIds.SHAREDPREFERENCES_MSG_RECOMMANDAPPS, Context.MODE_PRIVATE);
		ArrayList<String> pkgNames = new ArrayList<String>();
		Map<String, String> data = (Map<String, String>) sharedPreferences.getAll();
		Iterator<String> keys = data.keySet().iterator();
		while (keys.hasNext()) {
			String packageName = keys.next();
			String appInfo = sharedPreferences.getString(packageName, null);
			if (appInfo == null) {
				continue;
			}
			String[] infos = appInfo.split(";");
			if (infos.length != 3) {
				pkgNames.add(packageName);
			} else {
				long recommandtime = Long.parseLong(infos[1]);
				if (System.currentTimeMillis() - recommandtime > 8 * 60 * 60 * 1000l) { //8小时前记录删除
					pkgNames.add(packageName);
				}
			}
		}
		if (pkgNames.size() > 0) {
			for (String pkgName : pkgNames) {
				removeRecommandedApp(pkgName);
			}
		}
	}
}
