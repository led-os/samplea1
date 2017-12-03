package com.jiubang.ggheart.appgame.gostore.base.component;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.appgame.base.bean.BoutiqueApp;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.appgame.base.net.InstallCallbackManager;
import com.jiubang.ggheart.appgame.base.utils.AppGameDrawUtils;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreDisplayUtil;
import com.jiubang.ggheart.data.statistics.AppManagementStatisticsUtil;
import com.jiubang.ggheart.launcher.LauncherEnv;
/**
 * 应用详情推荐应用列表
 * @author panghuajuan
 *
 */
public class ThemeDetailBottomView extends LinearLayout {
	private int mWidth = 72;
	private int mHeight = 0;
	private AsyncImageManager mImgManager;
	private Drawable mDefaultIcon;
	private Bitmap mDefaultBmp;
	private int mScrollWidth = 0;
	private Context mContext = null;
	private List<BoutiqueApp> listApp = new ArrayList<BoutiqueApp>();
	private int mChildMargin = 4;

	private ArrayList<LinearLayout> mIconLayout = null;
	/**
	 * 相关推荐分类ID
	 */
	private int mRecmdId;

	public ThemeDetailBottomView(Context context) {
		super(context);
		mContext = context;
		initView();
		// TODO Auto-generated constructor stub
	}

	public ThemeDetailBottomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();

	}

	private void initView() {
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.CENTER);
		mImgManager = AsyncImageManager.getInstance();
		mDefaultIcon = getResources().getDrawable(
				R.drawable.appcenter_theme_detail_default_icon);
		BitmapDrawable bitmapDrawable = (BitmapDrawable) mDefaultIcon;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		mDefaultBmp = AppGameDrawUtils.getInstance().createMaskBitmap(
				getContext(), bitmap);
	}

	public void setChildWidth(int width) {
		mWidth = width;
		mScrollWidth = mWidth
				+ GoStoreDisplayUtil.scalePxToMachine(getContext(), 2)
				+ GoStoreDisplayUtil.scalePxToMachine(getContext(), 7) * 2;
	}

	public void setChildHeight(int height) {
		mHeight = height;
	}

	public void setListBean(List<BoutiqueApp> list,int recmdId) {
		mRecmdId = recmdId;
		int count = 6;
		if (DrawUtils.sDensity >= 2) {
			count = 7;
		}
		if(list.size() < count) {
			count = list.size();
		}
		mChildMargin = (DrawUtils.sWidthPixels - mWidth * count) / (count * 2);
		if (list.size() >= count) {
			for (int i = 0; i < count; i++) {
				listApp.add(list.get(i));
			}
		} else {
			listApp.addAll(list);
		}
		ArrayList<String> icons = new ArrayList<String>();
		for (int i = 0; i < listApp.size(); i++) {
			icons.add(listApp.get(i).info.icon);
		}
		loadIcon(icons);

	}

	private void loadIcon(final ArrayList<String> urls) {
		if (urls == null) {
			return;
		}
		int size = urls.size();
		mIconLayout = new ArrayList<LinearLayout>(size);
		for (final String url : urls) {
			final ImageView imgView = new ImageView(getContext());
			LinearLayout layout = new LinearLayout(getContext());
			int width = mWidth
					+ GoStoreDisplayUtil.scalePxToMachine(getContext(), 2);
			int height = mHeight
					+ GoStoreDisplayUtil.scalePxToMachine(getContext(), 2);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					width, height);
			int imgWidth = mWidth;
			int imgHeight = mHeight;
			LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
					imgWidth, imgHeight);
//			int margin = GoStoreDisplayUtil.scalePxToMachine(getContext(),
//					mChildMargin);
			int margin = mChildMargin;
			params.setMargins(margin, 0, margin, 0);
			layout.setLayoutParams(params);
			layout.setGravity(Gravity.CENTER);
			String imgName = String.valueOf(url.hashCode());
			imgView.setScaleType(ScaleType.FIT_CENTER);
			Bitmap bmp = mImgManager.loadImage(
					LauncherEnv.Path.GOSTORE_ICON_PATH, imgName, url, true,
					false, AppGameDrawUtils.getInstance().mMaskIconOperator,
					new AsyncImageLoadedCallBack() {
						@Override
						public void imageLoaded(Bitmap imageBitmap,
								String imgUrl) {
							imgView.setImageBitmap(imageBitmap);
						}
					});
			if (bmp != null) {
				imgView.setImageBitmap(bmp);
			} else {
				imgView.setImageBitmap(mDefaultBmp);
			}
			layout.setBackgroundResource(R.drawable.gostore_theme_detail_icons_selector);
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = urls.indexOf(url);
					if (position >= 0 && position < listApp.size()) {
						BoutiqueApp app = listApp.get(position);
						if (app != null) {

							// 判断treatment的值
							if (app.info.treatment > 0) {
								InstallCallbackManager.saveTreatment(
										app.info.packname, app.info.treatment);
							}
							// 判断是否需要安装成功之后回调
							if (app.info.icbackurl != null
									&& !app.info.icbackurl.equals("")) {
								InstallCallbackManager.saveCallbackUrl(
										app.info.packname, app.info.icbackurl);
							}
							AppsDetail.jumpToDetailNew(mContext, app,
									String.valueOf(mRecmdId),
									AppsDetail.START_TYPE_APPRECOMMENDED,
									position, true, 1);
							AppManagementStatisticsUtil.saveTabClickData(mContext, mRecmdId, null);
						}
					}

				}
			});
			mIconLayout.add(layout);
			layout.addView(imgView, imgParams);
			this.addView(layout);
		}
	}
	
	public void recycle() {
		if (mIconLayout != null) {
			for (LinearLayout layout : mIconLayout) {
				View view = layout.getChildAt(0);
				if (view != null && view instanceof ImageView) {
					((ImageView) view).setImageBitmap(null);
				}
				layout.setOnClickListener(null);
				layout.removeAllViews();
			}
			mIconLayout.clear();
			mIconLayout = null;
		}
		if (mImgManager != null) {
			mImgManager.removeAllTask();
			mImgManager = null;
		}
	}

}
