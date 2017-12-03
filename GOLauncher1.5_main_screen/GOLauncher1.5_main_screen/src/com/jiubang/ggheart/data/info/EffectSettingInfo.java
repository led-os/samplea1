package com.jiubang.ggheart.data.info;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.go.util.ConvertUtils;
import com.go.util.graphics.effector.united.EffectorControler;
import com.go.util.graphics.effector.united.IEffectorIds;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.data.tables.DynamicEffectTable;
/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2013-5-13]
 */
public class EffectSettingInfo implements Parcelable {
	public boolean mEnable;
	public int mScrollSpeed;
	public int mBackSpeed;
	public int mType;
	public int mEffectorType;
	public boolean mAutoTweakElasticity;
	public int[] mEffectCustomRandomEffects;

	// public int mEffectSelectItem;
	public EffectSettingInfo() {
		mEnable = true;
		mBackSpeed = DeskSettingConstants.SCREEN_CHANGE_SPEED_ELASTIC_DEFAULT;
		mScrollSpeed = DeskSettingConstants.SCREEN_CHANGE_SPEED_DEFAULT;
		mType = 1;
		setDefaultType();
		mAutoTweakElasticity = true;
		mEffectCustomRandomEffects = new int[] { -1 };
	}

	
	public void setDefaultType() {
		mBackSpeed = 10;
		mEffectorType = IEffectorIds.EFFECTOR_TYPE_CARD_FLIP;
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
		values.put(DynamicEffectTable.EABLE, ConvertUtils.boolean2int(mEnable));
		values.put(DynamicEffectTable.SCROLLSPEED, mScrollSpeed);
		values.put(DynamicEffectTable.BACKSPEED, mBackSpeed);
		values.put(DynamicEffectTable.EFFECT, mType);
		
		// 判断特效2D桌面是否支持，不支持则不保存更改（可能是3D特有特效）
		if (EffectorControler.getInstance().getEffectorInfoById(mEffectorType).mIsBothSupprot) {
			values.put(DynamicEffectTable.EFFECTORTYPE, mEffectorType);
		}
		
		values.put(DynamicEffectTable.EFFECTOR_TYPE_FOR_3D, mEffectorType); // 3D目前支持所有2D特效，故不用判断
		values.put(DynamicEffectTable.AUTOTWEAKELASTICITY,
				ConvertUtils.boolean2int(mAutoTweakElasticity));
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < mEffectCustomRandomEffects.length; i++) {
			buffer.append(mEffectCustomRandomEffects[i]);
			buffer.append(";");
		}
		values.put(DynamicEffectTable.EFFECTORRANDOMITEMS, buffer.toString());
		// values.put(DynamicEffectTable.EFFECTORITEM, mEffectSelectItem);
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
			int enableIndex = cursor.getColumnIndex(DynamicEffectTable.EABLE);
			int scrollspeedIndex = cursor.getColumnIndex(DynamicEffectTable.SCROLLSPEED);
			int backspeedIndex = cursor.getColumnIndex(DynamicEffectTable.BACKSPEED);
			int effectIndex = cursor.getColumnIndex(DynamicEffectTable.EFFECT);
			int effectorTypeIndex = cursor.getColumnIndex(DynamicEffectTable.EFFECTOR_TYPE_FOR_3D);
			int tweakElasticityIndex = cursor
					.getColumnIndex(DynamicEffectTable.AUTOTWEAKELASTICITY);
			int effectRandomItemsIndex = cursor
					.getColumnIndex(DynamicEffectTable.EFFECTORRANDOMITEMS);
			// int effectItemIndex =
			// cursor.getColumnIndex(DynamicEffectTable.EFFECTORITEM);

			if (enableIndex >= 0) {
				mEnable = ConvertUtils.int2boolean(cursor.getInt(enableIndex));
			}

			if (scrollspeedIndex >= 0) {
				mScrollSpeed = cursor.getInt(scrollspeedIndex);
			}

			if (backspeedIndex >= 0) {
				mBackSpeed = cursor.getInt(backspeedIndex);

			}

			if (effectIndex >= 0) {
				mType = cursor.getInt(effectIndex);
			}

			if (effectorTypeIndex >= 0) {
				mEffectorType = cursor.getInt(effectorTypeIndex);
			}

			if (tweakElasticityIndex >= 0) {
				mAutoTweakElasticity = ConvertUtils
						.int2boolean(cursor.getInt(tweakElasticityIndex));
			}
			if (effectRandomItemsIndex >= 0) {
				String buff = cursor.getString(effectRandomItemsIndex);
				String[] items = buff.split(";");
				if (items != null) {
					mEffectCustomRandomEffects = new int[items.length];
					for (int i = 0; i < items.length; i++) {
						try {
							mEffectCustomRandomEffects[i] = Integer.valueOf(items[i]);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			// if(effectItemIndex >= 0)
			// {
			// mEffectSelectItem = cursor.getInt(effectItemIndex);
			// }
		}
		return bData;
	}

	// TODO 临时处理
	// 设置 UI 转换
	// 设置0---100
	// UI 0---2000ms
	public int getDuration() {
		int speed = mScrollSpeed;
		if (speed < 0 || speed > 100) {
			speed = 0;
		}
		speed = (int) (50.0f / 60 * speed);
		// return 2000 * (100 - speed) / 100;
		final int duration = speed * speed / 10 - 20 * speed + 1200;
		return duration;
	}

	public int getOvershootAmount() {
		return mBackSpeed / 6; // 从[0, 100]转换为[0, 16]
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	public static final Parcelable.Creator<EffectSettingInfo> CREATOR = new Parcelable.Creator<EffectSettingInfo>() {
		@Override
		public EffectSettingInfo createFromParcel(Parcel source) {
			// 从Parcel中读取数据，返回DownloadTask对象
			return new EffectSettingInfo(source);
		}

		@Override
		public EffectSettingInfo[] newArray(int size) {
			return new EffectSettingInfo[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(mEnable ? 1 : 0);
			dest.writeInt(mScrollSpeed);
			dest.writeInt(mBackSpeed);
			dest.writeInt(mType);
			dest.writeInt(mEffectorType);
			dest.writeInt(mAutoTweakElasticity ? 1 : 0);

			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < mEffectCustomRandomEffects.length; i++) {
				buffer.append(mEffectCustomRandomEffects[i]);
				buffer.append(";");
			}
			dest.writeString(buffer.toString());
	}
	
	public EffectSettingInfo(Parcel in) {
			mEnable = ConvertUtils.int2boolean(in.readInt());
			mScrollSpeed = in.readInt();
			mBackSpeed = in.readInt();
			mType = in.readInt();
			mEffectorType = in.readInt();
			mAutoTweakElasticity = ConvertUtils.int2boolean(in.readInt());
			
			String buff = in.readString();
			if (null == buff) {
				return;
			}
			
			String[] items = buff.split(";");
			if (items != null) {
				mEffectCustomRandomEffects = new int[items.length];
				for (int i = 0; i < items.length; i++) {
					try {
						mEffectCustomRandomEffects[i] = Integer.valueOf(items[i]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
	}
}
