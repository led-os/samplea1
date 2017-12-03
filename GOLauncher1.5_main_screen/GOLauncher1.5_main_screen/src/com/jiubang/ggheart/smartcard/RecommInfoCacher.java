package com.jiubang.ggheart.smartcard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.gau.utils.cache.CacheManager;
import com.gau.utils.cache.impl.FileCacheImpl;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * @author guoyiqing
 * 
 */
public class RecommInfoCacher {

	private static final String TAG_FOLDS = "folds";
	private static final String TAG_LIGHTGAMES = "lightgames";
	private static final String KEY_RECOMMEND = "recommend";
	private String mInfoJson;

	private Context mContext;

	public RecommInfoCacher(Context context) {
		mContext = context;
	}

	private int getFoldsCode() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		return pref.getInt(PrefConst.KEY_RECOMMEND_FOLD_HASHCODE, 0);
	}

	private void setFoldsCode(int code) {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		pref.putInt(PrefConst.KEY_RECOMMEND_FOLD_HASHCODE, code);
		pref.commit();
	}

	private int getGamesCode() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		return pref.getInt(PrefConst.KEY_RECOMMEND_GAME_HASHCODE, 0);
	}

	private void setGamesCode(int code) {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		pref.putInt(PrefConst.KEY_RECOMMEND_GAME_HASHCODE, code);
		pref.commit();
	}

	public void cache(JSONObject json) {
		if (json == null || json.equals("")) {
			return;
		}
		try {
			JSONArray foldsJson = json.getJSONArray(TAG_FOLDS);
			JSONArray gamesJson = json.getJSONArray(TAG_LIGHTGAMES);
			int foldHashCode = foldsJson.toString().hashCode();
			int gameHashCode = gamesJson.toString().hashCode();
			CacheManager cacheManager = new CacheManager(new FileCacheImpl(
					LauncherEnv.Path.SMARTCARD_RECOMMEND_INFO_PATH));
			boolean change = false;
			if (foldHashCode != getFoldsCode()) {
				RecommInfoLoader.getLoader(mContext)
						.refreshFoldsInfo(foldsJson);
				setFoldsCode(foldHashCode);
				change = true;
			}
			if (gameHashCode != getGamesCode()) {
				RecommInfoLoader.getLoader(mContext)
						.refreshGamesInfo(gamesJson);
				setGamesCode(gameHashCode);
				change = true;
			} 
			if (change) {
				cacheManager.saveCache(KEY_RECOMMEND, json.toString()
						.getBytes());
			}
			mInfoJson = json.toString();
			clearExpiredData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void clearExpiredData() {
		PreferencesManager preferencesManager = new PreferencesManager(mContext,
				IPreferencesIds.SMART_CARD_LIGHT_GAME_SHOW_END_TIME, Context.MODE_PRIVATE);
		preferencesManager.clear();
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.SMART_CARD_REC_APP_INDEX, Context.MODE_PRIVATE);
		manager.clear();
	}
	
	public JSONArray getFoldsJson() {
		if (mInfoJson == null) {
			CacheManager cacheManager = new CacheManager(new FileCacheImpl(
					LauncherEnv.Path.SMARTCARD_RECOMMEND_INFO_PATH));
			byte[] bytes = cacheManager.loadCache(KEY_RECOMMEND);
			if (bytes != null && bytes.length > 0) {
				mInfoJson = new String(bytes);
			}
		}
		try {
			if (mInfoJson != null) {
				JSONObject json = new JSONObject(mInfoJson);
				JSONArray foldsJson = json.getJSONArray(TAG_FOLDS);
				return foldsJson;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getGamesJson() {
		if (mInfoJson == null) {
			CacheManager cacheManager = new CacheManager(new FileCacheImpl(
					LauncherEnv.Path.SMARTCARD_RECOMMEND_INFO_PATH));
			byte[] bytes = cacheManager.loadCache(KEY_RECOMMEND);
			if (bytes != null && bytes.length > 0) {
				mInfoJson = new String(bytes);
			}
		}
		try {
			if (mInfoJson != null) {
				JSONObject json = new JSONObject(mInfoJson);
				JSONArray gamesJson = json.getJSONArray(TAG_LIGHTGAMES);
				return gamesJson;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}

}
