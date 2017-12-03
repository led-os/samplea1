package com.jiubang.ggheart.switchwidget;

/**
 * 
 * <br>类描述:go开关widget
 * <br>功能详细描述:
 * 
 * @author  zhengxiangcan
 * @date  [2014-1-7]
 */
public class SwitchTable {
	
	public static final String TABLE_NAME = "go_switch_situation"; // 开关的各种情况
	public static final String ID = "id";
	public static final String WIDGETID = "widget_id"; // int
	public static final String SIDE = "switch_side"; // 开关的位置int
	public static final String SWITCH_ID = "switch_id"; // 开关的id号int

	// 创建数据库表语句，完成记录开关的位置与未来的情景模式的功能
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WIDGETID + " INTEGER, " + SIDE + " INTEGER, " + SWITCH_ID + " INTEGER)";

}
