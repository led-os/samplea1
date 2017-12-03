package com.jiubang.ggheart.data.info;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.go.proxy.ApplicationProxy;
import com.go.util.AppUtils;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.appgame.base.manage.LruImageCache;
import com.jiubang.ggheart.common.bussiness.AppClassifyBussiness;
import com.jiubang.ggheart.common.data.AppExtraAttribute;
import com.jiubang.ggheart.launcher.ICustomAction;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 轻游戏图标info
 * @author yangguanxiang
 *
 */
public class LightGameAppItemInfo extends SpecialAppItemInfo {

	public final static String KEY_DOWNLOAD_URL = "downloadUrl";
	public final static String KEY_TITLE = "title";
	public final static String KEY_ICON_URL = "iconUrl";
	public final static String KEY_MAP_ID = "mapId";

	private int mMapId = -1;

	private String mDownloadUrl;
	private String mIconUrl;

	public LightGameAppItemInfo() {
		mClassification = AppClassifyBussiness.GAMES;
	}

	@Override
	public boolean doInvoke(int from) {
		if (mDownloadUrl == null) {
			String data = mExtra.getData();
			if (data != null) {
				JSONObject json = null;
				try {
					json = new JSONObject(data);
					mDownloadUrl = json.getString(KEY_DOWNLOAD_URL);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		if (mDownloadUrl != null) {
			AppUtils.gotoBrowser(ApplicationProxy.getContext(), mDownloadUrl);
		}

		return true;
	}

	@Override
	public String getPackageName() {
		return PackageName.RECOMMEND_APP_PACKAGE;
	}

	@Override
	public String getAction() {
		return ICustomAction.ACTION_LIGHTGAME;
	}

	@Override
	public int getDefaultIconResId() {
		return 0;
	}

	@Override
	public int getDefaultTitleResId() {
		return 0;
	}

	@Override
	protected Intent createIntent() {
		return super.createIntent();
	}

	public void putExtra(int mapId, String title, String downloadUrl, String iconUrl) {
		mMapId = mapId;
		mTitle = title;
		mDownloadUrl = downloadUrl;
		mIconUrl = iconUrl;
		String action = combineLightGameAction(mapId);
		ComponentName component = new ComponentName(getPackageName(), action);
		mIntent.setAction(action);
		mIntent.setComponent(component);
		JSONObject json = new JSONObject();
		try {
			json.put(KEY_MAP_ID, mapId);
			json.put(KEY_TITLE, title);
			json.put(KEY_DOWNLOAD_URL, downloadUrl);
			json.put(KEY_ICON_URL, iconUrl);
			mExtra.setData(json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mExtra.setComponentName(component);
	}

	@Override
	public String getTitle() {
		if (mTitle == null || mTitle.equals(DEFAULT_TITLE)) {
			String data = mExtra.getData();
			if (data != null) {
				JSONObject json = null;
				try {
					json = new JSONObject(data);
					mTitle = json.getString(KEY_TITLE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return mTitle;
	}

	@Override
	public Drawable getSpecialAppIcon(AsyncImageLoadedCallBack callback) {
		if (mIconUrl == null) {
			String data = mExtra.getData();
			if (data != null) {
				JSONObject json = null;
				try {
					json = new JSONObject(data);
					mIconUrl = json.getString(KEY_ICON_URL);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if (mIconUrl != null) {
			Bitmap b = AsyncImageManager.getLruInstance(LruImageCache.DEFAULT_SIZE).loadImage(
					LauncherEnv.Path.SMARTCARD_RECOMMEND_ICON_PATH, String.valueOf(mIconUrl.hashCode()), mIconUrl, true, null, callback);
			if (b != null) {
				mIcon = new BitmapDrawable(ApplicationProxy.getContext().getResources(), b);
			}
		}
		return mIcon;
	}

	public int getMapId() {
		if (mMapId == -1) {
			String data = mExtra.getData();
			if (data != null) {
				JSONObject json = null;
				try {
					json = new JSONObject(data);
					mMapId = json.getInt(KEY_MAP_ID);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return mMapId;
	}

	@Override
	public void setExtraAtturibute(AppExtraAttribute extra) {
		super.setExtraAtturibute(extra);
		String data = mExtra.getData();
		if (data != null) {
			JSONObject json = null;
			try {
				json = new JSONObject(data);
				putExtra(json.getInt(KEY_MAP_ID), json.getString(KEY_TITLE),
						json.getString(KEY_DOWNLOAD_URL), json.getString(KEY_ICON_URL));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static String combineLightGameAction(int mapId) {
		return ICustomAction.ACTION_LIGHTGAME + "_" + mapId;
	}
}
