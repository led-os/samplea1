package com.golauncher.message;

/**
 * 
 * @author liuheng
 * 
 */
public interface IMediaManagementMsgId {
	// LH TODO:add comment
	public static final int BASE_ID = 39000;
	public static final int SWITCH_MEDIA_MANAGEMENT_CONTENT = 39001;
	public static final int OPEN_IMAGE_BROWSER = 39002;
	public static final int MEDIA_MANAGEMENT_3D_DRAWER_BG = 39003;
	public static final int MEDIA_MANAGEMENT_PROGRESSBAR_SHOW = 39004;
	public static final int MEDIA_MANAGEMENT_PROGRESSBAR_HIDE = 39005;
	public static final int MEDIA_MANAGEMENT_BACK_TO_MAIN_SCREEN = 39006;
	public static final int MEDIA_MANAGEMENT_BACK_FROM_IMAGE_BROWSER = 39007;
	public static final int MEDIA_MANAGEMENT_FRAME_HIDE = 39008;
	public static final int MEDIA_MANAGEMENT_BACK_FROM_MUSIC_PALYER = 39009;
	/**
	 * 通知打开多媒体文件（图片、音乐、视频）
	 */
	public final static int OPEN_MEIDA_FILE = 39010;
	/**
	 * 定位多媒体文件
	 */
	public final static int LOCATE_MEDIA_ITEM = 39011;
	
	public final static int SET_IMAGE_BROWSER_DATA = 39012;
	
	/**
	 * 显示资源默认打开方式设置界面
	 */
	public final static int SHOW_MEDIA_OPEN_SETTING_ACTIVITY = 39013;
}
