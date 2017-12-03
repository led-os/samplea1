package com.jiubang.ggheart.apps.desks.Preferences.info;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * 分类对话款子组件info
 * @author yejijiong
 *
 */
public class DeskSettingClassifyGridItemInfo {
	/**
	 * 图标
	 */
	private Drawable mIcon;
	/**
	 * 不能使用状态的图标
	 */
	private Bitmap mUnableIcon;
	/**
	 * 标题
	 */
	private CharSequence mTitle;
	/**
	 * 图标背景
	 */
	private Drawable mIconBg;
	/**
	 * 多选状态下是否可选
	 */
	private boolean mMultipleCanChoose = true;
	/**
	 * 单选状态
	 */
	public static final int STATUS_SINGLE_CHOOSE = 0;
	/**
	 * 多选状态
	 */
	public static final int STATUS_MULTI_CHOOSE = 1;
	/**
	 * 当前状态
	 */
	private int mCurStatus = STATUS_SINGLE_CHOOSE;
	/**
	 * 是否单选下当前选中的值
	 */
	private boolean mIsCurSingleChoose = false;
	/**
	 * 是否多选下当前选中的值
	 */
	private boolean mIsCurMultipleChoose = false;
	private CharSequence mEntrie;
	private CharSequence mEntryValue;
	/**
	 * 是否显示New标识
	 */
	private boolean mIsNeedShowNew = false;
	/**
	 * 是否显示Prime标识
	 */
	private boolean mIsPrime = false;
	
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}
	
	public Drawable getIcon() {
		return mIcon;
	}
	
	public void setUnableIcon(Bitmap unableIcon) {
		mUnableIcon = unableIcon;
	}
	
	public Bitmap getUnableIcon() {
		return mUnableIcon;
	}
	
	public void setIconBg(Drawable iconBg) {
		mIconBg = iconBg;
	}
	
	public Drawable getIconBg() {
		return mIconBg;
	}
	
	public void setTitle(CharSequence title) {
		mTitle = title;
	}
	
	public CharSequence getTitle() {
		return mTitle;
	}
	
	public void setIsNeedShowNew(boolean isNeedShowNew) {
		mIsNeedShowNew = isNeedShowNew;
	}
	
	public boolean isNeedShowNew() {
		return mIsNeedShowNew;
	}
	
	public void setIsPrime(boolean isPrime) {
		mIsPrime = isPrime;
	}
	
	public boolean isPrime() {
		return mIsPrime;
	}
	
	public void setEntrie(CharSequence entrie) {
		mEntrie = entrie;
	}
	
	public CharSequence getEntrie() {
		return mEntrie;
	}
	
	public void setEntryValue(CharSequence value) {
		mEntryValue = value;
	}
	
	public CharSequence getEntryValue() {
		return mEntryValue;
	}
	
	public boolean isMultipleCanChoose() {
		return mMultipleCanChoose;
	}
	public void setMultipleCanChoose(boolean multipleCanChoose) {
		mMultipleCanChoose = multipleCanChoose;
	}
	public int getCurStatus() {
		return mCurStatus;
	}
	public void setCurStatus(int status) {
		mCurStatus = status;
	}
	public boolean isCurSingleChoose() {
		return mIsCurSingleChoose;
	}
	public void setIsCurSingleChoose(boolean isCurSingleChoose) {
		mIsCurSingleChoose = isCurSingleChoose;
	}
	public boolean isCurMultipleChoose() {
		return mIsCurMultipleChoose;
	}
	public void setIsCurMultipleChoose(boolean isCurMultipleChoose) {
		mIsCurMultipleChoose = isCurMultipleChoose;
	}
}
