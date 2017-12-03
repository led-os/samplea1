package com.jiubang.ggheart.apps.desks.purchase;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.BroadCaster.BroadCasterObserver;
import com.golauncher.message.IAppManagerMsgId;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingAdvancedPayActivity;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogConfirm;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * <br>类描述:功能收费管理
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013年9月11日]
 */
public class FunctionPurchasePageActivity extends Activity
		implements
			OnClickListener,
			BroadCasterObserver {

	public static final String ENTRANCEID = "entranceid";
	public static final String TABID = "tabid";
	public static final int MSG_ITEM_PURCHASED = 1;
	private ArrayList<Integer> mPurchaseItems;
	private String[] mItemInfos;
	private String[] mItemTitle;
	private int[] mItemImgs = { R.drawable.function_purhcae_noad,
			R.drawable.function_purhcae_transitions, R.drawable.function_purhcae_lock,
			R.drawable.function_purhcae_actions, R.drawable.function_purhcae_filter };
	private LayoutInflater mInflater;
	private TextView mTitleView;
	private String mEntranceId;
	private String mTabId;
	private FunctionTrialTimeLeftView mTimeView;
	private BroadcastReceiver mReceiver;
	protected void onCreate(android.os.Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.function_purchase);
		findViewById(R.id.btn_fullpay).setOnClickListener(this);
		mTimeView = (FunctionTrialTimeLeftView) findViewById(R.id.timeleft);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTitleView = (TextView) findViewById(R.id.function_purchase_title);
		mItemTitle = getResources().getStringArray(R.array.function_purchase_items);
		mItemInfos = getResources().getStringArray(R.array.function_purchase_items_summery);
		setUpContentView();
		FunctionPurchaseManager.getInstance(getApplicationContext()).registerObserver(this);
		mEntranceId = getIntent().getStringExtra(ENTRANCEID);
		mTabId = getIntent().getStringExtra(TABID);
		GuiThemeStatistics.functionPurchaseStaticData("-1", "h000", 1, mEntranceId, mTabId,
				FunctionPurchaseManager.getInstance(getApplicationContext()).getTrialStatus());
		initReceiver();
	}

	private void initReceiver() {
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String packageName = intent.getDataString();
				if (packageName.equals(LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
					finish();
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver, filter);
	}
	private void setUpContentView() {
		FunctionPurchaseManager purchaseManager = FunctionPurchaseManager
				.getInstance(getApplicationContext());
		if (purchaseManager.queryItemPurchaseState(FunctionPurchaseManager.PURCHASE_ITEM_FULL)) {
			finish();
			return;
		}
		int[] items = purchaseManager.getPurchaseItems();
		if (items != null && items.length > 0) {
			mPurchaseItems = new ArrayList<Integer>();
			for (int i = 0; i < items.length; i++) {
				if (!purchaseManager.queryItemPurchaseState(items[i])) {
					mPurchaseItems.add(items[i]);
				}
			}
		}
		if (mPurchaseItems == null || mPurchaseItems.isEmpty()) {
			finish();
			return;
		}
		if (items.length != mPurchaseItems.size()) {
			findViewById(R.id.bottom_btn_group).setVisibility(View.GONE);
		}
		long trialDate = FunctionPurchaseManager.getInstance(getApplicationContext())
				.getTrialDate();
		long now = System.currentTimeMillis();
		boolean hasBuy = mPurchaseItems.size() < mItemInfos.length;
		if (FunctionPurchaseManager.getInstance(getApplicationContext()).isTrial() && trialDate > 0
				&& now < trialDate && trialDate - now < AlarmManager.INTERVAL_DAY) {
			mTimeView.setVisibility(View.VISIBLE);
			String title = null;
			if (hasBuy) {
				title = getString(R.string.function_part_trial_time_remain);
			} else {
				title = getString(R.string.function_all_trial_time_remain);
			}
			int timeLeft = (int) ((trialDate - now) / (1000 * 60 * 60));
			title = title.replace("%s", "<b><font color=#ffaa00>" + String.valueOf(timeLeft)
					+ "</font></b>");
			mTitleView.setText(Html.fromHtml(title));
		} else if (trialDate > 0) {
			if (hasBuy) {
				mTitleView.setText(R.string.function_part_trial_expired);
			} else {
				mTitleView.setText(R.string.function_all_trial_expired);
			}
		} else {
			mTitleView.setText(R.string.function_trial_title);
		}
		ListView list = (ListView) findViewById(R.id.functionlist);
		list.setCacheColorHint(Color.TRANSPARENT);
		list.setDivider(null);
		FunctionPurchaseAdapter adapter = new FunctionPurchaseAdapter();
		list.setAdapter(adapter);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FunctionPurchaseManager.getInstance(getApplicationContext()).unRegisterObserver(this);
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}
	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  rongjinsong
	 * @date  [2013年10月9日]
	 */
	public class FunctionPurchaseAdapter extends BaseAdapter implements OnClickListener {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mPurchaseItems != null) {
				return mPurchaseItems.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (mPurchaseItems != null) {
				return mPurchaseItems.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.function_purchase_item, null);
			}
			setUpView(convertView, position);
			return convertView;
		}

		private void setUpView(View view, int position) {
			ImageView img = (ImageView) view.findViewById(R.id.purchase_item_img);
			int id = (Integer) getItem(position);
			if (id > FunctionPurchaseManager.PURCHASE_ITEM_FULL) { // 开始设计时没考虑后面增加购买项，这里导致逻辑奇怪
				--id;
			}
			img.setImageResource(mItemImgs[id]);
			TextView infoText = (TextView) view.findViewById(R.id.purchase_item_info);
			infoText.setText(mItemInfos[id]);
			TextView title = (TextView) view.findViewById(R.id.purchase_item_title);
			title.setText(mItemTitle[id]);
			view.findViewById(R.id.purchase_item_get).setTag(position);
			view.findViewById(R.id.purchase_item_get).setOnClickListener(this);
			try {
				ImageView line = (ImageView) view.findViewById(R.id.list_line);
				Drawable bg = getResources().getDrawable(R.drawable.function_purhcae_line_list);
				if (bg != null) {
					line.setBackgroundDrawable(bg);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Object object = v.getTag();
			if (object != null && object instanceof Integer) {
				if (FunctionPurchaseManager.getInstance(getApplicationContext()).isPayAnyFunction()) {
					mTabId = FunctionPurchaseManager.TAB_FUNCTION_HAS_PAY;
				}
				FunctionPurchaseManager.getInstance(getApplicationContext()).purchaseItem(
						(Integer) getItem((Integer) object), mEntranceId, mTabId);
				GuiThemeStatistics.functionPurchaseStaticData(
						FunctionPurchaseManager.getInstance(getApplicationContext()).getSender(
								(Integer) getItem((Integer) object)), "j001", 0, mEntranceId,
						mTabId, FunctionPurchaseManager.getInstance(getApplicationContext())
								.getTrialStatus());
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_fullpay :

				if (FunctionPurchaseManager.getInstance(getApplicationContext()).isPayAnyFunction()) {
					mTabId = FunctionPurchaseManager.TAB_FUNCTION_HAS_PAY;
				}
				FunctionPurchaseManager.getInstance(getApplicationContext()).purchaseItem(
						FunctionPurchaseManager.PURCHASE_ITEM_FULL, mEntranceId, mTabId);
				GuiThemeStatistics.functionPurchaseStaticData("all", "j005", 0, mEntranceId,
						mTabId, FunctionPurchaseManager.getInstance(getApplicationContext())
								.getTrialStatus());
				finish();

				break;

			default :
				break;
		}
	}

	@Override
	public void onBCChange(int msgId, int param, Object ...object) {
		// TODO Auto-generated method stub
		switch (msgId) {
			case IAppManagerMsgId.FUNCTION_ITEM_PURCHASED :
				mHandler.sendEmptyMessage(MSG_ITEM_PURCHASED);
				break;

			default :
				break;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_ITEM_PURCHASED :
					setUpContentView();
					break;

				default :
					break;
			}
		};
	};
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& FunctionPurchaseManager.getInstance(getApplicationContext()).getTrialStatus() == FunctionPurchaseManager.STATUS_TRAIL_NON) {
			showTrialDialog();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	};
	
	private void showTrialDialog() {
		DialogConfirm dialog = new DialogConfirm(this);
		dialog.show();
		dialog.setTitle(R.string.desksetting_pay_dialog_trial);
		dialog.setMessage(R.string.desksetting_pay_dialog_trial_msg);
		dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FunctionPurchaseManager.ACTION_START_TRAIL);
				intent.putExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, mEntranceId);
				intent.putExtra(TABID, mTabId);
				sendBroadcast(intent);
				Toast.makeText(FunctionPurchasePageActivity.this, R.string.function_trial_tip, 3000)
						.show();
				finish();
			}
		});
		dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					finish();
					return true;
				}
				return false;
			}
		});
	}
}
