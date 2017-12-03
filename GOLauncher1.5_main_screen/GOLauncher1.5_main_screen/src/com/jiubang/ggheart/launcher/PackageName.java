package com.jiubang.ggheart.launcher;

/**
 * 包名集合
 * @author yangguanxiang
 *
 */
public class PackageName {
	public final static String GOOGLE_TALK_ANDROID_TALK = "com.google.android.talk";
	public final static String GOOGLE_TALK_ANDROID_GSF = "com.google.android.gsf";
	public final static String GMAIL = "com.google.android.gm";
	public final static String K9MAIL = "com.fsck.k9";
	public final static String FACEBOOK = "com.facebook.katana";
	public final static String SINA_WEIBO = "com.sina.weibo";
	public final static String TWITTER = "com.twitter.android";
	
	public final static String MEDIA_PLUGIN = "com.gau.golauncherex.mediamanagement";
	public final static String SHELL_PLUGIN = "com.gau.golauncherex.plugin.shell";
	public final static String NOTIFICATION_PLUGIN = "com.gau.golauncherex.notification";
	
	public final static String SIDEBAR_PACKAGE = "com.jiubang.plugin.sidebar";
	public final static String TOUCHER_EX = "com.gau.go.touchhelperex";
	public final static String TOUCHER_PRO = "com.gau.go.toucherpro";
	
	public final static String OLD_CALENDAR_PKG = "com.gau.go.launcherex.gowidget.calendarwidget";
	//innerWidget包名
	public final static String GO_INNER_WIDGET_PACKAGENAME = "com.jiubang.ggheart.innerwidgets.appgamewidget";
	// PackageName 有些地方取不到PackageName，写死在此处
    public final static String PACKAGE_NAME = "com.gau.go.launcherex";
    public final static String GOLAUNKER_PACKAGE_NAME = "com.gau.go.launker"; // go桌面锁屏版的包名
    public static final String PACKAGE_NAME_NEXTLAUNCHER = "com.gtp.nextlauncher";  //next桌面的包名
    public static final String PACKAGE_NAME_NEXTLAUNCHER_TRIAL = "com.gtp.nextlauncher.trial";  //next trial version
    
    // GO Lock 的PackageName
    public final static String GO_LOCK_PACKAGE_NAME = "com.jiubang.goscreenlock";
    
    // GO短信包名
    public final static String GOSMS_PACKAGE = "com.jb.gosms";
    
    // 假图标包名
    public static final String GO_STORE_PACKAGE_NAME = "com.gau.diy.gostore"; // GOStore
    public static final String GO_THEME_PACKAGE_NAME = "com.gau.diy.gotheme"; // 桌面主题
    public static final String GO_WIDGET_PACKAGE_NAME = "com.gau.diy.gowidget"; // GOWidget
    public static final String RECOMMAND_CENTER_PACKAGE_NAME = "com.gau.diy.recomendcenter"; // 应用中心
    public static final String GAME_CENTER_PACKAGE_NAME = "com.gau.diy.gamecenter"; // 游戏中心
    public static final String FREE_THEME_PACKAGE_NAME = "com.gau.diy.freetheme"; // 限免主题
    public static final String PROMANAGE_PACKAGE_NAME = "com.gau.diy.promanage";
    public static final String RECENTAPP_PACKAGE_NAME = "com.gau.diy.recentapp";
    
    public static final String PACKAGE_PREFIX_GO_WIDGET = "com.gau.go.launcherex.gowidget.";    //GOwidget包名前缀
    public static final String INTENT_CATEGORY_DEFAULT = "android.intent.category.DEFAULT"; //category的默认值
    public static final String PACKAGE_PREFIX_NEXT_APP = "com.gtp.nextlauncher.";   //next系列程序包名前缀
    
    public static final String GO_DEBUG_HELPER_PKGNAME = "com.daqi.debughelper"; //debughelper的包名
    
    //不需处理浏览器事件的包名集合，避免用户最近打开使用了淘宝客户端，然后点击dock栏上图标时，又启动了淘宝客户端你。
	public static final String[] PKGS_BROWSER_NOTNEEDHANDLED = { "com.taobao.taobao", "com.xiaomi.market" };
    
    /**
     * notification短信通知等模块
     */
    public final static String NOTIFICATION_PACKAGE_NAME = "com.gau.golauncherex.notification";
    
    // MultipleWallpaper多屏多壁纸 包名
    public final static String MULTIPLEWALLPAPER_PKG_NAME = "com.go.multiplewallpaper";
    // RecommLiveWallpaper推荐动态壁纸 包名
    public final static String RECOMM_LIVEWALLPAPER_PKG_NAME = "com.gau.go.launcher.lwp.core"; //com.gtp.nextlauncher.liverpaper.NextCore  com.gau.go.launcher.lwp.core
    
    // 动态壁纸选择界面 包名，用于判断设备是否支持动态壁纸
    public final static String WALLPAPER_LIVE_PICKER_PKG_NAME = "com.android.wallpaper.livepicker";

    // 桌面迁移工具包名
    public final static String DESKMIGRATE_PACKAGE_NAME = "com.ma.deskmigrate";
    
    // Locker
    public final static String LOCKER_PACKAGE = "com.jiubang.goscreenlock";
    public final static String LOCKER_PRO_PACKAGE = "com.jiubang.goscreenlock.pro"; // 收费版GO锁屏
    
    // Locker vip
    public final static String LOCKER_VIP_PACKAGE = "com.jiubang.goscreenlock.vip";
    
    // GO 备份
    public final static String GOBACKUP_PACKAGE = "com.jiubang.go.backup.pro";
    // GO HD桌面
    public final static String GOHD_LAUNCHER_PACKAGE = "com.go.launcherpad";
    
    // 语言包
    public final static String LANGUAGE_PACKAGE = "com.gau.go.launcherex.language";
    
    // 推荐APP
    public final static String RECOMMEND_NETQIN_PACKAGE = "com.netqin.ps";
    public final static String RECOMMEND_EVERNOTE_PACKAGE = "com.evernote";
    public final static String RECOMMAND_GOSMS_PACKAGE = "com.jb.gosms"; // go短信 widget
    public final static String RECOMMAND_GOPOWERMASTER_PACKAGE = "com.gau.go.launcherex.gowidget.gopowermaster"; // go省电 widget
    public final static String RECOMMAND_GOTASKMANAGER_PACKAGE = "com.gau.go.launcherex.gowidget.taskmanagerex"; // go任务管理器EX widget
    public final static String RECOMMAND_GOKEYBOARD_PACKAGE = "com.jb.gokeyboard"; // go输入法
    public final static String RECOMMAND_GOLOCKER_PACKAGE = "com.jiubang.goscreenlock"; // go锁屏
    public final static String RECOMMAND_GOBACKUP_PACKAGE = "com.jiubang.go.backup.pro"; // go备份 
    public final static String RECOMMAND_GOWEATHEREX_PACKAGE = "com.gau.go.launcherex.gowidget.weatherwidget"; // go天气EX 
    public final static String RECOMMAND_GOBACKUPEX_PACKAGE = "com.jiubang.go.backup.ex"; // go备份EX
    public final static String RECOMMAND_LOCKSCREEN_PACKAGE = "com.jiubang.goscreenlock.plugin.lockscreen"; // 一键锁屏
    public final static String RECOMMEND_NEXTLAUNCHER_PACKAGE = PACKAGE_NAME_NEXTLAUNCHER; // NextLauncher
    public final static String RECOMMEND_BAIDUBROWSER_PACKAGE = "com.baidu.browser.inter"; // baidu browser
    public final static String RECOMMEND_BAIDUBATTERSAVER_PACKAGE = "com.dianxinos.dxbs"; // 百度省电
    
    //非推荐的，剩余go widget 
    public final static String TIMER_PACKAGE = "com.gau.go.launcherex.gowidget.timer"; //倒计时
    public final static String SWITCHER_PLUS_PACKAGE = "com.gau.go.launcherex.gowidget.newswitchwidget"; // 开关+
    public final static String SCANNER_PACKAGE = "com.gau.go.launcherex.gowidget.gobarcodescanner"; //二维码
    public final static String FLASH_LIGHT_PACKAGE = "com.gau.go.launcherex.gowidget.flashlight"; // 手电筒
    public final static String TASK_PACKAGE = "com.gau.go.launcherex.gowidget.taskmanager"; // 任务管理器（老版）
    public final static String SWITCH_PACKAGE = "com.gau.go.launcherex.gowidget.switchwidget"; // 开关
    public final static String NEW_SWITCH_PACKAGE = "com.gau.go.launcherex.gowidget.newswitchwidget"; // 新开关
    public final static String CALENDAR_PACKAGE = "com.gau.go.launcherex.gowidget.calendarwidget"; // 日历
    public final static String NEW_CALENDAR_PACKAGE = "com.gau.go.launcherex.gowidget.newcalendarwidget"; // 新日历
    public final static String CLOCK_PACKAGE = "com.gau.go.launcherex.gowidget.clockwidget"; // 时钟
    public final static String NOTE_PACKAGE = "com.gau.go.launcherex.gowidget.notewidget"; // 便签
    public final static String NEW_NOTE_PACKAGE = "com.gau.go.launcherex.gowidget.newnotewidget"; // 新便签
    public final static String CONTACT_PACKAGE = "com.gau.go.launcherex.gowidget.contactwidget"; // 联系人
    public final static String SINA_PACKAGE = "com.gau.go.launcherex.gowidget.weibowidget"; // 新浪微博
    public final static String TENCNT_PACKAGE = "com.gau.go.launcherex.gowidget.qqweibowidget"; // 腾讯微博
    public final static String EMAIL_PACKAGE = "com.gau.go.launcherex.gowidget.emailwidget"; // 邮箱
    public final static String FACEBOOK_PACKAGE = "com.gau.go.launcherex.gowidget.fbwidget"; // FaceBook
    public final static String TWITTER_PACKAGE = "com.gau.go.launcherex.gowidget.twitterwidget"; // Twitter
    public final static String BOOKMARK_PACKAGE = "com.gau.go.launcherex.gowidget.bookmark"; // 书签
    public final static String APP_PACKAGE = "com.gau.go.launcherex"; // 应用游戏中心
    public final static String STORE_PACKAGE = "com.gau.go.launcherex"; // GO精品
    public final static String GOSEARCH_PACKAGE = "com.gau.go.launcherex.gowidget.gosearchwidget"; // GO搜索
    public final static String NOWWIDGET_PACKAGE = "com.gau.go.launcherex.gowidget.nowwidget"; // Now直播
    public final static String NEWS3G_PACKAGE = "com.gau.go.launcherex.gowidget.newswidget "; // 3G门户新闻
    public final static String SEARCH_WIDGET_PACKAGE = "com.gau.go.launcherex.gowidget.searchwidget"; // 搜索widget
    public final static String RECOMMAND_TTDONDTING_PACKAGE = "com.sds.android.ttpod"; // 天天动听 
    public final static String RECOMMAND_TIANQITONG_PACKAGE = "sina.mobile.tianqitong";
    public final static String CLEAN_MASTER_PACKAGE = "com.cleanmaster.mguard"; // clean master 包名
    public final static String CLEAN_MASTER_PACKAGE_CN = "com.cleanmaster.mguard_cn"; // clean master 包名
    public final static String OK_SCREEN_SHOT = "com.gau.go.launcherex.gowidget.okscreenshot"; // clean master 包名
    
    public final static String KITTY_PLAY_PACKAGE_EX_NAME = "com.kittyplay.ex"; // 新美化中心包名
    
    public final static String NEXT_BROWSER_PACKAGE_NAME = "com.jiubang.browser"; // next浏览器包名
    
    public final static String PRIME_KEY = "com.gau.go.launcherex.key"; // 付费key包名
    
    public final static String GO_LOCKER_PACKAGE = "com.jiubang.goscreenlock";
    
    public final static String BAIDU_MUSIC_PACKAGE_NAME_STRING = "com.ting.mp3.android"; // 百度音乐包名
    
    public final static String CHUBAO_PACKAGE_NAME_STRING = "com.cootek.smartdialer"; // 触宝包名
    
    public final static String MAXTHON_PACKAGE = "com.mx.browser"; // 遨游 包名
    
    public final static String SECURITY_GUARDS_PACKAGE = "com.qihoo.security"; // 360 包名
    
    //安卓优化大师
    public final static String DU_SPEED_BOOSTER = "com.dianxinos.optimizer.duplay"; 
    
    
    //=============================className=================================
    //天气主题设置Activity
    public final static String GO_WEATHEREX_THEME_SETTING_ACTIVITY = "com.gau.go.launcherex.gowidget.weather.view.ThemeSettingActivity";
    
    /**
     * 个性化推荐应用或轻游戏包名
     */
    public final static String RECOMMEND_APP_PACKAGE = "com.gau.diy.recommendapp";
    
    
    
    
}
