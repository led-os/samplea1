package com.jiubang.ggheart.plugin.shell.folder;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

import com.jiubang.ggheart.appgame.base.bean.AppDetailInfoBean;
import com.jiubang.ggheart.common.bussiness.AppClassifyBussiness;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FunAppItemInfo;
import com.jiubang.ggheart.data.info.FunFolderItemInfo;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;

/**
 * 
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 * 
 * @author dingzijian
 * @date [2013-2-21]
 */
public class GLAppFolderInfo {
	public static final int FOLDER_FROM_SCREEN = 0x1;
	public static final int FOLDER_FROM_APPDRAWER = 0x2;
	public static final int FOLDER_FROM_DOCK = 0x3;

	public static final int NO_RECOMMAND_FOLDER = AppClassifyBussiness.NO_CLASSIFY_APP;
	public static final int TYPE_RECOMMAND_GAME = AppClassifyBussiness.GAMES;
	public static final int TYPE_RECOMMAND_BUSINESS = AppClassifyBussiness.BUSINESS;
	public static final int TYPE_RECOMMAND_FINANCE = AppClassifyBussiness.FINANCE;
	public static final int TYPE_RECOMMAND_LEARNING = AppClassifyBussiness.LEARNING;
	public static final int TYPE_RECOMMAND_LIFESTYLE = AppClassifyBussiness.LIFESTYLE;
	public static final int TYPE_RECOMMAND_MAP = AppClassifyBussiness.MAP;
	public static final int TYPE_RECOMMAND_MEDIA = AppClassifyBussiness.MEDIA;
	public static final int TYPE_RECOMMAND_NETWORK = AppClassifyBussiness.NETWORK;
	public static final int TYPE_RECOMMAND_PERSONALIZATION = AppClassifyBussiness.PERSONALIZATION;
	public static final int TYPE_RECOMMAND_READING = AppClassifyBussiness.READING;
	public static final int TYPE_RECOMMAND_SHOPPING = AppClassifyBussiness.SHOPPING;
	public static final int TYPE_RECOMMAND_SOCIAL = AppClassifyBussiness.SOCIAL;
	public static final int TYPE_RECOMMAND_SYSTEM_APP = AppClassifyBussiness.SYSTEM_APP;
	public static final int TYPE_RECOMMAND_TOOLS = AppClassifyBussiness.TOOLS;

	public final int folderFrom;
	public final long folderId;
	public final int folderType;

	private UserFolderInfo mScreenFoIderInfo;
	private FunFolderItemInfo mAppDrawerFolderInfo;

	public GLAppFolderInfo(UserFolderInfo screenFolderInfo, int from) {
		folderId = screenFolderInfo.mInScreenId;
		mScreenFoIderInfo = screenFolderInfo;
		folderFrom = from;
		folderType = screenFolderInfo.getFolderType();
	}

	public GLAppFolderInfo(FunFolderItemInfo appDrawerFolderInfo) {
		folderId = appDrawerFolderInfo.getFolderId();
		mAppDrawerFolderInfo = appDrawerFolderInfo;
		folderFrom = FOLDER_FROM_APPDRAWER;
		folderType = appDrawerFolderInfo.getFolderType();
	}

	public UserFolderInfo getScreenFoIderInfo() {
		return mScreenFoIderInfo;
	}

	public FunFolderItemInfo getAppDrawerFolderInfo() {
		return mAppDrawerFolderInfo;
	}
	
	public List<AppItemInfo> getFolderAppItemInfos() {
		ArrayList<AppItemInfo> appItemInfos = new ArrayList<AppItemInfo>();
		switch (folderFrom) {
			case FOLDER_FROM_APPDRAWER :
				ArrayList<FunAppItemInfo> funAppItemInfos = mAppDrawerFolderInfo.getFolderContent();
				for (FunAppItemInfo funAppItemInfo : funAppItemInfos) {
					AppItemInfo appItemInfo = funAppItemInfo.getAppItemInfo();
					if (appItemInfo != null) {
						appItemInfos.add(funAppItemInfo.getAppItemInfo());
					}
				}
				return appItemInfos;
			default :
				ArrayList<ItemInfo> itemInfos = mScreenFoIderInfo.getContents();
				for (ItemInfo itemInfo : itemInfos) {
					if (itemInfo instanceof ShortCutInfo) {
						ShortCutInfo info = (ShortCutInfo) itemInfo;
						AppItemInfo appItemInfo = info.getRelativeItemInfo();
						if (appItemInfo != null) {
							appItemInfos.add(appItemInfo);
						}
					}
				}
				return appItemInfos;
		}
	}
	public boolean isRecommandType() {
		return folderType == TYPE_RECOMMAND_TOOLS
				|| folderType == TYPE_RECOMMAND_BUSINESS
				|| folderType == TYPE_RECOMMAND_FINANCE
				|| folderType == TYPE_RECOMMAND_LEARNING
				|| folderType == TYPE_RECOMMAND_LIFESTYLE
				|| folderType == TYPE_RECOMMAND_MAP
				|| folderType == TYPE_RECOMMAND_GAME
				|| folderType == TYPE_RECOMMAND_MEDIA
				|| folderType == TYPE_RECOMMAND_NETWORK
				|| folderType == TYPE_RECOMMAND_PERSONALIZATION
				|| folderType == TYPE_RECOMMAND_READING
				|| folderType == TYPE_RECOMMAND_SHOPPING
				|| folderType == TYPE_RECOMMAND_SYSTEM_APP
				|| folderType == TYPE_RECOMMAND_TOOLS;
	}

	public SparseArray<ArrayList<AppDetailInfoBean>> getFolderAdData() {
		switch (folderFrom) {
		case FOLDER_FROM_APPDRAWER:
			return mAppDrawerFolderInfo.getFolderAdDataArray();
		default:
			return mScreenFoIderInfo.getFolderAdDataArray();
		}
	}

}
