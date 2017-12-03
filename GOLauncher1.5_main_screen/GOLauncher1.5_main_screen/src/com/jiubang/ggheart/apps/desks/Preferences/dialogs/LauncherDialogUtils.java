package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.go.proxy.GoLauncherActivityProxy;

/**
 * 
 * @author guoyiqing
 * 
 */
public class LauncherDialogUtils {

	/**
	 * <br>
	 * 功能简述:设置对话框的宽度，使用屏幕宽度为横竖屏的宽度 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public static void setDialogWidth(final View layout, Context context) {
		if (layout != null) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			int width = metrics.widthPixels;
			int pddingWidth = (int) context.getResources().getDimension(
					R.dimen.dialog_padding_width);
			if (metrics.widthPixels < metrics.heightPixels) {
				layout.getLayoutParams().width = width - pddingWidth * 2;
			} else {
				layout.getLayoutParams().width = (int) (width * 0.54f);
			}
		}
	}
	
	public static void limitHeight(ViewGroup dialogLayout, Context context) {
		int limit = 0;
		int hLimit = 0;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		limit = (int) (metrics.heightPixels * 0.1f);
		hLimit = (int) (metrics.heightPixels * 0.9f);
		if (dialogLayout.getLayoutParams() instanceof LinearLayout.LayoutParams) {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dialogLayout
					.getLayoutParams();
			params.bottomMargin = limit >> 1;
			params.topMargin = limit >> 1;
			if (params.height > hLimit) {
				params.height = hLimit;
			}
		} else if (dialogLayout.getLayoutParams() instanceof FrameLayout.LayoutParams) {
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialogLayout
					.getLayoutParams();
			params.bottomMargin = limit >> 1;
			params.topMargin = limit >> 1;
			if (params.height > hLimit) {
				params.height = hLimit;
			}
		} else if (dialogLayout.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dialogLayout
					.getLayoutParams();
			params.bottomMargin = limit >> 1;
			params.topMargin = limit >> 1;
			if (params.height > hLimit) {
				params.height = hLimit;
			}
		}
		dialogLayout.requestLayout();
	}

	public static void traiHeight(final View contentView, final ListView layout) {
		layout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						int h = layout.getMeasuredHeight();
						int limit = (int) (GoLauncherActivityProxy.getScreenHeight() * 0.5f);
						Log.e("guoyiqing", "h:" + h);
						if (h > limit) {
							h = limit;
							if (layout.getTag() != null) {
								h = (Integer) layout.getTag();
							}
							layout.getLayoutParams().height = h;
							layout.setLayoutParams(layout.getLayoutParams());
							contentView.requestLayout();
//							layout.getViewTreeObserver()
//									.removeGlobalOnLayoutListener(this);
						}
						if (h != 0 && layout.getTag() == null) {
							layout.setTag(h);
						}
					}
				});
	}

}
