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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gau.go.gostaticsdk.utiltool.DrawUtils;
import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.MsgMgrProxy;
import com.go.util.AppUtils;
import com.go.util.market.MarketConstant;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IScreenEditMsgId;
import com.golauncher.message.IScreenFrameMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager;
import com.jiubang.ggheart.appgame.base.manage.AsyncImageManager.AsyncImageLoadedCallBack;
import com.jiubang.ggheart.apps.appfunc.controler.SwitchControler;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.apps.gowidget.GoWidgetConstant;
import com.jiubang.ggheart.apps.gowidget.GoWidgetProviderInfo;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.launcher.PackageName;
import com.jiubang.ggheart.plugin.mediamanagement.inf.AppFuncContentTypes;
import com.jiubang.ggheart.plugin.notification.NotificationSettingActivity;

/**
 * 国外用户go插件或小部件的apapter
 * @author liulixia
 *
 */
public class PluginItemViewAdapter extends BaseAdapter {
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private ArrayList<GoPluginOrWidgetInfo> mDatas = null;
	private ListView mListView; // 弹出菜单项
	private PopupWindow mPopupWindow = null; // 弹出窗口
	private int mPopupWindowYOff = 0;
	private int mPopupWindowXOff = 0;
	private int mMenuWidth = 0;
	private int mMenuItemHeight = 0;
	private final String mMARKETPRE = "http://play.google.com/store/apps/details?id=";
	private OnPluginClosedListener mListener;
//	private OnRefreshImageListener mRefreshImageListener;
	private SpaceCalculator mSpaceCalculator = null;
	private PackageManager mPackageManager = null;
	private AsyncImageManager mAsyncImageManager = null;
	
	public PluginItemViewAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mListView = new ListView(context);
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
//		Bitmap bm = getBitmap(widgetInfo.mWidgetImgId);
//		viewholder.mIcon.setImageBitmap(bm);
		viewholder.mGowidgetName.setText(widgetInfo.mWidgetName);
		int state = widgetInfo.mState;
		
		if (state == GoPluginOrWidgetInfo.INSTALLED) { //本地
			setInstallWidgetImage(viewholder.mIcon, widgetInfo.mWidgetPkgName);
			viewholder.mGowidgetState.setVisibility(View.GONE);
			viewholder.mMenu.setVisibility(View.VISIBLE);;
			viewholder.mMenu.setOnClickListener(new View.OnClickListener() { //点击menu
				
				@Override
				public void onClick(View v) {
					mMenuWidth = mSpaceCalculator.getItemWidth() * 2 / 3;
					mMenuItemHeight = mMenuWidth / 3;
					String uninstall = mContext.getResources().getString(R.string.uninstalltext);
					String[] menuValues = new String[]{uninstall};
					mPopupWindowYOff = mSpaceCalculator.getTextViewHeight() + mMenuItemHeight - DrawUtils.dip2px(5); //DrawUtils.dip2px(28)
					initMenuList(menuValues, widgetInfo.mWidgetPkgName);
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
			imgView.setTag(widgetInfo.mWidgetImgUrl);
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
			viewholder.mGowidgetState.setVisibility(View.VISIBLE);
			viewholder.mMenu.setVisibility(View.GONE);
			final String marketUrl = widgetInfo.mWidgetMarket;
			final String packageName = widgetInfo.mWidgetPkgName;
			if (marketUrl != null && !marketUrl.equals("")) {
				viewholder.mIcon.setOnClickListener(new View.OnClickListener() { //点击图片
					@Override
					public void onClick(View v) {
						if (packageName.equals("com.youmi.filemaster")) {
							//如果是文件全能王，加统计
							GuiThemeStatistics.guiStaticData(52, packageName, "a000",
									1, "0", "41", "", "");
						}
						goToMarket(marketUrl);
					}
				});
			}
		}
		
		return convertView;
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
	
	private void goToMarket(String url) {
		if (GoAppUtils.isMarketExist(mContext)) {
			String id = url.substring(mMARKETPRE.length());
			if (url.startsWith("https")) {
				id = url.substring(mMARKETPRE.length() + 1);
			}
			GoAppUtils.gotoMarket(mContext, MarketConstant.APP_DETAIL + id);
		} else {
			GoAppUtils.gotoBrowserInRunTask(mContext, url);
		}
	}
	/**
	 * 打开已安装的插件和小部件
	 * @param info
	 */
	private void openPluginOrWidget(GoPluginOrWidgetInfo info) {
		if (info.mWidgetType == GoPluginOrWidgetInfo.TYPE_PLUGIN) { //打开插件
			Intent intent = new Intent();
			try {
				if (info.mWidgetPkgName.equals(PackageName.MEDIA_PLUGIN)) { //资源管理器插件
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
				} else if (info.mWidgetPkgName.equals(PackageName.NOTIFICATION_PLUGIN)) { //通讯统计插件
//					intent.setAction(Intent.ACTION_MAIN);
//					intent.addCategory(Intent.CATEGORY_LAUNCHER);
//					ComponentName cn = new ComponentName(PackageName.NOTIFICATION_PLUGIN, "com.gau.golauncherex.notification.NotificationActivity");
//					intent.setComponent(cn);
					intent.setClass(mContext, NotificationSettingActivity.class);
					mContext.startActivity(intent);
					mListener.finishSelf();
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
					
					//3D
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
							IScreenFrameMsgId.GOTO_SCREEN_EDIT_TAB, -1, packageName, null);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
							IScreenEditMsgId.SCREEN_EDIT_ADD_GOWIDGET, 0, providerInfo, null);

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
	private void initMenuList(String[] menuItemNames, String pkgName) {

		MenuAdapter adapter = new MenuAdapter(menuItemNames, pkgName);
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
			if (position == 0) {
				String pkgName = (String) view.getTag();
				goToUninstall(pkgName);
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
	 * 菜单项适配器
	 * */
	class MenuAdapter extends BaseAdapter {

		private String[] mItems;
		private String mPkgName;

		public MenuAdapter(String[] items, String pkgName) {
			this.mItems = items;
			this.mPkgName = pkgName;
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
			itemTextView.setTag(mPkgName);
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
		CheckBox mCheckbox;
		RelativeLayout mRelativeLayout;
		ImageView mMenu;
	}
	
	/**
	 * 
	 * @author liulixia
	 *
	 */
	interface OnPluginClosedListener {
		public void finishSelf();
	}
	
	public void clear() {
		mDatas = null;
		mListView = null;
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}
}
