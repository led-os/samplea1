package com.golauncher.message;

/**
 * 
 * @author liuheng
 *
 */
public interface IDockMsgId {
	public final static int BASE_ID = 42000;
	
	/**
	 * Dock层消息：Dock条控制是否显示Dock条，如果显示则不显示，如果不显示则显示
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SHOW_HIDE = 42001;

	/**
	 * Dock层消息：Dock条控制转屏
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_TURN_SCREEN = 42002;

	/**
	 * Dock层消息： Dock条对话框点击选项“快捷方式”，进入快捷方式选择界面
	 * 
	 * @param param
	 *            -1：非自适应模式 - 有GO快捷方式
	 *            1：自适应模式 - 没有GO快捷方式
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ENTER_SHORTCUT_SELECT = 42004;

	/**
	 * 发给DOCK层消息：要求添加指定的应用
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            ApplicationItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ADD_APPLICATION = 42005;

	/**
	 * 发给DOCK层消息：要求添加指定的快捷方式
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            ApplicationItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ADD_SHORTCUT = 42006;

	/**
	 * 发给DOCK层消息：要求添加指定的应用到DOCK的手势识别
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            ApplicationItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ADD_APPLICATION_GESTURE = 42007;

	/**
	 * Dock层消息： Dock条对话框点击选项“快捷方式”，进入快捷方式选择界面
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ENTER_SHORTCUT_SELECT_FOR_GESTURE = 42008;

	/**
	 * 发给DOCK层消息：要求添加指定的手势快捷方式
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            ApplicationItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ADD_SHORTCUT_FOR_GESTURE = 42009;

	/**
	 * 发给DOCK层消息：显示DOCK
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SHOW = 42010;

	/**
	 * 发给DOCK层消息：隐藏DOCK
	 * 
	 * @param param
	 *            -1 是否做动画
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_HIDE = 42011;

	/**
	 * 发给DOCK层消息：DOCK设置更改了
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SETTING_CHANGED = 42012;

	/**
	 * 发给DOCK层消息：DOCK风格更改了
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            style 风格字符串
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SETTING_CHANGED_STYLE = 42013;

	/**
	 * 是否存在垃圾数据, 通过sendMessage的返回值获取结果
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int IS_EXIST_DOCK_TRASH_DATA = 42014;

	/**
	 * 更新ＤＯＣＫ背景图片
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int UPDATE_DOCK_BG = 42016;

	/**
	 * dock弹出垃圾数据提示框
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SHOW_DIRTYDATA_TIPS = 42017;

	/**
	 * dock层消息：卸载了通讯程序
	 * 
	 * @param -1
	 * @object null
	 * @objects ArrayList<Integer> list 保存卸载的通讯程序在dock中的下标索引集合
	 */
	public static int DOCK_APP_UNINSTALL_NOTIFICATION = 42018;

	/**
	 * 发给DOCK层消息：DOCK行数更改了
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            style 风格字符串
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SETTING_CHANGED_ROW = 42019;

	/**
	 * 发给DOCK层消息：DOCK设置要更新
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SETTING_NEED_UPDATE = 42020;

	/**
	 * 发给DOCK层消息：拖动DOCK图标，移除菜单
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int REMOVE_ACTION_MENU = 42022;

	/**
	 * 获取dock当前打开文件夹图标的layout的位置信息
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            Rect
	 * @param objects
	 *            null
	 */
	public final static int GET_DOCK_OPEN_FOLDER_ICON_LAYOUTDATA = 42023;

	/**
	 * 发给DOCK层消息：要求添加文件夹
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            UserFolderInfo对象
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ADD_FOLDER = 42024;

	/**
	 * 发给DOCK层消息：要求删除文件夹内Item
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            UserFolderInfo对象
	 * @param objects
	 *            null
	 */
	public final static int DOCK_DELETE_FOLDERITEM = 42025;

	/**
	 * 从dock发送到向导提示层消息：当前手势操作提示是长按哪个dockiconview触发的 用于手势操作向导的一张图片的显示位置定位
	 * 
	 * @param param
	 *            0~4 第几个icon
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_GUIDE_GESTURE_ICON_INDEX = 42026;

	/**
	 * dock层消息：弹出手势选择框
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SHOW_GESTURE_SELETION = 42027;

	/**
	 * dock层消息：刷新文件夹内容
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int REFRASH_FOLDER_CONTENT = 42028;

	/**
	 * dock层息：图标从DOCK条移动
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ICON_VIEW_MOVE = 42029;

	/**
	 * 删除一个dock图标
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            long inscreenid
	 * @param objects
	 *            null
	 */
	public final static int DELETE_DOCK_ITEM = 42030;

	/**
	 * dock换页
	 * 
	 * @param param
	 *            edgeType 对应是那个屏幕那个位置边界
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_SNAP_TO_SCREEN = 42031;

	/**
	 * dock卸载程序消息
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            View dragview
	 * @param objects
	 *            null
	 */
	public final static int DOCK_UNINSTALL_APP = 42032;

	/**
	 * dock当前操作的icon
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            ArrayList<DockIconView>
	 */
	public final static int DOCK_CURRENT_ICON = 42033;

	/**
	 * dock自适应提示退出
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_AUTO_FIT_GUIDE_QUITE = 42034;

	/**
	 * 询问dock层是否打开合并文件夹 发送时机：screenFrame在长按完，垃圾桶消失后调用
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_ASK_OPEN_MERGE_FOLDER = 42035;

	/**
	 * dock恢复默认图标
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int DOCK_RESET_DEFAULT = 42036;

	/**
	 * 删除文件夹全部内容
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            long folderid
	 * @param objects
	 *            null
	 */
	public final static int REMOVE_FOLDER_CONTENT = 42037;

	/***
	 * 去拿dock条当前的图片
	 */
	public final static int DOCK_GET_VIEW_IMAGE = 42038;

	/**
	 * 释放背景底座图片
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int CLEAR_BG = 42039;
	
	/**
	 * 自适应模式dock行批量添加程序
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            ArrayList<AppItemInfo> addItemInfos
	 */
	public final static int ADD_APPS_TO_DOCK_FIT = 42040;
	
	/**
	 * dock条长按弹出导航对话框初始化
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            ArrayList<AppItemInfo> addItemInfos
	 */
	public final static int DOCK_ADD_ICON_INIT = 42041;
	
	
	/**
	 * dock条长按添加图标-添加一个图标完成
	 * 
	 * @param param 
	 *            -1
	 * @param object
	 *            Point
	 * @param objects
	 *            ArrayList<Integer>
	 */
	public final static int DOCK_ADD_ICON_ADD_FINISH = 42042;
	
	/**
	 * dock条长按添加图标超过5个时DOCK条变红色
	 * 
	 * @param param 是否需要红色背景
	 *            1:true
	 *            -1:false
	 * @param object
	 *            Point
	 * @param objects
	 *            ArrayList<Integer>
	 */
	public final static int DOCK_ADD_ICON_RED_BG = 42043;
	
	/**
	 * dock条长按添加图标-添加一个图标
	 * 
	 * @param param 
	 *            -1:
	 * @param object
	 *            Point
	 * @param objects
	 *            ArrayList<Integer>
	 */
	public final static int DOCK_ADD_ICON_ADD_ONE = 42044;
	/**
	 * dock滚动到0屏时间隐藏移动
	 * 
	 * @param param 屏幕数
	 *            
	 * @param object
	 *            Point
	 * @param objects
	 *            ArrayList<Integer>
	 */
	public final static int DOCK_ZERO_SCREEN_MOVE = 42045;
	
	public static final int DOCK_ON_SCREEN_FOLDER_OPEN = 42046;
	
	public static final int DOCK_ON_SCREEN_FOLDER_CLOSE = 42047;


	/**
	 * 处理甩动Dock条图标做飞出动画后的处理
	 */
	public static final int DOCK_ON_DRAG_FLING = 42048;
	
	/**
	 * 处理甩动Dock条文件夹内图标做飞出动画后的处理
	 */
	public static final int DOCK_FOLDER_ON_DRAG_FLING = 42049;
	
	/**
	 * 开始ios动画
	 */
	public static final int DOCK_START_IOS_ANIMATION = 42050;
	
	/**
	 * 模糊Dock栏
	 */
	public static final int DOCK_ENABLE_BLUR = 42051;
	
	/**
	 * 取消模糊
	 */
	public static final int DOCK_DISABLE_BLUR = 42052;
}
