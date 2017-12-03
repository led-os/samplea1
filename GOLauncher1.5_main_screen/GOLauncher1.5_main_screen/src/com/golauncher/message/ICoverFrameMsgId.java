package com.golauncher.message;

/**
 * 
 * @author liuheng
 *
 */
public interface ICoverFrameMsgId {
	public final static int BASE_ID = 41000;
	
	/**
	 * 通知DiyScheduler添加一个view到罩子层
	 * 
	 * @param param
	 *            view的coverID
	 * @param object
	 *            附带参数：包名或者是view对象
	 * @param objects
	 *            null
	 */
	public static final int COVER_FRAME_ADD_VIEW = 41001;
	
	/**
	 * 通知DiyScheduler移除罩子层的一个view
	 * 
	 * @param param
	 *            view的coverID
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int COVER_FRAME_REMOVE_VIEW = 41002;


	/**
	 * 通知DiyScheduler显示罩子层（整个）
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int COVER_FRAME_SHOW_ALL = 41003;
	
	/**
	 * 通知DiyScheduler隐藏罩子层（整个）
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int COVER_FRAME_HIDE_ALL = 41004;
	
	/**
	 * 通知DiyScheduler移除罩子层（整个）
	 * 
	 * @param param
	 *            -1
	 * @param object
	 *            null
	 * @param objects
	 *            null
	 */
	public static final int COVER_FRAME_REMOVE_ALL = 41005;
}
