package com.jiubang.ggheart.data.tables;

/**
 * 
 */
public class SideToolsTable {

	public static final String TABLE_NAME = "sidetools";
	public static final String KEY_ID = "widget_id";
	public static final String TOOLS_PKGNAME = "side_pkgname";
	public static final String TOOLS_INSTALLDATE = "widget_installdate";
	public static final String IS_SELECT = "is_select";
	public static final String IS_FORCE_SHOW = "is_force_show";

	static public final String CREATETABLESQL = "create table " + TABLE_NAME
			+ " ( " + KEY_ID + " INTEGER primary key autoincrement, " + TOOLS_PKGNAME + " TEXT, " + IS_SELECT + " INTEGER, "
			+ IS_FORCE_SHOW + " INTEGER, " + TOOLS_INSTALLDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" + " ) ";
	
}
