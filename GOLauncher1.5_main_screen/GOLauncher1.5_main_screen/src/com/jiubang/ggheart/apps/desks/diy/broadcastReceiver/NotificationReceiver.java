package com.jiubang.ggheart.apps.desks.diy.broadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IAppDrawerMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.launcher.ICustomAction;

/**
 * 
 * 处理通知栏状态的的接收者
 * @author zouguiquan
 *
 */
public class NotificationReceiver extends BroadcastReceiver {

	public static final String RECOMMEND_3DCORE = "recommend_3dcore";
	public static final String APP_INTENT = "app_intent";
	public static final int RECOMMEND_3DCORE_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (ICustomAction.ACTION_CLEAN_NOTIFICATION_FOR_RECOMMEND_3DCORE.equals(action)) {
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(RECOMMEND_3DCORE, RECOMMEND_3DCORE_ID);

			PreferencesManager sharedPreferences = new PreferencesManager(context,
					IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
			sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_RECOMMEND_SHELL_ENGINE, true);
			sharedPreferences.putBoolean(
					IPreferencesIds.PREFERENCE_RECOMMEND_SHELL_ENGINE_NOTIFIY_EXIST, false);
			sharedPreferences.commit();
		} else if (ICustomAction.ACTION_APPDRAWER_LOCATE_APP.equals(action)) {
			Intent appIntent = intent.getParcelableExtra(APP_INTENT);
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_DRAWER,
					IAppDrawerMsgId.APPDRAWER_LOCATE_APP, -1, appIntent);
		}
	}
}
