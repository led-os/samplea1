package com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.service.wallpaper.WallpaperService;

import com.gau.go.launcherex.R;
import com.go.util.SortUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.bean.PushInfo;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.bean.WallpaperPushInfo;
import com.jiubang.ggheart.apps.desks.diy.frames.screeneditbox.tabs.push.util.ScreenEditPushConstants;

/**
 * 
 * @author zouguiquan
 *
 */
public class WallpaperOperation extends BasePushOperation {

	protected List<WallpaperPushInfo> mInstalledList;
	protected List<WallpaperPushInfo> mPushList;
	private PackageManager mPackageManager;
	private Intent mWallpaperListIntent;

	public static final String LIVE_WALLPAPER_PACKAGENAME = "com.android.wallpaper.livepicker";

	public WallpaperOperation(Context context, int pushType) {
		super(context);
		mPushType = pushType;
		mInstalledAppKey = ScreenEditPushConstants.sInstalledAppPerfix + pushType;
		mNewPushAppKey = ScreenEditPushConstants.sNewPushAppPerfix + pushType;
		mCurrentPushAppKey = ScreenEditPushConstants.sCurrentPushAppPerfix + pushType;

		mPackageManager = mContext.getPackageManager();
		mInstalledList = new ArrayList<WallpaperPushInfo>();
		mPushList = new ArrayList<WallpaperPushInfo>();
		mWallpaperListIntent = searchWallpaperListIntent();
	}

	public ArrayList<Object> loadDynamicaWallpaper() {

		if (mInstalledList == null) {
			mInstalledList = new ArrayList<WallpaperPushInfo>();
		}
		if (mPushList == null) {
			mPushList = new ArrayList<WallpaperPushInfo>();
		}
		mInstalledList.clear();
		mPushList.clear();
		mPushAllInstalled = false;

		//loadWPInstalledApp必须放在loadWPPushData前执行
		loadInstalledApp();
		loadPushData();

		//		if (mPushList != null) {
		//			log("loadDynamicaWallpaper pushList.size= " + mPushList.size());
		//		}

		if (mPushList != null && mPushList.size() == 0 && !mPushAllInstalled) {
			loadDefaultData();
			//			if (mPushList != null) {
			//				log("loadDynamicaWallpaper default.size= " + mPushList.size());
			//			}
		}

		//		if (mInstalledList != null) {
		//			log("loadDynamicaWallpaper installed.size= " + mInstalledList.size());
		//		} 

		ArrayList<Object> mergeList = mergeData(mInstalledList, mPushList);
		initServiceInfo(mergeList);
		return mergeList;
	}

	@Override
	protected void onParseInstalled(String packageName) {
		if (!checkInstalledContain(packageName)) {
			WallpaperPushInfo pushInfo = new WallpaperPushInfo();
			pushInfo.setPackageName(packageName);
			pushInfo.setHasInstall(true);
			pushInfo.setName(mPushController.getAppLable(packageName));
			mInstalledList.add(pushInfo);
		}
	}

	@Override
	protected void onParsePush(JSONObject jsonObject) {
		WallpaperPushInfo pushInfo = new WallpaperPushInfo();
		pushInfo.setPackageName(jsonObject.optString(ScreenEditPushConstants.sJsonPackage));
		pushInfo.setAppId(jsonObject.optString(ScreenEditPushConstants.sJsonAppId));
		pushInfo.setName(jsonObject.optString(ScreenEditPushConstants.sJsonName));
		pushInfo.setIconDownloadPath(jsonObject
				.optString(ScreenEditPushConstants.sJsonIconDownload));
		pushInfo.setIconResPath(jsonObject.optInt(ScreenEditPushConstants.sJsonIconRes));
		pushInfo.setDownloadurl(jsonObject.optString(ScreenEditPushConstants.sJsonDownloadUrl));
		pushInfo.setSize(jsonObject.optString(ScreenEditPushConstants.sJsonSize));
		pushInfo.setUpdatetimedesc(jsonObject.optString(ScreenEditPushConstants.sJsonUpdateTime));
		mPushList.add(pushInfo);
	}

	/**
	 * 加载还没拉取服务器推送数据前的默认列表
	 * @return
	 */
	public void loadDefaultData() {

		String packageName = null;
		WallpaperPushInfo pushInfo = null;

		pushInfo = new WallpaperPushInfo();
		packageName = "com.go.livewallpaper.Fireworks";
		pushInfo.setPackageName(packageName);
		if (GoAppUtils.isAppExist(mContext, packageName)) {
			if (!checkInstalledContain(packageName)) {
				pushInfo.setHasInstall(true);
				pushInfo.setName(mPushController.getAppLable(packageName));
				mInstalledList.add(pushInfo);
			}
		} else {
			pushInfo.setName("Love fireworks live wallpaper");
			pushInfo.setIconResPath(R.drawable.push_icon_fireworks);
			pushInfo.setDownloadurl("https://play.google.com/store/apps/details?id=com.go.livewallpaper.Fireworks");
			mPushList.add(pushInfo);
		}

		pushInfo = new WallpaperPushInfo();
		packageName = "com.go.livewallpaper.fractalclock";
		pushInfo.setPackageName(packageName);
		if (GoAppUtils.isAppExist(mContext, packageName)) {
			if (!checkInstalledContain(packageName)) {
				pushInfo.setHasInstall(true);
				pushInfo.setName(mPushController.getAppLable(packageName));
				mInstalledList.add(pushInfo);
			}
		} else {
			pushInfo.setName("LWP+Fractal clock");
			pushInfo.setIconResPath(R.drawable.push_icon_fractalclock);
			pushInfo.setDownloadurl("https://play.google.com/store/apps/details?id=com.go.livewallpaper.fractalclock");
			mPushList.add(pushInfo);
		}

		pushInfo = new WallpaperPushInfo();
		packageName = "com.jiubang.livewallpaper.wave";
		pushInfo.setPackageName(packageName);
		if (GoAppUtils.isAppExist(mContext, packageName)) {
			if (!checkInstalledContain(packageName)) {
				pushInfo.setHasInstall(true);
				pushInfo.setName(mPushController.getAppLable(packageName));
				mInstalledList.add(pushInfo);
			}
		} else {
			pushInfo.setName("Ocean wave live wallpaper");
			pushInfo.setIconResPath(R.drawable.push_icon_wave);
			pushInfo.setDownloadurl("https://play.google.com/store/apps/details?id=com.jiubang.livewallpaper.wave");
			mPushList.add(pushInfo);
		}

		pushInfo = new WallpaperPushInfo();
		packageName = "com.go.livewallpaper.nexuspro";
		pushInfo.setPackageName(packageName);
		if (GoAppUtils.isAppExist(mContext, packageName)) {
			if (!checkInstalledContain(packageName)) {
				pushInfo.setHasInstall(true);
				pushInfo.setName(mPushController.getAppLable(packageName));
				mInstalledList.add(pushInfo);
			}
		} else {
			pushInfo.setName("Nexus Pro Live wallpaper");
			pushInfo.setIconResPath(R.drawable.push_icon_nexuspro);
			pushInfo.setDownloadurl("https://play.google.com/store/apps/details?id=com.go.livewallpaper.nexuspro");
			mPushList.add(pushInfo);
		}
	}

	public boolean checkInstalledContain(String pkgName) {
		for (WallpaperPushInfo pushInfo : mInstalledList) {
			if (pushInfo.getPackageName().equals(pkgName)) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<Object> mergeData(List<WallpaperPushInfo> installed,
			List<WallpaperPushInfo> pushList) {
		ArrayList<Object> result = new ArrayList<Object>();
		orderByName(installed);
		result.addAll(installed);
		result.addAll(pushList);
		return result;
	}

	public void orderByName(List<WallpaperPushInfo> list) {
		if (list != null && list.size() > 0) {
			String sortMethod = "getName";
			String order = "ASC";
			SortUtils.sort(list, sortMethod, null, null, order);
		}
	}

	/**
	 * 设置包名对应的WallpaperInfo对象,在进行壁纸预览时要用该对象
	 * @param dataList
	 * @return
	 */
	@SuppressLint("NewApi")
	public void initServiceInfo(List<Object> dataList) {

		if (dataList == null || dataList.size() == 0) {
			return;
		}

		List<ResolveInfo> list = mPackageManager.queryIntentServices(new Intent(
				WallpaperService.SERVICE_INTERFACE), PackageManager.GET_META_DATA);

		if (list != null) {
			for (ResolveInfo info : list) {

				WallpaperInfo wallpaperInfo;
				try {
					wallpaperInfo = new WallpaperInfo(mContext, info);
				} catch (XmlPullParserException e) {
					continue;
				} catch (IOException e) {
					continue;
				}

				String packageName = wallpaperInfo.getPackageName();

				for (Object obj : dataList) {
					WallpaperPushInfo pushInfo = (WallpaperPushInfo) obj;
					if (pushInfo.getPackageName() != null
							&& packageName.equals(pushInfo.getPackageName())) {
						log("initServiceInfo packageName= " + packageName);
						pushInfo.setWallpaperInfo(wallpaperInfo);
					}
				}
			}
		}
	}

	private Intent searchWallpaperListIntent() {
		Intent result = null;
		Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER, null);
		List<ResolveInfo> list = mPackageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : list) {
			if (resolveInfo.activityInfo == null) {
				continue;
			}

			String pkgName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;
			if (pkgName != null && pkgName.equals(LIVE_WALLPAPER_PACKAGENAME)) {
				if (className != null) {
					result = new Intent();
					result.setComponent(new ComponentName(pkgName, className));
					log("searchWallpaperListIntent pkgName= " + pkgName + " className= "
							+ className);
				}
			}
		}
		return result;
	}

	public Intent getWallpaperListIntent() {
		return mWallpaperListIntent;
	}

	public void saveCurrentPushApp() {
		if (mPushList == null || mPushList.size() == 0) {
			return;
		}

		JSONArray jsonArray = new JSONArray();
		for (PushInfo info : mPushList) {
			JSONObject jsonObject = mPushController.createJSONObject(info.getPackageName());
			jsonArray.put(jsonObject);
		}

		mPushController.saveCurrentPushApp(jsonArray, mCurrentPushAppKey);
	}
}
