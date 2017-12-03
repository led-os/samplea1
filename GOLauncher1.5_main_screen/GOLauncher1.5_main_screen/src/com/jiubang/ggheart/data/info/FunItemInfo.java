package com.jiubang.ggheart.data.info;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.go.util.sort.IClickedCompareable;
import com.go.util.sort.ICreatTimeCompareable;
import com.go.util.sort.IPriorityLvCompareable;
import com.go.util.sort.ITitleCompareable;
import com.jiubang.ggheart.plugin.notification.NotificationType;

/**
 * 
 * <br>类描述: 功能表图标基类
 * <br>功能详细描述:
 * 
 * @author  yangguanxiang
 * @date  [2012-11-22]
 */
public abstract class FunItemInfo extends BaseItemInfo implements IPriorityLvCompareable, ITitleCompareable, IClickedCompareable, ICreatTimeCompareable {
	// 程序图标 TODO:考虑移到FunItemInfo外面
	public final static int TYPE_APP = 0;
	// 文件夹 TODO:考虑移到FunItemInfo外面
	public final static int TYPE_FOLDER = 1;

	public static final int END = 100; // 子类需要以此为消息id起点

	// 唯一标识intent
	protected Intent mIntent;
	// item项在功能表中的索引
	protected int mIndex;
	
	protected AppConfigInfo mHideInfo;
	/** 通讯统计应用类型 */
	protected int mNotificationType = NotificationType.IS_NOT_NOTIFICSTION;
	
	protected int mPriority;

	public void setHideInfo(AppConfigInfo hideInfo) {
		mHideInfo = hideInfo;
	}

	/**
	 * 获取下标
	 * 
	 * @return 下标
	 */
	public int getIndex() {
		return mIndex;
	}

	/**
	 * 设置下标
	 * 
	 * @param index
	 */
	public void setIndex(final int index) {
		mIndex = index;
	}

	/**
	 * 获取是否隐藏
	 * 
	 * @return 是否隐藏
	 */
	public boolean isHide() {
		if (mHideInfo != null) {
			return mHideInfo.getHide();
		} else {
			return false;
		}
	}

	/**
	 * 获取FolderId
	 * 
	 * @return FolderId
	 */
	public long getFolderId() {
		// TODO:FunFolderItemInfo 重要！！！
		return 0;
		// return mFolderId;
	}

	/**
	 * 获取文件夹图标路径
	 * 
	 * @return
	 */
	public String getIconPath() {
		return null;
	}

	/**
	 * 设置intent
	 * 
	 * @param intent
	 */
	public void setIntent(Intent intent) {
		mIntent = intent;
	}

	/**
	 * 取得唯一标识intent
	 */
	public Intent getIntent() {
		return mIntent;
	}

	/**
	 * 
	 * @author huyong
	 * @return
	 */
	public int getAppItemIndex() {
		return mIndex;
	}
	
	public int getPriorityLv() {
		return mPriority;
	}
	
	public void setPriorityLv(int priorityLv) {
		mPriority = priorityLv;
	}
	
	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public abstract String getTitle();
	
	public abstract void setIconTitle(String title);

	/**
	 * 获取时间
	 * 
	 * @param packageMgr
	 *            包管理器
	 * @return 时间long
	 */
	public abstract long getTime(PackageManager packageMgr);

	public abstract int getClickedCount(Context context);
	
	public abstract boolean isSysApp();
	
	public abstract boolean isNew();
	
	/**
	 * 获取类型：程序图标或文件夹
	 * 
	 * @return 类型
	 */
	public abstract int getType();

	public abstract int getUnreadCount();

	public abstract void setUnreadCount(int mUnreadCount);

	public abstract int getNotificationType();

	public abstract void setNotificationType(int mNotificationType);

}
