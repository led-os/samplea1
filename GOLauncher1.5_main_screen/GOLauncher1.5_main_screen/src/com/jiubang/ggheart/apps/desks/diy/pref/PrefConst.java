package com.jiubang.ggheart.apps.desks.diy.pref;

import android.app.admin.DevicePolicyManager;
import android.provider.Settings;

/**
 * 
 * @author guoyiqing
 *
 */
public class PrefConst {

	/**
	 * 系统设置包名
	 */
	public static final String SP_UNBACKUP_SYSTEM_SETTING_PKG = "system_setting_pkg";
	public static final String KEY_SYSTEM_SETTING_WIFI = Settings.ACTION_WIFI_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_MOBIDATA = Settings.ACTION_DATA_ROAMING_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_TETHER_ICS = "com.android.settings.TetherSettings";
	public static final String KEY_SYSTEM_SETTING_TETHER = Settings.ACTION_WIRELESS_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_BLUETOOTH = Settings.ACTION_BLUETOOTH_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_SCREENLOCK = DevicePolicyManager.ACTION_SET_NEW_PASSWORD;
	public static final String KEY_SYSTEM_SETTING_DISPLAY = Settings.ACTION_DISPLAY_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_RINGTONE = Settings.ACTION_SOUND_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_LANGUAGE = "com.android.settings.LanguageSettings";
	public static final String KEY_SYSTEM_SETTING_APPINFO = Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_STORAGE = Settings.ACTION_INTERNAL_STORAGE_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_BATTERY = Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS;
	public static final String KEY_SYSTEM_SETTING_DATAUSAGE_ICS = "com.android.settings.Settings$DataUsageSummaryActivity";
	public static final String KEY_SYSTEM_SETTING_DATAUSAGE = Settings.ACTION_DEVICE_INFO_SETTINGS;
	
	/**
	 * Go个性化推荐Widget相关Key(GoRecomm Widget)
	 */
	public static final String SP_UNBAKUP_PREFERENCE_GORECOMM_WIDGE = "preference_gorecomm_widget";
	//最后一次更新Go个性化推荐Widget数据时间
	public static final String KEY_LAST_UPDATE_GORECOMM_WIDGET_DATA_TIME = "last_update_gorecomm_widget_data_time_key";
	//请求Go个性化推荐Widget数据成功时间
	public static final String KEY_REQUEST_GORECOMM_WIDGET_DATA_SUCCESS_TIME = "request_gorecomm_widget_data_success_time_key";
	//请求Go个性化推荐Widget数据失败时间
	public static final String KEY_REQUEST_GORECOMM_WIDGET_DATA_FAILURE_TIME = "request_gorecomm_widget_data_failure_time_key";
	//开始请求Go个性化推荐Widget数据时间
	public static final String KEY_START_REQUEST_GORECOMM_WIDGET_DATA_TIME = "start_request_gorecomm_widget_data_time_key";
	
	/**
	 * AdmobSwitchUtils
	 */
	public static final String SP_UNBAKUP_ADMOB_UTIL_SP = "admob_util_sp";
	public static final String KEY_TIMESTAMP = "timestamp";
	
	/**
	 * 保存IMEI的sharedPreference文件名
	 */
	public static final String SP_UNBAKUP_STATISTICSUSERCOMMON = "statisticsusercommon";
	public static final String KEY_USER_IS_COVER = "useriscover"; // 保存覆盖安装的key文件名
	
	/**
	 * pad dialog
	 */
	public static final String SP_UNBACKUP_NEEDTODIALOG = "needToDialog";
	public static final String KEY_NEWSHOWDIALOG = "NewShowDialog";
	
	/**
	 * 获取root信息
	 */
	public static final String SP_UNBACKUP_ROOTINFO = "rootinfo";
	public final static String KEY_ROOT_INFO_DATA = "rootinfodata";
	
	/**
	 * 应用游戏中心widget，安装后第一次滑动在widget所在的屏幕时，Toast提示用户可以上下滑动切换内容
	 */
	public static final String SP_UNBACKUP_APPGAME_WIDGET_SHOW_MESSAGE = "appgame_widget_show_message";
	public static final String KEY_HAS_SHOW_MESSAGE = "has_show_message";
	
	/**
	 * 搜索键
	 */
	public static final String SP_UNBACKUP_SEARCH_STATISTIC = "search_statistic";
	public static final String KEY_LONG_MENU = "long_menu_key";
	public static final String KEY_SEARCH_KEY = "search_key";
	public static final String KEY_WIDGET_SEARCH_KEY = "widget_search_key";
	
	/**
	 * GA统计
	 */
	public static final String SP_UNBACKUP_INSTALL_PREFS = "install_prefs";
	public static final String KEY_REFERRER_INFO_STORE_FLAG = "referrer_info_store_flag";
	
	/**
	 * 在覆盖安装检测壁纸是否放大
	 */
	public static final String SP_UNBACKUP_NEEDTOADJUSTWALLPAPERDIMENSION = "needToAdjustWallpaperDimension";
	public static final String KEY_NEEDTOADJUSTWALLPAPERDIMENSIONVALUE = "needToAdjustWallpaperDimensionValue";
	
	
	/**
	 * 保存facebook开关标志
	 */
	public static final String SP_UNBACKUP_FACEBOOK_RECORD = "facebook_record";
	public static final String KEY_MESSAGE_CENTER_SWITCH = "message_center_switch";
	
	/**
	 * 通讯统计gmail
	 */
	public static final String SP_UNBACKUP_GMAIL_ACCOUNT_SETTING = "gmail_account_setting";
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_PASSWORD = "password";
	
	
	/**
	 * 检测当前语言，以便判断是否修改应用程序title TODO:与DiyScheduler的checkLanguage()统一
	 */
	public static final String SP_UNBACKUP_DIY = "DIY";
	public static final String KEY_LANGUAGE = "language";
	
	/**
	 * Statistics 
	 */
	public static final String SP_UNBACKUP_RANDOMDEVICEID = "randomdeviceid";
	public static final String KEY_RANDOM_DEVICE_ID = "random_device_id";
	
	/**
	 * 是否已经处理过一次应用程序更新消息
	 */
	public static final String SP_UNBACKUP_AUTOCHECK = "autocheck";
	public static final String KEY_FIRST_HANDLE_APPUPDATE_MSG = "first_handle_appupdate_msg_key";
	public static final String KEY_CHECK = "check"; //检查的sharedpreferences中的key
	public static final String KEY_CHECK_TIME = "check_time"; //检查的sharedpreferences中的上次时间值
	public static final String KEY_UPLOAD = "upload"; //检查的sharedpreferences中的上传
	
	/**
	 * noticationTip
	 */
	public static final String SP_UNBACKUP_NOTICATIONTIP = "noticationTip";
	public static final String KEY_NEEDSHOWTIP = "needShowTip";
	
	/**
	 * ErrorReport
	 */
	public static final String SP_UNBACKUP_ERRORREPORT = "ErrorReport";
	public static final String KEY_STARTTIME = "STARTTIME";
	public static final String KEY_SCREEN_COUNT = "SCREEN_COUNT";
	public static final String KEY_NEED_TO_SEND_DB = "need_to_send_db";
	
	
	/**
	 * dbexception
	 */
	public static final String SP_UNBACKUP_DBEXCEPTION = "dbexception";
	public static final String KEY_REPORT = "report";
	
	
	/**
	 * ScreenFrame删除出错统计信息
	 */
	public static final String SP_UNBACKUP_COUNT_FIVE = "count_five";
	public static final String KEY_COUNT_FIVE = "count";
	
	/**
	 * 保存颜色选择器的坐标--图标
	 */
	public static final String SP_UNBACKUP_ORIENTATION_XY_ICON = "orientation_xy_icon";
	public static final String KEY_ICON_INITIA_X = "initia_x_icon";
	public static final String KEY_ICON_INITIA_Y = "initia_y_icon";
	public static final String KEY_ICON_COLOR = "icon_color";
	
	
	/**
	 * 0屏搜索
	 */
	public static final String SP_UNBACKUP_ZERO_SCREEN_SEARCH = "zero_screen_search";
	public static final String KEY_ZERO_SCREEN_SEARCH_HISTORY_WORDS = "history_search_word";
	
	/**
	 * 保存颜色选择器的坐标--字体
	 */
	public static final String SP_UNBACKUP_ORIENTATION_XY_FONT = "orientation_xy_font";
	public static final String KEY_FONT_INITIA_X = "initia_x_font";
	public static final String KEY_FONT_INITIA_Y = "initia_y_font";
	public static final String KEY_FONT_COLOR = "font_color";
	
	/**
	 * 功能表搜索定位提示
	 */
	public static final String KEY_SHOW_APP_LOCATE_TIPS = "show_app_locate_tips";
	
	/**
	 * slide menu promotion
	 */
	public static final String KEY_SLIDEMENU_SHOW_PROMOTION_AD = "show_slidemenu_promotion_ad";
	public static final String KEY_SLIDEMENU_SHOW_CLEAN_MASTER_AD_TIMESTAMP = "show_slidemenu_show_clean_master_timestamp";
	public static final String KEY_SLIDEMENU_SHOW_DU_SPEED_TIMESTAMP = "show_slidemenu_show_du_speed_timestamp";
	
	/**
	 * 是否有恢复备份的操作
	 */
	public static final String KEY_HAS_RESTORE_FLAG = "has_restore_flag";
	
	//===========应用图标控制机制==========
	public static final String SP_UNBAKUP_PREFERENCE_APP_ICON_CONFIG = "preference_app_icon_config";
	//最后一次获取数据时服务端的时间戳
	public static final String KEY_APP_ICON_CONFIG_SERVER_TIME_STAMP = "last_update_app_icon_config_server_time_stamp_key";
	//最后一次成功获取服务端数据的时间
	public static final String KEY_REQUEST_APP_ICON_CONFIG_DATA_SUCCESS_TIME = "request_app_icon_config_data_success_time_key";
	//最后一次获取服务端数据失败的时间
	public static final String KEY_REQUEST_APP_ICON_CONFIG_DATA_FAILURE_TIME = "request_app_icon_config_data_failure_time_key";
	//最后一次刷新应用图标View数据的时间
	public static final String KEY_APP_ICON_CONFIG_REFRESH_VIEW_DATA_TIME = "app_icon_config_refresh_view_data_time_key";

	/**
	 * 添加界面图标排序方式(按名字或时间)
	 */
	public static final String KEY_SCREEN_EDIT_APPS_ORDER_TYPE = "screen_edit_apps_order_type";
	
	public static final String KEY_INSTALL_SHOW_NOTIFICATION = "install_show_notification";
	
	/**
	 * 游戏文件夹加速开关值
	 */
	public static final String KEY_GAME_FOLDER_ACCELERATE_SWITCH = "game_folder_accelerate_switch";
	public static final String KEY_SMART_CARD_LESS_CREATE_TIME = "smart_card_less_create_time";
	public static final String KEY_LIGHTAPP_INFO_DOWNLODE_TIME = "lightapp_upload_time";
	public static final String KEY_RECOMMEND_FOLD_HASHCODE = "recommend_fold_hashcode";
	public static final String KEY_RECOMMEND_GAME_HASHCODE = "recommend_GAME_hashcode";
	
	public static final String KEY_GAME_FOLDER_MODE = "game_folder_mode";
	
	/**
	 * GO天气内置Widget相关Key
	 */
	//纬度
	public static final String KEY_LATITUDE = "key_go_weather_latitude";
	//经度
	public static final String KEY_LONGITUDE = "key_go_weather_longitude";
	//上一次请求天气数据成功的时间
	public static final String KEY_LAST_GET_WEATHER_DATA_SUCCESS_TIME = "key_go_weather_last_get_weather_data_success_time";
	//上一次请求天气数据失败的时间
	public static final String KEY_LAST_GET_WEATHER_DATA_FAILED_TIME = "key_go_weather_last_get_weather_data_failed_time";
	//温度单位
	public static final String KEY_TEMPUNIT_VALUE = "key_go_weather_tempunit_value";
	//日期格式
	public static final String KEY_DATE_FORMAT_VALUE = "key_go_weather_date_format_value";

	//auto-foder, 
	public static final String KEY_APP_CLASSIFY_FIRST_TIME_AUTO_ARRAGEMENT = "app_classify_first_time_auto_arragement";
	/**
	 * 5.0版本激活码功能相关Key
	 */
	//激活码(只有激活成功过才有值)
	public static final String KEY_LAUNCHER_ACTIVATION_CODE_VALUE = "launcher_activation_code_value";
	//邀请入口标识(true:打开邀请入口;false:关闭邀请入口)
	public static final String KEY_LAUNCHER_ACTIVATION_INVITE_ENTR_FALG = "launcher_activation_invite_entr_flag";
	//可邀请人数
	public static final String KEY_LAUNCHER_ACTIVATION_INVITE_PEOPLE_NUMBER  = "launcher_activation_invite_people_number";
	
	
	
	
	
	public static final String KEY_SMART_CARD_GUIDE_PAGE_WEATHER_THE_SHOW = "smart_card_guide_page_weather_the_show";
}
