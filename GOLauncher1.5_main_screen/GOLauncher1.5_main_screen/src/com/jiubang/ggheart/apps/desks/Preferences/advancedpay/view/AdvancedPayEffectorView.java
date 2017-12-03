package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;
import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.ComprehensiveElve;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.Elve;

/**
 * 付费预览特效界面主View
 * @author yejijiong
 *
 */
public class AdvancedPayEffectorView extends AdvancedPayBaseView {

	private ComprehensiveElve mIconAndBgElveOne;
	private ComprehensiveElve mIconAndBgElveTwo;
	private ComprehensiveElve mBgElve;
	private ComprehensiveElve mIconElve;
	private ComprehensiveElve mDockElve;
	private float mIconAndBgElveMoveStartBase;
	private float mIconAndBgElveMoveEndBase;
	private float mIconAndBgElveScaleStartBase;
	private float mIconAndBgElveScaleEndBase;
	private float mBgElveScaleStartBase;
	private float mBgElveScaleEndBase;
	private float mIconElveScaleStartBase;
	private float miconElveScaleEndBase;

	public AdvancedPayEffectorView(Context context, int animateFinishType,
			IAdvancedPayListener listener) {
		super(context, animateFinishType, listener);
	}

	@Override
	protected void init() {
		mAnimateDuration = 2000; // 延长动画时间
		mIconAndBgElveMoveStartBase = 0.1f;
		mIconAndBgElveMoveEndBase = 0.4f;
		mIconAndBgElveScaleStartBase = 0.4f;
		mIconAndBgElveScaleEndBase = 0.6f;
		mBgElveScaleStartBase = 0.6f;
		mBgElveScaleEndBase = 0.8f;
		mIconElveScaleStartBase = 0.8f;
		miconElveScaleEndBase = 1.0f;
	}

	@Override
	protected void initElve() {
		Resources resource = mContext.getResources();
		mIconAndBgElveOne = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_effector_icon_and_bg), 0f, 1.0f);
		mIconAndBgElveOne.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA,
				mIconAndBgElveMoveStartBase, mIconAndBgElveMoveEndBase);
		mIconAndBgElveOne.addScaleAnimate(1.0f, 1.5f, Elve.SCALE_CENTER,
				mIconAndBgElveMoveStartBase, mIconAndBgElveMoveEndBase);		
		addElve(mIconAndBgElveOne);
		
		mIconAndBgElveTwo = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_effector_icon_and_bg), 0f, 1.0f);
		mIconAndBgElveTwo.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA,
				mIconAndBgElveMoveStartBase, mIconAndBgElveMoveEndBase);
		mIconAndBgElveTwo.addScaleAnimate(0.7f, 1.0f, Elve.SCALE_CENTER,
				mIconAndBgElveMoveStartBase, mIconAndBgElveMoveEndBase);
		mIconAndBgElveTwo.addScaleAnimate(1.0f, 1.0f, 0f, 1.0f, Elve.SCALE_CENTER,
				mIconAndBgElveScaleStartBase, mIconAndBgElveScaleEndBase);
		addElve(mIconAndBgElveTwo);
		
		mBgElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_effector_bg),
				0f, 1.0f);
		mBgElve.addScaleAnimate(0f, 1.0f, 1.0f, 1.0f, Elve.SCALE_CENTER, mBgElveScaleStartBase,
				mBgElveScaleEndBase);
		addElve(mBgElve);
		
		mIconElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_effector_icon),
				0f, 1.0f);
		mIconElve.addScaleAnimate(1.5f, 1.0f, Elve.SCALE_BOTTOM_MIDDLE, mIconElveScaleStartBase,
				miconElveScaleEndBase);
		mIconElve.addScaleAnimate(1.5f, 1.0f, Elve.SCALE_BOTTOM_MIDDLE, mIconElveScaleStartBase,
				miconElveScaleEndBase);
		mIconElve.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, mIconElveScaleStartBase,
				miconElveScaleEndBase);
		addElve(mIconElve);
		
		mDockElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_effector_dock),
				0f, 1.0f);
		mDockElve.addScaleAnimate(1.5f, 1.0f, Elve.SCALE_TOP_MIDDEL, mIconElveScaleStartBase,
				miconElveScaleEndBase);
		mDockElve.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, mIconElveScaleStartBase,
				miconElveScaleEndBase);
		addElve(mDockElve);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mIsInit) {
			float centerX = (right - left) / 2.0f;
			float centerY = (bottom - top) / 2.0f;
			int distanced = DrawUtils.dip2px(50);
			mIconAndBgElveOne.clearAllMoveAnimate();
			mIconAndBgElveOne.addMoveAnimate(centerX, centerY, centerX - distanced, centerY,
					mIconAndBgElveMoveStartBase, mIconAndBgElveMoveEndBase);
			mIconAndBgElveTwo.clearAllMoveAnimate();
			mIconAndBgElveTwo.addMoveAnimate(centerX + distanced, centerY, centerX, centerY,
					mIconAndBgElveMoveStartBase, mIconAndBgElveMoveEndBase);

			mBgElve.setCenter(centerX, centerY);
			mIconElve.setCenter(centerX, centerY);
			mDockElve.setCenter(centerX, centerY);
		}
	}

	@Override
	public String getMessageTip() {
		return mContext.getString(R.string.desksetting_pay_dialog_message_tip3);
	}

	@Override
	public String getMessageSummary() {
		return mContext.getString(R.string.desksetting_pay_dialog_message_summary3);
	}
	
	@Override
	public int getBgColor() {
		return Color.parseColor("#FFF39C12");
	}
	
	public void setEnterAlpha(int alpha) {
		if (mIsInit) {
			mIconAndBgElveOne.setAlpha(alpha);
		}
	}
	
	@Override
	public void setEnterScale(float scale) {
		if (mIsInit) {
			mIconAndBgElveOne.setScale(scale, scale, Elve.SCALE_CENTER);
		}
	}
}
