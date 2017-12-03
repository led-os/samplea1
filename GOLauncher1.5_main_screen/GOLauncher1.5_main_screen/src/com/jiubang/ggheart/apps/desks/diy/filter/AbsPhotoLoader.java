package com.jiubang.ggheart.apps.desks.diy.filter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ImageView;

/**
 * 
 * @author zouguiquan
 *
 */
public abstract class AbsPhotoLoader implements Callback {

	private static final String LOADER_THREAD_NAME = "AbsPhotoLoader";

	private static final int MESSAGE_REQUEST_LOADING = 1;
	private static final int MESSAGE_PHOTOS_LOADED = 2;

	private final int mDefaultResourceId;

	private final ConcurrentHashMap<Object, BitmapHolder> mBitmapCache = new ConcurrentHashMap<Object, BitmapHolder>();

	private final ConcurrentHashMap<ImageView, Object> mPendingRequests = new ConcurrentHashMap<ImageView, Object>();

	private final Handler mMainThreadHandler = new Handler(this);

	private LoaderThread mLoaderThread;

	private boolean mLoadingRequested;
	private boolean mPaused;

	protected Context mContext;

	public AbsPhotoLoader(Context context, int defaultResourceId) {
		mContext = context;
		mDefaultResourceId = defaultResourceId;
	}

	public void loadPhoto(ImageView view, Object photoId) {
		if (photoId == null || "".equals(photoId)) {
			view.setImageResource(mDefaultResourceId);
			mPendingRequests.remove(view);
		} else {
			boolean loaded = loadCachedPhoto(view, photoId);
			if (loaded) {
				mPendingRequests.remove(view);
			} else {
				mPendingRequests.put(view, photoId);
				if (!mPaused) {
					requestLoading();
				}
			}
		}
	}

	private boolean loadCachedPhoto(ImageView view, Object photoId) {
		BitmapHolder holder = mBitmapCache.get(photoId);
		if (holder == null) {
			holder = new BitmapHolder();
			mBitmapCache.put(photoId, holder);
		} else if (holder.mState == BitmapHolder.LOADED) {
			if (holder.mBitmapRmef == null) {
				view.setImageResource(mDefaultResourceId);
				return true;
			}

			Bitmap bitmap = holder.mBitmapRmef.get();
			if (bitmap != null) {
				view.setImageBitmap(bitmap);
				return true;
			}

			holder.mBitmapRmef = null;
		}

		view.setImageResource(mDefaultResourceId);
		holder.mState = BitmapHolder.NEEDED;
		return false;
	}

	public void stop() {
		pause();

		if (mLoaderThread != null) {
			mLoaderThread.quit();
			mLoaderThread = null;
		}

		mPendingRequests.clear();
		mBitmapCache.clear();
	}

	public void clear() {
		mPendingRequests.clear();
		mBitmapCache.clear();
	}

	public void pause() {
		mPaused = true;
	}

	public void resume() {
		mPaused = false;
		if (!mPendingRequests.isEmpty()) {
			requestLoading();
		}
	}

	private void requestLoading() {
		if (!mLoadingRequested) {
			mLoadingRequested = true;
			mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
		}
	}

	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case MESSAGE_REQUEST_LOADING : {
				mLoadingRequested = false;
				if (!mPaused) {
					if (mLoaderThread == null) {
						mLoaderThread = new LoaderThread();
						mLoaderThread.start();
					}

					mLoaderThread.requestLoading();
				}
				return true;
			}

			case MESSAGE_PHOTOS_LOADED : {
				if (!mPaused) {
					processLoadedImages();
				}
				return true;
			}
		}
		return false;
	}

	private void processLoadedImages() {
		Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
		while (iterator.hasNext()) {
			ImageView view = iterator.next();
			Object photoId = mPendingRequests.get(view);
			boolean loaded = loadCachedPhoto(view, photoId);
			if (loaded) {
				iterator.remove();
			}
		}

		if (!mPendingRequests.isEmpty()) {
			requestLoading();
		}
	}

	private void cacheBitmap(Object id, Bitmap bitmap) {
		if (mPaused) {
			return;
		}

		BitmapHolder holder = new BitmapHolder();
		holder.mState = BitmapHolder.LOADED;
		if (bitmap != null) {
			holder.mBitmapRmef = new SoftReference<Bitmap>(bitmap);
		}
		mBitmapCache.put(id, holder);
	}

	private void obtainPhotoIdsToLoad(ArrayList<Object> photoIds) {
		photoIds.clear();

		Iterator<Object> iterator = mPendingRequests.values().iterator();
		while (iterator.hasNext()) {
			Object id = iterator.next();
			BitmapHolder holder = mBitmapCache.get(id);
			if (holder != null && holder.mState == BitmapHolder.NEEDED) {
				holder.mState = BitmapHolder.LOADING;
				photoIds.add(id);
			}
		}
	}

	/**
	 * 
	 * @author zouguiquan
	 *
	 */
	private class LoaderThread extends HandlerThread implements Callback {

		private final ArrayList<Object> mPhotoIds = new ArrayList<Object>();
		private Handler mLoaderThreadHandler;

		public LoaderThread() {
			super(LOADER_THREAD_NAME);
		}

		public void requestLoading() {
			if (mLoaderThreadHandler == null) {
				mLoaderThreadHandler = new Handler(getLooper(), this);
			}
			mLoaderThreadHandler.sendEmptyMessage(0);
		}

		public boolean handleMessage(Message msg) {
			handleLoadPhotos();
			mMainThreadHandler.sendEmptyMessage(MESSAGE_PHOTOS_LOADED);
			return true;
		}

		private void handleLoadPhotos() {
			obtainPhotoIdsToLoad(mPhotoIds);

			int count = mPhotoIds.size();
			if (count == 0) {
				return;
			}

			for (Object id : mPhotoIds) {
				Bitmap bitmap = activityloadPhoto(id);
				cacheBitmap(id, bitmap);
				//            	mPhotoIds.remove(id);
			}

			//            count = mPhotoIds.size();
			//            for (int i = 0; i < count; i++) {
			//                cacheBitmap(mPhotoIds.get(i), null);
			//            }
		}
	}
	
	public void release() {
		mBitmapCache.clear();
		mPendingRequests.clear();
	}

	/**
	 * 
	 * @author zouguiquan
	 *
	 */
	private static class BitmapHolder {
		private static final int NEEDED = 0;
		private static final int LOADING = 1;
		private static final int LOADED = 2;

		int mState;
		SoftReference<Bitmap> mBitmapRmef;
	}

	public abstract Bitmap activityloadPhoto(Object id);
}
