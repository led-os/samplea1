package com.jiubang.ggheart.apps.desks.Preferences.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.components.DeskResourcesConfiguration;
import com.jiubang.ggheart.components.DeskTextView;

/**
 * 
 * <br>
 * 类描述: <br>
 * 功能详细描述:
 * 
 * @author licanhui
 * @date [2012-9-10]
 */
public class DeskSettingTitleView extends RelativeLayout implements IDeskSettingI18nSupporter {
	private DeskTextView mTitleTextView;
	
	public DeskSettingTitleView(Context context) {
		super(context);
	}

	public DeskSettingTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeskSettingItemView);
	
		//标题字体大小
		float titleTextSize = a.getDimension(R.styleable.DeskSettingItemView_titleTextSize, context
			.getResources().getDimension(R.dimen.desk_setting_title_text_default_size));
		
		a.recycle();
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.desk_setting_title_view, this);

		mTitleTextView = (DeskTextView) view.findViewById(R.id.title_name);
		mTitleTextView.setTextSize(DrawUtils.px2sp(titleTextSize));
		mTitleTextView.setItalic();
		//支持独立语言包
		DeskResourcesConfiguration resConfig = DeskResourcesConfiguration.getInstance();
		if (resConfig != null) {
			resConfig.configurationDeskSettingI18nSupporter(this, attrs);
		}
	}
	
	public void setTitleText(String title) {
		mTitleTextView.setText(title);
	}

	@Override
	public void setTitleText(int resId) {
		if (mTitleTextView != null) {
			mTitleTextView.setText(resId);
		}
	}

	@Override
	public void setSummaryText(int resId) {
	}
}
