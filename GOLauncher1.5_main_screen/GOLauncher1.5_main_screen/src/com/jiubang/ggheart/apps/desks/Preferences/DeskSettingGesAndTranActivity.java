package com.jiubang.ggheart.apps.desks.Preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.SettingProxy;
import com.go.util.ArrayUtils;
import com.go.util.graphics.effector.united.EffectorControler;
import com.go.util.graphics.effector.united.EffectorInfo;
import com.go.util.graphics.effector.united.IEffectorIds;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager.NewMarkKeys;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DeskSettingClassifyChoiceDialog;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingBaseInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.data.info.EffectSettingInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;

/**
 * 
 * @author zouguiquan
 *
 */
public class DeskSettingGesAndTranActivity extends DeskSettingBaseActivity {

	/**
	 * 桌面手势设置
	 */
	private DeskSettingItemBaseView mSettingGestrueScreen;
	/**
	 * 功能表手势设置
	 */
	private DeskSettingItemBaseView mSettingGestrueFunction;
	/**
	 * 屏幕切换特效
	 */
	private DeskSettingItemListView mSettingScreenChangeEffects;
	
	/**
	 * 付费监听
	 */
	private BroadcastReceiver mRefreshReceiver;
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
	
	private EffectorControler mEffectorControler;
	
	private FunAppSetting mFunAppSettingInfo;
	private EffectSettingInfo mEffectSettingInfo;
	
	/**
	 * 付费特效
	 */
	private CharSequence[] mScreenChangeEffectsNeedPayList = null;
	public int mScreenEffectsOldValue = -1; //屏幕特效选择前的值
	private final static int GRID_EFFECTOR_TYPE_CUSTOM = -2;
	private final static int GRID_EFFECTOR_TYPE_CYLINDER = 15; //圆柱特效
	private final static int GRID_EFFECTOR_TYPE_SPHERE = 16; //球特效
	
	public final static int APP_FUN_IN_OUT_EFFECTS_CUSTOM = -1; //功能表进出特效自定义选项
	public final static int APP_FUN_LANDSCAPE_EFFECTS_CUSTOM = -2; //横屏滚动特效自定义选项
	
	/**
	 * 功能表进出特效
	 */
	int[] mSettingFunAppInOutEffectsImageId = { R.drawable.funapp_inout_effect_default,
			R.drawable.funapp_inout_effect_none, R.drawable.funapp_inout_effect_tv, 
			R.drawable.funapp_inout_effect_windmill, R.drawable.funapp_inout_effect_zoom, 
			R.drawable.screenedit_effect06_easyroll};
	
	/**
	 * 功能表竖屏滚动特效
	 */
	int[] mSettingFunAppVerticalEffectsImageId = { R.drawable.funapp_vertical_effects_default,
			R.drawable.funapp_vertical_effects_waterfall };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_gesture_and_transition);
		
		mEffectorControler = EffectorControler.getInstance();

		//桌面手势设置
		mSettingGestrueScreen = (DeskSettingItemBaseView) findViewById(R.id.gesture_screen);
		mSettingGestrueScreen.setOnClickListener(this);

		//功能表手势设置
		mSettingGestrueFunction = (DeskSettingItemBaseView) findViewById(R.id.setting_gesture_fuction);
		mSettingGestrueFunction.setOpenIntent(new Intent(this, DeskSettingGestureFunctionActivity.class));
		
		//屏幕切换特效
		mSettingScreenChangeEffects = (DeskSettingItemListView) findViewById(R.id.desktop_transition);
		mSettingScreenChangeEffects.setOnValueChangeListener(this);
		mSettingScreenChangeEffects.setOnClickListener(this);
		
		//功能表进出特效
		mSettingFunAppInOutEffects = (DeskSettingItemListView) findViewById(R.id.fun_app_inout_effects);
		mSettingFunAppInOutEffects.setOnValueChangeListener(this);
		
		//横向滚屏特效
		mSettingFunAppLandscapeEffects = (DeskSettingItemListView) findViewById(R.id.fun_app_landscape_effects);
		mSettingFunAppLandscapeEffects.setOnValueChangeListener(this);
		mSettingFunAppLandscapeEffects.setOnClickListener(this);

		//竖向滚屏特效
		mSettingFunAppVerticalEffects = (DeskSettingItemListView) findViewById(R.id.fun_app_vertical_effects);
		mSettingFunAppVerticalEffects.setOnValueChangeListener(this);
		
		load();
		registeRefreshReceiver();
		GuiThemeStatistics
				.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.PREFERENCES_GT);
	}

	@Override
	public void load() {
		super.load();
		
		mScreenChangeEffectsNeedPayList = mEffectorControler.getPrimeEffectorIdArray();
		mFunAppSettingInfo = SettingProxy.getFunAppSetting();
		mEffectSettingInfo = SettingProxy.getEffectSettingInfo();

		if (DeskSettingUtils.isFirstShowFourGestureTip(this, false)) {
			mSettingGestrueScreen.setImageNewVisibile(View.VISIBLE);
		}
		
		if (mEffectorControler.checkHasNewEffector(EffectorControler.TYPE_SCREEN_SETTING)) {
			mSettingScreenChangeEffects.setImageNewVisibile(View.VISIBLE);
		}
		
		//设置桌面切换特效
		setSettingScreenChangeEffectsInfo();
		//功能表进出特效
		setFunAppInOutEffectsInfo(); 
		//横向滚屏特效
		setFunAppLandscapeEffectsInfo(); 
		//竖向滚屏特效
		setFunAppVerticalEffectsInfo(); 
		
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
				String title = getResources().getString(R.string.effector_setting_classify_title_3D);
				if (i == 1) {
					title = getResources().getString(R.string.effector_setting_classify_title_2D);
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
					null, getResources().getDrawable(R.drawable.desk_setting_effector_icon_bg),
					infos, String.valueOf(effectType),
					String.valueOf(IEffectorIds.EFFECTOR_TYPE_RANDOM_CUSTOM),
					getResources().getString(
							R.string.effect_type_random_custom), multisSelectValues, unChooseIds);
			DeskSettingConstants.setClassifyInfoValueAndSummary(effectType,
					mSettingFunAppLandscapeEffects); // 设置值和更新summary
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mSettingScreenChangeEffects != null) {
			mSettingScreenChangeEffects.changeOrientation(newConfig);
		}
		if (mSettingFunAppLandscapeEffects != null) {
			mSettingFunAppLandscapeEffects.changeOrientation(newConfig);
		}
		if (mSettingFunAppVerticalEffects != null) {
			mSettingFunAppVerticalEffects.changeOrientation(newConfig);
		}
		if (mSettingFunAppInOutEffects != null) {
			mSettingFunAppInOutEffects.changeOrientation(newConfig);
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
			CharSequence[] effectorNames = getResources().getTextArray(R.array.func_app_vertical_scroll_effect_entris);
			CharSequence[] effectorValues = getResources().getTextArray(R.array.func_app_vertical_scroll_effect_entry_values);
			int[] effectorDrawableIds = mSettingFunAppVerticalEffectsImageId;
			DeskSettingBaseInfo[] infos = new DeskSettingBaseInfo[1];
			infos[0] = mSettingFunAppVerticalEffects
					.generateClassifyListItemInfo(effectorNames,
							effectorValues, effectorDrawableIds, null);
			mSettingFunAppVerticalEffects.setClassifyDialogInfo(
					null,
					getResources().getDrawable(
							R.drawable.desk_setting_effector_icon_bg), infos,
					String.valueOf(effectType), null, null, null, null);
			DeskSettingConstants.setClassifyInfoValueAndSummary(effectType,
					mSettingFunAppVerticalEffects); // 设置值和更新summary
		}
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
			int randomCustomId = APP_FUN_IN_OUT_EFFECTS_CUSTOM;
			CharSequence[] effectorNames = getResources().getTextArray(R.array.inout_list_title);
			CharSequence[] effectorValues = getResources().getTextArray(R.array.inout_list_value);
			int[] effectorDrawableIds = mSettingFunAppInOutEffectsImageId;
			DeskSettingBaseInfo[] infos = new DeskSettingBaseInfo[1];
			infos[0] = mSettingFunAppInOutEffects
					.generateClassifyListItemInfo(effectorNames,
							effectorValues, effectorDrawableIds, null);
			CharSequence[] unChooseIds = new CharSequence[]{"1", "6"};
			mSettingFunAppInOutEffects.setClassifyDialogInfo(
					null, getResources().getDrawable(R.drawable.desk_setting_effector_icon_bg),
					infos, String.valueOf(effectType),
					String.valueOf(randomCustomId),
					getResources().getString(R.string.effect_type_random_custom), multisSelectValues, unChooseIds);
			DeskSettingConstants.setClassifyInfoValueAndSummary(effectType,
					mSettingFunAppInOutEffects); // 设置值和更新summary
		}
	}
	

	/**
	 * <br>功能简述:设置桌面切换特效
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setSettingScreenChangeEffectsInfo() {
		
		if (mEffectSettingInfo != null) {
			
			int oldSelectValue = mEffectSettingInfo.mEffectorType; // 获取数据库值
			int[] multisSelectValues = mEffectSettingInfo.mEffectCustomRandomEffects; // 随机自定义已选的值
			Object[] objects = mEffectorControler.getAllEffectorsWithSplit(EffectorControler.TYPE_SCREEN_SETTING, false);
			int size = objects.length;
			DeskSettingBaseInfo[] infos = new DeskSettingBaseInfo[size];
			
			for (int i = 0; i < size; i++) {
				String title = getResources().getString(R.string.effector_setting_classify_title_3D);
				if (i == 1) {
					title = getResources().getString(R.string.effector_setting_classify_title_2D);
				}
				Object[] objs = (Object[]) objects[i];
				CharSequence[] effectorNames = ArrayUtils.stringArrayToCharSequenceArray((String[]) objs[0]);
				CharSequence[] effectorValues = ArrayUtils.intArrayToCharSequenceArray((int[]) objs[1]);
				int[] effectorDrawableIds = (int[]) objs[2];
				infos[i] = mSettingScreenChangeEffects
						.generateClassifyListItemInfo(effectorNames,
								effectorValues, effectorDrawableIds, title);
			}
			
			CharSequence[] unChooseIds = ArrayUtils
					.intArrayToCharSequenceArray(mEffectorControler
							.getCanNotUseEffectorIds(EffectorControler.TYPE_SCREEN_SETTING, true));
			mSettingScreenChangeEffects.setClassifyDialogInfo(
					null, getResources().getDrawable(R.drawable.desk_setting_effector_icon_bg),
					infos, String.valueOf(oldSelectValue),
					String.valueOf(IEffectorIds.EFFECTOR_TYPE_RANDOM_CUSTOM),
					getResources().getString(R.string.effect_type_random_custom), 
					multisSelectValues, unChooseIds);
			
			// 设置值和更新summary
			DeskSettingConstants.setClassifyInfoValueAndSummary(oldSelectValue,
					mSettingScreenChangeEffects);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterRefreshReceiver();
	}
	
	@Override
	public boolean onValueChange(DeskSettingItemBaseView view, Object value) {

		//屏幕切换特效
		saveScreenEffect(view);
		//保存功能表进出特效 
		saveFunAppInOutEffects(view); 
		//保存横屏切换特效
		saveFunAppLandscapeEffects(view); 
		 //保存竖屏切换特效
		saveFunAppVerticalEffects(view);
		return true;
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
					this.getApplicationContext()).getPayFunctionState(
					FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_VISABLE) {
				if (effectorInfo.mIsPrime) {
					handleAppLendChoosePrimeEffector(curValue);
					return;
				}
			}
			//自定义
			if (curValue == APP_FUN_LANDSCAPE_EFFECTS_CUSTOM) {
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
	
	private void handleAppLendChoosePrimeEffector(int curValue) {
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
		DeskSettingUtils.showPayDialog(this, entranceId);
	}

	private void resetLandscapeEffectsToLoadValue() {
		DeskSettingClassifyChoiceDialog dialog = (DeskSettingClassifyChoiceDialog) mSettingFunAppLandscapeEffects
				.getDialog();
		dialog.setCurSingleChooseValue(String.valueOf(mFunAppSettingInfo.getIconEffect()), true); // 重新设置原来选择的值
		dialog.setNeedDismissDialog(false); // 设置本次点击弹框不消失
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
			if (curValue == APP_FUN_IN_OUT_EFFECTS_CUSTOM) {
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
	 * <br>功能简述:屏幕切换特效
	 * <br>功能详细描述:
	 * <br>注意:在改变值为自定时刷新的原因：如果本来是自定义。在save时再保存就判断没有更改值。无法保存
	 * @param view
	 */
	public void saveScreenEffect(DeskSettingItemBaseView view) {
		
		if (view == mSettingScreenChangeEffects && mEffectSettingInfo != null) {
			
			int curValue = Integer.parseInt(String.valueOf(mSettingScreenChangeEffects.getSelectValue()));
			EffectorInfo effectorInfo = mEffectorControler.getEffectorInfoById(curValue);
			
			//判断是否付费用户,是否200渠道用户
			if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_VISABLE) {
				if (effectorInfo.mIsPrime) { // 付费特效
					handleScreenChoosePrimeEffector(curValue);
					return;
				}
			}
			mSettingScreenChangeEffects.updateSumarryText(); //更新summary

			//自定义特效
			if (curValue == GRID_EFFECTOR_TYPE_CUSTOM) {
				//获取2级多选勾选的的值
				int[] curSecondValue = DeskSettingConstants
						.getMultiSelectValueInClassifyDialog(mSettingScreenChangeEffects);
				if (curSecondValue != null) {
					mEffectSettingInfo.mEffectCustomRandomEffects = curSecondValue;
				}
				mEffectSettingInfo.mEffectorType = GRID_EFFECTOR_TYPE_CUSTOM; //设置自定义类型
				SettingProxy.updateEffectSettingInfo(mEffectSettingInfo); //保存
			} else {
				// 考虑到部分用户的机子性能不好，不能很好支持实现圆柱和球特效，所以提醒用户
				if (curValue == GRID_EFFECTOR_TYPE_CYLINDER
						|| curValue == GRID_EFFECTOR_TYPE_SPHERE) {
					Toast.makeText(this, getString(R.string.effect_warn), Toast.LENGTH_LONG).show();
				}
				if (mEffectSettingInfo.mEffectorType != curValue) {
					mEffectSettingInfo.mEffectorType = curValue;
					SettingProxy.updateEffectSettingInfo(mEffectSettingInfo); //保存
				}
			}
		}
	}
	
	/**
	 * 选择了付费特效，跳转到特效付费预览
	 */
	private void handleScreenChoosePrimeEffector(int curValue) {
		resetScreenEffectsToLoadValue();
		int entranceId = 0;
		if (curValue == IEffectorIds.EFFECTOR_TYPE_CURVE) {
			entranceId = 401;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_FLYIN) {
			entranceId = 402;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_PAGETURN) {
			entranceId = 403;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_CROSSFADE) {
			entranceId = 404;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_CRYSTAL) {
			entranceId = 417;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_CLOTH) {
			entranceId = 423;
		} else if (curValue == IEffectorIds.EFFECTOR_TYPE_SNAKE) {
			entranceId = 426;
		} 
		DeskSettingUtils.showPayDialog(this, entranceId);
	}
	
	private void resetScreenEffectsToLoadValue() {
		DeskSettingClassifyChoiceDialog dialog = (DeskSettingClassifyChoiceDialog) mSettingScreenChangeEffects
				.getDialog();
		dialog.setCurSingleChooseValue(String.valueOf(mScreenEffectsOldValue), true); // 重新设置原来选择的值
		dialog.setNeedDismissDialog(false); // 设置本次点击弹框不消失
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.gesture_screen) {
			startActivity(new Intent(this, DeskSettingGestureScreenActivity.class));

			//if (!ShellPluginFactory.isUseShellPlugin(mContext)) { //移植3D手势
			mSettingGestrueScreen.setImageNewVisibile(View.GONE);
			//}
		} 		
		//屏幕特效点击时间，增加付费选项
		else if (id == R.id.desktop_transition) {
			mSettingScreenChangeEffects
					.getDeskSettingInfo()
					.getClassifyInfo()
					.setNeedShowNewTag(
							mEffectorControler
									.getNewEffectorIdArray(EffectorControler.TYPE_SCREEN_SETTING));

			//是否200渠道
			if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
					FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_VISABLE) {
				mSettingScreenChangeEffects.getDeskSettingInfo().getClassifyInfo()
						.setNeedPayList(mScreenChangeEffectsNeedPayList);
			} else if (FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
					FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_CAN_USE) {
				mSettingScreenChangeEffects.getDeskSettingInfo().getClassifyInfo()
						.setNeedPayList(null);
			}
			mScreenEffectsOldValue = Integer.parseInt(String.valueOf(mSettingScreenChangeEffects
					.getSelectValue()));
			mSettingScreenChangeEffects.onClick(v);
			mSettingScreenChangeEffects.setImageNewVisibile(View.GONE);
			mEffectorControler.updateEffectorNewState(EffectorControler.TYPE_SCREEN_SETTING, false);
		}
		else if (id == R.id.fun_app_landscape_effects) {
			mSettingFunAppLandscapeEffects
			.getDeskSettingInfo()
			.getClassifyInfo()
			.setNeedShowNewTag(
					mEffectorControler
							.getNewEffectorIdArray(EffectorControler.TYPE_APP_DRAWER_SETTING));
	
			//是否200渠道
			if (FunctionPurchaseManager.getInstance(getApplicationContext())
					.getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_VISABLE) {
				mSettingFunAppLandscapeEffects.getDeskSettingInfo().getClassifyInfo()
						.setNeedPayList(mScreenChangeEffectsNeedPayList);
			}
			mFunAppSettingInfo.setIconEffect(mFunAppSettingInfo.getIconEffect()); // 修改选择得值
			mSettingFunAppLandscapeEffects.onClick(v);
			mSettingFunAppLandscapeEffects.setImageNewVisibile(View.GONE);
			mEffectorControler.updateEffectorNewState(EffectorControler.TYPE_APP_DRAWER_SETTING, false);
		}
	}
	
	private void registeRefreshReceiver() {
		mRefreshReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context2, Intent intent) {
				if (intent != null) {
					FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(DeskSettingGesAndTranActivity.this);
					boolean hasPay = purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_EFFECT);
					if (hasPay) {
						removeEffectorPrime();
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(DeskSettingUtils.ACTION_HAD_PAY_REFRESH);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mRefreshReceiver, filter);
	}

	private void unRegisterRefreshReceiver() {

		if (mRefreshReceiver != null) {
			unregisterReceiver(mRefreshReceiver);
			mRefreshReceiver = null;
		}
	}
	
	protected void removeEffectorPrime() {
		if (mSettingScreenChangeEffects != null && mSettingScreenChangeEffects.getDialog() != null) {
			((DeskSettingClassifyChoiceDialog) mSettingScreenChangeEffects.getDialog())
					.removePrimeImage();
		}
		
		if (mSettingFunAppLandscapeEffects != null
				&& mSettingFunAppLandscapeEffects.getDialog() != null) {
			((DeskSettingClassifyChoiceDialog) mSettingFunAppLandscapeEffects.getDialog())
					.removePrimeImage();
		}
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		if (newMarkManager.isShowNew(NewMarkKeys.SCREEN_TRANSITION, false)) {
			mSettingScreenChangeEffects.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingScreenChangeEffects.setImageNewVisibile(View.GONE);
		}
		
		if (newMarkManager.isShowNew(NewMarkKeys.APPDRAWER_HOR_TRANSITION, false)) {
			mSettingFunAppLandscapeEffects.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingFunAppLandscapeEffects.setImageNewVisibile(View.GONE);
		}
		
		if (newMarkManager.isShowNew(NewMarkKeys.SCREEN_GESTURE, false)) {
			mSettingGestrueScreen.setImageNewVisibile(View.VISIBLE);
		} else {
			mSettingGestrueScreen.setImageNewVisibile(View.GONE);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}
