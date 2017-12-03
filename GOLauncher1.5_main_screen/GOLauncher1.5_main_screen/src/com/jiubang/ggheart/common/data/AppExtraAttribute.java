package com.jiubang.ggheart.common.data;

import android.content.ComponentName;

/**
 * 
 * @author wuziyi
 *
 */
public class AppExtraAttribute {
	
	private ComponentName mComponentName;
	private boolean mIsLock;
	private long mClickOpenTime;
	private boolean mIsNew;
	private String mData;
	
	/**
	 * 用户了解应用更新提示的时间
	 */
	private long mReadUpdateInfoTime;
	
	public AppExtraAttribute(ComponentName cn) {
		mComponentName = cn;
	}

	public long getReadUpdateInfoTime() {
		return mReadUpdateInfoTime;
	}

	public void setReadUpdateInfoTime(long lastCheckUpdateTime) {
		mReadUpdateInfoTime = lastCheckUpdateTime;
	}

	public ComponentName getComponentName() {
		return mComponentName;
	}
	public void setComponentName(ComponentName componentName) {
		mComponentName = componentName;
	}
	public boolean isLock() {
		return mIsLock;
	}
	public void setIsLock(boolean isLock) {
		mIsLock = isLock;
	}
	public long getClickTime() {
		return mClickOpenTime;
	}
	public void setClickTime(long clickTime) {
		mClickOpenTime = clickTime;
	}
	public boolean isNew() {
		return mIsNew;
	}
	public void setIsNew(boolean isNew) {
		mIsNew = isNew;
	}
	
	public String getData() {
		return mData;
	}
	
	public void setData(String data) {
		mData = data;
	}
}
