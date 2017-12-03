package com.jiubang.ggheart.components.advert.untils;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.BaseDialog;

/**
 * 
 * <br>类描述:普通确认对话框
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-10-29]
 */
public class DialogNoAdPayMsgCenter extends BaseDialog {
	private TextView mMsgView; //提示内容
	protected View.OnClickListener mOtherOnClickListener;


	public DialogNoAdPayMsgCenter(Context context) {
		super(context);
	}

	public DialogNoAdPayMsgCenter(Context context, int theme) {
		super(context, theme);
	}

	@Override
	public View getContentView() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_no_advert_pay_ad_center, null);
//		mDialogLayout = (LinearLayout) view.findViewById(R.id.dialog_layout);
//		mTitle = (TextView) view.findViewById(R.id.dialog_title);
		mMsgView = (TextView) view.findViewById(R.id.dialog_msg);
//		mOkButton = (Button) view.findViewById(R.id.dialog_ok);
//		mCancelButton = (Button) view.findViewById(R.id.dialog_cancel);
//		mOtherButton = (Button) view.findViewById(R.id.dialog_other);
//		setOtherButtonLister();
		setOtherButtonVisible(View.VISIBLE);
		return view;
	}


	/**
	 * <br>功能简述:设置显示内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param msg
	 */
	public void setMessage(String msg) {
		if (mMsgView != null) {
			mMsgView.setText(msg);
		}
	}
	
	/**
	 * <br>功能简述:设置显示内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param msg
	 */
	public void setMessage(CharSequence msg) {
		if (mMsgView != null) {
			mMsgView.setText(msg);
		}
	}

	/**
	 * <br>功能简述:设置显示内容
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param textId
	 */
	public void setMessage(int textId) {
		if (mMsgView != null) {
			mMsgView.setText(getContext().getText(textId));
		}
	}
	
	/**
	 * <br>功能简述:设置带html解释的string(例如:指定内空指定颜色)
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param string
	 */
	public void setHtmlMessage(String string) {
		if (mMsgView != null && string != null) {
			mMsgView.setText(Html.fromHtml(string));  
		}
	}


}
