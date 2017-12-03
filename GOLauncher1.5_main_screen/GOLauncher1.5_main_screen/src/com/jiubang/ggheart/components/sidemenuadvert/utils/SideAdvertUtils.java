package com.jiubang.ggheart.components.sidemenuadvert.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.go.proxy.ApplicationProxy;
import com.go.util.AppUtils;
import com.go.util.file.FileUtil;
import com.go.util.market.MarketConstant;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppFileUtil;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreOperatorUtil;
import com.jiubang.ggheart.components.gohandbook.SharedPreferencesUtil;
import com.jiubang.ggheart.components.sidemenuadvert.SideAdvertConstants;
import com.jiubang.ggheart.components.sidemenuadvert.tools.SideToolsDataInfo;
import com.jiubang.ggheart.components.sidemenuadvert.widget.SideWidgetDataInfo;
import com.jiubang.ggheart.data.statistics.Statistics;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * @describe:侧边栏广告工具类 
 * @author by zhangxi 
 * @date [2013-09-25]
 */
public class SideAdvertUtils {

	/**
	 * <br>
	 * 功能简述:上次获取消息时服务器下发的lts值 <br>
	 * 功能详细描述:第一次获取消息该值传0 <br>
	 * 注意:
	 * 
	 * @return
	 */
	public static long getLtsString(Context context) {
		SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(context);
		String ltsString = preferencesUtil
				.getString(SideAdvertConstants.SIDE_LTS_REQUEST_TIME, "0");
		try {
			long lts = Long.parseLong(ltsString);
			return lts;
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * <br>
	 * 功能简述:保存上次获取消息时服务器下发的lts值 <br>
	 * 功能详细描述:第一次获取消息该值传0 <br>
	 * 注意:
	 * 
	 * @return
	 */
	private static void saveLtsString(Context context, String ltsString) {
		if (context != null && ltsString != null) {
			SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(context);
			preferencesUtil.saveString(SideAdvertConstants.SIDE_LTS_REQUEST_TIME, ltsString);
		}
	}

	/**
	 * <br>
	 * 功能简述:发送消息 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param handler
	 * @param what
	 * @param urlString
	 */
	public static void sendHandlerMsg(Handler handler, int what, Object urlString) {
		if (handler != null) {
			Message msg = new Message();
			msg.what = what;
			msg.obj = urlString;
			handler.sendMessage(msg);
		}
	}


	/**
	 * @describe 下载widget
	 * @param context
	 * @param strPackage
	 *            widget包名
	 * @param strGA
	 * @param strURL
	 *            widget下载路径
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	public static void downLoadWidgetApk(Context context, SideWidgetDataInfo sideWidgetDataInfo) {
		String strPackage = sideWidgetDataInfo.getWidgetPkgName();
		String strURL = sideWidgetDataInfo.getWidgetDownLoadURL();
		String strGA = sideWidgetDataInfo.getGALink();

		if (strPackage == null || strURL == null) {
			return;
		}

		String tmpGAString;
		if (strGA == null) {
			tmpGAString = new String("");
		} else {
			tmpGAString = strGA;
		}

		if (Statistics.is200ChannelUid(context)) {
			if (GoAppUtils.isMarketExist(context)) {
				GoAppUtils.gotoMarket(context, MarketConstant.APP_DETAIL + strPackage
						+ tmpGAString);
			} else {
				AppUtils.gotoBrowser(context, MarketConstant.BROWSER_APP_DETAIL + strPackage
						+ tmpGAString);
			}
		} else {
			// 未安装的去goStore下载
			// AppsDetail.gotoDetailDirectly(context,
			// AppsDetail.START_TYPE_APPRECOMMENDED, strPackage);

			// 直接下载
			GoStoreOperatorUtil.downloadFileDirectly(ApplicationProxy.getContext(),
					sideWidgetDataInfo.getTitle(), LauncherEnv.Path.SIDEMENU_DOWNLOADS_PATH,
					strURL, Long.valueOf(sideWidgetDataInfo.getPreViewName()), strPackage + ".apk");
		}
	}
	
	/**
	 * @describe 下载widget
	 * @param context
	 * @param strPackage
	 *            widget包名
	 * @param strGA
	 * @param strURL
	 *            widget下载路径
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	public static void downLoadToolsApk(Context context, SideToolsDataInfo sideToolsDataInfo) {
		String strPackage = sideToolsDataInfo.getToolsPkgName();
		String strURL = sideToolsDataInfo.getToolsDownLoadURL();
		String strGA = sideToolsDataInfo.getGALink();

		if (strPackage == null || strURL == null) {
			return;
		}

		String tmpGAString;
		if (strGA == null) {
			tmpGAString = new String("");
		} else {
			tmpGAString = strGA;
		}

		if (Statistics.is200ChannelUid(context)) {
			if (GoAppUtils.isMarketExist(context)) {
				GoAppUtils.gotoMarket(context, MarketConstant.APP_DETAIL + strPackage
						+ tmpGAString);
			} else {
				AppUtils.gotoBrowser(context, MarketConstant.BROWSER_APP_DETAIL + strPackage
						+ tmpGAString);
			}
		} else {
			// 未安装的去goStore下载
			// AppsDetail.gotoDetailDirectly(context,
			// AppsDetail.START_TYPE_APPRECOMMENDED, strPackage);

			// 直接下载
//			GoStoreOperatorUtil.downloadFileDirectly(ApplicationProxy.getContext(),
//					sideToolsDataInfo.getTitle(), LauncherEnv.Path.SIDEMENU_DOWNLOADS_PATH,
//					strURL, Long.valueOf(sideToolsDataInfo.getPreViewName()), strPackage + ".apk");
		}
	}

	/**
	 * @describe 日志工具
	 * @param content
	 * @author zhangxi
	 * @data 2013-10-9
	 */
	public static void log(String content) {
		//		Log.i("zhangxi", content);
	}

	/**
	 * @author zhangxi
	 * @param sourceList
	 *            源list
	 * @param destList
	 *            目标list
	 * @param isSame
	 *            返回相同info的list,返回相差的destlist
	 * @return
	 */
	public static List<String> getSameInfoFromLists(List<String> sourceList, List<String> destList,
			boolean isSame) {
		if (sourceList == null || destList == null) {
			return null;
		}
		List<String> sameInfoList = new ArrayList<String>();
		List<String> diffInfoList = new ArrayList<String>();
		for (String destString : destList) {
			boolean isFindSame = false;
			for (String sourceString : sourceList) {
				if (destString.equals(sourceString)) {
					sameInfoList.add(destString);
					isFindSame = true;
				}
			}
			if (!isFindSame) {
				diffInfoList.add(destString);
			}
		}
		if (isSame) {
			return sameInfoList;
		} else {
			return diffInfoList;
		}
	}

	/**
	 * @Describe 清理路径下的文件，排除intputFiles
	 * @param dirPath 需要清理的文件夹
	 * @param intputFiles 需要排除的文件
	 */
	public static void clearDirExceptInputFiles(String dirPath, final List<String> intputFiles) {
		if (dirPath == null || intputFiles == null) {
			return;
		}

		// 获取文件夹下的文件
		List<String> fileDirList = FileUtil.getDirFiles(dirPath);

		// 挑出多余的图片，并删除
		List<String> fileDelList = getSameInfoFromLists(intputFiles, fileDirList, false);
		if (fileDelList != null) {
			for (String delString : fileDelList) {
				if (delString.length() > 0) {
					FileUtil.deleteFile(delString);
				}
			}
		}
	}

	/**
	 * 
	 * @param packageArray
	 * @return 是否安装
	 * @author zhangxi
	 * @data 2013-10-10
	 */
	public static boolean isAppListExist(final String[] packageArray) {
		if (packageArray == null) {
			return false;
		}
		boolean isInstalled = false;
		for (String packageNameString : packageArray) {
			if (GoAppUtils.isAppExist(ApplicationProxy.getContext(), packageNameString)) {
				isInstalled = true;
			}
		}
		return isInstalled;
	}

	public static boolean saveSideCache(final int cacheType, final byte[] jsonbytes) {
		boolean isSaveOK = false;
		String cacheFile = "";
		String cacheBackupFile = "";

		switch (cacheType) {
			case SideAdvertConstants.SIDE_CACHE_TYPE_TOOLS :
				cacheFile = SideAdvertConstants.SIDE_TOOLS_CACHEFILE;
				cacheBackupFile = SideAdvertConstants.SIDE_TOOLS_LASTCACHE;
				break;
			case SideAdvertConstants.SIDE_CACHE_TYPE_WIDGET :
				cacheFile = SideAdvertConstants.SIDE_WIDGET_CACHEFILE;
				cacheBackupFile = SideAdvertConstants.SIDE_WIDGET_LASTCACHE;
				break;
			default :
				break;
		}
		// 更新cache
		if (FileUtil.isFileExist(cacheFile)) {
			FileUtil.copyFile(cacheFile, cacheBackupFile);
		}

		isSaveOK = FileUtil.saveByteToSDFile(jsonbytes, cacheFile);

		// 首次进入确保有lastcache，以便再次无网络进入能够显示缓存数据
		if (isSaveOK && !FileUtil.isFileExist(cacheBackupFile)) {
			FileUtil.copyFile(cacheFile, cacheBackupFile);
		}

		return isSaveOK;
	}

	public static String getSideCache(final int cacheType) {
		String cacheFile = "";
		switch (cacheType) {
			case SideAdvertConstants.SIDE_CACHE_TYPE_TOOLS :
				cacheFile = SideAdvertConstants.SIDE_TOOLS_LASTCACHE;
				break;
			case SideAdvertConstants.SIDE_CACHE_TYPE_WIDGET :
				cacheFile = SideAdvertConstants.SIDE_WIDGET_LASTCACHE;
				break;
			default :
				break;
		}

		return RecommAppFileUtil.readFileToString(cacheFile);

		//		try {
		//			String localStr = RecommAppFileUtil.readFileToString(cacheFile);
		//			if (localStr != null) {
		//				JSONObject appInfo = new JSONObject(localStr);
		//				return appInfo;
		//			}
		//		} catch (JSONException e) {
		//			e.printStackTrace();
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		//		return null;
	}

	/**
	 * @describe HashMap转ArrayList
	 * @param mHashMap
	 * @param mArrayList
	 * @author zhangxi
	 * @param <T>
	 * @data 2013-10-9
	 */
	/*public static <T> void loadMapToList(
			final Map<Integer, CopyOnWriteArrayList<T>> mHashMap,
			ArrayList<T> mArrayList) {
		if (mHashMap == null || mArrayList == null) {
			return;
		}
		Iterator iter = mHashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer ikey = (Integer) entry.getKey();
			CopyOnWriteArrayList<T> vaList = (CopyOnWriteArrayList<T>) entry
					.getValue();
			CopyOnWriteArrayList<T> tmpAppAdList = (CopyOnWriteArrayList<T>) vaList
					.clone();
			for (T sideAdvertInfo : tmpAppAdList) {
				if (sideAdvertInfo.getAdvtype() == SideAdvertAppDataInfo.SIDEADVERT_ADVTYPE_APP) {
					
					//通过包名判断下发应用广告是否安装
					boolean isFindAppExist = false;
					if (GoAppUtils.isAppExist(mContext,
							sideAdvertInfo.getPackageName())) {
						isFindAppExist = true;
					}
					//通过包别名判断下发应用广告是否安装
					if (sideAdvertInfo.getPkgalias().length() > 0) {
						String[] pkgAliasStrings = sideAdvertInfo.getPkgalias().split("#");
						if (SideAdvertUtils.isAppListExist(pkgAliasStrings)) {
							isFindAppExist = true;
						}
					}
					
					if (Statistics.is200ChannelUid(mContext)) {
						// 排除prime版的应用广告，
						if (FunctionPurchaseManager
								.getInstance(mContext.getApplicationContext())
								.getPayFunctionState(
										FunctionPurchaseManager.PURCHASE_ITEM_AD) == FunctionPurchaseManager.STATE_CAN_USE
								&& DeskSettingUtils.isNoAdvert()) {
							isFindAppExist = true;
						}
					}
				
					if (isFindAppExist) {
						tmpAppAdList.remove(sideAdvertInfo);
					}
				}
			}
			if (tmpAppAdList.size() > 0) {
				Random rd1 = new Random();
				SideAdvertInfo oneAdvertInfo;
				oneAdvertInfo = tmpAppAdList.get(rd1.nextInt(tmpAppAdList
						.size()));
				if (oneAdvertInfo != null
						&& oneAdvertInfo instanceof SideAdvertInfo) {
					mArrayList.add((SideAdvertDataInfo) oneAdvertInfo);
				}
			}
		}
	}*/
}
