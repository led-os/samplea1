package com.jiubang.ggheart.apps.desks.diy.pref;

import static com.jiubang.ggheart.apps.desks.diy.pref.PrefKeyMap.OLD_KEY2OLD_NAME_MAP;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;

/**
 * 
 * @author guoyiqing
 * 
 */
public class OriginalPrefHandler {

	private static final byte[] MUTEX = new byte[0];
	private static final HashMap<String, PreferencesManager> NAME2SPMAP = new HashMap<String, PreferencesManager>();
	private static final String DEFAULT_SHAREDPREFERENCE_NAME = IPreferencesIds.DESK_SHAREPREFERENCES_FILE;
	private HashMap<String, PreferencesManager> mCommitEdits;
	private ExecutorService mThreadService;
	private Context mContext;

	public PreferencesManager mapSp(String key) {
		String value = OLD_KEY2OLD_NAME_MAP.get(key);
		if (value == null) {
			value = DEFAULT_SHAREDPREFERENCE_NAME;
		}
		PreferencesManager sp = NAME2SPMAP.get(key);
		if (sp == null) {
			sp = new PreferencesManager(mContext, value, Context.MODE_PRIVATE);
			NAME2SPMAP.put(value, sp);
		}
		return sp;
	}

	public OriginalPrefHandler(Context context) {
		mContext = context;
		mThreadService = Executors.newCachedThreadPool();
		mCommitEdits = new HashMap<String, PreferencesManager>();
	}

	public boolean getBoolean(String key, boolean defValue) {
		return mapSp(key).getBoolean(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return mapSp(key).getFloat(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return mapSp(key).getInt(key, defValue);
	}

	public long getLong(String key, long defValue) {
		return mapSp(key).getLong(key, defValue);
	}

	public String getString(String key, String defValue) {
		return mapSp(key).getString(key, defValue);
	}

	public void putBoolean(String key, boolean b) {
		PreferencesManager pm = mapSp(key);
		pm.putBoolean(key, b);
		putSafely(pm);
	}

	public void putInt(String key, int i) {
		PreferencesManager pm = mapSp(key);
		pm.putInt(key, i);
		putSafely(pm);
	}

	public void putFloat(String key, float f) {
		PreferencesManager pm = mapSp(key);
		pm.putFloat(key, f);
		putSafely(pm);
	}

	public void putLong(String key, long l) {
		PreferencesManager pm = mapSp(key);
		pm.putLong(key, l);
		putSafely(pm);
	}

	public void putString(String key, String s) {
		PreferencesManager pm = mapSp(key);
		pm.putString(key, s);
		putSafely(pm);
	}

	public void commit() {
		mThreadService.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (MUTEX) {
					for (Entry<String, PreferencesManager> entry : mCommitEdits
							.entrySet()) {
						entry.getValue().commit();
					}
					mCommitEdits.clear();
				}
			}
		});
	}

	private void putSafely(PreferencesManager sp) {
		synchronized (MUTEX) {
			mCommitEdits.put(sp.toString(), sp);
		}
	}

}
