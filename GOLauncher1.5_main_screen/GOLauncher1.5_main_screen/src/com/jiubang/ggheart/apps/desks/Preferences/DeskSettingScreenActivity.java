package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.ArrayList;

import android.os.Bundle;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.window.OrientationControl;
import com.golauncher.message.IAppCoreMsgId;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenAdvertMsgId;
import com.golauncher.message.IScreenFrameMsgId;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingSeekBarItemInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemCheckBoxView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.data.info.DesktopSettingInfo;
import com.jiubang.ggheart.data.info.EffectSettingInfo;
import com.jiubang.ggheart.data.info.GravitySettingInfo;
import com.jiubang.ggheart.data.info.ScreenSettingInfo;
import com.jiubang.ggheart.data.info.ScreenStyleConfigInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.theme.ThemeManager;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * @author zouguiquan
 *
 */
public class DeskSettingScreenActivity extends DeskSettingBaseActivity {
	
	/**
	 * 屏幕行列数
	 */
	private DeskSettingItemListView mSettingScreenRowsCols;
	/**
	 * 显示桌面状态栏
	 */
	private DeskSettingItemCheckBoxView mSettingShowDeskStatusbar;
	/**
	 * 屏幕方向
	 */
	private DeskSettingItemListView mSettingScreenOrientation;
	/**
	 * 屏幕切换速度
	 */
	private DeskSettingItemListView mSettingScreenChangeSpeed;
	/**
	 * 屏幕循环切换
	 */
	private DeskSettingItemCheckBoxView mSettingScreenChangeLoop;
	
	/**
	 * 显示桌面边距
	 */
	private DeskSettingItemCheckBoxView mSettingScreenShowMargin;
	
	/**
	 * 指示器风格
	 */
	private DeskSettingItemListView mMode; 
	/**
	 * 指示器位置
	 */
	private DeskSettingItemListView mPosition; 
	/**
	 * 桌面指示器
	 */
	private DeskSettingItemListView mScreenIndicator;
	
	//主题信息
	String[] mAllThemePackage; //包名
	String[] mAllThemeName; //主题名
	
	private DesktopSettingInfo mDesktopSettingInfo;
	private GravitySettingInfo mGravitySettingInfo;
	private EffectSettingInfo mEffectSettingInfo;
	private ScreenSettingInfo mScreenInfo; // 屏幕设置信息
	private ScreenStyleConfigInfo mScreenStyleInfo;
	private FunAppSetting mFunAppSetting;
	
	private final static int SCREEN_ROWS_COLS_CUSTOM = 4; //屏幕行列数自定义选项

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.desk_setting_new_screen);
		
		mDesktopSettingInfo = SettingProxy.getDesktopSettingInfo();
		mGravitySettingInfo = SettingProxy.getGravitySettingInfo();
		mEffectSettingInfo = SettingProxy.getEffectSettingInfo();
		mScreenInfo = SettingProxy.getScreenSettingInfo();
		mScreenStyleInfo = ThemeManager.getInstance(this).getScreenStyleSettingInfo();
		mFunAppSetting = SettingProxy.getFunAppSetting();
		mSettingScreenRowsCols = (DeskSettingItemListView) findViewById(R.id.screen_rows_cols);
		mSettingScreenRowsCols.setOnValueChangeListener(this);
		
		mSettingShowDeskStatusbar = (DeskSettingItemCheckBoxView) findViewById(R.id.desk_statusbar);
		mSettingShowDeskStatusbar.setOnValueChangeListener(this);
		
		mSettingScreenShowMargin = (DeskSettingItemCheckBoxView) findViewById(R.id.screen_showmargin);
		mSettingScreenShowMargin.setOnValueChangeListener(this);
		
		mSettingScreenOrientation = (DeskSettingItemListView) findViewById(R.id.screen_orientation);
		mSettingScreenOrientation.setOnValueChangeListener(this);
		
		mSettingScreenChangeSpeed = (DeskSettingItemListView) findViewById(R.id.screen_transition_effect);
		mSettingScreenChangeSpeed.setOnValueChangeListener(this);
		
		// 屏幕循环切换
		mSettingScreenChangeLoop = (DeskSettingItemCheckBoxView) findViewById(R.id.screen_looping);
		mSettingScreenChangeLoop.setOnValueChangeListener(this);
		
		mMode = (DeskSettingItemListView) findViewById(R.id.mode);
		mMode.setSummaryText(" ");
		mMode.setOnValueChangeListener(this);
		
		mPosition = (DeskSettingItemListView) findViewById(R.id.screen_position);
		mPosition.setOnValueChangeListener(this);
		
		mScreenIndicator = (DeskSettingItemListView) findViewById(R.id.screen_indicator);
		mScreenIndicator.setOnValueChangeListener(this);
		
		load();
		getAllThemeData();
		GuiThemeStatistics
		.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_SCREENS);
	}
	
	@Override
	public void load() {
		super.load();
		
		if (mGravitySettingInfo != null) {

			//屏幕方向
			int oldSelectValue = mGravitySettingInfo.mOrientationType;
			DeskSettingConstants.setSingleInfoValueAndSummary(oldSelectValue,
					mSettingScreenOrientation);
		}
		
		if (null != mScreenInfo) {
			mSettingScreenChangeLoop.setIsCheck(mScreenInfo.mScreenLooping);
			DeskSettingConstants.updateSingleChoiceListView(mScreenIndicator, getScreenIndicatorValue());
			DeskSettingConstants.updateSingleChoiceListView(mPosition, mScreenInfo.mIndicatorPosition);
		}
		
		loadDesk();
		//设置屏幕切换速度
		setSettingScreenChangeSpeed();
	}
	
	@Override
	protected void setAllThemesData(String[] allThemePackage, String[] allThemeName) {
		mAllThemePackage = allThemePackage;
		mAllThemeName = allThemeName;

		initDeskIndicatorList();
	}
	
	/**
	 * <br>功能简述:设置指示器风格数据
	 * <br>功能详细描述:
	 * <br>注意:在主题数据扫描完成后设置
	 */
	private void initDeskIndicatorList() {
		int size = mAllThemeName.length;
		int curThemeIndex = 0;
		String curThemePkg = ThemeManager.getInstance(this).getCurThemePackage(); // 当前主题

		String[] tmpEntris = getResources().getStringArray(R.array.screen_indicator_showmode_array);
		String[] tmpValues = getResources()
				.getStringArray(R.array.screen_indicator_showmode_values);
		String[] values = new String[size + tmpValues.length];
		String[] entries = new String[size + tmpEntris.length];
		if (tmpValues.length != tmpEntris.length) {
			return;
		}
		for (int i = 0; i < tmpEntris.length; i++) {
			entries[i] = tmpEntris[i];
			values[i] = tmpValues[i];
		}

		if (curThemePkg.equals(PackageName.PACKAGE_NAME)) {
			curThemeIndex = 0;
		}

		for (int i = 0; i < mAllThemePackage.length; i++) {

			values[i + tmpEntris.length] = mAllThemePackage[i];
			if (curThemePkg.equals(values[i + tmpEntris.length])) {
				curThemeIndex = i + tmpEntris.length;
			}
		}

		mMode.getDeskSettingInfo().getSingleInfo().setEntryValues(values);

		if (0 == curThemeIndex) {
			entries[0] = entries[0] + "(" + getResources().getString(R.string.current) + ")";
		}

		for (int i = 0; i < mAllThemeName.length; i++) {
			entries[i + tmpValues.length] = mAllThemeName[i];
			if (i + tmpValues.length == curThemeIndex) {
				entries[i + tmpValues.length] = entries[i + tmpValues.length] + "("
						+ getResources().getString(R.string.current) + ")";
			}
		}

		mMode.getDeskSettingInfo().getSingleInfo().setEntries(entries);

		if (null != mScreenInfo) {
			DeskSettingConstants.updateSingleChoiceListView(mMode, mScreenStyleInfo.getIndicatorStyle());
		}
	}
	
	/**
	 * <br>功能简述:设置屏幕切换速度
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setSettingScreenChangeSpeed() {
		if (mEffectSettingInfo != null) {
			//获取选择的类型值
			int screenChangeSpeedType = DeskSettingConstants.getScreenChangeSpeedType(
					mEffectSettingInfo.mScrollSpeed, mEffectSettingInfo.mBackSpeed);
			updateScreenChangeSpeedEntries(screenChangeSpeedType); //设置单选列表的选择值和更新summary

			DeskSettingConstants.setSingleInfoValueAndSummary(screenChangeSpeedType,
					mSettingScreenChangeSpeed); //设置单选列表的选择值和更新summary

			//速度seekBar
			DeskSettingSeekBarItemInfo speedSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
			speedSeekBarItemInfo.setTitle(getString(R.string.screen_speed)); //设置标题
			speedSeekBarItemInfo.setMinValue(DeskSettingConstants.SCREEN_CHANGE_SPEED_MIN); //设置最小值
			speedSeekBarItemInfo.setMaxValue(DeskSettingConstants.SCREEN_CHANGE_SPEED_MAX); //设置最大值
			speedSeekBarItemInfo.setSelectValue(mEffectSettingInfo.mScrollSpeed);; //设置默认选择值

			//弹力seekBar
			DeskSettingSeekBarItemInfo elasticSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
			elasticSeekBarItemInfo.setTitle(getString(R.string.screen_elastic)); //设置标题
			elasticSeekBarItemInfo
					.setMinValue(DeskSettingConstants.SCREEN_CHANGE_SPEED_ELASTIC_MIN); //设置最小值
			elasticSeekBarItemInfo
					.setMaxValue(DeskSettingConstants.SCREEN_CHANGE_SPEED_ELASTIC_MAX); //设置最大值
			elasticSeekBarItemInfo.setSelectValue(mEffectSettingInfo.mBackSpeed); //设置默认选择值

			//创建SeekBarInfo队列
			ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = new ArrayList<DeskSettingSeekBarItemInfo>();
			seekBarItemInfoList.add(speedSeekBarItemInfo);
			seekBarItemInfoList.add(elasticSeekBarItemInfo);

			DeskSettingConstants.setSecondInfoOfSeekBar(this, seekBarItemInfoList,
					R.array.screen_transition_entris, mSettingScreenChangeSpeed);

		}
	}
	
	/**
	 * <br>功能简述:加载桌面设置
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void loadDesk() {
		if (mDesktopSettingInfo != null) {
			//设置桌面状态栏
			mSettingShowDeskStatusbar.setIsCheck(mDesktopSettingInfo.mShowStatusbar);

			setSettingScreenRowsColsInfo();

			if (mDesktopSettingInfo.getMarginEnable()) {
				mSettingScreenShowMargin.setIsCheck(true);
			} else {
				mSettingScreenShowMargin.setIsCheck(false);
			}
		}		
	}
	
	/**
	 * <br>功能简述:设置桌面行列数
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setSettingScreenRowsColsInfo() {
		if (mDesktopSettingInfo != null) {

			//屏幕行列数
			//设置自动调整小部件和图标大小
			boolean autofit = mDesktopSettingInfo.getAutoFit();
			mSettingScreenRowsCols.getDeskSettingInfo().getSingleInfo().setCheckBoxIsCheck(autofit);

			int screenRowsColsType = mDesktopSettingInfo.mStyle;
			updateSceenRowsCoslEntries(screenRowsColsType);
			DeskSettingConstants.setSingleInfoValueAndSummary(screenRowsColsType,
					mSettingScreenRowsCols);

			//行数seekBar
			DeskSettingSeekBarItemInfo rowSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
			rowSeekBarItemInfo.setTitle(getString(R.string.screen_row_dialog_msg)); //设置标题
			rowSeekBarItemInfo.setMinValue(DeskSettingConstants.ROWS_COLS_MIN_SIZE); //设置最小值
			rowSeekBarItemInfo.setMaxValue(DeskSettingConstants.ROWS_COLS_MAX_SIZE); //设置最大值
			rowSeekBarItemInfo.setSelectValue(mDesktopSettingInfo.mRow); //设置默认选择值

			//列数seekBar
			DeskSettingSeekBarItemInfo colSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
			colSeekBarItemInfo.setTitle(getString(R.string.screen_column_dialog_msg)); //设置标题
			colSeekBarItemInfo.setMinValue(DeskSettingConstants.ROWS_COLS_MIN_SIZE); //设置最小值
			colSeekBarItemInfo.setMaxValue(DeskSettingConstants.ROWS_COLS_MAX_SIZE); //设置最大值
			colSeekBarItemInfo.setSelectValue(mDesktopSettingInfo.mColumn);; //设置默认选择值

			//创建SeekBarInfo队列
			ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = new ArrayList<DeskSettingSeekBarItemInfo>();
			seekBarItemInfoList.add(rowSeekBarItemInfo);
			seekBarItemInfoList.add(colSeekBarItemInfo);
			DeskSettingConstants.setSecondInfoOfSeekBar(this, seekBarItemInfoList,
					R.array.screen_rows_cols_title, mSettingScreenRowsCols);
		}

	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView baseView, Object value) {
		boolean bRet = true;
		//显示桌面状态栏
		if (baseView == mSettingShowDeskStatusbar) {
			save(); // 桌面状态栏，则需立刻保存，因为需要立即生效
		}
		
		if (baseView == mSettingScreenRowsCols) {
			changeScreenRowsCols(baseView, value);
		} else if (baseView == mSettingScreenOrientation) {
			saveScreenOrientation(baseView); //保存屏幕方向
		} else if (baseView == mSettingScreenChangeSpeed) {
			saveScreenChangeSpeed(baseView); //屏幕切换速度
		} else if (baseView == mSettingScreenChangeLoop) {
			if (null != mScreenInfo) {
				if (mScreenInfo.mScreenLooping != mSettingScreenChangeLoop.getIsCheck()) {
					mScreenInfo.mScreenLooping = mSettingScreenChangeLoop.getIsCheck();
					SettingProxy.updateScreenSettingInfo(mScreenInfo); //保存
				}
			}
		} else if (baseView == mMode || baseView == mPosition || baseView == mScreenIndicator) {
			DeskSettingConstants.updateSingleChoiceListView((DeskSettingItemListView) baseView,
					(String) value);
		}
		
		return bRet;
	}
	
	/**
	 * <br>功能简述:屏幕切换速度
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 */
	public void saveScreenChangeSpeed(DeskSettingItemBaseView view) {
		if (view == mSettingScreenChangeSpeed && mEffectSettingInfo != null) {
			int curValue = Integer.parseInt(String.valueOf(mSettingScreenChangeSpeed
					.getSelectValue()));
			//自定义类型-调节条队列数据
			ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = DeskSettingConstants
					.getSecondSeekBarItemInfo(mSettingScreenChangeSpeed);

			if (seekBarItemInfoList != null) {
				//自定义类型
				if (curValue == DeskSettingConstants.SCREEN_CHANGE_SPEED_TYPE_CUSTOM) {

					//获取选择的seekbar值
					int speedValue = seekBarItemInfoList.get(0).getSelectValue();
					int elasticValue = seekBarItemInfoList.get(1).getSelectValue();

					//判断值是否有改变
					if (mEffectSettingInfo.mScrollSpeed != speedValue
							|| mEffectSettingInfo.mBackSpeed != elasticValue) {
						mEffectSettingInfo.mScrollSpeed = speedValue;
						mEffectSettingInfo.mBackSpeed = elasticValue;

						SettingProxy.updateEffectSettingInfo(mEffectSettingInfo); //保存
					}
				} else {
					//通过选择得值（快速、普通、缓慢）获取速度和弹力值
					int speedValue = DeskSettingConstants.getScreenChangeSpeedSize(curValue);
					int elasticValue = DeskSettingConstants.getScreenChangeSpeedNormalElastic();

					//判断值是否有改变
					if (mEffectSettingInfo.mScrollSpeed != speedValue
							|| mEffectSettingInfo.mBackSpeed != elasticValue) {
						mEffectSettingInfo.mScrollSpeed = speedValue;
						mEffectSettingInfo.mBackSpeed = elasticValue;

						//设置自定义条的默认值，但选择默认速度60。再点击自定义时就默认选择60
						seekBarItemInfoList.get(0).setSelectValue(speedValue);
						seekBarItemInfoList.get(1).setSelectValue(elasticValue);

						SettingProxy.updateEffectSettingInfo(mEffectSettingInfo); //保存
					}
				}

				//更新Sumarry
				updateScreenChangeSpeedEntries(curValue); //设置单选列表的选择值和更新summary
				mSettingScreenChangeSpeed.updateSumarryText(); //设置单选列表的选择值和更新summary
			}
		}
	}
	
	/**
	 * <br>功能简述:更新屏切换速度的summary
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param value
	 */
	private void updateScreenChangeSpeedEntries(int type) {
		CharSequence[] entries = mSettingScreenChangeSpeed.getDeskSettingInfo().getSingleInfo()
				.getEntries();
		int entriesSize = entries.length;

		//设置自定义的显示值为：自定义（速度：100）
		if (type == DeskSettingConstants.SCREEN_CHANGE_SPEED_TYPE_CUSTOM) {
			entries[entriesSize - 1] = getString(R.string.desk_setting_custom_string)
					+ " (" + getString(R.string.screen_speed) + ":"
					+ mEffectSettingInfo.mScrollSpeed + ")";
		} else {
			entries[entriesSize - 1] = getString(R.string.desk_setting_custom_string);
		}
	}
	
	/**
	 * <br>功能简述:保存屏幕方向
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 * @param value
	 */
	public void saveScreenOrientation(DeskSettingItemBaseView view) {
		if (view == mSettingScreenOrientation) {
			mSettingScreenOrientation.updateSumarryText();
			mGravitySettingInfo.mOrientationType = Integer.parseInt(String
					.valueOf(mSettingScreenOrientation.getSelectValue()));
			SettingProxy.updateGravitySettingInfo(mGravitySettingInfo);
			// 检查屏幕翻转设置，并应用
			int oriType = SettingProxy.getGravitySettingInfo().mOrientationType;
			OrientationControl.setOrientation(this, oriType);
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCHEDULE_FRAME,
					ICommonMsgId.RESET_ORIENTATION, -1, null, null);
		}
	}
	
	@Override
	public void save() {
		super.save();
		saveDeskSetting(); //保存桌面设置
		saveIndicatorSetting();
	}
	
	private void saveIndicatorSetting() {
		boolean bChanged = false;
		String indicatorPackage = mScreenStyleInfo.getIndicatorStyle();
		
		String indicatormode = mMode.getDeskSettingInfo().getSingleInfo().getSelectValue();
		String indicatorposition = mPosition.getDeskSettingInfo().getSingleInfo().getSelectValue();
		String screenindicator = mScreenIndicator.getDeskSettingInfo().getSingleInfo().getSelectValue();

		if (!getScreenIndicatorValue().equals(screenindicator)) {
			setScreenIndicatorValue(screenindicator);
			bChanged = true;
		}

		// 桌面设置指示器位置
		if (!mScreenInfo.mIndicatorPosition.equals(indicatorposition)) {
			mScreenInfo.mIndicatorPosition = indicatorposition;
			//3D插件
			MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
					IScreenFrameMsgId.SCREEN_INDICRATOR_POSITION, -1, indicatorposition, null);

			bChanged = true;

		}

		// 设置指示器模式
		if (!indicatorPackage.equals(indicatormode)) {
			mScreenStyleInfo.setIndicatorStyle(indicatormode);
			mFunAppSetting.setIndicatorSetting(indicatormode);
			MsgMgrProxy.sendBroadcastHandler(this, IAppCoreMsgId.REFRESH_SCREENINDICATOR_THEME, -1,
					null, null);
		}

		if (bChanged) {
			SettingProxy.updateScreenSettingInfo(mScreenInfo);
		}
	}
	
	/**
	 * <br>功能简述:获取桌面指示器菜单项的值
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private String getScreenIndicatorValue() {
		String items[] = getResources().getStringArray(R.array.screen_indicator_values);
		if (items != null && items.length >= 3) {
			if (mScreenInfo.mAutoHideIndicator) {
				return items[2];
			} else if (mScreenInfo.mEnableIndicator) {
				return items[0];
			} else {
				return items[1];
			}
		}

		return "";
	}
	
	/**
	 * <br>功能简述:设置桌面指示器菜单项的值
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param value
	 */
	private void setScreenIndicatorValue(String value) {
		String items[] = getResources().getStringArray(R.array.screen_indicator_values);
		mScreenInfo.mEnableIndicator = true;
		mScreenInfo.mAutoHideIndicator = false;
		if (items != null && items.length >= 3) {
			if (value.equals(items[2])) {
				mScreenInfo.mAutoHideIndicator = true;
			} else if (value.equals(items[0])) {
				mScreenInfo.mEnableIndicator = true;
			} else {
				mScreenInfo.mEnableIndicator = false;
			}
		}
	}

	/**
	 * <br>功能简述:保存桌面设置
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void saveDeskSetting() {
		if (mDesktopSettingInfo != null) {
			boolean isChangeDesk = false;

			//桌面显示状态栏
			if (mDesktopSettingInfo.mShowStatusbar != mSettingShowDeskStatusbar.getIsCheck()) {
				mDesktopSettingInfo.mShowStatusbar = mSettingShowDeskStatusbar.getIsCheck();
				isChangeDesk = true;
			}

			//是否开启桌面布局新算法
			if (mDesktopSettingInfo.getMarginEnable() != mSettingScreenShowMargin.getIsCheck()) {
				mDesktopSettingInfo.setMarginEnable(mSettingScreenShowMargin.getIsCheck());
				mDesktopSettingInfo.setReload(true);
				isChangeDesk = true;
//				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
//						IDiyMsgIds.SHOW_NEW_MARGINS, -1,
//						mDesktopSettingInfo.getMarginEnable(), null);
			}
			saveScreenRowsCols();
			if (isChangeDesk) {
				//更改数据库数据
				SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo);
			}
		}
	}
	
	/**
	 * <br>功能简述:保存屏幕行列数
	 * <br>功能详细描述:
	 * <br>注意:需要放到退出时才保存。因为执行保存会修改桌面东西。导致很慢
	 * @param view
	 * @param value
	 */
	public void saveScreenRowsCols() {
		if (mDesktopSettingInfo != null) {
			boolean changeRowsCols = false;

			//单选确认框
			int curValue = 0;
			if (mSettingScreenRowsCols != null) {
				curValue = Integer.parseInt(String.valueOf(mSettingScreenRowsCols.getSelectValue()));
			}

			//自定义
			if (curValue == SCREEN_ROWS_COLS_CUSTOM) {
				ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = DeskSettingConstants
						.getSecondSeekBarItemInfo(mSettingScreenRowsCols);
				if (mSettingScreenRowsCols != null) {
					//获取选择的seekbar值
					int rowsValue = seekBarItemInfoList.get(0).getSelectValue();
					int colsValue = seekBarItemInfoList.get(1).getSelectValue();
					if (mDesktopSettingInfo.mRow != rowsValue
							|| mDesktopSettingInfo.mColumn != colsValue) {
						mDesktopSettingInfo.mRow = rowsValue;
						mDesktopSettingInfo.mColumn = colsValue;
						mDesktopSettingInfo.mStyle = curValue;
						changeRowsCols = true;
					}
				}
			} else {
				int oldValue = mDesktopSettingInfo.mStyle;
				if (curValue != oldValue) {
					mDesktopSettingInfo.setRows(curValue);
					mDesktopSettingInfo.setColumns(curValue);
					mDesktopSettingInfo.mStyle = curValue;
					changeRowsCols = true;
				}
			}

			//判断是否修改自动调整小部件和图片位置
			boolean autofit = true;
			if (mSettingScreenRowsCols != null) {
				autofit = mSettingScreenRowsCols.getDeskSettingInfo().getSingleInfo()
						.getCheckBoxIsCheck();
			}
			if (mDesktopSettingInfo.getAutoFit() != autofit) {
				mDesktopSettingInfo.setAutoFit(autofit);
				changeRowsCols = true;
			}

			if (changeRowsCols) {
				//屏幕数做过修改。15屏广告就不再请求了
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
						IScreenAdvertMsgId.SET_CAN_REQUEST_ADVERT_STATE, -1, false);
				//24小时重新请求
				MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN_ADVERT_BUSINESS,
						IScreenAdvertMsgId.SET_CAN_REQUEST_AGAIN_STATE, -1, false);
				SettingProxy.updateDesktopSettingInfo(mDesktopSettingInfo); //保存数据
			}
		}
	}
	
	/**
	 * <br>功能简述:修改屏幕行列数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 * @param value
	 */
	public void changeScreenRowsCols(DeskSettingItemBaseView view, Object value) {
		if (view == mSettingScreenRowsCols) {
			//判断是否从seekBarDialog那里返回，特殊处理
			if (value instanceof String[]) {
				updateSceenRowsCoslEntriesCustomTemp(); //更新临时显示内容，等刷新SingleDialogWithCheckBox内容时可以调用
				mSettingScreenRowsCols.updateSingleDialogWithCheckBox();
			} else {
				//单选确认框
				int curValue = Integer.parseInt(String.valueOf(mSettingScreenRowsCols
						.getSelectValue()));
				//判断是否自定义
				if (curValue == SCREEN_ROWS_COLS_CUSTOM) {
					updateSceenRowsCoslEntriesCustomTemp();
				} else {
					updateSceenRowsCoslEntries(curValue); //更新显示内容
				}
			}
			mSettingScreenRowsCols.updateSumarryText(); //更新Summary
		}
	}
	
	/**
	 * <br>功能简述:更新屏幕行列数的Entries-自定义—临时更新
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param value
	 */
	private void updateSceenRowsCoslEntriesCustomTemp() {
		ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = DeskSettingConstants
				.getSecondSeekBarItemInfo(mSettingScreenRowsCols);
		if (mSettingScreenRowsCols != null) {
			//获取选择的seekbar值
			int rowsValue = seekBarItemInfoList.get(0).getSelectValue();
			int colsValue = seekBarItemInfoList.get(1).getSelectValue();

			CharSequence[] entries = mSettingScreenRowsCols.getDeskSettingInfo().getSingleInfo()
					.getEntries();
			int entriesSize = entries.length;
			entries[entriesSize - 1] = getString(R.string.screen_grid_diy) + " (" + rowsValue + "*"
					+ colsValue + ")";
		}
	}
	
	/**
	 * <br>功能简述:更新屏幕行列数的Entries
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param value
	 */
	private void updateSceenRowsCoslEntries(int type) {
		CharSequence[] entries = mSettingScreenRowsCols.getDeskSettingInfo().getSingleInfo()
				.getEntries();
		int entriesSize = entries.length;

		//设置自定义的显示值为：自定义行列（6X6）
		if (type == SCREEN_ROWS_COLS_CUSTOM) {
			entries[entriesSize - 1] = getString(R.string.screen_grid_diy) + " ("
					+ mDesktopSettingInfo.mRow + "*" + mDesktopSettingInfo.mColumn + ")";
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