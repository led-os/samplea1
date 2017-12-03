package com.jiubang.ggheart.apps.desks.appfunc.menu;

/**
 * 正在运行菜单信息
 * @author yejijiong
 *
 */
public class AppFuncProManageMenuItemInfo extends BaseMenuItemInfo  {

	public final static int ACTION_REFRESH = 0;
	public final static int ACTION_LOCK_LIST = 1;
	public final static int ACTION_HIDE_LOCK_APP = 2;
	public final static int ACTION_SHOW_LOCK_APP = 3;

	public AppFuncProManageMenuItemInfo(int actionId, int textId) {
		mActionId = actionId;
		mTextId = textId;
	}
	
	public AppFuncProManageMenuItemInfo(int actionId, String text) {
		mActionId = actionId;
		mText = text;
	}
}
