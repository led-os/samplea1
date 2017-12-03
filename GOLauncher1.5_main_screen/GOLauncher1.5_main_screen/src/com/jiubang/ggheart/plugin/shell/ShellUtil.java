package com.jiubang.ggheart.plugin.shell;

import java.lang.Thread.UncaughtExceptionHandler;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

/**
 * 
 * <br>类描述: Native工具类
 * <br>功能详细描述:
 * 
 */
public class ShellUtil {

	private static UncaughtExceptionHandler sUncaughtExceptionHandler;

	public static void setOnNativeCrashedHandler(UncaughtExceptionHandler handler) {
		sUncaughtExceptionHandler = handler;
	}

	static {
		System.loadLibrary("shellutil");
//		init(); //暂时屏蔽
	}

	// 初始化中断信号的捕获函数
	private static native void init();

	/**
	 * @hide
	 */
	public static void onNativeCrashed(int signal) {
		Log.e("ShellUtil", "ShellUtil.onNativeCrashed(" + signal + ")");
		Exception ex = new RuntimeException("ShellUtil.onNativeCrashed(" + signal + ")");
		ex.printStackTrace();
		if (sUncaughtExceptionHandler != null) {
			sUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), ex);
		}
	}
	
	public static native void glVertexAttribPointer(int indx, int size, int type,
			boolean normalized, int stride, int offset);

	public static native void glDrawElements(int mode, int count, int type, int offset);

	/**
	 * @hide
	 */
	public static native void saveScreenshotTGA(int x, int y, int w, int h, String fileName);

	/**
	 * @hide
	 */
	public static native void saveScreenshot(int x, int y, int w, int h, int[] buffer);

	/**
	 * <br>功能简述: 将图片转化为HSV内部存储格式
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param bitmap
	 * @param optimized 图片是否为HSV格式优化过，即满足色调为0，也即对于每个像素R>=G=B，这样优化过的图片只能设置统一的的色调值
	 * @return
	 */
	public static boolean convertToHSV(Bitmap bitmap, boolean optimized) {
		if (bitmap == null || bitmap.getConfig() != Config.ARGB_8888) {
			return false;
		}

		/*
		// java代码可能会比较慢，并且如果 bitmap.isMutable() 为 false 会导致 bitmap.setPixels 异常,需要复制一份
		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		final int size = width * height;
		int[] pixels = new int[size * 4];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		if (!optimized) {
			final float[] hsv = new float[3];
			for (int index = 0; index < size; ++index) {
				final int color = pixels[index];
				final int alpha = Color.alpha(color);
				final int red = Color.red(color);
				final int green = Color.green(color);
				final int blue = Color.blue(color);
				Color.RGBToHSV(red, green, blue, hsv);
				final int r = (int) (hsv[0] * (255 * 255 / 360.0f) / alpha);	// h / a
				final int g = (int) (hsv[1] * hsv[2] * 255);					// s * v
				final int b = (int) ((1 - hsv[1]) * hsv[2] * 255);				// (1 - s) * v
				pixels[index] = Color.argb(alpha, r, g, b);
			}
		} else {
			for (int index = 0; index < size; ++index) {
				final int color = pixels[index];
				final int alpha = Color.alpha(color);
				final int red = Color.red(color);
				final int green = Color.green(color);
				final int blue = Color.blue(color);
				pixels[index] = Color.argb(alpha, red, red - green, blue);
			}
		}
		try {
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		} catch (Exception e) {
			return false;
		}
		return true;
		*/
		return convertToHSVInternal(bitmap, optimized);

	}

	private static native boolean convertToHSVInternal(Bitmap bitmap, boolean optimized);

}