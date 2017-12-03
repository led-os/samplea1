package com.jiubang.ggheart.common.bussiness;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.go.util.SortHelper;
import com.go.util.sort.CompareMethod;
import com.go.util.sort.CompareTitleMethod;
import com.go.util.sort.ITitleCompareable;
import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.common.data.AppInvokeLockDataModel;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;

/**
 * 
 * @author wuziyi
 *
 */
public class AppInvokeLockBussiness extends BaseBussiness {
	private AppInvokeLockDataModel mDataModel;
	private ArrayList<ComponentName> mInvokeLockList;

	public AppInvokeLockBussiness(Context context) {
		super(context);
		mDataModel = new AppInvokeLockDataModel(context);
		initAppInvokeLockMap();
	}
	
	public void initAppInvokeLockMap() {
		mInvokeLockList = mDataModel.getAppLockItems();
	}
	
	
	public List<AppItemInfo> getAllAppListForShow() {
		if (mInvokeLockList == null) {
			initAppInvokeLockMap();
		}
		if (mInvokeLockList == null) {
			return new ArrayList<AppItemInfo>();
		}
		List<AppItemInfo> infoList = mAppDataEngine.getAllAppItemInfos();
		List<AppItemInfo> lockApps = getAllLockItems();
		infoList.removeAll(lockApps);
		CompareMethod<ITitleCompareable> method = new CompareTitleMethod();
		SortHelper.doSort(infoList, method);
		SortHelper.doSort(lockApps, method);
		
		infoList.addAll(0, lockApps);
		return infoList;
	}
	
	public List<AppItemInfo> getAllLockItems() {
		List<AppItemInfo> infoList = new ArrayList<AppItemInfo>();
		List<ComponentName> delInfo = new ArrayList<ComponentName>();
		if (mInvokeLockList == null) {
			return infoList;
		}
		for (ComponentName cn : mInvokeLockList) {
			AppItemInfo appInfo = mAppDataEngine.getAppItem(cn);
			if (appInfo != null) {
				infoList.add(appInfo);
			} else {
				delInfo.add(cn);
			}
		}
		if (!delInfo.isEmpty()) {
			for (ComponentName cn : delInfo) {
				removeAppInvokeLockItem(cn);
			}
		}
		return infoList;
	}

	/**
	 * 增加一个应用启动锁（数据库和内存同时操作）
	 * @param intent
	 */
	public void addAppInvokeLockItem(Intent intent) {
		if (mInvokeLockList == null) {
			return;
		}
		try {
			mDataModel.addAppLockItem(intent);
			mInvokeLockList.add(intent.getComponent());
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除一个应用启动锁（数据库和内存同时操作）
	 * @param intent
	 */
	public void removeAppInvokeLockItem(Intent intent) {
		ComponentName cn = intent.getComponent();
		removeAppInvokeLockItem(cn);
	}
	
	public void removeAppInvokeLockItem(ComponentName cn) {
		if (mInvokeLockList == null) {
			return;
		}
		if (cn != null) {
			try {
				mDataModel.delAppLockItem(cn);
				// 同步内存
				mInvokeLockList.remove(cn);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 批量增加应用启动锁（使用事务写数据库）
	 * @param list
	 */
	public void addAppInvokeLockItems(ArrayList<Intent> list) {
		if (mInvokeLockList == null) {
			return;
		}
		try {
			// 先清除再添加
			mDataModel.clearAppLockItems();
			for (Intent intent : list) {
				mDataModel.addAppLockItem(intent);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
			return;
		}
		// 只有添加数据库都成功才去更新内存
		mInvokeLockList.clear();
		for (Intent intent : list) {
			mInvokeLockList.add(intent.getComponent());
		}
	}
	
	/**
	 * 批量删除应用启动锁（使用事务写数据库）
	 * @param list
	 */
	public void removeAppInvokeLockItems(ArrayList<Intent> list) {
		if (mInvokeLockList == null) {
			return;
		}
		try {
			mDataModel.removeAppInvokeLockItems(list);
			// 同步内存
			for (Intent intent : list) {
				ComponentName cn = intent.getComponent();
				if (cn != null) {
					for (ComponentName cname : mInvokeLockList) {
						if (cn.equals(cname)) {
							mInvokeLockList.remove(cname);
						}
					}
				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否应用了应用锁
	 * @param intent
	 */
	public boolean isLockedApp(Intent intent) {
		if (mInvokeLockList == null) {
			return false;
		}
		if (intent != null) {
			ComponentName cName = intent.getComponent();
			if (cName != null) {
				for (ComponentName cn : mInvokeLockList) {
					if (cName.equals(cn)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public AppItemInfo getLockedAppInfo(String packageName) {
		if (mInvokeLockList == null) {
			return null;
		}
		if (packageName != null) {
			for (ComponentName cn : mInvokeLockList) {
				if (packageName.equals(cn.getPackageName())) {
					return mAppDataEngine.getAppItem(cn);
				}
			}
		}
		return null;
	}
	
	/**
	 * 是否应用了应用锁
	 * @param componentName
	 */
	public boolean isLockedApp(ComponentName componentName) {
		if (mInvokeLockList == null) {
			return false;
		}
		if (componentName != null) {
			for (ComponentName cn : mInvokeLockList) {
				if (componentName.equals(cn)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public AppItemInfo getLockedAppInfo(ComponentName componentName) {
		if (mInvokeLockList == null) {
			return null;
		}
		if (componentName != null) {
			for (ComponentName cn : mInvokeLockList) {
				if (componentName.equals(cn)) {
					return mAppDataEngine.getAppItem(cn);
				}
			}
		}
		return null;
	}
	
}
