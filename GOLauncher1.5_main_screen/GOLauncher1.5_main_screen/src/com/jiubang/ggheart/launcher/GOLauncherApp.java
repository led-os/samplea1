package com.jiubang.ggheart.launcher;

import java.util.Locale;

import org.acra.CrashReport;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.components.DeskResources;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;

/**
 * 
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 */
public class GOLauncherApp extends Application {
	
	// 解决电子市场上no empty constructor错误
	public GOLauncherApp() {
		super();
		// 初始化MsgMgr
		MsgMgrProxy.getMsgMgr();
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		SettingProxy setting = SettingProxy.getInstance(this);
		// setting.doBindService();
		
		ApplicationProxy.init(this);
		
		// 初始化统计
		StatisticsManager.getInstance(this).enableSoftManager();
		
		DrawUtils.resetDensity(this);
		
		CrashReport crashReport = new CrashReport();
		crashReport.start(this);
//		ShellUtil.setOnNativeCrashedHandler(ErrorReporter.getInstance());
	}

	@Override
	public Resources getResources() {
		DeskResourcesConfiguration configuration = DeskResourcesConfiguration
				.getInstance();
		if (null != configuration) {
			Resources resources = configuration.getDeskResources();
			if (null != resources) {
				return resources;
			}
		}

		return super.getResources();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		try {
			Resources res = getResources();
			if (res instanceof DeskResources) {
				res.updateConfiguration(
						super.getResources().getConfiguration(), super
								.getResources().getDisplayMetrics());
			}
			// 获得设置对象
			Configuration config = res.getConfiguration();
			// 获得屏幕参数：主要是分辨率，像素等。
			DisplayMetrics dm = res.getDisplayMetrics();
			PreferencesManager preferences = new PreferencesManager(this,
					IPreferencesIds.DESK_SHAREPREFERENCES_FILE,
					Context.MODE_PRIVATE);
			String currentlanguage = preferences.getString(
					IPreferencesIds.CURRENT_SELECTED_LANGUAGE, "");
			if (currentlanguage != null && !currentlanguage.equals("")) {
				if (currentlanguage.length() == 5) {
					String language = currentlanguage.substring(0, 2);
					String country = currentlanguage.substring(3, 5);
					config.locale = new Locale(language, country);
				} else {
					config.locale = new Locale(currentlanguage);
				}
				res.updateConfiguration(config, dm);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	//
	// private NotificationControler mNotificationControler = null;

	// private NotificationControler getNotificationControler() {
	// if (null == mNotificationControler) {
	// mNotificationControler = new NotificationControler(mContext,
	// getAppDataEngine());
	// }
	//
	// return mNotificationControler;
	// }

	/**
	 * <br>
	 * 功能简述: 退出桌面的时候，清空付费的管理类 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
//	private void destoryAppInBilling() {
//		AppInBillingManager instance = AppInBillingManager.getInstance();
//		if (instance != null) {
//			instance.destory();
//		}
//	}

}
