package com.jiubang.ggheart.admob;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gau.go.launcherex.R;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-13]
 */
public class GoAdView extends FrameLayout implements OnClickListener, AdListener {

	public GoAdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GoAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GoAdView(Context context) {
		super(context);
	}
	
	private AdView mAdView;
	private ImageView mCloseView;
	private AdListener mAdListener;
	private boolean mAdded;
	private Activity mActivity;
	private OnAdDeleteListener mDeleteListener;
	private AdRequest mAdRequest;
	private int mPosId;
	private int mPid;
	
	public void initViews(Activity activity, int pid, int posId) {
		mActivity = activity;
		mAdView = new AdView(activity, AdSize.BANNER, AdConstanst.mapPublishId(pid, posId));
		mAdRequest = new AdRequest();
//		mAdRequest.addTestDevice("226BD33C756F0F8663D9255C74F1C592");
		mAdView.loadAd(mAdRequest);
		mAdView.setAdListener(this);
		addView(mAdView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mPid = pid;
		mPosId = posId;
	}
	
	public void setNewInfo(int pid, int posId) {
		mPid = pid;
		mPosId = posId;
	}
	
	private void addCloseView() {
		if (mActivity == null) {
			return;
		}
		mCloseView = new ImageView(mActivity);
		mCloseView.setBackgroundResource(R.drawable.go_adview_close_selector);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 0;
		params.leftMargin = 0;
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		mCloseView.setOnClickListener(this);
		addView(mCloseView, params);
	}
	
	public void setAdListener(AdListener adListener) {
		mAdListener = adListener;
	}
	
	public void setOnDeleteListener(OnAdDeleteListener listener) {
		mDeleteListener = listener;
	}
	
	public void destroy() {
		if (mAdView != null) {
			mAdView.stopLoading();
			mAdView.destroy();
			mAdView = null;
		}
		removeAllViews();
		mActivity = null;
	}

	@Override
	public void onClick(View v) {
		if (v == mCloseView) {
			if (mActivity != null) {
				AdController.getController(mActivity).checkDelete(v.getContext());  // 必须在回调之前执行,因为mActivity会被清理掉
			}
			if (mDeleteListener != null) {
				mDeleteListener.onDelete(this);
				mDeleteListener.onDeleteView(this, mPid, mPosId);
			}
			if (getParent() != null && getParent() instanceof ViewGroup) {
				((ViewGroup) getParent()).removeView(this);
			}
			if (mActivity != null) {
				AdViewStatistics.getStatistics(mActivity).appendAdClose(mActivity,
						mPid, mPosId, 1);
			}
		}
	}

	@Override
	public void onDismissScreen(Ad arg0) {
		if (mAdListener != null) {
			mAdListener.onDismissScreen(arg0);
		}
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		if (mAdListener != null) {
			mAdListener.onFailedToReceiveAd(arg0, arg1);
		}
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		if (mAdListener != null) {
			mAdListener.onLeaveApplication(arg0);
		}
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		if (mAdListener != null) {
			mAdListener.onPresentScreen(arg0);
		}
		if (mActivity != null) {
			AdViewStatistics.getStatistics(mActivity).appendAdClick(mActivity,
					mPid, mPosId, 1);
		}
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		if (!mAdded) {
			addCloseView();
			mAdded = true;
		}
		if (mAdListener != null) {
			mAdListener.onReceiveAd(arg0);
		}
	}
	
	
}
