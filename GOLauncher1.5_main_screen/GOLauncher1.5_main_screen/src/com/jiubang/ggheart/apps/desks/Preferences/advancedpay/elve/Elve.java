package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * 精灵类
 * @author yejijiong
 *
 */
public class Elve {
	private float mCenterX = 0;
	private float mCenterY = 0;
	private int mAlpha = 255;
	private float mScaleX = 1;
	private float mScaleY = 1;
	private float mRotateAngle = 0;
	protected int mRotateType = ROTATE_NONE;
	private int mScaleType = SCALE_NONE;
	private Drawable mDrawable;
	private int mDrawableHeightHalf;
	private int mDrawableWidthHalf;
	/**
	 * 当前进度变化倍数
	 */
	protected float mChangeMultiple = 1.0f;
	/**
	 * 开始进行动画的基数
	 */
	protected float mStartAnimateBase = 0;
	/**
	 * 动画结束的基数
	 */
	protected float mEndAnimateBase = 1.0f;
	
	public static final int MAX_ALPHA = 255;
	public static final int MIN_ALPHA = 0;
	public static final int ROTATE_NONE = 0;
	public static final int ROTATE_TOP_MIDDLE = 1;
	public static final int ROTATE_BOTTOM_LEFT = 2;
	public static final int ROTATE_CENTER = 3;
	public static final int SCALE_NONE = 0;
	public static final int SCALE_CENTER = 1;
	public static final int SCALE_RIGHT_MIDDLE = 2;
	public static final int SCALE_LEFT_MIDDLE = 3;
	public static final int SCALE_BOTTOM_MIDDLE = 4;
	public static final int SCALE_TOP_MIDDEL = 5;
	
	public Elve(Drawable drawable, float startAnimateBase, float endAnimateBase) {
		setDrawable(drawable);
		mStartAnimateBase = startAnimateBase;
		mEndAnimateBase = endAnimateBase;
	}
	
	public void setCenter(float centerX, float centerY) {
		mCenterX = centerX;
		mCenterY = centerY;
	}
	
	public float getCenterX() {
		return mCenterX;
	}

	public float getCenterY() {
		return mCenterY;
	}

	public int getAlpha() {
		return mAlpha;
	}

	public void setAlpha(int alpha) {
		mAlpha = alpha;
	}

	public void setScale(float scaleX , float scaleY, int scaleType) {
		mScaleX = scaleX;
		mScaleY = scaleY;
		mScaleType = scaleType;
	}

	public void setRotate(float rotateAngle, int rotateType) {
		mRotateAngle = rotateAngle;
		mRotateType = rotateType;
	}

	public Drawable getDrawable() {
		return mDrawable;
	}
	
	protected float getRotateAngle() {
		return mRotateAngle;
	}

	public void setDrawable(Drawable drawable) {
		mDrawable = drawable;
		mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(),
				mDrawable.getIntrinsicHeight());
		mDrawableHeightHalf = mDrawable.getIntrinsicHeight() >> 1;
		mDrawableWidthHalf = mDrawable.getIntrinsicWidth() >> 1;
	}

	public void drawElve(Canvas canvas) {
		if (mDrawable != null) {
			mDrawable.setAlpha(mAlpha);
			canvas.save();
			canvas.translate(mCenterX, mCenterY); // 移动图片中点
			if (mScaleType == SCALE_CENTER) {
				canvas.scale(mScaleX, mScaleY); // 从中点进行放缩
			} else if (mScaleType == SCALE_RIGHT_MIDDLE) { // 从右边中点进行放缩
				canvas.translate(mDrawableWidthHalf, 0);
				canvas.scale(mScaleX, mScaleY);
				canvas.translate(-mDrawableWidthHalf, 0);
			} else if (mScaleType == SCALE_LEFT_MIDDLE) { // 从左边中点进行放缩
				canvas.translate(-mDrawableWidthHalf, 0);
				canvas.scale(mScaleX, mScaleY);
				canvas.translate(mDrawableWidthHalf, 0);
			} else if (mScaleType == SCALE_BOTTOM_MIDDLE) { // 从底部中点进行放缩
				canvas.translate(0, mDrawableHeightHalf);
				canvas.scale(mScaleX, mScaleY);
				canvas.translate(0, -mDrawableHeightHalf);
			} else if (mScaleType == SCALE_TOP_MIDDEL) { // 从顶部中点进行缩放
				canvas.translate(0, -mDrawableHeightHalf);
				canvas.scale(mScaleX, mScaleY);
				canvas.translate(0, mDrawableHeightHalf);
			}
			if (mRotateType == ROTATE_BOTTOM_LEFT) {
				canvas.rotate(mRotateAngle, -mDrawableWidthHalf, mDrawableHeightHalf); // 移动到左下角，并进行旋转
			} else if (mRotateType == ROTATE_TOP_MIDDLE) { // 移动到顶部中点进行缩放
				canvas.rotate(mRotateAngle, 0, -mDrawableHeightHalf);
			} else if (mRotateType == ROTATE_CENTER) { // 已经处于中点，直接进行旋转
				canvas.rotate(mRotateAngle);
			}
			canvas.translate(-mDrawableWidthHalf, -mDrawableHeightHalf); // 移动到左上角，进行绘制
			mDrawable.draw(canvas);
			canvas.restore();
		}
	}

	public void calculateAnimateValue(float curAnimatePercent) {
	}
	
	protected float caculateAnimatePercent(float curAnimatePercent) {
		if (curAnimatePercent < mStartAnimateBase) { // 小于动画开始基数，直接return 0
			return 0;
		} else {
			curAnimatePercent = (curAnimatePercent - mStartAnimateBase)
					/ (mEndAnimateBase - mStartAnimateBase) * mChangeMultiple;
			if (curAnimatePercent > 1.0f) {
				curAnimatePercent = 1.0f;
			} else if (curAnimatePercent < 0) {
				curAnimatePercent = 0;
			}
			return curAnimatePercent;
		}
	}
}
