package com.jiubang.ggheart.apps.desks.diy;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.components.DeskToast;

/**
 * 
 *
 */
public class LockScreenHandler {
	public static void showLockScreenNotification(Context context) {
		// GoAppUtils.sendNotificationDisplaySeconds(context,
		// R.drawable.screen_lock,
		// context.getString(R.string.notification_screen_lock),
		// INotificationId.DISPLAY_SCREEN_LOCK);
		DeskToast.makeText(context, R.string.toast_screen_unlock, Toast.LENGTH_SHORT).show();
	}

	public static void showUnlockScreenNotification(Context context) {
		// GoAppUtils.sendNotificationDisplaySeconds(context,
		// R.drawable.screen_unlock,
		// context.getString(R.string.notification_screen_unlock),
		// INotificationId.DISPLAY_SCREEN_UNLOCK);
		DeskToast.makeText(context, R.string.notification_screen_unlock, Toast.LENGTH_SHORT).show();
	}

	public static void showUnlockScreenNotificationLong(Context context) {
		Intent it = new Intent();
		it.setClassName(context, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
		it.putExtra("screenlocked", "screenlocked");
		GoAppUtils.sendNotification(context, it, R.drawable.screen_lock,
				context.getString(R.string.screen_lock_tutoria_title),
				context.getString(R.string.screen_lock_tutoria_title),
				context.getString(R.string.screen_lock_edit_tutoria),
				INotificationId.DISPLAY_SCREEN_UNLOCK);
	}
}
