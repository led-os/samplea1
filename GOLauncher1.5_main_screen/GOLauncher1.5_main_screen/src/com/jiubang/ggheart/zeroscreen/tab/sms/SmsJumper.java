package com.jiubang.ggheart.zeroscreen.tab.sms;

import android.content.Context;
import android.content.Intent;

/**
 * 
 * <br>类描述:短信跳转处理逻辑
 * <br>功能详细描述:
 * 
 * @author  zhengxiangcan
 * @date  [2013-1-10]
 */
public class SmsJumper {

	private static SmsJumper sJumper;
	private static final String TAG = "SmsJumper";

	private SmsJumper() {
	}

	public static synchronized SmsJumper getJumper() {
		if (sJumper == null) {
			sJumper = new SmsJumper();
		}
		return sJumper;
	}

	///**
	// * <br>功能简述:跳转发生那个短信界面
	// * <br>功能详细描述:
	// * <br>注意:该方法只能调用系统短信
	// * @param context
	// * @param phoneNumber
	// */
	//	public void jumpToSms(Context context, String phoneNumber) {
	//		if (context == null) {
	//			return;
	//		}
	//		String sendTo = "smsto://" + phoneNumber;
	//		Uri smsToUri = Uri.parse(sendTo);
	//		Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri);
	//		context.startActivity(mIntent);
	//	}
	/**
	 * <br>功能简述:跳转短信发送界面
	 * <br>功能详细描述:
	 * <br>注意:可调用第3方app
	 * @param context
	 * @param phoneNumber
	 */
	public void jumpToSms(Context context, String phoneNumber) {
		if (context == null) {
			return;
		}
		try {
			Intent it = new Intent(Intent.ACTION_VIEW);
			it.putExtra("address", phoneNumber);
			it.setType("vnd.android-dir/mms-sms");
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
		} catch (Exception e) {

		}
	}

	/**
	 * <br>功能简述:跳转短信主界面
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 */
	public void jumpToSms(Context context) {
		if (context == null) {
			return;
		}
		try {
			Intent it = new Intent(Intent.ACTION_MAIN);
			it.setType("vnd.android-dir/mms-sms");
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
		} catch (Exception e) {
		}
	}
}
