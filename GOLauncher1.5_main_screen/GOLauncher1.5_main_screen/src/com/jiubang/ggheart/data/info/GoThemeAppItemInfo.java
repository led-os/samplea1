package com.jiubang.ggheart.data.info;

import android.app.Activity;
import android.content.Intent;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeManageActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeManageView;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.notification.NotificationControler;

/**
 * 主题管理假图标info
 * @author yangguanxiang
 *
 */
public class GoThemeAppItemInfo extends SpecialAppItemInfo {

	@Override
	public boolean doInvoke(int from) {
		Activity activity = GoLauncherActivityProxy.getActivity();
		NotificationControler control = AppCore.getInstance()
				.getNotificationControler();
		control.updataGoTheme(ThemeConstants.LAUNCHER_FEATURED_THEME_ID, 0);
		Intent mythemesIntent = new Intent();
		mythemesIntent.putExtra("entrance",
				ThemeManageView.LAUNCHER_THEME_VIEW_ID);
		mythemesIntent.setClass(activity, ThemeManageActivity.class);
		activity.startActivity(mythemesIntent);
		// GuiThemeStatistics.setCurrentEntry(GuiThemeStatistics.ENTRY_DESK_ICON,
		// mActivity);
		GuiThemeStatistics.guiStaticData("",
				GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
				String.valueOf(GuiThemeStatistics.ENTRY_DESK_ICON), "", "", "");
		return true;
	}

	@Override
	public int getDefaultIconResId() {
		return R.drawable.theme;
	}

	@Override
	public int getDefaultTitleResId() {
		return R.string.go_theme;
	}

	@Override
	public String getPackageName() {
		return PackageName.GO_THEME_PACKAGE_NAME;
	}

	@Override
	public String getAction() {
		return ICustomAction.ACTION_FUNC_SPECIAL_APP_GOTHEME;
	}


}
