package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox;

/**
 * 屏幕添加层对,go动态壁纸,主题,小部件的操作统计
 * @author zouguiquan
 *
 */
public class ScreenEditStatistics {
	//使用测试统计服务器
	public final static boolean sStatisticDebug = false;
	
	public final static int STATISTICS_FUN_ID = 54;
	public final static int STATISTICS_OPERATE_SUCCESS = 1;
	public final static String STATISTICS_ENTER = "0";
	
	public final static String STATISTICS_TYPE_WIDGET = "103";
	public final static String STATISTICS_TYPE_DESKTOP_WALLPAPER = "202";
	public final static String STATISTICS_TYPE_DYNAMICA_WALLPAPER = "203";		//对go动态壁纸的统计
	public final static String STATISTICS_TYPE_MULTI_WALLPAPER = "204";
	public static String STATISTICS_TYPE_DESKTOP_THEME = "301";
	public static String STATISTICS_TYPE_LOCK_THEME = "302";
	
	public final static String STATISTICS_OPERATE_MODULE_CLICK = "h000";			//统计操作代码,点击某个模块
	public final static String STATISTICS_OPERATE_APP_CLICK = "i000";			//统计操作代码,点击已安装应用
	public final static String STATISTICS_OPERATE_RECOMMEND_CLICK = "c000";		//统计操作代码,点击推荐应用
	public final static String STATISTICS_OPERATE_APP_INSTALLED = "b000";		//统计操作代码,安装推荐应用
	public final static String STATISTICS_OPERATE_MORE_INFO = "more";			//统计操作代码,点击获取更多模块
}
