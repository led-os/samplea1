package com.jiubang.ggheart.iconconfig;

/**
 * 应用图标配置信息表
 * @author caoyaming
 *
 */
public class AppIconConfigInfoTable {
	//表名
	public static final String TABLE_NAME = "APP_ICON_CONFIG_INFO";
	//主健
	public static final String ID = "ID";
	//应用ComponentName
	public static final String APP_COMPONENTNAME = "APP_COMPONENTNAME";
	//显示的未读数字
	public static final String SHOW_NUMBER = "SHOW_NUMBER";
	//开始版本(如:4.14)
	public static final String START_VERSION = "START_VERSION";
	//结束版本(如:4.15)
	public static final String END_VERSION = "END_VERSION";
	//开始时间(yyyyMMddHHmmss)
	public static final String VALID_START_TIME = "VALID_START_TIME";
	//结束时间(yyyyMMddHHmmss)
	public static final String VALID_END_TIME = "VALID_END_TIME";
	//建表语句
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + APP_COMPONENTNAME
			+ " TEXT, " + SHOW_NUMBER + " INTEGER, " + START_VERSION
			+ " TEXT, " + END_VERSION + " TEXT, " + VALID_START_TIME
			+ " TEXT, " + VALID_END_TIME + " TEXT)";
}
