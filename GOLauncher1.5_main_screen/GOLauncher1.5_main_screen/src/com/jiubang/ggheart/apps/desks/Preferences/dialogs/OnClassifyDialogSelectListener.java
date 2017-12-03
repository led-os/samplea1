package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

/**
 * 分类选择对话框监听器
 * @author yejijiong
 *
 */
public interface OnClassifyDialogSelectListener {

	public abstract boolean onDialogSelectValue(Object value);
	
	public abstract void onMultiSelectChange();
}
