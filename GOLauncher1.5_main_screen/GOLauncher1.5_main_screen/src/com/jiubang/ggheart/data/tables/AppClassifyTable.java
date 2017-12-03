package com.jiubang.ggheart.data.tables;

/**
 * 应用分类表
 */
//CHECKSTYLE:OFF
public class AppClassifyTable {

	public static final String TABLE_NAME = "appclassification";
	public static final String PACKAGE_NAME = "packagename";
	public static final String CLASSIFICATION = "classification";

	static public String CREATETABLESQL = "create table appclassification " + "(" + "packagename text, "
			+ "classification text" + ")";

}
