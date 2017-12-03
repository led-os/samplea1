package com.jiubang.ggheart.apps.desks.diy.plugin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;

/**
 * 插件管理器每个子项
 * @author liulixia
 *
 */
public class PluginItemView extends RelativeLayout {
	private ImageView mImageView;
	private RelativeLayout mRelativeLayout;
	private RelativeLayout mTextLayout;
	private TextView mWidgetStateText;
	private ImageView mWidgetMenu;
	
	SpaceCalculator mSpaceCalculator = null;
	public PluginItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSpaceCalculator = SpaceCalculator.getInstance(context);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mRelativeLayout = (RelativeLayout) findViewById(R.id.widgetLayout);
		mImageView = (ImageView) findViewById(R.id.widgetImage);
		mTextLayout = (RelativeLayout) findViewById(R.id.widgetTextLinearLayout);
		mWidgetMenu = (ImageView) findViewById(R.id.widgetMenu);
		mWidgetStateText = (TextView) findViewById(R.id.widgetState);
		setLayoutParams();
	}
	
	public void setLayoutParams() {
		int mItemWidth = mSpaceCalculator.getItemWidth();
		int mItemHeight = mSpaceCalculator.getItemHeight();
		int mItemImageHeight = mSpaceCalculator.getImageHeight();
		int mItemTextHeight = mSpaceCalculator.getTextViewHeight();
		ViewGroup.LayoutParams params = mImageView.getLayoutParams();
		params.height = mItemImageHeight;
		mImageView.setLayoutParams(params);
		
		params = mRelativeLayout.getLayoutParams();
		if (params == null) {
			params = new AbsListView.LayoutParams(mItemWidth, mItemHeight);
		} else {
			params.width = mItemWidth;
			params.height = mItemHeight;
		}
		mRelativeLayout.setLayoutParams(params);
		
		params = mTextLayout.getLayoutParams();
		params.height = mItemTextHeight;
		mTextLayout.setLayoutParams(params);
		
		params = mWidgetMenu.getLayoutParams();
		if (params != null) {
			params.width = mItemWidth / 4;
			mWidgetMenu.setLayoutParams(params);
		}
		
		params = mWidgetStateText.getLayoutParams();
		if (params != null) {
			params.width = mItemWidth / 4;
			mWidgetStateText.setLayoutParams(params);
		}
	}
}
