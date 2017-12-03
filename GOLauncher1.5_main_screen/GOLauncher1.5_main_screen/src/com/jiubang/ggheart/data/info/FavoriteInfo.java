package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;

import com.jiubang.ggheart.data.tables.PartToScreenTable;

/**
 * 推荐widget的数据bean
 * @author 
 *
 */
public class FavoriteInfo extends ItemInfo {
	public GoWidgetBaseInfo mWidgetInfo;
	public String mUrl; // 下载地址
	public int mPreview; // 预览图片
	public int mTitleId; // 下载对话框显示的widget名字对应的字符串id
	public String mValidArea;
	public String mInvalidArea;
	public int mDetailId; // 详细描述的字符串id
	public String mClickUrl; // 实时统计的回调链接
	public String mAId; // 实时统计广告id
	public String mMapId; // 实时统计的mapid
	public String mGALink; // GA链接
	public boolean mShowInstall; // 已经安装的话是否显示，默认不显示

	public FavoriteInfo() {
		mItemType = IItemType.ITEM_TYPE_FAVORITE;
		mWidgetInfo = new GoWidgetBaseInfo();
	}

	@Override
	public void writeObject(ContentValues values, String table) {
		super.writeObject(values, table);

		if (table.equals(PartToScreenTable.TABLENAME)) {
			values.put(PartToScreenTable.WIDGETID, mWidgetInfo.mWidgetId);
		}
	}

	@Override
	public void readObject(Cursor cursor, String table) {
		super.readObject(cursor, table);

		if (table.equals(PartToScreenTable.TABLENAME)) {
			mWidgetInfo.mWidgetId = cursor
					.getInt(cursor.getColumnIndex(PartToScreenTable.WIDGETID));
		}
	}
}
