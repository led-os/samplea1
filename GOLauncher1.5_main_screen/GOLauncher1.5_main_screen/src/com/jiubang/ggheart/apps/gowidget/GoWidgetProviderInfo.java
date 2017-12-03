package com.jiubang.ggheart.apps.gowidget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;

/**
 * 非内置GoWidget信息
 * @author zouguiquan
 *
 */
public class GoWidgetProviderInfo extends BaseWidgetInfo {

	public String mDownloadUrl; 		// 下载地址
	public String mIconPath; 			// 图标地址  网络url 本地local 系统小部件system
	public String mVersionName; 		// 版本名称
	public String mGoWidgetPkgName; 	// gowidget包名
	public String mGALink; 				// GA链接
	public int mVersionCode; 			// 版本号
	public boolean mInstalled;

	public GoWidgetProviderInfo() {
		mProvider = new AppWidgetProviderInfo();
	}

	public GoWidgetProviderInfo(String packageName, String className) {
		mProvider = new AppWidgetProviderInfo();
		mProvider.provider = new ComponentName(packageName, className);
	}

	@Override
	public String getPkgName() {
		if (mProvider != null && mProvider.provider != null) {
			return mProvider.provider.getPackageName();
		}
		return null;
	}

	@Override
	public void setPkgName(String pkgName) {
	}

	@Override
	public String getIconPath() {
		return mIconPath;
	}

	@Override
	public String getTitle() {
		if (mProvider != null) {
			return mProvider.label;
		}
		return null;
	}
}
