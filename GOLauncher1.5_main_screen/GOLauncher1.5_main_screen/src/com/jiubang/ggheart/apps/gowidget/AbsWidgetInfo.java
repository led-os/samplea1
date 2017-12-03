package com.jiubang.ggheart.apps.gowidget;

import android.content.res.Resources;

/**
 * 
 * @author zouguiquan
 *
 */
public abstract class AbsWidgetInfo {
	public Resources resouces;
	public String title;
	public int resouceId;

	public int mRow;
	public int mCol;
	private int mAddIndex; // 添加时所在列表的位置

	public int mTotalWidth;
	public int mTotalHeight;

	public AbsWidgetInfo() {
	}

	public void setAddIndex(int addIndex) {
		mAddIndex = addIndex;
	}

	public int getAddIndex() {
		return mAddIndex;
	}
}
