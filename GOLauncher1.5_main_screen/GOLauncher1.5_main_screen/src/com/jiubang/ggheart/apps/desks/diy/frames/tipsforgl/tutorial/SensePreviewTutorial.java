package com.jiubang.ggheart.apps.desks.diy.frames.tipsforgl.tutorial;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.core.framework.IFrameworkMsgId;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.components.DeskButton;
import com.jiubang.ggheart.components.DeskTextView;
import com.jiubang.ggheart.data.model.ScreenDataModel;

/**
 * 
 * 类描述:屏幕编辑提示
 * 功能详细描述:屏幕编辑的第一页提示所使用的自定义view，添加屏幕
 * 
 * @author  
 * @date  [2012-9-6]
 */
public class SensePreviewTutorial extends ViewGroup implements View.OnClickListener {

	public final static int MAX_CARD_NUMS = 9;
	private DeskButton mOk;
	private DeskTextView mEditScreenTitle;
	private DeskTextView mTextHome;
	private DeskTextView mTextAdd;
	private int mImageAddW;
	private int mImageAddH;
	private Context mContext;
	private int mScreenCnt;
	private static final float TEXTSIZE = 14.67f;
	private int mCurrentScreen = 0;
	private CircleLightView mCircleLight;
	private int mRadius;
	private int mCircleX;
	private int mCircleY;

	// CardLayout底图使用NinePatch图片，其边框间隙如下
	private int mCardPaddingLeft = 9;

	private int mCardWidth; // CardLayout的宽度
	private int mCardHeight; // CardLayout的高度

	private int mMarginTop; // SenseWorkspace的上边距
	private int mMarginLeft; // SenseWorkspace的左边距

	private int mSpaceX; // CardLayout的水平间隙
	private int mSpaceY; // CardLayout的垂直间隙

	private int mHomeImageViewTop;

	public SensePreviewTutorial(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public SensePreviewTutorial(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		mRadius = getResources().getDimensionPixelSize(R.dimen.screenedit_light_circle_radius);

		ScreenDataModel dataModel = new ScreenDataModel(context);
		mScreenCnt = dataModel.getScreenCount();

		mCircleLight = new CircleLightView(context);

		int[] xy = new int[2];
		computeAddXY(xy);
		mCircleX = xy[0];
		mCircleY = xy[1];
		CircleLightView.setPlusXY(xy[0], xy[1]);
		addView(mCircleLight);

		mTextAdd = new DeskTextView(context);
		mTextAdd.setTextSize(TEXTSIZE);
		mTextAdd.setTextColor(0xffd4d4d4);
		mTextAdd.setText(R.string.preview_add_tutoria);
		if (mScreenCnt >= MAX_CARD_NUMS) {
			mTextAdd.setText(R.string.too_many_screen_tip);
		}
		addView(mTextAdd);

		mTextHome = new DeskTextView(context);
		mTextHome.setTextSize(TEXTSIZE);
		mTextHome.setTextColor(0xffd4d4d4);
		mTextHome.setText(R.string.preview_sethome_tutoria);
		addView(mTextHome);

		mEditScreenTitle = new DeskTextView(context);
		mEditScreenTitle.setText(R.string.preview_edit_screen_title);
		mEditScreenTitle.setTextColor(Color.rgb(175, 248, 112));
		mEditScreenTitle.setTextSize(20F);
		addView(mEditScreenTitle);

		mOk = new DeskButton(context);
		mOk.setText(R.string.tip_btn_txt);
		mOk.setBackgroundResource(R.drawable.gotit_state_background);
		mOk.setTextColor(Color.WHITE);
		mOk.setTextSize(17.3F);
		mOk.setGravity(Gravity.CENTER);
		mOk.setOnClickListener(this);
		addView(mOk);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		boolean isPortait = GoLauncherActivityProxy.isPortait();
		int count = getChildCount();
		if (count > 0) {
			int screenCount = mScreenCnt % 9;
			for (int i = 0; i < count; i++) {
				View view = getChildAt(i);
				if (view == mEditScreenTitle) {
					int left = (int) getResources().getDimension(
							R.dimen.screenedit_title_marginleft);
					int top = (int) getResources().getDimension(R.dimen.screenfolder_title_to_top)
							- StatusBarHandler.getStatusbarHeightByActivity();
					view.layout(left, top, r, b - mEditScreenTitle.getWidth());
				} else if (view == mTextHome) {
					int homeX;
					int homeY = mMarginTop + mHomeImageViewTop;
					if (mScreenCnt <= 2) {
						homeX = mCardWidth / 2 + mMarginLeft;

					} else {
						homeX = 2 * mCardWidth + mCardWidth / 2 + 2 * mSpaceX + mMarginLeft;
					}
					String text = mTextHome.getText().toString();
					int rowCount = (int) (text.length() * mTextHome.getTextSize()
							/ (r - (homeX - 2 * mRadius)) + 0.5f);
					if (rowCount == 0) {
						rowCount++;
					}
					view.layout(homeX - 2 * mRadius,
							homeY - mRadius - rowCount * (int) mTextHome.getTextSize(), r, b);
				} else if (view == mTextAdd) {
					if (mScreenCnt < MAX_CARD_NUMS) {
						if (mScreenCnt / 9 != mCurrentScreen) {
							view.setVisibility(View.GONE);
							continue;
						}
					}
					if (mScreenCnt < MAX_CARD_NUMS) {
						view.layout(mCircleX - mRadius / 2 - mRadius, mCircleY + mRadius
								+ (int) mTextAdd.getTextSize() * 2, r, b);
					}
					/*
					 * else { view.layout(x,
					 * mImgAdd.getTop(),SenseWorkspace.CardWidth*2
					 * +SenseWorkspace.SpaceX*2, b); }
					 */
				} else if (view == mOk) {
					int width = getContext().getResources().getDimensionPixelSize(
							R.dimen.screenfolder_button_width);
					int height = getContext().getResources().getDimensionPixelSize(
							R.dimen.screenfolder_button_height);
					int left = 0;
					int top = 0;

					top = mMarginTop + mCardHeight * 2/*
																					* +
																					* SenseWorkspace
																					* .
																					* MarginTop
																					* /
																					* 5
																					*/;
					if (mScreenCnt >= MAX_CARD_NUMS) {
						left = mCardWidth;
						top = mMarginTop + mCardHeight * 2 + mSpaceY * 2 + mCardHeight * 3 / 4;
					} else if (screenCount <= 5 || screenCount == 8) {
						// left =
						// getContext().getResources().getDimensionPixelSize(R.dimen.screenedit_button_marginleft);
						left = mCardPaddingLeft;
					} else {
						left = 2 * mCardWidth + mSpaceX;
					}

					view.layout(left, top, left + width, top + height);
				} else if (view == mCircleLight) {
					int navBarHeight = Machine.IS_SDK_ABOVE_KITKAT
							? DrawUtils.getNavBarHeight()
							: 0;
					view.layout(0, 0, GoLauncherActivityProxy.getScreenWidth(),
							GoLauncherActivityProxy.getScreenHeight() + navBarHeight);
				}
			}

		}
	}

	private void computeAddXY(int[] xy) {
		int x = 0;
		int y = 0;

		if (mScreenCnt >= 9) {
			mCircleLight.is9Screen(true);
			x = mCardWidth * 2 + mSpaceX * 2 + mMarginLeft + (mCardWidth - mImageAddW) / 2;
			y = mCardHeight * 2 + mSpaceY * 2 + (mCardHeight - mImageAddH) / 2 + +mMarginTop;

		} else {
			switch (mScreenCnt % 9) {
				case 1 :
					x = mCardWidth + mCardWidth / 2 + mSpaceX + mMarginLeft;
					y = mCardHeight / 2 + mMarginTop;
					break;
				case 2 :
					x = mCardWidth * 2 + mCardWidth / 2 + mSpaceX * 2 + mMarginLeft;
					y = mCardHeight / 2 + mMarginTop;
					break;
				case 3 :
					x = mCardWidth / 2 + mMarginLeft;
					y = mCardHeight + mCardHeight / 2 + mSpaceY + mMarginTop;
					break;
				case 4 :
					x = mCardWidth + mCardWidth / 2 + mSpaceX + mMarginLeft;
					y = mCardHeight + mCardHeight / 2 + mSpaceY + mMarginTop;
					break;
				case 5 :
					x = mCardWidth * 2 + mCardWidth / 2 + mSpaceX * 2 + mMarginLeft;
					y = mCardHeight + mCardHeight / 2 + mSpaceY + mMarginTop;
					break;
				case 6 :
					x = mCardWidth / 2 + mMarginLeft;
					y = mCardHeight * 2 + mCardHeight / 2 + mSpaceY * 2 + mMarginTop;
					break;
				case 7 :
					x = mCardWidth + mCardWidth / 2 + mSpaceX + mMarginLeft;
					y = mCardHeight * 2 + mCardHeight / 2 + mSpaceY * 2 + mMarginTop;
					break;
				case 8 :
				case 0 :
					x = mCardWidth * 2 + mCardWidth / 2 + mSpaceX * 2 + mMarginLeft;
					y = mCardHeight * 2 + mCardHeight / 2 + mSpaceY * 2 + mMarginTop;
				default :
					break;
			}
			//根据卡片的位置去极端圆的位置。（2014-01-15）
			//					y = y - StatusBarHandler.getStatusbarHeight();

			xy[0] = x;
			xy[1] = y;
		}
	}
	/*
	 * private int computeAddTextX(int txtWidth){ int x=0; if(mScreenCnt >=
	 * SenseWorkspace.MAX_CARD_NUMS) { return SenseWorksp@Overrideace.CardWidth/3; }
	 * switch (mScreenCnt%9) { case 1: case 4: case 7: x =
	 * mImgAdd.getLeft()-SenseWorkspace.CardWidth/2; break; case 2: case 5: case
	 * 8: x = mImgAdd.getLeft()-txtWidth/2; break; case 3: case 6: x =
	 * mImgAdd.getLeft(); break; default: break; } return x; }
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	public void onClick(View v) {
		if (mCircleLight != null) {
			mCircleLight.recycle();
			mCircleLight = null;
		}
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, IFrameworkMsgId.REMOVE_FRAME,
				IDiyFrameIds.GUIDE_GL_FRAME, null, null);

	}

	public void setPreviewCurrentScreen(int screen, Object obj) {
		mCurrentScreen = screen;
		ArrayList<Integer> list = (ArrayList<Integer>) obj;
		if (list != null && list.size() > 0) {
			mCardPaddingLeft = list.get(0);

			mCardWidth = list.get(1); // CardLayout的宽度
			mCardHeight = list.get(2); // CardLayout的高度

			mMarginTop = list.get(3); // SenseWorkspace的上边距
			mMarginLeft = list.get(4); // SenseWorkspace的左边距

			mSpaceX = list.get(5); // CardLayout的水平间隙
			mSpaceY = list.get(6); // CardLayout的垂直间隙

			mHomeImageViewTop = list.get(7);

			int homeX;
			//mMarginTop 在GLSense中没有减去了状态栏的高度计算的。根据卡片的位置去极端圆的位置。（2014-01-15）
//			int homeY = mMarginTop + mHomeImageViewTop;
			int homeY = mMarginTop + mCardHeight;
//			homeY -= StatusBarHandler.getStatusbarHeight();
			if (mScreenCnt <= 2) {
				homeX = mCardWidth / 2 + mMarginLeft;
			} else {
				homeX = 2 * mCardWidth + mCardWidth / 2 + 2 * mSpaceX + mMarginLeft;
			}
			CircleLightView.setHomeXY(homeX, homeY);
			int[] xy = new int[2];
			computeAddXY(xy);
			mCircleX = xy[0];
			mCircleY = xy[1];
			CircleLightView.setPlusXY(xy[0], xy[1]);
		}
	}
}
