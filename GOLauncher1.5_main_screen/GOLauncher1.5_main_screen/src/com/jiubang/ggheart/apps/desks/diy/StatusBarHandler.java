package com.jiubang.ggheart.apps.desks.diy;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.go.util.window.WindowControl;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;

/**
 * 桌面状态栏处理模块
 * 
 * @author yuankai
 * @version 1.0
 */
public class StatusBarHandler {
    /**
     * Window flag: request a translucent status bar with minimal system-provided
     * background protection.
     *
     * <p>This flag can be controlled in your theme through the
     * {@link android.R.attr#windowTranslucentStatus} attribute; this attribute
     * is automatically set for you in the standard translucent decor themes
     * such as
     * {@link android.R.style#Theme_Holo_NoActionBar_TranslucentDecor},
     * {@link android.R.style#Theme_Holo_Light_NoActionBar_TranslucentDecor},
     * {@link android.R.style#Theme_DeviceDefault_NoActionBar_TranslucentDecor}, and
     * {@link android.R.style#Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor}.</p>
     *
     * <p>When this flag is enabled for a window, it automatically sets
     * the system UI visibility flags {@link View#SYSTEM_UI_FLAG_LAYOUT_STABLE} and
     * {@link View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN}.</p>
     */
    public static final int FLAG_TRANSLUCENT_STATUS = 0x04000000; // WindowManager.FLAG_TRANSLUCENT_STATUS

    /**
     * Window flag: request a translucent navigation bar with minimal system-provided
     * background protection.
     *
     * <p>This flag can be controlled in your theme through the
     * {@link android.R.attr#windowTranslucentNavigation} attribute; this attribute
     * is automatically set for you in the standard translucent decor themes
     * such as
     * {@link android.R.style#Theme_Holo_NoActionBar_TranslucentDecor},
     * {@link android.R.style#Theme_Holo_Light_NoActionBar_TranslucentDecor},
     * {@link android.R.style#Theme_DeviceDefault_NoActionBar_TranslucentDecor}, and
     * {@link android.R.style#Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor}.</p>
     *
     * <p>When this flag is enabled for a window, it automatically sets
     * the system UI visibility flags {@link View#SYSTEM_UI_FLAG_LAYOUT_STABLE} and
     * {@link View#SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION}.</p>
     */
    public static final int FLAG_TRANSLUCENT_NAVIGATION = 0x08000000; // WindowManager.FLAG_TRANSLUCENT_NAVIGATION
    
	public final static String FIELD_FULL_SCREEN_WIDTH = "full_screen_width";
	public final static String FIELD_FULL_SCREEN_HEIGHT = "full_screen_height";
	public final static String FIELD_UPDATE_DB = "update_db";
	public final static int TYPE_FULLSCREEN_RETURN_HEIGHT = 1; // 全屏的时候才返回状态栏高度的值
	public final static int TYPE_NOT_FULLSCREEN_RETURN_HEIGHT = 2; // 非全屏的时候才返回状态栏高度的值
	public final static int STATUS_BAR_HEIGHT = ApplicationProxy.getContext().getResources()
			.getInteger(R.integer.status_bar_height);
	private static int sStatusbarHeight = -1;
	private boolean mHasFirstNotify = false; // 第一次必须发送屏幕状态
	private static Integer sTransparentValue;
	private final static int STATUS_BAR_DEFAULT_HEIGHT_DP = 25;
	private static void initStatusBarHeight() {
		Class<?> clazz = null;
		Object obj = null;
		Field field = null;
		try {
			clazz = Class.forName("com.android.internal.R$dimen");
			obj = clazz.newInstance();
			if (Machine.isMeizu()) {
				try {
					field = clazz.getField("status_bar_height_large");
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			if (field == null) {
				field = clazz.getField("status_bar_height");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (field != null && obj != null) {
			try {
				int id = Integer.parseInt(field.get(obj).toString());
				sStatusbarHeight = ApplicationProxy.getContext().getResources()
						.getDimensionPixelSize(id);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} 
//		else {
//			Rect rect = new Rect();
//			GoLauncher goLauncher = GoLauncher.getContext();
//			if (goLauncher != null) {
//				try {
//					goLauncher.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//					sStatusbarHeight = rect.top;
//				} catch (Throwable t2) {
//					sStatusbarHeight = -1;
//				}
//			}
//		}
		
		if (Machine.isTablet(ApplicationProxy.getContext())
				&& sStatusbarHeight > DrawUtils.dip2px(STATUS_BAR_DEFAULT_HEIGHT_DP)) {
			//状态栏高度大于25dp的平板，状态栏通常在下方
			sStatusbarHeight = 0;
		} else {
			if (sStatusbarHeight <= 0
					|| sStatusbarHeight > DrawUtils.dip2px(STATUS_BAR_DEFAULT_HEIGHT_DP * 2)) {
				//安卓默认状态栏高度为25dp，如果获取的状态高度大于2倍25dp的话，这个数值可能有问题，用回桌面定义的值从新获取。出现这种可能性较低，只有小部分手机出现
				if (DrawUtils.sVirtualDensity == -1) {
					sStatusbarHeight = DrawUtils.dip2px(STATUS_BAR_HEIGHT);
				} else {
					sStatusbarHeight = (int) (STATUS_BAR_HEIGHT * DrawUtils.sVirtualDensity + 0.5f);
				}
			}
		}
	}

	/**
	 * 设置全屏
	 * 
	 * @param isFullScreen
	 *            是否全屏
	 * @param updateData
	 *            是否更新数据库
	 */
	public void setFullScreen(final boolean isFullScreen, boolean updateData) {
		Activity activity = GoLauncherActivityProxy.getActivity();
		WindowControl.setIsFullScreen(activity, isFullScreen, true);

		// 获取最新的宽高
		final Rect displayRect = WindowControl.getDisplayRect(GoLauncherActivityProxy.getActivity());
		if (!isFullScreen) {
			displayRect.bottom -= DrawUtils.dip2px(STATUS_BAR_HEIGHT);
		}

		// 广播变更
		Bundle bundle = new Bundle();
		bundle.putInt(FIELD_FULL_SCREEN_WIDTH, displayRect.width());
		bundle.putInt(FIELD_FULL_SCREEN_HEIGHT, displayRect.height());
		bundle.putBoolean(FIELD_UPDATE_DB, updateData);
		MsgMgrProxy.sendBroadcast(this, IFrameworkMsgId.SYSTEM_FULL_SCREEN_CHANGE,
				isFullScreen ? 1 : 0, bundle);
		// 如果是屏幕层操作使通知栏隐藏，即不修改数据库里的数据
		// if(SensePreviewFrame.previewOperate == true){
		// SensePreviewFrame.previewOperate = false;
		// return;
		// }
		if (updateData) {
			updateHideSetting(isFullScreen);
		}
	}

	/**
	 * 设置全屏
	 * 
	 * @param isFullScreen
	 *            是否全屏
	 */
	public void setFullScreen(final boolean isFullScreen) {
		setFullScreen(isFullScreen, true);
	}

	/**
	 * 获取是否全屏
	 * 
	 * @return 是否全屏
	 */
	public boolean isFullScreen(Activity activity) {
		return WindowControl.getIsFullScreen(activity);
	}

	/**
	 * 检验设置情况
	 */
	public void checkForStatusBar() {
		setFullScreen(isHide());
		if (!mHasFirstNotify) {
			mHasFirstNotify = true;
		}
	}

	// private boolean isAutoHide()
	// {
	// return GoSettingControler.getInstance(ApplicationProxy.getContext())
	// .getDesktopSettingInfo().mAutoHideStatusbar;
	// }

	public static boolean isHide() {
		// 此处意义是反的，打钩代表不隐藏
		return !SettingProxy.getDesktopSettingInfo().mShowStatusbar;
	}

	private synchronized void updateHideSetting(boolean isHide) {
		DesktopSettingInfo info = SettingProxy.getDesktopSettingInfo();
		if (info.mShowStatusbar == isHide) {
			info.mShowStatusbar = !isHide;
			SettingProxy.updateDesktopSettingInfo(info);
		}
	}

	public static int getStatusbarHeight() {
		if (sStatusbarHeight == -1) {
			initStatusBarHeight();
		}
		return sStatusbarHeight;
	}
	
	public static int getStatusbarVisibleHeight() {
		return getStatusbarHeight(TYPE_NOT_FULLSCREEN_RETURN_HEIGHT);
	}
	
	/**
	 * 根据传入的类型判断是否需要返回状态栏的高度
	 * @param dependType {@link #TYPE_FULLSCREEN_RETURN_HEIGHT 此 type = 1，如果当前是全屏则返回状态栏的高度}
	 *                   {@link #TYPE_NOT_FULLSCREEN_RETURN_HEIGHT 此 type = 2， 如果当前是非全屏则返回状态栏的高度}
	 * @return
	 */
	public static int getStatusbarHeight(int dependType) {
		if (dependType == TYPE_FULLSCREEN_RETURN_HEIGHT) {
			return WindowControl.getIsFullScreen(GoLauncherActivityProxy.getActivity()) ? getStatusbarHeight() : 0;
		} else if (dependType == TYPE_NOT_FULLSCREEN_RETURN_HEIGHT) {
			return WindowControl.getIsFullScreen(GoLauncherActivityProxy.getActivity()) ? 0 : getStatusbarHeight();
		}
		return 0;
	}
	
	/**
	 * 透明通知栏
	 * @param window
	 * @param isTransparent
	 */
	public static void setStatusBarTransparentKitKat(Window window, boolean isTransparent) {
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			if (isTransparent) {
				window.addFlags(FLAG_TRANSLUCENT_STATUS);
			} else {
				window.clearFlags(FLAG_TRANSLUCENT_STATUS);
			}
		}
	}
	
	/**
     * <br>功能简述: 获取设置透明状态栏的system ui visibility的值， 这是部分有提供接口的rom使用的
     *
     * <br>功能详细描述:
     * <br>注意:
     * @param view 传入activity的decor view
     * @return
     */
	public static final Integer getStatusBarTransparentValue() {
		if (sTransparentValue != null) {
			return sTransparentValue;
		}
		String[] systemSharedLibraryNames = ApplicationProxy.getContext().getPackageManager()
				.getSystemSharedLibraryNames();
		String fieldName = null;
		for (String lib : systemSharedLibraryNames) {
			if ("touchwiz".equals(lib)) {
				fieldName = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
			} else if (lib.startsWith("com.sonyericsson.navigationbar")) {
				fieldName = "SYSTEM_UI_FLAG_TRANSPARENT";
			} else if (lib.startsWith("com.htc.")) {
				//TODO HTC的透明设置方式暂时没有找到，先不做
			}
		}

		if (fieldName != null) {
			try {
				Field field = View.class.getField(fieldName);
				if (field != null) {
					Class<?> type = field.getType();
					if (type == int.class) {
						int value = field.getInt(null);
						sTransparentValue = value;
					}
				}
			} catch (Exception e) {
			}
		}
		return sTransparentValue;
	}
	
	public static final boolean supportTransparentStatusBar() {
		Integer state = getStatusBarTransparentValue();
		if (state != null) {
			return true;
		}
		return false;
	}
	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static final void setStatusBarTransparent(Window window, boolean transparent) {
		int support = Machine.isSupportAPITransparentStatusBar() ? 1 : 0;
		switch (support) {
			case 1 :
				if (window == null || window.getDecorView() == null) {
					return;
				}
				Integer state = null;
				if (transparent) {
					state = getStatusBarTransparentValue();
					if (state != null) {
						window.getDecorView().setSystemUiVisibility(state);
					}
				} else {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
				}
				break;
			case 0 :
				break;
			default :
				break;
		}
	}

    
	/**
	 * 透明虚拟键
	 * @param window
	 * @param isTransparent
	 */
	public static void setNavBarTransparentKitKat(Window window, boolean isTransparent) {
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			if (isTransparent) {
				window.addFlags(FLAG_TRANSLUCENT_NAVIGATION);
			} else {
				window.clearFlags(FLAG_TRANSLUCENT_NAVIGATION);
			}
		}
	}
	
	/**
	 * 获取状态栏高度
	 * 
	 * @param activity
	 * @return 全屏时返回0，非全屏返状态栏实际高度
	 */
	public static int getStatusbarHeight(Activity activity) {
		boolean isFullScreen = WindowControl.getIsFullScreen(activity);
		if (!isFullScreen) {
			return StatusBarHandler.getStatusbarHeight();
		}
		return 0;
	}
	
	/**
	 * 获取状态栏高度
	 * 
	 * @return 全屏时返回0，否则返回状态栏实际高度(px)
	 */
	public static int getStatusbarHeightByActivity() {
		boolean isFullScreen = GoLauncherActivityProxy.isFullScreen();
		if (!isFullScreen) {
			return StatusBarHandler.getStatusbarHeight();
		}
		return 0;
	}
	
	/**
	 * 获取GOLauncher实际显示高度(px) 跟当前横竖屏状态有关
	 * 
	 * @return
	 */
	public static int getDisplayHeight() {
		int height = GoLauncherActivityProxy.getScreenHeight() - getStatusbarHeight();
		return height;
	}

	/**
	 * 获取GOLauncher实际显示宽度(px) 跟当前横竖屏状态有关
	 * 
	 * @return
	 */
	public static int getDisplayWidth() {
		return GoLauncherActivityProxy.getScreenWidth();
	}
}
