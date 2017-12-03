package com.jiubang.ggheart.apps.desks.appfunc.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;
import com.gau.go.launcherex.R;
import com.go.util.graphics.ImageUtil;
import com.jiubang.ggheart.components.DeskTextView;

/**
 * 功能表列表菜单选项View
 * @author yangguanxiang
 *
 */
public class AppFuncMenuItemView extends DeskTextView {

	private int mTitleNum;
	private Paint mTitleNumPaint;
	private Drawable mTitleNumDrawable;
	private Rect mTitleNumRect;
	private Drawable mNewDrawable;
	private Paint mNewPaint;
	private boolean mNeedShowNewDrawable = false;
	public AppFuncMenuItemView(Context context) {
		super(context);
	}

	public AppFuncMenuItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getText() == null || getText().equals("")) {
			int heightSpec = heightMeasureSpec;
			int widthSpec = widthMeasureSpec;
			Drawable[] drawables = getCompoundDrawables();
			if (drawables != null) {
				LayoutParams params = getLayoutParams();
				boolean widthWrapContent = true;
				boolean heightWrapContent = true;
				if (params != null) {
					if (params.width != LayoutParams.WRAP_CONTENT) {
						widthWrapContent = false;
					}
					if (params.height != LayoutParams.WRAP_CONTENT) {
						heightWrapContent = false;
					}
				}
				if (heightWrapContent) {
					int height = getCompoundPaddingTop() + getCompoundPaddingBottom();
					if (drawables[0] != null && drawables[0].getIntrinsicHeight() > height) {
						height = drawables[0].getIntrinsicHeight();
					}
					if (drawables[2] != null && drawables[2].getIntrinsicHeight() > height) {
						height = drawables[2].getIntrinsicHeight();
					}
					heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
				}

				if (widthWrapContent) {
					int width = getCompoundPaddingLeft() + getCompoundPaddingRight();
					if (drawables[1] != null && drawables[1].getIntrinsicWidth() > width) {
						width = drawables[1].getIntrinsicWidth();
					}
					if (drawables[3] != null && drawables[3].getIntrinsicWidth() > width) {
						width = drawables[3].getIntrinsicWidth();
					}
					widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
				}
			}
			super.onMeasure(widthSpec, heightSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		layoutTitleNum();
	}

	protected void layoutTitleNum() {
		CharSequence text = getText();
		if (text != null && mTitleNum > 0) {
			if (mTitleNumPaint == null) {
				mTitleNumPaint = new Paint();
				int fontSize = getResources().getDimensionPixelSize(R.dimen.dock_notify_font_size);
				mTitleNumPaint.setTextSize(fontSize);
				mTitleNumPaint.setStyle(Style.FILL_AND_STROKE);
				mTitleNumPaint.setAntiAlias(true);
				mTitleNumPaint.setColor(android.graphics.Color.WHITE);
				mTitleNumPaint.setTypeface(Typeface.DEFAULT_BOLD);
				mTitleNumPaint.setTextAlign(Paint.Align.CENTER);
			}
			if (mTitleNumDrawable == null) {
				//				mTitleNumDrawable = getResources().getDrawable(
				//						R.drawable.recomm_appsmanagement_update_count_notification_orange);
				mTitleNumDrawable = getResources().getDrawable(R.drawable.stat_notify_no_nine);
			}
			if (mTitleNumRect == null) {
				mTitleNumRect = new Rect();
			}
			String numStr = String.valueOf(mTitleNum);
			mTitleNumPaint.getTextBounds(numStr, 0, numStr.length(), mTitleNumRect);
			Rect rect = new Rect();
			getPaint().getTextBounds(text.toString(), 0, text.length(), rect);
			int defaultWidth = getResources().getDimensionPixelSize(R.dimen.dock_notify_width);
			int numWidth = mTitleNumRect.width() + (int) (defaultWidth * 2.0 / 3.0f);
			if (mTitleNum < 10) {
				numWidth = defaultWidth;
			}
			int numHeight = getResources().getDimensionPixelSize(R.dimen.dock_notify_height);
			int numLeft = getPaddingLeft() + rect.width();
			int numRight = numLeft + numWidth;
			int numTop = (int) ((getHeight() - rect.height()) / 2.0f - numHeight / 2.0f);
			int numBottom = numTop + numHeight;
			mTitleNumDrawable.setBounds(numLeft, numTop, numRight, numBottom);
		}
		
		if (mNeedShowNewDrawable) {
			mNewPaint = new Paint();
			if (mNewDrawable == null) {
				mNewDrawable = getResources().getDrawable(R.drawable.new_mark);
			}
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		CharSequence text = getText();
		if (text != null && mTitleNum > 0) {
			if (mTitleNumPaint != null && mTitleNumDrawable != null) {
				//				mTitleNumDrawable.draw(canvas);
				Rect rect = mTitleNumDrawable.getBounds();
				ImageUtil.drawImage(canvas, mTitleNumDrawable, ImageUtil.STRETCHMODE, rect.left,
						rect.top, rect.right, rect.bottom, mTitleNumPaint);
				canvas.drawText(String.valueOf(mTitleNum), rect.centerX(), rect.centerY()
						+ mTitleNumRect.height() / 3.0f, mTitleNumPaint);
			}
		}
		
		if (mNeedShowNewDrawable) {
			Rect rect = mNewDrawable.getBounds();
			Rect textRect = new Rect();
			ImageUtil.drawImage(canvas, mNewDrawable, ImageUtil.STRETCHMODE, rect.left,
					rect.top, rect.right, rect.bottom, mTitleNumPaint);
			BitmapDrawable bitmapDrawable = (BitmapDrawable) mNewDrawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			getPaint().getTextBounds(text.toString(), 0, text.length(), textRect);
			int left = textRect.width() + getPaddingLeft() + DrawUtils.dip2px(5);
			if (left > getWidth() - bitmap.getWidth() - getPaddingRight()) {
				left = getWidth() - bitmap.getWidth() - getPaddingRight();
			}
			canvas.drawBitmap(bitmap, left, (textRect.height()) / 2, mNewPaint);
		}
	}
	public void setTitleNum(int num) {
		mTitleNum = num;
		layoutTitleNum();
	}
	
	/**
	 * 设置是否显示new标识
	 * @param showNewDrawable
	 */
	public void setNewDrawable(boolean showNewDrawable) {
		mNeedShowNewDrawable = showNewDrawable;
	}
}
