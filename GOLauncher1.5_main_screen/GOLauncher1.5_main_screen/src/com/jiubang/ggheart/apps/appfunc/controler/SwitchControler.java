package com.jiubang.ggheart.apps.appfunc.controler;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.ICoverFrameMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IMediaManagementMsgId;
import com.golauncher.message.IScreenFrameMsgId;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.ggheart.data.Controler;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.plugin.mediamanagement.inf.AppFuncContentTypes;

/**
 * 切换控制器
 * @author yejijiong
 *
 */
public class SwitchControler extends Controler {

	private static SwitchControler sInstance;

	public SwitchControler(Context context) {
		super(context);
	}

	public synchronized static SwitchControler getInstance(Context context) {
		if (sInstance == null && context != null) {
			sInstance = new SwitchControler(context);
		}
		return sInstance;
	}

	/**
	 * 显示资源管理层
	 * @param type
	 * @param isRemoveSearchFrame
	 */
	public void showMediaManagementFrame(int type, boolean isRemoveSearchFrame) {
		if (!isRemoveSearchFrame) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.HIDE_FRAME,
					IDiyFrameIds.APP_DRAWER_SEARCH, null, null);
		} else {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
					IDiyFrameIds.APP_DRAWER_SEARCH, null, null);
		}
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.SHOW_FRAME,
				IDiyFrameIds.MEDIA_MANAGEMENT_FRAME, true, null);
		// 隐藏罩子层的view
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICoverFrameMsgId.COVER_FRAME_HIDE_ALL,
				-1, null, null);
		// 隐藏主题中间层
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_HIDE_MIDDLE_VIEW, -1);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
				IScreenFrameMsgId.SCREEN_LEAVE_EDIT_STATE, -1);
		setType(type);
	}

	/**
	 * 显示资源管理内具体页面
	 * @param type
	 * @param isRemoveSearchFrame
	 * @param object
	 * @param objList
	 */
	public void showMediaManagementContent(int type, boolean isRemoveSearchFrame, Object object,
			List<?> objList) {
		showMediaManagementFrame(type, isRemoveSearchFrame);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
				IMediaManagementMsgId.SWITCH_MEDIA_MANAGEMENT_CONTENT, -1, object, objList);
	}

	/**
	 * 显示图片文件夹页面
	 */
	public void showMediaManagementImageContent() {
		showMediaManagementContent(AppFuncContentTypes.IMAGE, true,
				new Object[] { AppFuncContentTypes.IMAGE }, null);
	}

	/**
	 * 显示音乐文件夹页面
	 */
	public void showMediaManagementMusicContent() {
		showMediaManagementContent(AppFuncContentTypes.MUSIC, true,
				new Object[] { AppFuncContentTypes.MUSIC }, null);
	}

	/**
	 * 显示视频文件夹页面
	 */
	public void showMediaManagementVideoContent() {
		showMediaManagementContent(AppFuncContentTypes.VIDEO, true,
				new Object[] { AppFuncContentTypes.VIDEO }, null);
	}

	/**
	 * 显示播放器页面
	 */
	public void showMediaManagementMusicPlayerContent() {
		showMediaManagementContent(AppFuncContentTypes.MUSIC_PLAYER, true, new Object[] {
				AppFuncContentTypes.MUSIC_PLAYER, ICustomAction.DESTINATION_RETURN_TO_GOMUSIC,
				null, null }, null);
	}

	/**
	 * 显示功能表层
	 * @param animation
	 */
	public void showAppDrawerFrame(boolean animation) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
				IMediaManagementMsgId.MEDIA_MANAGEMENT_FRAME_HIDE, -1, true, /*objList*/null);
		if (MsgMgrProxy.getTopFrameId() == IDiyFrameIds.APP_DRAWER_SEARCH) {
			Object[] objs = new Object[2];
			objs[0] = true;
			objs[1] = IDiyFrameIds.APP_DRAWER;
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
					IDiyFrameIds.APP_DRAWER_SEARCH, objs, null);
		} else {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.SHOW_FRAME,
					IDiyFrameIds.APP_DRAWER, false, null);
		}
		setType(AppFuncContentTypes.APP);
	}

	/**
	 * 显示搜索层
	 * @param object
	 * @param objList
	 */
	public void showSearchFrame(Object object, List<?> objList) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
				IMediaManagementMsgId.MEDIA_MANAGEMENT_FRAME_HIDE, -1, true, null);

		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SHELL_FRAME, IFrameworkMsgId.SHOW_FRAME,
				IDiyFrameIds.APP_DRAWER_SEARCH, false, null);
		setType(AppFuncContentTypes.SEARCH);
	}

	/**
	 * 显示屏幕层
	 */
	public void showScreenFrame(boolean animation) {
		List<Object> objList = new ArrayList<Object>();
		objList.add(IDiyFrameIds.SCREEN);
		objList.add(animation);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
				IMediaManagementMsgId.MEDIA_MANAGEMENT_FRAME_HIDE, -1, true, objList);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
				IDiyFrameIds.APP_DRAWER_SEARCH, null, null);
		setType(AppFuncContentTypes.APP);
	}

	public void imageBrowserBack(boolean isBackToSearchFrame) {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
				IDiyFrameIds.IMAGE_BROWSER_FRAME, null, null);
		if (isBackToSearchFrame) {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.HIDE_FRAME,
					IDiyFrameIds.MEDIA_MANAGEMENT_FRAME, null, null); // 这里如果使用这个，会使FileEngine被清空，导致定位爆指针
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.SHOW_FRAME,
					IDiyFrameIds.APP_DRAWER_SEARCH, null, null);
			setType(AppFuncContentTypes.SEARCH);
		} else {
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
					IDiyFrameIds.APP_DRAWER_SEARCH, null, null);
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.SHOW_FRAME,
					IDiyFrameIds.MEDIA_MANAGEMENT_FRAME, null, null);
		}
	}

	public void musicPlayBackToSearch() {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
				IMediaManagementMsgId.MEDIA_MANAGEMENT_FRAME_HIDE, -1, true, null);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.SHOW_FRAME,
				IDiyFrameIds.APP_DRAWER_SEARCH, null, null);
		setType(AppFuncContentTypes.SEARCH);
	}

	public void setType(int type) {
		if (type != -1) {
			AppFuncContentTypes.sType = type;
			if (type == AppFuncContentTypes.APP) { // 在这里进行统一处理
				MediaFileSuperVisor.getInstance(mContext).destroyFileEngine(); // 销毁多媒体引擎
			}
		}
	}
}
