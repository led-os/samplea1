package com.jiubang.ggheart.apps.desks.diy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 
 * @author liuheng
 *
 */
public class NotificationBarReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(ICustomAction.ACTION_OPEN_GGMENU)) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.SHOW_MENU, 2,
					null, null);
		}
	}
}
