/**
 * 
 */
package com.jiubang.ggheart.data.theme.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IAppDrawerMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.diy.guide.RateGuideTask;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeDetailActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemePurchaseManager;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.CouponManageActivity;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.themeicon.ThemeIconManager;

/**
 * @author ruxueqin
 * 
 */
public class MyThemeReceiver extends BroadcastReceiver {
	public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 0x00000020;
	public static final String PKGNAME_STRING = "pkgname";
	public static final String LAUNCHER_PKGNAME_STRING = "launcher_pkgname";
	public static final String THEME_PRICE_INAPPBILLING = "theme_price_inappbilling";
	public static final String THEME_TYPE = "theme_type";
	public static final String SUPPORT_COUPON = "support_coupon";
	public static final String USE_COUPON_THEME_INFO = "use_coupon_theme_info";
	//	public static final String THEME_BROADCAST_STRING = "com.gau.go.launcherex.MyThemes.mythemeaction";
	public static final String ACTION_TYPE_STRING = "type";
	public static final int CHANGE_THEME = 1;
	public static final int GOTO_DETAIL = 2;
	public static final int GOTO_COUPON = 3;
	//	public final static String ACTION_HIDE_THEME_ICON = "com.gau.go.launcherex.action.hide_theme_icon";
	/**
	 * 
	 */
	public MyThemeReceiver() {

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println("MyThemeReceiver===" + action);
		String launcherPkgName = intent.getStringExtra(LAUNCHER_PKGNAME_STRING);
		if (ICustomAction.ACTION_THEME_BROADCAST.equals(action)) {
			try {
				int type = intent.getIntExtra(ACTION_TYPE_STRING, -1);
				switch (type) {
					case CHANGE_THEME :
						//判断是否由此桌面去应用主题，不是此桌面应用主题，直接跳出不应用主题
						if (launcherPkgName != null && !launcherPkgName.equals(context.getPackageName())) {
							return;
						}
						String pkgName = intent.getStringExtra(PKGNAME_STRING);
						applyTheme(context, pkgName);
						RateGuideTask.getInstacne(context.getApplicationContext()).scheduleShowRateDialog(
								RateGuideTask.EVENT_APPLY_THEME);
						//主题切换后，取消旧的计时任务，重新开启当前主题下发计时任务,同时取消旧的通知
						ThemeIconManager.getInstance(context).postScheduleNextRequestThemeIcon(context);
						break;

					case IAppCoreMsgId.REFRESH_SCREENICON_THEME :
						MsgMgrProxy.sendBroadcastHandler(this, IAppCoreMsgId.REFRESH_SCREENICON_THEME,
								-1, null, null);
						break;
					case IAppDrawerMsgId.APPDRAWER_TAB_HOME_THEME_CHANGE :
						MsgMgrProxy.sendBroadcastHandler(this,
								IAppDrawerMsgId.APPDRAWER_TAB_HOME_THEME_CHANGE, -1, null, null);
						break;
					case IAppCoreMsgId.REFRESH_FOLDER_THEME :
						MsgMgrProxy.sendBroadcastHandler(this, IAppCoreMsgId.REFRESH_FOLDER_THEME, -1,
								null, null);
						break;
					case IAppCoreMsgId.REFRESH_SCREENINDICATOR_THEME :
						MsgMgrProxy.sendBroadcastHandler(this,
								IAppCoreMsgId.REFRESH_SCREENINDICATOR_THEME, -1, null, null);
						MsgMgrProxy.sendBroadcastHandler(this,
								IAppDrawerMsgId.APPDRAWER_INDICATOR_THEME_CHANGE, -1, null, null);
						break;
					case IAppCoreMsgId.REFRESH_GGMENU_THEME :
						MsgMgrProxy.sendBroadcastHandler(this, IAppCoreMsgId.REFRESH_GGMENU_THEME, -1,
								null, null);
						break;
					case IDockMsgId.UPDATE_DOCK_BG :
						SettingProxy.clearDockSettingInfo();
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME,
								IDockMsgId.UPDATE_DOCK_BG, -1, null, null);
						break;
					case IDockMsgId.DOCK_SETTING_CHANGED_STYLE :
						SettingProxy.clearDockSettingInfo();
						break;
					case FunAppSetting.INDEX_BGSWITCH :
						SettingProxy.getFunAppSetting()
								.resetFuncBgObserver();
						break;
					case GOTO_DETAIL :
						//判断是否由此桌面去应用主题，不是此桌面应用主题，直接跳出不应用主题
						if (launcherPkgName != null && !launcherPkgName.equals(context.getPackageName())) {
							return;
						}
						pkgName = intent.getStringExtra(PKGNAME_STRING);
						Intent it = new Intent(context, ThemeDetailActivity.class);
						it.putExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY, pkgName);
						it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(it);
						break;
					case GOTO_COUPON :
						//去优惠券页面
						if (launcherPkgName != null && !launcherPkgName.equals(context.getPackageName())) {
							return;
						}
						pkgName = intent.getStringExtra(PKGNAME_STRING);
						float price = intent.getFloatExtra(THEME_PRICE_INAPPBILLING, 1.99f);
						String themeType = intent.getStringExtra(THEME_TYPE);
						it = new Intent(context, CouponManageActivity.class);
						it.putExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY, pkgName);
						it.putExtra(THEME_PRICE_INAPPBILLING, price);
						it.putExtra(THEME_TYPE, themeType);
						it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(it);
						break;
					default :
						break;
				}
			} catch (Exception e) {

			}
		} else if (ICustomAction.ACTION_THEME_BROADCAST_SCREENEDIT // 添加界面主题设置广播
				.equals(action)) {
			try {
				int type = intent.getIntExtra(ACTION_TYPE_STRING, -1);
				switch (type) {
					case CHANGE_THEME :
						//判断是否由此桌面去应用主题，不是此桌面应用主题，直接跳出不应用主题
						if (launcherPkgName != null && !launcherPkgName.equals(context.getPackageName())) {
							return;
						}
						String pkgName = intent.getStringExtra(PKGNAME_STRING);
						applyTheme(context, pkgName);
						RateGuideTask.getInstacne(context.getApplicationContext()).scheduleShowRateDialog(
								RateGuideTask.EVENT_APPLY_THEME);
						//主题切换后，取消旧的计时任务，重新开启当前主题下发计时任务,同时取消旧的通知
						ThemeIconManager.getInstance(context).postScheduleNextRequestThemeIcon(context);
						break;
					default :
						break;
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 应用主题
	 * */
	private void applyTheme(Context context, String pkgName) {
		if (null == ApplicationProxy.getContext()
				|| null == ThemeManager.getInstance(ApplicationProxy.getContext())) {
			return;
		}

		ThemeManager tmg = ThemeManager.getInstance(ApplicationProxy.getContext());
		String curThemePackage = tmg.getCurThemePackage();
		if (pkgName != null && pkgName.equals(curThemePackage)) {
			// 如果当前已经应用该主题，则提示
			// Toast.makeText(GoLauncher.getContext(),R.string.theme_already_using,
			// Toast.LENGTH_SHORT).show();
		} else if (!ThemeManager.isInstalledTheme(ApplicationProxy.getContext(), pkgName)) {
			// new add
			Toast.makeText(GoLauncherActivityProxy.getActivity(), "Theme is not installed on your phone",
					Toast.LENGTH_SHORT).show();
		} else {
			tmg.applyThemePackage(pkgName, true);
			Intent intent = new Intent(ICustomAction.ACTION_HIDE_THEME_ICON);
			int level = ThemePurchaseManager.getCustomerLevel(context);
			intent.putExtra("viplevel", level);
			intent.putExtra(PKGNAME_STRING, pkgName);
			intent.putExtra(LAUNCHER_PKGNAME_STRING, context.getPackageName());
			if (Machine.IS_HONEYCOMB_MR1) {
				// 3.1之后，系统的package manager增加了对处于“stopped state”应用的管理
				intent.setFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
			}
			context.sendBroadcast(intent);
			
			//让主题显示全屏广告
			intent = new Intent(ICustomAction.ACTION_THEME_SHOW_FULLSCREEN_AD);
			intent.putExtra(PKGNAME_STRING, pkgName);
			if (Machine.IS_HONEYCOMB_MR1) {
				// 3.1之后，系统的package manager增加了对处于“stopped state”应用的管理
				intent.setFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
			}
			context.sendBroadcast(intent);
		}
	}
}
