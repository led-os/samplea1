package com.golauncher.message;

/**
 * 
 * @author liuheng
 *
 */
public interface IAppDrawerMsgId {
	public final static int BASE_ID = 35000;
	
	public static final int APPDRAWER_EXIT = 35001;
	public static final int APPDRAWER_POPUP_MENU = 35002;
	public static final int APPDRAWER_START_SORT = 35003;
	public static final int APPDRAWER_PRO_MANAGE_REFRESH = 35004;
	public static final int APPDRAWER_ALL_APP_SCROLL_TO_FIRST = 35005;
	public static final int APPDRAWER_ALL_APP_SHOW_HOME_KEY_ONLY_CHANGE = 35006;
	public static final int APPDRAWER_ON_SCREEN_REMOVED = 35007;
	public static final int APPDRAWER_ALL_APP_SCROLL_TO_TARGETITEM = 35008;
	public static final int APPDRAWER_ALL_APP_SCROLL_AND_OPENFOLDER = 35009;
	public static final int APPDRAWER_ALL_APP_ADD_ITEM_TO_FOLDERBAR = 35010;
	public static final int APPDRAWER_ALL_APP_CREATE_ITEM_BY_FOLDERBAR = 35011;
	public static final int APPDRAWER_ALL_APP_ICON_STATE_CHANGE = 35012;

	public static final int APPDRAWER_NOTIFY_REGET_UPDATEABEL_APPS_COUNT = 35013;
	public static final int APPDRAWER_LOCATE_APP = 35014;
	public static final int APPDRAWER_ALL_APP_REMOVE_ICON = 35015;

	public static final int APPDRAWER_UPDATE_FOLDER_ACTION_BAR_ICON_TITLE_COLOR = 35016;
	public static final int APPDRAWER_REFREASH_FOLDERBAR_TARGET = 35017;
	public static final int APPDRAWER_RESET_SCROLL_STATE = 35018;
	public static final int APPDRAWER_ARRANGE_APP = 35019;

	public static final int APPDRAWER_ALL_APP_MENU_BE_OPEN = 35020;
	public static final int APPDRAWER_SLIDE_MENU_ACTION = 35021;
	public static final int APPDRAWER_PRO_MANAGE_CHANGE_TO_EDIT_STATE = 35023;
	/**
	 * 功能表侧边栏，进入某个特定功能
	 * 
	 * @param param
	 *            IViewId，每个功能块有对应的id
	 * @param object
	 *            是否需要动画，Boolean
	 */

	public static final int APPDRAWER_SHOW_SIDEBAR_GUIDE_CLOUD = 35024;
	
	/**
	 * 处理甩动功能表／文件夹内图标做飞出动画后的处理
	 */
	public static final int APPDRAWER_ICON_ON_DRAG_FLING = 35025;
	
	/**
	 * 发给功能表，tab和底座主题应用改变
	 */
	public final static int APPDRAWER_TAB_HOME_THEME_CHANGE = 35026;
	/*
	 * 发给功能表，指示器主题应用改变
	 */
	public final static int APPDRAWER_INDICATOR_THEME_CHANGE = 35027;

	/*
	 * 程序列表可更新消息
	 */
	public static final int EVENT_APPS_LIST_UPDATE = 35029;
	
	/**
	 * 可更新程序数目改变
	 */
	public static final int EVENT_UPDATEABLE_APPS_COUNT_CHANGE = 35031;
	/**
	 * 功能表加载完成
	 */
	public static final int APPDRAWER_SPECIAL_FOLDER_DISMISS = 35032;
	/**
	 * 功能表侧边栏，进入某个特定功能
	 * 
	 * @param param
	 *            IViewId，每个功能块有对应的id
	 * @param object
	 *            是否需要动画，Boolean
	 */
	public static final int APPDRAWER_ENTER_FUNCTION_SLOT = 35033;
	
	public static final int FOLDER_ACTION_BAR_ICON_ELEMENT_START_UP_ANIMATION = 35034;

	public static final int FOLDER_ACTION_BAR_ICON_ELEMENT_START_DOWN_ANIMATION = 35035;
	
	public static final int SIDE_GUIDE_PORTLAND = 35036;
}
