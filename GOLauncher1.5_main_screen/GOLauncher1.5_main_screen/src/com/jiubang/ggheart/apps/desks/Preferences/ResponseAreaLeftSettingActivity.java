package com.jiubang.ggheart.apps.desks.Preferences;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;

/**
 * 
 * @author liangdaijian
 *
 */
public class ResponseAreaLeftSettingActivity extends DeskSettingBaseActivity {
	private SliderSettings mSettings = null;

	private View mMoveView;
	private View mMoveView2;

	private ImageView mTopView;
	private ImageView mCenterView;
	private ImageView mBottomView;

	private int mWidth;
	private int mHeight;
	private boolean mHasMessue;

	private ViewResponAreaInfo mViewResponAreaInfo;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT > 8) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		setContentView(R.layout.response_area_setting);

		initSettingItem();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initSettingItem() {
		mSettings = SliderSettings.getInstence(getApplicationContext());
		mViewResponAreaInfo = mSettings.getResponAreaInfoLeft();

		mMoveView = findViewById(R.id.go_lock_widegt_area_move);
		mMoveView2 = findViewById(R.id.go_lock_widegt_area_move2);
		mMoveView2.setVisibility(View.INVISIBLE);

		mTopView = (ImageView) findViewById(R.id.go_lock_widegt_area_top);
		mCenterView = (ImageView) findViewById(R.id.go_lock_widegt_area_center);
		mBottomView = (ImageView) findViewById(R.id.go_lock_widegt_area_bottom);

		final View mainView = findViewById(R.id.go_lock_widegt_area_setting);
		mainView.setOnTouchListener(new View.OnTouchListener() {
			int mType = -1;
			float mLastX, mLastY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (x >= mTopView.getLeft() - (mTopView.getWidth() >> 1)
							&& x <= mTopView.getRight() + (mTopView.getWidth() >> 1)
							&& y >= mTopView.getTop() - (mTopView.getHeight() >> 1)
							&& y <= mTopView.getBottom() + (mTopView.getHeight() >> 1)) {
						mType = 0;
					} else if (x >= mCenterView.getLeft() - (mCenterView.getWidth() >> 1)
							&& x <= mCenterView.getRight() + (mCenterView.getWidth() >> 1)
							&& y >= mCenterView.getTop() - (mCenterView.getHeight() >> 1)
							&& y <= mCenterView.getBottom() + (mCenterView.getHeight() >> 1)) {
						mType = 1;
					} else if (x >= mBottomView.getLeft() - (mBottomView.getWidth() >> 1)
							&& x <= mBottomView.getRight() + (mBottomView.getWidth() >> 1)
							&& y >= mBottomView.getTop() - (mBottomView.getHeight() >> 1)
							&& y <= mBottomView.getBottom() + (mBottomView.getHeight() >> 1)) {
						mType = 2;
					} else if (x >= mMoveView.getLeft() && x <= mMoveView.getRight()
							&& y >= mMoveView.getTop() && y <= mMoveView.getBottom()) {
						//						mType = 3;
					} else if (x >= mMoveView2.getLeft() && x <= mMoveView2.getRight()
							&& y >= mMoveView2.getTop() && y <= mMoveView2.getBottom()) {
						mType = 3;
					}

					int leng0 = (int) ((x - (mTopView.getLeft() + mTopView.getRight()) / 2)
							* (x - (mTopView.getLeft() + mTopView.getRight()) / 2) + (y - (mTopView
							.getTop() + mTopView.getBottom()) / 2)
							* (y - (mTopView.getTop() + mTopView.getBottom()) / 2));
					int leng1 = (int) ((x - (mCenterView.getLeft() + mCenterView.getRight()) / 2)
							* (x - (mCenterView.getLeft() + mCenterView.getRight()) / 2) + (y - (mCenterView
							.getTop() + mCenterView.getBottom()) / 2)
							* (y - (mCenterView.getTop() + mCenterView.getBottom()) / 2));
					int leng2 = (int) ((x - (mBottomView.getLeft() + mBottomView.getRight()) / 2)
							* (x - (mBottomView.getLeft() + mBottomView.getRight()) / 2) + (y - (mBottomView
							.getTop() + mBottomView.getBottom()) / 2)
							* (y - (mBottomView.getTop() + mBottomView.getBottom()) / 2));
					int leng = Math.min(Math.min(leng0, leng1), leng2);
					if (mType >= 0 && mType != 3) {
						if (leng == leng0) {
							mType = 0;
							if (leng == leng2) {
								if (mMoveView.getTop() < mHeight >> 1) {
									mType = 2;
								}
							}
						} else if (leng == leng1) {
							mType = 1;
						} else if (leng == leng2) {
							mType = 2;
						}
					}
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					int left = mMoveView.getLeft();
					int right = mMoveView.getRight();
					int top = mMoveView.getTop();
					int bottom = mMoveView.getBottom();

					if (mType == 0) {
						top += (int) (y - mLastY);
						if (top < 0) {
							top = 0;
						} else if (top > bottom - dip2px(30)) {
							top = bottom - dip2px(30);
						}
					} else if (mType == 1) {
						right += (int) (x - mLastX);
						if (right < left) {
							right = left;
						} else if (right > (int) (mWidth * 0.1f)) {
							right = (int) (mWidth * 0.1f);
						}
						if (right < Math.min(dip2px(5), (int) (mWidth * 0.02))) {
							right = Math.min(dip2px(5), (int) (mWidth * 0.02));
						}
					} else if (mType == 2) {
						bottom += (int) (y - mLastY);
						if (bottom > mHeight) {
							bottom = mHeight;
						} else if (top + dip2px(30) > bottom) {
							bottom = top + dip2px(30);
						}
					} else if (mType == 3) {
						top += (int) (y - mLastY);
						bottom += (int) (y - mLastY);
						if (top < 0) {
							top = 0;
							bottom = top + mMoveView.getHeight();
						}
						if (bottom > mHeight) {
							bottom = mHeight;
							top = bottom - mMoveView.getHeight();
						}
					}

					mMoveView.layout(left, top, right, bottom);

					mTopView.layout((int) (right - mTopView.getWidth() / 2f),
							(int) (top - mTopView.getHeight() / 2f),
							(int) (right + mTopView.getWidth() / 2f),
							(int) (top + mTopView.getHeight() / 2f));
					mCenterView.layout((int) (right - mCenterView.getWidth() / 2f),
							(int) (((top + bottom) >> 1) - mCenterView.getHeight() / 2f),
							(int) (right + mCenterView.getWidth() / 2f),
							(int) (((top + bottom) >> 1) + mCenterView.getHeight() / 2f));
					mBottomView.layout((int) (right - mBottomView.getWidth() / 2f),
							(int) (bottom - mBottomView.getHeight() / 2f),
							(int) (right + mBottomView.getWidth() / 2f),
							(int) (bottom + mBottomView.getHeight() / 2f));
				}

				else {
					mType = -1;
					mViewResponAreaInfo.setLeftAreaX((float) mMoveView.getLeft() / mWidth);
					mViewResponAreaInfo.setLeftAreaY((float) mMoveView.getTop() / mHeight);
					mViewResponAreaInfo.setLeftAreaWidth((float) mMoveView.getWidth() / mWidth);
					mViewResponAreaInfo.setLeftAreaHeight((float) mMoveView.getHeight() / mHeight);
					mSettings.setResponAreaInfoLeft(mViewResponAreaInfo);
					setArea();
				}
				mLastX = x;
				mLastY = y;
				return true;
			}
		});
		ViewTreeObserver vto2 = mainView.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (!mHasMessue) {
					mainView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					mWidth = mainView.getWidth();
					mHeight = mainView.getHeight();
					mHasMessue = true;
					setArea();
				}
			}
		});

		mHasMessue = false;
	}

	private int dip2px(float dipValue) {
		return (int) (dipValue * this.getResources().getDisplayMetrics().density + 0.5f);
	}

	private void setArea() {
		try {
			FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
					(int) (mWidth * mViewResponAreaInfo.getLeftAreaWidth()),
					(int) (mHeight * mViewResponAreaInfo.getLeftAreaHeight()));
			p.gravity = Gravity.TOP | Gravity.LEFT;
			p.leftMargin = (int) (mWidth * mViewResponAreaInfo.getLeftAreaX());
			p.topMargin = (int) (mHeight * mViewResponAreaInfo.getLeftAreaY());
			mMoveView.setLayoutParams(p);

			FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(dip2px(46 / 1.5f),
					dip2px(46 / 1.5f));
			p1.gravity = Gravity.TOP | Gravity.LEFT;
			p1.leftMargin = (int) (mWidth
					* (mViewResponAreaInfo.getLeftAreaX() + mViewResponAreaInfo.getLeftAreaWidth()) - mTopView
					.getWidth() / 2f);
			p1.topMargin = (int) (mHeight * mViewResponAreaInfo.getLeftAreaY() - mTopView
					.getHeight() / 2f);
			mTopView.setLayoutParams(p1);

			FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(dip2px(45 / 1.5f),
					dip2px(45 / 1.5f));
			p2.gravity = Gravity.TOP | Gravity.LEFT;
			p2.leftMargin = (int) (mWidth
					* (mViewResponAreaInfo.getLeftAreaX() + mViewResponAreaInfo.getLeftAreaWidth()) - mCenterView
					.getWidth() / 2f);
			p2.topMargin = (int) (mHeight
					* (mViewResponAreaInfo.getLeftAreaY() + mViewResponAreaInfo.getLeftAreaHeight() / 2) - mCenterView
					.getHeight() / 2f);
			mCenterView.setLayoutParams(p2);

			FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(dip2px(46 / 1.5f),
					dip2px(46 / 1.5f));
			p3.gravity = Gravity.TOP | Gravity.LEFT;
			p3.leftMargin = (int) (mWidth
					* (mViewResponAreaInfo.getLeftAreaX() + mViewResponAreaInfo.getLeftAreaWidth()) - mBottomView
					.getWidth() / 2f);
			p3.topMargin = (int) (mHeight
					* (mViewResponAreaInfo.getLeftAreaY() + mViewResponAreaInfo.getLeftAreaHeight()) - mBottomView
					.getHeight() / 2f);
			mBottomView.setLayoutParams(p3);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}
