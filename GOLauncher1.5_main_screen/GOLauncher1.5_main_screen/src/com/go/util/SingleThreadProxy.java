package com.go.util;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 异步执行多个任务的线程
 * 多个任务需要排队
 * @author liuheng
 *
 */
public class SingleThreadProxy {
	// 异步线程，用于处理数据库写操作等异步请求
	private static HandlerThread sWorkerThread = new HandlerThread("golauncher-loader");
	
	static {
		sWorkerThread.start();
	}
	
	private static Handler sWorker = new Handler(sWorkerThread.getLooper());

	/**
	 * 提交一个Runable到异步线程上
	 * 
	 * @param r
	 */
	public static boolean postRunable(Runnable r) {
		if (null == sWorker) {
			return false;
		}
		
		sWorker.post(r);
		return true;
	}
}
