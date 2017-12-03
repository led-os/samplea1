package com.jiubang.ggheart.components.appmanager;

import com.jiubang.ggheart.data.info.ItemInfo;

/**
 * 
 * <br>类描述:桌面清理扫描对象
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-9]
 */
public class CleanScreenInfo {
	public int mId; //id
	public int mParentId; //父id
	public ItemInfo mItemInfo; //图标对象
	public String mFartherTitle; //分类标题
	public int mNoResultTitle; //分类标题
	public boolean mIsCheck; //是否勾选
	public boolean mIsShowCheckBox = true; //是否显示checkbox
	public int mType; // 类型
	public long mFolderId; //如果是文件夹里面的图标所在文件夹id
}
