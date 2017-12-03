/*
 * 文 件 名:  AppRecommendDialogActivity.java
 * 版    权:  3G
 * 描    述:  <描述>
 * 修 改 人:  liuxinyang
 * 修改时间:  2012-12-11
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.jiubang.ggheart.appgame.base.component;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;

/**
 * <br>类描述:程序安装完成后，弹出对话框提示用户激活程序
 * <br>功能详细描述:
 * 
 * @author  liuxinyang
 * @date  [2012-12-11]
 */
public class AppRecommendDialogActivity extends Activity implements OnClickListener {

	private String mPackageName = "";

	private Button mActiveButton = null;

	private Button mCancelButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appgame_recommend_app_open);
		mPackageName = getIntent().getStringExtra("packageName");
		initView();
	}

	private void initView() {
		if (mPackageName == null || mPackageName.equals("")) {
			return;
		}
		PackageManager pm = this.getPackageManager();
		ApplicationInfo info;
		try {
			info = pm.getPackageInfo(mPackageName, 1).applicationInfo;
			// 设置软件名称 
			TextView tv = (TextView) findViewById(R.id.appgame_recommend_app_open_tip);
			String appLabel = info.loadLabel(pm).toString();
			String str = this.getResources().getString(R.string.advert_dialog_content, appLabel);
			tv.setText(str);
			// 设置软件名称
			ImageView icon = (ImageView) findViewById(R.id.appgame_recommend_app_open_icon);
			Drawable drawable = info.loadIcon(pm);
			icon.setBackgroundDrawable(drawable);
			mActiveButton = (Button) findViewById(R.id.appgame_recommend_app_open_active);
			mActiveButton.setOnClickListener(this);
			mCancelButton = (Button) findViewById(R.id.appgame_recommend_app_open_cancel);
			mCancelButton.setOnClickListener(this);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mActiveButton) {
			PackageManager pm = this.getPackageManager();
			if (pm == null) {
				return;
			}
			Intent in = null;
			in = pm.getLaunchIntentForPackage(mPackageName);
			if (in == null) {
				return;
			}
			in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			in.setAction(android.content.Intent.ACTION_VIEW);
			ApplicationProxy.getContext().startActivity(in);
//			StatisticsData.removeTreatment(ApplicationProxy.getContext(), mPackageName);
			this.finish();
		} else if (v == mCancelButton) {
			 this.finish();
		}
	}
	
}
