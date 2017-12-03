package com.jiubang.ggheart.components.advert;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.SparseArray;

import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IAdvertCentertMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.components.advert.AdvertTask.IAdvertTaskObserve;

/**
 * 广告控制中心
 */
public class AdvertCenter implements IMessageHandler, IAdvertTaskObserve {
	private static final String TAG = "AdvertCenter";
	
	public Context mContext;
	private SparseArray<ArrayList<AdvertTask>> mAdvertArray = new SparseArray<ArrayList<AdvertTask>>();
	AlarmManager mAlarmManager;
	
	private final static String ACTION_TIMEUP = "com.jiubang.adcenter.timeup";
	private final static String HANDLERID = "handlerid";
	private final static String TYPE = "type";
	
	TaskReceive mTaskReceive = new TaskReceive();

	private PendingIntent mPeriodIntent;
	
	public AdvertCenter() {
		mContext = ApplicationProxy.getApplication();
		mAdvertArray = new SparseArray<ArrayList<AdvertTask>>();
		mAlarmManager = (AlarmManager) ApplicationProxy.getContext()
				.getSystemService(Context.ALARM_SERVICE);
		MsgMgrProxy.registMsgHandler(this);
		
		IntentFilter filter = new IntentFilter(ACTION_TIMEUP);
		mContext.registerReceiver(mTaskReceive, filter);
	}

	@Override
	public int getMsgHandlerId() {
		return IDiyFrameIds.ADVERT_CENTER;
	}
	
	private AdvertTask getCloseinTask() {
		AdvertTask ret = null;
		int len = mAdvertArray.size();
		for (int i = 0; i < len; ++i) {
			ArrayList<AdvertTask> list = mAdvertArray.valueAt(i);
			if (null == list) {
				continue;
			}
			
			int size = list.size();
			for (int j = 0; j < size; ++j) {
				AdvertTask tmp = list.get(j);
				if (ret == null) {
					ret = tmp;
				} else {
					if (ret.mTickTimer > tmp.mTickTimer) {
						ret = tmp;
					}
				}
			}
		}
		
		return ret;
	}
	
	private AdvertTask getTask(int handlerid, int type) {
		ArrayList<AdvertTask> list = mAdvertArray.get(handlerid);
		int len = list.size();
		for (int i = len - 1; i >= 0; --i) {
			AdvertTask tmp = list.get(i);
			if (tmp.mAdType == type) {
				return tmp;
			}
		}
		
		return null;
	}

	// 添加任务
	private void addTask(AdvertTransBean bean) {
		AdvertTask task = new AdvertTask(bean, this);
		if (task.mPeriod == 0) {
			// 如果周期为0则当成是一次性请求
			task.startRequest();
			return;
		}
		
		int index = mAdvertArray.indexOfKey(bean.mHandlerId);
		if (index < 0) {
			ArrayList<AdvertTask> list = new ArrayList<AdvertTask>();
			list.add(task);
			mAdvertArray.append(bean.mHandlerId, list);
		} else {
			ArrayList<AdvertTask> list = mAdvertArray.get(bean.mHandlerId);
			int len = list.size();
			for (int i = len - 1; i >= 0; --i) {
				AdvertTask tmp = list.get(i);
				if (tmp.mAdType == bean.mAdType) {
					list.remove(i);
				}
			}

			list.add(task);
		}
		
		Log.d(TAG, "addTask " + "handler:" + task.mHandlerId + " type:"
				+ task.mAdType + " period:" + task.mPeriod);
		
		startAlarm();
	}
	
	private void startAlarm() {
		AdvertTask closeinTask = getCloseinTask();
		if (null == closeinTask) {
			return;
		}
		
		Intent intent = new Intent(ACTION_TIMEUP);
		intent.putExtra(HANDLERID, closeinTask.mHandlerId);
		intent.putExtra(TYPE, closeinTask.mAdType);
		
		mPeriodIntent = PendingIntent.getBroadcast(
				ApplicationProxy.getContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, closeinTask.mTickTimer, mPeriodIntent);
	}

	private void removeTask(Object... obj) {
		if (obj == null || obj.length > 0) {
			int handleId = ((Integer) obj[0]).intValue();
			ArrayList<AdvertTask> list = mAdvertArray.get(handleId);
			if (obj.length > 1) {
				int type = ((Integer) obj[1]).intValue();
				int len = list.size();
				for (int i = len - 1; i >= 0; --i) {
					AdvertTask task = list.get(i);
					if (task.mAdType == type) {
						if (task.mPeriod > 0) {
							mAlarmManager.cancel(mPeriodIntent);
						}
						list.remove(i);
					}
				}
			} else {
				int len = list.size();
				for (int i = len - 1; i >= 0; --i) {
					AdvertTask task = list.get(i);
					if (task.mPeriod > 0) {
						mAlarmManager.cancel(mPeriodIntent);
					}
				}
				mAdvertArray.remove(handleId);
			}
		}
	}

	@Override
	public boolean handleMessage(Object who, int msgId, int param,
			Object... obj) {
		boolean ret = false;
		switch (msgId) {
		case IAdvertCentertMsgId.START_REQUEST_ADVERT: {
			if (obj != null && obj.length > 0) {
				AdvertTransBean bean = (AdvertTransBean) obj[0];
				addTask(bean);
			}
		}
			break;
		case IAdvertCentertMsgId.CANCEL_REQUEST_ADVERT: {
			removeTask(obj);
		}

			break;
		default:
			break;
		}
		return ret;
	}
	
	
	/**
	 * 定时器接收者
	 * @author liuheng
	 *
	 */
	class TaskReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (null == intent || !intent.getAction().equals(ACTION_TIMEUP)) {
				return;
			}
			
			int handlerid = intent.getIntExtra(HANDLERID, IDiyFrameIds.INVALID_FRAME);
			int type = intent.getIntExtra(TYPE, -1);
			Log.d(TAG, "onReceive:" + handlerid + "  " + type);
			
			AdvertTask task = getTask(handlerid, type);
			if (null != task) {
				task.startRequest();
			}
		}
	}


	@Override
	public void requestOnStart(AdvertTask task) {
		startAlarm();
	}

	@Override
	public void requestFinished(AdvertTask task) {
		
	}
}
