package com.jiubang.ggheart.uninstallcheck;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.go.util.AppUtils;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.shell.ShellPluginFactory;

/**
 * 
 * @author hanson
 * uninstall business logic
 */
public class UninstallBussinessCheck {

	private static final String LOG_TAG = "UninstallBussinessCheck";
	
	
	/**
	 * start uninstall service
	 * 
	 * @author hanson
	 */
	private static void startUninstallService(Context context) {
		try {
			Intent intent = new Intent();
			intent.setClass(context.getApplicationContext(), UninstallService.class);
			Log.d(LOG_TAG, "Before start Uninstall Service");
			context.startService(intent);
			Log.d(LOG_TAG, "End start Uninstall AppService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*****************************************************后续增加推荐，从此处修改
	/**
	 * 推荐next桌面的入口
	 * @param context
	 */
	public static void recommendNextLauncher(Context context) {
		//not need to start the uninstall service if next launcher exists
		//as uninstallcheck native lib is loaded from static block, we need to check the condition in this class from method 'isNeedToRecommendNextLauncher'
		//else the static will be called before invoked method
		 
		if (isNeedToRecommendNextLauncher(context)) {
			if (UninstallCheck.checkLockFile(context.getApplicationContext()) == 0) {
				startUninstallService(context);
			} else {
				Log.d(LOG_TAG, "check lock file locked");
			}
		}
	}
	
	/**
	 * 业务判断是否需要推荐nextluancher。现有条件4个：
	 * 1.373渠道；
	 * 2.sdk >= 8；
	 * 3.未安装nextLauncher；
	 * 4.支持3D引擎；
	 * @param context
	 * @return
	 */
	private static boolean isNeedToRecommendNextLauncher(Context context) {
		boolean needToRecommend = false;
		boolean isSupportShellEngin = ShellPluginFactory.isSupportShellPlugin(context);
		if (isSupportShellEngin) {
			//next Launcher未安装
			boolean isNotExitNextLauncher = !(AppUtils.isAppExist(context, PackageName.PACKAGE_NAME_NEXTLAUNCHER)
					|| AppUtils.isAppExist(context, PackageName.PACKAGE_NAME_NEXTLAUNCHER_TRIAL));
			if (isNotExitNextLauncher) {
//				String uid = GoStorePhoneStateUtil.getUid(context);
//				uid.equals("373");   	//支持的渠道目前仅在373实现
				needToRecommend = true; //V4.15开始，全渠道推广next桌面。
			}
		}
		Log.i(LOG_TAG, "isNeedToRecommendNextLauncher = " + ", " + needToRecommend + ", " + isSupportShellEngin);
		return needToRecommend;
	}
	
}
