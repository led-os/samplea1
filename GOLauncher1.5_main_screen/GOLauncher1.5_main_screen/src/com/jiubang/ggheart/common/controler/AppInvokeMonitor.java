package com.jiubang.ggheart.common.controler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.content.Context;
import android.util.SparseArray;

/**
 * 检测在检测范围内的程序是否处于正在运行栈顶
 * @author wuziyi
 *
 */
public class AppInvokeMonitor {
	private static final int PERIOD_TIME = 750;
	private static final int DELAY_TIME = 0;
	private ActivityManager mActivityManager;
	private Context mContext;
	private static AppInvokeMonitor sSelfObject;
	private SparseArray<MonitorTask> mTaskMap;
	
	private Timer mTimer;
	
	public static AppInvokeMonitor getInstance(Context context) {
		if (sSelfObject == null) {
			sSelfObject = new AppInvokeMonitor(context);
		}
		return sSelfObject;
	}
	
	private AppInvokeMonitor(Context context) {
		super();
		mContext = context;
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		mTimer = new Timer(true);
		mTaskMap = new SparseArray<MonitorTask>();
	}
	
	public synchronized void startMonitor(int taskId) {
		MonitorTask task = mTaskMap.get(taskId);
		if (mTimer == null) {
			mTimer = new Timer(true);
		}
		if (task == null) {
			task = new MonitorTask();
			mTaskMap.put(taskId, task);
		}
		if (!task.isStartMonitor()) {
			task.setStartMonitor(true);
			mTimer.schedule(task, DELAY_TIME, PERIOD_TIME);
		}
	}
	
	public synchronized void stopMonitor(int taskId) {
		MonitorTask task = mTaskMap.get(taskId);
		if (task != null && task.isStartMonitor()) {
			task.cancel();
			task.setStartMonitor(false);
			mTaskMap.remove(taskId);
			if (mTaskMap.size() == 0) {
				mTimer.cancel();
				mTimer.purge();
				mTimer = null;
			}
		}
	}
	
	public void registObserver(int taskId, MonitorTaskObserver observer) {
		MonitorTask task = mTaskMap.get(taskId);
		if (task != null) {
			task.registObserver(observer);
		}
	}
	
	public void unregistObserver(int taskId, MonitorTaskObserver observer) {
		MonitorTask task = mTaskMap.get(taskId);
		if (task != null) {
			task.unregistObserver(observer);
		}
	}
	
	public boolean isStartMonitor(int taskId) {
		MonitorTask task = mTaskMap.get(taskId);
		if (task != null) {
			return task.isStartMonitor();
		}
		return false;
	}
	
	/**
	 * 线程任务
	 * @author wuziyi
	 *
	 */
	private class MonitorTask extends TimerTask {
		
		private List<MonitorTaskObserver> mObservers;
		private boolean mIsStartMonitor;
		private String mCurTopPkg;
		
		public MonitorTask() {
			mObservers = new ArrayList<AppInvokeMonitor.MonitorTaskObserver>();
		}

		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			String topPackageName;
			topPackageName = ((ActivityManager.RunningTaskInfo) mActivityManager.getRunningTasks(1).get(0)).topActivity.getPackageName();
			if (!topPackageName.equals(mCurTopPkg)) {
				for (MonitorTaskObserver observer : mObservers) {
					observer.onRunningTasksChanged(mCurTopPkg, topPackageName);
				}
				mCurTopPkg = topPackageName;
			}
		}
		
		public void registObserver(MonitorTaskObserver observer) {
			if (!mObservers.contains(observer)) {
				mObservers.add(observer);
			}
		}
		
		public void unregistObserver(MonitorTaskObserver observer) {
			mObservers.remove(observer);
		}
		
		public boolean isStartMonitor() {
			return mIsStartMonitor;
		}
		
		public void setStartMonitor(boolean isStart) {
			mIsStartMonitor = isStart;
		}
	}

	/**
	 * 栈顶扫描观察者
	 * @author wuziyi
	 *
	 */
	public interface MonitorTaskObserver {
		public void onRunningTasksChanged(String lastPkg, String curTopPkg);
	}
}
