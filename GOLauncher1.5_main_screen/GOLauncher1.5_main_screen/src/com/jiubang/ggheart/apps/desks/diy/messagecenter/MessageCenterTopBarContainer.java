package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
/**
 * 消息中心的TopBarContainer
 * @author zengyingzhen
 *
 */
public class MessageCenterTopBarContainer extends AbsMessageCenterView
		implements View.OnClickListener {
	private Context mContext;
	private IMessageCenterActionListener mListener = null;

	private int mStatus;

	public MessageCenterTopBarContainer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mStatus = MESSAGE_CENTER_VIEW_STATUS_NORMAL;
		init();
	}

	public MessageCenterTopBarContainer(Context context, int status) {
		// TODO Auto-generated constructor stub
		super(context);
		mContext = context;
		mStatus = status;
		init();
	}

	public MessageCenterTopBarContainer(Context context, int status,
			IMessageCenterActionListener listener) {
		// TODO Auto-generated constructor stub
		super(context);
		mListener = listener;
		mContext = context;
		mStatus = status;
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(mContext);
		inflater.inflate(R.layout.message_center_topbar, this);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		switch (mStatus) {
		case MESSAGE_CENTER_VIEW_STATUS_NORMAL:
			findViewById(R.id.message_center_topbar_back_layout)
					.setOnClickListener(this);
			findViewById(R.id.message_center_topbar_delete_btn)
					.setOnClickListener(this);
			break;
		case MESSAGE_CENTER_VIEW_STATUS_DELETE:
			ImageView confirm = (ImageView) findViewById(R.id.message_center_topbar_back);
			confirm.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.message_center_topbar_confirm));
			int padding = (int) getResources().getDimension(R.dimen.message_center_topbar_deltet_confirm_padding);
			confirm.setBackgroundResource(R.drawable.message_center_onclick_selector);
			confirm.setPadding(padding, 0, padding, 0);
			// 在删除状态下点击是确认删除
			confirm.setOnClickListener(this);

			ImageView line = (ImageView) findViewById(R.id.message_center_topbar_icon);
			line.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.message_center_topbar_line));

			RelativeLayout topbar = (RelativeLayout) findViewById(R.id.message_center_topbar);
			topbar.setBackgroundResource(R.drawable.message_center_topbar_bg_black);

			ImageView delete_open = (ImageView) findViewById(R.id.message_center_topbar_delete_btn);
//			delete_open.setImageDrawable(mContext.getResources().getDrawable(
//					R.drawable.message_center_topbar_delete_open));
//			delete_open.setOnClickListener(this);
			delete_open.setImageDrawable(null);
			delete_open.setVisibility(GONE);

			TextView title = (TextView) findViewById(R.id.message_center_topbar_title);
			title.setTextColor(Color.WHITE);
			title.setText("0 " + getResources().getString(R.string.message_center_selected));

			break;

		default:
			break;
		}
	}

	public int getCurrentStatus() {
		return mStatus;
	}

	public void showDeleteMsgCount(int deleteCount) {
		// TODO Auto-generated method stub
		if (mStatus == MESSAGE_CENTER_VIEW_STATUS_DELETE) {
			TextView title = (TextView) findViewById(R.id.message_center_topbar_title);
			title.setTextColor(Color.WHITE);
			title.setText(deleteCount + " " + getResources().getString(R.string.message_center_selected));
		}
	}

	@Override
	public void statusChange(int statusID) {
		// TODO Auto-generated method stub
		// 做动画
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.message_center_topbar_back_layout:
			// back
			if (mListener != null) {
				mListener
						.action(IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_BACK);
			}
			break;
		case R.id.message_center_topbar_back:
			
			if (mStatus == MESSAGE_CENTER_VIEW_STATUS_NORMAL) {
				// cancel
				if (mListener != null) {
					mListener
							.action(IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS);
				}
			} else if (mStatus == MESSAGE_CENTER_VIEW_STATUS_DELETE) {
				// 删除选中消息
				if (mListener != null) {
					mListener
							.action(IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_DELETE);
				}
			}
			break;
		case R.id.message_center_topbar_delete_btn:
			// 进入删除模式
			if (mListener != null) {
				mListener
						.action(IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS);
			}
			break;

		default:
			break;
		}
	}

	public void showDeleteBtn() {
		findViewById(R.id.message_center_topbar_delete_btn).setVisibility(
				View.VISIBLE);
	}

	public void dismissDeleteBtn() {
		findViewById(R.id.message_center_topbar_delete_btn).setVisibility(
				View.GONE);
	}

}
