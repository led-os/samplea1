package com.jiubang.ggheart.apps.appfunc.controler;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.gau.go.launcherex.R;
import com.go.launcher.taskmanager.TaskMgrControler;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.AppUtils;
import com.go.util.ConvertUtils;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.apps.appfunc.business.AllAppBussiness;
import com.jiubang.ggheart.apps.appfunc.business.AllBussinessHandler;
import com.jiubang.ggheart.apps.appfunc.business.RecentAppBussiness;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.appfunc.AppFuncExceptionHandler;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants.MessageID;
import com.jiubang.ggheart.apps.desks.appfunc.model.IBackgroundInfoChangedObserver;
import com.jiubang.ggheart.apps.desks.diy.AppInvoker.IAppInvokerListener;
import com.jiubang.ggheart.apps.desks.diy.INotificationId;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.broadcastReceiver.NotificationReceiver;
import com.jiubang.ggheart.apps.desks.diy.guide.RateGuideTask;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean.AppBean;
import com.jiubang.ggheart.common.bussiness.AppClassifyBussiness;
import com.jiubang.ggheart.common.bussiness.AppClassifyBussiness.ArragneAppListener;
import com.jiubang.ggheart.common.bussiness.AppClassifyOnlineHandler;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.components.folder.advert.FolderAdController;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.Controler;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FunAppItemInfo;
import com.jiubang.ggheart.data.info.FunFolderItemInfo;
import com.jiubang.ggheart.data.info.FunItemInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.launcher.ThreadName;
import com.jiubang.ggheart.plugin.mediamanagement.MediaPluginFactory;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderController;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderInfo;

/**
 * 
 * <br>类描述: 功能表模块控制器
 * <br>功能详细描述: 负责功能表模块内各业务逻辑处理器的生命周期控制，各业务逻辑处理器返回数据的合成和整理，任务派遣，状态监听等。
 * 
 * @author  yangguanxiang
 * @date  [2012-12-27]
 */
public class AppDrawerControler extends Controler implements ICleanable, IAppInvokerListener {
	// 这堆外部消息不想放在这里，很TMD烦
	public static final int INSTALL_APP = 1;
	public static final int INSTALL_APPS = 2;
	public static final int UNINSTALL_APP = 3;
	public static final int UNINSTALL_APPS = 4;
	public static final int LOAD_FINISH = 5;
	public static final int ADDITEM = 6;
	public static final int REMOVEITEM = 7;
	public static final int SORTFINISH = 8;
	public static final int SDCARDOK = 9;
	public static final int BATADD = 10;
	public static final int FINISHLOADINGSDCARD = 11;
	public static final int STARTSAVE = 12; // 开始保存
	public static final int FINISHSAVE = 13; // 已保存完毕
	public static final int FINISHLOADICONTITLE = 14; // 已加载icon和title完毕
	public static final int TIMEISUP = 15; // 2分钟计时到点
	public static final int BATUPDATE = 16; // 有一批程序可更新
	public static final int HIDE_APPS = 17; // 隐藏程序更新
	public static final int ADDITEMS = 18;
	public static final int REMOVEITEMS = 19;
	public static final int REFREASH_APPDRAWER = 20;
	public static final int REFREASH_FOLDERBAR_TARGET = 21;
//	public static final int REFREASH_ACTION_BAR = 22;
	public static final int FIRST_INIT_DONE = 23;
	public static final int RELOAD_INIT_FINISH = 24;
	public static final int ARRANGE_END = 25;
	
	private static AppDrawerControler sInstance;
	
	//	private FileEngine mFileEngine;
	private AllAppBussiness mAllAppBussiness;
	private RecentAppBussiness mRecentAppBussiness;
//	private RunningAppBussiness mRunningAppBussiness;
	//	private SearchBussiness mSearchBussiness;
	private AppConfigControler mAppConfigControler;
	
	private AllBussinessHandler mAllAppHandler;
	// 安装卸载的缓存数据
	private ArrayList<CacheItemInfo> mCachedApps;
	/**
	 * 最近打开的缓存数据
	 */
	ArrayList<FunAppItemInfo> mRecFunAppItems;
	/**
	 * 正在运行的缓存数据
	 */
	ArrayList<FunAppItemInfo> mProManageAppItems;
	/**
	 * 包含了全部元素，文件夹元素，所有文件夹中所有元素，功能表中元素
	 */
	private AbstractMap<String, FunItemInfo> mAllFunItemInfoMap;
	protected List<FunItemInfo> mItemInfosExceptHide;
	public static byte[] sLayoutLock = new byte[0]; 
	
	public static final String GOSTORECOUNT = "gostorecount";
	public static final String GOSTORE_SHOW_MESSAGE = "gostore_show_message";
	public static final String APPFUNC_APPMENU_SHOW_MESSAGE = "appfunc_appmenu_show_message";
	public static final String APPICON_SHOW_MESSSAGE = "appicon_show_message";

	private IBackgroundInfoChangedObserver mRecentAppObserver;
	private IBackgroundInfoChangedObserver mProManageObserver;
	private Object mInitAllAppLock = new Object();
	private TaskMgrControler mTaskMgrControler;
	private AppsBean mUpdateBeans = null;
	// 状态控制
	private StateParaphrase mStateParaphrase;

	public void setRecentAppObserver(IBackgroundInfoChangedObserver recentAppObserver) {
		mRecentAppObserver = recentAppObserver;
	}
	
	public void setProManageObserver(IBackgroundInfoChangedObserver proManageObserver) {
		mProManageObserver = proManageObserver;
	}

	public static AppDrawerControler getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new AppDrawerControler(context);
		}
		return sInstance;
	}

	private AppDrawerControler(Context context) {
		super(context);
		// 监听程序注册信息的改变
		mAppConfigControler = AppConfigControler.getInstance(context);
		// 注册为监听者
		mAppConfigControler.registerObserver(this);
		mAllAppHandler = new AllBussinessHandler(context, this);
		mAllAppBussiness = new AllAppBussiness(context, mAllAppHandler);
		mRecentAppBussiness = new RecentAppBussiness(context);
		mAllFunItemInfoMap = new ConcurrentHashMap<String, FunItemInfo>();
		mItemInfosExceptHide = new CopyOnWriteArrayList<FunItemInfo>();
		mTaskMgrControler = AppCore.getInstance().getTaskMgrControler();
		mStateParaphrase = new StateParaphrase();
	}

	public ArrayList<AppItemInfo> getRecentAppItems() {
		return mRecentAppBussiness.getRecentAppItems();
	}
	
	
	public ArrayList<FunAppItemInfo> getRecentFunAppItems(int maxCount) {
		if (mRecFunAppItems == null) {
			mRecFunAppItems  = new ArrayList<FunAppItemInfo>();
		} else {
			mRecFunAppItems.clear();
		}
		ArrayList<Intent> recAppItems = mRecentAppBussiness.getRecentAppIntents();
		
		for (int i = 0; i < recAppItems.size() && i < maxCount; i++) {
			Intent intent = recAppItems.get(i);
			FunAppItemInfo itemInfo = (FunAppItemInfo) mAllFunItemInfoMap.get(ConvertUtils.intentToString(intent));
			if (itemInfo != null && !itemInfo.isHide()) {
				mRecFunAppItems.add(itemInfo);
			} else if (itemInfo == null) {
				itemInfo = createSpecialInfo(intent);
				if (itemInfo != null && !itemInfo.isHide()) {
					mRecFunAppItems.add(itemInfo);
				}
			}
		}
		return mRecFunAppItems;
	}
	
	public ArrayList<FunAppItemInfo> getRecentFunAppItemInfo() {
		return mRecFunAppItems;
	}

	/**
	 * <br>功能简述: 添加最近打开数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 * @param index
	 */
	public void addRecentAppItem(final Intent intent) {
		try {
			mRecentAppBussiness.addRecentAppItem(intent);
			if (mRecentAppObserver != null) {
				mRecentAppObserver.handleChanges(MessageID.APP_ADDED, null, null);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <br>功能简述: 删除最近打开数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 */
	public void removeRecentAppItem(final Intent intent) {
		mRecentAppBussiness.removeRecentAppItem(intent);
		if (mRecentAppObserver != null) {
			mRecentAppObserver.handleChanges(MessageID.APP_REMOVED, null, null);
		}
	}

	/**
	 * <br>功能简述: 删除所有最近打开数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void removeAllRecentAppItems() {
		mRecentAppBussiness.removeAllRecentAppItems();
		if (mRecentAppObserver != null) {
			mRecentAppObserver.handleChanges(MessageID.CLEAR_RECENTAPP, null, null);
		}
	}

	public FunItemInfo getFunItemInfo(Intent intent) {
		String intentString = ConvertUtils.intentToString(intent);
		return mAllFunItemInfoMap.get(intentString);
	}

	/**
	 * 获取功能表所有数据，包含隐藏数据（没执行过加载就是空数据）
	 */
	public List<FunItemInfo> getFunItemInfos() {
		return mAllAppBussiness.getFunItemInfos();
	}
	
	public List<FunItemInfo> getFunItemInfosExceptHide(boolean isRefreshList) {
		if (isRefreshList) {
			pickUpHideApps(getFunItemInfos());
		}
		return mItemInfosExceptHide;
	}
	
	public List<FunAppItemInfo> getFunItemInfosExceptFolder() {
		ArrayList<FunAppItemInfo> appItemInfos = new ArrayList<FunAppItemInfo>();
		Iterator<FunItemInfo> iter = mItemInfosExceptHide.iterator();
		while (iter.hasNext()) {
			FunItemInfo funItemInfo = iter.next();
			if (funItemInfo instanceof FunAppItemInfo) {
				appItemInfos.add((FunAppItemInfo) funItemInfo);
			}
		}
		return appItemInfos;
	} 
	
	/**
	 * 从应用列表中挑出隐藏应用，提供新的列表给grid显示<br>
	 * 注意：目前该方法可能会与加载的线程同时执行
	 * @param allAppList
	 */
	private void pickUpHideApps(List<FunItemInfo> allAppList) {
		// 做次垃圾回收，以防泄漏
		if (mItemInfosExceptHide != null) {
			mItemInfosExceptHide.clear();
		}
		synchronized (AllAppBussiness.getAllAppLock()) {
			Iterator<FunItemInfo> iter = allAppList.iterator();
			while (iter.hasNext()) {
				FunItemInfo funItemInfo = iter.next();
				if (!funItemInfo.isHide()) {
					mItemInfosExceptHide.add(funItemInfo);
				}
			}
			AllAppBussiness.getAllAppLock().notify();
		}
	}
	
	/**
	 * 异步加载功能表数据
	 */
	public void startAllAppInitThread() {
		synchronized (mInitAllAppLock) {
			if (mAllAppHandler.isStartedInitAllApp()) {
				try {
					throw new RuntimeException("start init twice? mAllAppHandler isInitedAllFunItemInfo?" + mAllAppHandler.isInitedAllFunItemInfo() + " mAllAppHandler isFirstInit?" + mAllAppHandler.isFirstInit());
				} catch (Exception e) {
					GoAppUtils.postLogInfo("wuziyi", Log.getStackTraceString(e));
				}
				
				// 下面这一行，是处理问题的方法之一，直接判定为已经加载完毕
//				mAllAppHandler.setInitedAllFunItemInfo(true);
				return;
			}
			boolean toUpdate = mStateParaphrase.checkUpdate();
			mAllAppBussiness.startInitThread(mCachedApps, mAllFunItemInfoMap, toUpdate);
		}
	}

	@Override
	public void onInvokeApp(Intent intent) {
		if (intent != null) {
			addRecentAppItem(intent);
		}
	}

	public void handleMessage(int msgId, int param, Object ...obj) {
		switch (msgId) {
			case IAppCoreMsgId.EVENT_UNINSTALL_APP : {
				mRecentAppBussiness.handleUnInstall((ArrayList<AppItemInfo>) obj[1]);
				mRecentAppObserver.handleChanges(MessageID.APP_REMOVED, obj[0], obj[1]);
			}
				break;
			default :
				break;
		}
	}

	@Override
	public void cleanup() {
		if (mRecentAppBussiness != null) {
			mRecentAppBussiness.cleanup();
			mRecentAppBussiness = null;
		}
	}

	public static void destroy() {
		if (sInstance != null) {
			sInstance.cleanup();
			sInstance = null;
		}
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		switch (msgId) {
//			case AppConfigControler.ADDHIDEITEM: {
//				handleHideItem((Intent) object[0], true);
//			}
//				break;
			case AppConfigControler.ADDHIDEITEMS: {
				ArrayList<Intent> list = (ArrayList<Intent>) object[1];
				for (Intent intent : list) {
//					FunItemInfo info = mAllAppBussiness.getFunItemInfo(intent);
					FunItemInfo info = mAllFunItemInfoMap.get(ConvertUtils.intentToString(intent));
					if (info != null) {
						info.setHideInfo(mAppConfigControler.getHideInfo(intent));
					}
				}
				broadCast(HIDE_APPS, param, object[0], object[1]);
			}
				break;
//			case AppConfigControler.DELETEHIDEITEM: {
//				handleHideItem((Intent) object, false);
//			}
//				break;
//			case AppConfigControler.DELETEHIDEITEMS: {
//				handleHideItems((ArrayList<Intent>) objects, false);
//			}
//				break;
//			case AppConfigControler.HIDEAPPCHANG: {
//				handleHideItems((ArrayList<Intent>) objects);
//			}
//				break;
			default:
				break;
		}
	}

	private void handleHideItems(ArrayList<Intent> intents) {
		
		
	}

//	/**
//	 * 设置批量隐藏/不隐藏程序
//	 * 
//	 * @param appItems
//	 */
//	private void handleHideItems(final ArrayList<Intent> appItems,
//			final boolean hide) {
//		if (null == appItems) {
//			return;
//		}
//		Intent it = null;
//		int size = appItems.size();
//		for (int i = 0; i < size; ++i) {
//			it = appItems.get(i);
//			if (null == it) {
//				continue;
//			}
//			handleHideItem(it, hide);
//		}
//	}

//	/**
//	 * 设置隐藏程序
//	 * 
//	 * @param intent
//	 */
//	private void handleHideItem(final Intent intent, final boolean hide) {
//		if (null == intent) {
//			return;
//		}
//		if (mAllAppHandler.isInitedAllFunItemInfo()) {
//			FunItemInfo info = mAllAppBussiness.getFunItemInfo(intent);
//				// 若在根目录
//			if (info != null) {
//				if (info instanceof FunAppItemInfo) {
//					info.setHide(hide);
//				} else if (info instanceof FunFolderItemInfo) {
//					// 若是文件夹
//					((FunFolderItemInfo) info).setHideFunAppItemInfo(intent, hide);
//				}
//			}
//		}
//	}

	/**
	 * <br>
	 * 功能简述:重新开始异步加载一遍所有程序数据 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public final void reloadAllAppItems() {
		mAllAppHandler.resetInitFlag();
		if (mAllFunItemInfoMap != null && !mAllFunItemInfoMap.isEmpty()) {
			Iterator<Entry<String, FunItemInfo>> it = mAllFunItemInfoMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, FunItemInfo> en = it.next();
				FunItemInfo itemInfo = en.getValue();
				if (itemInfo instanceof FunAppItemInfo) {
					FunAppItemInfo appItemInfo = (FunAppItemInfo) itemInfo;
					appItemInfo.selfDestruct();
				} else if (itemInfo instanceof FunFolderItemInfo) {
					FunFolderItemInfo folder = (FunFolderItemInfo) itemInfo;
					if (folder.getFolderType() != GLAppFolderInfo.NO_RECOMMAND_FOLDER) {
						FolderAdController.getInstance().unRegisterFolderAdDataObserver(folder);
					}
				}
				it.remove();
			}
//			mAllFunItemInfoMap.clear();
		}
		startAllAppInitThread();
	}

	public void startSaveThread() {
		mAllAppBussiness.startSaveThread();
	}
	
	public boolean isInitedAllFunItemInfo() {
		return mAllAppHandler.isInitedAllFunItemInfo();
	}
	
	public void handleCacheInfo(Object object, boolean isInstall) {
		addInCacheList((ArrayList<AppItemInfo>) object, isInstall);
		if (mAllAppHandler.isInitedAllFunItemInfo()) {
			handleCachedAppsList();
		}
		
	}
	
	/**
	 * 功能表增加元素(添加到数据库和内存，默认添加到最后)
	 * @param funItemInfo
	 * @param needRefreash
	 */
	public void addFunItemInfo(FunItemInfo funItemInfo, boolean needRefreash) {
		addFunItemInfo(mItemInfosExceptHide.size(), funItemInfo, needRefreash, true, true, true);
	}
	
	/**
	 * 功能表增加元素(添加到数据库和内存)
	 * @param location
	 * @param funItemInfo
	 * @param needRefreash 是否需要刷新功能表
	 */
	public void addFunItemInfo(int location, FunItemInfo funItemInfo, boolean needRefreash) {
		addFunItemInfo(location, funItemInfo, needRefreash, true, true, true);
	}
	
	/**
	 * 功能表增加元素
	 * @param location 
	 * @param funItemInfo
	 * @param needRefreash 是否需要刷新功能表
	 * @param notDuplicate 是否检查重复数据
	 * @param updateMemory 是否更新内存集合
	 * @param updateDB 是否需要更新数据库
	 */
	public void addFunItemInfo(int location, FunItemInfo funItemInfo, boolean needRefreash, boolean notDuplicate, boolean updateMemory, boolean updateDB) {
		try {
			if (updateMemory) {
				String key = ConvertUtils.intentToString(funItemInfo.getIntent());
				if (funItemInfo instanceof FunFolderItemInfo) {
					mAllFunItemInfoMap.put(key, funItemInfo);
				} else if (funItemInfo instanceof FunAppItemInfo) {
					if (!mAllFunItemInfoMap.containsKey(key)) {
						mAllFunItemInfoMap.put(key, funItemInfo);
					}
				}
				synchronized (sLayoutLock) {
					mItemInfosExceptHide.add(location, funItemInfo);
				}
			}
			location = adjustPosition(location);
			mAllAppBussiness.addFunItemInfo(location, funItemInfo, notDuplicate, updateMemory, updateDB);
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
		if (needRefreash) {
			Message msg = mAllAppHandler.obtainMessage();
			msg.what = AllBussinessHandler.MSG_ADDITEM;
			msg.arg1 = location;
			msg.obj = funItemInfo;
			mAllAppHandler.sendMessage(msg);
		}
	}
	
	public void addFunItemInfos(int location, List<FunItemInfo> funItemInfos, boolean needRefreash) {
		try {
			for (FunItemInfo funItemInfo : funItemInfos) {
				String key = ConvertUtils.intentToString(funItemInfo.getIntent());
				if (funItemInfo instanceof FunFolderItemInfo) {
					mAllFunItemInfoMap.put(key, funItemInfo);
				} else if (funItemInfo instanceof FunAppItemInfo) {
					if (!mAllFunItemInfoMap.containsKey(key)) {
						mAllFunItemInfoMap.put(key, funItemInfo);
					}
				}
			}
			synchronized (sLayoutLock) {
				mItemInfosExceptHide.addAll(location, funItemInfos);
			}
			location = adjustPosition(location);
			mAllAppBussiness.addFunItemInfos(location, funItemInfos);
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
		if (needRefreash) {
			Message msg = mAllAppHandler.obtainMessage();
			msg.what = AllBussinessHandler.MSG_ADDITEMS;
			msg.arg1 = location;
			msg.obj = funItemInfos;
			mAllAppHandler.sendMessage(msg);
		}
	}
	
	public void removeFunItemInfo(Intent intent, boolean needRefreash) {
		FunItemInfo itemInfo = getFunItemInfo(intent);
		if (itemInfo != null) {
			removeFunItemInfo(itemInfo, needRefreash);
		}
	}
	
	public void removeFunItemInfo(FunItemInfo funItemInfo, boolean needRefreash) {
		int index = -1;
		try {
			if (funItemInfo instanceof FunFolderItemInfo) {
				mAllFunItemInfoMap.remove(ConvertUtils.intentToString(funItemInfo.getIntent()));
			}
			synchronized (sLayoutLock) {
				index = mItemInfosExceptHide.indexOf(funItemInfo);
			}
			mItemInfosExceptHide.remove(funItemInfo);
			mAllAppBussiness.removeFunItemInfo(funItemInfo);
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
		if (needRefreash) {
			Message msg = mAllAppHandler.obtainMessage();
			msg.what = AllBussinessHandler.MSG_REMOVEITEM;
			msg.obj = funItemInfo;
			msg.arg1 = index;
			mAllAppHandler.sendMessage(msg);
		}
	}
	
	public void removeFunItemInfo(int location, boolean needRefreash) {
		FunItemInfo itemInfo = null;
		int index = -1;
		try {
			itemInfo = mAllAppBussiness.removeFunItemInfo(location);
			if (itemInfo instanceof FunFolderItemInfo) {
				mAllFunItemInfoMap.remove(ConvertUtils.intentToString(itemInfo.getIntent()));
			}
			index = mItemInfosExceptHide.indexOf(itemInfo);
			synchronized (sLayoutLock) {
				mItemInfosExceptHide.remove(index);
			}
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
		if (needRefreash) {
			Message msg = mAllAppHandler.obtainMessage();
			msg.what = AllBussinessHandler.MSG_REMOVEITEM;
			msg.obj = itemInfo;
			msg.arg1 = index;
			mAllAppHandler.sendMessage(msg);
		}
	}
	
	public void removeFunItemInfos(ArrayList<? extends FunItemInfo> funItemInfos, boolean needRefreash) {
		try {
			for (FunItemInfo funItemInfo : funItemInfos) {
				if (funItemInfo instanceof FunFolderItemInfo) {
					mAllFunItemInfoMap.remove(ConvertUtils.intentToString(funItemInfo.getIntent()));
				}
			}
			synchronized (sLayoutLock) {
				mItemInfosExceptHide.removeAll(funItemInfos);
			}
			mAllAppBussiness.removeFunItemInfos(funItemInfos);
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
		if (needRefreash) {
			Message msg = mAllAppHandler.obtainMessage();
			msg.what = AllBussinessHandler.MSG_REMOVEITEMS;
			msg.obj = funItemInfos;
			mAllAppHandler.sendMessage(msg);
		}
	}
	
	public void moveInfoWhenExtrusion(int resIdx, int tarIdx) {
		if (resIdx == tarIdx) {
			return;
		}
		FunItemInfo itemInfo = mItemInfosExceptHide.remove(resIdx);
		mItemInfosExceptHide.add(tarIdx, itemInfo);
	}
	
	public void moveFunItemInfo(FunItemInfo itemInfo, int tarIdx) {
		try {
			mAllAppBussiness.moveFunItemInfo(itemInfo, tarIdx);
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
	}
	
	public void moveFunItemInfo(int resIdx, int tarIdx) {
		try {
			resIdx = adjustPosition(resIdx);
			tarIdx = adjustPosition(tarIdx);
			mAllAppBussiness.moveFunItemInfo(resIdx, tarIdx);
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
	}
	
	/**
	 * 按照当前设置进行一次排序
	 */
	public void startSort(boolean isUpdataDB) {
		try {
			mAllAppBussiness.startSort(isUpdataDB);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	public void sortFolders() {
		try {
			mAllAppBussiness.sortFolders();
		} catch (DatabaseException e) {
			AppFuncExceptionHandler.handle(e, this);
		}
	}
	
	public void handleSDCardEvent(ArrayList<AppItemInfo> sdCardInfos) {
		mStateParaphrase.updateState(StateParaphrase.SDCARD_IS_OK);
//		mCacheSDcard = sdCardInfos;
		// 如果数据仍未被初始化完
		if (isInitedAllFunItemInfo() == false) {
			return;
		}
		handleCacheSDcardList(sdCardInfos);
		// // 检查是否要更新
		// boolean toUpdate = mStateParaphrase.checkUpdate();
		//
		// // sd卡数据更新了, 更新列表
		// handleSDAppItems(mCacheSDcard, toUpdate);

		// SD卡准备了
		broadCast(SDCARDOK, 0, null, null);
	}
	
	public void handleTimeUpEvent(ArrayList<AppItemInfo> sdCardInfos) {
		mStateParaphrase.updateState(StateParaphrase.TIME_IS_UP);
//		mCacheSDcard = sdCardInfos;
		// 如果数据仍未被初始化完
		if (isInitedAllFunItemInfo() == false) {
			return;
		}
		handleCacheSDcardList(sdCardInfos);
		// // 检查是否要更新
		// boolean toUpdate = mStateParaphrase.checkUpdate();
		//
		// // sd卡数据更新了, 更新列表
		// handleSDAppItems(mCacheSDcard, toUpdate);

		// 通知2分钟计时到点
		broadCast(TIMEISUP, 0, null, null);
	}
	
	/**
	 * 处理缓存中SDcard的数据
	 */
	private void handleCacheSDcardList(ArrayList<AppItemInfo> sdCardInfos) {
		if (null == sdCardInfos) {
			return;
		}
		boolean toUpdate = mStateParaphrase.checkUpdate();
		// sd卡数据更新了, 更新列表
		handleSDAppItems(sdCardInfos, toUpdate);
		// 清除缓存数据
//		sdCardInfos = null;
		// 更新title到数据库
		startSaveTitleThread();
	}
	
	/**
	 * 检查当前SD卡事件，3分钟事件是否已经触发，是否需要更新数据库
	 * @return true：接下来的操作需要更新数据库
	 */
//	public boolean checkUpdate() {
//		return mStateParaphrase.checkUpdate();
//	}
	
	/**
	 * 处理sd卡程序，更新内存及数据库
	 */
	private void handleSDAppItems(final ArrayList<AppItemInfo> appItemInfos,
			final boolean updateData) {
		new Thread() {
			@Override
			public void run() {
				synchronized (mAllAppBussiness.getAllAppLock()) {
					if (null == appItemInfos) {
						return;
					}
					// 所有的文件夹
					//				ArrayList<Long> folderIds = new ArrayList<Long>();
					// 复制缓存数据, 这份集合对象会被很多地方搞，以防万一
					ArrayList<AppItemInfo> toAddItemInfos = (ArrayList<AppItemInfo>) appItemInfos
							.clone();
					List<FunItemInfo> allItem = getFunItemInfos();

					AppItemInfo appItemInfo = null;
					// TODO:线程安全性
//					int size = allItem.size();
					int index = 0;
					for (FunItemInfo funItemInfo : allItem) {
						// 若是文件夹
						if (0 != funItemInfo.getFolderId()) {
							// if (FunItemInfo.TYPE_FOLDER ==
							// funItemInfo.getType()) {
							// TODO:去除此行
							//						folderIds.add(funItemInfo.getFolderId());
							// 处理文件夹中的
							((FunFolderItemInfo) funItemInfo).handleSDAppItems(toAddItemInfos);
							index++;
							continue;
						}

						appItemInfo = ((FunAppItemInfo) funItemInfo).getAppItemInfo();
						if (null == appItemInfo) {
							index++;
							continue;
						}

						int idx = mAllAppBussiness.findInList(toAddItemInfos, appItemInfo.mIntent);
						// 不是暂存数据
						if (!appItemInfo.isTemp()) {
							// 若功能表列表中已存在，则从添加列表中删除
							if (idx >= 0) {
								// 从toAddItemInfos中删除
								toAddItemInfos.remove(idx);
							}
							index++;
							continue;
						}

						if (idx >= 0) {
							// 若在sd卡程序数组中
							// 更新数据
							AppItemInfo info = AppDataEngine.getInstance(mContext).getAppItem(
									appItemInfo.mIntent);
							if (info != null) {
								((FunAppItemInfo) funItemInfo).setAppItemInfo(info);
							}
							// 从toAddItemInfos中删除
							toAddItemInfos.remove(idx);
						} else {
							if (updateData) {
								// 不在sd卡程序数组中,从内存及数据库中移除
								removeFunItemInfo(funItemInfo, false);
								mAllFunItemInfoMap.remove(ConvertUtils.intentToString(funItemInfo.getIntent()));
								//							removeFunItemInfo(index, false);
								// mAppConfigControler.delHideAppItem(appItemInfo.mIntent);
							}
						}
						index++;
					}
					if (updateData) {
						for (AppItemInfo appItemInfo2 : toAddItemInfos) {
							FunItemInfo itemInfo = new FunAppItemInfo(appItemInfo2);
							addFunItemInfo(itemInfo, false);
						}
					}
					// 通知sd卡数据已加载好
					refreshAppDrawer();

					Message message = mAllAppHandler.obtainMessage();
					message.what = AllBussinessHandler.MSG_FINISHLOADINGSDCARD;
					mAllAppHandler.sendMessage(message);
				}
			}
		}.start();
	}
	

	private synchronized void startSaveTitleThread() {
		// LogUnit.i("FunControler", "startSaveTitleThread()");
		// 更新title到数据库
		new Thread(ThreadName.FUNC_SAVE_TITLE) {
			@Override
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				try {
					mAllAppBussiness.updateTitle();
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void updateFunItemTitle(Intent intent, String title) {
		try {
			mAllAppBussiness.updateFunItemTitle(intent, title);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释义类，决策该状态下的操作
	 * 
	 * @author guodanyang
	 * 
	 */
	private class StateParaphrase {
		// 状态
		private int mState = NONE;

		// State
		private static final int NONE = 0;
		private static final int SDCARD_IS_OK = 1;
		private static final int TIME_IS_UP = 2;

		public void updateState(final int state) {
			mState = state;
		}

		public boolean checkUpdate() {
			boolean toUpdate = false;
			switch (mState) {
				case NONE: {
					toUpdate = false;
					break;
				}
				case SDCARD_IS_OK: {
					toUpdate = true;
					break;
				}
	
				case TIME_IS_UP: {
					// 若SD被移走或者位于正常的mount状态,则更新
					String sdState = Environment.getExternalStorageState();
					// Log.i("FunControler",
					// "---------------checkUpdate, status: "
					// + sdState);
					if (sdState.equals(Environment.MEDIA_MOUNTED)
							|| sdState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)
					/* || sdState.equals(Environment.MEDIA_REMOVED) */) {
						toUpdate = true;
					}
					// toUpdate = !Environment.getExternalStorageState().equals(
					// Environment.MEDIA_SHARED);
					break;
				}
				default:
					break;
			}
			return toUpdate;
		}
	}
	
	/**
	 * 缓存类，用于记录安装卸载程序信息
	 * 
	 * @author yangguanxiang
	 * 
	 */
	public class CacheItemInfo {
		public AppItemInfo itemInfo;
		public boolean isInstall; // True为安装, False为卸载

		public CacheItemInfo(AppItemInfo info, boolean isInstall) {
			this.itemInfo = info;
			this.isInstall = isInstall;
		}
	}
	
	/**
	 * 将安装卸载数据加入缓存列表
	 * 
	 * @param addList
	 * @param isInstall
	 */
	public void addInCacheList(ArrayList<AppItemInfo> addList,	boolean isInstall) {
		if (null == addList) {
			return;
		}
		if (mCachedApps == null) {
			mCachedApps = new ArrayList<CacheItemInfo>();
		}
		synchronized (mCachedApps) {
			for (AppItemInfo appItemInfo : addList) {
				CacheItemInfo cacheItemInfo = new CacheItemInfo(appItemInfo,
						isInstall);
				mCachedApps.add(cacheItemInfo);
			}
		}
	}
	
	/**
	 * 将安装卸载数据加入缓存列表
	 * 
	 * @param addList
	 * @param isInstall
	 */
	public void addInCacheList(AppItemInfo app,	boolean isInstall) {
		if (null == app) {
			return;
		}
		if (mCachedApps == null) {
			mCachedApps = new ArrayList<CacheItemInfo>();
		}
		synchronized (mCachedApps) {
			CacheItemInfo cacheItemInfo = new CacheItemInfo(app, isInstall);
			mCachedApps.add(cacheItemInfo);
		}
	}
	
	/**
	 * 处理缓存中的安装卸载数据(其实这玩意应该通用到其他grid，如文件夹，最近打开)
	 */
	public synchronized void handleCachedAppsList() {
		if (null == mCachedApps) {
			return;
		}
		synchronized (mCachedApps) {
			for (CacheItemInfo cacheInfo : mCachedApps) {
				if (cacheInfo.isInstall) {
					// 全新安装的应用真的会在文件夹里面，就是临时的机器人图标
					// 我有个很严重的问题，全新安装的应用会在文件夹里面？
					// 先从内存中的文件夹里搜索，如果找不到才添加到文件夹外部
					// 安装程序
					try {
						if (cacheInfo.itemInfo != null) {
							String key = ConvertUtils.intentToString(cacheInfo.itemInfo.mIntent);
							FunAppItemInfo addItemInfo = (FunAppItemInfo) mAllFunItemInfoMap.get(key);
							if (addItemInfo == null) {
								// 功能表的数据中(包括已经在文件夹内的)找不到
								// 构造插入对象
								addItemInfo = new FunAppItemInfo(cacheInfo.itemInfo);
//								addItemInfo.setIsNew(true);
								addItemInfo.setHideInfo(AppConfigControler.getInstance(mContext).getHideInfo(addItemInfo.getIntent()));
								mAllFunItemInfoMap.put(key, addItemInfo);
								if (cacheInfo.itemInfo.mClassification != AppClassifyBussiness.NO_CLASSIFY_APP) {
									// 加到文件夹
									FunFolderItemInfo folderItemInfo = getExistFolder(cacheInfo.itemInfo.mClassification);
									if (folderItemInfo != null) {
										GLAppFolderController folder = GLAppFolderController.getInstance();
										folder.addAppToDrawerFolder(folderItemInfo, addItemInfo, true);
										
										PrivatePreference pp = PrivatePreference.getPreference(mContext);
										if (!pp.getBoolean(PrefConst.KEY_INSTALL_SHOW_NOTIFICATION, false)) {
											// 弹通知提示
											String ticker = mContext.getString(R.string.appdrawer_install_into_folder, cacheInfo.itemInfo.mTitle, folderItemInfo.getTitle());
											String title = mContext.getString(R.string.appdrawer_locate_app_in_folder, cacheInfo.itemInfo.mTitle, folderItemInfo.getTitle());
											String content = mContext.getString(R.string.appdrawer_install_content);
											Intent intent = new Intent(ICustomAction.ACTION_APPDRAWER_LOCATE_APP);
											intent.putExtra(NotificationReceiver.APP_INTENT, cacheInfo.itemInfo.mIntent);
											AppUtils.sendNotificationBCintent(mContext, intent, R.drawable.icon, ticker, title,
													content, INotificationId.APPDRAWER_LOACAT_APP, PendingIntent.FLAG_CANCEL_CURRENT);
											pp.putBoolean(PrefConst.KEY_INSTALL_SHOW_NOTIFICATION, true);
										}
									} else {
										mAllAppBussiness.addFunItemInfo(addItemInfo);
									}
								} else {
									// 加到队列末尾
									mAllAppBussiness.addFunItemInfo(addItemInfo);
								}
							} else {
								// 找得到
								addItemInfo.setAppItemInfo(cacheInfo.itemInfo);
							}
//							// 通知安装了程序
							broadCast(INSTALL_APP, 0, addItemInfo);
						}
					} catch (DatabaseException e) {
						AppFuncExceptionHandler.handle(e, this);
					}
				} else {
						// 卸载程序
						// 应用有可能在文件夹里面，这里查找删除，后续完成
					if (cacheInfo.itemInfo != null) {
						uninstallApp(cacheInfo.itemInfo.mIntent);
					}
				}
			}
			// 清除缓存数据
			mCachedApps.clear();
			mCachedApps = null;
		}
	}
	
	public void uninstallApp(Intent intent) {
		try {
			FunItemInfo delItemInfo = getFunItemInfo(intent);
			mAllAppBussiness.removeFunItemInfo(delItemInfo);
			mAllFunItemInfoMap.remove(ConvertUtils.intentToString(delItemInfo.getIntent()));
			// 从隐藏名单中去除
			mAppConfigControler.delHideAppItem(delItemInfo.getIntent(), false);
//			// 通知卸载了程序
			broadCast(UNINSTALL_APP, 0, delItemInfo);
		} catch (Exception e) {
			AppFuncExceptionHandler.handle(e, this);
		}
	}
	
	/**
	 * <br>
	 * 功能简述:获取保存在shareprefencd中的应用程序可更新个数。 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @return
	 */
	public int getUpdateableAppCount() {
		// 从shareprefencd里得到数字
		PreferencesManager preferences = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE,
				Context.MODE_PRIVATE);
		int mBeancount = preferences.getInt(GOSTORECOUNT, 0);
		return mBeancount;
	}
	
	/**
	 * 是否存在隐藏应用
	 * @return
	 */
	public boolean isExistHideApps() {
		return mAppConfigControler.isExistHideApp();
	}
	
	/**
	 * 创建特别的info（某些应用，拥有不同的intent启动应用，外部的资源不足以提供info时调用）
	 * @param intent
	 * @return
	 */
	public FunAppItemInfo createSpecialInfo(Intent intent) {
		FunAppItemInfo funAppItemInfo = null;
		AppItemInfo appItemInfo = AppDataEngine.getInstance(mContext).getAppItem(intent);
		if (appItemInfo != null) {
			funAppItemInfo = new FunAppItemInfo(appItemInfo);
			funAppItemInfo.setIntent(intent);
			funAppItemInfo.setHideInfo(AppConfigControler.getInstance(mContext).getHideInfo(intent));
		}
		return funAppItemInfo;
	}
	
	/**
	 * 获取正在运行程序数据
	 */
	public ArrayList<FunAppItemInfo> getProManageFunAppItems() {
		ArrayList<TaskMgrControler.ProgressesInfo> infoList = mTaskMgrControler.getProgressesInfoList();
		if (mProManageAppItems == null) {
			mProManageAppItems  = new ArrayList<FunAppItemInfo>();
		} else {
			mProManageAppItems.clear();
		}
		if (infoList == null) {
			return mProManageAppItems;
		}
		boolean isShowNeglectApp = false;
		if (FunAppSetting.NEGLECTAPPS == SettingProxy.getFunAppSetting()
				.getShowNeglectApp()) {
			isShowNeglectApp = true;
		}
		for (TaskMgrControler.ProgressesInfo info : infoList) {
			FunAppItemInfo itemInfo = (FunAppItemInfo) mAllFunItemInfoMap.get(ConvertUtils
					.intentToString(info.mIntent));
			if (itemInfo == null) {
				itemInfo = createSpecialInfo(info.mIntent);
			}
			if (itemInfo != null && !itemInfo.isHide()) {
				itemInfo.setPid(info.mPid);
				itemInfo.setIsIgnore(info.mIsInWhiteList);
				if (!itemInfo.isIgnore()) { // 不显示白名单时不将白名单加进List
					mProManageAppItems.add(itemInfo);
				} else {
					if (!isShowNeglectApp) {
						mProManageAppItems.add(0, itemInfo);
					}
				}
			}
		}
		return mProManageAppItems;
	}
	
	/**
	 * 清除所有正在运行程序（白名单程序除外）
	 */
	public void terminateAllProManageTask() {
		terminateAllProManageTask(mProManageAppItems);
	}

	/**
	 * 清除指定正在运行程序（白名单程序除外）
	 */
	public void terminateAllProManageTask(ArrayList<FunAppItemInfo> appInfoList) {
		if (appInfoList != null) {
			long memOld = mTaskMgrControler.retriveAvailableMemory() / 1024;
			mTaskMgrControler.terminateAllProManageTask(appInfoList);
			long memNew = mTaskMgrControler.retriveAvailableMemory() / 1024;
			int memCleaned = (int) (memNew - memOld);
			memCleaned = memCleaned > 0 ? memCleaned : 20;
			RateGuideTask.getInstacne(mContext.getApplicationContext()).scheduleShowRateDialog(
					RateGuideTask.EVENT_RUNNING_CLEAN, String.valueOf(memCleaned));
			if (mProManageObserver != null) {
				mProManageObserver.handleChanges(MessageID.ALL_TASKMANAGE, null, null);
			}
		}
	}

	/**
	 * 获取功能表所有文件夹信息
	 * @return
	 */
	public List<FunFolderItemInfo> getFunFolders() {
		return mAllAppBussiness.getFunFolders();
	}
	
	/**
	 * 打开程序详细信息
	 * @param intent
	 */
	public void skipAppInfobyIntent(Intent intent) {
		mTaskMgrControler.skipAppInfobyIntent(intent);
	}
	
	/**
	 * 添加程序至白名单中
	 * @param intent
	 */
	public void addIgnoreAppItem(Intent intent) {
		mTaskMgrControler.addIgnoreAppItem(intent);
		notifyLockListChange();
	}
	
	/**
	 * 在白名单中删除程序
	 * @param intent
	 */
	public void delIgnoreAppItem(Intent intent) {
		mTaskMgrControler.delIgnoreAppItem(intent);
		notifyLockListChange();
	}
	
	/**
	}
	 * 结束进程
	 * @param pid 进程唯一的标识
	 */
	public void terminateApp(FunAppItemInfo info) {
		if (info != null) {
			mTaskMgrControler.terminateApp(info.getPid());
			if (mProManageObserver != null) {
				mProManageObserver.handleChanges(MessageID.SINGLE_TASKMANAGE, info, null);
			}
		}
	}

	/**
	 * 通知锁定程序列表发生变化
	 */
	public void notifyLockListChange() {
		if (mProManageObserver != null) {
			mProManageObserver.handleChanges(MessageID.LOCK_LIST_CHANGED, null, null);
		}
	}

	/**
	 * 获取当前可用内存空间
	 */
	public long retriveAvailableMemory() {
		return mTaskMgrControler.retriveAvailableMemory();
	}
	
	/**
	 * 获取手机总内存空间
	 * @return
	 */
	public long retriveTotalMemory() {
		return mTaskMgrControler.retriveTotalMemory();
	}
	
	public boolean appListUpdate(Object param) {
		if (param == null || !(param instanceof AppsBean)) {
			return false;
		}
		AppsBean beans = (AppsBean) param;
		clearAllAppUpdate();
		if (beans != null) {
			mUpdateBeans = beans;
			HashMap<Integer, Byte> controlMap = beans.mControlcontrolMap;
			if (controlMap != null && !controlMap.isEmpty()) {
				setmControlInfo(controlMap.get(2), controlMap.get(3),
						controlMap.get(4));
			}

			// 更新后更新软件的集合
			ArrayList<AppBean> beanList = beans.mListBeans;
			if (beanList != null) {
				setUpdateableAppCount(beanList.size());
				checkAppUpdate(beans);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 功能简述:设置更新信息 <br>
	 * 功能详细描述:根据网络获取的应用程序更新信息检查itemInfos中的程序信息是否有更新，如有则设置显示更新标识 <br>
	 * @param itemInfos
	 * @param inFolder
	 */
	public void checkAppUpdate(AppsBean updateBeans) {
		if (mAllFunItemInfoMap == null) {
			return;
		}
		FunAppItemInfo appInfo = null;
		for (FunItemInfo info : mAllFunItemInfoMap.values()) {
			if (info instanceof FunAppItemInfo) {
				appInfo = (FunAppItemInfo) info;
				appInfo.setIsUpdate(false);
			}
		}
		if (updateBeans != null && updateBeans.mListBeans != null) {
			for (AppBean bean : updateBeans.mListBeans) {
				for (FunItemInfo info : mAllFunItemInfoMap.values()) {
					if (info != null && info.getType() == FunItemInfo.TYPE_APP) {
						String pkName = info.getIntent().getComponent()
								.getPackageName();
						if (pkName != null && pkName.equals(bean.mPkgName)) {
							if (info instanceof FunAppItemInfo) {
								appInfo = (FunAppItemInfo) info;
								appInfo.setIsUpdate(true);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 功能简述:清除所有更新信息，包括功能表中所有程序的更新信息和缓存中保存的从网络中获取的应用更新数据 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public boolean clearAllAppUpdate() {
		boolean flag = false;
		if (mAllFunItemInfoMap != null) {
			for (FunItemInfo info : mAllFunItemInfoMap.values()) {
				FunAppItemInfo appInfo = null;
				if (info != null && info instanceof FunAppItemInfo) {
					appInfo = (FunAppItemInfo) info;
					if (appInfo.isUpdate()) {
						flag = true;
						appInfo.setIsUpdate(false);
					}
				}
			}
		}
		return flag;
	}

	public void refreshAppDrawer() {
		Message msg = mAllAppHandler.obtainMessage();
		msg.what = AllBussinessHandler.MSG_REFRESH_APPDRAWER;
		mAllAppHandler.sendMessage(msg);
	}
	
	public void refreshFolderBarTarget(FunFolderItemInfo folderInfo) {
		Message msg = mAllAppHandler.obtainMessage();
		msg.obj = folderInfo;
		msg.what = AllBussinessHandler.MSG_REFRESH_FOLDERBAR_TARGET;
		mAllAppHandler.sendMessage(msg);
	}
	
	
	/**
	 * 保存应用更新提示是否开关信息
	 * 
	 * @param appIconControl
	 *            是否显示功能表图标右上角应用更新提示 0为不显示，1为显示
	 * @param gostoreControl
	 *            是否显示GO Store功能表图标右上角更新提示 0为不显示，1为显示
	 * @param appFuncMenuControl
	 *            是否显示功能表Menu菜单应用更新提示 0为不显示，1为显示
	 */
	public void setmControlInfo(byte appIconControl, byte gostoreControl,
			byte appFuncMenuControl) {
		PreferencesManager preferences = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE,
				Context.MODE_PRIVATE);
		preferences.putInt(APPICON_SHOW_MESSSAGE, appIconControl); // 功能表图标右上角
		preferences.putInt(GOSTORE_SHOW_MESSAGE, gostoreControl); // GO
																	// Store功能表图标右上角
		preferences.putInt(APPFUNC_APPMENU_SHOW_MESSAGE, appFuncMenuControl); // 功能表Menu菜单
		preferences.commit();
	}
	
	/**
	 * 功能简述:保存应用程序可更新个数到shareprefencd中 <br>
	 * 功能详细描述: <br>
	 * @param mBeancount
	 */
	public void setUpdateableAppCount(int mBeancount) {
		PreferencesManager preferences = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE,
				Context.MODE_PRIVATE);
		preferences.putInt(GOSTORECOUNT, mBeancount);
		preferences.commit();
	}
	
	/**
	 * 获取当前可更新的应用程序列表信息
	 * 
	 * @return
	 */
	public AppsBean getUpdateableAppBeans() {
		return mUpdateBeans;
	}
	
	/**
	 * 检查是否资源管理插件包安装
	 */
	public void checkIsMediaManagementPluginInstall(Object object) {
		if (object != null && object instanceof String) {
			if (((String) object).equals(PackageName.MEDIA_PLUGIN)) {
				// 通知多媒体插件包安装
				MediaPluginFactory.setMediaPluginExist(1);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_DRAWER,
						IAppCoreMsgId.MEDIA_PLUGIN_CHANGE, -1, true, null);
			}
		}
	}
	
	/**
	 * 检查是否资源管理插件包卸载
	 * @param object
	 */
	public void checkIsMediaManagementPluginUnInstall(Object object) {
		if (object != null && object instanceof String) {
			if (((String) object).equals(PackageName.MEDIA_PLUGIN)) {
				// 多媒体插件包卸载，删除.dex文件
				MediaPluginFactory.deleteDexFile(PackageName.MEDIA_PLUGIN);
//				// 多媒体插件包卸载，重启桌面
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
//						ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
			}
		}
	}
	
	/**
	 * 检查是否资源管理插件包更新
	 * @param object
	 */
	public void checkIsMediaManagementPluginUpdate(Object object) {
		if (object != null && object instanceof String) {
			if (((String) object).equals(PackageName.MEDIA_PLUGIN)) {
				// 多媒体插件包更新，删除.dex文件
				MediaPluginFactory.deleteDexFile(PackageName.MEDIA_PLUGIN);
				// 多媒体插件包更新，重启桌面
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICommonMsgId.RESTART_GOLAUNCHER, -1, null, null);
			}
		}
	}
	
	public void backupAppTable() {
		mAllAppBussiness.backupAppTable();
	}
	
	public boolean checkSupportAppTableRestore() {
		return mAllAppBussiness.checkSupportAppTableRestore();
	}
	
	public void restoreAppTable() {
		mAllAppBussiness.restoreAppTable();
	}
	/**
	 * 调整因为隐藏而改变了位置
	 * @param position 不含有隐藏程序时的位置
	 * @return 含有隐藏程序的位置，不会超过队列最大值
	 */
	private int adjustPosition(int position) {
		if (mAppConfigControler.isExistHideApp()) {
			// 如果存在隐藏程序，那么该info要添加到的位置要进行调整
			List<FunItemInfo> infos = mAllAppBussiness.getFunItemInfos();
			int size = infos.size();
			for (int i = 0; i <= position && position < size; i++) {
				FunItemInfo info = infos.get(i);
				if (info.isHide()) {
					position++;
				}
			}
		}
		return position;
	}
	
	private void createArrangeAppFolders(CommonControler common, List<FunAppItemInfo> appList) {
		HashMap<Integer, ArrayList<FunAppItemInfo>> appGroupMap = new HashMap<Integer, ArrayList<FunAppItemInfo>>();
		// 首先对所有未在文件夹内的应用进行分类
		synchronized (AppClassifyBussiness.sClassifyLock) {
			for (FunAppItemInfo info : appList) {
				if (info.getAppItemInfo().mClassification != AppClassifyBussiness.NO_CLASSIFY_APP) {
					ArrayList<FunAppItemInfo> group = null;
					if ((group = appGroupMap
							.get(info.getAppItemInfo().mClassification)) == null) {
						group = new ArrayList<FunAppItemInfo>();
						appGroupMap.put(info.getAppItemInfo().mClassification,
								group);
					}
					group.add(info);
				}
			}
		}
		// StringBuilder builder = new StringBuilder();
		// 跟进类型创建文件夹
		Iterator<Entry<Integer, ArrayList<FunAppItemInfo>>> groupIterator = appGroupMap.entrySet().iterator();
		GLAppFolderController folderController = GLAppFolderController.getInstance();
		
		boolean needBackUp = true;
		while (groupIterator.hasNext()) {
			Entry<Integer, ArrayList<FunAppItemInfo>> entry = groupIterator.next();
			int classify = (Integer) entry.getKey();
			ArrayList<FunAppItemInfo> list = entry.getValue();
			String folderName = common.getFolderName(classify);
			FunFolderItemInfo folderItemInfo = getExistFolder(classify);
			if (list.size() > 1 || folderItemInfo != null) {
				try {
					if (needBackUp) {
						backupAppTable();
						needBackUp = false;
					}
					if (folderItemInfo != null) {
						folderController.addAppToDrawerFolderBatch(folderItemInfo, list, false);
						folderItemInfo.sortAfterAdd();
					} else {
						AppDrawerSortHelper.sortList(list, mContext);
						folderItemInfo = folderController.createAppDrawerFolder(list, folderName, 0, false,
								classify);
					}
					if (AppClassifyBussiness.GAMES == classify) {
						GuiThemeStatistics.pluginStaticData("sort_game");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
//		refreshFolderBarTarget(null);
//		refreshAppDrawer();
	}
	
	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param folderName
	 * @return
	 */
	private FunFolderItemInfo getExistFolder(int classification) {
		List<FunFolderItemInfo> folderItemInfos = getFunFolders();
		if (folderItemInfos != null) {
			for (FunFolderItemInfo folder : folderItemInfos) {
				if (classification == folder.getFolderType()) {
					return folder;
				}
			}
		}
		return null;
	}
	
	public void arrangeAppAuto(int entrance, boolean needOnline) {
		final CommonControler common = CommonControler.getInstance(mContext);
		final List<FunAppItemInfo> appList = getFunItemInfosExceptFolder();
		ArrayList<AppItemInfo> infoList = new ArrayList<AppItemInfo>();
		for (FunAppItemInfo funAppItemInfo : appList) {
			infoList.add(funAppItemInfo.getAppItemInfo());
		}
		common.queryAppsClassify(infoList, new ArragneAppListener() {
			
			@Override
			public void onArrangeFinish(int funId) {
				switch (funId) {
					case AppClassifyOnlineHandler.ARRANGE_APP_FUN_ID :
						createArrangeAppFolders(common, appList);
						sortFolders();
						arrangeEnd();
						break;
	
					default:
						break;
				}
			}
			
			@Override
			public void onArrangeException(int funId) {
				switch (funId) {
					case AppClassifyOnlineHandler.ARRANGE_APP_FUN_ID :
						createArrangeAppFolders(common, appList);
						sortFolders();
//						refreshAppDrawer();
						arrangeEnd();
						break;
	
					default:
						break;
				}
			}
		}, entrance, needOnline);
	}
	
	private void arrangeEnd() {
		Message msg = mAllAppHandler.obtainMessage();
		msg.what = AllBussinessHandler.MSG_ARRANGE_END;
		mAllAppHandler.sendMessage(msg);
	}
}
