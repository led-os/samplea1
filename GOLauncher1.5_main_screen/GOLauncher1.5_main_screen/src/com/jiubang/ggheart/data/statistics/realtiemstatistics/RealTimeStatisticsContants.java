package com.jiubang.ggheart.data.statistics.realtiemstatistics;

/**
 * 实时统计相关常量
 * 
 * @author zhouzefeng
 * 
 * 2013.04.16
 */
public class RealTimeStatisticsContants {
	
	/**
	 * 实时统计保存sharedprefrence的xml名称
	 */
	public final static String REALTIME_STATISTICS_APPGAME_DATA = "realtime_statistics_appgame_data";
	/**
	 * 实时统计保存当前入口的sharedprefrence的key值
	 */
	public final static String REALTIME_STATISTICS_CURENT_ENTARNCE_KEY = "realtime_statistics_current_entrance_key";
	/**
	 * SDK统计下桌面应用中心的功能ID
	 */
	public final static int FUN_ID_APPGAME = 1;
	public static final int  FUN_ID_QUIXEY_SEARCH = 32 ;
	public static final int  FUN_ID_APPGAME_SEARCH = 33 ;
	/**
	 * 更新分类ID 
	 */
	public static final String  UPDATE_CATEGORY_ID = "1";
	/**
	 * 搜索入口id
	 */
	public static final int  SEARCH_ENTRY_APPGAME = 1 ;		//应用中心
	/**
	 * 详情界面的位置信息
	 */
	public static final int APP_DETAIL = 88;
	
	//-----------------------------操作码-------------------------
	/**
	 * 登录操作码
	 */
	public final static String OPERATE_LOGIN = "g001";
	/**
	 * TAB栏点击操作码
	 */
	public final static String OPERATE_TAB_CLICK = "h000";
	/**
	 * 下载点击操作码
	 */
	public final static String OPERATE_DOWNLAOD_CLICK = "a000";
	/**
	 * 下载安装操作码
	 */
	public final static String OPERATE_DOWNLAOD_INSTALLED = "b000";
	/**
	 * 更新点击操作码
	 */
	public final static String OPERATE_UPDATE_CLICK = "d000";
	/**
	 * 更新安装操作码
	 */
	public final static String OPERATE_UPDATE_INSTALLED = "e000";
	/**
	 * 搜索操作
	 */
	public static final String ACTION_SEARCH = "s001";
	/**
	 * 详情点击
	 */
	public final static String OPERATE_DETAIL_CLICK = "c000";
	/**
	 * 操作成功
	 */
	public final static int OPERATE_SUCCESS = 1 ;
	/**
	 * 操作失败
	 */
	public final static int OPERATE_FAIL = 0 ;
	
	//-----------------------------操作码-END------------------------
	/**
	 * 传递，保存入口的key值
	 */
	public final static String ENTRANCE_KEY = "entrance_key";
	
	/**
	 * 桌面应用中心的入口统计字段
	 * @author zhouzefeng
	 *
	 */
	public static class AppgameEntrance {
		/**
		 * 功能表菜单入口
		 */
		public static final int FUNC_MENU = 1;
		/**
		 * 通知栏入口，调整至更新界面
		 */
		public static final int NOTIFICATION = 2;
		/**
		 * GOSTORE入口
		 */
		public static final int GO_STORE = 3;
		/**
		 * 功能表GOSTORE 右上角图标数字入口
		 */
		public static final int FUNC_GOSTORE_DIGITAL = 4;
		/**
		 * 功能表应用图标 右上角图标数字入口
		 */
		public static final int FUNC_APP_DIGITAL = 5;
		/**
		 *桌面图标入口
		 */
		public static final int DESK_ICON = 6;
		/**
		 * 功能表图标入口
		 */
		public static final int FUNC_ICON = 7;
		/**
		 * 桌面菜单GOMARKET入口
		 */
		public static final int DESK_MENU_GOMARKET = 8;
		/**
		 * WIDGET 入口
		 */
		public static final int WIDGET = 9;
		/**
		 * 功能表应用市场右上角入口
		 */
		public static final int FUNC_GOMARKET_DIGITAL = 10;
		/**
		 * 功能表搜索入口
		 */
		public static final int FUNC_SEARCH = 11;
		/**
		 * WIDGET搜索入口
		 */
		public static final int WIDGET_SERACH = 12;
		/**
		 * 主题预览界面入口
		 */
		public static final int THEME_PREVIEW = 13;
		/**
		 * 更新提示页面主题推荐入口
		 */
		public static final int UPDATE_TIP_THEME = 14;
		/**
		 * 壁纸添加界面入口
		 */
		public static final int WALLPAPER_ADD = 15;
		/**
		 *桌面菜单精品入口
		 */
		public static final int DESK_MENU_GOSTORE = 16;
		/**
		 *功能表精品入口
		 */
		public static final int FUNC_GOSTORE = 17;
		/**
		 *消息中心入口
		 */
		public static final int MESSAGE_CENTER = 18;
		/**
		 *桌面菜单APP入口
		 */
		public static final int DESK_APP = 19;
		/**
		 * 桌面应用中心图标
		 */
		public static final int DESK_APP_GOMARKET_ICON = 20 ;
		/**
		 * 3d功能表上面操作条图标
		 */
		public static final int FUNC_TOP_BAR_ICON = 23 ;
		
		
	}
}





