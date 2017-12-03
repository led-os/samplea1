package com.go.util;

import java.util.ArrayList;

import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;

/**
 * 广播器
 * 
 * @author guodanyang
 * 
 */
public class BroadCaster {

	private ArrayList<BroadCasterObserver> mObservers;
	
	//防止广播被滥用出现死锁问题的成员变量同步锁
	private Object mLockerForBroad = new Object();

	/**
	 * 广播器观察者
	 * 
	 * @author guodanyang
	 * 
	 */
	public static interface BroadCasterObserver {
		/**
		 * 回调
		 * 
		 * @param msgId
		 *            id
		 * @param param
		 *            辅助参数
		 * @param object
		 *            辅助参数对象
		 * @param objects
		 *            辅助参数对象数组
		 */
		public void onBCChange(int msgId, int param, Object ...objects);
	}

	/**
	 * 注册观察者
	 * 
	 * @param oberver
	 *            观察者
	 */
	public void registerObserver(BroadCasterObserver oberver) {
		if (oberver == null) {
			// TODO:打日志
			return;
		}
		
		synchronized (mLockerForBroad) {
			
			if (mObservers == null) {
				mObservers = new ArrayList<BroadCasterObserver>();
			}
			
			// TODO:此类中的接口改为同步的
			try {
				if (mObservers.indexOf(oberver) < 0) {
					mObservers.add(oberver);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError error) {
				OutOfMemoryHandler.gcIfAllocateOutOfHeapSize();
			}
		}
	}

	/**
	 * 去注册观察者
	 * 
	 * @param observer
	 *            观察者
	 * @return 是否去注册成功 true:成功 false:不成功或不存在此观察者
	 */
	public boolean unRegisterObserver(BroadCasterObserver observer) {
		
		synchronized (mLockerForBroad) {
			if (null == mObservers) {
				return false;
			}
			
			return mObservers.remove(observer);
		}
	}

	/**
	 * 清理所有的观察者
	 */
	public void clearAllObserver() {
		
		synchronized (mLockerForBroad) {
			if (mObservers != null) {
				mObservers.clear();
				mObservers = null;
			}
		}
	}

	/**
	 * 广播
	 * 
	 * @param msgId
	 * @param param
	 * @param object
	 * @param objects
	 */
	public void broadCast(int msgId, int param, Object ...objects) {
		
		synchronized (mLockerForBroad) {
			if (mObservers == null) {
				return;
			}
			
			ArrayList<BroadCasterObserver> clone = (ArrayList<BroadCasterObserver>) mObservers.clone();
			for (BroadCasterObserver broadCasterObserver : clone) {
				if (broadCasterObserver != null) {
					broadCasterObserver.onBCChange(msgId, param, objects);
				}
			}
		}
	}

	public ArrayList<BroadCasterObserver> getObserver() {
		return mObservers;
	}
}
