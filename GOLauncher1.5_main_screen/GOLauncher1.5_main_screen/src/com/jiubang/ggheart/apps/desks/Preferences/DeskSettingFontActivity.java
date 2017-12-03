package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.res.Configuration;
import android.os.Bundle;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingVisualFontTabView;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;

/**
 * 
 * @author guoyiqing
 * 
 */
public class DeskSettingFontActivity extends DeskSettingBaseActivity {

	private DeskSettingVisualFontTabView mFontTabView; // 字体
	private DeskSettingItemCheckBoxView mSettingShowAppName;
	private DeskSettingItemCheckBoxView mSettingShowAppNameAndBg;
	private DesktopSettingInfo mDesktopSettingInfo;
	private DeskSettingItemCheckBoxView mSettingShowFunAppName;
	private FunAppSetting mFunAppSettingInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_font_main);
		mFontTabView = (DeskSettingVisualFontTabView) findViewById(R.id.font);
		mSettingShowAppName = (DeskSettingItemCheckBoxView) findViewById(R.id.show_app_name);
		mSettingShowAppName.setOnValueChangeListener(this);
		mSettingShowAppNameAndBg = (DeskSettingItemCheckBoxView) findViewById(R.id.show_app_name_bg);
		mSettingShowAppNameAndBg.setOnValueChangeListener(this);
		mSettingShowFunAppName = (DeskSettingItemCheckBoxView) findViewById(R.id.fun_app_show_app_name);
		mSettingShowFunAppName.setOnValueChangeListener(this);
		initInfos();
		load();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_FONT);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DrawUtils.resetDensity(this);
		mFontTabView.changeOrientation(newConfig);
	}

	private void initInfos() {
		// 获取
		mDesktopSettingInfo = SettingProxy.getDesktopSettingInfo();
		// 设置
		mFontTabView.setInfo(mDesktopSettingInfo);
		setSettingShowAppName(mDesktopSettingInfo.mTitleStyle);
		mFunAppSettingInfo = SettingProxy.getFunAppSetting();
	}

	@Override
	public void load() {
		super.load();
		if (mFontTabView != null) {
			mFontTabView.load();
		}
		loadFunApp();
	}

	@Override
	public void save() {
		super.save();
		if (mFontTabView != null) {
			mFontTabView.save();
		}
		saveFunAppSetting();
		saveDeskSetting();
	}

	/**
	 * <br>功能简述:保存桌面设置
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void saveDeskSetting() {
		if (mDesktopSettingInfo != null) {
			boolean isChangeDesk = false;
			//是否显示程序名称和背景
			int titleStyle = getSettingShowAppNameTitleStyle();
			if (mDesktopSettingInfo.mTitleStyle != titleStyle) {
				mDesktopSettingInfo.mTitleStyle = titleStyle;
				isChangeDesk = true;
			}
			/*//是否开启桌面布局新算法
			if (mDesktopSettingInfo.mEnableMargins != mSettingShowScreenMargins.getIsCheck()) {
				mDesktopSettingInfo.mEnableMargins = mSettingShowScreenMargins.getIsCheck();
				isChangeDesk = true;
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
						IDiyMsgIds.SHOW_NEW_MARGINS, -1,
						mDesktopSettingInfo.mEnableMargins, null);
			}*/
			if (isChangeDesk) {
				//更改数据库数据
				SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo);
			}
		}

	}
	
	@Override
	public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
		boolean bRet = true;
		if (baseView == mSettingShowAppName) {
			if ((Boolean) value == true) {
				mSettingShowAppNameAndBg.setEnabled(true);
			} else {
				mSettingShowAppNameAndBg.setIsCheck(false);
				mSettingShowAppNameAndBg.setEnabled(false);
			}
		}
		return bRet;
	}

	public void loadFunApp() {
		if (mFunAppSettingInfo != null) {
			mSettingShowFunAppName
					.setIsCheck(mFunAppSettingInfo.getAppNameVisiable() == FunAppSetting.ON);
		}
	}
	
	public void saveFunAppSetting() {
		if (mFunAppSettingInfo != null) {
			int value;
			int isCheck;
			value = mFunAppSettingInfo.getAppNameVisiable();
			isCheck = DeskSettingConstants.boolean2Int(mSettingShowFunAppName.getIsCheck());
			if (value != isCheck) {
				mFunAppSettingInfo.setAppNameVisiable(isCheck);
			}
		}
	}
	
	/**
	 * <br>功能简述:	设置是否显示程序名称和背景
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param titleStyle
	 */
	public void setSettingShowAppName(int titleStyle) {

		//显示程序名称背景
		if (titleStyle == 0) {
			mSettingShowAppName.setIsCheck(true);
			mSettingShowAppNameAndBg.setIsCheck(true);
		}

		//显示程序名称
		else if (titleStyle == 1) {
			mSettingShowAppName.setIsCheck(true);
			mSettingShowAppNameAndBg.setIsCheck(false);
		}
		//没有显示程序名称
		else if (titleStyle == 2) {
			mSettingShowAppName.setIsCheck(false);
			mSettingShowAppNameAndBg.setIsCheck(false);
			mSettingShowAppNameAndBg.setEnabled(false);
			mSettingShowAppNameAndBg.getCheckBox().setEnabled(false);
			mSettingShowAppNameAndBg.setTitleColor(R.color.desk_setting_item_summary_color);
		}
	}

	/**
	 * <br>功能简述:获取是否显示程序名称的设置
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public int getSettingShowAppNameTitleStyle() {
		int titleStyle;
		if (mSettingShowAppName.getIsCheck()) {
			if (mSettingShowAppNameAndBg.getIsCheck()) {
				titleStyle = 0;
			} else {
				titleStyle = 1;
			}
		} else {
			titleStyle = 2;
		}
		return titleStyle;
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
