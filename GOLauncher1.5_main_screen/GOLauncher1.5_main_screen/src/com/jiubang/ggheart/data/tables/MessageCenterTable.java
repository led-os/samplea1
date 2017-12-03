package com.jiubang.ggheart.data.tables;
/**
 * 
 * <br>类描述:消息中心数据
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2012-10-15]
 */
public interface MessageCenterTable {
	/**
	 * 表名
	 */
	static public String TABLENAME = "messagecenter";

	static public String ID = "mesageid";

	static public String TITLE = "title";

	static public String READED = "readed";

	static public String VIEWTYPE = "viewtype";

	static public String TYPE = "type";

	static public String DATE = "date";

	static public String URL = "url";

	static public String STIME_START = "stime_start";

	static public String STIME_END = "stime_end";

	static public String ICON = "icon";

	static public String INTRO = "intro";

	static public String ACTTYPE = "acttype";

	static public String ACTVALUE = "actvalue";

	static public String ZICON1 = "zicon1";

	static public String ZICON2 = "zicon2";

	static public String ZPOS = "zpos";
	static public String ZTIME = "ztime";
	static public String ISCLOSED = "isclosed";
	static public String FILTER_PKGS = "filter_pkgs";
	static public String CLICK_CLOSE = "clickclosed";
	
	static public String DYNAMIC = "dynamic";
	static public String ICONPOS = "iconpos";
	static public String FULL_SCREEN_ICON = "fullscreenicon";
	static public String REMOVED = "removed";

	static public String WHITE_LIST = "whitelist";
	
	static public String ISWIFI = "iswifi";
	static public String OUTDATE = "outdate";
	static public String DELETED = "deleted";
	static public String COUPON = "coupon";
	static public String PROMOTION = "promotion";
	static public String INSTRUCT = "instruct";
	static public String PACKAGENAME = "packagename";
	static public String MAPID = "mapid";
	static public String ADDACT = "addact";
	static public String ISADV = "isadv";
	static public String MSGIMGURL = "msgimgurl";
	
	static public String UP_PACKAGE_NAME = "uppkgname";
	static public String UP_VERSION_CODE = "upversioncode";
	static public String UP_VERSION = "upversion";
	static public String UP_UPDATES = "upupdates";
	static public String UP_FILE_URL = "upfileurl";
	static public String UP_DELTA_FILE_URL = "updeltafileurl";
	static public String UP_DELTA_RESULT = "updeltaresult";
	static public String UP_UPDATE_MODE = "upmode";
	static public String UP_SECOND_INTRO = "upsecondintro";
	static public String UP_AUTO_DOWNLOAD = "autodownload";

	/**
	 * 表语句
	 */
	static public String CREATETABLESQL = "create table messagecenter " + "(" + "mesageid text, "
			+ "title text, " + "readed numeric, " + "viewtype numeric, " + "type text, "
			+ "date text, " + "url text, " + "stime_start text," + "stime_end text," + "icon text,"
			+ "intro text," + "acttype numeric," + "actvalue text," + "zicon1 text,"
			+ "zicon2 text," + "zpos numeric," + "ztime numeric," + "isclosed numeric," + "filter_pkgs text,"
			+ "clickclosed numeric," + "dynamic numeric," + "iconpos numeric," 
			+ "fullscreenicon text," + "removed numeric, " + "whitelist text," 
			+ "iswifi numeric," + "outdate text,"
			+ "deleted numeric," + "coupon text," + "promotion text,"
			+ "instruct text," + "packagename text,"
			+ "mapid text, addact numeric," + "isadv numeric"
			+ "uppkgname text, upversioncode numeric, upversion text, upupdates text,"
			+ "upfileurl text, updeltafileurl text, updeltaresult text, upmode numeric,"
			+ "upsecondintro text, msgimgurl text,"
			+ "autodownload numeric, " + "PRIMARY KEY (mesageid)" + ")";

}
