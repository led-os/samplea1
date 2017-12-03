package com.jiubang.ggheart.apps.desks.Preferences;

import java.net.URI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.graphics.DrawUtils;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingVisualBackgroundTabView;
import com.jiubang.ggheart.apps.desks.diy.CustomIconUtil;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.IRequestCodeIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.frames.dock.DefaultStyle.DockLogicControler;
import com.jiubang.ggheart.apps.desks.golauncherwallpaper.ChooseWallpaper;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.theme.ThemeManager;

/**
 * 
 * @author guoyiqing
 * 
 */
public class DeskSettingBackgroundActivity extends DeskSettingBaseActivity
		implements OnClickListener {

	private DeskSettingVisualBackgroundTabView mBackgroundTabView; // 背景
	private ShortCutSettingInfo mShortcutInfo;
	private FunAppSetting mFunAppSetting;
	private ScreenSettingInfo mScreenInfo;
	private PreferencesManager mPreferencesManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_background_main);
		mBackgroundTabView = (DeskSettingVisualBackgroundTabView) findViewById(R.id.background);
		mPreferencesManager = new PreferencesManager(this,
				IPreferencesIds.DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW,
				Context.MODE_WORLD_WRITEABLE);
		// 屏幕循环切换
		initInfos();
		load();
		getAllThemeData();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_BG);
	}

	/**
	 * <br>
	 * 功能简述:统一获取个性化设置里需要用到的数据，然后设置到各个tab <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void initInfos() {
		// 获取
		mShortcutInfo = SettingProxy.getShortCutSettingInfo();
		mFunAppSetting = SettingProxy.getFunAppSetting();
		mScreenInfo = SettingProxy.getScreenSettingInfo();
		// 设置
		mBackgroundTabView.setInfos(mShortcutInfo, mFunAppSetting, mScreenInfo);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DrawUtils.resetDensity(this);
		mBackgroundTabView.changeOrientation();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null == data) {
			// 回滚回之前的值
			if (IRequestCodeIds.REQUEST_OPERATION_SELECT_BACKGROUND == requestCode
					|| DeskSettingVisualBackgroundTabView.DRAWERBG_CLIP_CODE == requestCode) {
				restoreAppdrawerBgType();
			} else if (IRequestCodeIds.DOCK_CROP_CUSTOM_BG == requestCode
					|| IRequestCodeIds.REQUEST_CHANGE_CROP_ICON == requestCode
					|| IRequestCodeIds.DOCK_GO_THEME_BG == requestCode) {
				restoreDockBgType();
			}
			return;
		}

		switch (requestCode) {
		case IRequestCodeIds.DOCK_GO_THEME_BG: {
			if (resultCode == IRequestCodeIds.REQUEST_OPERATION_SELECT_DOCK_BACKGROUND
					&& null != AppCore.getInstance()) {
				// DOCK背景选择器
				Bundle bundle = data.getExtras();
				String pkgName = bundle
						.getString(ChooseWallpaper.BACGROUND_IMG_RESPKGNAME);
				String resName = bundle
						.getString(ChooseWallpaper.BACGROUND_IMG_NAME);

				if (pkgName.equals(ChooseWallpaper.TRANSPARENT_BG)) {
					mPreferencesManager
							.putBoolean(
									IPreferencesIds.DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW_DOCK,
									true);
					mPreferencesManager.commit();
					SettingProxy
							.updateCurThemeShortCutSettingBgSwitch(false);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
							IDockMsgId.CLEAR_BG, -1, null, null);
					showAndUpdateDockPicTransparent();
				} else {
					mPreferencesManager
							.putBoolean(
									IPreferencesIds.DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW_DOCK,
									false);
					mPreferencesManager.commit();
					SettingProxy.updateShortCutBg(
							ThemeManager.getInstance(ApplicationProxy.getContext())
									.getCurThemePackage(), pkgName, resName,
							false);

					// save();
					SettingProxy
							.updateCurThemeShortCutSettingBgSwitch(true);
					SettingProxy
							.updateCurThemeShortCutSettingCustomBgSwitch(true);
					SettingProxy.updateShortCutCustomBg(
							false);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
							IDockMsgId.UPDATE_DOCK_BG, -1, null, null);
					showAndUpdateDockPic();

				}

			} else {
				restoreDockBgType();
			}
		}
			break;

		case IRequestCodeIds.DOCK_CROP_CUSTOM_BG: {
			if (resultCode == Activity.RESULT_OK) {
				gotoCropCustomBg(data);
			}
		}
			break;

		case IRequestCodeIds.REQUEST_CHANGE_CROP_ICON: {
			if (resultCode == Activity.RESULT_OK) {
				try {
					SettingProxy.updateShortCutBg(ThemeManager.getInstance(ApplicationProxy.getContext())
							.getCurThemePackage(), null, DockLogicControler
							.getDockBgReadFilePath(), true);

					// save();
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
							IDockMsgId.UPDATE_DOCK_BG, -1, null, null);
				} catch (Exception e) {
					// 防空指针
					e.printStackTrace();
				}
			}
			showAndUpdateDockPic();
		}
			break;

		case IRequestCodeIds.REQUEST_OPERATION_SELECT_BACKGROUND: {
			try {
				// 获取数据
				Uri imageUri = data.getData();
				// 不裁剪
				if (null == imageUri) {
					Bundle bundle = data.getExtras();
					if (null != bundle) {
						String pn = bundle
								.getString(ChooseWallpaper.BACGROUND_IMG_RESPKGNAME);
						if (pn.endsWith(ChooseWallpaper.TRANSPARENT_BG)) {
							mPreferencesManager
									.putBoolean(
											IPreferencesIds.DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW_APPDRAWER,
											true);
							mPreferencesManager.commit();
							saveAppdrawerPic(FunAppSetting.BG_NON,
									FunAppSetting.DEFAULTBGPATH);
							showAndUpdateAppdrawerPicTransparent();
						} else {
							mPreferencesManager
									.putBoolean(
											IPreferencesIds.DESK_SETTING_VISUAL_BACKGROUND_TAB_VIEW_APPDRAWER,
											false);
							mPreferencesManager.commit();
							String name = bundle
									.getString(ChooseWallpaper.BACGROUND_IMG_RESNAME);
							URI uri = new URI(
									DeskSettingVisualBackgroundTabView.BGSETTINGTAG,
									pn, name);

							// 成功更换功能表背景主题图片
							saveAppdrawerPic(FunAppSetting.BG_GO_THEME,
									uri.toString());
							showAndUpdateAppdrawerPic();
						}

					}
				} else {
					// 调用照相机的裁剪
					clipAppdrawerPic(imageUri);
				}
			} catch (Exception e) {
				String textString = this.getString(R.string.NotFindCROP);
				DeskToast.makeText(this, textString, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

		}
			break;

		case DeskSettingVisualBackgroundTabView.DRAWERBG_CLIP_CODE: {
			// 成功更换功能表自定义背景
			// 通知功能表背景改变
			saveAppdrawerPic(FunAppSetting.BG_CUSTOM);
			showAndUpdateAppdrawerPic();
		}
			break;

		default:
			break;
		}
	}

	@Override
	public void load() {
		super.load();
		mBackgroundTabView.load();
	}

	@Override
	public void save() {
		super.save();
		mBackgroundTabView.save();
	}

	/**
	 * <br>
	 * 功能简述:更新dock背景图片 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void showAndUpdateDockPic() {
		// 显示
		mBackgroundTabView.showDockPic(true);
		// 更新
		mBackgroundTabView.updateDockWallpaper();
	}

	/**
	 * <br>
	 * 功能简述:更新dock背景图片 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void showAndUpdateDockPicTransparent() {
		// 显示
		mBackgroundTabView.showDockPic(false);
		// 更新
		mBackgroundTabView.updateDockWallpaper();
	}

	/**
	 * <br>
	 * 功能简述:更新功能表背景图片 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void showAndUpdateAppdrawerPic() {
		// 显示
		mBackgroundTabView.showAppdrawerPic(true);
		// 更新
		mBackgroundTabView.updateAppdrawerWallpaper();
	}

	/**
	 * 进入自定义图标
	 */
	private void gotoCropCustomBg(Intent data) {
		Intent intent = CustomIconUtil.getCropImageIntent(this, data,
				CustomIconUtil.DOCK_BG);
		if (intent != null) {
			startActivityForResult(intent,
					IRequestCodeIds.REQUEST_CHANGE_CROP_ICON);
		}
	}

	/**
	 * <br>
	 * 功能简述:更新功能表背景图片 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void showAndUpdateAppdrawerPicTransparent() {
		// 显示
		mBackgroundTabView.showAppdrawerPic(false);
		// 更新
		mBackgroundTabView.updateAppdrawerWallpaper();
	}

	public void clipAppdrawerPic(Uri imageUri) {
		mBackgroundTabView.clipAppdrawerPic(imageUri);
	}

	public void saveAppdrawerPic(int setting) {
		mBackgroundTabView.saveAppdrawerPic(setting);
	}

	public void saveAppdrawerPic(int setting, String path) {
		mBackgroundTabView.saveAppdrawerPic(setting, path);
	}

	public void restoreDockBgType() {
		mBackgroundTabView.restoreDockBgType();
	}

	public void restoreAppdrawerBgType() {
		mBackgroundTabView.restoreAppdrawerBgType();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBackgroundTabView.onResume();
	}

	@Override
	protected void setAllThemesData(String[] allThemePackage,
			String[] allThemeName) {
		super.setAllThemesData(allThemePackage, allThemeName);
		if (mBackgroundTabView != null) {
			mBackgroundTabView.setThemesData(allThemePackage, allThemeName);
		}
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		if (mBackgroundTabView != null) {
			mBackgroundTabView.checkNewMark(newMarkManager);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}
