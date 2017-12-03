package com.jiubang.ggheart.apps.desks.settings;

import android.content.res.Resources;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.util.device.ConfigurationInfo;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.data.statistics.StaticScreenSettingInfo;
/**
 * 自适应管理类
 * @author dengdazhong
 *
 */
public class AutoFitDeviceManager {
	
	public static void autoFit() {
		autoFitColumnRow();
		autoFitMargin();
	}
	
	private static void autoFitColumnRow() {
		final int deviceLevel = ConfigurationInfo.getDeviceLevel();
		// 如果是高级配置（内存比重比较大）则开启常驻内存
		if (deviceLevel == ConfigurationInfo.HIGH_DEVICE) {
			StaticScreenSettingInfo.sIsPemanentMemory = true;
		}
		// 如果是低配置（内存比重比较大）则默认屏幕为3屏
		else if (deviceLevel == ConfigurationInfo.LOW_DEVICE) {
			StaticScreenSettingInfo.sNeedDelScreen = true;
		}
		// 默认先从配置读取
		final Resources resources = ApplicationProxy.getContext().getResources();
		StaticScreenSettingInfo.sScreenRow = resources
				.getInteger(R.integer.screen_default_row);
		StaticScreenSettingInfo.sScreenCulumn = resources
				.getInteger(R.integer.screen_default_column);
		StaticScreenSettingInfo.sColRowStyle = resources
				.getInteger(R.integer.screen_col_row_style);
		StaticScreenSettingInfo.sAutofit = resources
				.getBoolean(R.bool.screen_appicon_autofit);
		int width = GoLauncherActivityProxy.isPortait() ? DrawUtils.sWidthPixels : DrawUtils.sHeightPixels;
		int height = GoLauncherActivityProxy.isPortait() ? DrawUtils.sHeightPixels : DrawUtils.sWidthPixels;
		// 再进行行列数的自适应
		final int statusBarHeight = StatusBarHandler.getStatusbarHeight();
		final int indicatorHeight_Port = DrawUtils.dip2px(21);
		final int indicatorHeight_Land = resources
				.getDimensionPixelSize(R.dimen.dots_indicator_height_land1);
		final int dockHeight = DrawUtils.dip2px(60);
		final int cellWidthPort = resources
				.getDimensionPixelSize(R.dimen.cell_width_port_auto_fit);
		final int cellHeightPort = resources
				.getDimensionPixelSize(R.dimen.cell_height_port_auto_fit);
		final float a = ((float) DrawUtils.sHeightPixels / DrawUtils.sWidthPixels) / 1.5f;
		final int textSize = DrawUtils.dip2px(12);
		final int cellHeightLandMin = (int) (resources
				.getDimensionPixelSize(R.dimen.screen_icon_large_size) + textSize * 0.5);
		final int columnPort = width / cellWidthPort;
		final int rowPort = (int) ((height - statusBarHeight
				- indicatorHeight_Port - dockHeight) / (cellHeightPort * a));
		final int columnLand = (height - dockHeight)
				/ cellWidthPort;
		final int rowLand = (width - indicatorHeight_Land - statusBarHeight)
				/ cellHeightLandMin;
		if (columnPort < columnLand) {
			StaticScreenSettingInfo.sScreenCulumn = Math.max(columnPort,
					StaticScreenSettingInfo.sScreenCulumn);
		} else {
			StaticScreenSettingInfo.sScreenCulumn = Math.max(columnLand,
					StaticScreenSettingInfo.sScreenCulumn);
		}
		if (rowPort < rowLand) {
			StaticScreenSettingInfo.sScreenRow = Math.max(rowPort,
					StaticScreenSettingInfo.sScreenRow);
		} else {
			StaticScreenSettingInfo.sScreenRow = Math.max(rowLand,
					StaticScreenSettingInfo.sScreenRow);
		}
		if (StaticScreenSettingInfo.sScreenRow < 3) {
			StaticScreenSettingInfo.sScreenRow = 3;
		} else if (StaticScreenSettingInfo.sScreenRow > 10) {
			StaticScreenSettingInfo.sScreenRow = 10;
		}
		if (StaticScreenSettingInfo.sScreenCulumn < 3) {
			StaticScreenSettingInfo.sScreenCulumn = 3;
		} else if (StaticScreenSettingInfo.sScreenCulumn > 10) {
			StaticScreenSettingInfo.sScreenCulumn = 10;
		}
		final int column = StaticScreenSettingInfo.sScreenCulumn;
		final int row = StaticScreenSettingInfo.sScreenRow;
		if (column == 4 && row == 4) {
			StaticScreenSettingInfo.sColRowStyle = 1;
		} else if (column == 5 && row == 4) {
			StaticScreenSettingInfo.sColRowStyle = 2;
		} else if (column == 5 && row == 5) {
			StaticScreenSettingInfo.sColRowStyle = 3;
		} else {
			StaticScreenSettingInfo.sColRowStyle = 4;
		}
	}
	
	private static void autoFitMargin() {
		final Resources resources = ApplicationProxy.getContext().getResources();
		boolean autoFitMargin = resources.getBoolean(R.bool.screen_autofit_margin);
		StaticScreenSettingInfo.sScreenMarginEnable = autoFitMargin;
	}
}
