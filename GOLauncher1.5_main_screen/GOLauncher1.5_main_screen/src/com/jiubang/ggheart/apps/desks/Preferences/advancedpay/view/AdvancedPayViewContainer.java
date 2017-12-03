package com.jiubang.ggheart.apps.desks.Preferences.advancedpay.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingAdvancedPayActivity;
import com.jiubang.ggheart.apps.desks.Preferences.advancedpay.IAdvancedPayListener;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.components.DeskTextView;

/**
 * 付费预览界面容器
 * @author yejijiong
 *
 */
public class AdvancedPayViewContainer extends ViewGroup {

	private DeskTextView mMessageTip;
	private DeskTextView mMessageSummary;
	private DeskTextView mSingleMessage;
	private AdvancedPayBaseView mAdvancedPayView;
	private Context mContext;
	private int mType;
	private int mTextPadding;
	
	public AdvancedPayViewContainer(Context context, int type, IAdvancedPayListener listener) {
		super(context);
		mContext = context;
		mType = type;
		mTextPadding = DrawUtils.dip2px(40);
		mAdvancedPayView = getAdvancedPayView(type, listener);
		addView(mAdvancedPayView);
		if (mType != DeskSettingAdvancedPayActivity.PAY_START_PAGE) {
			mMessageTip = new DeskTextView(mContext);
			mMessageSummary = new DeskTextView(mContext);
			mMessageTip.setGravity(Gravity.CENTER_HORIZONTAL);
			mMessageTip.setTextSize(18f);
			mMessageTip.setTextColor(0xFFFFFFFF);
			mMessageTip.setText(mAdvancedPayView.getMessageTip());
			mMessageTip.getPaint().setFakeBoldText(true);
			addView(mMessageTip);
			mMessageSummary.setGravity(Gravity.CENTER_HORIZONTAL);
			mMessageSummary.setSingleLine(false);
			mMessageSummary.setTextSize(14f);
			mMessageSummary.setTextColor(0xCCFFFFFF);
			mMessageSummary.setText(mAdvancedPayView.getMessageSummary());
			mMessageSummary.setPadding(mTextPadding, 0, mTextPadding, 0);
			addView(mMessageSummary);
		} else {
			mSingleMessage = new DeskTextView(mContext);
			mSingleMessage.setGravity(Gravity.CENTER_HORIZONTAL);
			mSingleMessage.setTextSize(16f);
			mSingleMessage.setTextColor(0xFFFFFFFF);
			mSingleMessage.setText(mAdvancedPayView.getSingleMessage());
			addView(mSingleMessage);
		}
	}

	private AdvancedPayBaseView getAdvancedPayView(int type, IAdvancedPayListener listener) {
		switch (type) {
			case DeskSettingAdvancedPayActivity.PAY_NO_ADS :
				return new AdvancedPayNoAdvertView(mContext, AdvancedPayBaseView.FINISH_TYPE_NONE,
						listener);
			case DeskSettingAdvancedPayActivity.PAY_EFFECTOR :
				return new AdvancedPayEffectorView(mContext,
						AdvancedPayBaseView.FINISH_TYPE_CONTINUE_REFRESH, listener);
			case DeskSettingAdvancedPayActivity.PAY_SECURITY_LOCK :
				return new AdvancedPaySafetyLockView(mContext,
						AdvancedPayBaseView.FINISH_TYPE_NONE, listener);
			case DeskSettingAdvancedPayActivity.PAY_SIDEBAR :
				return new AdvancedPaySidebarView(mContext, AdvancedPayBaseView.FINISH_TYPE_NONE,
						listener);
			case DeskSettingAdvancedPayActivity.PAY_GESTURE :
				return new AdvancedPayGestureView(mContext, AdvancedPayBaseView.FINISH_TYPE_NONE,
						listener);
			case DeskSettingAdvancedPayActivity.PAY_START_PAGE :
				return new AdvancedPayStartPageView(mContext);
			case DeskSettingAdvancedPayActivity.PAY_WALLPAPER_FILTER :
				return new AdvancedPayWallpaperFilterView(mContext,
						AdvancedPayBaseView.FINISH_TYPE_NONE, listener);
			default :
				return new AdvancedPayBaseView(mContext, AdvancedPayBaseView.FINISH_TYPE_NONE,
						listener);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int size = getChildCount();
		for (int i = 0; i < size; i++) {
			View childView = getChildAt(i);
			if (childView == null || childView.getLayoutParams() == null) {
				continue;
			}
			if (childView.getVisibility() != GONE) {
				childView.measure(widthMeasureSpec, heightMeasureSpec);
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int statusBarHeight = StatusBarHandler.getStatusbarHeight();
		int payViewTopPadding = DrawUtils.dip2px(12);
		int payViewBottomPadding = DrawUtils.dip2px(25); // 指示器高度
		int width = r - l;
		int height = b - t;
		int bottom = statusBarHeight;
		int top = 0;
		int tipTopPadding = DrawUtils.dip2px(75);
		int summaryTopPadding = DrawUtils.dip2px(15);
		if (mMessageTip != null) {
			top = bottom + tipTopPadding;
			bottom = top + mMessageTip.getMeasuredHeight();
			mMessageTip.layout(0, top, width, bottom);
		}
		if (mMessageSummary != null) {
			top = bottom + summaryTopPadding;
			bottom = top + mMessageSummary.getMeasuredHeight();
			mMessageSummary.layout(0, top, width, bottom);
		}
			
		int singleMessageTopPadding = DrawUtils.dip2px(280);
		if (mSingleMessage != null) {
			top = bottom + singleMessageTopPadding;
			bottom = top + mSingleMessage.getMeasuredHeight();
			mSingleMessage.layout(0, top, width, bottom);
		}
		top = bottom + payViewTopPadding;
		mAdvancedPayView.layout(0, top, width, height - payViewBottomPadding);
	}

	public AdvancedPayBaseView getAdvancedPayView() {
		return mAdvancedPayView;
	}
	
	//当为促销的时候，用来把相应的文字隐藏起来。
    public void setSingleMessage(boolean mPromotion) {
        if (mPromotion && mSingleMessage != null
                && mSingleMessage.getVisibility() == View.VISIBLE) {
            mSingleMessage.setVisibility(View.GONE);
        }
    }
}
