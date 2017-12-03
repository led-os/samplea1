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
 * 付费预览手势界面主View
 * @author yejijiong
 *
 */
public class AdvancedPayGestureView extends AdvancedPayBaseView {

	private Elve mPhoneElve;
	private Elve mPhoneBgElve;
	private ComprehensiveElve mPointElve;
	private ComprehensiveElve mTrackElve;
	private ComprehensiveElve mTwitterBgElve;
	private ComprehensiveElve mTwitterLogoElve;
	private ComprehensiveElve mTwitterContextElve;

	private Paint mPaint;
	private Canvas mTempTrackCanvas;
	private Bitmap mTempTrackBitmap;
	private Bitmap mTrackMaskBitmap;
	private Canvas mTempTwitterCanvas;
	private Bitmap mTempTwitterBitmap;
	private Bitmap mTwitterMaskBitmap;
	private Paint mTempPaint;
	private float mPointMoveStartBase;
	private float mPointMoveEndBase;
	private float mPointAlphaStartBase;
	private float mPointAlphaEndBase;
	private float mTwitterMoveStartBase;
	private float mTwitterMoveEndBase;
	private float mTwitterElasticMoveDuration;
	private float mTwitterScaleStartBase;
	private float mTwitterScaleEndBase;
	private int mPhonePaddingTop;

	public AdvancedPayGestureView(Context context, int animateFinishType,
			IAdvancedPayListener listener) {
		super(context, animateFinishType, listener);
	}

	@Override
	protected void init() {
		PorterDuffXfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
		mTrackMaskBitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.advanced_pay_gesture_track_mask)).getBitmap();
		mTempTrackCanvas = new Canvas();
		mTempTrackBitmap = Bitmap.createBitmap(mTrackMaskBitmap.getWidth(),
				mTrackMaskBitmap.getHeight(), Config.ARGB_8888);
		mTempTrackCanvas.setBitmap(mTempTrackBitmap);
		mTwitterMaskBitmap = ((BitmapDrawable) mContext.getResources().getDrawable(
				R.drawable.advanced_pay_phone_mask)).getBitmap();
		mTempTwitterCanvas = new Canvas();
		mTempTwitterBitmap = Bitmap.createBitmap(mTwitterMaskBitmap.getWidth(),
				mTwitterMaskBitmap.getHeight(), Config.ARGB_8888);
		mTempTwitterCanvas.setBitmap(mTempTwitterBitmap);
		mTempPaint = new Paint();
		mTempPaint.setXfermode(xFermode);
		mTempPaint.setColor(Color.BLACK);
		mTempPaint.setAntiAlias(true);
		mTempPaint.setFilterBitmap(true);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		mPointMoveStartBase = 0.1f;
		mPointMoveEndBase = 0.3f;
		mPointAlphaStartBase = 0.35f;
		mPointAlphaEndBase = 0.5f;
		mTwitterMoveStartBase = 0.5f;
		mTwitterMoveEndBase = 0.65f;
		mTwitterElasticMoveDuration = 0.025f; // 共0.03*6=0.18f
		mTwitterScaleStartBase = mTwitterMoveEndBase + mTwitterElasticMoveDuration * 6 + 0.1f;
		mTwitterScaleEndBase = 1.0f;
		mPhonePaddingTop = DrawUtils.dip2px(16);
		mAnimateDuration = 2000;
	}

	@Override
	protected void initElve() {
		Resources resource = mContext.getResources();
		mPhoneElve = new Elve(resource.getDrawable(R.drawable.advanced_pay_phone), 1.0f, 1.0f);
		addElve(mPhoneElve);
		mPhoneBgElve = new Elve(resource.getDrawable(R.drawable.advanced_pay_gesture_phone_bg),
				1.0f, 1.0f);
		addElve(mPhoneBgElve);
		mPointElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_gesture_point), 0f, 1.0f);
		mPointElve.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, mPointAlphaStartBase,
				mPointAlphaEndBase);
		addElve(mPointElve);
		mTrackElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_gesture_track), 0f, 1.0f);
		mTrackElve.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, mPointAlphaStartBase,
				mPointAlphaEndBase);
		mTwitterBgElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_gesture_twitter_bg), 0f, 1.0f);
		mTwitterBgElve.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, mTwitterScaleStartBase,
				mTwitterScaleEndBase);
		mTwitterLogoElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_gesture_twitter_logo), 0f, 1.0f);
		mTwitterLogoElve.addScaleAnimate(1.0f, 1.5f, Elve.SCALE_CENTER, mTwitterScaleStartBase,
				mTwitterScaleEndBase);
		mTwitterLogoElve.addAlphaValue(Elve.MAX_ALPHA, Elve.MIN_ALPHA, mTwitterScaleStartBase,
				mTwitterScaleEndBase);
		mTwitterContextElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_gesture_twitter_context), 0f, 1.0f);
		mTwitterContextElve.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, mTwitterMoveEndBase,
				mTwitterMoveEndBase + 0.01f);
		addElve(mTwitterContextElve);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mIsInit) {
			float centerX = (right - left) / 2;
			float centerY = (bottom - top) / 2;
			float phoneTop = centerY - mPhoneElve.getDrawable().getIntrinsicHeight() / 2.0f;
			mPhoneElve.setCenter(centerX, centerY);
			mPhoneBgElve.setCenter(centerX, centerY);
			float startY = phoneTop + DrawUtils.dip2px(100)
					+ mPointElve.getDrawable().getIntrinsicHeight() / 2.0f;
			float endY = phoneTop + DrawUtils.dip2px(40)
					+ mPointElve.getDrawable().getIntrinsicHeight() / 2.0f;
			mPointElve.addMoveAnimate(centerX, startY, centerX, endY, mPointMoveStartBase,
					mPointMoveEndBase);
			float trackX = mTrackElve.getDrawable().getIntrinsicWidth() / 2.0f;
			float maskPadddingTop = DrawUtils.dip2px(30);
			float trackStartY = DrawUtils.dip2px(100) - maskPadddingTop
					+ mTrackElve.getDrawable().getIntrinsicHeight() / 2.0f;
			float trackEndY = DrawUtils.dip2px(40) - maskPadddingTop
					+ mTrackElve.getDrawable().getIntrinsicHeight() / 2.0f;
			mTrackElve.addMoveAnimate(trackX, trackStartY, trackX, trackEndY, mPointMoveStartBase,
					mPointMoveEndBase);
			float twitterBgStartX = mTwitterMaskBitmap.getWidth()
					+ mTwitterBgElve.getDrawable().getIntrinsicWidth() / 2.0f;
			float twitterBgEndX = mTwitterMaskBitmap.getWidth() / 2.0f;
			float twitterBgY = mPhonePaddingTop + mTwitterBgElve.getDrawable().getIntrinsicHeight()
					/ 2.0f;
			mTwitterBgElve.addMoveAnimate(twitterBgStartX, twitterBgY, twitterBgEndX, twitterBgY,
					mTwitterMoveStartBase, mTwitterMoveEndBase);
			mTwitterLogoElve.clearAllMoveAnimate(); // 清楚上一次所有的移动动画
			mTwitterLogoElve.addMoveAnimate(twitterBgStartX, twitterBgY, twitterBgEndX, twitterBgY,
					mTwitterMoveStartBase, mTwitterMoveEndBase);
			float elasticMoveStartBase = mTwitterMoveEndBase;
			float elasticMoveEndBase = elasticMoveStartBase + mTwitterElasticMoveDuration;
			float elasticMoveStartX = twitterBgEndX;
			float elasticMoveEndX = twitterBgEndX - DrawUtils.dip2px(10);
			mTwitterLogoElve.addMoveAnimate(elasticMoveStartX, twitterBgY, elasticMoveEndX,
					twitterBgY, elasticMoveStartBase, elasticMoveEndBase);
			elasticMoveStartBase = elasticMoveEndBase;
			elasticMoveEndBase = elasticMoveStartBase + mTwitterElasticMoveDuration * 2;
			elasticMoveStartX = elasticMoveEndX;
			elasticMoveEndX = twitterBgEndX + DrawUtils.dip2px(6);
			mTwitterLogoElve.addMoveAnimate(elasticMoveStartX, twitterBgY, elasticMoveEndX,
					twitterBgY, elasticMoveStartBase, elasticMoveEndBase);
			elasticMoveStartBase = elasticMoveEndBase;
			elasticMoveEndBase = elasticMoveStartBase + mTwitterElasticMoveDuration * 2;
			elasticMoveStartX = elasticMoveEndX;
			elasticMoveEndX = twitterBgEndX - DrawUtils.dip2px(3);
			mTwitterLogoElve.addMoveAnimate(elasticMoveStartX, twitterBgY, elasticMoveEndX,
					twitterBgY, elasticMoveStartBase, elasticMoveEndBase);
			elasticMoveStartBase = elasticMoveEndBase;
			elasticMoveEndBase = elasticMoveStartBase + mTwitterElasticMoveDuration;
			elasticMoveStartX = elasticMoveEndX;
			elasticMoveEndX = twitterBgEndX;
			mTwitterLogoElve.addMoveAnimate(elasticMoveStartX, twitterBgY, elasticMoveEndX,
					twitterBgY, elasticMoveStartBase, elasticMoveEndBase);
			mTwitterContextElve.setCenter(centerX, phoneTop + mPhonePaddingTop
					+ mTwitterContextElve.getDrawable().getIntrinsicHeight() / 2.0f);
		}
	}

	@Override
	protected void calculateAnimateValue(float curAnimatePercent) {
		super.calculateAnimateValue(curAnimatePercent);
		if (mIsInit) {
			mTrackElve.calculateAnimateValue(curAnimatePercent);
			mTwitterBgElve.calculateAnimateValue(curAnimatePercent);
			mTwitterLogoElve.calculateAnimateValue(curAnimatePercent);
		}
	}

	@Override
	protected void drawChildren(Canvas canvas) {
		super.drawChildren(canvas);
		if (mIsInit) {
			mTempTrackCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			mTrackElve.drawElve(mTempTrackCanvas);
			mTempTrackCanvas.drawBitmap(mTrackMaskBitmap, 0, 0, mTempPaint);
			canvas.drawBitmap(mTempTrackBitmap,
					mPhoneElve.getCenterX() - mTempTrackBitmap.getWidth() / 2,
					mPhoneElve.getCenterY() - mTempTrackBitmap.getHeight() / 2, mPaint);

			mTempTwitterCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			mTwitterBgElve.drawElve(mTempTwitterCanvas);
			mTwitterLogoElve.drawElve(mTempTwitterCanvas);
			mTempTwitterCanvas.drawBitmap(mTwitterMaskBitmap, 0, 0, mTempPaint);
			canvas.drawBitmap(mTempTwitterBitmap,
					mPhoneElve.getCenterX() - mTempTwitterBitmap.getWidth() / 2,
					mPhoneElve.getCenterY() - mTempTwitterBitmap.getHeight() / 2, mPaint);
		}
	}

	@Override
	public String getMessageTip() {
		return mContext.getResources().getString(R.string.desksetting_pay_dialog_message_tip2);
	}

	@Override
	public String getMessageSummary() {
		return mContext.getResources().getString(R.string.desksetting_pay_dialog_message_summary2);
	}
	
	@Override
	public int getBgColor() {
		return Color.parseColor("#FF7F99A8");
	}
	
	@Override
	public void setEnterAlpha(int alpha) {
		if (mIsInit) {
			mPhoneElve.setAlpha(alpha);
			mPhoneBgElve.setAlpha(alpha);
			mPointElve.setAlpha(alpha);
			mTrackElve.setAlpha(alpha);
		}
	}
	
	@Override
	public void setEnterScale(float scale) {
		if (mIsInit) {
			mPhoneElve.setScale(scale, scale, Elve.SCALE_CENTER);
			mPhoneBgElve.setScale(scale, scale, Elve.SCALE_CENTER);
			mPointElve.setScale(scale, scale, Elve.SCALE_CENTER);
			mTrackElve.setScale(scale, scale, Elve.SCALE_CENTER);
		}
	}
}
