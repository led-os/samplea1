package com.jiubang.ggheart.apps.desks.diy.filter.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;
import com.jiubang.ggheart.apps.desks.diy.filter.FilterActivity;
import com.jiubang.ggheart.apps.desks.diy.filter.FilterManager;
import com.jiubang.ggheart.apps.desks.diy.filter.FilterPhotoLoader;
import com.jiubang.ggheart.apps.desks.diy.filter.core.FilterParameter;
import com.jiubang.ggheart.apps.desks.diy.filter.core.FilterService;
import com.jiubang.ggheart.apps.desks.diy.filter.view.LinearLayoutScroller.ScrollerCallback;
import com.jiubang.ggheart.apps.gowidget.gostore.views.ScrollerViewGroup;
import com.jiubang.ggheart.components.DeskTextView;

/**
 * 滤镜选择界面
 * @author zouguiquan
 *
 */
public class FilterChooser extends RelativeLayout
		implements
			ScreenScrollerListener,
			OnClickListener,
			ScrollerCallback, OnTouchListener {

	private FilterWallpaperView mWallpaperView;
	private LinearLayoutScroller mHorizontalScroller;
	private ScrollerViewGroup mScrollerView;
	private LayoutInflater mInflater;
	private View mBtnSetWallpaper;
	private View mBtnPreview;
	private View mTopContainer;
	private View mBottomContainer;
	private View mLeftArrow;
	private View mRightArrow;

	private FilterService mFilterService;
	private FilterManager mFilterManager;
	private Handler mHandler;
	private FilterPhotoLoader mPhotoLoader;
	private ImageView mFiltePrime;
	private List<FilterParameter> mFilterList;
	private int mFilterThumbIndex = 0;

	private int mZoomDuration = 350;
	private boolean mInit;
	private boolean mValid;
	private int[] mPayExtraIds;
	private static final int ID_WALLPAPER_PREVIEW = 11;
	public static final int FILTER_WALLPAER_INDEX = 0;
	public static final int FILTER_ICON_INDEX = 1;

	public FilterChooser(Context context) {
		this(context, null);
	}

	public FilterChooser(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FilterChooser(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mFilterManager = FilterManager.getInstance();
		mFilterService = FilterService.getService(getContext());
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPhotoLoader = new FilterPhotoLoader(context, R.drawable.filter_default_icon);
		mFilterList = FilterService.getService(context).getAllFilter(FilterService.USE_TYPE_WALLPAPER);
		addDefaultFilter();
	}

	private void addDefaultFilter() {
		FilterParameter defaultFilter = new FilterParameter(0, R.string.filter_type_original, new int[]{703, 716});
		defaultFilter.setFilterNameResId(R.string.filter_type_original);
		mFilterList.add(0, defaultFilter);
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	public void initView() {
		mWallpaperView = new FilterWallpaperView(getContext());
		mWallpaperView.setId(ID_WALLPAPER_PREVIEW);
		mWallpaperView.setOnClickListener(this);
		mWallpaperView.setOnTouchListener(this);
		mHorizontalScroller = (LinearLayoutScroller) findViewById(R.id.filter_selector);
		mHorizontalScroller.setScrollerCallback(this);

		mScrollerView = (ScrollerViewGroup) findViewById(R.id.filter_chooser_content);
		mScrollerView.setScreenScrollerListener(this);
		mScrollerView.addView(mWallpaperView);
		mScrollerView.setScreenCount(1);

		mBtnSetWallpaper = findViewById(R.id.btn_set_wallpaper);
		mBtnPreview = findViewById(R.id.btn_preview);
		mBtnSetWallpaper.setOnClickListener(this);
		mBtnPreview.setOnClickListener(this);

		mTopContainer = findViewById(R.id.filter_top_container);
		mBottomContainer = findViewById(R.id.filter_bottom_container);
		mLeftArrow = findViewById(R.id.leftArrow);
		mRightArrow = findViewById(R.id.rightArrow);
		mLeftArrow.setOnClickListener(this);
		mRightArrow.setOnClickListener(this);
	}

	public void setPayied(boolean payied) {
		mFiltePrime = (ImageView) findViewById(R.id.filter_prime);
		if (payied) {
			mFiltePrime.setVisibility(GONE);
		} else {
			mFiltePrime.setVisibility(VISIBLE);
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == ID_WALLPAPER_PREVIEW) {
			if (v.getHeight() * 0.90f > event.getY()) {
				mValid = true;
			} else {
				mValid = false;
			}
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_set_wallpaper :
				mHandler.sendEmptyMessage(FilterActivity.HANDER_MESSAGE_SET_WALLPAPER);
				break;
			case R.id.btn_preview :
				if (mScrollerView.getAnimation() == null) {
					hide();
				}
				break;
			case R.id.leftArrow :
				mHorizontalScroller.setCurrentScreen(mHorizontalScroller.getCurrentScreen() - 1);
				break;
			case R.id.rightArrow :
				mHorizontalScroller.setCurrentScreen(mHorizontalScroller.getCurrentScreen() + 1);
				break;
			case ID_WALLPAPER_PREVIEW :
				if (mValid) {
					if (mScrollerView.getAnimation() == null) {
						hide();
					}
				}
				break;
			default :
				break;
		}
	}

	private void updateScrollerArrow() {
		int currentScreen = mHorizontalScroller.getCurrentScreen();
		int pageCount = mHorizontalScroller.getPageCount();
		if (currentScreen > 0) {
			mLeftArrow.setVisibility(View.VISIBLE);
		} else {
			mLeftArrow.setVisibility(View.INVISIBLE);
		}

		if (currentScreen < pageCount - 1) {
			mRightArrow.setVisibility(View.VISIBLE);
		} else {
			mRightArrow.setVisibility(View.INVISIBLE);
		}
	}

	private void updateThumbSelectState() {
		int childCount = mHorizontalScroller.getChildCount();
		if (mFilterThumbIndex >= 0 && mFilterThumbIndex <= childCount - 1) {
			for (int i = 0; i < childCount; i++) {
				View child = mHorizontalScroller.getChildAt(i);
				View imageContainer = child.findViewById(R.id.imageContainer);
				DeskTextView title = (DeskTextView) child.findViewById(R.id.selector_title);
				if (i == mFilterThumbIndex) {
					imageContainer.setBackgroundResource(R.drawable.filter_select_item_bg);
					title.setTextColor(Color.parseColor("#99cc00"));
				} else {
					imageContainer.setBackgroundResource(R.drawable.filter_unselect_iten_bg);
					title.setTextColor(Color.parseColor("#202020"));
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mScrollerView.getAnimation() != null || mTopContainer.getAnimation() != null
				|| mBottomContainer.getAnimation() != null) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void hide() {
		Animation topAnim = AnimationUtils.loadAnimation(getContext(), R.anim.filter_top_out);
		topAnim.setFillAfter(true);
		Animation bottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.filter_bottom_out);
		bottomAnim.setFillAfter(true);

		mTopContainer.startAnimation(topAnim);
		mBottomContainer.startAnimation(bottomAnim);

		Animation wallpaperAnim = getWPZoomInAnim();
		mScrollerView.startAnimation(wallpaperAnim);
	}

	public void show(int previewScreenCount, int previewCurrentScreen) {
		setVisibility(View.VISIBLE);
		Animation topAnim = AnimationUtils.loadAnimation(getContext(), R.anim.filter_top_in);
		Animation bottomAnim = AnimationUtils.loadAnimation(getContext(), R.anim.filter_bottom_in);

		mTopContainer.startAnimation(topAnim);
		mBottomContainer.startAnimation(bottomAnim);

		Animation wallpaperAnim = getWPZoomOutAnim(previewScreenCount, previewCurrentScreen);
		mScrollerView.startAnimation(wallpaperAnim);
	}

	/**
	 * 壁纸进入预览时放大动画
	 * @return
	 */
	private Animation getWPZoomInAnim() {
		float scale = (float) getHeight() / (float) mWallpaperView.getFilterImage().getHeight();
		int imageTop = (mScrollerView.getHeight() - mWallpaperView.getFilterImage().getHeight()) / 2;
		float pivotX = mScrollerView.getWidth() / 2;
		float pivotY = (mTopContainer.getHeight() + imageTop * scale) / (scale - 1);
		ScaleAnimation animation = new ScaleAnimation(1.0f, scale, 1.0f, scale, pivotX, pivotY);
		animation.setFillAfter(true);
		animation.setDuration(mZoomDuration);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mHandler.sendEmptyMessage(FilterActivity.HANDER_MESSAGE_SHOW_WALLPAPER_PREVIEW);
			}
		});
		return animation;
	}

	/**
	 * 壁纸退出预览时缩小动画
	 * @return
	 */
	private Animation getWPZoomOutAnim(int previewScreenCount, int previewCurrentScreen) {
		float scale = (float) getHeight() / (float) mWallpaperView.getFilterImage().getHeight();
		int imageTop = (mScrollerView.getHeight() - mWallpaperView.getFilterImage().getHeight()) / 2;
		float pivotY = (mTopContainer.getHeight() + imageTop * scale) / (scale - 1);
		float pivotX = mScrollerView.getWidth() / 2;
		
		if (previewScreenCount == 3) {
			if (previewCurrentScreen == 0) {
				pivotX = 0;
			} else if (previewCurrentScreen == 2) {
				pivotX = mScrollerView.getWidth();
			}
		}

		ScaleAnimation animation = new ScaleAnimation(scale, 1.0f, scale, 1.0f, pivotX, pivotY);
		animation.setFillAfter(false);
		animation.setDuration(mZoomDuration);
		return animation;
	}

	public void initData() {
	}

	public void initThumbFilter() {
		if (mInit) {
			return;
		}
		mInit = true;
		// 壁纸滤镜：增大预览小图的尺寸,区别手机平板的计算规则
		// 平板固定显示７个，手机按照原来的规则
		boolean isTablet = Machine.isTablet(ApplicationProxy.getContext());
		int itemWidth = 0;
		int imageWidth = 0;
		if (isTablet) {
			int scrollerWidth = mHorizontalScroller.getWidth();
			int minGap = DrawUtils.dip2px(5);
			itemWidth = (scrollerWidth - minGap * 8) / 7;
			imageWidth = itemWidth - DrawUtils.dip2px(2);
			// 平板情况下，mHorizontalScroller高度也需要改变
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mHorizontalScroller
					.getLayoutParams();
			params.height = itemWidth + DrawUtils.dip2px(20);
			mHorizontalScroller.setLayoutParams(params);
		} else {
			itemWidth = (int) getResources().getDimension(R.dimen.filter_select_item_width);
		}
		int count = mFilterList.size();

		mHorizontalScroller.setItemWidth(itemWidth);
		boolean completeSetup = mHorizontalScroller.initScroller(count);
		if (completeSetup) {
			for (int i = 0; i < count; i++) {
				FilterParameter parameter = mFilterList.get(i);
				View child = mInflater.inflate(R.layout.diy_filter_selector_item, null);
				((DeskTextView) child.findViewById(R.id.selector_title)).setText(parameter
						.getFilterNameResId());
				// 平板的情况下，每个item的imageView也需要扩大宽高
				if (isTablet) {
					ImageView selectorImage = (ImageView) child.findViewById(R.id.selector_image);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) selectorImage
							.getLayoutParams();
					params.width = imageWidth;
					params.height = imageWidth;
					selectorImage.setLayoutParams(params);
					selectorImage.setScaleType(ScaleType.FIT_XY);
				}
				child.setTag(parameter);
				mHorizontalScroller.addChild(child, i);
			}

			startLoadThumb(mHorizontalScroller.getCurrentScreen());
			updateScrollerArrow();
			updateThumbSelectState();
		}
	}

	private void startLoadThumb(int currentScreen) {
		if (currentScreen >= 0) {
			int childCount = mHorizontalScroller.getChildCount();
			int pageSize = mHorizontalScroller.getPageSize();
			int startIndex = currentScreen * pageSize;
			int endIndex = Math.min(startIndex + pageSize, childCount);

			for (int i = startIndex; i < endIndex; i++) {
				ImageView image = (ImageView) mHorizontalScroller.getChildAt(i).findViewById(
						R.id.selector_image);
				if (image != null) {
					mPhotoLoader.loadPhoto(image, mFilterList.get(i));
				}
			}
		}
	}

	public void setWallpaper(Bitmap bitmap) {
		mWallpaperView.setWallpaper(bitmap, getHeight() / 2);
	}
	
	public int getFilterThumbIndex() {
		return mFilterThumbIndex;
	}

	@Override
	public ScreenScroller getScreenScroller() {
		return null;
	}

	@Override
	public void setScreenScroller(ScreenScroller scroller) {
	}

	@Override
	public void onFlingIntercepted() {
	}

	@Override
	public void onScrollStart() {
	}

	@Override
	public void onFlingStart() {
	}

	@Override
	public void onScrollChanged(int newScroll, int oldScroll) {
	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
	}

	@Override
	public void onScrollFinish(int currentScreen) {
	}

	@Override
	public void onHorizontalScrollFinish(int currentScreen) {
		startLoadThumb(currentScreen);
		updateScrollerArrow();
	}

	public boolean hasCompleteLayout() {
		return getWidth() > 0 && getHeight() > 0 && mHorizontalScroller.getWidth() > 0
				&& mHorizontalScroller.getHeight() > 0;
	}

	public int getPayExtraId(int payExtraIdIndex) {
		if (mPayExtraIds != null && mPayExtraIds.length > payExtraIdIndex) {
			return mPayExtraIds[payExtraIdIndex];
		} else {
			if (payExtraIdIndex == FilterParameter.PAY_EXTRA_IDS_INDEX_EDIT) {
				return 703;
			} else {
				return 716;
			}
		}
	}
	
	@Override
	public void onItemClick(View view, int index) {

		if (view.getTag() != null && view.getTag() instanceof FilterParameter) {
			if (mFilterThumbIndex == index) {
				return;
			}
			
			mFilterThumbIndex = index;
			updateThumbSelectState();
			final FilterParameter parameter = (FilterParameter) view.getTag();

			Message message = mHandler
					.obtainMessage(FilterActivity.HANDER_MESSAGE_UPDATE_PROGRESS_STATE);
			message.arg1 = View.VISIBLE;
			mHandler.sendMessage(message);

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Bitmap bitmap = null;
						if (parameter.getTypeId() == 0) {
							bitmap = mFilterManager.getOriginBitmap();
						} else {
							parameter.mSrcBitmap = mFilterManager.getOriginBitmap();
							bitmap = mFilterService.render(parameter);
						}

						if (bitmap != null) {
							Message message = mHandler
									.obtainMessage(FilterActivity.HANDER_MESSAGE_ON_RENDER_FINISH);
							message.obj = bitmap;
							mHandler.sendMessage(message);
						}
						mPayExtraIds = parameter.getPayExtraIds();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).start();
		}
	}

	public void release() {
		if (mPhotoLoader != null) {
			mPhotoLoader.release();
		}
	}

}
