package com.jiubang.ggheart.apps.desks.diy.themescan;

import android.app.Activity;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jiubang.ggheart.admob.AdConstanst;
import com.jiubang.ggheart.admob.AdInfo;
import com.jiubang.ggheart.admob.AdViewBuilder;
import com.jiubang.ggheart.admob.GoAdView;
import com.jiubang.ggheart.admob.OnAdDeleteListener;

/**
 * 
 * @author guoyiqing
 * @date [2013-9-13]
 */
public class ThemeTabBannerManager {

	private static ThemeTabBannerManager sCache;
	private SparseArray<GoAdView> mAdViewCache;
	private SparseBooleanArray mSparseBooleanArray;
	
	//判断是否已经点击关闭过
    private boolean mIsDeleted = false;
	private OnAdDeleteListener mAdDeleteListener = new OnAdDeleteListener() {
        
        @Override
        public void onDeleteView(GoAdView view, int pId, int posId) {
            if ((pId == 1 && (posId == AdConstanst.POS_ID_LAUNCHER_THEME_FEATURE_BANNER
                    || posId == AdConstanst.POS_ID_LAUNCHER_THEME_HOT_BANNER || posId == AdConstanst.POS_ID_LAUNCHER_THEME_LOCAL_BANNER))
                    || (pId == 3 && (posId == AdConstanst.POS_ID_LOCKER_THEME_FEATURE_BANNER || posId == AdConstanst.POS_ID_LOCKER_THEME_LOCAL_BANNER))) {
                cleanUp();
                mIsDeleted = true;
            }
        }
        
        @Override
        public void onDelete(GoAdView view) {
            // TODO Auto-generated method stub
            
        }
    };
	
	private ThemeTabBannerManager() {
		mAdViewCache = new SparseArray<GoAdView>();
		mSparseBooleanArray = new SparseBooleanArray();
	}

	
	public static synchronized ThemeTabBannerManager getCache() {
		if (sCache == null) {
			sCache = new ThemeTabBannerManager();
		}
		return sCache;
	}

	public void update(Activity activity, LinearLayout adLayout, int tabId) {
		int posId = -1;
		int pid = -1;
		if (tabId == ThemeConstants.LOCKER_FEATURED_THEME_ID) {
			pid = AdConstanst.PRODUCT_ID_LOCKER_THEME;
			posId = AdConstanst.POS_ID_LOCKER_THEME_FEATURE_BANNER;
		} else if (tabId == ThemeConstants.LOCKER_INSTALLED_THEME_ID) {
			pid = AdConstanst.PRODUCT_ID_LOCKER_THEME;
			posId = AdConstanst.POS_ID_LOCKER_THEME_LOCAL_BANNER;
		} else if (tabId == ThemeConstants.LAUNCHER_FEATURED_THEME_ID) {
			pid = AdConstanst.PRODUCT_ID_LAUNCHER_THEME;
			posId = AdConstanst.POS_ID_LAUNCHER_THEME_FEATURE_BANNER;
		} else if (tabId == ThemeConstants.LAUNCHER_HOT_THEME_ID) {
			pid = AdConstanst.PRODUCT_ID_LAUNCHER_THEME;
			posId = AdConstanst.POS_ID_LAUNCHER_THEME_HOT_BANNER;
		} else if (tabId == ThemeConstants.LAUNCHER_INSTALLED_THEME_ID) {
			pid = AdConstanst.PRODUCT_ID_LAUNCHER_THEME;
			posId = AdConstanst.POS_ID_LAUNCHER_THEME_LOCAL_BANNER;
		}
		mSparseBooleanArray.put(AdInfo.getSparseId(pid, posId), AdViewBuilder
				.getBuilder().isSwitchOpen(activity, pid, posId, true, 1));
		int size = adLayout.getChildCount();
		if (adLayout.getTag() != null && tabId == (Integer) adLayout.getTag()) {
			if (size > 0) {
				return;
			}
		} else {
			if (size > 0) {
				adLayout.removeAllViews();
			}
		}
		int id = AdInfo.getSparseId(pid, posId);
		GoAdView adView = null;
		if (mSparseBooleanArray.get(id)) {
			adView = mAdViewCache.get(pid);
			if (adView != null) {
				adView.setNewInfo(pid, posId);
				attachView(adLayout, tabId, adView);
			} else {
			    if (mIsDeleted) {
			        return;
			    }
				adView = AdViewBuilder.getBuilder().getAdView(activity, pid, posId, false);
				if (adView != null) {
					adView.setId(pid);
					adView.setOnDeleteListener(mAdDeleteListener);
					mAdViewCache.put(pid, adView);
					attachView(adLayout, tabId, adView);
				}
			}
		} 
	}

	private void attachView(LinearLayout adLayout, int tabId, GoAdView adView) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		if (adView.getParent() != null) {
			if (adView.getParent() == adLayout) {
				return;
			} else {
				if (adView.getParent() instanceof ViewGroup) {
					((ViewGroup) adView.getParent()).removeView(adView);
				}
			}
		}
		adLayout.addView(adView, params);
		adLayout.setTag(tabId);
	}

	public void cleanUp() {
		if (mAdViewCache != null) {
			int size = mAdViewCache.size();
			for (int i = 0; i < size; i++) {
				GoAdView adView = mAdViewCache.valueAt(i);
				if (adView != null) {
					adView.destroy();
				}
			}
			mAdViewCache.clear();
		}
	}

}
