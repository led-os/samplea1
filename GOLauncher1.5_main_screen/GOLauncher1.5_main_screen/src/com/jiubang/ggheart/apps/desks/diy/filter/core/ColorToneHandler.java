package com.jiubang.ggheart.apps.desks.diy.filter.core;

import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 * 
 */
public class ColorToneHandler extends AbsFilterHandler {

	@Override
	public Bitmap handler(FilterParameter parameter) {
		if (parameter == null || parameter.mSrcBitmap == null) {
			return null;
		}
		int width = parameter.mSrcBitmap.getWidth();
		int height = parameter.mSrcBitmap.getHeight();
		int[] pixels = new int[width * height];
		parameter.mSrcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		doFilt(pixels, width, height, parameter.getDiyColor(), 192);
		Bitmap bitmap = createBitmapSafe(pixels, width, height);
		if (bitmap == null) {
			bitmap = parameter.mSrcBitmap;
		}
		return bitmap;
	}

	public native void doFilt(int[] pixels, int width, int height, int tone,
			int saturation);

	@Override
	public int getHadlerIds() {
		return IFilterTypeIds.FILTER_TYPE_ROSE
				| IFilterTypeIds.FILTER_TYPE_SAPPHIRE
				| IFilterTypeIds.FILTER_TYPE_LIGHTGREEN
				| IFilterTypeIds.FILTER_TYPE_RED
				| IFilterTypeIds.FILTER_TYPE_LAVENDER
				| IFilterTypeIds.FILTER_TYPE_AUTUMN;
	}

}
