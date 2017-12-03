package com.jiubang.ggheart.admob;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

/**
 * 
 * <br>类描述:主题详情界面中共用一个广告
 * <br>功能详细描述:
 * 
 * @author  nixiaozhen
 * @date  [2014-3-17]
 */
public class GoDetailAdView {

    public static GoAdView sBannerAdView;

    public static LinearLayout sParent;

    //判断是否已经点击关闭过
    private static boolean sIsDeleted = false;

    private static OnAdDeleteListener sAdDeleteListener = new OnAdDeleteListener() {

        @Override
        public void onDeleteView(GoAdView view, int pId, int posId) {
            if ((pId == 1 && posId == AdConstanst.POS_ID_LAUNCHER_THEME_DETAIL_BANNER)
                    || (pId == 3 && posId == AdConstanst.POS_ID_LOCKER_THEME_DETAIL_BANNER)) {
                cleanView();
                sIsDeleted = true;
            }
        }

        @Override
        public void onDelete(GoAdView view) {
            // TODO Auto-generated method stub

        }
    };

    public static GoAdView getAdView(Context activity, int pid, int posId,
            LinearLayout parent) {
        if (AdViewBuilder.getBuilder().isSwitchOpen(activity, pid, posId, true,
                1)) {
            if (sParent != null) {
                sParent.removeAllViews();
            }
            if (sBannerAdView == null && activity instanceof Activity
                    && !sIsDeleted) {
                GoAdView adView = AdViewBuilder.getBuilder().getAdView(
                        (Activity) activity, pid, posId, false);
                if (adView != null) {
                    sBannerAdView = adView;
                    adView.setOnDeleteListener(sAdDeleteListener);
                }
            } else {
                if (sBannerAdView != null) {
                    sBannerAdView.setNewInfo(pid, posId);
                }
            }
            sParent = parent;
            return sBannerAdView;
        }
        return null;
    }

    public static void cleanView() {
        if (sParent != null) {
            sParent.removeAllViews();
            sParent = null;
        }

        if (sBannerAdView != null) {
            sBannerAdView.destroy();
            sBannerAdView = null;
        }

    }
}
