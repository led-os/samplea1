package com.jiubang.ggheart.apps.desks.appfunc;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.util.log.LogConstants;
import com.jiubang.ggheart.apps.desks.Preferences.dialogs.LauncherDialogUtils;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.components.DeskButton;
import com.jiubang.ggheart.components.DeskTextView;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.components.GoProgressBar;
import com.jiubang.ggheart.components.MultiCheckViewGroup;
import com.jiubang.ggheart.components.OnMultiItemClickedListener;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.launcher.ThreadName;

/**
 * 选择锁定应用的Activity
 * 
 * @author yangguanxiang
 * 
 */
public class LockAppActivity extends Activity
		implements
			OnClickListener,
			OnMultiItemClickedListener {

	public static final int UPDATEINDICATOR = 1;
	private static final int INITFINISH = 2;

	private volatile ArrayList<AppItemInfo> mList;
	/**
	 * 选钩列表
	 */
	private ArrayList<Boolean> mBooleanList;
	private ArrayList<Boolean> mBooleanCopyList;
	private CommonControler mCommonControler;
	private Object mMutex;
	private DeskButton mHideOK, mHideCancle;
	private LinearLayout mContentLayout;
	private GoProgressBar mGoProgressBar;
	private MultiCheckViewGroup mMultiCheckViewGroup;
	private boolean mIsModify;
	private int mModifyNum;
	private DeskTextView mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.muti_choice_layout);
		mGoProgressBar = (GoProgressBar) findViewById(R.id.appfunc_hide_app_progress);
		mMultiCheckViewGroup = (MultiCheckViewGroup) findViewById(R.id.multi_check_viewgroup);
		mCommonControler = CommonControler.getInstance(getBaseContext());
		mMutex = new Object();
		mContentLayout = (LinearLayout) findViewById(R.id.contentview);
		limitDialogLayout(mContentLayout);
		mTitle = (DeskTextView) findViewById(R.id.title);
		mTitle.setText(R.string.app_fun_lock_app_title);
		mHideOK = (DeskButton) findViewById(R.id.hideok);
		mHideCancle = (DeskButton) findViewById(R.id.hidecancle);
		mHideOK.setOnClickListener(this);
		mHideCancle.setOnClickListener(this);
		mMultiCheckViewGroup.setMultiItemClickedListener(this);
		initList();
	}

	/**
	 * @param dialogLayout
	 */
	protected void limitDialogLayout(ViewGroup dialogLayout) {
		LauncherDialogUtils.setDialogWidth(dialogLayout, this);
		LauncherDialogUtils.limitHeight(dialogLayout, this);
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case INITFINISH :
					if (mList != null && mBooleanList != null) {
						mMultiCheckViewGroup.setContentList(mList, mBooleanList);
					}
					dismissProgressDialog();
					break;
				default :
					break;
			}
			// 取消加载框
			dismissProgressDialog();
		}
	};

	@Override
	protected void onDestroy() {
		synchronized (mMutex) {
			if (mList != null) {
				mList.clear();
			}
			if (mBooleanList != null) {
				mBooleanList.clear();
			}
			if (mBooleanCopyList != null) {
				mBooleanCopyList.clear();
			}
			if (mMultiCheckViewGroup != null) {
				mMultiCheckViewGroup.recyle();
			}
			if (mHideOK != null && mHideOK instanceof DeskButton) {
				((DeskButton) mHideOK).selfDestruct();
			}
			if (mHideCancle != null && mHideCancle instanceof DeskButton) {
				((DeskButton) mHideCancle).selfDestruct();
			}
			dismissProgressDialog();
			super.onDestroy();
		}
	}

	private void initList() {
		// 显示提示框
		showProgressDialog();

		new Thread(ThreadName.INIT_HIDE_APP_LIST) {
			@Override
			public void run() {
				synchronized (mMutex) {
					if (mCommonControler != null) {
						mList = (ArrayList<AppItemInfo>) mCommonControler.getAllAppListForShow();
					}
					if (mList == null) {
						mList = new ArrayList<AppItemInfo>();
					}
					if (mBooleanList == null) {
						mBooleanList = new ArrayList<Boolean>();
					}
					if (mBooleanCopyList == null) {
						mBooleanCopyList = new ArrayList<Boolean>();
					}
					if (mList != null && null != mCommonControler) {
						int size = mList.size();
						for (int i = 0; i < size; i++) {
							boolean b = mCommonControler
									.isLockedApp(((AppItemInfo) mList.get(i)).mIntent);
							mBooleanList.add(i, b);
							mBooleanCopyList.add(i, b);
						}
					}
					Message message = mHandler.obtainMessage();
					message.what = INITFINISH;
					mHandler.sendMessage(message);
				}
			}

		}.start();

	}

	private void showProgressDialog() {
		if (mGoProgressBar != null && mGoProgressBar.getVisibility() == View.INVISIBLE) {
			mGoProgressBar.setVisibility(View.VISIBLE);
		}
	}

	private void dismissProgressDialog() {
		if (mGoProgressBar != null && mGoProgressBar.getVisibility() == View.VISIBLE) {
			mGoProgressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.hideok :
				if (mModifyNum != 0 && mIsModify) {
					Boolean b = null;
					if (null != mList && null != mBooleanList && null != mCommonControler) {
						ArrayList<Intent> intentList = new ArrayList<Intent>();
						for (int i = 0; i < mBooleanList.size(); i++) {
							b = mBooleanList.get(i);
							if (b == Boolean.TRUE) {
								intentList.add(((AppItemInfo) mList.get(i)).mIntent);
							}
						}
						mCommonControler.addAppInvokeLockItems(intentList);
						mIsModify = false;
					}
					DeskToast.makeText(this, R.string.app_fun_hide_app_success_toast,
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					finish();
				}
				break;
			case R.id.hidecancle :
				finish();
				break;
			default :
				break;
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		android.view.ViewGroup.LayoutParams layoutParams = mContentLayout.getLayoutParams();
		layoutParams.height = (int) getResources().getDimension(R.dimen.folder_edit_view_height);
		layoutParams.width = (int) getResources().getDimension(R.dimen.folder_edit_view_width);
		mContentLayout.setLayoutParams(layoutParams);
		limitDialogLayout(mContentLayout);
		mMultiCheckViewGroup.setGridViewWidth(layoutParams.width);
		mMultiCheckViewGroup.onConfigurationChanged();
	}

	@Override
	public void onMultiItemClicked(int position, boolean isSelected) {
		// 点击图标的响应回调事件
		mIsModify = true;
		if (isSelected == mBooleanCopyList.get(position)) {
			mModifyNum--;
		} else {
			mModifyNum++;
		}

	}

	@Override
	public void onBackPressed() {
		try {
			super.onBackPressed();
		} catch (Exception e) {
			Log.e(LogConstants.HEART_TAG, "onBackPressed err " + e.getMessage());
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onReachMaxSeletedCount() {
		// TODO Auto-generated method stub
		
	}
}
