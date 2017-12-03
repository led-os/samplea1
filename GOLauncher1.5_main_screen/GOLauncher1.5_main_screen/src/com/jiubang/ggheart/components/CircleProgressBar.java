package com.jiubang.ggheart.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;

/**
 * 圆形带进度的进度条
 * @author yangguanxiang
 *
 */
public class CircleProgressBar extends View {

	private float mProgress;
	private float mProgressAngle;
	private int mBackgroundColor;
	private int mBackgroundAlpha = 255;
	private int mProgressColor;
	private int mProgressAlpha = 255;
	private Paint mPaint;
	private RectF mRect = new RectF();
	public CircleProgressBar(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
	}

	@Override
	public void setBackgroundColor(int color) {
		mBackgroundColor = color;
	}

	public void setProgressColor(int color) {
		mProgressColor = color;
	}

	public void setBackgroundAlpha(int alpha) {
		mBackgroundAlpha = alpha;
	}

	public void setProgressAlpha(int alpha) {
		mProgressAlpha = alpha;
	}

	public void setStroke(int stroke) {
		mPaint.setStrokeWidth(stroke);
		invalidate();
	}

	public void setProgress(float progress) {
		if (progress >= 0 && progress <= 1) {
			mProgress = progress;
			mProgressAngle = 360 * mProgress;
			invalidate();
		}
	}

	public float getProgress() {
		return mProgress;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		float stroke = mPaint.getStrokeWidth();
		mRect.left = stroke;
		mRect.top = stroke;
		mRect.right = getWidth() - stroke;
		mRect.bottom = getHeight() - stroke;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		//画背景
		mPaint.setColor(mBackgroundColor);
		mPaint.setAlpha(mBackgroundAlpha);
		canvas.drawArc(mRect, 0, 360, false, mPaint);

		//画进度条
		mPaint.setColor(mProgressColor);
		mPaint.setAlpha(mProgressAlpha);
		canvas.drawArc(mRect, -90, mProgressAngle, false, mPaint);

	}

}
