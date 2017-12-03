package com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.proxy.ValueReturned;
import com.go.proxy.VersionControl;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.ICoverFrameMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenPreviewMsgId;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.apps.desks.diy.frames.cover.CoverFrame;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.GlobalSetConfig;
import com.jiubang.ggheart.data.info.GestureSettingInfo;
import com.jiubang.ggheart.data.statistics.StaticTutorial;
import com.jiubang.ggheart.plugin.shell.IViewId;
import com.jiubang.ggheart.plugin.shell.ShellPluginFactory;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2013-1-7]
 */
public class GuideControler implements IMessageHandler {

	public static final int CLOUD_ID_DOCK_GESTURE = 0x1;
	public static final int CLOUD_ID_CUSTOM_GESTURE = 0x2;
	public static final int CLOUD_ID_SCREEN_PRIVIEW = 0x3;
	public static final int CLOUD_ID_SUPER_WIDGET = 0x4;
	public static final int CLOUD_ID_APPDRAWER_SIDEBAR = 0x5;

	public static final int CLOUD_TYPE_SCREEN = 0x4;
	public static final int CLOUD_TYPE_SCREEN_PRIVIEW = 0x5;
	public static final int CLOUD_TYPE_APPDRAWER = 0x6;
	

	private static final int CLOUD_SUPER_WIDGET_SHOWTIME_INTERNAL = 48 * 60 * 60 * 1000;

	private static GuideControler sGuideControler;
	private GuideCloudView mDockGesture;
	private GuideCloudView mCustomGesture;
	private GuideCloudView mScreenPreview;
	private GuideCloudView mSuperWidget;
	private GuideCloudView mSlideMenuGuide;

	private HashMap<Integer, GuideCloudView> mCloudViewIDMap;

	private PreferencesManager mSharedPreferences;
	private Handler mHandler;

	private Context mContext;
	
	
	private GuideControler(Context context) {
		mContext = context;
		mCloudViewIDMap = new HashMap<Integer, GuideCloudView>();
		mSharedPreferences = new PreferencesManager(mContext, IPreferencesIds.USERTUTORIALCONFIG,
				Context.MODE_PRIVATE);
		mHandler = new Handler(Looper.getMainLooper());
		MsgMgrProxy.registMsgHandler(this);
	}

	public static GuideControler getInstance(Context context) {
		if (sGuideControler == null) {
			sGuideControler = new GuideControler(context);
		}
		return sGuideControler;
	}

	private void addToCoverFrame(int cloudViewID) {
		View showView = null;
		int param = CoverFrame.COVER_VIEW_SCREEN_GUIDE;
		switch (cloudViewID) {
			case CLOUD_ID_DOCK_GESTURE :
				if (mDockGesture == null) {
					mDockGesture = new GuideCloudView(mContext);
					mDockGesture.setId(CLOUD_ID_DOCK_GESTURE);
				}
				mCloudViewIDMap.put(CLOUD_TYPE_SCREEN, mDockGesture);
				showView = mDockGesture;
				break;
			case CLOUD_ID_CUSTOM_GESTURE :
				if (mCustomGesture == null) {
					mCustomGesture = new GuideCloudView(mContext);
					mCustomGesture.setId(CLOUD_ID_CUSTOM_GESTURE);
				}
				mCloudViewIDMap.put(CLOUD_TYPE_SCREEN, mCustomGesture);
				showView = mCustomGesture;
				break;
			case CLOUD_ID_SCREEN_PRIVIEW :
				if (mScreenPreview == null) {
					mScreenPreview = new GuideCloudView(mContext);
					mScreenPreview.setId(CLOUD_ID_SCREEN_PRIVIEW);
				}
				mCloudViewIDMap.put(CLOUD_TYPE_SCREEN_PRIVIEW, mScreenPreview);
				param = CoverFrame.COVER_VIEW_SCREEN_PREVIEW_GUIDE;
				showView = mScreenPreview;
				break;
			case CLOUD_ID_SUPER_WIDGET :
				if (mSuperWidget == null) {
					mSuperWidget = new GuideCloudView(mContext);
					mSuperWidget.setId(CLOUD_ID_SUPER_WIDGET);
				}
				mCloudViewIDMap.put(CLOUD_TYPE_SCREEN, mSuperWidget);
				showView = mSuperWidget;
				break;
			case CLOUD_ID_APPDRAWER_SIDEBAR :
				if (mSlideMenuGuide == null) {
					mSlideMenuGuide = new GuideCloudView(mContext);
					mSlideMenuGuide.setId(CLOUD_ID_APPDRAWER_SIDEBAR);
				}
				mCloudViewIDMap.put(CLOUD_TYPE_APPDRAWER, mSlideMenuGuide);
				param = CoverFrame.COVER_VIEW_APPDRAWER_GUIDE;
				showView = mSlideMenuGuide;
				break;
			default :
				break;
		}
		final View child = showView;
		FrameLayout container = new FrameLayout(mContext) {
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				if (child != null) {
					return child.onTouchEvent(event);
				}
				return super.onTouchEvent(event);
			}
		};
		ViewParent parent = showView.getParent();
		if (parent != null && parent instanceof ViewGroup) {
			((ViewGroup) parent).removeView(showView);
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		container.addView(showView, params);
		showView.setVisibility(View.VISIBLE);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
				ICoverFrameMsgId.COVER_FRAME_ADD_VIEW, param, container,
				null);
	}

	private void removeFromCoverFrame(int cloudViewID) {
		int param = CoverFrame.COVER_VIEW_SCREEN_GUIDE;
		int cloudType = -1;
		switch (cloudViewID) {
			case CLOUD_ID_DOCK_GESTURE :
			case CLOUD_ID_CUSTOM_GESTURE :
			case CLOUD_ID_SUPER_WIDGET :
				cloudType = CLOUD_TYPE_SCREEN;
				break;
			case CLOUD_ID_APPDRAWER_SIDEBAR :
				param = CoverFrame.COVER_VIEW_APPDRAWER_GUIDE;
				cloudType = CLOUD_TYPE_APPDRAWER;
				break;
			case CLOUD_ID_SCREEN_PRIVIEW :
				param = CoverFrame.COVER_VIEW_SCREEN_PREVIEW_GUIDE;
				cloudType = CLOUD_TYPE_SCREEN_PRIVIEW;
				break;
			default :
				break;
		}
		if (mCloudViewIDMap.containsKey(cloudType)) {
			GuideCloudView view = mCloudViewIDMap.get(cloudType);
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
			view.setVisibility(View.GONE);
			mCloudViewIDMap.remove(cloudType);
		}
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
				ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW, param, null, null);
	}
	

	public void hideCloudViewByType(int type) {
		int param = -1;
		switch (type) {
			case CLOUD_TYPE_SCREEN :
				param = CoverFrame.COVER_VIEW_SCREEN_GUIDE;
				break;
			case CLOUD_TYPE_SCREEN_PRIVIEW :
				param = CoverFrame.COVER_VIEW_SCREEN_PREVIEW_GUIDE;
				break;
			case CLOUD_TYPE_APPDRAWER :
				param = CoverFrame.COVER_VIEW_APPDRAWER_GUIDE;
				break;

			default :
				break;
		}
		if (param != -1) {
			GuideCloudView cloudView = mCloudViewIDMap.get(type);
			if (cloudView != null) {
				cloudView.setVisibility(View.GONE);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW, param, null, null);
			}
		}
	}
	
	public boolean reshowCloudViewByType(int type) {
		if (mCloudViewIDMap.containsKey(type)) {
			GuideCloudView cloudView = mCloudViewIDMap.get(type);
			// ADT-16974 长按dock栏出现小白云后，进入添加界面，小白云仍然会展示
			if (cloudView.getId() != CLOUD_ID_DOCK_GESTURE) {
				addToCoverFrame(cloudView.getId());
				return true;
			}
		}
		return false;
	}

	public boolean reshowCloudViewById(int cloudViewID) {
		int type = getType(cloudViewID);
		GuideCloudView cloudView = mCloudViewIDMap.get(type);
		if (cloudView != null) {
			if (cloudView.getId() == cloudViewID) {
				addToCoverFrame(cloudViewID);
				return true;
			}
		}
		return false;
	}
	
	public void hideCloudViewById(int cloudViewID) {
		if (!isCloudViewTriggeredToShow(cloudViewID)) {
			return;
		}
		int type = getType(cloudViewID);
		hideCloudViewByType(type);
	}

	private int getType(int cloudViewID) {
		int type = -1;
		switch (cloudViewID) {
			case CLOUD_ID_DOCK_GESTURE :
			case CLOUD_ID_CUSTOM_GESTURE :
			case CLOUD_ID_SUPER_WIDGET :
				type = CLOUD_TYPE_SCREEN;
				break;
			case CLOUD_ID_SCREEN_PRIVIEW :
				type = CLOUD_TYPE_SCREEN_PRIVIEW;
				break;
			case CLOUD_ID_APPDRAWER_SIDEBAR :
				type = CLOUD_TYPE_APPDRAWER;
				break;
			default :
				break;
		}
		return type;
	}
	
	/**
	 * 功能简述:弹出功能表引导小白云提示
	 * 功能详细描述:检查是否需要弹出dock图标提示并根据情况弹出提示
	 * @author zhangxi
	 */
	public void showSlideMenuGuide() {
		boolean shouldshowguide = mSharedPreferences.getBoolean(
				IPreferencesIds.SHOULD_SHOW_APPDRAWER_SIDEBAR_GUIDE, true);
		if (shouldshowguide) {
//			if (mAppDrawer != null && mAppDrawer.isShown()
//					&& isCloudViewShowing(CLOUD_ID_APPDRAW_SIDEBAR)) {
//				return;
//			}
			addToCoverFrame(CLOUD_ID_APPDRAWER_SIDEBAR);
			mSlideMenuGuide.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeFromCoverFrame(CLOUD_ID_APPDRAWER_SIDEBAR);
					GuideForGlFrame
							.setmGuideType(GuideForGlFrame.GUIDE_TYPE_APPDRAWER_SLIDEMENU_GUIDE);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
							IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
					StaticTutorial.sCheckAppDrawerSideBar = false;
					mSharedPreferences.putBoolean(
							IPreferencesIds.SHOULD_SHOW_APPDRAWER_SIDEBAR_GUIDE, false);
					mSharedPreferences.commit();
				}
			});
		} else {
			StaticTutorial.sCheckAppDrawerSideBar = false;
		}
	}
	
	/**
	 * 功能简述:弹出dock图标提示
	 * 功能详细描述:检查是否需要弹出dock图标提示并根据情况弹出提示
	 * 注意:
	 * @return true 需要弹出提示，并弹出提示
	 * 		   false 不需要弹出提示
	 */
	public void showDockGesture() {
		
		boolean shouldshowguide = mSharedPreferences.getBoolean(
				IPreferencesIds.SHOULD_SHOW_DOCK_BAR_ICON_GESTURE, true);
		if (shouldshowguide) {
			if (mDockGesture != null && mDockGesture.isShown()
					&& isCloudViewTriggeredToShow(CLOUD_ID_DOCK_GESTURE)) {
				return;
			}
			addToCoverFrame(CLOUD_ID_DOCK_GESTURE);
			mDockGesture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeFromCoverFrame(CLOUD_ID_DOCK_GESTURE);
					GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_DOCK_BAR_ICON_GESTURE);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
							IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
					StaticTutorial.sCheckDockBarIcon = false;
					mSharedPreferences.putBoolean(
							IPreferencesIds.SHOULD_SHOW_DOCK_BAR_ICON_GESTURE, false);
					mSharedPreferences.commit();
				}
			});
		} else {
			StaticTutorial.sCheckDockBarIcon = false;
		}
	}

	public void showCustomGesture() {
		boolean shouldshowguide = mSharedPreferences.getBoolean(
				IPreferencesIds.SHOULD_SHOW_CUSTOM_GESTURE, true);
		if (shouldshowguide && checkNeedShowCustomGestureGuide()) {
			if (mCustomGesture != null && mCustomGesture.isShown()
					&& isCloudViewTriggeredToShow(CLOUD_ID_CUSTOM_GESTURE)) {
				return;
			}
			addToCoverFrame(CLOUD_ID_CUSTOM_GESTURE);
			mCustomGesture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeFromCoverFrame(CLOUD_ID_CUSTOM_GESTURE);
					GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_CUSTOM_GESTURE);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
							IFrameworkMsgId.SHOW_FRAME, IDiyFrameIds.GUIDE_GL_FRAME, null, null);
					StaticTutorial.sCheckCustomGesture = false;
					mSharedPreferences
							.putBoolean(IPreferencesIds.SHOULD_SHOW_CUSTOM_GESTURE, false);
					mSharedPreferences.commit();
				}
			});
		} else {
			StaticTutorial.sCheckCustomGesture = false;
		}
	}
	
	public static boolean sNeedShowPreviewGuide = false;
	public void showPreviewGuide(final int currentScreen) {
		boolean needStartutorial = mSharedPreferences.getBoolean(
				IPreferencesIds.SHOULD_SHOW_PRIVIEW_GUIDE, true);
		if (needStartutorial) {
			if (sNeedShowPreviewGuide) {
				showTutorial(currentScreen);
				sNeedShowPreviewGuide = false;
				return;
			}
			addToCoverFrame(CLOUD_ID_SCREEN_PRIVIEW);
			if (mScreenPreview != null) {
				mScreenPreview.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						removeFromCoverFrame(CLOUD_ID_SCREEN_PRIVIEW);
						showTutorial(currentScreen);
					}
				});
			}
		} else {
			StaticTutorial.sCheckShowScreenEdit = false;
		}
	}

	private void showTutorial(int currentScreen) {
		StaticTutorial.sCheckShowScreenEdit = false;
		// 得到当前屏幕状态
		if (!StatusBarHandler.isHide()) {
			// SensePreviewFrame.previewOperate = true;
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					ICommonMsgId.SHOW_HIDE_STATUSBAR, -2, true, null);
		}

		GuideForGlFrame.setmGuideType(GuideForGlFrame.GUIDE_TYPE_SCREEN_EDIT);

		boolean ret = ShellPluginFactory.getShellManager().isViewVisible(IViewId.SCREEN_PREVIEW);
		ValueReturned value = new ValueReturned();
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_PREVIEW,
				IScreenPreviewMsgId.GET_PREVIEW_PARAM, -1, value);
		ArrayList<Integer> list = (ArrayList<Integer>) value.mValue;
		if (!ret) {
			return;
		}
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.SHOW_FRAME,
				IDiyFrameIds.GUIDE_GL_FRAME, null, null);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.GUIDE_GL_FRAME,
				IScreenPreviewMsgId.PREVIEW_CURRENT_SCREEN_INDEX, currentScreen, list);
		mSharedPreferences.putBoolean(IPreferencesIds.SHOULD_SHOW_PRIVIEW_GUIDE, false);
		mSharedPreferences.commit();

	}
	/**
	 * 功能描述：检测是否需要弹出自定义手势提示
	 * 过滤条件：若为新安装，返回false
	 * 如果已使用过自定义手势，存在自定义手势的记录，返回false
	 * 分别检测HOME键、上滑、下滑手势，若存在一个设为自定义手势，则返回false		
	 * @return boolean值    若为true，则需要弹出自定义手势提示，false则不需要
	 */
	private static boolean checkNeedShowCustomGestureGuide() {
		try {
			// 判断是否使用过自定义手势
			DataProvider dataProvider = DataProvider.getInstance(ApplicationProxy.getContext());
			Cursor cursor = dataProvider.queryDiyGestures();
			if (cursor != null) {
				try {
					if (cursor.getCount() != 0) {
						return false;
					}
				} catch (Exception e) {
				} finally {
					cursor.close();
				}
			}
			GestureSettingInfo info = null;
			// 检测HOME键手势
			info = SettingProxy.getGestureSettingInfo(GestureSettingInfo.GESTURE_HOME_ID);
			if (info.mGestureAction == GlobalSetConfig.GESTURE_GOSHORTCUT
					&& info.mGoShortCut == GlobalSetConfig.GESTURE_SHOW_DIYGESTURE) {
				return false;
			}
			// 检测上滑手势
			info = SettingProxy.getGestureSettingInfo(GestureSettingInfo.GESTURE_UP_ID);
			if (info.mGestureAction == GlobalSetConfig.GESTURE_GOSHORTCUT
					&& info.mGoShortCut == GlobalSetConfig.GESTURE_SHOW_DIYGESTURE) {
				return false;
			}
			// 检测下滑手势
			info = SettingProxy.getGestureSettingInfo(GestureSettingInfo.GESTURE_DOWN_ID);
			if (info.mGestureAction == GlobalSetConfig.GESTURE_GOSHORTCUT
					&& info.mGoShortCut == GlobalSetConfig.GESTURE_SHOW_DIYGESTURE) {
				return false;
			}
			// 检测双击手势
			info = SettingProxy.getGestureSettingInfo(GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID);
			if (info.mGestureAction != GlobalSetConfig.GESTURE_GOSHORTCUT
					|| info.mGoShortCut != GlobalSetConfig.GESTURE_SHOW_DIYGESTURE) {
				return false;
			}
		} catch (Throwable e) {
			return false;
		}
		// 判断是否是新安装
		if (VersionControl.getFirstRun()) {
			return false;
		}
		return true;
	}

	public boolean isCloudViewTriggeredToShow(int cloudViewID) {
		if (mCloudViewIDMap == null) {
			return false;
		}
		GuideCloudView cloudView = null;
		switch (cloudViewID) {
			case CLOUD_ID_DOCK_GESTURE :
				cloudView = mCloudViewIDMap.get(CLOUD_TYPE_SCREEN);
				if (cloudView == null) {
					return false;
				} else {
					return cloudView.getId() == CLOUD_ID_DOCK_GESTURE;
				}
			case CLOUD_ID_CUSTOM_GESTURE :
				cloudView = mCloudViewIDMap.get(CLOUD_TYPE_SCREEN);
				if (cloudView == null) {
					return false;
				} else {
					return cloudView.getId() == CLOUD_ID_CUSTOM_GESTURE;
				}
			case CLOUD_ID_SCREEN_PRIVIEW :
				cloudView = mCloudViewIDMap.get(CLOUD_TYPE_SCREEN_PRIVIEW);
				if (cloudView == null) {
					return false;
				} else {
					return cloudView.getId() == CLOUD_ID_SCREEN_PRIVIEW;
				}
			case CLOUD_ID_SUPER_WIDGET :
				cloudView = mCloudViewIDMap.get(CLOUD_TYPE_SCREEN);
				if (cloudView == null) {
					return false;
				} else {
					return cloudView.getId() == CLOUD_ID_SUPER_WIDGET;
				}
			case CLOUD_ID_APPDRAWER_SIDEBAR :
				cloudView = mCloudViewIDMap.get(CLOUD_TYPE_APPDRAWER);
				if (cloudView == null) {
					return false;
				} else {
					return cloudView.getId() == CLOUD_ID_APPDRAWER_SIDEBAR;
				}
			default :
				return false;
		}
	}

	@Override
	public int getMsgHandlerId() {
		// TODO Auto-generated method stub
		return IDiyFrameIds.GUIDE_CONTROLER;
	}

	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		switch (msgId) {
			case IFrameworkMsgId.SYSTEM_ON_RESUME :
//				if ((mDockGesture != null && mDockGesture.isShown())
//						|| (mCustomGesture != null && mCustomGesture.isShown())) {
//					mDockGesture.postInvalidate();
//					mCustomGesture.postInvalidate();
//				}
//			case IDiyMsgIds.SYSTEM_CONFIGURATION_CHANGED :
//				if (isCloudViewShowing(CLOUD_ID_DOCK_GESTURE)
//						|| isCloudViewShowing(CLOUD_ID_CUSTOM_GESTURE)
//						|| isCloudViewShowing(CLOUD_ID_SUPER_WIDGET)) {
//					hideCloudView();
//				}
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						//检测是否应该继续存在。
//						if (isCloudViewShowing(CLOUD_ID_DOCK_GESTURE)) {
//							showDockGesture();
//						} else if (isCloudViewShowing(CLOUD_ID_SUPER_WIDGET)) {
//							showSuperWidgetGuide();
//						} else if (isCloudViewShowing(CLOUD_ID_CUSTOM_GESTURE)) {
//							showCustomGesture();
//						}
						if (isCloudViewTriggeredToShow(CLOUD_ID_SUPER_WIDGET)) {
							removeFromCoverFrame(CLOUD_ID_SUPER_WIDGET);
						}
//						if (StaticTutorial.sCheckSuperWidget) {
//							showSuperWidgetGuide();
//						}
					}
				}, 600);
				return true;
		}
		return false;
	}
}
