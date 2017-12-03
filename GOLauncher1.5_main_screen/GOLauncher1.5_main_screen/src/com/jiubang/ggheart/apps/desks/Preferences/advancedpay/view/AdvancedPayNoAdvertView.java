package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.ComprehensiveElve;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.Elve;

/**
 * 无广告展示预览主View
 * @author yejijiong
 *
 */
public class AdvancedPayNoAdvertView extends AdvancedPayBaseView {
	
	private Elve mPhoneElve;
	private ComprehensiveElve mDiamondElve;
	private ComprehensiveElve mEdgeElve;
	private ComprehensiveElve mTextElveOne;
	private ComprehensiveElve mTextElveTwo;
	private ComprehensiveElve mTextElveThree;
	private ComprehensiveElve mTextElveFour;
	private ComprehensiveElve mTextElveFive;
	private ComprehensiveElve mTextElveSix;
	private ComprehensiveElve mTextElveSeven;
	private ComprehensiveElve mTextElveEight;
	private Paint mPaint;
	private Canvas mTempCanvas;
	private Bitmap mTempBitmap;
	private Paint mTempPaint;
	private Bitmap mMaskBitmap;
	private int mDiamondElveStartCenterY;
	private int mDiamondElveSEndCenterY;
	
	public AdvancedPayNoAdvertView(Context context, int animateFinishType, IAdvancedPayListener listener) {
		super(context, animateFinishType, listener);
	}
	
	@Override
	protected void init() {
		mMaskBitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.advanced_pay_phone_mask)).getBitmap();
		PorterDuffXfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
		mTempCanvas = new Canvas();
		mTempBitmap = Bitmap.createBitmap(mMaskBitmap.getWidth(), mMaskBitmap.getHeight(),
				Config.ARGB_8888);
		mTempCanvas.setBitmap(mTempBitmap);
		mTempPaint = new Paint();
		mTempPaint.setXfermode(xFermode);
		mTempPaint.setColor(Color.BLACK);
		mTempPaint.setAntiAlias(true);  
		mTempPaint.setFilterBitmap(true);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mDiamondElveStartCenterY = DrawUtils.dip2px(-50);
		mDiamondElveSEndCenterY = DrawUtils.dip2px(80);
		mAnimateDuration = 1500;
	}
	
	@Override
	protected void initElve() {
		Resources resource = mContext.getResources();
		mPhoneElve = new Elve(
				resource.getDrawable(R.drawable.advanced_pay_phone),
				1.0f, 1.0f);
		addElve(mPhoneElve);
		mDiamondElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_diamond),
				0f, 1.0f);
		mEdgeElve = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_edge),
				0f, 1.0f);
		mTextElveOne = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_one),
				0f, 1.0f);
		addElve(mTextElveOne);
		mTextElveTwo = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_two),
				0f, 1.0f);
		addElve(mTextElveTwo);
		mTextElveThree = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_three),
				0f, 1.0f);
		addElve(mTextElveThree);
		mTextElveFour = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_four),
				0f, 1.0f);
		addElve(mTextElveFour);
		mTextElveFive = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_five),
				0f, 1.0f);
		addElve(mTextElveFive);
		mTextElveSix = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_six),
				0f, 1.0f);
		addElve(mTextElveSix);
		mTextElveSeven = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_seven),
				0f, 1.0f);
		addElve(mTextElveSeven);
		mTextElveEight = new ComprehensiveElve(resource.getDrawable(R.drawable.advanced_pay_no_advert_text_eight),
				0f, 1.0f);
		addElve(mTextElveEight);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mIsInit) {
			float phoneTop = (bottom - top) / 2.0f - mPhoneElve.getDrawable().getIntrinsicHeight() / 2.0f;
			float centerX = (right - left) / 2.0f;
			float phoneLeft = centerX - mPhoneElve.getDrawable().getIntrinsicWidth() / 2.0f;
			mPhoneElve.setCenter(centerX, (bottom - top) / 2.0f);
			float x = mPhoneElve.getDrawable().getIntrinsicWidth() / 2.0f;
			mDiamondElve.addMoveAnimate(x, mDiamondElveStartCenterY, x, mDiamondElveSEndCenterY,
					0.1f, 0.75f);
			mEdgeElve.addMoveAnimate(x, 0, x, mPhoneElve.getDrawable().getIntrinsicHeight(), 0.1f,
					1.0f);
			float startX = phoneLeft + DrawUtils.dip2px(39) + mTextElveOne.getDrawable().getIntrinsicWidth() / 2.0f;
			float startY = phoneTop + DrawUtils.dip2px(46) + mTextElveOne.getDrawable().getIntrinsicHeight() / 2.0f;
			float targetY = (bottom - top) / 2.0f + mPhoneElve.getDrawable().getIntrinsicHeight() / 2.0f + DrawUtils.dip2px(15);
			mTextElveOne.addMoveAnimate(startX, startY, startX, targetY, 0.3f, 0.6f);
			mTextElveOne.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.55f, 0.6f);
			startX = phoneLeft + DrawUtils.dip2px(13) + mTextElveTwo.getDrawable().getIntrinsicWidth() / 2.0f;
			startY = phoneTop + DrawUtils.dip2px(58) + mTextElveTwo.getDrawable().getIntrinsicHeight() / 2.0f;
			mTextElveTwo.addMoveAnimate(startX, startY, startX, targetY, 0.35f, 0.65f);
			mTextElveTwo.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.6f, 0.65f);
			startX = phoneLeft + DrawUtils.dip2px(47) + mTextElveThree.getDrawable().getIntrinsicWidth() / 2.0f;
			startY = phoneTop + DrawUtils.dip2px(73) + mTextElveThree.getDrawable().getIntrinsicHeight() / 2.0f;
			mTextElveThree.addMoveAnimate(startX, startY, startX, targetY, 0.4f, 0.7f);
			mTextElveThree.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.65f, 0.7f);
			startX = phoneLeft + DrawUtils.dip2px(16) + mTextElveFour.getDrawable().getIntrinsicWidth() / 2.0f;
			startY = phoneTop + DrawUtils.dip2px(81) + mTextElveFour.getDrawable().getIntrinsicHeight() / 2.0f;
			mTextElveFour.addMoveAnimate(startX, startY, startX, targetY, 0.45f, 0.75f);
			mTextElveFour.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.7f, 0.75f);
			startX = phoneLeft + DrawUtils.dip2px(45) + mTextElveFive.getDrawable().getIntrinsicWidth() / 2.0f;
			startY = phoneTop + DrawUtils.dip2px(88) + mTextElveFive.getDrawable().getIntrinsicHeight() / 2.0f;
			mTextElveFive.addMoveAnimate(startX, startY, startX, targetY, 0.5f, 0.8f);
			mTextElveFive.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.75f, 0.8f);
			startX = phoneLeft + DrawUtils.dip2px(10) + mTextElveSix.getDrawable().getIntrinsicWidth() / 2.0f;
			startY = phoneTop + DrawUtils.dip2px(98) + mTextElveSix.getDrawable().getIntrinsicHeight() / 2.0f;
			mTextElveSix.addMoveAnimate(startX, startY, startX, targetY, 0.55f, 0.85f);
			mTextElveSix.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.8f, 0.85f);
			startX = phoneLeft + DrawUtils.dip2px(54) + mTextElveSeven.getDrawable().getIntrinsicWidth() / 2.0f;
			startY = phoneTop + DrawUtils.dip2px(102) + mTextElveSeven.getDrawable().getIntrinsicHeight() / 2.0f;
			mTextElveSeven.addMoveAnimate(startX, startY, startX, targetY, 0.6f, 0.9f);
			mTextElveSeven.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.85f, 0.9f);
			startX = phoneLeft + DrawUtils.dip2px(18) + mTextElveEight.getDrawable().getIntrinsicWidth() / 2.0f;
			startY = phoneTop + DrawUtils.dip2px(112) + mTextElveEight.getDrawable().getIntrinsicHeight() / 2.0f;
			mTextElveEight.addMoveAnimate(startX, startY, startX, targetY, 0.65f, 0.95f);
			mTextElveEight.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, 0.9f, 0.95f);
		}
	}
	
	@Override
	protected void drawChildren(Canvas canvas) {
		super.drawChildren(canvas);
		if (mIsInit) {
			mTempCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			mDiamondElve.drawElve(mTempCanvas);
			mEdgeElve.drawElve(mTempCanvas);
			mTempCanvas.drawBitmap(mMaskBitmap, 0, 0, mTempPaint);
			canvas.drawBitmap(mTempBitmap, mPhoneElve.getCenterX() - mTempBitmap.getWidth() / 2,
					mPhoneElve.getCenterY() - mTempBitmap.getHeight() / 2, mPaint);
		}
	}
	
	@Override
	protected void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		if (mIsInit) {
			mDiamondElve.calculateAnimateValue(curAnimatePercent);
			mEdgeElve.calculateAnimateValue(curAnimatePercent);
		}
	}
	
	@Override
	public String getMessageTip() {
		return mContext.getString(R.string.desksetting_pay_dialog_message_tip1);
	}

	@Override
	public String getMessageSummary() {
		return mContext.getString(R.string.desksetting_pay_dialog_message_summary1);
	}
	
	@Override
	public int getBgColor() {
		return Color.parseColor("#FF3EBA71");
	}
	
	@Override
	public void setEnterAlpha(int alpha) {
		if (mIsInit) {
			mPhoneElve.setAlpha(alpha);
			mTextElveOne.setAlpha(alpha);
			mTextElveTwo.setAlpha(alpha);
			mTextElveThree.setAlpha(alpha);
			mTextElveFour.setAlpha(alpha);
			mTextElveFive.setAlpha(alpha);
			mTextElveSix.setAlpha(alpha);
			mTextElveSeven.setAlpha(alpha);
			mTextElveEight.setAlpha(alpha);
		}
	}
	
	@Override
	public void setEnterScale(float scale) {
		if (mIsInit) {
			mPhoneElve.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveOne.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveTwo.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveThree.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveFour.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveFive.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveSix.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveSeven.setScale(scale, scale, Elve.SCALE_CENTER);
			mTextElveEight.setScale(scale, scale, Elve.SCALE_CENTER);
		}
	}
}
