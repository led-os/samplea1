package com.jiubang.ggheart.components.advert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.gau.go.launcherex.R;
import com.go.util.AppUtils;
import com.go.util.GotoMarketIgnoreBrowserTask;
import com.go.util.market.MarketConstant;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.components.advert.untils.DialogNoAdvertPayActivity;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 广告弹框中心
 * @author yejijiong
 *
 */
public class AdvertDialogCenter {
	public static long sDownloadCleanMasterInAppManager = 0; // 从应用管理点击clean master对话框的安装按钮的时间，30分钟内有效
	public static long sDownloadCleanMasterMark = 0; // 记录点击clean master对话框的安装按钮的时间，30分钟内有效
	public static long sDownloadNextBrowserMark = 0; // 记录点击NEXT浏览器对话框的安装按钮的时间，30分钟内有效
	public static long sDownloadMathonBrowserMark = 0; // 记录点击Mathon浏览器对话框的安装按钮的时间，30分钟内有效
	public static long sDownloadScurityMark = 0; // 记录点击360对话框的安装按钮的时间，30分钟内有效
	public static long sDownloadDuSpeedBoosterMark = 0; // 记录点击安卓优化大师安装按钮的时间，30分钟有效
	
	/**
	 * 显示推荐clean master对话框
	 * @param context
	 */
	public static void showCleanMasterDialog(final Activity activity) {
		if (activity == null) {
			return;
		}
		
		final AdvertDialog dialog = new AdvertDialog(activity);
		dialog.show();
		//由于用户安装总数下降，效果未达到预期，暂时去掉图片显示及更换提示语
//		dialog.setImageLayoutParams(LayoutParams.FILL_PARENT, DrawUtils.dip2px(110));
		dialog.setTitle(R.string.residue_file_dialog_title);
		dialog.setMessage(R.string.residue_file_dialog_msg_no_ad);
		dialog.setNegativeButton(R.string.cancle, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GuiThemeStatistics
						.guiStaticData(40, "2463865", "cancel", 1, "-1", "-1", "-1", "-1");
				dialog.dismiss();
				//CM 卸载引导:三次卸载应用后弹框-点击取消/返回时弹出,去掉推荐Prime入口。（2014-01-14）
//				showRemoveAdDialog(activity, 210);
			}
		});
		dialog.setPositiveButton(R.string.widget_choose_download, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GuiThemeStatistics.guiStaticData(40, "2463865", "a000", 1, "-1", "-1", "-1", "-1");
				sDownloadCleanMasterMark = System.currentTimeMillis();
				if (!GoAppUtils.isAppExist(activity, PackageName.CLEAN_MASTER_PACKAGE)) {

					if (GoAppUtils.isMarketExist(activity)) {
						GoAppUtils.gotoMarket(activity, MarketConstant.APP_DETAIL
								+ PackageName.CLEAN_MASTER_PACKAGE
								+ LauncherEnv.Plugin.CLEAN_MASTER_GA_FOR_DIALOG);
					} else {
						AppUtils.gotoBrowser(activity, MarketConstant.BROWSER_APP_DETAIL
								+ PackageName.CLEAN_MASTER_PACKAGE
								+ LauncherEnv.Plugin.CLEAN_MASTER_GA_FOR_DIALOG);
					}
				}
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
			  //CM 卸载引导:三次卸载应用后弹框-点击取消/返回时弹出,去掉推荐Prime入口。（2014-01-14）
//				showRemoveAdDialog(activity, 210);
			}
		});
	}
	
	public static void showRemoveAdDialog(Context context, int entrace) {
		PreferencesManager sp = new PreferencesManager(context,
				IPreferencesIds.USERTUTORIALCONFIG, Context.MODE_PRIVATE);
		boolean shouldShowRemoveAd = sp.getBoolean(
				IPreferencesIds.SHOULD_SHOW_REMOVE_AD_DIALOG, true);
		if (shouldShowRemoveAd) {
			sp.putBoolean(IPreferencesIds.SHOULD_SHOW_REMOVE_AD_DIALOG, false);
			// 只有200渠道才会
			if (FunctionPurchaseManager.getInstance(context)
					.getPayFunctionState(
							FunctionPurchaseManager.PURCHASE_ITEM_AD) == FunctionPurchaseManager.STATE_VISABLE) {
			    //屏蔽用户点击5次后弹出的admob广告关闭按钮，弹出的推荐Prime对话框。（2014-01-14）
				Intent i = new Intent(context, DialogNoAdvertPayActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("entrace", entrace);
				context.startActivity(i);
			}
		}
		sp.commit();
	}
	
	/**
	 * <br>功能简述:点击dock栏浏览器推荐遨游浏览器
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param activity
	 */
    public static void showMaxthonDialog(final Activity activity) {
        if (activity == null) {
            return;
        }

        final AdvertDialog dialog = new AdvertDialog(activity);
        dialog.show();
        //由于用户安装总数下降，效果未达到预期，暂时去掉图片显示及更换提示语
        //      dialog.setImageLayoutParams(LayoutParams.FILL_PARENT, DrawUtils.dip2px(110));
        dialog.setTitle(R.string.maxthon_browser_title);
        dialog.setMessage(R.string.maxthon_browser_content);
        dialog.setNegativeButton(R.string.maxthon_browser_cancle, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GuiThemeStatistics.guiStaticDataFor360Mathon(40, "5380697", "cancel",
                        1, "-1", "-1", "-1", "-1", "571");
                dialog.dismiss();
                //CM 卸载引导:三次卸载应用后弹框-点击取消/返回时弹出,去掉推荐Prime入口。（2014-01-14）
                //              showRemoveAdDialog(activity, 210);
            }
        });
        dialog.setPositiveButton(R.string.maxthon_browser_download,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GuiThemeStatistics.guiStaticDataFor360Mathon(40, "5380697", "a000",
                                1, "-1", "-1", "-1", "-1", "571");
                        sDownloadMathonBrowserMark = System.currentTimeMillis();
                        //                if (!AppUtils.isAppExist(activity, LauncherEnv.Plugin.CLEAN_MASTER_PACKAGE)) {

                        if (GoAppUtils.isMarketExist(activity)) {
                            GoAppUtils.gotoMarket(
                                    activity,
                                    MarketConstant.APP_DETAIL
                                            + PackageName.MAXTHON_PACKAGE
                                            + LauncherEnv.Plugin.MAXTHON_BROWSER_GA_FOR_DIALOG);
                        } else {
                            AppUtils.gotoBrowser(
                                    activity,
                                    MarketConstant.BROWSER_APP_DETAIL
                                            + PackageName.MAXTHON_PACKAGE
                                            + LauncherEnv.Plugin.MAXTHON_BROWSER_GA_FOR_DIALOG);
                        }
                        //                }
                    }
                });
    }
	
    /**
     * <br>功能简述:点击3次正在运行，弹出推荐360
     * <br>功能详细描述:
     * <br>注意:
     * @param activity
     */
    public static void show360RecommendDialog(final Activity activity) {
        if (activity == null) {
            return;
        }

        final AdvertDialog dialog = new AdvertDialog(activity);
        dialog.show();
        //由于用户安装总数下降，效果未达到预期，暂时去掉图片显示及更换提示语
        //      dialog.setImageLayoutParams(LayoutParams.FILL_PARENT, DrawUtils.dip2px(110));
        dialog.setTitle(R.string.recommend_safe_title);
        dialog.setMessage(R.string.recommend_safe_content);
        dialog.setNegativeButton(R.string.safe_cancle, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GuiThemeStatistics.guiStaticDataFor360Mathon(40, "5371909", "cancel", 1,
                        "-1", "-1", "-1", "-1", "570");
                dialog.dismiss();
                //CM 卸载引导:三次卸载应用后弹框-点击取消/返回时弹出,去掉推荐Prime入口。（2014-01-14）
                //              showRemoveAdDialog(activity, 210);
            }
        });
        dialog.setPositiveButton(R.string.safe_download,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GuiThemeStatistics.guiStaticDataFor360Mathon(40, "5371909", "a000", 1,
                                "-1", "-1", "-1", "-1", "570");
                        sDownloadScurityMark = System.currentTimeMillis();
                        //                if (!AppUtils.isAppExist(activity, LauncherEnv.Plugin.CLEAN_MASTER_PACKAGE)) {

                        if (GoAppUtils.isMarketExist(activity)) {
                        	GoAppUtils.gotoMarket(
                                    activity,
                                    MarketConstant.APP_DETAIL
                                            + PackageName.SECURITY_GUARDS_PACKAGE
                                            + LauncherEnv.Plugin.SECURITY_GUARDS_GA_FOR_DIALOG);
                        } else {
                            AppUtils.gotoBrowser(
                                    activity,
                                    MarketConstant.BROWSER_APP_DETAIL
                                            + PackageName.SECURITY_GUARDS_PACKAGE
                                            + LauncherEnv.Plugin.SECURITY_GUARDS_GA_FOR_DIALOG);
                        }
                        //                }
                    }
                });
    }
    
    /**
     * <br>功能简述:点击3次正在运行，弹出推荐安卓优化大师
     * <br>功能详细描述:
     * <br>注意:
     * @param activity
     */
	public static void showDuSpeedBoosterDialog(final Activity activity, final String mapId,
			final String aId, final String url) {
		if (activity == null) {
			return;
		}

		final AdvertDialog dialog = new AdvertDialog(activity);
		dialog.show();
		dialog.setTitle(R.string.recommend_safe_title);
		dialog.setMessage(R.string.recommend_safe_content);
		dialog.setNegativeButton(R.string.safe_cancle, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GuiThemeStatistics.guiStaticDataWithAId(40, mapId, "cancel", 1, "-1", "-1",
						"-1", "-1", aId);
				dialog.dismiss();
				//CM 卸载引导:三次卸载应用后弹框-点击取消/返回时弹出,去掉推荐Prime入口。（2014-01-14）
				//              showRemoveAdDialog(activity, 210);
			}
		});
		dialog.setPositiveButton(R.string.safe_download, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GuiThemeStatistics.guiStaticDataWithAId(40, mapId, "a000", 1, "-1", "-1",
						"-1", "-1", aId);
				GuiThemeStatistics.recordAdvertId(v.getContext(), mapId, aId);
				sDownloadDuSpeedBoosterMark = System.currentTimeMillis();
				GotoMarketIgnoreBrowserTask.startExecuteTask(activity, url);
			}
		});
	}
	
	
	public static void showUpdateKeyDialog(final Activity activity) {
		DialogConfirm updateKeyDialog = new DialogConfirm(activity);
		updateKeyDialog.show();
		updateKeyDialog.setTitle(activity.getString(R.string.key_version_update));
		updateKeyDialog.setMessage(R.string.key_text);
		updateKeyDialog.setNegativeButtonVisible(View.GONE);
		updateKeyDialog.setPositiveButton(R.string.key_update, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				GoAppUtils.gotoBrowserIfFailtoMarket(activity,
						MarketConstant.APP_DETAIL + PackageName.PRIME_KEY,
						LauncherEnv.Plugin.PRIME_KEY_URL);

			}
		});
	}
}
