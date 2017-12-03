package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.gau.go.launcherex.R;
import com.go.commomidentify.IGoLauncherClassName;
import com.go.proxy.ApplicationProxy;
import com.jiubang.ggheart.apps.desks.imagepreview.ImagePreviewResultType;
import com.jiubang.ggheart.data.AppCore;
import com.jiubang.ggheart.data.tables.ShortcutTable;
import com.jiubang.ggheart.data.theme.DeskThemeControler;
import com.jiubang.ggheart.data.theme.ImageExplorer;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean;
import com.jiubang.ggheart.data.theme.bean.DeskThemeBean.FolderStyle;

/**
 * 后台数据信息
 * 
 * @author ruxueqin
 */
public class DockItemInfo extends BaseItemInfo implements IDatabaseObject {
	public static final int ICONCHANGED = 6; // 图标改变
	public static final int INTENTCHANGED = 7; // Intent改变

	public GestureInfo mGestureInfo;

	public FeatureItemInfo mItemInfo;

	// 排序索引值
	public int mIndex;

	// 在第几行
	private int mRowId = -1;

	// 所在行的第几个图标
	private int mIndexInRow = -1;

	// bitmap　size用于在setIcon时进行对原图片缩放
	private int mBmpSize;

	/*************************************************************************/

	// 源应用数据
	// private AppItemInfo mAppItemInfo;

	// 关联应用源的标记
	// public Intent mIntent;

	// 特色图标
	// public BitmapDrawable mUserIcon; //经过主题、风格变换后的图标
	// 不直接存储图片
	// 根据类型来区分ICON类型
	// 0 : 来自资源
	// 1 : 来自文件
	// 2 : 来自系统默认
	// public int mIconType;
	// public int mIconId;
	// public String mIconPackage; //目标图标的所在主题
	public String mUsePackage; // 设置图标时的当前主题
	// public String mIconPath; //图片名称
	private BitmapDrawable mFolderIcon; // 用来保存folder合成图

	/**
	 * DockItemInfo构造函数
	 * 
	 * @param type
	 *            　IItemType类型
	 * @param bmpSize
	 *            　图标图片size
	 */
	public DockItemInfo(int type, int bmpSize) {
		mGestureInfo = new GestureInfo();

		mItemInfo = (FeatureItemInfo) ItemInfoFactory.createItemInfo(type);
		mItemInfo.registerObserver(this);

		mBmpSize = bmpSize;
	}

	public void setInfo(FeatureItemInfo info) {
		if (null != mItemInfo) {
			mItemInfo.unRegisterObserver(this);
		}

		mItemInfo = info;
		if (null != mItemInfo) {
			mItemInfo.registerObserver(this);
		}
	}

	public int getDockIndex() {
		return mIndex;
	}

	public BitmapDrawable getIcon() {
		return (BitmapDrawable) ((ShortCutInfo) mItemInfo).mIcon;
	}

	@Override
	@SuppressWarnings({ "rawtypes" })
	public void onBCChange(int msgId, int param, Object ...object) {
		super.onBCChange(msgId, param, object);
		switch (msgId) {
			case AppItemInfo.INCONCHANGE :
				broadCast(ICONCHANGED, 0, null, null);
				break;
			case AppItemInfo.UNREADCHANGE : 
				broadCast(AppItemInfo.UNREADCHANGE, param, object);
				break;
	
			default:
				break;
		}
	}

	@Override
	public void readObject(Cursor cursor, String table) {
		mGestureInfo.readObject(cursor, table);
		mItemInfo.readObject(cursor, table);
	}

	@Override
	public void writeObject(ContentValues values, String table) {
		if (ShortcutTable.TABLENAME.equals(table)) {
			mGestureInfo.writeObject(values, table);
			mItemInfo.writeObject(values, table);

			// dock3.0:加入rowid和indexinrow
			values.put(ShortcutTable.ROWSID, mRowId);
			values.put(ShortcutTable.MINDEX, mIndexInRow);
			values.put(ShortcutTable.THEMENAME, IGoLauncherClassName.DEFAULT_THEME_PACKAGE);
		}
	}

	public BitmapDrawable getFolderBackIcon() {
		if (mItemInfo.mFeatureIconType == ImagePreviewResultType.TYPE_DEFAULT) {
			DeskThemeControler themeControler = AppCore.getInstance().getDeskThemeControler();
			FolderStyle folderStyle = null;
			// GO主题类型
			ImageExplorer imageExplorer = ImageExplorer.getInstance(ApplicationProxy.getContext());
			if (themeControler != null /* && themeControler.isUesdTheme() */) {
				DeskThemeBean themeBean = themeControler.getDeskThemeBean();
				if (themeBean != null && themeBean.mScreen != null) {
					folderStyle = themeBean.mScreen.mFolderStyle;
				}
			}
			if (folderStyle != null && folderStyle.mBackground != null) {
				Drawable icon = imageExplorer.getDrawable(folderStyle.mPackageName,
						folderStyle.mBackground.mResName);
				if (icon != null) {
					return (BitmapDrawable) icon;
				}
			}
			return (BitmapDrawable) ApplicationProxy.getContext().getResources()
					.getDrawable(R.drawable.folder_back);
		}
		if (null != mItemInfo.getFeatureIcon()) {
			return (BitmapDrawable) mItemInfo.getFeatureIcon();
		}
		return null;
	}

	public boolean isCustomStyle() {
		if (mItemInfo.mFeatureIconType == ImagePreviewResultType.TYPE_PACKAGE_RESOURCE
				|| mItemInfo.mFeatureIconType == ImagePreviewResultType.TYPE_IMAGE_FILE
				|| mItemInfo.mFeatureIconType == ImagePreviewResultType.TYPE_APP_ICON
				|| mItemInfo.mFeatureIconType == ImagePreviewResultType.TYPE_IMAGE_FILE) {
			return true;
		}
		return false;
	}

	/**
	 * @return the mRowId
	 */
	public int getmRowId() {
		return mRowId;
	}

	/**
	 * @param mRowId
	 *            the mRowId to set
	 */
	public void setmRowId(int mRowId) {
		this.mRowId = mRowId;
	}

	/**
	 * @return the mIndexInRow
	 */
	public int getmIndexInRow() {
		return mIndexInRow;
	}

	/**
	 * @param mIndexInRow
	 *            the mIndexInRow to set
	 */
	public void setmIndexInRow(int mIndexInRow) {
		this.mIndexInRow = mIndexInRow;
	}

	public void setBmpSize(int size) {
		mBmpSize = size;
	}

	public int getBmpSize() {
		return mBmpSize;
	}

	/**
	 * <br>
	 * 功能简述:释放info <br>
	 * 功能详细描述: <br>
	 * 注意:在删除dock图标或减少dock行数时调用
	 */
	public void selfDestruct() {
		if (mItemInfo != null
				&& (mItemInfo.getObserver() == null || mItemInfo.getObserver().size() <= 1)) {
			/*
			 * 如果mItemInfo.getObserver().size() > 1,
			 * 说明这个mItemInfo除了DockItemInfo还有其他监听者，此时不释放mItemInfo
			 */
			mItemInfo.selfDestruct();
		}
		clearAllObserver();
	}
	
	public int getUnreadCount() {
		return mItemInfo.getRelativeItemInfo().getUnreadCount();
	}
}
