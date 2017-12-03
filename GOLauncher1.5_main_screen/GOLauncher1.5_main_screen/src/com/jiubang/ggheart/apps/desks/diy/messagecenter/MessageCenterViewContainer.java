package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import java.util.Vector;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageCenterListContainer.MessageCenterListAdapter;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean.MessageHeadBean;
/**
 * 消息中心容器
 * @author zengyingzhen
 *
 */
public class MessageCenterViewContainer extends AbsMessageCenterView {
	private MessageCenterTopBarContainer mTopBarContainer;
	private MessageCenterListContainer mListContainer;
	private MessageViewFactory mViewFactory;
	private RelativeLayout mTopLayout;
	private RelativeLayout mListLayout;

	public MessageCenterViewContainer(Context context) {
		super(context);
		init(context, null);
	}

	public MessageCenterViewContainer(Context context,
			IMessageCenterActionListener listener) {
		// TODO Auto-generated constructor stub
		super(context);
		init(context, listener);
	}

	private void init(Context context, IMessageCenterActionListener listener) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.message_center_container, this);
		mViewFactory = new MessageViewFactory(context, listener);
		mTopBarContainer = (MessageCenterTopBarContainer) mViewFactory
				.createView(ConstValue.CONTAINER_TOPBAR,
						MESSAGE_CENTER_VIEW_STATUS_NORMAL);
		mListContainer = (MessageCenterListContainer) mViewFactory.createView(
				ConstValue.CONTAINER_LIST, MESSAGE_CENTER_VIEW_STATUS_NORMAL);
		mTopLayout = (RelativeLayout) findViewById(R.id.message_center_topbar_layout);
		mListLayout = (RelativeLayout) findViewById(R.id.message_center_list_layout);
		mTopLayout.addView(mTopBarContainer);
		mListLayout.addView(mListContainer);
	}

	/**
	 * <br>
	 * 功能简述:显示没有消息的页面 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param str1
	 *            提示语1
	 * @param str2
	 *            提示语2
	 */
	public void showEmptyList(String str1, String str2) {
		// TODO Auto-generated method stub
		mListContainer.showEmptyMsgView(str1, str2);
	}

	/**
	 * <br>
	 * 功能简述:显示listview <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void showMsgList() {
		mListContainer.showMsgListView();
	}

	/**
	 * <br>
	 * 功能简述:更新list的数据 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param context
	 * @param msgsList
	 */
	public void updateList(Context context, Vector<MessageHeadBean> msgsList) {
		// TODO Auto-generated method stub
		mListContainer.updateList(context, msgsList);
		if (msgsList != null && msgsList.size() > 0) {
			mTopBarContainer.showDeleteBtn();
		} else {
			mTopBarContainer.dismissDeleteBtn();
		}
	}

	/**
	 * <br>
	 * 功能简述: <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param listener
	 */
	public void setListItemOnClickListener(OnItemClickListener listener) {
		mListContainer.setOnItemClickListener(listener);
	}

	public MessageCenterListAdapter getListAdapter() {
		return mListContainer.getListAdapter();
	}

	/**
	 * <br>
	 * 功能简述:获取当前topbarContaniner的状态 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @return
	 */
	public int getTopBarCurrentStatus() {
		return mTopBarContainer.getCurrentStatus();
	}

	/**
	 * <br>
	 * 功能简述:获取当前ListContaniner的状态 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @return
	 */
	public int getListCurrentStatus() {
		return mListContainer.getCurrentStatus();
	}

	/**
	 * 重置勾选已读消息
	 */
	public void resetDeleteMsgCount() {
		mListContainer.resetDeleteMsgCount();
	}

	/**
	 * <br>
	 * 功能简述:获取用户勾选删除的个数 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @return
	 */
	public int getDeleteMsgCount() {
		return mListContainer.getDeleteMsgCount();
	}

	/**
	 * <br>
	 * 功能简述:显示用户勾选删除的个数 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param deleteCount
	 */
	public void showDeleteMsgCount(int deleteCount) {
		mTopBarContainer.showDeleteMsgCount(deleteCount);
	}

	/**
	 * 显示删除按钮
	 */
	public void showDeleteBtn() {
		mTopBarContainer.showDeleteBtn();
	}

	/**
	 * 隐藏删除按钮
	 */
	public void dissmissDeleteBtn() {
		mTopBarContainer.dismissDeleteBtn();
	}

	@Override
	public void statusChange(int statusID) {
		if (statusID == -1) {
			//
			statusID = getTopBarCurrentStatus() + 1;
			statusID = statusID % 2;
		}
		mTopLayout.removeView(mTopBarContainer);
		mTopBarContainer = (MessageCenterTopBarContainer) mViewFactory
				.createView(ConstValue.CONTAINER_TOPBAR, statusID);
		mTopLayout.addView(mTopBarContainer);
		// mTopBarContainer.statusChange(statusID);
		mListContainer.statusChange(statusID);
		MessageManager mManager = MessageManager
				.getMessageManager(ApplicationProxy.getContext());
		Vector<MessageHeadBean> msgsList = mManager.getMessageList();
		if (msgsList != null && msgsList.size() > 0) {
			mTopBarContainer.showDeleteBtn();
		} else {
			mTopBarContainer.dismissDeleteBtn();
		}
	}

	/**
	 * 
	 * <br>
	 * 类描述:消息中心view工厂类 <br>
	 * 功能详细描述:生产正常状态和删除状态的view
	 * 
	 * @author zengyingzhen
	 * @date [2014年4月8日]
	 */
	protected class MessageViewFactory {
		private SparseArray<AbsMessageCenterView> mTopbarContainerMaps = new SparseArray<AbsMessageCenterView>();
		private SparseArray<AbsMessageCenterView> mListContainerMaps = new SparseArray<AbsMessageCenterView>();
		private Context mContext;
		private IMessageCenterActionListener mListener;

		public MessageViewFactory(Context context) {
			// TODO Auto-generated constructor stub
			mContext = context;
		}

		public MessageViewFactory(Context context,
				IMessageCenterActionListener listener) {
			// TODO Auto-generated constructor stub
			mContext = context;
			mListener = listener;
		}

		public AbsMessageCenterView createView(String name, int status) {
			AbsMessageCenterView container = checkCache(name, status);
			if (container == null) {
				if (name.equals(ConstValue.CONTAINER_TOPBAR)) {
					container = new MessageCenterTopBarContainer(mContext,
							status, mListener);
					mTopbarContainerMaps.put(status, container);
				} else if (name.equals(ConstValue.CONTAINER_LIST)) {
					container = new MessageCenterListContainer(mContext,
							status, mListener);
					mTopbarContainerMaps.put(status, container);
				}
			}
			return container;
		}

		private AbsMessageCenterView checkCache(String name, int status) {
			// TODO Auto-generated method stub
			AbsMessageCenterView container = null;
			if (name.equals(ConstValue.CONTAINER_TOPBAR)) {
				mTopbarContainerMaps.get(status);
			} else if (name.equals(ConstValue.CONTAINER_LIST)) {
				mListContainerMaps.get(status);
			}
			return container;
		}
	}
}
