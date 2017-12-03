package com.jiubang.ggheart.plugin.theme;

import java.util.ArrayList;

import android.util.Log;

import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.plugin.common.AppInfoBean;
import com.jiubang.ggheart.plugin.shell.IOrientationControler;
import com.jiubang.ggheart.plugin.shell.ShellPluginFactory;
import com.jiubang.ggheart.plugin.theme.inf.IThemeLauncherProxy;

/**
 * 
 * @author yangguanxiang
 *
 */
public class ThemeLauncherProxy implements IThemeLauncherProxy {

	@Override
	public boolean action(int actionId, int param, Object... objects) {
		switch (actionId) {
			case ActionIds.STATUS_BAR_ACTION :
				Log.i("Test", "status bar action: " + param);
				onStatusBarAction(param == 1 ? true : false);
				break;
			case ActionIds.ORIENTATION_ACTION :
				Log.i("Test", "orientation action: " + param);
				onOrientationAction(param);
				break;
			case ActionIds.RESET_ORIENTATION :
				Log.i("Test", "reset orientation");
				resetOrientationAction();
				break;
			case ActionIds.RETRIEVE_APPLICATION_INFOS :
				Log.i("Test", "retrieve app infos");
				retrieveAppInfos((ArrayList<AppInfoBean>) objects[0]);
				break;
			case ActionIds.SHOW_APPDRAWER :
				Log.i("Test", "show appdrawer");
				showAppDrawer();
				break;
			default :
				break;
		}
		return false;
	}

	private void onStatusBarAction(boolean show) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.SHOW_HIDE_STATUSBAR,
				-2, show, null);
	}

	private void onOrientationAction(int type) {
		IOrientationControler controler = ShellPluginFactory.getShellManager()
				.getGLOrientationControler();
		controler.keepOrientationAllTheTime(true, type);
	}

	private void resetOrientationAction() {
		int type = SettingProxy.getGravitySettingInfo().mOrientationType;
		IOrientationControler controler = ShellPluginFactory.getShellManager()
					.getGLOrientationControler();
		controler.keepOrientationAllTheTime(false, type);
	}

	private void retrieveAppInfos(ArrayList<AppInfoBean> infoList) {
		AppDataEngine engine = AppDataEngine.getInstance(ApplicationProxy.getContext());
		ArrayList<AppItemInfo> infos = engine.getAllAppItemInfos();
		if (infos != null && infoList != null) {
			infoList.clear();
			for (AppItemInfo appItemInfo : infos) {
				AppInfoBean bean = new AppInfoBean();
				bean.setIntent(appItemInfo.mIntent);
				bean.setIcon(appItemInfo.mIcon);
				bean.setTitle(appItemInfo.mTitle);
				infoList.add(bean);
			}
		}
	}

	private void showAppDrawer() {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.SHOW_APP_DRAWER, -1,
				null, null);
	}

}
