package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.ComprehensiveElve;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.Elve;

/**
 * 壁纸滤镜广告View
 * @author yejijiong
 *
 */
public class AdvancedPayWallpaperFilterView extends AdvancedPayBaseView {
	
	private ComprehensiveElve mFilterElveOriginal;
	private ComprehensiveElve mFilterElveOne;
	private ComprehensiveElve mFilterElveTwo;
	private ComprehensiveElve mFilterElveThree;
	private ComprehensiveElve mFilterElveFour;
	private int mOriginalElveStartCenterY;
	private int mOriginalElveTargetCenterY;
	private float mOriginalElveStartBase;
	private float mOriginalElveEndBase;
	private int mFilterOneTargetCenterY;
	private int mFilterOneTargetPaddingCenterX;
	private int mFilterTwoTargetCenterY;
	private int mFilterTwoTargetPaddingCenterX;
	private float mFilterElveMoveStartBase;
	private float mFilterElveMoveEndBase;
	private float mFilterElveRotateStartBaseOne;
	private float mFilterElveRotateEndBaseOne;
	private float mFilterElveRotateStartBaseTwo;
	private float mFilterElveRotateEndBaseTwo;
	private float mFilterElveRotateStartBaseThree;
	private float mFilterElveRotateEndBaseThree;
	private float mFilterElveRotateStartBaseFour;
	private float mFilterElveRotateEndBaseFour;
	private float mFilterElveRotateValueOne;
	private float mFilterElveRotateValueTwo;
	private float mFilterElveRotateValueThree;

	public AdvancedPayWallpaperFilterView(Context context, int animateFinishType,
			IAdvancedPayListener listener) {
		super(context, animateFinishType, listener);
	}

	@Override
	protected void init() {
		super.init();
		mOriginalElveStartCenterY = DrawUtils.dip2px(-35);
		mOriginalElveTargetCenterY = DrawUtils.dip2px(65);
		mOriginalElveStartBase = 0.1f;
		mOriginalElveEndBase = 0.4f;
		mFilterOneTargetCenterY = DrawUtils.dip2px(3);
		mFilterOneTargetPaddingCenterX = DrawUtils.dip2px(90);
		mFilterTwoTargetCenterY = DrawUtils.dip2px(-57);
		mFilterTwoTargetPaddingCenterX = DrawUtils.dip2px(40);
		mFilterElveMoveStartBase = 0.3f;
		mFilterElveMoveEndBase = 0.7f;
		mFilterElveRotateStartBaseOne = 0.7f;
		mFilterElveRotateEndBaseOne = 0.8f;
		mFilterElveRotateStartBaseTwo = 0.8f;
		mFilterElveRotateEndBaseTwo = 0.87f;
		mFilterElveRotateStartBaseThree = 0.87f;
		mFilterElveRotateEndBaseThree = 0.92f;
		mFilterElveRotateStartBaseFour = 0.92f;
		mFilterElveRotateEndBaseFour = 0.95f;
		mFilterElveRotateValueOne = -20f;
		mFilterElveRotateValueTwo = 15f;
		mFilterElveRotateValueThree = -10f;
		mAnimateDuration = 1200;
	}

	@Override
	protected void initElve() {
		Resources resource = mContext.getResources();
		mFilterElveOriginal = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_wallpaper_filter_original), 0f, 1.0f);
		mFilterElveOriginal.addScaleAnimate(1.0f, 0.5f, Elve.SCALE_CENTER, mOriginalElveStartBase,
				mOriginalElveEndBase);
		mFilterElveOriginal.addAlphaValue(Elve.MAX_ALPHA, 153, mOriginalElveStartBase,
				mOriginalElveEndBase);
		addElve(mFilterElveOriginal);
		mFilterElveOne = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_wallpaper_filter_one), 0f, 1.0f);
		mFilterElveOne.addScaleAnimate(0f, 1.0f, Elve.SCALE_CENTER, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveOne.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveOne.addRotateAnimate(0f, mFilterElveRotateValueOne, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseOne, mFilterElveRotateEndBaseOne);
		mFilterElveOne.addRotateAnimate(mFilterElveRotateValueOne, mFilterElveRotateValueTwo,
				Elve.ROTATE_CENTER, mFilterElveRotateStartBaseTwo, mFilterElveRotateEndBaseTwo);
		mFilterElveOne.addRotateAnimate(mFilterElveRotateValueTwo, mFilterElveRotateValueThree,
				Elve.ROTATE_CENTER, mFilterElveRotateStartBaseThree, mFilterElveRotateEndBaseThree);
		mFilterElveOne.addRotateAnimate(mFilterElveRotateValueThree, 0f, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseFour, mFilterElveRotateEndBaseFour);
		addElve(mFilterElveOne);
		mFilterElveTwo = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_wallpaper_filter_two), 0f, 1.0f);
		mFilterElveTwo.addScaleAnimate(0f, 1.0f, Elve.SCALE_CENTER, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveTwo.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveTwo.addRotateAnimate(0f, mFilterElveRotateValueOne, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseOne, mFilterElveRotateEndBaseOne);
		mFilterElveTwo.addRotateAnimate(mFilterElveRotateValueOne, 0f, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseTwo, mFilterElveRotateEndBaseTwo);
		mFilterElveTwo.addRotateAnimate(mFilterElveRotateValueTwo, mFilterElveRotateValueThree,
				Elve.ROTATE_CENTER, mFilterElveRotateStartBaseThree, mFilterElveRotateEndBaseThree);
		mFilterElveTwo.addRotateAnimate(mFilterElveRotateValueThree, 0f, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseFour, mFilterElveRotateEndBaseFour);
		addElve(mFilterElveTwo);
		mFilterElveThree = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_wallpaper_filter_three), 0f, 1.0f);
		mFilterElveThree.addScaleAnimate(0f, 1.0f, Elve.SCALE_CENTER, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveThree.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveThree.addRotateAnimate(0f, mFilterElveRotateValueOne, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseOne, mFilterElveRotateEndBaseOne);
		mFilterElveThree.addRotateAnimate(mFilterElveRotateValueOne, 0f, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseTwo, mFilterElveRotateEndBaseTwo);
		mFilterElveThree.addRotateAnimate(mFilterElveRotateValueTwo, mFilterElveRotateValueThree,
				Elve.ROTATE_CENTER, mFilterElveRotateStartBaseThree, mFilterElveRotateEndBaseThree);
		mFilterElveThree.addRotateAnimate(mFilterElveRotateValueThree, 0f, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseFour, mFilterElveRotateEndBaseFour);
		addElve(mFilterElveThree);
		mFilterElveFour = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_wallpaper_filter_four), 0f, 1.0f);
		mFilterElveFour.addScaleAnimate(0f, 1.0f, Elve.SCALE_CENTER, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveFour.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, mFilterElveMoveStartBase,
				mFilterElveMoveEndBase);
		mFilterElveFour.addRotateAnimate(0f, mFilterElveRotateValueOne, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseOne, mFilterElveRotateEndBaseOne);
		mFilterElveFour.addRotateAnimate(mFilterElveRotateValueOne, 0f, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseTwo, mFilterElveRotateEndBaseTwo);
		mFilterElveFour.addRotateAnimate(mFilterElveRotateValueTwo, mFilterElveRotateValueThree,
				Elve.ROTATE_CENTER, mFilterElveRotateStartBaseThree, mFilterElveRotateEndBaseThree);
		mFilterElveFour.addRotateAnimate(mFilterElveRotateValueThree, 0f, Elve.ROTATE_CENTER,
				mFilterElveRotateStartBaseFour, mFilterElveRotateEndBaseFour);
		addElve(mFilterElveFour);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mIsInit) {
			float centerX = (right - left) / 2.0f;
			float centerY = (bottom - top) / 2.0f;
			mFilterElveOriginal.addMoveAnimate(centerX, centerY + mOriginalElveStartCenterY, centerX,
					centerY + mOriginalElveTargetCenterY, mOriginalElveStartBase, mOriginalElveEndBase);
			mFilterElveOne.addMoveAnimate(centerX, centerY + mOriginalElveTargetCenterY, centerX
					- mFilterOneTargetPaddingCenterX, centerY + mFilterOneTargetCenterY,
					mFilterElveMoveStartBase, mFilterElveMoveEndBase);
			mFilterElveTwo.addMoveAnimate(centerX, centerY + mOriginalElveTargetCenterY, centerX
					- mFilterTwoTargetPaddingCenterX, centerY + mFilterTwoTargetCenterY,
					mFilterElveMoveStartBase, mFilterElveMoveEndBase);
			mFilterElveThree.addMoveAnimate(centerX, centerY + mOriginalElveTargetCenterY, centerX
					+ mFilterOneTargetPaddingCenterX, centerY + mFilterOneTargetCenterY,
					mFilterElveMoveStartBase, mFilterElveMoveEndBase);
			mFilterElveFour.addMoveAnimate(centerX, centerY + mOriginalElveTargetCenterY, centerX
					+ mFilterTwoTargetPaddingCenterX, centerY + mFilterTwoTargetCenterY,
					mFilterElveMoveStartBase, mFilterElveMoveEndBase);
		}
	}

	@Override
	public String getMessageTip() {
		return mContext.getString(R.string.desksetting_pay_dialog_message_tip_wallpaper_filter);
	}

	@Override
	public String getMessageSummary() {
		return mContext.getString(R.string.desksetting_pay_dialog_message_summary_wallpaper_filter);
	}

	@Override
	public int getBgColor() {
		return Color.parseColor("#FFF57364");
	}
	
	@Override
	public void setEnterAlpha(int alpha) {
		if (mIsInit) {
			mFilterElveOriginal.setAlpha(alpha);
		}
	}
	
	@Override
	public void setEnterScale(float scale) {
		if (mIsInit) {
			mFilterElveOriginal.setScale(scale, scale, Elve.SCALE_CENTER);
		}
	}
}
