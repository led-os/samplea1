package com.go.proxy;

import android.app.Application;
import android.content.Context;

/**
 * Application代理
 * 和GoLauncher无业务关系
 * @author liuheng
 *
 */
public class ApplicationProxy {
	
	private static Application sInstance;
	
	public static Application getApplication() {
		if (null != sInstance) {
			return sInstance;
		}
		
		return null;
	}
	
	public static Context getContext() {
		if (null == sInstance) {
			return null;
		}
		return sInstance.getApplicationContext();
	}

	public static void init(Application app) {
		sInstance = app;
	}
}
