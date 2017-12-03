package com.jiubang.ggheart.apps.desks.diy.plugin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.view.WindowManager;

import com.gau.go.launcherex.R;

/**
 * 管理对控件高度，宽度，间距等的计算方法
 * 
 * @author liulixia
 * */
public class SpaceCalculator {

	/**
	 * 是否为竖屏
	 * */
	public static boolean sPortrait = true;
	/**
	 * 竖屏下，每行显示的主题个数
	 * */
	public static int sItemCountV = 0;
	/**
	 * 横屏下，每行显示的主题个数
	 * */
	public static int sItemCountH = 0;

	private Activity mContext;
	private static SpaceCalculator sInstance = null;
	private static int sEdgePadding;
	private static int sEachotherPadding;
	private int mScreenWidth; // 屏幕宽度
	private int mItemHeight = 0; // item项高度
	private int mItemWidth = 0; //item项宽度
	private int mItemImageHeight = 0; //item项图片高度
	private int mItemTextViewHeight = 0; //item文字高度


	private SpaceCalculator(Context context) {
		this.mContext = (Activity) context;
		mContext = (Activity) context;
		Rect frame = new Rect();
		mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	}

	public synchronized static SpaceCalculator getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new SpaceCalculator(context);
		}
		return sInstance;
	}

	/**
	 * 
	 * 获取文字高度
	 * */
	public int getFontHeight() {
		Paint paint = new Paint();
		paint.setTextSize(12);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent) + 2;

	}
	
	public static void setIsPortrait(boolean isPortrait) {
		sPortrait = isPortrait;
	}


	public void calculateItemViewInfo() {
		int showCountInRow = 2;	//每行排列的主题数目
		
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		// 图片离屏幕2边的距离
		sEdgePadding = mContext.getResources().getDimensionPixelSize(
				R.dimen.theme_list_item_padding_edge_portrait);
		// 图片之间的距离
		sEachotherPadding = mContext.getResources().getDimensionPixelSize(
				R.dimen.theme_list_item_padding_eachother_portrait);
		if (!sPortrait) {
			showCountInRow = 3; //横屏的列数
			sEachotherPadding = mContext.getResources().getDimensionPixelSize(
					R.dimen.plugin_manage_item_padding_eachother_land);
		}
		mItemWidth = (mScreenWidth - 2 * sEdgePadding - (showCountInRow - 1) * sEachotherPadding) / showCountInRow;
		int width = showCountInRow * mItemWidth + 2 * sEdgePadding + (showCountInRow - 1) * sEachotherPadding;
		if (mScreenWidth - width > 1) {
			int extraPadding = (mScreenWidth - width) / 2;	//计算出往两边加的额外的边距
			sEdgePadding = sEdgePadding + extraPadding;
		}
		mItemHeight = 373 * mItemWidth / 308; //按比例计算图片高度 
		mItemImageHeight = 297 * mItemWidth / 308;
		mItemTextViewHeight = mItemHeight - mItemImageHeight;
		
	}
	
	public static int getEdgePadding() {
		return sEdgePadding;
	}
	
	public static int getEachotherPadding() {
		return sEachotherPadding;
	}
	
	public int getItemWidth() {
		return mItemWidth;
	}
	
	public int getItemHeight() {
		return mItemHeight;
	}
	
	public int getImageHeight() {
		return mItemImageHeight;
	}
	
	public int getTextViewHeight() {
		return mItemTextViewHeight;
	}
}
