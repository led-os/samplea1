package com.jiubang.ggheart.data.tables;

/**
 * 
 * @author zhangkai
 *
 */
public class ZeroScreenAdSuggestSiteTable {

	public static final String TABLE_NAME = "zeroscreenadsuggstsite";

	public static final String ID = "_id";

	/**
	 * 国家
	 */
	public static final String COLUMN_COUNTRY = "country";

	/**
	 * 推荐数据的版本号
	 */
	public static final String COLUMN_VERSION = "version";

	/**
	 * 网站标题（如“新浪”）
	 */
	public static final String COLUMN_TITLE = "title";

	/**
	 * 网站网址 如 www.sina.com.cn
	 */
	public static final String COLUMN_URL = "url";

	/**
	 * 网站的顶级域名 如sina
	 */
	public static final String COLUMN_DOMAIN = "domain";

	/**
	 * 存放图标的服务器地址
	 */
	public static final String COLUMN_ICON_HOST = "icon_host";

	/**
	 * 网站Logo图标的网址
	 */
	public static final String COLUMN_LOGO_URL = "logo_url";

	/**
	 * 网站Logo图标
	 */
	public static final String COLUMN_LOGO_ICON = "logo_icon";

	/**
	 * 网站快速标签图标的背景图的颜色
	 */
	public static final String COLUMN_BACK_COLOR = "back_color";

	/**
	 * 网站快速标签图标的前景图的网址
	 */
	public static final String COLUMN_FRONT_URL = "front_url";

	/**
	 * 网站快速标签图标的前景图
	 */
	public static final String COLUMN_FRONT_ICON = "front_icon";
	/**
	 * 是否显示加号
	 */
	public static final String COLUMN_IS_PLUS = "is_plus";
	//---for database use---
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " (" + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_COUNTRY + " TEXT, " + COLUMN_VERSION
			+ " INTEGER, " + COLUMN_TITLE + " TEXT NOT NULL, " + COLUMN_URL + " TEXT NOT NULL, "
			+ COLUMN_DOMAIN + " TEXT, " + COLUMN_ICON_HOST + " TEXT, " + COLUMN_LOGO_URL
			+ " TEXT, " + COLUMN_BACK_COLOR + " INTEGER, " + COLUMN_FRONT_URL + " TEXT, "
			+ COLUMN_LOGO_ICON + " BLOB DEFAULT NULL, " + COLUMN_FRONT_ICON + " BLOB DEFAULT NULL)";
}
