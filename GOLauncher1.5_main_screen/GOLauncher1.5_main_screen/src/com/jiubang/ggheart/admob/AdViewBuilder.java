package com.jiubang.ggheart.admob;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;

import com.go.util.device.Machine;
import com.google.ads.AdRequest;
import com.google.ads.InterstitialAd;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemeConstants;
import com.jiubang.ggheart.apps.desks.diy.themescan.ThemePurchaseManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.components.advert.AdvertDialogCenter;

/**
 * 
 * @author guoyiqing
 * @date [2013-9-12]
 */
public class AdViewBuilder {

	private static AdViewBuilder sBuilder;

	private AdViewBuilder() {
	}

	public static synchronized AdViewBuilder getBuilder() {
		if (sBuilder == null) {
			sBuilder = new AdViewBuilder();
		}
		return sBuilder;
	}

	/**
	 * 
	 * @param activity
	 * @param pid
	 * @param posId
	 * @param record
	 *            是否要统计当前取开关信息,如已经先访问过isSwitchOpen,再拿GOAdView,则record必须为false,
	 *            否则重复
	 * @return
	 */
	public GoAdView getAdView(Activity activity, int pid, int posId,
			boolean record) {
		if (forceClose(activity)) {
			return null;
		}
		if (!Machine.isNetworkOK(activity)) {
			return null;
		}
		if (!isSwitchOpen(activity, pid, posId, record, 1)) {
			return null;
		}
		if (AdViewStatistics.getStatistics(activity).checkNeedShow()) {
			GoAdView adView = new GoAdView(activity);
			adView.initViews(activity, pid, posId);
			return adView;
		}
		return null;
	}

	public boolean isSwitchOpen(Context activity, int pid, int posId,
			boolean record, int adType) {

		SparseArray<AdInfo> infos = AdController.getController(activity)
				.getAllInfos();
		if (infos == null || infos.size() <= 0) {
			if (record) {
				AdViewStatistics.getStatistics(activity).appendAdShow(activity,
						pid, posId, adType, 0);
			}
			return false;
		}
		AdInfo info = infos.get(AdInfo.getSparseId(pid, posId));
		if (info == null || !info.mSwitchState) {
			if (record) {
				AdViewStatistics.getStatistics(activity).appendAdShow(activity,
						pid, posId, adType, 0);
			}
			return false;
		}
		if (record) {
			AdViewStatistics.getStatistics(activity).appendAdShow(activity, pid,
					posId, adType, 1);
		}
		return true;
	}
	/**
	 * <br>功能简述: 广告为优先级
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param activity
	 * @param pid
	 * @param posId
	 * @return 0 admob优先， 1 getjar 优先
	 */
	public int getProvity(Context activity, int pid, int posId) {
		int result = AdInfo.PROVITY_ADMOB;
		SparseArray<AdInfo> infos = AdController.getController(activity)
				.getAllInfos();
		if (infos != null && infos.size() > 0) {
			AdInfo info = infos.get(AdInfo.getSparseId(pid, posId));
			if (info != null) {
				result = info.mProvity;
			}
		}
		return result;
	}
	
	public boolean isGetjarEnable(Context activity, int pid, int posId) {
		boolean result = forceClose(activity);
		if (!result) {
			SparseArray<AdInfo> infos = AdController.getController(activity)
					.getAllInfos();
			if (infos != null && infos.size() > 0) {
				AdInfo info = infos.get(AdInfo.getSparseId(pid, posId));
				if (info != null) {
					result = info.mGetJarEnable && info.mSwitchState;
				}
			}

		}
		return result;
	}

	private boolean forceClose(Context context) {
		int level = ThemePurchaseManager.getCustomerLevel(context);
		if (level == ThemeConstants.CUSTOMER_LEVEL1) {
			return true;
		} else if (level == ThemeConstants.CUSTOMER_LEVEL2) {
			return true;
		}
		if (FunctionPurchaseManager.getInstance(context)
				.queryItemPurchaseState(
						FunctionPurchaseManager.PURCHASE_ITEM_AD)) {
			return true;
		}
		return false;
	}

	/**
	 * <br>
	 * 注意:不显示,返回null,
	 * InterstitialAd需设setAdListener,在其回调方法里onReceiveAd,接受到广告时InterstitialAd
	 * .show()
	 * 
	 * @param context
	 * @param pid
	 * @param posId
	 * @return
	 */
	public InterstitialAd getInterstitialAd(Activity activity, int pid,
			int posId) {
		if (forceClose(activity)) {
			return null;
		}
		if (!Machine.isNetworkOK(activity)) {
			return null;
		}
		if (!isSwitchOpen(activity, pid, posId, true, 2)) {
			return null;
		}
		if (AdViewStatistics.getStatistics(activity).checkNeedShow()) {
			InterstitialAd interstitialAd = new InterstitialAd(activity,
					AdConstanst.mapPublishId(pid, posId));
			AdRequest adRequest = new AdRequest();
			// adRequest.addTestDevice("A2ACEA59D33404F2ECC68DD6D0D57228");
			interstitialAd.loadAd(adRequest);
			return interstitialAd;
		}
		return null;
	}

	public void showAdConfirmDialog(final Context context) {
		AdvertDialogCenter.showRemoveAdDialog(context, 206);
	}

	public void cleanUp(Context context) {
		AdViewStatistics.getStatistics(context).cleanUp();
	}

}
