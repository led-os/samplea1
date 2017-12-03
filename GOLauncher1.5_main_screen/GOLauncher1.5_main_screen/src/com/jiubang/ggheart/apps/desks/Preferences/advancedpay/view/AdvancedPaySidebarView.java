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
 * 付费预览特效主View
 * @author yejijiong
 *
 */
public class AdvancedPaySidebarView extends AdvancedPayBaseView {
	private Elve mPhoneElve;
	private Elve mPhoneScreenElve;
	private ComprehensiveElve mSideBarElve;
	private ComprehensiveElve mAppElveOne;
	private ComprehensiveElve mAppElveTwo;
	private ComprehensiveElve mAppElveThree;
	private ComprehensiveElve mAppElveFour;
	private ComprehensiveElve mAddAppElve;
	private Paint mPaint;
	private Canvas mTempCanvas;
	private Bitmap mTempBitmap;
	private Paint mTempPaint;
	private Bitmap mMaskBitmap;
	private int mPhonePaddingTop;
	private float mScreenTop;
	private float mAppCenterX;
	private int mAppCenterDistance;
	
	public AdvancedPaySidebarView(Context context, int animateFinishType, IAdvancedPayListener listener) {
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
		
		mPhonePaddingTop = DrawUtils.dip2px(16);
		mAppCenterDistance = DrawUtils.dip2px(27);
		mAnimateDuration = 1500;
	}
	
	@Override
	protected void initElve() {
		Resources resource = mContext.getResources();
		mPhoneElve = new Elve(
				resource.getDrawable(R.drawable.advanced_pay_phone),
				1.0f, 1.0f);
		addElve(mPhoneElve);
		mPhoneScreenElve =  new Elve(
				resource.getDrawable(R.drawable.advanced_pay_sidebar_phone_screen),
				1.0f, 1.0f);
		addElve(mPhoneScreenElve);
		mSideBarElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_sidebar_side_bar),
				0, 1.0f);
		float scaleDuration = 0.2f; // 放大缩小过程持续的时间基数
		mAppElveOne = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_sidebar_app_one),
				0, 1.0f);
		mAppElveOne.addScaleAnimate(0f, 1.0f,
				Elve.SCALE_CENTER, 0.4f, 0.4f + scaleDuration);
		mAppElveTwo = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_sidebar_app_two),
				0, 1.0f);
		mAppElveTwo.addScaleAnimate(0f, 1.0f,
				Elve.SCALE_CENTER, 0.5f, 0.5f + scaleDuration);
		mAppElveThree = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_sidebar_app_three),
				0, 1.0f);
		mAppElveThree.addScaleAnimate(0f, 1.0f,
				Elve.SCALE_CENTER, 0.6f, 0.6f + scaleDuration);
		mAppElveFour = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_sidebar_app_four),
				0, 1.0f);
		mAppElveFour.addScaleAnimate(0f, 1.0f,
				Elve.SCALE_CENTER, 0.7f, 0.7f + scaleDuration);
		mAddAppElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_sidebar_add_app),
				0, 1.0f);
		mAddAppElve.addScaleAnimate(0f, 1.0f,
				Elve.SCALE_CENTER, 0.8f, 0.8f + scaleDuration);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mIsInit) {
			float centerX = (right - left) / 2;
			float centerY = (bottom - top) / 2;
			mPhoneElve.setCenter(centerX, centerY);
			mPhoneScreenElve.setCenter(centerX, centerY);
			float phoneTop = mPhoneElve.getCenterY() - mPhoneElve.getDrawable().getIntrinsicHeight() / 2.0f;
			mScreenTop = phoneTop + mPhonePaddingTop;
			mAppCenterX = mPhoneElve.getCenterX() + mPhoneElve.getDrawable().getIntrinsicWidth()
					/ 2.0f - DrawUtils.dip2px(4) - DrawUtils.dip2px(13.5f);

			float appCenterY = mScreenTop + DrawUtils.dip2px(4) + DrawUtils.dip2px(10);
			mAppElveOne.setCenter(mAppCenterX, appCenterY);
			
			appCenterY = appCenterY + mAppCenterDistance;
			mAppElveTwo.setCenter(mAppCenterX, appCenterY);
			
			appCenterY = appCenterY + mAppCenterDistance;
			mAppElveThree.setCenter(mAppCenterX, appCenterY);
			
			appCenterY = appCenterY + mAppCenterDistance;
			mAppElveFour.setCenter(mAppCenterX, appCenterY);
			
			appCenterY = appCenterY + mAppCenterDistance;
			mAddAppElve.setCenter(mAppCenterX, appCenterY);
			
			float sideBarStartCenterX = mPhoneElve.getDrawable().getIntrinsicWidth()
					+ mSideBarElve.getDrawable().getIntrinsicWidth() / 2.0f;
			float sideBarTargetCenterX = mPhoneElve.getDrawable().getIntrinsicWidth()
					- DrawUtils.dip2px(4) - DrawUtils.dip2px(13.5f);
			float sideBarCenterY = mPhonePaddingTop + mSideBarElve.getDrawable().getIntrinsicHeight() / 2.0f;
			mSideBarElve.addMoveAnimate(sideBarStartCenterX, sideBarCenterY, sideBarTargetCenterX,
					sideBarCenterY, 0.1f, 0.4f);
		}
	}
	
	@Override
	protected void drawChildren(Canvas canvas) {
		super.drawChildren(canvas);
		if (mIsInit) {
			mTempCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			mSideBarElve.drawElve(mTempCanvas);
			mTempCanvas.drawBitmap(mMaskBitmap, 0, 0, mTempPaint);
			canvas.drawBitmap(mTempBitmap, mPhoneElve.getCenterX() - mTempBitmap.getWidth() / 2,
					mPhoneElve.getCenterY() - mTempBitmap.getHeight() / 2, mPaint);
			mAppElveOne.drawElve(canvas);
			mAppElveTwo.drawElve(canvas);
			mAppElveThree.drawElve(canvas);
			mAppElveFour.drawElve(canvas);
			mAddAppElve.drawElve(canvas);
		}
	}
	
	@Override
	protected void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		if (mIsInit) {
			mSideBarElve.calculateAnimateValue(curAnimatePercent);
			mAppElveOne.calculateAnimateValue(curAnimatePercent);
			mAppElveTwo.calculateAnimateValue(curAnimatePercent);
			mAppElveThree.calculateAnimateValue(curAnimatePercent);
			mAppElveFour.calculateAnimateValue(curAnimatePercent);
			mAddAppElve.calculateAnimateValue(curAnimatePercent);
		}
	}
	
	@Override
	public String getMessageTip() {
		return mContext.getResources().getString(R.string.desksetting_pay_dialog_message_tip5);
	}

	@Override
	public String getMessageSummary() {
		return mContext.getResources().getString(R.string.desksetting_pay_dialog_message_summary5);
	}
	
	@Override
	public int getBgColor() {
		return Color.parseColor("#FFAF7BC3");
	}
	
	@Override
	public void setEnterAlpha(int alpha) {
		if (mIsInit) {
			mPhoneElve.setAlpha(alpha);
			mPhoneScreenElve.setAlpha(alpha);
		}
	}
	
	@Override
	public void setEnterScale(float scale) {
		if (mIsInit) {
			mPhoneElve.setScale(scale, scale, Elve.SCALE_CENTER);
			mPhoneScreenElve.setScale(scale, scale, Elve.SCALE_CENTER);
		}
	}
}
