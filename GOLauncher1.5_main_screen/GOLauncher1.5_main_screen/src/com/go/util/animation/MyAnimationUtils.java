package com.go.util.animation;


/**
 * 动画相关的工具类
 * 
 * @author dengweiming
 * 
 */
public class MyAnimationUtils {

	/**
	 * 计算缩放中心位置的X坐标
	 * 
	 * @param left
	 *            原始左边界
	 * @param width
	 *            原始宽度
	 * @param left2
	 *            目标左边界
	 * @param width2
	 *            目标宽度， 如果和原始宽度相等则没有意义（因为不能缩放）
	 * @return
	 */
	public static float solveScaleCenterX(float left, float width, float left2, float width2) {
		/*
		 * 假设缩放中心为(cx, cy)，则在X轴上有 (cx - left) / (cx - left2) = (cx - right) /
		 * (cx - right2) = (cx - width + left) / (cx - width2 + left2) 解得 cx =
		 * (left * width2 - left2 * width) / (width2 - width)
		 */
		return width2 == width ? 0 : (left * width2 - left2 * width) / (width2 - width);
	}

	/**
	 * 计算缩放中心位置的Y坐标
	 * 
	 * @param top
	 *            原始上边界
	 * @param height
	 *            原始高度
	 * @param top2
	 *            目标上边界
	 * @param height2
	 *            目标高度， 如果和原始高度相等则没有意义（因为不能缩放）
	 * @return
	 */
	public static float solveScaleCenterY(float top, float height, float top2, float height2) {
		return height2 == height ? 0 : (top * height2 - top2 * height) / (height2 - height);
	}

}
