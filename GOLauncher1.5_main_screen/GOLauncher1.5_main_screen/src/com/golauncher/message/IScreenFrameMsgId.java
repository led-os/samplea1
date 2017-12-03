package com.golauncher.message;

/**
 * 
 * @author liuheng
 *
 */
public interface IScreenFrameMsgId {
	public static final int BASE_ID = 36000;
	
	/**
	 * 屏幕删除一个View，包括数据库和UI
	 */
	public static final int SCREEN_DELETE_VIEW = 36001;
	
	/**
	 * 重新加载屏幕层
	 */
	public static final int SCREEN_RELOAD = 36002;
	
	/**
	 * 屏幕添加一个View，包括数据库和UI(根据传过的ID)
	 */
	public static final int SCREEN_ADD_VIEW = 36003;
	
	public final static int PREVIEW_SCREEN_ADD = 36004;
	
	/**
	 * 屏幕预览层移动结束后保存数据
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int PREVIEW_SAVE_SCREEN_DATA = 36005;
	
	/**
	 * 屏幕预览层消息：通知屏幕显示或隐藏
	 * 
	 * @param param
	 *            0 - 隐藏桌面 , 1 - 显示桌面
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int PREVIEW_NOTIFY_DESKTOP = 36006;
	
	/**
	 * 屏幕预览层消息：要求预览层做位置交换动画
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            Bundle对象
	 * @param objects
	 *            List<Rect>预览层更新当前屏的所有区域
	 */
	public static final int PREVIEW_REPLACE_CARD = 36007;
	
	/***
	 * 预览页面，手势返回主页
	 */
	public static final int PREVIEW_TO_MAIN_SCREEN = 36008;
	
	public final static int PREVIEW_SCREEN_ENTER = 36009;
	
	public final static int PREVIEW_SCREEN_REMOVE = 36010;
	
	/**
	 * 拖动widget进入屏幕预览,缩小widget
	 */
	public static final int PREVIEW_RESIZE_DRAGVIEW = 36011;
	
	public final static int SCREEN_EDIT_PRE_ADD = 36012;
	
	/**
	 * 告诉屏幕层返回对应的添加界面一级页面
	 */
	public static final int SCREEN_BACK_TO_LAST_LEVEL = 36013;
	

	/**
	 * 设置特效tab（预览）
	 */

	public static final int SCREENEDIT_SHOW_TAB_EFFECT_SETTING = 36014;
	
	/***
	 * 离开添加新文件夹状态
	 */
	public final static int LEAVE_NEW_FOLDER_STATE = 36015;
	
	/**
	 * 添加widget时锁定屏幕
	 */
	public static final int LOCK_SCREEN_TO_SCROLL = 36016;
	
	/**
	 * 判断屏幕层是否处理静止状态
	 */
	public static final int SCREEN_IS_SCROLL_FINISHED = 36017;
	
	/**
	* 开始做水波纹
	*/
	public static final int EFFECT_START_WAVE = 36018;
	
	public final static int SCREEN_SHOWING_AUTO_EFFECT = 36019;
	
	/**
	 * 屏幕编辑界面请求添加一元素到桌面当前屏幕
	 * @param param 添加元素的类型｛link ADD_APPS_ID, ADD_GOSHORTCUTS_ID, ADD_FOLDER_ID｝
	 * @param objects 指定要添加的元素，属于 GLView， tag必须包含ItemInfo
	 */
	public final static int SCREEN_EDIT_ITEM_TO_SCREEN = 36020;
	
	/***
	 * 删除文件夹中飞入的图标
	 */
	public final static int SCREEN_DEL_ITEM_FROM_FOLDER = 36021;
	
	/**
	 * 屏幕层消息：长按事件
	 * 
	 * @param param -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_LONG_CLICK = 36022;


	/**
	 * 屏幕层消息：更新指示器
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_UPDATE_INDICATOR = 36023;


	/**
	 * 通知screen更新指定的item的位置信息
	 * 
	 * @param param    新的屏幕索引 
	 * @param objects  第一个元素为iteminfo
	 */
	public static final int SCREEN_UPDATE_ITEM_INFOMATION = 36024;

	/**
	 * 屏幕层消息：一批组件更改位置，要保存数据库
	 * 
	 * @param param screenIndex
	 * @param objects ArrayList<ItemInfo> 要修改的数据
	 */
	public static final int SCREEN_CHANGE_VIEWS_POSITIONS = 36025;

	/**
	 * 屏幕层消息：合并文件夹
	 * 
	 * @param param screenIndex
	 * @param objects ArrayList<DragView> dragView
	 */
	public static final int SCREEN_MERGE_ITEMS = 36026;

	/**
	 * 屏幕层消息：移动到文件夹内
	 * 
	 * @param param screenIndex
	 * @param objects ArrayList<DragView> dragView
	 */
	public static final int SCREEN_MOVE_TO_FOLDER = 36027;

	public final static int SCREEN_GET_CELLLAYOUT = 36028;

	/**
	 * 屏幕层消息：向屏幕层（GLScreen）请求生成一个桌面item
	 * 同步执行
	 * @param param screenIndex
	 * @param objects ArrayList<Object> {itemInfo, GLView(生成后的view)}
	 */
	public static final int SCREEN_CREATE_ITEM = 36029;

	/**
	 * 屏幕层消息：向屏幕层（GLScreen）请求添加一个itemInfo进数据库
	 * 同步执行
	 * @param param screenIndex
	 * @param objects ArrayList<Object> {itemInfo, dragType} 
	 */
	public static final int SCREEN_ADD_DESKTOP_ITEM = 36030;

	/**
	 * @param param screenIndex 指定页面的索引
	 * @param objects ｛是否需要执行特效翻滚， 页面切换时间｝
	 * 屏幕层消息：翻滚到指定页面
	 */
	public static final int SCREEN_SNAP_TO_SCREEN = 36031;

	/**
	 * 屏幕层消息：找最近的空格子
	 */
	public final static int SCREEN_FIND_NEAREST_VACANT_CELL = 36032;

	/**
	 * 屏幕层消息：添加功能表内的应用图标到屏幕层
	 */
	public final static int SCREEN_ADD_APPDRAWER_ICON_TO_SCREEN = 36033;
	
	/**
	 * 屏幕层消息：告诉屏幕层添加一个空白的屏幕
	 * @param -1
	 * @param objects null
	 */
	public static final int SCREEN_ADD_BLANK_CELLLAYOUT = 36034;
	
	/**
	 * 屏幕层消息：告诉屏幕层将空白的屏幕转变为正常的屏幕
	 * @param -1
	 * @param objects null
	 */
	public static final int SCREEN_BLANK_TO_NORMAL = 36035;
	
	/**
	 * 屏幕层消息：告诉屏幕层添加一个文件夹
	 * @param -1
	 * @param objects userFolderInfo
	 */
	public static final int SCREEN_ADD_USER_FOLDER = 36036;
	
	/**
	 * 添加gowidget
	 * 
	 * @param param
	 * 				widget id
	 * @param object
	 *            bundle widget详细信息
	 * @param objects
	 *            null
	 */
	public static final int SCREEN_ADD_GO_WIDGET = 36037;


	/**
	 * 屏幕自动飞动画
	 */
	public static final int SCREEN_AUTO_FLY = 36038;

	
	/**
	 * 通过区域获得celllayout的格子信息，起始和占的行列数
	 * 
	 * @param param
	 * 				-1
	 * @param objects[0]
	 *            rect
	 * @param objects[1]
	 *            int[4]
	 * 
	 */
	public static final int SCREEN_RECT_TO_POSITION = 36039;
	

	/**
	 * 退出缩放界面
	 */
	public static final int SCREEN_EXIT_RESIZE_WIDGET = 36040;
	
	/**
	 * widget消息交互
	 */
	/**
	 * 进入屏幕
	 */
	public static final int SCREEN_FIRE_WIDGET_ONENTER = 36041;
	
	/**
	 * 退出屏幕
	 */
	public static final int SCREEN_FIRE_WIDGET_ONLEAVE = 36042;
		
	/**
	 * 拖动图标到屏幕边缘进入屏幕预览,屏幕层边缘背景显示
	 * @param param
	 * 1 表示进入左边 
	 * 2 表示进去右边
	 * 0 表示离开边缘区域
	 */
	public static final int SCREEN_ENTER_SCROLL_ZONE_BG = 36043;
	
	/**
	 * 通知屏幕层展现中间层
	 * @param param   -1 
	 * @param objects null
	 */
	public static final int SCREEN_SHOW_MIDDLE_VIEW = 36044;
	
	/**
	 * 屏幕层消息：Widget是否使用DrawingCache
	 */
	public final static int SCREEN_ENABLE_WIDGET_DRAWING_CACHE = 36045;
	
	
	/**
	 * 屏幕层消息：屏幕预览添加元素到屏幕层
	 */
	public final static int PREVIEW_ADD_TO_SCREEN = 36046;
	
	
	
	/**
	 * 屏幕层消息：要求屏幕层设置指定屏为主屏
	 * @param param
	 *            屏索引
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_SET_HOME = 36047;
	
		
	/**
	 * 发送屏幕层消息：手势从屏幕层进入功能表
	 */
	public final static int SCREEN_TO_APPDRAWER = 36048;
	
	
	/**
	 * 通知桌面将壁纸偏移设为0
	 */
	public final static int SCREEN_UPDATE_WALLPAPER_FOR_SCROLL_MODE_CHANGE = 36049;
	
	/**
	 * 挤压dock图标到桌面放置,向screenFrame拿放置位置
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            Point center
	 * @param objects
	 *            ArrayList<int[]> 保存网格点
	 */
	public final static int GET_DROP_DOCK_LOCATION = 36050;
	
	/**
	 * 与dock交换位置，并添加到桌面
	 */
	public final static int EXCHANGE_ICON_FROM_DOCK = 36051;
	
	
	/**
	 * 发送广播给多屏多壁纸应用，通知当前屏幕数与当前屏幕下标
	 */
	public static final int SCREEN_SEND_BROADCASTTO_MULTIPLEWALLPAPER = 36052;
		
	/**
	 * 由于Dock的可见性发生变化，通知workspace调整布局
	 * 
	 * @param param      1 == SHOW, 0 == HIDE
	 * @param object     null
	 * @param objects    null
	 */
	public static final int SCREEN_NEED_TO_LAYOUT_BY_DOCK = 36053;
	
	/**
	 * 通知桌面屏幕层（显示／隐藏）指示器的消息
	 * 
	 * @param param        1 表示显示， 0 表示隐藏
	 * @param object       null
	 * @param objects      null
	 */
	public static final int SCREEN_SHOW_OR_HIDE_INDICATOR = 36054;
	

	/**
	 * 屏幕层消息：要求刷新屏幕层组件的屏幕索引
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_REFRESH_INDEX = 36055;

	/**
	 * 设置当前屏幕
	 * 
	 * @param param    当前屏幕的索引，不能为 -1
	 * @param object   null
	 * @param objects  null
	 */
	public static final int SCREEN_SET_CURRENTSCREEN = 36056;
	
	/**
	 * 获取当前屏幕及主屏幕
	 */
	public static final int SCREEN_GET_HOME_CURRENT_SCREEN = 36057;
	
	/**
	 * 强制屏幕重新layout
	 */
	public static final int SCREEN_FORCE_RELAYOUT = 36058;
		
	/**
	 * 发给屏幕层消息：是否正处于编辑界面
	 */
	public final static int SCREEN_IS_IN_EDIT_STATE = 36059;
	
	/**
	 * 发给屏幕层消息：退出编辑界面
	 */
	public final static int SCREEN_EXIT_EDIT_STATE = 36060;
	
	/**
	 * 发给屏幕层消息：显示或隐藏GGMenu
	 */
	public final static int SCREEN_ON_GGMENU_SHOW = 36061;
	
	/**
	 * 通过垃圾桶卸载程序
	 */
	public static final int UNINSTALL_APP_FROM_DELETEZONE = 36062;
	
	/**
	 * 是否开启CellLayout的绘图绘图缓冲
	 */
	public static final int SCREEN_ENABLE_CELLLAYOUT_DRAWING_CACHE = 36063;
	
	
	
	public static final int REQUEST_SCREEN_EDIT_PUSH_LIST = 36064;
		
	public final static int SCREEN_GET_SYSTEM_WIDGET_ID = 36065;
	
	/**
	 * 隐藏当前小白云
	 */
	public final static int SCREEN_HIDE_CURRENT_GUIDE = 36066;
	/**
	 * 重新显示上次取消的小白云
	 */
	public final static int SCREEN_RESHOW_LAST_GUIDE = 36067;
	
	
	/**
	 * 设置0屏指示器
	 */
	public static final int SCREEN_ZERO_SETINDICATOR = 36068;
	
	/**
	 * 进入0屏时dock和指示器的隐藏
	 */
	public static final int SCREEN_ZERO_INDICATOR_AND_DOCKMOVE = 36069;
	 
	
	/**
	 * remove the screen from mem
	 */
	public static final int SCREEN_REMOVE_BLANK_SCREEN = 36070;
	
	
	/**
	 * 通知screenFrame进入桌面编辑
	 * 
	 * @param param
	 *            1 表示带动画， 否则表示不带动画
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int SCREEN_ENTER_SCREEN_EDIT_LAYOUT = 36071;
	
	
	
	/**
	 * 隐藏桌面中间层
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_HIDE_MIDDLE_VIEW = 36072;
	
	/**
	 * 屏幕层消息：
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            pkg
	 * @param objects
	 *            null
	 */
	public static final int GOTO_SCREEN_EDIT_TAB = 36073;
	
	/**
	 * 屏幕层消息：指示器位置重置
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int SCREEN_INDICRATOR_POSITION = 36074;
	
	/**
	 * 桌面图标恢复默认
	 */
	public final static int SCREEN_RESET_DEFAULT = 36075;
	
	/**
	 * 发送屏幕层消息：要求AppwidgetHost生成一个新的appwidget的ID
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            Bundle对象,key为"id"的值表示ID，将新的ID通过value返回
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_GET_ALLOCATE_APPWIDGET_ID = 36076;
	
	/**
	 * 发送屏幕层消息：要求AppwidgetHost删除一个appwidget的ID
	 * 
	 * @param param
	 *            id
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_DEL_APPWIDGET_ID = 36077;
	
	/**
	 * 发送屏幕层消息：要求屏幕层添加一个appwidget到屏幕上
	 * 
	 * @param param
	 *            从系统获取的ID
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_ADD_APPWIDGET = 36078;
	
	/**
	 * 让屏幕可滚动
	 */
	public final static int ALLOW_SCREEN_TO_SCROLL = 36079;
	
	/**
	 * 屏幕层消息：要求显示屏幕预览
	 * 
	 * @param param
	 *            1 表示从设置进入预览，0表示其他途径进入预览
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_SHOW_PREVIEW = 36080;
	
	/**
	 * 发给屏幕层消息：要求添加指定的快捷方式（双11）
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            ApplicationItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_ADD_SHORTCUT_FOR_ELEVEN = 36081;
	
	/**
	 * 发送屏幕层消息：要求屏幕层添加一个激活的文件夹到屏幕上
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            LiveFolderInfo对象
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_ADD_LIVE_FOLDER = 36082;
	
	/**
	 * 屏幕层消息：要求显示主屏幕
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_SHOW_HOME = 36083;
	
	/**
	 * 屏幕层消息：要求显示主屏幕，如果当前是主屏幕，则显示屏幕预览
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_SHOW_MAIN_SCREEN_OR_PREVIEW = 36084;
	
	/**
	 * 替换网秦下载图标为网秦应用图标
	 */
	public static final int SCREEN_REPLACE_RECOMMEND_ICON = 36085;
	
	/**
	 * 替换文件夹里面的推荐图标为应用图标
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            应用包名
	 * @param objects
	 *            null
	 */
	public final static int REPLACE_RECOMMAND_ICON_IN_FOLDER = 36086;
	
	/**
	 * 发给屏幕层消息：一键清理屏幕获取扫描结果
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *           -1
	 * @param objects
	 *            null
	 */
	public final static int CLEAN_SCREEN_SCAN_SCREEN = 36087;
	
	/**
	 * 发给屏幕层消息：一键清理屏幕获取扫描结果
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *           -1
	 * @param objects
	 *            null
	 */
	public final static int CLEAN_SCREEN_DEL_ONE_ICON = 36088;
	
	/**
	 * 发给屏幕层消息：桌面1、5屏添加广告图标
	 * 
	 * @param param
	 *            插入哪个到哪个屏幕
	 * @param object
	 *            ItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_ADD_ADVERT_SHORT_CUT = 36089;
	
	/**
	 * 发给屏幕层消息：桌面1、5屏添加广告文件夹
	 * 
	 * @param param
	 *            插入哪个到哪个屏幕
	 * @param object
	 *            ItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_ADD_ADVERT_FOLDER = 36090;
	
	/**
	 * 发给屏幕层消息：清除15屏广告图标
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *           -1
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_CLEAR_ADVERT_ICON = 36091;
	
	/**
	 * 发给屏幕层消息：设置首屏图标信息缓存
	 * 
	 * @param param 屏幕数
	 *            
	 * @param object
	 *           -1
	 * @param objects
	 *            null
	 */
	public final static int SET_HOME_SCREEN_ICON_CACHE = 36092;
	
	public final static int CUSTOMER_PICK_WIDGET = 36093;
	
	/**
	 * 发给屏幕层消息：要求添加指定的应用,如果当前屏已满,则选择一个最近的屏添加
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            ApplicationItemInfo对象
	 * @param objects
	 *            null
	 */
	public static final int SCREEN_ADD_SHORTCUT_COMPLETE = 36094;
	
	/**
	 * 屏幕层消息：该消息用来提示GO锁屏切换
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int GO_LOCKER_PRECHANGE = 36095;
	
	/**
	 * 屏幕层消息：该消息用来提示GO锁屏切换完成
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int GO_LOCKER_CHANGED = 36096;
	
	/**
	 * 屏幕层消息：在当前屏添加一个搜索框
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int SCREEN_ADD_SEARCH_WIDGET = 36097;
	
	/**
	 * 屏幕层消息：发给0屏的消息
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_ZERO_SEND_MESSAGE = 36098; 
	
	/**
	 * 屏幕层消息：是否显示0屏
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_SHOW_ZERO_SCREEN = 36099; 
	
	public final static int SCREEN_GUIDE_ENTER_ANIM = 36100;
	
	/**
	 * 清除恢复appwidget的标识位与info.用于用户取消恢复appwidget
	 */
	public final static int SCREEN_CLEAN_RESTOREAPPWIDGET_INFO = 36101;
	
	/**
	 * 屏幕层消息完成屏幕的加载
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            Rect
	 * @param objects
	 */
	public final static int SCREEN_FINISH_LOADING = 36102;
	
	/**
	 * 设置背景
	 * 
	 * @param param
	 *            y轴上的偏移量
	 * @param object
	 *            Drawable 当前壁纸
	 * @param objects
	 *            null
	 */
	public final static int SET_WALLPAPER_DRAWABLE = 36105;
	
	/**
	 * 发给屏幕层消息：要求添加指定的快捷方式
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            ApplicationItemInfo对象
	 * @param objects
	 *            null
	 */
	public final static int SCREEN_ADD_SHORTCUT = 36106;
	
	/**
	 * 通知3D插件显示GLGGMenu
	 */
	public static final int SHOW_GLGGMENU = 36107;
	
	/**
	 * 应用GoWidget主题
	 * 
	 * @param param
	 *            widgetId
	 * @param object
	 *            Bundle
	 * @param objects
	 *            null
	 */
	public final static int APPLY_GO_WIDGET_THEME = 36108;
	
	/**
	 * 
	 */
	public final static int SCREEN_CHANGE_TO_SMALL = 36109; 
	
	public final static int SCREEN_LEAVE_EDIT_STATE = 36110;
	
	/**
	 * 在添加模块下，屏幕缩放动画是否已经结束
	 */
	public static final int SCREEN_IS_SCALE_ANIM_FINISHED = 36111;
}
