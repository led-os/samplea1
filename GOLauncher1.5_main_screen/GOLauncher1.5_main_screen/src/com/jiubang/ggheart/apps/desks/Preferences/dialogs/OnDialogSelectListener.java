package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-9-13]
 */
public interface OnDialogSelectListener extends OnDialogButtonClickListener {

	/**
	 * 
	 * @param value
	 * @return 返回值:是否拦截{@link #onDialogSelectValue(Object)}的调用,默认是false,
	 * 注意,若拦截了,则该次选中不会赋值到info
	 */
	public abstract boolean onPreDialogSelectValue(Object value);
	
	public abstract boolean onDialogSelectValue(Object value);
	
}
