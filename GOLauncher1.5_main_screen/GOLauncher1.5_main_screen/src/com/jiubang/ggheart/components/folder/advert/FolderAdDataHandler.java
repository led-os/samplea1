package com.jiubang.ggheart.components.folder.advert;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

import com.go.proxy.ApplicationProxy;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.bean.AppDetailInfoBean;
import com.jiubang.ggheart.appgame.base.data.AppsDetailParser;
/**
 * 
 * @author dingzijian
 *
 */
public class FolderAdDataHandler {

	public FolderAdDataHandleFinishListener mDataHandleFinishListener;
/**
 * 
 * @author dingzijian
 *
 */
	public interface FolderAdDataHandleFinishListener {
		public void onAdDataHandleFinish(SparseArray<ArrayList<AppDetailInfoBean>> array,
				int folderType);
	}

	public void setDataHandleFinishListener(
			FolderAdDataHandleFinishListener dataHandleFinishListener) {
		this.mDataHandleFinishListener = dataHandleFinishListener;
	}

	public void handleRequestAdData(JSONArray adDataJsonArray, int folderType) {
		SparseArray<ArrayList<AppDetailInfoBean>> sparseArray = new SparseArray<ArrayList<AppDetailInfoBean>>();
		int length = adDataJsonArray.length();
		for (int i = 0; i < length; i++) {
			try {
				JSONObject jsonObject = adDataJsonArray.getJSONObject(i);
				int position = jsonObject.getInt("position");
				JSONObject appInfoJson = jsonObject.getJSONObject("appinfo");
				ArrayList<AppDetailInfoBean> beans = sparseArray.get(position,
						new ArrayList<AppDetailInfoBean>());
				AppDetailInfoBean detailInfoBean = AppsDetailParser.parseDetailInfo(appInfoJson);
				if (!GoAppUtils.isAppExist(ApplicationProxy.getContext(), detailInfoBean.mPkgName)) {
					beans.add(detailInfoBean);
				}
				sparseArray.put(position, beans);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (mDataHandleFinishListener != null) {
			mDataHandleFinishListener.onAdDataHandleFinish(sparseArray, folderType);
		}
	}
}
