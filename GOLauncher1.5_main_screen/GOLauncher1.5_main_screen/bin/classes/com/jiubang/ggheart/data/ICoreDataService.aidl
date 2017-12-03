package com.jiubang.ggheart.data;

import java.util.List;
import com.jiubang.ggheart.data.info.DeskMenuSettingInfo;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.EffectSettingInfo;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;
import com.jiubang.ggheart.data.info.ShortCutSettingInfo;
import com.jiubang.ggheart.data.info.GravitySettingInfo;
import com.jiubang.ggheart.data.info.DeskLockSettingInfo;
import com.jiubang.ggheart.data.info.GestureSettingInfo;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;

import com.jiubang.ggheart.apps.font.FontBean;
import com.go.proxy.ISettingsCallback;

interface ICoreDataService {
	boolean addAppFuncSetting(String packageName);
	String getAppFuncSetting(String pkname, int key);
	boolean setAppFuncSetting(String pkname, int key, String value);
	
	ScreenSettingInfo getScreenSettingInfo();
	DesktopSettingInfo getDesktopSettingInfo();
	EffectSettingInfo getEffectSettingInfo();
	ThemeSettingInfo getThemeSettingInfo();
	ShortCutSettingInfo getShortCutSettingInfo();
	GravitySettingInfo getGravitySettingInfo();
	ShortCutSettingInfo getDefaultThemeShortCutSettingInfo(String pkg);
	DeskLockSettingInfo getDeskLockSettingInfo();
	GestureSettingInfo getGestureSettingInfo(int gestureId);
	
	void removeSettingCallback(ISettingsCallback callback);
	void addSettingCallback(ISettingsCallback callback);
	void cleanup();
	
	void updateDesLockSettingInfo(in DeskLockSettingInfo info);
	void updateShortCutStyleByPackage(String packageName, String style);
	void clearDockSettingInfo();
	void resetShortCutBg(String useThemeName, String targetThemeName, String resName);
	void clearDirtyStyleSetting(String uninstallPackageName);
	void addScreenStyleSetting(String packageName);
	void updateEnable(boolean val);
	boolean updateShortCutCustomBg(boolean iscustom);
	boolean updateShortCutBg(String useThemeName, String targetThemeName, String resName, boolean isCustomPic);
	void updateCurThemeShortCutSettingCustomBgSwitch(boolean isOn);
	void updateCurThemeShortCutSettingBgSwitch(boolean isOn);
	void updateCurThemeShortCutSettingStyle(String style);
	int updateShortCutSettingNonIndepenceTheme(in ShortCutSettingInfo info);
	void updateShortcutSettingInfo();
	void updateUsedFontBean(in FontBean bean);
	void updateScreenIndicatorThemeBean(String packageName);
	void updateThemeSettingInfo(in ThemeSettingInfo info);
	void updateThemeSettingInfo2(in ThemeSettingInfo info, boolean broadCast);
	void updateScreenSettingInfo(in ScreenSettingInfo info);
	void updateScreenSettingInfo2(in ScreenSettingInfo info, boolean broadCaset);
	void updateGravitySettingInfo(in GravitySettingInfo info);
	void updateGestureSettingInfo(int type, in GestureSettingInfo info);
	void updateEffectSettingInfo(in EffectSettingInfo info);
	void updateDesktopSettingInfo2(in DesktopSettingInfo info, in boolean broadCast);
	void updateDesktopSettingInfo(in DesktopSettingInfo info);
	void updateDeskMenuSettingInfo(in DeskMenuSettingInfo info);
	void updateFontBeans(in List<FontBean> beans);
	
	void enablePurchaseFunction(int functionId);
	void onFunctionTrialExpired(int forceClear);
}