package com.jiubang.ggheart.apps.desks.diy;

import com.jiubang.ggheart.apps.desks.diy.guide.RateGuideTask;


/**
 * IPreferencesIds
 */
public interface IPreferencesIds {
	public static final String DESK_SHAREPREFERENCES_FILE = "desk";
	// 进入功能表次数
	public static final String ENTER_FUNC = "ENTER_FUNC";

	// 点击侧边栏引导标识位
	public static final String TOUCH_SIDEBAR = "TOUCH_SIDEBAR";

	/**
	 * 屏幕预览编辑时提示次数
	 */
	public static final String PREVIEW_DRAG_TIP_TIME = "preview_drag_tip";

	/**
	 * 长按图标显示提示信息
	 */
	public static final String SCREEN_LONG_PRESS_TIP = "screen_long_press_tip";

	/**
	 * 按dock+号图标显示提示信息
	 */
	// public static final String Dock_PRESS_TIP = "dock_tip";

	/**
	 * 进入预览时提示的次数
	 */
	public static final String PREVIEW_TIP_TIME = "preview_tip_time";


	/**
	 * 菜单新建文件夹次数
	 */
	public static final String APP_FUNC_NEW_FOLDER = "APP_FUNC_NEW_FOLDER";



	/**
	 * 我的应用，列表当前排序方式
	 */
	public static final String APPS_ORDER_TYPE = "apps_order_type";

	/**
	 * 我的应用，批量卸载 是否首次进入
	 */
	public static final String APPS_UNINSTALL_SHOW_DIALOG = "apps_uninstall_show_dialog";

	/**
	 * 我的应用，记录root权限信息
	 */
	public static final String APPS_UNINSTALL_GET_ROOT = "apps_uninstall_get_root";

	/**
	 * 当前桌面的语言
	 */
	public static final String CURRENT_SELECTED_LANGUAGE = "currentseltet_language";

	/**
	 * 当前桌面的语言备份
	 */
	public static final String CURRENT_SELECTED_LANGUAGE_BACKUP = "currentseltet_language_back";

	/**
	 * 当前桌面的数据库版本号
	 */
	public static final String GO_DB_VERSION = "current_dbversion";

	/**
	 * 桌面设置使用到的SharedPreferences文件
	 */
	public final static String SETTING_PREFERENCES = "setting_preferences";

	public final static String SHOW_ALERT_DIALOG_FOR_ACTION_BAR_SETTING = "show_alert_dialog_for_action_bar_setting";

	/**
	 * 特殊处理-保存桌面设置-壁纸，功能表，快捷条背景设置
	 */
	public static final String DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW = "desk_setting_visual_background_tab_view";
	public static final String DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW_APPDRAWER = "desk_setting_visual_background_tab_view_appdrawer";
	public static final String DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW_DOCK = "desk_setting_visual_background_tab_view_dock";

	/**
	 * 是否支持DB数据共享
	 */
	public final static String DB_PROVIDER_SUPPORT = "db_provider_support";
	public final static String IMPORT_SUPPORT = "support";
	public final static String HAS_IMPORTED = "has_imported";

	public static final String FEATUREDTHEME_CONFIG = "featuredtheme_config";
	public static final String LAUNCHER_FEATUREDTHEME_STAMP = "launcher_featuredtheme_stamp";
	public static final String LCOKER_FEATUREDTHEME_STAMP = "lcoker_featuredtheme_stamp";
	public static final String LAUNCHER_HOTTHEME_STAMP = "launcher_hottheme_stamp";
	public static final String BANNER_POST_STAMP = "banner_post_stamp";
	public static final String LOCKER_BANNER_POST_STAMP = "locker_banner_post_stamp";
	public static final String SPEC_POST_STAMP = "spec_post_stamp";
	public static final String HASNEWTHEME = "hasnewtheme";
	public static final String SHOWHASNEWTHEME = "showhasnewtheme";
//	public static final String HASSHOWFEATURENOTIFY = "hasshowfeaturenotify";
//	public static final String HASSHOWLOCKERNOTIFY = "hasshowlockernotify";
//	public static final String HASSHOWHOTNOTIFY = "hasshowhotnotify";
	public static final String LOCKER_HASNEWTHEME = "has_new_locker_theme";
	public static final String LAST_SHOW_NEWTHEME_STAMP = "last_show_stamp";
	public static final String NEED_TO_SHOW_MORE_TIP_POINT = "need_to_show_more_tip_point";
	public static final String TIP_TO_UPDATE_UI3_THEME_HAS_CLICKED = "TIP_TO_UPDATE_UI3_THEME_HAS_CLICKED";

	// add by zhoujun 服务器更新推荐应用的标识值，服务器通过该值判断是否有更新
	public static final String APP_MANAGER_RECOMMEND = "recommendedApp";
	public static final String APP_MANAGER_RECOMMEND_PHEAD = "recommendedApp_phead";
	public static final String APP_MANAGER_RECOMMEND_MARK = "mark";
	public static final String APP_MANAGER_CATEGORIES_APP_MARK = "CATEGORIES_APP_MARK";
	public static final String APP_MANAGER_CATEGORIES_GAME_MARK = "CATEGORIES_GAME_MARK";
	public static final String APP_MANAGER_FEATURE_APP_MARK = "FEATURE_APP_MARK";
	public static final String APP_MANAGER_FEATURE_GAME_MARK = "FEATURE_GAME_MARK";

	public static final String PREFENRCE_NETLOG_STATISTICS_DATA = "netlog_statistics_data";
	//应用游戏中心widget，安装后第一次滑动在widget所在的屏幕时，Toast提示用户可以上下滑动切换内容
	public static final String APPGAME_WIDGET_SHOW_MESSAGE = "appgame_widget_show_message";

	/**
	 * 应用市场，公共util
	 */
	public static final String GOMARKET_PUBLIC_UTIL = "gomarket_public_util";

	/**
	 * 用户向导配置
	 */
	public static final String USERTUTORIALCONFIG = "tutorial";
	public static final int SHOW_SCREEN_EFFECT_FIRST_GUIDE_COUNT = 20; // 第一次提示滑屏次数
	public static final int SHOW_SCREEN_EFFECT_SECONDE_GUIDE_COUNT = 20; // 第二次提示滑屏次数
	public static final String SCREEN_CHANGE_COUNT = "screenchangecount"; // 一共滑了几次屏
	// 是否在第一次桌面滑屏特效提示中设置了特效
	public static final String SHOULD_SHOW_SCREEN_EFFECT_SECOND_TIP = "should_show_screen_effect_second_tip";
	public static final String SHOULD_SHOW_DOCK_SLIP_GUIDE = "should_show_dock_slip_guide";
	public static final String SHOULD_SHOW_DOCK_GESTURE_GUIDE = "should_show_dock_gesture_guide";
	public static final String SHOULD_SHOW_APPFUNC_FOLDER_GUIDE = "show_appfunc_folder_guide";
	public static final String SHOULD_SHOW_APPFUNC_DRAG_GUIDE = "show_appfunc_drag_guide";
	public static final String SHOULD_SHOW_APPFUNC_MENU_GUIDE = "show_appfunc_menu_guide";
	public static final String SHOULD_SHOW_SCREEN_LOCK_GUIDE = "show_screen_lock_guide";
	public static final String SHOULD_SHOW_SCREEN_LOCK_GUIDE_PAY_DIALOG = "show_screen_lock_guide_pay_dialog";
	public static final String SHOULD_SHOW_PREVIEW_HOME = "show_preview_home"; //add by jiang 设置为默认桌面后  按home键跳屏幕预览
	public static final String SHOULD_SHOW_SCREEN_LOCK_GGMENU = "show_screen_lock_ggmenu";  // 是否高亮显示ggmenu锁定编辑
	public static final String SHOULD_SHOW_WAPAPERSETTING_GUIDE = "should_show_wallpapersetting_guide";
	public static final String SHOULD_SHOW_PRIVIEW_GUIDE = "sensetutorial";
	public static final String SHOULD_SHOW_SCREENFOLDER_GUIDE = "should_show_screenfolder_guide";
	public static final String SHOULD_SHOW_SCREEN_MENU_OPEN_GUIDE = "show_screen_menu_open_guide";
	public static final String SHOULD_SHOW_APPFUNC_APP_UPDATE_GUIDE = "show_appfunc_app_update_guide";
	public static final String SHOULD_SHOW_DOCK_AUTO_FIT_GUIDE = "should_show_dock_auto_fit_guide";
	public static final String SHOULD_SHOW_CUSTOM_GESTURE = "show_custom_gesture_guide"; // 自定义手势提示
	public static final String CANCLE_DIYGESTURE_TIME = "cancle_diygesture_time"; // 关闭自定义手势的次数
	public static final String SHOULD_SHOW_ONE_X_GUIDE = "should_show_one_x_guide";
	public static final String SHOULD_SHOW_LANGUAGE_GUIDE = "languagetutorial";
	public static final String SHOULD_SHOW_DOCK_BAR_ICON_GESTURE = "should_show_dock_bar_icon_guide"; // dock栏图标提示 
//	public static final String SHOULD_SHOW_LIKE_US_LIGHT = "should_show_like_us_light"; // 需要显示like us的高亮图
	public static final String LAUNCHER_INSTALL_OR_SETDEFAULT_TIME = "launcher_install_or_setdefalut_time"; //新用户安装或恢复默认时间
	public static final String SHOULD_SHOW_PRIVIEW_EDIT = "should_show_priview_edit"; //屏幕预览是否要高亮显示
	public static final String SHOULD_SHOW_APPDRAWER_SIDEBAR_GUIDE = "should_show_appdrawer_sidebar_guide"; //功能表侧边栏引导提示
	public static final String IS_NEW_USER = "is_new_user"; //是否是全新用户，覆盖安装后为false
	
	public final static String NOTIFICATION_SETTING = "notification_setting";

	//统计
	public static final String ALL_PURPOSE_STAT = "all_purpose_stat"; //万能统计的SharedPreferences
	public static final String DESK_ACTION_DATA = "desk_action_data"; //桌面用户行为统计的SharedPreferences
	public static final String APP_FUNC_ACTION_DATA = "app_func_action_data"; //功能表用户行为的SharedPreferences
	public static final String THEME_TAB_STAT_DATA = "theme_tab_stat_data"; //桌面主题tab统计数据
	public static final String USERECORD_STAT = "userecord_stat"; // 使用记录用SharedPreferences
	public static final String MENU_COUNT_DATA = "menu_count_data"; // 使用记录用SharedPreferences
	public static final String STATISTIC_HTTP_EXCEPTION_DATE = "statistic_http_exception_date"; // 使用记录用SharedPreferences
	public static final String STATISTIC_NO_UPLOAD_DATE = "statistic_no_upload_date"; // 使用记录用SharedPreferences
	public static final String APP_DATA = "app_data"; // 使用记录用SharedPreferences
	public static final String APP_SHOW_DATA = "app_show_data"; // 使用记录用SharedPreferences
	public static final String STATISTIC_DATA = "statistic_data"; // 使用记录用SharedPreferences
	public static final String APPFUNC_INOUT_EFFECT = "menu_inout_effect";
	public static final String APPFUNC_HORIZONTAL_EFFECT = "menu_horizontal_effect";
	public static final String APPFUNC_VERTICAL_EFFECT = "menu_vertical_effect";
	public static final String BACKUP = "backup";
	public static final String ENTRY_COUNT_STAT = "entry_count_stat";
	public static final String SEARCH_KEYWORDS_STAT = "search_keywords_stat";
	public static final String USER_BASE_STAT = "user_base_stat";
	public static final String STATISTICS_DATA_FILE_NAME = "statisticsData"; // 保存统计数据的sharedPreference文件名
	public static final String STATISTICS_USE_TIME_KEY = "use_time"; // 保存桌面使用时间所使用的KEY
	public static final String STATISTICS_NET_TIME_KEY = "net_time"; // 保存网络请求成功使用时间所使用的KEY
	public static final String STAT_GUI_TAB = "stat_gui_tab"; //GUI　TAB点击次数统计的SharedPreferences
	public static final String STAT_GUI_ENTRY = "stat_gui_entry"; //GUI进入次数统计的SharedPreferences
	public static final String APPGAME_TREATMENT = "appgame_treatment"; //应用游戏中心应用treatment的SharedPreferences

	public final static String SHAREDPREFERENCES_THEME = "themepackage"; // 主题的sharedpreferences名称

	public final String CUR_THEME_PKG = "cur_theme_pkg";
	public final String CUR_THEME_PKG_PREFERENCES = "pubicthemespreferences";

	public static final String SHOULD_SHOW_MORE_APP_TIP = "should_show_more_app_tip";
	public static final String HIDE_APP_SHOW_TIP_COUNT = "hide_app_show_tip_count";

	public static final String ADVERT_SCREEN_DATA = "advert_screen_data"; // 使用记录用SharedPreferences 15屏广告点击统计
	public static final String ADVERT_NEET_OPEN_DATA = "advert_net_open_data"; //15屏广告安装后需要进行打开提示
	
	public static final String CLEAN_SCREEN_AUTO_ADD_SHORTCUT_FILE = "clean_screen_auto_add_shortcut_file"; //自动生成图标的列表
	public static final String CLEAN_SCREEN_AUTO_ADD_KEY = "clean_screen_auto_add_shortcut_key";
	public static final String CLEAN_SCREEN_APP_OPEN_TIME_FILE = "clean_screen_open_time_file"; //应用程序打开的时间

	//收费版加入
	public static final String PREFERENCES_DESK_SETTING_NO_ADVERT_FIRST_OPEN = "desk_setting_no_advert_first_open"; //没有广告第一次打开
	public static final String PREFERENCES_DESK_SETTING_FOUR_GESTURE_FIRST_OPEN = "desk_setting_four_gesture_first_open"; //第一次进入新手势设置
	public static final String PREFERENCES_DESK_SETTING_NEW_EFFECT_FIRST_OPEN = "desk_setting_new_effect_first_open"; //第一次进入特效设置
	public static final String PREFERENCES_APPFUNC_SETTING_NEW_EFFECT_FIRST_OPEN = "appfunc_setting_new_effect_first_open"; //第一次进入功能表特效设置
	public static final String PREFERENCES_SETTING_HADPAY_VIEW_OPEN = "setting_had_pay_first_open"; //第一次进入激活界面
	public static final String PREFERENCES_SETTING_HADPAY_NO_AD_OPEN = "setting_had_pay_no_advert"; //只记录一次点击去广告
	public static final String PREFERENCES_SCREENEDIT_NEW_EFFECT_FIRST_OPEN = "screenedit_new_effect_first_open"; //第一次进入添加界面特效设置
	public static final String PREFERENCES_LOCK_FIRST_OPEN = "lock_first_open"; //第一次进入安全锁的设置
	public static final String PREFERENCES_APPFUNC_SETTING_CRYSTAL_EFFECT_FIRST_OPEN = "appfunc_setting_crystal_effect_first_open"; //第一次进入功能表水晶特效设置
	public static final String PREFERENCES_DESK_SETTING_CRYSTAL_EFFECT_FIRST_OPEN = "desk_setting_crystal_effect_first_open"; //第一次进入水晶特效设置
	public static final String PREFERENCES_SCREENEDIT_CRYSTAL_EFFECT_FIRST_OPEN = "screenedit_crystal_effect_first_open"; //第一次进入添加界面特效设置
	public static final String PREFERENCES_SIDE_DOCK_FIRST_SHOW = "side_dock_first_show"; //第一次进入侧边栏的设置
	public static final String PREFERENCES_SIDE_DOCK_FIRST_OPEN = "side_dock_first_open"; //第一次进入侧边栏的设置
	public static final String PREFERENCES_SIDE_DOCK_POSITION_FIRST_OPEN = "side_dock_position_first_open"; //第一次进入侧边栏左右的设置
	public static final String PREFERENCES_SIDE_DOCK_AREA_FIRST_OPEN = "side_dock_area_first_open"; //第一次进入侧边栏区域的设置
	public static final String PREFERENCES_OPEN_BROWSER_COUNT = "open_browser_count"; //浏览器启动次数
	public static final String PREFERENCES_OPEN_BROWSER_COUNT_CN = "open_browser_count_cn"; //浏览器启动次数-CN包
	public static final String PREFERENCES_BROWSER_ADVERT_NEED_SHOW_CN = "browser_advert_show_count_cn"; //浏览器广告弹出-CN包
	public static final String PREFERENCES_NO_ADVERT_PAY_DIALOG_FIRST_OPEN = "desk_setting_no_pay_dialog_advert_first_open"; //广告提示付费页第一次打开
	public static final String PREFERENCES_CLEAR_RECENT_COUNT = "open_browser_count"; //清理最近打开次数
	public static final String PREFERENCES_HAD_OPEN_BROWSER = "open_had_browser"; //已经弹出了遨游浏览器推荐。
	public static final String PREFERENCES_TASK_KILL_NUM = "preferences_task_kill_num"; //点击正在运行查杀按钮次数。
	public static final String PREFERENCES_TASK_KILL_IS_NEED_DISPLAY_RECOMMENDED = "preferences_task_kill_is_need_display_recommended"; //点击正在运行按钮，是否需要弹出推荐对话框
	public static final String PREFERENCES_OPEN_DOCK_BROWSER_COUNT = "open_dock_browser_count"; //dock栏浏览器启动次数
	public static final String PREFERENCES_PAY_STATICST_FIRST_UPLOAD = "preferences_pay_staticst_first_upload"; //是否第一次上传google付费成功的标志
	public static final String PREFERENCES_GETJAR_PAY_STATICST_FIRST_UPLOAD = "preferences_getjar_pay_staticst_first_upload"; //是否第一次上传getjar google付费成功的标志

	public static final String IS_SIDEBAR_RUNNING = "is_sidebar_running"; // 侧边栏功能是否运行，异常退出标志，用来重启
	//ＵＩ4.0新壁纸提醒
	public static final String SCREENEDIT_FIRST_SHOW_WALLPAPERTAB = "screenedit_first_show_wallpapertab"; //添加界面壁纸Tab第一次打开
	public static final String SCREENEDIT_FIRST_SHOW_GOWALLPAPER = "screenedit_first_show_gowallpaper"; //添加界面GO壁纸第一次打开
	
	public static final String SCREENEDIT_FIRST_SHOW_GOWIDGET = "screenedit_first_show_gowidget"; //添加界面GO小部件第一次打开
	
	//滤镜新功能提示
	public static final String SCREENEDIT_FIRST_SHOW_FILTER = "screenedit_first_show_filter"; 
	
	public static final String PREFERENCES_WALLPAPER_FILTER_FIRST_SHOW = "setting_wallpaper_filter_first_show"; //第一次进入侧边栏的设置
	
	//消息中心
	public final static String SHAREDPREFERENCES_MSG_UPDATE = "msg_update";
	public final static String SHAREDPREFERENCES_MSG_STATISTICSDATA = "msgcenter_statisticsdata";
	public final static String SHAREDPREFERENCES_MSG_STATISTICSDATA_UPLOAD_INFO = "msgcenter_statisticsdata_upload_info";
	public final static String SHAREDPREFERENCES_MSG_SHOW_TIMES = "show_times";
	public final static String SHAREDPREFERENCES_MSG_CLICK_TIMES = "click_times";
	public final static String SHAREDPREFERENCES_MSG_PUSH_TIMES = "push_times";
	public final static String SHAREDPREFERENCES_MSG_NEW_PRODUCT_STAMP = "new_product_stamp";
	public final static String SHAREDPREFERENCES_MSG_BUTTON_NAME = "button_name";
	public final static String SHAREDPREFERENCES_MSG_BUTTON_CLICK_TIMES = "button_click_times";
	public final static String SHAREDPREFERENCES_MSG_COVER_FRAME_CLOSE_BUTTON_CLICK_TIMES = "cover_frame_button_click_times";
	public final static String SHAREDPREFERENCES_MSG_RECOMMANDAPPS = "msg_recommandapps"; //保存第三方应用推荐信息
	

	public final static String SHAREDPREFERENCES_MSG_THEME_NOTIFY_STATICS_DATA = "theme_notify_statics";

	public final static String SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_STATICS_DATA = "locker_theme_notify_statics";

	public final static String SHAREDPREFERENCES_MSG_THEME_NOTIFY_SHOW_STATICS_DATA = "theme_notify_show_statics";
	public final static String SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_SHOW_STATICS_DATA = "locker_notify_show_statics";

	public final static String SHAREDPREFERENCES_MSG_OTHER_INFO = "msg_other_info";
	public final static String SHAREDPREFERENCES_MSG_WATI_TO_NOTIFY = "wait_to_notify";
	public final static String SHAREDPREFERENCES_MSG_FIRST_REQUEST = "first_request";
	public final static String SHAREDPREFERENCES_MSG_WATI_TO_SHOW_DIALOG = "wait_to_show_dialog";

	//facebook
	public final static String FACEBOOK_RECORD = "facebook_record";
	public final static String FACEBOOK_REMIND_SHARE_TIEM = "remind_share_time"; // 用于实现定时弹框
	public final static String FACEBOOK_USE_DIALOG_LOGIN = "usedialoglogin"; // 强制使用FBDialog登陆
	public final static String FACEBOOK_LAST_BACKUP_TIME = "last_backup_time"; // 最近备份时间
	public final static String FACEBOOK_LOGIN_AS_USER = "login_user"; // 当前登陆用户
	public final static String FACEBOOK_FIRST_SWITCH_THEME = "first_switch_theme"; // 用户安装后第一次切换主题
	public final static String FACEBOOK_OPEN_GRAPH_SWITCH = "open_graph_switch"; // OPenGraph开关
	public final static String FACEBOOK_MESSAGE_CENTER_SWITCH = "message_center_switch"; //消息中心控制开关
	public final static String FACEBOOK_RESTORE_NAME_LIST = "restore_name_list"; // 此机上用过facebook恢复备份的帐号
	public final static String FACEBOOK_RESTART_AFTER_RESTORE = "restart_after_restore"; // facebook恢复备份后,是否显示分享框

	public final static String THEME_CUSTOMER_LEVEL_1 = "theme_customer_level_1";
	public final static String THEME_CUSTOMER_LEVEL_2 = "theme_customer_level_2";
	public final static String CUSTOMER_VIP_NEW = "is_not_inclue_locker";
	public final static String CUSTOMER_VIP = "customer_vip";
	public final static String HAD_SHOW_VIP_TIP = "had_show_vip_tip";
	public final static String IS_GOLOCKER_VIP = "is_golocker_vip";

	//touchhelper
	public final static String DEFAULT_TOUCHHELPER_PKG = "default_touchhelper_pkg";
	
	//systemsettingcenter
	public final static String SYSTEM_SETTING_CENTER_SHOW_DIALOG = "system_setting_center_show_dialog"; // 是否显示弹框，boolean
	public final static String SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE = "system_setting_center_show_mode"; // 设置模式：0：快捷设置　１：系统设置

	//桌面菜单-》系统设置-》模式选择弹框与否及选择哪种模式的保存值所在SHAREDPREFERENCES文件
	public final static String BACKUP_SHAREDPREFERENCES_SYSTEM_SETTING_FILE = "system_setting_mode_file";
	

	/**
	 * 是否再次提醒评分
	 */
	public static final String REMIND_RATE = "remind_rate";
	
	/**
	 * 是否在升级引导页点过评分引导
	 */
	public static final String RATE_GUIDEFRAME = "rate_guideframe";

	/**
	 * 记录桌面首次启动，启动“评分引导自动弹出“定时器的时间
	 * 注释：字符串内容未做修改，避免与旧版本缓存数据冲突，仅仅修改常量名称，便于理解
	 */
	public static final String RATE_FIRST_CHECK_TIME = "remind_rate_time";
	
	/**
	 * 上一次显示评分引导框的时间点
	 * 注释：4.12需求，仅从4.12版本开始记录时间
	 */
	public static final String RATE_LAST_SHOW_TIME = "rate_last_show_time";
	
	/**
	 * 记录提示用户卸载不常用应用时间
	 */
	public static final String REMIND_NOTIFY_UNINSTALL_TIME = "remind_notify_uninstall_time";
	
	/**
	 * 是否打开对少使用应用进行卸载的提示功能
	 */
	public static final String ALLOW_NOTIFY_UNINSTALL_LESSUSE_APP = "allow_notify_uninstall_lessuse_app";

	/**
	 * 是否需要弹出评分
	 */
	public static final String IS_NEED_SHOW_RATE_DIALOG = "is_need_show_rate_dialog";
	/**
	 * 触发弹框的事件
	 */
	public static final String IS_NEED_SHOW_RATE_DIALOG_EXTRA_EVENT = "is_need_show_rate_dialog_extra_event";
	/**
	 * 辅助参数，用作记录内存清理值{@link RateGuideTask#EVENT_RUNNING_CLEAN}
	 */
	public static final String IS_NEED_SHOW_RATE_DIALOG_EXTRA_MEMCLEANED = "is_need_show_rate_dialog_extra_parameter";
	
	/**
	 * 是否已经弹出评分
	 */
	public static final String HAS_SHOW_RATE_DIALOG = "has_show_rate_dialog";
	/**
	 * 上一版本是否已经弹出过评分引导
	 */
	public static final String HAS_SHOW_RATE_DIALOG_LASTVERSION = "has_show_rate_dialog_lastversion";
	/**
	 * 记录是否第一次运行。并进行评分提醒
	 */
	public static final String FIRST_RUN_REMIND_RATE = "first_run_remind_rate";

	/**
	 * 功能表搜索的perference；
	 */
	public static final String APPFUNC_SEARCH_STATISTIC_DATA = "appfunc_search_statistic_data";

	public static final String FOLDER_DATA_CORRUPTION = "folder_data_corruption";

	public static final String LOCKER_SETTING_PRF = "locker_setting_prf";
	public static final String ENTER_LOCKER_TAB = "enter_locker_tab";
	public static final String CLICK_LOCKER_SETTING = "click_locker_setting";
	public static final String HAS_SHOW_LOCKER_SETTING_NOTIFY = "has_show_locker_setting_notify";

	public static final String THEME_SETTING_CONFIG = "theme_setting_config";
	public static final String HAS_SHOW_VIPUPGRADE = "has_show_vipupgrade";
	
	/**
	 * 下载管理，记录下载任务状态
	 */
	public static final String DOWNLOAD_MANAGER_TASK_STATE = "download_manager_task_state";


	/**
	 * 插件管理
	 */
	public static final String NEED_UPDATE_GOPLUGINS = "need_update_goplugins"; // 可以更新的go插件及小部件

	/**
	 * 3DEngine选择器
	 */
	public static final String PREFERENCE_ENGINE = "engine";
	/**
	 * 3DEngine选择保存值
	 */
	public static final String PREFERENCE_ENGINE_SELECTED = "engine_selected";
	
	/**
	 * 覆盖前3DEngine选择值
	 */
	public static final String PREFERENCE_LAST_ENGINE_SELECTED = "preference_last_engine_selected";
	
	/**
	 * 对2D用户推荐3D
	 */
	public static final String PREFERENCE_RECOMMEND_SHELL_ENGINE = "recommend_shell_engine";
	
	/**
	 * 2D自动引导3D的通知栏是否存在
	 */
	public static final String PREFERENCE_RECOMMEND_SHELL_ENGINE_NOTIFIY_EXIST = "preference_has_show_recommend_shell_engine_notifiy";
	
	/**
	 * 3DEngine初始化失败
	 */
	public static final String PREFERENCE_ENGINE_INITED_FAILED = "engine_inited_failed";

	public static final String PREFERENCE_RATE_CONFIG = "rate_config";
	
	/**
	 * 是否需要上传3DEngine状态改变值
	 */
	public static final String PREFERENCE_ENGINE_CHANGED_NEED_UPLOAD_STATISTICS = "engine_changed_need_upload_statistics";
	/**
	 * 显示或隐藏3D插件设置项
	 */
	public static final String PREFERENCE_ENGINE_SHOW_SETTING = "engine_show_setting";
	
	public static final String PREFERENCE_APPDRAW_ARRANGE_CONFG = "appdraw_arrange_confg";
	public static final String PREFERENCE_APPDRAW_ARRANGE_FOLDERS_ID = "folders_id";	
	/**
	 * 功能表menu的clean master是否点击过
	 */
	public static final String PREFERENCE_APPFUNC_CLEANMASTER_NEW = "cleanmaster_new";
	public static final String PREFERENCE_APPFUNC_CLEANMASTER_HAS_CLICKED = "cleanmaster_has_clicked";
	public static final String PREFERENCE_APPFUNC_MENU_NEED_SHOW_LIGHT = "appfunc_menu_need_show_light";
	public static final String PREFERENCE_APPFUNC_HIDE_APP_HAS_CLICKED = "hide_app_has_clicked";
	/**记录是否已经显示cleanmaster推荐对话框*/
	public static final String PREFERENCE_HAS_SHOW_CLEAN_MASTER_DIALOG = "has_show_clean_master_dialog";
	public static final String PREFERENCE_UNINSTALL_COUNT_FOR_CM = "uninstall_count_for_cm";
	
	public static final String PREFERENCE_2D_EFFECT = "2d_effect";
	public static final String PREFERENCE_3D_EFFECT = "3d_effect";
	
	public static final String PREFERENCE_SETTING_CFG = "desk_cfg";
	public static final String PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_TIMESTAMP = "autobackup_timestamp";
	public static final String PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_CHECKDB = "autobackup_checkdb";
	public static final String PREFERENCE_SETTING_CFG_ITEM_AUTOBACKUP_RESTOREDEFAULT = "autobackup_restoredefault";
	public static final String PREFERENCE_SETTING_CFG_ITEM_FONTFILETYPE = "fontfiletype";
	public static final String PREFERENCE_SETTING_CFG_ITEM_FONTPACKAGE = "fontpackage";
	public static final String PREFERENCE_SETTING_CFG_ITEM_FONTTITLE = "fonttitle";
	public static final String PREFERENCE_SETTING_CFG_ITEM_FONTFILE = "fontfile";
	public static final String PREFERENCE_SETTING_CFG_ITEM_FONTSTYLE = "fontstyle";
	public static final String PREFERENCE_SETTING_CFG_ITEM_CLICK_KITTYPLAY_COUNT = "click_kittyPlay_count";
	public static final String PREFERENCE_SETTING_CFG_ITEM_OPEN_GOSTORE = "open_gostore";
	public static final String PREFERENCE_SETTING_CFG_ITEM_LIMITFREE_VALID_INSTALL = "limit_free_install_valid";
	public static final String PREFERENCE_SETTING_CFG_ITEM_LIMITFREE_VALID = "limit_free_valid";
	public static final String PREFERENCE_SETTING_CFG_ITEM_GOTO_SLIDEDOCK = "goto_slidedock";
	/**
	 * 新版本是否将字体正在使用的字体设置同步到preference中
	 */
	public static final String PREFERENCE_SETTING_CFG_ITEM_SYNC_FONT_CFG = "has_sync_font_cfg";

	//侧边栏
	public static final String PREFERENCE_SIDE_DOCK = "side_dock";

	public static final String PREFERENCE_LEFT_AREAINFO_LEFTAREAX = "leftareainfoleftareax";
	public static final String PREFERENCE_LEFT_AREAINFO_LEFTAREAY = "leftareainfoleftareay";
	public static final String PREFERENCE_LEFT_AREAINFO_LEFTAREAWIDTH = "leftareainfoleftareaw";
	public static final String PREFERENCE_LEFT_AREAINFO_LEFTAREAHEIGHT = "leftareainfoleftareah";

	public static final String PREFERENCE_RIGHT_AREAINFO_RIGHTAREAX = "rightareainforightareax";
	public static final String PREFERENCE_RIGHT_AREAINFO_RIGHTAREAY = "rightareainforightareay";
	public static final String PREFERENCE_RIGHT_AREAINFO_RIGHTAREAWIDTH = "rightareainforightareaw";
	public static final String PREFERENCE_RIGHT_AREAINFO_RIGHTAREAHEIGHT = "rightareainforightareah";
		
	//如果恢复默认时需要delete某个sharepreference请将它加入这里
	public static final String[] NEED_DELETE_PREFERENCES = { PREFERENCE_APPDRAW_ARRANGE_CONFG };
	//天气通
	public final static String TIANQITONG_WIDGET_ADD = "tianqitong_widget_add";
	//天天动听
		public final static String TTDONGTING_WIDGET_ADD = "ttdongting_widget_add";
		public final static String TTDONGTING_WIDGET_IS_ADD = "ttdongting_widget_is_add"; 
		public final static String TTDONGTING_WIDGET_HAS_ADDED = "ttdongting_widget_has_added";
	
	/**
	 * FOLDER_AD
	 */
	public static final String FOLDER_AD_PREFERENCES = "folder_ad_preferences";
	
	public static final String FOLDER_AD_LAST_REQUEST_TIME = "folder_ad_last_request_time";
	
	public static final String FOLDER_AD_VIEW_LAST_OPEN_TIME = "folder_ad_view_last_open_time";
	
	public static final String FOLDER_AD_VIEW_LAST_SHOW_POS = "folder_ad_view_last_show_pos";
	
	/**
	 *
	 */
	public static final String SHOW_GESTURE_PRIME_DIALOG = "show_gestur_prime_dialog";

	/**
	 * 不再无广告提示对话框
	 */
	public static final String SHOULD_SHOW_REMOVE_AD_DIALOG = "should_show_remove_ad_dialog";
	
	/**
	 * 显示侧边栏前dock条滑动导第二屏的次数
	 * 侧边 Dock 引导,场景如下: 未付费用户第二次滑动到 dock 第二行时出现，Prime推荐去掉优化。(2014-01-14)
	 */
//	public static final String DOCK_SIDE_DOCK_GUIDE_MASK_SLIDING_COUNT = "dock_side_dock_guide_mask_sliding_count";
	
	/**
	 * 
	 */
	public static final String GO_MARKET_PAY_ENTRANCE = "go_market_pay_entrance";
	
	public static final String PREFERENCE_PURCHASE_CFG = "purchase_cfg";
	
	public static final String PREFERENCE_PURCHASE_CFG_ITEM_TRIAL_DATE = "trial_date";
	public static final String PREFERENCE_PURCHASE_CFG_ITEM_SHOW_UPDATE_DIALOG = "show_update_dialog";
	public static final String PREFERENCE_PURCHASE_CFG_ITEM_SHOW_TRIAL_PAGE = "show_trial_page";
	public static final String PREFERENCE_PURCHASE_CFG_ITEM_SHOW_INSTALL_PAGE = "show_install_page";
	
	/**
	 * 区分是否添加双11图标
	 */
	public static final String DOUBLE_ELEVEN_IS_ADD_SC = "is_ad_sc";
	/**
	 * 双11图标广播到其他桌面
	 */
	public static final String DOUBLE_ELEVEN_IS_ADD_SHORTCUT = "is_add_sc";
	
	public static final String APP_CLASSIFY_DB_UPDATE_TIME = "app_classify_db_update_time";
	//如果恢复默认时需要清除某个sharepreference请将它加入这里
	public static final String[] NEED_CLEAR_PREFERENCES = { DESK_SHAREPREFERENCES_FILE,
			USERTUTORIALCONFIG, DB_PROVIDER_SUPPORT, SHAREDPREFERENCES_MSG_UPDATE,
			SHAREDPREFERENCES_MSG_RECOMMANDAPPS, FEATUREDTHEME_CONFIG,
			SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_SHOW_STATICS_DATA,
			SHAREDPREFERENCES_MSG_THEME_NOTIFY_SHOW_STATICS_DATA,
			SHAREDPREFERENCES_MSG_THEME_NOTIFY_STATICS_DATA, SHAREDPREFERENCES_MSG_OTHER_INFO,
			SHAREDPREFERENCES_MSG_LOCKER_THEME_NOTIFY_STATICS_DATA, ADVERT_NEET_OPEN_DATA,
			FOLDER_DATA_CORRUPTION, PREFERENCE_ENGINE, CLEAN_SCREEN_AUTO_ADD_SHORTCUT_FILE,
			PREFERENCE_APPFUNC_CLEANMASTER_NEW, PREFERENCE_APPDRAW_ARRANGE_CONFG,
			PREFERENCE_SETTING_CFG, PREFERENCE_SIDE_DOCK, PREFERENCES_OPEN_BROWSER_COUNT,
			FOLDER_AD_PREFERENCES, PREFERENCE_SETTING_CFG_ITEM_CLICK_KITTYPLAY_COUNT,
			BACKUP_SHAREDPREFERENCES_SYSTEM_SETTING_FILE};
	
	
	/**
	 * SIDEADVERT_DOWNLOAD
	 * add by zhangxi @2013-10-9
	 */
	public static final String SIDEADVERT_DOWNLOAD_PREFERENCES = "sideadvert_download_preferences";
	
	// 进入功能表次数
	public static final String ALL_APP_MENU_SHOW_TIME_3D = "all_app_menu_show_time_3d";
	
	// 广告控制，只使用一个版本
	public static String AD_RANDOM_SWITCH = "ad_random_switch";
	public static String AD_RANDOM_ITEM = "ad_random_item";
	
	
	//功能表侧边栏引导尺寸
    public static final String APPDRAW_GUIDE_TOP = "appdraw_guide_top";
    public static final String APPDRAW_GUIDE_BUTTOM = "appdraw_guide_buttom";
    public static final String APPDRAW_GUIDE_LEFT = "appdraw_guide_left";
    public static final String APPDRAW_GUIDE_RIGHT_PORT = "appdraw_guide_right_port";
    public static final String APPDRAW_GUIDE_RIGHT_LAND = "appdraw_guide_right_land";
	
    /**
	 * 显示或隐藏0屏幕
	 */
	public static final String PREFERENCE_ZERO_SCREEN_SHOW_SETTING = "zero_screen_show_setting";

	/**
	 * 0屏推荐网址的 8小时更新一次
	 */
	public static final String PREFERENCE_ZERO_SCREEN_IS_EIGHT_HOURS = "zero_screen_is_eight_hours";

	/**
	 * 0屏搜索
	 */
	public static final String PREFERENCE_ZERO_SCREEN_SEARCH = "zero_screen_search";
	/**
	 * 0屏搜索历史记录
	 */
	public static final String PREFERENCE_ZERO_SCREEN_SEARCH_HISTORY_WORDS = "history_search_word";

	
	public static final String PREFERENCE_ZERO_SCREEN_IS_BE_OPERTATOR = "zero_screen_is_be_opertator";

	public static final String PREFERENCE_ZERO_SCREEN_WEB_VIEW_URL = "zero_screen_iwebview_url";
	
	public static final String PREFERENCE_LAST_VERSION_CODE = "last_version_code";
	
	public static final String PREFERENCE_BETA_INFO = "beta_info";
	
	public static final String PREFERENCE_ACCEPT_BETA = "accept_beta";
	
	public static final String PREFERENCE_HAS_SHOW_BETA_DIALOG = "has_show_beta_dialog";
	
	/**
	 * 是否启用通讯统计
	 */
	public static final String PREFERENCE_IS_NOTIFICATION_PLUGIN_ENABLE = "is_notification_plugin_enable";
	
	/**
	 * 是否不使用新布局
	 */
	public static final String PREFERENCE_IS_USING_OLD_CELLLAYOUT = "is_using_old_celllayout";
	
	public static final String GO_LAUNCHER_USER_BEHAVIOR_STATIC_DATA_CACHE = "go_launcher_user_behavior_static_data_cache";
	
	/**
	 * 记录广告ID
	 */
	public static final String PREFERENCE_RECORD_ADVERT_ID = "advert_id";
	/**
	 * 桌面第一次运行，记录一下是不是clean master比桌面早存在于用户设备 
	 * 安装GO桌面时，CM已安装，则推荐安卓优化大师
	 * 安装GO桌面时，CM未安装，则推荐CM
	 */
	public static final String PREFERENCE_IS_RECOMMEND_DUSPEEDBOOSTER = "is_recommend_DuSpeedBooster";
	
	/**
	 * smartCard
	 */
	public static final String SMART_CARD_LIGHT_GAME_SHOW_END_TIME = "smart_card_light_game_show_end_time";
	
	public static final String SMART_CARD_REC_APP_INDEX = "smart_card_rec_app_index";
}
