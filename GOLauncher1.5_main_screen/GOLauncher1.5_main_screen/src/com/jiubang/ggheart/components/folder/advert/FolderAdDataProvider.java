package com.jiubang.ggheart.components.folder.advert;

import java.util.ArrayList;

import android.util.SparseArray;

import com.go.util.BroadCaster;
import com.jiubang.ggheart.appgame.base.bean.AppDetailInfoBean;
import com.jiubang.ggheart.data.info.FunFolderItemInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
/**
 * 
 * @author dingzijian
 *
 */
public class FolderAdDataProvider extends BroadCaster {

	public static final int FOLDER_ADVERT_DATA_TYPE_GAME = 0;

	private FolderAdDataNeedUpdateListener mDataNeedUpdateListener;

	public static final int BC_MSG_FOLDER_AD_DATA = 22221;

	public FolderAdDataProvider() {

	}

	@Override
	public void registerObserver(BroadCasterObserver oberver) {
		super.registerObserver(oberver);
		onFolderInfoRegister(oberver);
	}

	private void onFolderInfoRegister(BroadCasterObserver oberver) {
		int folderType = -1;
		if (oberver instanceof FunFolderItemInfo) {
			folderType = ((FunFolderItemInfo) oberver).getFolderType();
		}
		if (oberver instanceof UserFolderInfo) {
			folderType = ((UserFolderInfo) oberver).getFolderType();
		}
		if (mDataNeedUpdateListener != null && folderType != -1) {
			mDataNeedUpdateListener.folderAdDataNeedUpdate(folderType);
		}
	}
/**
 * 
 * @author dingzijian
 *
 */
	public interface FolderAdDataNeedUpdateListener {
		public void folderAdDataNeedUpdate(int folderTypeID);

	}

	public void onFolderAdDataReady(SparseArray<ArrayList<AppDetailInfoBean>> sparseArray,
			int folderType) {
		broadCast(BC_MSG_FOLDER_AD_DATA, folderType, sparseArray, null);
	};

	public void setDataNeedUpdateListener(FolderAdDataNeedUpdateListener dataNeedUpdateListener) {
		this.mDataNeedUpdateListener = dataNeedUpdateListener;
	}
}
