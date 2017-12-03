package com.jiubang.ggheart.apps.desks.Preferences.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.ArrayUtils;
import com.go.util.graphics.effector.united.EffectorControler;
import com.go.util.graphics.effector.united.EffectorInfo;
import com.go.util.graphics.effector.united.IEffectorIds;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingGestureFunctionActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager.NewMarkKeys;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DeskSettingClassifyChoiceDialog;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingBaseInfo;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;

/**
 * 
 * <br>类描述:桌面设置-应用设置-功能表View
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-10-19]
 */
public class DeskSettingScreenAppFunTabView extends DeskSettingVisualAbsTabView {
	/**
	 * 功能表滚屏模式
	 */
//	private DeskSettingItemListView mSettingFunAppScrollType;

	//	/**
	//	 * 功能表行列数
	//	 */
	//	private DeskSettingItemListView mSettingFunAppRowsCols;

	/**
	 * 功能表进出特效
	 */
	private DeskSettingItemListView mSettingFunAppInOutEffects;

	/**
	 * 横屏滚动特效
	 */
	private DeskSettingItemListView mSettingFunAppLandscapeEffects;

	/**
	 * 竖向滚动特效
	 */
	private DeskSettingItemListView mSettingFunAppVerticalEffects;

	/**
	 * 功能表手势设置
	 */
	private DeskSettingItemBaseView mSettingGestrueFunction; //功能表手势设置

	/**
	 * 功能表循环切换
	 */
//	private DeskSettingItemCheckBoxView mSettingAppDrawerChangeLoop;
	/**
	 * 功能表进出特效
	 */
	int[] mSettingFunAppInOutEffectsImageId = { R.drawable.funapp_inout_effect_default,
			R.drawable.funapp_inout_effect_none, R.drawable.funapp_inout_effect_tv, 
			R.drawable.funapp_inout_effect_windmill, R.drawable.funapp_inout_effect_zoom, 
			R.drawable.screenedit_effect06_easyroll};

	/**
	 * 横屏切换付费特效
	 */
	private CharSequence[] mScreenChangeEffectsNeedPayList = null;

	/**
	 * 功能表竖屏滚动特效
	 */
	int[] mSettingFunAppVerticalEffectsImageId = { R.drawable.funapp_vertical_effects_default,
			R.drawable.funapp_vertical_effects_waterfall };

	private FunAppSetting mFunAppSettingInfo;
	public Context mContext;
	
	private EffectorControler mEffectorControler;

	public DeskSettingScreenAppFunTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mEffectorControler = EffectorControler.getInstance();
		findView();
	}

	public DeskSettingScreenAppFunTabView(Context context) {
		super(context);
		mContext = context;
		mEffectorControler = EffectorControler.getInstance();
		findView();
	}

	@Override
	protected void findView() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View mFunctionView = inflater.inflate(R.layout.desk_setting_app_of_function, null);

		//功能表滚屏模式
//		mSettingFunAppScrollType = (DeskSettingItemListView) mFunctionView
//				.findViewById(R.id.func_app_scroll_type);
//		mSettingFunAppScrollType.setOnValueChangeListener(this);

		//功能表行列数
		//		mSettingFunAppRowsCols = (DeskSettingItemListView) mFunctionView
		//				.findViewById(R.id.func_app_rows_cols);
		//		mSettingFunAppRowsCols.setOnValueChangeListener(this);

//		mSettingAppDrawerChangeLoop = (DeskSettingItemCheckBoxView) mFunctionView
//				.findViewById(R.id.func_app_looping);
//		mSettingAppDrawerChangeLoop.setOnValueChangeListener(this);

		//功能表进出特效
		mSettingFunAppInOutEffects = (DeskSettingItemListView) mFunctionView
				.findViewById(R.id.fun_app_inout_effects);
		mSettingFunAppInOutEffects.setOnValueChangeListener(this);

		//横向滚屏特效
		mSettingFunAppLandscapeEffects = (DeskSettingItemListView) mFunctionView
				.findViewById(R.id.fun_app_landscape_effects);
		mSettingFunAppLandscapeEffects.setOnValueChangeListener(this);
		mSettingFunAppLandscapeEffects.setOnClickListener(this);

		//竖向滚屏特效
		mSettingFunAppVerticalEffects = (DeskSettingItemListView) mFunctionView
				.findViewById(R.id.fun_app_vertical_effects);
		mSettingFunAppVerticalEffects.setOnValueChangeListener(this);

		//功能表手势设置
		mSettingGestrueFunction = (DeskSettingItemBaseView) mFunctionView
				.findViewById(R.id.setting_gesture_fuction);
		mSettingGestrueFunction.setOpenIntent(new Intent(mContext,
				DeskSettingGestureFunctionActivity.class));
		addView(mFunctionView);
	}

	public void load() {
		mFunAppSettingInfo = SettingProxy.getFunAppSetting();
		mScreenChangeEffectsNeedPayList = mEffectorControler.getPrimeEffectorIdArray();
		loadFunApp();

//		if (mFunAppSettingInfo != null) {
//			boolean isLoop = mFunAppSettingInfo.getScrollLoop() == 1 ? true : false;
//			mSettingAppDrawerChangeLoop.setIsCheck(isLoop);
//		}
		setEffectState();
	}

	private void setEffectState() {
		int direction = mFunAppSettingInfo.getTurnScreenDirection();
		if (direction == FunAppSetting.SCREEN_SCROLL_HORIZONTAL) {
			mSettingGestrueFunction.setEnabled(true);
			mSettingFunAppLandscapeEffects.setEnabled(true);
			mSettingFunAppVerticalEffects.setEnabled(false);
			mSettingFunAppLandscapeEffects.updateSumarryText();
			mSettingFunAppVerticalEffects.setSummaryText(R.string.vertical_scroll_effect_uneanble_hint);
		} else {
			mSettingFunAppLandscapeEffects.setEnabled(false);
			mSettingFunAppVerticalEffects.setEnabled(true);
			mSettingFunAppLandscapeEffects
					.setSummaryText(R.string.horizontal_scroll_effect_uneanble_hint);
			mSettingFunAppVerticalEffects.updateSumarryText();
			mSettingGestrueFunction.setEnabled(false);
		}
	}

	
	/**
	 * <br>功能简述:加载功能表设置数据
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void loadFunApp() {
		setFunAppRowsColsInfo(); //功能表行列数
		setFunAppInOutEffectsInfo(); //功能表进出特效
		setFunAppLandscapeEffectsInfo(); //横向滚屏特效
		setFunAppVerticalEffectsInfo(); //竖向滚屏特效
		setFunAppScrollTypeInfo(); //功能表滚屏模式，需要放到横向滚屏特效和竖向滚屏特效后面。设置它们是否可选
	}

	/**
	 * <br>功能简述:设置功能表行列数
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setFunAppRowsColsInfo() {
		//		if (mFunAppSettingInfo == null) {
		//			return;
		//		}
		//
		//		boolean isSmallScreen = DeskSettingConstants.checkIsSmallScreen(); //是否小屏幕
		//
		//		//检查是否小屏幕手机，是的话就重新设置选项
		//		DeskSettingSingleInfo singleInfo = mSettingFunAppRowsCols.getDeskSettingInfo().getSingleInfo();
		//		if (isSmallScreen) {
		//			CharSequence[] entries = getResources().getTextArray(R.array.qvga_fun_app_rows_cols);
		//			CharSequence[] entryValues = getResources().getTextArray(R.array.qvga_fun_app_rows_cols_value);
		//			singleInfo.setEntries(entries);
		//			singleInfo.setEntryValues(entryValues);
		//		}
		//
		//		int funAppRowsColsType = mFunAppSettingInfo.getLineColumnNum();
		//		updateFunAppRowsCoslEntries(funAppRowsColsType);
		//		DeskSettingConstants.setSingleInfoValueAndSummary(funAppRowsColsType, mSettingFunAppRowsCols);
		//
		//		//行数seekBar
		//		DeskSettingSeekBarItemInfo rowSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
		//		rowSeekBarItemInfo.setTitle(mContext.getString(R.string.screen_row_dialog_msg)); //设置标题
		//		rowSeekBarItemInfo.setMinValue(DeskSettingConstants.ROWS_COLS_MIN_SIZE); //设置最小值
		//		rowSeekBarItemInfo.setMaxValue(DeskSettingConstants.ROWS_COLS_MAX_SIZE); //设置最大值
		//		rowSeekBarItemInfo.setSelectValue(mFunAppSettingInfo.getRowNum()); //设置默认选择值,默认值为4
		//
		//		//列数seekBar
		//		DeskSettingSeekBarItemInfo colSeekBarItemInfo = new DeskSettingSeekBarItemInfo();
		//		colSeekBarItemInfo.setTitle(mContext.getString(R.string.screen_column_dialog_msg)); //设置标题
		//		colSeekBarItemInfo.setMinValue(DeskSettingConstants.ROWS_COLS_MIN_SIZE); //设置最小值
		//		colSeekBarItemInfo.setMaxValue(DeskSettingConstants.ROWS_COLS_MAX_SIZE); //设置最大值
		//		colSeekBarItemInfo.setSelectValue(mFunAppSettingInfo.getColNum());; //设置默认选择值
		//
		//		//创建SeekBarInfo队列
		//		ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = new ArrayList<DeskSettingSeekBarItemInfo>();
		//		seekBarItemInfoList.add(rowSeekBarItemInfo);
		//		seekBarItemInfoList.add(colSeekBarItemInfo);
		//
		//		//小屏幕
		//		if (isSmallScreen) {
		//			DeskSettingConstants.setSecondInfoOfSeekBar(mContext, seekBarItemInfoList,
		//					R.array.qvga_fun_app_rows_cols, mSettingFunAppRowsCols);
		//		} else {
		//			DeskSettingConstants.setSecondInfoOfSeekBar(mContext, seekBarItemInfoList,
		//					R.array.fun_app_rows_cols, mSettingFunAppRowsCols);
		//		}
	}

	/**
	 * <br>功能简述:设置功能表进出特效
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void setFunAppInOutEffectsInfo() {
		if (mFunAppSettingInfo != null) {
			int effectType = mFunAppSettingInfo.getInOutEffect(); // 获取数据库值
			int[] multisSelectValues = mFunAppSettingInfo.getAppInOutCustomRandomEffect(); // 随机自定义已选的值
			int randomCustomId = DeskSettingConstants.APP_FUN_IN_OUT_EFFECTS_CUSTOM;
			CharSequence[] effectorNames = mContext.getResources().getTextArray(R.array.inout_list_title);
			CharSequence[] effectorValues = mContext.getResources().getTextArray(R.array.inout_list_value);
			int[] effectorDrawableIds = mSettingFunAppInOutEffectsImageId;
			DeskSettingBaseInfo[] infos = new DeskSettingBaseInfo[1];
			infos[0] = mSettingFunAppInOutEffects
					.generateClassifyListItemInfo(effectorNames,
							effectorValues, effectorDrawableIds, null);
			CharSequence[] unChooseIds = new CharSequence[]{"1", "6"};
			mSettingFunAppInOutEffects.setClassifyDialogInfo(
					null, mContext.getResources().getDrawable(R.drawable.desk_setting_effector_icon_bg),
					infos, String.valueOf(effectType),
					String.valueOf(randomCustomId),
					mContext.getResources().getString(
							R.string.effect_type_random_custom), multisSelectValues, unChooseIds);
			DeskSettingConstants.setClassifyInfoValueAndSummary(effectType,
					mSettingFunAppInOutEffects); // 设置值和更新summary
		}
	}

	/**
	 * <br>功能简述:功能表横向滚屏特效
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void setFunAppLandscapeEffectsInfo() {
		if (mFunAppSettingInfo != null) {
			int effectType = mFunAppSettingInfo.getIconEffect(); // 获取数据库值
			int[] multisSelectValues = mFunAppSettingInfo.getAppIconCustomRandomEffect(); // 随机自定义已选的值
			Object[] objects = mEffectorControler.getAllEffectorsWithSplit(
					EffectorControler.TYPE_APP_DRAWER_SETTING, false);
			int size = objects.length;
			DeskSettingBaseInfo[] infos = new DeskSettingBaseInfo[size];
			for (int i = 0; i < size; i++) {
				String title = mContext.getResources().getString(
						R.string.effector_setting_classify_title_3D);
				if (i == 1) {
					title = mContext.getResources().getString(
							R.string.effector_setting_classify_title_2D);
				}
				Object[] objs = (Object[]) objects[i];
				CharSequence[] effectorNames = ArrayUtils
						.stringArrayToCharSequenceArray((String[]) objs[0]);
				CharSequence[] effectorValues = ArrayUtils
						.intArrayToCharSequenceArray((int[]) objs[1]);
				int[] effectorDrawableIds = (int[]) objs[2];
				infos[i] = mSettingFunAppLandscapeEffects
						.generateClassifyListItemInfo(effectorNames,
								effectorValues, effectorDrawableIds, title);
			}
			CharSequence[] unChooseIds = ArrayUtils
					.intArrayToCharSequenceArray(mEffectorControler
							.getCanNotUseEffectorIds(EffectorControler.TYPE_SCREEN_SETTING, true));
			mSettingFunAppLandscapeEffects.setClassifyDialogInfo(
					null, mContext.getResources().getDrawable(R.drawable.desk_setting_effector_icon_bg),
					infos, String.valueOf(effectType),
					String.valueOf(IEffectorIds.EFFECTOR_TYPE_RANDOM_CUSTOM),
					mContext.getResources().getString(
							R.string.effect_type_random_custom), multisSelectValues, unChooseIds);
			DeskSettingConstants.setClassifyInfoValueAndSummary(effectType,
					mSettingFunAppLandscapeEffects); // 设置值和更新summary
		}
	}

	/**
	 * <br>功能简述:功能表竖向滚屏特效
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	private void setFunAppVerticalEffectsInfo() {
		if (mFunAppSettingInfo != null) {
			int effectType = mFunAppSettingInfo.getVerticalScrollEffect(); // 获取数据库值
			CharSequence[] effectorNames = mContext.getResources().getTextArray(R.array.func_app_vertical_scroll_effect_entris);
			CharSequence[] effectorValues = mContext.getResources().getTextArray(R.array.func_app_vertical_scroll_effect_entry_values);
			int[] effectorDrawableIds = mSettingFunAppVerticalEffectsImageId;
			DeskSettingBaseInfo[] infos = new DeskSettingBaseInfo[1];
			infos[0] = mSettingFunAppVerticalEffects
					.generateClassifyListItemInfo(effectorNames,
							effectorValues, effectorDrawableIds, null);
			mSettingFunAppVerticalEffects.setClassifyDialogInfo(
					null,
					mContext.getResources().getDrawable(
							R.drawable.desk_setting_effector_icon_bg), infos,
					String.valueOf(effectType), null, null, null, null);
			DeskSettingConstants.setClassifyInfoValueAndSummary(effectType,
					mSettingFunAppVerticalEffects); // 设置值和更新summary
		}
	}

	/**
	 * <br>功能简述:设置功能表滚屏模式
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setFunAppScrollTypeInfo() {
//		int direction = mFunAppSettingInfo.getTurnScreenDirection();
//		int loop = mFunAppSettingInfo.getScrollLoop();
//
//		int scrollType = DeskSettingConstants.getFunAppScrollType(direction, loop);
//		DeskSettingConstants.setSingleInfoValueAndSummary(scrollType, mSettingFunAppScrollType);
//
//		setFunAppLandAndVerEffectsState(direction);
	}

	/**
	 * <br>功能简述:根据滚屏模式设置横屏特效/竖屏特效是否可选
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param direction
	 */
	public void setFunAppLandAndVerEffectsState(int direction) {
		//如果是横屏
		if (direction == FunAppSetting.SCREEN_SCROLL_HORIZONTAL) {
			mSettingFunAppLandscapeEffects.setEnabled(true);
			mSettingFunAppVerticalEffects.setEnabled(false);
			mSettingFunAppLandscapeEffects.updateSumarryText();
			mSettingFunAppVerticalEffects.setSummaryText(R.string.func_app_icon_effect_enable_hint);
			mSettingGestrueFunction.setEnabled(true);
		} else {
			mSettingFunAppLandscapeEffects.setEnabled(false);
			mSettingFunAppVerticalEffects.setEnabled(true);
			mSettingFunAppLandscapeEffects
					.setSummaryText(R.string.func_app_icon_effect_enable_hint);
			mSettingFunAppVerticalEffects.updateSumarryText();
			mSettingGestrueFunction.setEnabled(false);
		}
	}

	/**
	 * <br>功能简述:更新功能表行列数的Entries
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param value
	 */
	private void updateFunAppRowsCoslEntries(int type) {
		//		CharSequence[] entries = mSettingFunAppRowsCols.getDeskSettingInfo().getSingleInfo()
		//				.getEntries();
		//		int entriesSize = entries.length;
		//
		//		//设置自定义的显示值为：自定义行列（6X6）
		//		if (type == APP_FUN_ROWS_COLS_CUSTOM) {
		//			entries[entriesSize - 1] = mContext.getString(R.string.screen_grid_diy) + " ("
		//					+ mFunAppSettingInfo.getRowNum() + "×" + mFunAppSettingInfo.getColNum() + ")";
		//		} else {
		//			entries[entriesSize - 1] = mContext.getString(R.string.screen_grid_diy);
		//		}
	}

	public void save() {
		saveAppDrawerChangeLoop();
	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView view, Object value) {

//		saveFunAppScrollType(view); //功能表滚屏模式

		saveFunAppRowsCols(view); //保存功能表行列数

		saveFunAppInOutEffects(view); //保存功能表进出特效 

		saveFunAppLandscapeEffects(view); //保存横屏切换特效

		saveFunAppVerticalEffects(view); //保存竖屏切换特效
		
		
		return true;
	}

	@Override
	public void onClick(View v) {
		//屏幕特效点击时间，增加付费选项
		if (v.getId() == R.id.fun_app_landscape_effects) {
			mSettingFunAppLandscapeEffects
					.getDeskSettingInfo()
					.getClassifyInfo()
					.setNeedShowNewTag(
							mEffectorControler
									.getNewEffectorIdArray(EffectorControler.TYPE_APP_DRAWER_SETTING));
			
			//是否200渠道
			if (FunctionPurchaseManager.getInstance(getContext().getApplicationContext())
					.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_VISABLE) {
				mSettingFunAppLandscapeEffects.getDeskSettingInfo().getClassifyInfo()
						.setNeedPayList(mScreenChangeEffectsNeedPayList);
			}
			mFunAppSettingInfo.setIconEffect(mFunAppSettingInfo.getIconEffect()); // 修改选择得值
			mSettingFunAppLandscapeEffects.onClick(v);
			mSettingFunAppLandscapeEffects.setImageNewVisibile(GONE);
			mEffectorControler.updateEffectorNewState(EffectorControler.TYPE_APP_DRAWER_SETTING, false);
		}
	}
	
	/**
	 * <br>功能简述:保存功能表滚动模式
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 */
	public void saveFunAppScrollType(DeskSettingItemBaseView view) {
//		if (view == mSettingFunAppScrollType) {
//			mSettingFunAppScrollType.updateSumarryText();
//
//			int selectValue = Integer.parseInt(String.valueOf(mSettingFunAppScrollType
//					.getSelectValue()));
			// 是否循环模式
//			if (selectValue == FunAppSetting.SCREEN_SCROLL_VERTICAL
//					|| selectValue == FunAppSetting.SCREEN_SCROLL_HORIZONTAL) {
//				mFunAppSettingInfo.setScrollLoop(0);
//			} else {
//				mFunAppSettingInfo.setScrollLoop(1);
//			}

			// 修改翻屏风格
//			int direction = FunAppSetting.SCREEN_SCROLL_HORIZONTAL;
//			
//			if (selectValue == FunAppSetting.SCREEN_SCROLL_VERTICAL) {
//				direction = FunAppSetting.SCREEN_SCROLL_VERTICAL;
//			}
//			mFunAppSettingInfo.setTurnScreenDirection(direction);
//
//			setFunAppLandAndVerEffectsState(direction); //根据滚屏模式设置横屏特效/竖屏特效是否可选 
//		}

	}

	/**
	 * <br>功能简述:保存功能表行列数
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 */
	public void saveFunAppRowsCols(DeskSettingItemBaseView view) {
		//		if (view == mSettingFunAppRowsCols && mFunAppSettingInfo != null) {
		//			int curValue = Integer
		//					.parseInt(String.valueOf(mSettingFunAppRowsCols.getSelectValue()));
		//			//自定义
		//			if (curValue == APP_FUN_ROWS_COLS_CUSTOM) {
		//				ArrayList<DeskSettingSeekBarItemInfo> seekBarItemInfoList = DeskSettingConstants
		//						.getSecondSeekBarItemInfo(mSettingFunAppRowsCols);
		//				if (seekBarItemInfoList != null) {
		//					//获取选择的seekbar值
		//					int rowsValue = seekBarItemInfoList.get(0).getSelectValue();
		//					int colsValue = seekBarItemInfoList.get(1).getSelectValue();
		//					//判断自定义的值是否改变
		//					if (mFunAppSettingInfo.getRowNum() != rowsValue
		//							|| mFunAppSettingInfo.getColNum() != colsValue) {
		//						mFunAppSettingInfo.setRowNum(rowsValue);
		//						mFunAppSettingInfo.setColNum(colsValue);
		//						mFunAppSettingInfo.setLineColumnNum(curValue); //设置当前选择类型并保持	
		//					}
		//				}
		//			} else {
		//				//先判断选项是否有更改
		//				int oldValue = mFunAppSettingInfo.getLineColumnNum();
		//				if (curValue != oldValue) {
		//					// 只改变FunAppSetting数据库中行列数的值，没对grid行列数进行修改。
		//					AppFuncUtils.getInstance(mContext).setGridStandard(curValue, null,
		//							AppFuncConstants.ALLAPPS_GRID);
		//					mFunAppSettingInfo.setLineColumnNum(curValue); //设置当前选择类型	
		//				}
		//			}
		//
		//			//更新Sumarry
		//			updateFunAppRowsCoslEntries(curValue); //设置单选列表的选择值和更新summary
		//			mSettingFunAppRowsCols.updateSumarryText(); //更新summary	
		//		}
	}

	/**
	 * <br>功能简述:保存功能表进出特效
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 */
	public void saveFunAppInOutEffects(DeskSettingItemBaseView view) {
		if (view == mSettingFunAppInOutEffects && mFunAppSettingInfo != null) {
			int curValue = Integer.parseInt(String.valueOf(mSettingFunAppInOutEffects
					.getSelectValue()));

			//自定义
			if (curValue == DeskSettingConstants.APP_FUN_IN_OUT_EFFECTS_CUSTOM) {
				//获取2级多选勾选的的值
				int[] curSecondValue = DeskSettingConstants
						.getMultiSelectValueInClassifyDialog(mSettingFunAppInOutEffects);
				if (curSecondValue != null) {
					mFunAppSettingInfo.setAppInOutCustomRandomEffect(curSecondValue); //设置自定义选择的值
					mFunAppSettingInfo.setInOutEffect(curValue); // 修改功能表进出功能表特效
				}
			} else {
				int oldValue = mFunAppSettingInfo.getInOutEffect();
				//先判断选项是否有更改
				if (curValue != oldValue) {
					mFunAppSettingInfo.setInOutEffect(curValue); // 修改功能表进出功能表特效
				}
			}
			mSettingFunAppInOutEffects.updateSumarryText(); //更新summary	
		}
	}
	
	/**
	 * <br>功能简述:保存功能表横屏切换特效
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 */
	public void saveFunAppLandscapeEffects(DeskSettingItemBaseView view) {
		if (view == mSettingFunAppLandscapeEffects && mFunAppSettingInfo != null) {
			int curValue = Integer.parseInt(String.valueOf(mSettingFunAppLandscapeEffects
					.getSelectValue()));
			EffectorInfo effectorInfo = mEffectorControler.getEffectorInfoById(curValue);
			//判断是否付费用户,是否200渠道用户
			if (FunctionPurchaseManager.getInstance(
					getContext().getApplicationContext()).getPayFunctionState(
					FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_VISABLE) {
				if (effectorInfo.mIsPrime) {
					handleChoosePrimeEffector(curValue);
					return;
				}
			}
			//自定义
			if (curValue == DeskSettingConstants.APP_FUN_LANDSCAPE_EFFECTS_CUSTOM) {
				//获取2级多选勾选的的值
				int[] curSecondValue = DeskSettingConstants
						.getMultiSelectValueInClassifyDialog(mSettingFunAppLandscapeEffects);
				if (curSecondValue != null) {
					mFunAppSettingInfo.setAppIconCustomRandomEffect(curSecondValue); //设置自定义选择的值
					mFunAppSettingInfo.setIconEffect(curValue); // 修改选择得值
				}
			} else {
				int oldValue = mFunAppSettingInfo.getIconEffect();
				//先判断选项是否有更改
				if (curValue != oldValue) {
					mFunAppSettingInfo.setIconEffect(curValue); // 修改选择得值
				}
			}
			mSettingFunAppLandscapeEffects.updateSumarryText(); //更新summary	
		}
	}
	
	private void handleChoosePrimeEffector(int curValue) {
		resetLandscapeEffectsToLoadValue();
		int entranceId = 0;
		if (curValue == IEffectorIds.EFFECTOR_TYPE_CURVE) {
			entranceId = 405;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_FLYIN) {
			entranceId = 406;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_PAGETURN) {
			entranceId = 407;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_CROSSFADE) {
			entranceId = 408;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_CRYSTAL) {
			entranceId = 419;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_SNAKE) {
			entranceId = 427;
		}
		DeskSettingUtils.showPayDialog(mContext, entranceId);
	}
	
	private void resetLandscapeEffectsToLoadValue() {
		DeskSettingClassifyChoiceDialog dialog = (DeskSettingClassifyChoiceDialog) mSettingFunAppLandscapeEffects
				.getDialog();
		dialog.setCurSingleChooseValue(String.valueOf(mFunAppSettingInfo.getIconEffect()), true); // 重新设置原来选择的值
		dialog.setNeedDismissDialog(false); // 设置本次点击弹框不消失
	}

	/**
	 * <br>功能简述:保存功能表竖屏切换特效
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 */
	public void saveFunAppVerticalEffects(DeskSettingItemBaseView view) {
		if (view == mSettingFunAppVerticalEffects && mFunAppSettingInfo != null) {

			mSettingFunAppVerticalEffects.updateSumarryText(); //刷新summary

			int selectValue = Integer.parseInt(String.valueOf(mSettingFunAppVerticalEffects
					.getSelectValue()));
			if (mFunAppSettingInfo.getVerticalScrollEffect() != selectValue) {
				mFunAppSettingInfo.setVerticalScrollEffect(selectValue);
			}
		}
	}

	public void changeOrientation() {
		//		mSettingFunAppScrollType.dismissDialog();
		//		mSettingFunAppRowsCols.dismissDialog();
		//		mSettingFunAppInOutEffects.dismissDialog();
		//		mSettingFunAppLandscapeEffects.dismissDialog();
		//		mSettingFunAppVerticalEffects.dismissDialog();
	}
	/**
	 * <br>功能简述:保存屏幕循环切换
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void saveAppDrawerChangeLoop() {
//		if (mFunAppSettingInfo != null) {
//			if (mSettingAppDrawerChangeLoop.getIsCheck()) {
//				mFunAppSettingInfo.setScrollLoop(1);
//			} else {
//				mFunAppSettingInfo.setScrollLoop(0);
//			}
//		}
	}
	
	public void removeEffectorPrime() {
		if (mSettingFunAppLandscapeEffects != null
				&& mSettingFunAppLandscapeEffects.getDialog() != null) {
			((DeskSettingClassifyChoiceDialog) mSettingFunAppLandscapeEffects.getDialog())
					.removePrimeImage();
		}
	}
	
	public void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		if (newMarkManager.isShowNew(NewMarkKeys.APPDRAWER_HOR_TRANSITION, false)) {
			mSettingFunAppLandscapeEffects.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingFunAppLandscapeEffects.setImageNewVisibile(View.GONE);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}
