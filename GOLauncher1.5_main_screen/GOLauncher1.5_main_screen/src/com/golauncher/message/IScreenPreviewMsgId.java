package com.golauncher.message;

/**
 * 
 * @author yangguanxiang
 *
 */
public interface IScreenPreviewMsgId {
	public final static int BASE_ID = 37000;

	/**
	 * 屏幕预览当前屏
	 */
	public static final int PREVIEW_CURRENT_SCREEN_INDEX = 37001;

	public final static int PREVIEW_INIT = 37002;

	public final static int PREVIEW_DELETE_SCREEN = 37003;



	/***
	 * 预览页面，是否正显示
	 */
	//	public static final int PREVIEW_SHOWING = 5010;

	/***
	 * 预览页面，手势返回主页（带动画）
	 */
	public static final int PREVIEW_TO_MAIN_SCREEN_ANIMATE = 37004;

	/**
	 * 屏幕预览层消息：要求作离开动作
	 * 
	 * @param param
	 *            屏索引
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int PREVIEW_LEAVE_ANIMATE = 37005;

	
	
	/**
	 * 屏幕预览层消息：要求放大某一个卡片并获取被放大的卡片的位置信息
	 * 
	 * @param param
	 *            当前屏的卡片索引
	 * @param object
	 *            被放大的卡片的Rect对象
	 * @param objects
	 *            null
	 */
	public static final int PREVIEW_ENLARGE_CARD = 37006;
	/**
	 * 屏幕预览层消息：要求恢复一个被放大的卡片
	 * 
	 * @param param
	 *            当前屏的卡片索引
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int PREVIEW_RESUME_CARD = 37007;
	/**
	 * 屏幕预览层消息：获取当前屏索引所对应的绝对屏索引
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            Bundle对象，ScreenPreviewFrame.FIELD_ABS_INDEX获取绝对的索引值
	 * @param objects
	 *            null
	 */
	public static final int PREVIEW_GET_ABS_SCREEN_INDEX = 37008;

	
	/**
	 * 获取屏幕预览参数
	 */
	public static final int GET_PREVIEW_PARAM = 37009;
}
