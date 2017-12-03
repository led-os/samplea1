package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.ComprehensiveElve;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.Elve;

/**
 * 安全锁广告预览展示主View
 * @author yejijiong
 *
 */
public class AdvancedPaySafetyLockView extends AdvancedPayBaseView {
	
	private Elve mAppIconsElve;
	private ComprehensiveElve mWhiteBgElve;
	private ComprehensiveElve mNumberElve;
	private ComprehensiveElve mNumberGreenElve;
	private ComprehensiveElve mLocksElve;
	
	public AdvancedPaySafetyLockView(Context context, int animateFinishType, IAdvancedPayListener listener) {
		super(context, animateFinishType, listener);
	}
	
	@Override
	protected void init() {
		mAnimateDuration = 2000;
	}
	
	@Override
	protected void initElve() {
		Resources resource = mContext.getResources();
		mAppIconsElve = new Elve(
				resource.getDrawable(R.drawable.advanced_pay_safety_lock_app_icons), 1.0f, 1.0f);
		addElve(mAppIconsElve);
		mWhiteBgElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_safety_lock_white_bg), 0f, 1.0f);
		mWhiteBgElve.addScaleAnimate(0f, 1.0f, Elve.SCALE_CENTER, 0.1f, 0.3f);
		addElve(mWhiteBgElve);
		mNumberElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_safety_lock_number), 0f, 1.0f);
		mNumberElve.addScaleAnimate(0f, 1.0f, Elve.SCALE_CENTER, 0.1f, 0.3f);
		mNumberElve.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, 0.1f, 0.3f);
		mNumberElve.addScaleAnimate(1.0f, 0f, Elve.SCALE_CENTER, 0.65f, 0.85f);
		mNumberElve.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.65f, 0.85f);
		addElve(mNumberElve);
		mNumberGreenElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_safety_lock_number_green), 0f, 1.0f);
		mNumberGreenElve.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, 0.3f, 0.5f);
		mNumberGreenElve.addScaleAnimate(1.0f, 0f, Elve.SCALE_CENTER, 0.65f, 0.85f);
		mNumberGreenElve.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.65f, 0.85f);
		addElve(mNumberGreenElve);
		mLocksElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_safety_lock_locks), 0f, 1.0f);
		mLocksElve.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, 0.8f, 1.0f);
		addElve(mLocksElve);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mIsInit) {
			mAppIconsElve.setCenter((right - left) / 2, (bottom - top) / 2);
			mWhiteBgElve.setCenter(mAppIconsElve.getCenterX(), mAppIconsElve.getCenterY());
			mNumberElve.setCenter(mAppIconsElve.getCenterX(), mAppIconsElve.getCenterY());
			mNumberGreenElve.setCenter(mAppIconsElve.getCenterX(), mAppIconsElve.getCenterY());
			mLocksElve.setCenter(mAppIconsElve.getCenterX(), mAppIconsElve.getCenterY());
		}
	}
	
	@Override
	public String getMessageTip() {
		return mContext.getResources().getString(R.string.desksetting_pay_dialog_message_tip4);
	}

	@Override
	public String getMessageSummary() {
		return mContext.getResources().getString(R.string.desksetting_pay_dialog_message_summary4);
	}
	
	@Override
	public int getBgColor() {
		return Color.parseColor("#FF3FA2E1");
	}
	
	@Override
	public void setEnterAlpha(int alpha) {
		if (mIsInit) {
			mAppIconsElve.setAlpha(alpha);
		}
	}
	
	@Override
	public void setEnterScale(float scale) {
		if (mIsInit) {
			mAppIconsElve.setScale(scale, scale, Elve.SCALE_CENTER);
		}
	}
}
