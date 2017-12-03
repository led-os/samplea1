package com.jiubang.ggheart.screen.systemsettings;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.jiubang.ggheart.apps.desks.Preferences.OnValueChangeListener;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DeskSettingSingleChoiceWithCheckboxDialog;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingSingleInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.PreferencesModelFixer;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;

/**
 * 启动系统设置界面
 * @author liuheng
 *
 */
public class SystemSettingControler {
	private static SystemSettingControler sInstance;
	private Context mContext;

	public static SystemSettingControler getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new SystemSettingControler(context);
		}
		return sInstance;
	}

	private SystemSettingControler(Context context) {
		mContext = context;
	}

	public void startSystemSetting() {
		PreferencesModelFixer.fixOldSystemSettingPreference(mContext);
		// 弹选择框
		final PreferencesManager pm = new PreferencesManager(mContext,
				IPreferencesIds.BACKUP_SHAREDPREFERENCES_SYSTEM_SETTING_FILE, Context.MODE_PRIVATE);
		boolean show = pm.getBoolean(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG, true);
		if (show) {
			DeskSettingItemListView view = (DeskSettingItemListView) LayoutInflater.from(mContext)
					.inflate(R.layout.system_setting_center_entrance_dialog, null);
			String mode = pm.getString(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE, "1");
			final DeskSettingSingleInfo singleInfo = view.getDeskSettingInfo().getSingleInfo();
			singleInfo.setSelectValue(mode);
			view.setOnValueChangeListener(new OnValueChangeListener() {
				@Override
				public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
					String selectValue = singleInfo.getSelectValue();
					boolean isCheck = singleInfo.getCheckBoxIsCheck();
					// 如果勾选了checkbox，则不再弹框
					pm.putBoolean(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG, !isCheck);
					pm.putString(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE,
							selectValue);
					pm.commit();
					startSetting();
					return true;
				}

				@Override
				public boolean onPreValueChange(
						DeskSettingItemBaseView baseView, Object value) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			view.performClick();
			DeskSettingSingleChoiceWithCheckboxDialog dialog = (DeskSettingSingleChoiceWithCheckboxDialog) (view
					.getDialog());
			dialog.setDissmissWhenClickItem(false);
		} else {
			startSetting();
		}
	}

	private void startSetting() {
		final PreferencesManager pm = new PreferencesManager(mContext,
				IPreferencesIds.BACKUP_SHAREDPREFERENCES_SYSTEM_SETTING_FILE, Context.MODE_PRIVATE);

		// LH TODO:为什么要用string
		String mode = pm.getString(IPreferencesIds.SYSTEM_SETTING_CENTER_SHOW_DIALOG_MODE, "1");
		if ("1".equals(mode)) {
			// 快捷设置
			Intent settings = new Intent(mContext, SystemSettingsCenter.class);
			GoLauncherActivityProxy.getActivity().startActivity(settings);
			GuiThemeStatistics
					.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_01);
		} else {
			//　系统设置
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_SETTINGS);
			GoLauncherActivityProxy.getActivity().startActivity(intent);
			GuiThemeStatistics
					.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.QUICK_SETTINGS_02);
		}
	}
}
