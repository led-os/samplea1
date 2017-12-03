package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager.NewMarkKeys;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingSeekBarItemInfo;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingSingleInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * @author guoyiqing
 * 
 */
public class DeskSettingFunActivity extends DeskSettingBaseActivity {

	public final static int APP_FUN_ROWS_COLS_CUSTOM = 5; // 功能表行列数自定义选项
	private DeskSettingItemListView mSettingFunAppRowsCols;
	private FunAppSetting mFunAppSettingInfo;
	private DeskSettingItemCheckBoxView mSettingShowFunAppName;
	private DeskSettingItemCheckBoxView mSettingShowFunAppTab;
	private DeskSettingItemCheckBoxView mSettingShowFunAppUpdateTips;
	private DeskSettingItemListView mAppdrawerSelectCard;
	private String[] mAllThemePackage; // 所有主题包名
	private String[] mAllThemeName; // 所有主题名称
	private DeskSettingItemListView mSettingFunAppScrollType;
	private DeskSettingItemCheckBoxView mSettingAppDrawerChangeLoop;
	private DeskSettingItemBaseView mSettingAppDrawerBackground;
	private DeskSettingItemBaseView mSettingAppDrawerGestureTransition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_fun_main);
		mFunAppSettingInfo = SettingProxy.getFunAppSetting();
		// 功能表行列数
		mSettingFunAppRowsCols = (DeskSettingItemListView) findViewById(R.id.func_app_rows_cols);
		mSettingFunAppRowsCols.setOnValueChangeListener(this);

		mSettingShowFunAppName = (DeskSettingItemCheckBoxView) findViewById(R.id.fun_app_show_app_name);
		mSettingShowFunAppName.setOnValueChangeListener(this);

		mSettingShowFunAppTab = (DeskSettingItemCheckBoxView) findViewById(R.id.show_fun_app_tab);
		mSettingShowFunAppTab.setOnValueChangeListener(this);

		mSettingShowFunAppUpdateTips = (DeskSettingItemCheckBoxView) findViewById(R.id.func_app_update_tips);
		mSettingShowFunAppUpdateTips.setOnValueChangeListener(this);

		mAppdrawerSelectCard = (DeskSettingItemListView) findViewById(R.id.fun_app_appdrawer_selectcard);
		mAppdrawerSelectCard.setOnValueChangeListener(this);

		// 功能表滚屏模式
		mSettingFunAppScrollType = (DeskSettingItemListView) findViewById(R.id.func_app_scroll_type);
		mSettingFunAppScrollType.setOnValueChangeListener(this);

		// 功能表循环切换
		mSettingAppDrawerChangeLoop = (DeskSettingItemCheckBoxView) findViewById(R.id.func_app_looping);
		mSettingAppDrawerChangeLoop.setOnValueChangeListener(this);

		mSettingAppDrawerBackground = (DeskSettingItemBaseView) findViewById(R.id.func_app_background);
		mSettingAppDrawerBackground.setOnClickListener(this);

		mSettingAppDrawerGestureTransition = (DeskSettingItemBaseView) findViewById(R.id.func_app_gesture_and_transition);
		mSettingAppDrawerGestureTransition.setOnClickListener(this);

		getAllThemeData();
		loadFunApp();
	}

	@Override
	public void save() {
		super.save();
		saveFunAppSetting();
		saveAppDrawerChangeLoop();
	}

	/**
	 * <br>
	 * 功能简述:设置功能表滚屏模式 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void setFunAppScrollTypeInfo() {
		int direction = mFunAppSettingInfo.getTurnScreenDirection();
		int loop = mFunAppSettingInfo.getScrollLoop();

		int scrollType = DeskSettingConstants.getFunAppScrollType(direction, loop);
		DeskSettingConstants.setSingleInfoValueAndSummary(scrollType, mSettingFunAppScrollType);

		// 特效已经单独抽了出来
		// setFunAppLandAndVerEffectsState(direction); //根据滚屏模式设置横屏特效/竖屏特效是否可选
	}

	/**
	 * <br>
	 * 功能简述:保存功能表滚动模式 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param view
	 */
	public void saveFunAppScrollType(DeskSettingItemBaseView view) {
		if (view == mSettingFunAppScrollType) {
			mSettingFunAppScrollType.updateSumarryText();

			int selectValue = Integer.parseInt(String.valueOf(mSettingFunAppScrollType
					.getSelectValue()));
			// 是否循环模式
			// if (selectValue == FunAppSetting.SCREEN_SCROLL_VERTICAL
			// || selectValue == FunAppSetting.SCREEN_SCROLL_HORIZONTAL) {
			// mFunAppSettingInfo.setScrollLoop(0);
			// } else {
			// mFunAppSettingInfo.setScrollLoop(1);
			// }

			// 修改翻屏风格
			int direction = FunAppSetting.SCREEN_SCROLL_HORIZONTAL;

			if (selectValue == FunAppSetting.SCREEN_SCROLL_VERTICAL) {
				direction = FunAppSetting.SCREEN_SCROLL_VERTICAL;
			}
			mFunAppSettingInfo.setTurnScreenDirection(direction);

			// 特效已经单独抽了出来
			// setFunAppLandAndVerEffectsState(direction);
			// //根据滚屏模式设置横屏特效/竖屏特效是否可选
		}
	}

	/**
	 * <br>
	 * 功能简述:外部设置主题包数据 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param themePkgs
	 * @param themeNames
	 */
	public void setAllThemesData(String[] themePkgs, String[] themeNames) {
		mAllThemePackage = themePkgs;
		mAllThemeName = themeNames;
		initAppDrawTabBgList();

		appdrawerSelectCardList();
	}

	/**
	 * <br>
	 * 功能简述:更新主题相关view状态 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void appdrawerSelectCardList() {
		if (null != mFunAppSettingInfo) {
			DeskSettingConstants.updateSingleChoiceListView(mAppdrawerSelectCard,
					mFunAppSettingInfo.getTabHomeBgSetting());
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * <br>
	 * 功能简述:初始化功能表背景选择项信息 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void initAppDrawTabBgList() {

		int size = mAllThemeName.length;
		int curThemeIndex = 0;
		String curThemePkg = ThemeManager.getInstance(this).getCurThemePackage(); // 当前主题

		String[] values = new String[size + 1];
		values[0] = PackageName.PACKAGE_NAME;

		if (curThemePkg.equals(values[0])) {
			curThemeIndex = 0;
		}

		for (int i = 0; i < mAllThemePackage.length; i++) {

			values[i + 1] = mAllThemePackage[i];
			if (curThemePkg.equals(values[i + 1])) {
				curThemeIndex = i + 1;
			}
		}

		DeskSettingSingleInfo info = mAppdrawerSelectCard.getDeskSettingInfo().getSingleInfo();

		info.setEntryValues(values);
		String[] entries = new String[size + 1];
		entries[0] = getResources().getString(R.string.defaultstyle);

		if (0 == curThemeIndex) {
			entries[0] = entries[0] + "(" + getResources().getString(R.string.current) + ")";
		}

		for (int i = 0; i < mAllThemeName.length; i++) {
			entries[i + 1] = mAllThemeName[i];
			if (i + 1 == curThemeIndex) {
				entries[i + 1] = entries[i + 1] + "(" + getResources().getString(R.string.current)
						+ ")";
			}
		}

		info.setEntries(entries);
	}

	/**
	 * <br>
	 * 功能简述:保存功能表设置 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void saveFunAppSetting() {
		if (mFunAppSettingInfo != null) {
			int value;
			int isCheck;

			value = mFunAppSettingInfo.getShowTabRow();
			isCheck = DeskSettingConstants.boolean2Int(mSettingShowFunAppTab.getIsCheck());
			if (value != isCheck) {
				mFunAppSettingInfo.setShowTabRow(isCheck); // 直接更改数据,保护数据库
			}

			value = mFunAppSettingInfo.getAppNameVisiable();
			isCheck = DeskSettingConstants.boolean2Int(mSettingShowFunAppName.getIsCheck());
			if (value != isCheck) {
				mFunAppSettingInfo.setAppNameVisiable(isCheck);
			}

			value = mFunAppSettingInfo.getAppUpdate();
			isCheck = DeskSettingConstants.boolean2Int(mSettingShowFunAppUpdateTips.getIsCheck());
			if (value != isCheck) {
				mFunAppSettingInfo.setAppUpdate(isCheck);
			}

		}
	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
		boolean bRet = true;
		if (baseView == mSettingFunAppRowsCols) {
			saveFunAppRowsCols(baseView); // 保存功能表行列数
		}
		if (baseView == mSettingFunAppScrollType) {
			saveFunAppScrollType(baseView);
		} else if (baseView == mAppdrawerSelectCard) {
			mFunAppSettingInfo.setTabHomeBgSetting(value.toString());
			DeskSettingConstants.updateSingleChoiceListView(mAppdrawerSelectCard, value.toString());
		}
		/*
		 * else if (baseView == mSettingShowScreenMargins) { if ((Boolean) value
		 * == true) { mSettingShowScreenMargins.setIsCheck(true); } else {
		 * mSettingShowScreenMargins.setIsCheck(false); } }
		 */

		return bRet;
	}

	/**
	 * <br>
	 * 功能简述:判断是否需要提示对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @return
	 */
	public boolean getShowActionBarPre() {
		SharedPreferences sharedPref = getSharedPreferences(
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		if (sharedPref.getBoolean(IPreferencesIds.SHOW_ALERT_DIALOG_FOR_ACTION_BAR_SETTING, true)) {
			return true;
		} else {
			return false;
		}
	}

	public void setShowActionBarPre() {
		SharedPreferences sharedPref = getSharedPreferences(
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		Editor edit = sharedPref.edit();
		edit.putBoolean(IPreferencesIds.SHOW_ALERT_DIALOG_FOR_ACTION_BAR_SETTING, false);
		edit.commit();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.func_app_background) {
			startActivity(new Intent(this, DeskSettingFunBackgroundActivity.class));
		} else if (v.getId() == R.id.func_app_gesture_and_transition) {
			startActivity(new Intent(this, DeskSettingFunGesAndTranActivity.class));
		}
	}

	/**
	 * <br>
	 * 功能简述:保存功能表行列数 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param view
	 */
	public void saveFunAppRowsCols(DeskSettingItemBaseView view) {
		if (view == mSettingFunAppRowsCols && mFunAppSettingInfo != null) {
			int curValue = Integer
					.parseInt(String.valueOf(mSettingFunAppRowsCols.getSelectValue()));
			// 自定义
			if (curValue == APP_FUN_ROWS_COLS_CUSTOM) {
				ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = DeskSettingConstants
						.getSecondSeekBarItemInfo(mSettingFunAppRowsCols);
				if (seekBarItemInfoList != null) {
					// 获取选择的seekbar值
					int rowsValue = seekBarItemInfoList.get(0).getSelectValue();
					int colsValue = seekBarItemInfoList.get(1).getSelectValue();
					// 判断自定义的值是否改变
					if (mFunAppSettingInfo.getRowNum() != rowsValue
							|| mFunAppSettingInfo.getColNum() != colsValue) {
						mFunAppSettingInfo.setRowNum(rowsValue);
						mFunAppSettingInfo.setColNum(colsValue);
					}
					mFunAppSettingInfo.setLineColumnNum(curValue); // 设置当前选择类型并保持
				}
			} else {
				// 先判断选项是否有更改
				int oldValue = mFunAppSettingInfo.getLineColumnNum();
				if (curValue != oldValue) {
					// 只改变FunAppSetting数据库中行列数的值，没对grid行列数进行修改。
					AppFuncUtils.getInstance(this).setGridStandard(curValue,
							AppFuncConstants.ALLAPPS_GRID);
					mFunAppSettingInfo.setLineColumnNum(curValue); // 设置当前选择类型
				}
			}

			// 更新Sumarry
			updateFunAppRowsCoslEntries(curValue); // 设置单选列表的选择值和更新summary
			mSettingFunAppRowsCols.updateSumarryText(); // 更新summary
		}
	}

	/**
	 * <br>
	 * 功能简述:更新功能表行列数的Entries <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param value
	 */
	private void updateFunAppRowsCoslEntries(int type) {
		CharSequence[] entries = mSettingFunAppRowsCols.getDeskSettingInfo().getSingleInfo()
				.getEntries();
		int entriesSize = entries.length;

		// 设置自定义的显示值为：自定义行列（6X6）
		if (type == APP_FUN_ROWS_COLS_CUSTOM) {
			entries[entriesSize - 1] = getString(R.string.screen_grid_diy)
					+ " (" + mFunAppSettingInfo.getRowNum() + "*"
					+ mFunAppSettingInfo.getColNum() + ")";
		} else {
			entries[entriesSize - 1] = getString(R.string.screen_grid_diy);
		}
	}

	/**
	 * <br>
	 * 功能简述:加载功能表设置 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void loadFunApp() {
		if (mFunAppSettingInfo != null) {
			mSettingShowFunAppTab
					.setIsCheck(mFunAppSettingInfo.getShowTabRow() == FunAppSetting.ON);

			mSettingShowFunAppName
					.setIsCheck(mFunAppSettingInfo.getAppNameVisiable() == FunAppSetting.ON);

			mSettingShowFunAppUpdateTips
					.setIsCheck(mFunAppSettingInfo.getAppUpdate() == FunAppSetting.ON);

			if (mFunAppSettingInfo != null) {
				boolean isLoop = mFunAppSettingInfo.getScrollLoop() == 1 ? true : false;
				mSettingAppDrawerChangeLoop.setIsCheck(isLoop);
			}

			setFunAppRowsColsInfo(); // 功能表行列数
			setFunAppScrollTypeInfo();
			
		}
	}

	/**
	 * <br>
	 * 功能简述:保存屏幕循环切换 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void saveAppDrawerChangeLoop() {
		if (mFunAppSettingInfo != null) {
			if (mSettingAppDrawerChangeLoop.getIsCheck()) {
				mFunAppSettingInfo.setScrollLoop(1);
			} else {
				mFunAppSettingInfo.setScrollLoop(0);
			}
		}
	}

	/**
	 * <br>
	 * 功能简述:设置功能表行列数 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void setFunAppRowsColsInfo() {
		if (mFunAppSettingInfo == null) {
			return;
		}

		boolean isSmallScreen = DeskSettingConstants.checkIsSmallScreen(); // 是否小屏幕

		// 检查是否小屏幕手机，是的话就重新设置选项
		DeskSettingSingleInfo singleInfo = mSettingFunAppRowsCols.getDeskSettingInfo()
				.getSingleInfo();
		if (isSmallScreen) {
			CharSequence[] entries = getResources().getTextArray(R.array.qvga_fun_app_rows_cols);
			CharSequence[] entryValues = getResources().getTextArray(
					R.array.qvga_fun_app_rows_cols_value);
			singleInfo.setEntries(entries);
			singleInfo.setEntryValues(entryValues);
		}

		int funAppRowsColsType = mFunAppSettingInfo.getLineColumnNum();
		updateFunAppRowsCoslEntries(funAppRowsColsType);
		DeskSettingConstants.setSingleInfoValueAndSummary(funAppRowsColsType,
				mSettingFunAppRowsCols);

		// 行数seekBar
		DeskSettingSeekBarItemInfo rowSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
		rowSeekBarItemInfo.setTitle(getString(R.string.screen_row_dialog_msg)); // 设置标题
		rowSeekBarItemInfo.setMinValue(DeskSettingConstants.ROWS_COLS_MIN_SIZE); // 设置最小值
		rowSeekBarItemInfo.setMaxValue(DeskSettingConstants.ROWS_COLS_MAX_SIZE); // 设置最大值
		rowSeekBarItemInfo.setSelectValue(mFunAppSettingInfo.getRowNum()); // 设置默认选择值,默认值为4

		// 列数seekBar
		DeskSettingSeekBarItemInfo colSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
		colSeekBarItemInfo.setTitle(getString(R.string.screen_column_dialog_msg)); // 设置标题
		colSeekBarItemInfo.setMinValue(DeskSettingConstants.ROWS_COLS_MIN_SIZE); // 设置最小值
		colSeekBarItemInfo.setMaxValue(DeskSettingConstants.ROWS_COLS_MAX_SIZE); // 设置最大值
		colSeekBarItemInfo.setSelectValue(mFunAppSettingInfo.getColNum());; // 设置默认选择值

		// 创建SeekBarInfo队列
		ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = new ArrayList<DeskSettingSeekBarItemInfo>();
		seekBarItemInfoList.add(rowSeekBarItemInfo);
		seekBarItemInfoList.add(colSeekBarItemInfo);

		// 小屏幕
		if (isSmallScreen) {
			DeskSettingConstants.setSecondInfoOfSeekBar(this, seekBarItemInfoList,
					R.array.qvga_fun_app_rows_cols, mSettingFunAppRowsCols);
		} else {
			DeskSettingConstants.setSecondInfoOfSeekBar(this, seekBarItemInfoList,
					R.array.fun_app_rows_cols, mSettingFunAppRowsCols);
		}
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		if (newMarkManager.isShowNew(NewMarkKeys.APPDRAWER_GESTURE_TRANSITION, false)) {
			mSettingAppDrawerGestureTransition.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingAppDrawerGestureTransition.setImageNewVisibile(View.GONE);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}

}
