package com.golauncher.message;

/**
 * 屏幕广告消息ID
 */
public interface IScreenAdvertMsgId {
	public final static int BASE_ID = 47000;
	
	/**
	 * 发给屏幕层消息：判断桌面1、5屏第一次请求成功后判断是否对屏幕做过修改
	 */
	public final static int SCREEN_CAN_CHANGE_ADVERT_SHORT_CUT = 47001;
	
	/**
	 * 发给屏幕层消息：判断桌面首屏可以插入广告图标
	 */
	public final static int SCREEN_CAN_ADD_ADVERT_TO_HOME_SCREEN = 47002;
	
	/**
	 * 发给屏幕层消息：判断桌面1、5屏是否可以插入广告图标
	 */
	public final static int SCREEN_CAN_ADD_ADVERT_SHORT_CUT = 47003;
	
	/**
	 * 设置是否可以可以请求广告数据
	 */
	public final static int SET_CAN_REQUEST_ADVERT_STATE = 47004;
	
	/**
	 * 设置是否24小时重新请求
	 */
	public final static int SET_CAN_REQUEST_AGAIN_STATE = 47005;
	
	public final static int REQUEST_ADVERT_STAT_CLICK_ACTION = 47006;
	
	public final static int START_REQUEST_ADVERT_DATA = 47007;
	
	public final static int SET_ADVERT_APP_IS_OPEN = 47008;
	
	public final static int SET_OPEN_CACHE = 47009;
	
	public final static int REQUEST_ADVERT_STAT_INSTALL = 47010;
	
	public final static int REQUEST_ADVERT_STAT_INSTALL_WIDGET = 47011;
	
	public final static int CHECK_IS_NOT_OPEN = 47012;
	
	public final static int CHECK_REQUEST_AGAIN = 47013;
	
	public final static int CHECK_IS_IN_ADVERT_LIST = 47014;
}
