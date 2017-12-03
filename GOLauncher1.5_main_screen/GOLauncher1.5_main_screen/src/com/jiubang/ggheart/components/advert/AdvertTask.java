package com.jiubang.ggheart.components.advert;

import java.net.URISyntaxException;

import android.util.Log;

import com.gau.utils.net.IConnectListener;
import com.gau.utils.net.INetRecord;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.IResponse;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.golauncher.message.IAdvertCentertMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.ggheart.apps.gowidget.gostore.net.SimpleHttpAdapter;
import com.jiubang.ggheart.components.gohandbook.StringOperator;

/**
 * 广告数据结构
 * @author liuheng
 *
 */
public class AdvertTask {
	private static final String TAG = "AdvertTask";
	public String mAdUrl;
	public int mHandlerId = IDiyFrameIds.INVALID_FRAME;
	public int mAdType = -1;
	public long mPeriod = 0;
	public byte[] mPostData;
	
	public long mTickTimer = 0;
	public int mMode = AdvertTransBean.NET_MODE_BYTESTREAM;
	
	IAdvertTaskObserve mObv;
	
	public AdvertTask(AdvertTransBean bean, IAdvertTaskObserve obv) {
		mAdUrl = bean.mUrl;
		mHandlerId = bean.mHandlerId;
		mAdType = bean.mAdType;
		mPeriod = bean.mPeriod;
		mPostData = bean.mPostData;
		
		mTickTimer = mPeriod + System.currentTimeMillis();
		
		mObv = obv;
	}
	
	public void startRequest() {
		Log.d(TAG, "startRequest");
		if (mPeriod > 0) {
			mTickTimer = mPeriod + System.currentTimeMillis();
		}
		
		if (null != mObv) {
			mObv.requestOnStart(this);
		}
		
		// CHECK_IS_NEED_REQUEST_ADVERT
		
		getAdvertData(mHandlerId, mAdType, mAdUrl, mPostData);
	}

	private void getAdvertData(final int handlerId, final int type,
			final String url, final byte[] postData) {
		IConnectListener connectlistener = new IConnectListener() {
			@Override
			public void onStart(THttpRequest request) {
				MsgMgrProxy.sendMessage(this, handlerId,
						IAdvertCentertMsgId.ADVERT_REQUEST_ON_START, -1, type);
			}

			@Override
			public void onFinish(THttpRequest request, IResponse response) {
				MsgMgrProxy.sendMessage(this, handlerId,
						IAdvertCentertMsgId.ADVERT_REQUEST_ON_FINISH, -1, type,
						response);
			}

			@Override
			public void onException(THttpRequest request, int reason) {
				MsgMgrProxy.sendMessage(this, handlerId,
						IAdvertCentertMsgId.ADVERT_REQUEST_ON_EXCEPTION, -1,
						type, reason);
			}
		};

		THttpRequest request = null;
		try {
			request = new THttpRequest(url, postData, connectlistener);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		if (null == request) {
			return;
		}

		request.setOperator(new StringOperator()); // 设置返回数据类型-字符串

		// 设置报错提示
		request.setNetRecord(new INetRecord() {
			@Override
			public void onTransFinish(THttpRequest arg0, Object arg1,
					Object arg2) {
			}

			@Override
			public void onStartConnect(THttpRequest arg0, Object arg1,
					Object arg2) {
			}

			@Override
			public void onException(Exception e, Object arg1, Object arg2) {
				e.printStackTrace();
			}

			@Override
			public void onConnectSuccess(THttpRequest arg0, Object arg1,
					Object arg2) {
			}
		});

		try {
			SimpleHttpAdapter httpAdapter = SimpleHttpAdapter.getInstance(ApplicationProxy.getContext());
			httpAdapter.addTask(request);
		} catch (Exception e) {
			e.printStackTrace();
			MsgMgrProxy.sendMessage(this, handlerId,
					IAdvertCentertMsgId.ADVERT_REQUEST_ON_EXCEPTION, -1, type, e);
		}
	}
	
	/**
	 * 广告任务观察者
	 * @author liuheng
	 *
	 */
	public interface IAdvertTaskObserve {
		public void requestOnStart(AdvertTask task);
		public void requestFinished(AdvertTask task);
	}
}
