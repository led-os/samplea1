package com.jiubang.ggheart.apps.desks.purchase;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.encrypt.CryptTool;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.gau.utils.cache.utils.CacheUtil;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.util.AppUtils;
import com.go.util.BroadCaster;
import com.go.util.device.Machine;
import com.go.util.market.MarketConstant;
import com.golauncher.message.IAppManagerMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingAdvancedPayActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingSideDockActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.purchase.PromotionsControl.IActiveListener;
import com.jiubang.ggheart.billing.IPurchaseStateListener;
import com.jiubang.ggheart.billing.PurchaseStateManager;
import com.jiubang.ggheart.billing.ThemeAppInBillingManager;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.LauncherEnv;
/**
 * 
 * <br>类描述:功能收费管理类
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年9月13日]
 */
public class FunctionPurchaseManager extends BroadCaster implements IPurchaseStateListener {
	
	public static final String TAB_FUNCTION_HAS_PAY = "44";
	public static final String TAB_FUNCTION_NO_PAY = "45";

	public static final String UNPAY_TIP_LOCKAPP = "lock_app";
	public static final String UNPAY_TIP_GUESTURE = "guesture";
	public static final String UNPAY_TIP_LOCKHIDEAPP = "lockhideapp";
	public static final String UNPAY_TIP_BACKUP = "lockbackup";
	//注意下面定义的一些数组内容顺序是要一致的
	public static final int PURCHASE_ITEM_AD = 0;
	public static final int PURCHASE_ITEM_EFFECT = 1;
	public static final int PURCHASE_ITEM_SECURITY = 2;
	public static final int PURCHASE_ITEM_QUICK_ACTIONS = 3;
	public static final int PURCHASE_ITEM_FULL = 4;
	public static final int PURCHASE_ITEM_FILTER = 5;
	public static final int PURCHASE_ITEM_ACTIVE_CODE = -1;

	public static final int[] PURCHASE_ITEMS = { PURCHASE_ITEM_AD, PURCHASE_ITEM_EFFECT,
			PURCHASE_ITEM_SECURITY, PURCHASE_ITEM_QUICK_ACTIONS, PURCHASE_ITEM_FILTER };

	public static final String ACTION_PURCHASE_LIMIT_FREEM = "com.gau.jiubang.FUNCTION_PURCHASED_LIMIT_FREE";
	public static final String ACTION_PURCHASE_STATE_CHANAGED = "com.gau.jiubang.FUNCTION_PURCHASED";
	public static final String ACTION_TRIAL_EXPIRED = "com.gau.go.launcherex.EXPIRED";
	public static final String ACTION_START_TRAIL = "com.gau.go.launcherex.START_FUNCTION_TRAIL";
	public static final String ACTION_SHOW_PAYPAGE = "com.gau.go.launcherex.SHOW_PAYPAGE";

	public static final String FULL_PAY_BILLING_ID = "go_launcher_prime";

	public static final String REQUEST_PURCHASE_ACTION = "com.gau.go.launcherex.key.getjar.action.REQUESTPURCHASE";
	public static final String PURCHASE_TOKEN = "purchase_token";
	public static final String PURCHASE_PRICE = "purchase_price";
	public static final String PURCHASE_PUBLICKEY = "purchase_publickey";
	public static final String PURCHASE_PRODUCTID = "purchase_productid";
	public static final String PURCHASE_PRODUCTID_AD = "purchase_item_ad";
	public static final String PURCHASE_PRODUCTID_EFFECT = "purchase_item_effect";
	public static final String PURCHASE_PRODUCTID_SECURITY = "purchase_item_security";
	public static final String PURCHASE_PRODUCTID_ACTIONS = "purchase_item_quick_actions";
	public static final String PURCHASE_PRODUCTID_FILTER = "purchase_item_quick_filter";
	public static final String PURCHASE_RESULT = "purchase_result";
	public static final String ACTIVIE_SUCCESS = "code_active_success";

	public static final int[] PURCHASE_GETJAR_PRICE_RAITE = { 4, 4, 3, 4, 4 }; //价格倍数表，

	public static final String[] PURCHASE_GETJAR_PRODUCTID = { PURCHASE_PRODUCTID_AD,
			PURCHASE_PRODUCTID_EFFECT, PURCHASE_PRODUCTID_SECURITY, PURCHASE_PRODUCTID_ACTIONS,
			PURCHASE_PRODUCTID_FILTER };

	/**
	 * 付费功能当前状态，默认可见但需要付费才能用
	 */
	private static int sPAY_FUNCTION_STATE = 1;

	/**
	 * 付费功能状态-不可见不可用
	 */
	public static final int STATE_GONE = 0;

	/**
	 * 付费功能状态-可见但需要付费才能用
	 */
	public static final int STATE_VISABLE = 1;

	/**
	 * 付费功能状态-可见可用
	 */
	public static final int STATE_CAN_USE = 2;

	public static final int STATUS_TRAIL_NON = 1;
	public static final int STATUS_TRAIL_IN = 2;
	public static final int STATUS_TRAIL_EXPIRED = 3;

	
	public static final int CHECK_LIMIT_FREE_OK = 1; //限免有效
	public static final int CHECK_LIMIT_FREE_INVALID = 2; //限免无效
	
	private static FunctionPurchaseManager sInstance;
	private Context mContext;
	private CacheManager mCacheManager;
	private static final String FILENAME = "deskpayinfo";
	private static final String TAG_TRIAL_DATE = "trial_date";
	private String mPayInfoFileName;
	private long mTrialDate; // 过期日期
	private ArrayList<Integer> mPaidItems;
	private int[] mItemArrary = { PURCHASE_ITEM_AD, PURCHASE_ITEM_EFFECT, PURCHASE_ITEM_FILTER,
			PURCHASE_ITEM_SECURITY, PURCHASE_ITEM_QUICK_ACTIONS }; // 全额购买会在初始化时再次确认，不放在这里
	private AlarmManager mAlarmManager;
	private BroadcastReceiver mReceiver;
	private boolean mTrialing = false;

	private ThemeAppInBillingManager mInBillingManager;

	private String mTrialExpriedPreferenceId;
	private PendingIntent mAlarmIntent;

	private String mEntranceId;
	private String mTabId;
	private Object mMutex;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private PreferencesManager mPreference;
	private PromotionsControl mPromotionsControl;
	private FunctionPurchaseManager(Context context) {
		mContext = context;
		mPaidItems = new ArrayList<Integer>();
		mMutex = new Object();
		initReceiver();
		mCacheManager = new CacheManager(new FileCacheImpl(LauncherEnv.Path.SDCARD
				+ LauncherEnv.Path.LAUNCHER_DIR));
		mPayInfoFileName = CryptTool.encrypt(FILENAME, getAndroidId());
		initPayStatus();
		mTrialExpriedPreferenceId = CryptTool.encrypt(
				IPreferencesIds.PREFERENCE_PURCHASE_CFG_ITEM_TRIAL_DATE, getAndroidId());
	}

	public static synchronized FunctionPurchaseManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new FunctionPurchaseManager(context);
		}
		return sInstance;
	}

	/**
	 * <br>功能简述:初始化付费状态
	 * <br>功能详细描述:因为涉及到IO操作，把付费状态保存在内存，避免以后每次查询都做IO
	 * <br>注意:
	 */
	public void initPayStatus() {
		new Thread() {
			public void run() {
				synchronized (mMutex) {
					boolean hasPay = DeskSettingUtils.checkHadPay(mContext);
					if (hasPay) {
						mPaidItems.add(PURCHASE_ITEM_FULL);
					} else {
						checkSdCard();
						if (!queryItemPurchaseState(PURCHASE_ITEM_FULL)
								&& !queryItemPurchaseState(PURCHASE_ITEM_ACTIVE_CODE)) {
							checkPreferences();
						}
						if (AppUtils.isAppExist(mContext, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
							showTrialPage();
						}
					}
					String processName = AppUtils.getCurProcessName(mContext); // 只在主进程中同步设置
					if (processName != null && processName.equals(LauncherEnv.PROCESS_NAME)) {
						checkSetting();
					}
				}
			};
		}.start();
	}


	/**
	 * <br>功能简述:查看sd卡保存的记录
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void checkSdCard() {
		ArrayList<String> paiditems = null;
		JSONObject obj = getPurchaseCache(); // 查看sd卡记录
		if (obj != null) {
			paiditems = parsePaidInfo(obj);
		}

		if (paiditems != null) {
			for (String item : paiditems) {
				try {
					Integer itemCode = Integer.valueOf(item);
					mPaidItems.add(itemCode);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * <br>功能简述:查看保存在preference的记录
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void checkPreferences() {
		if (AppUtils.isAppExist(mContext, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
			if (PurchaseStateManager.query(mContext, FULL_PAY_BILLING_ID) != null) {
				mPaidItems.add(PURCHASE_ITEM_FULL);
				return;
			}
			PreferencesManager preferencesManager = getCfgPreferencesManager();
			for (int i = 0; i < mItemArrary.length; i++) {

				String item = CryptTool.encrypt(String.valueOf(mItemArrary[i]), getAndroidId());
				String value = preferencesManager.getString(item, "");
				boolean bPurchased = false;
				try {
					bPurchased = Boolean.valueOf(CryptTool.decrypt(value, getAndroidId()
							+ mItemArrary[i]));
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (bPurchased) {
					mPaidItems.add(mItemArrary[i]);
				}
			}
		} else if (PurchaseStateManager.query(mContext, ACTIVIE_SUCCESS) != null) {
			mPaidItems.add(PURCHASE_ITEM_ACTIVE_CODE);
			return;
		}

	}
		
	
	private void checkSetting() {
		if (!isItemCanUse(PURCHASE_ITEM_AD) || !isItemCanUse(PURCHASE_ITEM_EFFECT)) {
			sendTrialExpiredBroadCast(1);
			return;
		}
	}
	
	private void showTrialPage() {
		if (!queryItemPurchaseState(PURCHASE_ITEM_FULL)) {
			if (isTrial()) {
				startCheckTrialTask();
				mTrialing = true;
			} else if (getTrialDate() > 0) {
				mHandler.postDelayed(new Runnable() { // 延迟弹框，初始化出现在桌面的启动过程，避免打乱桌面加载这里延迟1min。

							@Override
							public void run() {
								// TODO Auto-generated method stub
								sendTrialExpiredBroadCast(1);
								onTrialExpired();
							}
						}, 60 * 1000);
			}
		}
	}

	private JSONObject getPurchaseCache() {
		byte[] cacheData = null;
		JSONObject obj = null;
		if (mCacheManager.isCacheExist(mPayInfoFileName)) {
			cacheData = mCacheManager.loadCache(mPayInfoFileName);
		}
		if (cacheData != null) {
			try {
				obj = CacheUtil.byteArrayToJson(cacheData);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return obj;
	}

	/**
	 * <br>功能简述:启动试用超时定时器
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void startCheckTrialTask() {
		if (mAlarmManager == null) {
			mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		}
		long tiggertTime = getTrialDate();
		Intent intent = new Intent(ACTION_TRIAL_EXPIRED);
		mAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, tiggertTime, mAlarmIntent);
		intent = null;
	}

	/**
	 * <br>功能简述:开始试用
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void startTrial() {
		long date = getTrialDate();
		long now = System.currentTimeMillis();
		if (date == 0 || (date > now && date - now < AlarmManager.INTERVAL_DAY)) {
			mTrialing = true;
			if (date == 0) {
				saveTrialDate(System.currentTimeMillis() + AlarmManager.INTERVAL_DAY);
			}
			startCheckTrialTask();
			mContext.sendBroadcast(new Intent(DeskSettingUtils.ACTION_HAD_PAY_REFRESH));
		}
	}

	/**
	 * <br>功能简述:保存过期日期
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param date 过期日期
	 */
	private void saveTrialDate(long date) {
		synchronized (mMutex) {
			mTrialDate = date;
			PreferencesManager preferencesManager = getCfgPreferencesManager();
			// 加密id和时间防止破解
			String edate = CryptTool.encrypt(String.valueOf(date), getAndroidId());
			preferencesManager.putString(mTrialExpriedPreferenceId, edate);
			preferencesManager.commit();

			try {
				JSONObject obj = null;
				byte[] cacheData = null;
				if (mCacheManager.isCacheExist(mPayInfoFileName)) {
					cacheData = mCacheManager.loadCache(mPayInfoFileName);
				}
				if (cacheData != null) {
					obj = CacheUtil.byteArrayToJson(cacheData);
				}
				if (obj == null) {
					obj = new JSONObject();
				}
				try {
					obj.put(TAG_TRIAL_DATE, date);
					mCacheManager.saveCache(mPayInfoFileName, obj.toString().getBytes());
				} catch (Exception e) {
					// TODO: handle exception
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	/**
	 * <br>功能简述:是否正在试用
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public boolean isTrial() {
		if (GoAppUtils.isAppExist(mContext, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
			PreferencesManager pm = getCfgPreferencesManager();
			//如果已显示过过期框说明以前已试用过，这里针对改时间的一个“小保护”
			boolean hadNotShow = pm.getBoolean(
					IPreferencesIds.PREFERENCE_PURCHASE_CFG_ITEM_SHOW_UPDATE_DIALOG, true);

			long now = System.currentTimeMillis();
			long trialDate = getTrialDate();
			return trialDate > now && Math.abs(trialDate - now) < AlarmManager.INTERVAL_DAY
					&& hadNotShow;
		}
		return false;
	}

	private PreferencesManager getCfgPreferencesManager() {
		if (mPreference == null) {
			String preferencesFile = CryptTool.encrypt(IPreferencesIds.PREFERENCE_PURCHASE_CFG,
					getAndroidId());
			preferencesFile = preferencesFile.replace("/", "");
			preferencesFile = preferencesFile.replace("$", "");
			preferencesFile = preferencesFile.replace("*", "");
			preferencesFile = preferencesFile.replace(".", "");
			mPreference = new PreferencesManager(mContext, preferencesFile, Context.MODE_WORLD_READABLE);
		}

		return mPreference;

	}


	public int getTrialStatus() {
		getTrialDate();
		if (mTrialDate == 0) {
			return STATUS_TRAIL_NON;
		} else if (isTrial()) {
			return STATUS_TRAIL_IN;
		} else {
			return STATUS_TRAIL_EXPIRED;
		}
	}

	/**
	 * <br>功能简述:获取过期日期
	 * <br>功能详细描述:
	 * <br>注意: 取sd卡和PreferencesManager中较大者（防止用户修改数据，目前只能做到这步，后续考虑保存到网络？）
	 * @return
	 */
	public long getTrialDate() {
		long now = System.currentTimeMillis();

		if (mTrialDate == 0) {
			long date1 = 0;
			long date2 = 0;
			PreferencesManager preferencesManager = getCfgPreferencesManager();
			// 加密id和时间防止破解
			String trialDate = preferencesManager.getString(mTrialExpriedPreferenceId, "0");
			trialDate = CryptTool.decrypt(trialDate, getAndroidId());
			try {
				date1 = Long.valueOf(trialDate);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (now < date1 || date1 == 0) { // 已过期就不用再看sd数据了
				//取sd卡，防止用户清数据
				byte[] cacheData = null;
				if (mCacheManager.isCacheExist(mPayInfoFileName)) {
					cacheData = mCacheManager.loadCache(mPayInfoFileName);
				}
				if (cacheData != null) {
					JSONObject obj = null;
					try {
						if (cacheData != null) {
							obj = CacheUtil.byteArrayToJson(cacheData);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					if (obj != null) {
						date2 = obj.optLong(TAG_TRIAL_DATE);
					}
				}
			}
			mTrialDate = Math.max(date1, date2);
		}

		return mTrialDate;
	}

	/**
	 * <br>功能简述:查询功能是否可以使用
	 * <br>功能详细描述:
	 * <br>注意:可以使用两个条件：1.试用;2.已付费
	 * @param purchaseItem
	 * @return
	 */
	public boolean isItemCanUse(int purchaseItem) {
		if (getTrialStatus() == STATUS_TRAIL_EXPIRED) {
			mTrialing = false;
		} else if (getTrialStatus() == STATUS_TRAIL_IN) {
			mTrialing = true;
		}
		return mTrialing || queryItemPurchaseState(purchaseItem);
	}

	/**
	 * <br>功能简述:根据付费项查询是否已付费
	 * <br>功能详细描述:首选检查是否全部付费再看具体项
	 * <br>注意:
	 * @param purchaseItem
	 * @return
	 */
	public boolean queryItemPurchaseState(int purchaseItem) {
		boolean bPurchased = false;
		synchronized (mMutex) {
			if (mPaidItems != null) {
				bPurchased = mPaidItems.contains(PURCHASE_ITEM_FULL) |  mPaidItems.contains(PURCHASE_ITEM_ACTIVE_CODE);
				if (!bPurchased) {
					if (purchaseItem == PURCHASE_ITEM_FULL) {
						for (int i = 0; i < PURCHASE_ITEMS.length; i++) {
							if (!(bPurchased = mPaidItems.contains(PURCHASE_ITEMS[i]))) {
								break;
							}
						}
					} else {
						bPurchased = mPaidItems.contains(purchaseItem);
					}

				}
			}
		}
		return bPurchased;
	}

	public void savePurchasedItem(final int purchaseItem) {
		synchronized (mMutex) {
			if (!mPaidItems.contains(purchaseItem)) {
				mPaidItems.add(purchaseItem);
			}
			if (mTrialing
					&& (mPaidItems.size() == mItemArrary.length || purchaseItem == PURCHASE_ITEM_FULL)) {
				if (mAlarmManager != null) {
					mAlarmManager.cancel(mAlarmIntent);
				}
			}
		}
		sendPurchasedBroadCast(purchaseItem);
		String processName = AppUtils.getCurProcessName(mContext); // 只在主进程保存
		if (processName != null && processName.equals(LauncherEnv.PROCESS_NAME)) {
			new Thread() {
				public void run() {
					PreferencesManager preferencesManager = getCfgPreferencesManager();
					String item = CryptTool.encrypt(String.valueOf(purchaseItem),
							getAndroidId());
					String value = CryptTool.encrypt(String.valueOf(true), getAndroidId()
							+ purchaseItem);
					preferencesManager.putString(item, value);
					preferencesManager.commit();
					save2SDScard(String.valueOf(purchaseItem));

				};
			}.start();
		}
	}

	public boolean isPayAnyFunction() {
		return !mPaidItems.isEmpty();
	}

	public void save2SDScard(String purchaseItem) {
		synchronized (mMutex) {
			try {
				byte[] cacheData = null;
				if (mCacheManager.isCacheExist(mPayInfoFileName)) {
					cacheData = mCacheManager.loadCache(mPayInfoFileName);
				}
				JSONObject obj = null;
				if (cacheData != null) {
					try {
						obj = CacheUtil.byteArrayToJson(cacheData);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				ArrayList<String> list = parsePaidInfo(obj);
				if (list != null && list.contains(purchaseItem)) {
					return;
				}
				if (list == null) {
					list = new ArrayList<String>();
				}
				list.add(purchaseItem);
				JSONArray jsonArray = null;

				if (obj != null) {
					try {
						jsonArray = obj.getJSONArray("paiditems");
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else {
					obj = new JSONObject();
				}

				if (jsonArray == null) {
					jsonArray = new JSONArray();
					obj.put("paiditems", jsonArray);
				}

				JSONObject json = new JSONObject();
				json.put("item", purchaseItem);

				jsonArray.put(json);
				mCacheManager.saveCache(mPayInfoFileName, obj.toString().getBytes());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private ArrayList<String> parsePaidInfo(JSONObject paidJson) {
		if (paidJson != null) {
			try {
				JSONArray jsonArray = paidJson.optJSONArray("paiditems");
				if (jsonArray != null && jsonArray.length() > 0) {
					int length = jsonArray.length();
					ArrayList<String> paidInfoList = new ArrayList<String>(length);
					for (int i = 0; i < length; i++) {
						JSONObject json = jsonArray.getJSONObject(i);
						String item = json.optString("item", "");
						if (!item.equals("")) {
							paidInfoList.add(item);
						}
					}
					return paidInfoList;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * <br>功能简述:获取付费功能的状态
	 * <br>功能详细描述:这个接口用于UI上的显示，包含了付费状态和地区，渠道等因素，从原来的DeskSettingUtils移过来
	 * <br>注意:
	 */
	public int getPayFunctionState(int purchaseItem) {
		int state;
		boolean hasPay = isItemCanUse(purchaseItem);
		//付款就可见可用
		if (hasPay) {
			state = STATE_CAN_USE;
		} else if (Statistics.is200ChannelUid(mContext)) { //未付费，不是韩国地区，是200渠道，可见但需要付费才能用就
			state = STATE_VISABLE;
		} else {
			//都不是就可能是cn包。可见可以用
			state = STATE_CAN_USE;
		}

		return state;
	}

	public int[] getPurchaseItems() {
		return mItemArrary;
	}

	private void initReceiver() {
		if (mContext == null) {
			return;
		}
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals(ACTION_PURCHASE_STATE_CHANAGED)) {
					String productid = intent.getStringExtra(PURCHASE_PRODUCTID);
					int payResult = intent.getIntExtra(PURCHASE_RESULT, 1);
					if (productid != null && payResult == 0) {
						for (int i = 0; i < PURCHASE_GETJAR_PRODUCTID.length; i++) {
							if (PURCHASE_GETJAR_PRODUCTID[i].equals(productid)) {
								handleItemPurchased(PURCHASE_ITEMS[i]);
								break;
							}
						}
					}
				} else if (intent.getAction().equals(ACTION_TRIAL_EXPIRED)) {
					if (!queryItemPurchaseState(PURCHASE_ITEM_FULL)) {
						sendTrialExpiredBroadCast(0);
						onTrialExpired();
					}
				} else if (intent.getAction().equals(ACTION_START_TRAIL)) {
					String entrance = intent
							.getStringExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID);
					String tabId = intent.getStringExtra(FunctionPurchasePageActivity.TABID);
					startTrial();
					GuiThemeStatistics.functionPurchaseStaticData("all", "free_trial", 1, entrance,
							tabId, 2);
				} /*else if (intent.getAction().equals(ACTION_PURCHASE_LIMIT_FREEM)) {
					handCheckLimitFreeResult(intent);
				}*/
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_PURCHASE_STATE_CHANAGED);
		filter.addAction(ACTION_START_TRAIL);
//		filter.addAction(ACTION_PURCHASE_LIMIT_FREEM);
		filter.addAction(ACTION_TRIAL_EXPIRED);
		mContext.registerReceiver(mReceiver, filter);
	}

	private void handleItemPurchased(int item) {
		String processName = AppUtils.getCurProcessName(mContext);
		savePurchasedItem(item);
		if (processName != null && processName.equals(LauncherEnv.PROCESS_NAME)) { // 只在主进程中上传统计 ，防止多分统计
			GuiThemeStatistics.functionPurchaseStaticData(getSender(item), "j001", 1, mEntranceId,
					mTabId, getTrialStatus());
		}

	}

	public void onPrimeKeyInstall() {
		initPayStatus();
		PreferencesManager pm = new PreferencesManager(mContext, IPreferencesIds.PREFERENCE_SETTING_CFG, Context.MODE_PRIVATE
				| Context.MODE_MULTI_PROCESS);
		boolean startSlideDock = pm.getBoolean(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_GOTO_SLIDEDOCK, false);
		if (startSlideDock) {
			Intent intent = new Intent(mContext, DeskSettingSideDockActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("showTips", true);
			mContext.startActivity(intent);
			pm.putBoolean(IPreferencesIds.PREFERENCE_SETTING_CFG_ITEM_GOTO_SLIDEDOCK, false);
			pm.commit();
			return;
		}
		//檢查是否是符合限免条件
//		if (Machine.isValidLimitFreeInstall(mContext) && Machine.isLimitFreeDate()) {
//
//			if (Machine.isLimitFreeUser(mContext)) { // 下载前已验证过,直接允许用户使用
//				savePurchasedItem(PURCHASE_ITEM_FULL);
//				sendPurchasedBroadCast(PURCHASE_ITEM_FULL);
//				if (GoLauncher.getContext() != null) {
//					DeskSettingUtils.showHadPayDialog(GoLauncher.getContext());
//				}
//			} else {
//				if (Machine.isNetworkOK(mContext)) {
//					if (mPromotionsControl == null) {
//						mPromotionsControl = new PromotionsControl(mContext);
//					}
//					mPromotionsControl.checkLimitFreemValid();
//				}
//			}
//		} else {
			startPayPage("5");
//		}
	}
	/**
	 * <br>功能简述:用于点击key包时启动
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void startPayPage(String entrance) {
		if (queryItemPurchaseState(PURCHASE_ITEM_FULL)) {
			if (GoLauncherActivityProxy.getActivity() != null) {
				DeskSettingUtils.showHadPayDialog(GoLauncherActivityProxy.getActivity());
			}
			return;
		}
		getTrialDate();
		startItemPayPage(entrance);
	}

	/**
	 * <br>功能简述:统计使用
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param productid
	 * @return
	 */
	public String getSender(int productid) {
		if (productid == PURCHASE_ITEM_EFFECT) {
			return "2";
		} else if (productid == PURCHASE_ITEM_SECURITY) {
			return "3";
		} else if (productid == PURCHASE_ITEM_QUICK_ACTIONS) {
			return "4";
		} else if (productid == PURCHASE_ITEM_AD) {
			return "1";
		} else if (productid == PURCHASE_ITEM_FILTER) {
			return "5";
		}
		return "all";
	}

	private synchronized void onTrialExpired() {
		PreferencesManager preferencesManager = getCfgPreferencesManager();
		boolean showUpdateDialog = preferencesManager.getBoolean(
				IPreferencesIds.PREFERENCE_PURCHASE_CFG_ITEM_SHOW_UPDATE_DIALOG, true);
		if (showUpdateDialog && !queryItemPurchaseState(PURCHASE_ITEM_FULL)) {
			preferencesManager.putBoolean(
					IPreferencesIds.PREFERENCE_PURCHASE_CFG_ITEM_SHOW_UPDATE_DIALOG, false);
			preferencesManager.commit();

			startItemPayPage("6");
		}
	}

	/**
	 * <br>功能简述:启动功能购买
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void startItemPayPage(String entrance) {
		String tabId = FunctionPurchaseManager.TAB_FUNCTION_NO_PAY;
		if (isPayAnyFunction()) {
			tabId = FunctionPurchaseManager.TAB_FUNCTION_HAS_PAY;
		}
		Intent intent = new Intent(mContext, FunctionPurchasePageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(FunctionPurchasePageActivity.ENTRANCEID, entrance);
		intent.putExtra(FunctionPurchasePageActivity.TABID, tabId);
		mContext.startActivity(intent);
	}
	/**
	 * <br>功能简述:购买全部功能
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void purchaseItem(int item, String entranceId, String tabId) {
		mEntranceId = entranceId;
		mTabId = tabId;

		if (item == PURCHASE_ITEM_FULL) {
			//			String productid = "android.test.purchased";
			if (mInBillingManager == null) {
				mInBillingManager = ThemeAppInBillingManager.getInstance(mContext);
			}
			mInBillingManager.requestPurchase(FULL_PAY_BILLING_ID, FULL_PAY_BILLING_ID, this);
		} else {
			if (item > PURCHASE_ITEM_FULL) { // 开始设计时没考虑后面增加购买项，这里导致逻辑奇怪
				--item;
			}
			Intent intent = new Intent(REQUEST_PURCHASE_ACTION);
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			intent.putExtra(PURCHASE_PRICE, PURCHASE_GETJAR_PRICE_RAITE[item]);
			intent.putExtra(PURCHASE_PRODUCTID, PURCHASE_GETJAR_PRODUCTID[item]);
			mContext.sendBroadcast(intent);
//			GetJarManager.buildInstance(mContext).requestPurchase(PURCHASE_GETJAR_PRODUCTID[item], PURCHASE_GETJAR_PRODUCTID[item], "function", PURCHASE_GETJAR_PRICE_RAITE[item]);
		}
	}

	@Override
	public void purchaseState(int purchaseState, String productid) {
		// TODO Auto-generated method stub
		if (ThemeAppInBillingManager.PURCHASE_STATE_PURCHASED == purchaseState
				&& productid.equals(FULL_PAY_BILLING_ID)) {
			savePurchasedItem(PURCHASE_ITEM_FULL);
            if (DeskSettingUtils
                    .isFirstUpLoadStatist(
                            mContext,
                            LauncherEnv.Path.STATIST_GERJAR_FILE_NAME,
                            IPreferencesIds.PREFERENCES_GETJAR_PAY_STATICST_FIRST_UPLOAD)) {
                GuiThemeStatistics.functionPurchaseStaticData("all", "j005", 1,
                        mEntranceId, mTabId, getTrialStatus());
            }
		}
	}

	private void sendPurchasedBroadCast(int productId) {
		broadCast(IAppManagerMsgId.FUNCTION_ITEM_PURCHASED, productId, null, null);
		mContext.sendBroadcast(new Intent(DeskSettingUtils.ACTION_HAD_PAY_REFRESH));
	}

	public void sendTrialExpiredBroadCast(int forceClear) {
		mTrialing = false;
		broadCast(IAppManagerMsgId.FUNCTION_TRIALEXPIRED, forceClear, null, null);
	}

	/**
	 * <br>功能简述:提供给过期时触发收费界面
	 * <br>功能详细描述:一些触发购买的操作触发此次购买，一种类型只触发一次，复杂！
	 * <br>注意:
	 */
	public boolean showItemExpiredNotPayPaye(String entrance, String item) {
		if (GoAppUtils.isAppExist(mContext, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
			PreferencesManager preferencesManager = getCfgPreferencesManager();
			if (preferencesManager.getBoolean(item, true)) {
				preferencesManager.putBoolean(item, false);
				preferencesManager.commit();
				startItemPayPage(entrance);
				return true;
			}
		}
		return false;
	}

	public void downLoadGetJarKey() {
		//检查是否符合限免用户, 当前时间是否在23-24两天，桌面是否在这两天安装的 
//		if (Machine.isLimitFreeDate() && Machine.isValidLimitFreeInstall(mContext)
//				&& Machine.isNetworkOK(mContext)) {
//			if (mPromotionsControl == null) {
//				mPromotionsControl = new PromotionsControl(mContext);
//			}
//			mPromotionsControl.checkLimitFreemValid();
//		}
		GoAppUtils.gotoBrowserIfFailtoMarket(mContext, MarketConstant.APP_DETAIL
				+ LauncherEnv.Plugin.PRIME_GETJAR_KEY, LauncherEnv.Plugin.PRIME_GETJAR_KEY_URL);
	}

	public static void destory() {
		if (sInstance != null) {
			sInstance.clean();
		}
	}

	private void clean() {
		if (mReceiver != null && mContext != null) {
			try {
				mContext.unregisterReceiver(mReceiver);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (mInBillingManager != null) {
			mInBillingManager.destory();
		}
		clearAllObserver();
	}
	
	/**
	 * <br>功能简述:限免检查结果
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param intent
	 */
//	private void handCheckLimitFreeResult(Intent intent) {
//		boolean saveResult = intent.getBooleanExtra("saveResult", false);
//		if (saveResult == true) {
//			int result = intent.getIntExtra("result", CHECK_LIMIT_FREE_INVALID);
//			if (result == CHECK_LIMIT_FREE_OK) {
//				if (GoAppUtils.isAppExist(mContext, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
//					savePurchasedItem(PURCHASE_ITEM_FULL);
//					if (GoLauncher.getContext() != null) {
//						DeskSettingUtils.showHadPayDialog(GoLauncher.getContext());
//					}
//				} else { //此时key包还未安装，待安装后再开放收费功能
//					Machine.enableLimitFree(mContext);
//				}
//			} else if (result == CHECK_LIMIT_FREE_INVALID
//					&& GoAppUtils.isAppExist(mContext, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) { // 验证为非限免用户，弹出付费对话框
//				String processName = AppUtils.getCurProcessName(mContext); // 只在主进程保存
//				if (processName != null && processName.equals(LauncherEnv.PROCESS_NAME)) {
//					startPayPage("5");
//				}
//			}
//		} else if (GoAppUtils.isAppExist(mContext, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) { // 临时性让用户使用
//			if (!mPaidItems.contains(PURCHASE_ITEM_FULL)) {
//				mPaidItems.add(PURCHASE_ITEM_FULL);
//			}
//		}
//		sendPurchasedBroadCast(PURCHASE_ITEM_FULL);
//	}
	
	
	/**
	 * <br>功能简述:获取androidid
	 * <br>功能详细描述:某些2.2设备上拿到的为空，因为只是用到作为加密的密钥，这里默认做个处理
	 * <br>注意:
	 * @return
	 */
	private String getAndroidId() {
		String id = Machine.getAndroidId();
		if (id == null) {
			id = "golancher";
		}
		return id;
	}
	
	
	public void checkActiveCode(String code, IActiveListener listener) {
		if (mPromotionsControl == null) {
			mPromotionsControl = new PromotionsControl(mContext);
		}
		mPromotionsControl.checkActiveCode(code, listener);
	}
	
}
