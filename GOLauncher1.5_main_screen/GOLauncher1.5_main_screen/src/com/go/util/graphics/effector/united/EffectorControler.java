package com.go.util.graphics.effector.united;

import java.util.ArrayList;

import android.content.Context;
import android.util.SparseArray;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.ArrayUtils;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;

/**
 * 滚屏特效控制器
 * @author yejijiong
 *
 */
public class EffectorControler {
	private static EffectorControler sInstance;
	/**
	 * 存储所有特效信息的Map，key为特效ID
	 */
	private SparseArray<EffectorInfo> mEffectorInfoMap;
	private Context mContext;
	/**
	 * 随机自定义需要去掉的特效ID
	 */
	private ArrayList<Integer> mCustomRemoveIdList;
	private EffectorDataModel mDataModel;
	/**
	 * 付费特效
	 */
	private CharSequence[] mPrimeEffectorArray;
	/**
	 * 随机特效可使用的特效ID数组（key分别为屏幕（付费、非付费），功能表（付费、非付费））
	 */
	private SparseArray<int[]> mRandomEffectorIdArray;
	
	/**
	 * 桌面滚屏特效设置
	 */
	public static final int TYPE_SCREEN_SETTING = 0;
	/**
	 * 添加界面滚屏特效
	 */
	public static final int TYPE_EFFECT_TAB = 1;
	/**
	 * 功能表滚屏特效设置
	 */
	public static final int TYPE_APP_DRAWER_SETTING = 2;
	
	@SuppressWarnings("serial")
	public EffectorControler() {
		mContext = ApplicationProxy.getContext();
		mDataModel = new EffectorDataModel(mContext);
		mCustomRemoveIdList = new ArrayList<Integer>() { // 在随机自定义需要去掉的特效，在这里进行定义
			{
				add(-1); // 随机特效ID
				add(-2); // 随机自定义特效ID
			}
		};
		mRandomEffectorIdArray = new SparseArray<int[]>();
		initEffectorInfos();
	}
	
	public static EffectorControler getInstance() {
		if (sInstance == null) {
			sInstance = new EffectorControler();
		}
		return sInstance;
	}
	
	private void initEffectorInfos() {
		if (mEffectorInfoMap != null) {
			return;
		}
		mEffectorInfoMap = EffectorParser.getEffectors(mContext);
		mDataModel.initEffectorInfoMapShowNewFlag(mEffectorInfoMap);
	}
	
	public EffectorInfo getEffectorInfoById(int id) {
		return mEffectorInfoMap.get(id);
	}
	
	/**
	 * 获取随机特效可使用的特效ID组合
	 * @return
	 */
	public int[] getRandomEffectIdArray(int type) {
		int key = 0;
		if (type == TYPE_APP_DRAWER_SETTING) {
			key = 2;
		}
		if (FunctionPurchaseManager.getInstance(mContext.getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_CAN_USE) {
			key++;
		}
		int[] idArray = mRandomEffectorIdArray.get(key);
		if (idArray == null) {
			idArray = getAllEffectorIds(type, true);
			mRandomEffectorIdArray.put(key, idArray);
		}
		return idArray;
	}
	
	/**
	 * 获取所有特效
	 * 下标0-特效名数组(String[])；
	 * 下标1-特效值数组(int[])；
	 * 下表2-图片ID数组(int[])
	 */
	public Object[] getAllEffectors(int type, boolean isForRandomCustom) {
		Object[] objects = new Object[3];
		int[] effectorIdArray = getAllEffectorIds(type, isForRandomCustom);
		int size = effectorIdArray.length;
		String[] effectorNameArray = new String[size];
		int[] drawableIdArray = new int[size];
		for (int i = 0; i < size; i++) {
			EffectorInfo info = getEffectorInfoById(effectorIdArray[i]);
			effectorNameArray[i] = mContext.getResources().getString(info.mEffectorNameId);
			drawableIdArray[i] = info.mEffectorDrawableId;
		}
		objects[0] = effectorNameArray;
		objects[1] = effectorIdArray;
		objects[2] = drawableIdArray;
		return objects;
	}
	
	/**
	 * 获取所有特效（进行2D与3D的分拆）
	 * 下标0-特效名数组(String[][])；
	 * 下标1-特效值数组(int[][])；
	 * 下表2-图片ID数组(int[][])
	 */
	public Object[] getAllEffectorsWithSplit(int type, boolean isForRandomCustom) {
		Object[] objects = new Object[2];
		int[] effectorIdArray = getAllEffectorIds(type, isForRandomCustom);
		ArrayList<Integer> effectorIdList2D = new ArrayList<Integer>();
		ArrayList<Integer> effectorIdList3D = new ArrayList<Integer>();
		for (int id : effectorIdArray) {
			if (checkEffectorIsBothSupport(id)) { // 2D特效，且不是随机和随机自定义
				if (/*id != IEffectorIds.EFFECTOR_TYPE_RANDOM
					&&*/ id != IEffectorIds.EFFECTOR_TYPE_RANDOM_CUSTOM) {
					effectorIdList2D.add(id);
				}
			} else {
				effectorIdList3D.add(id);
			}
		}
		objects[0] = getEffectorByIdList(effectorIdList3D);
		objects[1] = getEffectorByIdList(effectorIdList2D);
		return objects;
	}
	
	/**
	 * 获取不能使用的特效ID数组
	 * @return
	 */
	public int[] getCanNotUseEffectorIds(int type, boolean isForRandomCustom) {
		int[] idArray = getAllEffectorIds(type);
		boolean isUseShellPlugin = true;
		boolean isRemovePrime = false;
		if (FunctionPurchaseManager.getInstance(mContext.getApplicationContext()).getPayFunctionState(
				FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_GONE
				|| (isForRandomCustom && FunctionPurchaseManager.getInstance(
						mContext.getApplicationContext()).getPayFunctionState(
						FunctionPurchaseManager.PURCHASE_ITEM_EFFECT) == FunctionPurchaseManager.STATE_VISABLE)) {
			isRemovePrime = true;
		}
		boolean isRemove3DEffector = false;
		if (!isUseShellPlugin && isForRandomCustom) {
			isRemove3DEffector = true;
		}
		ArrayList<Integer> effectorIdList = new ArrayList<Integer>();
		for (int id : idArray) {
			EffectorInfo info = getEffectorInfoById(id);
			if (info != null) {
				if ((isRemovePrime && info.mIsPrime)
						|| (isForRandomCustom && mCustomRemoveIdList
								.contains(info.mEffectorId))
						|| (isRemove3DEffector && !info.mIsBothSupprot)) {
					effectorIdList.add(info.mEffectorId);
				}
			}
		}
		int size = effectorIdList.size();
		int[] effectorIdArray = new int[size];
		for (int i = 0; i < size; i++) {
			effectorIdArray[i] = effectorIdList.get(i);
		}
		return effectorIdArray;
	}
	
	private Object[] getEffectorByIdList(ArrayList<Integer> idList) {
		Object[] objs = new Object[3];
		int size = idList.size();
		int[] effectorIdArray = new int[size];
		String[] effectorNameArray = new String[size];
		int[] drawableIdArray = new int[size];
		for (int i = 0; i < size; i++) {
			int id = idList.get(i);
			EffectorInfo info = getEffectorInfoById(id);
			effectorIdArray[i] = id;
			effectorNameArray[i] = mContext.getResources().getString(info.mEffectorNameId);
			drawableIdArray[i] = info.mEffectorDrawableId;
		}
		objs[0] = effectorNameArray;
		objs[1] = effectorIdArray;
		objs[2] = drawableIdArray;
		return objs;
	}
	
	private int[] getAllEffectorIds(int type) {
		String[] idArray = null;
		if (type == TYPE_SCREEN_SETTING || type == TYPE_EFFECT_TAB) { // 桌面特效
			// 使用3D模式需要使用3D的数组
			idArray = mContext.getResources().getStringArray(
					R.array.desktop_transition_value_for_3d);
		} else { // 功能表特效
			idArray = mContext.getResources().getStringArray(
					R.array.func_app_icon_effect_entry_values_for_3d);
		}
		return ArrayUtils.stringArrayToIntArray(idArray);
	}
	
	/**
	 * 获取所有特效的ID数组
	 * @param type
	 * @param isForRandomCustom
	 * @return
	 */
	public int[] getAllEffectorIds(int type, boolean isForRandomCustom) {
		int[] allIdArray = getAllEffectorIds(type);
		int[] cannotUseIdArray = getCanNotUseEffectorIds(type, isForRandomCustom);
		if (cannotUseIdArray == null || cannotUseIdArray.length <= 0) {
			return allIdArray;
		} else {
			int[] idArray = new int[allIdArray.length - cannotUseIdArray.length];
			int index = 0;
			for (int i = 0; i < allIdArray.length; i++) {
				boolean canUse = true;
				int id = allIdArray[i];
				for (int j = 0; j < cannotUseIdArray.length; j++) {
					if (id == cannotUseIdArray[j]) {
						canUse = false;
						break;
					}
				}
				if (canUse) {
					idArray[index] = id;
					index++;
				}
			}
			return idArray;
		}
	}
	
	/**
	 * 获取是否有新的特效
	 * @param type 类型
	 * @return
	 */
	public boolean checkHasNewEffector(int type) {
		boolean flag = false;
		int[] idArray = getAllEffectorIds(type, false);
		for (int i = 0; i < idArray.length; i++) {
			EffectorInfo info = mEffectorInfoMap.get(idArray[i]);
			if (info != null) {
				if ((type == TYPE_SCREEN_SETTING && info.mIsScreenSettingShowNew)
						|| (type == TYPE_EFFECT_TAB && info.mIsEffectTabShowNew)
						|| (type == TYPE_APP_DRAWER_SETTING && info.mIsAppDrawerSettingShowNew)) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 根据类型获取显示New标识的特效ID数组
	 * @param type
	 * @return
	 */
	public CharSequence[] getNewEffectorIdArray(int type) {
		ArrayList<CharSequence> idList = new ArrayList<CharSequence>();
		int[] idArray = getAllEffectorIds(type, false);
		for (int i = 0; i < idArray.length; i++) {
			EffectorInfo info = mEffectorInfoMap.get(idArray[i]);
			if (info != null) {
				if ((type == TYPE_SCREEN_SETTING && info.mIsScreenSettingShowNew)
						|| (type == TYPE_EFFECT_TAB && info.mIsEffectTabShowNew)
						|| (type == TYPE_APP_DRAWER_SETTING && info.mIsAppDrawerSettingShowNew)) {
					idList.add(String.valueOf(info.mEffectorId));
				}
			}
		}
		if (idList.size() > 0) {
			return idList.toArray(new CharSequence[idList.size()]);
		} else {
			return null;
		}
	}
	
	/**
	 * 获取付费特效ID数组
	 * @return
	 */
	public CharSequence[] getPrimeEffectorIdArray() {
		if (mPrimeEffectorArray == null) {
			ArrayList<CharSequence> primeList = new ArrayList<CharSequence>();
			for (int i = 0; i < mEffectorInfoMap.size(); i++) {
				EffectorInfo info = mEffectorInfoMap.get(mEffectorInfoMap.keyAt(i));
				if (info != null && info.mIsPrime) {
					primeList.add(String.valueOf(info.mEffectorId));
				}
			}
			mPrimeEffectorArray = primeList.toArray(new CharSequence[primeList.size()]);
		}
		return mPrimeEffectorArray;
	}
	
	/**
	 * 更新滚屏特效不显示New标示
	 * @param type
	 */
	public void updateEffectorNewState(int type, boolean newState) {
		ArrayList<EffectorInfo> infoList = new ArrayList<EffectorInfo>();
		int[] idArray = getAllEffectorIds(type, false);
		for (int i = 0; i < idArray.length; i++) {
			EffectorInfo info = mEffectorInfoMap.get(idArray[i]);
			if (info != null) {
				boolean flag = false;
				if (type == TYPE_SCREEN_SETTING && info.mIsScreenSettingShowNew != newState) {
					info.mIsScreenSettingShowNew = newState;
					flag = true;
				} else if (type == TYPE_EFFECT_TAB && info.mIsEffectTabShowNew != newState) {
					info.mIsEffectTabShowNew = newState;
					flag = true;
				} else if (type == TYPE_APP_DRAWER_SETTING && info.mIsAppDrawerSettingShowNew != newState) {
					info.mIsAppDrawerSettingShowNew = newState;
					flag = true;
				}
				if (flag) {
					infoList.add(info);
				}
			}
		}
		mDataModel.updateEffectorInfoList(infoList);
	}
	
	/**
	 * 根据特效ID查询是否付费特效
	 * @param effectorId
	 * @return
	 */
	public boolean checkEffectorIsPrime(int effectorId) {
		return getEffectorInfoById(effectorId).mIsPrime;
	}
	
	/**
	 * 根据特效ID查询是否3D特有特效
	 * @param effectorId
	 * @return
	 */
	public boolean checkEffectorIsBothSupport(int effectorId) {
		return getEffectorInfoById(effectorId).mIsBothSupprot;
	}
	
	/**
	 * xml文件与数据库进行比对，查找是否有新加特效，若有，将新特效加入数据库
	 */
	public boolean compareHasNewEffector() {
		boolean flag = mDataModel.compareHasNewEffector(mEffectorInfoMap);
		if (flag) { // 如果有新特效，需要重新初始化New标致属性
			mDataModel.initEffectorInfoMapShowNewFlag(mEffectorInfoMap);
		}
		return true;
	}
}
