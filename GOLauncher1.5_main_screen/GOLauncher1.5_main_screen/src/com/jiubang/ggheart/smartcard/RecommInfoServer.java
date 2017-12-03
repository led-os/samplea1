package com.jiubang.ggheart.smartcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.jiubang.ggheart.apps.desks.diy.SpecialAppManager;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;
import com.jiubang.ggheart.apps.desks.diy.pref.PrefConst;
import com.jiubang.ggheart.apps.desks.diy.pref.PrivatePreference;
import com.jiubang.ggheart.common.bussiness.LightAppConnector;
import com.jiubang.ggheart.common.bussiness.LightAppConnector.RequestLightAppDataListener;
import com.jiubang.ggheart.data.info.LightGameAppItemInfo;
import com.jiubang.ggheart.data.info.SpecialAppItemInfo;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderController;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderInfo;

/**
 * 
 * @author guoyiqing
 * 
 */
public class RecommInfoServer implements RequestLightAppDataListener {

	private final static long CHECK_INTERVAL = 8 * 60 * 60 * 1000; // 每隔
																	// 8小时检查一次更新
	private static final String TAG_PACKAGENAME = "packagename";
	private static final String TAG_ISFIRST = "isfirst";
	private static final String TAG_APPS = "apps";
	private static final String TAG_FOLDS = "folds";
	private static final String TAG_LIGHTGAMES = "lightgames";
	private static final String TAG_GAMES = "games";
	private static final String TAG_FOLDID = "foldid";
	private static final String RECOMMINFO_THREAD = "recomminfo_thread";
	private static final String TAG_MAPID = "mapid";
	private static RecommInfoServer sInstance;
	private Context mContext;
	private boolean mLoadingData;

	private RecommInfoServer(Context context) {
		mContext = context;
	}

	public static synchronized RecommInfoServer getServer(Context context) {
		if (sInstance == null) {
			sInstance = new RecommInfoServer(context);
		}
		return sInstance;
	}

	private void setLastCheckTime() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		pref.putLong(PrefConst.KEY_LIGHTAPP_INFO_DOWNLODE_TIME,
				System.currentTimeMillis());
		pref.commit();
	}

	private long getLastCheckTime() {
		PrivatePreference pref = PrivatePreference.getPreference(mContext);
		return pref.getLong(PrefConst.KEY_LIGHTAPP_INFO_DOWNLODE_TIME, 0);
	}

	public void loadDataAysnc() {
		final long lastCheck = getLastCheckTime();
		if (System.currentTimeMillis() - lastCheck <= CHECK_INTERVAL) {
			return;
		}
		if (mLoadingData) {
			return;
		}
		mLoadingData = true;
		new Thread(RECOMMINFO_THREAD) {
			public void run() {
				HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
				GLAppFolderController folderController = GLAppFolderController
						.getInstance();
				SparseArray<Set<String>> folderInfos = folderController
						.getFolderTypeIds();
				int size = folderInfos.size();
				if (size == 0) {
					mLoadingData = false;
					return;
				}
				List<JSONObject> jSet = new ArrayList<JSONObject>();
				JSONObject obj = null;
				boolean isFirst = lastCheck == 0;
				try {
					for (int i = 0; i < size; i++) {
						obj = new JSONObject();
						obj.put(TAG_FOLDID, folderInfos.keyAt(i));
						obj.putOpt(TAG_APPS,
								new JSONArray(pkgName2(folderInfos.valueAt(i))));
						obj.put(TAG_ISFIRST, isFirst ? 1 : 0);
						jSet.add(obj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				JSONArray jsonArray = new JSONArray(jSet);
				map.put(TAG_FOLDS, jsonArray);
				Set<String> gamePkgs = folderInfos
						.get(GLAppFolderInfo.TYPE_RECOMMAND_GAME);
				if (gamePkgs != null) {
					map.put(TAG_GAMES, new JSONArray(pkgName2(gamePkgs)));
				} else {
					map.put(TAG_GAMES, new JSONArray());
				}
				map.put(TAG_LIGHTGAMES, new JSONArray(getLightGameInfos()));
				LightAppConnector connector = new LightAppConnector();
				connector.setRequestLightAppListener(RecommInfoServer.this);
				connector.requestLightAppData(1, map);
			};
		}.start();
	}

	private List<JSONObject> getLightGameInfos() {
		List<JSONObject> list = new ArrayList<JSONObject>();
		SpecialAppManager manager = SpecialAppManager.getInstance();
		ArrayList<SpecialAppItemInfo> infos = manager
				.getAppItemInfos(ICustomAction.ACTION_LIGHTGAME);
		Set<Integer> mapIds = new HashSet<Integer>();
		if (infos != null) {
			for (SpecialAppItemInfo info : infos) {
				if (info instanceof LightGameAppItemInfo) {
					mapIds.add(((LightGameAppItemInfo) info).getMapId());
				}
			}
		}
		try {
			for (Integer integer : mapIds) {
				JSONObject obj = new JSONObject();
				obj.putOpt(TAG_MAPID, integer);
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private List<JSONObject> pkgName2(Set<String> pkgs) {
		List<JSONObject> list = new ArrayList<JSONObject>();
		try {
			for (String pkg : pkgs) {
				JSONObject obj = new JSONObject();
				obj.putOpt(TAG_PACKAGENAME, pkg);
				list.add(obj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void onRequsetFinish(JSONObject json, int funId) {
		if (json == null || json.equals("")
				|| funId != LightAppConnector.SMART_CARD_DATA) {
			return;
		}
		try {
			JSONObject result = json.getJSONObject(MessageListBean.TAG_RESULT);
			int status = result.getInt(MessageListBean.TAG_STATUS);
			if (status == ConstValue.STATTUS_OK) {
				new RecommInfoCacher(mContext).cache(json);
				RecommIconPreLoader.getServer(mContext).preLoadIconAsync();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setLastCheckTime();
		mLoadingData = false;
		Log.e("guoyiqing", "onRequsetFinish:json->" + json);
	}

	@Override
	public void onRequestException(int funId) {
		mLoadingData = false;
		Log.e("guoyiqing", "onRequestException:funId->" + funId);
	}

}
