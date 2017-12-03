package com.jiubang.ggheart.themeicon;

/**
 * 主题Icon信息表
 * @author caoyaming
 *
 */
public class ThemeIconInfoTable {
	//表名
	public static final String TABLE_NAME = "THEME_ICON_INFO";
	//主题包名
	public static final String THEME_PACKAGENAME = "THEME_PACKAGENAME";
	//Icon对应的应用ComponentName
	public static final String APP_COMPONENTNAME = "APP_COMPONENTNAME";
	//Icon图片下载地址
	public static final String DOWNLOAD_ICON_URL = "DOWNLOAD_ICON_URL";
	//Icon更新时间
	public static final String ICON_UPDATE_TIME = "ICON_UPDATE_TIME";
	//Icon资源存放目录(sdcard)
	public static final String ICON_SAVE_PATH = "ICON_SAVE_PATH";
	//建表语句
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " (" + THEME_PACKAGENAME + " TEXT, "
			+ APP_COMPONENTNAME + " TEXT, " + DOWNLOAD_ICON_URL + " TEXT, " + ICON_UPDATE_TIME + " TEXT, " + ICON_SAVE_PATH + " TEXT)";
}
