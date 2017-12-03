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
public class MilkHandler extends AbsFilterHandler {

	private static final String MILK_BACKGROUND_FILE_NAME = "milk_background.png";
	private static final String MILK_OVERLAY_MAP_FILE_NAME = "overlay_map.png";
	private static final String MILK_HUDSON_MAP_FILE_NAME = "milk_map.png";
	public static final String[] EXSIT_FILE_ARRAY = new String[] {
			MILK_BACKGROUND_FILE_NAME, MILK_OVERLAY_MAP_FILE_NAME,
			MILK_HUDSON_MAP_FILE_NAME };

	private SoftReference<BitmapInfo> mInput4BitmapRef;
	private SoftReference<BitmapInfo> mInput2BitmapRef;
	private SoftReference<BitmapInfo> mInput3BitmapRef;
	private Context mContext;

	public MilkHandler(Context context) {
		try {
			mContext = context;
			if (context == null) {
				return;
			}
			Bitmap input2Bitmap = BitmapFactory.decodeStream(context
					.getAssets().open(MILK_BACKGROUND_FILE_NAME));
			Bitmap input3Bitmap = BitmapFactory.decodeStream(context
					.getAssets().open(MILK_OVERLAY_MAP_FILE_NAME));
			Bitmap input4Bitmap = BitmapFactory.decodeStream(context
					.getAssets().open(MILK_HUDSON_MAP_FILE_NAME));
			BitmapInfo info = new BitmapInfo(getPixels(input4Bitmap),
					input4Bitmap.getWidth(), input4Bitmap.getHeight());
			mInput4BitmapRef = new SoftReference<BitmapInfo>(info);
			info = new BitmapInfo(getPixels(input2Bitmap),
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
						MILK_BACKGROUND_FILE_NAME));
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
						MILK_OVERLAY_MAP_FILE_NAME));
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

	private BitmapInfo getInput4Info() {
		BitmapInfo info = mInput4BitmapRef.get();
		Bitmap bitmap = null;
		if (info == null) {
			Context context = mContext;
			if (context == null) {
				return null;
			}
			try {
				bitmap = BitmapFactory.decodeStream(context.getAssets().open(
						MILK_HUDSON_MAP_FILE_NAME));
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
		BitmapInfo info4 = getInput4Info();
		if (info2 == null || info3 == null || info4 == null) {
			return parameter.mSrcBitmap;
		}
		doFilt(pixels, width, height, info2.mPixels, info2.mWidth,
				info2.mHeight, info3.mPixels, info3.mWidth, info3.mHeight,
				info4.mPixels, info4.mWidth, info4.mHeight, destPixels);
		Bitmap bitmap = createBitmapSafe(destPixels, width, height);
		if (bitmap == null) {
			bitmap = parameter.mSrcBitmap;
		}
		return bitmap;
	}

	public native void doFilt(int[] pixels1, int width1, int height1,
			int[] pixels2, int width2, int height2, int[] pixels3, int width3,
			int height3, int[] pixels4, int width4, int height4, int[] pixels7);

	@Override
	public int getHadlerIds() {
		return IFilterTypeIds.FILTER_TYPE_MILK;
	}

}
