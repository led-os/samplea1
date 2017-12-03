package com.jiubang.ggheart.apps.desks.diy.filter;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;

import com.go.util.graphics.BitmapUtility;

/**
 * 
 * @author zouguiquan
 *
 */
public class FilterBitmapHolder {

	private Bitmap mOriginBitmap;
	private SoftReference<Bitmap> mFilterBitmap;
	private /*SoftReference<*/Bitmap/*>*/ mThumbBitmap;

	public Bitmap getOriginBitmap() {
		return mOriginBitmap;
	}

	public void setOriginBitmap(Bitmap originWallpaper) {
		mOriginBitmap = originWallpaper;
	}

	public Bitmap getFilterBitmap() {
		if (mFilterBitmap != null) {
			return mFilterBitmap.get();
		}
		return null;
	}

	public void setFilterBitmap(Bitmap filterBitmap) {
		if (mFilterBitmap != null) {
			mFilterBitmap.clear();
		}
		mFilterBitmap = new SoftReference<Bitmap>(filterBitmap);
	}

	public void setThumbBitmap(Bitmap bitmap, int thumbWidth) {
		if (bitmap == null) {
			return;
		}

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int min = Math.min(width, height);
		float scale = (float) thumbWidth / (float) min;
		Bitmap newBitmap = BitmapUtility.zoomBitmap(bitmap, scale, scale);
		
//		if (newBitmap != null) {
//			mThumbBitmap = new SoftReference<Bitmap>(newBitmap);
//		}
		mThumbBitmap = newBitmap;
	}

	public Bitmap getThumbBitmap() {
//		if (mThumbBitmap != null) {
//			return mThumbBitmap.get();
//		}
		return mThumbBitmap;
	}

	public void release() {
		if (mOriginBitmap != null) {
			mOriginBitmap.recycle();
		}
		if (mFilterBitmap != null) {
			Bitmap bmp = mFilterBitmap.get();
			if (bmp != null) {
				bmp.recycle();
			}
			mFilterBitmap.clear();
		}
//		if (mThumbBitmap != null) {
//			if (mThumbBitmap.get() != null) {
//				mThumbBitmap.get().recycle();
//			}
//			mThumbBitmap.clear();
//		}
	}
}
