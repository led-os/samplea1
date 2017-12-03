package com.jiubang.ggheart.apps.desks.diy.filter.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.jiubang.ggheart.apps.desks.diy.filter.CompareGridAdapter;
import com.jiubang.ggheart.apps.desks.diy.filter.FilterActivity;
import com.jiubang.ggheart.apps.desks.diy.filter.FilterManager;
import com.jiubang.ggheart.components.DeskButton;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.AppDataThemeBean;
import com.jiubang.ggheart.data.theme.bean.ThemeBean;
import com.jiubang.ggheart.data.theme.parser.AppThemeParser;

/**
 * 
 * @author zouguiquan
 *
 */
public class FilterPreview extends FrameLayout implements View.OnClickListener {

	private static final String INIT_DATA_THREAD = "init_data_thread";
	private Handler mHandler;
	private GridView mGridView;
	private DeskButton mBtnSetNow;
	private ScrollerableWallpaper mScrollerableWallpaper;

	private final int mAlphaDuration = 180;
	private ImageView mFiltePrime;

	public FilterPreview(Context context) {
		this(context, null);
	}

	public FilterPreview(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FilterPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public int getCurrentScreen() {
		return mScrollerableWallpaper.getCurrentScreen();
	}
	
	public int getScreenCount() {
		return mScrollerableWallpaper.getScreenCount();
	}

	public void setPayied(boolean payied) {
		mFiltePrime = (ImageView) findViewById(R.id.filter_preview_prime);
		if (payied) {
			mFiltePrime.setVisibility(GONE);
		} else {
			mFiltePrime.setVisibility(VISIBLE);
		}
	}
	
	public void show() {
		setVisibility(View.VISIBLE);
		Bitmap bitmap = FilterManager.getInstance().getFilterBitmap();
		if (bitmap == null) {
			bitmap = FilterManager.getInstance().getOriginBitmap();
		}

		if (bitmap != null) {
			Drawable drawable = new BitmapDrawable(bitmap);
			mScrollerableWallpaper.initScroller(drawable);
		} 

		showChildWithAlpha(mGridView);
		showChildWithAlpha(mBtnSetNow);
	}
	
	private void showChildWithAlpha(View child) {
		child.setVisibility(View.VISIBLE);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(mAlphaDuration);
		child.startAnimation(alphaAnimation);
	}
	
	private void hideChildWithAlpha(final View child) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setDuration(mAlphaDuration);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				post(new Runnable() {

					@Override
					public void run() {
						child.setVisibility(View.INVISIBLE);
					}
				});
			}
		});
		child.startAnimation(alphaAnimation);
	}

	public void hide() {
		setVisibility(View.GONE);
	}

	public void initView() {
		mScrollerableWallpaper = (ScrollerableWallpaper) findViewById(R.id.scrollerable_wp);
		mScrollerableWallpaper.setOnClickListener(this);
		mGridView = (GridView) findViewById(R.id.compareGrid);
		mBtnSetNow = (DeskButton) findViewById(R.id.setNow);
		mBtnSetNow.setOnClickListener(this);
	}

	public void initData() {
		new Thread(INIT_DATA_THREAD) {
			public void run() {
//				DesktopSettingInfo info = GoSettingControler.getInstance(getContext())
//						.createDesktopSettingInfo();
//				int count = info.mColumn * 2;
				int count = 4 * 2;
				ThemeManager themeManager = ThemeManager.getInstance(getContext());
				AppDataThemeBean themeBean = (AppDataThemeBean) themeManager
						.getThemeBean(ThemeBean.THEMEBEAN_TYPE_APPDATA);
				if (themeBean == null) {
					themeBean = (AppDataThemeBean) new AppThemeParser()
					.autoParseAppThemeXml(getContext(),
							IGoLauncherClassName.DEFAULT_THEME_PACKAGE, false);
				}
				if (themeBean == null) {
					return;
				}
				
				List<Drawable> list = new ArrayList<Drawable>();
				Map<String, String> map = themeBean.getFilterAppsMap();
				Set<String> nameSet = new HashSet<String>();
				for (Map.Entry<String, String> entry : map.entrySet()) {
					String drawableName = entry.getValue();
					if (drawableName == null) {
						continue;
					}
					
					if (list.size() >= count) {
						break;
					}
					
					if (!nameSet.contains(drawableName)) {
						Drawable icon = ImageExplorer.getInstance(getContext()).getDrawable(drawableName);
						if (icon != null) {
							list.add(icon);
							nameSet.add(drawableName);
						}
					}
					
//					if (drawableName != null) {
//						Drawable icon = ImageExplorer.getInstance(getContext()).getDrawable(drawableName);
//						if (nameSet.size() < count) {
//							if (icon != null) {
//								nameSet.add(drawableName);
//							}
//						} else {
//							break;
//						}
//					}
				}
				
//				Iterator<String> iterator = nameSet.iterator();
//				while (iterator.hasNext()) {
//					Drawable icon = ImageExplorer.getInstance(getContext()).getDrawable(iterator.next());
//					list.add(icon);
//				}
//				postInit(info.mColumn, list);
				postInit(4, list);
			};
		}.start();
		
	}

	private void postInit(final int column, final List<Drawable> list) {
		if (mHandler != null) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if (mGridView != null) {
						mGridView.setNumColumns(column);
						mGridView.setAdapter(new CompareGridAdapter(getContext(), list));
						mGridView.setEnabled(false);
						mGridView.setClickable(false);
						mGridView.setPressed(false);
					}
				}
			});
		};
	}
	
	
	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.scrollerable_wp :
				if (mScrollerableWallpaper.isScrollerStart()) {
					mScrollerableWallpaper.setIsScrollerStart(false);
				} else {
					if (mGridView.getAnimation() == null) {
						if (mGridView.getVisibility() == View.VISIBLE) {
							hideChildWithAlpha(mGridView);
						} else {
							showChildWithAlpha(mGridView);
						}
					}
				}

				break;
			case R.id.setNow :
				mHandler.sendEmptyMessage(FilterActivity.HANDER_MESSAGE_SET_WALLPAPER);
				break;
			default :
				break;
		}
	}
}
