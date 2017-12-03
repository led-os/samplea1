package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;

/**
 * 消息中心内容列表的ListItemView
 * 
 * @author zengyingzhen
 * 
 */
public class MessageCenterListItemView extends AbsMessageCenterView {
	private ViewHolder mHolder;
	private int mStatus = IMessageCenterViewStatus.MESSAGE_CENTER_VIEW_STATUS_NORMAL;

	public MessageCenterListItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

//	public MessageCenterListItemView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		// TODO Auto-generated constructor stub
//		init(context);
//	}
//
//	public MessageCenterListItemView(Context context, AttributeSet attrs,
//			int defStyle) {
//		super(context, attrs, defStyle);
//		// TODO Auto-generated constructor stub
//		init(context);
//	}

	public MessageCenterListItemView(Context context, int status) {
		// TODO Auto-generated constructor stub
		super(context);
		mStatus = status;
		init(context);

	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		LayoutInflater.from(context).inflate(
				R.layout.message_center_listcontainer_listitem, this);
		mHolder = new ViewHolder();
		mHolder.mTitle = (TextView) findViewById(R.id.messagetitle);
		mHolder.mClockImg = (ImageView) findViewById(R.id.message_center_clock);
		mHolder.mTime = (TextView) findViewById(R.id.messagestamp);
		mHolder.mBigImg = (ImageView) findViewById(R.id.message_center_listitem_bigimage);
		mHolder.mContent = (TextView) findViewById(R.id.message_center_listitem_content);
		mHolder.mCheckBox = (CheckBox) findViewById(R.id.message_center_listitem_checkbox);
		mHolder.mReadTag = (ImageView) findViewById(R.id.readtag);
		mHolder.mListItemLayout = (RelativeLayout) findViewById(R.id.message_center_listItem_layout);
		mHolder.mcheckBoxLayout = (RelativeLayout) findViewById(R.id.message_center_checkbox_layout);
		mHolder.mListLayout = (RelativeLayout) findViewById(R.id.message_center_listItem);
		mHolder.mListLayout.setPadding(
				getResources().getDimensionPixelOffset(
						R.dimen.message_center_list_container_padding_left), 0,
				0, 0);
		int width = mWidth - getResources().getDimensionPixelOffset(R.dimen.message_center_list_container_item_padding) * 2;
		int hight = (int) (width / 2.05);
		mHolder.mBigImg.setLayoutParams(new LayoutParams(width, hight));
		mHolder.mStatus = mStatus;
		setItemLayout(mStatus);
	}

	public void extendMoveRight(boolean isAnimation, int statusID) {
		mHolder.mCheckBox.setVisibility(VISIBLE);
		final int scrollRightToX = getResources().getDimensionPixelOffset(
				R.dimen.message_center_list_container_scroll_right_dimen);
		if (isAnimation) {
			float moveDimension = getResources().getDimension(
					R.dimen.message_center_list_container_move_right_dimen);
			Animation animation = new TranslateAnimation(0, moveDimension, 0, 0);
			animation.setFillAfter(true);
			animation.setDuration(400);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {

					mHolder.mcheckBoxLayout.clearAnimation();
					mHolder.mListItemLayout.clearAnimation();
					// holder.mTest.clearAnimation();
					mHolder.mListLayout.scrollTo(scrollRightToX, 0);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

			});
			mHolder.mcheckBoxLayout.startAnimation(animation);
			mHolder.mListItemLayout.startAnimation(animation);
			// holder.mTest.startAnimation(animation);

			mHolder.mCheckBox.setVisibility(View.VISIBLE);
			if (mHolder.mIsRead) {
				mHolder.mCheckBox.setChecked(true);
			} else {
				mHolder.mCheckBox.setChecked(false);
			}
		} else {
			mHolder.mListLayout.scrollTo(scrollRightToX, 0);
		}
		mHolder.mStatus = statusID;
	}

	public void extendMoveLeft(boolean isAnimation, int statusID) {
		final int scrollLeftToX = getResources().getDimensionPixelOffset(
				R.dimen.message_center_list_container_scroll_left_dimen);
		if (isAnimation) {
			float moveDimension = getResources().getDimension(
					R.dimen.message_center_list_container_move_left_dimen);
			Log.d("zyz", "moveLeftDimension:" + moveDimension);
			Log.d("zyz", "scrollLeftToX:" + scrollLeftToX);
			Animation animation = new TranslateAnimation(0, moveDimension, 0, 0);
			animation.setFillAfter(true);
			animation.setDuration(400);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					mHolder.mcheckBoxLayout.clearAnimation();
					mHolder.mListItemLayout.clearAnimation();
					// holder.mTest.clearAnimation();
					mHolder.mListLayout.scrollTo(scrollLeftToX, 0);
					mHolder.mCheckBox.setVisibility(INVISIBLE);
				}
			});
			// holder.mTest.startAnimation(animation);
			mHolder.mcheckBoxLayout.startAnimation(animation);
			mHolder.mListItemLayout.startAnimation(animation);
		} else {
			mHolder.mListLayout.scrollTo(scrollLeftToX, 0);
			mHolder.mCheckBox.setVisibility(INVISIBLE);
		}
		mHolder.mStatus = statusID;
	}

	public void extend() {
		// Animation animation = new TranslateAnimation(0, 200, 0, 0);
		// animation.setFillAfter(true);
		// animation.setDuration(500);
		// mcheckBoxLayout.startAnimation(animation);
		// mListItemLayout.startAnimation(animation);
	}

	public void extend2() {
		// Animation animation = new TranslateAnimation(200, 0, 0, 0);
		// animation.setFillAfter(true);
		// animation.setDuration(500);
		// mcheckBoxLayout.startAnimation(animation);
		// mListItemLayout.startAnimation(animation);
	}

	public ViewHolder getViewHolder() {
		return mHolder;
	}

	/**
	 * ViewHolder
	 * 
	 * @author zengyingzhen
	 * 
	 */
	public class ViewHolder {
		public int mStatus;
		public boolean mIsRead;
		public ImageView mReadTag;
		public TextView mTitle;
		public ImageView mClockImg;
		public TextView mTime;
		public ImageView mBigImg;
		public String mImageUrl;
		public TextView mContent;
		public CheckBox mCheckBox;
		public RelativeLayout mListItemLayout;
		public RelativeLayout mcheckBoxLayout;
		public RelativeLayout mListLayout;
	}

	public void setItemLayout(int stauts) {
		// TODO Auto-generated method stub
		if (mStatus == IMessageCenterViewStatus.MESSAGE_CENTER_VIEW_STATUS_DELETE) {
			mHolder.mListLayout.scrollTo(-200, 0);
		} else if (mStatus == IMessageCenterViewStatus.MESSAGE_CENTER_VIEW_STATUS_NORMAL) {
			mHolder.mListLayout.scrollTo(0, 0);
		}
	}

	@Override
	public void statusChange(int statusID) {
		// TODO Auto-generated method stub
		
	}

}