package com.jiubang.ggheart.apps.desks.imagepreview;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.go.proxy.GoLauncherActivityProxy;
import com.go.proxy.MsgMgrProxy;
import com.go.util.AppUtils;
import com.go.util.GoViewCompatProxy;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.go.util.market.MarketConstant;
import com.golauncher.message.ICommonMsgId;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IDockMsgId;
import com.golauncher.message.IFolderMsgId;
import com.golauncher.message.IScreenFrameMsgId;
import com.golauncher.utils.GoAppUtils;
import com.jiubang.ggheart.appgame.appcenter.component.AppsManagementActivity;
import com.jiubang.ggheart.appgame.base.component.MainViewGroup;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingUtils;
import com.jiubang.ggheart.apps.desks.diy.CustomIconUtil;
import com.jiubang.ggheart.apps.desks.diy.IRequestCodeIds;
import com.jiubang.ggheart.apps.desks.diy.StatusBarHandler;
import com.jiubang.ggheart.apps.desks.dock.DockStyleIconManager;
import com.jiubang.ggheart.apps.desks.dock.DockStylePkgInfo;
import com.jiubang.ggheart.apps.desks.dock.StyleBaseInfo;
import com.jiubang.ggheart.apps.desks.imagepreview.BladeView.OnColumnChangeListener;
import com.jiubang.ggheart.apps.desks.imagepreview.BladeView.OnScrollBarTouchListener;
import com.jiubang.ggheart.apps.desks.imagepreview.GoThemeInstalledReceiver.IReloadViewData;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewAdapter.FileImageNode;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewAdapter.IImageNode;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewAdapter.PackageImageNode;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewAdapter.ResourceImageNode;
import com.jiubang.ggheart.components.DeskActivity;
import com.jiubang.ggheart.components.DeskProgressDialog;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.AppDataEngine;
import com.jiubang.ggheart.data.CustomIconRes;
import com.jiubang.ggheart.data.statistics.GuiThemeStatistics;
import com.jiubang.ggheart.data.statistics.IGoLauncherUserBehaviorStatic;
import com.jiubang.ggheart.data.theme.ThemeManager;

/**
 * 更换图标
 * 
 * @author licanhui
 */
public class ChangeIconPreviewActivity extends DeskActivity implements OnClickListener, IReloadViewData {
	/**
	 * 标记是从哪种请求返回的更换图标操作
	 */
	public static int sFromWhatRequester;

	public static final int SCREEN_STYLE = 1; // 桌面普通图标
	public static final int DOCK_STYLE_FROM_EDIT = 2; // dock直接进入更换图标选择界面
	public static final int USER_FOLDER_STYLE = 3; // 桌面user文件夹的样式
	public static final int DOCK_FOLDER_STYLE = 4; // dock user文件夹的样式
	public static final int SCREEN_FOLDER_ITEM_STYLE = 5; // 桌面文件夹内的图标
	public static final int DOCK_FOLDER_ITEM_STYLE = 6; // dock文件夹内的图标
	public static final String DEFAULT_NAME = "defaultName";
	public static final String DEFAULT_ICON_BITMAP = "defaultIconBitmap";
	
	private RelativeLayout mLayout;
	private LayoutInflater mInflater;
	private TextView mThemeNameTextView; // 当前主题标题
//	private LinearLayout mTitleLinearLayout;
	private ImageView mDefaultIcon; // 默认图标
	private TextView mDefaultName; // 默认名称
//	private LinearLayout mChangeThemeBtn; // 点击主题
	private ImageView mCustomBtn; // 自定义按钮
	private ImageView mDownBtn; // 下载按钮
	private Button mResetBtn; // 恢复默认按钮
	private ImageView mBackBtn;
	private RelativeLayout mChangeIconGridLayout;
	private BladeView mBladeView;

	private ImageGridView mImageGridView;
	private ImagePreviewAdapter mImageAdapter;
	private ImageGridParam mParam;
	private ImagePreviewData mData;

	private ProgressDialog mProgressDialog; // 加载所有主题文件夹图标的进度条
	private Handler mHandler;
	public static final int LOAD_ICON_FINISH = 1; // 完成加载主题的标志
	public static final int DISSMISS_LOAD_DIALOG = 2; // 完成加载主题的标志
	public static final int UPDATE_ICON_FINISH = 3; // 完成更新主题的标志
	
	private ArrayList<String> mPackageList = null; // 所有文件夹包的队列
	private ArrayList<String> mResNameList = null; // 所有文件夹图标的名称队列
	private ChangeThemeMenu mChangeThemeMenu = null; // 更换主题菜单
	public int mSelection = -1; // 目前选择的位置
	private LinearLayout mMoreIconLayout;

	private String mLastPkg;
	private GoThemeInstalledReceiver mThemePkgChangedReceiver;
	private boolean mHasFocus;
	private boolean mNeedReLoadWhileOnResume;
	private String mThemePkgNameUpdated;
	
	private int mOldPaddingLeft;
	private int mOldPaddingTop;
	private int mOldPaddingRight;
	private int mOldPaddingBottom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if (GoLauncherActivityProxy.getActivity() == null) {
			finish();
			return;
		}
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = (RelativeLayout) mInflater.inflate(R.layout.new_change_theme_icon_select, null);
		mLayout.setBackgroundColor(0xB31f1f1f);
		setContentView(mLayout);
		mDefaultIcon = (ImageView) findViewById(R.id.defaultIcon);
		mDefaultName = (TextView) findViewById(R.id.defaultName);
		mThemeNameTextView = (TextView) findViewById(R.id.themeName);
		mCustomBtn = (ImageView) findViewById(R.id.customBtn);
		mDownBtn = (ImageView) findViewById(R.id.downBtn);
		mResetBtn = (Button) findViewById(R.id.resetBtn);
		mChangeIconGridLayout = (RelativeLayout) findViewById(R.id.changeIconLayout);
		mBackBtn = (ImageView) findViewById(R.id.change_icon_back);
		mBladeView = (BladeView) findViewById(R.id.bladeView);
		mThemeNameTextView.setOnClickListener(this);
		mCustomBtn.setOnClickListener(this);
		mDownBtn.setOnClickListener(this);
		mResetBtn.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
		mBladeView.setOnTouchScrollBarListener(mScrollBarTouchListener);
		mBladeView.setScrollBarWidth(getResources()
				.getDimensionPixelSize(R.dimen.bladeview_width));
		
		setDefaultIconAndName(); // 设置默认图标和名字
		initHandler();
		initmParam(); // 初始化排版参数
		initGridView(); // 初始化排版参数
		mMoreIconLayout = (LinearLayout) findViewById(R.id.bottom_more_icon_layout);
		mMoreIconLayout.setOnClickListener(this);
		
		setGridViewData(false);

		// 注册广播监听主题包安装状态
		registerThemePkgChangedReceiver();
		
		mOldPaddingLeft = mLayout.getPaddingLeft();
		mOldPaddingTop = mLayout.getPaddingTop();
		mOldPaddingRight = mLayout.getPaddingRight();
		mOldPaddingBottom = mLayout.getPaddingBottom();
		
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			// android4.4以上，通知栏透明
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
							| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			StatusBarHandler.setStatusBarTransparentKitKat(this.getWindow(),
					true);
			int flag = GoViewCompatProxy.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			if (Machine.canHideNavBar()) {
				StatusBarHandler.setNavBarTransparentKitKat(this.getWindow(),
						true);
				flag |= GoViewCompatProxy.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
			}
			GoViewCompatProxy.setSystemUiVisibility(mLayout,
					flag);
			changeNavBarLocation();
		}
		
	}
	
	
	/**
	 * 在设备方向改变的时候，底部的虚拟键应该预留空位
	 */
	public void changeNavBarLocation() {
		int navbarLocation = DrawUtils.getNavBarLocation();
		final int topD = StatusBarHandler.isHide() ? 0 : StatusBarHandler
				.getStatusbarHeight();
		if (navbarLocation == DrawUtils.NAVBAR_LOCATION_RIGHT) {
			mLayout.setPadding(mOldPaddingLeft, mOldPaddingTop + topD,
					mOldPaddingRight + DrawUtils.getNavBarWidth(),
					mOldPaddingBottom);
		} else {
			mLayout.setPadding(mOldPaddingLeft, mOldPaddingTop + topD,
					mOldPaddingRight,
					mOldPaddingBottom + DrawUtils.getNavBarHeight());
		}
	}
	
	/**
	 * 设置默认图标和名字
	 */
	public void setDefaultIconAndName() {
		Intent intent = getIntent();
		if (null != intent) {
			Bundle bundle = intent.getExtras();
			if (null != bundle) {
				// 设置默认图标
				Bitmap bmp = bundle.getParcelable(DEFAULT_ICON_BITMAP);
				// 某些系统由于图片格式的不一样。Bitmap被序列化传递过来后获取的getConfig为NULL，无法正常显示图片
				// 可以采取把Bitmap转化成字节数组进行传递
				if (bmp != null && bmp.getConfig() != null) {
					mDefaultIcon.setImageBitmap(bmp);
				} else {
					// 设置系统机器人图片
					BitmapDrawable sysBitmapDrawable = AppDataEngine.getInstance(this)
							.getSysBitmapDrawable();
					mDefaultIcon.setImageDrawable(sysBitmapDrawable);
				}

				// 设置默认名称
				String defaultNameString = bundle.getString(DEFAULT_NAME);
				if (defaultNameString != null) {
					mDefaultName.setText(defaultNameString);
				}
			}
		}
	}

	/**
	 * 初始化排版参数
	 */
	private void initmParam() {
		mParam = new ImageGridParam();
		Resources resources = this.getResources();
		mParam.mWidth = resources.getDimensionPixelSize(R.dimen.imagepreview_grid_width);
		mParam.mHeight = resources.getDimensionPixelSize(R.dimen.imagepreview_grid_height);
		mParam.mLeftPadding = resources.getDimensionPixelSize(R.dimen.imagepreview_grid_l_padding);
		mParam.mTopPadding = resources.getDimensionPixelSize(R.dimen.imagepreview_grid_t_padding);
		mParam.mRightPadding = resources.getDimensionPixelSize(R.dimen.imagepreview_grid_r_padding);
		mParam.mBottomPadding = resources
				.getDimensionPixelSize(R.dimen.imagepreview_grid_b_padding);
	}

	/**
	 * 用于监听gridView的列数变化。
	 */
	private OnColumnChangeListener mListener = new OnColumnChangeListener() {
		@Override
		public void onColumnChange(int columns) {
			if (mBladeView != null) {
				mBladeView.setHasInit(false);
			}
		}
	};
	
	private OnScrollBarTouchListener mScrollBarTouchListener = new OnScrollBarTouchListener() {
		@Override
		public void onScrollBarTouch(float ratio) {
			if (mImageGridView != null && mImageAdapter != null) {
				// 所有行数
				int totalItemCount = mImageAdapter.getCount();
				int targetRows = (int) (totalItemCount * ratio);
				if (targetRows < 0) {
					targetRows = 0;
				}
				mImageGridView.smoothScrollToPosition(targetRows);
			}
		}
	};
	
	/**
	 * 初始化普通图标的GridView
	 */
	private void initGridView() {
		// 宫格
		mImageGridView = (ImageGridView) findViewById(R.id.gridview);
		mImageGridView.setParams(mParam);
		mImageGridView.setSelector(R.drawable.change_icon_tab_selector);
		mImageGridView.setOnColumnChangeListener(mListener);
		// 监听
		mImageGridView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						IImageNode node = (IImageNode) parent.getAdapter()
								.getItem(position);
						if (null != node) {
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							if (node instanceof ImagePreviewAdapter.FileImageNode) {
								bundle.putInt(
										ImagePreviewResultType.TYPE_STRING,
										ImagePreviewResultType.TYPE_IMAGE_FILE);
								bundle.putString(
										ImagePreviewResultType.IMAGE_PATH_STRING,
										((FileImageNode) node).getFilePath());
							} else if (node instanceof ImagePreviewAdapter.ResourceImageNode) {
								// 现在版本升级：原来GOLauncher内的（非主题包内的）图片也当TYPE_PACKAGE_RESOURCE类型来保存
								bundle.putInt(
										ImagePreviewResultType.TYPE_STRING,
										ImagePreviewResultType.TYPE_PACKAGE_RESOURCE);
								bundle.putString(
										ImagePreviewResultType.IMAGE_PACKAGE_NAME,
										IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
								bundle.putString(
										ImagePreviewResultType.IMAGE_PATH_STRING,
										((ResourceImageNode) node)
												.getResourceName());
							} else if (node instanceof ImagePreviewAdapter.DrawableImageNode) {
								bundle.putInt(
										ImagePreviewResultType.TYPE_STRING,
										ImagePreviewResultType.TYPE_DEFAULT);
							} else if (node instanceof ImagePreviewAdapter.PackageImageNode) {

								PackageImageNode nodePackage = (PackageImageNode) parent
										.getAdapter().getItem(position);
								boolean isThemePay = ThemeManager.getInstance(
										ApplicationProxy.getContext())
										.getThemeResrouceBeanisPay(
												nodePackage.getPackageName());
								if (!isThemePay) {
									DeskSettingUtils.showNeedPayThemeToast();
									return;
								}

								if (mData != null) {
									int folderIconPosition = mData
											.getFolderIconPosition();
									if (folderIconPosition == -1
											|| position < folderIconPosition) {
										bundle.putInt(
												ImagePreviewResultType.TYPE_STRING,
												ImagePreviewResultType.TYPE_PACKAGE_RESOURCE);
									} else {
										bundle.putInt(
												ImagePreviewResultType.TYPE_STRING,
												ImagePreviewResultType.TYPE_APP_ICON);
									}
								}
								bundle.putString(
										ImagePreviewResultType.IMAGE_PACKAGE_NAME,
										((PackageImageNode) node)
												.getPackageName());
								bundle.putString(
										ImagePreviewResultType.IMAGE_PATH_STRING,
										((PackageImageNode) node)
												.getPackageResName());
							}
							intent.putExtras(bundle);
							setResult(RESULT_OK, intent);
						}
						finish();
					}
				});

		mImageGridView.setVerticalScrollBarEnabled(false);
		mImageGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mBladeView == null || visibleItemCount == 0) {
					return;
				}
				if (visibleItemCount - firstVisibleItem >= totalItemCount) {
					mBladeView.setVisibility(View.GONE);
				} else {
					mBladeView.setVisibility(View.VISIBLE);
				}
				int numOfColumns = mImageGridView.getNumCols();
				// 可见的行数
				int canSeeRow = 0;
				if (visibleItemCount % numOfColumns != 0) {
					canSeeRow = visibleItemCount / numOfColumns + 1;
				} else {
					canSeeRow = visibleItemCount / numOfColumns;
				}
				// 所有行数
				int totalRows = 0;
				if (totalItemCount % numOfColumns != 0) {
					totalRows = totalItemCount / numOfColumns + 1;
				} else {
					totalRows = totalItemCount / numOfColumns;
				}
				// 滚动条的高度比例
				if (firstVisibleItem == 0 && mImageAdapter.getIsDataInit()
						&& totalItemCount == mImageAdapter.getCount()
						&& !mBladeView.getHasInit()) {
					float bladeViewRatio = (float) canSeeRow / totalRows;
					int bladeViewHeight = (int) (mBladeView.getHeight() * bladeViewRatio);
					mBladeView.setScrollBarHeight(bladeViewHeight);
					mBladeView.setHasInit(true);
				}
				// 滚动条的初始位置比例
				int nowRows = firstVisibleItem / numOfColumns;
				// 当前所处的位置比例
				float ratio = (float) nowRows / totalRows;
				mBladeView.refreshScrollBar(ratio);
			}
		});
	}

	/**
	 * 初始化图标数据
	 */
	public void setGridViewData(final boolean isUpdate) {
		showProgressDialog(); // 显示等待框
		new Thread() {
			@Override
			public void run() {
				if (sFromWhatRequester == USER_FOLDER_STYLE
						|| sFromWhatRequester == DOCK_FOLDER_STYLE) {
					// 初始化文件夹图标数据
					mData = new ImagePreviewData(ChangeIconPreviewActivity.this, true);
				} else {
					// 初始化普通图标数据
					mData = new ImagePreviewData(ChangeIconPreviewActivity.this, false);
				}
				mHandler.sendEmptyMessage(isUpdate ? UPDATE_ICON_FINISH : LOAD_ICON_FINISH);
			}
		}.start();
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case LOAD_ICON_FINISH :
						initChangeThemeMenu(false);
						break;
						
					case DISSMISS_LOAD_DIALOG :
						dismissProgressDialog(); // 取消加载框
						break;
					case UPDATE_ICON_FINISH:
						initChangeThemeMenu(true);
						break;
				}
			}
		};
	}

	/**
	 * <br>功能简述: 初始化更换主题列表
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param isUpdate 由主题包安装卸载导致的界面刷新
	 */
	private void initChangeThemeMenu(final boolean isUpdate) {
		ArrayList<String> datas = new ArrayList<String>();
		int selection = 0;
		try {
			// 如果是文件夹就在第一项加"文件夹图标"选项
			if (sFromWhatRequester == USER_FOLDER_STYLE || sFromWhatRequester == DOCK_FOLDER_STYLE) {
				datas.add(0, getString(R.string.change_icon_folder));
			}

			String curName = ThemeManager.getInstance(ApplicationProxy.getContext()).getCurThemePackage();
			resetMoreLayutState(curName);
			String themeName = null;
			String themePkgName = null;
			for (int i = 0; i < mData.getThemeInfoBeans().size(); i++) {
				themeName = mData.getThemeInfoBeans().get(i).getThemeName();
				themePkgName = mData.getThemeInfoBeans().get(i).getPackageName();
				if (curName.equals(themePkgName)) {
					datas.add(themeName + "(" + getString(R.string.current) + ")");
					if (sFromWhatRequester == USER_FOLDER_STYLE
							|| sFromWhatRequester == DOCK_FOLDER_STYLE) {
						selection = 0; // 设置默认选择项
					} else if (!isUpdate) {
						selection = i;
					}
				} else {
					datas.add(themeName);
					if (themePkgName.equals(mThemePkgNameUpdated)
							&& isUpdate
							&& !(sFromWhatRequester == USER_FOLDER_STYLE || sFromWhatRequester == DOCK_FOLDER_STYLE)) {
						selection = i;
				}
			}
			}
		} catch (Throwable e) {
			// 发生异常，不赋值
		}

		if (sFromWhatRequester == DOCK_STYLE_FROM_EDIT) {
			// 如果是从dock请求更换图标，要判断当前是否有安装dock风格包
			DockStyleIconManager manager = DockStyleIconManager.getInstance(ApplicationProxy.getContext());
			ArrayList<StyleBaseInfo> mList = manager.getAllStyleBaseInfos();
			for (StyleBaseInfo info : mList) {
				datas.add(info.mStyleName);
			}

			mList.clear();
			mList = null;
		}

		mChangeThemeMenu = new ChangeThemeMenu(this, datas);
		mChangeThemeMenu.setmItemClickListener(this);
		selectTheme(selection, false, true);
	}

	/**
	 * 选择主题
	 * 
	 * @param position
	 * @param configurationChanged
	 */
	public void selectTheme(int position, boolean configurationChanged, boolean isUpdate) {
		if (mChangeThemeMenu != null) {
			mChangeThemeMenu.dismiss();
		}
		if (!configurationChanged && !isUpdate) {
			if (position == mSelection || mData == null) {
				//ADT-15823 界面一直提示正在初始化
				dismissProgressDialog();
				return;
			}
		}

		try {
			if (sFromWhatRequester == USER_FOLDER_STYLE || sFromWhatRequester == DOCK_FOLDER_STYLE) {
				// 如果选择文件夹第一个选项
				if (position == 0) {
					changeThemeOfAllFolder(); // 显示所有文件夹图标
				} else {
					changeTheme(mData.getThemeInfoBeans().get(position - 1).getPackageName()); // 数据需要-1
				}
			} else {
				if (position < mData.getThemeInfoBeans().size()) {
					changeTheme(mData.getThemeInfoBeans().get(position).getPackageName());
				}
				// 选择了dock风格包
				else if (position >= mData.getThemeInfoBeans().size()) {
					int count = position - (mData.getThemeInfoBeans().size());
					DockStyleIconManager manager = DockStyleIconManager.getInstance(ApplicationProxy.getContext());
					ArrayList<StyleBaseInfo> mList = manager.getAllStyleBaseInfos();
					DockStylePkgInfo info = manager.getDockStylePkgInfo(mList.get(count).mPkgName);
					if (null != info) {
						changeDockStyle(info.mPkgName, info.mImageResList);
					}
				}
			}
			mThemeNameTextView.setText(mChangeThemeMenu.getmStrings().get(position)); // 设置标题名称
			mSelection = position;
			// 图标数据更改，bladeview就需要重新初始化
			if (mBladeView != null) {
				mBladeView.setHasInit(false);
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			// 发生异常，不处理
		}
	}

	/**
	 * 显示所有文件夹图标
	 */
	public void changeThemeOfAllFolder() {
		if (null != mImageAdapter) {
			mImageAdapter.free();
			mImageAdapter = null;
		}
		mImageAdapter = new ImagePreviewAdapter(this, mParam);
		mImageGridView.setAdapter(mImageAdapter);
		mPackageList = mData.getmFolderPackageList();
		mResNameList = mData.getmFolderResNameList();
		mData.resetFolderIconPosition();
		mImageAdapter.initPackageResourceArrayInAllPacksges(mPackageList, mResNameList);
		showProgressDialog();
		mImageAdapter.start(mHandler);
	}

	/**
	 * 换主题，响应为可选图标集替换
	 */
	private void changeTheme(String themePkg) {
		if (null == mData) {
			return;
		}

		if (null != mImageAdapter) {
			mImageAdapter.free();
			mImageAdapter = null;
		}
		mImageAdapter = new ImagePreviewAdapter(this, mParam);
		mImageGridView.setAdapter(mImageAdapter);

		mData.initData(themePkg); // 初始化图标信息数据

		changeTheme(themePkg, mData.getmDrawable(), mData.getmFloder(), mData.getmStringsarray(),
				mData.getmResNameList());
	}

	private void changeDockStyle(String themePkg, ArrayList<String> mArrayList) {
		if (null == mData) {
			return;
		}

		if (null != mImageAdapter) {
			mImageAdapter.free();
			mImageAdapter = null;
		}
		mImageAdapter = new ImagePreviewAdapter(this, mParam);
		mImageGridView.setAdapter(mImageAdapter);

		changeTheme(themePkg, mData.getmDrawable(), null, null, mArrayList);
	}

	/**
	 * 可支持的图片来源类型
	 * 
	 * @param packageStr
	 * @param drawable
	 * @param folder
	 * @param stringsarray
	 * @param resNames
	 */
	private void changeTheme(String packageStr, Drawable drawable, String folder,
			String[] stringsarray, ArrayList<String> resNames) {
		if (null == mImageAdapter) {
			mImageAdapter = new ImagePreviewAdapter(this, mParam);
		}

		mImageAdapter.initDrawable(drawable);
		mImageAdapter.initFolder(folder);
		mImageAdapter.initResourceStringArray(stringsarray);
		if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(packageStr)
				|| IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER.equals(packageStr)) {

			// 解决ADT-7663 UI3.0主题换图标界面，图标显示异常，文件夹图标未放在第一位
			// 如果加载包含文件夹图标时，则将resNames中的文件夹图标先放入Adapter的list中
			// modify by zhengxiangcan 2012.09.03
			if (sFromWhatRequester == USER_FOLDER_STYLE || sFromWhatRequester == DOCK_FOLDER_STYLE) {
				ArrayList<String> tempList = new ArrayList<String>();
				tempList.add(resNames.get(0));
				mImageAdapter.initPackageResourceArray(packageStr, tempList);
				resNames.remove(0);
			}

			//如果是UI3.0图标库，会先加入默认主题图标库
			mImageAdapter.initPackageResourceArray(IGoLauncherClassName.DEFAULT_THEME_PACKAGE,
					CustomIconRes.getDefaultResList());
		}
		resetMoreLayutState(packageStr);
		mImageAdapter.initPackageResourceArray(packageStr, resNames);
		
		showProgressDialog();
		mImageAdapter.start(mHandler);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHasFocus = true;
		resetMoreLayutState(mLastPkg);  // 仅为预防这是安装了ui3.0
		if (mNeedReLoadWhileOnResume) {
			mNeedReLoadWhileOnResume = false;
			setGridViewData(true);
	}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mHasFocus = false;
	}

	private void resetMoreLayutState(String pkg) {
		mLastPkg = pkg;
		if (GoAppUtils.isAppExist(getApplicationContext(), IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER)) {
			if (mMoreIconLayout != null) {
				mMoreIconLayout.setVisibility(View.GONE);
			}
		} else {
			if (IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3.equals(pkg)) {
				if (mMoreIconLayout != null) {
					mMoreIconLayout.setVisibility(View.VISIBLE);
				}
			} else {
				if (mMoreIconLayout != null) {
					mMoreIconLayout.setVisibility(View.GONE);
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// 点击了菜单的一项
		if (v instanceof TextView && v.getTag() != null && v.getTag() instanceof Integer) {
			int position = (Integer) v.getTag();
			selectTheme(position, false, false);
		} else {
			switch (v.getId()) {

			// 自定义按钮
				case R.id.customBtn :
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					try {
						startActivityForResult(intent, IRequestCodeIds.REQUEST_CHANGE_ICON);
					} catch (Exception e) {
						e.printStackTrace();
						DeskToast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT)
								.show();
					}
					GuiThemeStatistics
					.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.ICON_CHANGE_03);
					break;

				// 下载按钮
				case R.id.downBtn :
					//					Intent intentDown = new Intent();
					//					intentDown.setClass(this, GoStore.class);
					//					intentDown.putExtra("sort", SortsBean.SORT_THEME + "");
					//					startActivity(intentDown);
					AppsManagementActivity.startAppCenter(this,
							MainViewGroup.ACCESS_FOR_APPCENTER_THEME, false);
					GuiThemeStatistics
					.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.ICON_CHANGE_04);
					break;

				// 恢复默认图标
				case R.id.resetBtn :
					// 桌面
					if (sFromWhatRequester == SCREEN_STYLE
							|| sFromWhatRequester == USER_FOLDER_STYLE) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
								IScreenFrameMsgId.SCREEN_RESET_DEFAULT, -1, null, null);
						finish();
					}
					// DOCK条
					else if (sFromWhatRequester == DOCK_STYLE_FROM_EDIT
							|| sFromWhatRequester == DOCK_FOLDER_STYLE) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
								IDockMsgId.DOCK_RESET_DEFAULT, -1, null, null);
						finish();
					}
					// 文件夹内部图标
					else if (sFromWhatRequester == SCREEN_FOLDER_ITEM_STYLE
							|| sFromWhatRequester == DOCK_FOLDER_ITEM_STYLE) {
						Bundle bundle = new Bundle();
						bundle.putInt(ImagePreviewResultType.TYPE_STRING,
								ImagePreviewResultType.TYPE_DEFAULT);
						
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_FOLDER,
								IFolderMsgId.FOLDER_RESET_DEFAULT, -1, bundle);
						finish();
					}
					GuiThemeStatistics
					.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.ICON_CHANGE_02);
					break;

				// 更换主题
				case R.id.themeName :
					mChangeThemeMenu.show(mThemeNameTextView/*mTitleLinearLayout*/);
					GuiThemeStatistics
							.goLauncherUserBehaviorStaticDataCache(IGoLauncherUserBehaviorStatic.ICON_CHANGE_01);
					break;
				case R.id.bottom_more_icon_layout :
					if (GoAppUtils.isMarketExist(this)) {
						GoAppUtils.gotoMarket(this, MarketConstant.APP_DETAIL
								+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER);
					} else {
						AppUtils.gotoBrowser(this, MarketConstant.APP_DETAIL
								+ IGoLauncherClassName.DEFAULT_THEME_PACKAGE_3_NEWER);
					}
					break;
				case R.id.change_icon_back:
					finish();
					break;
				default :
					break;
			}
		}
	}

	/**
	 * 显示进度条
	 * 
	 * @param index
	 */
	private void showProgressDialog() {
		if (null == mProgressDialog) {
			mProgressDialog = DeskProgressDialog.show(this, null,
					this.getString(R.string.initialization), true);
			mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					return true;
				}
			});
		}
	}

	/**
	 * 关闭进度条
	 */
	private void dismissProgressDialog() {
		if (mProgressDialog != null) {
			try {
				mProgressDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mProgressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mImageAdapter) {
			mImageAdapter.cancel();
		}
		unRegisterThemePkgChangedReceiver();
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int frameid = sFromWhatRequester;
		switch (requestCode) {
		// 打开裁剪Intent
			case IRequestCodeIds.REQUEST_CHANGE_ICON :
				if (resultCode == Activity.RESULT_OK && null != data) {
					Intent intent = null;
					if (frameid == DOCK_STYLE_FROM_EDIT || frameid == DOCK_FOLDER_STYLE) {
						intent = CustomIconUtil.getCropImageIntent(this, data,
								CustomIconUtil.DOCK_ICON);
					} else if (frameid == SCREEN_STYLE || frameid == USER_FOLDER_STYLE
							|| frameid == SCREEN_FOLDER_ITEM_STYLE
							|| frameid == DOCK_FOLDER_ITEM_STYLE) {
						intent = CustomIconUtil.getCropImageIntent(this, data,
								CustomIconUtil.SCREEN_ICON);
					}
					if (intent != null) {
						startActivityForResult(intent, IRequestCodeIds.REQUEST_CHANGE_CROP_ICON);
					} else {
						finish();
					}

				}
				break;

			// 裁剪完成后发消息通知修改图标
			case IRequestCodeIds.REQUEST_CHANGE_CROP_ICON :
				if (resultCode == Activity.RESULT_OK) {
					String cropFilePath = CustomIconUtil.getCropFilePath(data.getAction());
					Bundle bundle = data.getExtras();
					bundle.putString(ImagePreviewResultType.IMAGE_PATH_STRING, cropFilePath);
					bundle.putInt(ImagePreviewResultType.TYPE_STRING,
							ImagePreviewResultType.TYPE_IMAGE_FILE);

					if (frameid == DOCK_STYLE_FROM_EDIT || frameid == DOCK_FOLDER_STYLE) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.DOCK,
								ICommonMsgId.CHANGE_ICON_STYLE, -1, bundle, null);
					} else if (frameid == SCREEN_STYLE || frameid == USER_FOLDER_STYLE) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.SCREEN,
								ICommonMsgId.CHANGE_ICON_STYLE, -1, bundle, null);
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_FOLDER,
								ICommonMsgId.CHANGE_ICON_STYLE, -1, bundle, null);
					} else if (frameid == ChangeIconPreviewActivity.SCREEN_FOLDER_ITEM_STYLE
							|| frameid == ChangeIconPreviewActivity.DOCK_FOLDER_ITEM_STYLE) {
						MsgMgrProxy.sendMessage(this, IDiyFrameIds.APP_FOLDER,
								ICommonMsgId.CHANGE_ICON_STYLE, -1, bundle, null);
					}
				}
				finish();
				break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// 横竖屏切换的时候必须重新选择。否则mImageGridView不会刷新，因为mImageGridView改变了每行显示的个数
//		selectTheme(mSelection, true);
		DrawUtils.resetDensity(this);
		if (Machine.IS_SDK_ABOVE_KITKAT) {
			changeNavBarLocation();
		}
		mImageGridView.requestLayout();
	}

	public static void setFromWhatRequester(int fromWhatRequester) {
		ChangeIconPreviewActivity.sFromWhatRequester = fromWhatRequester;
	}
	
	private void registerThemePkgChangedReceiver() {
		if (mThemePkgChangedReceiver == null) {
			mThemePkgChangedReceiver = new GoThemeInstalledReceiver(this);
		}
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mThemePkgChangedReceiver, filter);
	}

	private void unRegisterThemePkgChangedReceiver() {
		if (mThemePkgChangedReceiver != null) {
			unregisterReceiver(mThemePkgChangedReceiver);
		}

	}

	@Override
	public void reloadViewData(String themePkgName) {
		//TODO 重新加载数据并刷新界面
		mThemePkgNameUpdated = themePkgName;
		if (mHasFocus) {
			setGridViewData(true);
			mNeedReLoadWhileOnResume = false;
		} else {
			mNeedReLoadWhileOnResume = true;
		}
	}
	
}
