package com.jiubang.ggheart.components.folder.advert;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.util.SparseArray;

import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.AppUtils;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.go.util.GotoMarketIgnoreBrowserTask;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.core.message.IMessageHandler;
import com.jiubang.ggheart.appgame.base.bean.AppDetailInfoBean;
import com.jiubang.ggheart.appgame.gostore.base.component.AppsThemeDetailActivity;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.components.folder.advert.FolderAdDataHandler.FolderAdDataHandleFinishListener;
import com.jiubang.ggheart.components.folder.advert.FolderAdDataProvider.FolderAdDataNeedUpdateListener;
import com.jiubang.ggheart.components.folder.advert.FolderAdDataRequestor.FolderAdDataRequestFinishListener;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FunFolderItemInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;

/**
 * 
 * @author dingzijian
 *
 */
public class FolderAdController
		implements
			IMessageHandler,
			FolderAdDataNeedUpdateListener,
			FolderAdDataRequestFinishListener,
			FolderAdDataHandleFinishListener {
	private static volatile FolderAdController sInstance;
	private FolderAdDataProvider mAdDataProvider;
	private FolderAdDataHandler mAdDataHandler;
	private FolderAdDataRequestor mAdDataRequestor;

	public static final long THIRTY_MINS = 1000 * 30 * 30;
	public static final String INSTALL_TIME = "/install_time";
	public static final String DOWNLOAD_TIME = "/download_time";
	public static final String APP_ID = "/app_id";
	private FolderAdController() {
		init();
	}

	private void init() {
		MsgMgrProxy.registMsgHandler(this);
		mAdDataProvider = new FolderAdDataProvider();
		mAdDataProvider.setDataNeedUpdateListener(this);
		mAdDataHandler = new FolderAdDataHandler();
		mAdDataHandler.setDataHandleFinishListener(this);
		mAdDataRequestor = new FolderAdDataRequestor();
		mAdDataRequestor.setAdDataRequestFinishListener(this);
	}

	public static FolderAdController getInstance() {
		if (sInstance == null) {
			sInstance = new FolderAdController();
		}
		return sInstance;
	}

	@Override
	public int getMsgHandlerId() {
		return IDiyFrameIds.FOLDER_ADVERT;
	}

	@Override
	public boolean handleMessage(Object who, int msgId, int param, Object ...obj) {
		switch (msgId) {
			case IAppCoreMsgId.EVENT_INSTALL_APP :
				ArrayList<AppItemInfo> appItemInfos = (ArrayList<AppItemInfo>) obj[1];
				//				boolean needUpdateData = false;
				//				int folderType = GLAppFolderInfo.NO_RECOMMAND_FOLDER;
				//				for (AppItemInfo appItemInfo : appItemInfos) {
				//					if (appItemInfo.mClassification == AppClassifyBussiness.GAMES) {
				//						needUpdateData = true;
				//						folderType = GLAppFolderInfo.TYPE_RECOMMAND_GAME;
				//					}
				//				}
				if (appItemInfos.isEmpty()) {
					return true;
				}
				checkFolderAdDataUpdate();
				PreferencesManager manager = new PreferencesManager(ApplicationProxy.getContext(),
						IPreferencesIds.FOLDER_AD_PREFERENCES, Context.MODE_PRIVATE);
				for (AppItemInfo itemInfo : appItemInfos) {
					String pkgName = itemInfo.getAppPackageName();
					long downLoadTime = manager.getLong(pkgName + DOWNLOAD_TIME, -1);
					int appId = manager.getInt(pkgName + APP_ID, -1);
					if (downLoadTime != -1 && appId != -1) {
						if (System.currentTimeMillis() - downLoadTime < THIRTY_MINS) {
							GuiThemeStatistics.folderAdStaticData(appId + "", "b000", 1);
							manager.putLong(itemInfo.getAppPackageName() + INSTALL_TIME,
									System.currentTimeMillis());
							manager.commit();
						}
						manager.remove(pkgName + DOWNLOAD_TIME);
						manager.remove(pkgName + APP_ID);
					}
				}
				break;

			default :
				break;
		}
		return false;
	}

	public void releaseSelf() {
		MsgMgrProxy.unRegistMsgHandler(this);
		mAdDataProvider = null;
		mAdDataHandler = null;
		mAdDataRequestor = null;
		sInstance = null;
	}

	@Override
	public void folderAdDataNeedUpdate(int folderTypeID) {
		mAdDataRequestor.requestFolderAdData(folderTypeID);
	}

	public void checkFolderAdDataUpdate() {
		/**这部份代码不需要合并到CN**/
		if (checkHasPayed()) {
			return;
		}
		/**这部份代码不需要合并到CN**/
		ArrayList<?> adDataObserver = mAdDataProvider.getObserver();
		if (adDataObserver != null) {
			for (Object observer : adDataObserver) {
				int folderType = -1;
				if (observer instanceof UserFolderInfo) {
					folderType = ((UserFolderInfo) observer).getFolderType();
				}
				if (observer instanceof FunFolderItemInfo) {
					folderType = ((FunFolderItemInfo) observer).getFolderType();
				}
				if (folderType != -1) {
					mAdDataRequestor.requestFolderAdData(folderType);
				}
			}
		}
	}

	@Override
	public void onAdDataRequestFinish(JSONArray adDataJsonArray, int tid) {
		mAdDataHandler.handleRequestAdData(adDataJsonArray, tid);
	}

	@Override
	public void onAdDataHandleFinish(SparseArray<ArrayList<AppDetailInfoBean>> array, int folderType) {
		mAdDataProvider.onFolderAdDataReady(array, folderType);
	}

	public synchronized void registerFolderAdDataObserver(BroadCasterObserver oberver) {
		/**这部份代码不需要合并到CN**/
		if (checkHasPayed()) {
			return;
		}
		/**这部份代码不需要合并到CN**/
		mAdDataProvider.registerObserver(oberver);
	}

	/**这部份代码不需要合并到CN**/
	public boolean checkHasPayed() {
		if (FunctionPurchaseManager.getInstance(ApplicationProxy.getApplication())
				.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_AD) == FunctionPurchaseManager.STATE_CAN_USE) {
			releaseSelf();
			return true;
		}
		return false;
	}
	/**这部份代码不需要合并到CN**/

	public void unRegisterFolderAdDataObserver(BroadCasterObserver oberver) {
		mAdDataProvider.unRegisterObserver(oberver);
	}

	public void downLoadApk(AppDetailInfoBean infoBean) {
		switch (infoBean.mDownloadType) {
			case AppsThemeDetailActivity.DETAIL_DOWNLOAD_TYPE_MARKET :
				if (GoAppUtils.isMarketExist(ApplicationProxy.getContext())) {
					GoAppUtils.gotoMarket(ApplicationProxy.getContext(), infoBean.mDownloadUrl);
				} else {
					AppUtils.gotoBrowser(ApplicationProxy.getContext(), infoBean.mDownloadUrl);
				}
				break;
			case AppsThemeDetailActivity.DETAIL_DOWNLOAD_TYPE_FTP :

			case AppsThemeDetailActivity.DETAIL_DOWNLOAD_TYPE_WEB :
				// V4.15 浏览器的跳转方式有改变 
				if (GotoMarketIgnoreBrowserTask.isRedirectUrl(infoBean.mDownloadUrl)) {
					GotoMarketIgnoreBrowserTask.startExecuteTask(ApplicationProxy.getContext(), infoBean.mDownloadUrl);
				} else {
					AppUtils.gotoBrowser(ApplicationProxy.getContext(), infoBean.mDownloadUrl);
				}
//				AppUtils.gotoBrowser(ApplicationProxy.getContext(), infoBean.mDownloadUrl);
				break;
			default :
				break;
		}
		//文件夹点击下载统计
		GuiThemeStatistics.folderAdStaticData(infoBean.mAppId + "", "a000", 1);
		PreferencesManager manager = new PreferencesManager(ApplicationProxy.getContext(),
				IPreferencesIds.FOLDER_AD_PREFERENCES, Context.MODE_PRIVATE);
		manager.putLong(infoBean.mPkgName + DOWNLOAD_TIME, System.currentTimeMillis());
		manager.putInt(infoBean.mPkgName + APP_ID, infoBean.mAppId);
		manager.commit();
	}
}
