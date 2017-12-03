package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve;

import android.graphics.drawable.Drawable;

/**
 * 逐帧精灵
 * @author yejijiong
 *
 */
public class FramesElve extends Elve {
	/**
	 * 逐帧图片数组，长度为l
	 */
	Drawable[] mDrawableArray;
	/**
	 * 逐帧图片阀值，长度为l - 1
	 */
	float[] mDrawableThresholdArray;
	
	public FramesElve(Drawable[] drawableArray, float[] drawableThresholdArray,
			float startAnimateBase, float endAnimateBase) {
		super(drawableArray[0], startAnimateBase, endAnimateBase);
		mDrawableArray = drawableArray;
		mDrawableThresholdArray = drawableThresholdArray;
		setDrawable(mDrawableArray[0]);
	}

	@Override
	public void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		int index = 0;
		for (int i = 0; i < mDrawableThresholdArray.length; i++) {
			if (i == 0) {
				if (curAnimatePercent <= mDrawableThresholdArray[i]) {
					index = i;
					break;
				}
			} else if (i == mDrawableThresholdArray.length - 1) {
				if (curAnimatePercent <= mDrawableThresholdArray[i]) {
					index = i;
				} else {
					index = i + 1;
				}
				break;
			} else {
				if (curAnimatePercent > mDrawableThresholdArray[i - 1]
						&& curAnimatePercent < mDrawableThresholdArray[i]) {
					index = i;
					break;
				}
			}
		}
		setDrawable(mDrawableArray[index]);
	}
}

