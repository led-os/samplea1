package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.bean;

import android.app.WallpaperInfo;

/**
 * 屏幕添加层推送实体类
 * @author zouguiquan
 *
 */
public class PushInfo {

	private String mAppId;
	private String mName;
	private String mPackageName;
	private String mIconDownloadPath;
	private int mIconResPath;
	private String mDownloadurl;
	private String mSummary;
	private String mSize;
	private String mUpdatetimedesc;
	private boolean mHasInstall;
	private WallpaperInfo mWallpaperInfo;

	public String getAppId() {
		return mAppId;
	}
	public void setAppId(String appId) {
		mAppId = appId;
	}
	public WallpaperInfo getWallpaperInfo() {
		return mWallpaperInfo;
	}
	public void setWallpaperInfo(WallpaperInfo wallpaperInfo) {
		this.mWallpaperInfo = wallpaperInfo;
	}
	public boolean isHasInstall() {
		return mHasInstall;
	}
	public void setHasInstall(boolean hasInstall) {
		this.mHasInstall = hasInstall;
	}
	public String getIconDownloadPath() {
		return mIconDownloadPath;
	}
	public void setIconDownloadPath(String iconDownloadPath) {
		this.mIconDownloadPath = iconDownloadPath;
	}
	public int getIconResPath() {
		return mIconResPath;
	}
	public void setIconResPath(int iconResPath) {
		this.mIconResPath = iconResPath;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public String getPackageName() {
		return mPackageName;
	}
	public void setPackageName(String packageName) {
		this.mPackageName = packageName;
	}

	public String getDownloadurl() {
		return mDownloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.mDownloadurl = downloadurl;
	}
	public String getSummary() {
		return mSummary;
	}
	public void setSummary(String summary) {
		this.mSummary = summary;
	}
	public String getSize() {
		return mSize;
	}
	public void setSize(String size) {
		this.mSize = size;
	}
	public String getUpdatetimedesc() {
		return mUpdatetimedesc;
	}
	public void setUpdatetimedesc(String updatetimedesc) {
		this.mUpdatetimedesc = updatetimedesc;
	}
}
