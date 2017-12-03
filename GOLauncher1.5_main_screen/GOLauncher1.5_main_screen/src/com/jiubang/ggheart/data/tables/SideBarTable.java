package com.jiubang.ggheart.data.tables;

/**
 * 侧边栏应用表
 * @author dengdazhong
 *
 */
public class SideBarTable {
	public static final String TABLENAME = "sidebar_app_list";
	public static final String ID = "_id";
	public static final String COUNT = "_count";
	public static final String PKG_NAME = "pkg_name";
	public static final String CLASS_NAME = "class_name";
	public static final String POSITION = "position";
	public static final String TYPE = "type";
	public static final String ICON = "icon";
	public static final String CREATETABLESQL = "CREATE TABLE " + TABLENAME + " ( " + ID
			+ " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + PKG_NAME + " TEXT," + CLASS_NAME
			+ " TEXT," + POSITION + " INTEGER, " + TYPE + "  INTEGER," + ICON + " BLOB	)";
}
