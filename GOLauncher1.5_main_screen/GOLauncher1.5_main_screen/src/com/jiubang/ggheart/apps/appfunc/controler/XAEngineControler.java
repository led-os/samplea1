package com.jiubang.ggheart.apps.appfunc.controler;

import java.util.HashSet;

import com.jiubang.core.mars.ITicker;
import com.jiubang.core.mars.XAEngine;

/**
 * XAEngine控制器
 * @author yejijiong
 *
 */
public class XAEngineControler {
	private static int sRefreshStep = 15;
	private static boolean isInit = false;
	private static HashSet<ITicker> mTicker = new HashSet<ITicker>();
	
	public static void resume(ITicker ticker) {
		addTicker(ticker);
		if (XAEngine.isStopped()) {
			XAEngine.resume();
		}
	}
	
	public static void stop(ITicker ticker) {
		removeTicker(ticker);
		if (!XAEngine.isStopped()) {
			XAEngine.stop();
		}
	}
	
	public static void destroy() {
		XAEngine.destory();
		isInit = false;
	}
	
	public static void initEngine(ITicker ticker) {
		if (!isInit) {
			isInit = true;
			sRefreshStep = 15;
			XAEngine.init(sRefreshStep);
			XAEngine.start();
		}
		addTicker(ticker);
		//TODO:这句resume需要后面去掉，由外面调用
		resume(ticker);
	}
	
	private static void addTicker(ITicker ticker) {
		if (ticker != null && !mTicker.contains(ticker)) {
			XAEngine.addTicker(ticker);
			mTicker.add(ticker);
		}
	}
	
	public static void removeTicker(ITicker ticker) {
		if (ticker != null && mTicker.contains(ticker)) {
			boolean flag = false;
			if (!XAEngine.isStopped()) {
				flag = true;
				XAEngine.stop();
			}
			XAEngine.removeTicker(ticker);
			mTicker.remove(ticker);
			if (flag) {
				XAEngine.resume();
			}
		}
	}
}
