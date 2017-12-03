package com.jiubang.ggheart.admob;


/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-12]
 */
public class AdTable {

	public static final String TABLE_NAME = "admob_table";

	public static final String ID = "id";

	public static final String PRODUCT_ID = "pid";

	public static final String POSITION_ID = "pos_id";

	public static final String SWICH_STATE = "switch";
	
	public static final String GETJAR_ENABLE = "getjar_enable";
	
	public static final String PROVITY = "provity";

	public static final String CREATE_TABLE_SQL = "CREATE TABLE if not exists " + TABLE_NAME + " ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PRODUCT_ID + " INTEGER, " + POSITION_ID
			+ " INTEGER, " + SWICH_STATE + " INTEGER, " + GETJAR_ENABLE + " INTEGER, " + PROVITY + " INTEGER)";

}
