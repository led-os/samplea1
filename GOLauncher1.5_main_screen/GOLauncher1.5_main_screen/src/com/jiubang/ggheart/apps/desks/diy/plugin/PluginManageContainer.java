package com.jiubang.ggheart.apps.desks.diy.plugin;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.gau.go.launcherex.R;
import com.jiubang.core.framework.ICleanable;
import com.jiubang.ggheart.apps.desks.diy.plugin.PluginItemViewAdapter.OnPluginClosedListener;
import com.jiubang.ggheart.components.DeskTextView;

/**
 * 插件管理界面容器
 * @author liulixia
 *
 */
public class PluginManageContainer extends RelativeLayout 
				implements ICleanable {
	private Context mContext;
	private GridView mGridView = null;
	private PluginItemViewAdapter mAdapter = null;
	private PluginCnItemViewAdapter mCnAdapter = null;
	private RelativeLayout mNoMsgView = null;
	private DeskTextView mNoMsgTextView = null;
	private SpaceCalculator mSpaceCalculator = null;
	private int mType = 0; //用于区分插件还是小部件容器
	
	public PluginManageContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mSpaceCalculator = SpaceCalculator.getInstance(context);
	}

	public PluginManageContainer(Context context) {
		super(context);
		mContext = context;
	}

	public void setType(int type) {
		mType = type;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		if (mAdapter != null) {
			mAdapter.clear();
			mAdapter = null;
		}
		if (mCnAdapter != null) {
			mCnAdapter.clear();
			mCnAdapter = null;
		}
		mGridView.setAdapter(null);
	}
	
	protected void init() {
		mGridView = (GridView) findViewById(R.id.download_gowidget_gridView);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mNoMsgView = (RelativeLayout) findViewById(R.id.empty_msg);
		mNoMsgTextView = (DeskTextView) findViewById(R.id.nomsgtextview);
		setGridViewProperties();
	}
	
	private void setGridViewProperties() {
		mGridView.setColumnWidth(mSpaceCalculator.getItemWidth());
		if (SpaceCalculator.sPortrait) { //竖屏显示两个
			mGridView.setNumColumns(2);
		} else { //横屏三个
			mGridView.setNumColumns(3);
		}
	}
	
	public void changeOrientation(boolean isCnUser) {
		setGridViewProperties();
		if (!isCnUser) {
			mAdapter.dismissMenu();
		} else {
			mCnAdapter.dismissMenu();
		}
	}
	
	public void setData(boolean isCnUser, ArrayList<GoPluginOrWidgetInfo> datas, OnPluginClosedListener listener) {
		if (!isCnUser) {
			if (mAdapter == null) {
				mAdapter = new PluginItemViewAdapter(mContext);
			}
			mGridView.setAdapter(mAdapter);
			mAdapter.setData(datas, listener);
			mAdapter.notifyDataSetChanged();
		} else {
			if (mCnAdapter == null) {
				mCnAdapter = new PluginCnItemViewAdapter(mContext);
			}
			mGridView.setAdapter(mCnAdapter);
			mCnAdapter.setData(datas, listener);
			mCnAdapter.notifyDataSetChanged();
		}
	}
	
	public void updateData(boolean isCnUser) {
		if (!isCnUser) {
			mAdapter.notifyDataSetChanged();
		} else {
			mCnAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 是否有已安装的plugin或widget
	 * @param hasItem
	 */
	public void hasPluginsOrWidgets(boolean hasItem) {
		if (hasItem) { //本地有数据
			mGridView.setVisibility(View.VISIBLE);
			mNoMsgView.setVisibility(View.GONE);
		} else { //本地无数据
			mGridView.setVisibility(View.GONE);
			mNoMsgView.setVisibility(View.VISIBLE);
			if (mType == PluginManageView.GO_PLUGIN_VIEW_ID) {
				mNoMsgTextView.setText(R.string.plugin_manage_plugin_install_empty);
			} else {
				mNoMsgTextView.setText(R.string.plugin_manage_widget_install_empty);
			}
		}
	}
	
	/**
	 * 是否已安装完全部的plugins或widgets
	 * @param installedAll
	 */
	public void hasAllPluginsOrWidgets(boolean hasAll) {
		if (hasAll) { //已安装完全部plugins
			mGridView.setVisibility(View.GONE);
			mNoMsgView.setVisibility(View.VISIBLE);
			if (mType == PluginManageView.GO_PLUGIN_VIEW_ID) {
				mNoMsgTextView.setText(R.string.plugin_manage_plugin_feature_empty);
			} else {
				mNoMsgTextView.setText(R.string.plugin_manage_widget_feature_empty);
			}
		} else { 
			mGridView.setVisibility(View.VISIBLE);
			mNoMsgView.setVisibility(View.GONE);
		}
	}
}
