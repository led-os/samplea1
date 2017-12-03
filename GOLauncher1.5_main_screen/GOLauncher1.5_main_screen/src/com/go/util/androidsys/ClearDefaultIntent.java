package com.go.util.androidsys;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import com.go.commomidentify.IGoLauncherClassName;
import com.go.util.AppUtils;
import com.go.util.ClL;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * clear default intent
 * @author liuheng
 *
 */
public class ClearDefaultIntent {
	/**
	 * clear桌面
	 */
	public static boolean clDH(Context context) {
		try {
			final String packageStr = AppUtils.getDefaultLauncher(context);

			if ("com.yulong.android.launcher3".equals(packageStr) && Build.BRAND.toLowerCase().contains("coolpad") 
				||	("com.miui.home".equals(packageStr) && Build.BRAND.toLowerCase().contains("xiaomi"))) {
				//coolpad手机，并设内置桌面为默认桌面，清除不了默认
				return false;
			}
			
			// 没有设置
			// 设置为Go桌面
			// 设置为GO锁屏
			if (null == packageStr || packageStr.equals(PackageName.PACKAGE_NAME)
					|| packageStr.equals(PackageName.GO_LOCK_PACKAGE_NAME)
					|| packageStr.equals("com.eikatou0.appspot.home2shortcut")
					|| packageStr.equals("sg.ruqqq.quickdesk")
					|| packageStr.equals("com.shisho.taskswitcher")
					|| packageStr.equals("com.smart.taskswitcher")
					|| packageStr.equals("org.rabold.android.taskswitcher")
					|| packageStr.equals("com.esdmobile.taskswitcher")
					|| packageStr.equals("com.tkdtnek23.app.multitaskinglite")
					|| packageStr.equals("go.launcher.theme.KissMe")
					|| packageStr.equals("com.gau.go.launcherex.theme.xiaowanzi")
					|| packageStr.equals("com.gau.go.launcherex.theme.valenmm")
					|| packageStr.equals("ccom.gau.go.launcherex.theme.love")
					|| packageStr.equals("com.gau.go.launcherex.theme.KissMe")
					|| packageStr.equals("com.gau.go.launcherex.theme.autumn")
					|| packageStr.equals("com.gau.go.launcherex.theme.kissintnerain")
					|| packageStr.equals("com.gau.go.launcherex.theme.glow")
					|| packageStr.equals("com.gau.go.launcherex.theme.retropatterns")
					|| packageStr.equals("com.gau.go.launcherex.zh")
					|| packageStr.equals(PackageName.GOLAUNKER_PACKAGE_NAME)
					|| packageStr.equals(PackageName.PACKAGE_NAME_NEXTLAUNCHER)
					|| packageStr.equals("tw.kewang.padfonelauncherswitcher")
					|| packageStr.equals("com.nextlauncher.defaultlauncherpatch")) {
				return false;
			}
			PackageManager localPackageManager = context.getPackageManager();

			ComponentName component = new ComponentName(context, ClL.class);
			localPackageManager.setComponentEnabledSetting(component, 1, 1);

			Intent intent = new Intent(ICustomAction.ACTION_MAIN);
			intent.addCategory("android.intent.category.HOME");
			context.startActivity(intent);

			localPackageManager.setComponentEnabledSetting(component, 0, 1);

			Intent localIntent3 = new Intent();
			localIntent3.setClassName(context, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
			context.startActivity(localIntent3);
			return true;
		} catch (Throwable e) {
			// e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 清理当前程序的默认属性
	 * @param context
	 */
	public static void clearCurrentPkgDefault(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			pm.clearPackagePreferredActivities(context.getPackageName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
