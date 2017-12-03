package com.jiubang.ggheart.plugin.mediamanagement;

import java.util.List;

import com.go.proxy.MsgMgrProxy;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.plugin.mediamanagement.inf.IMediaMessageManager;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2012-11-23]
 */
public class MediaMessageManager implements IMediaMessageManager {

	// LH TODO: check type is using?
	@Override
	public boolean sendMessage(Object who, int type, int handlerId, int msgId, int param, Object object, List<?> objList) {
		return MsgMgrProxy.sendMessage(who, handlerId, msgId, param, object, objList);
	}

	@Override
	public boolean sendMessage(Object who, int handlerId, int msgId, int param, Object object, List<?> objList) {
		return MsgMgrProxy.sendMessage(who, handlerId, msgId, param, object, objList);
	}

	@Override
	public void sendBroadcastMessage(Object who, int msgId, int param, Object object, List<?> objList) {
		MsgMgrProxy.sendBroadcast(who, msgId, param, object, objList);
	}

	@Override
	public void sendBroadcastMessage(Object who, int type, int msgId, int param, Object object, List<?> objList, int[] orderedHandlers) {
		MsgMgrProxy.sendBroadcastbyOrders(who, msgId, param, orderedHandlers, object, objList);
	}

	@Override
	public void postMessage(Object who, int handlerId, int msgId, int param, Object object, List<?> objList) {
		MsgMgrProxy.postMessage(who, handlerId, msgId, param, object, objList);
	}

	@Override
	public void postBroadcastMessage(Object who, int msgId, int param, Object object, List<?> objList) {
		MsgMgrProxy.postBroadcastMessage(who, msgId, param, object, objList);
	}

	@Override
	public void postBroadcastMessage(Object who, int msgId, int param, Object object, List<?> objList, int[] orderedHandlers) {
		MsgMgrProxy.postBroadcastMessageOrder(who, msgId, param, orderedHandlers, object, objList);
	}

	@Override
	public boolean registMsgHandler(IMessageHandler handler) {
		return MsgMgrProxy.registMsgHandler(handler);
	}

	@Override
	public boolean unRegistMsgHandler(IMessageHandler handler) {
		return MsgMgrProxy.unRegistMsgHandler(handler);
	}
}
