package com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2013-1-4]
 */
public class GuideCloudView extends View {

	private Animation mEnterAnimation;
	private int mShowStatus; //显示状态
	private static final int STATUS_ENTER = 0; //正在进场
	private static final int STATUS_ACTING = 1; //正在表演
	//	private static final int STATUS_IMMOBILE = 2; //静止状态
	private int mLeft;
	private int mRight;
	private int mTop;
	private int mBottom;
	private Bitmap mDefaultActingBmp1;
	private Bitmap mDefaultActingBmp2;
	private Bitmap mDefaultImmobileBmp;
	private int mActingIndex;
	private static final int VIEW_UPDATE_TIME = 500;
	private boolean mNeedDraw = true;
	private Bitmap mShowBmp;
	private Handler mHandler;
	private boolean mHasPost;
	private int mStatusbarHeight;
	private boolean mDoAction;
	public GuideCloudView(Context context) {
		super(context);
		init();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		switch (mShowStatus) {
			case STATUS_ENTER :
				if (mDefaultImmobileBmp != null && !mDefaultImmobileBmp.isRecycled()) {
					if (StatusBarHandler.isHide()) {
						if (GoLauncherActivityProxy.isFullScreen()) {
							canvas.drawBitmap(mDefaultImmobileBmp, 0, 0, null);
						} else {
							canvas.drawBitmap(mDefaultImmobileBmp, 0,
									StatusBarHandler.getStatusbarHeight(), null);
						}
					} else {
						canvas.drawBitmap(mDefaultImmobileBmp, 0,
								StatusBarHandler.getStatusbarHeight(), null);
					}
				}
				break;
			case STATUS_ACTING :
				if (mNeedDraw) {
					if (mActingIndex % 2 == 0) {
						mShowBmp = mDefaultActingBmp1;
					} else {
						mShowBmp = mDefaultActingBmp2;
					}
					mActingIndex++;
				}
				if (mShowBmp != null && !mShowBmp.isRecycled()) {
					//				if (mHead.mZpos == RIGHT_MIDDLE || mHead.mZpos == RIGHT_TOP) {
					//					mLeft = StatusBarHandler.getDisplayWidth() - showBmp.getWidth();
					//				}
					mRight = mLeft + mShowBmp.getWidth();
					mBottom = mTop + mShowBmp.getHeight();
					if (StatusBarHandler.isHide()) {
						if (GoLauncherActivityProxy.isFullScreen()) {
							canvas.drawBitmap(mShowBmp, mLeft, mTop, null);
						} else {
							canvas.drawBitmap(mShowBmp, mLeft,
									mTop + StatusBarHandler.getStatusbarHeight(), null);
						}
					} else {
						canvas.drawBitmap(mShowBmp, mLeft,
								mTop + StatusBarHandler.getStatusbarHeight(), null);
					}

					mNeedDraw = false;
					if (!mHasPost) {
						mHasPost = true;
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								mNeedDraw = true;
								mHasPost = false;
							}
						}, VIEW_UPDATE_TIME);
					}
					postInvalidateDelayed(VIEW_UPDATE_TIME);
				}
				break;
			default :
				break;
		}
	}
	private void init() {
		mStatusbarHeight = StatusBarHandler.getStatusbarHeightByActivity();
		mHandler = new Handler(Looper.getMainLooper());
		mLeft = DrawUtils.dip2px(8);
		mTop = DrawUtils.dip2px(8);
		Context context = ApplicationProxy.getContext();
		BitmapDrawable bitmapDrawable = null;
		if (context != null) {
			if (mDefaultActingBmp1 == null) {
				bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(
						R.drawable.guide_tips_btn3);
				if (bitmapDrawable != null) {
					mDefaultActingBmp1 = bitmapDrawable.getBitmap();
				}
			}
			if (mDefaultActingBmp2 == null) {
				bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(
						R.drawable.guide_tips_btn2);
				if (bitmapDrawable != null) {
					mDefaultActingBmp2 = bitmapDrawable.getBitmap();
				}
			}
			if (mDefaultImmobileBmp == null) {
				bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(
						R.drawable.guide_tips_btn1);
				if (bitmapDrawable != null) {
					mDefaultImmobileBmp = bitmapDrawable.getBitmap();
				}
			}
		}

		int picWidth = 0;
		if (mDefaultActingBmp1 != null) {
			picWidth = mDefaultActingBmp1.getWidth();
			mEnterAnimation = new TranslateAnimation(-picWidth, mLeft, mTop, mTop);
			mShowStatus = STATUS_ENTER;
			mEnterAnimation.setDuration(500);
			mEnterAnimation.setRepeatCount(0);
			mEnterAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);
			setAnimation(mEnterAnimation);
		}
		//		startAnimation(mEnterAnimation);
	}
	@SuppressLint("NewApi")
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		switch (visibility) {
			case View.VISIBLE :
				mShowStatus = STATUS_ENTER;
				mEnterAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);
				setAnimation(mEnterAnimation);
				mDoAction = false;
				break;

			default :
				break;
		}
		super.onVisibilityChanged(changedView, visibility);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			return false;
//		}
		float x = event.getX();
		float y = event.getY();
		if (StatusBarHandler.isHide()) {
			if (x > mLeft & x < mRight && y > mTop && y < mBottom) {
				return handleEvent(action, event);
			}
		} else {
			if (x > mLeft & x < mRight && y > mTop + StatusBarHandler.getStatusbarHeight()
					&& y < mBottom + StatusBarHandler.getStatusbarHeight()) {
				return handleEvent(action, event);
			}
		}
		mDoAction = false;
		return false;
	}

	private boolean handleEvent(int action, MotionEvent event) {
		if (action == MotionEvent.ACTION_DOWN) {
			mDoAction = true;
			return super.onTouchEvent(event);
		} else if (action == MotionEvent.ACTION_MOVE) {
			return false;
		}
		if (mDoAction) {
			mDoAction = false;
			return super.onTouchEvent(event);
		}
		return false;
	}
	
	@Override
	protected void onAnimationEnd() {
		if (mShowStatus == STATUS_ENTER) {
			mShowStatus = STATUS_ACTING;
			clearAnimation();
			invalidate();
		}
		super.onAnimationEnd();
	}

}
