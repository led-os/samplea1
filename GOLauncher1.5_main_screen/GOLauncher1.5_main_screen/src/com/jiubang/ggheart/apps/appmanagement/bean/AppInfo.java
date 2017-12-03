package com.jiubang.ggheart.apps.appmanagement.bean;

import java.io.File;

import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;

import com.go.util.sort.ICreatTimeCompareable;
import com.go.util.sort.IPkgSizeCompareable;

/**
 * 软件管理
 * 
 * @author zhoujun
 * 
 */
public class AppInfo implements ICreatTimeCompareable , IPkgSizeCompareable{
	public int mType; // 我的应用列表中，判断是否是group
	public long mAppSize; // 应用程序的大小
	public String mPackageName; // 应用程序包名
	public boolean mIsInternal; // 是否安装在手机内存
	public String mTitle = null; // 部件名称
	public BitmapDrawable mIcon = null;
	public int mLocation = -1; // 可存储位置
	public long mFirstInstallTime; // 安装时间
	public boolean mIsSelected = false; // 是否被选中（批量卸载中用到）

	public void setAppInfo(String filePath, String internalPath) {

		if (filePath != null && filePath.length() > 0) {
			mIsInternal = filePath.startsWith(internalPath);
			File file = new File(filePath);
			if (file.exists()) {
				mAppSize = file.length();
				mFirstInstallTime = file.lastModified();
			}

		}
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public final String getTitle() {
		return mTitle;
	}

	public final long getSize() {
		return mAppSize;
	}
	
	/**
	 * 获取安装时间
	 * @return
	 */
	public final long getTime() {
		return mFirstInstallTime;
	}

	@Override
	public long getTime(PackageManager pm) {
		return mFirstInstallTime;
	}
}
