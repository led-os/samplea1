package com.jiubang.ggheart.data.tables;

/**
 * 主题相关表
 * @author yangguanxiang
 *
 */
public class ThemeTable {
	/**
	 * 表名
	 */
	public static final String TABLENAME = "theme";

	/**
	 * 表字段: THEMENAME
	 */
	public static final String THEMENAME = "themename";
	/**
	 * 表字段: VERSION
	 */
	public static final String VERSION = "version";
	/**
	 * 表字段: AUTOCHECKVERSION
	 */
	public static final String AUTOCHECKVERSION = "autocheckversion";
	/**
	 * 表字段: BACKGROUNDIMAGE
	 */
	public static final String BACKGROUNDIMAGE = "backgroundimage";
	/**
	 * 表字段: ISPEMANENTMEMORY
	 */
	public static final String ISPEMANENTMEMORY = "ispemanentmemory";
	/**
	 * 表字段: ISCACHEDESK
	 */
	public static final String ISCACHEDESK = "iscachedesk";
	/**
	 * 表字段: FONT
	 */
	public static final String FONT = "font";

	public static final String LASTSHOWTIME = "lastshowtime";

	public static final String PREVENTFORCECLOSE = "preventforceclose";

	public static final String HIGHQUALITYDRAWING = "highqualitydrawing";

	public static final String TRANSPARENTSTATUSBAR = "transparentstatusbar";
	
	/**
	 * 是否显示通知栏背景
	 */
	public static final String IS_SHOW_STATUS_BAR_BG = "isshowstatusbarbg";

	/*
	 * 第一次运行
	 */
	public static final String FIRSTRUN = "firstrun";

	public static final String TIPCANCELDEFAULTDESK = "tipcanceldefaultdesk";

	public static final String CLOUD_SECURITY = "cloudsecurity";
	
	/**
	 * 是否去广告
	 */
	public static final String NO_ADVERT = "noadvert";
	
	/**
	 * 是否显示0屏
	 */
	public static final String SHOW_ZERO_SCREEN = "zeroscreen";
	
	/**
	 * 返回键切换0屏
	 */
	public static final String KEYBACK_CHANGE_ZERO_SCREEN = "keychangezeroscreen";
	
	/**
	 * 表语句
	 */
	public static final String CREATETABLESQL = "create table theme " + "(" + "themename text, "
			+ "version numeric, " + "autocheckversion numeric, " + "backgroundimage text, "
			+ "ispemanentmemory numeric, " + "iscachedesk numeric, " + "font text, "
			+ "lastshowtime numeric, " + "preventforceclose numeric, "
			+ "highqualitydrawing numeric, " + "transparentstatusbar numeric, "
			+ "firstrun numeric, " + "tipcanceldefaultdesk numeric, " + "cloudsecurity numeric, "
			+ "noadvert numeric, " + "zeroscreen numeric, " + "keychangezeroscreen numeric, " + "isshowstatusbarbg numeric"
			+ ")";
}
