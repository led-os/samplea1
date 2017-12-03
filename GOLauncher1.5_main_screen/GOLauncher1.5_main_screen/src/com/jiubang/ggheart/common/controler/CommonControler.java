package com.jiubang.ggheart.common.controler;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import com.go.proxy.SettingProxy;
import com.jiubang.ggheart.common.bussiness.AppClassifyBussiness;
import com.jiubang.ggheart.common.bussiness.AppClassifyBussiness.ArragneAppListener;
import com.jiubang.ggheart.common.bussiness.AppExtraInfoBussiness;
import com.jiubang.ggheart.common.bussiness.AppInvokeLockBussiness;
import com.jiubang.ggheart.common.data.AppExtraAttribute;
import com.jiubang.ggheart.data.Controler;
import com.jiubang.ggheart.data.DatabaseException;
import com.jiubang.ggheart.data.info.AppItemInfo;


/**
 * 公共控制器
 * 功能：（分类应用，产生默认文件夹名称，应用锁集合业务逻辑，应用启动监听线程）
 * @author wuziyi
 *
 */
public class CommonControler extends Controler {

	private static CommonControler sSelfObject;
	private AppClassifyBussiness mAppClassifyBussiness;
	private AppInvokeLockBussiness mAppInvokeLockBussiness;
	private AppInvokeMonitor mAppInvokeMonitor;
	private AppExtraInfoBussiness mAppExtraInfoBussiness;
	
	public final static int APP_LOCK_MONITOR_TASK_ID = 0;
	public final static int MONITOR_BROWSER_INVOKE_TASK_ID = 1;

	private CommonControler(Context context) {
		super(context);
		mAppClassifyBussiness = new AppClassifyBussiness(context);
		mAppInvokeLockBussiness = new AppInvokeLockBussiness(context);
		mAppInvokeMonitor = AppInvokeMonitor.getInstance(context);
		mAppExtraInfoBussiness = new AppExtraInfoBussiness(context);
	}
	
	public synchronized static CommonControler getInstance(Context Context) {
		if (sSelfObject == null) {
			sSelfObject = new CommonControler(Context);
		}
		return sSelfObject;
	}
	
	/**
	 * 查询多个应用分类(初始化时分类)
	 * @param infoList
	 */
	@SuppressWarnings("unchecked")
	public void initAllAppClassify() {
		mAppClassifyBussiness.initAllAppClassify();
	}

	/**
	 * 查询一个应用分类
	 * @param Info
	 */
	public void queryAppClassify(AppItemInfo info) {
		mAppClassifyBussiness.queryAppClassify(info);
	}
	
	/**
	 * 查询info列表的分类
	 * @param info
	 */
	public void queryAppsClassify(ArrayList<AppItemInfo> info) {
		mAppClassifyBussiness.queryAppsClassify(info);
	}
	
	/**
	 * 获取某个分类的集合
	 * @param classification
	 * @param infos
	 * @return 有可能为null
	 */
	public List<AppItemInfo> getClassifyList(int classification, ArrayList<AppItemInfo> infos) {
		SparseArray<ArrayList<AppItemInfo>> map = mAppClassifyBussiness.generateClassifyMap(infos);
		return map.get(classification);
	}
	
	/**
	 * 查询一个应用分类（安装新应用时调用，速度相对较快）
	 * @param pkgName 包名
	 * @param infoList 该包名对应的info列表
	 */
	public void queryAppsClassify(String packageName, ArrayList<AppItemInfo> appItemInfos) {
		mAppClassifyBussiness.queryAppClassify(packageName, appItemInfos);
	}
	
	/**
	 * 智能分类(访问网络版)
	 * @param infoList
	 */
	public void queryAppsClassify(ArrayList<AppItemInfo> infoList, ArragneAppListener listener, int entrance, boolean needOnLine) {
		mAppClassifyBussiness.queryAppsClassify(infoList, listener, entrance, needOnLine);
	}
	
	/**
	 * 根据分类产生一个默认文件夹名称
	 * @return
	 */
	public String generateFolderName(ArrayList<AppItemInfo> infoList) {
		return mAppClassifyBussiness.generateFolderName(infoList);
	}
	
	public String getFolderName(int classification) {
		return mAppClassifyBussiness.getFolderName(classification);
	}
	
	/**
	 * 获取应用锁列表
	 * @param intent
	 */
	public List<AppItemInfo> getAppInvokeLockItems() {
		return mAppInvokeLockBussiness.getAllLockItems();
	}
	
	/**
	 * 给应用锁选择窗口提供的数据集合
	 * @return
	 */
	public List<AppItemInfo> getAllAppListForShow() {
		return mAppInvokeLockBussiness.getAllAppListForShow();
	}
	
	/**
	 * 增加一个应用启动锁（数据库和内存同时操作）
	 * @param intent
	 */
	public void addAppInvokeLockItem(Intent intent) {
		mAppInvokeLockBussiness.addAppInvokeLockItem(intent);
	}
	
	/**
	 * 删除一个应用启动锁（数据库和内存同时操作）
	 * @param intent
	 */
	public void removeAppInvokeLockItem(Intent intent) {
		mAppInvokeLockBussiness.removeAppInvokeLockItem(intent);
	}
	
	/**
	 * 批量增加应用启动锁（使用事务写数据库）
	 * @param list
	 */
	public void addAppInvokeLockItems(ArrayList<Intent> list) {
		mAppInvokeLockBussiness.addAppInvokeLockItems(list);
	}
	
	/**
	 * 批量删除应用启动锁（使用事务写数据库）
	 * @param list
	 */
	public void removeAppInvokeLockItems(ArrayList<Intent> list) {
		mAppInvokeLockBussiness.removeAppInvokeLockItems(list);
	}

	/**
	 * 是否应用了应用锁
	 * @param intent
	 */
	public boolean isLockedApp(Intent intent) {
		return mAppInvokeLockBussiness.isLockedApp(intent);
	}
	
	public boolean isLockedApp(ComponentName componentName) {
		return mAppInvokeLockBussiness.isLockedApp(componentName);
	}
	
	public AppItemInfo getLockAppInfo(ComponentName componentName) {
		return mAppInvokeLockBussiness.getLockedAppInfo(componentName);
	}
	
	/**
	 * 是否应用了应用锁(如果一个包名下有多个图标入口的应用，可能判断不准，不建议使用)
	 * @param packageName
	 */
	public AppItemInfo getLockAppInfo(String packageName) {
		return mAppInvokeLockBussiness.getLockedAppInfo(packageName);
	}
	
	public synchronized void startLockAppMonitor() {
		boolean isEnableAppLock = SettingProxy.getDeskLockSettingInfo().mAppLock;
		if (isEnableAppLock) {
			mAppInvokeMonitor.registObserver(APP_LOCK_MONITOR_TASK_ID, InvokeLockControler.getInstance(mContext));
			mAppInvokeMonitor.startMonitor(APP_LOCK_MONITOR_TASK_ID);
		}
	}
	
	public synchronized void stopLockAppMonitor() {
		InvokeLockControler controlder = InvokeLockControler.getInstance(mContext);
		controlder.ignorePkg(null);
		mAppInvokeMonitor.unregistObserver(APP_LOCK_MONITOR_TASK_ID, controlder);
		mAppInvokeMonitor.stopMonitor(APP_LOCK_MONITOR_TASK_ID);
	}
	
	public boolean isAppClassifyLoadFinish() {
		return mAppClassifyBussiness.isAppClassifyLoadFinish();
	}
	
	public AppExtraAttribute getAppExtraAtturibute(Intent intent) {
		return mAppExtraInfoBussiness.getAppExtraAtturibute(intent);
	}
	
	public AppExtraAttribute getAppExtraAtturibute(ComponentName cn) {
		return mAppExtraInfoBussiness.getAppExtraAtturibute(cn);
	}
	
	public void addAppExtraAtturibute(AppExtraAttribute extra) {
		mAppExtraInfoBussiness.addAppExtraInfo(extra);
	}
	
	public void addAppExtraAttuributes(List<AppExtraAttribute> extras) {
		mAppExtraInfoBussiness.addAppExtraInfos(extras);
	}
	
	public void removeAppExtraAtturibute(AppExtraAttribute extra) {
		try {
			mAppExtraInfoBussiness.removeAppExtraInfo(extra);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeAppExtraAttuributes(List<AppExtraAttribute> infos) {
		mAppExtraInfoBussiness.removeAppExtraInfos(infos);
	}
	
	public void initAllAppAttributes() {
		mAppExtraInfoBussiness.initAttributeMap();
	}
	
	public void saveAppExtraAtturibute(AppExtraAttribute extra) {
		mAppExtraInfoBussiness.saveAppExtraAtturibute(extra);
	}
	
}
