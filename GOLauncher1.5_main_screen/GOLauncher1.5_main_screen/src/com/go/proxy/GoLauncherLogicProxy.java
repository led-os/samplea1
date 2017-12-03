package com.go.proxy;

import android.graphics.Typeface;

import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingVisualIconTabView;
import com.jiubang.ggheart.apps.font.FontControler;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.ThemeSettingInfo;

/**
 * GoLauncher代理
 * 处理一些和GoLauncher相关的业务（需要用到Activity的接口）
 * 但是又和GoLauncher的业务相关（不相关的在GoLauncherActivityProxy）
 * @author liuheng
 * 
 */
public class GoLauncherLogicProxy {
	private static int sStartTutorialMask = -1;

	public static int getIconSizeStyle() {
		DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
		if (null != info) {
			return info.getIconSizeStyle();
		}
			
		return DeskSettingVisualIconTabView.DEFAULT_ICON_SIZE;
	}

	public static boolean isLargeIcon() {
		if (getIconSizeStyle() == DeskSettingVisualIconTabView.LARGE_ICON_SIZE) {
			return true;
		}
		return false;
	}

	public static int getAppFontSize() {		
		DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
		if (null != info) {
			return info.getFontSize();
		}
		
		return 13;
	}

	public static boolean getCustomTitleColor() {
		DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
		if (info != null) {
			return info.mCustomTitleColor;
		}
		
		return false;
	}

	public static int getAppTitleColor() {		
		DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
		if (info != null && info.mCustomTitleColor) {
			return info.mTitleColor;
		}
			
		return 0;
	}

	public static boolean getIsShowAppTitle() {
		boolean ret = true;
		
		DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
		if (null != info) {
			return info.isShowTitle();
		}
			
		return ret;
	}

	public static boolean getIsShowAppTitleBg() {
		boolean ret = true;
		
		DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
		if (null != info) {
			return !info.isTransparentBg();
		}
		
		return ret;
	}

	public static Typeface getAppTypeface() {
		FontControler controler = FontControler.getInstance(ApplicationProxy
				.getContext());
		if (controler != null) {
			return controler.getUsedFontBean().mFontTypeface;
		}
		return null;
	}

	public static int getAppTypefaceStyle() {
		FontControler controler = FontControler.getInstance(ApplicationProxy
				.getContext());
		if (controler != null) {
			return controler.getUsedFontBean().mFontStyle;
		}
		return 0;
	}
	
	public static int getTutorialMask() {
		return sStartTutorialMask;
	}

	public static void setTutorialMask(int mask) {
		sStartTutorialMask = mask;
	}
	
	/**
	 * 是否开启高质量绘图
	 * 
	 * @return 高质量返回true
	 */
	public static boolean isHighQualityDrawing() {
		ThemeSettingInfo info = SettingProxy.getThemeSettingInfo();
		if (info != null) {
			return info.mHighQualityDrawing;
		}
		return false;
	}
}
