package com.jiubang.ggheart.apps.desks.diy;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.util.Log;
import android.view.Display;

import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.go.util.log.LogConstants;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  lichong
 * @date  [2014年3月3日]
 */
public class WallpaperDensityUtil {
	static final int WALLPAPER_SCREENS_SPAN_WIDTH_1 = 1;
	static final int WALLPAPER_SCREENS_SPAN_WIDTH_2 = 2;
	
	static final int HW_D2_0082_SCREEN_HEIGHT = 1920;

	/**
	 * 试图解决壁纸缩放问题 说明：如果是关了“壁纸滚动”设置且为竖屏,就单屏壁纸，其他情况为双屏壁纸
	 */
	public static void setWallpaperDimension(Activity activity) {
		if (null == activity) {
			return;
		}
		boolean isWallpaperScroll = SettingProxy.getScreenSettingInfo().mWallpaperScroll;
		WallpaperManager wpm = (WallpaperManager) activity
				.getSystemService(Context.WALLPAPER_SERVICE);

		Display display = activity.getWindowManager().getDefaultDisplay();
		boolean isPortrait = display.getWidth() < display.getHeight();

		int width = isPortrait ? display.getWidth() : display.getHeight();
		int height = DrawUtils.sStatusHeight
				+ (isPortrait ? display.getHeight() : display.getWidth());
		final int wallpaperSpan = (!isWallpaperScroll && isPortrait)
				? WALLPAPER_SCREENS_SPAN_WIDTH_1
				: WALLPAPER_SCREENS_SPAN_WIDTH_2;
		/**
		 * 问题：ADT-16275 华为D2恢复默认导致的壁纸问题
		 * 原因：华为D2机型，虚拟菜单键可以手动隐藏和显示，影响桌面初始化时候屏幕高度计算
		 * 修改：考虑到对虚拟菜单键做个性化定制的机型比较少见，现针对D2机型，壁纸高度初始化做特殊处理
		 */
		if (Machine.isHW_D2_0082()) {
			height = HW_D2_0082_SCREEN_HEIGHT;
		}
		try {
			wpm.suggestDesiredDimensions(width * wallpaperSpan, height);
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "fail to setWallpaperDimension");
		}
	}
}
