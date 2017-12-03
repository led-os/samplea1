package com.go.util.market;

/**
 * 
 * @author liuheng
 *
 */
public interface MarketConstant {
	public static final String PACKAGE = "com.android.vending";

	// 用包名搜索market上的软件
	public static final String BY_PKGNAME = "market://search?q=pname:";

	// 直接使用关键字搜索market上的软件
	public static final String BY_KEYWORD = "market://search?q=";

	// 进入软件详细页面
	public static final String APP_DETAIL = "market://details?id=";

	// 浏览器版本的电子市场详情地址
	public static final String BROWSER_APP_DETAIL = "https://play.google.com/store/apps/details?id=";
}
