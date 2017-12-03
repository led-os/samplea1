package com.jiubang.ggheart.apps.desks.diy;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gau.go.launcherex.R;
import com.go.util.AppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogActivity;
import com.jiubang.ggheart.components.DeskButton;

/**
 * 如果设备不支持3D模式，则弹出该对话框引导用户下载2D版本
 * @author yangguanxiang
 *
 */
public class GoLauncherDeliverActivity extends DialogActivity implements OnClickListener {

	private String URL_GOTO_BLOG = "http://bbs.a9vg.com";
	private DeskButton mBtnDownload;
	private DeskButton mBtnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.golauncher_deliver_dialog);
		findViews();
	}

	private void findViews() {
		mBtnDownload = (DeskButton) findViewById(R.id.btn_download);
		mBtnDownload.setOnClickListener(this);
		mBtnCancel = (DeskButton) findViewById(R.id.btn_cancel);
		mBtnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.btn_download :
				gotoBlog();
				break;

			default :
				break;
		}
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppUtils.killProcess();
	}

	private void gotoBlog() {
		AppUtils.gotoBrowser(getApplicationContext(), URL_GOTO_BLOG);
	}
}
