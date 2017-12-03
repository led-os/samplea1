package com.jiubang.ggheart.appgame.base.net;

import android.content.Context;

import com.gau.utils.net.INetRecord;
import com.gau.utils.net.request.THttpRequest;
import com.jiubang.ggheart.appgame.base.data.AppGameNetInfoLog;
import com.jiubang.ggheart.appgame.base.data.ClassificationExceptionRecord;
import com.jiubang.ggheart.appgame.base.data.TestAppGameNetLogControll;

/**
 * 应用游戏中心，连网取数据的网络状态处理器，用于统计连网信息
 * 
 * @author xiedezhi
 * @date [2012-8-21]
 */
public class CopyOfAppGameNetRecord implements INetRecord {
	/**
	 * 开始连接的时间
	 */
	private long mStartConnectTime;
	private Context mContext;
	private long mDownloadTime;
	/**
	 * 是否开启网络错误信息邮箱反馈
	 */
	private boolean mOpenErrorMailFeedback;

	/**
	 * @param openErrorMailFeedback
	 *            是否开启网络错误信息邮箱反馈
	 */
	public CopyOfAppGameNetRecord(Context context, boolean openErrorMailFeedback) {
		if (context == null) {
			throw new IllegalArgumentException("ClassificationNetRecord context can not be null");
		}
		mContext = context;
		mOpenErrorMailFeedback = openErrorMailFeedback;
	}

	@Override
	public void onStartConnect(THttpRequest request, Object arg1, Object arg2) {
		// Log.e("XIEDEZHI", "ClassificationNetRecord onStartConnect url = "
		// + request.getCurrentUrl().toString());
		mStartConnectTime = System.currentTimeMillis();
		if (mOpenErrorMailFeedback) {
			// 开始记录网络错误信息
			ClassificationExceptionRecord.getInstance().startRecord(mContext,
					Thread.currentThread());
			// 请求数据的url记录下来
			// 这里不是要拿首选URL，而是要拿当前取数据的url
			ClassificationExceptionRecord.getInstance().markUrl(request.getCurrentUrl().toString());
		}
		// 开始网络信息收集
		TestAppGameNetLogControll.getInstance().startRecord(mContext,
				TestAppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE,
				5);

		// 记录网络请求的url
		// 这里不是要拿首选URL，而是要拿当前取数据的url
		TestAppGameNetLogControll.getInstance().setUrl(
				TestAppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE,
				request.getCurrentUrl().toString());

		// 记录是否使用长连接
		int linkType = 0;
		if (request.getIsKeepAlive()) {
			linkType = AppGameNetInfoLog.LINK_TYPE_ALIVE;
		} else {
			linkType = AppGameNetInfoLog.LINK_TYPE_NORMAL;
		}
		TestAppGameNetLogControll.getInstance().setLinkType(
				TestAppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE, linkType);
	}

	@Override
	public void onConnectSuccess(THttpRequest request, Object arg1, Object arg2) {
		// Log.e("XIEDEZHI", "ClassificationNetRecord onConnectSuccess url = "
		// + request.getCurrentUrl().toString());
		// 记录网络连接时间
		TestAppGameNetLogControll.getInstance().setConnectionTime(
				TestAppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE,
				System.currentTimeMillis() - mStartConnectTime);
		mStartConnectTime = System.currentTimeMillis();
	}

	@Override
	public void onTransFinish(THttpRequest request, Object arg1, Object arg2) {
		// Log.e("XIEDEZHI", "ClassificationNetRecord onTransFinish url = "
		// + request.getCurrentUrl().toString());
		// 结束网络信息收集
/*		TestAppGameNetLogControll.getInstance().stopRecord(
				TestAppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE, mContext);*/
		mDownloadTime = mStartConnectTime - System.currentTimeMillis();
	}

	@Override
	public void onException(Exception e, Object arg1, Object arg2) {
		e.printStackTrace();
		if (mOpenErrorMailFeedback) {
			// 记录错误信息
			ClassificationExceptionRecord.getInstance().record(e);
		}
		// 记录网络连接时间
		TestAppGameNetLogControll.getInstance().setConnectionTime(
				TestAppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE,
				System.currentTimeMillis() - mStartConnectTime);
		// 记录异常信息，同时保存网络信息
		TestAppGameNetLogControll.getInstance().setExceptionCode(
				TestAppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE, e);
	}
	public long getDownloadTime() {
		return mDownloadTime;
	}
}
