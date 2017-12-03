package com.jiubang.ggheart.data.tables;


/**
 * @author zhangkai 快速拨号数据表的行
 */
public class VideoCenterInfoTable {

	public int mAppId; // 应用id
	public String mPkgName; // 应用包名
	public String mName; // 应用程序名
	public String mIconUrl; // 应用图标url
	public String mDownloadUrl; // 下载地址
	public String mPicUrl; // 截图的下载地址
	public String mVideoCenterSlogan; // 影视中心推荐位广告语

	public static final String TABLE_NAME = "videmocenterinfo";

	/**
	 * ID编号
	 */
	public static final String ID = "_id";

	/**
	 * 应用id
	 */
	public static final String APPID = "appid";

	/**
	 * 应用包名
	 */
	public static final String PKGNAME = "pkgname";

	/**
	 * 应用程序名
	 */
	public static final String NAME = "name";

	/**
	 * 应用图标url
	 */
	public static final String ICONURL = "iconurl";

	/**
	 * 下载地址
	 */
	public static final String DOWNLOADURL = "downloadurl";

	/**
	 * 截图的下载地址
	 */
	public static final String PICURL = "picurl";

	/**
	 * 影视中心推荐位广告语
	 */
	public static final String SLOGAN = "slogan";

	/**
	 * 弹框用的
	 */
	public static final String SUMMARY = "summary";

	// ---for database use---
	public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + APPID + " TEXT, "
			+ PKGNAME + " TEXT, " + NAME + " TEXT, " + ICONURL + " TEXT, "
			+ DOWNLOADURL + " TEXT, " + PICURL + " TEXT, " + SUMMARY
			+ " TEXT, " + SLOGAN + " TEXT)";

}
