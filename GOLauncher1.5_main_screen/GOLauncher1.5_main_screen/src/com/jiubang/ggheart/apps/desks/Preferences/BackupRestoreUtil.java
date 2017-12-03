package com.jiubang.ggheart.apps.desks.Preferences;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.jiubang.ggheart.apps.desks.backup.ExportDatabaseTask;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 备份相关接口
 * @author liuheng
 *
 */
public class BackupRestoreUtil {

	/**
	 * <br>功能简述:备份sharePreference到指定路径
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context 
	 * @param preferencesName
	 * @param path
	 */
	public static void backUpPreference(Context context, String preferencesName, String path) {
		File sharedPreferenceFile = new File(Environment.getDataDirectory() + "/data/"
				+ context.getPackageName() + "/shared_prefs/" + preferencesName);
		File tmpSPFile = new File(path);
		File backupSPFiles = new File(path + "/" + preferencesName);

		// note:此处为避免因SharedPreferences文件的拷贝出错，而影响到数据库文件的备份与提示工作
		try {
			tmpSPFile.mkdirs();
			// 拷贝SharedPreferences文件
			backupSPFiles.createNewFile();
			DeskSettingConstants.copyOutPutFile(sharedPreferenceFile, backupSPFiles, 0); // 不需要加密
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入指定路径下的preference
	 * @param context
	 * @param path
	 * @param preferencesName
	 * @param type
	 */
	public static void importSharedPreferences(Context context, String path, String preferencesName) {
		File backupSPFile = null;
		backupSPFile = new File(path + "/" + preferencesName);
		if (backupSPFile == null || !backupSPFile.exists()) {
			// 首先查找新版preferences目录下是否存在，若未找到，则再查找老版DB目录下是否存在SharedPreferences文件
			return;
		}
		if (backupSPFile != null && backupSPFile.exists()) {
			File tmpSPFile = new File(Environment.getDataDirectory() + "/data/"
					+ context.getPackageName() + "/shared_prefs/" + preferencesName);
			if (tmpSPFile.exists()) {
				tmpSPFile.delete();
			}
			try {
				tmpSPFile.createNewFile();
				DeskSettingConstants.copyInputFile(backupSPFile, tmpSPFile, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void importSharedPreferences(Context context, String preferencesName, int type) {
		File backupSPFile = null;
		if (type == ExportDatabaseTask.TYPE_LOCAL) {
			backupSPFile = new File(LauncherEnv.Path.SDCARD + LauncherEnv.Path.PREFERENCESFILE_PATH
					+ "/" + preferencesName);
			if (backupSPFile == null || !backupSPFile.exists()) {
				// 首先查找新版preferences目录下是否存在，若未找到，则再查找老版DB目录下是否存在SharedPreferences文件
				backupSPFile = new File(LauncherEnv.Path.SDCARD + LauncherEnv.Path.DBFILE_PATH
						+ "/" + preferencesName);
			}
		}
		if (backupSPFile != null && backupSPFile.exists()) {
			File tmpSPFile = new File(Environment.getDataDirectory() + "/data/"
					+ context.getPackageName() + "/shared_prefs/" + preferencesName);
			if (tmpSPFile.exists()) {
				tmpSPFile.delete();
			}
			try {
				tmpSPFile.createNewFile();
				DeskSettingConstants.copyInputFile(backupSPFile, tmpSPFile, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
