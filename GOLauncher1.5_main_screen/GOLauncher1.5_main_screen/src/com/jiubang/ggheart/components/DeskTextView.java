package com.jiubang.ggheart.components;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 桌面通用TextView
 * @author yangguanxiang
 *
 */
public class DeskTextView extends TextView implements ISelfObject, TextFontInterface {
	private TextFont mTextFont;
	private Typeface mTypeface;
	private int mStyle;

	public DeskTextView(Context context) {
		super(context);
		selfConstruct();
	}

	public DeskTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		selfConstruct();

		if (null != DeskResourcesConfiguration.getInstance()) {
			DeskResourcesConfiguration.getInstance().configurationPreference(this, attrs);
		}
	}

	public DeskTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		selfConstruct();

		if (null != DeskResourcesConfiguration.getInstance()) {
			DeskResourcesConfiguration.getInstance().configurationPreference(this, attrs);
		}
	}

	@Override
	public void selfConstruct() {
		onInitTextFont();
	}

	@Override
	public void selfDestruct() {
		onUninitTextFont();
	}

	@Override
	public void onInitTextFont() {
		if (null == mTextFont) {
			mTextFont = new TextFont(this);
		}
	}

	@Override
	public void onUninitTextFont() {
		if (null != mTextFont) {
			mTextFont.selfDestruct();
			mTextFont = null;
		}
	}

	@Override
	public void onTextFontChanged(Typeface typeface, int style) {
		mTypeface = typeface;
		mStyle = style;
		setTypeface(mTypeface, mStyle);
	}

	public void reInitTypeface() {
		setTypeface(mTypeface, mStyle);
	}
	
	/**
	 * 设置为斜体
	 */
	public void setItalic() {
		Typeface tf = Typeface.DEFAULT;
		Resources res = getResources();
		if (res != null) {
			Configuration config = res.getConfiguration();
			if (config != null) {
				Locale locale = config.locale;
				if (locale != null) {
					String language = locale.getLanguage();
					if ("zh".equals(language) || "ko".equals(language) || "ja".equals(language)) {
						tf = Typeface.MONOSPACE;
					}
				}
			}
		}
		setTypeface(tf, Typeface.ITALIC);
	}
}
