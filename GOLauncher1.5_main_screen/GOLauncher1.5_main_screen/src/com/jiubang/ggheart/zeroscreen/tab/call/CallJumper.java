package com.jiubang.ggheart.zeroscreen.tab.call;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.Intents;

/**
 * 
 * <br>类描述:短信跳转处理逻辑
 * <br>功能详细描述:
 * 
 * @author  zhengxiangcan
 * @date  [2013-1-10]
 */
public class CallJumper {

	private static CallJumper sJumper;

	private CallJumper() {
	}

	public static synchronized CallJumper getJumper() {
		if (sJumper == null) {
			sJumper = new CallJumper();
		}
		return sJumper;
	}

	/**
	 * <br>功能简述:拨号
	 * <br>功能详细描述:
	 * <br>注意:直接拨号
	 * @param context
	 * @param phoneNumber
	 */
	public void jumpToCall(Context context, String phoneNumber) {
		if (context == null) {
			return;
		}
		try {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <br>功能简述:跳转拨号主界面
	 * <br>功能详细描述:
	 * <br>注意:进入拨号输入界面，预先输入phoneNumber
	 * @param context
	 */
	public void jumpToCallDial(Context context, String phoneNumber) {
		if (context == null) {
			return;
		}
		try {
			Uri uri;
			if (phoneNumber == null || phoneNumber.equals("-1")) {
				uri = Uri.parse("tel:");
			} else {
				uri = Uri.parse("tel:" + phoneNumber);
			}
			Intent intent = new Intent(Intent.ACTION_DIAL, uri);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	/**
	//	 * <br>功能简述:跳转系统联系人选择界面
	//	 * <br>功能详细描述:
	//	 * <br>注意:
	//	 * @param context
	//	 */
	//	public void jumpToCallPick(Context context, ArrayList<Integer> list) {
	//		try {
	//			Intent intent = new Intent();
	//			intent.setClass(context, SelectContactActivity.class);
	//			Bundle b = new Bundle();
	//			b.putIntegerArrayList("had_checked_contacts", list);
	//			intent.putExtras(b);
	//			if (context instanceof Activity) {
	//				((Activity) context).startActivityForResult(intent, CallConstants.PICK_CONTACT);
	//			}
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}

	/**
	 * <br>功能简述:跳转联系人详情
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param uri
	 */
	public void jumpToCallView(Context context, String uri) {
		try {
			
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <br>功能简述:跳转添加联系人界面
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @param num
	 */
	public void jumpToAddCallView(Context context, String num) {
		try {
			Intent  intent = new Intent(Intent.ACTION_INSERT, 
	                Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
	        intent.setType("vnd.android.cursor.dir/person");
	        intent.setType("vnd.android.cursor.dir/contact");
	        intent.setType("vnd.android.cursor.dir/raw_contact");
	        intent.putExtra(Intents.Insert.PHONE, num);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
