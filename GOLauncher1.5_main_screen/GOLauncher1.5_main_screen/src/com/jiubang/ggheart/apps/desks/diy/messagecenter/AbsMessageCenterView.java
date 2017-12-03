package com.jiubang.ggheart.apps.desks.diy.messagecenter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.RelativeLayout;
/**
 * 消息中心的父类view
 * @author zengyingzhen
 *
 */
public abstract class AbsMessageCenterView extends RelativeLayout implements
		IMessageCenterViewStatus {
	public int mWidth;
	public int mHeight;

	public AbsMessageCenterView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wMgr.getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
	}

}