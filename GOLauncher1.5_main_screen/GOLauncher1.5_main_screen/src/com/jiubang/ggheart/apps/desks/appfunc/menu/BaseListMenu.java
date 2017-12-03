package com.jiubang.ggheart.apps.desks.appfunc.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.SettingProxy;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.appfunc.theme.AppFuncThemeController;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants;
import com.jiubang.ggheart.apps.desks.appfunc.model.DeliverMsgManager;
import com.jiubang.ggheart.apps.desks.appfunc.model.INotifyHandler;
import com.jiubang.ggheart.data.theme.ThemeManager;

/**
 * 菜单基类
 */
public abstract class BaseListMenu extends BaseMenu implements OnItemClickListener, INotifyHandler {

	protected BaseListMenuView mListView;

	protected Drawable mMenuBgV; // 背景(竖屏)
	protected Drawable mMenuDividerV; // 分割线（竖屏）
	/**
	 * 菜单项被选中后的背景图
	 */
	protected Drawable mItemSelectedBg;
	protected int mTextColor; // 颜色
	protected BaseMenuAdapter mAdapter;
	protected boolean mInitialized;

	protected FunAppSetting mFunAppSetting;

	public BaseListMenu(Activity activity) {
		mActivity = activity;
		mThemeCtrl = AppFuncThemeController.getInstance(mActivity);
		mListView = new BaseListMenuView(mActivity);
		mFunAppSetting = SettingProxy.getFunAppSetting();
		// 注册主题变更事件
		DeliverMsgManager.getInstance().registerDispenseMsgHandler(AppFuncConstants.LOADTHEMERES,
				this);
		loadThemeResource();
	}

	protected void loadThemeResource() {
		String curPackageName = ThemeManager.getInstance(mActivity).getCurThemePackage();
		String packageName = null;
		if (!curPackageName.equals(SettingProxy.getFunAppSetting()
				.getTabHomeBgSetting())) {
			packageName = SettingProxy.getFunAppSetting()
					.getTabHomeBgSetting();
		}
		if (!GoAppUtils.isAppExist(mActivity, packageName)) {
			packageName = ThemeManager.getInstance(ApplicationProxy.getContext()).getCurThemePackage();
		}
		
		mMenuBgV = mThemeCtrl.getDrawable(mThemeCtrl.getThemeBean().mAllAppMenuBean.mMenuBgV,
				packageName);
		if (mMenuBgV == null) {
			mMenuBgV = mActivity.getResources().getDrawable(R.drawable.allapp_menu_bg_vertical);
			mTextColor = 0xff000000;
		} else {
			mTextColor = mThemeCtrl.getThemeBean().mAllAppMenuBean.mMenuTextColor;
		}
		mMenuDividerV = mThemeCtrl.getDrawable(
				mThemeCtrl.getThemeBean().mAllAppMenuBean.mMenuDividerV, packageName);
		if (mMenuDividerV == null) {
			mMenuDividerV = mActivity.getResources().getDrawable(
					R.drawable.allfunc_allapp_menu_line);
		}

		mItemSelectedBg = mThemeCtrl.getDrawable(
		mThemeCtrl.getThemeBean().mAllAppMenuBean.mMenuItemSelected, packageName);
		if (mItemSelectedBg == null) {
			mItemSelectedBg = mActivity.getResources().getDrawable(R.drawable.menu_item_background);
		}

	}

	protected void initialize() {
		mAdapter.setTextColor(mTextColor);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		if (mItemSelectedBg != null) {
			mListView.setSelector(mItemSelectedBg);
		}
		mListView.setLayoutParams(layoutParams);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnKeyListener(this);
		mListView.setAlwaysDrawnWithCacheEnabled(true);
		mListView.setSelectionAfterHeaderView();
		mListView.setSmoothScrollbarEnabled(true);
		mInitialized = true;
	}

	public void show(View parent, int x, int y, int width, int height) {
		mListView.clearFocus(); // 防止快速双击菜单残留点击颜色
		if (mPopupWindow != null && isShowing()) {
			dismiss();
		}
		if (!mInitialized) {
			initialize();
		}
		if (GoLauncherActivityProxy.isPortait()) {
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
		} else {
			if (mMenuBgV != null) {
				mListView.setBackgroundDrawable(mMenuBgV);
			}

			if (mMenuDividerV != null) {
				mListView.setDivider(mMenuDividerV);
			}

			mPopupWindow = new PopupWindow(mListView, width, height, true);
			mListView.setParent(this);
			// mPopupWindow.setAnimationStyle(R.style.AnimationZoomH);
			initAnimationStyle(mPopupWindow);
			mPopupWindow.setFocusable(false);
			mPopupWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, x, y);
			mPopupWindow.setFocusable(true);
			mPopupWindow.update();
		}

	}
	
	/**
	 * 以竖屏的菜单动作方式弹出菜单
	 * @param parent
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void showByVerticalAnimation(View parent, int x, int y, int width, int height) {
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
		mPopupWindow.setAnimationStyle(R.style.AnimationZoom);
		mPopupWindow.setFocusable(false);
		mPopupWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.BOTTOM, x,
				y);
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
	}

	protected void initAnimationStyle(PopupWindow popupWindow) {
		if (popupWindow != null) {
			if (GoLauncherActivityProxy.isPortait()) {
				popupWindow.setAnimationStyle(R.style.AnimationZoom);
			} else {
				popupWindow.setAnimationStyle(R.style.AnimationZoomH);
			}
		}
	}

	@Override
	public void dismiss() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	@Override
	public boolean isShowing() {
		if (mPopupWindow != null) {
			return mPopupWindow.isShowing();
		}
		return false;
	}

	public void setAdapter(BaseMenuAdapter adapter) {
		mAdapter = adapter;
	}

	public void setItemResources(ArrayList<BaseMenuItemInfo> itemInfos) {
		if (mAdapter == null) {
			mAdapter = new BaseMenuAdapter(mActivity, itemInfos);
		} else {
			mAdapter.setItemList(itemInfos);
			mAdapter.notifyDataSetChanged();
		}
	}

	public void setItemResources(int[] textResIds) {
		int size = textResIds.length;
		ArrayList<BaseMenuItemInfo> itemInfos = new ArrayList<BaseMenuItemInfo>(size);
		for (int resId : textResIds) {
			BaseMenuItemInfo itemInfo = new BaseMenuItemInfo();
			itemInfo.mTextId = resId;
			itemInfos.add(itemInfo);
		}
		setItemResources(itemInfos);
	}

	@Override
	public void notify(int key, Object obj) {
		// TODO Auto-generated method stub
		switch (key) {
			case AppFuncConstants.LOADTHEMERES :
				loadThemeResource();
				mInitialized = false;
				break;

			default :
				break;
		}
	}

	@Override
	public void recyle() {
		mActivity = null;
		mThemeCtrl = null;
		mListView = null;
		mFunAppSetting = null;
		// 反注册主题变更事件
		DeliverMsgManager.getInstance().unRegisterDispenseMsgHandler(AppFuncConstants.LOADTHEMERES,
				this);
		// 字体资源释放
		DeskSettingConstants.selfDestruct(mListView);
	}
}
