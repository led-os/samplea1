package com.jiubang.ggheart.apps.gowidget;

import android.appwidget.AppWidgetProviderInfo;

/**
 * 
 * @author zouguiquan
 *
 */
public abstract class BaseWidgetInfo {
	
	protected AppWidgetProviderInfo mProvider;

	public abstract void setPkgName(String pkgName);
	
	public abstract String getPkgName();
	
	public abstract String getIconPath();
	
	public abstract String getTitle();
	
	public AppWidgetProviderInfo getProviderInfo() {
		return mProvider;
	}
	
	public void setProviderInfo(AppWidgetProviderInfo providerInfo) {
		mProvider = providerInfo;
	}
}
