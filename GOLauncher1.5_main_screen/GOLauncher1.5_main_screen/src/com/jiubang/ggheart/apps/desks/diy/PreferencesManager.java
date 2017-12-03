package com.jiubang.ggheart.apps.desks.diy;

import java.io.File;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Environment;

import com.go.proxy.ApplicationProxy;

/**
 * 
 * @author wenjiaming
 * 
 */
public final class PreferencesManager {
	private SharedPreferences mPreferences;
	private Editor mEditor;

	/**
	 * 默认构造函数
	 * @name IPreferencesIds.DESK_SHAREPREFERENCES_FILE
	 * @mode MODE_PRIVATE
	 */
	public PreferencesManager(Context context) {
		this(context, IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
	}

	/**
	 * 自定义构造函数
	 * @throws Exception 
	 */
	public PreferencesManager(Context context, String name, int mode) {
		if (context != null) {
			try {
				mPreferences = context.getSharedPreferences(name, mode);
				mEditor = mPreferences.edit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 清除数据
	 */
	public void clear() {
		if (mEditor != null) {
			mEditor.clear().commit();
		} else if (mPreferences != null) {
			mEditor = mPreferences.edit();
			mEditor.clear().commit();
		}
	}

	public void remove(String key) {
		mPreferences.edit().remove(key).commit();
	}

	public Map<String, ?> getAll() {
		return mPreferences.getAll();
	}

	public boolean contains(String key) {
		return mPreferences.contains(key);
	}

	public boolean getBoolean(String key, boolean defValue) {
		if (mPreferences != null) {
			return mPreferences.getBoolean(key, defValue);
		}
		return defValue;
	}

	public float getFloat(String key, float defValue) {
		if (mPreferences != null) {
			return mPreferences.getFloat(key, defValue);
		}
		return defValue;
	}

	public int getInt(String key, int defValue) {
		if (mPreferences != null) {
			return mPreferences.getInt(key, defValue);
		}
		return defValue;
	}

	public long getLong(String key, long defValue) {
		if (mPreferences != null) {
			return mPreferences.getLong(key, defValue);
		}
		return defValue;
	}
	public String getString(String key, String defValue) {
		if (mPreferences != null) {
			return mPreferences.getString(key, defValue);
		}
		return defValue;
	}

	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		mPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

	public void putBoolean(String key, boolean b) {
		if (mEditor != null) {
			mEditor.putBoolean(key, b);
		}
	}

	public void putInt(String key, int i) {
		if (mEditor != null) {
			mEditor.putInt(key, i);
		}
	}

	public void putFloat(String key, float f) {
		if (mEditor != null) {
			mEditor.putFloat(key, f);
		}
	}

	public void putLong(String key, long l) {
		if (mEditor != null) {
			mEditor.putLong(key, l);
		}
	}

	public void putString(String key, String s) {
		if (mEditor != null) {
			mEditor.putString(key, s);
		}
	}

	public boolean commit() {
		boolean bRet = false;
		if (mEditor != null) {
			bRet = mEditor.commit();
		}
		return bRet;
	}
	//	public static PreferencesManager getInstance() {
	//		if (instance == null) {
	//			instance = new PreferencesManager();
	//		}
	//		return instance;
	//	}

	public static void clearPreferences(Context context) {
		if (context != null) {
			for (int i = 0; i < IPreferencesIds.NEED_CLEAR_PREFERENCES.length; i++) {
				String name = IPreferencesIds.NEED_CLEAR_PREFERENCES[i];
				context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit();
			}
			for (int i = 0; i < IPreferencesIds.NEED_DELETE_PREFERENCES.length; i++) {
				String name = IPreferencesIds.NEED_DELETE_PREFERENCES[i];
				deleteSharedPreference(name);
			}
		}
	}
	public static boolean deleteSharedPreference(String preferencesName) {
		if (preferencesName == null) {
			return false;
		}
		File spFile = new File(Environment.getDataDirectory() + "/data/"
				+ ApplicationProxy.getContext().getPackageName() + "/shared_prefs/" + preferencesName
				+ ".xml");
		return spFile.delete();
	}
	
	public static boolean sharedPreferenceExists(String preferencesName) {
		if (preferencesName == null) {
			return false;
		}
		File spFile = new File(Environment.getDataDirectory() + "/data/"
				+ ApplicationProxy.getContext().getPackageName() + "/shared_prefs/" + preferencesName + ".xml");
		return spFile.exists();
	}
}
