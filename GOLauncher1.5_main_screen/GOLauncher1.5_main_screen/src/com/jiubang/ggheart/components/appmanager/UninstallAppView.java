package com.jiubang.ggheart.components.appmanager;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;

/**
 * 
 * <br>类描述:清理屏幕
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-8]
 */
public class UninstallAppView extends LinearLayout implements OnClickListener {
	
	public Activity mActivity;
	public RelativeLayout mScanResultLayout; //扫描结果布局
	public Button mBtnClear; //一键清理按钮

	public ListView mScanListView; //listview
	public UninstallListAdapter mAdapter;

	ArrayList<UninstallAppInfo> mSacnResultList = new ArrayList<UninstallAppInfo>(); //数据源
	ArrayList<UninstallAppInfo> mUsedList = new ArrayList<UninstallAppInfo>();
	ArrayList<UninstallAppInfo> mNoUsedList = new ArrayList<UninstallAppInfo>();

	public UninstallAppView(Activity activity) {
		super(activity);
		mActivity = activity;
		LayoutInflater inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.uninstall_app_view, this);

		initView();
		initListView();
		sacnAppList(activity);
	}

	public void initView() {
		mScanResultLayout = (RelativeLayout) findViewById(R.id.sacan_result_layout);
		mBtnClear = (Button) findViewById(R.id.btn_one_key_clernt);
		mBtnClear.setOnClickListener(this);
	}

	public void initListView() {
		mScanListView = (ListView) findViewById(R.id.result_listview);
		mAdapter = new UninstallListAdapter(mActivity);
		mScanListView.setAdapter(mAdapter);
		mAdapter.updateDataSource(mSacnResultList);
		mScanListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if (mSacnResultList != null && position < mSacnResultList.size()) {
					UninstallAppInfo selectInfo = mSacnResultList.get(position);
					selectInfo.mIsCheck = !selectInfo.mIsCheck;
					//点击标题，设置是否全选
					if (selectInfo.mType == AppManagerUtils.TYPE_FARTHER_TITLE) {
						//						for (UninstallAppInfo childInfo : mSacnResultList) {
						//							if (childInfo.mParentId == selectInfo.mId) {
						//								childInfo.mIsCheck = selectInfo.mIsCheck;
						//							}
						//						}
					} else {
						//点击子项，设置标题checkbox是否勾上
						int selectInfoParentId = selectInfo.mParentId;
						boolean flag = true;
						for (UninstallAppInfo info : mSacnResultList) {
							if (info.mParentId == selectInfoParentId) {
								if (!info.mIsCheck) {
									flag = false;
									break;
								}
							}
						}
						setTitleCheckBoxState(selectInfoParentId, flag);
					}
					mAdapter.notifyDataSetChanged();
				}
				setCleanBtnState(); //设置清除按钮的状态和内容
			}
		});
	}

	/**
	 * <br>功能简述:设置标题项的check数据状态
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param id
	 * @param flag
	 */
	public void setTitleCheckBoxState(int id, boolean flag) {
		for (UninstallAppInfo info : mSacnResultList) {
			if (info.mId == id) {
				info.mIsCheck = flag;
				break;
			}
		}
	}

	/**
	 * <br>功能简述:设置清除按钮的状态和内容
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void setCleanBtnState() {
		int slectSize = 0;
		//检查选择了几个选项
		for (UninstallAppInfo info : mSacnResultList) {
			if (info.mType != AppManagerUtils.TYPE_FARTHER_TITLE
					&& info.mType != AppManagerUtils.TYPE_NO_RESULT) {
				if (info.mIsCheck) {
					slectSize = slectSize + 1;
				}
			}
		}
		if (slectSize == 0) {
			mBtnClear.setText(R.string.uninstalltext);
			mBtnClear.setEnabled(false);
		} else {
			mBtnClear.setText(mActivity.getString(R.string.uninstalltext) + "(" + slectSize + ")");
			mBtnClear.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		//一键清理按钮
		if (v == mBtnClear) {
			delSelectIcon();
			GuiThemeStatistics
					.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.APP_MANAGER_03);
		}
	}

	/**
	 * <br>功能简述:扫描桌面
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	@SuppressWarnings("unchecked")
	public void sacnAppList(Activity activity) {
		ArrayList<ArrayList<UninstallAppInfo>> list = new ArrayList<ArrayList<UninstallAppInfo>>();
		mSacnResultList.clear();
		mNoUsedList.clear();
		mUsedList.clear();
		list.add(mNoUsedList);
		list.add(mUsedList);
		AppManagerUtils.getWeekOpenAppList(mActivity, activity, list);

		Object[] noUserListSort = null;
		Object[] userListSort = null;
		try {
			noUserListSort = mNoUsedList.toArray();
			Arrays.sort(noUserListSort); //排序
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			userListSort = mUsedList.toArray();
			Arrays.sort(userListSort); //排序
		} catch (Exception e) {
			e.printStackTrace();
		}

		//较小使用
		setSacnResultList(1000, R.string.clear_screen_uninstall_no_used,
				R.string.clear_screen_uninstall_no_used_nodata, noUserListSort);

		//最近使用
		setSacnResultList(2000, R.string.clear_screen_uninstall_used,
				R.string.clear_screen_uninstall_used_nodata, userListSort);

		//刷新列表
		mAdapter.notifyDataSetChanged();
		setCleanBtnState(); //设置清除按钮的状态和内容
	}

	/**
	 * <br>功能简述:设置每个分类的数据
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param parentId
	 * @param titleResId
	 * @param noResultResId
	 * @param addList
	 */
	public void setSacnResultList(int parentId, int titleResId, int noResultResId, Object[] addList) {
		//添加标题栏
		UninstallAppInfo titleInfo = new UninstallAppInfo();
		titleInfo.mId = parentId; //设置标题id
		titleInfo.mType = AppManagerUtils.TYPE_FARTHER_TITLE;
		titleInfo.mFartherTitle = mActivity.getString(titleResId, 0); //设置标题
		titleInfo.mIsShowCheckBox = false; //设置显示checkbox,默认不显示
		mSacnResultList.add(titleInfo);

		//添加子项
		if (addList != null && addList.length > 0) {
			int size = addList.length;
			for (int i = 0; i < size; i++) {
				UninstallAppInfo itemInfo = (UninstallAppInfo) addList[i];
				itemInfo.mType = AppManagerUtils.TYPE_UNINSTALL_APP;
				itemInfo.mParentId = parentId; //设置父id
				mSacnResultList.add(itemInfo);
			}

			titleInfo.mFartherTitle = mActivity.getString(titleResId, String.valueOf(size)); //设置标题
		} else {
			//沒有数据时显示的内容
			UninstallAppInfo noResult = new UninstallAppInfo();
			noResult.mType = AppManagerUtils.TYPE_NO_RESULT;
			noResult.mNoResultTitle = noResultResId; //标题
			mSacnResultList.add(noResult);
		}
	}

	/**
	 * <br>功能简述:删除选择的图标
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public void delSelectIcon() {
		ArrayList<UninstallAppInfo> delAppList = new ArrayList<UninstallAppInfo>();
		for (UninstallAppInfo uninstallAppInfo : mSacnResultList) {
			//去掉标题和没有内容显示部分
			if (uninstallAppInfo.mType != AppManagerUtils.TYPE_NO_RESULT
					&& uninstallAppInfo.mType != AppManagerUtils.TYPE_FARTHER_TITLE
					&& uninstallAppInfo.mIsCheck) {
				delAppList.add(uninstallAppInfo);
			}
		}

		for (UninstallAppInfo uninstallAppInfo : delAppList) {
			AppItemInfo appItemInfo = uninstallAppInfo.mAppItemInfo;
			String packageName = appItemInfo.mIntent.getComponent().getPackageName();
			AppManagerUtils.uninstallAPK(packageName, mActivity);
		}
	}

	public void onDestroy() {
		if (mSacnResultList != null) {
			mSacnResultList.clear();
			mSacnResultList = null;
		}

		if (mNoUsedList != null) {
			mNoUsedList.clear();
			mNoUsedList = null;
		}

		if (mUsedList != null) {
			mUsedList.clear();
			mUsedList = null;
		}
	}

	public void dispatchInstallEvent(Context context, Intent intent) {
		final String action = intent.getAction();
		if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
				|| Intent.ACTION_PACKAGE_REMOVED.equals(action)
				|| Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			
			sacnAppList(mActivity);
		}
	}
}
