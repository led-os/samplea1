package com.jiubang.ggheart.apps.desks.Preferences.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.gau.go.launcherex.R;
import com.go.util.graphics.BitmapUtility;

/**
 * 顶部带透明渐变的ScrollView
 * @author zouguiquan
 *
 */
public class TranslucentScrollView extends ScrollView {

	private Drawable mTopTranslateDrawable;
	private Bitmap mTopTranslateBitmap;
	private int mScrollTop;

	public TranslucentScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TranslucentScrollView);
		mTopTranslateDrawable = typedArray.getDrawable(R.styleable.TranslucentScrollView_translucent);
		if (mTopTranslateDrawable == null) {
			mTopTranslateDrawable = context.getResources().getDrawable(
					R.drawable.desk_setting_top_translucent);
		}
	}

	public TranslucentScrollView(Context context) {
		this(context, null);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mTopTranslateDrawable != null) {
			int height = mTopTranslateDrawable.getIntrinsicHeight();
			if (mTopTranslateBitmap == null) {
				mTopTranslateBitmap = BitmapUtility.createBitmapFromDrawable(mTopTranslateDrawable, getWidth(), height);
			}
			canvas.drawBitmap(mTopTranslateBitmap, 0, mScrollTop, null);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		mScrollTop = t;
	}
}
