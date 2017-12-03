package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.SettingProxy;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingSeekBarItemInfo;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingSingleInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncConstants;
import com.jiubang.ggheart.apps.desks.appfunc.help.AppFuncUtils;
import com.jiubang.ggheart.apps.desks.diy.IPreferencesIds;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * @author zouguiquan
 * 
 */
public class DeskSettingAppDrawerActivity extends DeskSettingBaseActivity {

	/**
	 * 功能表行列数
	 */
	private DeskSettingItemListView mSettingFunAppRowsCols;
	/**
	 * 功能表滚屏模式
	 */
	private DeskSettingItemListView mSettingFunAppScrollType;
	/**
	 * 功能表循环切换
	 */
	private DeskSettingItemCheckBoxView mSettingAppDrawerChangeLoop;
	/**
	 * 显示应用更新提示
	 */
	private DeskSettingItemCheckBoxView mSettingShowFunAppUpdateTips;
	/**
	 * 显示顶部操作栏
	 */
	private DeskSettingItemCheckBoxView mSettingShowFunAppTab;
	/**
	 * 功能选项卡和底座背景
	 */
	private DeskSettingItemListView mAppdrawerSelectCard;

	private FunAppSetting mFunAppSettingInfo;
	private ScreenSettingInfo mScreenInfo;
	private String[] mAllThemePackage; // 所有主题包名
	private String[] mAllThemeName; // 所有主题名称

	public final static int APP_FUN_ROWS_COLS_CUSTOM = 5; // 功能表行列数自定义选项

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.desk_setting_app_drawer);
		mFunAppSettingInfo = SettingProxy.getFunAppSetting();
		mScreenInfo = SettingProxy.getScreenSettingInfo();

		// 功能表行列数
		mSettingFunAppRowsCols = (DeskSettingItemListView) findViewById(R.id.func_app_rows_cols);
		mSettingFunAppRowsCols.setOnValueChangeListener(this);

		// 功能表滚屏模式
		mSettingFunAppScrollType = (DeskSettingItemListView) findViewById(R.id.func_app_scroll_type);
		mSettingFunAppScrollType.setOnValueChangeListener(this);

		// 功能表循环切换
		mSettingAppDrawerChangeLoop = (DeskSettingItemCheckBoxView) findViewById(R.id.func_app_looping);
		mSettingAppDrawerChangeLoop.setOnValueChangeListener(this);

		// 显示应用更新提示
		mSettingShowFunAppUpdateTips = (DeskSettingItemCheckBoxView) findViewById(R.id.func_app_update_tips);
		mSettingShowFunAppUpdateTips.setOnValueChangeListener(this);

		// 显示顶部操作栏
		mSettingShowFunAppTab = (DeskSettingItemCheckBoxView) findViewById(R.id.show_fun_app_tab);
		mSettingShowFunAppTab.setOnValueChangeListener(this);

		// 选项卡和底座风格
		mAppdrawerSelectCard = (DeskSettingItemListView) findViewById(R.id.appdrawer_selectcard);
		mAppdrawerSelectCard.setSummaryText(" ");
		mAppdrawerSelectCard.setOnValueChangeListener(this);


		getAllThemeData();
		load();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_APP_DRAWER);
	}

	@Override
	public void load() {
		super.load();

		setFunAppRowsColsInfo(); // 功能表行列数

		setFunAppScrollTypeInfo(); // 功能表滚屏模式，需要放到横向滚屏特效和竖向滚屏特效后面。设置它们是否可选

		mSettingShowFunAppTab
				.setIsCheck(mFunAppSettingInfo.getShowTabRow() == FunAppSetting.ON);

		mSettingShowFunAppUpdateTips.setIsCheck(mFunAppSettingInfo
				.getAppUpdate() == FunAppSetting.ON);

		if (mFunAppSettingInfo != null) {
			boolean isLoop = mFunAppSettingInfo.getScrollLoop() == 1 ? true
					: false;
			mSettingAppDrawerChangeLoop.setIsCheck(isLoop);
		}

		/*if (mScreenInfo != null) {
			DeskSettingConstants.updateSingleChoiceListView(mAppDrawerPosition,
					mScreenInfo.mAppDrawerIndicatorPosition);
		}*/
	}

	@Override
	protected void setAllThemesData(String[] allThemePackage,
			String[] allThemeName) {
		mAllThemePackage = allThemePackage;
		mAllThemeName = allThemeName;
		initAppDrawTabBgList();
		appdrawerSelectCardList();
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
		String curThemePkg = ThemeManager.getInstance(this)
				.getCurThemePackage(); // 当前主题

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

		DeskSettingSingleInfo info = mAppdrawerSelectCard.getDeskSettingInfo()
				.getSingleInfo();

		info.setEntryValues(values);
		String[] entries = new String[size + 1];
		entries[0] = getResources().getString(R.string.defaultstyle);

		if (0 == curThemeIndex) {
			entries[0] = entries[0] + "("
					+ getResources().getString(R.string.current) + ")";
		}

		for (int i = 0; i < mAllThemeName.length; i++) {
			entries[i + 1] = mAllThemeName[i];
			if (i + 1 == curThemeIndex) {
				entries[i + 1] = entries[i + 1] + "("
						+ getResources().getString(R.string.current) + ")";
			}
		}

		info.setEntries(entries);
	}

	/**
	 * <br>
	 * 功能简述:更新主题相关view状态 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void appdrawerSelectCardList() {
		DeskSettingConstants.updateSingleChoiceListView(mAppdrawerSelectCard,
				mFunAppSettingInfo.getTabHomeBgSetting());
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

		int scrollType = DeskSettingConstants.getFunAppScrollType(direction,
				loop);
		DeskSettingConstants.setSingleInfoValueAndSummary(scrollType,
				mSettingFunAppScrollType);

		// 特效已经单独抽了出来
		// setFunAppLandAndVerEffectsState(direction); //根据滚屏模式设置横屏特效/竖屏特效是否可选
	}

	@Override
	public void save() {
		super.save();

		saveFunAppSetting(); // 保存功能表设置
		saveAppDrawerChangeLoop();
		SettingProxy.updateScreenSettingInfo(mScreenInfo);
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
	 * 功能简述:保存功能表滚动模式 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param view
	 */
	public void saveFunAppScrollType(DeskSettingItemBaseView view) {
		if (view == mSettingFunAppScrollType) {
			mSettingFunAppScrollType.updateSumarryText();

			int selectValue = Integer.parseInt(String
					.valueOf(mSettingFunAppScrollType.getSelectValue()));
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
	 * 功能简述:保存功能表设置 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public void saveFunAppSetting() {
		if (mFunAppSettingInfo != null) {
			int value;
			int isCheck;

			value = mFunAppSettingInfo.getShowTabRow();
			isCheck = DeskSettingConstants.boolean2Int(mSettingShowFunAppTab
					.getIsCheck());
			if (value != isCheck) {
				mFunAppSettingInfo.setShowTabRow(isCheck); // 直接更改数据,保护数据库
			}

			value = mFunAppSettingInfo.getAppUpdate();
			isCheck = DeskSettingConstants
					.boolean2Int(mSettingShowFunAppUpdateTips.getIsCheck());
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
		} else if (baseView == mSettingFunAppScrollType) {
			saveFunAppScrollType(baseView);
		} else if (baseView == mAppdrawerSelectCard) {
			String newValue = value.toString();
			mFunAppSettingInfo.setTabHomeBgSetting(newValue);
			DeskSettingConstants.updateSingleChoiceListView(
					mAppdrawerSelectCard, newValue);
		}

		return bRet;
	}

	/**
	 * <br>
	 * 功能简述:显示是否提示关闭"底部操作栏"对话框 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	private void showActionBarConfirmDialog() {
		final DialogConfirm mActionBarConfirmDialog = new DialogConfirm(this);
		mActionBarConfirmDialog.show();
		mActionBarConfirmDialog
				.setTitle(R.string.setting_dialog_show_action_bar_title);
		mActionBarConfirmDialog
				.setMessage(R.string.setting_dialog_show_action_bar_message);
		mActionBarConfirmDialog
				.setTipCheckBoxText(R.string.setting_dialog_dont_show_again);
		mActionBarConfirmDialog.showTipCheckBox();
		mActionBarConfirmDialog.setPositiveButton(null,
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						/*mSettingShowFunAppHomeKey.setEnabled(false);
						mSettingShowFunAppHomeKey.setIsCheck(false);*/
						CheckBox dontShowAgain = mActionBarConfirmDialog
								.getTipCheckBox();
						if (dontShowAgain.isChecked()) {
							setShowActionBarPre();
						}
					}
				});

		mActionBarConfirmDialog.setNegativeButton(null,
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						//mSettingShowFunAppActionBar.setIsCheck(true); // 重新设置勾选状态
						mActionBarConfirmDialog.dismiss();
					}
				});

		mActionBarConfirmDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// 返回按钮
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					//mSettingShowFunAppActionBar.setIsCheck(true); // 重新设置勾选状态
					dialog.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	public void setShowActionBarPre() {
		SharedPreferences sharedPref = getSharedPreferences(
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE,
				Context.MODE_PRIVATE);
		Editor edit = sharedPref.edit();
		edit.putBoolean(
				IPreferencesIds.SHOW_ALERT_DIALOG_FOR_ACTION_BAR_SETTING, false);
		edit.commit();
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
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE,
				Context.MODE_PRIVATE);
		if (sharedPref.getBoolean(
				IPreferencesIds.SHOW_ALERT_DIALOG_FOR_ACTION_BAR_SETTING, true)) {
			return true;
		} else {
			return false;
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
		DeskSettingSingleInfo singleInfo = mSettingFunAppRowsCols
				.getDeskSettingInfo().getSingleInfo();
		if (isSmallScreen) {
			CharSequence[] entries = getResources().getTextArray(
					R.array.qvga_fun_app_rows_cols);
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
		colSeekBarItemInfo
				.setTitle(getString(R.string.screen_column_dialog_msg)); // 设置标题
		colSeekBarItemInfo.setMinValue(DeskSettingConstants.ROWS_COLS_MIN_SIZE); // 设置最小值
		colSeekBarItemInfo.setMaxValue(DeskSettingConstants.ROWS_COLS_MAX_SIZE); // 设置最大值
		colSeekBarItemInfo.setSelectValue(mFunAppSettingInfo.getColNum());
		; // 设置默认选择值

		// 创建SeekBarInfo队列
		ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = new ArrayList<DeskSettingSeekBarItemInfo>();
		seekBarItemInfoList.add(rowSeekBarItemInfo);
		seekBarItemInfoList.add(colSeekBarItemInfo);

		// 小屏幕
		if (isSmallScreen) {
			DeskSettingConstants.setSecondInfoOfSeekBar(this,
					seekBarItemInfoList, R.array.qvga_fun_app_rows_cols,
					mSettingFunAppRowsCols);
		} else {
			DeskSettingConstants.setSecondInfoOfSeekBar(this,
					seekBarItemInfoList, R.array.fun_app_rows_cols,
					mSettingFunAppRowsCols);
		}
	}

	public void saveFunAppRowsCols(DeskSettingItemBaseView view) {
		if (view == mSettingFunAppRowsCols && mFunAppSettingInfo != null) {
			int curValue = Integer.parseInt(String
					.valueOf(mSettingFunAppRowsCols.getSelectValue()));
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
		CharSequence[] entries = mSettingFunAppRowsCols.getDeskSettingInfo()
				.getSingleInfo().getEntries();
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

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
	  //对于设置界面中使用需要付费的主题，则弹出付费对话框。
        String selectValue = (String) value;
        boolean isThemePay = ThemeManager.getInstance(ApplicationProxy.getContext()).getThemeResrouceBeanisPay(
                selectValue);
        if (!isThemePay) {
            DeskSettingUtils.showNeedPayThemeToast();
            return true;
        }
		return false;
	}
}