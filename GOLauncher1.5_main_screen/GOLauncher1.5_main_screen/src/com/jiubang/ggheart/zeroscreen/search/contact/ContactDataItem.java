package com.jiubang.ggheart.zeroscreen.search.contact;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Contacts;

import com.jiubang.ggheart.zeroscreen.search.contact.util.PinyinSearchable;

/**
 * <br>类描述：联系人数据实体类
 * <br>功能详细描述：
 *
 * @author xiewenjun
 * @date [2013-9-6]
 *
 */
@SuppressWarnings("deprecation")
//CHECKSTYLE:OFF
public class ContactDataItem extends PinyinSearchable 
	implements Comparable<ContactDataItem>, Parcelable {
	
	/**
	 * 
	 * @author liulixia
	 *
	 */
	public static class PhoneNumber implements Parcelable {
		
		public static final int MOBILE = 0;
		public static final int HOME = 1;
		public static final int WORK = 2;
		public static final int OTHER = 3;
		
		public String number;
		public int type;
		
		private static int getTypeV1(int type) {
			int type2 = OTHER;
			switch (type) {
			case Contacts.Phones.TYPE_MOBILE:
				type2 = MOBILE;
				break;
			case Contacts.Phones.TYPE_HOME:
				type2 = HOME;
				break;
			case Contacts.Phones.TYPE_WORK:
				type2 = WORK;
				break;
			default:
				type2 = OTHER;
				break;
			}
			return type2;
		}
		
		private static int getTypeV2(int type) {
			int type2 = OTHER;
			switch (type) {
			case 2:
				type2 = MOBILE;
				break;
			case 1:
				type2 = HOME;
				break;
			case 3:
				type2 = WORK;
				break;
			default:
				type2 = OTHER;
				break;
			}
			return type2;
		}
		
		public static int getType(int type) {
			if(Build.VERSION.SDK_INT >= 5) {
				return getTypeV2(type);
			}
			return getTypeV1(type);
 		}
		
		@Override
		public String toString() {
			return  number;
		}
		
		/* *************** Parcelable 接口的实现 ******************/
		
		@Override
	    public int describeContents() {
	        return 0;
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        dest.writeInt(type);
	        dest.writeString(number);
	    }
	    
	    public static final Parcelable.Creator<PhoneNumber> CREATOR 
	        = new Parcelable.Creator<PhoneNumber>() {
	    
	        @Override
	        public PhoneNumber createFromParcel(Parcel in) {
	            PhoneNumber item = new PhoneNumber();
	            item.type = in.readInt();
	            item.number = in.readString();
	            return item;
	        }
	    
	        @Override
	        public PhoneNumber[] newArray(int size) {
	            return new PhoneNumber[size];
	        }
	    };
	    
	    /* *************** Parcelable 接口的实现 END ******************/
	}

	public static final long INVALIDID = -1l;
	
	private long mId;
	
	private String mName;
	private ArrayList<PhoneNumber> mPhones = new ArrayList<PhoneNumber>();
	
	// 只在最近联系人时使用
	private boolean mCallLog;
	
	private int mCallCount = 0;
	private String mCallTime = null;
	
	public ContactDataItem() {
		mId = INVALIDID;
		mName = "";
		mFirstLetters = "";
	    mPinyin = ""; 
	    mSortKey = "";
	    mCallLog = false;
	    mCallCount = 0;
	    mCallTime = "";
	}
	
	public ContactDataItem(long id) {
		mId = id;
		mName = "";
		mFirstLetters = "";
	    mPinyin = ""; 
	    mSortKey = "";
	    mCallLog = false;
	    mCallCount = 0;
	    mCallTime = "";
	}
	
	public void setId(long id) {
		if(id <= 0) {
			id = INVALIDID;
		}
		mId = id;
	}
	
	public void setName(String name) {
		if(name == null) {
			name = "";
		}
		mName = name.trim();
	}
	
	public void setCallLog(boolean callLog) {
		mCallLog = callLog;
	}
	
	public boolean isCallLog() {
		return mCallLog;
	}
	
	public void addPhone(PhoneNumber phone) {
		mPhones.add(phone);
	}
	
	public void addPhone(List<PhoneNumber> phones) {
		mPhones.addAll(phones);
	}
	
	public long getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getDisplayName(Context context) {
		if(mName.length() == 0) {
		//	String name = context.getString(R.string.zero_screen_search_local_contact_name_unknown);
			return null;
		}
		return mName;
	}
	
	public int size() {
		int size = mPhones.size();
		return size;
	}
	
	public ArrayList<PhoneNumber> getPhones() {
		return mPhones;
	}
	
	public PhoneNumber getFirstPhone() {
		PhoneNumber phone = null;
		if(size() > 0) {
			phone = mPhones.get(0);
		}
		return phone;
	}
	
	private String mFirstLetters;
    private String mPinyin; 
    private String mSortKey;
    
	@Override
	public String getFirstLetters() {
		return mFirstLetters;
	}

	public void setFirstLetters(String firstLetters) {
		if(firstLetters == null) {
			firstLetters = "";
		}
		mFirstLetters = firstLetters;
	}
	
	@Override
	public String getPinyin() {
		return mPinyin;
	}
	
	public String getCanonicalPinyin() {
    	String pinyin = mPinyin;
    	if(pinyin.length() > 0) {
			char c = pinyin.charAt(0);
			if(c >= '0' && c <= '9') {
				pinyin = "#" + pinyin;
			}
		} else {
			pinyin = "#";
		}
    	return pinyin;
    }
	
	public void setPinyin(String pinyin){
		if(pinyin == null) {
			pinyin = "";
		}
		mPinyin = pinyin;
	}
	
	public String getSortKey() {
		return mSortKey;
	}

	public void setSortKey(String sortKey) {
		if (sortKey == null) {
			sortKey = "";
		}
		if (sortKey.length() > 0) {
			char c = sortKey.charAt(0);
			if (c >= '0' && c <= '9') {
				sortKey = "#" + sortKey;
			}
		} else {
			sortKey = "#";
		}
		mSortKey = sortKey;
	}

	@Override
	public int compareTo(ContactDataItem another) {
		if(another == null) {
			return -1;
		} else if(getSortKey().startsWith("#") && another.getSortKey().startsWith("#")){
			return getSortKey().compareTo(another.getSortKey());
		}else if (getSortKey().startsWith("#")) {
			return 1;
		} else if (another.getSortKey().startsWith("#")) {
			return -1;
		} else {
			return getSortKey().compareTo(another.getSortKey());
		}
	}

	@Override
	public String getSearchField() {
		return getName();
	}
	
	public ContactDataItem deepCopy() {
		ContactDataItem copy = new ContactDataItem(mId);
		copy.setName(new String(mName));
		copy.setCallLog(mCallLog);
		for(PhoneNumber phone : mPhones) {
			//fix the bug: D6CE463D29F9FBCB5352DAA119EC3085
			if(null == phone){
				continue;
			}
			
			PhoneNumber copyPhone = new PhoneNumber();
			copyPhone.number = new String(phone.number);
			copyPhone.type = phone.type;
			copy.addPhone(copyPhone);
			copy.mFirstLetters = new String(mFirstLetters);
			copy.mPinyin = new String(mPinyin);
			copy.mSortKey = new String(mSortKey);
		}
		return copy;
	}
	
	
	/* *************** Parcelable 接口的实现 ******************/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mFirstLetters);
        dest.writeString(mPinyin);
        dest.writeString(mSortKey);
        if(mPhones != null && mPhones.size() > 0) {
            dest.writeInt(mPhones.size());
            for (PhoneNumber phoneNumber : mPhones) {
                dest.writeParcelable(phoneNumber, 0);
            }
        } else {
            dest.writeInt(0);
        }
    }
    
    public static final Parcelable.Creator<ContactDataItem> CREATOR 
        = new Parcelable.Creator<ContactDataItem>() {
    
        @Override
        public ContactDataItem createFromParcel(Parcel in) {
            ContactDataItem item = new ContactDataItem();
            item.readFromParcel(in);
            return item;
        }
    
        @Override
        public ContactDataItem[] newArray(int size) {
            return new ContactDataItem[size];
        }
    };
    
    /**
     * 从指定的{@link Parcel}中读取出所有的数据
     * @param in 数据来源的{@link Parcel}对象
     */
    public void readFromParcel(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mFirstLetters = in.readString();
        mPinyin = in.readString();
        mSortKey = in.readString();
        int numberSize = in.readInt();
        if(numberSize > 0) {
            for (int i = 0; i < numberSize; i++) {
                PhoneNumber phoneNumber = in.readParcelable(PhoneNumber.class.getClassLoader());
                mPhones.add(phoneNumber);
            }
        }
    }

	public int getCallCount() {
		return mCallCount;
	}

	public void setCallCount(int mCallCount) {
		this.mCallCount = mCallCount;
	}

	public String getCallTime() {
		return mCallTime;
	}

	public void setCallTime(String mCallTime) {
		this.mCallTime = mCallTime;
	}
    
    /* *************** Parcelable 接口的实现 END ******************/
}
