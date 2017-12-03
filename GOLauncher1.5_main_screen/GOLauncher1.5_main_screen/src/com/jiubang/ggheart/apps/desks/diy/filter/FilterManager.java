package com.jiubang.ggheart.apps.desks.diy.filter;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.gau.go.launcherex.R;
import com.go.util.graphics.BitmapUtility;

/**
 * 
 * @author zouguiquan
 *
 */
public class FilterManager {

	private static FilterManager sInstance;
	private FilterBitmapHolder mDataHolder;

	static {
		sInstance = new FilterManager();
	}

	public FilterManager() {
		mDataHolder = new FilterBitmapHolder();
	}

	public static FilterManager getInstance() {
		return sInstance;
	}

	public Drawable getOriginDrawable() {
		BitmapDrawable drawable = new BitmapDrawable(mDataHolder.getOriginBitmap());
		return drawable;
	}

	public Bitmap getOriginBitmap() {
		return mDataHolder.getOriginBitmap();
	}

	public Bitmap getThumbBitmap() {
		return mDataHolder.getThumbBitmap();
	}
	
	public void setFilterBitmap(Bitmap filterBitmap) {
		mDataHolder.setFilterBitmap(filterBitmap);
	}
	
	public Bitmap getFilterBitmap() {
		return mDataHolder.getFilterBitmap();
	}

	public Bitmap getWallpaper(Activity activity) {

		Bitmap result = null;
		Resources resources = activity.getResources();
		WallpaperManager wallPaperManager = WallpaperManager.getInstance(activity);
		Drawable drawable = wallPaperManager.getDrawable();

		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			result = ((BitmapDrawable) drawable).getBitmap();
		} else {
			result = BitmapUtility.createBitmapFromDrawable(drawable);
		}

		cacheWallpaper(result, resources);

		return result;
	}

	private void cacheWallpaper(Bitmap bitmap, Resources resources) {
		if (bitmap != null) {
			int thumbWidth = (int) resources.getDimension(R.dimen.filter_thumb_width);
			mDataHolder.setOriginBitmap(bitmap);
			mDataHolder.setThumbBitmap(bitmap, thumbWidth);
		}
	}

	public void release() {
		mDataHolder.release();
	}
}
