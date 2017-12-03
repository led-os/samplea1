package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import java.util.LinkedList;
import java.util.Vector;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageCenterListItemView.ViewHolder;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean.MessageHeadBean;

/**
 * 消息中心的ListContainer
 * 
 * @author zengyingzhen
 * 
 */
public class MessageCenterListContainer extends AbsMessageCenterView {
	private IMessageCenterActionListener mListener;
	private int mStatus;

	private TextView mNoMsgtextview2;
	private TextView mNoMsgtext;
	private ListView mListContainer;
	private MessageCenterListAdapter mAdapter;
	private RelativeLayout mEmptyView;
	private LinkedList<MessageCenterListItemView> mItemViews;

	public MessageCenterListContainer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mStatus = MESSAGE_CENTER_VIEW_STATUS_NORMAL;
		init(context);
	}

	public MessageCenterListContainer(Context context, int status) {
		// TODO Auto-generated constructor stub
		super(context);
		mStatus = status;
		init(context);
	}

	public MessageCenterListContainer(Context context, int status,
			IMessageCenterActionListener listener) {
		// TODO Auto-generated constructor stub
		super(context);
		mStatus = status;
		mListener = listener;
		init(context);
	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.message_center_list, this);
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		mItemViews = new LinkedList<MessageCenterListItemView>();
		mListContainer = (ListView) findViewById(R.id.message_center_listView);
		mAdapter = new MessageCenterListAdapter(context, null);
		mListContainer.setAdapter(mAdapter);

		mNoMsgtextview2 = (TextView) findViewById(R.id.message_center_nomsgtextview2);
		mNoMsgtext = (TextView) findViewById(R.id.message_center_nomsgtext);
		mEmptyView = (RelativeLayout) findViewById(R.id.message_center_empty_msg);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListContainer.setOnItemClickListener(listener);
	}

	public void showEmptyMsgView(String str1, String str2) {
		mListContainer.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
		if (str1 != null) {
			mNoMsgtext.setText(str1);
		}
		if (str2 != null) {
			mNoMsgtextview2.setText(str2);
		}
	}

	public void showMsgListView() {
		mListContainer.setVisibility(View.VISIBLE);
		mEmptyView.setVisibility(View.GONE);
	}

	/**
	 * <br>
	 * 功能简述:更新list数据 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param context
	 * @param msgsList
	 */
	public void updateList(Context context, Vector<MessageHeadBean> msgsList) {
		// TODO Auto-generated method stub
		if (msgsList != null && msgsList.size() > 0) {
			showMsgListView();
			if (mAdapter == null) {
				mAdapter = new MessageCenterListAdapter(context, msgsList);
			} else {
				mAdapter.setAdapterData(msgsList);
			}
			mListContainer.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		} else {
			showEmptyMsgView(null, null);
		}

	}

	public MessageCenterListAdapter getListAdapter() {
		// TODO Auto-generated method stub
		if (mAdapter != null) {
			return mAdapter;
		} else {
			return null;
		}
	}

	public int getCurrentStatus() {
		return mStatus;
	}

	public void resetDeleteMsgCount() {
		// TODO Auto-generated method stub
		if (mAdapter != null) {
			mAdapter.resetDeleteMsgCount();
		}
	}

	public int getDeleteMsgCount() {
		// TODO Auto-generated method stub
		if (mAdapter != null) {
			return mAdapter.getDeleteMsgCount();
		}
		return 0;
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void statusChange(int statusID) {
		// TODO Auto-generated method stub
		if (mAdapter != null) {
			int first = mListContainer.getFirstVisiblePosition();
			int last = mListContainer.getLastVisiblePosition();
			int count = last - first + 1;
			for (int i = 0; i < mItemViews.size(); i++) {
				MessageCenterListItemView listItemView = mItemViews.get(i);
				if (i >= count) {
					// 看不到直接移动
					if (statusID == MESSAGE_CENTER_VIEW_STATUS_DELETE) {
						listItemView.extendMoveRight(false, statusID);
					} else if (statusID == MESSAGE_CENTER_VIEW_STATUS_NORMAL) {
						listItemView.extendMoveLeft(false, statusID);
					}
				} else {
					// 看到的做动画
					if (statusID == MESSAGE_CENTER_VIEW_STATUS_DELETE) {
						listItemView.extendMoveRight(true, statusID);
					} else if (statusID == MESSAGE_CENTER_VIEW_STATUS_NORMAL) {
						listItemView.extendMoveLeft(true, statusID);
					}
				}
			}
		}
		mStatus = statusID;
	}

	/**
	 * 适配器
	 * 
	 * @author zengyingzhen
	 * 
	 */
	public class MessageCenterListAdapter extends BaseAdapter {

		public Vector<MessageHeadBean> mMsgs;
		private Context mContext;
		private AsyncImageManager mImageManager;

		public MessageCenterListAdapter(Context context,
				Vector<MessageHeadBean> msgs) {
			mMsgs = msgs;
			mContext = context;
			mImageManager = AsyncImageManager.getDefaultInstance();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mMsgs == null) {
				return 0;
			}
			return mMsgs.size();
		}

		public void setAdapterData(Vector<MessageHeadBean> msgs) {
			mMsgs = msgs;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				MessageCenterListItemView listItemView = new MessageCenterListItemView(
						mContext, mStatus);
				convertView = listItemView;
			}
			if (mItemViews.contains(convertView)) {
				mItemViews.remove(convertView);
				mItemViews.addFirst((MessageCenterListItemView) convertView);
			} else {
				mItemViews.addFirst((MessageCenterListItemView) convertView);
			}
			convertView.setTag(getItem(position));
			final ViewHolder holder = ((MessageCenterListItemView) convertView)
					.getViewHolder();
			holder.mListItemLayout.clearAnimation();
			holder.mcheckBoxLayout.clearAnimation();
			holder.mListLayout.clearAnimation();
			// 处理checkbox
			holder.mCheckBox.setChecked(mMsgs.get(position).mIsReadyDelete);

			// 处理图片
			Bitmap bitmap = null;
			holder.mImageUrl = mMsgs.get(position).mMsgimgurl;
			if (mMsgs.get(position).mMsgimgurl != null
					&& !mMsgs.get(position).mMsgimgurl.equals("")) {
				bitmap = mImageManager.loadImage(
						ConstValue.sMESSAGE_CENTER_IMAGE_PATH,
						holder.mImageUrl.hashCode() + ".jpg", holder.mImageUrl,
						true, null, new AsyncImageLoadedCallBack() {

							@Override
							public void imageLoaded(Bitmap imageBitmap,
									String imgUrl) {
								// TODO Auto-generated method stub
								if (holder.mImageUrl.equals(imgUrl)) {
									holder.mBigImg.setImageBitmap(imageBitmap);
								}

							}
						});
				if (bitmap == null) {
					// 设置默认图片
					holder.mBigImg.setImageDrawable(mContext.getResources()
							.getDrawable(
									R.drawable.message_center_default_image));
				} else {
					holder.mBigImg.setImageBitmap(bitmap);
				}
				holder.mBigImg.setVisibility(VISIBLE);
			} else {
				holder.mBigImg.setVisibility(GONE);
			}

			// ------------处理图片 end

			if (mMsgs.get(position).misReaded) {
				// 已读
				holder.mTitle.setTextColor(mContext.getResources().getColor(
						R.color.message_center_title_readed_color));
				holder.mClockImg.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.message_center_clock_readed));
				holder.mTime.setTextColor(mContext.getResources().getColor(
						R.color.message_center_time_readed_color));
				holder.mReadTag.setVisibility(INVISIBLE);
				holder.mContent.setTextColor(mContext.getResources().getColor(
						R.color.message_center_title_readed_color));
				holder.mIsRead = true;
			} else {
				// 未读
				holder.mTitle.setTextColor(mContext.getResources().getColor(
						R.color.message_center_title_unread_color));
				holder.mClockImg.setImageDrawable(mContext.getResources()
						.getDrawable(R.drawable.message_center_clock_unread));
				holder.mTime.setTextColor(mContext.getResources().getColor(
						R.color.message_center_time_unread_color));
				holder.mReadTag.setVisibility(VISIBLE);
				holder.mContent.setTextColor(mContext.getResources().getColor(
						R.color.message_center_title_unread_color));
				holder.mIsRead = false;
			}
			holder.mTitle.setText(getTitle(position));
			String countent = getSummery(position);
			if (countent != null && !countent.equals("")) {
				holder.mContent.setText(countent);
				holder.mContent.setVisibility(VISIBLE);
			} else {
				holder.mContent.setVisibility(GONE);
			}

			String messageDate = getDate(position);
			if (messageDate != null) {
				String temp = MessageCenterActivity.compareDate(messageDate,
						mContext);
				holder.mTime.setText(temp);
			} else {
				holder.mTime.setText("");
			}

			return convertView;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return mMsgs.isEmpty();
		}

		public String getTitle(int position) {

			if (mMsgs == null || position > mMsgs.size()) {
				return "";
			}
			return mMsgs.get(position).mTitle;
		}

		public String getDate(int position) {
			if (mMsgs == null || position > mMsgs.size()) {
				return "";
			}
			return mMsgs.get(position).mMsgTimeStamp;
		}

		public String getImageUrl(int position) {
			if (mMsgs == null || position > mMsgs.size()) {
				return null;
			}
			return mMsgs.get(position).mMsgimgurl;
		}

		public String getSummery(int position) {
			if (mMsgs == null || position > mMsgs.size()) {
				return "";
			}
			return mMsgs.get(position).mSummery;
		}

		public void resetDeleteMsgCount() {
			// TODO Auto-generated method stub
			if (mMsgs == null) {
				return;
			}
			for (int i = 0; i < mMsgs.size(); i++) {
				MessageHeadBean bean = mMsgs.get(i);
				if (bean.misReaded) {
					bean.mIsReadyDelete = true;
				} else {
					bean.mIsReadyDelete = false;
				}
			}
		}

		public int getDeleteMsgCount() {
			// TODO Auto-generated method stub
			int deleteCount = 0;
			if (mMsgs != null) {
				for (int i = 0; i < mMsgs.size(); i++) {
					MessageHeadBean bean = mMsgs.get(i);
					if (bean.mIsReadyDelete) {
						deleteCount++;
					}
				}
			}

			return deleteCount;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (mMsgs == null || position > mMsgs.size()) {
				return null;
			}
			return mMsgs.get(position);
		}

	}

}
