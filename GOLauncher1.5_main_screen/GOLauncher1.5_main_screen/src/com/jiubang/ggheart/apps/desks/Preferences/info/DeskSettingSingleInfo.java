package com.jiubang.ggheart.apps.desks.Preferences.info;

/**
 * 
 * <br>类描述:单选对象
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-9-10]
 */
public class DeskSettingSingleInfo extends DeskSettingBaseInfo {
	private String mSelectValue; //选择的值
	private String mSelectValueTemp; //临时选择的值
	private CharSequence[] mNeedPayList; //需要付费才能使用的选项
	private CharSequence[] mNeedShowNewTag; // 是否要显示new标识

	public String getSelectValue() {
		return mSelectValue;
	}

	public void setSelectValue(String selectValue) {
		this.mSelectValue = selectValue;
	}

	public String getSelectValueTemp() {
		return mSelectValueTemp;
	}

	public void setSelectValueTemp(String selectValueTemp) {
		this.mSelectValueTemp = selectValueTemp;
	}

	public CharSequence[] getNeedPayList() {
		return mNeedPayList;
	}

	public void setNeedPayList(CharSequence[] needPayList) {
		this.mNeedPayList = needPayList;
	}
	
	public CharSequence[] getNeedShowNewTag() {
		return mNeedShowNewTag;
	}

	public void setNeedShowNewTag(CharSequence[] needShowNewTag) {
		this.mNeedShowNewTag = needShowNewTag;
	}
	/**
	 * Returns the entry corresponding to the current value.
	 * 
	 * @return The entry corresponding to the current value, or null.
	 */
	public CharSequence getEntry() {
		int index = getValueIndex();
		return index >= 0 && mEntries != null ? mEntries[index] : null;
	}



	/**
	 * <br>功能简述:获取选择值在哪个位置
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public int getValueIndex() {
		if (mSelectValue != null) {
			return findIndexOfValue(mSelectValue);
		}
		return -1;
	}

	/**
	 * <br>功能简述:获取选择值对应的内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public String getSelectValueText() {
		int index = getValueIndex();
		if (index != -1) {
			return mEntries[index].toString();
		}
		return "";
	}
}
