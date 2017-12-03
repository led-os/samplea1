package com.jiubang.ggheart.apps.desks.Preferences.info;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

/**
 * 分类选择框信息
 * @author yejijiong
 *
 */
public class DeskSettingClassifyInfo {
	
	private String mTitle;
	private Drawable mGridItemIconBg;
	private ArrayList<DeskSettingBaseInfo> mListItemInfoList = new ArrayList<DeskSettingBaseInfo>();
	private CharSequence[] mMultipleUnchooseValues;
	private CharSequence mCurSingleChooseValue;
	private CharSequence[] mCurMultipleChooseValue;
	/**
	 * 二级菜单的value值
	 */
	private CharSequence mMulitipleValue;
	/**
	 * 二级菜单的名称
	 */
	private CharSequence mMultipleValueText;
	/**
	 * 否要显示new标识
	 */
	private CharSequence[] mNeedShowNewTag;
	/**
	 * 需要付费才能使用的选项
	 */
	private CharSequence[] mNeedPayList;
	
	public void setGridItemIconBg(Drawable drawable) {
		mGridItemIconBg = drawable;
	}
	
	public Drawable getGridItemIconBg() {
		return mGridItemIconBg;
	}
	
	public CharSequence[] getNeedShowNewTag() {
		return mNeedShowNewTag;
	}

	public void setNeedShowNewTag(CharSequence[] needShowNewTag) {
		mNeedShowNewTag = needShowNewTag;
	}
	
	public CharSequence[] getNeedPayList() {
		return mNeedPayList;
	}

	public void setNeedPayList(CharSequence[] needPayList) {
		mNeedPayList = needPayList;
	}
	
	public CharSequence getCurSingleChooseValue() {
		return mCurSingleChooseValue;
	}

	public void setCurSingleChooseValue(CharSequence value) {
		mCurSingleChooseValue = value;
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
	
	public CharSequence getMultipleValue() {
		return mMulitipleValue;
	}
	
	public void setMultipleValue(CharSequence value) {
		mMulitipleValue = value;
	}

	public CharSequence getMultipleValueText() {
		return mMultipleValueText;
	}

	public void setMultipleValueText(CharSequence text) {
		mMultipleValueText = text;
	}

	public ArrayList<DeskSettingBaseInfo> getListItemInfoList() {
		return mListItemInfoList;
	}

	public void addListItemInfo(DeskSettingBaseInfo info) {
		mListItemInfoList.add(info);
	}
	
	public void setCurMultipleChooseValue(CharSequence[] values) {
		mCurMultipleChooseValue = values;
	}
	
	public CharSequence[] getCurMultipleChooseValue() {
		return mCurMultipleChooseValue;
	}

	public CharSequence[] getMultipleUnchooseValues() {
		return mMultipleUnchooseValues;
	}

	public void setMultipleUnchooseValues(CharSequence[] multipleUnchooseValues) {
		mMultipleUnchooseValues = multipleUnchooseValues;
	}
	
	/**
	 * 获取是否处于二级菜单状态
	 */
	public boolean getIsInMultiple() {
		return mCurSingleChooseValue.equals(mMulitipleValue);
	}
	
	/**
	 * <br>功能简述:获取选择值对应的内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public CharSequence getCurSingleChooseValueText() {
		if (mCurSingleChooseValue == mMulitipleValue) {
			return mMultipleValueText;
		} else {
			for (DeskSettingBaseInfo infos : mListItemInfoList) {
				for (int i = 0; i < infos.getEntryValues().length; i++) {
					CharSequence value = infos.getEntryValues()[i];
					if (mCurSingleChooseValue.equals(value)) {
						return infos.getEntries()[i];
					}
				}
			}
		}
		return "";
	}
	
}
