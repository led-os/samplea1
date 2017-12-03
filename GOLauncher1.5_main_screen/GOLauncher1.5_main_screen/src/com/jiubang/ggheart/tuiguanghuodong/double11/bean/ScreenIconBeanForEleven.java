package com.jiubang.ggheart.tuiguanghuodong.double11.bean;

import android.graphics.drawable.Drawable;

/**
 * 
 * @author zhujian
 * 
 */
public class ScreenIconBeanForEleven extends PromotionBaseBean {

	public String mStartDate;
	public String mEndDate;
	public String mTime;
	public Drawable mDrawable;
	
	// 最新
	public String mVersion;
	public String mName;
	public String mUrl;
	public String mCount;
	public String mIconUrl;
	public String mIconName;

	public ScreenIconBeanForEleven() {
//		mName = "抢购";
//		mDrawable = ApplicationProxy.getContext().getResources()
//				.getDrawable(R.drawable.double11_screenshortcut2);
	}
	
	public String toString() {
		return "ScreenIconBean [version=" + mVersion + ", launcherStartVersion=" + mLauncherStartVersion
				+ ", launcherEndVerstion=" + mLauncherEndVersion + ", validStartTime=" + mValidStartTime
				+ ", validEndTime=" + mValidEndTime + ", url=" + mUrl
				+ ", showStartTime=" + mShowStartTime + ", showEndTime=" + mShowEndTime 
				+ ", mIconUrl=" + mIconUrl + ", mIconName=" + mIconName + "]";
	}
}
