package com.jiubang.ggheart.components.appmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingBaseActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;

/**
 * 
 * @author zouguiquan
 *
 */
public class SimpleAppManagerSettingActivity extends DeskSettingBaseActivity {
	
	private DeskSettingItemCheckBoxView mAllowNotify;
	private PreferencesManager mSharedPreferences;
	
	private long mLastClickTime; // 最后一次点击时间
	private static final long CLICK_TIME = 400; // 每次点击间隔时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clean_screen_setting_view);

		mSharedPreferences = new PreferencesManager(this,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		initSetting();
	}

	private void initSetting() {
		mAllowNotify = (DeskSettingItemCheckBoxView) findViewById(R.id.allow_notify);
		mAllowNotify.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSharedPreferences.putBoolean(IPreferencesIds.ALLOW_NOTIFY_UNINSTALL_LESSUSE_APP, isChecked);
				mSharedPreferences.commit();
			}
		});
		mAllowNotify.setOnClickListener(this);
		
		boolean defValue = mSharedPreferences.getBoolean(IPreferencesIds.ALLOW_NOTIFY_UNINSTALL_LESSUSE_APP, true);
		mAllowNotify.setIsCheck(defValue);
	}

	@Override
	public void onClick(View v) {
		long curTime = System.currentTimeMillis();
		if (curTime - mLastClickTime < CLICK_TIME) {
			return;
		}
		mLastClickTime = curTime;
		
		switch (v.getId()) {
			case R.id.allow_notify :
				switchNotifyUninstall(v);
				break;

			default :
				break;
		}
	}

	private void switchNotifyUninstall(View view) {
		boolean checked = mAllowNotify.getCheckBox().isChecked();
		checked = !checked;
		mAllowNotify.setIsCheck(checked);
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}
