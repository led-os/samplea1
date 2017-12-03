package com.jiubang.ggheart.data;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
/**
 * 
 * <br>类描述:DB错误Handler
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年10月16日]
 */
public class DatabaseAccessErrorHandler {
	private static DialogConfirm sDialog;
	public static void handle(final String dbName, Throwable t) {
		if (t != null) {
			Log.e("DatabaseAccessErrorHandler", "Throwable msg=" + t.getMessage());
			GoAppUtils.postLogInfo("DatabaseError", Log.getStackTraceString(t));
		}
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (GoLauncherActivityProxy.getActivity() != null) {
					if (sDialog == null) {
						sDialog = new DialogConfirm(GoLauncherActivityProxy.getActivity());
					}
					if (!sDialog.isShowing()) {
						sDialog.show();
						sDialog.setTitle(R.string.dialog_warning);
						sDialog.setMessage(R.string.dialog_database_access_error);
						sDialog.setPositiveButton(R.string.dialog_reset_default,
								new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
												ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
									}
								});
						sDialog.setNegativeButton(R.string.dialog_ignore, null);
					}
				}
			}
		});
	}
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年10月16日]
 */
	private static class RestoreDefaultTask extends AsyncTask<String, Void, Void> {
		ProgressDialog mDialog = new ProgressDialog(GoLauncherActivityProxy.getActivity());
		private boolean mIsRestart = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog.setMessage(ApplicationProxy.getContext().getString(
					R.string.restore_default_golauncher));
			mDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				delDB(params[0]);
				delDiygesture();
				// 通知gowidget清理数据库
				AppCore.getInstance().getGoWidgetManager().onResetDefault();
				// 删除所有SharedPreferences文件
				PreferencesManager.clearPreferences(ApplicationProxy.getContext());
				ThemeManager.clearThemeSharedpreference(ApplicationProxy.getContext());
				ApplicationProxy.getContext().sendBroadcast(
						new Intent(ICustomAction.ACTION_LAUNCHER_RESETDEFAULT));
				mIsRestart = true;
			} catch (Throwable e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			mDialog.dismiss();
			if (mIsRestart) {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
			}
		}

		private void delDB(String dbName) {
			ApplicationProxy.getContext().deleteDatabase(dbName);
		}

		private void delDiygesture() {
			String filepath = LauncherEnv.Path.SDCARD + LauncherEnv.Path.DIY_GESTURE_PATH
					+ "diyGestures";
			File file = new File(filepath);
			if (file.exists()) {
				file.delete();
			}
		}
	}
}
