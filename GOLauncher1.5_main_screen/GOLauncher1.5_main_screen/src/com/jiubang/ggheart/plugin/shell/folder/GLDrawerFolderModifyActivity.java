package com.jiubang.ggheart.plugin.shell.folder;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.ConvertUtils;
import com.go.util.SortHelper;
import com.go.util.sort.CompareMethod;
import com.go.util.sort.CompareTitleMethod;
import com.go.util.sort.ITitleCompareable;
import com.golauncher.message.IAppDrawerMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.jiubang.ggheart.apps.appfunc.controler.AppDrawerControler;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.FunAppItemInfo;
import com.jiubang.ggheart.data.info.FunFolderItemInfo;
import com.jiubang.ggheart.data.info.FunItemInfo;
import com.jiubang.ggheart.launcher.ThreadName;

/**
 * 功能表文件夹新增或修改文件夹的activity
 * 
 * @author dingzijian
 * 
 */
public class GLDrawerFolderModifyActivity extends GLFolderModifyBaseActivity {

	private AppDrawerControler mDrawerControler;

	private FunFolderItemInfo mFunFolderItemInfo;
	
	protected int mAppDrawerFolderType = FunFolderItemInfo.TYPE_NORMAL;
	//顶部文件接操作栏新建文件夹用
	public static final String APPDRAWER_FOLDER_TYPE = "appdrawer_folder_type";
	
	private Intent mSelectedForCreate;
	
	private boolean mIsSelectedNotInDrawer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mDrawerControler = AppDrawerControler.getInstance(ApplicationProxy.getContext());
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void loadAppListForCreate() {
		// 显示提示框
		showProgressDialog();
		
		new Thread(ThreadName.INIT_NEW_FOLDER_APP_LIST) {
			@Override
			public void run() {
				synchronized (mMutex) {
					mContentList.clear();
					mBooleanList.clear();
					// 通过Intent组装一个Info列表
					List<FunAppItemInfo> arrayList = mDrawerControler.getFunItemInfosExceptFolder();
					// 对组装好的Info列表进行排序
					if (!arrayList.isEmpty()) {
						CompareMethod<ITitleCompareable> method = new CompareTitleMethod();
						SortHelper.doSort(arrayList, method);
					} else {
						mHandler.sendEmptyMessage(MSG_NO_ITEMS);
					}
					// // 把排序好的准备加入文件夹的程序放入选择列表
					for (FunAppItemInfo info : arrayList) {
						boolean isSelected = ConvertUtils.intentCompare(mSelectedForCreate,
								info.getIntent());
						if (isSelected) {
							mContentList.add(0, info);
							mBooleanList.add(0, isSelected);
							mCheckedNum++;
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									setFinishBtnState(true);
								}
							});
						} else {
							mContentList.add(info);
							mBooleanList.add(isSelected);
						}
					}
					if (mCheckedNum == 0 && mSelectedForCreate != null) {
						// 这种情况表示传进来的intent不在功能表，再到文件夹里面找一个
						FunItemInfo item = mDrawerControler.getFunItemInfo(mSelectedForCreate);
						mContentList.add(0, item);
						mBooleanList.add(0, true);
						mCheckedNum++;
						mIsSelectedNotInDrawer = true;
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								setFinishBtnState(true);
							}
						});
					}
					Message message = mHandler.obtainMessage(LOAD_APPS_FINISH);
					mHandler.sendMessage(message);
				}
			}
		}.start();
	}
	@Override
	protected void loadAppListForModify() {
		// 显示提示框
		showProgressDialog();

		new Thread(ThreadName.INIT_MODIFY_FOLDER_APP_LIST) {
			@Override
			public void run() {
				synchronized (mMutex) {
					mContentList.clear();
					if (mBooleanList == null) {
						mBooleanList = new ArrayList<Boolean>();
					}
					mBooleanList.clear();
					if (mFunFolderItemInfo != null) {
						ArrayList<FunAppItemInfo> appsInFolder = (ArrayList<FunAppItemInfo>) mFunFolderItemInfo
								.getFolderContentExceptHide();
						if (appsInFolder.size() > 0) {
							CompareMethod<ITitleCompareable> method = new CompareTitleMethod();
							SortHelper.doSort(appsInFolder, method);
							for (FunAppItemInfo info : appsInFolder) {
								mContentList.add(info);
								mBooleanList.add(true);
								mCheckedNum++;
							}
						}
					}
					List<FunAppItemInfo> list = mDrawerControler.getFunItemInfosExceptFolder();
					if (list.size() > 0) {
						CompareMethod<ITitleCompareable> method = new CompareTitleMethod();
						SortHelper.doSort(list, method);
						for (FunAppItemInfo info : list) {
							mContentList.add(info);
							mBooleanList.add(false);
						}
					}

					Message message = mHandler.obtainMessage();
					message.what = LOAD_APPS_FINISH;
					mHandler.sendMessage(message);
				}
			}
		}.start();
	}

	// 处理列表元素
	private void handleElmentsForCreate() {

		//		if (!mHasSubmited) {
		//			mHasSubmited = true;
		showProgressDialog();
		new Thread("create folder") {

			@Override
			public void run() {

				Looper.prepare();
				synchronized (mMutex) {
					ArrayList<FunAppItemInfo> list = new ArrayList<FunAppItemInfo>();
					ArrayList<AppItemInfo> appItemInfos = new ArrayList<AppItemInfo>();
					int size = mBooleanList.size();
					for (int i = 0; i < size; i++) {
						if (mBooleanList.get(i).booleanValue()) {
							FunAppItemInfo itemInfo = (FunAppItemInfo) mContentList.get(i);
							if (mIsSelectedNotInDrawer) {
								// 特殊处理从文件夹中选中的应用，代码上是这么难看的的了，没办法了
								boolean isSelected = ConvertUtils.intentCompare(mSelectedForCreate,
										itemInfo.getIntent());
								if (isSelected) {
									mFolderController.removeAppFromDrawerFolder(
											itemInfo.getInWhitchFolder(), mSelectedForCreate, true,
											GLAppFolderController.REMOVE_FOLDER_LESS_ONE);
									mIsSelectedNotInDrawer = false;
								}
							}
							list.add(itemInfo);
							appItemInfos.add(itemInfo.getAppItemInfo());
						}
					}
					boolean needName = mNameText.getText().equals(getString(R.string.folder_name))
							? true
							: false;
					if (needName) {
						mFolderName = CommonControler
								.getInstance(GLDrawerFolderModifyActivity.this).generateFolderName(
										appItemInfos);
					}
					mFolderController.createAppDrawerFolder(list, mFolderName);
					// 通过功能表顶部工具条生产的文件夹，通知消失图标
					if (mAppDrawerFolderType != FunFolderItemInfo.TYPE_NORMAL && mAppDrawerFolderType != FunFolderItemInfo.TYPE_NEW_FOLDER) {
						MsgMgrProxy.sendHandler(null, IDiyFrameIds.APP_DRAWER, IAppDrawerMsgId.APPDRAWER_SPECIAL_FOLDER_DISMISS,
								mAppDrawerFolderType, null, null);
					}
					Message message = mHandler.obtainMessage();
					message.what = HANDLE_APPS_FINISH;
					mHandler.sendMessage(message);

				}
				Looper.myLooper().quit();
			}
		}.start();
	}

	protected void handleElmentsForModify() {
		if (mFunFolderItemInfo != null) {
			showProgressDialog();
			final String name = mNameText.getText().toString();
			new Thread("move icon to folder") {

				@Override
				public void run() {

					Looper.prepare();
					synchronized (mMutex) {
						ArrayList<FunAppItemInfo> list = new ArrayList<FunAppItemInfo>();
						ArrayList<AppItemInfo> appItemInfos = new ArrayList<AppItemInfo>();
						for (int i = 0; i < mBooleanList.size(); i++) {

								FunAppItemInfo itemInfo = (FunAppItemInfo) mContentList.get(i);
								list.add(itemInfo);
								if (mBooleanList.get(i).booleanValue()) {
									appItemInfos.add(itemInfo.getAppItemInfo());
								}
						}
						boolean needName = getString(R.string.folder_name).equals(name)
								? true
								: false;
						if (needName) {
							mFolderName = CommonControler.getInstance(
									GLDrawerFolderModifyActivity.this).generateFolderName(
									appItemInfos);
						}
						//						//TODO 修改文件夹内容
						mFolderController
								.modifyDrawerFolder(list, mBooleanList, mFunFolderItemInfo);
						mDrawerControler.refreshFolderBarTarget(mFunFolderItemInfo);
						Message message = mHandler.obtainMessage();
						message.what = HANDLE_APPS_FINISH;
						if (!mBooleanList.contains(true)) {
							message.arg1 = MSG_FOLDER_EMPTY;
						}
						mHandler.sendMessage(message);
					}
					Looper.myLooper().quit();
				}
			}.start();

		}
		//	}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.finish_btn : {
				switch (mFolderAction) {
					case CREATE_FOLDER :
						handleElmentsForCreate();
						break;
					case MODITY_FOLDER :
						handleElmentsForModify();
						break;
					default :
						break;
				}
			}
				break;
			default :
				break;
		}
	}

	@Override
	protected void initData() {
		switch (mFolderAction) {
			case CREATE_FOLDER : {
				Bundle bundle = getIntent().getExtras();
				if (bundle != null) {
					mFolderName = bundle.getString(FOLDER_NAME);
					mAppDrawerFolderType = bundle.getInt(APPDRAWER_FOLDER_TYPE);
					mSelectedForCreate = bundle.getParcelable(FOLDER_SELECTED_INTENT_FOR_CREATE);
				}
				if (mFolderName == null || "".equals(mFolderName)) {
					mFolderName = getString(R.string.folder_name);
				}
				loadAppListForCreate();
				break;
			}
			case MODITY_FOLDER : {
				GLAppFolderInfo folderInfo = mFolderController.getFolderInfoById(mFolderID,
						GLAppFolderInfo.FOLDER_FROM_APPDRAWER);
				if (folderInfo == null) {
					finish();
				} else {
					mFunFolderItemInfo = folderInfo.getAppDrawerFolderInfo();
					mFolderName = mFunFolderItemInfo.getTitle();
					mAppDrawerFolderType = mFunFolderItemInfo.getFolderActionBarType();
					if (mFolderName == null) {
						mFolderName = getString(R.string.folder_name);
					}
					loadAppListForModify();
				}
				break;
			}
			default :
				break;
		}
	}
	@Override
	public void onReachMaxSeletedCount() {
		// TODO Auto-generated method stub
		
	}
}
