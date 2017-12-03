package com.jiubang.ggheart.components.advert.untils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;

/**
 * 
 */
public class DialogNoAdvertPayActivity extends Activity {
	public Context mContext;
	private int mEntrace = 202;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mContext = this;
		showNotificationDialog();
		mEntrace = getIntent().getIntExtra("entrace", 202);
	}
	
	
	public void showNotificationDialog() {
		DialogConfirm dialog = new DialogConfirm(mContext);
		dialog.show();
		dialog.setTitle(R.string.dialog_notification_no_advert_title);
		dialog.setMessage(R.string.dialog_notification_no_advert_message);
		dialog.setPositiveButton(R.string.dialog_notification_no_advert_block_ads, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DeskSettingUtils.showPayDialog(mContext, mEntrace); // 显示付费对话框
				DialogNoAdvertPayActivity.this.finish();
			}
		});

		dialog.setNegativeButton(null, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogNoAdvertPayActivity.this.finish();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				DialogNoAdvertPayActivity.this.finish();
			}
		});
	}
	
}
