package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.gau.go.launcherex.R;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingBaseInfo;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingClassifyInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingTitleView;

/**
 * 分类对话框适配器
 * @author yejijiong
 *
 */
public class DeskSettingClassifyDialogAdapter extends BaseAdapter {

	private Context mContext;
	private int mNumColumns = 4;
	DeskSettingClassifyInfo mClassifyInfo;
	private ConcurrentHashMap<Integer, DeskSettingClassifyDialogGridViewAdapter> mListItemAdapterMap;
	private OnClassifyDialogSelectListener mListener;

	public DeskSettingClassifyDialogAdapter(Context context,
			DeskSettingClassifyInfo deskSettingClassifyInfo, OnClassifyDialogSelectListener listener) {
		mContext = context;
		mClassifyInfo = deskSettingClassifyInfo;
		mListItemAdapterMap = new ConcurrentHashMap<Integer, DeskSettingClassifyDialogGridViewAdapter>();
		mListener = listener;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mClassifyInfo != null) {
			count = mClassifyInfo.getListItemInfoList().size();
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
			convertView = inflater.inflate(
					R.layout.desk_setting_dialog_for_classify_choice_list_item,
					null);
		}
		DeskSettingBaseInfo info = mClassifyInfo
				.getListItemInfoList().get(position);
		DeskSettingClassifyDialogGridViewAdapter adapter;
		if (mListItemAdapterMap.containsKey(position)) {
			adapter = mListItemAdapterMap.get(position);
		} else {
			adapter = new DeskSettingClassifyDialogGridViewAdapter(mContext,
					info, mClassifyInfo, mListener);
			mListItemAdapterMap.put(position, adapter);
		}
		GridView gridView = (GridView) convertView
				.findViewById(R.id.desk_setting_classify_choice_list_item_gridview);
		gridView.setVerticalSpacing(DrawUtils.dip2px(8));
		gridView.setHorizontalSpacing(DrawUtils.dip2px(17));
		gridView.setNumColumns(mNumColumns);
		gridView.setAdapter(adapter);
		DeskSettingTitleView titleView = (DeskSettingTitleView) convertView
				.findViewById(R.id.desk_setting_classify_choice_list_item_title);
		titleView.findViewById(R.id.title_view_line).setVisibility(View.VISIBLE);
		String title = info.getTitle();
		if (title != null && !title.equals("")) {
			titleView.setVisibility(View.VISIBLE);
			titleView.setTitleText(title);
		} else {
			titleView.setVisibility(View.GONE);
		}
		return convertView;
	}

	public void setNumColumns(int numColumns) {
		if (numColumns > 0) {
			mNumColumns = numColumns;
		}
	}

	public void checkBoxStatusChange(boolean isCheck) {
		for (DeskSettingClassifyDialogGridViewAdapter adapter : mListItemAdapterMap
				.values()) {
			adapter.checkBoxStatusChange(isCheck);
		}
	}
	
	public CharSequence[] getMultiChooseVlaue() {
		int size = mListItemAdapterMap.values().size();
		Object[] objs = new Object[size];
		for (int i = 0; i < size; i++) {
			DeskSettingClassifyDialogGridViewAdapter adapter = mListItemAdapterMap
					.get(i);
			objs[i] = adapter.getMultiChooseVlaue();
		}
		size = 0;
		for (int i = 0; i < objs.length; i++) {
			CharSequence[] values = (CharSequence[]) objs[i];
			size += values.length;
		}
		CharSequence[] values = new CharSequence[size];
		int index = 0;
		for (int i = 0; i < objs.length; i++) {
			CharSequence[] vs = (CharSequence[]) objs[i];
			for (CharSequence v : vs) {
				values[index] = v;
				index++;
			}
		}
		return values;
	}
	
	public void updateChooseImage(CharSequence value) {
		for (DeskSettingClassifyDialogGridViewAdapter adapter : mListItemAdapterMap.values()) {
			adapter.updateChooseImage(value);
		}
	}
	
	public void removePrimeImage() {
		for (DeskSettingClassifyDialogGridViewAdapter adapter : mListItemAdapterMap.values()) {
			adapter.removePrimeImage();
		}
	}
}
