package com.jiubang.ggheart.screen.systemsettings;

import android.content.Intent;

/**
 * 系统设置的数据bean
 * @author chenguanyu
 *
 */
public class SystemSettingBean {

	public String mAction = null;
	public Intent mIntent = null;
	public int mLabelId = -1;
	public int mIcon = -1;
	
	public SystemSettingBean(String action) {
		mAction = action;
	}
	
	/**
	 * 清理资源
	 */
	public void cleanData() {
		if (mAction != null) {
			mAction = null;
		}
		
		if (mIntent != null) {
			mIntent = null;
		}
	}
}
