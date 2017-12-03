package com.jiubang.ggheart.activationcode.invite;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.gau.go.launcherex.R;
import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.util.AppUtils;
import com.go.util.StringUtil;
import com.go.util.device.Machine;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.appgame.base.net.AppGameNetRecord;
import com.jiubang.ggheart.appgame.base.net.AppHttpAdapter;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.data.statistics.Statistics;

/**
 * 5.0版本邀请好友功能管理类
 * @author caoyaming
 *
 */
public class InviteController {
	//邀请好友请求地址(正式)
	//private static final String INVITE_URL = "http://api.goforandroid.com/activationcode/invite.do";
	//邀请好友请求地址(正式)
	private static final String INVITE_URL = "http://indchat.3g.cn:8080/activationcode/invite.do";
	//===============服务端返回码=======================
	//邀请成功(200)
	public static final String HTTP_CODE_INVITE_SUCCESS = "200"; 
	//邮箱已经退订(300)---当邮箱已经退订了,服务器是不会再向该用户发送邮件
	private static final String HTTP_CODE_EMAIL_UNSUBSCRIBE = "300"; 
	//系统处理失败(400)
	private static final String HTTP_CODE_SYSTEM_HANDLE_FAILURE = "400"; 
	//Context
	private Context mContext;
	//PrivatePreference
	private PrivatePreference mPreference;
	//当前类对象
	private static InviteController sInstance;
	private InviteController(Context context) {
		mContext = context;
		mPreference = PrivatePreference.getPreference(context);

	}
	/**
	 * 获取单例对象
	 * @param context 
	 * @return
	 */
	public static InviteController getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new InviteController(context);
		}
		return sInstance;
	}
	/**
	 * 上传邀请好友信息
	 * @param inviteEmail 邀请好友的邮箱
	 * @param listener 结果监听
	 */
	public boolean uploadInviteInfo(final String inviteEmail, final ReuqestDataListener listener) {
		if (listener == null) {
			return false;
		}
		//判断网络是否可用
		if (!Machine.isNetworkOK(mContext)) {
			//网络不可用
			listener.onException(mContext.getResources().getString(R.string.activationcode_invite_network_unavailable));
			return false;
		}
		//创建线程邀请好友
		new Thread(new Runnable() {
			@Override
			public void run() {
				THttpRequest request = null;
				try {
					request = new THttpRequest(INVITE_URL, createHttpRequestData(inviteEmail).getBytes(), new IConnectListener() {
						@Override
						public void onStart(THttpRequest request) {
							//请求开始
						}
						@Override
						public void onFinish(THttpRequest request, IResponse response) {
							//请求完成
							if (response != null && response.getResponse() != null) {
								//获取返回码
								String responseCode = StringUtil.toString(response.getResponse());
								//返回处理结果
								listener.onFinish(responseCode, convertResponseCodeToMessage(responseCode));
							} else {
								//激活失败	
								listener.onException(mContext == null ? "" : mContext.getResources().getString(R.string.activationcode_invite_failure));
							}
						}
						@Override
						public void onException(THttpRequest request, int reason) {
							//请求失败	
							listener.onException(mContext == null ? "" : mContext.getResources().getString(R.string.activationcode_invite_failure));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					//请求失败	
					listener.onException(mContext == null ? "" : mContext.getResources().getString(R.string.activationcode_invite_failure));
					return;
				}
				if (request != null) {
					request.setOperator(new HttpDataOperator(false));
					request.setNetRecord(new AppGameNetRecord(mContext, true));
					AppHttpAdapter httpAdapter = AppHttpAdapter.getInstance(mContext);
					httpAdapter.addTask(request, true);
				}
			}
		}).start();
		return true;
	}
	/**
	 * 创建请求参数
	 * @param inviteEmail 邀请邮箱
	 * @return
	 */
	private String createHttpRequestData(String inviteEmail) {
		JSONObject requestJsonObject = new JSONObject();
		//协议版本号(必填,目前是:1)
		try {
			requestJsonObject.put("pver", "1"); 
			//软件版本(必填)
			requestJsonObject.put("version", AppUtils.getVersionCodeByPkgName(mContext, mContext.getPackageName()));
			//语言(国家),优先拿SIM卡
			requestJsonObject.put("lang", RecommAppsUtils.language(mContext));
			//渠道(必填)
			requestJsonObject.put("channel", StringUtil.toInteger(Statistics.getUid(mContext), -1));
			//手机系统id(必填)
			requestJsonObject.put("androidid", StringUtil.toString(Machine.getAndroidId()));	
			//被邀请用户邮箱(必填)
			requestJsonObject.put("inviteEmail", inviteEmail);				
			//用户邮箱邀请者邮箱(没有邮箱可"")
			requestJsonObject.put("selfEmail", StringUtil.toString(Machine.getGmail(mContext)));				
			//产品id(必填,next桌面为:0;GO桌面prime限免为:1;主题大礼包:2;GO桌面泄密包为:3)
			requestJsonObject.put("pid", 3);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestJsonObject.toString();
	}
	/**
	 * 将返回码转换成提示消息
	 * @param responseCode 返回码
	 * @return
	 */
	private String convertResponseCodeToMessage(String responseCode) {
		int resourcesId = 0;
		if (HTTP_CODE_INVITE_SUCCESS.equals(responseCode)) {
			//邀请成功(200)
			resourcesId = R.string.activationcode_invite_success;
		} else if (HTTP_CODE_EMAIL_UNSUBSCRIBE.equals(responseCode)) {
			//邮箱已经退订(300)
			resourcesId = R.string.activationcode_invite_email_unsubscribed;
		} else if (HTTP_CODE_SYSTEM_HANDLE_FAILURE.equals(responseCode)) {
			//系统处理失败(400)
			resourcesId = R.string.activationcode_invite_system_handle_failure;
		}
		return mContext.getResources().getString(resourcesId);
	}
	
	/**
	 * 请求数据监听器
	 * @author caoyaming
	 *
	 */
	public interface ReuqestDataListener {
		/**
		 * 请求数据成功
		 * @param responseCode 返回码
		 * @param message 消息
		 */
		public void onFinish(String responseCode, String message);
		/**
		 * 请求数据失败
		 * @param errorMessage 错误消息
		 */
		public void onException(String errorMessage);
	}
	/**
	 * 获取可以邀请人数(默认5人)
	 * @return 邀请人数
	 */
	public int getInviteNumber() {
		return mPreference.getInt(PrefConst.KEY_LAUNCHER_ACTIVATION_INVITE_PEOPLE_NUMBER, 5);
	}
	/**
	 * 修改可以邀请人数(减一)
	 * @return 修改后的值
	 */
	public int updateInviteNumber() {
		//获取修改的值
		int number = getInviteNumber() - 1;
		if (number < 0) {
			number = 0;
		}
		mPreference.putInt(PrefConst.KEY_LAUNCHER_ACTIVATION_INVITE_PEOPLE_NUMBER, number);
		mPreference.commit();
		return number;
	}
	/**
	 * 是否显示邀请菜单
	 * @return true:显示  false:不显示
	 */
	public static boolean isShowInviteMenu(Context context) {
		PrivatePreference preference = PrivatePreference.getPreference(context);
		//剩余邀请数大于0并且显示邀请开关为打开状态,则显示邀请入口
		return preference.getInt(PrefConst.KEY_LAUNCHER_ACTIVATION_INVITE_PEOPLE_NUMBER, 5) > 0 && preference.getBoolean(PrefConst.KEY_LAUNCHER_ACTIVATION_INVITE_ENTR_FALG, false);
	}
}
