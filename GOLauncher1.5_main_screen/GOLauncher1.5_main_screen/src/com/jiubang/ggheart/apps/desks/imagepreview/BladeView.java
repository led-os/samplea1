package com.jiubang.ggheart.apps.desks.imagepreview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gau.go.launcherex.R;

/**
 * 
 * <br>
 * 类描述:类似于联系人右边滑动条的控件 <br>
 * 功能详细描述:
 * 
 * @author WANGZHUOBIN
 * @date [2013-7-19]
 */
public class BladeView extends View {
	private final static boolean DEBUG = false;
	private final static String TAG = "wangzhuobin";

	// 画笔
	private Paint mPaint = new Paint();

	// 是否绘制滚动条
	private boolean mDrawScrollBar = true;
	// 滚动条的图片
	private Drawable mScrollBarDrawable;
	// 滚动条绘制的开始位置的比例
	private float mScrollBarStartRatio;
	// 滚动条的宽高
	private int mScrollBarWidth = 20;
	private int mScrollBarHeight = 40;

	// 是否绘制白线
	private boolean mDrawLine = true;
	private Drawable mLineDrawable = null;
	
	private boolean mHasInit = false;
	
	private OnScrollBarTouchListener mListener = null;

	public BladeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BladeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BladeView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mScrollBarDrawable = context.getResources().getDrawable(
				R.drawable.new_change_icon_sliderbar);
		mLineDrawable = context.getResources().getDrawable(
				R.drawable.new_change_icon_sliderline);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mPaint == null) {
			return;
		}
		int height = getHeight();
		int width = getWidth();
		if (mDrawLine) {
			mPaint.setAntiAlias(true);
			canvas.save();
			canvas.translate((width - 5) / 2, 0);
			mLineDrawable.setBounds(0, 0, 5, height);
			mLineDrawable.draw(canvas);
			canvas.restore();
		}
		if (mDrawScrollBar && mScrollBarDrawable != null) {
			mPaint.reset();
			int scrollBarStart = (int) (mScrollBarStartRatio * height);
			mScrollBarDrawable.setBounds(0, 0, mScrollBarWidth,
					mScrollBarHeight);
			canvas.save();
			canvas.translate((width - mScrollBarWidth) / 2, scrollBarStart);
			mScrollBarDrawable.draw(canvas);
			canvas.restore();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		if (DEBUG) {
			Log.i(TAG, "dispatchTouchEvent -- y:" + y);
		}
		final float ratio = (float) ((y - mScrollBarHeight / 2) / getHeight());
		//--------------------------------------------//
		int halfHeight = mScrollBarHeight / 2;
		float mratio = (float) halfHeight / getHeight();
		//--------------------------------------------//
		if (DEBUG) {
			Log.i(TAG, "dispatchTouchEvent -- ratio:" + ratio);
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mScrollBarDrawable = getContext().getResources().getDrawable(
					R.drawable.new_change_icon_sliderbar_light);
			// 落点不在滚动条上，则响应
			if (!isTouchOnScrollBar(ratio)) {
				performTouchScrollbar(ratio);
			}
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			performTouchScrollbar(ratio + mratio);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mScrollBarDrawable = getContext().getResources().getDrawable(
					R.drawable.new_change_icon_sliderbar);
			invalidate();
			break;
		}
		return true;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public boolean isDrawScrollBar() {
		return mDrawScrollBar;
	}

	public void setDrawScrollBar(boolean drawScrollBar) {
		this.mDrawScrollBar = drawScrollBar;
	}

	public Drawable getScrollBarDrawable() {
		return mScrollBarDrawable;
	}

	public void setScrollBarDrawable(Drawable scrollBarDrawable) {
		this.mScrollBarDrawable = scrollBarDrawable;
	}

	public void refreshScrollBar(float scrollBarStartRatio) {
		mScrollBarStartRatio = scrollBarStartRatio;
		invalidate();
	}

	public int getScrollBarWidth() {
		return mScrollBarWidth;
	}

	public void setScrollBarWidth(int scrollBarWidth) {
		this.mScrollBarWidth = scrollBarWidth;
	}

	public int getScrollBarHeight() {
		return mScrollBarHeight;
	}

	public void setScrollBarHeight(int scrollBarHeight) {
		this.mScrollBarHeight = scrollBarHeight;
	}

	public void setHasInit(boolean flag) {
		mHasInit = flag;
	}
	
	public boolean getHasInit() {
		return mHasInit;
	}
	
	public void setOnTouchScrollBarListener(OnScrollBarTouchListener listener) {
		mListener = listener;
	}
	
	private void performTouchScrollbar(float ratio) {
		if (mListener != null) {
			mListener.onScrollBarTouch(ratio);
		}
	}
	
	/**
	 * 
	 * <br>
	 * 类描述:点击监听器 <br>
	 * 功能详细描述:
	 * 
	 * @author WANGZHUOBIN
	 * @date [2013-7-29]
	 */
	public interface OnColumnChangeListener {
		void onColumnChange(int columns);
	}
	
	/**
	 * 
	 *
	 */
	public interface OnScrollBarTouchListener {
		void onScrollBarTouch(float ratio);
	}

	private boolean isTouchOnScrollBar(float ratio) {
		int halfHeight = mScrollBarHeight / 2;
		float mratio = (float) halfHeight / getHeight();
		float minRatio = mScrollBarStartRatio - mratio;
		float maxRatio = mScrollBarStartRatio + mratio;
		if (minRatio < ratio && ratio < maxRatio) {
			return true;
		}
		return false;
	}
	
}
