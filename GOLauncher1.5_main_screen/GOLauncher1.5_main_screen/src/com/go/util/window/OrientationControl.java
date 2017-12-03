package com.go.util.window;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Display;
import android.view.Surface;

import com.go.util.device.Machine;
import com.jiubang.ggheart.plugin.common.OrientationTypes;


/**
 * 旋转控制器
 * 
 * @author yuankai
 * @version 1.0
 */
public class OrientationControl {

	public static final int ORIENTATION_REVERSE_PORTRAIT = 9;
	public static final int ORIENTATION_REVERSE_LANDSCAPE = 8;

	private static boolean sSmallModle = false;
	private static boolean sPreviewModle = false;

	/**
	 * 获取当前手机屏幕方向
	 * 
	 * @param activity
	 *            活动
	 * @return ActivityInfo的常量值
	 */
	public static int getRequestOrientation(Activity activity) {
		return activity.getResources().getConfiguration().orientation;
	}

	/**
	 * 设置当前手机屏幕方向
	 * 
	 * @param activity
	 *            活动
	 * @param requestedOrientation
	 *            要求的方向
	 */
	public static void setRequestOrientation(Activity activity, int requestedOrientation) {
		activity.setRequestedOrientation(requestedOrientation);
	}

	/**
	 * 根据用户数据设置 activity屏幕方向
	 * 
	 * @param activity
	 */
	public static void setOrientation(Activity activity, int oriType) {
		setOrientation(activity, -1, oriType);
	}

	/**
	 * 设置当前手机屏幕方向
	 * 
	 * @param activity
	 *            活动
	 * @param orientationType
	 *            为-1时则读取数据库的状态
	 */
	public static void setOrientation(Activity activity, int orientationType, int oriType) {
		Configuration configuration = activity.getResources().getConfiguration();
		boolean boradHide = configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES;

		if (sSmallModle) {
			oriType = OrientationTypes.VERTICAL;
		}

		if (oriType == OrientationTypes.AUTOROTATION) {
			setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		} else if (oriType == OrientationTypes.HORIZONTAL) {
//			setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		    setSensorLandcape(activity);
		} else if (oriType == OrientationTypes.VERTICAL) {
			if (boradHide || sSmallModle) {
				setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		} else if (oriType == OrientationTypes.IGNOREBORAD) {
			// if(boradHide && Configuration.ORIENTATION_UNDEFINED !=
			// configuration.orientation){
			// setRequestOrientation(activity,
			// ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			// }
			// else
			if (/* !boradHide && */Configuration.ORIENTATION_PORTRAIT != configuration.orientation) {
				setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
	}

	
	/**
	 * <br>功能简述:当api >=9时，横屏设置会跟随重力感应旋转
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param activity
	 */
    private static void setSensorLandcape(Activity activity) {

        if (Machine.isSDKGreaterNine()) {
            setRequestOrientation(activity,
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
	
    /**
     * <br>功能简述:当跟随系统重力感应进入屏幕预览界面时，屏幕预览的方向
     * <br>功能详细描述:
     * 1.SCREEN_ORIENTATION_PORTRAIT：为竖屏
     * 2.SCREEN_ORIENTATION_LANDSCAPE：为横屏
     * 3.SCREEN_ORIENTATION_REVERSE_PORTRAIT：为反向竖屏 >= API 9
     * 4.SCREEN_ORIENTATION_REVERSE_LANDSCAPE：为反向横屏。>= API 9
     * 
     * <br>注意:getRotation()方法为 >= API 8
     * 所以调用此方法时，却保API >= 9
     * @param activity
     * @param type
     */
    @SuppressLint("NewApi")
    public static void setByRoate(Activity activity, int type) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case 0 :
                setRequestOrientation(activity,
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 1 :
                setRequestOrientation(activity,
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 2 :
                setRequestOrientation(activity,
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case 3 :
                setRequestOrientation(activity,
                        ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            default :
                setOrientation(activity, type);
                break;
        }
    }

	public static void changeOrientationByKeyboard(Activity activity, boolean keyboardOpen,
			Configuration newConfig, int oriType) {
		if (activity == null) {
			return;
		}
		if (sPreviewModle) {
			return;
		}
		//
		// // 检查屏幕翻转设置，并应用
		// boolean isGravityEnable = GoSettingControler.getInstance(ApplicationProxy.getContext())
		// .getGravitySettingInfo().mEnable;
		// // 检查屏幕翻转设置，并应用
		// int orgType = GoSettingControler.getInstance(ApplicationProxy.getContext())
		// .getGravitySettingInfo().mOrientationType;
		// if(islandscape)
		// {
		// setRequestOrientation(activity,
		// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// }
		// else
		// {
		// if(!isGravityEnable)
		// {
		// if(keyboardOpen && newConfig.orientation !=
		// Configuration.ORIENTATION_LANDSCAPE)
		// {
		// setRequestOrientation(activity,
		// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// }
		// else if(!keyboardOpen && newConfig.orientation !=
		// Configuration.ORIENTATION_PORTRAIT)
		// {
		// setRequestOrientation(activity,
		// ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// }
		// }
		// }
		
		if (sSmallModle) {
			oriType = OrientationTypes.VERTICAL;
			keyboardOpen = false;
		}
		if (oriType != OrientationTypes.AUTOROTATION) {
			if (keyboardOpen && oriType != OrientationTypes.IGNOREBORAD
					&& newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE) {
				setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else if (oriType == OrientationTypes.VERTICAL && !keyboardOpen && newConfig.orientation != oriType)// 如果原来是竖屏模式，键盘收起后恢复竖屏
			{
				setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			if (/* keyboardOpen && */oriType == OrientationTypes.IGNOREBORAD
					&& newConfig.orientation != Configuration.ORIENTATION_PORTRAIT) {
				setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			// else if(!keyboardOpen && oriType == IGNOREBORAD
			// && newConfig.orientation != Configuration.ORIENTATION_UNDEFINED)
			// {
			// setRequestOrientation(activity,
			// ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			// }
		} else if (!keyboardOpen && newConfig.orientation != Configuration.ORIENTATION_UNDEFINED) {
			setRequestOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	@SuppressLint("NewApi")
	public static int mapConfigurationOriActivityInfoOri(Activity activity, int configOri) {
		final Display d = activity.getWindowManager().getDefaultDisplay();
		int naturalOri = Configuration.ORIENTATION_LANDSCAPE;
		switch (d.getRotation()) {
			case Surface.ROTATION_0 :
			case Surface.ROTATION_180 :
				// We are currently in the same basic orientation as the natural
				// orientation
				naturalOri = configOri;
				break;
			case Surface.ROTATION_90 :
			case Surface.ROTATION_270 :
				// We are currently in the other basic orientation to the
				// natural
				// orientation
				naturalOri = (configOri == Configuration.ORIENTATION_LANDSCAPE)
						? Configuration.ORIENTATION_PORTRAIT
						: Configuration.ORIENTATION_LANDSCAPE;
				break;
		}

		int[] oriMap = { ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, ORIENTATION_REVERSE_PORTRAIT,
				ORIENTATION_REVERSE_LANDSCAPE };
		// Since the map starts at portrait, we need to offset if this device's
		// natural orientation
		// is landscape.
		int indexOffset = 0;
		if (naturalOri == Configuration.ORIENTATION_LANDSCAPE) {
			indexOffset = 1;
		}
		return oriMap[(d.getRotation() + indexOffset) % 4];
	}

	/**
	 * 设置是否为屏幕编辑态（小屏幕状态）
	 * 
	 * @param bool
	 */
	public static void setSmallModle(boolean bool) {
		sSmallModle = bool;
	}

	/**
	 * 判断是否为屏幕编辑状态
	 * 
	 * @return
	 */
	public static boolean isSmallModle() {
		return sSmallModle;
	}

	/**
	 * 保持当前屏幕状态
	 * 
	 */
	public static void keepCurrentOrientation(Activity activity) {
		// 2.2以上用这个方法
		int orientation = activity.getResources().getConfiguration().orientation;
		if (Build.VERSION.SDK_INT >= 8) {
			int mOrientation = mapConfigurationOriActivityInfoOri(activity, orientation);
			activity.setRequestedOrientation(mOrientation);
		}
		// 以下用另外的方法2.1
		else {
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
	}
	
	public static void setPreviewModel(boolean flag) {
		sPreviewModle = flag;
	}

	public static boolean isPreviewModel() {
		return sPreviewModle;
	}
}
