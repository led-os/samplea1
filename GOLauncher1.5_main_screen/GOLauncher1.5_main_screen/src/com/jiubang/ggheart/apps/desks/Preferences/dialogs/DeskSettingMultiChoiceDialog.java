package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingInfo;
import com.jiubang.ggheart.apps.desks.Preferences.info.DeskSettingMultiInfo;

/**
 * 
 * <br>类描述:多选对话框
 * <br>功能详细描述:
 * 
 * @author  chenguanyu
 * @date  [2012-9-12]
 */
public class DeskSettingMultiChoiceDialog extends DeskSettingBaseDialog {

	public DeskSettingMultiChoiceDialog(Context context, DeskSettingInfo deskSettingInfo,
			OnDialogSelectListener onDialogSelectListener) {
		super(context, deskSettingInfo, onDialogSelectListener);
	}

	@Override
	public View getContentView() {
		final DeskSettingMultiInfo deskSettingMultiInfo = mDeskSettingInfo.getMultiInInfo();

		if (deskSettingMultiInfo == null) {
			return null;
		}
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.desk_setting_dialog_for_list, null);
		
		final CharSequence[] entryValues = deskSettingMultiInfo.getEntryValues();
		String dialogTittle = deskSettingMultiInfo.getTitle();

		mTitle.setText(dialogTittle);
		ListView listView = (ListView) view
				.findViewById(R.id.desk_setting_dialog_singleormulti_list);
		final DeskSettingMultiDialogAdapter adapter = new DeskSettingMultiDialogAdapter(mContext,
				deskSettingMultiInfo);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean[] selectedGroup = adapter.getSelectedGroup();
				CheckBox checkBox = (CheckBox) view
						.findViewById(R.id.desk_setting_dialog_item_checkbox);

				if (checkBox.isChecked()) {
					checkBox.setChecked(false);
					selectedGroup[position] = false;
				} else {
					checkBox.setChecked(true);
					selectedGroup[position] = true;
				}

				setOkButtonEnabled(selectedGroup);

			}
		});

		setOkButtonEnabled(adapter.getSelectedGroup()); //设置确定按钮是否可点
		setPositiveButton(null, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//设置父类单选框选择得值 
				DeskSettingConstants.setparentInfoSelectValueTemp(mDeskSettingInfo);

				boolean[] selectedGroup = adapter.getSelectedGroup();
				ArrayList<Integer> selectIndexs = new ArrayList<Integer>();
				for (int i = 0; i < selectedGroup.length; i++) {
					if (selectedGroup[i]) {
						selectIndexs.add(i);
					}
				}

				int size = selectIndexs.size();
				if (size > 0) {
					CharSequence[] selectValues = new CharSequence[size];
					String[] values = new String[size];
					for (int i = 0; i < size; i++) {
						selectValues[i] = entryValues[selectIndexs.get(i)];
						values[i] = selectValues[i].toString();
						deskSettingMultiInfo.setSelectValues(values);
					}
					mOnDialogSelectListener.onDialogSelectValue(values);
					dismiss();
				} else {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.desk_setting_multi_select_tips), Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		mCancelButtonLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return view;
	}

	/**
	 * <br>功能简述:设置确定按钮是否可点
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param selectedGroup 勾选列表
	 */
	public void setOkButtonEnabled(boolean[] selectedGroup) {
		if (mOkButtonLayout == null || mChildOkButton == null) {
			return;
		}

		if (selectedGroup != null) {
			int selectedGroupSize = selectedGroup.length;
			for (int i = 0; i < selectedGroupSize; i++) {
				if (selectedGroup[i]) {
					mOkButtonLayout.setEnabled(true);
					mChildOkButton.setTextColor(mContext.getResources().getColor(
							R.color.desk_setting_button_color));
					return;
				}
			}
		}
		mOkButtonLayout.setEnabled(false);
		mChildOkButton.setTextColor(mContext.getResources().getColor(
				R.color.desk_setting_item_summary_color));
	}
}
