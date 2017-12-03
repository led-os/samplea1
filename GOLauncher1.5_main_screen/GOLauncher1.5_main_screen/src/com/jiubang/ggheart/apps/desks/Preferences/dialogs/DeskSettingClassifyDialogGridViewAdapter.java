package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingBaseInfo;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingClassifyGridItemInfo;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingClassifyInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingClassifyDialogGridItemView;

/**
 * 分类对话框Grid适配器
 * @author yejijiong
 *
 */
public class DeskSettingClassifyDialogGridViewAdapter extends BaseAdapter {

	private Context mContext;
	protected CharSequence[] mEntries; //内容的数组
	protected CharSequence[] mEntryValues; //内容的数组对应的value数组
	protected int[] mImageId; //图片数组(drawable)
	private ConcurrentHashMap<CharSequence, DeskSettingClassifyGridItemInfo> mGridItemInfoMap;
	private CharSequence[] mMultipleUnchooseValues;
	private CharSequence mCurSingleChooseValue;
	private CharSequence[] mCurMultipleChooseValue;
	private DeskSettingClassifyInfo mClassifyInfo;
	private CharSequence[] mNeedShowNewTag;
	private CharSequence[] mNeedPayList;
	private OnClassifyDialogSelectListener mListener;
	private Drawable mIconBg;
	
	public DeskSettingClassifyDialogGridViewAdapter(Context context,
			DeskSettingBaseInfo listItemInfo,
			DeskSettingClassifyInfo classifyInfo, OnClassifyDialogSelectListener listener) {
		mContext = context;
		mEntries = listItemInfo.getEntries();
		mEntryValues = listItemInfo.getEntryValues();
		mImageId = listItemInfo.getImageId();
		mGridItemInfoMap = new ConcurrentHashMap<CharSequence, DeskSettingClassifyGridItemInfo>();
		mMultipleUnchooseValues = classifyInfo.getMultipleUnchooseValues();
		mCurSingleChooseValue = classifyInfo.getCurSingleChooseValue();
		mCurMultipleChooseValue = classifyInfo.getCurMultipleChooseValue();
		mNeedShowNewTag = classifyInfo.getNeedShowNewTag();
		mNeedPayList = classifyInfo.getNeedPayList();
		mClassifyInfo = classifyInfo;
		mListener = listener;
		mIconBg = classifyInfo.getGridItemIconBg();
	}
	
	@Override
	public int getCount() {
		int count = 0;
		if (mEntryValues != null) {
			count = mEntryValues.length;
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = (DeskSettingClassifyDialogGridItemView) inflater
					.inflate(R.layout.desk_setting_classifydialog_grid_item,
							null);
		}
		DeskSettingClassifyDialogGridItemView itemView = (DeskSettingClassifyDialogGridItemView) convertView;
		itemView.setDialogSelectListener(mListener);
		itemView.setItemInfo(getGridItemInfo(mContext.getResources().getDrawable(
				mImageId[position]), mIconBg, mEntries[position], mEntryValues[position], mEntries[position]));
		return convertView;
	}
	
	private DeskSettingClassifyGridItemInfo getGridItemInfo(Drawable icon,
			Drawable iconBg, CharSequence title, CharSequence value,
			CharSequence entrie) {
		if (mGridItemInfoMap.containsKey(value)) {
			return mGridItemInfoMap.get(value);
		} else {
			DeskSettingClassifyGridItemInfo info = new DeskSettingClassifyGridItemInfo();
			info.setIcon(icon);
			info.setIconBg(iconBg);
			info.setTitle(title);
			if (mClassifyInfo.getIsInMultiple()) {
				info.setCurStatus(DeskSettingClassifyGridItemInfo.STATUS_MULTI_CHOOSE);
			} else {
				info.setCurStatus(DeskSettingClassifyGridItemInfo.STATUS_SINGLE_CHOOSE);
			}
			if (mCurSingleChooseValue != null
					&& mCurSingleChooseValue.equals(value)) { // 单选状态下当前选中
				info.setIsCurSingleChoose(true);
			}
			if (mMultipleUnchooseValues != null
					&& mMultipleUnchooseValues.length > 0) { // 多选状态下不能选择
				boolean isMultipleCanChoose = true;
				for (CharSequence chooseValue : mMultipleUnchooseValues) {
					if (value.equals(chooseValue)) {
						isMultipleCanChoose = false;
						break;
					}
				}
				info.setMultipleCanChoose(isMultipleCanChoose);
			}
			if (mCurMultipleChooseValue != null && mCurMultipleChooseValue.length > 0) { // 多选状态下当前选中
				boolean isMultipleChoose = false;
				for (CharSequence chooseValue : mCurMultipleChooseValue) {
					if (value.equals(chooseValue) && info.isMultipleCanChoose()) {
						isMultipleChoose = true;
						break;
					}
				}
				info.setIsCurMultipleChoose(isMultipleChoose);
			} else { // 如果无多选下已经选中的，默认全选（去除不可使用的）
				if (info.isMultipleCanChoose()) {
					info.setIsCurMultipleChoose(true);
				} else {
					info.setIsCurMultipleChoose(false);
				}
			}
			if (mNeedShowNewTag != null && mNeedShowNewTag.length > 0) { // 检查是否显示New标识
				for (CharSequence showNewValue : mNeedShowNewTag) {
					if (value.equals(showNewValue)) {
						info.setIsNeedShowNew(true);
						break;
					}
				}
			}
			if (mNeedPayList != null && mNeedPayList.length > 0) { // 检查选项是否需要付费
				for (CharSequence payValue : mNeedPayList) {
					if (value.equals(payValue)) {
						info.setIsPrime(true);
						break;
					}
				}
			}
			info.setEntryValue(value);
			info.setEntrie(entrie);
			mGridItemInfoMap.put(value, info);
			return info;
		}
	}
	
	public void checkBoxStatusChange(boolean isCheck) {
		for (DeskSettingClassifyGridItemInfo info : mGridItemInfoMap.values()) {
			if (isCheck) {
				info.setCurStatus(DeskSettingClassifyGridItemInfo.STATUS_MULTI_CHOOSE);
			} else {
				info.setCurStatus(DeskSettingClassifyGridItemInfo.STATUS_SINGLE_CHOOSE);
			}
		}
		notifyDataSetChanged();
	}
	
	public CharSequence[] getMultiChooseVlaue() {
		ArrayList<CharSequence> list = new ArrayList<CharSequence>();
		for (DeskSettingClassifyGridItemInfo info : mGridItemInfoMap.values()) {
			if (info.isCurMultipleChoose()) {
				list.add(info.getEntryValue());
			}
		}
		return list.toArray(new CharSequence[list.size()]);
	}
	
	public void updateChooseImage(CharSequence curSingleChooseValue) {
		for (DeskSettingClassifyGridItemInfo info : mGridItemInfoMap.values()) {
			if (!info.getEntryValue().equals(curSingleChooseValue)) {
				info.setIsCurSingleChoose(false);
			} else {
				info.setIsCurSingleChoose(true);
			}
		}
		notifyDataSetChanged();
	}
	
	public void removePrimeImage() {
		for (DeskSettingClassifyGridItemInfo info : mGridItemInfoMap.values()) {
			if (info.isPrime()) {
				info.setIsPrime(false);
			} 
		}
		notifyDataSetChanged();
	}
}
