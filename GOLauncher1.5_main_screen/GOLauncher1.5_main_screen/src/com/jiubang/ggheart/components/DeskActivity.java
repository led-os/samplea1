package com.jiubang.ggheart.components;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.go.util.log.LogConstants;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.apps.desks.diy.PreferencesManager;

/**
 * 
 * @author 
 *
 */
public class DeskActivity extends Activity implements ISelfObject, TextFontInterface {
	private TextFont mTextFont;
	private ArrayList<TextView> mTextViews = new ArrayList<TextView>();

	@Override
	public void selfConstruct() {

	}

	@Override
	public void selfDestruct() {
		mTextViews.clear();
		mTextViews = null;

		onUninitTextFont();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewFinder.findView(getWindow().getDecorView(), mTextViews);
		onInitTextFont();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		selfDestruct();
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
		int sz = mTextViews.size();
		for (int i = 0; i < sz; i++) {
			TextView textView = mTextViews.get(i);
			if (null == textView) {
				continue;
			}
			textView.setTypeface(typeface, style);
		}
	}

	@Override
	public Resources getResources() {
		DeskResourcesConfiguration configuration = DeskResourcesConfiguration.getInstance();
		if (null != configuration) {
			Resources resources = configuration.getDeskResources();
			if (null != resources) {
				return resources;
			}
		}

		return super.getResources();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Resources res = getResources();
		if (res instanceof DeskResources) {

			try {
				res.updateConfiguration(super.getResources().getConfiguration(), super.getResources()
						.getDisplayMetrics());
				Configuration config = res.getConfiguration(); //获得设置对象
				DisplayMetrics dm = res.getDisplayMetrics(); //获得屏幕参数：主要是分辨率，像素等。
				PreferencesManager preferences = new PreferencesManager(this,
						IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
				String currentlanguage = preferences.getString(
						IPreferencesIds.CURRENT_SELECTED_LANGUAGE, "");
				if (currentlanguage != null && !currentlanguage.equals("")) {
					if (currentlanguage.length() == 5) {
						String language = currentlanguage.substring(0, 2);
						String country = currentlanguage.substring(3, 5);
						config.locale = new Locale(language, country);
					} else {
						config.locale = new Locale(currentlanguage);
					}
					res.updateConfiguration(config, dm);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		try {
			super.onBackPressed();
		} catch (Exception e) {
			Log.e(LogConstants.HEART_TAG, "onBackPressed err " + e.getMessage());
		}
	}
}
