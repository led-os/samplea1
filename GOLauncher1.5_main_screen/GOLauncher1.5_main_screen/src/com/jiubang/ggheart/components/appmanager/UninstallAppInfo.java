package com.jiubang.ggheart.components.appmanager;

import com.jiubang.ggheart.data.info.AppItemInfo;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-7-31]
 */
public class UninstallAppInfo implements Comparable<UninstallAppInfo> {
	public int mId; //id
	public int mParentId; //父id
	public AppItemInfo mAppItemInfo; //图标对象
	public String mFartherTitle; //分类标题
	public int mNoResultTitle; //分类标题
	public boolean mIsCheck; //是否勾选
	public boolean mIsShowCheckBox = true; //是否显示checkbox
	public int mType; // 类型
	public long mClickTime = 0; //上次点击打开的时间
	
	@Override
	public int compareTo(UninstallAppInfo another) {
		//用位置排序
		if (this.mClickTime > another.mClickTime) {
			return 1;
		} else {
			return -1;
		}
	}
}
