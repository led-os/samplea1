package com.jiubang.ggheart.data.info;

import android.content.Context;

import com.go.commomidentify.IGoLauncherClassName;
import com.jiubang.ggheart.data.Setting;
import com.jiubang.ggheart.data.model.GoContentProviderUtil;
import com.jiubang.ggheart.data.theme.ThemeManager;
/**
 * 
 * <br>类描述:屏幕风格
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013-8-21]
 */
public class ScreenStyleConfigInfo{

	private ThemeManager mThemeManager;

	private Context mContext;
	public ScreenStyleConfigInfo(Context context, ThemeManager themeManager) {

		//		mDataModel = dataModel;
		mThemeManager = themeManager;
		mContext = context;
		initSetting();
	}

	public void initSetting() {
		// 主题相关
		if (mThemeManager != null) {
			//			 mDataModel.addScreenStyleSetting(mThemeManager.getCurThemePackage());
			GoContentProviderUtil.addScreenStyleSetting(mContext,
					mThemeManager.getCurThemePackage());
		} else {
			GoContentProviderUtil.addScreenStyleSetting(mContext,
					IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
		}
	}

	public void setIconStyle(String stylePackage) {
		setStyleSetting(mThemeManager.getCurThemePackage(), Setting.ICONSTYLEPACKAGE, stylePackage);
	}

	public void setFolderStyle(String stylePackage) {
		setStyleSetting(mThemeManager.getCurThemePackage(), Setting.FOLDERSTYLEPACKAGE,
				stylePackage);
	}

	public void setGGmenuStyle(String stylePackage) {
		setStyleSetting(mThemeManager.getCurThemePackage(), Setting.GGMENUPACKAGE, stylePackage);
	}

	public void setIndicatorStyle(String stylePackage) {
		setStyleSetting(mThemeManager.getCurThemePackage(), Setting.INDICATOR, stylePackage);
	}

	public void setStyleSetting(final String packageName, final int settingItem, final String value) {
		//		mDataModel.updateScreenStyleSetting(packageName, settingItem, value);
		GoContentProviderUtil.updateScreenStyleSetting(mContext, packageName, settingItem, value);
	}

	public String getIconStyle() {
		String str = null;
		if (null != mThemeManager) {
			// 其他主题
			str = getStyleSetting(mThemeManager.getCurThemePackage(), Setting.ICONSTYLEPACKAGE);
		} else {
			// 默认主题
			str = getStyleSetting(IGoLauncherClassName.DEFAULT_THEME_PACKAGE, Setting.ICONSTYLEPACKAGE);
		}

		if (null == str) {
			return IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}

		return str;
	}

	public String getFolderStyle() {
		String str = null;
		if (null != mThemeManager) {
			// 其他主题
			str = getStyleSetting(mThemeManager.getCurThemePackage(), Setting.FOLDERSTYLEPACKAGE);
		} else {
			// 默认主题
			str = getStyleSetting(IGoLauncherClassName.DEFAULT_THEME_PACKAGE, Setting.FOLDERSTYLEPACKAGE);
		}

		if (null == str) {
			return IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}

		return str;
	}

	public String getGGmenuStyle() {
		String str = null;
		if (null != mThemeManager) {
			// 其他主题
			str = getStyleSetting(mThemeManager.getCurThemePackage(), Setting.GGMENUPACKAGE);
		} else {
			// 默认主题
			str = getStyleSetting(IGoLauncherClassName.DEFAULT_THEME_PACKAGE, Setting.GGMENUPACKAGE);
		}

		if (null == str) {
			return IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}

		return str;
	}

	public String getIndicatorStyle() {
		String str = null;
		if (null != mThemeManager) {
			// 其他主题
			str = getStyleSetting(mThemeManager.getCurThemePackage(), Setting.INDICATOR);
		} else {
			// 默认主题
			str = getStyleSetting(IGoLauncherClassName.DEFAULT_THEME_PACKAGE, Setting.INDICATOR);
		}

		if (null == str) {
			return IGoLauncherClassName.DEFAULT_THEME_PACKAGE;
		}

		return str;
	}

	public String getStyleSetting(final String packageName, final int settingItem) {
		return GoContentProviderUtil.getScreenStyleSetting(mContext, packageName, settingItem);
	}

}
