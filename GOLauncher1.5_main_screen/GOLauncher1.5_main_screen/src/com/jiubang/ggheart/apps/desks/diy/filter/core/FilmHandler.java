package com.jiubang.ggheart.apps.desks.diy.filter.core;

import java.lang.ref.SoftReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @author guoyiqing
 * 
 */
public class FilmHandler extends AbsFilterHandler {

	private static final String FILM_MAP_FILE_NAME = "film_map.png";
	private static final String FILM_VIGNETTE_FILE_NAME = "film_vignette_map.png";
	public static final String[] EXSIT_FILE_ARRAY = new String[] {
			FILM_MAP_FILE_NAME, FILM_VIGNETTE_FILE_NAME };
	private SoftReference<BitmapInfo> mInput2BitmapRef;
	private SoftReference<BitmapInfo> mInput3BitmapRef;
	private Context mContext;

	public FilmHandler(Context context) {
		try {
			mContext = context;
			if (context == null) {
				return;
			}
			Bitmap input2Bitmap = BitmapFactory.decodeStream(context
					.getAssets().open(FILM_MAP_FILE_NAME));
			Bitmap input3Bitmap = BitmapFactory.decodeStream(context
					.getAssets().open(FILM_VIGNETTE_FILE_NAME));
			BitmapInfo info = new BitmapInfo(getPixels(input2Bitmap),
					input2Bitmap.getWidth(), input2Bitmap.getHeight());
			mInput2BitmapRef = new SoftReference<BitmapInfo>(info);
			info = new BitmapInfo(getPixels(input3Bitmap),
					input3Bitmap.getWidth(), input3Bitmap.getHeight());
			mInput3BitmapRef = new SoftReference<BitmapInfo>(info);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	private int[] getPixels(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		return pixels;
	}

	private BitmapInfo getInput3Info() {
		BitmapInfo info = mInput3BitmapRef.get();
		Bitmap bitmap = null;
		if (info == null) {
			Context context = mContext;
			if (context == null) {
				return null;
			}
			try {
				bitmap = BitmapFactory.decodeStream(context.getAssets().open(
						FILM_VIGNETTE_FILE_NAME));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
			info = new BitmapInfo(getPixels(bitmap), bitmap.getWidth(),
					bitmap.getHeight());
		}
		return info;
	}

	private BitmapInfo getInput2Info() {
		BitmapInfo info = mInput2BitmapRef.get();
		Bitmap bitmap = null;
		if (info == null) {
			Context context = mContext;
			if (context == null) {
				return null;
			}
			try {
				bitmap = BitmapFactory.decodeStream(context.getAssets().open(
						FILM_MAP_FILE_NAME));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
			info = new BitmapInfo(getPixels(bitmap), bitmap.getWidth(),
					bitmap.getHeight());
		}
		return info;
	}

	@Override
	public Bitmap handler(FilterParameter parameter) {
		if (parameter.mSrcBitmap == null) {
			return null;
		}
		int width = parameter.mSrcBitmap.getWidth();
		int height = parameter.mSrcBitmap.getHeight();
		int[] pixels = new int[width * height];
		int[] destPixels = new int[width * height];
		parameter.mSrcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		BitmapInfo info2 = getInput2Info();
		BitmapInfo info3 = getInput3Info();
		if (info2 == null || info3 == null) {
			return parameter.mSrcBitmap;
		}
		doFilt(pixels, width, height, info2.mPixels, info2.mWidth,
				info2.mHeight, info3.mPixels, info3.mWidth, info3.mHeight,
				destPixels);
		Bitmap bitmap = createBitmapSafe(destPixels, width, height);
		if (bitmap == null) {
			bitmap = parameter.mSrcBitmap;
		}
		return bitmap;
	}

	public native void doFilt(int[] pixels, int width, int height,
			int[] mPixels, int mWidth, int mHeight, int[] mPixels2,
			int mWidth2, int mHeight2, int[] destPixels);

	@Override
	public int getHadlerIds() {
		return IFilterTypeIds.FILTER_TYPE_FILM;
	}

}
