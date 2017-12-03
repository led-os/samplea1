/**
 * 
 */
package com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.graphics.DrawUtils;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenPreviewMsgId;
import com.jiubang.core.framework.AbstractFrame;
import com.jiubang.core.framework.IFrameManager;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.DesktopIndicator;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.IndicatorListner;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicator;
import com.jiubang.ggheart.apps.desks.diy.frames.screen.ScreenIndicatorItem;
import com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl.tutorial.SensePreviewTutorial;
import com.jiubang.ggheart.apps.gowidget.gostore.views.ScrollerViewGroup;
import com.jiubang.ggheart.data.statistics.StaticTutorial;

/**
 * @author ruxueqin
 * 
 */
public class GuideForGlFrame extends AbstractFrame
		implements
			ScreenScrollerListener,
			IndicatorListner {
	private View mLayout;

	private LayoutInflater mInflater;
	// Tips类型
	private static int sGuideType;
	//  public static final int GUIDE_TYPE_SCREEN_EFFECT = 1; //屏幕切换特效提示
	//  public static final int GUIDE_TYPE_SCREEN_EFFECT_SECOND_TIP = 2;
	// //屏幕切换特效第二个提示，是否使用这个特效询问
	public static final int GUIDE_TYPE_FUNC_FOLDER = 6; // 功能表文件夹提示
	//	public static final int GUIDE_TYPE_FUNC_DRAG = 7; // 功能表拖动提示
	// TODO:现在已经不属于桌面引导，而是添加模块里面的一个设置界面，可以考虑抽离出去 -By Yugi 2012.8.2
	//  public static final int GUIDE_TYPE_WALLPAPER_SETTING = 8; //单／双屏壁纸向导提示
	//	public static final int GUIDE_TYPE_SCREEN_MENU_OPEN = 9; // 桌面向上滑动打开菜单提示
	//	public static final int GUIDE_TYPE_SCREENFOLDER = 11; // 桌面文件夹向导提示
	// TODO:把第二页跟第一页整合起来，没必要分开两个frame -By Yugi 2012.8.2
	//	public static final int GUIDE_TYPE_SCREEN_EDIT_NEXT_PAGE = 12; // 屏幕编辑的第二页提示
	//	public static final int GUIDE_TYPE_DOCK_AUTO_FIT = 13; // Dock栏自适应提示 
	public static final int GUIDE_TYPE_SCREEN_EDIT = 1; // 屏幕编辑提示
	public static final int GUIDE_TYPE_CUSTOM_GESTURE = 2; // 自定义手势提示
	public static final int GUIDE_TYPE_DOCK_BAR_ICON_GESTURE = 3; // DOCK栏拖动删除 /添加 icon
	public static final int GUIDE_TYPE_FULL_SCREEN_WIDGET = 4;
	public static final int GUIDE_TYPE_APPDRAWER_SLIDEMENU_GUIDE = 5;

	private static final int SET_SHOW_HIDE_STATUSBAR = -2;
	private DesktopIndicator mIndicator;

	private ScrollerViewGroup mGuideViewGroup;
	private Activity mActivity = null;
	private RelativeLayout mGuideLayout;
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public GuideForGlFrame(Activity arg0, IFrameManager arg1, int arg2) {
		super(arg0, arg1, arg2);
		mActivity = arg0;
		initLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jiubang.core.framework.AbstractFrame#getContentView()
	 */
	@Override
	public View getContentView() {
		return mLayout;
	}

	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		boolean ret = false;
		switch (msgId) {
			case ICommonMsgId.BACK_TO_MAIN_SCREEN : {
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
				ret = true;
				break;
			}

			case IFrameworkMsgId.SYSTEM_CONFIGURATION_CHANGED : {
				try {
					if (sGuideType == GUIDE_TYPE_DOCK_BAR_ICON_GESTURE) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
								IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
								null);
						GuideForGlFrame
								.setmGuideType(GuideForGlFrame.GUIDE_TYPE_DOCK_BAR_ICON_GESTURE);
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
										IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME,
										null, null);
					}
					/*
					 * if (mGuideType == GUIDE_TYPE_SCREEN_EFFECT) {
					 * ((GuideForScreenEffectView
					 * )mLayout).changeOritation(param); }else if (mGuideType ==
					 * GUIDE_TYPE_SCREEN_EFFECT_SECOND_TIP) {
					 * ((GuideForScreenEffectSecondTipView
					 * )mLayout).changeOritation (param); }
					 */
					// else if (mGuideType == GUIDE_TYPE_WALLPAPER_SETTING) {
					// MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					// IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME,
					// null,
					// null);
					// GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_WALLPAPER_SETTING);
					// MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					// IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
					// null);
					// }
					//					if (sGuideType == GUIDE_TYPE_FUNC_DRAG) {
					//						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					//								IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
					//								null);
					//						StaticTutorial.sCheckFuncDrag = true;
					//						PreferencesManager sharedPreferences = new PreferencesManager(mActivity,
					//								IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
					//						Editor editor = sharedPreferences.edit();
					//						editor.putBoolean(IPreferencesIds.SHOULD_SHOW_APPFUNC_DRAG_GUIDE, true);
					//						editor.commit();
					//						GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_FUNC_DRAG);
					//						MsgMgrProxy.sendHandler(this, IDiyFrameIds.APPFUNC_FRAME,
					//								IDiyMsgIds.APPDRAWER_ENTER_DRAG_TUTORIAL, -1, null, null);
					//					} else 
					//				    if (sGuideType == GUIDE_TYPE_SCREEN_EDIT
					//							|| sGuideType == GUIDE_TYPE_SCREEN_EDIT_NEXT_PAGE) {
					//						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					//								IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
					//								null);
					//					} else if (sGuideType == GUIDE_TYPE_SCREENFOLDER) {
					//						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					//								IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
					//								null);
					//					} else if (sGuideType == GUIDE_TYPE_SCREEN_MENU_OPEN) {
					//						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					//								IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
					//								null);
					//						GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_SCREEN_MENU_OPEN);
					//						GoLauncher
					//								.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					//										IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME,
					//										null, null);
					//					} else if (sGuideType == GUIDE_TYPE_DOCK_AUTO_FIT) {
					//						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					//								IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null,
					//								null);
					//						GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_DOCK_AUTO_FIT);
					//						GoLauncher
					//								.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					//										IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME,
					//										null, null);
					//					}
				} catch (ClassCastException e) {
				}
			}
				break;

			case IScreenPreviewMsgId.PREVIEW_CURRENT_SCREEN_INDEX : {
				if (sGuideType == GUIDE_TYPE_SCREEN_EDIT) {
					SensePreviewTutorial sensePreviewTutorial = (SensePreviewTutorial) mLayout
							.findViewById(R.id.screentutorial);
					if (null != sensePreviewTutorial) {
						sensePreviewTutorial.setPreviewCurrentScreen(param, obj[0]);
					}
				}
			}
				break;

			default :
				break;

		}
		return ret;
	}

	private void initLayout() {
		mInflater = mActivity.getLayoutInflater();

		switch (sGuideType) {
		// case GuideForGlFrame.GUIDE_TYPE_SCREEN_EFFECT:
		// //桌面滑屏特效帮助提示
		// mLayout = inflater.inflate(R.layout.guideforscreeneffect, null);
		// break;

		// case GUIDE_TYPE_SCREEN_EFFECT_SECOND_TIP:
		// //桌面滑屏特效帮助第二提示
		// mLayout = inflater.inflate(R.layout.guideforscreeneffectsecond,
		// null);
		// break;
			case GUIDE_TYPE_APPDRAWER_SLIDEMENU_GUIDE:
				mLayout = mInflater.inflate(R.layout.appdrawer_slidemenu_tutorial, null);
				if (!StatusBarHandler.isHide()) {
					mLayout.setPadding(0, StatusBarHandler.getStatusbarHeight(), 0, 0);
				}
				
//				AppDrawGuideTutorial appDrawGuideTutorial = (AppDrawGuideTutorial) mLayout
//						.findViewById(R.id.appdrawguidetutorial);
				break;

			case GUIDE_TYPE_SCREEN_EDIT :
				mLayout = mInflater.inflate(R.layout.sensetutoria, null);
				break;

			case GUIDE_TYPE_FUNC_FOLDER :
				mLayout = mInflater.inflate(R.layout.appfuncfolderguide, null);
				break;

			// case GUIDE_TYPE_WALLPAPER_SETTING:
			// mLayout =
			// inflater.inflate(R.layout.guide_screen_wallpaper_setting,
			// null);
			// break;
			//
			case GUIDE_TYPE_CUSTOM_GESTURE :
				initGuideViewGroup();
				getScreenScroller().setCurrentScreen(0);
				mIndicator.setCurrent(0);
				mLayout = mGuideLayout;
				break;

			case GUIDE_TYPE_DOCK_BAR_ICON_GESTURE :
				initGuideViewGroup();
//				getScreenScroller().setCurrentScreen(1);
				getScreenScroller().setCurrentScreen(0);
//				mIndicator.setCurrent(1);
				mIndicator.setCurrent(0);
				mLayout = mGuideLayout;
				break;
			default :
				// 没找到，退出帮助层
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						IFrameworkMsgId.REMOVE_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
				break;
		}
	}

	private ScrollerViewGroup initGuideViewGroup() {
		mGuideLayout = new RelativeLayout(mActivity);
        if (!StatusBarHandler.isHide()) {
            mGuideLayout.setPadding(0, StatusBarHandler.getStatusbarHeight(),
                    0, 0);
		}
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mGuideViewGroup = new ScrollerViewGroup(mActivity, this);
		mGuideViewGroup.setLayoutParams(layoutParams);
		mGuideLayout.addView(mGuideViewGroup);
        //		View gestureVIew = mInflater.inflate(R.layout.guide_for_custom_gesture, null);
        View dockBarVIew = mInflater.inflate(R.layout.guide_for_dock_bar_icon,
                null);

        //有虚拟键盘以及3D模式下在代码设置边距
        if (DrawUtils.isNavBarAvailable() && GoLauncherActivityProxy.isPortait()) {
            ImageView moveDesk = (ImageView) dockBarVIew
                    .findViewById(R.id.img_move_to_desk);
            ImageView moveDock = (ImageView) dockBarVIew
                    .findViewById(R.id.img_move_to_dock);
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            Resources resources = mActivity.getResources();
            lp1.setMargins(
                    resources
                            .getDimensionPixelSize(R.dimen.dock_left_guesture_left),
                    resources
                            .getDimensionPixelSize(R.dimen.dock_left_guesture_top),
                    resources
                            .getDimensionPixelSize(R.dimen.dock_left_guesture_right),
                    resources
                            .getDimensionPixelSize(R.dimen.dock_left_guesture_bottom));
            moveDesk.setLayoutParams(lp1);

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            lp2.setMargins(
                    resources
                            .getDimensionPixelSize(R.dimen.dock_right_guesture_left),
                    resources
                            .getDimensionPixelSize(R.dimen.dock_right_guesture_top),
                    resources
                            .getDimensionPixelSize(R.dimen.dock_right_guesture_right),
                    resources
                            .getDimensionPixelSize(R.dimen.dock_right_guesture_bottom));
            moveDock.setLayoutParams(lp2);
        }
		
//		mGuideViewGroup.addView(gestureVIew);
		mGuideViewGroup.addView(dockBarVIew);
		mGuideViewGroup.setScreenCount(0);
		initIndicator();
		mGuideLayout.addView(mIndicator);
		return mGuideViewGroup;
	}

	private void initIndicator() {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.topMargin = DrawUtils.dip2px(4);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		mIndicator = new DesktopIndicator(mActivity);
		mIndicator.setVisibility(View.VISIBLE);
		mIndicator.setLayoutParams(layoutParams);
		mIndicator.setIndicatorListner(this);
		mIndicator.setDefaultDotsIndicatorImage(R.drawable.guide_indicator_light,
				R.drawable.guide_indicator_normal);
		mIndicator.setDotIndicatorLayoutMode(ScreenIndicator.LAYOUT_MODE_ADJUST_PICSIZE);
		mIndicator.setDotIndicatorDrawMode(ScreenIndicatorItem.DRAW_MODE_INDIVIDUAL);
		if (mGuideViewGroup != null) {
			mIndicator.setTotal(mGuideViewGroup.getChildCount());
		}
	}
	/**
	 * @return the mGuideType
	 */
	public static int getmGuideType() {
		return sGuideType;
	}

	/**
	 * @param mGuideType
	 *            the mGuideType to set
	 */
	public static void setmGuideType(int mGuideType) {
		GuideForGlFrame.sGuideType = mGuideType;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// //不发送消息移除当前的frame了,即当出现用户帮助时 ，不能按下back键后退。(添加界面，壁纸除外)
			// if(GuideForGlFrame.mGuideType == GUIDE_TYPE_WALLPAPER_SETTING){
			// MsgMgrProxy.sendMessage(this,
			// IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME, getId(),
			// null, null);
			// }
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onRemove() {
		super.onRemove();

		if (sGuideType == GUIDE_TYPE_SCREEN_EDIT) {
			// 取消全屏
			if (!StatusBarHandler.isHide()) {
				// SensePreviewFrame.previewOperate = true;
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICommonMsgId.SHOW_HIDE_STATUSBAR, SET_SHOW_HIDE_STATUSBAR, false, null);
			}
			//		} else if (sGuideType == GUIDE_TYPE_SCREEN_MENU_OPEN) {
			//
			//			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IDiyMsgIds.OPEN_SCREEN_MENU,
			//					-1, null, null);
			//		} else if (sGuideType == GUIDE_TYPE_DOCK_AUTO_FIT) {
			//			MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
			//					IDiyMsgIds.DOCK_AUTO_FIT_GUIDE_QUITE, -1, null, null);
		}
		DeskSettingConstants.selfDestruct(mLayout);
	}

	@Override
	public ScreenScroller getScreenScroller() {
		if (mGuideViewGroup != null) {
			return mGuideViewGroup.getScreenScroller();
		}
		return null;
	}

	@Override
	public void setScreenScroller(ScreenScroller scroller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFlingIntercepted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFlingStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollChanged(int newScroll, int oldScroll) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
		updateIndicator(newScreen);
	}

	@Override
	public void onScrollFinish(int currentScreen) {
		final PreferencesManager sharedPreferences = new PreferencesManager(
				mActivity, IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
		switch (currentScreen) {
			case 0 :
				StaticTutorial.sCheckCustomGesture = false;
				sharedPreferences.putBoolean(IPreferencesIds.SHOULD_SHOW_CUSTOM_GESTURE, false);
				break;
			case 1 :
				StaticTutorial.sCheckDockBarIcon = false;
				sharedPreferences.putBoolean(IPreferencesIds.SHOULD_SHOW_DOCK_BAR_ICON_GESTURE,
						false);
				break;
			default :
				break;
		}
		sharedPreferences.commit();
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scrollBy(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getScrollX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScrollY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clickIndicatorItem(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sliding(float percent) {
		// TODO Auto-generated method stub

	}
	private void updateIndicator(int position) {
		if (mIndicator != null && mGuideViewGroup != null && position >= 0
				&& position < mGuideViewGroup.getChildCount()) {
			mIndicator.setCurrent(position);
		}
	}
}
