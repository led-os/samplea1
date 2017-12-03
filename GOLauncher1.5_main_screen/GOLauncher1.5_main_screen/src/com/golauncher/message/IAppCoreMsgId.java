package com.golauncher.message;

import android.content.Intent;

/**
 * 
 * @author liuheng
 *
 */
public interface IAppCoreMsgId {
	public static final int BASE_ID = 31000;
	/**
	 * 后台消息定义，范围为1100～1199 包含AppDataEngine、ThemeManager中的消息定义
	 */

	/**
	 * 后台数据改变
	 */
	public static final int APPCORE_DATACHANGE = 31001;

	/**
	 * 安装程序
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            String pkgName
	 * @param objects
	 *            ArrayList<AppItemInfo>
	 */
	public static final int EVENT_INSTALL_APP = 31002;

	/**
	 * 卸载程序
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            String pkgName
	 * @param objects
	 *            ArrayList<AppItemInfo>
	 */
	public static final int EVENT_UNINSTALL_APP = 31003;

	/**
	 * 安装包（不在AppDrawer显示的应用程序，如主题、GoWidget，Notification）
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            String pkgName
	 * @param objects
	 *            null
	 */
	public static final int EVENT_INSTALL_PACKAGE = 31004;

	/**
	 * 卸载包（没有launch入口的应用）
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            String pkgName
	 * @param objects
	 *            null
	 */
	public static final int EVENT_UNINSTALL_PACKAGE = 31005;

	/**
	 * 升级包(没有launch入口的应用)
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            String pkgName
	 * @param objects
	 *            null
	 */
	public static final int EVENT_UPDATE_PACKAGE = 31006;

	/**
	 * 安装在SD卡上且不出现在appdrawer的包，例如桌面或widget主题包
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            ArrayList<String> package list
	 */
	public static final int EVENT_UPDATE_EXTERNAL_PACKAGES = 31007;

	/**
	 * 初始化加载完成
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int EVENT_LOAD_FINISH = 31008;

	/**
	 * 所有程序标题加载完毕
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int EVENT_LOAD_TITLES_FINISH = 31009;

	/**
	 * 所有程序图标加载完毕
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int EVENT_LOAD_ICONS_FINISH = 31010;

	/**
	 * SD卡mount（手机连接为充电模式），刷新一下自定义图标
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int EVENT_SD_MOUNT = 31011;

	/**
	 * SD卡选择了磁盘共享模式
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int EVENT_SD_SHARED = 31012;

	/**
	 * sd卡准备好，需要刷新 对应于系统消息{@link Intent#ACTION_EXTERNAL_APPLICATIONS_AVAILABLE}
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            ArrayList<AppItemInfo> 变更的程序列表
	 */
	public static final int EVENT_REFLUSH_SDCARD_IS_OK = 31013;

	/**
	 * 定时扫描时间到，需要刷新
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            ArrayList<AppItemInfo> 变更的程序列表
	 */
	public static final int EVENT_REFLUSH_TIME_IS_UP = 31014;

	/***
	 * 隐藏全屏progressBar
	 */
	public final static int DISMISS_PROGRESSBAR = 31015;

	/**
	 * 主题改变
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int EVENT_THEME_CHANGED = 31016;

	/**
	 * 主题应用失败
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int EVENT_THEME_APPLY_FAILED = 31017;

	/**
	 * 检验主题图标
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int EVENT_CHECK_THEME_ICON = 31018;

	/**
	 * 刷新主题预览
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int REFRESH_THEME_SCAN_VIEW = 31019;
	/**
	 * 刷新GGMENU
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int REFRESH_GGMENU_THEME = 31020;
	/**
	 * 刷新桌面图标
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int REFRESH_SCREENICON_THEME = 31021;
	/**
	 * 刷新文件夹
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int REFRESH_FOLDER_THEME = 31022;
	/**
	 * 刷新桌面指示器
	 * 
	 * @param param
	 *            0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int REFRESH_SCREENINDICATOR_THEME = 31023;
	/**
	 * 显示或隐藏图标底座
	 * 
	 * @param param
	 *            1 or 0
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int EVENT_SHOW_OR_HIDE_ICON_BASE = 31024;

	public final static int EVENT_CHANGE_WIDGET_THEME = 31025;

	public final static int RESPONSE_GLIDE_UP_DOWN_ACTION = 31026;

	/**
	 * 内购付费成功
	 */
	public final static int THEME_INAPP_PAID_FINISHED = 31027;

	/**
	 * 通知开始加载多媒体数据（用于功能表搜索多媒体）
	 */
	public final static int START_LOAD_MEDIA_DATA = 31028;

	/**
	 * 多媒体数据加载完成（图片、视频、音乐）
	 */
	public final static int MEDIA_DATA_LOAD_FINISH = 31029;

	/**
	 * 通知清空内存中的多媒体数据，销毁FileEngine
	 */
	public final static int DESTROY_FILE_ENGINE = 31030;

	/**
	 * 指示器位置改变
	 */
	public final static int INDICATOR_CHANGE_POSITION = 31032;
	/**
	 * 屏幕方向改变
	 */
	public final static int SCREEN_ORIENTATION_CHANGE = 31033;
	/**
	 * 是否显示状态栏改变
	 */
	public final static int SHOW_STATUS_BAR_SHOW_CHANGE = 31034;
	/**
	 * 多媒体插件安装或卸载
	 */
	public final static int MEDIA_PLUGIN_CHANGE = 31035;

	public final static int FOLDER_THEME_CHANGE = 31037;

	/**
	 * GGMenu是否显示中（显示return true）
	 */
	public final static int MENU_IS_SHOWING = 31038;

	/**
	 * 处理GLGGMenu的点击事件
	 */
	public final static int HANDLE_GL_GGMENU_ITEM_CLICK = 31039;

	/**
	 * 获取GGMenu数据
	 */
	public final static int GET_GG_MENU_DATA = 31040;

	/**
	 * 开启桌面新算法
	 */
	public final static int SHOW_NEW_MARGINS = 31041;

	public final static int BIND_SYSTEM_WIDGET = 31042;

	public final static int ADD_SYSTEM_ITEM_TO_SCREEN = 31043;

	/**
	 * 从服务器拉取屏幕添加层中的推荐数据
	 */
	public final static int REQUEST_SCREEN_EDIT_PUSH_LIST = 31044;
	
	
	public final static int EVENT_UPDATE_APP = 31045;
	
	public final static int EVENT_CHANGE_APP = 31046;
}
