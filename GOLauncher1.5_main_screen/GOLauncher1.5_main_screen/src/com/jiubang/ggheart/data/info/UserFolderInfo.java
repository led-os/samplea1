/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiubang.ggheart.data.info;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.appgame.base.bean.AppDetailInfoBean;
import com.jiubang.ggheart.components.folder.advert.FolderAdController;
import com.jiubang.ggheart.components.folder.advert.FolderAdDataProvider;
import com.jiubang.ggheart.data.tables.PartToScreenTable;
import com.jiubang.ggheart.data.tables.ShortcutTable;
import com.jiubang.ggheart.plugin.shell.folder.GLAppFolderInfo;

/**
 * Represents a folder containing shortcuts or apps.
 */
public class UserFolderInfo extends ScreenFolderInfo {
	/**
	 * The apps and shortcuts
	 */
	
	private final ArrayList<ItemInfo> mContents = new ArrayList<ItemInfo>();

	public Drawable mIcon = null;
	public boolean mIsUserIcon = false;
	public boolean mContentsInit = false;
	public int mOldContentCnt = 0;

	public int mTotleUnreadCount = 0; // 通讯统计2.0 文件夹支持
	public boolean mIsFirstCreate = false;
	
	private int mFolderType = GLAppFolderInfo.NO_RECOMMAND_FOLDER;
	
	private SparseArray<ArrayList<AppDetailInfoBean>> mFolderAdDataArray;
	
	public UserFolderInfo() {
		mItemType = IItemType.ITEM_TYPE_USER_FOLDER;
	}

	public UserFolderInfo(UserFolderInfo folderInfo) {
		super(folderInfo);
		mItemType = IItemType.ITEM_TYPE_USER_FOLDER;
		mFolderType = folderInfo.getFolderType();
		if (mFolderType != GLAppFolderInfo.NO_RECOMMAND_FOLDER) {
			FolderAdController.getInstance().registerFolderAdDataObserver(this);
		}
		mFolderAdDataArray = folderInfo.getFolderAdDataArray();
	}

	/* liyh 2010-11-23 15:56 增加创建文件夹时赋值默认图标 */
	public void setIcon(Drawable aicon, boolean isUserIcon) {
		mIcon = aicon;
		mIsUserIcon = isUserIcon;
	}

	/**
	 * Add an app or shortcut
	 * 
	 * @param item
	 */
	public void add(ItemInfo item) {
		synchronized (mContents) {
			item.registerObserver(this);
			mContents.add(item);
			if (item instanceof ShortCutInfo) {
				if (((ShortCutInfo) item).getRelativeItemInfo() == null) {
					return;
				}
				mTotleUnreadCount += ((ShortCutInfo) item).getRelativeItemInfo().getUnreadCount();
			}
		}
	}

	public void remove(ItemInfo item) {
		int i = 0;
		synchronized (mContents) {
			while (i < mContents.size()) {
				if (mContents.get(i) == item) {
					item.unRegisterObserver(this);
					mContents.remove(i);
					if (item instanceof ShortCutInfo) {
						if (((ShortCutInfo) item).getRelativeItemInfo() == null) {
							return;
						}
						mTotleUnreadCount -= ((ShortCutInfo) item).getRelativeItemInfo()
								.getUnreadCount();
					}
					continue;
				}
				i++;
			}
		}
	}

	public void remove(long id) {
		int i = 0;
		synchronized (mContents) {
			while (i < mContents.size()) {
				final ItemInfo item = mContents.get(i);
				if (item.mInScreenId == id) {
					item.unRegisterObserver(this);
					mContents.remove(i);
					if (item instanceof ShortCutInfo) {
						if (((ShortCutInfo) item).getRelativeItemInfo() == null) {
							return;
						}
						mTotleUnreadCount -= ((ShortCutInfo) item).getRelativeItemInfo()
								.getUnreadCount();
					}
					continue;
				}
				i++;
			}
		}
	}

	/**
	 * 删除指定shortcutinfo程序
	 * 
	 * @param Intent
	 * @return ArrayList<ShortCutInfo> 如果没找到程序，size=0
	 */
	public ArrayList<ShortCutInfo> remove(ShortCutInfo shortCutInfo) {
		ArrayList<ShortCutInfo> listToClearAllObserver = new ArrayList<ShortCutInfo>();
		if (shortCutInfo == null || shortCutInfo.mIntent == null) {
			return listToClearAllObserver;
		}
//		int shortcutInfoType = shortCutInfo.mItemType;
		synchronized (mContents) {
			for (int i = 0; i < mContents.size(); i++) {
				final ItemInfo item = mContents.get(i);
				if (item == null) {
					continue;
				}
				item.unRegisterObserver(this);
				if (item instanceof ShortCutInfo) {
					ShortCutInfo info = (ShortCutInfo) item;
					if (info.mIntent == null /*|| info.mItemType != shortcutInfoType*/) {
						continue;
					}
					boolean equal = false;
					if (info.mItemType == IItemType.ITEM_TYPE_APPLICATION
							&& shortCutInfo.mIntent.getComponent() != null) {
						// 通过componentName比较，不用转String
						equal = ConvertUtils.intentCompare(shortCutInfo.mIntent, info.mIntent);
					} else {
						// 如果componentName都为null,则通过转String比较
						equal = ConvertUtils.intentToStringCompare(shortCutInfo.mIntent,
								info.mIntent);
					}
					if (equal) {
						listToClearAllObserver.add(info);
						mContents.remove(i);
						i--;

						AppItemInfo relativeItemInfo = info.getRelativeItemInfo();
						if (relativeItemInfo != null) {
							mTotleUnreadCount -= relativeItemInfo.getUnreadCount();
						}

						continue;
					}
				}
			}
		}
		for (ItemInfo itemInfoToClearAllObserver : listToClearAllObserver) {
			itemInfoToClearAllObserver.clearAllObserver();
		}
		return listToClearAllObserver;
	}

	/**
	 * 删除指定intent程序
	 * 
	 * @param Intent
	 * @return ArrayList<ShortCutInfo> 如果没找到程序，size=0
	 */
	public ArrayList<ShortCutInfo> remove(Intent intent) {
		ArrayList<ShortCutInfo> listToClearAllObserver = new ArrayList<ShortCutInfo>();
		if (intent == null) {
			return listToClearAllObserver;
		}
		synchronized (mContents) {
			for (int i = 0; i < mContents.size(); i++) {
				final ItemInfo item = mContents.get(i);
				if (item == null) {
					continue;
				}
				item.unRegisterObserver(this);
				if (item instanceof ShortCutInfo) {
					ShortCutInfo info = (ShortCutInfo) item;
					if (info.mIntent == null) {
						continue;
					}
					boolean equal = (ConvertUtils.intentCompare(intent, info.mIntent))
							|| (intent.getComponent() == null
									&& info.mIntent.getComponent() == null && ConvertUtils
										.shortcutIntentCompare(intent, info.mIntent));
					if (equal) {
						listToClearAllObserver.add(info);
						mContents.remove(i);
						i--;
						AppItemInfo relativeItemInfo = info.getRelativeItemInfo();
						if (relativeItemInfo != null) {
							mTotleUnreadCount -= relativeItemInfo.getUnreadCount();
						}
						continue;
					}
				}
			}
		}
		for (ItemInfo itemInfoToClearAllObserver : listToClearAllObserver) {
			itemInfoToClearAllObserver.clearAllObserver();
		}
		return listToClearAllObserver;
	}

	/**
	 * 删除指定intent程序
	 * 
	 * @param Intent
	 * @return ArrayList<ShortCutInfo> 如果没找到程序，size=0
	 */
	public ShortCutInfo removeApp(Intent intent) {
		synchronized (mContents) {
			ShortCutInfo cutInfo = null;
			for (int i = 0; i < mContents.size(); i++) {
				final ItemInfo item = mContents.get(i);
				if (item == null) {
					continue;
				}
				item.unRegisterObserver(this);
				if (item instanceof ShortCutInfo) {
					ShortCutInfo info = (ShortCutInfo) item;
					if (info.mIntent == null) {
						continue;
					}
					boolean equal = (ConvertUtils.intentCompare(intent, info.mIntent))
							|| (intent.getComponent() == null
									&& info.mIntent.getComponent() == null && ConvertUtils
										.shortcutIntentCompare(intent, info.mIntent));
					if (equal) {
						cutInfo = (ShortCutInfo) mContents.remove(i);
						i--;
						AppItemInfo relativeItemInfo = info.getRelativeItemInfo();
						if (relativeItemInfo != null) {
							mTotleUnreadCount -= relativeItemInfo.getUnreadCount();
						}
						continue;
					}
				}
			}
			return cutInfo;
		}
	}

	public ShortCutInfo getChildInfo(int index) {
		synchronized (mContents) {
			if (index >= 0 && index < mContents.size()) {
				return (ShortCutInfo) mContents.get(index);
			}
		}
		return null;
	}

	public int getChildCount() {
		synchronized (mContents) {
			return mContents.size();
		}
	}

	@Override
	public void clearAllObserver() {
		super.clearAllObserver();
		ArrayList<ItemInfo> listToClearAllObserver = new ArrayList<ItemInfo>();
		synchronized (mContents) {
			int size = mContents.size();
			for (int i = 0; i < size; i++) {
				final ItemInfo info = mContents.get(i);
				if (info != null) {
					listToClearAllObserver.add(info);
				}
			}
		}
		for (ItemInfo itemInfoToClearAllObserver : listToClearAllObserver) {
			itemInfoToClearAllObserver.clearAllObserver();
		}
		listToClearAllObserver.clear();
		listToClearAllObserver = null;
	}

	public void clear() {
		/**
		 * freeze问题：clearAllObserver()是BroadCaster的synchronized函数，原则上不应该在一个类的同步块
		 * /函数中调用到另一个类的同步块/函数
		 * 特别是BroadCaster的synchronized函数还包括broadCast,这是外部经常调到到的
		 * ,很容易两个线程互锁了。例子：ADT-3859
		 * 修改方法：把需要clearAllObserver()的iteminfo缓存起来，把clearAllObserver
		 * ()操作移到synchronized (mContents)外
		 */
		ArrayList<ShortCutInfo> listToClearAllObserver = new ArrayList<ShortCutInfo>();
		synchronized (mContents) {
			while (mContents.size() > 0) {
				final ItemInfo itemInfo = mContents.remove(0);
				if (itemInfo != null && itemInfo instanceof ShortCutInfo) {
					listToClearAllObserver.add((ShortCutInfo) itemInfo);
				}
			}
		}
		for (ShortCutInfo itemInfoToClearAllObserver : listToClearAllObserver) {
			itemInfoToClearAllObserver.selfDestruct();
		}
		/**
		 * ADT-14905 文件夹有统计到消息时，修改桌面行列数后，文件夹上面消息数显示不正确
		 */
		mTotleUnreadCount = 0;
		listToClearAllObserver.clear();
		listToClearAllObserver = null;
	}

	public void clearContents() {
		synchronized (mContents) {
			mContents.clear();
		}
	}

	public void addAll(ArrayList<ItemInfo> list) {
		synchronized (mContents) {
			// if (ShellPluginFactory.isUseShellPlugin(GoLauncher.getContext()))
			// {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).registerObserver(this);
				AppItemInfo info = ((ShortCutInfo) list.get(i))
						.getRelativeItemInfo();
				if (info != null) {
					mTotleUnreadCount += info.getUnreadCount();
				}
			}
			// }
			mContents.addAll(list);
		}
	}

	@Override
	public void selfDestruct() {
		super.selfDestruct();
		if (mIcon != null) {
			mIcon.setCallback(null);
		}
	}

	// @Override
	// public void writeObject(ContentValues values, String table) {
	// super.writeObject(values, table);
	// if (table.equals(PartToScreenTable.TABLENAME)){
	// values.put(PartToScreenTable.SORTTYPE, String.valueOf(mSortTpye));
	// }
	// else if(table.equals(ShortcutTable.TABLENAME)){
	// values.put(ShortcutTable.SORTTYPE, String.valueOf(mSortTpye));
	// }
	// }
	//
	// @Override
	// public void readObject(Cursor cursor, String table) {
	// super.readObject(cursor, table);
	// if (table.equals(PartToScreenTable.TABLENAME)){
	// mSortTpye =
	// cursor.getInt(cursor.getColumnIndex(PartToScreenTable.SORTTYPE));
	// }
	// else if(table.equals(ShortcutTable.TABLENAME)){
	// mSortTpye = cursor.getInt(cursor.getColumnIndex(ShortcutTable.SORTTYPE));
	// }
	// }

	public ArrayList<ItemInfo> getContents() {
		return mContents;
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		// TODO Auto-generated method stub
		super.onBCChange(msgId, param, object);
		switch (msgId) {
			case AppItemInfo.UNREADCHANGE :
//				int count = 0;
//				for (int i = 0; i < mContents.size(); i++) {
//					count = count
//							+ ((ShortCutInfo) mContents.get(i)).getRelativeItemInfo()
//									.getUnreadCount();
//				}
//				mTotleUnreadCount = count;
//				//计算总的数量
//				broadCast(AppItemInfo.UNREADTYPECHANGEFORDOCKANDSCREEN, param, object, objects);
				updateUnreadCount();
				broadCast(AppItemInfo.UNREADCHANGE, param, object);
				break;

			case FolderAdDataProvider.BC_MSG_FOLDER_AD_DATA :
				if (param == mFolderType) {
					mFolderAdDataArray = (SparseArray<ArrayList<AppDetailInfoBean>>) object[0];
				}
				break;
			default :
				break;
		}
	}
	
	public int getFolderType() {
		return mFolderType;
	}

	public void setFolderType(int type) {
		mFolderType = type;
		if (mFolderType != GLAppFolderInfo.NO_RECOMMAND_FOLDER) {
			FolderAdController.getInstance().registerFolderAdDataObserver(this);
		}
	}
	
	@Override
	public void writeObject(android.content.ContentValues values, String table) {
		super.writeObject(values, table);
		if (table.equals(PartToScreenTable.TABLENAME)) {
			values.put(PartToScreenTable.FOLDERTYPE, mFolderType);
		} else if (table.equals(ShortcutTable.TABLENAME)) {
			values.put(ShortcutTable.FOLDERTYPE, mFolderType);
		}
	};
	
	@Override
	public void readObject(Cursor cursor, String table) {
		super.readObject(cursor, table);
		if (table.equals(PartToScreenTable.TABLENAME)) {
			mFolderType = cursor.getInt(cursor.getColumnIndex(PartToScreenTable.FOLDERTYPE));
		} else if (table.equals(ShortcutTable.TABLENAME)) {
			mFolderType = cursor.getInt(cursor.getColumnIndex(ShortcutTable.FOLDERTYPE));
		}
		if (mFolderType != GLAppFolderInfo.NO_RECOMMAND_FOLDER) {
			FolderAdController.getInstance().registerFolderAdDataObserver(this);
		}
	}

	public SparseArray<ArrayList<AppDetailInfoBean>> getFolderAdDataArray() {
		return mFolderAdDataArray;
	}

	public void setFolderAdDataArray(SparseArray<ArrayList<AppDetailInfoBean>> folderAdDataArray) {
		this.mFolderAdDataArray = folderAdDataArray;
	}
	
	private void updateUnreadCount() {
		mTotleUnreadCount = 0;
		for (ItemInfo info : mContents) {
			if (info != null) {
				AppItemInfo relativeItemInfo = ((ShortCutInfo) info).getRelativeItemInfo();
				if (relativeItemInfo != null) {
					mTotleUnreadCount += relativeItemInfo.getUnreadCount();
				}
			}
		}
	}
	
}