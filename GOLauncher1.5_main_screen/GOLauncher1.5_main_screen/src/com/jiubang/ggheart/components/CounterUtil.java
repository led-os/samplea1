package com.jiubang.ggheart.components;
/**
 * 右上角图标工具类
 * @author dengdazhong
 *
 */
public class CounterUtil {
	public static int getCounterIconSize(int iconSize) {
		return iconSize * 56 / 112;
	}
	
	public static int getXRightMargin(int iconSize) {
		return 14 * iconSize / 112;
	}
	
	public static float getScale() {
		return 56 / 112f;
	}
}
