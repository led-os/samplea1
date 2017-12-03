package com.jiubang.ggheart.apps.desks.backup;

import java.io.File;

import android.content.Context;
import android.content.Intent;

import com.go.util.file.FileUtil;
import com.jiubang.ggheart.apps.desks.Preferences.BackupRestoreUtil;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.settings.BackUpSettingReceiver;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.ThreadName;

/**
 * 自動備份工具類
 * 
 * @author rongjinsong
 * 
 */
public class AutoBackUpTool {

	public static void backUpSettings(Context context) {
		backUpDB(context);
		backUpOtherFiles(context);
	}

	public static void backUpDB(Context context) {
		Intent bkIntent = new Intent(ICustomAction.ACTION_BACKRECEIVER);
		bkIntent.putExtra(BackUpSettingReceiver.BACKCMD,
				BackUpSettingReceiver.BACKUP_DB);
		bkIntent.putExtra(BackUpSettingReceiver.BACKPATH,
				LauncherEnv.Path.DB_AUTO_BACKUP_PATH + "/db");
		bkIntent.putExtra(BackUpSettingReceiver.BACKALL, true);
		context.sendBroadcast(bkIntent);
	}

	public static void backUpOtherFiles(final Context context) {
		new Thread(ThreadName.AUTO_BACKUP) {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String filepath = null; // backup源文件
				String backupfilepath = null; // backup目标文件

				filepath = LauncherEnv.Path.SDCARD
						+ LauncherEnv.Path.DIY_GESTURE_PATH + "diyGestures";
				backupfilepath = LauncherEnv.Path.DB_AUTO_BACKUP_PATH
						+ "/Gestures" + "/backup";
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

				BackupRestoreUtil.backUpPreference(context,
						IPreferencesIds.USERTUTORIALCONFIG + ".xml",
						LauncherEnv.Path.DB_AUTO_BACKUP_PATH + "/preference");
			}

		}.start();
	}

	/**
	 * 恢复自定义手势备份
	 */
	public static void restoreDiygesture() {
		String backupfilepath = null;
		backupfilepath = LauncherEnv.Path.DB_AUTO_BACKUP_PATH + "/Gestures"
				+ "/backup";
		File backupfile = new File(backupfilepath);
		if (backupfile.exists()) {
			// 1：删除手势文件
			String filepath = LauncherEnv.Path.SDCARD
					+ LauncherEnv.Path.DIY_GESTURE_PATH + "diyGestures";
			File file = new File(filepath);
			if (file.exists()) {
				file.delete();
			}
			// 2：导入手势备份文件
			try {
				file.createNewFile();
				DeskSettingConstants.copyInputFile(backupfile, file, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static File deleteDbFile(String path) {
		File dbFile = new File(path);
		FileUtil.deleteFile(path);
		deleteDbWalFiles(path);
		return dbFile;
	}

	public static void deleteDbWalFiles(String path) {
		String tmpFilePath = path + "-shm";
		FileUtil.deleteFile(tmpFilePath);
		tmpFilePath = path + "-wal";
		FileUtil.deleteFile(tmpFilePath);
	}
}
