package com.jiubang.ggheart.admob;

import java.util.List;

import android.content.Context;
import android.util.SparseArray;

import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;

/**
 * 
 * @author  guoyiqing
 * @date  [2013-9-12]
 */
public class AdController {

	private static AdController sController;
	private AdDataModel mDataModel;
	private Context mContext;

	private AdController(Context context) {
		mContext = context;
		mDataModel = new AdDataModel(mContext);
		mAdInfos = converteToSparse(mDataModel.getAllAdInfos());
	}

	private SparseArray<AdInfo> mAdInfos;

	public static synchronized AdController getController(Context context) {
		if (sController == null) {
			sController = new AdController(context);
		}
		return sController;
	}

	public SparseArray<AdInfo> getAllInfos() {
		return mAdInfos;
	}

	public void updateAdInfos(List<AdInfo> infos, int adlimit, int pertime) {
		mAdInfos = converteToSparse(infos);
		mDataModel.updateOrInsertAdInfos(infos);
		AdViewStatistics.getStatistics(mContext).setAdLimit(adlimit);
		AdViewStatistics.getStatistics(mContext).setPertime(pertime);
	}

	private SparseArray<AdInfo> converteToSparse(List<AdInfo> infos) {
		SparseArray<AdInfo> sparseArray = new SparseArray<AdInfo>();
		if (infos != null) {
			for (AdInfo info : infos) {
				sparseArray.put(info.getSparseId(), info);
			}
		}
		return sparseArray;
	}

	/**
	 * <br>功能简述:4.16开始每次都点击进入到广告页面推荐Prime
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 */
	public void checkDelete(Context context) {
//		if (!Statistics.isKoreaLocal(mContext)) {
	    //4.16版本去掉限制，点击关闭按钮后，每次都进入到广告推荐Prime页面。
//			int count = AdViewStatistics.getStatistics(mContext).getDeleteCount();
//			if (count == 1) {
				DeskSettingUtils.showPayDialog(mContext, 205);
//			} else if (count >= 5 && count < 10) {
				// 以前规则是第一次和第十次跳，现在改为第一次和第五次，避免流失已经点击了第五次到第十次之间的用户
				// 采用在点击次数大于等于五并且小于十的情况下，显示去广告页面，并且sharePreference + 5
//				AdViewStatistics.getStatistics(mContext).addDeleteCount(5);
//				AdViewBuilder.getBuilder().showAdConfirmDialog(context);
//			}
//		}
	}

}
