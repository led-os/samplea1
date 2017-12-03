package com.jiubang.ggheart.apps.desks.diy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.go.proxy.MsgMgrProxy;
import com.go.proxy.ValueReturned;
import com.go.util.GoViewCompatProxy;
import com.go.util.device.Machine;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IFrameControllerMsgId;
import com.jiubang.core.framework.AbstractFrame;
import com.jiubang.core.framework.FrameManager;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.core.message.MessageManager;
import com.jiubang.ggheart.plugin.shell.IShellManager;
import com.jiubang.ggheart.plugin.shell.ShellPluginFactory;

/**
 * 帧控制器，用于控制DIY桌面的帧的生命周期,与业务逻辑挂钩
 * 
 * @author yuankai
 * @version 1.0
 */
public class FrameControl extends FrameManager implements ICleanable, IMessageHandler {
	private List<AbstractFrame> mCachedFrames; // 缓存的帧
	private Activity mActivity = null;
	
	/**
	 * 被缓存的帧ID,这些帧不会每次显示时被重新初始化
	 */
	private final static int[] CACHED_FRAME_IDS = {};

	/**
	 * 帧控制器构造方法
	 * 
	 * @param activity
	 *            活动
	 * @param frameManager
	 *            帧管理器
	 * @param theme
	 *            主题数据
	 */
	public FrameControl(Activity activity, ViewGroup layout, MessageManager manager) {
		super(layout, manager);
		mActivity = activity;
		mCachedFrames = new ArrayList<AbstractFrame>();
		MsgMgrProxy.registMsgHandler(this);
	}

	/**
	 * <br>功能简述:初始化页面的方法
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public boolean initCachedFrame() {
		return init3DMode();
	}


	/**
	 * <br>功能简述:3D页面初始化的方法
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private boolean init3DMode() {
		try {
			ShellPluginFactory.buildShellPlugin(mActivity, this);
		} catch (Throwable t) {
			t.printStackTrace();
//			DeskToast.makeText(mActivity, "init 3D mode failed", Toast.LENGTH_SHORT).show();
//			ShellPluginFactory.sUseEngineFlag = false;
//			init2DMode();
			onInit3DModeFailed();
			return false;
		}
		mActivity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		IShellManager shellManager = ShellPluginFactory.getShellManager();
		View contentView = shellManager.getContentView();
		View overlayedViewGroup = shellManager.getOverlayedViewGroup();
		mActivity.setContentView(contentView);
		mActivity.addContentView(overlayedViewGroup, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mActivity.addContentView(getRootView(), new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// 透明通知栏和透明虚拟键
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			StatusBarHandler.setStatusBarTransparentKitKat(
					mActivity.getWindow(), true);
			int flag = GoViewCompatProxy.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			if (Machine.canHideNavBar()) {
				StatusBarHandler.setNavBarTransparentKitKat(
						mActivity.getWindow(), true);
				flag |= GoViewCompatProxy.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
			}
			GoViewCompatProxy.setSystemUiVisibility(contentView, flag);
			GoViewCompatProxy.setSystemUiVisibility(overlayedViewGroup, flag);
			GoViewCompatProxy.setSystemUiVisibility(getRootView(), flag);
		}
		return true;
	}

	/**
	 * <br>功能简述:3D模式初始化失败的处理方法
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void onInit3DModeFailed() {
//		// 在SharedPreferences记录3D初始化失败
//		PreferencesManager sharedPreferences = new PreferencesManager(
//				mActivity, IPreferencesIds.PREFERENCE_ENGINE, Context.MODE_PRIVATE);
//		sharedPreferences.putBoolean(IPreferencesIds.PREFERENCE_ENGINE_INITED_FAILED, true);
//		sharedPreferences.commit();
//		// 然后重启
//		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
//				ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
	}

	/**
	 * 显示
	 * 
	 * @param frameId
	 *            帧ID
	 * @return 是否操作成功
	 */
	public synchronized boolean showFrame(int frameId) {
		if (showFilter(frameId)) {
			return false;
		}

		AbstractFrame frame = getFrame(frameId);
		if (frame != null) {
			if (frame.getVisibility() != View.VISIBLE) {
				setFrameVisiable(frameId, View.VISIBLE);
			}
			return true;
		} else {
			frame = produceIfNotCached(frameId);
			if (frame != null) {
				return addFrame(frame, View.VISIBLE);
			}
		}
		return false;
	}

	/**
	 * 如果对应ID的帧当前正在显示，则移除 如果对应ID的帧当前未显示，则显示
	 * 
	 * @param id
	 *            帧ID
	 */
	public void showOrHide(int id) {
		if (isExits(id)) {
			hideFrame(id);
		} else {
			showFrame(id);
			AbstractFrame topFrame = getTopFrame();
			if (topFrame != null && topFrame.getMsgHandlerId() != id) {
				hideFrame(topFrame.getMsgHandlerId());
				topFrame = getTopFrame();
			}
		}
	}

	/**
	 * 隐藏
	 * 
	 * @param frameId
	 *            帧ID
	 */
	public void hide(int frameId) {
		setFrameVisiable(frameId, View.INVISIBLE);
	}

	/**
	 * 不参与排版
	 * 
	 * @param frameId
	 *            帧ID
	 */
	public void gone(int frameId) {
		setFrameVisiable(frameId, View.GONE);
	}

	/**
	 * 判断frame是否顶层
	 * 
	 * @param frameid
	 * @return
	 */
	public boolean isForeground(int frameid) {
		AbstractFrame topFrame = getTopFrame();
		if (topFrame != null && topFrame.getMsgHandlerId() == frameid) {
			return true;
		}
		return false;
	}


	/**
	 * 移除最顶层帧
	 * 
	 * @return 是否成功
	 */
	public boolean removeTopFrame() {
		final AbstractFrame topFrame = getTopFrame();
		if (topFrame != null) {
			return removeFrame(topFrame);
		}
		return false;
	}

	/**
	 * 不显示
	 * 
	 * @param frameId
	 *            帧ID
	 * @return 是否操作成功
	 */
	public boolean hideFrame(int frameId) {
		if (hideFilter(frameId)) {
			hide(frameId);
			return true;
		} else {
			boolean ret = false;
			try {
				ret = removeFrame(frameId);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			return ret;
		}
	}

	/**
	 * 
	 * @param frameId
	 * @return 返回true需要保留frmae在栈中，否则删除
	 */
	private boolean hideFilter(int frameId) {
		boolean ret = false;
//		switch (frameId) {
//			case IDiyFrameIds.SCREEN_FRAME :
//			case IDiyFrameIds.DOCK_FRAME :
//				ret = true;
//				break;
//
//			case IDiyFrameIds.SCREEN_EDIT_BOX_FRAME : {
//				setFrameVisiable(IDiyFrameIds.DOCK_FRAME, View.VISIBLE);
//				// 暂时设回false
//				ret = false;
//				break;
//			}
//
//			case IDiyFrameIds.SCREEN_PREVIEW_FRAME : {
//				// 添加屏幕预览层时，将Dock和screen层显示
//				setFrameVisiable(IDiyFrameIds.DOCK_FRAME, View.VISIBLE);
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_FRAME,
//						IScreenPreviewMsgId.PREVIEW_NOTIFY_DESKTOP, 1, null, null);
//				ret = true;
//			}
//				break;
//
//			case IDiyFrameIds.APPFUNC_FRAME : {
//				// 展现罩子层的view
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
//						IDiyMsgIds.COVER_FRAME_SHOW_ALL, -1, null, null);
//				// 展现中间层
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_FRAME,
//						IDiyMsgIds.SCREEN_SHOW_MIDDLE_VIEW, -1, null, null);
//				ret = true;
//			}
//				break;
//			default :
//				ret = isCacheId(frameId);
//				break;
//		}
		return ret;
	}

	/**
	 * 获取被缓存的帧对象
	 * 
	 * @param frameId
	 *            帧ID
	 * @return 帧对象
	 */
	public AbstractFrame getCachedFrame(int frameId) {
		int size = mCachedFrames.size();
		for (int i = 0; i < size; i++) {
			AbstractFrame frame = mCachedFrames.get(i);
			if (frame.getMsgHandlerId() == frameId) {
				return frame;
			}
		}

		return null;
	}

	private synchronized AbstractFrame produceIfNotCached(int frameId) {
		AbstractFrame frame = null;
		if (isCacheId(frameId)) {
			frame = getCachedFrame(frameId);
			if (frame == null) {
				frame = FrameFactory.produce(mActivity, this, frameId);
				if (frame != null) {
					mCachedFrames.add(frame);
				}
			}
		} else {
			frame = FrameFactory.produce(mActivity, this, frameId);
		}
		return frame;
	}

	/**
	 * 处理特殊层的添加操作
	 * 
	 * @param id
	 *            帧ID
	 * @return 是否处理 true过滤使之不显示
	 */
	private boolean showFilter(int frameId) {
		boolean ret = false;
//		switch (frameId) {
//			case IDiyFrameIds.SCREEN_PREVIEW_FRAME :
//				break;
//			case IDiyFrameIds.APPFUNC_FRAME :
//				// 隐藏罩子层的view
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
//						IDiyMsgIds.COVER_FRAME_HIDE_ALL, -1, null, null);
//				// 隐藏主题中间层
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_FRAME,
//						IDiyMsgIds.SCREEN_HIDE_MIDDLE_VIEW, -1, null, null);
//				break;
//			default :
//				break;
//		}

		return ret;
	}

	/**
	 * 帧是否存在
	 * 
	 * @param id
	 *            帧id
	 * @return 是否在数组中并且可见
	 */
	public boolean isExits(int id) {
		final AbstractFrame frame = getFrame(id);
		return frame != null && frame.getVisibility() == View.VISIBLE;
	}

	private synchronized boolean isCacheId(int id) {
		int size = CACHED_FRAME_IDS.length;
		for (int i = 0; i < size; i++) {
			if (CACHED_FRAME_IDS[i] == id) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void cleanup() {
		mCachedFrames.clear();
		mFrames.clear();
		mCleanManager.cleanup();
		//		removeCoverFrame();
	}

	@Override
	public float getLastMotionX() {
		final ViewGroup root = getRootView();
		if (root != null && root instanceof DiyFrameLayout) {
			return ((DiyFrameLayout) root).getLastMotionX();
		}
		return 0f;
	}

	@Override
	public float getLastMotionY() {
		final ViewGroup root = getRootView();
		if (root != null && root instanceof DiyFrameLayout) {
			return ((DiyFrameLayout) root).getLastMotionY();
		}
		return 0f;
	}
	
	public boolean isFrameShowing(int frameId) {
		AbstractFrame frame = getFrame(frameId);
		return frame != null && frame.getVisibility() == View.VISIBLE;
	}

	@Override
	public int getMsgHandlerId() {
		return IDiyFrameIds.FRAME_CONTROLLER;
	}

	private int getValue(int msgId) {
		AbstractFrame frame = getTopFrame();
		if (null == frame) {
			return IDiyFrameIds.INVALID_FRAME;
		}
		return frame.getMsgHandlerId();
	}
	
	// LH TODO:去掉
	/**
	 * 获取层
	 * 
	 * @param frameId
	 *            层id{@link IDiyFrameIds}
	 * @return
	 */
	/*
	public static AbstractFrame getFrameStatic(int frameId) {
		if (null != sInstance) {
			return sInstance.getFrame(frameId);
		}
		
		return null;
	}
	*/

	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		boolean ret = false;
		switch (msgId) {
		case IFrameControllerMsgId.GET_TOP_FRAME_ID: {
				int val = getValue(msgId);
				ValueReturned tmp = (ValueReturned) obj[0];
				tmp.mValue = Integer.valueOf(val);
				ret = true;
			}
			break;
			
		case IFrameControllerMsgId.GET_FRAME_VISIBILITY: {
				AbstractFrame val = getFrame(param);
				if (null != val) {
					ValueReturned tmp = (ValueReturned) obj[0];
					tmp.mValue = Integer.valueOf(val.getVisibility());
				}
				ret = true;
			}
			break;
		case IFrameControllerMsgId.FRAME_EXIST: {
				AbstractFrame val = getFrame(param);
				ValueReturned tmp = (ValueReturned) obj[0];
				if (null != val) {
					tmp.mValue = Boolean.valueOf(null != val);
				}
				ret = true;
			}
			break;
		case IFrameControllerMsgId.ISOPAQUE: {
				int frameid = param;
				AbstractFrame frame = getFrame(frameid);
				if (null != frame) {
					ValueReturned tmp = (ValueReturned) obj[0];
					tmp.mValue = Boolean.valueOf(frame.isOpaque());
				}
				ret = true;
			}
			break;
			
		case ICommonMsgId.GET_CONTENTVIEW: {
				AbstractFrame frame = getFrame(param);
				if (null != frame) {
					ValueReturned tmp = (ValueReturned) obj[0];
					tmp.mValue = frame.getContentView();
				}
				ret = true;
			}
			break;
		default:
			break;
		}
		return ret;
	}
}
