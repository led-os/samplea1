package com.jiubang.ggheart.apps.desks.imagepreview;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.launcher.MyThread;

/**
 * 
 * <br>类描述:换图标适配器
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-7-29]
 */
public class ImagePreviewAdapter extends BaseAdapter {
	protected Context mContext;
	private Object mMutex;
	protected ImageGridParam mItemParam;
	private ImageExplorer mImageExplorer;
	
	protected List<IImageNode> mAllImageNodeList; //所有的数据队列（没过略空白图片）
	public ArrayList<IImageNode> mHasDataImageNodeList = new ArrayList<IImageNode>(); //遍历后存在图片的数据队列
	public ArrayList<SoftReference<Drawable>> mDrawableList = new ArrayList<SoftReference<Drawable>>(); //遍历后存在图片的图片队列


	private boolean mIsDataInit = false;
	
	public boolean getIsDataInit() {
		return mIsDataInit;
	}
	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public interface IImageNode {
		public void setDrawable(Drawable drawable);

		public Drawable getDrawable();

		public Drawable loadDrawable();

		public void freeDrawable();
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public class NoNeedFreeImageNode implements IImageNode {
		protected Drawable mDrawable;

		public NoNeedFreeImageNode() {

		}

		@Override
		public void setDrawable(Drawable drawable) {
			mDrawable = drawable;
		}

		@Override
		public Drawable getDrawable() {
			return mDrawable;
		}

		@Override
		public Drawable loadDrawable() {
			return null;
		}

		@Override
		public void freeDrawable() {
			mDrawable = null;
		}
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public class NeedFreeImageNode extends NoNeedFreeImageNode {
		public NeedFreeImageNode() {

		}

		@Override
		public void freeDrawable() {
			if (null != mDrawable) {
				BitmapDrawable bmpDrawable = (BitmapDrawable) mDrawable;
				bmpDrawable.getBitmap().recycle();
				mDrawable = null;
			}
		}
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public class FileImageNode extends NeedFreeImageNode {
		private String mFilePath;

		public FileImageNode(String path) {
			mFilePath = path;
		}

		public String getFilePath() {
			return mFilePath;
		}

		@Override
		public Drawable loadDrawable() {
			try {
				return Drawable.createFromPath(mFilePath);

			} catch (OutOfMemoryError e) {
				OutOfMemoryHandler.handle();
			} catch (Exception e) {
			}
			return null;
		}
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public class PackageImageNode extends NoNeedFreeImageNode {
		private String mPackageName;
		private String mPackageResName;

		public PackageImageNode(String packageName, String packageResName) {
			mPackageName = packageName;
			mPackageResName = packageResName;
		}

		public String getPackageName() {
			return mPackageName;
		}

		public String getPackageResName() {
			return mPackageResName;
		}

		@Override
		public Drawable loadDrawable() {
			return mImageExplorer.getDrawable(mPackageName, mPackageResName);
		}
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public class ResourceImageNode extends NoNeedFreeImageNode {
		private String mResourceName;

		public ResourceImageNode(String resourceName) {
			mResourceName = resourceName;
		}

		public String getResourceName() {
			return mResourceName;
		}

		@Override
		public Drawable loadDrawable() {
			try {
				ImageExplorer imageExplorer = ImageExplorer.getInstance(mContext);
				return imageExplorer.getDrawable(IGoLauncherClassName.DEFAULT_THEME_PACKAGE,
						mResourceName);

			} catch (OutOfMemoryError e) {
				OutOfMemoryHandler.handle();
			} catch (Throwable e) {

			}
			return null;
		}
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public class DrawableImageNode extends NoNeedFreeImageNode {
		public DrawableImageNode(Drawable drawable) {
			mDrawable = drawable;
		}
	}

	public ImagePreviewAdapter(Context context, ImageGridParam param) {
		mContext = ApplicationProxy.getContext();
		mItemParam = param;
		mMutex = new Object();
		mAllImageNodeList = new ArrayList<IImageNode>();
		mImageExplorer = ImageExplorer.getInstance(context);
	}

	public void initDrawable(Drawable drawable) {
		if (null != drawable) {
			initImageNode(mAllImageNodeList, drawable);
		}
	}

	public void initResourceStringArray(String[] resourceStringArray) {
		if (null != resourceStringArray) {
			initImageNode(mAllImageNodeList, resourceStringArray);
		}
	}

	public void initPackageResourceArray(String packageName, ArrayList<String> packageResourceArray) {
		if (null != packageName && null != packageResourceArray) {
			initImageNode(mAllImageNodeList, packageName, packageResourceArray);
		}
	}

	public void initFolder(String scanFolder) {
		if (null != scanFolder) {
			File file = new File(scanFolder);
			if (file.exists()) {
				initImageNode(mAllImageNodeList, file);
			}
		}
	}

	public void initPackageResourceArrayInAllPacksges(ArrayList<String> packageNames,
			ArrayList<String> resName) {
		if (null != packageNames && null != resName) {
			initImageNode(mAllImageNodeList, packageNames, resName);
		}
	}

	private MyThread mLoadThread;

	public void start(final Handler handler) {
		mLoadThread = new MyThread() {
			@Override
			protected void doBackground() {
				synchronized (mMutex) {
					if (null != mAllImageNodeList) {
						int size = mAllImageNodeList.size();
						for (int i = 0; i < size; i++) {
							if (!getRunFlag()) {
								break;
							}
							if (mAllImageNodeList != null && i < mAllImageNodeList.size()) {
								IImageNode node = mAllImageNodeList.get(i);
								if (null == node) {
									continue;
								}

								Drawable drawable = node.loadDrawable();
								if (drawable != null) {
									mHasDataImageNodeList.add(node);
									SoftReference<Drawable> reference = new SoftReference<Drawable>(drawable);
									mDrawableList.add(reference);
								}
							}
						}
						updateUI(null);
					}
					mIsDataInit = true;
				}
			}

			@Override
			protected void doUpdateUI(Object object) {
				notifyDataSetInvalidated();
				handler.sendEmptyMessage(ChangeIconPreviewActivity.DISSMISS_LOAD_DIALOG);
			}
		};
		mLoadThread.start();
		mIsDataInit = false;
	}
	
	public void cancel() {
		free();
		if (null != mLoadThread) {
			mLoadThread.setRunFlag(false);
			mLoadThread = null;
		}
	}

	public void free() {
		if (mAllImageNodeList != null) {
			mAllImageNodeList.clear();
		}
		if (mDrawableList != null) {
			mDrawableList.clear();
		}
		
//		if (mDrawableList != null) {
//			int sz = mDrawableList.size();
//			for (int i = 0; i < sz; i++) {
//				Drawable drawable = mDrawableList.get(i).get();
//				if (null != drawable) {
//					BitmapDrawable bmpDrawable = (BitmapDrawable) drawable;
//					Bitmap bitMap = bmpDrawable.getBitmap();
//					if (bitMap != null && !bitMap.isRecycled()) {
//						bmpDrawable.getBitmap().recycle();
//						drawable = null;
//					}
//					
//				}
//			}
//			System.gc();
//			mDrawableList.clear();
//		}
		
	}

	private void initImageNode(List<IImageNode> imageNodeList, Drawable drawable) {
		DrawableImageNode node = new DrawableImageNode(drawable);
		if (null == imageNodeList) {
			imageNodeList = new ArrayList<IImageNode>();
		}
		imageNodeList.add(node);
	}

	private void initImageNode(List<IImageNode> imageNodeList, String[] resourceIdArray) {
		int len = resourceIdArray.length;
		for (int i = 0; i < len; i++) {
			ResourceImageNode node = new ResourceImageNode(resourceIdArray[i]);
			if (null == imageNodeList) {
				imageNodeList = new ArrayList<IImageNode>();
			}
			imageNodeList.add(node);
		}
	}

	private void initImageNode(List<IImageNode> imageNodeList, String packageName,
			ArrayList<String> packageResourceArray) {
		int sz = packageResourceArray.size();
		for (int i = 0; i < sz; i++) {
			PackageImageNode node = new PackageImageNode(packageName, packageResourceArray.get(i));
			if (null == imageNodeList) {
				imageNodeList = new ArrayList<IImageNode>();
			}
			imageNodeList.add(node);
		}
	}

	/**
	 * 
	 * @param imageNodeList
	 * @param packageNames
	 *            所有的包名
	 * @param resName
	 *            要获取的图片的名字
	 */
	private void initImageNode(List<IImageNode> imageNodeList, ArrayList<String> packageNames,
			ArrayList<String> resNameList) {
		int count = packageNames.size();
		for (int i = 0; i < count; i++) {
			PackageImageNode node = new PackageImageNode(packageNames.get(i), resNameList.get(i));
			if (null == imageNodeList) {
				imageNodeList = new ArrayList<IImageNode>();
			}
			imageNodeList.add(node);
		}
	}

	private void initImageNode(List<IImageNode> imageNodeList, File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				initImageNode(imageNodeList, files[i]);
			}
		} else {
			if (isImageFile(file)) {
				FileImageNode node = new FileImageNode(file.getPath());
				if (null == imageNodeList) {
					imageNodeList = new ArrayList<IImageNode>();
				}
				imageNodeList.add(node);
			}
		}
	}

	private boolean isImageFile(File file) {
		return true;
	}

	@Override
	public int getCount() {
		if (mHasDataImageNodeList == null) {
			return 0;
		}

		return mHasDataImageNodeList.size();
	}

	@Override
	public Object getItem(int position) {
		return mHasDataImageNodeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = null;
		if (null == convertView) {
			imageView = new ImageView(mContext);
			if (null != mItemParam) {
				imageView.setLayoutParams(new GridView.LayoutParams(mItemParam.mWidth, mItemParam.mHeight));
				imageView.setPadding(mItemParam.mLeftPadding, mItemParam.mTopPadding,
						mItemParam.mRightPadding, mItemParam.mBottomPadding);
			}
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}
		
		if (mDrawableList != null &&  position < mDrawableList.size()) {
			Drawable drawable = mDrawableList.get(position).get();
			
			//如果图片为null，异步加载图片
			if (drawable == null) {
				if (mHasDataImageNodeList != null &&  position < mHasDataImageNodeList.size()) {
					GetDrawableAsyncTask task = new GetDrawableAsyncTask(imageView, mHasDataImageNodeList.get(position), position);
					task.execute();
				}
			}
			
			imageView.setImageDrawable(drawable);
		}
		
		return imageView;
	}
	
	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  licanhui
	 * @date  [2013-7-29]
	 */
	public class GetDrawableAsyncTask extends ImageLoadTask<Integer, Integer, Drawable> {  
	    private ImageView mImageView;
	    private  IImageNode mImageNode;  
	    private  int mPosition;  
	      
	    public GetDrawableAsyncTask(ImageView imageView, IImageNode imageNode, int position) {  
	        mImageView = imageView;  
	        mImageNode = imageNode;
	        mPosition = position;
			mImageView.setTag(position);
	    }  
	  
	    @Override  
	    protected Drawable doInBackground(Integer... params) {  
	        return mImageNode.loadDrawable();
	    }  
	  
	    @Override  
	    protected void onPostExecute(Drawable drawable) {  
	    	if (mImageView.getTag().equals(mPosition)) {
	    		mImageView.setImageDrawable(drawable);
	    		SoftReference<Drawable> reference = new SoftReference<Drawable>(drawable);
	    		if (mDrawableList != null &&  mPosition < mDrawableList.size()) {
	    			mDrawableList.set(mPosition, reference);
				}
			} 
	    }  
	  
	    @Override  
	    protected void onPreExecute() {  
	    	
	    }  
	  
	    @Override  
	    protected void onProgressUpdate(Integer... values) {  
	    	
	    }  
	}  
	
	
	
//	Handler mHandler = new Handler();
//	
//	static ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
//	
//	public void loadChangetIconIcon(final ImageView imageView, final IImageNode imageNode, final int position, final AsyncImageLoadedCallBack callBack) {
//		imageView.setTag(position);	
//		
//		Thread loadimg = new Thread() {
//				@Override
//				public void run() {
//					try {
//						final Drawable drawable = imageNode.loadDrawable();
//							Runnable cbRun = new Runnable() {
//								@Override
//								public void run() {
//								    	if (imageView.getTag().equals(position)) {
//										imageView.setImageDrawable(drawable);
//							    		SoftReference<Drawable> reference = new SoftReference<Drawable>(drawable);
//										mDrawableList.set(position, reference);
//								    	}
//							    }
//							};
//							
//							mHandler.post(cbRun);
//					} catch (OutOfMemoryError error) {
//						error.printStackTrace();
//					}
//				}
//			};
//			mThreadPool.execute(loadimg);
//	}
	
	
	
}
