package com.golauncher.message;

/**
 * 
 * @author liuheng
 *
 */
public interface IAppManagerMsgId {
	public static final int BASE_ID = 40000;
	
	/**
	 * “全部更新”行为按钮消息
	 */
	public static final int APPS_MANAGEMENT_OPERATION_BUTTON = 40001;

	/**
	 * 等待下载消息
	 */
	public static final int APPS_MANAGEMENT_WAIT_FOR_DOWNLOAD = 40002;
	/**
	 * 开始下载消息
	 */
	public static final int APPS_MANAGEMENT_START_DOWNLOAD = 40003;
	/**
	 * 正在下载消息
	 */
	public static final int APPS_MANAGEMENT_DOWNLOADING = 40004;

	/**
	 * 下载完毕消息
	 */
	public static final int APPS_MANAGEMENT_DOWNLOAD_COMPLETED = 40005;

	/**
	 * 取消下载消息
	 */
	public static final int APPS_MANAGEMENT_DOWNLOAD_CANCELED = 40006;

	/**
	 * 下载失败消息
	 */
	public static final int APPS_MANAGEMENT_DOWNLOAD_FAILED = 40007;

	/**
	 * 通知栏显示更新信息消息
	 */
	public static final int EVENT_APPS_LIST_UPDATE_NOTIFICATION = 40008;

	/**
	 * 通知“软件更新”TAB更新显示信息
	 */
	public static final int APPS_MANAGEMENT_UPDATE_COUNT = 40009;

	/**
	 * 点击“更新全部” 或 “取消全部”按钮
	 */
	public static final int APPCENTER_APPMANAGER_ALL_UPDATE_OR_CANCEL = 40010;
	/**
	 * 通知AppsManageView向任务管理器发送获取应用程序size的广播
	 */
	public static final int APPS_MANAGEMENT_QUERY_APP_SIZE = 40011;

	/**
	 * 通知推荐应用可以更新的应用
	 */
	public static final int APPS_MANAGEMENT_RECOMMENDED_APP = 40012;
	/**
	 * 应用下载或更新完成后，通知安装的消息
	 */
	public static final int APPS_MANAGEMENT_INSTALL_APP = 40013;

	/**
	 * 通知应用管理卸载应用
	 */
	public static final int APPS_MANAGEMENT_UNINSTALL_APP = 40014;

	/**
	 * 显示忽略更新界面
	 */
	public static final int SHOW_NO_PROMPT_UPDATE_VIEW = 40015;

	/**
	 * 移除忽略更新界面
	 */
	public static final int REMOVE_NO_PROMPT_UPDATE_VIEW = 40016;

	/**
	 * 显示批量卸载界面
	 */
	public static final int SHOW_UNINSTALL_APP_VIEW = 40017;

	/**
	 * 移除批量卸载界面
	 */
	public static final int REMOVE_UNINSTALL_APP_VIEW = 40018;

	/**
	 * 显示搜索界面
	 */
	public static final int SHOW_SEARCH_VIEW = 40019;

	/**
	 * 移除搜索界面
	 */
	public static final int REMOVE_SEARCH_VIEW = 40020;

	/**
	 * 显示所有应用页面
	 */
	public static final int SHOW_ALL_APPS_VIEW = 40021;
	/**
	 * 移除所有应用页面
	 */
	public static final int REMOVE_ALL_APPS_VIEW = 40022;
	/**
	 * 应用游戏中心，切换tab栏
	 */
	public static final int SKIP_SIDE_TAB = 40023;
	/**
	 * 应用游戏中心，首次进入首层tab界面加载完毕
	 */
	public static final int TOPTAB_VIEW_LOAD_FINISH = 40024;
	/**
	 * 应用游戏中心，联网获取可更新应用数据
	 */
	public static final int REFRESH_UPDATE_DATA = 40025;
	/**
	 * 应用游戏中心，通知主界面当网络可用时刷新界面
	 */
	public static final int REFRESH_WHEN_NETWORK_OK = 40026;
	/**
	 * 应用游戏中心，主页面上展示一个progressbar表示正在后台加载新数据
	 */
	public static final int SHOW_PREVLOAD_PROGRESS = 40027;
	/**
	 * 应用游戏中心，主页面上把progressbar移除表示后台已经加载完新数据
	 */
	public static final int HIDE_PREVLOAD_PROGRESS = 40028;
	/**
	 * 应用游戏中心，展示应用搬家界面
	 */
	public static final int SHOW_APP_MIGRATION_VIEW = 40029;
	/**
	 * 应用游戏中心，移除应用搬家界面
	 */
	public static final int REMOVE_APP_MIGRATION_VIEW = 40030;
	/**
	 * 显示应用游戏中心安装包管理页面
	 */
	public static final int SHOW_PACKAGE_MANAGEMENT_VIEW = 40031;
	/**
	 * 移除应用游戏中心安装包管理页面
	 */
	public static final int REMOVE_PACKAGE_MANAGEMENT_VIEW = 40032;
	/**
	 * 应用游戏中心，通知TabDataManager更新有变化的数据，并如果当前页面数据有变化，刷新当前页面
	 */
	public static final int REFRESH_TOPTAB_DATA = 40033;
	/**
	 * 应用游戏中心，忽略更新通知应用更新界面更新数据bean
	 */
	public static final int SEND_APP_TO_UPDATE_VIEW = 40034;
	/**
	 * 应用游戏中心，忽略更新与可更行应用数据交换
	 */
	public static final int CHANGE_APPLIST_INFO = 40035;
	/**
	 * 应用游戏中心，应用更新-用户点击后实现自动上滑到可见区域
	 */
	public static final int TOP_OF_LISTVIEW = 40036;
	/**
	 * 应用游戏中心，显示搜索按钮
	 */
	public static final int SHOW_SEARCH_BUTTON = 40037;
	/**
	 * 应用游戏中心，导步启动加载首页
	 */
	public static final int LOAD_MAIN_VIEW = 40038;
	/**
	 * 应用游戏中心，统计tab点击
	 */
	public static final int SAVE_TAB_CLICK = 40039;
	/**
	 * 应用游戏中心，统计tab点击
	 */
	public static final int REALTIME_SAVE_TAB_CLICK = 40040;

	/**
	 * 功能付费完成
	 */
	public static final int FUNCTION_ITEM_PURCHASED = 40041;
	/**
	 * 功能试用过期
	 */
	public static final int FUNCTION_TRIALEXPIRED = 40042;
}
