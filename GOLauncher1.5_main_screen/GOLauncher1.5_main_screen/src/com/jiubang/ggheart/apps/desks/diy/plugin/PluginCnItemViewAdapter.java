package com.jiubang.ggheart.apps.desks.diy.plugin;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;
import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.util.AppUtils;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.downloadmanager.AppsDownloadActivity;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.appgame.base.utils.AppDownloadListener;
import com.jiubang.ggheart.apps.appfunc.controler.SwitchControler;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.desks.diy.OutOfMemoryHandler;
import com.jiubang.ggheart.apps.desks.diy.plugin.PluginItemViewAdapter.OnPluginClosedListener;
import com.jiubang.ggheart.apps.gowidget.GoWidgetConstant;
import com.jiubang.ggheart.apps.gowidget.GoWidgetProviderInfo;
import com.jiubang.ggheart.apps.gowidget.gostore.util.GoStoreOperatorUtil;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.mediamanagement.inf.AppFuncContentTypes;

/**
 * 国内包插件管理器adapter
 * @author liulixia
 *
 */
public class PluginCnItemViewAdapter extends BaseAdapter {
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private ArrayList<GoPluginOrWidgetInfo> mDatas = null;
	private ListView mListView; // 弹出菜单项
	private PopupWindow mPopupWindow = null; // 弹出窗口
	private int mPopupWindowYOff = 0;
	private int mPopupWindowXOff = 0;
	private int mMenuWidth = 0;
	private int mMenuItemHeight = 0;
	private ImageView mUpdateSignImageView; // Update主题标识
	private OnPluginClosedListener mListener;
	private GoToAppsDownloadListener mGoToAppsDownloadListener = null;
	private SpaceCalculator mSpaceCalculator = null;
	private PackageManager mPackageManager = null;
	private AsyncImageManager mAsyncImageManager = null;
	private static final int UPDATE_LOG_ID = 101;
	
	public PluginCnItemViewAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mListView = new ListView(context);
		mGoToAppsDownloadListener = new GoToAppsDownloadListener();
		mSpaceCalculator = SpaceCalculator.getInstance(context);
//		mMenuWidth = mContext.getResources().getDimensionPixelSize(R.dimen.plugin_manage_menu_width);
		mPackageManager = mContext.getPackageManager();
		mAsyncImageManager = AsyncImageManager.getInstance();
	}

	public void setData(ArrayList<GoPluginOrWidgetInfo> datas, OnPluginClosedListener listener) {
		mDatas = datas;
		mListener = listener;
	}
	
	@Override
	public int getCount() {
		if (mDatas == null) {
			return 0;
		}
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		if (mDatas == null) {
			return null;
		}
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mDatas == null || mDatas.size() == 0) {
			return null;
		}
		Viewholder viewholder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.plugin_widget_item, null);
			viewholder = new Viewholder();
			viewholder.mIcon = (ImageView) convertView.findViewById(R.id.widgetImage);
			viewholder.mGowidgetName = (TextView) convertView.findViewById(R.id.widgetName);
			viewholder.mGowidgetState = (TextView) convertView.findViewById(R.id.widgetState);
			viewholder.mMenu = (ImageView) convertView.findViewById(R.id.widgetMenu);
			viewholder.mRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.widgetLayout);
			convertView.setTag(viewholder);
		} else {
			PluginItemView itemView = (PluginItemView) convertView;
			if (mSpaceCalculator.getItemWidth() != itemView.getWidth()) {
				itemView.setLayoutParams();
			}
			viewholder = (Viewholder) convertView.getTag();
		}
		final GoPluginOrWidgetInfo widgetInfo = mDatas.get(position);
		//TODO
//		Bitmap bm = getBitmap(widgetInfo.mWidgetImgId);
//		viewholder.mIcon.setImageBitmap(bm);
		viewholder.mGowidgetName.setText(widgetInfo.mWidgetName);
		final RelativeLayout layout = viewholder.mRelativeLayout;
		int state = widgetInfo.mState;
		
		if (state == GoPluginOrWidgetInfo.INSTALLED) { //本地
			viewholder.mGowidgetState.setVisibility(View.GONE);
			viewholder.mMenu.setVisibility(View.VISIBLE);
			setInstallWidgetImage(viewholder.mIcon, widgetInfo.mWidgetPkgName);
			viewholder.mMenu.setImageResource(R.drawable.plugin_manage_menu_icon);
			if (widgetInfo.mNeedUpdate) {
				addLogoForUpdate(layout);
			} else {
				removeLogoForUpdate(layout);
			}
			viewholder.mMenu.setOnClickListener(new View.OnClickListener() { //点击menu
				
				@Override
				public void onClick(View v) {
					mMenuWidth = mSpaceCalculator.getItemWidth() * 2 / 3;
					mMenuItemHeight = mMenuWidth / 3;
					
					String update = mContext.getResources().getString(R.string.plugin_manage_update);
					String uninstall = mContext.getResources().getString(R.string.uninstalltext);
					String[] menuValues = null;
					if (widgetInfo.mNeedUpdate) {
						menuValues = new String[]{update, uninstall};
						mPopupWindowYOff = mSpaceCalculator.getTextViewHeight() + 2 * mMenuItemHeight - DrawUtils.dip2px(5);
//						mPopupWindowYOff = mSpaceCalculator.getTextViewHeight() + DrawUtils.dip2px(66);
					} else {
						menuValues = new String[]{uninstall};
//						mPopupWindowYOff = mSpaceCalculator.getTextViewHeight() + DrawUtils.dip2px(28);
						mPopupWindowYOff = mSpaceCalculator.getTextViewHeight() + mMenuItemHeight - DrawUtils.dip2px(5);
					}
					initMenuList(menuValues, widgetInfo);
					openMenu(v);
				}
			});
			
			viewholder.mIcon.setOnClickListener(new View.OnClickListener() { //点击图片
				
				@Override
				public void onClick(View v) {
					openPluginOrWidget(widgetInfo);
				}
			});
		} else {
			final ImageView imgView = viewholder.mIcon;
			Bitmap bmp = mAsyncImageManager.loadImage(LauncherEnv.Path.PLUGIN_MANAGE_IMAGE_PATH, widgetInfo.mWidgetPkgName,
					widgetInfo.mWidgetImgUrl, true, false, null, new AsyncImageLoadedCallBack() {
						@Override
						public void imageLoaded(Bitmap imageBitmap, String imgUrl) {
							imgView.setScaleType(ScaleType.FIT_XY);
							imgView.setImageBitmap(imageBitmap);
						}
					});
			if (bmp != null) {
				imgView.setScaleType(ScaleType.FIT_XY);
				imgView.setImageBitmap(bmp);
			} else {
				imgView.setScaleType(ScaleType.FIT_XY);
				imgView.setImageResource(R.drawable.plugin_manage_item_image_default);
			}
			switch (state) {
				case GoPluginOrWidgetInfo.NOT_INSTALLED:  //未安装并无下载
					viewholder.mMenu.setVisibility(View.GONE);
					viewholder.mGowidgetState.setVisibility(View.GONE);
					viewholder.mIcon.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							String ftpUrl = widgetInfo.mWidgetFtpUrl;
							GoStoreOperatorUtil.downloadFileDirectly(mContext, widgetInfo.mWidgetName,
									ftpUrl, System.currentTimeMillis(),
									widgetInfo.mWidgetPkgName, new Class[] { AppDownloadListener.class }, null, 0, null);
							widgetInfo.mState = GoPluginOrWidgetInfo.WAIT_FOR_DOWNLOAD;
							widgetInfo.mPrecent = 0;
							PluginCnItemViewAdapter.this.notifyDataSetChanged();
						}
					});
					break;
				case GoPluginOrWidgetInfo.DOWNLOAD_COMPLETE: //已下载
					viewholder.mGowidgetState.setVisibility(View.GONE);
					viewholder.mMenu.setVisibility(View.VISIBLE);
					viewholder.mMenu.setImageResource(R.drawable.plugin_manage_download_complete);
					viewholder.mMenu.setOnClickListener(mGoToAppsDownloadListener);
					viewholder.mIcon.setOnClickListener(mGoToAppsDownloadListener);
					break;
				case GoPluginOrWidgetInfo.DOWNLOAD_FAIL: //失败
					viewholder.mIcon.setOnClickListener(mGoToAppsDownloadListener);
					viewholder.mGowidgetState.setVisibility(View.VISIBLE);
					viewholder.mMenu.setVisibility(View.GONE);
					viewholder.mGowidgetState.setText(R.string.ftp_download_fail);
					break;
				case GoPluginOrWidgetInfo.WAIT_FOR_DOWNLOAD: //等待下载
				case GoPluginOrWidgetInfo.DOWNLOADING: //正在下载
					viewholder.mGowidgetState.setVisibility(View.VISIBLE);
					viewholder.mMenu.setVisibility(View.GONE);
					viewholder.mGowidgetState.setText(widgetInfo.mPrecent + "%");
					viewholder.mIcon.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, AppsDownloadActivity.class);
							mContext.startActivity(intent);
						}
					});
					break;
				case GoPluginOrWidgetInfo.PAUSE:
					viewholder.mGowidgetState.setVisibility(View.GONE);
					viewholder.mMenu.setVisibility(View.VISIBLE);
					viewholder.mMenu.setImageResource(R.drawable.plugin_manage_download_pause);
					viewholder.mMenu.setOnClickListener(mGoToAppsDownloadListener); 
					viewholder.mIcon.setOnClickListener(mGoToAppsDownloadListener);
					break;
				default:
					break;
			}
		}
		
		return convertView;
	}
	
	/**
	 * 
	 * @author liulixia
	 *
	 */
	class GoToAppsDownloadListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, AppsDownloadActivity.class);
			mContext.startActivity(intent);
		}
		
	}
	
	/**
	 * 
	 * @author liulixia
	 *
	 */
	class GetInstallWidgetsIcon extends AsyncTask<ImageView, Void, Void> {

		@Override
		protected Void doInBackground(ImageView... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	private void setInstallWidgetImage(ImageView imageView, String packageName) {
		try {
			Drawable drawable = mPackageManager.getApplicationIcon(packageName);
			if (drawable != null) {
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				drawable = AppDataEngine.getInstance(mContext).createBitmapDrawable(drawable);
				imageView.setImageDrawable(drawable);
//				imageView.setImageBitmap(((BitmapDrawable)drawable).getBitmap());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void openPluginOrWidget(GoPluginOrWidgetInfo info) {
		if (info.mWidgetType == GoPluginOrWidgetInfo.TYPE_PLUGIN) { //打开插件
			Intent intent = new Intent();
			try {
				if (info.mWidgetPkgName.equals(PackageName.MEDIA_PLUGIN)) {
					intent.setClassName(mContext, IGoLauncherClassName.GOLAUNCHER_ACTIVITY);
					mContext.startActivity(intent);
					switch (AppFuncContentTypes.sType_for_setting) {
						case AppFuncContentTypes.IMAGE :
							SwitchControler.getInstance(mContext).showMediaManagementImageContent();
							break;
						case AppFuncContentTypes.MUSIC :
							SwitchControler.getInstance(mContext).showMediaManagementMusicContent();
							break;
						case AppFuncContentTypes.VIDEO :
							SwitchControler.getInstance(mContext).showMediaManagementVideoContent();
							break;
						default :
							SwitchControler.getInstance(mContext).showMediaManagementImageContent();
							break;
					}
				} else {
					intent = mContext.getPackageManager().getLaunchIntentForPackage(info.mWidgetPkgName);
					mContext.startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			intent = null;
		} else { //进入go小部件添加界面
			String packageName = info.mWidgetPkgName;
			PackageInfo packageInfo = AppUtils.getAppPackageInfo(mContext, packageName);
			if (packageInfo != null) {
				GoWidgetProviderInfo providerInfo = new GoWidgetProviderInfo(
						packageName, "");
				Resources resources = getGoWidgetResources(packageName);
				if (resources != null) {
					try {
						// 查找Widget的名字
						int identifier = resources.getIdentifier(
								GoWidgetConstant.WIDGET_TITLE, "string",
								packageName);
						if (identifier != 0) {
							providerInfo.getProviderInfo().label = resources
									.getString(identifier);
						}

						// 查找Widget的图标
						identifier = resources.getIdentifier(
								GoWidgetConstant.WIDGET_ICON, "string",
								packageName);

						if (identifier > 0) {
							String icon = resources.getString(identifier);
							providerInfo.getProviderInfo().icon = resources.getIdentifier(icon,
									"drawable", packageName);
						}
					} catch (Exception e) {
						Log.e("gowidget", "parse widget info error, pkg = "
								+ packageName);
					}
				}
				if (mListener != null && providerInfo != null) {
					mListener.finishSelf();
				}
			}
		}
	}
	
	private Resources getGoWidgetResources(String themePackage) {
		if (themePackage == null) {
			return null;
		}

		Resources resources = null;
		try {
			resources = mContext.getPackageManager().getResourcesForApplication(themePackage);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return resources;
	}
	
	/**
	 * 初始化菜单项
	 * */
	private void initMenuList(String[] menuItemNames, GoPluginOrWidgetInfo info) {

		MenuAdapter adapter = new MenuAdapter(menuItemNames, info);
		mListView.setAdapter(adapter);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setOnItemClickListener(new MenuOnItemClickListener());
		mListView.setDrawingCacheEnabled(true);
		mListView.setSelector(R.drawable.plugin_manage_menu_item_click);
		mListView.setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.plugin_manage_menu_bg));
		mListView.setDivider(mContext.getResources().getDrawable(
				R.drawable.allfunc_allapp_menu_line));

	}
	
	/**
	 * 本地安装插件弹出框
	 * @author liulixia
	 *
	 */
	class MenuOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			GoPluginOrWidgetInfo widgetInfo = (GoPluginOrWidgetInfo) view.getTag();
			if (widgetInfo.mNeedUpdate) {
				if (position == 0) {
					GoStoreOperatorUtil.downloadFileDirectly(mContext, widgetInfo.mWidgetName,
							widgetInfo.mWidgetFtpUrl, System.currentTimeMillis(),
							widgetInfo.mWidgetPkgName, new Class[] { AppDownloadListener.class }, null, 0, null);
				} else {
					goToUninstall(widgetInfo.mWidgetPkgName);
				}
			} else {
				goToUninstall(widgetInfo.mWidgetPkgName);
			}
			if (isMenuShowing()) {
				dismissMenu();
			}
		}
		
	}
	
	private void goToUninstall(String packageName) {
		if (GoAppUtils.isAppExist(mContext, packageName)) {
			Uri packageURI = Uri.parse("package:" + packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			mContext.startActivity(uninstallIntent);
		} 
	}
	/**
	 * 打开菜单
	 * */
	private void openMenu(View v) {
		dismissMenu();
		mPopupWindow = new PopupWindow(mListView, mMenuWidth,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
		// 必须设置背景
		mPopupWindowXOff = mMenuWidth - v.getWidth() - DrawUtils.dip2px(5);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 设置焦点
		mPopupWindow.setFocusable(true);
		// 设置点击其他地方 就消失
		mPopupWindow.setOutsideTouchable(true);
//		mPopupWindow.showAsDropDown(v, 0, 0);
		mPopupWindow.showAsDropDown(v, -mPopupWindowXOff, -mPopupWindowYOff);
		mPopupWindow.update();

	}
	
	/**
	 * 判断菜单是否显示
	 * */
	private boolean isMenuShowing() {
		return mPopupWindow != null && mPopupWindow.isShowing();
	}

	/**
	 * 取消菜单
	 * */
	public void dismissMenu() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}

	}
	
	/**
	 * 若widget或小部件是有更新的，则添加标识图片（Update）
	 */
	private void addLogoForUpdate(RelativeLayout layout) {
		try {
			mUpdateSignImageView = (ImageView) layout.findViewById(UPDATE_LOG_ID);
			if (mUpdateSignImageView != null) {
				layout.removeView(mUpdateSignImageView);
			}
			if (mUpdateSignImageView == null) {
				mUpdateSignImageView = new ImageView(mContext);
			}
			Drawable signDrawable = mContext.getResources()
					.getDrawable(R.drawable.theme_update);
			mUpdateSignImageView.setImageDrawable(signDrawable);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mUpdateSignImageView.setId(UPDATE_LOG_ID);
			layout.addView(mUpdateSignImageView, params);
		} catch (OutOfMemoryError e) {
			OutOfMemoryHandler.handle();
		} catch (Throwable e) {
			
		}
	}
	
	/**
	 * 本地安装没有更新数据时，删除掉update标识
	 * @param layout
	 */
	private void removeLogoForUpdate(RelativeLayout layout) {
		try {
			mUpdateSignImageView = (ImageView) layout.findViewById(UPDATE_LOG_ID);
			if (mUpdateSignImageView != null) {
				layout.removeView(mUpdateSignImageView);
			}
		} catch (Exception e) {

		}
	}
	
	
	/**
	 * 菜单项适配器
	 * */
	class MenuAdapter extends BaseAdapter {

		private String[] mItems;
		private GoPluginOrWidgetInfo mWidgetInfo = null;

		public MenuAdapter(String[] items, GoPluginOrWidgetInfo widgetInfo) {
			this.mItems = items;
			this.mWidgetInfo = widgetInfo;
		}

		@Override
		public int getCount() {
			return mItems == null ? 0 : mItems.length;
		}

		@Override
		public Object getItem(int position) {

			return mItems == null ? null : mItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView itemTextView = null;
			if (convertView == null) {
				itemTextView = (TextView) mInflater.inflate(R.layout.plugin_manage_menu_item,
						null);
				DeskSettingConstants.setTextViewTypeFace(itemTextView);
			} else {
				itemTextView = (TextView) convertView;
			}
			String itemText = mItems[position];
			ViewGroup.LayoutParams params = itemTextView.getLayoutParams();
			if (params == null) {
				params = new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, mMenuItemHeight);
			} else {
				params.height = mMenuItemHeight;
			}
			itemTextView.setLayoutParams(params);
			itemTextView.setText(itemText);
			itemTextView.setTag(mWidgetInfo);
			return itemTextView;
		}

	}
	
	/**
	 * 
	 * @author liulixia
	 *
	 */
	class Viewholder {
		ImageView mIcon;
		TextView mGowidgetName;
		TextView mGowidgetState;
		RelativeLayout mRelativeLayout;
		ImageView mMenu;
	}
	
	
	public void clear() {
		if (mUpdateSignImageView != null) {
			mUpdateSignImageView = null;
		}
		mDatas = null;
		mListView = null;
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}
}
