package com.jiubang.ggheart.common.controler;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
/**
 * 
 * <br>类描述:屏幕开关的广播接收者
 * <br>功能详细描述:
 * 
 * @author  wuziyi
 * @date  [2012-9-21]
 */
public class ScreenOnOffReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == intent)
		{
			return;
		}
		String action = intent.getAction();
		if (null == action)
		{
			return;
		}
		CommonControler controler = CommonControler.getInstance(context);
		if (action.equals(Intent.ACTION_SCREEN_ON))
		{
			primeKeyDaemon(context);
			//开启栈顶包名扫描方法
//			if (controler != null) {
//				controler.startMonitor();
//			}
		}
		else if (action.equals(Intent.ACTION_SCREEN_OFF))
		{
			//停止使用栈顶包名扫描方法
//			if (controler != null) {
//				controler.stopMonitor();
//			}
		} else if (action.equals(ICustomAction.ACTION_LAUNCHER_START)) {
			//停止使用栈顶包名扫描方法
//			if (controler != null) {
//				controler.stopMonitor();
//			}
		}
		else if (action.equals(ICustomAction.ACTION_LAUNCHER_EXIT)) {
			//开启栈顶包名扫描方法
//			if (controler != null) {
//				controler.startMonitor();
//			}
		}
		else if (action.equals(ICustomAction.ACTION_LAUNCHER_STOP)) {
			//开启栈顶包名扫描方法
			if (controler != null) {
				controler.startLockAppMonitor();
			}
		}
		else if (action.equals(ICustomAction.ACTION_LAUNCHER_ONSTART)) {
			//停止使用栈顶包名扫描方法
//			if (controler != null) {
//				controler.stopMonitor();
//			}
		}
		else if (action.equals(ICustomAction.ACTION_LAUNCHER_RESETDEFAULT)) {
			//主包恢复默认广播（需要清理所有勾选）TODO://清理所有未读数据
//			if (controler != null) {
//				controler.clearAllMonitorApps();
//			}
		}
	}
	
	/**
	 * 守护key包的天使
	 * @param context
	 */
	private void primeKeyDaemon(Context context) {
		// key包异常退出后，亮屏重新唤起
		if (SettingProxy.getDesktopSettingInfo().mEnableSideDock) {

			Intent serviceIntent = new Intent();
			ComponentName com = null;
			String pkgName = null;
			String clsName = "com.jiubang.plugin.controller.AppService";
			if (GoAppUtils.isAppExist(context,
					LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
				pkgName = LauncherEnv.Plugin.PRIME_GETJAR_KEY;

			} else {
				pkgName = PackageName.PRIME_KEY;
			}
			if (!AppUtils.isServiceRunning(context, pkgName, clsName)) {
				com = new ComponentName(pkgName, clsName);
				serviceIntent.setComponent(com);
				context.startService(serviceIntent);
			}
		}
	}
}
