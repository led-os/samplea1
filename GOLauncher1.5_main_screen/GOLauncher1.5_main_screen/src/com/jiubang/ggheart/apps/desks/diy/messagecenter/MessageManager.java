package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.BroadCaster;
import com.go.util.ConvertUtils;
import com.go.util.GotoMarketIgnoreBrowserTask;
import com.go.util.device.Machine;
import com.go.util.file.FileUtil;
import com.go.util.log.LogConstants;
import com.go.util.log.LogUnit;
import com.go.util.market.MarketConstant;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.ICoverFrameMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.component.AppsManagementActivity;
import com.jiubang.ggheart.appgame.base.component.AppsDetail;
import com.jiubang.ggheart.appgame.base.component.MainViewGroup;
import com.jiubang.ggheart.appgame.base.utils.ApkInstallUtils;
import com.jiubang.ggheart.appgame.download.ServiceCallbackDownload;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.INotificationId;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.frames.cover.CoverFrame;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageBaseBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean.MessageHeadBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageStatisticsBean;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageWidgetBean;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeDetailActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeManageActivity;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeManageView;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.BaseBean;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.CouponBean;
import com.jiubang.ggheart.apps.desks.diy.themescan.coupon.PromotionBean;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.components.advert.untils.NoAdvertCheckReceiver;
import com.jiubang.ggheart.data.info.MessageInfo;
import com.jiubang.ggheart.data.model.MessageCenterDataModel;
import com.jiubang.ggheart.data.statistics.AppRecommendedStatisticsUtil;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.realtiemstatistics.RealTimeStatisticsContants;
import com.jiubang.ggheart.data.tables.MessageCenterTable;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * 类描述: 消息中心管理Manaager 功能详细描述:
 * 
 * @date [2012-9-28]
 */
public class MessageManager extends BroadCaster implements PraseListener, MessageDownLoadObserver {

	private static MessageManager selfInstance;
	private MessageHttp mMsgHttp;
	private MessageListBean mMsgListBean; // 所有消息列表
	private int mUnReadedCnt = 0;
	private MessageCenterDataModel mDataModel;
	private String mMsgId = null; // 消息内容id
	private int mEntrance = -1;

	private static final String MARKET_PRE = "market://";
	private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

	private static final int MAX_MSG_COUNT = 20;

	private Context mContext;
	/**
	 * 文件状态， 0 代表没有下载文件 ，1 代表已经下载但是没有安装该文件 ，2 代表已经安装了该文件
	 */
	public static final int FILE_NOT_EXIST = 0;
	public static final int FILE_NOT_INSTALL = 1;
	public static final int FILE_INSTALLED = 2;

	public static final int MSG_ZIP_DOWNLOAD_FINISHED = 0;
	public static final int MSG_ZIP_DOWNLOAD_FAILED = 1;
	private final static int MSG_REFRASH_ICONVIEW = 0X2;
	public static final int MSG_REUPDATE_STATISTICSDATA = 3;
	public static final int MSG_REUPDATE_THEME_STATISTICSDATA = 4;
	public static final int MSG_REUPDATE_ERRORSTATISTICSDATA = 5;

	private ArrayList<Bitmap> mEnterBmpList; // 进场动画图片
	private ArrayList<Bitmap> mActingBmpList; // 退场动画图片

	private final static int MSG_PREPARE_ICONVIEW = 0X34;

	private MessageHeadBean mMsgHeadBean; // 主题推送的特殊头消息

	private Timer mTimer = null;
	private WifiStateReceiver mReceiver = null;
	/**
	 * 2分钟监控
	 */
	private static final long WATCH_TIME_STEP = 2 * 60 * 1000;
	private byte[] mLock = new byte[0];

	private CacheManager mCacheManager = null;

	private MessageManager(Context context) {
		mDataModel = new MessageCenterDataModel(context);
		init();
		mMsgHttp = new MessageHttp(context);
		mMsgHttp.setPraseListener(this);
		mContext = context;
		mCacheManager = new CacheManager(new FileCacheImpl(LauncherEnv.Path.COUPON_PATH));
		if (ConstValue.DEBUG) {
			initProperties();
		}
	}

	public static synchronized MessageManager getMessageManager(Context context) {
		if (selfInstance == null) {
			selfInstance = new MessageManager(context);
		}
		return selfInstance;
	}

	private static void releaseSelf() {
		selfInstance = null;
	}

	/**
	 * 功能简述: 申请消息列表网络请求
	 */
	public void postUpdateRequest(final int auto) {
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			// mListener.updateFinish(false);
			broadCast(MessageCenterActivity.GET_MSG_NO_NETWORK, -1, null, null);
		} else {
			new Thread() {

				@Override
				public void run() {
					super.run();
					mMsgHttp.postUpdateRequest(auto);
				}

			}.start();
		}
		
	}

	/**
	 * 功能简述: 获取桌面后台链接
	 * 
	 * @param id
	 */
	public void postGetUrlRequest() {
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			broadCast(MessageCenterActivity.GET_MSG_NO_NETWORK, ConvertUtils.boolean2int(false),
					null, null);
		}

		new Thread() {

			@Override
			public void run() {
				super.run();
				mMsgHttp.postGetUrlRequest();
			}

		}.start();
	}

	/**
	 * 
	 * @param couponId
	 */
	public void postGetCouponRequest(final String couponId) {
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			// mListener.updateFinish(false);
			broadCast(MessageCenterActivity.GET_MSG_NO_NETWORK, -1, null, null);
		}
		new Thread() {

			@Override
			public void run() {
				super.run();
				mMsgHttp.postGetCouponRequest(couponId);
			}

		}.start();
	}
	
	/**
	 * 功能简述: 申请消息内容网络请求
	 * 
	 * @param id
	 */
	public void postGetMsgRequest(final String id) {
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			broadCast(MessageCenterActivity.GET_MSG_NO_NETWORK, ConvertUtils.boolean2int(false),
					null, null);
		}

		new Thread() {

			@Override
			public void run() {
				super.run();
				setCurrentMsgId(id);
				mMsgHttp.postGetMsgContentRequest(id);
			}

		}.start();
	}

	public void recycle() {
		mMsgId = null;
		if (mMsgListBean != null) {
			mMsgListBean.clearMsgs();
		}
		mMsgListBean = null;
		mMsgHttp = null;

		if (mReceiver != null && mContext != null) {
			if (ConstValue.DEBUG) {
				Log.i(ConstValue.MSG_TAG, "-------unregisterWifiStateReceiver----------");
			}
			mContext.unregisterReceiver(mReceiver);
			mReceiver = null;
		}

		releaseSelf();
		clearAllObserver();
	}

	public Vector<MessageHeadBean> getMessageList() {
		if (mMsgListBean == null) {
			return null;
		}
		return mMsgListBean.getAllMessagHead();
	}

	public MessageHeadBean getMessageHeadBean(String id) {
		if (mMsgListBean == null) {
			return null;
		}
		return mMsgListBean.getMessageHead(id);
	}

	public void markAllReaded() {
		if (mMsgListBean == null) {
			return;
		}
		removeCoverFrameView();
		for (int i = 0; i < mMsgListBean.getAllMessagHead().size(); i++) {
			MessageHeadBean head = mMsgListBean.getAllMessagHead().get(i);
			if (!head.misReaded) {
				markAsReaded(head);
			}
		}
	}

	/**
	 * 删除所有已读消息
	 */
	public void deleteAllReaded() {
		synchronized (mLock) {
			if (mMsgListBean == null) {
				return;
			}
			for (int i = 0; i < mMsgListBean.getAllMessagHead().size(); i++) {
				MessageHeadBean head = mMsgListBean.getAllMessagHead().get(i);
				if (head.misReaded) {
					if ((head.mViewType & MessageBaseBean.VIEWTYPE_STATUS_BAR) != 0) {
						NotificationManager nm = (NotificationManager) ApplicationProxy.getContext()
								.getSystemService(Context.NOTIFICATION_SERVICE);
						nm.cancel(head.mId, INotificationId.MESSAGECENTER_NEW_MESSAGE);
					}
					markAsDeleted(head);
					i--;
				}
			}
		}
	}
	/**
	 * 删除选中消息(已读消息会自动选择)
	 */
	public void deleteCheckedMessage() {
		// TODO Auto-generated method stub
		synchronized (mLock) {
			if (mMsgListBean == null) {
				return;
			}
			for (int i = 0; i < mMsgListBean.getAllMessagHead().size(); i++) {
				MessageHeadBean head = mMsgListBean.getAllMessagHead().get(i);
				if (head.mIsReadyDelete) {
					if ((head.mViewType & MessageBaseBean.VIEWTYPE_STATUS_BAR) != 0) {
						NotificationManager nm = (NotificationManager) ApplicationProxy.getContext()
								.getSystemService(Context.NOTIFICATION_SERVICE);
						nm.cancel(head.mId, INotificationId.MESSAGECENTER_NEW_MESSAGE);
					}
					markAsReaded(head);
					markAsDeleted(head);
					i--;
				}
			}
		}
	}

	public void clearMsg() {
		if (mMsgListBean != null) {
			mMsgListBean.clearMsgs();
		}
		mDataModel.deleteAllMessages();
		mMsgId = null;
		mUnReadedCnt = 0;
	}

	public void setCurrentMsgId(String msgId) {
		mMsgId = msgId;
	}

	public String getMsgId() {
		return mMsgId;
	}

	public boolean isReaded(String id) {
		return mMsgListBean.getMsgReaded(id);
	}

	public int getViewType(String id) {
		return mMsgListBean.getMsgViewType(id);
	}

	/**
	 * 
	 * 消息列表解析完毕的回调方法执行
	 */
	@Override
	public void listParseFinish(boolean bool, MessageListBean msgs) {
		synchronized (mLock) {

			if (msgs != null) {
				if (mMsgListBean == null) {
					mMsgListBean = new MessageListBean();

				}

				Vector<MessageHeadBean> beans = msgs.getAllMessagHead();
				if (!beans.isEmpty()) {
					ArrayList<MessageStatisticsBean> statisticsBeans = new ArrayList<MessageStatisticsBean>(beans.size());
					for (int i = 0; i < beans.size(); i++) {
						MessageHeadBean bean = beans.get(i);
						MessageStatisticsBean statisticsBean = new MessageStatisticsBean();
						statisticsBean.mMsgId = bean.mId;
						statisticsBean.mMapId = bean.mMapId == null ? "" : bean.mMapId;
						statisticsBean.mOprCode = "push";
						statisticsBean.mEntrance = MessageBaseBean.VIEWTYPE_NORMAL;
						statisticsBeans.add(statisticsBean);
					}
					mMsgHttp.saveStatisticsDatas(statisticsBeans, null, null, null, null, -1);
					updateStatisticsData(0l);

					saveMsgList(msgs);
				}
				addSaveMessagesToList();
				HttpUtil.sortList(mMsgListBean.getAllMessagHead());
				deleteFilterMsgs();
				filterMsgByWhitelist();
				deleteOutDateMsg(); 
				deleteHasDeletedMsg(); //对广告类消息也做一定的处理
				countUnreadCnt();
				initDeleteCheckBox();

			}
			if (msgs != null && !msgs.getAllMessagHead().isEmpty()) { // 收到的消息msg数量不为0，需刷新
				broadCast(MessageCenterActivity.GET_MSG_LIST_FINISH,
						ConvertUtils.boolean2int(bool), 1, null);
			} else { // 收到的消息数量为0，不需要刷新
				broadCast(MessageCenterActivity.GET_MSG_LIST_FINISH,
						ConvertUtils.boolean2int(bool), 0, null);
			}
		}
	}

	private void initDeleteCheckBox() {
		// TODO Auto-generated method stub
		Vector<MessageHeadBean> msgs = mMsgListBean.getAllMessagHead();
		if (msgs == null) {
			return;
		}
		for (int i = 0; i < msgs.size(); i++) {
			// 自动勾选已读消息
			msgs.get(i).mIsReadyDelete = msgs.get(i).misReaded;
		}
	}

	/**
	 * 功能简述: 删除一些过时的Msg 功能详细描述: 最多显示和保留20跳消息数据 注意:
	 */
	private void deleteOutDateMsg() {
		Vector<MessageHeadBean> heads = mMsgListBean.getAllMessagHead();
		if (heads.size() > MAX_MSG_COUNT) {
			int cnt = heads.size() - MAX_MSG_COUNT;
			for (int i = 0; i < cnt; i++) {
				MessageHeadBean head = heads.get(heads.size() - i - 1);
				mDataModel.deleteMessage(head.mId);
				heads.remove(head);
			}
		}
	}

	/**
	 * 功能简述: 删除掉已处于删除状态的已读消息 
	 * 4.15： 以及删除当不可推送广告消息时，删除该条广播消息
	 */
	private void deleteHasDeletedMsg() {
		Vector<MessageHeadBean> heads = mMsgListBean.getAllMessagHead();
		if (heads != null) {
		    
		    //isPrimeAd为false时，不可以推广告消息，即已经打开屏蔽广告开关。
		    boolean isRecommandPrimeAd = DeskSettingUtils.isPrimeAd(mContext);
			for (int i = 0; i < heads.size(); i++) {
				MessageHeadBean head = heads.get(i);
				
				//屏蔽开关打开，且这条消息为广告消息
				boolean isCanDeleteMessage = isCanDeleteAdMessage(head.mIsadv, isRecommandPrimeAd);
				if (head.mIsDeleted || isCanDeleteMessage) {
				    if (!head.mIsDeleted) {
				        mDataModel.deleteMessage(head.mId);
				    }
					heads.remove(head);
					i--;
				}
			}
		}
	}

	/**
	 * <br>功能简述:这条广告需要删除掉
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param adv
	 * @param isRecommandPrimeAd
	 * @return
	 */
	private boolean isCanDeleteAdMessage(int adv, boolean isRecommandPrimeAd) {
	    boolean result = false;
	    
	    if (!isRecommandPrimeAd && adv == 1) {
	        result = true;
	    }
	    
	    return result;
	}
	
	
	/**
	 * 功能描述：黑名单过滤掉已安装包名信息列表中的消息以及3d开关控制消息
	 */
	private void deleteFilterMsgs() {
		Vector<MessageHeadBean> heads = mMsgListBean.getAllMessagHead();
		Vector<MessageHeadBean> installPkgs = new Vector<MessageHeadBean>();
		Vector<MessageHeadBean> instructFilters = new Vector<MessageHeadBean>(); //指令消息过滤
		if (heads != null && heads.size() > 0) {
			for (int i = 0; i < heads.size(); i++) {
				MessageHeadBean head = heads.get(i);
				String filterpkgsString = head.mFilterPkgs;
				boolean isDeleted = false;
				if (filterpkgsString != null) {
					String[] filterpkgs = filterpkgsString.split(",");

					for (int j = 0; j < filterpkgs.length; j++) {
						String pkg = filterpkgs[j];
						// 判断过滤的包名程序已安装
						if (GoAppUtils.isAppExist(mContext, pkg)) {
							if (ConstValue.DEBUG) {
								Log.i(ConstValue.MSG_TAG, "deleteFilterMsgs filterPkgs id =" + head.mId);
							}
							installPkgs.add(head);
							mDataModel.deleteMessage(head.mId);
							heads.remove(head);
							i--;
							isDeleted = true;
							break;
						}

					}
				}
			}
			// 上传错误统计日志
			if (installPkgs.size() > 0) {
				updateErrorStatisticsData(installPkgs, 1, 1, 0);
			}

			if (instructFilters.size() > 0) {
				updateErrorStatisticsData(instructFilters, 1, 3, 0);
			}
		}
	}
	
	/**
	 * 上传3D开关控制统计数据
	 */
	public void upload3DInstructStatistics() {
		mMsgHttp.upload3DInstructStatistics();
	}
	
	/**
	 * 根据白名单信息过滤消息（未安装白名单内指定的包名或者安装版本不在规定内需过滤）
	 */
	private void filterMsgByWhitelist() {
		Vector<MessageHeadBean> heads = mMsgListBean.getAllMessagHead();
		Vector<MessageHeadBean> showMsgHeads = new Vector<MessageHeadBean>();

		for (int i = 0; i < heads.size(); i++) {
			MessageHeadBean head = heads.get(i);
			String whitelist = head.mWhiteList;
			if (ConstValue.DEBUG) {
				Log.i(ConstValue.MSG_TAG, "filterMsgByWhitelist id =" + head.mId + ",whitelist = "
						+ whitelist);
			}
			if (whitelist != null && !whitelist.equals("") && !whitelist.startsWith("null")) {
				String[] pkgInfos = whitelist.split(",");
				for (int j = 0; j < pkgInfos.length; j++) { // 格式为包名#minversioncode|maxversioncode
					String pkgInfo = pkgInfos[j];
					int index = pkgInfo.indexOf("#");
					int index1 = pkgInfo.indexOf("|");
					String pkgName = ""; // 包名
					String minVersionCode = ""; // 最小versionCode
					String maxVersioncode = ""; // 最大versionCode
					// 获取包名等信息
					if (index < 0) {
						pkgName = pkgInfo;
					} else {
						pkgName = pkgInfo.substring(0, index);
						if (index1 < 0) {
							minVersionCode = pkgInfo.substring(index + 1);
						} else {
							minVersionCode = pkgInfo.substring(index + 1, index1);
							maxVersioncode = pkgInfo.substring(index1 + 1);
						}
					}
					if (ConstValue.DEBUG) {
						Log.d(ConstValue.MSG_TAG, "名单信息：" + pkgInfo + ",包名：" + pkgName + ",min = "
								+ minVersionCode + ",max = " + maxVersioncode);
					}
					if (GoAppUtils.isAppExist(mContext, pkgName)) {
						int min = -1;
						int max = -1;
						int versionCode = -1;
						try {
							if (!minVersionCode.equals("")) {
								min = Integer.parseInt(minVersionCode);
							}

						} catch (Exception e) {

						}
						try {
							if (!maxVersioncode.equals("")) {
								max = Integer.parseInt(maxVersioncode);
							}

						} catch (Exception e) {

						}
						try {
							versionCode = mContext.getPackageManager().getPackageInfo(pkgName, 0).versionCode;
						} catch (NameNotFoundException e) {

						}

						if (ConstValue.DEBUG) {
							Log.i(ConstValue.MSG_TAG, "包的versioncode :" + versionCode + ",min = "
									+ min + ",max = " + max);
						}
						if (canShowMsg(versionCode, min, max)) {
							if (ConstValue.DEBUG) {
								Log.d(ConstValue.MSG_TAG, "安装了应用。。。。可显示消息");
							}
							showMsgHeads.add(head);
							break;
						}
					}
				}
			} else { // 未填写白名单表示都显示
				showMsgHeads.add(head);
			}
		}

		Vector<MessageHeadBean> deleteHeads = new Vector<MessageHeadBean>();
		// 找到所有可显示的信息，清除不可显示的信息
		for (int i = 0; i < heads.size(); i++) {
			MessageHeadBean head = heads.get(i);
			if (!showMsgHeads.contains(head)) {
				deleteHeads.add(head);
				mDataModel.deleteMessage(head.mId);
				heads.remove(head);
				i--;
			}
		}

		// 上传统计
		if (deleteHeads.size() > 0) {
			updateErrorStatisticsData(deleteHeads, 1, 2, 0);
		}
	}

	/**
	 * 判断版本号是否在区间内
	 * 
	 * @param versionCode
	 * @param min
	 * @param max
	 * @return
	 */
	private boolean canShowMsg(int versionCode, int min, int max) {
		boolean canShow = false;
		if (min != -1) {
			if (max == -1) {
				if (versionCode >= min) {
					canShow = true;
				}
			} else {
				if (versionCode >= min && versionCode < max) {
					canShow = true;
				}
			}
		} else {
			if (max == -1) {
				canShow = true;
			} else {
				if (versionCode < max) {
					canShow = true;
				}
			}
		}
		return canShow;
	}

	public void markAsReaded(String id) {
		if (mMsgListBean != null) {
			MessageHeadBean head = mMsgListBean.getMessageHead(id);
			markAsReaded(head);
		}
	}

	public void markAsReaded(MessageHeadBean msg) {
		if (msg == null || msg.misReaded) {
			return;
		}
		MessageInfo info = convertBean2Info(msg);
		info.misReaded = true;
		mDataModel.updateRecord(info);
		if (mUnReadedCnt > 0) {
			mUnReadedCnt--;
		}
		mMsgListBean.setReaded(msg.mId, true);
	}

	public void markAsClickClosed(MessageHeadBean msg) {
		if (msg == null || msg.mClickClosed) {
			return;
		}
		MessageInfo info = convertBean2Info(msg);
		info.mClickClosed = true;
		mDataModel.updateRecord(info);
		mMsgListBean.setClickClosed(msg.mId, true);
	}

	public void markAsRemoved(MessageHeadBean msg) {
		if (msg == null || msg.mIsRemoved) {
			return;
		}
		MessageInfo info = convertBean2Info(msg);
		info.mIsRemoved = true;
		mDataModel.updateRecord(info);
		mMsgListBean.setRemoved(msg.mId, true);
	}

	public void markAsDeleted(MessageHeadBean msg) {
		if (msg == null || msg.mIsDeleted) {
			return;
		}
		MessageInfo info = convertBean2Info(msg);
		info.mIsDeleted = true;
		mDataModel.updateRecord(info);
		mMsgListBean.removeDeletedMsg(msg);
	}

	private MessageInfo convertBean2Info(MessageHeadBean bean) {
		MessageInfo info = new MessageInfo();
		info.mId = bean.mId;
		info.misReaded = isReaded(bean.mId);
		info.mTimeStamp = bean.mMsgTimeStamp;
		info.mType = bean.mType;
		info.mTitle = bean.mTitle;
		info.mViewType = bean.mViewType;
		info.mUrl = bean.mUrl;
		info.mActtype = bean.mActType;
		info.mActValue = bean.mActValue;
		info.mEndTime = bean.mEndTime;
		info.mIconUrl = bean.mIcon;
		info.mIntro = bean.mSummery;
		info.mStartTime = bean.mStartTime;
		info.mZIcon1 = bean.mZicon1;
		info.mZIcon2 = bean.mZicon2;
		info.mZpos = bean.mZpos;
		info.mZtime = bean.mZtime;
		info.mIsClosed = bean.mIsColsed;
		info.mFilterPkgs = bean.mFilterPkgs;
		info.mClickClosed = bean.mClickClosed;
		info.mDynamic = bean.mDynamic;
		info.mIconpos = bean.mIconpos;
		info.mFullScreenIcon = bean.mFullScreenIcon;
		info.mIsRemoved = bean.mIsRemoved;
		info.mWhiteList = bean.mWhiteList;
		info.mIsNew = bean.mIsNew;
		info.mIsWifi = bean.mIsWifi;
		info.mMsgOutDateTime = bean.mMsgOutDateTime;
		info.mIsDeleted = bean.mIsDeleted;
		info.mCoupon = bean.mCoupon;
		info.mPromotion = bean.mPromotion;
		info.mInstruct = bean.mInstruct;
		info.mPackageName = bean.mPackageName;
		info.mMapId = bean.mMapId;
		info.mAddact = bean.mAddact;
		info.mIsadv = bean.mIsadv;
		info.mMsgimgurl = bean.mMsgimgurl;
		return info;
	}

	/**
	 * 功能简述: 消息列表的初始化 功能详细描述: 从数据库中读取内容，转化成MessageListBean 注意:
	 */
	private void init() {
		Cursor cursor = mDataModel.queryMessages();
		if (cursor != null) {
			cursor.moveToPosition(-1);
			mMsgListBean = new MessageListBean();
			Vector<MessageHeadBean> msgs = mMsgListBean.getAllMessagHead();
			while (cursor.moveToNext()) {
				int readIndex = cursor.getColumnIndex(MessageCenterTable.READED);
				int deleteIndex = cursor.getColumnIndex(MessageCenterTable.DELETED);
				if (deleteIndex != -1) {
					boolean delete = ConvertUtils.int2boolean(cursor.getInt(deleteIndex));
					if (delete) { // 消息已经删除不缓存
						continue;
					}
				}
				if (readIndex != -1) {
					boolean read = ConvertUtils.int2boolean(cursor.getInt(readIndex));
					if (!read) {
						mUnReadedCnt++;
					}

					MessageInfo info = new MessageInfo();
					info.parseFromCursor(cursor);
					MessageHeadBean bean = convertInfo2Bean(info);
					msgs.add(bean);
				}
			}
			cursor.close();
			cursor = null;
		}

	}

	private void countUnreadCnt() {
		Vector<MessageHeadBean> msgs = mMsgListBean.getAllMessagHead();
		if (msgs == null /* || mReadedList == null */) {
			return;
		}
		mUnReadedCnt = 0;
		for (int i = 0; i < msgs.size(); i++) {
			MessageHeadBean msg = msgs.get(i);
			if (!msg.misReaded) {
				mUnReadedCnt++;
			} 
		}
	}

	public int getUnreadedCnt() {
		if (ConstValue.DEBUG) {
			Log.d(ConstValue.MSG_TAG, "count:" + mUnReadedCnt);
		}
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			return 0;
		}
		return mUnReadedCnt;
	}

	public int getReadedCnt() {
		if (!Machine.isNetworkOK(ApplicationProxy.getContext())) {
			return 0;
		} else {
			Vector<MessageHeadBean> msgs = mMsgListBean.getAllMessagHead();
			int readedCnt = msgs.size() - mUnReadedCnt;
			if (ConstValue.DEBUG) {
				Log.d(ConstValue.MSG_TAG, "readedCnt count:" + readedCnt);
			}
			return readedCnt;
		}
	}

	public synchronized void showMessage(MessageHeadBean bean) {
		if (bean == null) {
			return;
		}
		markAsReaded(bean);
		setCurrentMsgId(bean.mId);
		Intent intent = new Intent(ApplicationProxy.getContext(), MessageContentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("msgid", bean.mId);
		if (bean.mType == MessageBaseBean.TYPE_HTML) {
			bundle.putString("msgurl", bean.mUrl);
		} else {
			bundle.putString("msgurl", bean.mActValue);
		}
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ApplicationProxy.getContext().startActivity(intent);
	}

	private synchronized void showWebView(MessageHeadBean bean) {
		if (bean == null) {
			return;
		}
		markAsReaded(bean);
		setCurrentMsgId(bean.mId);
		Intent intent = new Intent(ApplicationProxy.getContext(), MessageContentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("msgid", bean.mId);
		bundle.putString("msgurl", bean.mActValue);
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ApplicationProxy.getContext().startActivity(intent);
	}

	public void showDialogMessage(String id) {
		if (id == null) {
			return;
		}
		MessageHeadBean bean = getMessageHeadBean(id);
		if (bean == null) {
			return;
		}
		
		//判断桌面是否在前台运行，只在桌面在前台运行才弹窗
		boolean launcherIsTop = Machine.isTopActivity(ApplicationProxy.getContext(),
		        PackageName.PACKAGE_NAME);
		
		if (launcherIsTop) {
			// 上传展示数据
			String mapId = bean.mMapId == null ? "" : bean.mMapId;
			mMsgHttp.saveStatisticsDatas(null, bean.mId, mapId, "show", "1", MessageBaseBean.VIEWTYPE_DIALOG);
			updateStatisticsData(0);
			
			setCurrentMsgId(bean.mId);
			markAsReaded(bean);
			Intent intent = new Intent(ApplicationProxy.getContext(), MessageDialogContentActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("msgid", bean.mId);
			if (bean.mType == MessageBaseBean.TYPE_HTML) {
				bundle.putString("msgurl", bean.mUrl);
			} else {
				bundle.putString("msgurl", bean.mActValue);
			}
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				ApplicationProxy.getContext().startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			saveWaitToShowDialogMsg(false);
		} else {
			saveWaitToShowDialogMsg(true);
		}
	}
	
	/**
	 * 保存消息需要弹窗显示时桌面是否在后台的信息，当前桌面不在顶层，下次启动桌面一份钟后再弹
	 * @param isNeedToWaitShow
	 */
	private void saveWaitToShowDialogMsg(boolean isNeedToWaitShow) {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		manager.putBoolean(IPreferencesIds.SHAREDPREFERENCES_MSG_WATI_TO_SHOW_DIALOG, isNeedToWaitShow);
		manager.commit();
	}
	
	/**
	 * <br>功能简述: 检查未被执行的评分弹框任务
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void checkUnExcutedShowDialogMsg() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				PreferencesManager preferencesManager = new PreferencesManager(mContext,
						IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
				AlarmManager alarmManager = null;
				//是否需要显示弹窗消息
				boolean needToShowDialogMsg = preferencesManager.getBoolean(
						IPreferencesIds.SHAREDPREFERENCES_MSG_WATI_TO_SHOW_DIALOG, false);
				if (needToShowDialogMsg) {
					if (alarmManager == null) {
						alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
					}
					Intent rateAppIntent = new Intent(ICustomAction.ACTION_MSGCENTER_SHOWMSG);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
							rateAppIntent, 0);
					alarmManager.set(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis() + 60 * 1000, pendingIntent);
				}
			}

		}.start();
	}

	/**
	 * 功能简述: 消息中心通知栏的展示的准备
	 * 
	 * @param bean
	 */
	public void sendNotify(MessageHeadBean bean) {
		if (bean == null) {
			return;
		}
		mMsgHeadBean = bean;
		if (mMsgHeadBean.mIsWifi == 1 && !isWifiConnected()) { // 不在wifi控制下展示或者在wifi控制下网络连接正常展示
			// mMsgHttp.saveWaitToNotifyData(mMsgHeadBean.mId);
			// registerWifiStateReceiver();
			saveDataInfoAndRegisterWifiReceiver(bean);
		} else {
			mMsgHeadBean.initIconView(this);
			mHandler.sendEmptyMessage(MSG_PREPARE_ICONVIEW);
		}

	}

	public void saveDataInfoAndRegisterWifiReceiver(MessageHeadBean bean) {
		mMsgHttp.saveWaitToNotifyData(bean.mId);
		registerWifiStateReceiver();
	}

	/**
	 * 判断wifi连接是否正常
	 * 
	 * @return
	 */
	public boolean isWifiConnected() {
		boolean isConnected = false;
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == State.CONNECTED) {
			isConnected = true;
		}
		if (ConstValue.DEBUG) {
			Log.v(ConstValue.MSG_TAG, "isWifiConnected  isConnected = " + isConnected);
		}
		return isConnected;
	}

	/**
	 * 注册wifi状态监听器
	 */
	private void registerWifiStateReceiver() {
		if (mReceiver == null && mContext != null) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
			mReceiver = new WifiStateReceiver();
			mContext.registerReceiver(mReceiver, filter);
			if (ConstValue.DEBUG) {
				Log.v(ConstValue.MSG_TAG, "----------registerWifiStateReceiver----------");
			}
		}
	}

	/**
	 * 监听wifi状态变更类
	 * 
	 * @author liulixia
	 * 
	 */
	private class WifiStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (ConstValue.DEBUG) {
				Log.i(ConstValue.MSG_TAG, "WifiStateReceiver  onReceive  action = " + action);
			}
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState) {
					case WifiManager.WIFI_STATE_DISABLED :
						if (ConstValue.DEBUG) {
							Log.d(ConstValue.MSG_TAG,
									"WifiStateReceiver  onReceive  WIFI_STATE_DISABLED");
						}
						if (mTimer != null) {
							mTimer.cancel();
						}
						break;
					case WifiManager.WIFI_STATE_ENABLED :
						String msgId = mMsgHttp.getWaitToNotifyData();
						if (ConstValue.DEBUG) {
							Log.d(ConstValue.MSG_TAG,
									"WifiStateReceiver  onReceive  WIFI_STATE_ENABLED msgId = "
											+ msgId);
						}
						if (msgId != null) {
							mTimer = new Timer();
							NotificationTimerTask task = new NotificationTimerTask(msgId);
							mTimer.schedule(task, WATCH_TIME_STEP);
						}
						break;
				}
			}
		}

	}

	/**
	 * 定时弹通知栏信息类
	 * 
	 * @author liulixia
	 * 
	 */
	private class NotificationTimerTask extends TimerTask {
		private String mMessageId = null;

		public NotificationTimerTask(String msgId) {
			mMessageId = msgId;
		}

		@Override
		public void run() {
			if (ConstValue.DEBUG) {
				LogUnit.diyInfo(ConstValue.MSG_TAG, "打开wifi后定时器运行，msgId = " + mMessageId);
			}
			MessageHeadBean bean = getMessageHeadBean(mMessageId);
			if (bean != null && !isInMessageCenter() && !bean.misReaded
					&& (bean.mMsgOutDateTime == null || !isExpired(bean.mMsgOutDateTime))) {
				if (!bean.mIsRemoved && (bean.mViewType & MessageBaseBean.VIEWTYPE_STATUS_BAR) != 0) { // 通知栏
					mMsgHeadBean = bean;
					mMsgHeadBean.initIconView(MessageManager.this);
					mHandler.sendEmptyMessage(MSG_PREPARE_ICONVIEW);
				} else if ((bean.mViewType & MessageBaseBean.VIEWTYPE_DESK_TOP) != 0
						&& !bean.mClickClosed) { // 罩子层
					prepareZipRes(bean);
				} else if ((bean.mViewType & MessageBaseBean.VIEWTYPE_DIALOG) != 0) { // 弹出框
					showDialogMessage(bean.mId);
				}
			}
			mMsgHttp.removeWaitToNotifyData();
			if (mReceiver != null && mContext != null) {
				if (ConstValue.DEBUG) {
					Log.i(ConstValue.MSG_TAG, "-------unregisterWifiStateReceiver----------");
				}
				mContext.unregisterReceiver(mReceiver);
				mReceiver = null;
			}
		}

	}
	
	/**
	 * 是否过期
	 * @param end
	 * @return
	 */
	public boolean isExpired(String end) {
		try {
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 日期格式
			df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			String sDate = df.format(date);
			if (sDate.compareTo(end) > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 功能简述: 消息中心通知栏的展示
	 * 
	 * @param bean
	 */
	public void showMsgNotify(MessageHeadBean bean) {
		if (bean == null || bean.mIsRemoved || bean.misReaded) { //如果已读或已移除，不再弹
			return;
		}
		Intent intent = null;

		Bundle bundle = new Bundle();
		if (bean.mType == MessageBaseBean.TYPE_HTML) {
			intent = new Intent(ApplicationProxy.getContext(), MessageContentActivity.class);
			bundle.putString("msgid", bean.mId);
			bundle.putString("msgurl", bean.mUrl);
			bundle.putInt("where", MessageBaseBean.VIEWTYPE_STATUS_BAR);
			bundle.putInt("msgiswifi", bean.mIsWifi);
		} else {
			intent = new Intent();
			bundle.putString("msgId", bean.mId);
			bundle.putInt("msgiswifi", bean.mIsWifi);
			intent.setClass(ApplicationProxy.getContext(), MsgNotifyActivity.class);
		}

		intent.putExtras(bundle);
		try {
			PendingIntent contentIntent = PendingIntent.getActivity(ApplicationProxy.getContext(), 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			Notification notification = new Notification(R.drawable.msg_center_notification,
					bean.mTitle, System.currentTimeMillis());
			RemoteViews contentView = new RemoteViews(ApplicationProxy.getContext().getPackageName(),
					R.layout.msg_center_noitify_content);

			if (mMsgHeadBean.mBitmap != null && !mMsgHeadBean.mBitmap.isRecycled()) {
				if (mMsgHeadBean.mFullScreenIcon != null
						&& !mMsgHeadBean.mFullScreenIcon.equals("")) {
					// Bitmap bmp = bitmpaScale(mMsgHeadBean.mBitmap);
					contentView.setViewVisibility(R.id.theme_view_image, View.GONE);
					contentView.setViewVisibility(R.id.theme_detail_content, View.GONE);
					contentView.setViewVisibility(R.id.theme_full_screen_icon, View.VISIBLE);
					contentView.setImageViewBitmap(R.id.theme_full_screen_icon,
							mMsgHeadBean.mBitmap);
				} else {
					contentView.setImageViewBitmap(R.id.theme_view_image, mMsgHeadBean.mBitmap);
					contentView.setTextViewText(R.id.theme_title, bean.mTitle);
					contentView.setTextViewText(R.id.theme_content, bean.mSummery);
				}
			} else {
				contentView.setImageViewResource(R.id.theme_view_image,
						R.drawable.msg_center_notification);
				contentView.setTextViewText(R.id.theme_title, bean.mTitle);
				contentView.setTextViewText(R.id.theme_content, bean.mSummery);
			}

			notification.contentIntent = contentIntent;
			notification.contentView = contentView;

//			Bundle deleteBundle = new Bundle();
//			deleteBundle.putString("msgId", bean.mId);
//			deleteBundle.putBoolean("remove", true);
//			Intent deleteIntent = new Intent();
//			deleteIntent.setClass(ApplicationProxy.getContext(), MsgNotifyActivity.class);
//			deleteIntent.putExtras(deleteBundle);
//			notification.deleteIntent = PendingIntent.getActivity(ApplicationProxy.getContext(), 1,
//					deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			//判断消息是否广告消息
			if (bean.mAddact == 1 || bean.mAddact == 3) {
				//手动清除时发送广播,引导用户购买付费版
				Intent delIntent = new Intent(NoAdvertCheckReceiver.NOADVERT_CHECK_ACTION);
				notification.deleteIntent = PendingIntent.getBroadcast(ApplicationProxy.getContext(), 0, delIntent, 0);
			}

			// 点击后自动消失
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			NotificationManager nm = (NotificationManager) ApplicationProxy.getContext()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(bean.mId, INotificationId.MESSAGECENTER_NEW_MESSAGE, notification);
			// nm.notify(INotificationId.MESSAGECENTER_NEW_MESSAGE,
			// notification);

//			saveShowStatisticsData(bean.mId);
//			Vector<MessageHeadBean> statisticsMsgs = new Vector<MessageHeadBean>();
//			statisticsMsgs.add(bean);
//			updateStatisticsData(statisticsMsgs, MessageBaseBean.VIEWTYPE_STATUS_BAR, 0,
//					IPreferencesIds.SHAREDPREFERENCES_MSG_SHOW_TIMES, null);
			String mapId = bean.mMapId == null ? "" : bean.mMapId;
			mMsgHttp.saveStatisticsDatas(null, bean.mId, mapId, "show", "1", MessageBaseBean.VIEWTYPE_STATUS_BAR);
			updateStatisticsData(0);

			//弹一次就把它设置为已移除状态，不再重复弹
			markAsRemoved(bean);
		} catch (Exception e) {
			Log.i(LogConstants.HEART_TAG, "start notification error id = "
					+ INotificationId.MESSAGECENTER_NEW_MESSAGE);
		}
	}

	public void abortPost() {
		if (mMsgHttp != null) {
			mMsgHttp.abortPost();
		}
	}

	public ArrayList<MessageInfo> getNeedShowMessageInfo() {
		Cursor cursor = mDataModel.queryNeedShowdMessages();
		ArrayList<MessageInfo> msgInfos = null;
		if (cursor != null) {
			msgInfos = new ArrayList<MessageInfo>();
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				MessageInfo info = new MessageInfo();
				info.parseFromCursor(cursor);
				msgInfos.add(info);
			}
			cursor.close();
			cursor = null;
		}

		return msgInfos;
	}
	
	/**
	 * <br>功能简述:数据库耗时操作
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
    public ArrayList<MessageInfo> getPromotionMessageInfo() {
        Cursor cursor = mDataModel.queryPromotionMessages();
        ArrayList<MessageInfo> msgInfos = null;
        if (cursor != null) {
            msgInfos = new ArrayList<MessageInfo>();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                MessageInfo info = new MessageInfo();
                info.parseFromCursor(cursor);
                msgInfos.add(info);
            }
            cursor.close();
            cursor = null;
        }

        return msgInfos;
    }

    public ArrayList<PromotionBean> getPromotionList(
            ArrayList<MessageInfo> msgList) {
        ArrayList<PromotionBean> promotionList = new ArrayList<PromotionBean>();

        for (MessageInfo msg : msgList) {

            if (msg.mPromotion != null) {
                parseMessageList(promotionList, msg.mPromotion);
            }
        }

        return promotionList;
    }

    private void parseMessageList(ArrayList<PromotionBean> promotionList,
            String content) {

        String[] contents = content.split("#");
        String type = contents[0];
        String code = contents[1];
        String sTime = contents[2];
        String eTime = contents[3];
        if (type != null && code != null && sTime != null && eTime != null) {
            PromotionBean promotionBean = new PromotionBean(code, sTime, eTime);
            promotionList.add(promotionBean);
        }

    }

    public boolean isPromotionPeriod(ArrayList<PromotionBean> promotionList) {
        boolean result = false;
        if (promotionList == null || promotionList.size() <= 0) {
            return result;
        }
        int size = promotionList.size();
        for (int i = 0; i < size; i++) {
            PromotionBean bean = promotionList.get(i);
            if (bean.isAfterStime() && bean.isVaild()) {
                result = true;
            }
        }
        return result;
    }
	
	/**
	 * 功能简述: 保存消息列表到数据库中
	 * 
	 * @param listBean
	 */
	private void saveMsgList(MessageListBean listBean) {
		if (listBean == null || listBean.getAllMessagHead().size() == 0) {
			return;
		}
		Vector<MessageHeadBean> msgs = listBean.getAllMessagHead();
		for (int i = 0; i < msgs.size(); i++) {
			MessageHeadBean bean = msgs.get(i);
			MessageInfo info = convertBean2Info(bean);
			mDataModel.insertRecord(info);
		}
	}

	/**
	 * 功能简述: 从数据库中读取消息内容到list中
	 * 
	 */
	private void addSaveMessagesToList() {
		Cursor cursor = mDataModel.queryMessages();
		if (cursor != null) {
			mMsgListBean.clearMsgs();
			Vector<MessageHeadBean> msgs = mMsgListBean.getAllMessagHead();
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				MessageInfo info = new MessageInfo();
				info.parseFromCursor(cursor);
				if (info.mId == null) {
					continue;
				}
				MessageHeadBean bean = convertInfo2Bean(info);
				if (mMsgListBean.getMessageHead(bean.mId) != null) {
					mMsgListBean.getMessageHead(bean.mId).misReaded = info.misReaded;
				} else {
					msgs.add(bean);
				}
			}
			cursor.close();
		}
		cursor = null;
	}

	/**
	 * 功能简述: 处理消息内容中的控件的点击事件 功能详细描述: 根据MessageWidgetBean中的动作类型，以及当前所处的activity环境
	 * 注意:
	 * 
	 * @param bean
	 * @param activity
	 */
	public void handleWidgetClick(MessageWidgetBean bean, Activity activity) {
		if (ConstValue.DEBUG) {
			LogUnit.diyInfo(ConstValue.MSG_TAG, "handleWidgetClick，actiontype = " + bean.mActtype
					+ ", actionvalue = " + bean.mActvaule);
		}
		if (bean.mActtype == MessageWidgetBean.ACTTYPE_NON) {
			activity.finish();
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_CANCLE) {
			activity.finish();
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_LINK) {
			// 使用浏览器去url
			GoAppUtils.gotoBrowserInRunTask(mContext, bean.mActvaule);
//			gotoBrowser(bean.mActvaule);
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_GOSTORE) {
			if (bean.mActvaule == null) {
				return;
			}
			if (bean.mActvaule.equals(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE)) {
				Intent it = new Intent(bean.mActvaule);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICommonMsgId.START_ACTIVITY, -1, it, null);
			} else {
				AppsDetail.gotoDetailDirectly(mContext, AppsDetail.START_TYPE_APPRECOMMENDED,
						bean.mActvaule);
				// GoStoreOperatorUtil.gotoStoreDetailDirectly(activity,
				// bean.mActvaule);
			}
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_OPENGO) {
			if (bean.mActvaule == null) {
				return;
			}

			Intent it = new Intent(bean.mActvaule);
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.START_ACTIVITY,
					-1, it, null);
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_OTHER) {
			if (bean.mActvaule == null) {
				return;
			}
			if (bean.mActvaule.startsWith(MARKET_PRE)) {
				if (GoAppUtils.isMarketExist(activity)) {
					GoAppUtils.gotoMarket(
							activity,
							MarketConstant.APP_DETAIL
									+ bean.mActvaule.substring(MARKET_PRE.length()));
				} else {
					// AppUtils.gotoBrowser(activity,
					// MARKET_URL +
					// bean.mActvaule.substring(MARKET_PRE.length()));
					GoAppUtils.gotoBrowserInRunTask(mContext, MARKET_URL + bean.mActvaule.substring(MARKET_PRE.length()));
//					gotoBrowser(MARKET_URL + bean.mActvaule.substring(MARKET_PRE.length()));
				}
			}
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_DOWNLOAD) {
			if (activity instanceof MessageContentActivity) {
				int state = getFileState(bean);
				switch (state) {
					case FILE_NOT_EXIST :
						((MessageContentActivity) activity).startDownLoad(bean.mActvaule);
						break;
					case FILE_NOT_INSTALL :
						String fileName = LauncherEnv.Path.MESSAGECENTER_PATH + mMsgId + ".apk";
						ApkInstallUtils.installApk(fileName);
						break;
					case FILE_INSTALLED :
						// do nothing
						break;
					default :
						break;
				}
			} else if (activity instanceof MessageDialogContentActivity) {
				int state = getFileState(bean);
				switch (state) {
					case FILE_NOT_EXIST :
						((MessageDialogContentActivity) activity).startDownLoad(bean.mActvaule);
						activity.finish();
						break;
					case FILE_NOT_INSTALL :
						String fileName = LauncherEnv.Path.MESSAGECENTER_PATH + mMsgId + ".apk";
						ApkInstallUtils.installApk(fileName);
						activity.finish();
						break;
					case FILE_INSTALLED :
						// do nothing
						activity.finish();
						break;
					default :
						break;
				}
			}
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_NEWACTION) {
			action(bean.mActvaule);
			if (bean.mActvaule != null && bean.mActvaule.startsWith(ConstValue.PREFIX_GUI)) {
				// GuiThemeStatistics.setCurrentEntry(GuiThemeStatistics.ENTRY_MESSAGE_CENTER,
				// mContext);
				String id = getId(bean.mActvaule, ConstValue.PREFIX_GUI);
				if (id != null) {
					if (id.equals("1")) {
						GuiThemeStatistics.guiStaticData("",
								GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
								String.valueOf(GuiThemeStatistics.ENTRY_MESSAGE_CENTER), "", "", "");
					} else if (id.equals("2")) {
						GuiThemeStatistics.guiStaticData("", GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
								String.valueOf(GuiThemeStatistics.ENTRY_MESSAGE_CENTER_LOCKER), "", "", "");
					}
				}
			}
		} else if (bean.mActtype == MessageWidgetBean.ACTTYPE_INSTRUCT) { //指令命令控制
			activity.finish();
		}

		// 上传点击
//		uploadWidgetStatistics(bean.mType);
		MessageHeadBean msgHeadBean = null;
		synchronized (mLock) {
			msgHeadBean = getMessageHeadBean(mMsgId);
		}
		String mapId = "";
		if (msgHeadBean != null) {
			mapId = msgHeadBean.mMapId == null ? "" : msgHeadBean.mMapId;
			mMsgHttp.saveRecommandedApps(msgHeadBean.mPackageName, msgHeadBean.mMapId, msgHeadBean.mId);
		}
		mMsgHttp.saveStatisticsDatas(null, mMsgId, mapId, bean.mType, "1", mEntrance);
		updateStatisticsData(0);
	}

	/**
	 * 设置点击入口（消息中心、通知栏、罩子层、对话框）
	 * 
	 * @param entrance
	 */
	public void setEntrance(int entrance) {
		mEntrance = entrance;
	}

	/**
	 * 功能简述: 判断当前环境是否在消息中心里面 功能详细描述: 注意:
	 * 
	 * @return
	 */
	public boolean isInMessageCenter() {
		if (getObserver() != null) {
			ArrayList<BroadCasterObserver> list = getObserver();
			for (BroadCasterObserver observer : list) {
				if (observer instanceof MessageCenterActivity
						|| observer instanceof MessageContentActivity) {
					return true;
				}
			}
		}
		return false;
	}

	public int getFileState(MessageWidgetBean bean) {
		int state = FILE_NOT_EXIST;
		if (bean.mActtype == MessageWidgetBean.ACTTYPE_DOWNLOAD) {
			String fileName = LauncherEnv.Path.MESSAGECENTER_PATH + mMsgId + ".apk";

			if (bean.mActvaule != null) {
				String[] urlContent = bean.mActvaule.split(MessageBaseBean.URL_SPLIT);
				if (urlContent.length < 2) {
					return state;
				}
				String[] nameContent = urlContent[1].split(MessageBaseBean.URL_SPLIT_NAME);
				String pkgName = nameContent[0];

				File newfile = new File(fileName);
				if (GoAppUtils.isAppExist(mContext, pkgName)) {
					if (newfile.exists()) {
						state = FILE_INSTALLED;
					}
				} else {
					if (newfile.exists()) {
						state = FILE_NOT_INSTALL;
					}
				}
			}
		}
		return state;
	}

	private void initProperties() {
		if (FileUtil.isSDCardAvaiable()
				&& !FileUtil.isFileExist(LauncherEnv.Path.MESSAGECENTER_PATH + "properties.txt")) {
			try {
				new Thread() {
					public void run() {
						String uid = GoStorePhoneStateUtil.getUid(mContext);
						String info = uid + "#" + ConstValue.sHosturlBase;
						FileUtil.saveByteToSDFile(info.getBytes(),
								LauncherEnv.Path.MESSAGECENTER_PATH + "properties.txt");
					}
				}.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LogUnit.sLogToFile = "messagecenter";
	}

	/**
	 * <br>
	 * 功能简述:向CoverFrame中添加view <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param head
	 */
	public void addViewToCoverFrame(final MessageHeadBean head) {

		if (head == null) {
			return;
		}
		removeCoverFrameView();
		final CoverFrameView view = new CoverFrameView(mContext, head, mEnterBmpList, mActingBmpList);
		FrameLayout container = new FrameLayout(mContext) {
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				if (view != null) {
					return view.onTouchEvent(event);
				}
				return super.onTouchEvent(event);
			}
		};
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		view.setMessageHead(head);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (head.mIsWifi == 1 && !isWifiConnected()) {
					Toast.makeText(mContext, R.string.msgcenter_suggest_open_wifi,
							2000).show();
					return;
				}
				handleMsgClick(head, MessageBaseBean.VIEWTYPE_DESK_TOP);
				// updateStatisticsData(head,
				// MessageBaseBean.VIEWTYPE_DESK_TOP);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW, CoverFrame.COVER_VIEW_MESSAGECENTER,
						null, null);
				recyleZicons();
			}
		});
		container.addView(view, params);
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICoverFrameMsgId.COVER_FRAME_ADD_VIEW,
				CoverFrame.COVER_VIEW_MESSAGECENTER, container, null);
//		saveShowStatisticsData(head.mId);
//		Vector<MessageHeadBean> statisticsMsgs = new Vector<MessageHeadBean>();
//		statisticsMsgs.add(head);
//		updateStatisticsData(statisticsMsgs, MessageBaseBean.VIEWTYPE_DESK_TOP, 0,
//				IPreferencesIds.SHAREDPREFERENCES_MSG_SHOW_TIMES, null);
		String mapId = head.mMapId == null ? "" : head.mMapId;
		mMsgHttp.saveStatisticsDatas(null, head.mId, mapId, "show", "1", MessageBaseBean.VIEWTYPE_DESK_TOP);
		updateStatisticsData(0);
	}

	/**
	 * <br>
	 * 功能简述:准备罩子层消息所需要的资源 <br>
	 * 功能详细描述:下载zip包以及解压 <br>
	 * 注意:解压后的文件夹名为id+zip1/2
	 * 
	 * @param head
	 */
	public void prepareZipRes(final MessageHeadBean head) {
		new Thread() {
			@Override
			public void run() {
				String zip1 = "zip1.zip";
				String zip2 = "zip2.zip";
				super.run();
				if (head.mZicon1 != null || head.mZicon2 != null) {
					File file = new File(LauncherEnv.Path.MESSAGECENTER_PATH + head.mId + zip1);
					if (!file.exists()) {
						if (!mMsgHttp.downloadZipRes(head.mZicon1,
								LauncherEnv.Path.MESSAGECENTER_PATH, head.mId + zip1)) {
						}
					}
					try {
						HttpUtil.unZipFolder(LauncherEnv.Path.MESSAGECENTER_PATH + head.mId + zip1,
								LauncherEnv.Path.MESSAGECENTER_PATH + head.mId + "zip1");
					} catch (Exception e) {
						e.printStackTrace();
					}
					file = new File(LauncherEnv.Path.MESSAGECENTER_PATH + head.mId + zip2);
					if (!file.exists()) {
						if (!mMsgHttp.downloadZipRes(head.mZicon2,
								LauncherEnv.Path.MESSAGECENTER_PATH, head.mId + zip2)) {
						}
					}
					try {
						HttpUtil.unZipFolder(LauncherEnv.Path.MESSAGECENTER_PATH + head.mId + zip2,
								LauncherEnv.Path.MESSAGECENTER_PATH + head.mId + "zip2");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				decodeBitmaps(head);
				Message msg = Message.obtain();
				msg.what = MSG_ZIP_DOWNLOAD_FINISHED;
				msg.obj = head;
				mHandler.sendMessage(msg);
			}
		}.start();

	}

	/**
	 * <br>
	 * 功能简述:用于处理消息列表的点击事件 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param bean
	 * @param fromWhere
	 *            相应那里的点击事件
	 */
	public void handleMsgClick(MessageHeadBean bean, int fromWhere) {
		if (bean == null) {
			return;
		}
		if (ConstValue.DEBUG) {
			LogUnit.diyInfo(ConstValue.MSG_TAG, "handleMsgClick，actiontype = " + bean.mActType
					+ ", actionvalue = " + bean.mActValue);
		}
		if (!bean.misReaded) {
		    //注释以下代码，区分优惠卷和促销卷2014-02-25
			/*if (bean.mType == MessageBaseBean.TYPE_COUPON
					|| bean.mType == MessageBaseBean.TYPE_PROMOTION) {*/
		    if (bean.mType == MessageBaseBean.TYPE_COUPON) {
				saveCoupon(bean.mCoupon);
			}
		    //促销券不需要保存到sdcard
//		    if (bean.mType == MessageBaseBean.TYPE_PROMOTION) {
//		        saveCoupon(bean.mPromotion);
//		    }
			markAsReaded(bean.mId);
			if ((bean.mViewType & MessageBaseBean.VIEWTYPE_DESK_TOP) != 0) {
				removeCoverFrameView();
			}
		}
		if (bean.mType == MessageBaseBean.TYPE_HTML
				|| bean.mActType == MessageWidgetBean.ACTTYPE_WEBVIEW) {
			showMessage(bean);
		} else if (bean.mActType == MessageWidgetBean.ACTTYPE_NEWACTION) {
			action(bean.mActValue);
		} else if (bean.mActType == MessageWidgetBean.ACTTYPE_LINK) {
			// 使用浏览器去url
			// AppUtils.gotoBrowser(mContext, bean.mActValue);
			// V4.15需求 URL地址有可能是302重定向类型，这种广告连接的跳转采取特殊方法
			if (GotoMarketIgnoreBrowserTask.isRedirectUrl(bean.mActValue)) {
				GotoMarketIgnoreBrowserTask.startExecuteTask(mContext, bean.mActValue);
			} else {
				GoAppUtils.gotoBrowserInRunTask(mContext, bean.mActValue);
//				gotoBrowser(bean.mActValue);
			}
//			gotoBrowser(bean.mActValue);
		} else if (bean.mActType == MessageWidgetBean.ACTTYPE_GOSTORE) {
			AppRecommendedStatisticsUtil.getInstance().saveCurrentEnter(mContext,
					AppRecommendedStatisticsUtil.ENTRY_TYPE_MESSAGE_CENTER);
			if (bean.mActValue == null) {
				return;
			}
			if (bean.mActValue.equals(ICustomAction.ACTION_FUNC_SPECIAL_APP_GOSTORE)) {
				Intent it = new Intent(bean.mActValue);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICommonMsgId.START_ACTIVITY, -1, it, null);
			} else {
				AppsDetail.gotoDetailDirectly(mContext, AppsDetail.START_TYPE_APPRECOMMENDED,
						bean.mActValue);
				// GoStoreOperatorUtil.gotoStoreDetailDirectly(mContext,
				// bean.mActValue);
			}
		} else if (bean.mActType == MessageWidgetBean.ACTTYPE_OPENGO) {
			if (bean.mActValue == null) {
				return;
			}
			Intent it = new Intent(bean.mActValue);
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICommonMsgId.START_ACTIVITY,
					-1, it, null);
		} else if (bean.mActType == MessageWidgetBean.ACTTYPE_OTHER) {
			if (bean.mActValue == null) {
				return;
			}
			if (bean.mActValue.startsWith(MARKET_PRE)) {
				if (GoAppUtils.isMarketExist(mContext)) {
					GoAppUtils.gotoMarket(
							mContext,
							MarketConstant.APP_DETAIL
									+ bean.mActValue.substring(MARKET_PRE.length()));
				} else {
					// AppUtils.gotoBrowser(mContext,
					// MARKET_URL +
					// bean.mActValue.substring(MARKET_PRE.length()));
					GoAppUtils.gotoBrowserInRunTask(mContext, MARKET_URL + bean.mActValue.substring(MARKET_PRE.length()));
//					gotoBrowser(MARKET_URL + bean.mActValue.substring(MARKET_PRE.length()));
				}
			}
		} else if (bean.mActType == MessageWidgetBean.ACTTYPE_DOWNLOAD) {
			if (bean.mActValue != null) {
				String[] urlContent = bean.mActValue.split(MessageBaseBean.URL_SPLIT);
				if (urlContent.length != 2) {
					return;
				}
				String[] nameContent = urlContent[1].split(MessageBaseBean.URL_SPLIT_NAME);
				if (nameContent.length != 2) {
					return;
				}
				final String pkgName = nameContent[0];
				final String appName = nameContent[1]; // 显示在通知栏上面的下载app name
//				GoStoreOperatorUtil.downloadFileDirectly(mContext, appName, bean.mActValue,
//						Long.valueOf(bean.mId), pkgName, null, 0, null);
				
				final String downUrl = bean.mActValue;
				final long taskId = Long.valueOf(bean.mId);
				// 国内直接ftp下载
				ServiceCallbackDownload.ServiceCallbackRunnable runnable = new ServiceCallbackDownload.ServiceCallbackRunnable() {
					@Override
					public void run() {
						downloadFileDirectly(mContext, appName,
								downUrl, taskId, pkgName, null,
								0, null);
					}
				};
				ServiceCallbackDownload.callbackDownload(ApplicationProxy.getContext(), runnable);
			}

		}

//		saveClickStatisticsData(bean.mId);
//		Vector<MessageHeadBean> statisticsMsgs = new Vector<MessageHeadBean>();
//		statisticsMsgs.add(bean);
//		updateStatisticsData(statisticsMsgs, fromWhere, 0,
//				IPreferencesIds.SHAREDPREFERENCES_MSG_CLICK_TIMES, null);
		mEntrance = fromWhere;
		
		String mapId = bean.mMapId == null ? "" : bean.mMapId;
		mMsgHttp.saveStatisticsDatas(null, bean.mId, mapId, "click", "1", mEntrance);
		updateStatisticsData(0);
		mMsgHttp.saveRecommandedApps(bean.mPackageName, bean.mMapId, bean.mId);

		if (bean.mActValue != null && bean.mActValue.startsWith(ConstValue.PREFIX_GUI)) {
			// GuiThemeStatistics.setCurrentEntry(GuiThemeStatistics.ENTRY_MESSAGE_PUSH,
			// mContext);
			String id = getId(bean.mActValue, ConstValue.PREFIX_GUI);
			if (id != null) {
				if (id.equals("1")) {
					GuiThemeStatistics.guiStaticData("", GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
							String.valueOf(GuiThemeStatistics.ENTRY_MESSAGE_CENTER), "", "", "");
				} else if (id.equals("2")) {
					GuiThemeStatistics.guiStaticData("", GuiThemeStatistics.OPTION_CODE_LOGIN, 1,
							String.valueOf(GuiThemeStatistics.ENTRY_MESSAGE_CENTER_LOCKER), "", "", "");
				}
			}
		}
	}
	/**
	 * <br>功能简述:解析actValue;
	 * <br>actValue的格式如下：
	 * <br>a) intent:classname=*****?arg1=I3#arg2=Stest#arg3=Ztrue#arg4=J8888#arg5=F1.0#arg5=D1.00
	 * <br>b) intent:action=*****?arg1=I3#arg2=Stest
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return 
	 * @throws Exception 
	 */
	private Intent parseActValue(String actValue) throws Exception {
		// TODO Auto-generated method stub
		String[] actValues = actValue.split("\\?", 2);
		String intentStr = actValues[0];
		intentStr = intentStr.replace("intent:", "");

		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String[] actionOrClass = intentStr.split("=", 2);
		if (actionOrClass.length > 1) {
			if (intentStr.startsWith("classname=")) {
				intent.setClassName(mContext, actionOrClass[1]);
				Log.i("zyz", "class:" + actionOrClass[1]);
			} else if (intentStr.startsWith("action=")) {
				intent.setAction(actionOrClass[1]);
				Log.i("zyz", "action:" + actionOrClass[1]);
			}
		} else {
			Log.e("zyz", "actionOrClass 不能为空");
			throw new Exception(
					"messageManager->parseActValue:actionOrClass 不能为空");
		}

		if (actValues.length > 1) {
			// 如果有参数
			String[] attributes = actValues[1].split("#");
			for (int i = 0; i < attributes.length; i++) {
				String[] atts = attributes[i].split("=", 2);
				if (atts.length != 2) {
					Log.e("zyz", "参数格式错误:=号后面没有数据");
					break;
				}
				String attName = atts[0];
				Log.i("zyz", "attName:" + attName);
				String attType = "";
				String attValue = "";
				attType = atts[1].substring(0, 1);
				Log.i("zyz", "attType:" + attType);
				if (atts[1].length() < 2) {
					// 等号后面只有1个字符，可能参数为空字符串
					// 当attType=S的时候attValue保持空字符串
					if (!attType.equals("S")) {
						// 因为参数格式错误，舍弃这次的参数
						Log.e("zyz", "参数值的格式错误,舍弃这次的参数");
						continue;
					}
				} else {
					attValue = atts[1].substring(1, atts[1].length());
				}
				Log.i("zyz", "attValue:" + attValue);
				buildInent(intent, attName, attType, attValue);
			}
		}

		return intent;
	}
	/**
	 * <br>功能简述:构建intent的参数
	 * <br>功能详细描述:
	 * <br>注意:attType的含义:
	 *  I表示int，
		S表示String,
		Z表示bool,
		J表示long
		F表示float
		D表示double
		B表示Byte
	 * @param intent
	 * @param attName 属性名
	 * @param attType 属性类型
	 * @param attValue 属性值
	 */
	private void buildInent(Intent intent, String attName, String attType, String attValue) {
		// TODO Auto-generated method stub
		if (attType.equals("I")) {
			intent.putExtra(attName, Integer.parseInt(attValue));
		} else if (attType.equals("S")) {
			intent.putExtra(attName, attValue);
		} else if (attType.equals("Z")) {
			intent.putExtra(attName, Boolean.parseBoolean(attValue));
		} else if (attType.equals("J")) {
			intent.putExtra(attName, Long.parseLong(attValue));
		} else if (attType.equals("F")) {
			intent.putExtra(attName, Float.parseFloat(attValue));
		} else if (attType.equals("D")) {
			intent.putExtra(attName, Double.parseDouble(attValue));
		} else if (attType.equals("B")) {
			intent.putExtra(attName, Byte.parseByte(attValue));
		}
		
	}

	/**
	 * <br>
	 * 功能简述:1.3版本后的跳转响应 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param actValue
	 */
	public void action(String actValue) {
		if (actValue == null) {
			return;
		}

		if (actValue.startsWith(ConstValue.PREFIX_GOSTOREDETAIL)) {
			String id = getId(actValue, ConstValue.PREFIX_GOSTOREDETAIL);
			AppsDetail.gotoDetailDirectly(mContext, AppsDetail.START_TYPE_APPRECOMMENDED,
					Integer.valueOf(id));
		} else if (actValue.startsWith(ConstValue.PREFIX_GOSTORETYPE)) {
			AppsManagementActivity.startAppCenter(mContext,
					MainViewGroup.ACCESS_FOR_APPCENTER_THEME, false);
		} else if (actValue.startsWith(ConstValue.PREFIX_GUI)) {
			String id = getId(actValue, ConstValue.PREFIX_GUI);
			if (id != null) {
				Intent mythemesIntent = new Intent();
				if (id.equals("1")) {
					mythemesIntent.putExtra("entrance", ThemeManageView.LAUNCHER_THEME_VIEW_ID);
				} else if (id.equals("2")) {
					mythemesIntent.putExtra("entrance", ThemeManageView.LOCKER_THEME_VIEW_ID);
				}
				mythemesIntent.setClass(mContext, ThemeManageActivity.class);
				mythemesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(mythemesIntent);
			}
		} else if (actValue.startsWith(ConstValue.PREFIX_GUIDETAIL)) {

			Intent intent = new Intent(mContext, ThemeDetailActivity.class);
			intent.putExtra(ThemeConstants.DETAIL_MODEL_EXTRA_KEY,
					ThemeConstants.DETAIL_MODEL_FEATURED_EXTRA_VALUE);
			intent.putExtra(ThemeConstants.PACKAGE_NAME_EXTRA_KEY, "");
			intent.putExtra(ThemeConstants.DETAIL_ID_EXTRA_KEY,
					Integer.valueOf(getId(actValue, ConstValue.PREFIX_GUIDETAIL)));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		} else if (actValue.startsWith(ConstValue.PREFIX_GUISPEC)) {
			String id = getId(actValue, ConstValue.PREFIX_GUISPEC);
			try {
				GoAppUtils.goToThemeSpec((Activity) mContext, Integer.valueOf(id), true);
//				Intent intent = new Intent();
//				intent.putExtra("ty", Integer.valueOf(id));
//				intent.putExtra("entrance", true);
//				intent.setClass(mContext, BannerDetailActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (actValue.startsWith(ConstValue.PREFIX_HTTP)) {
			// V4.15需求 URL地址有可能是302重定向类型，这种广告连接的跳转采取特殊方法
			if (GotoMarketIgnoreBrowserTask.isRedirectUrl(actValue)) {
				GotoMarketIgnoreBrowserTask.startExecuteTask(mContext, actValue);
			} else {
				GoAppUtils.gotoBrowserInRunTask(mContext, actValue);
//				gotoBrowser(actValue);
			}
		} else if (actValue.startsWith(ConstValue.PREFIX_MARKET)) {
			String id = getId(actValue, ConstValue.PREFIX_MARKET);
			if (GoAppUtils.isMarketExist(mContext)) {
				GoAppUtils.gotoMarket(mContext, MarketConstant.APP_DETAIL + id);
			} else {
				GoAppUtils.gotoBrowserInRunTask(mContext, MarketConstant.BROWSER_APP_DETAIL + id);
//				gotoBrowser(MarketConstant.BROWSER_APP_DETAIL + id);
			}
		} else if (actValue.startsWith(MarketConstant.APP_DETAIL)) {
			String id = getId(actValue, MarketConstant.APP_DETAIL);
			if (GoAppUtils.isMarketExist(mContext)) {
				GoAppUtils.gotoMarket(mContext, MarketConstant.APP_DETAIL + id);
			} else {
				GoAppUtils.gotoBrowserInRunTask(mContext, MarketConstant.BROWSER_APP_DETAIL + id);
//				gotoBrowser(MarketConstant.BROWSER_APP_DETAIL + id);
			}
		} else if (actValue.startsWith(ConstValue.PREFIX_APPCENTERTYPE)) { // 应用中心分类
			String id = getId(actValue, ConstValue.PREFIX_APPCENTERTYPE);
			try {
				int typeId = Integer.parseInt(id);
				AppsManagementActivity.startAppCenter(mContext, typeId, false,
						RealTimeStatisticsContants.AppgameEntrance.MESSAGE_CENTER);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (actValue.startsWith(ConstValue.PREFIX_APPCENTERTOPIC)) { // 应用中心专题
			String id = getId(actValue, ConstValue.PREFIX_APPCENTERTOPIC);
			try {
				int topicId = Integer.parseInt(id);
				AppsManagementActivity.startTopic(mContext, topicId, true,
						RealTimeStatisticsContants.AppgameEntrance.MESSAGE_CENTER);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (actValue.startsWith(ConstValue.PREFIX_APPCENTERDETAIL)) { // 应用中心详情
			String id = getId(actValue, ConstValue.PREFIX_APPCENTERDETAIL);
			try {
				int detailId = Integer.parseInt(id);
				AppsDetail.gotoDetailDirectly(mContext, AppsDetail.START_TYPE_APPRECOMMENDED,
						detailId, RealTimeStatisticsContants.AppgameEntrance.MESSAGE_CENTER);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (actValue.startsWith(ConstValue.PREFIX_INTENT)) {
			// TODO 解析mActalue
//			Log.i("zyz", "actValue:" + actValue);
			try {
				Intent intent = parseActValue(actValue);
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
						ICommonMsgId.START_ACTIVITY, -1, intent, null);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("zyz", "打开intent失败");
				e.printStackTrace();
			}
			// mContext.startActivity(intent);
		} else {
			Log.e("MessageManager", "actValue没有被适配:" + actValue);
		}

	}

	/**
	 * <br>
	 * 功能简述:获得actvalue中的参数id <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param actValue
	 * @param prex
	 * @return
	 */
	private String getId(String actValue, String prex) {
		String id = null;
		if (actValue != null && prex != null && actValue.length() > prex.length()) {
			id = actValue.substring(prex.length());
		}
		return id;
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_ZIP_DOWNLOAD_FINISHED :
					if (msg.obj != null && msg.obj instanceof MessageHeadBean) {
						addViewToCoverFrame((MessageHeadBean) msg.obj);
					}
					break;
				case MSG_PREPARE_ICONVIEW :
					mMsgHeadBean.downloadDrawable(mMsgHeadBean);
					break;
				case MSG_REFRASH_ICONVIEW :
					if (!isInMessageCenter()) {
						showMsgNotify(mMsgHeadBean);
					}
					break;
				case MSG_REUPDATE_STATISTICSDATA :
					updateStatisticsData(System.currentTimeMillis());
					break;
				case MSG_REUPDATE_THEME_STATISTICSDATA :
					if (msg.obj != null) {
						updateThemeNotifyStatisticsData(msg.arg1, (Long) msg.obj,
								ConvertUtils.int2boolean(msg.arg2));
					}
					break;
				case MSG_REUPDATE_ERRORSTATISTICSDATA :
					if (msg.obj != null && msg.getData() != null) {
						Bundle data = msg.getData();
						long uuid = data.getLong("uuid");
						int errorType = data.getInt("errortype");
						int errorReason = data.getInt("errorreason");
						updateErrorStatisticsData((Vector<MessageHeadBean>) msg.obj, errorType,
								errorReason, uuid);
					}
					break;
				default :
					break;
			}
		}

	};

	private void decodeBitmaps(MessageHeadBean bean) {
		File dir = new File(LauncherEnv.Path.MESSAGECENTER_PATH + bean.mId + "zip1");
		if (dir.exists() && dir.isDirectory()) {
			if (mEnterBmpList == null) {
				mEnterBmpList = new ArrayList<Bitmap>();
			}
			mEnterBmpList.clear();
			String[] files = dir.list();
			HttpUtil.sortFile(files);
			for (int i = 0; files != null && i < files.length; i++) {
				Bitmap orginalBmp = BitmapFactory.decodeFile(dir.getPath() + File.separator
						+ files[i]);
				Bitmap bmp = bitmpaScale(orginalBmp);
				if (bmp != null) {
					mEnterBmpList.add(bmp);
				}
			}
		}
		dir = new File(LauncherEnv.Path.MESSAGECENTER_PATH + bean.mId + "zip2");
		if (dir.exists() && dir.isDirectory()) {
			if (mActingBmpList == null) {
				mActingBmpList = new ArrayList<Bitmap>();
			}
			mActingBmpList.clear();
			String[] files = dir.list();
			HttpUtil.sortFile(files);
			for (int i = 0; files != null && i < files.length; i++) {
				Bitmap orginalBmp = BitmapFactory.decodeFile(dir.getPath() + File.separator
						+ files[i]);
				Bitmap bmp = bitmpaScale(orginalBmp);
				if (bmp != null) {
					mActingBmpList.add(bmp);
				}
			}
		}
	}

	private Bitmap bitmpaScale(Bitmap bmp) {
		if (bmp == null) {
			return null;
		}
		Bitmap scaleBitmap = null;
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		if (bmpWidth > 0 && bmpHeight > 0) {
			float scale = 1.0f;
			int width = GoLauncherActivityProxy.getScreenWidth();
			int height = GoLauncherActivityProxy.getScreenHeight();
			scale = width > height ? (height + 0.1f) / 480 : (width + 0.1f) / 480;
			int dstWidth = (int) (bmpWidth * scale);
			int dstHeight = (int) (bmpHeight * scale);
			try {
				scaleBitmap = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, false);
			} catch (Exception e) {
				scaleBitmap = null;
				e.printStackTrace();
			}
		}
		// if (bmp != null && !bmp.isRecycled()) {
		// bmp.recycle();
		// bmp = null;
		// }
		return scaleBitmap;
	}

	private void recyleZicons() {
		if (mEnterBmpList != null && mEnterBmpList.size() != 0) {
			for (int i = 0; i < mEnterBmpList.size(); i++) {
				Bitmap bmp = mEnterBmpList.get(i);
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
				}
			}
			mEnterBmpList.clear();
			mEnterBmpList = null;
		}
		if (mActingBmpList != null && mActingBmpList.size() != 0) {
			for (int i = 0; i < mActingBmpList.size(); i++) {
				Bitmap bmp = mActingBmpList.get(i);
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
				}
			}
			mActingBmpList.clear();
			mActingBmpList = null;
		}
	}

	@Override
	public void onDownLoadFinsish() {
		mHandler.sendEmptyMessage(MSG_REFRASH_ICONVIEW);
	}

	/**
	 * <br>
	 * 功能简述:移除罩子层视图 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void removeCoverFrameView() {
		MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME, ICoverFrameMsgId.COVER_FRAME_REMOVE_VIEW,
						CoverFrame.COVER_VIEW_MESSAGECENTER, null, null);
	}

	public void clickCloseCoverFrameView(MessageHeadBean bean) {
		removeCoverFrameView();
		markAsClickClosed(bean);
		String mapId = bean.mMapId == null ? "" : bean.mMapId;
		mMsgHttp.saveStatisticsDatas(null, bean.mId, mapId, "close", "1", MessageBaseBean.VIEWTYPE_DESK_TOP);
		updateStatisticsData(0);
	}

	/**
	 * <br>
	 * 功能简述:MessageInfo 转为MessageHeadBean <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param info
	 * @return
	 */
	private MessageHeadBean convertInfo2Bean(MessageInfo info) {
		MessageHeadBean bean = new MessageHeadBean();
		bean.mId = info.mId;
		bean.misReaded = info.misReaded;
		bean.mMsgTimeStamp = info.mTimeStamp;
		bean.mType = info.mType;
		bean.mTitle = info.mTitle;
		bean.mViewType = info.mViewType;
		bean.mUrl = info.mUrl;
		bean.mActType = info.mActtype;
		bean.mActValue = info.mActValue;
		bean.mEndTime = info.mEndTime;
		bean.mIcon = info.mIconUrl;
		bean.mSummery = info.mIntro;
		bean.mStartTime = info.mStartTime;
		bean.mZicon1 = info.mZIcon1;
		bean.mZicon2 = info.mZIcon2;
		bean.mZpos = info.mZpos;
		bean.mZtime = info.mZtime;
		bean.mIsColsed = info.mIsClosed;
		bean.mFilterPkgs = info.mFilterPkgs;
		bean.mClickClosed = info.mClickClosed;
		bean.mDynamic = info.mDynamic;
		bean.mIconpos = info.mIconpos;
		bean.mFullScreenIcon = info.mFullScreenIcon;
		bean.mIsRemoved = info.mIsRemoved;
		bean.mWhiteList = info.mWhiteList;
		bean.mIsWifi = info.mIsWifi;
		bean.mMsgOutDateTime = info.mMsgOutDateTime;
		bean.mIsDeleted = info.mIsDeleted;
		bean.mCoupon = info.mCoupon;
		bean.mPromotion = info.mPromotion;
		bean.mInstruct = info.mInstruct;
		bean.mPackageName = info.mPackageName;
		bean.mMapId = info.mMapId;
		bean.mAddact = info.mAddact;
		bean.mIsadv = info.mIsadv;
		bean.mMsgimgurl = info.mMsgimgurl;
		return bean;
	}

	public void saveStatisticsDatas(ArrayList<MessageStatisticsBean> statisticsBeans, String msgId, 
			String mapId, String oprCode, String oprResult, int entrance) {
		mMsgHttp.saveStatisticsDatas(statisticsBeans, msgId, mapId, oprCode, oprResult, entrance);
	}
	
	/**
	 * 判断已安装的第三方应用是否由消息中心推送的
	 * @param packageName
	 * @return
	 */
	public void messageCenterRecommandAppUpdateStatistics(String packageName) {
		boolean isRecommanded =  mMsgHttp.isRecommandedApp(packageName);
		if (ConstValue.DEBUG) {
			Log.e(ConstValue.MSG_TAG, "isMessageCenterRecommandApp packageName =" + packageName + ", isRecommanded = " + isRecommanded);
		}
		if (isRecommanded) {
			String msgId = mMsgHttp.getMsgId();
			String mapId = mMsgHttp.getMapId();
			mMsgHttp.saveStatisticsDatas(null, msgId, mapId, "b000", "1", mEntrance);
			updateStatisticsData(0);
		}
	}
	
	/**
	 * <br>
	 * 功能简述: <br>
	 * 功能详细描述: <br>
	 * 注意:uuid 调用时为0，只有失败后自动再次上传时会是上次的值
	 * 
	 * @param bean
	 * @param entrance
	 * @param uuid
	 *            传送失败后再次调用的id，第一次调用为0
	 */
	public void updateStatisticsData(final long uuid) {

		new Thread() {
			@Override
			public void run() {
				super.run();
				if (!mMsgHttp.updateStatisticsData() && uuid == 0) {
					Message msg = Message.obtain();
					msg.what = MSG_REUPDATE_STATISTICSDATA;
					mHandler.sendMessageDelayed(msg, 6000);
				}
			}
		}.start();
	}

	public void updateErrorStatisticsData(final Vector<MessageHeadBean> beans, final int errorType,
			final int errorReason, final long uuid) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				if (beans != null && beans.size() > 0) {
					long id = uuid;
					if (uuid == 0) {
						id = System.currentTimeMillis();
					}
					if (!mMsgHttp.updateErrorStatisticsData(beans, errorType, errorReason, id)
							&& uuid == 0) {
						Message msg = Message.obtain();
						msg.what = MSG_REUPDATE_ERRORSTATISTICSDATA;
						msg.obj = beans;
						Bundle bundle = new Bundle();
						bundle.putLong("uuid", id);
						bundle.putInt("errortype", errorType);
						bundle.putInt("errorreason", errorReason);
						msg.setData(bundle);
						mHandler.sendMessageDelayed(msg, 6000);
					}
				}
			}
		}.start();
	}

	/**
	 * <br>
	 * 功能简述: <br>
	 * 功能详细描述: <br>
	 * 注意:uuid 调用时为0，只有失败后自动再次上传时会是上次的值
	 * 
	 * @param bean
	 * @param entrance
	 * @param uuid
	 *            传送失败后再次调用的id，第一次调用为0
	 * @param isShow
	 *            是否是显示统计数据
	 */
	public void updateThemeNotifyStatisticsData(final int type, final long uuid,
			final boolean isShow) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				long id = uuid;
				if (uuid == 0) {
					id = System.currentTimeMillis();
				}
				if (!mMsgHttp.updateThemeNotifyStatisticsData(type, id, isShow) && uuid == 0) {
					Message msg = Message.obtain();
					msg.what = MSG_REUPDATE_THEME_STATISTICSDATA;
					msg.arg1 = type;
					msg.obj = id;
					msg.arg2 = ConvertUtils.boolean2int(isShow);
					mHandler.sendMessageDelayed(msg, 6000);
				}
			}
		}.start();
	}

	/**
	 * 将优惠劵保存到sd卡
	 * 
	 * @param content
	 */
	public boolean saveCoupon(String content) {
		boolean success = false;
		if (content != null && FileUtil.isSDCardAvaiable()) {
			String[] contents = content.split("#");
			if (contents != null) {
				try {
					int type = Integer.valueOf(contents[0]);
					String code = contents[1];
					if (!FileUtil.isFileExist(LauncherEnv.Path.COUPON_PATH + code)) {
						if (type == MessageBaseBean.TYPE_COUPON && contents.length == 5) {
							JSONObject obj = new JSONObject();
							String value = contents[2];
							String stime = contents[3];
							String etime = contents[4];
							obj.put(CouponBean.TAG_CODE, code);
							obj.put(CouponBean.TAG_FACEVALE, value);
							obj.put(CouponBean.TAG_TYPE, BaseBean.TYPE_COUPON);
							obj.put(CouponBean.TAG_STIME, stime);
							obj.put(CouponBean.TAG_ETIME, etime);
							mCacheManager.saveCache(code, obj.toString().getBytes());
						} else if (type == MessageBaseBean.TYPE_PROMOTION && contents.length == 4) {
							JSONObject obj = new JSONObject();
							String stime = contents[2];
							String etime = contents[3];
							obj.put(PromotionBean.TAG_CODE, code);
							obj.put(PromotionBean.TAG_STIME, stime);
							obj.put(PromotionBean.TAG_ETIME, etime);
							obj.put(CouponBean.TAG_TYPE, BaseBean.TYPE_PROMOTION);
							obj.put(CouponBean.TAG_USED, false);
							mCacheManager.saveCache(code, obj.toString().getBytes());
						}
						success = true;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		broadCast(MessageCenterActivity.GET_MSG_COUPON_WRITETOSD,
				ConvertUtils.boolean2int(success), 0, null);
		return success;
	}

	/**
	 * 返回功能预告的url
	 * @return url
	 */
	public String getFuncForeUrl() {
		return mMsgHttp.postGetFuncForeUrlRequest();
	}

	@Override
	public void couponParseFinish(boolean bool, int status, CouponBean coupon) {
		if (!bool) {
			broadCast(MessageCenterActivity.GET_MSG_CONTENT_FAILED, -1, null, null);
		} else {
			if (coupon != null) {
				StringBuffer builder = new StringBuffer();
				builder.append(MessageBaseBean.TYPE_COUPON).append("#").append(coupon.getCode()).append("#")
						.append(coupon.getFaceValue()).append("#").append(coupon.getSTime()).append("#")
						.append(coupon.getETime());
				saveCoupon(builder.toString());
			}
			broadCast(MessageCenterActivity.GET_MSG_COUPON_FINISH, status, coupon, null);
		}
	}
	/**
	 * 清除通知栏消息
	 */
	public void cleanNotification() {
		// TODO Auto-generated method stub
		if (mMsgListBean != null) {
			Vector<MessageHeadBean> mMessageHeads = mMsgListBean
					.getAllMessagHead();
			for (MessageHeadBean bean : mMessageHeads) {
				if (!bean.misReaded
						&& (bean.mViewType & MessageBaseBean.VIEWTYPE_STATUS_BAR) != 0) {
					NotificationManager nm = (NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
					nm.cancel(bean.mId,
							INotificationId.MESSAGECENTER_NEW_MESSAGE);
				}

			}
		}

	}

}