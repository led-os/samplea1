package com.jiubang.ggheart.zeroscreen.search.contact;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.jiubang.ggheart.zeroscreen.search.contact.ContactDataItem.PhoneNumber;

//CHECKSTYLE:OFF
public class SmsContactUtilV2 {
//	private static final String LOG_TAG = "Mms/SmsContactUtilV2";
	
	/** The authority for the contacts provider */
    public static final String AUTHORITY = "com.android.contacts";
    
    /** A content:// style uri to the authority for the contacts provider */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    
    /**
     * The content:// style URI for this table, which requests a directory
     * of data rows matching the selection criteria.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "data");
    
    /**
     * The name of the account instance to which this row belongs, which when paired with
     * {@link #ACCOUNT_TYPE} identifies a specific account.
     * <P>Type: TEXT</P>
     */
    public static final String ACCOUNT_NAME = "account_name";
    
    /**
     * The type of account to which this row belongs, which when paired with
     * {@link #ACCOUNT_NAME} identifies a specific account.
     * <P>Type: TEXT</P>
     */
    public static final String ACCOUNT_TYPE = "account_type";
    
    /**
     * Flag indicating if the contacts belonging to this group should be
     * visible in any user interface.
     * <p>
     * Type: INTEGER (boolean)
     */
    public static final String GROUP_VISIBLE = "group_visible";
    
    /**
     * The content:// style URI for this table
     */
    public static final Uri CONTACTS_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "contacts");
    
    public static final Uri PHONE_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "phones");
    
    public static final Uri GROUP_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "groups");
    
    public static final String COUNT_TYPE_PHONE = "vnd.sec.contact.phone";
    
    public static final String COUNT_TYPE_GOOGLE = "com.google";
    
    /**
     * The display title of this group.
     * <p>
     * Type: TEXT
     */
    public static final String GROUP_TITLE = "title";
    
    /**
     * The ID of this group if it is a System Group, i.e. a group that has a special meaning
     * to the sync adapter, null otherwise.
     * <P>Type: TEXT</P>
     */
    public static final String SYSTEM_ID = "system_id";
    
    /**
     * The content:// style URI for this table, which requests a directory
     * of data rows matching the selection criteria.
     */
    public static final Uri DATA_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "data");
    
    
    public static final Uri EMAIL_CONTENT_URI = Uri.withAppendedPath(DATA_CONTENT_URI,"emails");
    
    /**
     * The content:// style URI for this table. Append the phone number you want to lookup
     * to this URI and query it to perform a lookup. For example:
     * <pre>
     * Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_URI, Uri.encode(phoneNumber));
     * </pre>
     */
    public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(AUTHORITY_URI,"phone_lookup");
    
    public static final String CONTACT_ID = "contact_id";
    
    public static final String MIMETYPE = "mimetype";
    
    /**
     * The content:// style URI for this table
     */
    public static final Uri RAW_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "raw_contacts");
    
    /** MIME type used when storing this in data table. */
    public static final String CONTENT_ADDR_ITEM_TYPE = "vnd.android.cursor.item/postal-address_v2";
    
    /** MIME type used when storing this in data table. */
    public static final String CONTENT_GROUP_ITEM_TYPE = "vnd.android.cursor.item/group_membership";
    
    public static final String CONTENT_PHONE_ITEM_TYPE = "vnd.android.cursor.item/phone_v2";
    
    /** MIME type used when storing this in data table. */
    public static final String EMIL_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/email_v2";
    
    /** MIME type used when storing this in data table. */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/name";
    
    /** MIME type used when storing this in data table. */
    public static final String NOTE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/note";
    
    /** MIME type used when storing this in data table. */
    public static final String WEB_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/website";
        
    /** MIME type used when storing this in data table. */
    public static final String NICKNAME_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/nickname";
    
    /** MIME type used when storing this in data table. */
    public static final String IM_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/im";
    
    /** MIME type used when storing this in data table. */
    public static final String NAME_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/name";
    
    /** MIME type used when storing this in data table. */
    public static final String ORG_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/organization";
    
    /** MIME type used when storing this in data table. */
    public static final String PHOTO_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/photo";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA = "data1";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA1 = "data1";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA2 = "data2";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA3 = "data3";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA4 = "data4";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA5 = "data5";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA7 = "data7";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA8 = "data8";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA9 = "data9";
    
    /** Generic data column, the meaning is {@link #MIMETYPE} specific */
    public static final String DATA10 = "data10";
    
    /**
     * Generic data column, the meaning is {@link #MIMETYPE} specific. By convention,
     * this field is used to store BLOBs (binary data).
     */
    public static final String DATA15 = "data15";
    
    /**
     * The row id of the group that this group membership refers to. Exactly one of
     * this or {@link #GROUP_SOURCE_ID} must be set when inserting a row.
     * <P>Type: INTEGER</P>
     */
    public static final String GROUP_ROW_ID = DATA1;
    
    public static final String NUMBER = DATA;
    
    /**
     * The user defined label for the phone number.
     * <P>Type: TEXT</P>
     */
    public static final String LABEL = DATA3;
    
    /**
     * Can be street, avenue, road, etc. This element also includes the
     * house number and room/apartment/flat/floor number.
     * <p>
     * Type: TEXT
     */
    public static final String STREET = DATA4;
    
    /**
     * Can be city, village, town, borough, etc. This is the postal town
     * and not necessarily the place of residence or place of business.
     * <p>
     * Type: TEXT
     */
    public static final String CITY = DATA7;
    
    /**
     * A state, province, county (in Ireland), Land (in Germany),
     * departement (in France), etc.
     * <p>
     * Type: TEXT
     */
    public static final String REGION = DATA8;
    
    /**
     * Postal code. Usually country-wide, but sometimes specific to the
     * city (e.g. "2" in "Dublin 2, Ireland" addresses).
     * <p>
     * Type: TEXT
     */
    public static final String POSTCODE = DATA9;
    
    /**
     * The name or code of the country.
     * <p>
     * Type: TEXT
     */
    public static final String COUNTRY = DATA10;
    
    /**
     * Thumbnail photo of the raw contact. This is the raw bytes of an image
     * that could be inflated using {@link android.graphics.BitmapFactory}.
     * <p>
     * Type: BLOB
     */
    public static final String PHOTO = DATA15;
    
    /**
     * Covers actual P.O. boxes, drawers, locked bags, etc. This is
     * usually but not always mutually exclusive with street.
     * <p>
     * Type: TEXT
     */
    public static final String POBOX = DATA5;
    
    /**
     * The type of data, for example Home or Work.
     * <P>Type: INTEGER</P>
     */
    public static final String TYPE = DATA2;
    
    /**
     * The given name for the contact.
     * <P>Type: TEXT</P>
     */
    public static final String GIVEN_NAME = DATA2;
    
    /**
     * The name that should be used to display the contact.
     * <i>Unstructured component of the name should be consistent with
     * its structured representation.</i>
     * <p>
     * Type: TEXT
     */
    public static final String DISPLAY_NAME = DATA1;
    
    /**
     * The family name for the contact.
     * <P>Type: TEXT</P>
     */
    public static final String FAMILY_NAME = DATA3;
    
    /**
     * The note text.
     * <P>Type: TEXT</P>
     */
    public static final String NOTE = DATA1;
    
    /**
     * The position title at this company as the user entered it.
     * <P>Type: TEXT</P>
     */
    public static final String TITLE = DATA4;
    
    /**
     * The company as the user entered it.
     * <P>Type: TEXT</P>
     */
    public static final String COMPANY = DATA;
    
    /**
     * The website URL string.
     * <P>Type: TEXT</P>
     */
    public static final String URL = DATA;
    
    /**
     * The name itself
     */
    public static final String NAME = DATA;
    
    public static final long mInvalidContactId = -1; 
    
    /**
     * The unique ID for a row.
     * <P>Type: INTEGER (long)</P>
     */
    public static final String _ID = "_id";
    public static final String R_ID = "raw_contact_id";
    
    public static final String[] ALL_CONTACTS_PROJECTION = {
    	"contact_id",
    	"display_name",
    	"data2",
    	"data1"
    };
    
	public static List<PhoneNumber> getPhoneNumberes(Context context, long contactId, boolean mobileOnly) {
		final String[] projection = { NUMBER, TYPE, LABEL };
		final String selection = CONTACT_ID + " = ? AND " + MIMETYPE + " = ?";
		final String[] args = { String.valueOf(contactId), CONTENT_PHONE_ITEM_TYPE };
		Cursor cursor = null;
		List<PhoneNumber> numbers = new ArrayList<PhoneNumber>();
		try {
			ContentResolver cr = context.getContentResolver();
			cursor = cr.query(PHONE_CONTENT_URI, projection, selection, args, null);
			while (cursor.moveToNext()) {
				String numberStr = cursor.getString(0);
				int type = cursor.getInt(1);
				type = PhoneNumber.getType(type);
				if(mobileOnly && type != PhoneNumber.MOBILE) {
					continue;
				}
				PhoneNumber number = new PhoneNumber();
				number.number = numberStr;
				number.type = type;
				numbers.add(0, number);
			}
		} catch (Exception e) {
			e.printStackTrace();
			numbers.clear();
		} finally {
			if(cursor != null) cursor.close();
		}
		return numbers;
	}
	
	public static ContactDataItem getContactDataItem(Context context, long contactId, boolean mobileOnly) {
		ContactDataItem data = new ContactDataItem(contactId);
		Cursor cursor = null;
		final String[] projection = { DISPLAY_NAME };
		final String selection = CONTACT_ID + " = ? AND " + MIMETYPE + " = ?";
		final String[] args = { String.valueOf(contactId), NAME_CONTENT_ITEM_TYPE };
		try {
			ContentResolver cr = context.getContentResolver();
			cursor = cr.query(CONTENT_URI, projection, selection, args, null);
			cursor.moveToFirst();
			String name = cursor.getString(0);
			data.setName(name);
			List<PhoneNumber> phones = getPhoneNumberes(context, contactId, mobileOnly);
			data.addPhone(phones);
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
		} finally {
			if(cursor != null) cursor.close();
		}
		return data;
	}
	
	public static BitmapDrawable getRoundedCornerContactPhoto(Context context, long contactId) {
		BitmapDrawable photo = null;
		Cursor cursor = null;
		String selection = CONTACT_ID + " = ? AND " + MIMETYPE + " = ?";
		String[] args = { String.valueOf(contactId), PHOTO_CONTENT_ITEM_TYPE };
		try {
			ContentResolver cr = context.getContentResolver();
			cursor = cr.query(CONTENT_URI, null, selection, args, null);
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(cursor.getColumnIndex(PHOTO));
//				photo = BitmapFactory.decodeByteArray(data, 0, data.length);
//				photo = BitmapUtil.getHeaderDrawable(data); 
			}
		} catch (Exception e) {
			photo = null;
		} finally {
			if(cursor != null) cursor.close();
		}
		return photo;
	}
}
