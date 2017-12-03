package com.jiubang.ggheart.apps.desks.diy;

import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.VersionControl;
import com.go.util.device.Machine;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogActivity;
import com.jiubang.ggheart.apps.desks.diy.guide.RateGuideTask;
import com.jiubang.ggheart.components.DeskButton;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.Statistics;

/**
 * 
 * 类描述: 弹出框评分内容入口类
 * 功能详细描述:
 * @date  [2012-10-12]
 */
public class RateDialogContentActivity extends DialogActivity implements OnClickListener {
	
	public static final int VERSIONCODE_411 = 307;
	public static final int VERSIONCODE_412 = 309;
	public static final String MEM_CLEANED_DEFAULT = "20";
	public static final String EXTRA_EVENT = "extra_event";
	public static final String EXTRA_PARAMS = "extra_parameter";
	
	private TextView mRemindTextView;
	private LinearLayout mRate_Button;
	private RateGuideTask mRateGuidemanager;
	private int mEvent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRateGuidemanager = RateGuideTask.getInstacne(ApplicationProxy.getContext());
		initView();
	}
	
	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			PreferencesManager ratePreferences = new PreferencesManager(this,
					IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
			if (VersionControl.getCurrentVersionCode() >= VERSIONCODE_411
					&& VersionControl.isNewUser()) {
				ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, true);
			} else {
				ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, false);
			}
			ratePreferences.commit();
			mRateGuidemanager.rateStatistics(mEvent, mRateGuidemanager.hasShowLastVersion(), true);
		}
		return super.onKeyUp(keyCode, event);
	}



	public void initView() {
		setContentView(R.layout.desk_rate_dialog_content);
		LinearLayout dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
		limitDialogLayout(dialogLayout);
		ImageView img = (ImageView) findViewById(R.id.content_img);
		int layoutWidth = dialogLayout.getLayoutParams().width;
		img.getLayoutParams().height = (int) ((layoutWidth + 0.1f) / 620 * 290);
		mRemindTextView = (TextView) findViewById(R.id.remind_msg);
		LinearLayout feedback_Button = (LinearLayout) findViewById(R.id.feedback);
		feedback_Button.setOnClickListener(this);
		mRate_Button = (LinearLayout) findViewById(R.id.rate);
		mRate_Button.setOnClickListener(this);
		LinearLayout never_Button = (LinearLayout) findViewById(R.id.remind_never);
		never_Button.setOnClickListener(this);
		PreferencesManager preferencesManager = new PreferencesManager(this,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE,
				Context.MODE_PRIVATE);
		preferencesManager.putBoolean(IPreferencesIds.FIRST_RUN_REMIND_RATE,
				false);
		preferencesManager.commit();

		// 根据不同的触发条件，显示不同的提示语。
		String[] params = getIntent().getStringArrayExtra(EXTRA_PARAMS);
		mEvent = getIntent().getIntExtra(EXTRA_EVENT,
				RateGuideTask.EVENT_UNKONW);
		String rateTimes = mRateGuidemanager.hasShowLastVersion() ? "2" : "1";
		GuiThemeStatistics.ratingDialogStaticData("f000", mRateGuidemanager.getStatisticsEntrance(mEvent), rateTimes);
		// 二次评分引导弹框
		if ("2".equals(rateTimes)) {
			mRemindTextView.setText(getRemindMsgTwiceGuide());
		}else{ // 首次评分引导弹框
			switch (mEvent) {
				case RateGuideTask.EVENT_APPLY_THEME:
					mRemindTextView.setText(R.string.rate_remind_msg_applytheme);
					break;
				case RateGuideTask.EVENT_RECENTLY_CLEAN:
					mRemindTextView.setText(R.string.rate_remind_msg_recentlyclean);
					break;
				case RateGuideTask.EVENT_RUNNING_CLEAN:
					mRemindTextView.setText(getString(
							R.string.rate_remind_msg_runningclean,
							(params != null && params.length > 0) ? params[0]
									: MEM_CLEANED_DEFAULT));
					break;
				default:
					// event获取异常的情况下，保持原先逻辑
					// 非200包,国内用户
					if (!Statistics.is200ChannelUid(this) && GoAppUtils.isCnUser(this)) {
						mRemindTextView.setText(R.string.rate_remind_msg_cn);
						DeskButton button = (DeskButton) mRate_Button.findViewById(R.id.rate_child);
						if (button != null) {
							button.setText(R.string.rate_remind_tips);
					}
					}
					break;
				}
		}
	}
	
	@Override
	public void onClick(View v) {
		PreferencesManager preferencesManager = new PreferencesManager(this,
				IPreferencesIds.DESK_SHAREPREFERENCES_FILE, Context.MODE_PRIVATE);
		PreferencesManager ratePreferences = new PreferencesManager(this,
				IPreferencesIds.PREFERENCE_RATE_CONFIG, Context.MODE_PRIVATE);
		switch (v.getId()) {
			case R.id.feedback :
				// v4.11及以上版本，初次使用，点击“取消”，升级新版本后，将再次弹出评分引导
				if (VersionControl.getCurrentVersionCode() >= VERSIONCODE_411
						&& VersionControl.isNewUser()) {
					ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, true);
				} else {
					ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, false);
				}
				ratePreferences.commit();
				mRateGuidemanager.rateStatistics(mEvent, mRateGuidemanager.hasShowLastVersion(), true);
				finish();
				break;

			case R.id.rate :
				if (!Statistics.is200ChannelUid(this) && GoAppUtils.isCnUser(this)) {
					mRateGuidemanager.sendMail();
				} else {
					mRateGuidemanager.directGooglePlayRate();
					ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, false);
					ratePreferences.commit();
					preferencesManager.putBoolean(IPreferencesIds.FIRST_RUN_REMIND_RATE, false);
					preferencesManager.commit();
				}
				mRateGuidemanager.rateStatistics(mEvent, mRateGuidemanager.hasShowLastVersion(), false);
				break;

			case R.id.remind_never :
				ratePreferences.putBoolean(IPreferencesIds.REMIND_RATE, false);
				ratePreferences.commit();
				preferencesManager.putBoolean(IPreferencesIds.FIRST_RUN_REMIND_RATE, false);
				preferencesManager.commit();
				mRateGuidemanager.rateStatistics(mEvent, mRateGuidemanager.hasShowLastVersion(), true);
				break;
			default :
				break;
		}
		this.finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initView();
	}

	/**
	 * <br>功能简述: 获取二次评分引导的提示语
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private String getRemindMsgTwiceGuide(){
		String remindMsg;
		String timeMsg;
		String timeUnit = null;
		long timeValue;
		long timeMsc = System.currentTimeMillis() - Machine.checkInstallMsc(this);
		timeMsc = timeMsc < 0 ? 0 : timeMsc;
		long hours = TimeUnit.MILLISECONDS.toHours(timeMsc);
		long days = TimeUnit.MILLISECONDS.toDays(timeMsc);
		if (days >= 5) {
			try {
				timeUnit = getString(R.string.unit_days_5_);
			} catch (Exception e) {
			}
			if (TextUtils.isEmpty(timeUnit)) {
				timeUnit = getString(R.string.unit_days);
			}
			timeValue = days;
		} else if (days >= 3) {
			try {
				timeUnit = getString(R.string.unit_days_2_4);
			} catch (Exception e) {
			}
			if (TextUtils.isEmpty(timeUnit)) {
				timeUnit = getString(R.string.unit_days);
			}
			timeValue = days;
		} else if (hours >= 5) {
			try {

			} catch (Exception e) {
			}
			if (TextUtils.isEmpty(timeUnit)) {
				timeUnit = getString(R.string.unit_hours);
			}
			timeValue = hours;
		} else if (hours >= 2) {
			try {
				timeUnit = getString(R.string.unit_hours_2_4);
			} catch (Exception e) {
			}
			timeUnit = getString(R.string.unit_hours_2_4);
			if (TextUtils.isEmpty(timeUnit)) {
				timeUnit = getString(R.string.unit_hours);
			}
			timeValue = hours;
		} else {
			try {
				timeUnit = getString(R.string.unit_hours_1);
			} catch (Exception e) {
			}
			if (TextUtils.isEmpty(timeUnit)) {
				timeUnit = getString(R.string.unit_hours);
			}
			timeValue = hours;
		}
		timeValue = timeValue <= 0 ? 1 : timeValue;
		timeMsg = timeValue + " " + timeUnit;
		remindMsg = getString(R.string.rate_remind_msg_twice, timeMsg);
		return remindMsg;
	}
}
