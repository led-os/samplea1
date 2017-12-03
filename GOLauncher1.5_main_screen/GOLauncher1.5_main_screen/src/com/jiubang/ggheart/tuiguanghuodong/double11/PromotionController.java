package com.jiubang.ggheart.tuiguanghuodong.double11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;

import com.go.util.ServerUtils;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 推广活动控制器。
 * 
 * @author chenshihang
 * 
 */
public class PromotionController {

	public static final boolean DEBUG = false;

	public static final String TAG = "PromotionController";

	public static String sXmlUrl = LauncherEnv.Server.PROMOTION_XML_URL_PRO; //正式服务的地址
	public static final String XML_FILE_NAME = "promotion_config.xml";
	public static final String PROMOTION_FILE_PATH = LauncherEnv.Path.SDCARD
			+ LauncherEnv.Path.LAUNCHER_DIR + "/promotion";

//	public static final long INTERVAL = 24 * 60 * 60 * 1000; // 定期检查更新的间隔
//	public static final long MIN_INTERVAL = 10 * 60 * 1000; // 检查更新的最小时间间隔
//	public static final long FIRST_RUN_CHECK_DELAY = 20 * 1000; // 新装或升级，第一次检查延时
	
	private long mSafeInterval = 24 * 60 * 60 * 1000; // 定期检查更新的间隔
	private long mMinInterval  = 60 * 60 * 1000; // 检查更新的最小时间间隔
	private long mFirstRunCheckDelay  = 20 * 1000; // 新装或升级，第一次检查延时

	private Context mContext;
	private static PromotionController sInstance = null;
	
	static {
		if (ServerUtils.isUseTestServer(LauncherEnv.Server.PROMOTION_XML_CONFIG_USE_TEST_SERVER)) {
			sXmlUrl = LauncherEnv.Server.PROMOTION_XML_URL_SIT;
		}
	}

	public static PromotionController getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new PromotionController(context);
		}
		return sInstance;
	}

	public PromotionController(Context context) {
		this.mContext = context;
	}

	private boolean mHasDoFirstCheckFlag = false; // 是否已经执行第一次启动时的检查（此检查有延时）
	private boolean mCanDoCheckFlag = false; // 是否可以进行常规检查，即每次返回桌面时的检查

	public void checkConfig() {
		if (!mHasDoFirstCheckFlag) {
			mHasDoFirstCheckFlag = true;
			TimerTask timerTask = new TimerTask() {

				@Override
				public void run() {
					doCheckConfig();
					mCanDoCheckFlag = true;
				}
			};
			Timer timer = new Timer();
			timer.schedule(timerTask, mFirstRunCheckDelay);
		}
		if (mCanDoCheckFlag) {
			doCheckConfig();
		}
	}

	private void doCheckConfig() {
		if (GoStorePhoneStateUtil.isNetWorkAvailable(mContext)) {
			// 检查推广时间，拉取文件时间条件
//			if (donotCheckTooFrequent() && checkPullConfigTime()) {
//				downloadPromotionConfigFile();
//			}
		}
	}

	/**
	 * ftp服务有缓存机制，测试时在url后拼接任意字符串可即使取得最新文件
	 * 
	 * @return
	 */
	private String getXMLUrl() {
		if (DEBUG) {
			return sXmlUrl + "?" + System.currentTimeMillis();
		} else {
			return sXmlUrl;
		}
	}

	/**
	 * 下载配置文件
	 */
	private synchronized void downloadPromotionConfigFile() {
		if (DEBUG) {
			Log.i(TAG, "begin to download xml");
		}
		getRemoteText();
	}

	public synchronized void getRemoteText() {
		new Thread() {
			@Override
			public void run() {
				HttpURLConnection mUrlConn = null;
				InputStream input = null;
				try {
					URL url = new URL(getXMLUrl());
					mUrlConn = (HttpURLConnection) url.openConnection();
					mUrlConn.setReadTimeout(30000);
					mUrlConn.setConnectTimeout(30000);
					input = mUrlConn.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
			//		saveNewConfigString(sb.toString());
					onDownloadPromotionConfigFileFinish();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						input.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (mUrlConn != null) {
						mUrlConn.disconnect();
					}
				}
			}
		}.start();
	}

//	private void saveNewConfigString(String configString) {
//		PreferencesManager pm = getSP();
////		pm.putString(IPreferencesIds.PREFERENCE_1111_PROMOTION_CONFIG_STRING,
////				configString);
//		pm.commit();
//	}
//
//	private String getConfigString() {
//		PreferencesManager pm = getSP();
////		return pm.getString(
////				IPreferencesIds.PREFERENCE_1111_PROMOTION_CONFIG_STRING, "");
//	}

	/**
	 * 下载完配置文件回调
	 */
	public void onDownloadPromotionConfigFileFinish() {
//		saveLastCheckTime(System.currentTimeMillis());
//		// 解析文件，保存版本，
//		BasicConfigBean basicConfigBean = (BasicConfigBean) parseBean(new BasicConfigParser());
//		if (DEBUG) {
//			Log.i(TAG, (basicConfigBean != null) ? basicConfigBean.toString()
//					: "parse basic info null");
//		}
//
//		if (basicConfigBean != null
//				&& (getLastConfigVersion() == -1 || getLastConfigVersion() < basicConfigBean.version)) {
//			// 下载了新版本的配置文件
//			// 发送广播
//			if (DEBUG) {
//				Log.i(TAG, "send broadcoast");
//			}
//			
///*			UninstallLauncherConfigBean configBean = (UninstallLauncherConfigBean) parseBean(new UninstallLauncherConfigParser());
//			if (configBean != null) {
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
//						IDiyMsgIds.UNSTALL_LAUNCHER_SHOW_ADVERT_SETTING, -1, configBean, null);
//			}*/
//			
//			mContext.sendBroadcast(new Intent(
//					ICustomAction.PROMOTION_FILE_UPDATED));
//			
//			mSafeInterval = basicConfigBean.safeInterval;
//			mMinInterval = basicConfigBean.minInterval;
//
//			saveLastPullConfigTime(System.currentTimeMillis());
//
//
//			if (basicConfigBean.nextCheckTime.equals("-1")) {
//				saveNextPullConfigWaitingTime(-1);
//			} else {
//				long waitingTime = convertDateString2Long(basicConfigBean.nextCheckTime)
//						- System.currentTimeMillis();
//				saveNextPullConfigWaitingTime(waitingTime > 0 ? waitingTime : 0);
//			}
//			saveLastConfigVersion(basicConfigBean.version);
//		}
	}

//	// 限制不能太频繁发送请求，间隔时间有默认值，可服务器下发
//	private boolean donotCheckTooFrequent() {
//
////		if (DEBUG) {
////			return true;
////		}
//
//		long lastCheckTime = getLastCheckTime();
//		long current = System.currentTimeMillis();
////		saveLastCheckTime(current);
//		return current - lastCheckTime >= mMinInterval;
//
////		long lastCheckTime = getLastPullConfigTime();
////		long current = System.currentTimeMillis();
//////		saveLastCheckTime(current);
////		return current - lastCheckTime >= mMinInterval;
//	}
//
//	/**
//	 * 检查拉取配置文件的时机是否满足，依据配置中设定的下一次拉取时间及一些限制和保护措施
//	 * 
//	 * @return
//	 */
//	private boolean checkPullConfigTime() {
//		long current = System.currentTimeMillis();
//		if (getNextPullConfigWaitingTime() == -1) {
//			return false;
//		}
//		if (getLastPullConfigTime() == -1) {
//			return true;
//		}
//
//		boolean result = getLastPullConfigTime()
//				+ getNextPullConfigWaitingTime() <= current;
//
//		// 加保险，当超过24小时未更新时标记为需要更新
//		result = result || current - getLastPullConfigTime() > mSafeInterval;
//		return result;
//	}

	/**
	 * 上一个配置文件的版本，没有获取过文件时为-1
	 * 
	 * @return
	 */
//	private int getLastConfigVersion() {
//		PreferencesManager sp = getSP();
//		return sp.getInt(
//				IPreferencesIds.PREFERENCE_1111_PROMOTION_LAST_CONFIG_VERSION,
//				-1);
//	}
//
//	private void saveLastConfigVersion(int newVersion) {
//		PreferencesManager sp = getSP();
//		sp.putInt(
//				IPreferencesIds.PREFERENCE_1111_PROMOTION_LAST_CONFIG_VERSION,
//				newVersion);
//		sp.commit();
//	}

	/**
	 * 获取上一次拉取配置文件的时间
	 * 
	 * @return
	 */
//	private long getLastPullConfigTime() {
//		PreferencesManager sp = getSP();
//		return sp
//				.getLong(
//						IPreferencesIds.PREFERENCE_1111_PROMOTION_LAST_PULL_CONFIG_TIME,
//						-1);
//	}
//
//	private void saveLastPullConfigTime(long time) {
//		PreferencesManager sp = getSP();
//		sp.putLong(
//				IPreferencesIds.PREFERENCE_1111_PROMOTION_LAST_PULL_CONFIG_TIME,
//				time);
//		sp.commit();
//	}
//
//	private long getLastCheckTime() {
//		PreferencesManager sp = getSP();
//		return sp.getLong(
//				IPreferencesIds.PREFERENCE_1111_PROMOTION_LAST_CHECK_TIME, 0);
//	}
//
//	private void saveLastCheckTime(long time) {
//		PreferencesManager sp = getSP();
//		sp.putLong(IPreferencesIds.PREFERENCE_1111_PROMOTION_LAST_CHECK_TIME,
//				time);
//		sp.commit();
//	}

	/**
	 * 下一次获取文件的间隔时间。默认为0，永久为-1
	 * 
	 * @return
	 */
//	private long getNextPullConfigWaitingTime() {
//		PreferencesManager sp = getSP();
//		return sp
//				.getLong(
//						IPreferencesIds.PREFERENCE_1111_PROMOTION_NEXT_PULL_CONFIG_WAITING_TIME,
//						0);
//	}
//
//	private void saveNextPullConfigWaitingTime(long time) {
//		PreferencesManager sp = getSP();
//		sp.putLong(
//				IPreferencesIds.PREFERENCE_1111_PROMOTION_NEXT_PULL_CONFIG_WAITING_TIME,
//				time);
//		sp.commit();
//	}
//
//	/**
//	 * 通过解析器得到bean
//	 * 
//	 * @param promotionBaseParser
//	 * @return 配置文件配置的bean内容，无配置文件时返回null
//	 */
//	public PromotionBaseBean parseBean(PromotionBaseParser promotionBaseParser) {
//		PromotionBaseBean bean = null;
//		XmlPullParser xpp = null;
//		String configString = getConfigString();
//
//		if (!"".equals(configString)) {
//			try {
//				XmlPullParserFactory factory = XmlPullParserFactory
//						.newInstance();
//				factory.setNamespaceAware(true);
//				xpp = factory.newPullParser();
//				xpp.setInput(new StringReader(configString.toString()));
//				int eventType = xpp.getEventType();
//				while (eventType != XmlPullParser.END_DOCUMENT) {
//					if (eventType == XmlPullParser.START_TAG) {
//						String tagName = xpp.getName();
//						if (xpp.getDepth() == 2
//								&& tagName.equals(promotionBaseParser
//										.getModuleTopNodeName())) {
//							bean = promotionBaseParser.onParse(xpp);
//						}
//					}
//					eventType = xpp.next();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (null != xpp) {
//					xpp = null;
//				}
//			}
//		}
//		return bean;
//	}
//
//	private PreferencesManager getSP() {
//		return new PreferencesManager(mContext,
//				IPreferencesIds.TAOBAO_1111_PROMOTION, Context.MODE_PRIVATE);
//	}

	public static long convertDateString2Long(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(dateString);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
