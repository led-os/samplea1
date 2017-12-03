package com.jiubang.ggheart.plugin.shell;

import android.view.KeyEvent;
import android.view.View;

import com.jiubang.ggheart.apps.desks.diy.frames.screen.controller.ScreenControler;


/**
 * ShellManager接口，用于主包与3D插件包进行交互
 * @author yangguanxiang
 *
 */
public interface IShellManager {
	public View getOverlayedViewGroup();
	public View getContentView();
	public ScreenControler getScreenControler();
	public boolean isViewVisible(int viewId);
	public IOrientationControler getGLOrientationControler();
	
	/**
	 * 把Runnable post到渲染线程上执行
	 * @param runnable
	 */
	public void postRunnableSafely(Runnable runnable);
	
	public boolean onKeyDown(int keyCode, KeyEvent event);

	public boolean onKeyUp(int keyCode, KeyEvent event);

	public boolean onKeyLongPress(int keyCode, KeyEvent event);

	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event);
}
