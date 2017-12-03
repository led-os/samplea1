package com.jiubang.ggheart.apps.desks.diy.messagecenter;
/**
 * 事件回调
 * @author zengyingzhen
 *
 */
public interface IMessageCenterActionListener {
	public static int MESSAGE_CENTER_VIEW_ACTION_BACK = 1;
	public static int MESSAGE_CENTER_VIEW_ACTION_DELETE = 2;
	public static int MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS = 3;
	public static int MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS_NORMAL = 4;
	public static int MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS_DELETE = 5;

	public void action(int actionCode);
}
