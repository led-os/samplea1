package com.jiubang.ggheart.components.sidemenuadvert.tools;

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
public class SideToolsDataInfo extends SideToolsInfo {
	public static final int SIDEWIDGET_LOCALXML_INFO = 3;
	public static final int SIDEWIDGET_DOWNLOAD_INFO = 4;
	
	private boolean mIsInstalled; // Tools是否安装
	private String mToolsDownLoadURL; //Tools应用下载地址
	private String mIconUrl; //预览图下载地址
	private String mGALink; //GA
	private int mIconFileName; //Widget浏览图在本地保持的图片名称 = mIconUrl.hashcode()
	
	private boolean mIsClickInSlideMenu; //是否在侧边栏小部件点击过

	public SideToolsDataInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public int getWidgetIconIDFromPkg(Context context) {
		int iconID = 0;
		try {
			Resources resources = context.getPackageManager()
					.getResourcesForApplication(mToolsPkgName);
			// 查找Widget的图标
			int identifier = resources.getIdentifier(
					GoWidgetConstant.WIDGET_ICON, "string", mToolsPkgName);

			if (identifier > 0) {
				String icon = resources.getString(identifier);
				iconID = resources.getIdentifier(icon, "drawable",
						mToolsPkgName);
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
			SideAdvertUtils.log(getTitle() + "Tools点击下载");
			setIsClickInSlideMenu(true); // 记录点击
//			SideAdvertUtils.downLoadToolsApk(context, this);
		} else {
			//另外打开
		}
	}


	public boolean isInstalled() {
		return mIsInstalled;
	}

	public void setIsInstalled(boolean mIsInstalled) {
		this.mIsInstalled = mIsInstalled;
	}

	public String getToolsDownLoadURL() {
		return mToolsDownLoadURL;
	}

	public void setToolsDownLoadURL(String mToolsDownLoadURL) {
		this.mToolsDownLoadURL = mToolsDownLoadURL;
	}

	public String getIconUrl() {
		return mIconUrl;
	}

	public void setIconUrl(String iconUrl) {
		mIconFileName = iconUrl.hashCode();
		this.mIconUrl = iconUrl;
	}

	public String getGALink() {
		return mGALink;
	}

	public void setGALink(String mGALink) {
		this.mGALink = mGALink;
	}

	public int getIconFileName() {
		return mIconFileName;
	}
//
//	public void setPreViewName(int mPreViewName) {
//		this.mPreViewName = mPreViewName;
//	}

	public boolean isIsClickInSlideMenu() {
		return mIsClickInSlideMenu;
	}

	public void setIsClickInSlideMenu(boolean mIsClickInSlideMenu) {
		this.mIsClickInSlideMenu = mIsClickInSlideMenu;
	}

}
