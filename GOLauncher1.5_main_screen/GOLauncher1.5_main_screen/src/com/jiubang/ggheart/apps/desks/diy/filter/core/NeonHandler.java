package com.jiubang.ggheart.apps.desks.diy.filter.core;

import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 *
 */
public class NeonHandler extends AbsFilterHandler {

	@Override
	public Bitmap handler(FilterParameter parameter) {
		if (parameter == null || parameter.mSrcBitmap == null) {
			return null;
		}
		int width = parameter.mSrcBitmap.getWidth();
		int height = parameter.mSrcBitmap.getHeight();
		int[] pixels = new int[width * height];
		parameter.mSrcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		doFilt(pixels, width, height, 0xFFFF0000);
		Bitmap bitmap = createBitmapSafe(pixels, width, height);
		if (bitmap == null) {
			bitmap = parameter.mSrcBitmap;
		}
		return bitmap;
	}
	
	public native void doFilt(int[] pixels, int width, int height, int faceColor);

	@Override
	public int getHadlerIds() {
		return IFilterTypeIds.FILTER_TYPE_NEON;
	}

}
