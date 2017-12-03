package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.gau.go.launcherex.R;
import com.go.proxy.ApplicationProxy;
import com.go.util.ConvertUtils;
import com.go.util.device.Machine;
import com.go.util.graphics.DrawUtils;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingVisualFontTabView;
import com.jiubang.ggheart.apps.desks.Preferences.view.DeskSettingVisualIconTabView;
import com.jiubang.ggheart.data.statistics.StaticScreenSettingInfo;
import com.jiubang.ggheart.data.tables.DesktopTable;
import com.jiubang.ggheart.launcher.PackageName;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author 
 * @date  [2012-10-18]
 */
public class DesktopSettingInfo implements Parcelable {
	public boolean mShowPattem;
	public String mPattem;
	public int mPattemId;
	public int mPattemType;
	public boolean mShowStatusbar;
	public boolean mShowTitle;
	public int mRow;
	public int mColumn;
	public int mStyle;
	public boolean mThemeIconStyleSwitch;
	public String mThemeIconStylePackage;

	@Deprecated
	public String mFolderThemeIconStylePackage;

	@Deprecated
	public String mGGmenuThemeIconStylePackage;
	private boolean mAutofit;
	public int mTitleStyle;
	public boolean mCustomAppBg;
	public int mPressColor;
	public int mFocusColor;
	public boolean mLargeIcon;
	public int mIconSize;
	public boolean mShowIconBase;
	private int mFontSize;
	public boolean mCustomTitleColor;
	public int mTitleColor;

	private boolean mReload;

	/**
	 *  是否开启快捷栏
	 */
	public boolean mEnableSideDock;

	/**
	 * 侧边栏方向
	 */
	public int mSideDockPosition;
	
	/**
	 * 是否开启布局新算法
	 */
	private boolean mEnableMargins;
	
	
	// private boolean mTablet = false;

	public DesktopSettingInfo() {
		mShowPattem = false;
		mPattemId = 0;
		mPattemType = 0;
		mShowStatusbar = true;
		mShowTitle = true;
		mRow = StaticScreenSettingInfo.sScreenRow;
		mColumn = StaticScreenSettingInfo.sScreenCulumn;
		mStyle = StaticScreenSettingInfo.sColRowStyle;
		mAutofit = StaticScreenSettingInfo.sAutofit;
		// mShowPattem = true;
		mThemeIconStyleSwitch = true;
		mThemeIconStylePackage = PackageName.PACKAGE_NAME;
		mFolderThemeIconStylePackage = PackageName.PACKAGE_NAME;
		mGGmenuThemeIconStylePackage = PackageName.PACKAGE_NAME;
		mTitleStyle = 1;
		mCustomAppBg = false;
		mPressColor = 0XACE501;
		mFocusColor = 0XACE501;
		// 小分辨率（240*320）的手机首次安装时，不使用大图标 ,modify by yangbing 2012-05-15
		mLargeIcon = DrawUtils.sDensity <= 0.75 ? false : true;
		mIconSize = 0;
		mShowIconBase = true;
		mFontSize = 0;
		mCustomTitleColor = false;
		mTitleColor = 0xffffffff;
		
		mEnableSideDock = false;
		
		mSideDockPosition = 1;
		
		mEnableMargins = StaticScreenSettingInfo.sScreenMarginEnable;
	}

	/**
	 * 加入键值对
	 * 
	 * @param values
	 *            键值对
	 */
	public void contentValues(ContentValues values) {
		if (null == values) {
			return;
		}
		values.put(DesktopTable.BACKGROUNDPATTEMSWITCH, ConvertUtils.boolean2int(mShowPattem));
		values.put(DesktopTable.BACKGROUNDPATTEM, mPattem);
		values.put(DesktopTable.BACKGROUNDPATTEMID, mPattemId);
		values.put(DesktopTable.BACKGROUNDPATTEMTYPE, mPattemType);
		values.put(DesktopTable.AUTOSTATUSBAR, ConvertUtils.boolean2int(mShowStatusbar));
		values.put(DesktopTable.SHOWTITLE, ConvertUtils.boolean2int(mShowTitle));
		values.put(DesktopTable.ROW, mRow);
		values.put(DesktopTable.COLUMN, mColumn);
		values.put(DesktopTable.STYLE, mStyle);
		values.put(DesktopTable.THEMEICONSTYLE, ConvertUtils.boolean2int(mThemeIconStyleSwitch));
		values.put(DesktopTable.THEMEICONPACKAGE, mThemeIconStylePackage);
		values.put(DesktopTable.FOLDERTHEMEICONPACKAGE, mFolderThemeIconStylePackage);
		values.put(DesktopTable.GGMENUTHEMEICONPACKAGE, mGGmenuThemeIconStylePackage);
		values.put(DesktopTable.AUTOFITITEMS, ConvertUtils.boolean2int(mAutofit));
		values.put(DesktopTable.TITLESTYLE, mTitleStyle);
		values.put(DesktopTable.CUSTOMAPPBG, mCustomAppBg);
		values.put(DesktopTable.PRESSCOLOR, mPressColor);
		values.put(DesktopTable.FOCUSCOLOR, mFocusColor);
		values.put(DesktopTable.LARGEICON, mLargeIcon);
		values.put(DesktopTable.ICONSIZE, mIconSize);
		values.put(DesktopTable.SHOWICONBASE, mShowIconBase);
		values.put(DesktopTable.FONTSIZE, mFontSize);
		values.put(DesktopTable.CUSTOMTITLECOLOR, mCustomTitleColor);
		values.put(DesktopTable.TITLECOLOR, mTitleColor);
		values.put(DesktopTable.ENABLESIDEDOCK, mEnableSideDock);
		values.put(DesktopTable.SIDEDOCKPOSITION, mSideDockPosition);
		values.put(DesktopTable.ENABLEMARGINS, mEnableMargins);

	}

	/**
	 * 解析数据
	 * 
	 * @param cursor
	 *            数据集
	 */
	public boolean parseFromCursor(Cursor cursor) {
		if (null == cursor) {
			return false;
		}

		boolean bData = cursor.moveToFirst();
		if (bData) {
			int backgroundpattemswitchIndex = cursor
					.getColumnIndex(DesktopTable.BACKGROUNDPATTEMSWITCH);
			int backgroundpattemPathIndex = cursor.getColumnIndex(DesktopTable.BACKGROUNDPATTEM);
			int backgroundpattemIdIndex = cursor.getColumnIndex(DesktopTable.BACKGROUNDPATTEMID);
			int backgroundpattemTypeIndex = cursor
					.getColumnIndex(DesktopTable.BACKGROUNDPATTEMTYPE);
			int autostatusbarIndex = cursor.getColumnIndex(DesktopTable.AUTOSTATUSBAR);
			int showtitleIndex = cursor.getColumnIndex(DesktopTable.SHOWTITLE);
			int rowIndex = cursor.getColumnIndex(DesktopTable.ROW);
			int columnIndex = cursor.getColumnIndex(DesktopTable.COLUMN);
			int styleIndex = cursor.getColumnIndex(DesktopTable.STYLE);
			int themeIconStyleIndex = cursor.getColumnIndex(DesktopTable.THEMEICONSTYLE);
			int themeIconPackageIndex = cursor.getColumnIndex(DesktopTable.THEMEICONPACKAGE);
			int autofitIndex = cursor.getColumnIndex(DesktopTable.AUTOFITITEMS);
			int titlestyleIndex = cursor.getColumnIndex(DesktopTable.TITLESTYLE);
			int customAppBgIndex = cursor.getColumnIndex(DesktopTable.CUSTOMAPPBG);
			int pressColorIndex = cursor.getColumnIndex(DesktopTable.PRESSCOLOR);
			int focusColorIndex = cursor.getColumnIndex(DesktopTable.FOCUSCOLOR);
			int largeIconIndex = cursor.getColumnIndex(DesktopTable.LARGEICON);
			int folderIconPackageIndex = cursor.getColumnIndex(DesktopTable.FOLDERTHEMEICONPACKAGE);
			int ggmenuIconPackageIndex = cursor.getColumnIndex(DesktopTable.GGMENUTHEMEICONPACKAGE);
			int iconSizeIndex = cursor.getColumnIndex(DesktopTable.ICONSIZE);
			int showIconBaseIndex = cursor.getColumnIndex(DesktopTable.SHOWICONBASE);
			int fontSizeIndex = cursor.getColumnIndex(DesktopTable.FONTSIZE);
			int customTitleColorIndex = cursor.getColumnIndex(DesktopTable.CUSTOMTITLECOLOR);
			int titleColorIndex = cursor.getColumnIndex(DesktopTable.TITLECOLOR);
			int enableSideDock = cursor.getColumnIndex(DesktopTable.ENABLESIDEDOCK);
			int sideDockPosition = cursor.getColumnIndex(DesktopTable.SIDEDOCKPOSITION);
			int enablemargins = cursor.getColumnIndex(DesktopTable.ENABLEMARGINS);

			if (-1 == backgroundpattemswitchIndex || -1 == backgroundpattemPathIndex
					|| -1 == backgroundpattemIdIndex || -1 == backgroundpattemTypeIndex
					|| -1 == autostatusbarIndex || -1 == showtitleIndex || -1 == rowIndex
					|| -1 == columnIndex || -1 == styleIndex || -1 == themeIconStyleIndex
					|| -1 == themeIconPackageIndex || -1 == autofitIndex || -1 == titlestyleIndex
					|| -1 == customAppBgIndex || -1 == pressColorIndex || -1 == focusColorIndex
					|| -1 == largeIconIndex || -1 == folderIconPackageIndex
					|| -1 == ggmenuIconPackageIndex || -1 == iconSizeIndex
					|| -1 == showIconBaseIndex || -1 == fontSizeIndex
					|| -1 == customTitleColorIndex || -1 == titleColorIndex || -1 == enableSideDock
					|| -1 == sideDockPosition || -1 == enablemargins) {
				return false;
			}

			mShowPattem = ConvertUtils.int2boolean(cursor.getInt(backgroundpattemswitchIndex));
			mPattem = cursor.getString(backgroundpattemPathIndex);
			mPattemId = cursor.getInt(backgroundpattemIdIndex);
			mPattemType = cursor.getInt(backgroundpattemTypeIndex);
			mShowStatusbar = ConvertUtils.int2boolean(cursor.getInt(autostatusbarIndex));
			mShowTitle = ConvertUtils.int2boolean(cursor.getInt(showtitleIndex));
			mRow = cursor.getInt(rowIndex);
			mColumn = cursor.getInt(columnIndex);
			mStyle = cursor.getInt(styleIndex);
			mThemeIconStyleSwitch = ConvertUtils.int2boolean(cursor.getInt(themeIconStyleIndex));
			mThemeIconStylePackage = cursor.getString(themeIconPackageIndex);
			mAutofit = ConvertUtils.int2boolean(cursor.getInt(autofitIndex));
			mTitleStyle = cursor.getInt(titlestyleIndex);
			mCustomAppBg = ConvertUtils.int2boolean(cursor.getInt(customAppBgIndex));
			mPressColor = cursor.getInt(pressColorIndex);
			mFocusColor = cursor.getInt(focusColorIndex);
			mLargeIcon = ConvertUtils.int2boolean(cursor.getInt(largeIconIndex));
			mFolderThemeIconStylePackage = cursor.getString(folderIconPackageIndex);
			mGGmenuThemeIconStylePackage = cursor.getString(ggmenuIconPackageIndex);
			mIconSize = cursor.getInt(iconSizeIndex);
			mShowIconBase = ConvertUtils.int2boolean(cursor.getInt(showIconBaseIndex));
			mFontSize = cursor.getInt(fontSizeIndex);
			mCustomTitleColor = ConvertUtils.int2boolean(cursor.getInt(customTitleColorIndex));
			mTitleColor = cursor.getInt(titleColorIndex);
			
			mEnableSideDock =  ConvertUtils.int2boolean(cursor.getInt(enableSideDock));
			mSideDockPosition = cursor.getInt(sideDockPosition);
//			
			mEnableMargins = ConvertUtils.int2boolean(cursor.getInt(enablemargins));
		}
		return bData;
	}

	// 特殊处理风格对应行列数
	// 映射数组
	// 1. 索引
	// 2. 行
	// 3. 列
	private final int mIndexrowcolumn[][] = { { 0, 4, 4 }, { 1, 4, 4 }, { 2, 5, 4 }, { 3, 5, 5 } };

	public void setRows(int value) {
		int style = value;
		if (value < 0 || value > 3) {
			style = 1;
		}
		int record[] = mIndexrowcolumn[style];
		mRow = record[1];
	}

	public void setColumns(int value) {
		int style = value;
		if (value < 0 || value > 3) {
			style = 1;
		}
		int record[] = mIndexrowcolumn[style];
		mColumn = record[2];
	}

	// 获取行数
	public int getRows() {
		int row = mRow;
		if (mRow < 3 || mRow > 10) {
			row = 4;
		}
		return row;
	}

	// 获取列数
	public int getColumns() {
		int column = mColumn;
		if (mColumn < 3 || mColumn > 10) {
			column = 4;
		}
		return column;
	}

	// 获取程序名字是否显示
	public boolean isShowTitle() {
		if (mTitleStyle == 0 || mTitleStyle == 1) {
			return true;
		}
		return false;
	}

	// 获取透明状态
	public boolean isTransparentBg() {
		if (mTitleStyle == 1) {
			return true;
		}
		return false;
	}

	public void setReload(boolean reload) {
		mReload = reload;
	}

	public boolean isReload() {
		return mReload;
	}

	public int getIconSize() {
		final Context context = ApplicationProxy.getContext();
		if (context != null) {
			final int min = (int) (context.getResources().getDimensionPixelSize(
					R.dimen.screen_icon_size) / 1.5f);
			final int max = context.getResources().getDimensionPixelSize(
					R.dimen.screen_icon_large_size);

			if (mIconSize >= min && mIconSize <= max) {
				return mIconSize;
			}
		}
		return 0;
	}

	public void setIconSizeStyle(int style) {
		if (style == DeskSettingVisualIconTabView.LARGE_ICON_SIZE) {
			mLargeIcon = true;
		} else {
			mLargeIcon = false;
		}
	}

	public int getIconSizeStyle() {
		final Context context = ApplicationProxy.getContext();
		if (context != null) {
			final int min = (int) (context.getResources().getDimensionPixelSize(
					R.dimen.screen_icon_size) / 1.5f);

			if (mLargeIcon) {
				return DeskSettingVisualIconTabView.LARGE_ICON_SIZE;
			}
			// 这里还要判断mIconSize>max是因为，现在的自定义图标大小的值是根据机器的屏幕宽度动态计算出来的，所以有可能大于配置文件里的最大值
			else if (mIconSize >= min) {
				return DeskSettingVisualIconTabView.DIY_ICON_SIZE;
			}
		}
		return DeskSettingVisualIconTabView.DEFAULT_ICON_SIZE;
	}

	public int getFontSize() {
		if (mFontSize <= 0) {
			return 12;
		} else {
			return mFontSize;
		}
	}

	public void setFontSize(int size) {
		mFontSize = size;
	}

	public int getFontSizeStyle() {
		if (mFontSize == 0) {
			return DeskSettingVisualFontTabView.DEFAULT_FONT_SIZE;
		} else {
			return DeskSettingVisualFontTabView.DIY_FONT_SIZE;
		}
	}
	
	/**
	 * 设置是否显示边距
	 * @param enable
	 */
	public void setMarginEnable(boolean enable) {
		mEnableMargins = enable;
	}
	
	/**
	 * 获取是否显示边距
	 * @return
	 */
	public boolean getMarginEnable() {
		return mEnableMargins;
	}
	
	/**
	 * 设置图标自适应
	 * @param autoFit
	 */
	public void setAutoFit(boolean autoFit) {
		mAutofit = autoFit;
	}
	
	/**
	 * 获取图标自适应
	 * 这个函数会在设置页面用
	 * 其他地方(如celllayout，workspace)一律用getAutoFitWithMargin()
	 * @return
	 */
	public boolean getAutoFit() {
		return mAutofit;
	}
	
	/**
	 * 根据是否显示边距获取图标自适应值，显示边距的话图标也需要自适应
	 * @return
	 */
	public boolean getAutoFitWithMargin() {
		if (mEnableMargins) {
			return true;
		}
		return mAutofit;
	}
	/**
	 * 获取桌面图标的实际大小（值）
	 * 
	 * @param context
	 * @return
	 */
	public int getIconRealSize() {
		return getIconRealSize(getIconSizeStyle());
	}// end getIconRealSize

	public int getIconRealSize(int style) {
		final Context context = ApplicationProxy.getContext();
		int realSize = 72;
		if (context != null) {
			final Resources resources = context.getResources();
			realSize = (int) resources.getDimension(R.dimen.screen_icon_size);
			if (style == DeskSettingVisualIconTabView.LARGE_ICON_SIZE) {
				realSize = (int) resources.getDimension(R.dimen.screen_icon_large_size);
			} else if (Machine.isLephone()) {
				realSize = Machine.LEPHONE_ICON_SIZE;
			} else if (style == DeskSettingVisualIconTabView.DIY_ICON_SIZE) {
				// 自定义的尺寸
				realSize = mIconSize;
			}
		}
		return realSize;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mShowPattem ? 1 : 0);
		dest.writeString(mPattem);
		dest.writeInt(mPattemId);
		dest.writeInt(mPattemType);
		dest.writeInt(mShowStatusbar ? 1 : 0);
		dest.writeInt(mShowTitle ? 1 : 0);
		dest.writeInt(mRow);
		dest.writeInt(mColumn);
		dest.writeInt(mStyle);
		dest.writeInt(mThemeIconStyleSwitch ? 1 : 0);
		dest.writeString(mThemeIconStylePackage);
		dest.writeInt(mAutofit ? 1 : 0);
		dest.writeInt(mTitleStyle);
		dest.writeInt(mCustomAppBg ? 1 : 0);
		dest.writeInt(mPressColor);
		dest.writeInt(mFocusColor);
		dest.writeInt(mLargeIcon ? 1 : 0);
		dest.writeString(mFolderThemeIconStylePackage);
		dest.writeString(mGGmenuThemeIconStylePackage);
		dest.writeInt(mIconSize);
		dest.writeInt(mShowIconBase ? 1 : 0);
		dest.writeInt(mFontSize);
		dest.writeInt(mCustomTitleColor ? 1 : 0);
		dest.writeInt(mTitleColor);		
		dest.writeInt(mEnableSideDock ? 1 : 0);
		dest.writeInt(mSideDockPosition);
		dest.writeInt(mEnableMargins ? 1 : 0);
	}

	public DesktopSettingInfo(Parcel in) {
		mShowPattem = ConvertUtils.int2boolean(in.readInt());
		mPattem = in.readString();
		mPattemId = in.readInt();
		mPattemType = in.readInt();
		mShowStatusbar = ConvertUtils.int2boolean(in.readInt());
		mShowTitle = ConvertUtils.int2boolean(in.readInt());
		mRow = in.readInt();
		mColumn = in.readInt();
		mStyle = in.readInt();
		mThemeIconStyleSwitch = ConvertUtils.int2boolean(in.readInt());
		mThemeIconStylePackage = in.readString();
		mAutofit = ConvertUtils.int2boolean(in.readInt());
		mTitleStyle = in.readInt();
		mCustomAppBg = ConvertUtils.int2boolean(in.readInt());
		mPressColor = in.readInt();
		mFocusColor = in.readInt();
		mLargeIcon = ConvertUtils.int2boolean(in.readInt());
		mFolderThemeIconStylePackage = in.readString();
		mGGmenuThemeIconStylePackage = in.readString();
		mIconSize = in.readInt();
		mShowIconBase = ConvertUtils.int2boolean(in.readInt());
		mFontSize = in.readInt();
		mCustomTitleColor = ConvertUtils.int2boolean(in.readInt());
		mTitleColor = in.readInt();		
		mEnableSideDock =  ConvertUtils.int2boolean(in.readInt());
		mSideDockPosition = in.readInt();
		mEnableMargins = ConvertUtils.int2boolean(in.readInt());
	}

	public static final Parcelable.Creator<DesktopSettingInfo> CREATOR = new Parcelable.Creator<DesktopSettingInfo>() {
		@Override
		public DesktopSettingInfo createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回DownloadTask对象
			return new DesktopSettingInfo(source);
		}

		@Override
		public DesktopSettingInfo[] newArray(int size) {
			return new DesktopSettingInfo[size];
		}
	};
}
