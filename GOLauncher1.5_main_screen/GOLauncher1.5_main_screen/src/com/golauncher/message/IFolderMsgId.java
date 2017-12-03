package com.golauncher.message;

public interface IFolderMsgId {
	public final static int BASE_ID = 45000;

	public static final int FOLDER_RELAYOUT = 45001;

	public static final int ON_FOLDER_DROP_COMPELETE = 45002;

	/**
	 * 通知文件夹本不要自动关闭
	 */
	public static final int FOLDER_KEEP_OPEN = 45003;

	/**
	 * 文件夹定位图标
	 */
	public static final int FOLDER_LOCATE_APP = 45004;

	public static final int FOLDER_APP_LESS_TWO = 45005;

	/**
	 * 处理甩动桌面文件夹内图标做飞出动画后的处理
	 */
	public static final int SCREEN_FOLDER_ON_DRAG_FLING = 45006;
	
	public static final int FOLDER_RESET_DEFAULT = 45007;
	
	public static final int MODIFY_FOLDER_COMPELETE = 45008;
	
	public static final int FOLDER_SD_EVENT_REMOVE_ITEMS = 45009;
}
