package com.go.util.androidsys;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.components.DeskAlertDialog;

/**
 * 检测不保留后台活动
 * @author liuheng
 *
 */
public class CheckAFA {

	/**
	 * 检测不保留后台活动
	 */
	public static void checkAFA(Activity activity) {
		try {
			// 4.0以上的手机检测不保留后台活动状态
			if (Build.VERSION.SDK_INT >= 14) {
				// 1为不保留后台活动选项打钩 0为正常 状态
				int alwaysStatus = Settings.System.getInt(activity.getContentResolver(),
						Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
				// 弹出提示设置框
				if (alwaysStatus == 1) {
					showSettingFinishDialog(activity);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 不保留后台活动设置对话框
	 */
	public static void showSettingFinishDialog(final Activity activity) {
		final DeskAlertDialog deskAlertDialog = new DeskAlertDialog(activity);
		String title = activity.getString(R.string.finishactivity_title);
		deskAlertDialog.setTitle(title);
		String content = activity.getString(R.string.finishactivity_content);
		String positiveBtnText = activity.getString(R.string.ok);
		String negativeBtnText = activity.getString(R.string.cancle);
		deskAlertDialog.setMessage(content);

		deskAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveBtnText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// try {
						// Object obj = new Object();
						// //ActivityManagerService类
						// Class ActivityManagerService = null;
						// //设置不保留后台服务的方法
						// Method setAlwaysFinish = null;
						// Method getActivityManagerProx= null;
						// //获得ActivityManagerService类
						// ActivityManagerService =
						// Class.forName("com.android.server.am.ActivityManagerService");
						// if(ActivityManagerService != null){
						// //得到getActivityManagerProx
						// getActivityManagerProx =
						// ActivityManagerService.getMethod("getDefault");
						// if(getActivityManagerProx != null &&
						// getActivityManagerProx.invoke(obj) != null
						// && getActivityManagerProx.invoke(obj).getClass() !=
						// null){
						// //获得设置不保留后台活动方法setAlwaysFinish
						// setAlwaysFinish =
						// (getActivityManagerProx.invoke(obj).getClass()).getMethod("setAlwaysFinish",
						// boolean.class);
						// if(setAlwaysFinish != null){
						// //不设置后台保留活动
						// setAlwaysFinish.invoke(getActivityManagerProx.invoke(obj),
						// false);
						// }
						// }
						// }
						//
						// } catch (Throwable e) {
						// e.printStackTrace();
						// }
						// 跳转正不保留后台活动设置界面
						Intent intent = new Intent();
						intent.setAction("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS");
						activity.startActivity(intent);
					}
				});
		deskAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeBtnText,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		deskAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				deskAlertDialog.selfDestruct();
			}
		});
		deskAlertDialog.show();
	}

}
