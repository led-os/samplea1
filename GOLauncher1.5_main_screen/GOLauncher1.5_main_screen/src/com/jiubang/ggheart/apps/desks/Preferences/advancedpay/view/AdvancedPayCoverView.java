package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.ComprehensiveElve;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.elve.Elve;

/**
 * 付费预览罩子View
 * @author yejijiong
 *
 */
public class AdvancedPayCoverView extends AdvancedPayBaseView {

	private ComprehensiveElve mStartPageDiamondElve;
	private ComprehensiveElve mStartPageBackElve;
	private ComprehensiveElve mStartPageTextElve;
	private int mDiamondStartCenterY;
	private int mDiamondTargetCenterY;
	private int mTextElveStartCenterY;
	private float mDiamondScale;
	private float mTextElveScale;
	private float mCurScrollPercent;
	private Rect mTouchRect;
	private Drawable mTouchLightDrawable;
	private boolean mNeedDrawTouchLight = false;
	private ClickBackListenr mListener;
	
	private ComprehensiveElve mPrimeDateElve;
    private ComprehensiveElve mPrimeMessageElve;
    private float mPrimeMessageScale;

	public AdvancedPayCoverView(Context context, int animateFinishType,
			IAdvancedPayListener listener) {
		super(context, animateFinishType, listener);
	}

	public AdvancedPayCoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void init() {
		mDiamondStartCenterY = DrawUtils.dip2px(106);
		mDiamondTargetCenterY = DrawUtils.dip2px(24);
		mTextElveStartCenterY = DrawUtils.dip2px(183);
		mDiamondScale = 0.35f;
		mTextElveScale = 0.8f;
		mPrimeMessageScale = 0.6f;
		
		mTouchRect = new Rect(0, 0, DrawUtils.dip2px(45), DrawUtils.dip2px(48));
	}

	@Override
	protected void initElve() {
		Resources resource = mContext.getResources();
		mStartPageDiamondElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_start_page_diamond), 0, 1.0f);
		
		Drawable textDrawable = buildTextDrawable(
                resource.getString(R.string.desksetting_apy_dialog_cover_view_title),
                DrawUtils.sp2px(25), Color.parseColor("#FFFFFF"));
        mStartPageTextElve = new ComprehensiveElve(textDrawable, 0, 1.0f);
		
		mStartPageDiamondElve.addScaleAnimate(1.0f, mDiamondScale, Elve.SCALE_LEFT_MIDDLE, 0, 1.0f);
		if (isPromotion()) {
		    mStartPageDiamondElve.addAlphaValue(0, 153, 0, 1.0f);
		    mStartPageTextElve.addAlphaValue(0, 153, 0, 1.0f);
		    
		    
            mPrimeMessageElve = new ComprehensiveElve(
                    resource.getDrawable(R.drawable.desk_setting_pay_dialog_sale_prime_message_img), 0, 1.0f);
            mPrimeMessageElve.addScaleAnimate(1.0f, mPrimeMessageScale, Elve.SCALE_LEFT_MIDDLE, 0, 1.0f);
            mPrimeMessageElve.addAlphaValue(Elve.MAX_ALPHA, 0, 0, 1.0f);
            
            mPrimeDateElve = new ComprehensiveElve(
                    resource.getDrawable(R.drawable.desk_setting_pay_dialog_sale_img), 0, 1.0f);
            mPrimeDateElve.addScaleAnimate(0.9f, 0.2f, Elve.SCALE_LEFT_MIDDLE, 0, 1.0f);
            mPrimeDateElve.addAlphaValue(Elve.MAX_ALPHA, 0, 0.3f, 1.0f);
            
            addElve(mPrimeMessageElve);
            addElve(mPrimeDateElve);
		} else {
		    mStartPageDiamondElve.addAlphaValue(Elve.MAX_ALPHA, 153, 0, 1.0f);
		    mStartPageTextElve.addAlphaValue(Elve.MAX_ALPHA, 153, 0, 1.0f);
		}
//		addElve(mStartPageDiamondElve);
		mStartPageBackElve = new ComprehensiveElve(
				resource.getDrawable(R.drawable.advanced_pay_start_page_back), 0, 1.0f);
		mStartPageBackElve.addAlphaValue(Elve.MIN_ALPHA, Elve.MAX_ALPHA, 0, 1.0f);
		addElve(mStartPageBackElve);
//		Drawable textDrawable = buildTextDrawable(
//				resource.getString(R.string.desksetting_apy_dialog_cover_view_title),
//				DrawUtils.sp2px(25), Color.parseColor("#FFFFFF"));
//		mStartPageTextElve = new ComprehensiveElve(textDrawable, 0, 1.0f);
		mStartPageTextElve.addScaleAnimate(1.0f, mTextElveScale, Elve.SCALE_LEFT_MIDDLE, 0, 1.0f);
//		mStartPageTextElve.addAlphaValue(Elve.MAX_ALPHA, 153, 0, 1.0f);
		
		
		addElve(mStartPageDiamondElve);
		addElve(mStartPageTextElve);
		
		mTouchLightDrawable = resource.getDrawable(R.drawable.change_icon_tab_press);
		mTouchLightDrawable.setBounds(mTouchRect);
	}

	public void onScrollChange(float scrollPercent) {
		mCurScrollPercent = scrollPercent;
		if (mIsInit) {
			for (Elve elve : mElveList) { // 滚动量动画根据当前滚动量计算当前百分比
				elve.calculateAnimateValue(scrollPercent);
			}
			invalidate();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mIsInit) {
			int backDrawableWidth = mStartPageBackElve.getDrawable().getIntrinsicWidth();
			int diamondTargetCenterX = backDrawableWidth
					+ mStartPageDiamondElve.getDrawable().getIntrinsicWidth() / 2;
			float textElveCenterX = backDrawableWidth
					+ mStartPageDiamondElve.getDrawable().getIntrinsicWidth() * mDiamondScale
					+ DrawUtils.dip2px(4) + +mStartPageTextElve.getDrawable().getIntrinsicWidth()
					/ 2.0f;
			mStartPageDiamondElve.addMoveAnimate((right - left) / 2, mDiamondStartCenterY,
					diamondTargetCenterX, mDiamondTargetCenterY, 0, 1.0f);
			mStartPageBackElve.setCenter(mStartPageBackElve.getDrawable().getIntrinsicWidth() / 2,
					mDiamondTargetCenterY);
			mStartPageTextElve.addMoveAnimate((right - left) / 2, mTextElveStartCenterY,
					textElveCenterX, mDiamondTargetCenterY, 0, 1.0f);
			
			if (mPrimeDateElve != null) {
			    
			    float primeDateTargetCenterX = backDrawableWidth
                        + mPrimeDateElve.getDrawable().getIntrinsicWidth();
			    
			    float primeDateTargetCenterY = backDrawableWidth
                        + mPrimeDateElve.getDrawable().getIntrinsicHeight();
			    
			    mPrimeDateElve.addMoveAnimate((right - left) - primeDateTargetCenterX / 2 + DrawUtils.dip2px(25), primeDateTargetCenterY / 2 - DrawUtils.dip2px(15),
	                    0, 0, 0, 0);
			}
			
			if (mPrimeMessageElve != null) {
			    
			    float primeMessageTargetCenterX = backDrawableWidth
	                    + mPrimeMessageElve.getDrawable().getIntrinsicWidth() * mPrimeMessageScale;
			    
			    float primeMessageTargetCenterY = backDrawableWidth
                        + mPrimeMessageElve.getDrawable().getIntrinsicHeight() * mPrimeMessageScale;
			    
			    mPrimeMessageElve.addMoveAnimate((right - left) / 2, (bottom - top) / 2 + primeMessageTargetCenterY / 8,
			            -primeMessageTargetCenterX, (bottom - top) / 2, 0, 1.0f);
			}
			
			onScrollChange(mCurScrollPercent);
		}
	}

	private Drawable buildTextDrawable(String text, int textSize, int textColor) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint.setTextSize(textSize);
		paint.setColor(textColor);
		paint.setShadowLayer(3, 0, 4, Color.parseColor("#1E000000"));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		int textBaseHeight = bounds.height();
		int textWidth = (int) paint.measureText(text);
		Bitmap bitmap = Bitmap.createBitmap(textWidth, (int) (textBaseHeight * 1.2f), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(text, 0, textBaseHeight * 1.05f, paint);
		return new BitmapDrawable(mContext.getResources(), bitmap);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (mNeedDrawTouchLight) {
			mTouchLightDrawable.draw(canvas);
		}
		super.draw(canvas);
	}
	
	public void setListener(ClickBackListenr listener) {
		mListener = listener;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		int action = event.getAction();
		if (mCurScrollPercent >= 1.0f && mTouchRect.contains(x, y)) {
			if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
				mNeedDrawTouchLight = true;
			} else if (action == MotionEvent.ACTION_UP) {
				mNeedDrawTouchLight = false;
				if (mListener != null) {
					mListener.onclickBack();
				}
			}
			invalidate();
			return true;
		} else {
			mNeedDrawTouchLight = false;
			if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
				invalidate();
			}
			return super.onTouchEvent(event);
		}
	}
	
	/**
	 * 返回键回调接口
	 * @author yejijiong
	 *
	 */
	public interface ClickBackListenr {
		public void onclickBack();
	}
}
