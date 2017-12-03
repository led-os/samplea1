package com.jiubang.ggheart.plugin.shell.folder;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.view.View;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.ConvertUtils;
import com.go.util.SortHelper;
import com.go.util.sort.CompareMethod;
import com.go.util.sort.CompareTitleMethod;
import com.go.util.sort.ITitleCompareable;
import com.jiubang.ggheart.common.controler.CommonControler;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.info.AppItemInfo;
import com.jiubang.ggheart.data.info.IItemType;
import com.jiubang.ggheart.data.info.ItemInfo;
import com.jiubang.ggheart.data.info.SelfAppItemInfo;
import com.jiubang.ggheart.data.info.ShortCutInfo;
import com.jiubang.ggheart.data.info.UserFolderInfo;
import com.jiubang.ggheart.launcher.ThreadName;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  dingzijian
 * @date  [2013-3-21]
 */
public class GLScreenFolderModifyActivity extends GLFolderModifyBaseActivity {

	protected UserFolderInfo mUserFolderInfo;

	@Override
	protected void initData() {
		mUserFolderInfo = mFolderController.getFolderInfoById(mFolderID,
				GLAppFolderInfo.FOLDER_FROM_SCREEN).getScreenFoIderInfo();
		mFolderName = mUserFolderInfo.mTitle.toString();
		if (mFolderName == null) {
			mFolderName = getString(R.string.folder_name);
		}
		loadAppListForModify();
	}

	@Override
	protected void loadAppListForCreate() {

	}

	@Override
	protected void loadAppListForModify() {
		// 显示提示框
		showProgressDialog();

		new Thread(ThreadName.INIT_MODIFY_FOLDER_APP_LIST) {
			@Override
			public void run() {
				synchronized (mMutex) {
					// 先清空
					mContentList.clear();
					mBooleanList.clear();
					// 获取文件夹的资源列表
					ArrayList<ItemInfo> folderContentList = mUserFolderInfo.getContents();
					// 添加已存在的元素
					if (folderContentList != null && !folderContentList.isEmpty()) {
						for (ItemInfo info : folderContentList) {
							ShortCutInfo cutInfo = (ShortCutInfo) info;
							// 列表元素个数递增
							AppItemInfo appItemInfo = cutInfo.getRelativeItemInfo();
							if (appItemInfo != null) {
								if (appItemInfo instanceof SelfAppItemInfo) {
									appItemInfo = new SelfAppItemInfo();
									appItemInfo.mTitle = (String) cutInfo.getTitle();
									appItemInfo.mIcon = (BitmapDrawable) cutInfo.mIcon;
									appItemInfo.mIntent = cutInfo.mIntent;
								}
								mContentList.add(appItemInfo);
								mBooleanList.add(true);
								mCheckedNum++;
							}
						}
					}
					AppDataEngine engine = AppDataEngine.getInstance(ApplicationProxy.getContext());
					ArrayList<AppItemInfo> appList = engine.getCompletedAppItemInfosExceptHide();
					if (!appList.isEmpty()) {
						CompareMethod<ITitleCompareable> method = new CompareTitleMethod();
						SortHelper.doSort(appList, method);
						for (AppItemInfo info : appList) {
							if (info.mIntent != null && info.mIntent.getComponent() != null) {
								if (!isExist(info.mIntent)) {
									mContentList.add(info);
									mBooleanList.add(false);
								}
							}
						}
					}
					Message message = mHandler.obtainMessage();
					message.what = LOAD_APPS_FINISH;
					mHandler.sendMessage(message);
				}
			}
		}.start();
	}

	@Override
	protected void handleElmentsForModify() {
		final String name = (String) mNameText.getText();
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (mMutex) {
					ArrayList<AppItemInfo> appItemInfos = new ArrayList<AppItemInfo>();
					ArrayList<ItemInfo> addList = new ArrayList<ItemInfo>();
					ArrayList<ShortCutInfo> removeList = new ArrayList<ShortCutInfo>();
					int count = mContentList.size();
					for (int i = 0; i < count; i++) {
						AppItemInfo appItemInfo = (AppItemInfo) mContentList.get(i);
						if (mBooleanList.get(i).booleanValue()) {
							if (!isExist(appItemInfo.mIntent)) {
								appItemInfos.add(appItemInfo);
								ShortCutInfo shortCutInfo = appitemToShortCut(appItemInfo);
								addList.add(shortCutInfo);
								mUserFolderInfo.add(shortCutInfo);
							}
						} else {
							if (isExist(appItemInfo.mIntent)) {
								//							removeList.add((AppItemInfo) mContentList.get(i));
								ShortCutInfo cutInfo = mUserFolderInfo
										.removeApp(appItemInfo.mIntent);
								if (cutInfo != null) {
									removeList.add(cutInfo);
								}
							}
						}
					}
					//智能命名
					boolean needName = getString(R.string.folder_name).equals(name) ? true : false;
					if (needName) {
						mFolderName = CommonControler
								.getInstance(GLScreenFolderModifyActivity.this).generateFolderName(
										appItemInfos);
					}
					if (!addList.isEmpty()) {
						mFolderController.addAppToScreenFolderBatch(mFolderID, addList, false);
					}
					if (!removeList.isEmpty()) {
						mFolderController.removeAppFromScreenFolderBatch(mFolderID, removeList,
								false);
					}
					Message message = mHandler.obtainMessage(HANDLE_APPS_FINISH);
					if (!mBooleanList.contains(true)) {
						message.arg1 = MSG_FOLDER_EMPTY;
					}
					mHandler.sendMessage(message);

				}
			}
		}).start();
	}

	// 判断是否已经在原来的文件夹里面
	private boolean isExist(Intent intentSrc) {
		int count = mUserFolderInfo.getContents().size();
		ArrayList<ItemInfo> itemInfos = mUserFolderInfo.getContents();
		for (int i = 0; i < count; i++) {
			final Intent intentDes = ((ShortCutInfo) itemInfos.get(i)).mIntent;
			if (ConvertUtils.intentCompare(intentSrc, intentDes)) {
				return true;
			} else if (ConvertUtils.shortcutIntentCompare(intentSrc, intentDes)) {
				return true;
			} else if (ConvertUtils.selfIntentCompare(intentSrc, intentDes)) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<ItemInfo> appItemsToShortCuts(ArrayList<AppItemInfo> infos) {
		if (null == infos) {
			return null;
		}
		ArrayList<ItemInfo> rets = new ArrayList<ItemInfo>();
		int count = infos.size();
		for (int i = 0; i < count; i++) {
			AppItemInfo info = infos.get(i);
			if (null == info) {
				continue;
			}
			ShortCutInfo ret = appitemToShortCut(info);
			rets.add(ret);
		}
		return rets;
	}

	private ShortCutInfo appitemToShortCut(AppItemInfo info) {
		ShortCutInfo ret = new ShortCutInfo();
		ret.mInScreenId = System.currentTimeMillis();
		ret.mIcon = info.mIcon;
		ret.mIntent = info.mIntent;
		ret.mItemType = IItemType.ITEM_TYPE_APPLICATION;
		ret.mSpanX = 1;
		ret.mSpanY = 1;
		ret.mTitle = info.mTitle;
		ret.setRelativeItemInfo(info);
		return ret;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.finish_btn :
				handleElmentsForModify();
				break;

			default :
				break;
		}
		super.onClick(v);
	}

	@Override
	public void onReachMaxSeletedCount() {
		// TODO Auto-generated method stub
		
	}
}
