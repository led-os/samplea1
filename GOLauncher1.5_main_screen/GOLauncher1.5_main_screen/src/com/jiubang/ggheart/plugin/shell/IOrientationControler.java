package com.jiubang.ggheart.plugin.shell;

/**
 * 
 * @author yangguanxiang
 *
 */
public interface IOrientationControler {
	/**
	 * 保持现有横竖屏状态
	 */
	public void keepCurrentOrientation();

	/**
	 * 把横竖屏状态转换交回系统管理
	 */
	public void resetOrientation();

	/**
	 * 获取桌面设置屏幕状态
	 * @return
	 */
	public int getConfigOrientationType();

	/**
	 * 设置屏幕状态
	 * @param type: OrientationControl.AUTOROTATION ...
	 */
	public void setOrientationType(int type);

	/**
     * 设置屏幕预览状态
     * @param type: OrientationControl.AUTOROTATION ...
     */
    public void setPreviewOrientationType(int type);
	
	/**
	 * 获取当前屏幕状态
	 * @return
	 */
	public int getRequestOrientation();

	/**
	 * 设置屏幕状态
	 * @param type: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED ...
	 */
	public void setRequestOrientation(int type);

	public void setSmallModle(boolean bool);

	public void setPreviewModel(boolean flag);

	/**
	 * true：在整个过程中保持横竖屏，不管中间是否调用了resetOrientation
	 * false： 释放保持横竖屏状态
	 * @param keep
	 */
	public void keepOrientationAllTheTime(boolean keep);
	
	/**
	 * true：在整个过程中保持横竖屏，不管中间是否调用了resetOrientation
	 * int: 保持的状态：横屏/竖屏/自动旋转
	 * false： 释放保持横竖屏状态
	 * @param keep
	 */
	public void keepOrientationAllTheTime(boolean keep, int type);
}
