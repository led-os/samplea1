/**
 * 
 */
package com.jiubang.ggheart.data.tables;

/**
 * @author zhangxi
 *
 */
public class SideWidgetTable {

	public static final String TABLE_NAME = "sidewidget";
	public static final String KEY_ID = "widget_id";
	public static final String WIDGET_TITLE = "widget_title";
	public static final String WIDGET_PKGNAME = "widget_pkgname";
	public static final String WIDGET_PREVIEWNAME = "widget_previewname";
	public static final String WIDGET_INSTALLDATE = "widget_installdate";

	static public final String CREATETABLESQL = "create table " + TABLE_NAME
			+ " ( " + KEY_ID + " INTEGER primary key autoincrement, "
			+ WIDGET_TITLE + " text, " + WIDGET_PKGNAME + " text, "
			+ WIDGET_PREVIEWNAME + " INTEGER, " + WIDGET_INSTALLDATE
			+ " DATETIME DEFAULT CURRENT_TIMESTAMP" + " ) ";
	
	static public final String ALTERTABLESQL = "alter table " + TABLE_NAME
			+ " add column " + WIDGET_PREVIEWNAME + " INTEGER";

}
