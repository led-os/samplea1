package com.go.gl.scroller.effector.subscreeneffector;

import com.go.gl.graphics.GLCanvas;

/**
 * 分屏视图容器
 * @author dengweiming
 *
 */
public interface SubScreenContainer {

	/**
	 * 绘制某一屏的子视图。
	 * 里面可以先启用整屏的绘图缓冲（实际绘制内容较多的情况，例如整屏
	 * 都需要绘制，或者子视图重叠较多），或者屏内子视图的绘图缓冲
	 * （实际绘制内容较少的情况）。
	 * 
	 * @param canvas	画布
	 * @param screen	屏幕索引，注意判断是否越界
	 */
	void drawScreen(GLCanvas canvas, int screen);

	/**
	 * 绘制某一屏的子视图。
	 * @param canvas 画布
	 * @param screen 屏幕索引，注意判断是否越界
	 * @param alpha  不透明度[0, 255]
	 */
	void drawScreen(GLCanvas canvas, int screen, int alpha);
}