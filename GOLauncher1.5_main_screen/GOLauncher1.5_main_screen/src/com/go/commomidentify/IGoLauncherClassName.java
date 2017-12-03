package com.go.commomidentify;

import com.jiubang.ggheart.launcher.PackageName;

/**
 * 公共的类名和包名定义
 * @author liuheng
 *
 */
public interface IGoLauncherClassName {
	public static String GOLAUNCHER_ACTIVITY = "com.jiubang.ggheart.apps.desks.diy.GoLauncher";
	public static String GOLAUNCHER_APPSERVICE = "com.jiubang.ggheart.data.AppService";


	public static final String DEFAULT_THEME_PACKAGE = PackageName.PACKAGE_NAME; // 默认主题
	public static final String DEFAULT_THEME_PACKAGE_3 = "default_theme_package_3"; // 默认3.0主题
	public static final String DEFAULT_THEME_PACKAGE_3_NEWER = "com.gau.go.launcherex.theme.defaultthemethree"; // 默认3.1以上主题
	public static final String MAIN_THEME_PACKAGE = "com.gau.go.launcherex.theme"; // 主题包名
	public static final String OLD_THEME_PACKAGE = "go.launcher.theme";     //旧主题包名
}
