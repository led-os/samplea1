package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.ConvertUtils;
import com.go.util.log.LogConstants;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.MessageCenterListContainer.MessageCenterListAdapter;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageBaseBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean.MessageHeadBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageStatisticsBean;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.components.advert.untils.DialogNoAdPayMsgCenter;

/**
 * 
 * 类描述: 消息中心列表内容的入口类 功能详细描述:
 * 
 * @date [2012-9-28]
 */
public class MessageCenterActivity extends Activity implements
		OnItemClickListener, OnKeyListener,
		// OnClickListener,
		OnCancelListener, BroadCasterObserver {

	public final static int GET_MSG_LIST_FINISH = 0x01; // 获得消息列表
	public final static int GET_MSG_LIST_OK = 0x02;
	public final static int GET_MSG_LIST_ERRO = 0x03;
	public final static int GET_MSG_CONTENT_FINISH = 0X04; // 获得一个消息的具体内容
	public final static int GET_MSG_CONTENT_OK = 0x05;
	public final static int GET_MSG_CONTENT_FAILED = 0x06;
	public final static int GET_MSG_NO_NETWORK = 0x07;
	public final static int GET_MSG_COUPON_FINISH = 0x08;
	public final static int GET_MSG_COUPON_WRITETOSD = 0x09;
	private final static int DIALOG_WAIT = 1;
	private MessageManager mManager;
	private MessageCenterViewContainer mViewContainer;
	// 获取系统的日期
	private static String sDate;

	private boolean mIsVisiable = false;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == GET_MSG_LIST_OK) {
				removeDialog(DIALOG_WAIT);
				int change = msg.arg1;
				Vector<MessageHeadBean> orginalMsgsList = mManager
						.getMessageList();
				Vector<MessageHeadBean> msgsList = null;
				// Vector<MessageHeadBean> msgsList = mManager.getMessageList();
				if (orginalMsgsList != null && !orginalMsgsList.isEmpty()) {
					msgsList = (Vector<MessageHeadBean>) orginalMsgsList
							.clone();
					HttpUtil.sortList(msgsList);

					if (!mIsVisiable || mIsVisiable && change == 1) {
						// 当消息中心页面打开情况下，如若自动扫描数据也回调这接口，需判断数据是否有更新。
						mIsVisiable = true;
						ArrayList<MessageStatisticsBean> statisticsBeans = new ArrayList<MessageStatisticsBean>(
								msgsList.size());
						for (MessageHeadBean bean : msgsList) {
							MessageStatisticsBean statisticsBean = new MessageStatisticsBean();
							statisticsBean.mMsgId = bean.mId;
							statisticsBean.mMapId = bean.mMapId == null ? ""
									: bean.mMapId;
							statisticsBean.mOprCode = "show";
							statisticsBean.mEntrance = MessageBaseBean.VIEWTYPE_NORMAL;
							statisticsBeans.add(statisticsBean);
						}
						mManager.saveStatisticsDatas(statisticsBeans, null,
								null, null, null, -1);
						mManager.updateStatisticsData(0);
					}
				}
				mViewContainer.updateList(getApplicationContext(), msgsList);
				// setMsgCount();
			} else if (msg.what == GET_MSG_LIST_ERRO) {
				removeDialog(DIALOG_WAIT);
				Vector<MessageHeadBean> msgsList = (Vector<MessageHeadBean>) mManager
						.getMessageList().clone();
				if (msgsList != null && msgsList.size() > 0) {
					HttpUtil.sortList(msgsList);
					mViewContainer
							.updateList(getApplicationContext(), msgsList);
				} else {
					Toast.makeText(MessageCenterActivity.this,
							R.string.msgcenter_msg_update_erro, 500).show();
				}
				// setMsgCount();
			} else if (msg.what == GET_MSG_CONTENT_OK) {
				removeDialog(DIALOG_WAIT);
				// mManager.showMessage();
			} else if (msg.what == GET_MSG_CONTENT_FAILED) {
				removeDialog(DIALOG_WAIT);
				Toast.makeText(MessageCenterActivity.this,
						R.string.msgcenter_msg_update_erro, 500).show();
			} else if (msg.what == GET_MSG_NO_NETWORK) {
				Toast.makeText(MessageCenterActivity.this,
						R.string.http_exception, 500).show();
			} else if (msg.what == GET_MSG_COUPON_WRITETOSD) {
				int param = msg.arg1;
				boolean bool = ConvertUtils.int2boolean(param);
				if (bool) {
					Toast.makeText(MessageCenterActivity.this,
							R.string.coupon_click_get_success, 800).show();
				} else {
					Toast.makeText(MessageCenterActivity.this,
							R.string.coupon_click_get_fail, 800).show();
				}
				refreshListData();
			}
		}

	};
	
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		Log.d("zyz", "onConfigurationChanged   activity");
		refreshListData();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		IMessageCenterActionListener listener = new MessageCenterActionListener();
		mViewContainer = new MessageCenterViewContainer(
				getApplicationContext(), listener);
		mViewContainer.setListItemOnClickListener(this);
		setContentView(mViewContainer);

		// setContentView(R.layout.messagecentermain);

		// 取得系统的时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 日期格式
		sDate = df.format(date);

		// 空信息
		setUpList();
	}

	/**
	 * 功能简述: 创建消息列表 功能详细描述: 判断是否存在网络 ，若存在，则去网络请求数据 注意:
	 */
	private void setUpList() {
		if (mManager == null) {
			mManager = MessageManager.getMessageManager(ApplicationProxy
					.getContext());
			mManager.registerObserver(this);
		}
		if (Machine.isNetworkOK(this)) {
			Log.d("zyz", "消息中心那数据");
			getMessageList();
			mViewContainer.showMsgList();
		} else {
			mViewContainer.showEmptyList(null, null);
			Toast.makeText(MessageCenterActivity.this, R.string.http_exception,
					500).show();
		}
	}

	/**
	 * 功能简述: 网络请求，并注册观察者
	 */
	private void getMessageList() {
		showDialog(DIALOG_WAIT);
		mManager.postUpdateRequest(0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.d("zyz", "onResume");
		super.onResume();
		refreshListData();
	}

	private void refreshListData() {
		// if (Machine.isNetworkOK(this)) {
		// String str =
		// this.getResources().getString(R.string.message_center_nomsg);
		// mNoMsgtextview2.setText(str);
		// mNoMsgtext.setText("");
		MessageCenterListAdapter mAdapter = mViewContainer.getListAdapter();
		if (mAdapter != null && mManager != null
				&& mManager.getMessageList() != null
				&& mManager.getMessageList().size() > 0) {

			// setMsgCount();
			HttpUtil.sortList(mAdapter.mMsgs);
			mAdapter.notifyDataSetChanged();
			mViewContainer.showMsgList();

		} else {
			mViewContainer.showEmptyList(
					getString(R.string.themestore_back_to_try),
					getString(R.string.themestore_no_data));
			if (mManager != null) {
				mManager.removeCoverFrameView();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("zyz", "onDestroy   activity");
		super.onDestroy();
		removeDialog(DIALOG_WAIT);
		if (mManager != null) {
			mManager.unRegisterObserver(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		MessageCenterListItemView.ViewHolder holder = ((MessageCenterListItemView) view)
				.getViewHolder();
		MessageHeadBean bean = (MessageHeadBean) view.getTag();

		if (holder.mStatus == IMessageCenterViewStatus.MESSAGE_CENTER_VIEW_STATUS_DELETE) {
			if (bean.mIsReadyDelete) {
				holder.mCheckBox.setChecked(false);
				bean.mIsReadyDelete = false;
			} else {
				holder.mCheckBox.setChecked(true);
				bean.mIsReadyDelete = true;
			}
			showDeleteMsgCount();
		} else {
			if (bean == null) {
				return;
			}
			if (bean.mIsWifi == 1 && !mManager.isWifiConnected()) { // 联合运营平台，wifi关闭时
				Toast.makeText(MessageCenterActivity.this,
						R.string.msgcenter_suggest_open_wifi, 2000).show();
				return;
			}

			boolean isAllPay = FunctionPurchaseManager.getInstance(
					ApplicationProxy.getContext().getApplicationContext())
					.queryItemPurchaseState(
							FunctionPurchaseManager.PURCHASE_ITEM_FULL);
			if (bean.mType == 5 && isAllPay) {
				Toast.makeText(
						MessageCenterActivity.this,
						getResources().getString(
								R.string.message_center_promotion_tip),
						Toast.LENGTH_SHORT).show();
				return;
			}
			mManager.handleMsgClick(bean, MessageBaseBean.VIEWTYPE_NORMAL);
			 MessageCenterListAdapter mAdapter = mViewContainer.getListAdapter();
			 if (mAdapter != null) {
				 mAdapter.notifyDataSetChanged();
			}
		}

	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == DIALOG_WAIT) {
			dialog = ProgressDialog.show(this, "",
					getString(R.string.msgcenter_dialog_wait_msg), true);
			dialog.setOnKeyListener(this);
			dialog.setOnCancelListener(this);
		}
		return dialog;
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_UP
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			removeDialog(DIALOG_WAIT);
			if (mManager != null) {
				mManager.abortPost();
			}
			return true;
		}
		return false;
	}

	// @Override
	// public void updateFinish(boolean bool) {
	// // TODO Auto-generated method stub
	// mManager.removeUpdateListener();
	// removeDialog(DIALOG_WAIT);
	// if(bool)
	// {
	// mHandler.sendEmptyMessage(GET_DATE_OK);
	// }
	// else
	// {
	// mHandler.sendEmptyMessage(GET_DATE_ERRO);
	// }
	// }

	// @Override
	// public void getMsgFinish(boolean bool) {
	// // TODO Auto-generated method stub
	// mManager.removeUpdateListener();
	// removeDialog(DIALOG_WAIT);
	// if(bool)
	// {
	// mHandler.sendEmptyMessage(GET_MSG_OK);
	// }
	// else
	// {
	// mHandler.sendEmptyMessage(GET_MSG_FAILED);
	// }
	// }

	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if (v == mClearButton) {
	// if (mManager != null) {
	// mManager.markAllReaded();
	// Vector<MessageHeadBean> msgsList = mManager.getMessageList();
	// HttpUtil.sortList(msgsList);
	// if (mAdapter == null) {
	// mAdapter = new MyAdapter(msgsList);
	// } else {
	// ((MyAdapter) mAdapter).setAdapterData(msgsList);
	// }
	// setMsgCount();
	// if (mAdapter != null) {
	// mAdapter.notifyDataSetChanged();
	// }
	// }
	// } else if (v == mDeleteButton) { //点击删除按钮
	// //消息中心: 点击垃圾桶时弹出提示弹框,去掉推荐Prime入口。（2014-01-14）
	// /*//判断是否第一次显示
	// if (DeskSettingUtils.isFirstShowNoAdvertPayDialog(this) &&
	// checkDelMsgHasAd()) {
	// DeskSettingUtils.setNoAdvertPayDialogPreference(this); //设置下次不再显示
	// //是否付费用户,200渠道才弹出
	// if
	// (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
	// FunctionPurchaseManager.PURCHASE_ITEM_AD) ==
	// FunctionPurchaseManager.STATE_VISABLE) {
	// showNoAdPayDialog();
	// } else {
	// showNorDelDialog(); //显示普通对话框
	// }
	// } else {*/
	// showNorDelDialog();
	// // }
	//
	// }
	// }

	/**
	 * <br>
	 * 功能简述:检查已读消息是否包含广告消息 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @return
	 */
	public boolean checkDelMsgHasAd() {
		Vector<MessageHeadBean> msgsList = mManager.getMessageList();
		if (msgsList != null && msgsList.size() > 0) {
			int size = msgsList.size();
			for (int i = 0; i < size; i++) {
				MessageHeadBean head = msgsList.get(i);
				// 判断是否广告消息
				if (head.misReaded && (head.mAddact == 2 || head.mAddact == 3)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <br>
	 * 功能简述:显示普通删除提示框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void showNorDelDialog() {
		DialogConfirm mNormalDialog = new DialogConfirm(this);
		mNormalDialog.show();
		mNormalDialog.setTitle(this.getString(R.string.attention_title));
		mNormalDialog.setMessage(this
				.getString(R.string.msgcenter_delete_all_readed));
		mNormalDialog.setPositiveButton(null, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// delAllReaderMsg();
			}
		});
	}

	/**
	 * <br>
	 * 功能简述:显示引导付费提示框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void showNoAdPayDialog() {
		DialogNoAdPayMsgCenter dialog = new DialogNoAdPayMsgCenter(this);
		dialog.show();
		dialog.setTitle(R.string.dialog_msg_center_no_advert_title);
		dialog.setMessage(R.string.dialog_msg_center_no_advert_message);

		// 打开购买付费引导页
		dialog.setPositiveButton(
				R.string.dialog_msg_center_no_advert_block_ads,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						DeskSettingUtils.showPayDialog(
								MessageCenterActivity.this, 203); // 显示付费对话框
					}
				});

		// 清除消息
		dialog.setOtherButton(R.string.dialog_msg_center_no_adver_del,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// delAllReaderMsg();
					}
				});

		dialog.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	/**
	 * <br>
	 * 功能简述:删除所有已读消息 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	// public void delAllReaderMsg() {
	// mManager.deleteAllReaded();
	// Vector<MessageHeadBean> msgsList = mManager.getMessageList();
	// HttpUtil.sortList(msgsList);
	// if (mAdapter == null) {
	// mAdapter = new MyAdapter(msgsList);
	// } else {
	// ((MyAdapter) mAdapter).setAdapterData(msgsList);
	// }
	// setMsgCount();
	// if (mAdapter != null) {
	// mAdapter.notifyDataSetChanged();
	// }
	// }

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		if (mManager != null) {
			mManager.abortPost();
		}
	}

	/**
	 * 重置勾选已读消息
	 */
	private void resetDeleteMsgCount() {
		// TODO Auto-generated method stub
		mViewContainer.resetDeleteMsgCount();
	}

	/**
	 * 功能简述:显示勾选消息个数
	 */
	private void showDeleteMsgCount() {
		int deleteCount = mViewContainer.getDeleteMsgCount();
		mViewContainer.showDeleteMsgCount(deleteCount);
		// if (mManager.getMessageList() != null &&
		// mManager.getMessageList().size() > 0) {
		// mMsgCount.setText(String.valueOf("(" + mManager.getUnreadedCnt() +
		// "/"
		// + String.valueOf(mManager.getMessageList().size()))
		// + ")");
		// } else {
		// mListView.setVisibility(View.GONE);
		// mEmptyView.setVisibility(View.VISIBLE);
		// mMsgCount.setText(null);
		// }
		// if (mManager.getUnreadedCnt() > 0) {
		// mClearButton.setVisibility(View.VISIBLE);
		// } else {
		// mClearButton.setVisibility(View.GONE);
		// }
		//
		// if (mManager.getReadedCnt() > 0) {
		// mDeleteButton.setVisibility(View.VISIBLE);
		// } else {
		// mDeleteButton.setVisibility(View.GONE);
		// }
		// mMsgCount.invalidate();
	}

	// 消息的时间与系统的时间比对，然后返回相应的string 时间
	/**
	 * <br>
	 * 功能简述: <br>
	 * 功能详细描述:今天：today+HH:mm,今年：MM-dd HH:mm 不是今年：yyyy-MM-dd HH:mm <br>
	 * 注意:
	 * 
	 * @param messageDate
	 * @param context
	 * @return
	 */
	public static String compareDate(String messageDate, Context context) {

		if (sDate == null) {
			// 取得系统的时间
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 日期格式
			sDate = df.format(date);
		}

		// 标准时间date字符串化后 0~5之间是year ，5~10之间是month和day ,11~16之间是time

		String year = messageDate.substring(0, 5);
		String month_day = messageDate.substring(5, 10);
		String time = messageDate.substring(11, 16);

		String nowDate_year = sDate.substring(0, 5);
		String nowDate_month_day = sDate.substring(5, 10);

		String str = messageDate.substring(0, 10);
		java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		String str_today = context.getResources().getString(R.string.today);
		String show = null;
		try {
			Date d = format.parse(str);
			// str_date为 "yyyy年MM月dd日"
			// DateFormat df = new SimpleDateFormat(date_rule);// 日期格式
			show = format.format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (year.equals(nowDate_year)) {
			if (month_day.equals(nowDate_month_day)) {
				return str_today + " " + time;
			} else {
				return show != null ? show.substring(5) + " " + time : time;
			}
		} else {
			return show + " " + time;
		}

	}

	/**
	 * 回调函数的处理，并发消息到Handler中处理
	 */
	@Override
	public void onBCChange(int msgId, int param, Object... object) {
		// TODO Auto-generated method stub
		switch (msgId) {
		case GET_MSG_LIST_FINISH:
			boolean bool = ConvertUtils.int2boolean(param);
			if (bool) {
				int change = (Integer) object[0];
				Message msg = new Message();
				msg.what = GET_MSG_LIST_OK;
				msg.arg1 = change;
				mHandler.sendMessage(msg);
				// mHandler.sendEmptyMessage(GET_MSG_LIST_OK);
			} else {
				mHandler.sendEmptyMessage(GET_MSG_LIST_ERRO);
			}
			break;
		case GET_MSG_NO_NETWORK:
			mHandler.sendEmptyMessage(GET_MSG_NO_NETWORK);
			break;
		case GET_MSG_CONTENT_FINISH:
			bool = ConvertUtils.int2boolean(param);
			if (bool) {
				mHandler.sendEmptyMessage(GET_MSG_CONTENT_OK);
			} else {
				mHandler.sendEmptyMessage(GET_MSG_CONTENT_FAILED);
			}
			break;
		case GET_MSG_COUPON_WRITETOSD:
			// 优惠券已写到sd卡
			Message msg = Message.obtain();
			msg.what = GET_MSG_COUPON_WRITETOSD;
			msg.arg1 = param;
			mHandler.sendMessage(msg);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		try {
			super.onBackPressed();
		} catch (Exception e) {
			Log.e(LogConstants.HEART_TAG, "onBackPressed err " + e.getMessage());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	/**
	 * 回调实现类
	 * @author zengyingzhen
	 *
	 */
	public class MessageCenterActionListener implements
			IMessageCenterActionListener {

		@Override
		public void action(int actionCode) {
			// TODO Auto-generated method stub
			switch (actionCode) {
			case IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_BACK:
				// 退出activity
				finish();
				break;
			case IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS:
				// 切换状态
				Log.d("zyz", "通知messageManager切换状态");
				mViewContainer.statusChange(-1);
				resetDeleteMsgCount();
				showDeleteMsgCount();
				break;
			case IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS_NORMAL:
				// 切换正常状态
				Log.d("zyz", "通知messageManager切换正常状态");
				mViewContainer.statusChange(IMessageCenterViewStatus.MESSAGE_CENTER_VIEW_STATUS_NORMAL);
				break;
			case IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_CHANGE_STATUS_DELETE:
				// 切换删除状态
				Log.d("zyz", "通知messageManager切换状态");
				mViewContainer.statusChange(IMessageCenterViewStatus.MESSAGE_CENTER_VIEW_STATUS_DELETE);
				resetDeleteMsgCount();
				showDeleteMsgCount();
				break;
			case IMessageCenterActionListener.MESSAGE_CENTER_VIEW_ACTION_DELETE:
				// 删除选中消息
				Log.d("zyz", "获取选中消息，并通知messageManager删除选中消息");
				int deleteCount = mViewContainer.getDeleteMsgCount();
				if (deleteCount > 0) {
					mManager.deleteCheckedMessage();
					Vector<MessageHeadBean> msgsList = mManager.getMessageList();
					HttpUtil.sortList(msgsList);
					mViewContainer.updateList(getApplicationContext(), msgsList);
					Toast.makeText(getApplicationContext(), getResources()
							.getString(R.string.message_center_delete_completed),
							Toast.LENGTH_LONG).show();
				} 
				mViewContainer.statusChange(IMessageCenterViewStatus.MESSAGE_CENTER_VIEW_STATUS_NORMAL);
				break;
			

			default:
				break;
			}
		}

	}
}
