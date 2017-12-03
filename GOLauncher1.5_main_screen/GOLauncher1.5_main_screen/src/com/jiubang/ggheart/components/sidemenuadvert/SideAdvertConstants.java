package com.jiubang.ggheart.components.sidemenuadvert;

import com.go.util.ServerUtils;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * <br>类描述:侧边栏广告参数类
 * <br>功能详细描述:
 * 
 * @author  zhangxi
 * @date  [2013-10-9]
 */
public class SideAdvertConstants {
	public static String sGoWidgetBaseHostUrl = LauncherEnv.Server.GOWIDGET_BASE_HOSTURL_PRO; //gowidget正式地址
	public static String sGoWidgetBackupHostUrl = LauncherEnv.Server.GOWIDGET_BACKUP_HOSTURL_PRO; //gowidget备用地址
		
	public static final int GET_ADVERT_DATA_SUCCESS = 0; //请求获取广告数据成功
	public static final int GET_ADVERT_DATA_FAIL = 1; //请求获取广告数据失败
	
	public static final int DOWN_IMAGE_SUCCESS = 3; //下载图片成功
	public static final int DOWN_IMAGE_FAIL = 4; //下载图片失败
	
	public static final int STATISTICS_REQUEST_SUCCESS = 5;	//统计上传成功
	public static final int STATISTICS_REQUEST_FAIL = 6;	//统计上传失败
	
	public static final String SIDE_LTS_REQUEST_TIME = "side_lts_request_time"; // 请求lts数值
	
	public static final int SIDE_CACHE_TYPE_TOOLS = 1;
	public static final int SIDE_CACHE_TYPE_WIDGET = 2;
	
	public static final String SIDE_TOOLS_LASTCACHE = LauncherEnv.Path.SIDEMENU_CACHE_PATH + "lasttoolscache"; // widget获取cache
	public static final String SIDE_TOOLS_CACHEFILE = LauncherEnv.Path.SIDEMENU_CACHE_PATH + "toolscache"; // widget获取cache
	public static final String SIDE_WIDGET_LASTCACHE = LauncherEnv.Path.SIDEMENU_CACHE_PATH + "lastwidgetcache"; // widget获取cache
	public static final String SIDE_WIDGET_CACHEFILE = LauncherEnv.Path.SIDEMENU_CACHE_PATH + "widgetcache"; // widget获取cache

	
	public static final int IS_CAROUSEL = 1; //是轮换文件夹
	public static final int IS_NO_CAROUSEL = 0; //不是轮换文件夹
	
	public static final String SIDEADVERT_FROM_CONSTANT = "3";
	
	public static final int IS_FILE = 1; //是文件夹类型
	public static final int IS_NO_FILE = 0; //不是文件夹类型
	
	// http url的动作类型
	public static final int ACTTYPE_MARKET = 4;
	public static final int ACTTYPE_WAP = 7;
	public static final int ACTTYPE_FTP = 8;
	public static final int ACTTYPE_WEBVIEW = 9;
	public static final int ACTTYPE_LOCAL = 13;
	public static final int ACTTYPE_INTENT = 14;
	
	//widget XML标签
	public final static String XML_GOWIDGET_ITEM = "item";
	public final static String XML_GOWIDGET_PKGNAME = "pkgname";
	public final static String XML_GOWIDGET_THEMEINFO = "themeInfo";
	public final static String XML_GOWIDGET_PREVIEW = "preview";
	public final static String XML_GOWIDGET_GALINK = "galink";
	public final static String XML_GOWIDGET_PICURL_CN = "picurl_cn";
	public final static String XML_GOWIDGET_PICURL_AB = "picurl_ab";
	
	//tools XML标签
	public final static String XML_GOTOOLS_APPS = "recommendedtools";
	public final static String XML_GOTOOL_APP = "recommended_tool";
	
	//统计
	public static final String STATS_ADVERT_DOWNLOAD = "a000";
	public static final String STATS_ADVERT_INSTALL = "b000";
	public static final String STATS_ADVERT_DISPLAY = "f000";
	public static final int STATS_ADVERT_RST_SUCCESS = 1;
	
	private static String s_GOWIDGET_HOST_URL = sGoWidgetBaseHostUrl;
	
	private static Object sLocker_gowidget = new Object();
	
	static {
		if (ServerUtils.isUseTestServer(LauncherEnv.Server.SIDE_ADVERT_CONFIG_USE_TEST_SERVER)) {
			sGoWidgetBaseHostUrl = LauncherEnv.Server.GOWIDGET_BASE_HOSTURL_SIT;
			sGoWidgetBackupHostUrl = LauncherEnv.Server.GOWIDGET_BACKUP_HOSTURL_SIT;
			s_GOWIDGET_HOST_URL = sGoWidgetBaseHostUrl;
		}
	}
	
	public static void setGWHostUrl(String adHostUrl) {
		synchronized (sLocker_gowidget) {
			s_GOWIDGET_HOST_URL = adHostUrl;
		}
	}
	
	public static String getGWHostUrl() {
		synchronized (sLocker_gowidget) {
			return s_GOWIDGET_HOST_URL;
		}
	}
	
}
