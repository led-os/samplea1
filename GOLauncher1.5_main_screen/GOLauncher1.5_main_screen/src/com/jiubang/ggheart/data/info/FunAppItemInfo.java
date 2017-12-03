package com.jiubang.ggheart.data.info;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;

import com.jiubang.ggheart.components.ISelfObject;
import com.jiubang.ggheart.plugin.notification.NotificationType;

/**
 * 
 * <br>类描述: 功能表应用图标类
 * <br>功能详细描述:
 * 
 * @author  yangguanxiang
 * @date  [2012-11-22]
 */
public class FunAppItemInfo extends FunItemInfo implements ISelfObject {

	public static final int RESETBEAN = END + 0;
	/**
	 * 是否推荐应用发生改变
	 */
//	public static final int IS_RECOMMEND_APP_CHANGE = END + 7;

	private AppItemInfo mAppItemInfo;
//	private boolean mIsTemp; // 是否是暂存的
	private String mAppTitle;

//	private boolean mIsNew = false; // 是否是新安装的
	/**
	 * 是否可更新
	 */
	private boolean mIsUpdate = false;

	/**
	 * 应用程序进入文件夹的时间。
	 */
	private long mTimeInFolder;
	/**
	 *  进程唯一标识
	 */
	private int mPid;
	/**
	 * 是否是白名单中的
	 */
	private boolean mIsIgnore;

	public FunAppItemInfo(AppItemInfo appItemInfo) {
		super();
		if (null != appItemInfo) {
			mAppItemInfo = appItemInfo;
//			mIsNew = mAppItemInfo.mIsNewRecommendApp;
			// 更新唯一标识
			mIntent = appItemInfo.mIntent;
			// 监听数据体bean
			mAppItemInfo.registerObserver(this);
			mPriority = 1;
		} else {
			throw new IllegalArgumentException("appItemInfo is null");
		}
	}

	/**
	 * 设置数据bean
	 * 
	 * @param appItemInfo
	 */
	public void setAppItemInfo(AppItemInfo appItemInfo) {
		if (null != appItemInfo) {
			if (mAppItemInfo != null) {
				mAppItemInfo.unRegisterObserver(this);
			}
			mAppItemInfo = appItemInfo;
			// 更新唯一标识
			mIntent = appItemInfo.mIntent;
			// 监听数据体bean
			mAppItemInfo.registerObserver(this);
			broadCast(RESETBEAN, 0, mAppItemInfo, null);
		} else {
			throw new IllegalArgumentException("appItemInfo is null");
		}
	}

	/**
	 * 获取数据
	 * 
	 * @return
	 */
	public AppItemInfo getAppItemInfo() {
		return mAppItemInfo;
	}

	/**
	 * 获取应用程序是否为系统应用程序
	 * 
	 * @author huyong
	 * @return
	 */
	@Override
	public boolean isSysApp() {
		if (mAppItemInfo == null) {
			return false;
		} else {
			return mAppItemInfo.getIsSysApp();
		}
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	@Override
	public String getTitle() {
		if (null != mAppItemInfo && mAppItemInfo.getTitle() != null) {
			return mAppItemInfo.getTitle();
		} else if (null != mAppTitle) {
			return mAppTitle;
		}
		return AppItemInfo.DEFAULT_TITLE;
	}
	
	/**
	 * 设置名称
	 * 
	 * @param folderTitle
	 *            文件夹名称
	 * @throws DatabaseException
	 */
	@Override
	public void setIconTitle(String title) {
		mAppTitle = title;
	}

	/**
	 * 获取时间
	 * 
	 * @param packageMgr
	 * @return
	 */
	@Override
	public long getTime(PackageManager packageMgr) {
		if (null == mAppItemInfo) {
			return 0;
		}
		return mAppItemInfo.getTime(packageMgr);
	}
	
	/**
	 * 获得进程唯一标识
	 * @return
	 */
	public int getPid() {
		return mPid;
	}
	
	/**
	 * 获取是否在白名单中的
	 * @return
	 */
	public boolean isIgnore() {
		return mIsIgnore;
	}
	
	/**
	 * 设置进程唯一标识
	 * @param pId
	 */
	public void setPid(int pId) {
		mPid = pId;
	}
	
	/**
	 * 设置是否在白名单中的
	 */
	public void setIsIgnore(boolean isIgnore) {
		mIsIgnore = isIgnore;
	}

	/**
	 * 是否为暂存
	 * 
	 * @return 是否为暂存
	 */
	public boolean isTemp() {
		if (mAppItemInfo != null) {
			return mAppItemInfo.isTemp();
		}
		return true;
	}

	// public boolean isPriority() {
	// return false;
	// }

//	/**
//	 * 设置是否是新安装的
//	 */
//	public void setIsNew(boolean isNew) {
//		mAppItemInfo.setIsNewApp(isNew);
//	}

	public void setIsUpdate(boolean isUpdate) {
		mIsUpdate = isUpdate;
	}

	/**
	 * 设置是暂存的
	 * 
	 * @param isTemp
	 *            是否为暂存
	 */
	public void setIsTemp(boolean isTemp) {
		if (mAppItemInfo != null) {
			mAppItemInfo.setIsTemp(isTemp);
		}
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
			// 这3条消息也只是这里转发一下到图标，暂时没有其他多余的业务
			case AppItemInfo.INCONCHANGE :
			case AppItemInfo.TITLECHANGE :
			case AppItemInfo.UNREADCHANGE :
				broadCast(msgId, param, object);
				break;
			case AppItemInfo.UNREADTYPECHANGE :
				setNotificationType(NotificationType.NOTIFICATIONTYPE_MORE_APP);
				broadCast(AppItemInfo.UNREADTYPECHANGE, param, object);
				break;
			case AppItemInfo.IS_NEW_APP_CHANGE :
				if (object[0] instanceof Boolean) {
					broadCast(AppItemInfo.IS_NEW_APP_CHANGE, param, object);
				}
				break;
			default :
				// 额外的消息就再转发给自己的图标，最后交给图标判断是否处理吧
				super.broadCast(msgId, param, object);
				break;
		}
		// 直接转发
//		super.broadCast(msgId, param, object, objects);
	}

	public boolean isNew() {
		return mAppItemInfo.isNew();
	}

	public boolean isUpdate() {
		return mIsUpdate;
	}

	public long getTimeInFolder() {

		return mTimeInFolder;
	}

	public void setTimeInFolder(long mTimeInFolder) {
		this.mTimeInFolder = mTimeInFolder;
	}

	@Override
	public int getClickedCount(Context context) {
		if (mAppItemInfo == null) {
			return 0;
		}
		return mAppItemInfo.getActiveCount(context);
	}

	public long getInWhitchFolder() {
		List<BroadCasterObserver> list = getObserver();
		if (list != null) {
			for (BroadCasterObserver broadCasterObserver : list) {
				if (broadCasterObserver instanceof FunFolderItemInfo) {
					FunFolderItemInfo folder = (FunFolderItemInfo) broadCasterObserver;
					return folder.getFolderId();
				}
			}
		}
		return 0;
	}

	@Override
	public int getType() {
		return TYPE_APP;
	}

	@Override
	public int getUnreadCount() {
		if (mAppItemInfo == null) {
			return 0;
		}
		return mAppItemInfo.getUnreadCount();
	}

	@Override
	public void setUnreadCount(int unreadCount) {
		broadCast(AppItemInfo.UNREADCHANGE, unreadCount, null, null);
	}

	@Override
	public int getNotificationType() {
		return mNotificationType;
	}

	@Override
	public void setNotificationType(int notificationType) {
		mNotificationType = notificationType;
	}
	@Override
	public void registerObserver(BroadCasterObserver oberver) {
		if (oberver instanceof FunFolderItemInfo) {
			List<BroadCasterObserver> list = getObserver();
			FunFolderItemInfo folder = null;
			if (list != null) {
				for (BroadCasterObserver broadCasterObserver : list) {
					if (broadCasterObserver instanceof FunFolderItemInfo) {
						folder = (FunFolderItemInfo) broadCasterObserver;
					}
				}
				if (folder != null) {
					unRegisterObserver(folder);
				}
			}
		}
		super.registerObserver(oberver);
	}

	@Override
	public void selfConstruct() {
		
	}

	@Override
	public void selfDestruct() {
		mAppItemInfo.unRegisterObserver(this);
		super.clearAllObserver();
	}

}
