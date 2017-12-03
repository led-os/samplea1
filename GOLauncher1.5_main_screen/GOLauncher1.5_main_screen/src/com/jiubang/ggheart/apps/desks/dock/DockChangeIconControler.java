/**
 * 
 */
package com.jiubang.ggheart.apps.desks.dock;

import android.content.Context;

import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean.DockBean;

/**
 * @author ruxueqin
 * 
 */
public class DockChangeIconControler {
	private Context mContext;

	private static DockChangeIconControler sControler;

	private DockBean mDockBean;

	private String mThemePkg;

	/**
	 * 
	 */
	public DockChangeIconControler(Context context) {
		mContext = context;
	}

	public static DockChangeIconControler getInstance(Context context) {
		if (null == sControler) {
			sControler = new DockChangeIconControler(context);
		}
		return sControler;
	}

	public DockBean getDockBean(String themePkg) {
		if (null == themePkg) {
			return null;
		}

		if (!themePkg.equals(mThemePkg)
				|| (null != mDockBean && null != mDockBean.mSymtemDefualt && mDockBean.mSymtemDefualt
						.isEmpty())) {
			mDockBean = ThemeManager.getInstance(ApplicationProxy.getContext()).getDockBeanFromTheme(themePkg);
			mThemePkg = themePkg;
		}
		return mDockBean;
	}
}
