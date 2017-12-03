package com.jiubang.ggheart.tuiguanghuodong.double11.bean;


/**
 * 数据bean基类
 * 
 * @author chenshihang
 * 
 */
public class PromotionBaseBean {
	public String mValidStartTime = "";
	public String mValidEndTime = "";
	public String mShowStartTime = "";
	public String mShowEndTime = "";
	public String mLauncherStartVersion = "";
	public String mLauncherEndVersion = "";
	public long mScheduleRemoveTime = -1l;
	public long mScheduleNextCheckTime = -1l;
	
	/**
	 * 根据桌面版本号和消息结束时间判断消息是否过期
	 * @return
	 */
	public boolean isVaild() {
	
		return false;
	}
	
	
	public long getNextScheduleTime() {
		return -1;
	}
	
}
