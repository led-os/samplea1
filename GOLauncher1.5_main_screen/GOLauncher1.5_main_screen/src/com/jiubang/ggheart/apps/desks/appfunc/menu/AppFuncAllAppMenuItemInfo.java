package com.jiubang.ggheart.apps.desks.appfunc.menu;

/**
 * 功能表菜单bean
 */
public class AppFuncAllAppMenuItemInfo extends BaseMenuItemInfo {
	public final static int ACTION_RUNNING = 0;
	public final static int ACTION_SORT_ICON = 1;
	public final static int ACTION_CREATE_NEW_FOLDER = 2;
	public final static int ACTION_APP_CENTER = 3;
	public final static int ACTION_GAME_CENTER = 4;
	public final static int ACTION_APPDRAWER_SETTING = 5;
	public final static int ACTION_APP_MANAGEMENT = 6;
	public final static int ACTION_APP_ARRANGE_APP = 7;
	/**
	 * 清除应用缓存
	 */
	public final static int ACTION_CLEAN_APP_CACHE = 11;

	public AppFuncAllAppMenuItemInfo(int actionId, int textId) {
		mActionId = actionId;
		mTextId = textId;
	}
	
	public AppFuncAllAppMenuItemInfo(int actionId, String text) {
		mActionId = actionId;
		mText = text;
	}
	
	public AppFuncAllAppMenuItemInfo(int actionId, boolean customStyle) {
		mActionId = actionId;
		mCustomStyle = customStyle;
	}
}
