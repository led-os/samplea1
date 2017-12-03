package com.jiubang.ggheart.appgame.base.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.go.util.AppUtils;
import com.golauncher.utils.GoAppUtils;

/**
 * 
 * 应用游戏中心，应用安装验证器，负责验证某应用是否已经安装
 * 
 * @author  xiedezhi
 * @date  [2012-12-25]
 */
public class AppGameInstallingValidator {
	/**
	 * 单实例
	 */
	private volatile static AppGameInstallingValidator sInstance = null;

	/**
	 * 已经判断过的应用保存起来，不要每次都用PackageManager判断
	 */
	Map<String, Boolean> mInstalledMap = new HashMap<String, Boolean>();

	/**
	 * 默认构造函数
	 */
	private AppGameInstallingValidator() {
	}

	/**
	 * 获取单实例
	 */
	public static AppGameInstallingValidator getInstance() {
		if (sInstance == null) {
			synchronized (AppGameInstallingValidator.class) {
				if (sInstance == null) {
					sInstance = new AppGameInstallingValidator();
				}
			}
		}
		return sInstance;
	}

	/**
	 * 判断某应用是否已经安装
	 */
	public boolean isAppExist(Context context, String packageName) {
		if (packageName == null || context == null) {
			return false;
		}
		if (mInstalledMap.containsKey(packageName)) {
			return mInstalledMap.get(packageName);
		}
		boolean ret = GoAppUtils.isAppExist(context, packageName);
		mInstalledMap.put(packageName, ret);
		return ret;
	}
	
	/**
	 * 当收到应用安装卸载的广播时，更新当前map
	 */
	public void onAppAction(Context context, String packageName) {
		if (packageName == null || context == null) {
			return;
		}
		boolean ret = GoAppUtils.isAppExist(context, packageName);
		mInstalledMap.put(packageName, ret);
	}

	/**
	 * 判断应用是否可更新
	 */
	public boolean isCanUpdate(Context context, String pkgName, String newVerName) {
		if (pkgName == null || context == null) {
			return false;
		}
		String oldVerName = AppUtils.getVersionNameByPkgName(context, pkgName);
		return hasNewVersion(oldVerName, newVerName);
	}
	
	/**
     * 通过对比version判断是否有新版本
     *
     * @param oldVersion
     * @param newVersion
     * @return
     */
    public static boolean hasNewVersion(String oldVersion, String newVersion) {
    	if (oldVersion == null || newVersion == null) {
    		return false;
    	}
        oldVersion = oldVersion.trim();
        newVersion = newVersion.trim();
        if (oldVersion.length() <= 0 || newVersion.length() <= 0) {
            return false;
        }
        if (oldVersion.equals(newVersion)) {
            return false;
        }
        try {
            String[] oldVersionArray = oldVersion.split("\\D");
            String[] newVersionArray = newVersion.split("\\D");
            if (oldVersionArray.length <= 0 || newVersionArray.length <= 0) {
                return false;
            }
            for (int i = 0; i < oldVersionArray.length
                    && i < newVersionArray.length; i++) {
                int oldVersionnum = Integer.parseInt(oldVersionArray[i]);
                int newVersionnum = Integer.parseInt(newVersionArray[i]);
                if (newVersionnum > oldVersionnum) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
