package com.jiubang.ggheart.apps.desks.diy.filter.core;

import android.graphics.Bitmap;

/**
 * 
 * @author guoyiqing
 *
 */
public class CloudyHandler extends AbsFilterHandler {

	@Override
	public Bitmap handler(FilterParameter parameter) {
		if (parameter == null || parameter.mSrcBitmap == null) {
			return null;
		}
		int width = parameter.mSrcBitmap.getWidth();
		int height = parameter.mSrcBitmap.getHeight();
		int[] pixels = new int[width * height];
		parameter.mSrcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		doFilt(pixels, width, height, 5);
		Bitmap bitmap = createBitmapSafe(pixels, width, height);
		if (bitmap == null) {
			bitmap = parameter.mSrcBitmap;
		}
//		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		IntBuffer vbb = IntBuffer.wrap(pixels);    	
//		bitmap.copyPixelsFromBuffer(vbb);
//		vbb.clear();
		return bitmap;
	}
	
	public native void doFilt(int[] pixels, int width, int height, float angle);

	@Override
	public int getHadlerIds() {
		return IFilterTypeIds.FILTER_TYPE_CLOUDY;
	}

}
