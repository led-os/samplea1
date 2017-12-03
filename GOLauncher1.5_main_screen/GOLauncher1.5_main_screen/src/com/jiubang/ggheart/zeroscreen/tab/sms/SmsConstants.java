package com.jiubang.ggheart.zeroscreen.tab.sms;

/**
 * 
 * <br>类描述:短信常量
 * <br>功能详细描述:
 * 
 * @author  zhengxiangcan
 * @date  [2013-1-9]
 */
public class SmsConstants {
	public static final String SMS_URI_ALL = "content://sms";
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	public static final String SMS_URI_SEND = "content://sms/sent";
	public static final String SMS_URI_DRAFT = "content://sms/draft";
	public static final String CONTACT_URI = "content://com.android.contacts/data/phones/filter/"; // 用于获取联系人头像
	public static final String STR_RES = "android.provider.Telephony.SMS_RECEIVED";

	public static final String ID = "_id";
	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String BODY = "body";
	public static final String DATE = "date";
	public static final String TYPE = "type";
	public static final String READ = "read";
	/**
	 * 查询的字段
	 */
	public static final String[] CONVERSATION_PROJECTION = new String[] { ID, ADDRESS, BODY, DATE,
			TYPE, READ };
	/**
	 * 排序方式
	 */
	public static final String SORTBYDATA = "date DESC";

	public static final String QUERY_NEW_SMS_SELECTION = "type = 1 and read = 0";
}
