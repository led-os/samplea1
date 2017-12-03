package com.jiubang.ggheart.apps.desks.diy.pref;

import android.content.Context;
import android.os.Message;

import com.go.util.DeferredHandler;

/**
 * 
 * @author guoyiqing
 *
 */
public class PrefUpgradeHandler extends DeferredHandler {

	private static final int UPGRADE_MSG_CODE = 0xFF;
	private Context mContext;
	public PrefUpgradeHandler(Context context) {
		mContext = context;
	}
	
	public void sendPrefUpgradeMsg() {
		Message msg = new Message();
		msg.what = UPGRADE_MSG_CODE;
		sendMessage(msg);
	}
	
	@Override
	public void handleIdleMessage(Message msg) {
		switch (msg.what) {
		case UPGRADE_MSG_CODE:
			PrefMigrateManager.getManager().upgradeAsync(mContext);
			break;
		default:
			break;
		}
	}


}
