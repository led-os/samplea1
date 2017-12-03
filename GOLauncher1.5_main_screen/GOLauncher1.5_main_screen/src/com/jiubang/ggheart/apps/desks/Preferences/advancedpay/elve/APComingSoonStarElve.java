package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve;

import android.graphics.drawable.Drawable;

public class APComingSoonStarElve extends Elve {

	private float mOriginalCenterX;
	private float mOriginalCenterY;
	private float mTargetCenterY;
	private float mEachYChange = 0f;
	
	public APComingSoonStarElve(Drawable drawable, float startAnimateBase, float endAnimateBase) {
		super(drawable, startAnimateBase, endAnimateBase);
	}
	
	@Override
	public void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		if (curAnimatePercent > mStartAnimateBase) { // 只有大于开始时间才开始做星星动画
			doStarAnimate();
		} else { // 开始时间之前都处于Alpha为0的状态
			setAlpha(MIN_ALPHA);
			setCenter(mOriginalCenterX, mOriginalCenterY);
		}
	}
	
	public void setStarAnimateValue(float originalCenterX, float originalCenterY,
			float targetCenterX, float targetCenterY, float eachYChange) {
		mOriginalCenterX = originalCenterX;
		mOriginalCenterY = originalCenterY;
		mTargetCenterY = targetCenterY;
		mEachYChange = eachYChange;
		setCenter(originalCenterX, originalCenterY);
	}
	
	private void doStarAnimate() {
		float centerY = getCenterY();
		if (mEachYChange != 0) {
			if (centerY > mOriginalCenterY || centerY < mTargetCenterY) {
				centerY = mOriginalCenterY;
			} else {
				centerY += mEachYChange;
			}
		}
		setCenter(getCenterX(), centerY);
		float percent = (centerY - mOriginalCenterY) / (mTargetCenterY - mOriginalCenterY);
		if (percent < 0.05f || percent > 0.95f) { // 避免图片闪动
			setAlpha(MIN_ALPHA);
		} else {
			setAlpha((int) ((1 - percent) * MAX_ALPHA));
		}
	}
}
