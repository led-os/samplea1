package com.jiubang.ggheart.appgame.base.manage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.go.proxy.ApplicationProxy;
import com.go.util.file.FileUtil;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppsUtils;
import com.jiubang.ggheart.appgame.base.setting.AppGameSettingData;
import com.jiubang.ggheart.appgame.base.utils.AppGameDrawUtils;
import com.jiubang.ggheart.apps.gowidget.gostore.ThreadPoolManager;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.gorecomm.net.HttpRequestUtils;

/**
 * 使用线程池异步加载图片类
 * 
 * @author liliang
 * 
 */
public class AsyncImageManager {
	/**
	 * 跟线程池有关的变量
	 */
	private static final String THREAD_POOL_MANAGER_NAME = "asyncimagemanager_threadPoolManager";
	private static final int THREAD_POOL_COREPOOL_SIZE = 1;
	private static final int THREAD_POOL_MAXIMUMPOOL_SIZE = 3;
	private static final long THREAD_POOL_KEEPALIVE_TIME = 20;
	private static final int CONNECT_TIME_OUT = 10000;
	private static final int READ_TIME_OUT = 30000;
	private ThreadPoolManager mThreadPoolManager = null;
	/**
	 * 单例
	 */
	@Deprecated
	private static AsyncImageManager sInstance = null;
	 
	private static AsyncImageManager sDefaultInstance = null;
	private static AsyncImageManager sLruInstance = null;
	/**
	 * 监听WIFI是否可用
	 */
	private boolean mIsWifiEnable = false;
	/**
	 * 同步主线程的Handler
	 */
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
//	private DeferredHandler mDeferredHandler = new DeferredHandler() {
//		@Override
//		public void handleIdleMessage(Message msg) {
//			if (msg != null && msg.obj != null && msg.obj instanceof CallBackRunnable) {
//				CallBackRunnable call = (CallBackRunnable) msg.obj;
//				call.run();
//			}
//		}
//	};
	/**
	 * 图片缓存池
	 */
	private IImageCache mImageCache = null;

	/**
	 * 控制列表滑动停止后才加载图片的变量
	 */
	//是否是列表首次加载
	private boolean mIsFirstLoad = true;
	//是否允许加载，列表停止时允许加载
	private boolean mIsAllowLoad = true;
	//列表允许加载的开始项
	private int mLimitStart = 0;
	//列表允许加载的结束项
	private int mLimitEnd = 0;
	private byte[] mLoadLock = new byte[0];

	public AsyncImageManager(IImageCache imageCache) {
		mImageCache = imageCache;
		if (mImageCache == null) {
			mImageCache = new DefaultImageCache();
		}
		ThreadPoolManager.buildInstance(THREAD_POOL_MANAGER_NAME, THREAD_POOL_COREPOOL_SIZE,
				THREAD_POOL_MAXIMUMPOOL_SIZE, THREAD_POOL_KEEPALIVE_TIME, TimeUnit.SECONDS, false);
		mThreadPoolManager = ThreadPoolManager.getInstance(THREAD_POOL_MANAGER_NAME);
		//检查WIFI是否可用
		mIsWifiEnable = GoStorePhoneStateUtil.isWifiEnable(ApplicationProxy.getContext());
	}

	@Deprecated
	public static AsyncImageManager getInstance() {
		return buildInstance(null);
	}
	
	@Deprecated
	public synchronized static AsyncImageManager buildInstance(IImageCache imageCache) {
		if (sInstance == null) {
			sInstance = new AsyncImageManager(imageCache);
		}
		return sInstance;
	}
	
	/**
	 * 返回AsyncImageManager对象
	 * 注意：该实例使用DefaultImageCache
	 * @return
	 */
	public synchronized static AsyncImageManager getDefaultInstance() {
		if (sDefaultInstance == null) {
			sDefaultInstance = new AsyncImageManager(new DefaultImageCache());
		}
		return sDefaultInstance;
	}
	
	/**
	 * 返回AsyncImageManager对象
	 * 注意：该实例使用LruImageCache
	 * @return
	 */
	public synchronized static AsyncImageManager getLruInstance(int maxMemorySize) {
		if (maxMemorySize <= 0) {
			throw new IllegalArgumentException("maxMemorySize must more than zero");
		}
		if (sLruInstance == null) {
			sLruInstance = new AsyncImageManager(new LruImageCache(maxMemorySize));
		} else {
			if (((LruImageCache) sLruInstance.mImageCache).getMaxMemorySize() != maxMemorySize) {
				sLruInstance.clear();
				sLruInstance = new AsyncImageManager(new LruImageCache(maxMemorySize));
			}
		}
		return sLruInstance;
	}

	/**
	 * 功能简述:通过图片的URL从内存获取图片的方法 功能详细描述: 注意:
	 * 
	 * @param imgUrl
	 *            图片URL
	 * @return
	 */
	public Bitmap loadImgFromMemery(String imgUrl) {
		Bitmap bm = null;
		if (!TextUtils.isEmpty(imgUrl)) {
			bm = mImageCache.get(imgUrl);
		}
		return bm;
	}

	/**
	 * 功能简述:从SD卡加载图片的方法 功能详细描述: 注意:
	 * 
	 * @param imgPath
	 *            图片所在路径
	 * @param imgName
	 *            图片名称
	 * @param imgUrl
	 *            图片URL
	 * @param isCache
	 *            是否添加到内存的缓存里面
	 * @return
	 */
	public Bitmap loadImgFromSD(String imgPath, String imgName, String imgUrl, boolean isCache) {
		Bitmap result = null;
		try {
			if (FileUtil.isSDCardAvaiable()) {
				File file = new File(imgPath + imgName);
				if (file.exists()) {
					result = BitmapFactory.decodeFile(imgPath + imgName);
					if (result != null && isCache) {
						mImageCache.set(imgUrl, result);
					}
				}
			}
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 功能简述:从网络加载图片的方法 功能详细描述: 注意:
	 * 
	 * @param imgUrl
	 *            图片URL
	 * @return
	 */
	public Bitmap loadImgFromNetwork(String imgUrl) {
		Bitmap result = null;
		InputStream inputStream = null;
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
			//使用HttpClient方式从网络加载图片
			result = loadImagFromHttpClient(imgUrl);
		} else {
			HttpURLConnection urlCon = null;
			try {
				urlCon = createURLConnection(imgUrl);
				inputStream = (InputStream) urlCon.getContent();
				if (inputStream != null) {
					result = BitmapFactory.decodeStream(inputStream);
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				Log.e(AsyncImageManager.class.getName(), "loadImgFromNetwork(" + imgUrl + ")====" + e.toString(), e);
				System.gc();
				result = null;
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				Log.e(AsyncImageManager.class.getName(), "loadImgFromNetwork(" + imgUrl + ")====" + e.toString(), e);
				result = null;
			}  catch (SSLHandshakeException e) {
				//缺少安全证书时出现的异常,重新使用HttpClient方式请求.
				Log.e(AsyncImageManager.class.getName(), "loadImgFromNetwork(" + imgUrl + ")====" + e.toString(), e);
				//使用HttpClient方式从网络加载图片
				result = loadImagFromHttpClient(imgUrl);
				if (result == null) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(AsyncImageManager.class.getName(), "loadImgFromNetwork(" + imgUrl + ")====" + e.toString(), e);
				result = null;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (urlCon != null) {
					urlCon.disconnect();
				}
			}
		}
		return result;
	}
	/**
	 * 使用HttpClient方式从网络加载图片
	 * @param imgUrl 图片下载地址
	 * @return
	 */
	private Bitmap loadImagFromHttpClient(String imgUrl) {
		InputStream inputStream = null;
		try {
			//update by caoyaming 2014-03-12 将http请求封装在HttpRequestUtils类中(目的为了避免产生javax.net.ssl.SSLPeerUnverifiedException: No peer certificate异常)
			HttpResponse httpResponse = HttpRequestUtils.executeHttpRequest(imgUrl);
			if (httpResponse != null && httpResponse.getStatusLine() != null && (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK || httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION) && httpResponse.getEntity() != null) {
				inputStream = httpResponse.getEntity().getContent();
				if (inputStream != null) {
					return BitmapFactory.decodeStream(inputStream);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 功能简述: 加载图片，如果图片在内存里，则直接返回图片，否则异步从SD卡或者网络加载图片 功能详细描述: 注意:
	 * 
	 * @param imgPath 图片保存的SD卡目录
	 * @param imgName 图片保存的名称
	 * @param imgUrl  从网络加载图片的URL
	 * @param isCache 是否缓存到内存
	 * @param isNeed  是否强制加载
	 * @param callBack 回调
	 * @param operator 当图片从网络加载完时，先经过opera处理再保存到SD卡，如果不需要处理，则传null
	 * @return
	 */
	public Bitmap loadImage(final String imgPath, final String imgName, final String imgUrl,
			final boolean isCache, final boolean isNeed, final AsyncNetBitmapOperator operator,
			final AsyncImageLoadedCallBack callBack) {
		if (imgUrl == null || imgUrl.equals("")) {
			return null;
		}
		//TODO:这里是对省流量模式进行控制，已经跟业务有关，后续考虑移走
		// 这里根据设置对图片加载进行了控制
		AppGameSettingData instance = AppGameSettingData.getInstance(ApplicationProxy.getContext());
		int loadtype = 0;
		if (instance != null) {
			loadtype = instance.getTrafficSavingMode();
		}
		// 不需要下载图标
		if (!isNeed && !mIsWifiEnable && loadtype == AppGameSettingData.NOT_LOADING_IMAGES) {
			return null;
		}
		
		Bitmap result = loadImage(imgPath, imgName, imgUrl, isCache, operator, callBack);
		return result;
	}

	public Bitmap loadImage(final String imgPath, final String imgName, final String imgUrl,
			final boolean isCache, final AsyncNetBitmapOperator operator,
			final AsyncImageLoadedCallBack callBack) {
		Bitmap result = null;
		result = loadImgFromMemery(imgUrl);
		if (result == null) {
			if (mThreadPoolManager == null || mThreadPoolManager.isShutdown()) {
				Log.e("AsyncImageManager", "threadPoll == null || threadPoll.isShutdown()");
				return null;
			}
			
			Thread loadimg = new Thread() {
				@Override
				public void run() {
					Bitmap b = null;
					try {
						b = loadImgFromSD(imgPath, imgName, imgUrl, isCache);
						if (b == null) {
							b = loadImgFromNetwork(imgUrl);
							if (operator != null) {
								b = operator.operateBitmap(
										ApplicationProxy.getContext(), b);
							}
							if (b != null) {
								if (FileUtil.isSDCardAvaiable()) {
									FileUtil.saveBitmapToSDFile(b, imgPath + imgName,
											Bitmap.CompressFormat.PNG);
								}
							}
						}
					} catch (OutOfMemoryError error) {
						//爆内存
						error.printStackTrace();
					}
					if (b != null) {
						if (isCache) {
							mImageCache.set(imgUrl, b);
						}
						// 主线程显示图片
						CallBackRunnable cbRunnable = new CallBackRunnable(b, callBack, imgUrl);
//						Message message = new Message();
//						message.obj = cbRunnable;
//						mDeferredHandler.sendMessage(message);
						mHandler.post(cbRunnable);
					}
				}
			};
			mThreadPoolManager.execute(loadimg);
		}
		return result;
	}

	/**
	 * <br>功能简述:专门针对列表项加载图片的方法
	 * <br>功能详细描述:在列表停止滚动时才去加载图片
	 * <br>注意:
	 * @param position
	 * @param imgPath
	 * @param imgName
	 * @param imgUrl
	 * @param isCache
	 * @param isNeed
	 * @param operator 当图片从网络加载完时，先经过opera处理再保存到SD卡，如果不需要处理，则传null
	 * @param callBack
	 * @return
	 */
	public Bitmap loadImageForList(final int position, final String imgPath, final String imgName,
			final String imgUrl, final boolean isCache, final boolean isNeed, final AsyncNetBitmapOperator operator,
			final AsyncImageLoadedCallBack callBack) {
		if (imgUrl == null || imgUrl.equals("")) {
			return null;
		}
		//TODO:这里是对省流量模式进行控制，已经跟业务有关，后续考虑移走
		// 这里根据设置对图片加载进行了控制
		int loadtype = AppGameSettingData.getInstance(ApplicationProxy.getContext())
				.getTrafficSavingMode();
		// 不需要下载图标
		if (!isNeed && !mIsWifiEnable && loadtype == AppGameSettingData.NOT_LOADING_IMAGES) {
			return null;
		}
		Bitmap result = null;
		result = loadImgFromMemery(imgUrl);
		if (result == null) {

			Thread loadimg = new Thread() {
				@Override
				public void run() {
					//如果不允许加载，那么就是列表正在滚动，线程等待列表滚动停止时通知加载
					synchronized (mLoadLock) {
						while (!mIsAllowLoad) {
							try {
								mLoadLock.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					//在允许加载时，只有是第一次加载，或者是加载项在列表的可视项的范围内，才加载
					boolean isLoad = mIsFirstLoad || (position >= mLimitStart && position <= mLimitEnd);
					if (!isLoad) {
						return;
					}
					Bitmap b = null;
					try {
						b = loadImgFromSD(imgPath, imgName, imgUrl, isCache);
						if (b == null) {
							b = loadImgFromNetwork(imgUrl);
							if (operator != null) {
								b = operator.operateBitmap(
										ApplicationProxy.getContext(), b);
							}
							if (b != null) {
								if (FileUtil.isSDCardAvaiable()) {
									FileUtil.saveBitmapToSDFile(b, imgPath + imgName,
											Bitmap.CompressFormat.PNG);
								}
							}
						}
					} catch (OutOfMemoryError error) {
						//爆内存
						error.printStackTrace();
					}
					if (b != null) {
						if (isCache) {
							mImageCache.set(imgUrl, b);
						}
						// 主线程显示图片
						final Bitmap finalbitmap = b;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								callBack.imageLoaded(finalbitmap, imgUrl);
							}
						});
					}
				}
			};
			loadimg.setPriority(Thread.MIN_PRIORITY);
			loadimg.start();
			//TODO:因为线程里面有mLoadLock.wait()，这里的线程没有使用线程池，可能线程创建消耗比较大
		}
		return result;
	}
	
	/**
	 * 加载应用程序的图标
	 * 
	 * @param context
	 * @param packageName
	 *            应用包名
	 * @param isCache
	 *            是否缓存到内存
	 * @param callBack
	 * @param privateThreadPoll
	 * @return
	 */
	public Bitmap loadImageIcon(final Context context, final String packageName,
			final boolean isCache, final AsyncImageLoadedCallBack callBack) {
		Bitmap result = null;
		result = loadImgFromMemery(packageName);
		if (result == null) {
			if (mThreadPoolManager == null || mThreadPoolManager.isShutdown()) {
				Log.e("AsyncImageManager", "threadPoll == null || threadPoll.isShutdown()");
				return null;
			}
			Thread loadimg = new Thread() {
				@Override
				public void run() {
					try {
						Drawable drawable = loadIcon(context, packageName);
						Bitmap bitmap = null;
						if (drawable != null) {
							bitmap = RecommAppsUtils.drawableToBitmap(context, drawable);
						}
						if (bitmap != null) {
							//TODO：加遮罩，跟业务有关，后续考虑移走
							bitmap = AppGameDrawUtils.getInstance().createMaskBitmap(context,
									bitmap);
							if (bitmap != null) {
								if (isCache) {
									mImageCache.set(packageName, bitmap);
								}
								// 主线程显示图片
								CallBackRunnable cbRunnable = new CallBackRunnable(bitmap,
										callBack, packageName);
//								Message message = new Message();
//								message.obj = cbRunnable;
//								mDeferredHandler.sendMessage(message);
								mHandler.post(cbRunnable);
							}
						}
					} catch (OutOfMemoryError error) {
						//爆内存
						error.printStackTrace();
					}
				}
			};
			mThreadPoolManager.execute(loadimg);
		}
		return result;
	}

	/**
	 * 专门针对列表项加载应用程序的图标
	 * 
	 * @param context
	 * @param packageName
	 *            应用包名
	 * @param isCache
	 *            是否缓存到内存
	 * @param callBack
	 * @param privateThreadPoll
	 * @return
	 */
	public Bitmap loadImageIconForList(final int position, final Context context,
			final String packageName, final boolean isCache, final AsyncImageLoadedCallBack callBack) {
		Bitmap result = null;
		result = loadImgFromMemery(packageName);
		if (result == null) {
			Thread loadimg = new Thread() {
				@Override
				public void run() {
					//如果不允许加载，那么就是列表正在滚动，线程等待列表滚动停止时通知加载
					synchronized (mLoadLock) {
						while (!mIsAllowLoad) {
							try {
								mLoadLock.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					//在允许加载时，只有是第一次加载，或者是加载项在列表的可视项的范围内，才加载
					boolean isLoad = mIsFirstLoad || (position >= mLimitStart && position <= mLimitEnd);
					if (!isLoad) {
						return;
					}
					try {
						Drawable drawable = loadIcon(context, packageName);
						Bitmap bitmap = null;
						if (drawable != null) {
							bitmap = RecommAppsUtils.drawableToBitmap(context, drawable);
						}
						if (bitmap != null) {
							//TODO：加遮罩，跟业务有关，后续考虑移走
							bitmap = AppGameDrawUtils.getInstance().createMaskBitmap(context,
									bitmap);
							if (bitmap != null) {
								if (isCache) {
									mImageCache.set(packageName, bitmap);
								}
								// 主线程显示图片
								final Bitmap finalBitmap = bitmap;
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										callBack.imageLoaded(finalBitmap, packageName);
									}
								});
							}
						}
					} catch (OutOfMemoryError error) {
						//爆内存
						error.printStackTrace();
					}
				}
			};
			loadimg.setPriority(Thread.MIN_PRIORITY);
			loadimg.start();
			//TODO:因为线程里面有mLoadLock.wait()，这里的线程没有使用线程池，可能线程创建消耗比较大
		}
		return result;
	}
	
	/**
	 * 根据包名加载应用程序图标
	 * 
	 * @param context
	 * @param packname
	 * @return
	 * @throws NotFoundException
	 */
	private Drawable loadIcon(Context context, String packname) {
		Drawable drawable = null;
		try {
			PackageManager pkgmanager = context.getPackageManager();
			ApplicationInfo appInfo = pkgmanager.getApplicationInfo(packname,
					PackageManager.GET_META_DATA);
			if (appInfo != null) {
				drawable = pkgmanager.getApplicationIcon(appInfo);
			}
			if (drawable == null || drawable.getIntrinsicWidth() <= 0
					|| drawable.getIntrinsicHeight() <= 0) {
				return null;
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	/**
	 * 从网络加载图片保存到sdcard
	 * @param imgPath 图片保存目录(含文件名)
	 * @param imgUrl 图片下载地址
	 * @param isCache 是否保存到缓存
	 * @param callBack 回调方法
	 */
	public void loadNetworkImageToSdcard(final String imgPath, final String imgUrl, final boolean isCache, final AsyncImageLoadedCallBack callBack) {
		//创建线程读取sdcard或网络图片
		Thread loadimgThread = new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = null;
				try {
					//从sdcard中获取图片
					bitmap = loadImgFromSD(imgPath, imgUrl, isCache);
					if (bitmap == null) {
						//从网络获取
						bitmap = loadImgFromNetwork(imgUrl);
						if (bitmap != null) {
							//将图片保存到sdcard
							if (FileUtil.isSDCardAvaiable()) {
								FileUtil.saveBitmapToSDFile(bitmap, imgPath, Bitmap.CompressFormat.PNG);
							}
						}
					}
				} catch (OutOfMemoryError error) {
					//爆内存
					error.printStackTrace();
				}
				if (bitmap != null && isCache) {
					//将Bitmap保存到缓存中
					mImageCache.set(imgUrl, bitmap);
				}
				//回调
				if (callBack != null) {
					callBack.imageLoaded(bitmap, imgUrl);
				}
			}
		};
		//启动线程读取图片
		mThreadPoolManager.execute(loadimgThread);
	}
	/**
	 * 从sdcard读取图片
	 * @param imgPath 图片sdcard保存目录
	 * @param imgUrl 图片下载地址
	 * @param isCache 是否保存到缓存中
	 * @return
	 */
	public Bitmap loadImgFromSD(String imgPath, String imgUrl, boolean isCache) {
		Bitmap result = null;
		try {
			if (FileUtil.isSDCardAvaiable()) {
				File file = new File(imgPath);
				if (file.exists()) {
					result = BitmapFactory.decodeFile(imgPath);
					if (result != null && isCache) {
						mImageCache.set(imgUrl, result);
					}
				}
			}
		} catch (OutOfMemoryError ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	/**
	 * 功能简述: 根据URL生成HttpURLConnection的方法 功能详细描述: 注意:
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public HttpURLConnection createURLConnection(String url) throws Exception {
		// TODO:wangzhuobin 是否需要设置代理?
		HttpURLConnection urlCon = null;
		urlCon = (HttpURLConnection) new URL(url).openConnection();
		urlCon.setConnectTimeout(CONNECT_TIME_OUT);
		urlCon.setReadTimeout(READ_TIME_OUT);
		return urlCon;
	}

	/**
	 * 
	 * <br>类描述:获取图片后，同步到主线程，回调Runable
	 * <br>功能详细描述:
	 * 
	 */
	public static class CallBackRunnable implements Runnable {
		private Bitmap mBitmap = null;
		private AsyncImageLoadedCallBack mCallBack = null;
		private String mImgUrl = null;

		CallBackRunnable(Bitmap img, AsyncImageLoadedCallBack callBack, String imgUrl) {
			this.mBitmap = img;
			this.mCallBack = callBack;
			this.mImgUrl = imgUrl;
		}

		@Override
		public void run() {
			if (mCallBack != null) {
				mCallBack.imageLoaded(mBitmap, mImgUrl);
			}
		}
	}

	/**
	 * 
	 * br>类描述:获取图片成功后的回调接口
	 * <br>功能详细描述:
	 * 
	 * @author  wangzhuobin
	 * @date  [2012-10-18]
	 */
	public static interface AsyncImageLoadedCallBack {
		public void imageLoaded(Bitmap imageBitmap, String imgUrl);
	}

	@Deprecated
	public synchronized static void onDestory() {
		if (sInstance != null) {
			sInstance.clear();
			sInstance = null;
		}
	}
	
	/**
	 * 销毁使用DefaultImageCache的AsyncImageManager实例
	 */
	public synchronized static void onDestoryDefault() {
		if (sDefaultInstance != null) {
			sDefaultInstance.clear();
			sDefaultInstance = null;
		}
	}
	
	/**
	 * 销毁使用LruImageCache的AsyncImageManager实例
	 */
	public synchronized static void onDestoryLru() {
		if (sLruInstance != null) {
			sLruInstance.clear();
			sLruInstance = null;
		}
	}

	public void clear() {
		if (mImageCache != null) {
			mImageCache.clear();
		}
	}

	/**
	 * 取消所有未执行的加载图标任务
	 */
	public void removeAllTask() {
		mThreadPoolManager.removeAllTask();
	}
	
	/**
	 * 通知图片管理器网络状态发生改变
	 */
	public void netWorkStateChange() {
		mIsWifiEnable = GoStorePhoneStateUtil.isWifiEnable(ApplicationProxy.getContext());
	}
	
	/**
	 * Wi-Fi网络是否可用
	 * @return true:可用   false:不可用
	 */
	public boolean isWifiEnable() {
		return mIsWifiEnable;
	}
	/**
	 * <br>功能简述:重置列表控制信息的方法
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void restore() {
		synchronized (mLoadLock) {
			mIsAllowLoad = true;
			mIsFirstLoad = true;
		}
	}

	/**
	 * <br>功能简述:锁定列表控制信息的方法
	 * <br>功能详细描述:一般是在监听到列表正在滚动时进行调用
	 * <br>注意:
	 */
	public void lock() {
		synchronized (mLoadLock) {
			mIsAllowLoad = false;
			mIsFirstLoad = false;
		}
	}

	/**
	 * <br>功能简述:解锁列表控制信息的方法
	 * <br>功能详细描述:一般是在监听到列表完成滚动时进行调用，会通知等待加载图片的线程开始加载图片
	 * <br>注意:
	 */
	public void unlock() {
		synchronized (mLoadLock) {
			mIsAllowLoad = true;
			mLoadLock.notifyAll();
		}
	}
	/**
	 * <br>功能简述:设置加载位置控制的方法
	 * <br>功能详细描述:一般是列表可见的第一项和最后一项的位置，只有在可见区域内才加载
	 * <br>注意:
	 * @param start
	 * @param end
	 */
	public void setLimitPosition(int start, int end) {
		mLimitStart = start;
		mLimitEnd = end;
	}

	public void recycle(String key) {
		if (mImageCache != null) {
			mImageCache.recycle(key);
		}
	}
	
	/**
	 * 从网络加载图片后的图片处理器
	 * 
	 * @author xiedezhi
	 */
	public static interface AsyncNetBitmapOperator {
		public Bitmap operateBitmap(Context context, Bitmap imageBitmap);
	}
}
