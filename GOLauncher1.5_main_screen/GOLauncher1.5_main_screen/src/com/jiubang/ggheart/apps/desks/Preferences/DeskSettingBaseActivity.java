package com.jiubang.ggheart.apps.desks.Preferences;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.SettingProxy;
import com.go.util.SortUtils;
import com.go.util.window.OrientationControl;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.data.theme.bean.ThemeInfoBean;

/**
 * 
 * <br>类描述:设置Activity的父类
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-9-12]
 */
public abstract class DeskSettingBaseActivity extends DeskActivity
		implements
			OnValueChangeListener,
			OnClickListener {
	
	public static final int DIALOG_ID_INIT_LIST = 1; // 正在加载主题弹出框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (GoLauncherActivityProxy.getActivity() == null) {
			finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		int oriType = SettingProxy.getGravitySettingInfo().mOrientationType;
		OrientationControl.setOrientation(this, oriType);
//		int id = getResources().getIdentifier("permanentmemory_enable", "id", getPackageName());
//		if (id > 0) {
//			View view = findViewById(id);
//			if (view != null) {
//				view.setEnabled(false);
//			}
//		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = null;
		if (id == DIALOG_ID_INIT_LIST) {
			dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.icon_style_loading_message));
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
		}
		return dialog;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkNewMark(DeskSettingNewMarkManager.getInstance(getApplicationContext()));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//activity完全不可见时，save数据
		save();
	}

	/**
	 * <br>功能简述:加载数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void load() {
//		checkNewMark();
	}

	/**
	 * <br>功能简述:保存数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void save() {

	}
	
	
	protected abstract void checkNewMark(DeskSettingNewMarkManager newMarkManager);

	@Override
	public boolean onValueChange(DeskSettingItemBaseView view, Object value) {
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int oriType = SettingProxy.getGravitySettingInfo().mOrientationType;
		
			// 实体键盘处于推出状态，在此处添加额外的处理代码
			if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
				OrientationControl.changeOrientationByKeyboard(this, true, newConfig, oriType);
			} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
				OrientationControl.changeOrientationByKeyboard(this, false, newConfig, oriType);
			}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//退出时间注销所有DeskTextView
		DeskSettingConstants.selfDestruct(getWindow().getDecorView());
	}
	
	public void getAllThemeData() {
		// 启动异步扫描主题包
		GetAllThemesTask task = new GetAllThemesTask();
		task.execute();
	}
	
	/**
	 * 调用getAllThemeDat后的回调
	 * @param allThemePackage
	 * @param allThemeName
	 */
	protected void setAllThemesData(String[] allThemePackage, String[] allThemeName) {
		
	}
	
	/**
	 * <br>
	 * 功能简述:退出重新启动桌面 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void exitAndRestart() {
		setResult(DeskSettingConstants.RESULT_CODE_RESTART_GO_LAUNCHER, getIntent());
		save();
		this.finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == DeskSettingConstants.RESULT_CODE_RESTART_GO_LAUNCHER) {
			exitAndRestart();
		}
	}

	/**
	 * 
	 * <br>
	 * 类描述:扫描获取所有主题包 <br>
	 * 功能详细描述:
	 * 
	 * @author ruxueqin
	 * @date [2012-9-24]
	 */
	class GetAllThemesTask extends AsyncTask<Void, Void, String> {
		String[] mAllThemePackage;
		String[] mAllThemeName;

		@Override
		protected void onPreExecute() {
			// 显示扫描等待提示框
			showDialog(DIALOG_ID_INIT_LIST);
		}

		@Override
		protected String doInBackground(Void... params) {
			// 扫描全部主题
			ArrayList<ThemeInfoBean> themeInfos = ThemeManager.getInstance(ApplicationProxy.getContext()).getAllThemeInfosWithoutDefaultTheme();
			//A-Z排序
			Collections.sort(themeInfos, mComparator);
			
			int themeSize = 0;
			if (themeInfos != null) {
				themeSize = themeInfos.size();
			}

			// entries初始化
			mAllThemePackage = new String[themeSize];
			mAllThemeName = new String[themeSize];

			for (int i = 0; i < themeSize; i++) {
				mAllThemePackage[i] = themeInfos.get(i).getPackageName();
				mAllThemeName[i] = themeInfos.get(i).getThemeName();
			}

			if (null != themeInfos) {
				themeInfos.clear();
				themeInfos = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// 1:取消扫描提示框
			removeDialog(DIALOG_ID_INIT_LIST);

			setAllThemesData(mAllThemePackage, mAllThemeName);
		}
		
		/**
		 * 排序比较器，中英文按a~z进行混排
		 */
		private Comparator<ThemeInfoBean> mComparator = new Comparator<ThemeInfoBean>() {
			@Override
			public int compare(ThemeInfoBean object1, ThemeInfoBean object2) {
				int result = 0;
				String str1 = object1.getThemeName();
				String str2 = object2.getThemeName();
				str1 = SortUtils.changeChineseToSpell(DeskSettingBaseActivity.this, str1, R.raw.unicode2pinyin);
				str2 = SortUtils.changeChineseToSpell(DeskSettingBaseActivity.this, str2, R.raw.unicode2pinyin);
				Collator collator = null;
				if (Build.VERSION.SDK_INT < 16) {
					collator = Collator.getInstance(Locale.CHINESE);
				} else {
					collator = Collator.getInstance(Locale.ENGLISH);
				}

				if (collator == null) {
					collator = Collator.getInstance(Locale.getDefault());
				}
				result = collator.compare(str1.toUpperCase(), str2.toUpperCase());
				return result;
			}
		};
	}
}
