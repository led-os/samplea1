package com.jiubang.ggheart.appgame.gostore.base.component;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import com.jiubang.ggheart.admob.AdViewBuilder;
import com.jiubang.ggheart.admob.GoAdView;
import com.jiubang.ggheart.admob.OnAdDeleteListener;

/**
 * 
 * @author zhouxuewen
 *
 */
public class GoStoreAdView {

	public static GoAdView sBannerAdView;
	
	public static RelativeLayout sParent;
	
	public static GoAdView sDetailAdView;
	
	public static RelativeLayout sDetailParent;
	
	public static boolean sInBannerAdShow = true;
	
	public static boolean sInDetailAdShow = true;
	
	public static GoAdView getAdView(Context activity, int pid, int posId, RelativeLayout parent) {
		if (!sInBannerAdShow) {
			return null;
		}
		if (AdViewBuilder.getBuilder().isSwitchOpen(activity, 
				pid, posId, true, 1)) {
			if (sParent != null) {
				sParent.removeAllViews();
			}
			if (sBannerAdView == null) {
				GoAdView adView = AdViewBuilder.getBuilder().getAdView(
						(Activity) activity, pid,
						posId, false);
				if (adView != null) {
					sBannerAdView = adView;
					sBannerAdView.setOnDeleteListener(new OnAdDeleteListener() {
						
						@Override
						public void onDeleteView(GoAdView view, int pId, int posId) {
							// TODO Auto-generated method stub
							sInBannerAdShow = false;
						}
						
						@Override
						public void onDelete(GoAdView view) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			} else {
				sBannerAdView.setNewInfo(pid, posId);
			}
			sParent = parent;
			return sBannerAdView;
		}
		return null;
	}
	
	public static void initDetailAdView(Activity activity, int pid, int posId) {
		if (AdViewBuilder.getBuilder().isSwitchOpen(activity, 
				pid, posId, true, 1)) {
			if (sDetailAdView == null) {
				GoAdView adView = AdViewBuilder.getBuilder().getAdView(
						activity, pid,
						posId, false);
				if (adView != null) {
					sDetailAdView = adView;
					sDetailAdView.setOnDeleteListener(new OnAdDeleteListener() {
						
						@Override
						public void onDeleteView(GoAdView view, int pId, int posId) {
							// TODO Auto-generated method stub
							sInDetailAdShow = false;
						}
						
						@Override
						public void onDelete(GoAdView view) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		}
	}
	
	public static GoAdView getDetailAdView(Context activity, int pid, int posId, RelativeLayout parent) {
		if (!sInDetailAdShow) {
			return null;
		}
		if (AdViewBuilder.getBuilder().isSwitchOpen(activity, 
				pid, posId, true, 1)) {
			if (sDetailParent != null) {
				sDetailParent.removeAllViews();
			}
			if (sDetailAdView == null) {
				GoAdView adView = AdViewBuilder.getBuilder().getAdView(
						(Activity) activity, pid,
						posId, false);
				if (adView != null) {
					return adView;
				}
			} else {
				sDetailAdView.setNewInfo(pid, posId);
			}
			sDetailParent = parent;
			return sDetailAdView;
		}
		return null;
	}
}
