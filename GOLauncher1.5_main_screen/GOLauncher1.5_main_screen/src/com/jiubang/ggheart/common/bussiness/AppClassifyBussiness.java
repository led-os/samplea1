package com.jiubang.ggheart.common.bussiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import android.content.Context;
import android.util.SparseArray;

import com.gau.go.launcherex.R;
import com.go.util.device.Machine;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.bussiness.BaseBussiness;
import com.jiubang.ggheart.common.bussiness.AppClassifyOnlineHandler.RequestAppClassifyDataListener;
import com.jiubang.ggheart.common.data.AppClassifyDataModel;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;

/**
 * 应用分类事务类
 */
public class AppClassifyBussiness extends BaseBussiness implements RequestAppClassifyDataListener {
	public static final int NO_CLASSIFY_APP = -1;
	public static final int PERSONALIZATION = 0;
	public static final int LEARNING = 1;
	public static final int GAMES = 2;
	public static final int MEDIA = 3;
	public static final int NETWORK = 4;
	public static final int TOOLS = 5;
	public static final int LIFESTYLE = 6;
	public static final int SOCIAL = 7;
	public static final int MAP = 8;
	public static final int BUSINESS = 9;
	public static final int READING = 10;
	public static final int SYSTEM_APP = 11;
	public static final int SHOPPING = 12;
	public static final int FINANCE = 13;
	
	private AppClassifyDataModel mDataModel;
	
	private AppClassifyOnlineHandler mOnlineArrangeHandler;
	
//	private ArragneAppListener mArragneAppListener;
	
	private HashMap<Integer, ArragneAppListener> mArragneAppListeners;
	
	private boolean mIsAppClassifyLoadFinish;
	
	public static Object sClassifyLock;
	
	public AppClassifyBussiness(Context context) {
		super(context);
		mDataModel = new AppClassifyDataModel(context);
		mArragneAppListeners = new HashMap<Integer, AppClassifyBussiness.ArragneAppListener>();
		mOnlineArrangeHandler = new AppClassifyOnlineHandler();
		mOnlineArrangeHandler.setRequestArrangeAppListener(this);
		sClassifyLock = new Object();
	}
	
	/**
	 * 查询多个应用分类(初始化时分类)
	 * 注意：查询数据库进行匹配
	 * @param infoList
	 */
	@SuppressWarnings("unchecked")
	public void initAllAppClassify() {
		// 这一段如果耗时，可考虑异步，但要注意调用isAppClassifyLoadFinish的返回值
		// 不使用异步的话，会令EVENT_LOAD_FINISH广播的迟点发送
		// 无论如何都要先查一下本地数据
		HashMap<String, Object> classifiedApps = mAppDataEngine.getAppsMap();
		classifyAppitemInfos(classifiedApps);
		PreferencesManager preferencesManager = new PreferencesManager(mContext);
		long currentTime = System.currentTimeMillis();
		long lastUpateTime = preferencesManager.getLong(IPreferencesIds.APP_CLASSIFY_DB_UPDATE_TIME, -1);
		if (lastUpateTime == -1 || currentTime - lastUpateTime > 1000 * 60 * 60 * 24 * 7) {
			if (Machine.isNetworkOK(mContext)) {
				requestUpdateAppClassifyDB();
			}
		}
	}

	/**
	 * 查询一个应用info分类（安装新应用时调用）
	 * 注意：查询数据库进行匹配
	 * @param Info
	 */
	public void queryAppClassify(AppItemInfo Info) {
		String pkName = Info.mIntent.getComponent().getPackageName();
		if (pkName != null) {
			HashMap<String, Integer> result = mDataModel.getAppClassify(pkName);
			if (!result.isEmpty()) {
				Info.mClassification = result.get(pkName);
			}
		}
	}
	
	/**
	 * 查询一个应用分类（安装新应用时调用，速度相对较快）
	 * 注意：查询数据库进行匹配
	 * @param pkgName 包名
	 * @param infoList 该包名对应的info列表
	 */
	public void queryAppClassify(String pkgName, ArrayList<AppItemInfo> infoList) {
		if (pkgName != null) {
			HashMap<String, Integer> result = mDataModel.getAppClassify(pkgName);
			if (!result.isEmpty()) {
				for (AppItemInfo appItemInfo : infoList) {
					appItemInfo.mClassification = result.get(pkgName);
				}
			}
		}
	}
	
	/**
	 * 查询多个应用info的分类（安装新应用时调用）
	 * 注意：查询数据库进行匹配
	 * @param infoList
	 */
	public void queryAppsClassify(ArrayList<AppItemInfo> infoList) {
		// 把list转化成我们可供查询数据库的数据结构
		if (infoList != null) {
			HashMap<String, Object> pkgMap = new HashMap<String, Object>();
			for (AppItemInfo appItemInfo : infoList) {
				String pkg = appItemInfo.getAppPackageName();
				if (pkg != null) {
					if (pkgMap.containsKey(pkg)) {
						Object obj = pkgMap.get(pkg);
						if (obj instanceof ArrayList<?>) {
							((ArrayList<AppItemInfo>) obj).add(appItemInfo);
						} else if (obj instanceof AppItemInfo) {
							ArrayList<AppItemInfo> samePkgInfo = new ArrayList<AppItemInfo>();
							samePkgInfo.add((AppItemInfo) obj);
							samePkgInfo.add(appItemInfo);
							pkgMap.put(pkg, samePkgInfo);
						}
					} else {
						pkgMap.put(pkg, appItemInfo);
					}
				}
			}
			classifyAppitemInfos(pkgMap);
		}
	}

	/**
	 * 把转换好的map进行分类查询配对，并把结果设在appitemInfo的classification属性里
	 * @param pkgMap 以包名作为key的map
	 */
	private void classifyAppitemInfos(HashMap<String, Object> pkgMap) {
		HashMap<String, Integer> result = mDataModel.getAllAppClassifyItems(pkgMap.keySet());
		classifyAppitemInfos(pkgMap, result);
	}
	
	/**
	 * 把转换好的map进行分类查询配对，并把结果设在appitemInfo的classification属性里
	 * @param pkgMap 以包名作为key的map
	 * @param result 用别的结果源做分类
	 */
	private void classifyAppitemInfos(HashMap<String, Object> pkgMap, HashMap<String, Integer> result) {
		if (!pkgMap.isEmpty() && !result.isEmpty()) {
			synchronized (sClassifyLock) {
				Iterator<Entry<String, Object>> iter = pkgMap.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Object> entry = iter.next();
					String pkg = entry.getKey();
					Integer integer = result.get(pkg);
					if (integer != null) {
						int classification = integer.intValue();
						Object obj = entry.getValue();
						if (obj instanceof AppItemInfo) {
							((AppItemInfo) obj).mClassification = classification;
						} else if (obj instanceof ArrayList<?>) {
							ArrayList<AppItemInfo> info = (ArrayList<AppItemInfo>) obj;
							for (AppItemInfo appItemInfo : info) {
								appItemInfo.mClassification = classification;
							}
						}
					} else {
						// 提供的查询结果当中，没有关于该包名的分类
						// 也没啥要干的，应用原来已经被分类到哪里去的就到哪里去，不要做重置分类的操作
					}
				}
				
			}
		}
		mIsAppClassifyLoadFinish = true;
	}
	
	public String getFolderName(int classification) {
		if (classification == NO_CLASSIFY_APP) {
			return mContext.getResources().getString(R.string.folder_name);
		}
		
		HashMap<Integer, HashMap<String, String>> data = mDataModel.getFolderName(classification);
		HashMap<String, String> names = data.get(classification);
		PreferencesManager preferences = new PreferencesManager(mContext,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		String currentlanguage = preferences.getString(IPreferencesIds.CURRENT_SELECTED_LANGUAGE, "");
		String result = null;
		if (currentlanguage.equals("")) {
			Locale locale = Locale.getDefault();
			result = names.get(locale.getLanguage().toLowerCase(locale));
			if (result == null) {
				currentlanguage = String.format("%s_%s", locale.getLanguage().toLowerCase(locale), locale.getCountry()
						.toUpperCase(locale));
				result = names.get(currentlanguage);
			}
		}
		if (result == null) {
			result = names.get("en_US");
		}
		return result;
//		String[] classifyNames = mContext.getResources().getStringArray(
//				R.array.folder_classify_name);
//		if (classification < 0 || classifyNames.length <= classification) {
//			return mContext.getResources().getString(R.string.folder_name);
//		}
//		return classifyNames[classification];
	}
	
	/**
	 * 根据分类产生一个默认文件夹名称
	 * @return
	 */
	public String generateFolderName(ArrayList<AppItemInfo> infoList) {
		int classification = generateFolderType(infoList);
		return getFolderName(classification);
	}
	
	public int generateFolderType(ArrayList<AppItemInfo> infoList) {
		// 选择出现最多的类型名字
//		使用了稀疏数组，具体对该算法有速度的提升？
		SparseArray<ArrayList<AppItemInfo>> map = generateClassifyMap(infoList);
		int size = map.size();
		int max = 1;
		int classification = NO_CLASSIFY_APP;
		for (int i = 0; i < size; i++) {
			int cur = map.valueAt(i).size();
			if (cur >= max) { //如果只有一个元素，这里需要=
				max = cur;
				classification = map.keyAt(i);
			}
		}
		return classification;
	}

	/**
	 * 把传入列表分类并转化数据结构（智能分类）
	 * @param infoList
	 * @return map <BR/>
	 * ［分类名ID， 应用列表对象］ <BR/>
	 * ［游戏 ， ArrayList<AppItemInfo>］ <BR/>
	 * ［社交 ， ArrayList<AppItemInfo>］ <BR/>
	 * ［垃圾 ， ArrayList<AppItemInfo>］ <BR/>
	 */
	public SparseArray<ArrayList<AppItemInfo>> generateClassifyMap(ArrayList<AppItemInfo> infoList) {
		SparseArray<ArrayList<AppItemInfo>> map = new SparseArray<ArrayList<AppItemInfo>>();
//		HashMap<Integer, ArrayList<AppItemInfo>> map = new HashMap<Integer, ArrayList<AppItemInfo>>();
		for (AppItemInfo appItemInfo : infoList) {
			if (appItemInfo != null) {
				int classification = appItemInfo.mClassification;
				if (classification != NO_CLASSIFY_APP) { // -1表示没有分类
					ArrayList<AppItemInfo> sameClassifyApps = map.get(classification);
					if (sameClassifyApps != null) { 
						sameClassifyApps.add(appItemInfo);
			       		map.put(classification, sameClassifyApps);
			       	} else {
			       		sameClassifyApps = new ArrayList<AppItemInfo>();
			       		sameClassifyApps.add(appItemInfo);
			       		map.put(classification, sameClassifyApps);
					}
				}
			}
	    }
		return map;
	}
	
	/**
	 * 智能分类(访问网络版)
	 * @param infoList
	 */
	public void queryAppsClassify(ArrayList<AppItemInfo> infoList, ArragneAppListener listener, int entrance, boolean needOnLine) {
		if (needOnLine && Machine.isNetworkOK(mContext)) {
			mArragneAppListeners.put(AppClassifyOnlineHandler.ARRANGE_APP_FUN_ID, listener);
			requestArrangeApp(infoList, entrance);
		} else {
			//没有网络就直接回调结束
			listener.onArrangeFinish(AppClassifyOnlineHandler.ARRANGE_APP_FUN_ID);
			GuiThemeStatistics.arrangeAppStaticData("1", "sort", entrance + "");
		}
	}
	
	private void requestArrangeApp(ArrayList<AppItemInfo> appList, int entrance) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		String pkgNames = getPkgNamesString(appList);
		AppClassifyOnlineHandler.log(pkgNames);
		hashMap.put("pkgs", pkgNames);
		hashMap.put("mark", "2");
		mOnlineArrangeHandler.requestAppClassifyData(AppClassifyOnlineHandler.ARRANGE_APP_FUN_ID, hashMap, entrance);
	}

	private void requestUpdateAppClassifyDB() {
//		if (Machine.isNetworkOK(mContext)) {
			mOnlineArrangeHandler.requestAppClassifyData(AppClassifyOnlineHandler.UPDATE_DB_FUN_ID, new HashMap<String, String>(), 0);
//		}
	}
	
	private String getPkgNamesString(ArrayList<AppItemInfo> appList) {
		StringBuffer buffer = new StringBuffer();
		for (AppItemInfo appItemInfo : appList) {
			//过滤系统内置的app
			if (appItemInfo.getIsSysApp()) {
				continue;
			}
			String pkgName = appItemInfo.getAppPackageName();
			buffer.append(pkgName);
			buffer.append(",");
			buffer.append(appItemInfo.getTitle());
			buffer.append("#");
		}
		return buffer.toString();
	}
	
	/**
	 * 
	 * @author dingzijian
	 *
	 */
	public interface ArragneAppListener {
		public void onArrangeFinish(int funId);

		public void onArrangeException(int funId);
	}
	
	public boolean isAppClassifyLoadFinish() {
		return mIsAppClassifyLoadFinish;
	}

	@Override
	public void onRequestException(int entrance, int funId) {
		ArragneAppListener listener = mArragneAppListeners.remove(funId);
		if (listener != null) {
			listener.onArrangeException(funId);
		}
		GuiThemeStatistics.arrangeAppStaticData("1", "sort", entrance + "");
	}

	@Override
	public void onRequsetFinish(HashMap<String, Integer> appInfos, HashMap<Integer, HashMap<String, String>> folderMap, int entrance,
			int funId) {
		HashMap<String, Object> pkgMap = mAppDataEngine.getAppsMap();
		classifyAppitemInfos(pkgMap, appInfos);
		if (funId == AppClassifyOnlineHandler.UPDATE_DB_FUN_ID) {
			// 异步都可以
			mDataModel.updateAppClassifyDB(appInfos, folderMap);
			PreferencesManager preferencesManager = new PreferencesManager(mContext);
			preferencesManager.putLong(IPreferencesIds.APP_CLASSIFY_DB_UPDATE_TIME,
					System.currentTimeMillis());
			preferencesManager.commit();
		}
		ArragneAppListener listener = mArragneAppListeners.remove(funId);
		if (listener != null) {
			listener.onArrangeFinish(funId);
		}
		GuiThemeStatistics.arrangeAppStaticData("2", "sort", entrance + "");
	}
	
	
}
