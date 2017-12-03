package com.jiubang.ggheart.apps.desks.diy.filter.core;

import android.graphics.Bitmap;


/**
 * 
 * @author guoyiqing
 *
 */
public abstract class AbsFilterHandler {

	public abstract Bitmap handler(FilterParameter parameter);
	
	public boolean canHandle(int filterTypeId) {
		return (getHadlerIds() & filterTypeId) == filterTypeId;
	}
	
	public abstract int getHadlerIds();
	
	protected Bitmap createBitmapSafe(int[] pixels, int width, int height) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(pixels, width, height,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			try {
				bitmap = Bitmap.createBitmap(pixels, width, height,
						Bitmap.Config.ARGB_4444);
			} catch (OutOfMemoryError e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}
		return bitmap;
	}
	
}
