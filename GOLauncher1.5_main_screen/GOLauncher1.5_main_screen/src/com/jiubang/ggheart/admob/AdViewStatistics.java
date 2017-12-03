package com.jiubang.ggheart.admob;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.gau.go.gostaticsdk.StatisticsManager;
import com.gau.go.gostaticsdk.utiltool.Machine;
import com.gau.go.gostaticsdk.utiltool.UtilTool;
import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStorePhoneStateUtil;
import com.jiubang.ggheart.data.statistics.Statistics;

/**
 * 
 * @author guoyiqing
 * @date [2013-9-12]
 */
public class AdViewStatistics {

	private static AdViewStatistics sInstance;
	private PreferencesManager mSp;
	private int mAdLimit;
	private int mPerTime;
	private int mToday;
	private int mSum;
	private int mViewShowed;
	private static int sVersionCode;
	private static String sAndroidId;
	private ExecutorService mPool;

	private AdViewStatistics(Context context) {
		mSp = new PreferencesManager(context);
		mToday = mSp.getInt(AdConstanst.TODAY_KEY, -1);
		if (mToday == -1) {
			mToday = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
			mSp.putInt(AdConstanst.TODAY_KEY, mToday);
		}
		mAdLimit = mSp.getInt(AdConstanst.ADLIMIT_KEY, 0);
		mPerTime = mSp.getInt(AdConstanst.PERTIME_KEY, 0);
		if (mPerTime == 0) {
			mPerTime = 1;
		}
		mSum = mSp.getInt(AdConstanst.SUM_KEY, 0);
		mViewShowed = mSp.getInt(AdConstanst.VIEW_SHOWED_KEY, 0);
		mPool = Executors.newCachedThreadPool();
	}

	public static synchronized AdViewStatistics getStatistics(Context context) {
		if (sInstance == null) {
			sInstance = new AdViewStatistics(context);
		}
		return sInstance;
	}

	public int getDeleteCount() {
		int count = mSp.getInt(AdConstanst.AD_DELETE_COUNT_KEY, 0);
		count++;
		mSp.putInt(AdConstanst.AD_DELETE_COUNT_KEY, count);
		mSp.commit();
		return count;
	}

	/**
	 * <br>
	 * 功能简述:增加点击关闭广告次数 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param addCount
	 *            增加次数
	 */
	public void addDeleteCount(int addCount) {
		int count = mSp.getInt(AdConstanst.AD_DELETE_COUNT_KEY, 0);
		count = count + addCount;
		mSp.putInt(AdConstanst.AD_DELETE_COUNT_KEY, count);
		mSp.commit();
	}

	/**
	 * <br>
	 * 注意:检测统计上是否需要要弹ad,没事干别乱调,每调一次计数++
	 * 
	 * @return
	 */
	public boolean checkNeedShow() {
		int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		if (today != mToday) {
			mToday = today;
			mSum = 0;
			mViewShowed = 0;
		}
		mSum++;
		if (mViewShowed > mAdLimit) {
			save();
			return false;
		} else {
			if (mSum % mPerTime == 0) {
				mViewShowed++;
				save();
				return true;
			} else {
				save();
				return false;
			}
		}
	}

	private void save() {
		mSp.putInt(AdConstanst.SUM_KEY, mSum);
		mSp.putInt(AdConstanst.VIEW_SHOWED_KEY, mViewShowed);
		mSp.putInt(AdConstanst.TODAY_KEY, mToday);
		mSp.commit();
	}

	public void setAdLimit(int adlimit) {
		mAdLimit = adlimit;
		mSp.putInt(AdConstanst.ADLIMIT_KEY, mAdLimit);
		mSp.commit();
	}

	public void setPertime(int pertime) {
		if (pertime <= 0) {
			pertime = 1;
		}
		mPerTime = pertime;
		mSp.putInt(AdConstanst.PERTIME_KEY, mPerTime);
		mSp.commit();
	}

	private static final String PROTOCOL_DIVIDER = "||";
	public static final String ITEM_DIVIDER = "\r\n";
	public static final String ADVIEW_STATISTICS_FILE_NAME = "adview_statistics.txt";
	private static final String SAVE_AD_STATISTICS_THREAD = "save_ad_statistics_thread";

	public void appendAdShow(Context context, int pid, int posId, int adType,
			int switzh) {
		appendAdStatistics(context, "show", String.valueOf(switzh), pid, posId,
				adType);
	}

	public void appendAdClick(Context context, int pid, int posId, int adType) {
		appendAdStatistics(context, "click", "1", pid, posId,
				adType);
	}
	
	public void appendAdClose(Context context, int pid, int posId, int adType) {
		appendAdStatistics(context, "close", "1", pid, posId,
				adType);
	}
	
	private void appendAdStatistics(Context context, String option,
			String optionResult, int pid, int posId, int adType) {
		StringBuilder sb = new StringBuilder(20);
		sb.append(ITEM_DIVIDER);
		sb.append("41").append(PROTOCOL_DIVIDER);
		if (sAndroidId == null) {
			sAndroidId = Machine.getAndroidId(context);
		}
		sb.append(sAndroidId).append(PROTOCOL_DIVIDER);
		sb.append(UtilTool.getBeiJinTime()).append(PROTOCOL_DIVIDER);
		sb.append("83").append(PROTOCOL_DIVIDER);
		sb.append("").append(PROTOCOL_DIVIDER);
		sb.append(option).append(PROTOCOL_DIVIDER);
		sb.append(optionResult).append(PROTOCOL_DIVIDER);
		sb.append(Machine.getSimCountryIso(context)).append(PROTOCOL_DIVIDER);
		sb.append(Statistics.getUid(context)).append(PROTOCOL_DIVIDER);
		if (sVersionCode == 0) {
			sVersionCode = Statistics.buildVersionCode(context);
		}
		sb.append(sVersionCode).append(PROTOCOL_DIVIDER);
		sb.append(context.getString(R.string.curVersion)).append(
				PROTOCOL_DIVIDER);
		sb.append("").append(PROTOCOL_DIVIDER);
		sb.append(pid).append(PROTOCOL_DIVIDER);
		sb.append(posId).append(PROTOCOL_DIVIDER);
		sb.append(GoStorePhoneStateUtil.getVirtualIMEI(context)).append(
				PROTOCOL_DIVIDER);
		sb.append(StatisticsManager.getGOID(context)).append(PROTOCOL_DIVIDER);
		sb.append("").append(PROTOCOL_DIVIDER);
		sb.append(adType);
		saveToFileAsync(context, sb.toString());
	}

	private void saveToFileAsync(final Context context, final String content) {
		Thread saveThread = new Thread(SAVE_AD_STATISTICS_THREAD) {
			@Override
			public void run() {
				super.run();
				saveToFile(context, content);
			}
		};
		mPool.execute(saveThread);
	}

	private synchronized void saveToFile(Context context, String content) {
		if (content == null) {
			return;
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					context.openFileOutput(ADVIEW_STATISTICS_FILE_NAME,
							Context.MODE_APPEND)));
			writer.write(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void cleanUp() {
		if (mPool != null) {
			mPool.shutdown();
			mPool = null;
		}
	}

}
