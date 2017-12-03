package com.jiubang.ggheart.apps.desks.Preferences.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.gau.go.launcherex.R;

/**
 * 
 * <br>类描述:普通确认对话框
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-10-29]
 */
public class DesksettingNoAdvertTipDialog extends BaseDialog {

	public DesksettingNoAdvertTipDialog(Context context) {
		super(context);
	}

	public DesksettingNoAdvertTipDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	public View getContentView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.desk_setting_hadpay_dialog, null);
//		mDialogLayout = (LinearLayout) view.findViewById(R.id.dialog_layout);
//		mOkButton = (Button) view.findViewById(R.id.dialog_ok);
		
		return view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNegativeButtonVisible(View.GONE);
		setTitle(R.string.desksetting_pay_dialog_title);
	}

 
}
