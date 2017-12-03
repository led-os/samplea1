package com.golauncher.message;

/**
 * 
 * @author liuheng
 *
 */
public interface IFrameControllerMsgId {
	public final static int BASE_ID = 32000;
	
	/**
	 * 获取Top Frame Id
	 */
	public final static int GET_TOP_FRAME_ID = 32001;
	
	/**
	 * 获取frame是否显示
	 */
	public final static int GET_FRAME_VISIBILITY = 32002;
	
	/**
	 * 
	 */
	public final static int ISOPAQUE = 32003;
	
	public final static int FRAME_EXIST = 32004;
	
}
