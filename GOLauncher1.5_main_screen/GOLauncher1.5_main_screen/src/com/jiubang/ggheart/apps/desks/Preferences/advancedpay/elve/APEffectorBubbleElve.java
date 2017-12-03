package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;

/**
 * 特效泡泡精灵
 * @author yejijiong
 *
 */
public class APEffectorBubbleElve extends Elve {
	private Point mStartCenterPoint;
	private Point mEndCenterPoint;
	private int mShakeMaxDistance;
	public static final int SHAKE_DIRECTION_UP = 0;
	public static final int SHAKE_DIRECTION_DOWN = 1;
	private int mShakeDirection;
	private float mShakeEachChange;
	private boolean mIsAnimateFinish = false;
	
	public APEffectorBubbleElve(Drawable drawable, float startAnimateBase, float endAnimateBase, int shakeDirection) {
		super(drawable, startAnimateBase, endAnimateBase);
		mShakeMaxDistance = DrawUtils.dip2px(2);
		mShakeDirection = shakeDirection;
		mShakeEachChange = 0.3f;
	}
	
	@Override
	public void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		curAnimatePercent = caculateAnimatePercent(curAnimatePercent);
		boolean isDoingShake = false;
		if (curAnimatePercent == 1) { // 做抖动动画
			if (mIsAnimateFinish && mEndCenterPoint != null) {
				if (mShakeDirection == SHAKE_DIRECTION_UP) {
					if (getCenterY() <= mEndCenterPoint.y + mShakeMaxDistance) {
						setCenter(getCenterX(), getCenterY() + mShakeEachChange);
					} else {
						mShakeDirection = SHAKE_DIRECTION_DOWN;
					}
				} else if (mShakeDirection == SHAKE_DIRECTION_DOWN) {
					if (getCenterY() >= mEndCenterPoint.y - mShakeMaxDistance) {
						setCenter(getCenterX(), getCenterY() - mShakeEachChange);
					} else {
						mShakeDirection = SHAKE_DIRECTION_UP;
					}
				}
				isDoingShake = true;
			} else {
				mIsAnimateFinish = true;
			}
		} else {
			mIsAnimateFinish = false;
		}
		if (!isDoingShake) {
			float centerX = getCenterX();
			float centerY = getCenterY();
			if (mStartCenterPoint != null && mEndCenterPoint != null) {
				centerX = mStartCenterPoint.x + (mEndCenterPoint.x - mStartCenterPoint.x)
						* curAnimatePercent;
				centerY = mStartCenterPoint.y + (mEndCenterPoint.y - mStartCenterPoint.y)
						* curAnimatePercent;
			}
			setCenter(centerX, centerY);
			setAlpha((int) (MAX_ALPHA * curAnimatePercent));
			setScale(curAnimatePercent, curAnimatePercent, SCALE_CENTER);
		}
	}
	
	public void setMovePoint(Point startCenterPoint, Point endCenterPoint) {
		mStartCenterPoint = startCenterPoint;
		mEndCenterPoint = endCenterPoint;
	}

}
