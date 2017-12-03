package com.jiubang.ggheart.smartcard;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.SparseArray;

import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderInfo;

/**
 * 
 * @author guoyiqing
 * 
 */
public class RecommInfoLoader {

	private static RecommInfoLoader sInstance;
	private Context mContext;
	private SparseArray<List<Recommanditem>> mRecommendFolds;
	private List<Recommanditem> mRecommendLightGames;
	private RecommInfoCacher mRecommInfoCacher;

	public RecommInfoLoader(Context context) {
		mContext = context;
		mRecommInfoCacher = new RecommInfoCacher(mContext);
		mRecommendFolds = new SparseArray<List<Recommanditem>>();
		mRecommendLightGames = new ArrayList<Recommanditem>();
	}

	public static synchronized RecommInfoLoader getLoader(Context context) {
		if (sInstance == null) {
			sInstance = new RecommInfoLoader(context);
		}
		return sInstance;
	}

	public List<Recommanditem> loadLightGamesInfo() {
		if (mRecommendLightGames.isEmpty()) {
			JSONArray lightGamesJson = mRecommInfoCacher.getGamesJson();
			refreshGamesInfo(lightGamesJson);
		}
		return mRecommendLightGames;
	}

	public SparseArray<List<Recommanditem>> loadFoldsInfo() {
		if (mRecommendFolds.size() == 0) {
			JSONArray foldsJson = mRecommInfoCacher.getFoldsJson();
			refreshFoldsInfo(foldsJson);
		}
		return mRecommendFolds;
	}

	public void refreshFoldsInfo(JSONArray foldsJson) {
		mRecommendFolds.clear();
		try {
			if (foldsJson != null) {
				int length = foldsJson.length();
				JSONObject obj = null;
				List<Recommanditem> items = null;
				int foldId = 0;
				JSONArray apps = null;
				JSONObject app = null;
				int appsLength = 0;
				for (int i = 0; i < length; i++) {
					obj = foldsJson.getJSONObject(i);
					items = new ArrayList<Recommanditem>();
					foldId = obj.optInt("foldid");
					apps = obj.optJSONArray("apps");
					appsLength = apps.length();
					Recommanditem item = null;
					for (int j = 0; j < appsLength; j++) {
						app = apps.optJSONObject(j);
						item = new Recommanditem();
						item.setRecommType(Recommanditem.TYPE_APP);
						item.setMapId(app.optInt("mapid"));
						item.setAppName(app.optString("appname"));
						item.setIconUrl(app.optString("icon"));
						item.setVersion(app.optString("version"));
						item.setAppSize(app.optString("size"));
						item.setDisplayTime(app.optInt("displaytime"));
						item.setDownloadType(app.optInt("downloadtype"));
						item.setDisplayTimeBegin(app
								.optString("displaytimebegin"));
						item.setDisplayTimeEnd(app.optString("displaytimeend"));
						item.setDownloadUrl(app.optString("downloadurl"));
						item.setCBackUrl(app.optString("cbackurl"));
						item.setFolderType(foldId);
						item.setIconLocalUrl(RecommIconPreLoader.getServer(
								mContext).url2LocalPath(item.getIconUrl()));
						items.add(item);
					}
					mRecommendFolds.put(foldId, items);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshGamesInfo(JSONArray lightGamesJson) {
		mRecommendLightGames.clear();
		PreferencesManager preferencesManager = new PreferencesManager(
				mContext, IPreferencesIds.SMART_CARD_LIGHT_GAME_SHOW_END_TIME,
				Context.MODE_PRIVATE);
		try {
			if (lightGamesJson != null) {
				int foldId = GLAppFolderInfo.TYPE_RECOMMAND_GAME;
				JSONObject app = null;
				int appsLength = 0;
				appsLength = lightGamesJson.length();
				Recommanditem item = null;
				for (int j = 0; j < appsLength; j++) {
					app = lightGamesJson.optJSONObject(j);
					item = new Recommanditem();
					item.setRecommType(Recommanditem.TYPE_LIGHTGAME);
					item.setAppName(app.optString("name"));
					item.setIconUrl(app.optString("icon"));
					item.setDisplayTime(app.optLong("displaytime"));
					item.setDisplayTimeBegin(app.optString("displaytimebegin"));
					item.setDisplayTimeEnd(app.optString("displaytimeend"));
					item.setDownloadUrl(app.optString("url"));
					item.setMapId(app.optInt("mapid"));
					item.setFolderType(foldId);
					item.setSummary(app.optString("summary"));
					long showEndTime = preferencesManager.getLong(
							String.valueOf(item.getMapId()), 0);
					if (showEndTime == 0) {
						showEndTime = System.currentTimeMillis()
								+ item.getDisplayTime();
					}
					item.setIconLocalUrl(RecommIconPreLoader.getServer(
							mContext).url2LocalPath(item.getIconUrl()));
					item.setShowEndTime(showEndTime);
					mRecommendLightGames.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
