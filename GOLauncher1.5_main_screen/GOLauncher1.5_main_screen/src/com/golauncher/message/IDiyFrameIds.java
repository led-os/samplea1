package com.golauncher.message;


/**
 * 用于DIY桌面各个不同层的ID定义接口 层次之间ID不能重复，否则添加图层时会报错
 * 
 * @author yuankai
 * @version 1.0
 */
public interface IDiyFrameIds {
	
	/**
	 *无效Frame 
	 */
	public final static int INVALID_FRAME = -1;
	
	
	/**
	 * Dock条添加图标层
	 */
	public static final int DOCK_ADD_ICON_FRAME = 1;

	/**
	 * 调度层
	 */
	public final static int SCHEDULE_FRAME = 2;

	/**
	 * AppCore
	 */
	public final static int APPCORE = 3;

	/**
	 * widget主题选择层
	 */
	public final static int WIDGET_THEME_CHOOSE = 5;

	/**
	 * 桌面帮助提示层
	 */
	public final static int GUIDE_GL_FRAME = 6;

	/**
	 * 软件管理员
	 */
	public static final int APP_MANAGER = 7;

	/**
	 * 软件管理主层
	 */
	public static final int APPS_MANAGEMENT_MAIN_FRAME = 8;

	/**
	 * 应用更新管理页面
	 */
	public static final int APPS_MANAGEMENT_UPDATE_APP_FRAME = 10;

	/**
	 * GOStore层
	 */
	public static final int GO_STORE_FRAME = 11;

	/**
	 * 图片浏览层
	 */
	public final static int IMAGE_BROWSER_FRAME = 12;

	/**
	 * 应用推荐页面
	 */
	public static final int APPS_MANAGEMENT_RECOMMENDED_APP_FRAME = 13;

	/**
	 * 应用中心主页面
	 */
	public static final int APPS_MANAGEMENT_MAIN_VIEW_FRAME = 14;

	/**
	 * 通讯统计设置
	 */
	public static final int NOTIFICATION_FRAME = 16;
	
	/**
	 * 罩子层
	 */
	public static final int COVER_FRAME = 17;

	/**
	 * 资源管理MediaControler
	 */
	public static final int MEDIA_CONTROLER = 18;
	
	/**
	 * 3D插件Frame
	 */
	public static final int SHELL_FRAME = 19;
	
	/**
	 * 资源管理Frame
	 */
	public static final int MEDIA_MANAGEMENT_FRAME = 20;
	
	/**
	 * 文件夹广告推荐
	 */
	public static final int FOLDER_ADVERT = 23;
	/**
	 * 侧边栏广告
	 */
	public static final int SIDE_ADVERT = 24;
	
	/**
	 * 侧边栏广告
	 */
	public static final int SIDE_GUIDE = 25;
	
	/**
	 * FrameController
	 */
	public static final int FRAME_CONTROLLER = 27;
	
	/**
	 * GuideControler
	 */
	public static final int GUIDE_CONTROLER = 28;
	
	/**
	* MediaManagementOpenChooser
	*/
	public static final int MEDIAMANAGEMENTOPENCHOOSER_MSGHANLDER = 30;
	
	// 3D这边用到的ID，这里面和上面的部分可以合并，共用一个ID
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
	// public static final int SHELL_FRAME = -1;
	
	public static final int SCREEN = 101;
	
	public static final int DOCK = 102;
	
	public static final int APP_DRAWER = 103;

	public static final int APP_FOLDER = 104;

	public static final int SCREEN_EDIT = 105;
	
	public static final int COMMON_IMAGE_MANAGER = 106;
	
	public static final int GL_MEMORY_BAR = 107;
	
	public static final int APP_DRAWER_ALL_APP_GRID_VIEW = 108;
	
	public static final int APP_DRAWER_ALL_APP_ACTION_BAR = 109;
	
	public static final int SCREEN_PREVIEW = 110;
	
	public static final int APP_DRAWER_PREVIEW_BAR = 111;
	
	public static final int APP_DRAWER_PRO_MANAGE_GRID_VIEW = 112;
	
	public static final int APP_DRAWER_RECENT_APP_GRID_VIEW = 113;
	
	public static final int DELETE_ZONE = 114;

	public static final int APP_DRAWER_FOLDER_ACTION_BAR = 115;
	
	public static final int APP_DRAWER_PRO_MANAGE_ACTION_BAR = 116;
	
	public static final int APP_DRAWER_HIDE_APP_MANAGE = 117;
	
	public static final int APP_DRAWER_WIDGET_MANAGE = 118;
	
	public static final int APP_DRAWER_PRIME_MANAGE = 119;
	
	public static final int APP_DRAWER_ALL_APP_TOP_ACTION_BAR = 120;
	
	public static final int APP_DRAWER_SEARCH = 121;
	
	/**
	 * 桌面广告Business
	 */
	public static final int SCREEN_ADVERT_BUSINESS = 122;
	
	/**
	 * 消息控制中心
	 */
	public static final int ADVERT_CENTER = 123;
	
	/**
	 * 功能表广告业务
	 */
	public static final int APPDRAWER_ADVERT_BUSINESS = 124;
	/**
	 * 5.0桌面激活页
	 */
	public static final int ACTIVATION_FRAME = 124;
}
