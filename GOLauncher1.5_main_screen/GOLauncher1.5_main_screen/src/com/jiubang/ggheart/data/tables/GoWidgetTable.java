package com.jiubang.ggheart.data.tables;

import com.jiubang.ggheart.data.info.GoWidgetBaseInfo;

/**
 * 
 * @author
 *
 */
public final class GoWidgetTable {
	public static final String TABLENAME = "gowidget";
	public static final String WIDGETID = "widgetid";
	public static final String TYPE = "type";
	public static final String LAYOUT = "layout";
	public static final String PACKAGE = "package";
	public static final String CLASSNAME = "classname";
	public static final String THEME = "theme";
	public static final String THEMEID = "themeid";
	public static final String ENTRY = "entry";
	public static final String REPLACEGROUP = "replacegroup";
	/**
	 * 内置原型字段， 非内置此值默认为{@link GoWidgetBaseInfo#PROTOTYPE_NORMAL}
	 */
	public static final String PROTOTYPE = "prototype";

	public static final String CREATETABLESQL = "create table " + TABLENAME + "(" + WIDGETID
			+ " numeric, " + TYPE + " numeric, " + LAYOUT + " text, " + PACKAGE + " text, "
			+ CLASSNAME + " text, " + THEME + " text, " + THEMEID 
			+ " numeric, " + PROTOTYPE + " numeric, " +  ENTRY + " text, " + REPLACEGROUP + " numeric" + ")";
}
