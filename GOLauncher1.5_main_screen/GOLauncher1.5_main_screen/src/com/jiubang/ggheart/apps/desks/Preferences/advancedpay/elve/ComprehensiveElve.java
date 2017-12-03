package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.ElveAlphaAnimateInfo;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.ElveMoveAnimateInfo;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.ElveRotateAnimateInfo;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.ElveScaleAnimateInfo;

/**
 * 综合精灵，内含透明、放缩、旋转、位移动画等功能
 * @author yejijiong
 *
 */
public class ComprehensiveElve extends Elve {
	private List<ElveMoveAnimateInfo> mMoveAnimateList;
	private int mCurMoveAnimateIndex = 0;
	private List<ElveScaleAnimateInfo> mScaleAnimateList;
	private int mCurScaleAnimateIndex = 0;
	private List<ElveRotateAnimateInfo> mRotateAnimateList;
	private int mCurRotateAnimateIndex = 0;
	private List<ElveAlphaAnimateInfo> mAlphaAnimateList;
	private int mCurAlphaAnimateIndex = 0;
	
	public ComprehensiveElve(Drawable drawable, float startAnimateBase, float endAnimateBase) {
		super(drawable, startAnimateBase, endAnimateBase);
	}

	@Override
	public void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		curAnimatePercent = caculateAnimatePercent(curAnimatePercent);
		if (curAnimatePercent == 0) {
			resetAnimateIndex();
		}
		
		if (mMoveAnimateList != null
				&& mCurMoveAnimateIndex < mMoveAnimateList.size()) {
			ElveMoveAnimateInfo info = mMoveAnimateList
					.get(mCurMoveAnimateIndex);
			if (info.mStartMoveBase != info.mEndMoveBase) {
				if (curAnimatePercent < info.mStartMoveBase) {
					setCenter(info.mOriginalCenterX, info.mOriginalCenterY);
				} else if (curAnimatePercent >= info.mStartMoveBase
						&& curAnimatePercent <= info.mEndMoveBase) {
					float percent = (curAnimatePercent - info.mStartMoveBase)
							/ (info.mEndMoveBase - info.mStartMoveBase);
					float centerX = info.mOriginalCenterX
							+ (info.mTargetCenterX - info.mOriginalCenterX)
							* percent;
					float centerY = info.mOriginalCenterY
							+ (info.mTargetCenterY - info.mOriginalCenterY)
							* percent;
					setCenter(centerX, centerY);
				} else {
					setCenter(info.mTargetCenterX, info.mTargetCenterY);
					mCurMoveAnimateIndex++;
				}
			} else {
				setCenter(info.mOriginalCenterX, info.mOriginalCenterY);
			}
		}
		
		if (mScaleAnimateList != null
				&& mCurScaleAnimateIndex < mScaleAnimateList.size()) {
			ElveScaleAnimateInfo info = mScaleAnimateList
					.get(mCurScaleAnimateIndex);
			if (info.mStartScaleBase != info.mEndScaleBase) {
				if (curAnimatePercent < info.mStartScaleBase) {
					setScale(info.mStartScaleXValue, info.mStartScaleYValue, info.mScaleType);
				} else if (curAnimatePercent >= info.mStartScaleBase
						&& curAnimatePercent <= info.mEndScaleBase) {
					float percent = (curAnimatePercent - info.mStartScaleBase) / (info.mEndScaleBase - info.mStartScaleBase);
					float scaleX = info.mStartScaleXValue + (info.mEndScaleXValue - info.mStartScaleXValue) * percent;
					float scaleY = info.mStartScaleYValue + (info.mEndScaleYValue - info.mStartScaleYValue) * percent;
					setScale(scaleX, scaleY, info.mScaleType);
				} else {
					setScale(info.mEndScaleXValue, info.mEndScaleYValue, info.mScaleType);
					mCurScaleAnimateIndex++;
				}
			} else {
				setScale(info.mStartScaleXValue, info.mStartScaleYValue, info.mScaleType);
			}
		}
		
		if (mAlphaAnimateList != null
				&& mCurAlphaAnimateIndex < mAlphaAnimateList.size()) {
			ElveAlphaAnimateInfo info = mAlphaAnimateList
					.get(mCurAlphaAnimateIndex);
			if (info.mStartAlphaBase != info.mEndAlphaBase) {
				if (curAnimatePercent < info.mStartAlphaBase) {
					setAlpha(info.mStartAlphaValue);
				} else if (curAnimatePercent >= info.mStartAlphaBase
						&& curAnimatePercent <= info.mEndAlphaBase) {
					float percent = (curAnimatePercent - info.mStartAlphaBase) / (info.mEndAlphaBase - info.mStartAlphaBase);
					int alpha = (int) (info.mStartAlphaValue + (info.mEndAlphaValue - info.mStartAlphaValue) * percent);
					setAlpha(alpha);
				} else {
					setAlpha(info.mEndAlphaValue);
					mCurAlphaAnimateIndex++;
				}
			} else {
				setAlpha(info.mStartAlphaValue);
			}
		}
		
		if (mRotateAnimateList != null
				&& mCurRotateAnimateIndex < mRotateAnimateList.size()) {
			ElveRotateAnimateInfo info = mRotateAnimateList
					.get(mCurRotateAnimateIndex);
			if (info.mStartRotateBase != info.mEndRotateBase) {
				if (curAnimatePercent < info.mStartRotateBase) {
					setRotate(info.mStartRotateAngle, info.mRotateType);
				} else if (curAnimatePercent >= info.mStartRotateBase
						&& curAnimatePercent <= info.mEndRotateBase) {
					float percent = (curAnimatePercent - info.mStartRotateBase) / (info.mEndRotateBase - info.mStartRotateBase);
					float angle = info.mStartRotateAngle + (info.mEndRotateAngle - info.mStartRotateAngle) * percent;
					setRotate(angle, info.mRotateType);
				} else {
					setRotate(info.mEndRotateAngle, info.mRotateType);
					mCurRotateAnimateIndex++;
				}
			} else {
				setRotate(info.mStartRotateAngle, info.mRotateType);
			}
		}
	}
	
	private void resetAnimateIndex() {
		mCurMoveAnimateIndex = 0;
		mCurScaleAnimateIndex = 0;
		mCurRotateAnimateIndex = 0;
		mCurAlphaAnimateIndex = 0;
	}
	
	/**
	 * 如果在layout中调用addAnimate的方法，一定要先调用这个清除之前的动画，否则会跳帧
	 */
	public void clearAllMoveAnimate() {
		if (mMoveAnimateList != null) {
			mMoveAnimateList.clear();
		}
	}
	
	public void addMoveAnimate(float originalCenterX, float originalCenterY, float targetCenterX,
			float targetCenterY, float startMoveBase, float endMoveBase) {
		if (mMoveAnimateList == null) {
			mMoveAnimateList = new ArrayList<ElveMoveAnimateInfo>();
			setCenter(originalCenterX, originalCenterY);
		}
		ElveMoveAnimateInfo info = new ElveMoveAnimateInfo();
		info.mOriginalCenterX = originalCenterX;
		info.mOriginalCenterY = originalCenterY;
		info.mTargetCenterX = targetCenterX;
		info.mTargetCenterY = targetCenterY;
		info.mStartMoveBase = startMoveBase;
		info.mEndMoveBase = endMoveBase;
		mMoveAnimateList.add(info);
	}
	
	public void addScaleAnimate(float startScaleValue, float endScaleValue, int scaleType,
			float startScaleBase, float endScaleBase) {
		addScaleAnimate(startScaleValue, startScaleValue, endScaleValue, endScaleValue, scaleType,
				startScaleBase, endScaleBase);
	}
	
	public void addScaleAnimate(float startScaleXValue, float startScaleYValue,
			float endScaleXValue, float endScaleYValue, int scaleType, float startScaleBase,
			float endScaleBase) {
		if (mScaleAnimateList == null) {
			mScaleAnimateList = new ArrayList<ElveScaleAnimateInfo>();
			setScale(startScaleXValue, startScaleYValue, scaleType);
		}
		ElveScaleAnimateInfo info = new ElveScaleAnimateInfo();
		info.mStartScaleXValue = startScaleXValue;
		info.mStartScaleYValue = startScaleYValue;
		info.mEndScaleXValue = endScaleXValue;
		info.mEndScaleYValue = endScaleYValue;
		info.mScaleType = scaleType;
		info.mStartScaleBase = startScaleBase;
		info.mEndScaleBase = endScaleBase;
		mScaleAnimateList.add(info);
	}
	
	
	public void addAlphaValue(int startAlphaValue, int endAlphaValue, float startAlphaBase, float endAlphaBase) {
		if (mAlphaAnimateList == null) {
			mAlphaAnimateList = new ArrayList<ElveAlphaAnimateInfo>();
			setAlpha(startAlphaValue);
		}
		ElveAlphaAnimateInfo info = new ElveAlphaAnimateInfo();
		info.mStartAlphaValue = startAlphaValue;
		info.mEndAlphaValue = endAlphaValue;
		info.mStartAlphaBase = startAlphaBase;
		info.mEndAlphaBase = endAlphaBase;
		mAlphaAnimateList.add(info);
	}
	
	public void addRotateAnimate(float startRotateAngle, float endRotateAngle, int rotateType, float startRotateBase,
			float endRotateBase) {
		if (mRotateAnimateList == null) {
			mRotateAnimateList = new ArrayList<ElveRotateAnimateInfo>();
			setRotate(startRotateAngle, rotateType);
		}
		ElveRotateAnimateInfo info = new ElveRotateAnimateInfo();
		info.mStartRotateAngle = startRotateAngle;
		info.mEndRotateAngle = endRotateAngle;
		info.mRotateType = rotateType;
		info.mStartRotateBase = startRotateBase;
		info.mEndRotateBase = endRotateBase;
		mRotateAnimateList.add(info);
	}
}
