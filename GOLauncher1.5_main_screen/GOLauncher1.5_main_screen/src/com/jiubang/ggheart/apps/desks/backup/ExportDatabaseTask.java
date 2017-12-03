package com.jiubang.ggheart.apps.desks.backup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.gau.go.launcherex.R;
import com.go.util.file.FileUtil;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.data.AppClassifyDatabaseHelper;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.statistics.StatisticsDataBaseHelper;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * <br>类描述:桌面备份数据导出
 * <br>功能详细描述:
 * 
 * @author  
 * @date  
 */
public class ExportDatabaseTask extends AsyncTask<Void, Void, String> {
	public static final int ENCRYPTBYTE = 10; // 加密字节长度
	public static final String GOOGLE_ANALYTICS_FILENAME = "google_analytics";

	public static final int TYPE_LOCAL = 1; //本地备份
	private int mType = TYPE_LOCAL; // 备份类型

	private Activity mActivity;
	private IBackupDBListner mListner; //监听者
	private ProgressDialog mDialog = null;

	public void setActivity(Activity activity) {
		mActivity = activity;
	}

	public void setType(int type) {
		mType = type;
	}

	public void setListner(IBackupDBListner listner) {
		mListner = listner;
	}

	// can use UI thread here
	@Override
	protected void onPreExecute() {
		this.mDialog = new ProgressDialog(mActivity);
		this.mDialog.setMessage(mActivity.getResources().getString(R.string.dbfile_export_dialog));
		this.mDialog.show();
		DataProvider.getInstance(mActivity).close();

		mListner.onExportPreExecute();
	}

	// automatically done on worker thread (separate from UI thread)
	@Override
	protected String doInBackground(final Void... args) {
		synchronized (DataProvider.DB_LOCK) {
			boolean error = false;
			if (mType == TYPE_LOCAL) {
				BackupRestoreParser parser = BackupRestoreParser.getParser();
				List<BackupRestoreItem> items = parser
						.getBackupFileMap(mActivity);
				for (BackupRestoreItem backupRestoreItem : items) {
					if (backupRestoreItem.mIsFolder) {
						try {
							FileUtil.copyFolder(backupRestoreItem.mBackupPath,
									backupRestoreItem.mRestorePath, true,
									ENCRYPTBYTE);
						} catch (Exception e) {
							error = true;
							e.printStackTrace();
						}
					} else {
						FileUtil.copyBackupFile(backupRestoreItem.mBackupPath,
								backupRestoreItem.mRestorePath);
					}
				}
			}
			if (error) {
				return mActivity.getResources().getString(R.string.dbfile_export_error);
			} else {
				return mActivity.getResources().getString(R.string.dbfile_export_success);
			}
		}
	}

	// can use UI thread here
	@Override
	protected void onPostExecute(final String msg) {
		if (this.mDialog.isShowing()) {
			try {
				this.mDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mListner.onExportPostExecute(mType, msg);
	}

	/**
	 * 备份自定义手势文件
	 */
	private void backupDiygesture() {
		String filepath = null; //backup源文件
		String backupfilepath = null; //backup目标文件

		filepath = LauncherEnv.Path.SDCARD + LauncherEnv.Path.DIY_GESTURE_PATH + "diyGestures";
		if (mType == TYPE_LOCAL) {
			backupfilepath = LauncherEnv.Path.SDCARD + LauncherEnv.Path.DIY_GESTURE_PATH + "backup";
		}
		File backupfile = new File(backupfilepath);
		File file = new File(filepath);
		if (file.exists()) {
			try {
				FileUtil.createFile(backupfilepath, true);
				DeskSettingConstants.copyInputFile(file, backupfile, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 文件夹内的内容备份，若目标文件夹，未生成，则首先生成目标文件夹
	 * 
	 * @author huyong
	 * @param srcFolderPath ： 源文件夹目录
	 * @param destFolderpath：目标文件夹目录
	 * @param encrypt ：true for加密，false for 解密
	 * @param encryptByte ：加密字节数
	 * @throws IOException
	 */
	public static void copyFolder(String srcFolderPath, String destFolderpath, boolean encrypt,
			int encryptByte) throws IOException {
		if (srcFolderPath == null || destFolderpath == null) {
			return;
		}
		File srcFolder = new File(srcFolderPath);
		if (srcFolder == null || !srcFolder.exists() || !srcFolder.isDirectory()) {
			return;
		}

		if (encryptByte < 0) {
			encryptByte = 0;
		}

		// 构造目标文件夹
		File destFolder = new File(destFolderpath);
		destFolder.mkdirs();

		File[] srcFolderFiles = null; // 源文件夹
		srcFolderFiles = srcFolder.listFiles();
		if (srcFolderFiles == null) {
			return;
		}

		int count = srcFolderFiles.length;
		File srcFile = null;
		File destFile = null;
		String fileName = null;
		for (int i = 0; i < count; i++) {
			srcFile = srcFolderFiles[i];
			if (srcFile.isFile()) {
				// 开始拷贝
				fileName = srcFile.getName();
				if (fileName.contains(GOOGLE_ANALYTICS_FILENAME)) {
					continue;
				}
				if (fileName.contains(StatisticsDataBaseHelper.getDBName())) {
					// 统计数据库不用拷贝
					continue;
				}
				if (fileName.contains(AppClassifyDatabaseHelper.getDBName())) {
					// 应用分类数据库不用拷贝
					continue;
				}
				destFile = new File(destFolderpath + "/" + fileName);
				if (destFile.exists()) {
					destFile.delete();
				}
				destFile.createNewFile();
				if (encrypt) {
					DeskSettingConstants.copyOutPutFile(srcFile, destFile, encryptByte);
				} else {
					DeskSettingConstants.copyInputFile(srcFile, destFile, encryptByte);
				}
			}
		}
	}

}
