package com.jiubang.ggheart.apps.gowidget;

import android.graphics.drawable.Drawable;

/**
 * 
 * @author zouguiquan
 *
 */
public class ScreenEditItemInfo {
	
	private int mId;
	private String mTitle;			// 图标名称
	private Drawable mIcon;			// 图标
	private String mPkgName;

	public int getId() {
		return mId;
	}
	public void setId(int id) {
		mId = id;
	}
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String title) {
		mTitle = title;
	}
	public Drawable getIcon() {
		return mIcon;
	}
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}
	public String getPkgName() {
		return mPkgName;
	}
	public void setPkgName(String pkgName) {
		mPkgName = pkgName;
	}
}
