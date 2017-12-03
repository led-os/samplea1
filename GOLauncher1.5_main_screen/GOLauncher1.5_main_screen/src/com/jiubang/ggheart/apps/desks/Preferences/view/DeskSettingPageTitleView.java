package com.jiubang.ggheart.apps.desks.Preferences.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;

/**
 * 
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 * 
 * @author licanhui
 * @date [2012-9-10]
 */
public class DeskSettingPageTitleView extends RelativeLayout implements OnClickListener, IDeskSettingI18nSupporter {
	private Context mContext;
	private LinearLayout mBackLayout;
	private TextView mTitleTextView;

	public DeskSettingPageTitleView(Context context) {
		super(context);
	}

	public DeskSettingPageTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		Resources resources = context.getResources();
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeskSettingItemView);
		Drawable imageDrawable = a.getDrawable(R.styleable.DeskSettingItemView_image);
		int titleLineHeight = a.getDimensionPixelSize(
				R.styleable.DeskSettingItemView_titleTextLineHeight, -1);

		//针对4.08设置改版添加的属性
		Drawable titleBg = a.getDrawable(R.styleable.DeskSettingItemView_titleBg);
		int titleColor = a.getColor(R.styleable.DeskSettingItemView_titleTextColor,
				resources.getColor(android.R.color.white));
		float titleSize = a.getDimension(R.styleable.DeskSettingItemView_titleTextSize,
				resources.getDimension(R.dimen.desk_setting_pagetitle_font_size));
		int titleHeight = (int) a.getDimension(R.styleable.DeskSettingItemView_titleHeight,
				resources.getDimension(R.dimen.desk_setting_pagetitle_height));
		boolean showLogo = a.getBoolean(R.styleable.DeskSettingItemView_titleShowLogo, false);

		a.recycle();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.desk_setting_page_title_view, this);

		View rootView = view.findViewById(R.id.rootView);
		if (titleBg != null) {
			rootView.setBackgroundDrawable(titleBg);
		}

		ImageView imageView = (ImageView) view.findViewById(R.id.title_image);
		if (imageDrawable != null) {
			imageView.setImageDrawable(imageDrawable);
		}
		ImageView logoImage = (ImageView) view.findViewById(R.id.title_logo);
		if (logoImage != null && showLogo) {
			logoImage.setVisibility(View.VISIBLE);
		}

		mTitleTextView = (TextView) view.findViewById(R.id.title_name);
		mTitleTextView.setTextColor(titleColor);
		mTitleTextView.setTextSize(DrawUtils.px2sp(titleSize));

		if (titleLineHeight != -1) {
			ImageView lineImageView = (ImageView) view.findViewById(R.id.line);
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lineImageView
					.getLayoutParams();
			lp.height = titleLineHeight;
		}

		View containView = view.findViewById(R.id.containView);
		if (containView != null && titleHeight >= 0) {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) containView
					.getLayoutParams();
			lp.height = titleHeight;
		}

		mBackLayout = (LinearLayout) findViewById(R.id.back_layout);
		mBackLayout.setOnClickListener(this);
		//支持独立语言包
		DeskResourcesConfiguration resConfig = DeskResourcesConfiguration.getInstance();
		if (resConfig != null) {
			resConfig.configurationDeskSettingI18nSupporter(this, attrs);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_layout :
				((Activity) mContext).finish();
				break;

			default :
				break;
		}

	}

	/**
	 * <br>功能简述:获取返回布局
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public LinearLayout getBackLayout() {
		return mBackLayout;
	}

	/**
	 * <br>功能简述:设置标题
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param textString
	 */
	public void setTitleText(String textString) {
		mTitleTextView.setText(textString);
	}

	/**
	 * <br>功能简述:设置标题
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param resId
	 */
	@Override
	public void setTitleText(int resId) {
		if (mTitleTextView != null) {
			mTitleTextView.setText(resId);
		}
	}

	/**
	 * <br>功能简述:获取标题
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public TextView getTitleTextView() {
		return mTitleTextView;
	}

	@Override
	public void setSummaryText(int resId) {
		
	}
	
}
