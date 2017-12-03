package com.jiubang.ggheart.zeroscreen.search.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.jb.util.pySearch.SearchResultItem;
import com.jiubang.ggheart.apps.appfunc.controler.AppConfigControler;
import com.jiubang.ggheart.apps.desks.appfunc.help.FuncSearchUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.zeroscreen.search.bean.OnSearchListener;
import com.jiubang.ggheart.zeroscreen.search.bean.SearchResultInfo;
import com.jiubang.ggheart.zeroscreen.search.contact.util.ContactsSearchUtils;

/**
 * 搜索工具类
 * @author liulixia
 *
 */
//CHECKSTYLE:OFF
public class SearchUtils {
	private static SearchUtils mSearchUtils = null;
	private Context mContext;
	private OnSearchListener mSearchListener = null;
	//屏蔽掉最佳匹配规则 零屏二期
//	private SearchResultInfo mTotalMatchInfo = null; //完成匹配或相似度最高对象
	private FuncSearchUtils mFuncSearchUtils = null;
	private ContactsSearchUtils mContactsSearchUtils = null;
	private List<SearchResultInfo> mAllContacts = null;
	private ArrayList<SearchResultInfo> mSearchResults = null;
	private String mSearchText = null;
	private boolean mIsSearching = false;
	private String mContactTitle;
	private String mAppTitle;
//	private String mBestMatchContact;
//	private String mBestMatchApp;
	private String mMoreContacts;
	
	
	public static synchronized SearchUtils getInstance(Context context) {
		if (mSearchUtils == null) {
			mSearchUtils = new SearchUtils(context);
		}
		return mSearchUtils;
	}
	
	private SearchUtils(Context context) {
		mContext = context;
		mContactsSearchUtils = ContactsSearchUtils.getInstance(mContext);
		mFuncSearchUtils = FuncSearchUtils.getInstance(mContext);
/*		if (!ShellPluginFactory.isUseShellPlugin(context)) {
			mContactTitle = mContext.getResources().getString(R.string.zero_screen_search_local_contact);
			mAppTitle = mContext.getResources().getString(R.string.zero_screen_search_local_app);
//		mBestMatchContact = mContext.getResources().getString(R.string.zero_screen_search_local_best_match_contact);
//		mBestMatchApp = mContext.getResources().getString(R.string.zero_screen_search_local_best_match_app);
			mMoreContacts = mContext.getResources().getString(R.string.zero_screen_search_local_more_contacts);
		}*/
	}
	
	public void setTitiles(String contactTitle, String appTitle, String moreContacts) {
		mContactTitle = contactTitle;
		mAppTitle = appTitle;
		mMoreContacts = moreContacts;
	}
	
	public void setSearchListener(OnSearchListener listener) {
		mSearchListener = listener;
	}
	
	/**
	 * 搜索本地数据
	 * @param key
	 * @param isSearchInitial
	 */
	public void searchLocalResources(final String key, final boolean isSearchInitial) {
//		Log.e("llx", "SearchUtils<<<<<<<<<<<<<<<<<<<<<<查询, key = " + key + ", searchText = " + mSearchText + ",mIsSearching = " + mIsSearching);
		if (mSearchText != null && key.equals(mSearchText) 
				|| mIsSearching) {
			return;
		}
		
		if (key == null || key.equals("")) {
			mSearchText = key;
			if (mSearchListener != null) {
				mSearchListener.onRefreshList(null);
			}
			return;
		}
		
		mIsSearching = true;
		
		new Thread() {
			public void run() {
				mSearchText = key;
				
				if (mSearchResults != null) {
					mSearchResults.clear();
				}
				mAllContacts = mContactsSearchUtils.getPersonInfo(key);
//				mTotalMatchInfo = mContactsSearchUtils.getTotalMatchInfo();
				List<SearchResultInfo> apps = searchApps(key, isSearchInitial);
//				Log.i("llx", "SearchUtils................扫描app花费的时间为：" + (System.currentTimeMillis() - start) + ", mSearchText = " + mSearchText);
				
				//汇总所有查询到的信息
				if (mSearchResults == null) {
					mSearchResults = new ArrayList<SearchResultInfo>();
				}
				
				//找到最佳匹配项，二期屏蔽掉
/*				if (mTotalMatchInfo == null) {
					SearchResultInfo info = null;
					if (mAllContacts != null && mAllContacts.size() > 0) {
						info = mAllContacts.get(0);
						info.mType = SearchResultInfo.ITEM_TYPE_CONTACTS;
						info.mShowBottomLine = false;
						mTotalMatchInfo = info;
						mAllContacts.remove(0);
					} else if (apps != null && apps.size() > 0) {
						info = apps.get(0);
						info.mType = SearchResultInfo.ITEM_TYPE_APP;
						info.mShowBottomLine = false;
						mTotalMatchInfo = info;
						apps.remove(0);
					}
				}
				if (mTotalMatchInfo == null) {
					//没有最佳匹配项说明没有查询信息
					
				} else {
					SearchResultInfo info = null;
					info = new SearchResultInfo();
					info.mType = SearchResultInfo.ITEM_TYPE_TITLE;
					if (mTotalMatchInfo.mType == SearchResultInfo.ITEM_TYPE_CONTACTS) {
						info.mTitle = mBestMatchContact;
					} else {
						info.mTitle = mBestMatchApp;
					}
					mSearchResults.add(info);
					
					mSearchResults.add(mTotalMatchInfo);
					if (mAllContacts != null && mAllContacts.size() > 0) {
						//加上联系人信息头
						info = new SearchResultInfo();
						info.mType = SearchResultInfo.ITEM_TYPE_TITLE;
						info.mTitle = mContactTitle;
						mSearchResults.add(info);
						
						//只显示三个联系人
						if (mAllContacts.size() > 3) {
							for (int i = 0; i < 3; i++) {
								info = mAllContacts.get(i);
								mSearchResults.add(info);
							}
							//查看更多信息
							info = new SearchResultInfo();
							info.mType = SearchResultInfo.ITEM_TYPE_GET_MROE_CONTACTS;
							info.mTitle = mMoreContacts;
							mSearchResults.add(info);
						} else {
							mSearchResults.addAll(mAllContacts);
						}
					}
					
					if (apps != null && apps.size() > 0) {
						//加上应用信息头
						info = new SearchResultInfo();
						info.mType = SearchResultInfo.ITEM_TYPE_TITLE;
						info.mTitle = mAppTitle;
						mSearchResults.add(info);
						
						mSearchResults.addAll(apps);
					}
				}*/
				
				SearchResultInfo info = null;
				if (mAllContacts != null && mAllContacts.size() > 0) {
					//加上联系人信息头
					info = new SearchResultInfo();
					info.mType = SearchResultInfo.ITEM_TYPE_TITLE;
					info.mTitle = mContactTitle;
					mSearchResults.add(info);
					
					//有应用时只显示4个联系人
					if (apps != null && apps.size() > 0 && mAllContacts.size() > 4) {
						for (int i = 0; i < 4; i++) {
							info = mAllContacts.get(i);
							mSearchResults.add(info);
						}
						//查看更多信息
						info = new SearchResultInfo();
						info.mType = SearchResultInfo.ITEM_TYPE_GET_MROE_CONTACTS;
						info.mTitle = mMoreContacts;
						mSearchResults.add(info);
					} else {
						mSearchResults.addAll(mAllContacts);
					}
				}
				
				if (apps != null && apps.size() > 0) {
					//加上应用信息头
					info = new SearchResultInfo();
					info.mType = SearchResultInfo.ITEM_TYPE_TITLE;
					info.mTitle = mAppTitle;
					mSearchResults.add(info);
					
					mSearchResults.addAll(apps);
				}
//				Log.v("llx", ".........总共查询数目.........." + mSearchResults.size());
				mIsSearching = false;
				if (mSearchListener != null) {
					mSearchListener.onSearchFinish(mSearchText, mSearchResults);
				}
			};
		}.start();
	}
	
	/**
	 * 查看更多联系人
	 */
	public void showMoreContacts() {
		if (mSearchResults != null && mSearchResults.size() > 0) {
			int index = -1;
			for (int i = 0; i < mSearchResults.size(); i++) {
				SearchResultInfo result = mSearchResults.get(i);
				if (result.mType == SearchResultInfo.ITEM_TYPE_GET_MROE_CONTACTS) {
					index = i;
					break;
				}
			}
			if (index != -1 && mAllContacts != null && mAllContacts.size() > 4) {
				mSearchResults.remove(index);
				for (int i = (mAllContacts.size() - 1); i > 3; i--) {
					mSearchResults.add(index, mAllContacts.get(i));
				}
			}
		}
		
		if (mSearchListener != null) {
			mSearchListener.onRefreshList(mSearchResults);
		}
	}
	
	private ArrayList<SearchResultInfo> searchApps(String key, boolean isSearchInitial) {
		AppConfigControler appConfigControler = AppConfigControler.getInstance(mContext);
		ArrayList<AppItemInfo> appData = AppDataEngine.getInstance(mContext).getAllAppItemInfos();
		//TODO 如果搜索时还未拿到扫描数据
		
		ArrayList<SearchResultInfo> resultItems = new ArrayList<SearchResultInfo>();
		// 过滤掉隐藏程序,其实就是把隐藏程序从列表中删除
		Iterator<AppItemInfo> it = appData.iterator();
		while (it.hasNext()) {
			AppItemInfo item = it.next();
			if (appConfigControler.isHideApp(item.mIntent)) {
				it.remove();
			}
		}
		searchLocalApps(resultItems, key, appData, isSearchInitial);
		
		//二期屏蔽掉最佳匹配
/*		ArrayList<AppItemInfo> recentApps = AppDrawerControler.getInstance(mContext).getRecentAppItems();
		ArrayList<SearchResultInfo> sortResultItems = resultItems;
		if (recentApps != null && recentApps.size() > 0 
				&& resultItems.size() > 0) {
			sortResultItems = new ArrayList<SearchResultInfo>(resultItems.size());
			AppItemInfo recentInfo = null;
			SearchResultInfo resultInfo = null;
			int length = recentApps.size() - 1;
			for (int i = length; i > -1; i--) {
				recentInfo = recentApps.get(i);
				for (int j = 0; j < resultItems.size(); j++) {
					resultInfo = resultItems.get(j);
					if (recentInfo.mIntent.equals(resultInfo.mIntent)) {
						sortResultItems.add(0, resultInfo);
						resultItems.remove(j);
						break;
					}
				}
			}
			
			sortResultItems.addAll(resultItems);
		}
		return sortResultItems;*/
		
		return resultItems;
	}
	
	private void searchLocalApps(ArrayList<SearchResultInfo> resultItems, String key,
			List<AppItemInfo> appData, boolean isSearchInitial) {
		if (appData == null) {
			return;
		}
		Drawable tmpDrawable = null;
		BitmapDrawable tmpBitmapDrawable = null;
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		SearchResultInfo resultItem;
		for (AppItemInfo itemInfo : appData) {
			if (itemInfo.mTitle != null) {
				String searchKey = itemInfo.mTitle;
				if (isSearchInitial) {
					if (searchKey == null || searchKey.length() < 1) {
						continue;
					}
					searchKey = searchKey.substring(0, 1);
				}
				SearchResultItem item = mFuncSearchUtils.match(key, searchKey);
				if (item != null && item.mMatchValue > 0) {
					resultItem = new SearchResultInfo();
					resultItem.mType = SearchResultInfo.ITEM_TYPE_APP;
					resultItem.mIcon = itemInfo.mIcon;
					tmpDrawable = resultItem.mIcon;
					if (tmpDrawable instanceof BitmapDrawable) {
						// 需要设置icon的tensity
						tmpBitmapDrawable = (BitmapDrawable) tmpDrawable;
						tmpBitmapDrawable.setTargetDensity(displayMetrics);
					}
					resultItem.mIntent = itemInfo.mIntent;
					resultItem.setTitle(itemInfo.mTitle, item);
					//找到完全匹配对象，屏蔽掉最佳匹配
/*					if (key.equals(itemInfo.mTitle) && mTotalMatchInfo == null) {
						resultItem.mType = SearchResultInfo.ITEM_TYPE_APP;
						resultItem.mShowBottomLine = false;
						mTotalMatchInfo = resultItem;
					} else {
						resultItems.add(resultItem);
					}*/
					resultItems.add(resultItem);
				}
			}
		}
	};
	
	/**
	 * 保存历史搜索词条
	 */
	public void saveSearchText() {
		if (mSearchText == null || mSearchText.equals("")
				|| mSearchText.trim().equals("")) {
			return;
		}
//		SharedPreferences sp = mContext.getSharedPreferences(IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH, Context.MODE_PRIVATE);
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH, Context.MODE_PRIVATE);
		String historyText = manager.getString(IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH_HISTORY_WORDS, "");
		
		if (historyText.equals("")) {
			historyText = mSearchText;
		} else {
			String[] texts = historyText.split(",");
			boolean isNeedDelete = false;
			for (int i = 0; i < texts.length; i++) {
				if (mSearchText.equals(texts[i])) {
					if (i == 0) { //是历史记录第一项，不用删除
						return;
					} else {
						isNeedDelete = true;
						break;
					}
				}
			}
			
			
			String other = "";
			if (isNeedDelete) {
				if (historyText.endsWith(mSearchText)) {
					int lastIndex = historyText.lastIndexOf(",");
					other = historyText.substring(0, lastIndex);
				} else {
					int index = historyText.indexOf(mSearchText + ",");
					int length = mSearchText.length() + 1;
					String preString = historyText.substring(0, index);
					String pixString = historyText.substring(index + length); 
					other = preString + pixString;
				}
			} else {
				other = historyText;
			}
			historyText = mSearchText + "," + other;
			String[] items = historyText.split(",");
			if (items.length > 5) {
				int lastIndex = historyText.lastIndexOf(",");
				historyText = historyText.substring(0, lastIndex);
			}
		}
		manager.putString(IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH_HISTORY_WORDS, historyText);
		manager.commit();
		
		if (mSearchListener != null) {
			mSearchListener.onReloadHistoryText(true, historyText);
		}
	}
	
	/**
	 * 获取历史搜索词条
	 * @return
	 */
	public String getHistorySearchText() {
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH, Context.MODE_PRIVATE);
		String historyText = manager.getString(IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH_HISTORY_WORDS, "");
		return historyText;
	}
	
	/**
	 * 清除历史搜索词条
	 * @param text
	 */
	public void deleteHistorySearchText(String text) {
		if (text.equals("")) {
			return;
		}
		PreferencesManager manager = new PreferencesManager(mContext,
				IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH, Context.MODE_PRIVATE);
		boolean isChange = false;
		String saveText = "";
		if (text.equals("*deleteAllText*")) {
			isChange = true;
			manager.putString(IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH_HISTORY_WORDS, "");
			manager.commit();
		} else {
			String historyText = manager.getString(IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH_HISTORY_WORDS, "");
			if (historyText.contains(text)) {
				int index = 0;
				int length = 0;
				if (historyText.contains(text + ",")) {
					index = historyText.indexOf(text + ",");
					length = text.length() + 1;
				} else {
					index = historyText.indexOf(text);
					length = text.length();
				}
				String preString = historyText.substring(0, index);
				String pixString = historyText.substring(index + length); 
				saveText = preString + pixString;
				manager.putString(IPreferencesIds.PREFERENCE_ZERO_SCREEN_SEARCH_HISTORY_WORDS, saveText);
				manager.commit();
				isChange = true;
			}
		}
		
		if (isChange && mSearchListener != null) {
			mSearchListener.onReloadHistoryText(false, saveText);
		}
	}
	
	public void recyle() {
		mContactsSearchUtils.recyle();
		if (mSearchResults != null) {
			mSearchResults.clear();
		}
//		mTotalMatchInfo = null;
		if (mAllContacts != null) {
			mAllContacts.clear();
		}
		mSearchText = null;
	}
	
	public void onDestory() {
		mContactsSearchUtils.onDestory();
		mSearchUtils = null;
		mContext = null;
		if (mSearchResults != null) {
			mSearchResults.clear();
		}
//		mTotalMatchInfo = null;
		if (mAllContacts != null) {
			mAllContacts.clear();
		}
		mSearchText = null;
		removeListener();
	}
	
	public void removeListener() {
		mSearchListener = null;
	}
}
