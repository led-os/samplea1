package com.go.gl.scroller.effector.subscreeneffector;

import com.go.gl.graphics.GLCanvas;

/**
 * 
 * 类描述:功能表分屏视图容器
 * 功能详细描述:
 * 
 * @author  chenjiayu
 * @date  [2012-7-30]
 */
public interface AppdrawerSubScreenContainer extends SubScreenContainer {

	/**
	 * 功能简述:画背景
	 * 功能详细描述:
	 * 注意:
	 * @param canvas
	 * @param screen
	 */
	public void drawScreenBackground(GLCanvas canvas, int screen);

}
