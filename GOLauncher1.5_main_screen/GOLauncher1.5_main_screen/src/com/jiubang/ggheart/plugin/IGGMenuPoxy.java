package com.jiubang.ggheart.plugin;

/**
 * GGMenu代理接口
 * @author yejijiong
 *
 */
public interface IGGMenuPoxy {
	
	public static final int GLMENU_ID_FACEBOOK_LIKE_US = 127;
	public static final String SHOULD_SHOW_LIKE_US_LIGHT = "should_show_like_us_light"; // 需要显示like us的高亮图
	public static final String NEED_TO_SHOW_MORE_TIP_POINT = "need_to_show_more_tip_point";
	
	/**
	 * 获取是否需要显示更多的提示小点
	 * @param info
	 * @return
	 */
	public boolean getIsNeedToShowMoreTipPoint();
	
	/**
	 * 获取是否需要删除更多的提示小点
	 * @return
	 */
	public boolean getIsNeedRemoveMoreTipPoint();
}
