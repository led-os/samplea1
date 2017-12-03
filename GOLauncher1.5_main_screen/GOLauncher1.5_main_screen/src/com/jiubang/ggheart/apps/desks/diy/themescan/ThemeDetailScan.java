package com.jiubang.ggheart.apps.desks.diy.themescan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;

/**
 * @author chenguanyu
 */
public class ThemeDetailScan extends DetailScan {
	private int mMarkInfoScreen = 0;
	private boolean mIsThemeModifyed;
	private ThemeDetailView mParent = null;
	private boolean mIsInfoTextView;
	private Context mContext;
	private boolean mFullScreenEnable;
	public static final int ITEMS_H = 3;
	public static final float DESNT15 = 1.5f;
	public static final float DESNT075 = 0.75f;
	public ThemeDetailScan(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mParent = (ThemeDetailView) this.getParent();
	}

	public void setmIsThemeModifyed(boolean mIsThemeModifyed) {
		this.mIsThemeModifyed = mIsThemeModifyed;
	}

	public void setmIsInfoTextView(boolean mIsInfoTextView) {
		this.mIsInfoTextView = mIsInfoTextView;
	}

	@Override
	protected void initData() {
		if (null != mInfoBean && null != mScroller) {
			// 旧主题包只有一张预览图，UI2.0新主题包有多张预览图，第一张用在“我的主题”界面显示，在主题详情不显示
			int prePicsCount = ((ThemeInfoBean) mInfoBean).getPreViewDrawableNames().size();
			int i = 0;
			String vimgUrl = ((ThemeInfoBean) mInfoBean).getVimgUrl();
			boolean hasVideo = false;
			if (((ThemeInfoBean) mInfoBean).getFeaturedId() != 0) { //网络主题
				if (vimgUrl != null && !vimgUrl.equals("")) {
					hasVideo = true;
					mTotalScreenNum = prePicsCount + 1;
				} else {
					mTotalScreenNum = prePicsCount;
				}
			} else { //本地主题
				mTotalScreenNum = prePicsCount > 1 ? prePicsCount - 1 : prePicsCount;
				if (mTotalScreenNum == 1 && prePicsCount == 1) {
					i = 0;
				} else {
					i = mTotalScreenNum >= 1 ? 1 : 0;
				}
			}
			if (mFullScreenEnable) {
				mTotalScreenNum++;
			}
			//			mCurrentScreen = 0;
			if (SpaceCalculator.sPortrait) {
				mScroller.setScreenCount(mTotalScreenNum);
				mScroller.setCurrentScreen(mCurrentScreen);
			} else {
				// 横屏的时候一屏显示3个
				int pages = 0;
				pages = mTotalScreenNum / ITEMS_H;
				if (mTotalScreenNum % ITEMS_H > 0) {
					pages++;
				}
				mTotalScreenNum = pages;
				mScroller.setScreenCount(mTotalScreenNum);
				mScroller.setCurrentScreen(mCurrentScreen);
			}

			// init themsviews
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			//			String pkgName = ((ThemeInfoBean) mInfoBean).getPackageName();
			String previewName = null;
			//			int i = (prePicsCount > 1 && !hasVideo) ? 1 : 0;
			//			if (hasVideo && prePicsCount < 2) {
			//				prePicsCount = prePicsCount + 1;
			//			}
			int videoCnt = hasVideo ? 1 : 0;
			for (; i < prePicsCount + videoCnt; i++) {
				final SingleThemeView singleView = (SingleThemeView) inflater.inflate(
						R.layout.theme_detail_singletheme, null);
				setImageDefaultSize(singleView);
				if (hasVideo && i == 0) {
					previewName = ((ThemeInfoBean) mInfoBean).getVimgUrl();
					if (previewName != null && previewName.lastIndexOf("/") > 0) {
						previewName = previewName.substring(previewName.lastIndexOf("/") + 1);
					}
					singleView.setHasVideo(true);
				} else {
					int index = i;
					if (hasVideo) {
						index = i - 1;;
					}
					previewName = ((ThemeInfoBean) mInfoBean).getPreViewDrawableNames().get(index);
					singleView.setHasVideo(false);

				}

				if (prePicsCount > 1) {
					// 有多张图片
					if (mIsThemeModifyed) {
						if (SpaceCalculator.sPortrait) {
							// 竖屏的是只在第一张的右上角加已修改
							if (i == 1) {
								singleView.setmThemeDetailData((ThemeInfoBean) mInfoBean,
										previewName, true, isFeaturedThemeBean());
							} else {
								singleView.setmThemeDetailData((ThemeInfoBean) mInfoBean,
										previewName, false, isFeaturedThemeBean());
							}
						} else {
							// 横屏的时候第一屏的图片都要加
							if (i <= ITEMS_H) {
								singleView.setmThemeDetailData((ThemeInfoBean) mInfoBean,
										previewName, true, isFeaturedThemeBean());
							} else {
								singleView.setmThemeDetailData((ThemeInfoBean) mInfoBean,
										previewName, false, isFeaturedThemeBean());
							}

						}
					} else {
						singleView.setmThemeDetailData((ThemeInfoBean) mInfoBean, previewName,
								false, isFeaturedThemeBean());
					}
				} else {
					if (mIsThemeModifyed) {
						singleView.setmThemeDetailData((ThemeInfoBean) mInfoBean, previewName,
								true, isFeaturedThemeBean());
					} else {
						singleView.setmThemeDetailData((ThemeInfoBean) mInfoBean, previewName,
								false, isFeaturedThemeBean());
					}
				}
				final int index = i;
				final boolean has = hasVideo;
				ImageView image = singleView.getImageView();
				ImageView videoImage = singleView.getVideoFlagView();
				if (has && index == 0) {
					if (videoImage != null) {
						videoImage.setVisibility(VISIBLE);
						videoImage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								String vurl = ((ThemeInfoBean) mInfoBean).getVurl();
								if (vurl != null && !vurl.equals("")) {
									GoAppUtils.gotoBrowserInRunTask(mContext, vurl);
//									gotoBrowser(vurl);
								}

							}
						});
					}
				} else {
					if (image != null) {
						image.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								//								if (has && index == 0) {
								//									String vurl = ((ThemeInfoBean) mInfoBean).getVurl();
								//									if (vurl != null && !vurl.equals("")) {
								//										gotoBrowser(vurl);
								//									}
								//								} else {
								//已经加载完毕
								Intent intent = new Intent();
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra(ThemeConstants.FULLSCREEN_THEME_INFO,
										(ThemeInfoBean) mInfoBean);
								intent.putExtra(ThemeConstants.FULLSCREEN_CURRENT_INDEX, index);
								intent.setClass(getContext(),
										ThemePreviewFullScreenViewActivity.class);
								if (getContext() instanceof Activity) {
									int themeTrpe = ((Activity) getContext()).getIntent()
											.getIntExtra(ThemeConstants.TAB_TYPE_EXTRA_KEY,
													ThemeConstants.LAUNCHER_INSTALLED_THEME_ID);
									intent.putExtra(ThemeConstants.TAB_TYPE_EXTRA_KEY, themeTrpe);
								}
								mContext.startActivity(intent);
								//								}
							}
						});
					}
				}
				addView(singleView);
			}
			if (mFullScreenEnable) {
				final SingleThemeView singleView = (SingleThemeView) inflater.inflate(
						R.layout.theme_detail_singletheme, null);
				setImageDefaultSize(singleView);
				singleView.setHasVideo(false);
				singleView.addFullScreenMockDetail();
				addView(singleView);
			}
		}
	}

	/**
	 * 精选主题 当主题信息未加载完成时只显示背景
	 */
	public void setOnlyImageBackgroud() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		SingleThemeView singleView = (SingleThemeView) inflater.inflate(
				R.layout.theme_detail_singletheme, null);
		setImageDefaultSize(singleView);
		singleView.setOnlyImageBackgroud();
		addView(singleView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		if (metrics.widthPixels <= metrics.heightPixels) {
			SpaceCalculator.setIsPortrait(true);
			SpaceCalculator.getInstance(getContext()).calculateItemViewInfo();
		} else {
			SpaceCalculator.setIsPortrait(false);
			SpaceCalculator.getInstance(getContext()).calculateThemeListItemCount();
		}
		if (SpaceCalculator.sPortrait || mIsInfoTextView) {
			onLayoutPortrait(changed, l, t, r, b);
		} else {
			onLayoutLandscape(changed, l, t, r, b);
		}
	}

	protected void onLayoutPortrait(boolean changed, int l, int t, int r, int b) {
		// 主题信息文本两边空白的宽度总和
		int textWidth = (int) getResources().getDimension(R.dimen.theme_info_text_width);
		int onethemewidth = mLayoutWidth;
		int count = getChildCount();
		int page = 0;

		for (int i = 0; i < count; i++) {
			page = i;
			View childView = getChildAt(i);
			View view = childView.findViewById(R.id.themeview).findViewById(R.id.image);
			if (view == null) {
				ScrollView.LayoutParams scrollViewparams = new ScrollView.LayoutParams(
						onethemewidth - textWidth, LayoutParams.WRAP_CONTENT);
				view = childView.findViewById(R.id.themeview).findViewById(R.id.relative);

				view.setLayoutParams(scrollViewparams);
			}
			int left = page * mLayoutWidth;
			int top = 0;
			int right = left + mLayoutWidth;
			int bottom = top + mLayoutHeight;
			childView.measure(mLayoutWidth, mLayoutHeight);
			childView.layout(left, top, right, bottom);
		}
	}

	protected void onLayoutLandscape(boolean changed, int l, int t, int r, int b) {
		// 主题信息文本两边空白的宽度总和
		int textWidth = (int) getResources().getDimension(R.dimen.theme_info_text_width);
		int onethemewidth = mLayoutWidth / ITEMS_H;
		int onethemeheight = mLayoutHeight;
		int count = getChildCount();
		int page = 0;

		for (int i = 0; i < count; i++) {
			page = i;
			View childView = getChildAt(i);
			View view = childView.findViewById(R.id.themeview).findViewById(R.id.image);
			if (view == null) {
				ScrollView.LayoutParams scrollviewParams = new ScrollView.LayoutParams(
						onethemewidth - textWidth, onethemeheight);
				view = childView.findViewById(R.id.themeview).findViewById(R.id.relative);
				view.setLayoutParams(scrollviewParams);
			}
			int left = page * onethemewidth;
			int top = 0;
			int right = left + onethemewidth;
			int bottom = top + onethemeheight;
			childView.measure(onethemewidth, onethemeheight);
			childView.layout(left, top, right, bottom);

		}
	}

	/**
	 * added by liulix
	 * 判断如果屏幕高度大于一屏，则根据图片的宽计算长度
	 */
	private int getHeightBasedWidth(int width) {
		int screenheight = getContext().getResources().getDisplayMetrics().heightPixels;
		int top = DrawUtils.dip2px(48); //主题详情页title高度
		int bottom = SpaceCalculator.sPortrait ? DrawUtils.dip2px(53) : 0; //主题详情页应用按钮高度
		int indicator = SpaceCalculator.sPortrait ? DrawUtils.dip2px(52) : DrawUtils.dip2px(13);
		int maxHeight = screenheight - top - bottom - indicator;
		int layoutHeight = 822 * width / 498;
		return maxHeight < layoutHeight ? maxHeight : layoutHeight;
	}

	private int getSingleViewWidth() {
		int layoutWidth = 0;
		if (SpaceCalculator.sPortrait) {
			// 竖屏时图片的宽和高
			layoutWidth = (int) mContext.getResources().getDimension(
					R.dimen.singletheme_detail_pic_width);
			// 对不同density的机型机型处理
			if (DrawUtils.sDensity == DESNT075) {
				layoutWidth = (int) mContext.getResources().getDimension(
						R.dimen.singletheme_detail_pic_width_ldpi);
			} else if (DrawUtils.sDensity == 1.0) {
				layoutWidth = (int) mContext.getResources().getDimension(
						R.dimen.singletheme_detail_pic_width_mdpi);
			}
			layoutWidth -= DrawUtils.dip2px(15);
		} else {
			// 横屏时图片的宽和高
			layoutWidth = (int) mContext.getResources().getDimension(
					R.dimen.singletheme_detail_pic_land_width_hdpi);
			// 对不同density的机型机型处理
			if (DrawUtils.sDensity == DESNT075) {
				layoutWidth = (int) mContext.getResources().getDimension(
						R.dimen.singletheme_detail_pic_land_width_ldpi);
			} else if (DrawUtils.sDensity == 1.0) {
				layoutWidth = (int) mContext.getResources().getDimension(
						R.dimen.singletheme_detail_pic_land_width_mdpi);
			}
		}
		return layoutWidth;
	}

	private void setImageDefaultSize(SingleThemeView singleView) {
		int width = getSingleViewWidth();
		singleView.setDefaultWidth(width);
		int height = getHeightBasedWidth(width);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
		singleView.getImageView().setLayoutParams(params);
	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
		mMarkInfoScreen = oldScreen;
		super.onScreenChanged(newScreen, oldScreen);
	}

	@Override
	public void cleanup() {

	}

	@Override
	public void setInfoBean(Object bean) throws IllegalArgumentException {
		if (null == bean || !(bean instanceof ThemeInfoBean)) {
			throw new IllegalArgumentException();
		} else {
			super.setInfoBean(bean);
		}
	}

	public int markInfoScreen() {
		return mMarkInfoScreen;
	}

	public void setScreenNum() {
		mScroller.setScreenCount(1);
		mScroller.setCurrentScreen(0);

	}

	public void setParent(ThemeDetailView parent) {
		mParent = parent;
	}

	public boolean isFeaturedThemeBean() {
		return ((ThemeInfoBean) mInfoBean).getFeaturedId() != 0;
	}

	public void setFullScreenAdEnable(boolean b) {
		mFullScreenEnable = b;
	}
}
