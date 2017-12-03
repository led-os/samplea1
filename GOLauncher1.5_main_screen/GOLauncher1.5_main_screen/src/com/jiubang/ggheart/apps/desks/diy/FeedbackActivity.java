package com.jiubang.ggheart.apps.desks.diy;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.AppUtils;
import com.go.util.log.LogUnit;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingBaseActivity;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.components.DeskButton;
import com.jiubang.ggheart.components.DeskEditText;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 反馈activity
 * @author chenguanyu
 *
 */
public class FeedbackActivity extends DeskSettingBaseActivity {
	
	// 问题模块
	private DeskSettingItemListView mProblemModule = null;
	// 标题描述
	private DeskEditText mProblemTitle = null;
	// 内容描述
	private DeskEditText mProblemMsg = null;
	// 发送按钮
	private DeskButton mSendBtn = null;
	private int mPosition = -1;
	private int mVersionCode = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.feedback_activity_layout);
		
		mPosition = getIntent().getIntExtra("position", -1);
		
		initView();
		
		PackageManager pm = getPackageManager();
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (info != null) {
			mVersionCode = info.versionCode;
		}
	}
	
	/**
	 * 初始化
	 */
	private void initView() {
		mProblemModule = (DeskSettingItemListView) findViewById(R.id.feedback_module_item);
		mProblemModule.setOnValueChangeListener(this);
		mProblemTitle = (DeskEditText) findViewById(R.id.feedback_edit_title);
		mProblemMsg = (DeskEditText) findViewById(R.id.feedback_edit_msg);
		mSendBtn = (DeskButton) findViewById(R.id.feedback_send_btn);
		mSendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isOKToSend = haveAllInfomations();
				if (isOKToSend) {
					sendFeedback();
					finish();
				}
			}
		});
	}
	
	private void sendFeedback() {
		if (GoAppUtils.isAppExist(this, PackageName.GO_DEBUG_HELPER_PKGNAME)) {
			sendFeedbackService();
		} else {
			sendEmail();
		}
	}
	
	private void sendFeedbackService() {
		int versionCode = AppUtils.getVersionCodeByPkgName(this,
		        PackageName.GO_DEBUG_HELPER_PKGNAME);
		if (versionCode > 2) {
			// 采用新版feedback形式
			String contentTitle = createEmailSubject();
			String contentDetails = createEmailBody();
			String content = contentTitle + "\n\n" + contentDetails;
			
			LogUnit.feedbackDebugService(this, content);
		} else if (versionCode > 1) {
			// 采用新的feedback形式
			Log.i("debugservice", "-----4----DiyScheduler-----feedback-");
			LogUnit.feedbackDebugService(this);
		}
	
	}
	
	@Override
	public boolean onValueChange(DeskSettingItemBaseView view, Object value) {
		
		if (view == mProblemModule) {
			updateSummary(view);
		}
		
		return true;
	}
	
	/**
	 * 更新summary显示
	 * @param view
	 */
	@SuppressLint("ResourceAsColor")
	private void updateSummary(DeskSettingItemBaseView view) {
		DeskSettingItemListView itemListView = (DeskSettingItemListView) view;
		itemListView.updateSumarryText();
		itemListView.setTitleColor(R.color.desk_setting_item_title_color);
	}
	
	/**
	 * 判断是否所有信息都已完成填写
	 */
	private boolean haveAllInfomations() {
		boolean isAllInfomation = true;
		String problemSummary = mProblemModule.getSummaryText();
		String title = mProblemTitle.getText().toString().trim();
		String msg = mProblemMsg.getText().toString().trim();
		if (problemSummary == null || problemSummary.equals("") || problemSummary.equals(getString(R.string.feedback_problem_summary))) {
			isAllInfomation = false;
//			mProblemModule.showDialog();
			mProblemModule.getTitleTextView().setTextColor(Color.RED);
		} else if (title == null || title.equals("")) {
			isAllInfomation = false;
			Toast.makeText(this, R.string.feedback_edit_title_tip, Toast.LENGTH_LONG).show();
		} else if (msg == null || msg.equals("")) {
			isAllInfomation = false;
			Toast.makeText(this, R.string.feedback_edit_msg_tip, Toast.LENGTH_LONG).show();
		}
		return isAllInfomation;
	}
	
	private String createEmailSubject() {
		String bugForMailString = this.getResources().getString(
				R.string.feedback_select_type_bug_for_mail);
		String suggestionForMailString = this.getResources().getString(
				R.string.feedback_select_type_suggestion_for_mail);
		String questionForMailString = this.getResources().getString(
				R.string.feedback_select_type_question_for_mail);
		final CharSequence[] itemsForMail = { bugForMailString,
				suggestionForMailString, questionForMailString };
		String subject = "GO Launcher EX(v"
				+ this.getString(R.string.curVersion) + "--versionCode:" + mVersionCode + ") Feedback("
				+ itemsForMail[mPosition] + ")/" + mProblemModule.getSummaryText();
		return subject;
	}
	
	private String createEmailBody() {
		// String content =
		// this.getString(R.string.rate_go_launcher_mail_content) + "\n\n";
		String contentTitle = getString(R.string.feedback_mail_tip_title)
				+ mProblemTitle.getText().toString() + "\n\n";
		String contentDetails = getString(R.string.feedback_mail_tip_detail)
				+ mProblemMsg.getText().toString() + "\n\n";
		String content = contentTitle + contentDetails;
		StringBuffer body = new StringBuffer(content);
		body.append("\nProduct=" + android.os.Build.PRODUCT);
		body.append("\nPhoneModel=" + android.os.Build.MODEL);
		body.append("\nROM=" + android.os.Build.DISPLAY);
		body.append("\nBoard=" + android.os.Build.BOARD);
		body.append("\nDevice=" + android.os.Build.DEVICE);
		body.append("\nDensity="
				+ String.valueOf(this.getResources().getDisplayMetrics().density));
		body.append("\nPackageName=" + this.getPackageName());
		body.append("\nAndroidVersion=" + android.os.Build.VERSION.RELEASE);
		body.append("\nTotalMemSize="
				+ (AppUtils.getTotalInternalMemorySize() / 1024 / 1024)
				+ "MB");
		body.append("\nFreeMemSize="
				+ (AppUtils.getAvailableInternalMemorySize() / 1024 / 1024)
				+ "MB");
		body.append("\nRom App Heap Size="
				+ Integer
						.toString((int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L))
				+ "MB");
		return body.toString();
				
	}
	
	/**
	 * 发送邮件
	 * @param feedbackType 反馈类型
	 */
	private void sendEmail() {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String[] receiver = new String[] { "golauncher@goforandroid.com" };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receiver);

		String subject = createEmailSubject();
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		
		String body = createEmailBody();
		emailIntent.putExtra(Intent.EXTRA_TEXT, body);
		emailIntent.setType("plain/text");
		try {
			startActivity(emailIntent);
		} catch (Exception e) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://golauncher.goforandroid.com"));
			intent.setClassName("com.android.browser",
					"com.android.browser.BrowserActivity");
			try {
				startActivity(intent);
			} catch (Exception e2) {
				Toast.makeText(this, R.string.feedback_no_browser_tip,
						Toast.LENGTH_SHORT).show();
				Log.i("GoLauncher", "startActivityForResult have exception = "
						+ e.getMessage());

			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mProblemModule != null) {
			mProblemModule = null;
		}
		if (mProblemTitle != null) {
			mProblemTitle = null;
		}
		if (mProblemMsg != null) {
			mProblemMsg = null;
		}
		if (mSendBtn != null) {
			mSendBtn.setOnClickListener(null);
			mSendBtn = null;
		}
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
}
