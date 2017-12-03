package com.jiubang.ggheart.components.appmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.components.DeskTextView;
import com.jiubang.ggheart.data.info.AppItemInfo;

/**
 * 
 * <br>类描述:清理屏幕结构适配器
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2013-5-10]
 */
public class UninstallListAdapter extends BaseAdapter {
	private Activity mActivity = null;
	private Handler mHandler;
	private PackageManager mPackageManager;
	private LayoutInflater mInflater = null;
	private ArrayList<UninstallAppInfo> mDataSource = new ArrayList<UninstallAppInfo>();

	public UninstallListAdapter(Activity activity) {
		mActivity = activity;
		mPackageManager = mActivity.getPackageManager();
		mInflater = LayoutInflater.from(mActivity);
	}

	@Override
	public int getCount() {
		if (mDataSource == null) {
			return 0;
		}
		return mDataSource.size();
	}

	@Override
	public UninstallAppInfo getItem(int position) {
		try {
			if (mDataSource != null && position < mDataSource.size()) {
				return mDataSource.get(position);
			}
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		//重要：can not return int value larger than getViewTypeCount().
		return getItem(position).mType;
	}

	@Override
	public int getViewTypeCount() {
		return 7;
	}

	public void updateDataSource(ArrayList<UninstallAppInfo> dataSource) {
		mDataSource = dataSource;
		notifyDataSetChanged();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mDataSource == null || position < 0 || position >= mDataSource.size()) {
			return convertView;
		}
		
		int type = getItemViewType(position);
		switch (type) {
			case AppManagerUtils.TYPE_FARTHER_TITLE :
				convertView = getViewHolderTitle(position, convertView, parent);
				break;
				
			case AppManagerUtils.TYPE_NO_RESULT :
				convertView = getViewHolderNoResult(position, convertView, parent);
				break;

			case AppManagerUtils.TYPE_UNINSTALL_APP :
				convertView = getViewHolderIcon(position, convertView, parent);
				break;
				
			default :
				//TODO:返回加载失败页面
				break;

		}
		return convertView;
	}
	
	public View getViewHolderTitle(final int position, View convertView, ViewGroup parent) {
		ViewHolderTitle holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.clean_screen_list_item_title, null);
			holder = new ViewHolderTitle();
			holder.mTitle = (DeskTextView) convertView.findViewById(R.id.title);
			holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderTitle) convertView.getTag();
		}
		UninstallAppInfo info = mDataSource.get(position);
		holder.mTitle.setText(info.mFartherTitle);
		
		if (info.mIsShowCheckBox) {
			holder.mCheckBox.setVisibility(View.VISIBLE);
			holder.mCheckBox.setChecked(info.mIsCheck);
		} else {
			holder.mCheckBox.setVisibility(View.GONE);
		}
		return convertView;
	}

	
	public View getViewHolderIcon(final int position, View convertView, ViewGroup parent) {
		
		ViewHolderIcon holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.clean_screen_list_item_icon, null);
			holder = new ViewHolderIcon();
			holder.mIconImage = (ImageView) convertView.findViewById(R.id.icon_iamge);
			holder.mIconName = (DeskTextView) convertView.findViewById(R.id.icon_name);
			holder.memoryPss = (DeskTextView) convertView.findViewById(R.id.memory_pss);
			holder.installLocation = (DeskTextView) convertView.findViewById(R.id.install_location);
			holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderIcon) convertView.getTag();
		}
		
		UninstallAppInfo info = mDataSource.get(position);
		AppItemInfo appItemInfo = info.mAppItemInfo;
		
		if (appItemInfo != null) {
			holder.mIconImage.setImageDrawable(appItemInfo.mIcon);
			holder.mIconName.setText(appItemInfo.mTitle);
			if (appItemInfo.mPackageSize < 0) {
				holder.memoryPss.setText(0);
			} else {
				String sizeStr = Formatter.formatShortFileSize(mActivity, appItemInfo.mPackageSize);
				holder.memoryPss.setText(sizeStr);
			}
			
			if (appItemInfo.mIsInstallOnSDCard) {
				holder.installLocation.setText(R.string.app_manager_app_install_on_sdcard);
			} else {
				holder.installLocation.setText(R.string.app_manager_app_install_on_memory);
			}
		}
		
		holder.mCheckBox.setChecked(info.mIsCheck);
		return convertView;
	}
	
	public View getViewHolderNoResult(final int position, View convertView, ViewGroup parent) {
		ViewHolderNoResult holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.clean_screen_list_item_no_result, null);
			holder = new ViewHolderNoResult();
			holder.mTitle = (DeskTextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderNoResult) convertView.getTag();
		}
		UninstallAppInfo info = mDataSource.get(position);
		holder.mTitle.setText(info.mNoResultTitle);
		return convertView;
	}
	
	/**
	 * 标题
	 */
	public class ViewHolderTitle {
		public DeskTextView mTitle;
		public CheckBox mCheckBox;
	}
	
	/**
	 * 普通图标
	 */
	public class ViewHolderIcon {
		public ImageView mIconImage;
		public DeskTextView mIconName;
		public DeskTextView memoryPss;
		public DeskTextView installLocation;
		public CheckBox mCheckBox;
	}
	
	/**
	 * 没有结果
	 */
	public class ViewHolderNoResult {
		public DeskTextView mTitle;
	}
}
