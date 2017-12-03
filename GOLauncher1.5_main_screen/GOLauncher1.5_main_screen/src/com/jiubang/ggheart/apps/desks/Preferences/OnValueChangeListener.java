package com.jiubang.ggheart.apps.desks.Preferences;

import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;

/**
 * 
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 * 
 * @author licanhui
 * @date [2012-9-10]
 */
public interface OnValueChangeListener {

	/**
	 * 
	 * @param baseView
	 * @param value 
	 * @return 返回值:是否拦截{@link #onValueChange(DeskSettingItemBaseView, Object)}的调用,默认是false
	 */
	public abstract boolean onPreValueChange(DeskSettingItemBaseView baseView, Object value);
	
	public abstract boolean onValueChange(DeskSettingItemBaseView baseView, Object value);

}
