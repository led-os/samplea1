package com.jiubang.ggheart.data.tables;

/**
 * 安全锁密码信息表
 * @author wuziyi
 *
 */
public class LockInfoTable {
	public static final String TABLE_NAME = "lockinfo";
	public static final String KEY_ID = "key_id";
	public static final String PASSWORD = "password";
	public static final String EMAIL_ADDRESS = "email_address";

	static public final String CREATETABLESQL = "create table " + TABLE_NAME + " ( " + KEY_ID + " text, "
			+ PASSWORD + " text, " + EMAIL_ADDRESS + " text" + " ) ";
}
