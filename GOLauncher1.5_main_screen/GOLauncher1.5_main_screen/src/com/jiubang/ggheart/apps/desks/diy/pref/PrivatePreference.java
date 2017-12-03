package com.jiubang.ggheart.apps.desks.diy.pref;

import static com.jiubang.ggheart.apps.desks.diy.pref.PrefKeyMap.NEW_KEY2NEW_NAME_MAP;
import static com.jiubang.ggheart.apps.desks.diy.pref.PrefKeyMap.OLD_KEY2NEW_NAME_MAP;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

/**
 * 
 * @author guoyiqing
 * 
 */
@SuppressLint("CommitPrefEdits")
public class PrivatePreference implements OnPrefUpgradListener {

	private static final byte[] MUTEX = new byte[0];
	private static final String SP_TEMP_BACKUP_PRIVATE = "temp_backup_private_sp";
	private static final String SP_TEMP_UNBACKUP_PRIVATE_SP = "temp_unbackup_private_sp";
	private static PrivatePreference sInstance;
	private HashMap<String, SinglePreference> mName2SpMap;
	private HashMap<String, SinglePreference> mName2TempSpMap;
	private SinglePreference mBackupPrivateSp;
	private SinglePreference mTempBackupSp;
	private SinglePreference mTempUnBackupSp;
	private SinglePreference mUnBackupSp;
	private OriginalPrefHandler mOriginalHandler;
	private HashSet<SinglePreference> mCommitEdits;
	private volatile boolean mUpgradeFinished;
	private Context mContext;
	private static final String KEY_IS_UPGRADE_V415 = "is_upgrade_v415";

	private PrivatePreference(Context context) {
		mContext = context.getApplicationContext();
		mCommitEdits = new HashSet<SinglePreference>();
		mOriginalHandler = new OriginalPrefHandler(mContext);
		mName2SpMap = new HashMap<String, SinglePreference>();
		mName2TempSpMap = new HashMap<String, SinglePreference>();
		mUpgradeFinished = readUpgradeState();
		initSharedPreferences();
	}

	public boolean isUpgraded() {
		return mUpgradeFinished;
	}

	private boolean readUpgradeState() {
		String dir = Environment.getDataDirectory() + "/data/"
				+ mContext.getPackageName() + "/shared_prefs";
		new File(dir).mkdir();
		File file = new File(dir + "/" + PrefKeyMap.SP_UNBACKUP_PRIVATE
				+ ".xml");
		if (file.exists()) {
			SinglePreference singlePref = new SinglePreference(mContext,
					PrefKeyMap.SP_UNBACKUP_PRIVATE);
			return singlePref.getSp().getBoolean(KEY_IS_UPGRADE_V415, false);
		} else {
			return false;
		}
	}

	private void initSharedPreferences() {
		if (mUpgradeFinished) {
			mBackupPrivateSp = new SinglePreference(mContext,
					PrefKeyMap.SP_BACKUP_PRIVATE);
			mUnBackupSp = new SinglePreference(mContext,
					PrefKeyMap.SP_UNBACKUP_PRIVATE);
			mName2SpMap.put(PrefKeyMap.SP_BACKUP_PRIVATE, mBackupPrivateSp);
			mName2SpMap.put(PrefKeyMap.SP_UNBACKUP_PRIVATE, mUnBackupSp);
		} else {
			mTempBackupSp = new SinglePreference(mContext,
					SP_TEMP_BACKUP_PRIVATE);
			mTempUnBackupSp = new SinglePreference(mContext,
					SP_TEMP_UNBACKUP_PRIVATE_SP);
			mName2TempSpMap.put(PrefKeyMap.SP_BACKUP_PRIVATE, mTempBackupSp);
			mName2TempSpMap
					.put(PrefKeyMap.SP_UNBACKUP_PRIVATE, mTempUnBackupSp);
		}
	}

	/**
	 * 子进程禁止使用,请使用旧的,原因:Mode_Private,主进程数据和子进程互不相知
	 * @param context
	 * @return
	 */
	public static synchronized PrivatePreference getPreference(Context context) {
		if (sInstance == null) {
			sInstance = new PrivatePreference(context);
		}
		return sInstance;
	}

	private SinglePreference mapSp(String key, boolean isRead) {
		SinglePreference sp = null;
		if (isRead) {
			if (mUpgradeFinished) {
				String name = OLD_KEY2NEW_NAME_MAP.get(key);
				if (name == null) {
					name = NEW_KEY2NEW_NAME_MAP.get(key);
				}
				sp = mName2SpMap.get(name);
				if (sp == null) {
					sp = mUnBackupSp;
				}
				return sp;
			} else {
				if (OLD_KEY2NEW_NAME_MAP.get(key) != null) {
					return null;
				} else {
					String name = NEW_KEY2NEW_NAME_MAP.get(key);
					sp = mName2TempSpMap.get(name);
					if (sp == null) {
						sp = mTempUnBackupSp;
					}
					return sp;
				}
			}
		} else {
			String name = OLD_KEY2NEW_NAME_MAP.get(key);
			if (name == null) {
				name = NEW_KEY2NEW_NAME_MAP.get(key);
			}
			if (mUpgradeFinished) {
				sp = mName2SpMap.get(name);
				if (sp == null) {
					sp = mUnBackupSp;
				}
			} else {
				sp = mName2TempSpMap.get(name);
				if (sp == null) {
					sp = mTempUnBackupSp;
				}
			}
			return sp;
		}
	}

	public boolean getBoolean(String key, boolean defValue) {
		SinglePreference sp = mapSp(key, true);
		if (sp == null) {
			return mOriginalHandler.getBoolean(key, defValue);
		}
		return sp.getSp().getBoolean(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		SinglePreference sp = mapSp(key, true);
		if (sp == null) {
			return mOriginalHandler.getFloat(key, defValue);
		}
		return sp.getSp().getFloat(key, defValue);
	}

	public int getInt(String key, int defValue) {
		SinglePreference sp = mapSp(key, true);
		if (sp == null) {
			return mOriginalHandler.getInt(key, defValue);
		}
		return sp.getSp().getInt(key, defValue);
	}

	public long getLong(String key, long defValue) {
		SinglePreference sp = mapSp(key, true);
		if (sp == null) {
			return mOriginalHandler.getLong(key, defValue);
		}
		return sp.getSp().getLong(key, defValue);
	}

	public String getString(String key, String defValue) {
		SinglePreference sp = mapSp(key, true);
		if (sp == null) {
			return mOriginalHandler.getString(key, defValue);
		}
		return sp.getSp().getString(key, defValue);
	}

	public void putBoolean(String key, boolean b) {
		SinglePreference sp = mapSp(key, false);
		mOriginalHandler.putBoolean(key, b);
		Editor editor = sp.getEditor();
		editor.putBoolean(key, b);
		putSafely(sp);
	}

	public void putInt(String key, int i) {
		SinglePreference sp = mapSp(key, false);
		mOriginalHandler.putInt(key, i);
		Editor editor = sp.getEditor();
		editor.putInt(key, i);
		putSafely(sp);
	}

	public void putFloat(String key, float f) {
		SinglePreference sp = mapSp(key, false);
		Editor editor = sp.getEditor();
		editor.putFloat(key, f);
		putSafely(sp);
	}

	public void putLong(String key, long l) {
		SinglePreference sp = mapSp(key, false);
		mOriginalHandler.putLong(key, l);
		Editor editor = sp.getEditor();
		editor.putLong(key, l);
		putSafely(sp);
	}

	public void putString(String key, String s) {
		SinglePreference sp = mapSp(key, false);
		mOriginalHandler.putString(key, s);
		Editor editor = sp.getEditor();
		editor.putString(key, s);
		putSafely(sp);
	}

	public boolean commit() {
		boolean ret = false;
		mOriginalHandler.commit();
		synchronized (MUTEX) {
			for (SinglePreference pref : mCommitEdits) {
				ret |= pref.getEditor().commit();
			}
			mCommitEdits.clear();
		}
		return ret;
	}

	public void restoreDefault() {
		String dir = Environment.getDataDirectory() + "/data/"
				+ mContext.getPackageName() + "/shared_prefs";
		new File(dir).mkdir();
		File file = new File(dir + "/" + PrefKeyMap.SP_UNBACKUP_PRIVATE + ".xml");
		file.delete();
		file = new File(dir + "/" + PrefKeyMap.SP_BACKUP_PRIVATE + ".xml");
		file.delete();
		SinglePreference singlePref = new SinglePreference(mContext,
				PrefKeyMap.SP_UNBACKUP_PRIVATE);
		singlePref.getEditor().clear();
		singlePref.getEditor().putBoolean(KEY_IS_UPGRADE_V415, true).commit();
		singlePref = new SinglePreference(mContext,
				PrefKeyMap.SP_BACKUP_PRIVATE);
		singlePref.getEditor().clear().commit();
		mUpgradeFinished = true;
	}
	
	@Override
	public void onUpgradeFinish(PrefMigrateManager migrateManager) {
		SinglePreference singlePref = new SinglePreference(mContext,
				PrefKeyMap.SP_UNBACKUP_PRIVATE);
		singlePref.getEditor().putBoolean(KEY_IS_UPGRADE_V415, true).commit();
		if (!mUpgradeFinished) {
			mUpgradeFinished = true;
			List<String> temps1 = new ArrayList<String>();
			temps1.add(SP_TEMP_BACKUP_PRIVATE);
			List<String> temps2 = new ArrayList<String>();
			temps2.add(SP_TEMP_UNBACKUP_PRIVATE_SP);
			synchronized (MUTEX) {
				migrateManager.upgrade(temps1, PrefKeyMap.SP_BACKUP_PRIVATE,
						mContext, null);
				migrateManager.upgrade(temps2, PrefKeyMap.SP_UNBACKUP_PRIVATE,
						mContext, null);
			}
			initSharedPreferences();
			String dir = Environment.getDataDirectory() + "/data/"
					+ mContext.getPackageName() + "/shared_prefs";
			new File(dir).mkdir();
			File file = new File(dir + "/" + SP_TEMP_BACKUP_PRIVATE + ".xml");
			file.delete();
			file = new File(dir + "/" + SP_TEMP_UNBACKUP_PRIVATE_SP + ".xml");
			file.delete();

		}
	}

	private void putSafely(SinglePreference singlePreference) {
		synchronized (MUTEX) {
			mCommitEdits.add(singlePreference);
		}
	}

	
	/**
	 * 
	 * @author guoyiqing
	 * 
	 */
	class SinglePreference {

		private SharedPreferences mSp;
		private SharedPreferences.Editor mEditor;
		private String mName;

		public SinglePreference(Context context, String name) {
			mName = name;
			mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
			mEditor = mSp.edit();
		}

		public Editor getEditor() {
			return mEditor;
		}

		public SharedPreferences getSp() {
			return mSp;
		}

		public String getName() {
			return mName;
		}

		@Override
		public int hashCode() {
			return mName.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof SinglePreference) {
				return mName.equals(((SinglePreference) o).getName());
			}
			return false;
		}

	}

}
