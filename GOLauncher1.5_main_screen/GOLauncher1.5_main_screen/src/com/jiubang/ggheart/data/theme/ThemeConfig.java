package com.jiubang.ggheart.data.theme;

/**
 * 主题解析xml文件名称
 * @author liulixia
 *
 */
public interface ThemeConfig {
	public static String THEMECFGFILENAME = "themecfg.xml"; // 主题配置信息
	public static String NEWTHEMECFGFILENAME = "new_themecfg.xml"; // 大主题的配置信息文件
	public static String APPFILTERFILENAME = "appfilter.xml"; // 应用程序替换信息
	public static String APPFILTERFILENAME_UPGRADE = "appfilter_upgrade.xml"; // 应用程序替换信息
	public static String APPFUNCTHEMEFILENAME = "app_func_theme.xml";
	public static String DESKTHEMEFILENAME = "desk.xml";
	public static String DRAWRESOURCEFILENAME = "drawable.xml";
	public static String DRAWRESOURCEFILENAME_UPGRADE = "drawable_upgrade.xml";

	public static String THEMECFGFILENAME_3 = "themecfg_3.xml"; // 默认3.0主题配置信息
	public static String NEWTHEMECFGFILENAME_3 = "new_themecfg_3.xml"; // 默认3.0大主题的配置信息文件

	//dodol主题配置文件
	public static String DODOLTHEMEINFO = "theme_info.xml"; // 主题配置信息
	public static String DODOLTHEMEICON = "theme_icon.xml"; //应用程序替换信息
	public static String DODOLTHEMERESOURCE = "theme_resource.xml"; //壁纸、dock条等资源信息
	
	//atom主题配置文件
	public static String ATOMTHEMEINFO = "themeinfo.xml"; //主题配置信息
	public static String ATOMAPPMAP = "appmap.xml"; //应用程序替换信息
	public static String ATOMWALLPAPER = "wallpaper.xml"; //壁纸
}
