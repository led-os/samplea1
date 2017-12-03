package com.jiubang.ggheart.iconconfig;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.go.proxy.MsgMgrProxy;
import com.go.util.StringUtil;
import com.go.util.log.TimeUnit;
import com.golauncher.message.ICommonMsgId;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.plugin.notification.NotificationType;


/**
 * 应用图标配置管理类
 * @author caoyaming
 *
 */
public class AppIconConfigController {
	//通知刷新应用图标上的数字
	private static final int HANDLER_WHAT_REFRESH_APP_ICON_VIEW = 0x1001;
	//启动桌面后,第一次请求数据延时时间
	private static final long FIRST_REQUEST_DELAY_TIME  = 5 * 1000;
	//请求数据的间隔时间
	private static final long REQUEST_DATA_INTERVAL_TIME = 8 * 60 * 60 * 1000; 
	//请求数据失败后距离下次请求的间隔时间---1小时
	private static final long REQUEST_DATA_FAILURE_INTERVAL_TIME = 1 * 60 * 60 * 1000;
	//刷新AppIconView信息的间隔时间---1小时
	private static final long REFRESH_VIEW_DATA_INTERVAL_TIME = 5 * 60 * 1000;
	//Context 
	private Context mContext;
	//当前类对象
	private static AppIconConfigController sInstance = null;
	//PrivatePreference
	private PrivatePreference mPreference;
	//是否已经执行第一次启动时的检查(主要作用是为了延时请求数据,缓解桌面启动时的压力)
	private boolean mHasDoFirstCheckFlag = false; 
	//当前应用版本名称
	private String mVersionName = "";
	
	private AppIconConfigController(Context context) {
		mContext = context;
		//获取PrivatePreference
		mPreference = PrivatePreference.getPreference(mContext);
		try {
			//获取当前应用信息
			PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			//获取当前应用版本名称
			mVersionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取单例对象
	 * @param context
	 * @return
	 */
	public static synchronized AppIconConfigController getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new AppIconConfigController(context);
		}
		return sInstance;
	}
	
	/**
	 * Handler
	 */
	private Handler mAppIconConfigHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			switch (message.what) {
			case HANDLER_WHAT_REFRESH_APP_ICON_VIEW:
				//修改应用图标上的数字
				if (message.obj != null && message.obj instanceof AppItemInfo) {
					AppItemInfo appItemInfo = (AppItemInfo) message.obj;
					appItemInfo.setNotificationType(NotificationType.NOTIFICATIONTYPE_MORE_APP);
					appItemInfo.setUnreadCount(message.arg1);
					MsgMgrProxy.sendBroadcast(AppIconConfigController.this, ICommonMsgId.NOTIFICATION_CHANGED, NotificationType.NOTIFICATIONTYPE_MORE_APP, message.arg1, null);
				}
				break;
			default:
				break;
			}
			return false;
		}
	});
	/**
	 * 请求图标配置数据
	 */
	public void requestIconConfigData() {
		if (!mHasDoFirstCheckFlag) {
			//第一次执行启动检查,创建定时任务.updateScreenIconConfigToView
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					//刷新应用图标View配置信息(目的:防止有些Icon配置信息过期了,但还在屏幕图片上显示)
					refreshAppIconConfigToView();
					//请求新配置数据
					requestIconConfigData();
				}
			};
			//延时指定时间,执行定时任务.
			Timer timer = new Timer();
			timer.schedule(timerTask, FIRST_REQUEST_DELAY_TIME);
			//表示为已执行了一次启动时的检查
			mHasDoFirstCheckFlag = true;
		} else {
			//=====开始检查数据=====
			if (isCanRequestData()) {
				//符合请求数据条件
				AppIconConfigDataProvider.getInstance(mContext).requestIconUnreadDigitalData(new AppIconConfigDataProvider.ReuqestDataListener() {
					@Override
					public void onFinish(List<AppIconConfigBean> iconConfigDataList) {
						//成功请求到数据
						//第一步:保存获取数据成功时的时间
						saveLastRequestDataSuccessTime();
						//如果返回的数据为空,则不执行下面的操作
						if (iconConfigDataList == null || iconConfigDataList.size() <= 0) {
							return;
						}
						//第二步:将数据保存到DB中
						AppIconConfigDataModel.getInstance(mContext).insertAppIconConfigData(iconConfigDataList);
						//第三步：将屏幕图标数字应用到图标View上
						refreshAppIconConfigToView();
					}
					@Override
					public void onException(String errorMessage) {
						//请求数据异常,保存失败时间
						saveLastReuqestDataFailureTime();
					}
				});
			}
			//判断是否达到刷新应用图标条件
			if (isCanRefreshView()) {
				//刷新应用图标View配置信息
				refreshAppIconConfigToView();
			}
		}
	}
	/**
	 * 将屏幕图标数字应用到图标View上
	 */
	private void refreshAppIconConfigToView() {
		//获取所有的应用集合
		ConcurrentHashMap<ComponentName, AppItemInfo> appInfoMap = AppDataEngine.getInstance(mContext).getAllAppHashMap();
		if (appInfoMap != null && appInfoMap.size() > 0) {
			//保存更新应用Icon配置数据到View时的时间
			saveLastRefreshViewDataTime();
			//从DB中查询出未读数字大于0的图标配置信息
			AppIconConfigBean whereAppIconConfigBean = new AppIconConfigBean();
			whereAppIconConfigBean.setmShowNumber(0);
			List<AppIconConfigBean> iconConfigBeanList = AppIconConfigDataModel.getInstance(mContext).queryAppIconConfigData(whereAppIconConfigBean);
			if (iconConfigBeanList == null || iconConfigBeanList.size() == 0) {
				return;
			}
			AppItemInfo appItemInfo = null;
			for (AppIconConfigBean iconConfigBean : iconConfigBeanList) {
				if (iconConfigBean.getmComponentNameToComponentName() == null) {
					continue;
				}
				//获取应用信息
				appItemInfo = appInfoMap.get(iconConfigBean.getmComponentNameToComponentName());
				if (appItemInfo != null && isIconConfigDataValid(iconConfigBean)) {
					//有效图标配置信息,更新应用图标上的数字
					Message message = new Message();
					message.what = HANDLER_WHAT_REFRESH_APP_ICON_VIEW;
					message.arg1 = iconConfigBean.getmShowNumber();
					message.obj = appItemInfo;
					mAppIconConfigHandler.sendMessage(message);
				} else if (appItemInfo != null) {
					//应用图标配置不可用,修改应用上图标数字为0
					Message message = new Message();
					message.what = HANDLER_WHAT_REFRESH_APP_ICON_VIEW;
					message.arg1 = 0;
					message.obj = appItemInfo;
					mAppIconConfigHandler.sendMessage(message);
				}
			}
		}
	}
	/**
	 * 是否达到刷新应用图标View条件
	 * @return true:可以刷新  false:不可以刷新
	 */
	private boolean isCanRefreshView() {
		if (System.currentTimeMillis() - getLastRefreshViewDataTime() < REFRESH_VIEW_DATA_INTERVAL_TIME) {
			return false;
		}
		return true;
	}
	/**
	 * 屏幕图标配置信息是否有效(是否在有效时间和有效桌面版本内)
	 * @param iconConfigBean 图标配置信息
	 * @return true:有效  false:无效
	 */
	private boolean isIconConfigDataValid(AppIconConfigBean iconConfigBean) {
		if (iconConfigBean == null || TextUtils.isEmpty(iconConfigBean.getmComponentName())) {
			return false;
		}
		//第一步:判断是否在有效时间内
		//获取当前日期
		long currentDate = StringUtil.toLong(TimeUnit.dateToStr(new Date(), "yyyyMMddHHmmss"), 0L);
		if (!(iconConfigBean.getmValidStartTime() <= currentDate && currentDate <= iconConfigBean.getmValidEndTime())) {
			//不在有效时间之内
			return false;
		}
		//第二步:判断是否为有效的桌面版本
		if (!isValidVersion(mVersionName, iconConfigBean.getmStartVersion(), iconConfigBean.getmEndVersion())) {
			//无效版本
			return false;
		}
		return true;
	}
	/**
	 * 判断当前版本名称是否在开始版本到结束版本之间
	 * @param cureentVersionName 当前版本名称
	 * @param startVersionName 开始版本名称
	 * @param endVersionName 结束版本名称
	 * @return true 可用  false:不可用
	 */
	private boolean isValidVersion(String cureentVersionName, String startVersionName, String endVersionName) {
		if (TextUtils.isEmpty(cureentVersionName)) {
			return false;
		}
		//判断当前桌面版本是否为Beta版本,获取beta在版本名称中的位置.
		int index = cureentVersionName.toLowerCase().indexOf("beta");
		if (index > 0) {
			//是Beta版本,从版本名称中去除Beta
			cureentVersionName = cureentVersionName.substring(0, index);
		} 
		if (!TextUtils.isEmpty(startVersionName) && startVersionName.compareTo(cureentVersionName) > 0) {
			//开始版本大于当前版本
			return false;
		} 
		if (!TextUtils.isEmpty(endVersionName) && endVersionName.compareTo(cureentVersionName) < 0) {
			//结束版本少于当前版本
			return false;
		}
		return true;
	}
	
	/**
	 * 是否可以请求数据
	 * @return true:可以请求数据   false:不能请求数据
	 */
	private boolean isCanRequestData() {
		//第一步:请求间隔是否达到要求
		//获取最后一次请求成功时的时间
		long lastRequestTime = getLastRequestDataSuccessTime();
		//判断是否达到间隔时间
		if (System.currentTimeMillis() - lastRequestTime < REQUEST_DATA_INTERVAL_TIME) {
			//没有达到间隔时间
			return false;
		}
		//第二步:请求数据失败间隔是否达到要求
		if (System.currentTimeMillis() - getLastRequestDataFailureTime() < REQUEST_DATA_FAILURE_INTERVAL_TIME) {
			//没有达到间隔时间
			return false;
		}
		return true;
	}
	/**
	 * 取消未读数字
	 * @param appItemInfo 应用信息对象
	 */
	public void cancelUnReadCount(final AppItemInfo appItemInfo) {
		if (appItemInfo == null || appItemInfo.mIntent == null || appItemInfo.mIntent.getComponent() == null || appItemInfo.getUnreadCount() <= 0) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				//获取ComponentName
				String componentName = appItemInfo.mIntent.getComponent().toString();
				//第一步:通过componentName查询出对应配置信息
				AppIconConfigBean whereAppIconConfigBean = new AppIconConfigBean();
				whereAppIconConfigBean.setmComponentName(componentName);
				List<AppIconConfigBean> iconConfigBeanList = AppIconConfigDataModel.getInstance(mContext).queryAppIconConfigData(whereAppIconConfigBean);
				if (iconConfigBeanList == null || iconConfigBeanList.size() == 0) {
					return;
				}
				//第二步:验证是否符合显示条件,如果不符合,则不需要修改.
				if (!isIconConfigDataValid(iconConfigBeanList.get(0))) {
					//配置无效,直接返回即可.
					return;
				}
				//第三步:修改DB中对应的配置数据
				AppIconConfigBean appIconConfigBean = new AppIconConfigBean();
				appIconConfigBean.setmComponentName(componentName);
				appIconConfigBean.setmShowNumber(0); //修改为0(表示已显示过了)
				AppIconConfigDataModel.getInstance(mContext).updateAppIconConfigData(appIconConfigBean);
				//第四步:更新应用图标上的数字为0
				Message message = new Message();
				message.what = HANDLER_WHAT_REFRESH_APP_ICON_VIEW;
				message.arg1 = 0;
				message.obj = appItemInfo;
				mAppIconConfigHandler.sendMessage(message);
			}
		}).start();
	}
	/**
	 * 获取最后一次请求数据成功时的时间(毫秒数)
	 * @return
	 */
	private long getLastRequestDataSuccessTime() {
		return mPreference.getLong(PrefConst.KEY_REQUEST_APP_ICON_CONFIG_DATA_SUCCESS_TIME, 0);
	}
	/**
	 * 保存最后一次请求数据成功时的时间
	 */
	private void saveLastRequestDataSuccessTime() {
		mPreference.putLong(PrefConst.KEY_REQUEST_APP_ICON_CONFIG_DATA_SUCCESS_TIME, System.currentTimeMillis());
		mPreference.commit();
	}
	/**
	 * 获取最后一次请求数据失败时的时间(毫秒数)
	 * @return
	 */
	private long getLastRequestDataFailureTime() {
		return mPreference.getLong(PrefConst.KEY_REQUEST_APP_ICON_CONFIG_DATA_FAILURE_TIME, 0);
	}
	/**
	 * 保存图标配置数据更新失败的时间----(防止请求服务端失败后,出现过于频繁请求服务器的情况)
	 */
	private void saveLastReuqestDataFailureTime() {
		//保存数据
		mPreference.putLong(PrefConst.KEY_REQUEST_APP_ICON_CONFIG_DATA_FAILURE_TIME, System.currentTimeMillis());
		mPreference.commit();
	}
	/**
	 * 获取最后一次刷新应用图标View数据的时间(毫秒数)
	 * @return
	 */
	private long getLastRefreshViewDataTime() {
		return mPreference.getLong(PrefConst.KEY_APP_ICON_CONFIG_REFRESH_VIEW_DATA_TIME, 0);
	}
	/**
	 * 保存最后一次刷新应用图标View数据的时间(毫秒数)
	 */
	private void saveLastRefreshViewDataTime() {
		//保存数据
		mPreference.putLong(PrefConst.KEY_APP_ICON_CONFIG_REFRESH_VIEW_DATA_TIME, System.currentTimeMillis());
		mPreference.commit();
	}
}	
