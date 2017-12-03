package com.jiubang.ggheart.iconconfig;

import android.content.ComponentName;
import android.text.TextUtils;


/**
 * 应用图标配置数据Bean
 * @author caoyaming
 *
 */
public class AppIconConfigBean {
	//应用ComponentName
	private String mComponentName;
	//显示未读数字,默认0
	private int mShowNumber;
	//开始版本(如:4.14)
	private String mStartVersion;
	//结束版本(如:4.15)
	private String mEndVersion;
	//开始时间(yyyyMMddHHmmss)
	private long mValidStartTime;
	//结束时间(yyyyMMddHHmmss)
	private long mValidEndTime;
	public AppIconConfigBean() {
	}
	public String getmComponentName() {
		return mComponentName;
	}
	public void setmComponentName(String mComponentName) {
		this.mComponentName = mComponentName;
	}
	/**
	 * 将字符串转换成ComponentName类型
	 * @return
	 */
	public ComponentName getmComponentNameToComponentName() {
		if (TextUtils.isEmpty(mComponentName) || mComponentName.lastIndexOf("{") < 0 || mComponentName.lastIndexOf("}") < mComponentName.lastIndexOf("{")) {
			return null;
		}
		try {
			String temp = mComponentName.substring(mComponentName.lastIndexOf("{") + 1, mComponentName.lastIndexOf("}"));
			if (TextUtils.isEmpty(temp) || temp.indexOf("/") < 1) {
				return null;
			}
			String[] componentNameArray = temp.split("/");
			return new ComponentName(componentNameArray[0], componentNameArray[1]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public int getmShowNumber() {
		return mShowNumber;
	}
	public void setmShowNumber(int mShowNumber) {
		this.mShowNumber = mShowNumber;
	}
	public String getmStartVersion() {
		return mStartVersion;
	}
	public void setmStartVersion(String mStartVersion) {
		this.mStartVersion = mStartVersion;
	}
	public String getmEndVersion() {
		return mEndVersion;
	}
	public void setmEndVersion(String mEndVersion) {
		this.mEndVersion = mEndVersion;
	}
	public long getmValidStartTime() {
		return mValidStartTime;
	}
	public void setmValidStartTime(long mValidStartTime) {
		this.mValidStartTime = mValidStartTime;
	}
	public long getmValidEndTime() {
		return mValidEndTime;
	}
	public void setmValidEndTime(long mValidEndTime) {
		this.mValidEndTime = mValidEndTime;
	}
}
