package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingVisualIconTabView;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.ScreenStyleConfigInfo;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.theme.ThemeManager;

/**
 * 
 * @author zouguiquan
 *
 */
public class DeskSettingIconActivity extends DeskSettingBaseActivity {

	private DeskSettingVisualIconTabView mIconTabView; // 图标
	private View mIconPreviewTitle;
	private View mIconPreview;
	
	// 设置信息infos
	private DesktopSettingInfo mDesktopInfo; // 桌面设置信息
	private ShortCutSettingInfo mShortcutInfo; // 快捷条设置信息
	private ScreenStyleConfigInfo mScreenStyleInfo; // 屏幕风格设置

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.desk_setting_icon);

		mIconTabView = (DeskSettingVisualIconTabView) findViewById(R.id.icon);
		mIconPreviewTitle = findViewById(R.id.icon_preview_title);
		mIconPreview = findViewById(R.id.showicons);
		load();
		getAllThemeData();
		updateIconPreviewState();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_ICONS);
	}
	
	@Override
	protected void setAllThemesData(String[] allThemePackage, String[] allThemeName) {
		mIconTabView.setThemesData(allThemePackage, allThemeName);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mIconTabView != null) {
			try {
				mIconTabView.onResume();
			} catch (Throwable t) {
				finish();
			}
		}
	}

//	@Override
//	protected Dialog onCreateDialog(int id) {
//		ProgressDialog dialog = null;
//		if (id == DIALOG_ID_INIT_LIST) {
//			dialog = new ProgressDialog(this);
//			dialog.setMessage(getString(R.string.icon_style_loading_message));
//			dialog.setCancelable(true);
//			dialog.setOnCancelListener(new OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					finish();
//				}
//			});
//		}
//		return dialog;
//	}
	
	@Override
	protected void onStop() {
		super.onStop();
		removeDialog(DIALOG_ID_INIT_LIST);
	}
	
	@Override
	public void load() {
		super.load();
		mDesktopInfo = SettingProxy.getDesktopSettingInfo();
		mShortcutInfo = SettingProxy.getShortCutSettingInfo();
		mScreenStyleInfo = ThemeManager.getInstance(this).getScreenStyleSettingInfo();
		
		mIconTabView.setInfos(mDesktopInfo, mShortcutInfo, mScreenStyleInfo);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DrawUtils.resetDensity(this);
		mIconTabView.changeOrientation(newConfig);
		updateIconPreviewState();
	}

	@Override
	public void save() {
		mIconTabView.save();
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateIconPreviewState() {
		LayoutParams params = (LayoutParams) mIconPreview.getLayoutParams();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mIconPreviewTitle.setVisibility(View.GONE);
			params.topMargin = DrawUtils.dip2px(4);
		} else {
			mIconPreviewTitle.setVisibility(View.VISIBLE);
			params.topMargin = 0;
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}