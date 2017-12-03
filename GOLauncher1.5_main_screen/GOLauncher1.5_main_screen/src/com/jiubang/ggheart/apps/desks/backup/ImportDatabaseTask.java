package com.jiubang.ggheart.apps.desks.backup;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.util.file.FileUtil;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * <br>类描述:桌面备份数据导入
 * <br>功能详细描述:
 * 
 */
public class ImportDatabaseTask extends AsyncTask<Void, Void, String> {

	public static final String CONFIG_BACKUP_FILENAME = "gauGO";

	private Activity mActivity;
	private IBackupDBListner mListner; //监听者

	private ProgressDialog mDialog = null;
	private boolean mIsRestart = false;

	private int mType = ExportDatabaseTask.TYPE_LOCAL; // 备份类型

	public void setActivity(Activity activity) {
		mActivity = activity;
	}

	public void setType(int type) {
		mType = type;
	}

	public void setListner(IBackupDBListner listner) {
		mListner = listner;
	}

	@Override
	protected void onPreExecute() {
		this.mDialog = new ProgressDialog(mActivity);
		this.mDialog.setMessage(mActivity.getResources().getString(R.string.dbfile_import_dialog));
		this.mDialog.show();
		DataProvider.getInstance(mActivity).close();

		mListner.onImportPreExecute();
	}

	@Override
	protected String doInBackground(final Void... args) {
		synchronized (DataProvider.DB_LOCK) {
			deleteDbWalFiles(LauncherEnv.Path.SDCARD + LauncherEnv.Path.DBFILE_PATH
					+ "/androidheart.db");
			boolean error = false;
			if (mType == ExportDatabaseTask.TYPE_LOCAL) {
				BackupRestoreParser parser = BackupRestoreParser.getParser();
				List<BackupRestoreItem> items = parser
						.getBackupFileMap(mActivity);
				for (BackupRestoreItem backupRestoreItem : items) {
					if (backupRestoreItem.mIsFolder) {
						try {
							FileUtil.copyFolder(backupRestoreItem.mRestorePath,
									backupRestoreItem.mBackupPath, false,
									ExportDatabaseTask.ENCRYPTBYTE);
						} catch (Exception e) {
							error  = true;
							e.printStackTrace();
						}
					} else {
						FileUtil.copyBackupFile(backupRestoreItem.mRestorePath,
								backupRestoreItem.mBackupPath);
					}
				}
				mIsRestart = true;
				DataProvider.getInstance(mActivity).checkBackDB();
			}
			if (error) {
				return mActivity.getResources().getString(R.string.dbfile_import_error);
			} else {
				return null;
			}
			
		}
	}

	@Override
	protected void onPostExecute(final String msg) {
		if (this.mDialog.isShowing()) {
			try {
				this.mDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (msg != null && msg.length() > 0) {
			DeskToast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
		}
		if (mIsRestart) {
			exitAndRestart();
		}

		mListner.onImportPostExecute(mType, msg);
	}

	/**
	 * <br>功能简述:退出重新启动桌面
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void exitAndRestart() {
		//if stop the service, the 'System persistent' would not take effect
		Intent intent = new Intent();
		intent.setClassName(IGoLauncherClassName.GOLAUNCHER_ACTIVITY, IGoLauncherClassName.GOLAUNCHER_APPSERVICE);
		ApplicationProxy.getContext().stopService(intent);
		mActivity.setResult(DeskSettingConstants.RESULT_CODE_RESTART_GO_LAUNCHER,
				mActivity.getIntent());
		mActivity.finish();
//		ApplicationProxy.getApplication().exit(true);
	}

	/**
	 * 恢复自定义手势备份
	 */
	private void restoreDiygesture() {
		// 1：删除手势文件
		String filepath = LauncherEnv.Path.SDCARD + LauncherEnv.Path.DIY_GESTURE_PATH
				+ "diyGestures";
		File file = new File(filepath);
		if (file.exists()) {
			file.delete();
		}
		// 2：导入手势备份文件
		String backupfilepath = null;
		if (mType == ExportDatabaseTask.TYPE_LOCAL) {
			backupfilepath = LauncherEnv.Path.SDCARD + LauncherEnv.Path.DIY_GESTURE_PATH + "backup";
		}
		File backupfile = new File(backupfilepath);
		if (backupfile.exists()) {
			try {
				file.createNewFile();
				DeskSettingConstants.copyInputFile(backupfile, file, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private File deleteDbFile(String path) {
		File dbFile = new File(path);
		FileUtil.deleteFile(path);
		deleteDbWalFiles(path);
		return dbFile;
	}

	private void deleteDbWalFiles(String path) {
		String tmpFilePath = path + "-shm";
		FileUtil.deleteFile(tmpFilePath);
		tmpFilePath = path + "-wal";
		FileUtil.deleteFile(tmpFilePath);
	}
}
