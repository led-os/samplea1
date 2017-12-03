package com.jiubang.ggheart.apps.desks.appfunc.menu;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncUtils;

/**
 * 3D插件使用的SwitchMenu
 * @author yangguanxiang
 *
 */
public class ShellSwitchListMenu extends AppFuncSwitchListMenu {

	public ShellSwitchListMenu(Activity activity) {
		super(activity);
	}

	@Override
	protected void loadThemeResource() {
		super.loadThemeResource();
		mMenuBgV = mActivity.getResources().getDrawable(R.drawable.shell_switch_menu_bg);
	}

	@Override
	public void show(View parent) {
		if (!isShowing()) {
			mAdapter.setItemPadding(0, DrawUtils.dip2px(4), 0, 0);
			mAdapter.setItemTextSize(0);
			int offset = AppFuncUtils.getInstance(mActivity).getStandardSize(0);
			if (Machine.IS_SDK_ABOVE_KITKAT) {
				offset -= DrawUtils.getNavBarHeight();
			}
			show(parent, DrawUtils.dip2px(125), GoLauncherActivityProxy.getScreenHeight()
					- DrawUtils.dip2px(290) - offset,
					AppFuncUtils.getInstance(mActivity).getStandardSize(100),
					LayoutParams.WRAP_CONTENT);
		}
	}
	
	public void show(View parent, int x, int y, int width, int height) {
		mListView.clearFocus(); // 防止快速双击菜单残留点击颜色
		if (mPopupWindow != null && isShowing()) {	

			dismiss();
		}
		if (!mInitialized) {
			initialize();
		}
		if (mMenuBgV != null) {
			mListView.setBackgroundDrawable(mMenuBgV);
		}

		if (mMenuDividerV != null) {
			mListView.setDivider(mMenuDividerV);
		}

		mPopupWindow = new PopupWindow(mListView, width, height, true);
		mListView.setParent(this);
		// mPopupWindow.setAnimationStyle(R.style.AnimationZoom);
		initAnimationStyle(mPopupWindow);
		mPopupWindow.setFocusable(false);
		mPopupWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.BOTTOM, x, y);
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
	}
}
