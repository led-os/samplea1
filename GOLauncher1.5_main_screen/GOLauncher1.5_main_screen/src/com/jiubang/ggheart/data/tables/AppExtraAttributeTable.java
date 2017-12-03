package com.jiubang.ggheart.data.tables;

/**
 * 
 */
public class AppExtraAttributeTable {
	public static final String TABLE_NAME = "app_attribute";
	public static final String COMPONENT_NAME = "componentname";
	public static final String ISLOCK = "islock";
	public static final String ISNEW = "isnew";
	public static final String CLICK_OPEN_TIME = "click_open_time";
	public static final String READ_UPDATA_INFO_TIME = "read_updata_info_time";
	public static final String DATA = "data";

	static public final String CREATETABLESQL = "create table " + TABLE_NAME + " ( "
			+ COMPONENT_NAME + " text, " + ISLOCK + " numeric, " + ISNEW + " numeric, "
			+ CLICK_OPEN_TIME + " numeric, " + READ_UPDATA_INFO_TIME + " numeric, " + DATA
			+ " text" + " ) ";
}
