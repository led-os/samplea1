package com.golauncher.message;

/**
 * 
 * @author liuheng
 * 
 */
public interface IScreenEditMsgId {
	public final static int BASE_ID = 33000;

	/**
	 * 添加系统快捷方式
	 */
	public static final int SCREEN_EDIT_ADD_SYSTEM_SHORTCUT = 33001;

	/**
	 * 添加GO小部件至桌面
	 * 
	 * @param param
	 *            索引值
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_EDIT_ADD_GOWIDGET_TO_SCREEN = 33002;

	/**
	 * unuse 2014/04/12
	 * 进入桌面编辑状态时，通知添加模块进行进入动画
	 */
	public static final int SCREENEDIT_SHOW_ANIMATION_IN = 33003;

	/**
	 * 添加GO小部件
	 */
	public final static int SCREEN_EDIT_ADD_GOWIDGET = 33004;

	/**
	 * 添加GO小部件(切换到info界面)
	 */
	public final static int SCREEN_EDIT_ADD_GOWIDGET_INFO = 33005;

	/**
	 * 添加GO小部件(切换到预览界面)
	 */
	public final static int SCREEN_EDIT_ADD_GOWIDGET_PIC = 33006;

	/**
	 * 
	 */
	public final static int SCREEN_EDIT_CHANGE_NORMAL = 33007;

	public final static int SCREEN_EDIT_GLPROGRESSBAR = 33008;

	public final static int SCREEN_EDIT_GLPROGRESSBAR_LARGE = 33009;

	public final static int SCREEN_EDIT_INDICATOR = 33010;

	public final static int SCREEN_EDIT_INDICATOR_LARGE = 33011;

	public final static int SCREEN_EDIT_REFERSH_EFFECT = 33012;

	/**
	 * 添加widget完成时解锁屏幕
	 */
	public static final int UNLOCK_SCREEN_FOR_ADD_WIDGET = 33013;

	

	public final static int SCREEN_ADD_SYSTEMWIDGET = 33014;

	public static final int SCREEN_EDIT_ADD_SYSTEM_WIDGET_TO_SCREEN = 33015;

	/**
	 * 通知添加模块刷新壁纸选项列表
	 */
	public final static int SCREEN_EDIT_UPDATE_WALLPAPER_ITEMS = 33016;
	
	/**
	 * 跳转到应用程序添加Tab
	 */
	public final static int SCREEN_EDIT_GOTO_APPS_TAB = 33017;
	
	/**
	 * 跳转到文件夹添加Tab
	 */
	public final static int SCREEN_EDIT_GOTO_FORLDER_TAB = 33018;
	
	/**
	 * 跳转到gowidget第一级tab
	 */
	public final static int SCREEN_EDIT_GOTO_GOWIDGET_TAB = 33019;
	
	/**
	 * 跳转到gowidget第二级tab
	 */
	public final static int SCREEN_EDIT_GOTO_GOWIDGET_SUB_TAB = 33020;
	
	/**
	 * 跳转到系统widget第一级tab
	 */
	public static final int SCREEN_EDIT_GOTO_SYSTEMWIDGET_TAB = 33021;
	
	/**
	 * 跳转到系统widget第二级tab
	 */
	public static final int SCREEN_EDIT_GOTO_SYSTEMWIDGET_SUB_TAB = 33022;
	
	/**
	 * 跳转到GO快捷方式添加Tab
	 */
	public final static int SCREEN_EDIT_GOTO_GOSHORTCUT_TAB = 33023;
	
	/**
	 * 更新添加界面中的标题
	 */
	public final static int SCREEN_EDIT_REFRESH_TITLE = 33024;
	
	/**
	 * 添加界面中处理返回事件
	 */
	public final static int SCREEN_EDIT_HANDLE_KEY_BACK = 33025;
	
	/**
	 * 跳转到应用程序添加Tab
	 */
	public final static int SCREEN_EDIT_CHANGE_TAB = 33026;
	
	/**
	 * 进入添加模块后，是否在图标下面画一层暗影
	 */
	public final static int SCREEN_EDIT_DRAW_BACKGROUND = 33027;
}
