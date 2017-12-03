package com.jiubang.ggheart.components.appmanager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.appgame.appcenter.contorler.ApplicationManager;
import com.jiubang.ggheart.appgame.appcenter.contorler.ApplicationManager.IDownloadInvoker;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.appgame.base.utils.ApkInstallUtils;
import com.jiubang.ggheart.appgame.download.DownloadTask;
import com.jiubang.ggheart.apps.gowidget.gostore.component.SimpleImageView;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean.AppBean;
import com.jiubang.ggheart.apps.gowidget.gostore.util.FileUtil;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;

/**
 * 
 * <br>
 * 类描述: 应用更新列表项 <br>
 * 功能详细描述:
 * 
 * @author zhoujun
 * @date [2012-10-18]
 */
public class SimpleAppsUpdateItem extends RelativeLayout implements
		View.OnClickListener, IDownloadInvoker {

	private AppBean mAppBean;
	
	private TextView mAppSizeTextView = null;
	private TextView mUpdateDatetimeTextView = null;
	private TextView mAppNameTextView = null;

	private RelativeLayout mOperationLayout;
	private ImageView mOperationButton = null;
	private TextView mOperationStatusTextView = null;

	private RelativeLayout mContentlayout = null;
	private SimpleAppsUpdateItem mSelf = null;
	private ImageSwitcher mImageSwitcher = null;
	private SimpleImageView mAppImgView = null;
	private SimpleImageView mAppAnotherImgView = null;
	
	private Bitmap mDefaultBitmap = null;
	// 统计的安装位置
	private int mPositon = - 1;
	
	private Context mContext;
		
	public SimpleAppsUpdateItem(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public SimpleAppsUpdateItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimpleAppsUpdateItem(Context context) {
		this(context, null);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
		mSelf = this;
	}

	/**
	 * 初始化方法
	 */
	private void init() {
		mAppNameTextView = (TextView) findViewById(R.id.app_name_view);
		mAppSizeTextView = (TextView) findViewById(R.id.app_size_view);
		mUpdateDatetimeTextView = (TextView) findViewById(R.id.update_datetime_view);
		mImageSwitcher = (ImageSwitcher) findViewById(R.id.app_update_image_switcher);
		mAppImgView = (SimpleImageView) findViewById(R.id.update_app_icon);
		mAppAnotherImgView = (SimpleImageView) findViewById(R.id.update_app_another_icon);
		
		mContentlayout = (RelativeLayout) findViewById(R.id.content_layout);
		mOperationLayout = (RelativeLayout) findViewById(R.id.update_operation_layout);
		mOperationButton = (ImageView) findViewById(R.id.update_operation);
		mOperationStatusTextView = (TextView) findViewById(R.id.update_operation_status);
		mOperationButton.setOnClickListener(this);
		mOperationLayout.setOnClickListener(this);
	}

	/**
	 * 重置默认状态的方法
	 */
	public void resetDefaultStatus() {
		this.setTag(null);
		mAppBean.setAppBeanStatusChangeListener(null);
		mAppBean.setAppBeanDownloadListener(null);
		mAppBean = null;
		if (mAppImgView != null) {
			mAppImgView.clearIcon();
		}
		if (mAppAnotherImgView != null) {
			mAppAnotherImgView.clearIcon();
		}
		if (mAppNameTextView != null) {
			mAppNameTextView.setText("");
		}
		if (mAppSizeTextView != null) {
			mAppSizeTextView.setText("");
		}
		if (mUpdateDatetimeTextView != null) {
			mUpdateDatetimeTextView.setText("");
		}

//		if (mProgressPercentTextView != null) {
//			mProgressPercentTextView.setText("");
//			mProgressPercentTextView.setVisibility(View.INVISIBLE);
//		}

		if (mOperationButton != null) {
			mOperationButton.setBackgroundResource(R.drawable.app_manager_update_selector);
		}

		if (mOperationStatusTextView != null) {
			mOperationStatusTextView.setText(R.string.update);
		}

//		if (mShowDetailAndUpdate != null) {
//			mShowDetailAndUpdate.setVisibility(View.GONE);
//		}

	}

	public void destory() {
		if (mAppImgView != null) {
			mAppImgView.recycle();
			mAppImgView.clearIcon();
			mAppImgView = null;
		}
		if (mAppAnotherImgView != null) {
			mAppAnotherImgView.recycle();
			mAppAnotherImgView.clearIcon();
			mAppAnotherImgView = null;
		}
		if (mImageSwitcher != null) {
			mImageSwitcher = null;
		}
		mAppNameTextView = null;
//		mProgressPercentTextView = null;
		if (mOperationButton != null) {
			mOperationButton.setOnClickListener(null);
			mOperationButton = null;
		}
		// if (mButton != null) {
		// mButton.setOnClickListener(null);
		// mButton = null;
		// }
		mCallBack = null;
		mAppBean = null;

	}

//	private void setAppImage(int position) {
//		if (mAppImgView != null) {
//			mAppImgView.setTag(mAppBean.mPkgName);
//			Bitmap bm = AsyncImageManager.getInstance().loadImageIconForList(
//					position, getContext(), mAppBean.mPkgName, true, mCallBack);
//			if (bm != null) {
//				mAppImgView.setImageBitmap(bm);;
//			} else {
//				mAppImgView.setImageDrawable(getContext().getResources()
//						.getDrawable(R.drawable.gomarket_default_icon));
//			}
//		}
//	}
	
	private void setAppImage(int position) {
		if (mImageSwitcher != null) {
			mImageSwitcher.setTag(mAppBean.mPkgName);
			mImageSwitcher.getCurrentView().clearAnimation();
			mImageSwitcher.getNextView().clearAnimation();
			Bitmap bm = AsyncImageManager.getInstance().loadImageIconForList(
			position, getContext(), mAppBean.mPkgName, true, mCallBack);
			ImageView imageView = (ImageView) mImageSwitcher.getCurrentView();
			if (bm != null) {
				imageView.setImageBitmap(bm);
			} else {
				imageView.setImageBitmap(mDefaultBitmap);
			}
		}
	}
	
	private AsyncImageLoadedCallBack mCallBack = new AsyncImageLoadedCallBack() {
		@Override
		public void imageLoaded(Bitmap bm, String url) {
			if (mImageSwitcher != null && mImageSwitcher.getTag().equals(url)) {
				Drawable drawable = ((ImageView) mImageSwitcher
						.getCurrentView()).getDrawable();
				if (drawable instanceof BitmapDrawable) {
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					if (bitmap == mDefaultBitmap) {
						mImageSwitcher.setImageDrawable(new BitmapDrawable(bm));
					}
				}
			} else {
				bm = null;
			}
		}
	};

//	private AsyncImageLoadedCallBack mCallBack = new AsyncImageLoadedCallBack() {
//		@Override
//		public void imageLoaded(Bitmap bm, String url) {
//			if (mAppImgView != null && mAppImgView.getTag().equals(url)) {
//				mAppImgView.setImageBitmap(bm);
//			} else {
//				bm = null;
//			}
//		}
//	};

	private void setAppName(String name) {
		if (mAppNameTextView != null) {
			mAppNameTextView.setText(name);
		}
	}
	
	private void setAppSize(String size, String deltaSize) {
		if (mAppSizeTextView == null) {
			return;
		}
		mAppSizeTextView.setText(size);
	}


	private void setUpdateDatetime(String datetime) {
		if (mUpdateDatetimeTextView != null) {
			// String label = getResources().getString(R.string.gomarket_update_time);
			mUpdateDatetimeTextView.setText(datetime);
		}
	}

	private void setAlreadyDownloadPercent(int percent) {
//		if (mProgressPercentTextView != null) {
//			mProgressPercentTextView.setText(percent + "%");
//		}
	}

	public void bindAppBean(final Context context, final int position,
			AppBean appBean, Bitmap defaultBitmap) {
		mDefaultBitmap = defaultBitmap;
		mAppBean = appBean;
		setTag(mAppBean);
		setStatus(mAppBean);
		PackageManager pm = context.getPackageManager();
		String appName = mAppBean.getAppName(pm);
		setAppImage(position);
		setAppName(appName);
		//增量更新设置
		setAppSize(mAppBean.mAppSize , mAppBean.mAppDeltaSize);
		//add by zzf
		mPositon = position;

		setUpdateDatetime(mAppBean.mUpdateTime);

		mContentlayout.setTag(position);
	}
	/**
	 * 根据下载状态设置样式
	 * @param status
	 */
	protected void setStatus(AppBean mAppBean) {
		if (mAppBean == null) {
			return ;
		}
		switch (mAppBean.getStatus()) {
		case AppBean.STATUS_NORMAL:
			mOperationButton.setBackgroundResource(R.drawable.app_manager_update_selector);
			mOperationStatusTextView.setText(R.string.update);
			break;
		case AppBean.STATUS_WAITING_DOWNLOAD:
			break;
		case AppBean.STATUS_DOWNLOADING:
			break;
		case AppBean.STATUS_DOWNLOAD_COMPLETED:
			mOperationStatusTextView.setText(R.string.message_file_not_install);
			break;
		case AppBean.STATUS_DOWNLOAD_FAILED:
			break;
		case AppBean.STATUS_STOP:
			break;
		case AppBean.STATUS_CANCELING:
			break;
		default:
			break;
		}
	}

	public AppBean getAppBean() {
		return mAppBean;
	}

	@Override
	public void onClick(View v) {
		if (!FileUtil.isSDCardAvaiable()) {
			Toast.makeText(getContext(),
					getContext().getString(R.string.import_export_sdcard_unmounted), 1000).show();
			return;
		}
		
		actionDownload();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.APP_MANAGER_04);
	}
	
	public void actionDownload() {
		if (mAppBean != null) {
//			AppsManagementActivity.sendMessage(this,
//					IDiyFrameIds.APPS_MANAGEMENT_UPDATE_APP_FRAME,
//					IDiyMsgIds.APPS_MANAGEMENT_OPERATION_BUTTON, 2, mAppBean,
//					null);
			switch (mAppBean.getStatus()) {
			case AppBean.STATUS_NORMAL:
			case AppBean.STATUS_DOWNLOAD_FAILED:
				//add by zzf 实时统计
//				RealTimeStatisticsUtil
//						.upLoadDownloadStatistic(
//								getContext(),
//								mAppBean.mAppId,
//								RealTimeStatisticsContants.ACTION_APK_APP_UPDATE_CLICK,
//								RealTimeStatisticsContants.UPDATE_CATEGORY_ID,
//								mPositon, mAppBean.mPkgName);
				
//				AppsManagementActivity.getApplicationManager().actionDownload(
//						getContext(), this);
//				//下载更新
//				AppsManagementActivity.getApplicationManager().actionDownload(
//						getContext(), this);
				ApplicationManager.getInstance(mContext).actionDownload(mContext, this);
				break;
//			 case AppBean.STATUS_WAITING_DOWNLOAD:
//			 cancelDownload();
//			 break;
//			 case AppBean.STATUS_DOWNLOADING:
//			 cancelDownload();
//			 break;
			case AppBean.STATUS_DOWNLOAD_COMPLETED:
				// 统计安装量
//				AppRecommendedStatisticsUtil.getInstance().saveReadyToInstall(
//						v.getContext(), mAppBean.mPkgName,
//						String.valueOf(mAppBean.mAppId), 0, null);
				
				ApkInstallUtils.installApk(mAppBean.getFilePath());
				break;
			}
		}
	}
	
	@Override
	public void invokeDownload() {
		// 积分操作，应用更新的积分奖励为5分
//		PersonalPointsManager.getInstance(GoMarketApp.getContext())
//				.operatePersonalPoints(mAppBean.mPkgName, null, 1, 5,
//						String.valueOf(mAppBean.mAppId),
//						PersonalPointsManager.SREWARD_POINTS_INSTALL);
		
		ApplicationManager.getInstance(mContext).startDownload(mAppBean,
				DownloadTask.ICON_TYPE_LOCAL, mAppBean.mPkgName,
				AppsDetail.START_TYPE_APPMANAGEMENT);
		setStatus(mAppBean);
	}

	public RelativeLayout getmContentLayout() {
		return mContentlayout;
	}
	
	public void setPosition(int position) {
		this.mPositon = position ;
	}
}