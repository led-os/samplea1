package com.jiubang.ggheart.apps.desks.diy.filter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.go.util.graphics.DrawUtils;
import com.go.util.scroller.ScreenScroller;
import com.go.util.scroller.ScreenScrollerListener;
import com.jiubang.ggheart.apps.desks.diy.filter.FilterScreenScroller;

/**
 * 
 * @author zouguiquan
 *
 */
public class ScrollerableWallpaper extends FrameLayout implements ScreenScrollerListener {

	private FilterScreenScroller mScroller;
	private float mLastMotionX;
	private float mlastMotionY;
	private static final int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	
	private boolean mIsScrollerStart;

	public ScrollerableWallpaper(Context context) {
		this(context, null);
	}

	public ScrollerableWallpaper(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollerableWallpaper(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new FilterScreenScroller(context, this);
		mScroller.setDuration(300);
	}
	
	public void initScroller(Drawable drawable) {
		int bitmapWidth = ((BitmapDrawable) drawable).getBitmap().getWidth();
		if (bitmapWidth == getWidth()) {
			mScroller.setScreenCount(1);
			mScroller.setCurrentScreen(0);
		} else {
			mScroller.setScreenCount(3);
			mScroller.setCurrentScreen(1);
		}
		
		mScroller.setBackgroundAlwaysDrawn(false);
		mScroller.setBackgroundScrollEnabled(true);
		mScroller.setBackground(drawable);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mScroller.setScreenSize(right - left, bottom - top);
	}
	
	public int getCurrentScreen() {
		return mScroller.getCurrentScreen();
	}
	
	public int getScreenCount() {
		return mScroller.getScreenCount();
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
	
	public int getTouchState() {
		return mTouchState;
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mScroller != null) {
			mScroller.invalidateScroll();
			mScroller.drawBackground(canvas, mScroller.getScroll());
		}
		super.dispatchDraw(canvas);
	}
	
	@Override
	public void computeScroll() {
		if (mScroller != null) {
			mScroller.computeScrollOffset();
		}
	}
	
	public FilterScreenScroller getScroller() {
		return mScroller;
	}
	
	public boolean isScrollerStart() {
		return mIsScrollerStart;
	}
	
	public void setIsScrollerStart(boolean value) {
		mIsScrollerStart = value;
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
		mIsScrollerStart = true;
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
}
