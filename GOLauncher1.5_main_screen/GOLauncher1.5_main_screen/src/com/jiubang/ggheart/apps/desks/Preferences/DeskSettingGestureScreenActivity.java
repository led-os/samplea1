package com.jiubang.ggheart.apps.desks.Preferences;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.SettingProxy;
import com.go.util.log.LogConstants;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingNewMarkManager.NewMarkKeys;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.DialogGoShortCut;
import com.jiubang.ggheart.apps.desks.Preferences.info.GoShortCutInfo;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemBaseView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingItemListView;
import com.jiubang.ggheart.apps.desks.diy.IRequestCodeIds;
import com.jiubang.ggheart.apps.desks.purchase.FunctionPurchaseManager;
import com.jiubang.ggheart.apps.desks.settings.AppList;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.GlobalSetConfig;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.GestureSettingInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.SysAppInfo;
import com.jiubang.ggheart.launcher.IconUtilities;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2012-9-10]
 */
public class DeskSettingGestureScreenActivity extends DeskSettingBaseActivity {

	//点击HOME健，上滑手势，下滑手势，双击空白处手势的四个view
	private DeskSettingItemListView mSettingHomeGesture;
	private DeskSettingItemListView mSettingUpGesture;
	private DeskSettingItemListView mSettingDownGesture;
	private DeskSettingItemListView mSettingDoubleClick;
	
	private DeskSettingItemListView mPinchOutGesture; //双指打开
	private DeskSettingItemListView mSwipeUpGesture;  //双指上滑
	private DeskSettingItemListView mRotateCCWGesture; //逆时针
	private DeskSettingItemListView mRotateCWGesture; // 顺时针旋转

	private DialogGoShortCut mGoShortCutDialog;

	private int[] mImageId = new int[] { R.drawable.media_open_setting_none_icon,
			R.drawable.gesture_application, R.drawable.tab_add_shortcut_icon,
			R.drawable.screen_edit_go_shortcut };
	//GestureSettingInfo的HashMap
	private SparseArray<GestureSettingInfo> mGestureInfos;
	//DeskSettingItemListView HashMap
	private SparseArray<DeskSettingItemListView> mGestureItemListViewList;
	//点击的Item 如点击HOME健，上滑手势，下滑手势，双击空白处手势
	int mCurPositon = 0;
	private int mEntranceid = 8;
	private BroadcastReceiver mRefreshReceiver;
	private DeskSettingItemBaseView mCurrentItemView;
	private GestureSettingInfo mCurrentGestrureInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.desk_setting_gesture_screen);
		mEntranceid = getIntent().getIntExtra(DeskSettingAdvancedPayActivity.ENTRANCE_ID, 8);
		//加载view
		initView();
		//加载数据
		initData();
		load();
		registeRefreshReceiver(this);
	}

	private void initView() {
		mGestureItemListViewList = new SparseArray<DeskSettingItemListView>();
		mSettingHomeGesture = (DeskSettingItemListView) this
				.findViewById(R.id.home_gesture_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_HOME_ID, mSettingHomeGesture);
		mSettingHomeGesture.setOnValueChangeListener(this);
		mSettingHomeGesture.setOnClickListener(this);

		mSettingUpGesture = (DeskSettingItemListView) this.findViewById(R.id.up_gesture_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_UP_ID, mSettingUpGesture);
		mSettingUpGesture.setOnValueChangeListener(this);
		mSettingUpGesture.setOnClickListener(this);

		mSettingDownGesture = (DeskSettingItemListView) this
				.findViewById(R.id.down_gesture_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_DOWN_ID, mSettingDownGesture);
		mSettingDownGesture.setOnValueChangeListener(this);
		mSettingDownGesture.setOnClickListener(this);

		mSettingDoubleClick = (DeskSettingItemListView) this
				.findViewById(R.id.double_click_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID, mSettingDoubleClick);
		mSettingDoubleClick.setOnValueChangeListener(this);
		mSettingDoubleClick.setOnClickListener(this);
		
		mPinchOutGesture = (DeskSettingItemListView) this
				.findViewById(R.id.pinchout_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_PINCHOUT_ID, mPinchOutGesture);
		mPinchOutGesture.setOnValueChangeListener(this);
		mPinchOutGesture.setOnClickListener(this);
		
		mSwipeUpGesture = (DeskSettingItemListView) this
				.findViewById(R.id.swipeUp_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_SWIPEUP_ID, mSwipeUpGesture);
		mSwipeUpGesture.setOnValueChangeListener(this);
		mSwipeUpGesture.setOnClickListener(this);
		
		mRotateCCWGesture = (DeskSettingItemListView) this
				.findViewById(R.id.rotate_ccw_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_ROTATECCW_ID, mRotateCCWGesture);
		mRotateCCWGesture.setOnValueChangeListener(this);
		mRotateCCWGesture.setOnClickListener(this);
		
		mRotateCWGesture = (DeskSettingItemListView) this
				.findViewById(R.id.rotate_cw_setting);
		mGestureItemListViewList.put(GestureSettingInfo.GESTURE_ROTATECW_ID, mRotateCWGesture);
		mRotateCWGesture.setOnValueChangeListener(this);
		mRotateCWGesture.setOnClickListener(this);
		
		int state = FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS);
		switch (state) {
			//判断是否韩国版，韩国版就隐藏
			case FunctionPurchaseManager.STATE_GONE :
				mPinchOutGesture.setVisibility(View.GONE);
				mSwipeUpGesture.setVisibility(View.GONE);
				mRotateCCWGesture.setVisibility(View.GONE);
				mRotateCWGesture.setVisibility(View.GONE);
				break;
				
			//已付费，去掉prime标签	
			case FunctionPurchaseManager.STATE_CAN_USE :
				mPinchOutGesture.setImagePrimeVisibile(View.GONE);
				mSwipeUpGesture.setImagePrimeVisibile(View.GONE);
				mRotateCCWGesture.setImagePrimeVisibile(View.GONE);
				mRotateCWGesture.setImagePrimeVisibile(View.GONE);
				break;
			
			//未付费，需要显示prime标签
			case FunctionPurchaseManager.STATE_VISABLE :
				mPinchOutGesture.setImagePrimeVisibile(View.VISIBLE);
				mSwipeUpGesture.setImagePrimeVisibile(View.VISIBLE);
				mRotateCCWGesture.setImagePrimeVisibile(View.VISIBLE);
				mRotateCWGesture.setImagePrimeVisibile(View.VISIBLE);
				break;

			default :
				break;
		}
	}

	private void initData() {
		mGestureInfos = new SparseArray<GestureSettingInfo>();
		for (int i = 0; i < GestureSettingInfo.MAX_GESTURE_COUNT; i++) {
			mGestureInfos
					.put(GestureSettingInfo.GESTURE_HOME_ID + i,
							SettingProxy.getGestureSettingInfo(GestureSettingInfo.GESTURE_HOME_ID + i));
		}
		/*mGestureInfos.put(GestureSettingInfo.GESTURE_HOME_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_HOME_ID));
		mGestureInfos.put(GestureSettingInfo.GESTURE_UP_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_UP_ID));
		mGestureInfos.put(GestureSettingInfo.GESTURE_DOWN_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_DOWN_ID));
		mGestureInfos.put(GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID));
		mGestureInfos.put(GestureSettingInfo.GESTURE_PINCHOUT_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_PINCHOUT_ID));
		mGestureInfos.put(GestureSettingInfo.GESTURE_SWIPEUP_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_SWIPEUP_ID));
		mGestureInfos.put(GestureSettingInfo.GESTURE_ROTATECCW_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_ROTATECCW_ID));
		mGestureInfos.put(GestureSettingInfo.GESTURE_ROTATECW_ID,
				mControler.getGestureSettingInfo(GestureSettingInfo.GESTURE_ROTATECW_ID));*/
	}

	@Override
	public void load() {
		super.load();
		if (null != mGestureInfos) {
			int sz = mGestureInfos.size();
			for (int i = 0; i < sz; i++) {
				GestureSettingInfo info = mGestureInfos.get(i + 1);
				if (null == info) {
					continue;
				}
				DeskSettingItemListView deskSettingItemBaseView;
				if (info.mGestureId == GestureSettingInfo.GESTURE_HOME_ID) // HOME
				{
					deskSettingItemBaseView = mSettingHomeGesture;
				} else if (info.mGestureId == GestureSettingInfo.GESTURE_UP_ID) // UP
				{
					deskSettingItemBaseView = mSettingUpGesture;
				} else if (info.mGestureId == GestureSettingInfo.GESTURE_DOUBLLE_CLICK_ID) { // DOUBLE　CLICK
					deskSettingItemBaseView = mSettingDoubleClick;
				} else if (info.mGestureId == GestureSettingInfo.GESTURE_DOWN_ID) {
					deskSettingItemBaseView = mSettingDownGesture;
				} else if (info.mGestureId == GestureSettingInfo.GESTURE_PINCHOUT_ID) {
					deskSettingItemBaseView = mPinchOutGesture;
				} else if (info.mGestureId == GestureSettingInfo.GESTURE_SWIPEUP_ID) {
					deskSettingItemBaseView = mSwipeUpGesture;
				} else if (info.mGestureId == GestureSettingInfo.GESTURE_ROTATECCW_ID) {
					deskSettingItemBaseView = mRotateCCWGesture;
				} else {
					deskSettingItemBaseView = mRotateCWGesture;
				}
				String gestureSeleteValueString = getGestureSelect(info.mGestureAction);
				deskSettingItemBaseView.getDeskSettingInfo().getmGestureInfo().setImageId(mImageId);
				deskSettingItemBaseView.getDeskSettingInfo().getmGestureInfo()
						.setSelectValue(gestureSeleteValueString);
				String gestureString = getGestureString(info.mGestureAction);
				if ((info.mGestureAction == GlobalSetConfig.GESTURE_SELECT_SHORTCUT)
						|| (info.mGestureAction == GlobalSetConfig.GESTURE_SELECT_APP)) {
					String title = info.mGestrueName;
					// 兼容性(上个版本没有保存名称)
					if (null == title) {
						// 查出对应应用的名称
						final ArrayList<AppItemInfo> apps = AppDataEngine.getInstance(ApplicationProxy.getContext())
								.getAllCompletedAppItemInfos();
						final int appSize = apps.size();
						for (int j = 0; j < appSize; j++) {
							AppItemInfo itemInfo = apps.get(j);
							if (itemInfo != null && itemInfo.mIntent != null
									&& info.mAction != null
									&& info.mAction.equals(itemInfo.mIntent.toURI())) {
								title = itemInfo.mTitle;
								break;
							}
						}
					}
					if (title != null) {
						deskSettingItemBaseView.setSummaryText(gestureString + "->" + title);
					} else {
						deskSettingItemBaseView.setSummaryText(gestureString + "->"
								+ getString(R.string.notselectapp));
					}
				} else if (info.mGestureAction == GlobalSetConfig.GESTURE_GOSHORTCUT) {
					final String[] shortValues = getResources().getStringArray(
							R.array.gesture_goshortcut_value);
					final String[] shortCuts = getResources().getStringArray(
							R.array.gesture_goshortcut);
					for (int k = 0; k < shortValues.length; k++) {
						if (Integer.parseInt(shortValues[k]) == info.mGoShortCut) {
							deskSettingItemBaseView.setSummaryText(gestureString + "->"
									+ shortCuts[k]);
							break;
						}
					}
				} else if (info.mGestureAction == GlobalSetConfig.GESTURE_DISABLE) {
					deskSettingItemBaseView.setSummaryText(gestureString);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		boolean primeflag = false;
		
		//保存点击的view的下标
		if (v instanceof DeskSettingItemListView) {
			mCurPositon = mGestureItemListViewList.keyAt(mGestureItemListViewList
					.indexOfValue((DeskSettingItemListView) v));

			if (mCurPositon >= GestureSettingInfo.GESTURE_PINCHOUT_ID) {
				primeflag = true;
				((DeskSettingItemListView) v).setImageNewVisibile(View.GONE);
			}

			//未付费且是200渠道
			if (primeflag
					&& FunctionPurchaseManager.getInstance(getApplicationContext()).getPayFunctionState(
							FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS) == FunctionPurchaseManager.STATE_VISABLE) {
				if (GoAppUtils.isAppExist(this, LauncherEnv.Plugin.PRIME_GETJAR_KEY)) {
					FunctionPurchaseManager.getInstance(getApplicationContext()).startItemPayPage(
							mEntranceid + "");
				} else {
					DeskSettingUtils.showPayDialog(this, mCurPositon + 301
							- GestureSettingInfo.GESTURE_PINCHOUT_ID); // 显示付费对话框
				}
				return;
			}
			GestureSettingInfo info = mGestureInfos.get(mCurPositon);
			String gestureSeleteValueString = getGestureSelect(info.mGestureAction);
			((DeskSettingItemListView) v).getDeskSettingInfo().getmGestureInfo()
					.setSelectValue(gestureSeleteValueString);
			((DeskSettingItemListView) v).onClick(v);

		}
		
		
		/*switch (v.getId()) {
			case R.id.home_gesture_setting :
				mCurPositon = 1;
				break;

			case R.id.up_gesture_setting :
				mCurPositon = 2;
				break;

			case R.id.down_gesture_setting :
				mCurPositon = 3;
				break;

			case R.id.double_click_setting :
				mCurPositon = 4;
				break;
				
			case R.id.pinchout_setting :
				primeflag = true;
				mCurPositon = 5;
				break;
			case R.id.swipeUp_setting :
				primeflag = true;
				mCurPositon = 6;
				break;
			case R.id.rotate_ccw_setting :
				primeflag = true;
				mCurPositon = 7;
				break;
			case R.id.rotate_cw_setting :
				primeflag = true;
				mCurPositon = 8;
				break;

			default :
				break;
		}*/
		
	}

	@Override
	public boolean onValueChange(DeskSettingItemBaseView view, Object value) {
		if (view instanceof DeskSettingItemListView) {
			DeskSettingItemListView lView = (DeskSettingItemListView) view;
			lView.updateSumarryText();
			int selectValue = (Integer) value;
			GestureSettingInfo info = mGestureInfos.get(mCurPositon);

			switch (selectValue) {
				case 0 :
					//无响应
					String gestureSelect = getGestureString(GlobalSetConfig.GESTURE_DISABLE);
					String[] gestureSlectStrings = this.getResources().getStringArray(
							R.array.gesture_entries);
					lView.setSummaryText(gestureSelect);
					info.mGestureAction = GlobalSetConfig.GESTURE_DISABLE;
					info.mGestrueName = gestureSlectStrings[0];
					SettingProxy.updateGestureSettingInfo(mCurPositon, info);
					break;

				case 1 :
					//弹出应用列表
					Intent intent = new Intent(this, AppList.class);
					int requestcode = 0;
					switch (mCurPositon) {
						case 1 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_HOME_OPEN_APP;
							break;
						case 2 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_UP_GESTURE_OPEN_APP;
							break;
						case 3 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_DOWN_GESTURE_OPEN_APP;
							break;
						case 4 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_DOUBLE_CLICK_GESTURE_OPEN_APP;
							break;
						case 5 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_PINCHOUT_GESTURE_OPEN_APP;
							break;
						case 6 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_SWIPEUP_GESTURE_OPEN_APP;
							break;
						case 7 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_ROTATECCW_GESTURE_OPEN_APP;
							break;
						case 8 :
							requestcode = IRequestCodeIds.REQUEST_OPERATION_ROTATECW_GESTURE_OPEN_APP;
							break;

						default :
							break;
					}
					saveStartActivityForResult(intent, requestcode);
					break;

				case 2 :
					//快捷方式
					pickShortcut(IRequestCodeIds.REQUEST_PICK_SHORTCUT, R.string.select_app_icon);
					break;

				case 3 :
					//弹出go快捷方式列表
					mCurrentGestrureInfo = info;
					mCurrentItemView = lView;
					createGoShortCutDialog(lView, info);
					SettingProxy.updateGestureSettingInfo(mCurPositon, info);
					break;

				default :
					break;
			}
		}
		return super.onValueChange(view, value);
	}
	//创建快捷方式的dialog
	private void createGoShortCutDialog(final DeskSettingItemBaseView deskSettingItemBaseView,
			final GestureSettingInfo info) {
		mGoShortCutDialog = new DialogGoShortCut(this);
		mGoShortCutDialog.show();
		mGoShortCutDialog.setItemData(String.valueOf(info.mGoShortCut));
		mGoShortCutDialog.setPositiveButton(null, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<GoShortCutInfo> checkList = mGoShortCutDialog.getCheckList();
				if (checkList == null || checkList.size() != 1) {
					return;
				}

				GoShortCutInfo goShortCutInfo = checkList.get(0);

				String shortCutId = goShortCutInfo.getShortCutId();
				info.mGoShortCut = Integer.parseInt(shortCutId);
				//点击确认后更新gestureAction
				info.mGestureAction = GlobalSetConfig.GESTURE_GOSHORTCUT;
				String gestureSelect = getGestureString(info.mGestureAction);
				if (deskSettingItemBaseView != null) {
					final String[] shortCuts = getResources().getStringArray(
							R.array.gesture_goshortcut);

					int which = mGoShortCutDialog.getSingleChoiseCheckValue();
					if (which >= 0 && which < shortCuts.length) {
						deskSettingItemBaseView.setSummaryText(gestureSelect + "->"
								+ shortCuts[which]);
					}

				}
				//保存数据库
				SettingProxy.updateGestureSettingInfo(mCurPositon, info);
			}
		});
	}
	//通过mGestureAction来获取对应的字符
	private String getGestureString(int gestureSelect) {
		String selectString = null;
		String[] gestureSlectStrings = this.getResources().getStringArray(R.array.gesture_entries);
		switch (gestureSelect) {
			case GlobalSetConfig.GESTURE_DISABLE :
				selectString = gestureSlectStrings[0];
				break;
			case GlobalSetConfig.GESTURE_SELECT_APP :
				selectString = gestureSlectStrings[1];
				break;
			case GlobalSetConfig.GESTURE_SELECT_SHORTCUT :
				selectString = gestureSlectStrings[2];
				break;
			case GlobalSetConfig.GESTURE_GOSHORTCUT :
				selectString = gestureSlectStrings[3];
				break;
			default :
				break;
		}
		return selectString;
	}

	private String getGestureSelect(int gestureSelect) {
		String selectString = null;
		switch (gestureSelect) {
			case GlobalSetConfig.GESTURE_DISABLE :
				selectString = "0";
				break;
			case GlobalSetConfig.GESTURE_SELECT_APP :
				selectString = "1";
				break;
			case GlobalSetConfig.GESTURE_SELECT_SHORTCUT :
				selectString = "2";
				break;
			case GlobalSetConfig.GESTURE_GOSHORTCUT :
				selectString = "3";
				break;

			default :
				break;
		}
		return selectString;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case IRequestCodeIds.REQUEST_PICK_SHORTCUT : {
				if (resultCode == RESULT_OK) {
					processShortcut(data,
					/* IRequestCodeIds.REQUEST_PICK_APPLICATION, */
					IRequestCodeIds.REQUEST_CREATE_SHORTCUT);
				}
			}
				break;

			case IRequestCodeIds.REQUEST_CREATE_SHORTCUT : {
				completeAddShortcut(data);
			}
				break;

		case IRequestCodeIds.REQUEST_OPERATION_HOME_OPEN_APP:
		case IRequestCodeIds.REQUEST_OPERATION_UP_GESTURE_OPEN_APP:
		case IRequestCodeIds.REQUEST_OPERATION_DOWN_GESTURE_OPEN_APP:
		case IRequestCodeIds.REQUEST_OPERATION_DOUBLE_CLICK_GESTURE_OPEN_APP:
		case IRequestCodeIds.REQUEST_OPERATION_PINCHOUT_GESTURE_OPEN_APP:
		case IRequestCodeIds.REQUEST_OPERATION_SWIPEUP_GESTURE_OPEN_APP:
		case IRequestCodeIds.REQUEST_OPERATION_ROTATECCW_GESTURE_OPEN_APP:
		case IRequestCodeIds.REQUEST_OPERATION_ROTATECW_GESTURE_OPEN_APP: {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				Intent intent = bundle.getParcelable(AppList.INTENT_STRING);
				intent.putExtras(bundle);
				completeAddApplication(intent);
			}
		}
				break;

			default :
				break;
		}
	}

	/**
	 * 进入快捷方式选择界面
	 * 
	 * @param requestCode
	 *            请求码
	 * @param title
	 */
	public void pickShortcut(int requestCode, int title) {
		Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
		pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
		pickIntent.putExtra(Intent.EXTRA_TITLE, this.getText(title));
		// pickIntent.putExtras(bundle);
		saveStartActivityForResult(pickIntent, requestCode);
	}

	public void saveStartActivityForResult(Intent intent, int requestCode) {
		try {
			super.startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processShortcut(Intent intent, /* int requestCodeApplication, */
			int requestCodeShortcut) {
		startActivityForResult(intent, requestCodeShortcut);
	}

	private void completeAddApplication(Intent data) {
		if (data == null) {
			return;
		}

		// 获取出intent中包含的应用
		final ShortCutInfo info = infoFromApplicationIntent(this, data);
		//更新gestureaction
		GestureSettingInfo gestureSettinginfo = mGestureInfos.get(mCurPositon);
		gestureSettinginfo.mGestureAction = GlobalSetConfig.GESTURE_SELECT_APP;

		/**
		 * 图吧地图 URI_STRING:=#Intent;action=android.intent.action.MAIN....
		 * 而GO精品那些的
		 * URI_STRING:=package:com.gau.diy.gotheme#Intent;action=com.jiubang
		 * ..... 有用的部分是“#”开始的那些内容
		 */
		if (info != null) {
			// 其他的应用程序处理
			saveShortcutPreference(info);
		} else {
			/**
			 * GO精品、GO桌面主题、GO桌面小部件这三个应用的特殊处理，
			 * 因为这三个的URI比较特殊，比其他应用的URI前面多了"package:com.jiubang...."的东西,需要特殊处理
			 */
			saveSpecialPreference(data);
		}
	}

	private ShortCutInfo infoFromApplicationIntent(Context context, Intent data) {
		ComponentName component = data.getComponent();
		PackageManager packageManager = context.getPackageManager();
		ActivityInfo activityInfo = null;
		try {
			activityInfo = packageManager.getActivityInfo(component, 0); // noflags
		} catch (NameNotFoundException e) {
			Log.i(LogConstants.HEART_TAG, "Couldn't find ActivityInfo for selected application", e);
		}
		if (activityInfo != null) {
			ShortCutInfo itemInfo = new ShortCutInfo();

			itemInfo.mTitle = activityInfo.loadLabel(packageManager);
			if (itemInfo.mTitle == null) {
				itemInfo.mTitle = activityInfo.name;
			}

			itemInfo.setActivity(component, Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

			itemInfo.mIcon = IconUtilities.createIconThumbnail(activityInfo.loadIcon(packageManager),
					context);
			return itemInfo;
		}
		return null;
	}

	private void saveShortcutPreference(ShortCutInfo info) {
		if (info == null) {
			return;
		}
		if (info.mIntent == null) {
			return;
		}
		if (info.mIntent.toURI() == null || "".equals(info.mIntent.toURI())) {
			return;
		}
		String title = null;
		if (null != info.mTitle) {
			title = info.mTitle.toString();
		}
		GestureSettingInfo gestureSettinginfo = mGestureInfos.get(mCurPositon);
		gestureSettinginfo.mAction = info.mIntent.toURI();
		gestureSettinginfo.mGestrueName = title;
		String gestureString = getGestureString(gestureSettinginfo.mGestureAction);
		mGestureItemListViewList.get(mCurPositon).setSummaryText(gestureString + "->" + title);
		SettingProxy.updateGestureSettingInfo(mCurPositon, gestureSettinginfo);
	}

	// GO桌面主题、GO桌面小部件、GO精品等特殊的应用的处理
	private void saveSpecialPreference(Intent data) {
		Bundle bundle = data.getExtras();
		String appName = bundle.getString(AppList.TITLE_STRING);
		String appUri = bundle.getString(AppList.URI_STRING);
		if (appUri.contains("package:")) {
			String[] keys = appUri.split("#");
			appUri = "#" + keys[1];

			GestureSettingInfo gestureSettinginfo = mGestureInfos.get(mCurPositon);
			gestureSettinginfo.mAction = appUri;
			gestureSettinginfo.mGestrueName = appName;
			String gestureString = getGestureString(gestureSettinginfo.mGestureAction);
			mGestureItemListViewList.get(mCurPositon)
					.setSummaryText(gestureString + "->" + appName);
			SettingProxy.updateGestureSettingInfo(mCurPositon, gestureSettinginfo);
		}
	}

	private void completeAddShortcut(Intent data) {
		if (data == null) {
			return;
		}
		// 获取出intent中包含的应用
		final ShortCutInfo info = infoFromShortcutIntent(this, data);
		//更新gestureaction
		GestureSettingInfo gestureSettinginfo = mGestureInfos.get(mCurPositon);
		gestureSettinginfo.mGestureAction = GlobalSetConfig.GESTURE_SELECT_SHORTCUT;
		saveShortcutPreference(info);
	}

	private ShortCutInfo infoFromShortcutIntent(Context context, Intent data) {
		return SysAppInfo.createFromShortcut(context, data);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
//		mSettingHomeGesture.dismissDialog();
//		mSettingUpGesture.dismissDialog();
//		mSettingDownGesture.dismissDialog();
//		mSettingDoubleClick.dismissDialog();
		if (mGoShortCutDialog != null && mGoShortCutDialog.isShowing()) {
			mGoShortCutDialog.dismiss();
			createGoShortCutDialog(mCurrentItemView, mCurrentGestrureInfo);
		}
	}

	@Override
	protected void onDestroy() {
		unRegisterRefreshReceiver(this);
		mGoShortCutDialog = null;
		super.onDestroy();
		mCurrentGestrureInfo = null;
		mCurrentItemView = null;
	}
	
	private void registeRefreshReceiver(final Context context) {
		mRefreshReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context2, Intent intent) {
				if (intent != null) {
					FunctionPurchaseManager purchaseManager = FunctionPurchaseManager.getInstance(getApplicationContext());
					boolean hasPay = purchaseManager.isItemCanUse(FunctionPurchaseManager.PURCHASE_ITEM_QUICK_ACTIONS);
					if (hasPay) {
						mPinchOutGesture.setImagePrimeVisibile(View.GONE);
						mSwipeUpGesture.setImagePrimeVisibile(View.GONE);
						mRotateCCWGesture.setImagePrimeVisibile(View.GONE);
						mRotateCWGesture.setImagePrimeVisibile(View.GONE);
					}
				}

			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(DeskSettingUtils.ACTION_HAD_PAY_REFRESH);
		filter.setPriority(Integer.MAX_VALUE);
		context.registerReceiver(mRefreshReceiver, filter);
	}
	
	private void unRegisterRefreshReceiver(Context context) {
		try {
			context.unregisterReceiver(mRefreshReceiver);
		} catch (Exception e) {
			Log.e(e.toString(), e.getMessage());
		}
		
	}

	@Override
	protected void checkNewMark(DeskSettingNewMarkManager newMarkManager) {
		if (newMarkManager.isShowNew(NewMarkKeys.SCREEN_GESTURE, true)) {
			mPinchOutGesture.setImageNewVisibile(View.VISIBLE);
			mSwipeUpGesture.setImageNewVisibile(View.VISIBLE);
			mRotateCCWGesture.setImageNewVisibile(View.VISIBLE);
			mRotateCWGesture.setImageNewVisibile(View.VISIBLE);
		} else {
			mPinchOutGesture.setImageNewVisibile(View.GONE);
			mSwipeUpGesture.setImageNewVisibile(View.GONE);
			mRotateCCWGesture.setImageNewVisibile(View.GONE);
			mRotateCWGesture.setImageNewVisibile(View.GONE);
		}
	}

	@Override
	public boolean onPreValueChange(DeskSettingItemBaseView baseView,
			Object value) {
		return false;
	}
	
}
