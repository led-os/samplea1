package com.jiubang.ggheart.apps.desks.diy.filter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.go.util.graphics.DrawUtils;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;

/**
 * 
 * @author zouguiquan
 *
 */
public class LinearLayoutScroller extends LinearLayout implements ScreenScrollerListener {

	private ScreenScroller mScroller;
	private float mLastMotionX;
	private float mlastMotionY;
	private static final int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;

	private int mItemWidth;
	private int mItemGap;
	private int mPageCount;
	private int mPageSize;

	private int mMinGap = DrawUtils.dip2px(5);
	private int mScrollDuration = 400;
	
	private ScrollerCallback mScrollerCallback;

	public LinearLayoutScroller(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LinearLayoutScroller(Context context) {
		this(context, null);
	}
	
	public void setScrollerCallback(ScrollerCallback callback) {
		mScrollerCallback = callback;
	}

	private void init() {
		mScroller = new ScreenScroller(getContext(), this);
		mScroller.setBackgroundAlwaysDrawn(true);
		mScroller.setMaxOvershootPercent(0);
		mScroller.setDuration(mScrollDuration);
	}

	public void setScrollDuration(int duration) {
		mScrollDuration = duration;
		mScroller.setDuration(mScrollDuration);
	}

	/**
	 * 需要在initScroller方法前调用
	 * @param itemWidth
	 */
	public void setMinGap(int minGap) {
		mMinGap = minGap;
	}

	/**
	 * 需要在initScroller方法前调用
	 * @param itemWidth
	 */
	public void setItemWidth(int itemWidth) {
		mItemWidth = itemWidth;
	}

	public boolean initScroller(int childCount) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		if (width <= 0 || height <= 0) {
			return false;
		}

		if (mItemWidth <= 0) {
			Log.e("filter", "you should invoking setItemWidth first");
			return false;
		}

		mPageSize = width / mItemWidth;
		mItemGap = (width - mPageSize * mItemWidth) / (mPageSize + 1);

		while (mItemGap < mMinGap) {
			mPageSize--;
			mItemGap = (width - mPageSize * mItemWidth) / (mPageSize + 1);
		}

		mPageCount = childCount % mPageSize == 0 ? childCount / mPageSize : childCount / mPageSize
				+ 1;

		mScroller.setScreenCount(mPageCount);
		mScroller.setCurrentScreen(0);
		mScroller.setScreenSize(width, height);
		return true;
	}

	public void addChild(View child, int childIndex) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,
				LayoutParams.FILL_PARENT);
		if (childIndex % mPageSize == 0) {
			params.leftMargin = mItemGap;
			params.rightMargin = mItemGap;
		} else {
			params.rightMargin = mItemGap;
		}
		child.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int index = -1;
				for (int i = 0; i < getChildCount(); i++) {
					if (getChildAt(i) == v) {
						index = i;
						break;
					}
				}
				mScrollerCallback.onItemClick(v, index);
			}
		});
		addView(child, params);
	}

	public int getCurrentScreen() {
		if (mScroller != null) {
			return mScroller.getCurrentScreen();
		}
		return -1;
	}

	public int getPageSize() {
		return mPageSize;
	}
	
	public int getPageCount() {
		return mPageCount;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mScroller != null) {
			mScroller.invalidateScroll();
		}
		super.dispatchDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		if (mScroller != null) {
			return mScroller.onTouchEvent(event, event.getAction());
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction() & MotionEvent.ACTION_MASK;
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
			case MotionEvent.ACTION_DOWN : {
				mLastMotionX = x;
				mlastMotionY = y;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			}
			case MotionEvent.ACTION_MOVE : {
				final int xoffset = (int) (x - mLastMotionX);
				final int yoffset = (int) (y - mlastMotionY);
				if (Math.abs(yoffset) < Math.abs(xoffset)
						&& Math.abs(xoffset) > DrawUtils.sTouchSlop) {
					mTouchState = TOUCH_STATE_SCROLLING;
					mScroller.onTouchEvent(ev, MotionEvent.ACTION_DOWN);
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL :
			case MotionEvent.ACTION_UP : {
				mTouchState = TOUCH_STATE_REST;
				break;
			}
			default :
				break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public void computeScroll() {
		if (mScroller != null) {
			mScroller.computeScrollOffset();
		}
	}

	@Override
	public ScreenScroller getScreenScroller() {
		return mScroller;
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
		if (mScrollerCallback != null) {
			mScrollerCallback.onHorizontalScrollFinish(currentScreen);
		}
	}
	
	public void setCurrentScreen(int index) {
		if (index >= 0 && index < mPageCount) {
			mScroller.gotoScreen(index, mScrollDuration, false);
		}
	}
	
	/**
	 * 
	 * @author zouguiquan
	 *
	 */
	public interface ScrollerCallback {
		public void onHorizontalScrollFinish(int currentScreen);
		public void onItemClick(View view, int index);
	}
}
