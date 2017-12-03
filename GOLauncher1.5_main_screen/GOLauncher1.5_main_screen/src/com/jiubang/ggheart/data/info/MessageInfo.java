package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;

import com.go.util.ConvertUtils;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageBaseBean;
import com.jiubang.ggheart.data.tables.MessageCenterTable;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2012-10-16]
 */
public class MessageInfo {

	public String mId;
	public String mTitle;
	public boolean misReaded;
	public String mTimeStamp;
	public int mViewType;
	public int mType;
	public String mUrl;
	public String mStartTime;
	public String mEndTime;
	public String mIconUrl;
	public String mIntro;
	public int mActtype;
	public String mActValue;
	public String mZIcon1;
	public String mZIcon2;
	public int mZpos;
	public long mZtime;
	public boolean mIsClosed;	//罩子层是否有关闭功能
	public String mFilterPkgs;
	public boolean mClickClosed; //点击过罩子层的关闭按钮
	public int mDynamic; //图片是否动态
	public int mIconpos; //图片放置位置
	public String mFullScreenIcon; //全屏图url
	public boolean mIsRemoved;
	public String mWhiteList;
	public int mIsNew;
	public int mIsWifi; //是否在wifi下展示
	public String mMsgOutDateTime; //消息过期时间
	public boolean mIsDeleted; //是否已删除过
	public String mCoupon; //优惠卷促销卷
	public String mPromotion; //促销卷信息
	public String mInstruct; //指令信息
	public String mPackageName; //第三方推荐应用包名
	public String mMapId;
	public int mAddact;
	public int mIsadv; // 是否为广告消息 4.15新增
	public String mMsgimgurl; // 配合ui5.0改版，消息中心增加msgimgurl(消息中心展示图及通知栏大图共用)字段下发

	public MessageInfo() {
		misReaded = false;
		mClickClosed = false;
		mIsRemoved = false;
		mActtype = -1;
		mZpos = -1;
		mZtime = 60 * 1000;
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
		values.put(MessageCenterTable.ID, mId);
		values.put(MessageCenterTable.TITLE, mTitle);
		values.put(MessageCenterTable.READED, ConvertUtils.boolean2int(misReaded));
		values.put(MessageCenterTable.TYPE, mType);
		values.put(MessageCenterTable.VIEWTYPE, mViewType);
		values.put(MessageCenterTable.DATE, mTimeStamp);
		values.put(MessageCenterTable.URL, mUrl);
		values.put(MessageCenterTable.STIME_START, mStartTime);
		values.put(MessageCenterTable.STIME_END, mEndTime);
		values.put(MessageCenterTable.ICON, mIconUrl);
		values.put(MessageCenterTable.INTRO, mIntro);
		values.put(MessageCenterTable.ACTTYPE, mActtype);
		values.put(MessageCenterTable.ACTVALUE, mActValue);
		values.put(MessageCenterTable.ZICON1, mZIcon1);
		values.put(MessageCenterTable.ZICON2, mZIcon2);
		values.put(MessageCenterTable.ZPOS, mZpos);
		values.put(MessageCenterTable.ZTIME, mZtime);
		values.put(MessageCenterTable.ISCLOSED, ConvertUtils.boolean2int(mIsClosed));
		values.put(MessageCenterTable.FILTER_PKGS, mFilterPkgs);
		values.put(MessageCenterTable.CLICK_CLOSE, ConvertUtils.boolean2int(mClickClosed));
		values.put(MessageCenterTable.DYNAMIC, mDynamic);
		values.put(MessageCenterTable.ICONPOS, mIconpos);
		values.put(MessageCenterTable.FULL_SCREEN_ICON, mFullScreenIcon);
		values.put(MessageCenterTable.REMOVED, ConvertUtils.boolean2int(mIsRemoved));
		values.put(MessageCenterTable.WHITE_LIST, mWhiteList);
		values.put(MessageCenterTable.ISWIFI, mIsWifi);
		values.put(MessageCenterTable.OUTDATE, mMsgOutDateTime);
		values.put(MessageCenterTable.DELETED, ConvertUtils.boolean2int(mIsDeleted));
		values.put(MessageCenterTable.COUPON, mCoupon);
		values.put(MessageCenterTable.PROMOTION, mPromotion);
		values.put(MessageCenterTable.INSTRUCT, mInstruct);
		values.put(MessageCenterTable.PACKAGENAME, mPackageName);
		values.put(MessageCenterTable.MAPID, mMapId);
		values.put(MessageCenterTable.ADDACT, mAddact);
		values.put(MessageCenterTable.ISADV, mIsadv);
		values.put(MessageCenterTable.MSGIMGURL, mMsgimgurl);
	}

	public void parseFromCursor(Cursor cursor) {
		if (null == cursor) {
			return;
		}
		try {
			int idIndex = cursor.getColumnIndex(MessageCenterTable.ID);
			int titleIndex = cursor.getColumnIndex(MessageCenterTable.TITLE);
			int readedIndex = cursor.getColumnIndex(MessageCenterTable.READED);
			int typeIndex = cursor.getColumnIndex(MessageCenterTable.TYPE);
			int viewTypeIndex = cursor.getColumnIndex(MessageCenterTable.VIEWTYPE);
			int dateIndex = cursor.getColumnIndex(MessageCenterTable.DATE);
			int urlIndex = cursor.getColumnIndex(MessageCenterTable.URL);
			int startIndex = cursor.getColumnIndex(MessageCenterTable.STIME_START);
			int endIndex = cursor.getColumnIndex(MessageCenterTable.STIME_END);
			int iconIndex = cursor.getColumnIndex(MessageCenterTable.ICON);
			int introIndex = cursor.getColumnIndex(MessageCenterTable.INTRO);
			int acttypeIndex = cursor.getColumnIndex(MessageCenterTable.ACTTYPE);
			int actvalueIndex = cursor.getColumnIndex(MessageCenterTable.ACTVALUE);
			int zicon1Index = cursor.getColumnIndex(MessageCenterTable.ZICON1);
			int zicon2Index = cursor.getColumnIndex(MessageCenterTable.ZICON2);
			int posIndex = cursor.getColumnIndex(MessageCenterTable.ZPOS);
			int ztimeIndex = cursor.getColumnIndex(MessageCenterTable.ZTIME);
			int isCloseIndex = cursor.getColumnIndex(MessageCenterTable.ISCLOSED);
			int filterPkgsIndex = cursor.getColumnIndex(MessageCenterTable.FILTER_PKGS);
			int clickClosedIndex = cursor.getColumnIndex(MessageCenterTable.CLICK_CLOSE);
			int dynamicIndex = cursor.getColumnIndex(MessageCenterTable.DYNAMIC);
			int iconposIndex = cursor.getColumnIndex(MessageCenterTable.ICONPOS);
			int fullScreenIconIndex = cursor.getColumnIndex(MessageCenterTable.FULL_SCREEN_ICON);
			int removedIndex = cursor.getColumnIndex(MessageCenterTable.REMOVED);
			int whitelistIndex = cursor.getColumnIndex(MessageCenterTable.WHITE_LIST);
			int isWifiIndex = cursor.getColumnIndex(MessageCenterTable.ISWIFI);
			int outdateIndex = cursor.getColumnIndex(MessageCenterTable.OUTDATE);
			int deleteIndex = cursor.getColumnIndex(MessageCenterTable.DELETED);
			int couponIndex = cursor.getColumnIndex(MessageCenterTable.COUPON);
			int promotionIndex = cursor.getColumnIndex(MessageCenterTable.PROMOTION);
			int instructIndext = cursor.getColumnIndex(MessageCenterTable.INSTRUCT);
			int packageNameIndext = cursor.getColumnIndex(MessageCenterTable.PACKAGENAME);
			int mapidIndext = cursor.getColumnIndex(MessageCenterTable.MAPID);
			int addactIndext = cursor.getColumnIndex(MessageCenterTable.ADDACT);
			int isadvIndext = cursor.getColumnIndex(MessageCenterTable.ISADV);
			int msgimgurlIndex = cursor.getColumnIndex(MessageCenterTable.MSGIMGURL);

			if (-1 == idIndex || readedIndex == -1 || viewTypeIndex == -1 || readedIndex == -1
					|| typeIndex == -1 || titleIndex == -1 || dateIndex == -1 || urlIndex == -1
					|| startIndex == -1 || endIndex == -1 || iconIndex == -1 || introIndex == -1
					|| acttypeIndex == -1 || actvalueIndex == -1 || zicon1Index == -1
					|| zicon2Index == -1 || posIndex == -1 || isCloseIndex == -1
					|| filterPkgsIndex == -1 || clickClosedIndex == -1 || dynamicIndex == -1
					|| iconposIndex == -1 || fullScreenIconIndex == -1 || removedIndex == -1
					|| whitelistIndex == -1 || isWifiIndex == -1 || outdateIndex == -1
					|| deleteIndex == -1 || couponIndex == -1 || promotionIndex == -1 || instructIndext == -1
					|| packageNameIndext == -1 || mapidIndext == -1 || addactIndext == -1 || isadvIndext == -1 
					|| msgimgurlIndex == -1) {
				return;
			}

			mId = cursor.getString(idIndex);
			misReaded = ConvertUtils.int2boolean(cursor.getInt(readedIndex));
			mType = cursor.getInt(typeIndex);
			mTimeStamp = cursor.getString(dateIndex);
			mViewType = cursor.getInt(viewTypeIndex);
			mTitle = cursor.getString(titleIndex);
			mUrl = cursor.getString(urlIndex);
			mStartTime = cursor.getString(startIndex);
			mEndTime = cursor.getString(endIndex);
			mIconUrl = cursor.getString(iconIndex);
			mIntro = cursor.getString(introIndex);
			mActtype = cursor.getInt(acttypeIndex);
			mActValue = cursor.getString(actvalueIndex);
			mZIcon1 = cursor.getString(zicon1Index);
			mZIcon2 = cursor.getString(zicon2Index);
			mZpos = cursor.getInt(posIndex);
			mIsClosed = ConvertUtils.int2boolean(cursor.getInt(isCloseIndex));
			mFilterPkgs = cursor.getString(filterPkgsIndex);
			mClickClosed = ConvertUtils.int2boolean(cursor.getInt(clickClosedIndex));
			mDynamic = cursor.getInt(dynamicIndex);
			mIconpos = cursor.getInt(iconposIndex);
			mFullScreenIcon = cursor.getString(fullScreenIconIndex);
			mIsRemoved = ConvertUtils.int2boolean(cursor.getInt(removedIndex));
			mWhiteList = cursor.getString(whitelistIndex);
			mIsWifi = cursor.getInt(isWifiIndex);
			mMsgOutDateTime = cursor.getString(outdateIndex);
			mIsDeleted = ConvertUtils.int2boolean(cursor.getInt(deleteIndex));
			//区分优惠卷和促销卷2014-02-25
			/*if (mType == MessageBaseBean.TYPE_COUPON || mType == MessageBaseBean.TYPE_PROMOTION) {*/
			if (mType == MessageBaseBean.TYPE_COUPON) {
				mCoupon = cursor.getString(couponIndex);
			}
			if (mType == MessageBaseBean.TYPE_PROMOTION) {
			    mPromotion = cursor.getString(promotionIndex);
			}
			if (mType == MessageBaseBean.TYPE_INSTRUCT) {
				mInstruct = cursor.getString(instructIndext);
			}
			//后来加的字段
			if (ztimeIndex != -1) {
				mZtime = cursor.getLong(ztimeIndex);
			}
			
			mPackageName = cursor.getString(packageNameIndext);
			mMapId = cursor.getString(mapidIndext);
			mAddact = cursor.getInt(addactIndext);
			mIsadv = cursor.getInt(isadvIndext);
			mMsgimgurl = cursor.getString(msgimgurlIndex);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
