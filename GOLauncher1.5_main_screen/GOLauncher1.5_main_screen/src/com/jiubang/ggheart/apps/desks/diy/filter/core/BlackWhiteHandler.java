package com.jiubang.ggheart.apps.desks.diy.filter.core;

import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 *
 */
public class BlackWhiteHandler extends AbsFilterHandler {

	@Override
	public Bitmap handler(FilterParameter parameter) {
		if (parameter == null || parameter.mSrcBitmap == null) {
			return null;
		}
		int width = parameter.mSrcBitmap.getWidth();
		int height = parameter.mSrcBitmap.getHeight();
		int[] pixels = new int[width * height];
		parameter.mSrcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		doFilt(pixels, width, height);
		Bitmap bitmap = createBitmapSafe(pixels, width, height);
		if (bitmap == null) {
			bitmap = parameter.mSrcBitmap;
		}
		return bitmap;
	}
	
	public native void doFilt(int[] pixels, int width, int height);

	@Override
	public int getHadlerIds() {
		return IFilterTypeIds.FILTER_TYPE_BLACKWHITE;
	}

}
