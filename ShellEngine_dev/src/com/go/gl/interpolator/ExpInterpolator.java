package com.go.gl.interpolator;

import android.view.animation.Interpolator;

/**
 * 
 * <br>类描述:指数函数插值
 * <br>功能详细描述:
 * 
 * @author  mengsifan
 * @date  [2013-12-27]
 */
public class ExpInterpolator implements Interpolator {

	private static final float MAX_BASE = 0.001f;
	private static final float MIN_BASE = 0.000001f;

	/** 减速插值 */
	public static final int DECELERATE = 0;
	/** 加速插值 */
	public static final int ACCELERATE = 1;

	private float mBase = MAX_BASE;
	private float mApproachingEffect = 0;

	private int mVelocityMode = DECELERATE;

	/**
	 * 默认减速插值
	 * @param e 0 < e < 100，e越大，加速越快，逼近效果越好（但可能需要更长的动画时间）
	 */
	public ExpInterpolator(int e) {
		this(e, DECELERATE);
	}

	/**
	 * @param e 0 < e < 100，e越大，加速越快，逼近效果越好（但可能需要更长的动画时间）
	 * @param mode 速度的变化模式，有{@link DECELERATE}和{@link ACCELERATE}两种
	 */
	public ExpInterpolator(int e, int mode) {
		setApproachingEffect(e);
		setVelocityMode(mode);
	}

	@Override
	public float getInterpolation(float input) {
		if (input <= 0) {
			return 0;
		}
		if (input >= 1) {
			return 1;
		}
		switch (mVelocityMode) {
			case DECELERATE :
				return (float) (1 - Math.pow(mBase, input));
			case ACCELERATE :
				return (float) Math.pow(1 / mBase, input - 1);
		}
		return input;
	}

	public float getApproachingEffect() {
		return mApproachingEffect;
	}

	public void setApproachingEffect(int e) {
		this.mApproachingEffect = Math.min(100, Math.max(0, e));
		mBase = MIN_BASE + (MAX_BASE - MIN_BASE) / 100.0f * (100 - e);
	}

	public int getVelocityMode() {
		return mVelocityMode;
	}

	public void setVelocityMode(int mVelocityMode) {
		this.mVelocityMode = mVelocityMode;
	}
}