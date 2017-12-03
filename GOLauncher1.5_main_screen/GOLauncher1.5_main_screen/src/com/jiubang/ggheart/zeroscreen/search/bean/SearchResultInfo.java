package com.jiubang.ggheart.zeroscreen.search.bean;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.jb.util.pySearch.SearchResultItem;
import com.jiubang.ggheart.zeroscreen.search.contact.ContactDataItem;
/**
 * 搜索结果
 * @author liulixia
 *
 */
public class SearchResultInfo {
	public int mType = -1;
	/**
	 * 联系人类型
	 */
	public static final int ITEM_TYPE_CONTACTS = 0;
	/**
	 * 应用类型
	 */
	public static final int ITEM_TYPE_APP = 1;
	/**
	 * title头
	 */
	public static final int ITEM_TYPE_TITLE = 2;
	/**
	 * 查看更多联系人
	 */
	public static final int ITEM_TYPE_GET_MROE_CONTACTS = 3;
	
	/**
	 * 匹配关键字在标题中的索引值
	 */
	public int mMatchIndex = -1;
	/**
	 * 匹配的字符个数
	 */
	public int mMatchWords = 0;
	/**
	 * 联系人信息
	 */
	public ContactDataItem mPersonInfo;
	
	/**
	 * 应用程序标题
	 */
	public String mTitle;
	/**
	 * 应用程序图标
	 */
	public Drawable mIcon;
	
	public Intent mIntent;
	
	public String mLastOpenTime = null;
	
	public boolean mShowBottomLine = true;
	/**
	 * 根据搜索结果设置标题
	 * 
	 * @param title
	 * @param item
	 */
	public void setTitle(String title, SearchResultItem item) {
		if (item != null) {
			mMatchIndex = item.mMatchPos;
			mMatchWords = item.mMatchWords;
		}
		mTitle = title;
	}
}
