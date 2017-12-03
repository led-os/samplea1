package com.golauncher.message;

/**
 * 
 * @author yangguanxiang
 *
 */
public interface IDeleteZoneMsgId {
	public final static int BASE_ID = 50000;
	
	/**
	 * 继续删除动画
	 */
	public static final int DELETE_ZONE_CONTINUE_DELETE_ANIMATION = 50001;
	/**
	 * 关闭垃圾桶
	 */
	public static final int DELETE_ZONE_CLOSE_TRASHCAN = 50002;	
}
