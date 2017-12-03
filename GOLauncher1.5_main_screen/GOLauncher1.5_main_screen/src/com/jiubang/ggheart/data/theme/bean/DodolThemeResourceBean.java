package com.jiubang.ggheart.data.theme.bean;

import java.util.concurrent.ConcurrentHashMap;
/**
 * 
 *
 */
public class DodolThemeResourceBean extends ThemeBean {

	private ConcurrentHashMap<Integer, ThemeBean> mThemeResourceMap = null;

	public DodolThemeResourceBean(String pkgName) {
		super(pkgName);
		mThemeResourceMap = new ConcurrentHashMap<Integer, ThemeBean>();
		mThemeResourceMap.put(THEMEBEAN_TYPE_APPDATA, new AppDataThemeBean(pkgName));
		mThemeResourceMap.put(THEMEBEAN_TYPE_FUNCAPP, new AppFuncThemeBean(pkgName));
		mThemeResourceMap.put(THEMEBEAN_TYPE_DESK, new DeskThemeBean(pkgName));
	}

	public ConcurrentHashMap<Integer, ThemeBean> getThemeResourceMap() {
		return mThemeResourceMap;
	}
	
}
