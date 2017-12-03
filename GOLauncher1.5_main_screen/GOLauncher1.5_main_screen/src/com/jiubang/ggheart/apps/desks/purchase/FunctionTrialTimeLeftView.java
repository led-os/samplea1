package com.jiubang.ggheart.apps.desks.purchase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class FunctionTrialTimeLeftView extends View {

	private Paint mPaint;
	private String mTimeleft;
	private float mFontW;
	private float mFontH;
	private FontMetrics mFontMetrics;
	public FunctionTrialTimeLeftView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		long date = FunctionPurchaseManager.getInstance(getContext()).getTrialDate();
		long now = System.currentTimeMillis();
		mTimeleft = String.valueOf((int) ((date - now)/(1000*60*60)));
		mPaint.setTextSize(22);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mFontMetrics = mPaint.getFontMetrics();
		mFontW = mPaint.measureText(mTimeleft);
		mFontH = mFontMetrics.bottom - mFontMetrics.top;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mPaint.setColor(0XFFFFBA31);
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2, mPaint);
		mPaint.setColor(0xFFFFFFFF); 
		canvas.drawText(mTimeleft,  (getWidth() - mFontW)/2, getHeight()/2 + (mFontH-mFontMetrics.descent*2)/2, mPaint);
	}

}
