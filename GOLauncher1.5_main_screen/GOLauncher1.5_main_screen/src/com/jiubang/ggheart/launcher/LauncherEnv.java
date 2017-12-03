package com.jiubang.ggheart.launcher;

import android.os.Environment;

/**
 * 公共常量定义
 * 
 * @author huyong
 * 
 */
public final class LauncherEnv {
	
	/**
	 * 是否使用测试服务器状态
	 */
	public final static boolean SIT = false;
	
	public final static String PROCESS_NAME = "android.process.acore";
	
	/**
	 * 系统设置
	 */
	public final static String SETTINGS = "com.android.settings";

	// 程序名
	public final static String APP_NAME = "GOLauncherEX";

	
	public final static int APP_WIDGET_HOST_ID = 1024;
	
	
	// 语言定义
	public static final String LANGUAGE = "language";

	// 语言格式
	public static final String LANGUAGE_FORMAT = "%s-%s";

	// 未知版本号，用于显示更新提示
	public static final String UNKNOWN_VERSION = "unknown_version";
	// 未知版本号
	public static final int UNKNOWN_VERSIONCODE = 0;
	// 主题加密密钥
	public static final String THEMM_ENCRYPT_KEY = "gotheme";

	public static final String ALREADY_REPLACE_OLD_WIDGET = "already_replace_old_widget"; //记录是否删除过gostore旧的4*2和4*3的widget，防止多次判断
	public static final String ALREADY_REPLACE_ALL_GOSTORE_WIDGET = "already_replace_all_gostore_widget"; //记录是否删除过gostore所有的widget，防止多次判断
	public static final String ALREADY_PUT_GO_MARKET_ICON = "already_put_go_market_icon"; //记录是否添加了应用游戏中心icon，防止多次判断

	public static final String DISACCEPT_AGREEMENT = "disaccept_agreement";

	public static final int MASK_TUTORIAL_NONE = -1;
	public static final int MASK_TUTORIAL_PREVIEW = 1; // 屏幕预览与编辑提示
	// public static final int MASK_TUTORIAL_SCREEN_EFFECTS = 2;
	public static final int MASK_TUTORIAL_CREATE_FOLDER = 5;
	public static final int MASK_TUTORIAL_APPDRAWER_DRAG = 6;
	public static final int MASK_TUTORIAL_APPDRAWER_HIDE = 7;
	public static final int MASK_TUTORIAL_SCREEN_MENU = 8;
	public static final int MASK_TUTORIAL_SCREEN_WALLPAPER_SETTING = 9;
	public static final int MASK_TUTORIAL_SCREEN_FOLDER = 10;
	public static final int MASK_TUTORIAL_SCREEN_DOCK = 11;
	public static final int MASK_TUTORIAL_CUSTOM_GESTURE = 12; // 自定义手势提示
	public static final int MASK_TUTORIAL_DOCK_BAR_ICON = 13; // dock栏提示

	// 桌面主题的变量
	//	public static String MAIN_THEME_PACKAGE = "com.gau.go.launcherex.theme"; // 主题包名
	public static final String THEME_CATEGORY = "android.intent.category.DEFAULT"; // 主题包category

	//	// GO锁屏主题包名
	//	public final static String GOLOCK_THEME_ACTION = "com.jiubang.goscreenlock.theme";
	// GO锁屏主题包category
	public final static String GOLOCK_THEME_CATEGORY = "android.intent.category.INFO";
	
	// 由GO桌面推荐跳转到电子市场的后缀
	public final static String GOLAUNCHER_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3Dgo_launcher%26utm_medium%3Dhyperlink%26utm_campaign%3Dgo_store_ads";

	// GO备份的google分析链接
	public final static String GOBACKUP_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3Dlauncher_menu%26utm_medium%3Dlauncher%26utm_campaign%3DLauncher";

	// 桌面GO APP文件夹推荐应用的google分析链接
	public final static String GOLAUNCHER_GOOGLE_REFERRAL_LINK_FOR_GOFOLDER = "&referrer=utm_source%3DGoLauncher_GoApp_Folder%26utm_medium%3DHyperlink%26utm_term%3DGO%26utm_content%3Dlogolink%26utm_campaign%3DGoLauncher_GoApp_Folder";

	// 一键锁屏的google分析链接
	public final static String LOCK_SCREEN_REFERRAL_LINK = "&referrer=utm_source%3DGoLauncher_GoApp_Folder%26utm_medium%3DHyperlink%26utm_content%3Dlogolink%26utm_campaign%3DGoLauncher_GoApp_Folder";

	// 由GO桌面推荐widget跳转到电子市场的后缀
	public final static String GOLAUNCHER_FORWIDGET_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3DGO%2520Launcher%2520EX%26utm_medium%3DHyperlink%26utm_campaign%3DWidget%2520HomeScreen";

	// 由GO桌面推荐全屏插件跳转到电子市场的后缀
	public final static String GOLAUNCHER_FORTOUCHHELPER_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3Dgolauncher%26utm_medium%3DHyperlink%26utm_campaign%3Dgolauncher2finger";
	// 由GO桌面推荐evernote跳转到电子市场的后缀
	public final static String GOLAUNCHER_FOREVERNOTE_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3DGOLauncher_desktop_icon%26utm_medium%3Dlogolink%26utm_campaign%3DGOLauncher_desktop_icon";
	
	//TouchHelper电子市场地址
	public final static String EDITBOX_TOUCHHELPER_MARKET_REFERRAL_LINK = "&referrer=utm_source%3Dgolauncher%26utm_medium%3DHyperlink%26utm_campaign%3Dgolauncheraddtheme";

	// 多屏多壁纸跳转到电子市场的后缀
	public final static String MULTIPLEWALLPAPER_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3DGOLauncher_Widget%26utm_medium%3DHyperlink%26utm_campaign%3DGOLauncher_Widget";

	// GO内置天气Widget推荐GO天气的GA地址
	public final static String GO_WEATHER_EX_GA = "market://details?id=com.gau.go.launcherex.gowidget.weatherwidget&referrer=utm_source%3Dlauncher_internal%26utm_medium%3DHyperlink%26utm_campaign%3Dthemestore";
	
	// gostore中主题页面
	public final static String GOSTORE_THEMEPAGE = "com.gau.diy.gostore.theme";

	// Evernote文件夹名称
	public final static String EVERNOTE_FILE_NAME = LauncherEnv.Path.SDCARD + "/Evernote/code.txt";;

	// Evernote String
	public final static String EVERNOTE_STRING = "golaunch-3g";
	
	//GOWeahterEX 4＊1样式的信息，当GOWeatherEX安装后，用来替换内置天气GOWidget
	public final static String GO_WEATHER_EX_4_1_LAYOUT = "weather_4_1";
	public final static String GO_WEATHER_EX_CLASSNAME = "com.gau.go.launcherex.gowidget.weather.view.Setting41Activity";
	public final static int GO_WEATHER_EX_4_1_TYPE = 1;
	
	/**
	 * CN DB升级带入的常量
	 */
	public static final String AMAZON_PACKAGE_NAME = "com.gau.diy.amazon"; // 亚马逊

	/**
	 * url相关常量定义在此处
	 * 
	 * @author luopeihuan
	 * 
	 */
	public static final class Url {
		// 进行POST请求时的请求头
		public static final String POST_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";

		// 桌面下载地址
		public final static String LAUNCHER_URL = "http://smsftp.3g.cn/soft/3GHeart/com.gau.go.launcherex.apk";

		// 桌面官方主题下载站点
		public static final String GOLAUNCHER_THEME_SITE_URL = "http://theme.3g.cn/xuan/xuanList.aspx?fr=golauncherxuan";

		// // Notification下载地址(FTP)
		// public final static String NOTIFICATION_URL =
		// "http://61.145.124.93/soft/3GHeart/golauncher/notification/com.gau.golauncherex.notification.apk";

		// Notification下载地址(电子市场web版地址)
		public final static String NOTIFICATION_URL = "https://play.google.com/store/apps/details?id=com.gau.golauncherex.notification";

		// MultipleWallpaper多屏多壁纸下载地址(FTP)
		public final static String MULTIPLEWALLPAPER_URL = "http://61.145.124.93/soft/3GHeart/go_multiple_wallpaper.apk";
		
		// RecommLiveWallpaper推荐动态壁纸下载地址(FTP)  -- 暂未使用
		public final static String RECOMM_LIVEWALLPAPER_URL = "http://61.145.124.93/soft/3GHeart/go_multiple_wallpaper.apk";

		// 桌面迁移工具下载地址
		public final static String DESKMIGRATE_URL = "http://smsftp.3g.cn/soft/3GHeart/DeskMigrate.apk";

		// 独立语言包下载地址
		// 屏蔽by Ryan 2012.09.21 原因：下载URL不可用，避免被重新调用引起跳转失败，将其屏蔽
		//		public final static String LANGUAGE_URL = "http://smsftp.3g.cn/soft/3GHeart/language/";

		// widget下载地址：
		public final static String GOWIDGET_DOWNLOAD_URL = "http://goforandroid.com/gdt/sourceDL.aspx?softid=3&nav=2&typeid=35";

		// widget主题下载地址
		public final static String GOWIDGET_THEME_DOWNLOAD_URL = "http://goforandroid.com/gdt/sourceDL.aspx?softid=3&nav=2&typeid=40";

		// GO锁屏下载地址
		public final static String GOLOCKER_DOWNLOAD_URL = "http://smsftp.3g.cn/soft/golocker/GOLocker.apk";

		// 用户协议地址
		public final static String LICENSE_AGREEMENT_EN = "http://smsftp.3g.cn/soft/3GHeart/golauncher/license/golauncher_user_license_agreement_en.HTML";

		public final static String LICENSE_AGREEMENT_CN = "http://smsftp.3g.cn/soft/3GHeart/golauncher/license/golauncher_user_license_agreement_cn.HTML";

		// 安卓论坛root教程地址
		public final static String ROOT_TEACHING_CN = "http://www.anzhuo.cn/thread-244214-1-1.html";

		// 国外root教程地址
		public final static String ROOT_TEACHING_EN = "http://www.google.com/cse?q=root&sa=Search&cx=partner-pub-2900107662879704%3Afs7umqefhnf&ie=ISO-8859-1#gsc.tab=0&gsc.q=root&gsc.page=1";

		// GO HD桌面下载地址
		public final static String GOHDLAUNCHER_DOWNLOAD_URL = "http://smsftp.3g.cn/soft/3GHeart/golauncher/pad/GO_Launcher_HD.apk";

		// GO HD桌面 WEB电子市场下载地址
		public final static String GOHDLAUNCHER_WEB_URL = "https://play.google.com/store/apps/details?id=com.go.launcherpad";

		// GO天气EX的FTP下载地址
		public final static String GOWEATHEREX_FTP_URL = "http://smsftp.3g.cn/soft/3GHeart/golauncher/widget/qudao/goweatherex/goweatherex_203.apk";

		// GO短信FTP的下载地址
		public final static String GOSMSPRO_FTP_URL = "http://smsftp.3g.cn/soft/gosms/hutui/GOLauncherAppFolder/GOSmsPro.apk";

		// GO输入法的FTP下载地址
		public final static String GOKEYBOARD_FTP_URL = "http://down.goforandroid.com/soft/gokeyboard/gokeyboard_golauncherfold.apk";

		// GO省电FTP下载地址
		public final static String GOPOWERMASTER_FTP_URL = "http://smsftp.3g.cn/soft/3GHeart/golauncher/widget/qudao/gopowermaster/gopowermaster_203.apk";

		// GO任务管理器EX的FTP下载地址
		public final static String GOTASKMANAGEREX_FTP_URL = "http://smsftp.3g.cn/soft/GoTaskManagerEX/qudao/gotaskmanagerex_203.apk";

		// GO锁屏FTP下载地址
		public final static String GOLOCKER_FTP_URL = "http://cache.3g.cn/gosoft/go_launcher/golockerqudao/GOLocker_583.apk";

		// GO备份FTP下载地址
		public final static String GOBACKUP_FTP_URL = "http://smsftp.3g.cn/soft/gobackup/channel/launchermainscreen/gobackup.apk";

		// GO备份EXFTP下载地址
		public final static String GOBACKUP_EX_FTP_URL = "http://smsftp.3g.cn/soft/GOBackupPrp/channel/launchermainscreen/GOBackup.apk";

		// 一键锁屏FTP下载地址
		public final static String LOCK_SCREEN = "http://61.145.124.93/soft/golocker/GOLockerPluginLockScreen.apk";

		// 开关widget的FTP下载地址
		public final static String GOSWITCHWIDGET_FTP_URL = "http://down.goforandroid.com/soft/3GHeart/golauncher/widget/FTP/Switch.apk";

		// UI3.0新主题的FTP下载地址
		public final static String UI_3NEW_THEME_URL = "http://smsftp.3g.cn/soft/3GHeart/ThemeUI3.0.apk";

		// Menu菜单中GO锁屏的google分析地址
		public final static String GOLOCKER_IN_MENU_WITH_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3DGOLauncher_Menu%26utm_medium%3DHyperlink%26utm_campaign%3DGOLauncher_Menu";

		// 锁屏主题中，下载锁屏的google分析地址
		public final static String GOLOCKER_IN_THEME_WITH_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3DGOLauncher_Theme_LockerTheme%26utm_medium%3DHyperlink%26utm_campaign%3DGOLauncher_Theme_LockerTheme";

		// 资源管理插件下载地址(FTP)
		public final static String MEDIA_PLUGIN_FTP_URL = "http://61.145.124.93/soft/3GHeart/go_media_manager.apk";
		
		// 百度浏览器跳转link
		public final static String BAIDU_BROWSER_URL = "http://c.cpatrk.net/a?k=5c0Mtk";
		
		// 百度省电跳转link
		public final static String BAIDU_BATTERSAVER_URL = "http://app.appsflyer.com/com.dianxinos.dxbs?pid=3G&c=1";
		//卸载推荐nextlauncher页面
		public final static String NEXT_LAUNCHER_HOME_URL = "http://goappdl.goforandroid.com/website/next/go2next.html";
		// 安卓优化大师跳转link
		public final static String DU_SPEED_BOOSTER_URL = "http://app.appsflyer.com/com.dianxinos.optimizer.duplay?pid=3G";
		
		// GO内置天气Widget推荐GO天气的HTTP地址
		public final static String GO_WEATHER_EX_URL = "https://play.google.com/store/apps/details?id=com.gau.go.launcherex.gowidget.weatherwidget";
	}

	/**
	 * 插件相关常量（Notification和桌面搬家工具包名）
	 * 
	 * @author luopeihuan
	 */
	public static final class Plugin {
		/**
		 * notification短信通知等模块
		 */
//		public final static String NOTIFICATION_PACKAGE_NAME = "com.gau.golauncherex.notification";
		public final static String NOTIFICATION_SERVICE_NAME = "service.NotificationService";

		// Locker vip
//		public final static String LOCKER_VIP_PACKAGE = "com.jiubang.goscreenlock.vip";
		public final static String LOCKER_VIP_GA = "&referrer=utm_source%3DGOLauncherGUIStore%26utm_medium%3Dbanner%26utm_campaign%3DGOAPP";
		//		public final static String LOCKER_ACTION = "com.jiubang.goscreenlock";
		//		public final static String LOCKER_SETTING_ACTION = "com.jiubang.goscreenlock.setting";
		public final static String LOCKER_SERVICE_CLASS_NAME = "com.jiubang.goscreenlock.service.KeyguardService";
		//		public final static String LOCKER_THEME_ACTION = "com.jiubang.goscreenlock.theme";
		public final static String LOCKER_THEME_CATEGORY = "android.intent.category.INFO";


		// 360下载链接
		public final static String QIHU360_URL = "http://soft.3g.cn/xuan/download.aspx?fid=42054&cooid=553";


		public final static String RECOMMEND_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3D3Ggolauncher%26utm_medium%3DCPA%26utm_campaign%3DVault";
		// Next桌面的google分析链接
		public final static String RECOMMEND_NEXTLAUNCHER_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3Dgo_launcher%26utm_medium%3DHyperlink%26utm_campaign%3Dhomescreen_ads";
		// 资源管理器插件google分析链接
		public final static String RECOMMEND_MEDIA_PLUGIN_GOOGLE_REFERRAL_LINK = "&referrer=utm_source%3DGOLauncher_HomeScreen%26utm_medium%3DHyperlink%26utm_campaign%3DGOLauncher_HomeScreen ";

		//非推荐的，剩余go widget 
//		public final static String TIMER_PACKAGE = "com.gau.go.launcherex.gowidget.timer"; //倒计时
//		public final static String SWITCHER_PLUS_PACKAGE = "com.gau.go.launcherex.gowidget.newswitchwidget"; // 开关+
//		public final static String SCANNER_PACKAGE = "com.gau.go.launcherex.gowidget.gobarcodescanner"; //二维码
//		public final static String FLASH_LIGHT_PACKAGE = "com.gau.go.launcherex.gowidget.flashlight"; // 手电筒
//		public final static String TASK_PACKAGE = "com.gau.go.launcherex.gowidget.taskmanager"; // 任务管理器（老版）
//		public final static String SWITCH_PACKAGE = "com.gau.go.launcherex.gowidget.switchwidget"; // 开关
//		public final static String NEW_SWITCH_PACKAGE = "com.gau.go.launcherex.gowidget.newswitchwidget"; // 新开关
//		public final static String CALENDAR_PACKAGE = "com.gau.go.launcherex.gowidget.calendarwidget"; // 日历
//		public final static String NEW_CALENDAR_PACKAGE = "com.gau.go.launcherex.gowidget.newcalendarwidget"; // 新日历
//		public final static String CLOCK_PACKAGE = "com.gau.go.launcherex.gowidget.clockwidget"; // 时钟
//		public final static String NOTE_PACKAGE = "com.gau.go.launcherex.gowidget.notewidget"; // 便签
//		public final static String NEW_NOTE_PACKAGE = "com.gau.go.launcherex.gowidget.newnotewidget"; // 新便签
//		public final static String CONTACT_PACKAGE = "com.gau.go.launcherex.gowidget.contactwidget"; // 联系人
//		public final static String SINA_PACKAGE = "com.gau.go.launcherex.gowidget.weibowidget"; // 新浪微博
//		public final static String TENCNT_PACKAGE = "com.gau.go.launcherex.gowidget.qqweibowidget"; // 腾讯微博
//		public final static String EMAIL_PACKAGE = "com.gau.go.launcherex.gowidget.emailwidget"; // 邮箱
//		public final static String FACEBOOK_PACKAGE = "com.gau.go.launcherex.gowidget.fbwidget"; // FaceBook
//		public final static String TWITTER_PACKAGE = "com.gau.go.launcherex.gowidget.twitterwidget"; // Twitter
//		public final static String BOOKMARK_PACKAGE = "com.gau.go.launcherex.gowidget.bookmark"; // 书签
//		public final static String APP_PACKAGE = "com.gau.go.launcherex"; // 应用游戏中心
//		public final static String STORE_PACKAGE = "com.gau.go.launcherex"; // GO精品
//		public final static String GOSEARCH_PACKAGE = "com.gau.go.launcherex.gowidget.gosearchwidget"; // GO搜索
//		public final static String NOWWIDGET_PACKAGE = "com.gau.go.launcherex.gowidget.nowwidget"; // Now直播
//		public final static String NEWS3G_PACKAGE = "com.gau.go.launcherex.gowidget.newswidget "; // 3G门户新闻
//		public final static String SEARCH_WIDGET_PACKAGE = "com.gau.go.launcherex.gowidget.searchwidget"; // 搜索widget
//		public final static String RECOMMAND_TTDONDTING_PACKAGE = "com.sds.android.ttpod"; // 天天动听 
		public final static String RECOMMAND_TTDONDTING_DOWNLOAD_URL = "http://soft.3g.cn/Xuan/download.html?fid=589457&id=107&statid=249"; // 天天动听 
//		public final static String RECOMMAND_TIANQITONG_PACKAGE = "sina.mobile.tianqitong";
//		public final static String CLEAN_MASTER_PACKAGE = "com.cleanmaster.mguard"; // clean master 包名
		public final static String CLEAN_MASTER_GA_FAVORITE_WIDGET = "&referrer=utm_source%3D2000000006%26utm_campaign%3DGolauncher5"; // clean master推荐widget的GA
		public final static String CLEAN_MASTER_GA_IN_APPFUNC_MENU = "&referrer=utm_source%3D2000000007%26utm_campaign%3DGolauncher6"; // clean master在功能表的GA
		public final static String CLEAN_MASTER_GA_FOR_DIALOG = "&referrer=utm_source%3D2000000014%26utm_campaign%3DGolauncher13"; // clean master卸载3次后弹框的GA
		public final static String CLEAN_MASTER_SLIDEMENU_PROMOTION = "&referrer=utm_source%3D2000000014%26utm_medium%3DGolauncher1";
//		public final static String KITTY_PLAY_PACKAGE_NAME = "com.jiubang.kittyplay"; // 美化中心包名
		public final static String KITTY_PLAY_GA_FOR_LAUNCHER_THEME = "&referrer=utm_source%3Dlauncher%26utm_medium%3DHyperlink%26utm_campaign%3Dgetmorethemes"; // 新美化中心的GA
		public final static String KITTY_PLAY_GA_FOR_LOCKER_THEME = "&referrer=utm_source%3Dlocker%26utm_medium%3DHyperlink%26utm_campaign%3Dgetmorethemes";
		
		public static final String NEXT_BROWSER_FTP_DOWNLOAD = "http://godfs.3g.cn/dynamic/resdown/201305221125/NextBrowser.apk";
		public static final String NEXT_BROWSER_GA = "&referrer=utm_source%3Dgo_launcher%26utm_medium%3DHyperlink%26utm_campaign%3DGoLauncherDockLine";
		
		public final static String PRIME_GETJAR_KEY = "com.gau.go.launcherex.key.getjar"; // 付费key包名
		public final static int PRIME_KEY_WITH_SIDE_DOCK_VER = 5; // key包有侧边栏的版本
		
		public final static String PRIME_KEY_URL = "https://play.google.com/store/apps/details?id=com.gau.go.launcherex.key"; // 付费key网址
		public final static String PRIME_GETJAR_KEY_URL = "https://play.google.com/store/apps/details?id=com.gau.go.launcherex.key.getjar"; // 付费key网址

		public final static String GO_LOCKER_GA = "&referrer=utm_source%3DGOLauncherSetting%26utm_medium%3Dbanner%26utm_campaign%3DGOLauncher";
		
		public final static String MAXTHON_BROWSER_GA_FOR_DIALOG = "&referrer=utm_source%3D3GGo%26utm_medium%3D3GGoDestop%26utm_term%3Dmaxthon_browser%26utm_content%3DScreen0%26utm_campaign%3D6200889000"; // 点击两次dock栏浏览器后，弹出推荐遨游浏览器
		
		
        public final static String SECURITY_GUARDS_GA_FOR_DIALOG = "&referrer=qihoo_id%3D104672";
	}

	/**
	 * 路径类 所有路径相关的常量都同意放在此处
	 * 
	 * @author luopeihuan
	 */
	public static final class Path {
		/**
		 * sdcard head
		 */
		public final static String SDCARD = Environment.getExternalStorageDirectory().getPath();

		// 存储路径
		public final static String LAUNCHER_DIR = "/GOLauncherEX";

		public final static String LAUNCHER_FACEBOOK_DIR = LAUNCHER_DIR + "/facebook";

		/**
		 * 数据库文件备份目录
		 */
		public final static String DBFILE_NAME = "/db";

		/**
		 * 数据库文件备份目录
		 */
		public final static String DBFILE_PATH = LAUNCHER_DIR + DBFILE_NAME;

		/**
		 * 主题等SharedPreferences文件备份目录
		 */
		public final static String PREFERENCESFILE_NAME = "/preferences";

		/**
		 * 主题等SharedPreferences文件备份目录
		 */
		public final static String PREFERENCESFILE_PATH = LAUNCHER_DIR + PREFERENCESFILE_NAME;

		/**
		 * 日志文件备份目录
		 */
		public final static String LOG_DIR = LAUNCHER_DIR + "/log/";

		/**
		 * facebook备份目录
		 */
		public final static String FACEBOOK_DIR = LAUNCHER_DIR + "/facebook/";

		/**
		 * 图片文件目录
		 */
		public final static String COMMON_ICON_PATH = LAUNCHER_DIR + "/icon/";

		public final static String GOTHEMES_PATH = SDCARD + LAUNCHER_DIR + "/gotheme/";
		public final static String THEMES_PATH = SDCARD + "/theme/";
		public final static String GOTHEMES_DATACAHE_PATH = GOTHEMES_PATH + "view_data_cahe";
		public final static String GOTHEMES_BANNER_DATACAHE_PATH = GOTHEMES_PATH
				+ "banner_data_cahe";
		public final static String GOTLOCKER_BANNER_DATACAHE_PATH = GOTHEMES_PATH
				+ "locker_banner_data_cahe";
		public final static String GOTHEMES_SPEC_DATACAHE_PATH = GOTHEMES_PATH + "spec_data_cahe";
		public final static String ADVERT_PATH = SDCARD + LAUNCHER_DIR + "/advert/"; // 15屏广告图片保存路径
		public final static String MSG_THEME_PATH = SDCARD + LAUNCHER_DIR + "/theme_msg.xml"; // 超级主题消息文件
		public final static String SIDEMENU_TOOLS_PATH = SDCARD + LAUNCHER_DIR + "/sidemenu/tools/"; //侧边栏广告图片
		public final static String SIDEMENU_WIDGET_PATH = SDCARD + LAUNCHER_DIR + "/sidemenu/widget/"; //侧边栏WIDGET图片
		public final static String SIDEMENU_CACHE_PATH = SDCARD + LAUNCHER_DIR + "/sidemenu/cache/"; //侧边栏WIDGET图片
		public final static String SIDEMENU_DOWNLOADS_PATH = SDCARD + LAUNCHER_DIR + "/sidemenu/downloads/"; //advert及widget下载路径
		/**
		 * GO zip主题目录
		 */
		public final static String GOT_ZIP_HEMES_PATH = GOTHEMES_PATH + "themes/";
		
		public final static String COUPON_PATH = GOTHEMES_PATH + "coupon/";
		
		/**
		 * 付费统计上传的缓存标志
		 */
		public final static String STATIST_FILE_NAME = "statistupload";
		public final static String STATIST_GERJAR_FILE_NAME = "getjarstatistupload";
		public final static String STATIST_PATH = SDCARD + LAUNCHER_DIR + "/" + STATIST_FILE_NAME + "/";

		/**
		 * 
		 * 原始版本路径 将ma路径定义在这里 Dock、功能表背景
		 */
		public final static String DOCK_BG_PATH = SDCARD + "/ma/dockbg.png";

		/**
		 * 模块名 各模块文件夹 eg:/sdcard/golauncher/desk/diy/theme/theme1(主题包名)/func/...
		 */
		public final static String FUNC_FOLDER_OLD = "/ma";
		public final static String FUNC_FOLDER = "/func";
		public final static String DOCK_FOLDER = "/dock";
		public final static String SCREEN_FOLDER = "/screen";

		// dock背景
		public final static String DOCK_BG = "/bg.png";

		// 功能表背景
		public final static String APPDRAWER_BG = "bg2.png";

		/**
		 * 主题文件夹路径 主题路径下再按主题名建立文件夹 主题相关的东西放入主题文件夹下
		 */
		public final static String THEME_PATH = LAUNCHER_DIR + "/desk/diy/theme";

		/**
		 * 特色图标路径
		 */
		public final static String SPECIAL_ICON_PATH = LAUNCHER_DIR + "/desk/diy/specialicon";

		/**
		 * 字体路径
		 */
		public final static String FONT_PATH = LAUNCHER_DIR + "/fonts";
		
		/**
		 * 四合一付费主题相关信息保存路径
		 */
		public final static String PAID_THEME_INFO_PATH = SDCARD + "/GoTheme/";

		/**
		 * GOStore图标路径
		 */
		public static final String GOSTORE_ICON_PATH = SDCARD + "/GoStore/icon/";

		/**
		 * 屏幕截图文件夹
		 */
		public static final String SCREEN_CAPUTRE_PATH = SDCARD + LAUNCHER_DIR + "/capture/";

		/**
		 * 应用管理-推荐应用，推荐应用信息
		 */
		public static final String APP_MANAGER_RECOMMEND_INFO_PATH = SDCARD + "/AppGame/";

		/**
		 * 应用管理-推荐应用，应用icon路径
		 */
		public static final String APP_MANAGER_ICON_PATH = APP_MANAGER_RECOMMEND_INFO_PATH
				+ "Icon/";

		/**
		 * 应用游戏中心缓存路径
		 */
		public static final String APP_MANAGER_CACHE_PATH = APP_MANAGER_RECOMMEND_INFO_PATH
				+ "Cache1/";

		/**
		 * 应用游戏中心详情页缓存地址
		 */
		public static final String APPS_DETAIL_CACHE_PATH = APP_MANAGER_CACHE_PATH + "AppDetail/";

		/**
		 * 应用游戏中心保存分类id对应数据的本地路径
		 */
		public static final String SAVE_CLASSIFICATION_APP_INFO_PATH = APP_MANAGER_CACHE_PATH
				+ "Classification/";

		/**
		 * 应用中心，widget信息
		 */
		public static final String APPCENTER_WIDGET_INFO_PATH = APP_MANAGER_RECOMMEND_INFO_PATH
				+ "Widget/";

		/**
		 * 自定义手势－文件存储目录
		 */
		public static final String DIY_GESTURE_NAME = "/diygesture/";

		/**
		 * 自定义手势－文件存储目录
		 */
		public static final String DIY_GESTURE_PATH = LAUNCHER_DIR + DIY_GESTURE_NAME;

		/**
		 * 应用中心/游戏中心网络错误邮箱反馈附件地址
		 */
		public static final String APP_MANAGER_CLASSIFICATION_EXCEPTION_RECORD_PATH = APP_MANAGER_RECOMMEND_INFO_PATH
				+ "cla_ex_record.txt";

		/**
		 * 应用中心／游戏中心邮箱反馈附件地址
		 */
		public static final String APP_MANAGER_FEEDBACK_RECOPE_PATH = APP_MANAGER_RECOMMEND_INFO_PATH
				+ "feedback_record.txt";

		/**
		 * 游戏中心搜索记录保存路径
		 */
		public static final String GAME_CENTER_SEARCH_HISTORY = APP_MANAGER_CACHE_PATH
				+ "game_search_history.txt";

		/**
		 * 应用中心热门搜索关键字保存路径
		 */
		public static final String APP_CENTER_HOT_SEARCH_KEYWORD = APP_MANAGER_CACHE_PATH
				+ "app_hot_search_keyword.txt";

		/**
		 * 游戏中心热门搜索关键字保存路径
		 */
		public static final String GAME_CENTER_HOT_SEARCH_KEYWORD = APP_MANAGER_CACHE_PATH
				+ "game_hot_search_keyword.txt";

		/**
		 * 应用中心启动loading页信息的保存路径
		 */
		public static final String APP_CENTER_START_LOADING = APP_MANAGER_CACHE_PATH
				+ "app_start_loading.txt";

		/**
		 * 应用中心loading页面提示信息保存路径
		 */
		public static final String APP_CENTER_LOADING_TIP = APP_MANAGER_CACHE_PATH
				+ "app_loading_tip.txt";

		/**
		 * 一键装机应用信息缓存文件路径
		 */
		public static final String YJZJ_INFO_PATH = APP_MANAGER_CACHE_PATH + "yjzj";
		/**
		 * 
		 */
		public static final String MY_GAME_OPERATOR_PATH = APP_MANAGER_CACHE_PATH + "operator.txt";

		/***
		 * 分享功能，图片存储路径
		 */
		public static final String SHARE_IMAGE_PATH = SDCARD + LAUNCHER_DIR + "/share/";

		/**
		 * 消息中心文件存储目录
		 */
		public static final String MESSAGECENTER_PATH = SDCARD + LAUNCHER_DIR + "/messagecenter/";
		public static final String MESSAGECENTER_IMAGE_PATH = MESSAGECENTER_PATH + "image/";
		
		public static final String PLUGIN_MANAGE_IMAGE_PATH = SDCARD + LAUNCHER_DIR + "/plugin/";

		/**
		 * 下载管理的下载完成项信息存储路径
		 */
		public static final String DOWNLOAD_COMPLETE_PATH = SDCARD
				+ "/GoStore/recommendedApp/classification/downloadcomplete";
		/**
		 * 下载管理的未下载完成项信息存储路径
		 */
		public static final String DOWNLOAD_NOT_COMPLETE_PATH = SDCARD
				+ "/GoStore/recommendedApp/classification/notdownloadcomplete";

		/**
		 * 下载管理的已经安装的下载任务存储路径
		 */
		public static final String DOWNLOAD_AND_INSTALLED_PATH = SDCARD
				+ "/GoStore/recommendedApp/classification/installedDownloadTask";

		/**
		 * 截图存放路径
		 */
		public static final String SNAPSHOT_PATH = LauncherEnv.Path.SDCARD
				+ LauncherEnv.Path.LAUNCHER_DIR + "_Screenshot";

		public static final String GOLOCKER_ZIP_HEMES_PATH = SDCARD + "/GOLocker/ZipTheme/";
		
		/**
		 * 自动备份DB目录
		 */
		public static final String DB_AUTO_BACKUP_PATH = SDCARD + LAUNCHER_DIR + "/autobackup";
		
		/**
		 * 文件夹同类应用推荐广告缓存路径
		 */
		public static final String FOLDER_AD_DATA_CACHE_PATH = SDCARD + LAUNCHER_DIR + "/FolderAd";
		public static final String FOLDER_AD_DATA_ICON_CACHE_PATH = SDCARD + LAUNCHER_DIR + "/FolderAd/icon/";
		
		/**
		 * 屏幕编辑界面缓存路径
		 */
		public static final String SCREEN_EDIT_PATH = SDCARD + LAUNCHER_DIR + "/screenEdit";
		
		/**
		 * 屏幕编辑界面推荐应用缓存路径
		 */
		public static final String SCREEN_EDIT_PUSH_PATH = SCREEN_EDIT_PATH + "/push";
		
		/**
		 * 屏幕编辑界面推荐应用数据缓存路径
		 */
		public static final String SCREEN_EDIT_PUSH_CACHEDATA_PATH = SCREEN_EDIT_PUSH_PATH + "/cacheData";
		
		/**
		 * 屏幕编辑界面推荐应用图片缓存路径
		 */
		public static final String SCREEN_EDIT_PUSH_CACHEICON_PATH = SCREEN_EDIT_PUSH_PATH + "/cacheIcon/";
		
		
		
		public static final String INSTALL_STAMP = "install_stamp";
		
		//======================GO推荐Widget功能模块===============================
		//GORecommWidget图片缓存路径
		public static final String GORECOMM_WIDGET_IMAGE_PATH = SDCARD + LAUNCHER_DIR + "/GoRecomm/cacheImage/";
		//GORecommWidget数据缓存路径
		public static final String GORECOMM_WIDGET_DATA_CACHE_PATH = SDCARD + LAUNCHER_DIR + "/GoRecomm/cacheFile/";
		//======================主题Icon(UI3.0)动态更换功能模块=============================
		//主题Icon资源文件缓存路径
		public static final String THEME_ICON_RESOURCE_CACHE_PATH = SDCARD + LAUNCHER_DIR + "/ThemeIcon/.cacheFile/";
		
		public final static String SERVER_CONFIG_PATH = SDCARD + LAUNCHER_DIR + "/config/server.conf";
		
		public static final String SMARTCARD_RECOMMEND_INFO_PATH = SDCARD + LAUNCHER_DIR + "/smartcard/";
		
		public static final String SMARTCARD_RECOMMEND_ICON_PATH = SDCARD + LAUNCHER_DIR + "/smartcard/recommIcon/";
	}
	
	/**
	 * server相关常量定义在此处
	 */
	public static final class Server {
		// 侧边栏广告正式服务器地址
		public final static String SIDEADVERT_BASE_HOSTURL_PRO = "http://launchermsg.3g.cn/golaunchermsg/advservice.do?";	//侧边栏广告正式地址
		public final static String GOWIDGET_BASE_HOSTURL_PRO = "http://goappcenter.3g.net.cn/recommendedapp/remdinfo.do";	//gowidget正式地址
		public final static String GOWIDGET_BACKUP_HOSTURL_PRO = "http://goappcenter.goforandroid.com/recommendedapp/remdinfo.do";	//gowidget备用地址
		// 侧边栏广告测试服务器地址
		public static final String SIDEADVERT_BASE_HOSTURL_SIT = "http://appdistest.3g.cn:9999/golaunchermsg/advservice.do?"; 
		public static final String GOWIDGET_BASE_HOSTURL_SIT = "http://183.61.112.38:8011/recommendedapp/remdinfo.do"; 
		public static final String GOWIDGET_BACKUP_HOSTURL_SIT =  "http://183.61.112.38:8011/recommendedapp/remdinfo.do"; 
		
		// 消息中心正式服务器地址
		public static final String MSG_CENTER_BASE_HOSTURL_PRO = "http://launchermsg.3g.cn/golaunchermsg/msgservice.do?";
		// 消息中心测试服务器地址
		public static final String MSG_CENTER_BASE_HOSTURL_SIT = "http://appdistest.3g.cn:9999/golaunchermsg/msgservice.do?";
		
		// 版本自动检测正式服务器地址
		public static final String AUTO_VERSION_CHECK_URL_PRO = "http://imupdate.3g.cn:8888/versions/check3?encrypt=1";
		// 自动版本检测测试服务器地址
		public static final String AUTO_VERSION_CHECK_URL_SIT = "http://ggtest.3g.net.cn/versions/check3?encrypt=1";
		
		// GoStore正式服务器地址
		public static final String GO_STORE_BASE_HOSTURL_PRO = "http://gostore.3g.cn/gostore/entrance";
		// GoStore测试服务器地址
		public static final String GO_STORE_BASE_HOSTURL_SIT = "http://192.168.112.72:8081/gostore/entrance";
		
		// 应用中心国内域名正式服务器地址
		public static final String APP_CENTER_URL_CHINA_PRO = "http://goappcenter.3g.net.cn";
		// 应用中心国内域名测试后台服务器地址
		public static final String APP_CENTER_URL_CHINA_SIT = "http://183.61.112.38:8011";
		// 应用中心国外域名正式服务器地址
		public static final String APP_CENTER_URL_OTHERS_PRO = "http://goappcenter.goforandroid.com";
		// 应用中心国外域名测试服务器地址
		public static final String APP_CENTER_URL_OTHERS_SIT = "http://183.61.112.38:8011";
		
		// 内测管理正式服务器地址
		public static final String BETA_XML_URL_PRO = "http://appdistest.3g.cn:9999/golaunchermsg/getresource.do?key=betainfo&vc=300&local=cn";
		// 内测管理测试服务器地址
		public static final String BETA_XML_URL_SIT = "http://godfs.3g.cn/dynamic/resdown/201310281554/promotion_config_beta3.xml";
		
		// 广告正式服务器地址
		public static final String ADVERT_BASE_HOSTURL_PRO = "http://launchermsg.3g.cn/golaunchermsg/advservice.do?";
		// 广告测试服务器地址
		public static final String ADVERT_BASE_HOSTURL_SIT = "http://appdistest.3g.cn:9999/golaunchermsg/advservice.do?";
		
		// 统计数据上传的正式服务器地址
		public static final String STATISTICS_DATA_URL_PRO = "http://gostore.3g.cn/gostore/webcontent/function/GetjarPrice.jsp?aid=";
		// 统计数据上传的测试服务器地址
		public static final String STATISTICS_DATA_URL_SIT = "http://gotest.3g.net.cn:8091/gostore/webcontent/function/GetjarPrice.jsp";
		
		// admob正式服务器地址
		public static final String ADMOB_BASE_HOSTURL_PRO = "http://gostoreinter.3g.cn/gostore/webcontent/function/getAdmobCon.jsp?";
		// admob测试服务器地址
		public static final String ADMOB_BASE_HOSTURL_SIT = "http://gotest.3g.net.cn/gostore/webcontent/function/getAdmobCon.jsp?";
		
		// 四合一付费主题正式服务器地址
		public static final String PAID_THEME_INFO_URL_PRO = "http://gostore.3g.cn/gostore/webcontent/function/AllGetjarPrice.jsp?appuid=1&channel=";
		// 四合一付费主题测试服务器地址
		public static final String PAID_THEME_INFO_URL_SIT = "http://61.145.124.70:8081/gostore/webcontent/function/AllGetjarPrice.jsp?appuid=1&channel=";
		
		// 推广活动正式服务器地址
		public static final String PROMOTION_XML_URL_PRO = "http://godfs.3g.cn/dynamic/resdown/201310161354/promotion_config.xml";
		// 推广活动测试服务器地址
		public static final String PROMOTION_XML_URL_SIT = "http://godfs.3g.cn/dynamic/resdown/201310281554/promotion_config_beta3.xml";
		
		// 拼接广告的正式服务器地址
		public static final String AD_HTTP_URL_PRO = "http://appadv.3g.cn/adv/adv.do?";
		// 拼接广告的测试服务器地址
		public static final String AD_HTTP_URL_SIT = "http://61.145.124.64/adv/adv.do?";
		
		// UI3.0主题资源下发测试服务器地址
		public static final String THEME_ICON_ISSUED_SIT = "http://gotest.3g.net.cn/iconUpdate/common?funid=1&rd=";
		// UI3.0主题资源下发正式服务器地址
		public static final String THEME_ICON_ISSUED_PRO = "http://iconupdate.3g.cn/iconUpdate/common?funid=1&rd=";
		
		// 应用Icon配置资源下发正式服务器地址
		public static final String APP_ICON_CONFIG_URL_PRO = "gopromotion.3g.cn/gui1111/webcontent/function/unread.jsp";
		// 应用Icon配置资源下发测试服务器地址
		public static final String APP_ICON_CONFIG_URL_SIT = "http://gotest.3g.net.cn/gui1111/webcontent/function/unread.jsp";
				
		public static final String SIDE_ADVERT_CONFIG_USE_TEST_SERVER = "side_advert_config_use_test_server";
		public static final String MSG_CENTER_CONFIG_USE_TEST_SERVER = "msg_center_config_use_test_server";
		public static final String AUTO_VERSION_CHECK_CONFIG_USE_TEST_SERVER = "auto_version_check_config_use_test_server";
		public static final String GO_STORE_CONFIG_USE_TEST_SERVER = "go_store_config_use_test_server";
		public static final String APP_CENTER_CONFIG_USE_TEST_SERVER = "app_center_config_use_test_server";
		public static final String BETA_XML_CONFIG_USE_TEST_SERVER = "beta_xml_config_use_test_server";
		public static final String ADVERT_CONFIG_USE_TEST_SERVER = "advert_config_use_test_server";
		public static final String STATISTICS_DATA_CONFIG_USE_TEST_SERVER = "statistics_data_config_use_test_server";
		public static final String ADMOB_CONFIG_USE_TEST_SERVER = "admob_config_use_test_server";
		public static final String PAID_THEME_INFO_CONFIG_USE_TEST_SERVER = "paid_theme_info_config_use_test_server";
		public static final String PROMOTION_XML_CONFIG_USE_TEST_SERVER = "promotion_xml_config_use_test_server";
		public static final String AD_HTTP_CONFIG_USE_TEST_SERVER = "ad_http_config_use_test_server";
		public static final String THEME_ICON_ISSUED_TEST_SERVER = "theme_icon_issued_test_server";
		public static final String SCREEN_ICON_CONFIG_TEST_SERVER = "screen_icon_config_test_server";
	}
}
