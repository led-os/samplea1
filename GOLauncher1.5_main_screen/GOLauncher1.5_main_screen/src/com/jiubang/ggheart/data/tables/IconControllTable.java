package com.jiubang.ggheart.data.tables;

/**
 * CN包使用业务
 * TODO可控图标数据库
 * @author  makai
 * @data:  2014年2月24日 上午11:01:15
 * @version:  V1.0
 */
public class IconControllTable {
	
	public static final String TABLE_NAME = "go_icon_controllable"; 
	public static final String TYPE = "type";

	// 创建数据库表语句，完成记录开关的位置与未来的情景模式的功能
	public static final String CREATE_TABLE = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
			+ "version" + " text,"
			+ "pkgename" + " text,"
			+ "validstarttime" + " text,"
			+ "validendtime" + " text,"
			+ "launcherstartversion" + " text,"
			+ "launcherendverstion" + " text,"
			+ "shownumber" + " text,"
			+ TYPE + " text)";
	
	
	
}
