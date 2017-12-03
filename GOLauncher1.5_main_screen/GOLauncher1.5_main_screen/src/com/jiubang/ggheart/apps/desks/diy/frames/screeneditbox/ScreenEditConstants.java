package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox;

/**
 * 
 * @author zouguiquan
 *
 */
public interface ScreenEditConstants {
	
	public static final long DURATION_TAB_ENTER = 450;			//添加层显示时，动画的时间
	public static final long DURATION_TAB_CHANGE = 350;			//两个tab跳转动画的时间
	public static final long DURATION_TITLE_COMPONENT = 300;	
	public static final long DURATION_EDIT_EXIT = 300;
	
	public static final int ANIMATION_TAG_CHANGE_TAB = 10;		
	
	public static final int TAB_LEVEL_1 = 1; 					// Tab一级
	public static final int TAB_LEVEL_2 = 2; 					// Tab二级
	public static final int TAB_LEVEL_3 = 3; 					// Tab三级
	
	public static final int CLICK_TAB_ADD = 1; 		   			// 点击应用程序tab
	public static final int CLICK_TAB_FOLDER = 2; 				// 点击文件夹tab
	public static final int CLICK_TAB_GO_WIDGET = 3; 			// 点击go小部件
	public static final int CLICK_TAB_SYSTEM_WIDGET = 4; 		// 点击系统小部件
	public static final int CLICK_TAB_SHORTCUT = 5; 			// 点击快捷方式tab
	public static final int CLICK_TAB_GO_SHORTCUT = 6; 			// 点击Go桌面快捷方式tab
	
	public static final int TAB_ID_MAIN = 7; 					// 添加主界面tab
	public static final int TAB_ID_APPS = 8; 					// 添加应用程序
	public static final int TAB_ID_FOLDER = 9; 					// 添加文件夹
	public static final int TAB_ID_GOWIDGET = 10; 				// 添加GO小部件一级
	public static final int TAB_ID_SUB_GOWIDGET = 11; 			// 添加GO小部件二级
	public static final int TAB_ID_SYSTEMWIDGET = 12; 			// 添加系统小部件一级
	public static final int TAB_ID_SUB_SYSTEMWIDGET = 13; 		// 添加系统小部件二级
	public static final int TAB_ID_SHORTCUT = 14; 				// 添加系统快捷方式
	public static final int TAB_ID_GOSHORTCUT = 15; 			// 添加系GO捷方式
	public static final int TAB_ID_WALLPAPER = 16; 				// 修改壁纸
	public static final int TAB_ID_GOWALLPAPER = 17; 			// 设置GO壁纸
	public static final int TAB_ID_GODYNAMICA_WALLPAPER = 18; 	// 设置GO动态壁纸
	public static final int TAB_ID_EFFECTS = 19; 				// 修改特效		
	
	public static final int EXTERNAL_ID_APPDRAWER_SLIDEMENU = 20; 	
	
	public static final int TAB_APPS_ORDER_BY_NAME = 0;
	public static final int TAB_APPS_ORDER_BY_TIME = 1;
	public static final int TAB_APPS_ORDER_BY_FREQUENCY = 2;

	public static final int ADD_APPS_ID = 1000;
	public static final int ADD_GOSHORTCUTS_ID = 1001;
	public static final int ADD_FOLDER_ID = 1002;
	public static final int ADD_WIDGET_ID = 1003;
	public static final int ADD_SYSTEM_WIDGET_ID = 1004;
	public static final int ADD_SYSTEM_SHORTCUTS_ID = 1005;
	
	public static final String SEARCH_WIDGET_PACKAGE = "search_widget_package";
	public static final String SEARCH_WIDGET_TAG = "search";
	
	public final static int WALLPAPER_SCROLL_TYPE_TAG = 1;
	
	public final static float AUTO_FLY_SCALE = 1.5f;

	public static boolean sISANIMATION = false; // 当前动画状态
	public static final String ANIMATION_IN_TAG = "aniamtionInTag";
	public static final String ANIMATION_IN_PKG = "aniamtionInPkg";
	public static final String ANIMATION_IN_INFO = "aniamtionInInfo";
	public static boolean sISBACKTOAPPDRAWERWIDGET = false;
	public static boolean sISCOMEFROMAPPDRAWERWIDGET = false;
	
	public static final String TAB_MAIN = "main"; // 添加应用程序模块
	public static final String TAB_WALLPAPER = "wallpaper"; // 壁纸模块
	public static final String TAB_THEMELOCKER = "themelocker"; // 主题模块
	public static final String TAB_EFFECTS = "effects"; // 特效模块
	public static final String TAB_THEME = "theme"; // 主题模块（二级）
	public static final String TAB_LOCKER = "locker"; // 锁屏模块（二级）
	public static final String TAB_GOWALLPAPER = "gowallpaper"; // Go壁纸模块(二级)
	public static final String TAB_GODYNAMICA_WALLPAPER = "godynamicawallpaper"; // Go动态壁纸模块(二级)
	public static final String TAB_GOWIDGET = "gowidgets"; // Go小部件模块(二级)
	public static final String TAB_SYSTEMWIDGET = "systemwidgets"; // 系统小部件模块(二级)

	public static final String TAB_ADDAPPS = "add_apps"; // 应用程序添加(三级)
	public static final String TAB_ADDFOLDER = "add_folder"; // 文件夹添加(三级)
	public static final String TAB_ADDGOSHORTCUT = "add_goshortcut"; // Go快捷方式添加(三级)
	public static final String TAB_ADDGOWIDGET = "add_gowidget"; // Go小部件添加(三级)
	public static final String TAB_ADDSYSTEMWIDGET = "add_addsystemwidget"; // 系统小部件添加(三级)
}
