package com.jiubang.ggheart.apps.desks.diy;

import android.content.Context;


/**
 * 历史原因，产生sharedpreference管理混乱的问题
 * 一些模块包括n个功能点，共用一个sharedpreference文件，但其中部分功能点需要跟随备份与还原，
 * 另一部分功能点不能跟随备份与还原，而备份还原只能进行文件级别的整体拷贝与还原，因此导致部分功能点备份还原功能失效，但又不能直接修改保存。
 * 因此，本类的作用在于，将其中需跟随备份还原的功能点，独立出来，字段不变，转移到新的sharedpreference文件中，以便备份还原模块统一处理。
 * @author huyong
 *
 */
public class PreferencesModelFixer {

	public static void fixOldSystemSettingPreference(Context context) {
		
		final PreferencesManager pm = new PreferencesManager(context,
				IPreferencesIds.BACKUP_SHAREDPREFERENCES_SYSTEM_SETTING_FILE, Context.MODE_PRIVATE);
		boolean hasSavedMode = pm.contains(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE);
		boolean hasSavedShowDlg = pm.contains(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG);
		if (!hasSavedMode && !hasSavedShowDlg) {
			//尚未保存任何数据，表明是刚升级上来，尚未采用新版Preference数据，则需要更新。
			final PreferencesManager oldPM = new PreferencesManager(context,
					IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
			String oldSavedMode = oldPM.getString(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE, "1");
			pm.putString(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE, oldSavedMode);
			
			boolean oldShowDlg = oldPM.getBoolean(
					IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG, true);
			pm.putBoolean(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG, oldShowDlg);
			pm.commit();
			
		}
	}
	
}
