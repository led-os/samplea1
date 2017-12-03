package com.jiubang.ggheart.apps.desks.diy.messagecenter;
/**
 * 状态接口
 * @author zengyingzhen
 *
 */
public interface IMessageCenterViewStatus {
	public static int MESSAGE_CENTER_VIEW_STATUS_NORMAL = 0; // 正常情况
	public static int MESSAGE_CENTER_VIEW_STATUS_DELETE = 1; // 删除模式

	public void statusChange(int statusID);
}
