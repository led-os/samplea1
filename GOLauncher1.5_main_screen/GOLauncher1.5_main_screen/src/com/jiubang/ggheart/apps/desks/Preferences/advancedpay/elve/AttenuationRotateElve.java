package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve;

import android.graphics.drawable.Drawable;

/**
 * 衰减性旋转精灵，由系统时间进行控制
 * @author yejijiong
 *
 */
public class AttenuationRotateElve extends ComprehensiveElve {
	private float mCurrRotateAngle;  //当前位置
	private boolean mMoving = false;
	private static final long DURATION = 350; //最高点与最高点间做一次运动的时间
	public float mOffset; // 当前的偏移
	private float mMaxAngle = 0; // 允许的最大振幅        
	private float mMinAngle = 0f; // 最小振幅
	private long mDuration = DURATION; // 一个周期动画的时间            
	private long mLastGravityUpdateTime; //记录上次计算时间
	private float mAngleBeforeShake; // 摇摆前的角度
	private float mAngleAttenuation = 0.8f; //摆动一次角度衰减度
	private float mTimeAttenuation = 1f; //摆动一次周期时间衰减度
	private boolean mIsFlyback; //小于一定速度后的回归动画
	private float mAccele; // 加速度
	private float mMaxV; // 初始速度
	private float mOriginalRotateAngle;

	public AttenuationRotateElve(Drawable drawable, float startAnimateBase, float endAnimateBase) {
		super(drawable, startAnimateBase, endAnimateBase);
	}

	/**
	 * 设置衰减旋转参数
	 */
	public void setAttenuationRotateValue(float originalRotateAngle, float finalRotateAngle,
			int rotatType, long duration, float minAngle, float angleAttenuation, float timeAttenuation) {
		mRotateType = rotatType;
		mOriginalRotateAngle = originalRotateAngle;
		setRotate(mOriginalRotateAngle, mRotateType);
		mMoving = false;
		mDuration = duration;
		mMinAngle = minAngle;
		mAngleAttenuation = angleAttenuation;
		mTimeAttenuation = timeAttenuation;
		handShake(originalRotateAngle, finalRotateAngle);
	}

	@Override
	public void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		onHarmonic();
		setRotate(mCurrRotateAngle, mRotateType);
	}

	/**
	 * 震动前的准备工作
	 */
	public void handShake(float originalRotateAngle, float finalRotateAngle) {
		if (!mMoving) {
			mMoving = true;
		} else {
			return;
		}
		if (mMoving) {
			mMaxAngle = -(int) (originalRotateAngle - finalRotateAngle);
//			mDuration = DURATION;
			mOffset = originalRotateAngle;
			final float deffAngel = mMaxAngle - mOffset;
			mAngleBeforeShake = mOffset;
			int t = (int) (mDuration / 2);
			mAccele = (deffAngel + 0.1f) / (t * t);
			mMaxV = mAccele * mDuration * 0.5f; // 最大速度
			mIsFlyback = false;
			mLastGravityUpdateTime = System.currentTimeMillis();
		}
	}

	/**
	 * 左右震动
	 */
	private void onHarmonic() {
		if (!mMoving) {
			return;
		}
		long time = System.currentTimeMillis() - mLastGravityUpdateTime;
		if (mIsFlyback) {        //角度小于一定值，先加速后减速回到起点
			if (time <= mDuration) {
				float angle = 0;
				float half = 0;
				if (time <= mDuration / 2) { // 小于半个周期做加速运动
					angle = mAccele * time * time / 2;
				} else { // 大于半个周期先加速然后减速
					long t = mDuration / 2;
					half = mAccele * t * t / 2;
					time = time - t;
					angle = mMaxV * time - mAccele * time * time / 2;
				}
				mOffset = mAngleBeforeShake + half + angle;
			} else {
				mMoving = false;
				mOffset = 0;
			}
		} else {
			if (time < mDuration) {
				float angle = 0;
				float half = 0;
				if (time <= mDuration / 2) { // 小于半个周期做加速运动
					angle = mAccele * time * time / 2;
				} else { // 大于半个周期先加速然后减速
					long t = mDuration / 2;
					half = mAccele * t * t / 2;
					time = time - t;
					angle = mMaxV * time - mAccele * time * time / 2;
				}
				mOffset = mAngleBeforeShake + half + angle;
			} else {    // 大于一个周期
				mDuration *= mTimeAttenuation;
				mAngleBeforeShake = mOffset;
				mLastGravityUpdateTime = System.currentTimeMillis();
				if (Math.abs(mMaxAngle) < mMinAngle) {  //速度小于一定量后就开始做恢复到起始点的动画
					long t = mDuration / 2;
					mAccele = -mMaxAngle / (t * t);
					mMaxV = mAccele * t;
					mIsFlyback = true;
					return;
				} else {
					mMaxAngle = -mMaxAngle * mAngleAttenuation; // 最大角度反向
					final float deffAngel = mMaxAngle - mOffset;
					long t = mDuration / 2;
					mAccele = (deffAngel + 0.1f) / (t * t);
					mMaxV = mAccele * t;
				}
			}
		}
		mCurrRotateAngle = mOffset;
	}
}
