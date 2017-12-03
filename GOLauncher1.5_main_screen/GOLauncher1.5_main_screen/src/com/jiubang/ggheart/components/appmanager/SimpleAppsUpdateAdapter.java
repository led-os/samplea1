package com.jiubang.ggheart.components.appmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.gau.go.launcherex.R;
import com.jiubang.ggheart.appgame.appcenter.contorler.ApplicationManager;
import com.jiubang.ggheart.apps.gowidget.gostore.net.databean.AppsBean.AppBean;

/**
 * 应用更新数据适配器
 */
public class SimpleAppsUpdateAdapter extends BaseAdapter {

	private List<AppBean> mAppBeanList = null;
	private LayoutInflater mLayoutInflater = null;
	private Context mContext = null;
	/**
	 * 默认图标
	 */
	private Bitmap mDefaultBitmap = null;

	public SimpleAppsUpdateAdapter(Context context,
			ArrayList<AppBean> appBeanArrayList) {
		mContext = context;
		mAppBeanList = appBeanArrayList;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mAppBeanList == null ? 0 : mAppBeanList.size();
	}
	
	@Override
	public Object getItem(int position) {
		Object object = null;
		if (mAppBeanList != null && position < mAppBeanList.size()) {
			object = mAppBeanList.get(position);
		}
		return object;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (mAppBeanList != null && position < mAppBeanList.size()) {			
			convertView = getContentView(position, convertView, parent);
		}
		return convertView;
	}

	private View getContentView(int position, View convertView, ViewGroup parent) {
		SimpleAppsUpdateItem appInfoListItem = null;
		
		if (convertView == null) {
			appInfoListItem = (SimpleAppsUpdateItem) mLayoutInflater
					.inflate(R.layout.app_manager_upate_list_item, null);
		} else if (convertView instanceof SimpleAppsUpdateItem) {
			appInfoListItem = (SimpleAppsUpdateItem) convertView;
			appInfoListItem.resetDefaultStatus();
		}
		
		if (appInfoListItem != null) {
			final AppBean appBean = mAppBeanList.get(position);
			appInfoListItem.bindAppBean(mContext, position, appBean, mDefaultBitmap);
		}

		return appInfoListItem;
	}

	/**
	 * 设置数据源
	 * @param appBeanArrayList
	 */
	public void setDataSet(List<AppBean> appBeanArrayList) {
		mAppBeanList = appBeanArrayList;
		if (mAppBeanList != null && !mAppBeanList.isEmpty()) {
			ApplicationManager applicationManager = ApplicationManager.getInstance(mContext);

			for (AppBean appBean : mAppBeanList) {
				if (!applicationManager.checkIfVersionSyn(appBean)) {
					//如果不存，同步下downtask和appbean的状态
					applicationManager.checkDownloadStatus(mContext, appBean);
				}
			}
		}
	
		//通知数据发生变化
		notifyDataSetChanged();
	}
	
	//通知数据变化，刷新界面
	public void notifyDataSetChanged(ListView parent, AppBean appBean) {
		if (mAppBeanList != null && !mAppBeanList.isEmpty()) {
			int position = mAppBeanList.indexOf(appBean);
			if (position > -1) {
				int firstVisiblePos = parent.getFirstVisiblePosition();
				SimpleAppsUpdateItem item = (SimpleAppsUpdateItem) parent.getChildAt(position - firstVisiblePos);
				if (item != null) {
					//找出相对应的Item项 更新状态
					item.setStatus(appBean);
				}
			}
		}
	}

	public List<AppBean> getAppBeanList() {
		return mAppBeanList;
	}

	/**
	 * 设置列表展现的默认图标
	 */
	public void setDefaultIcon(Drawable drawable) {
		if (drawable != null && drawable instanceof BitmapDrawable) {
			mDefaultBitmap = ((BitmapDrawable) drawable).getBitmap();
		}
	}

}