package com.jiubang.ggheart.components.sidemenuadvert.widget;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

import com.jiubang.ggheart.apps.gowidget.GoWidgetConstant;
import com.jiubang.ggheart.components.sidemenuadvert.utils.SideAdvertUtils;

/**
 * 
 * @author zhangxi
 *
 */
public class SideWidgetDataInfo extends SideWidgetInfo {
	public static final int SIDEWIDGET_LOCALXML_INFO = 3;
	public static final int SIDEWIDGET_DOWNLOAD_INFO = 4;
	
	private boolean mIsInstalled; // Widget是否安装
	private String mWidgetDownLoadURL; //Widget应用下载地址
	private String mPreViewUrl; //预览图下载地址
	private String mGALink; //GA
	private int mPreViewName; //Widget浏览图在本地保持的图片名称 = mPreViewUrl.hashcode()
	
	private boolean mIsClickInSlideMenu; //是否在侧边栏小部件点击过

	public SideWidgetDataInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public int getWidgetIconIDFromPkg(Context context) {
		int iconID = 0;
		try {
			Resources resources = context.getPackageManager()
					.getResourcesForApplication(mWidgetPkgName);
			// 查找Widget的图标
			int identifier = resources.getIdentifier(
					GoWidgetConstant.WIDGET_ICON, "string", mWidgetPkgName);

			if (identifier > 0) {
				String icon = resources.getString(identifier);
				iconID = resources.getIdentifier(icon, "drawable",
						mWidgetPkgName);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iconID;
	}
	
	public void clickSelf(Context context) {
		if (!mIsInstalled) {
			//需要下载
			SideAdvertUtils.log(getTitle() + "widget点击下载");
			setIsClickInSlideMenu(true); // 记录点击
			SideAdvertUtils.downLoadWidgetApk(context, this);
		} else {
			//另外打开
		}
	}


	public boolean isIsInstalled() {
		return mIsInstalled;
	}

	public void setIsInstalled(boolean mIsInstalled) {
		this.mIsInstalled = mIsInstalled;
	}

	public String getWidgetDownLoadURL() {
		return mWidgetDownLoadURL;
	}

	public void setWidgetDownLoadURL(String mWidgetDownLoadURL) {
		this.mWidgetDownLoadURL = mWidgetDownLoadURL;
	}

	public String getPreViewUrl() {
		return mPreViewUrl;
	}

	public void setPreViewUrl(String mPreViewUrl) {
		this.mPreViewUrl = mPreViewUrl;
	}

	public String getGALink() {
		return mGALink;
	}

	public void setGALink(String mGALink) {
		this.mGALink = mGALink;
	}

	public int getPreViewName() {
		return mPreViewName;
	}

	public void setPreViewName(int mPreViewName) {
		this.mPreViewName = mPreViewName;
	}

	public boolean isIsClickInSlideMenu() {
		return mIsClickInSlideMenu;
	}

	public void setIsClickInSlideMenu(boolean mIsClickInSlideMenu) {
		this.mIsClickInSlideMenu = mIsClickInSlideMenu;
	}

}
