package com.jiubang.ggheart.zeroscreen.search.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.Contacts.Phones;
import android.telephony.PhoneNumberUtils;

import com.gau.go.launcherex.R;
import com.jb.util.pylib.Hanzi2Pinyin;
import com.jiubang.ggheart.zeroscreen.search.contact.ContactDataItem.PhoneNumber;
import com.jiubang.ggheart.zeroscreen.search.contact.pinyin.util.PinyinTool;
import com.jiubang.ggheart.zeroscreen.search.contact.pinyin.util.PinyinTools;

/**
 * <br>类描述：联系人数据缓存
 * <br>功能详细描述：数据的初始化及转拼音操作
 *
 * @author xiewenjun
 * @date [2013-9-6]
 *
 */
@SuppressWarnings("deprecation")
//CHECKSTYLE:OFF
public class ContactsDataCache {
	
	private static final int BUFFER_SIZE = 60;
	
	private List<ContactDataItem> mDataCache;
	private List<ContactDataItem> mBuffer;
	private HashMap<String, String> mDataBuffer;
	
	// 是否已经启动过了
	private boolean mIsStarted = false;
	// 联系人数据是否已经加载完毕
	private boolean mIsDataReady = false;
	
	private List<OnContactsChangeListener> mListeners;
	
	private Context mContext;
	private ContentResolver mContentResolver;
	private Hanzi2Pinyin mHanzi2Pinyin;
	
	private Thread mWorkThread;
	private Handler mHandler;
	private ContactsOberver mObserver;
	
	private boolean mMobileOnly = false;

	private static ContactsDataCache sInstance;
	public synchronized static ContactsDataCache getInstance() {
//		if (sInstance == null) {
//			throw new RuntimeException("ContactsDataCache hasn't been initialized");
//		}
		return sInstance;
	}
	
	public synchronized static void init(Context context) {
		if (sInstance == null) {
			sInstance = new ContactsDataCache(context);
		}
	}
	
	private ContactsDataCache(Context context) {
		mDataCache = new ArrayList<ContactDataItem>();
		mBuffer = new ArrayList<ContactDataItem>(BUFFER_SIZE);
		mDataBuffer = new HashMap<String, String>();
		
		mListeners = new ArrayList<OnContactsChangeListener>();
		
		mContext = context.getApplicationContext();
		mContentResolver = mContext.getContentResolver();
		
		mHanzi2Pinyin = Hanzi2Pinyin.getInstance(mContext, R.raw.unicode2pinyin);
		
		mHandler = new Handler();
		
		monitorContactsChange();
	}
	
	public synchronized void destroy() {
		mDataCache.clear();
		mDataCache = null;
		
		mBuffer.clear();
		mBuffer = null;
		
		mDataBuffer.clear();
		mDataBuffer = null;
		
		mListeners.clear();
		mListeners = null;
		
		mContext = null;
		
		if(mHanzi2Pinyin != null) {			
			mHanzi2Pinyin.ReleaseLib();
			mHanzi2Pinyin = null;
		}
		
		mWorkThread = null;
	    mHandler = null;
		
		cancelMonitorContactsChange();
		mContentResolver = null;
		
		sInstance = null;
	}
	
	public void addContactChangeListener(OnContactsChangeListener listener) {
		synchronized (mListeners) {
			mListeners.remove(listener);
			mListeners.add(listener);
		}
	}
	
	public void removeContactChangeListener(OnContactsChangeListener listener) {
		synchronized (mListeners) {
			mListeners.remove(listener);
		}
	}
	
	private void broadcastChange(final boolean finish) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mListeners) {
					for(OnContactsChangeListener l : mListeners) {
						l.onContactsChange(finish);
					}
				}
			}
		});
	}
	
	// 这里返回的是一个深度拷贝
	public ArrayList<ContactDataItem> getContacts() {
		ArrayList<ContactDataItem> list = null;
		synchronized (mDataCache) {			
			list = deepCopyContacts(mDataCache);
		}
		return list;
	}
	
	private ArrayList<ContactDataItem> deepCopyContacts(List<ContactDataItem> contacts) {
		ArrayList<ContactDataItem> list = new ArrayList<ContactDataItem>(contacts.size());
		for(ContactDataItem contact : contacts) {
			ContactDataItem copy = contact.deepCopy();
			list.add(copy);
		}
		return list;
	}
	
	public void startRead(boolean background) {
		mIsStarted = true;
		
		// TODO: 这个策略有漏洞
		if(mWorkThread != null && mWorkThread.isAlive()) {
			return;
		}
		
		if(mIsDataReady) {
			broadcastChange(true);
			return;
		}
		
		mWorkThread = new Thread(new Runnable() {
			public void run() {
				read();
				mIsDataReady = true;
			}
		});
		
		int priority = Thread.currentThread().getPriority() - 1;
		if(background || priority < Thread.MIN_PRIORITY) {
			priority = Thread.MIN_PRIORITY;
		}
		mWorkThread.setPriority(priority);
		mWorkThread.start();
	}
	
	public void setWorkThreadPriority(int priority) {
		if (mWorkThread != null && mWorkThread.isAlive()) {
			if(priority < Thread.MIN_PRIORITY) priority = Thread.MIN_PRIORITY;
			if(priority > Thread.MAX_PRIORITY) priority = Thread.MAX_PRIORITY;
			mWorkThread.setPriority(priority);
		}
	}
	
	private Cursor getContactCursor() {
		String sortOrder = "display_name" + " COLLATE LOCALIZED ASC";
		Cursor cursor = null;
		try {
			if (Build.VERSION.SDK_INT >= 5) {
				cursor = mContentResolver.query(SmsContactUtilV2.PHONE_CONTENT_URI,
						SmsContactUtilV2.ALL_CONTACTS_PROJECTION,
						null, null, sortOrder);
			} else {
				final String[] projection = {
						Phones.PERSON_ID, Phones.DISPLAY_NAME,
				    	Phones.TYPE, Phones.NUMBER};
				cursor = mContentResolver.query(Contacts.Phones.CONTENT_URI, 
						projection, null, null, sortOrder);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	private void read() {
		Cursor cursor = null;
		try {
			cursor = getContactCursor();
			inflate(cursor);
		} catch (Exception e) {
			e.printStackTrace();
			broadcastChange(true);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	private void monitorContactsChange() {
		mObserver = new ContactsOberver(mHandler);
		if( Build.VERSION.SDK_INT >= 5 ) {
			mContentResolver.registerContentObserver(SmsContactUtilV2.PHONE_CONTENT_URI, true, mObserver);  
		} else{
			mContentResolver.registerContentObserver(Contacts.Phones.CONTENT_URI, true, mObserver);  
		}
	}
	
	private void cancelMonitorContactsChange() {
		mContentResolver.unregisterContentObserver(mObserver);
		mObserver = null;
	}
	
	private void inflate(Cursor cursor) {
		mBuffer.clear();
		synchronized (mDataCache) {
			mDataCache.clear();
		}
		
		while (cursor.moveToNext()) {
			ContactDataItem data = null;
			if( Build.VERSION.SDK_INT >= 5 ) {
				data = buildV2(cursor);
			} else{
				data = buildV1(cursor);
			}
			if(data == null) continue;
//			Log.d("llx", "inflate...name = " + data.getName() + ", number = " + data.getPhones().get(0).number + ",pinyin=" + data.getPinyin()+", sortkey=" + data.getSortKey());
			addToList(mBuffer, data);
			if(mBuffer.size() == BUFFER_SIZE) {
				commitBuffer();
				broadcastChange(false);
			}
		}
		
		mDataBuffer.clear();
		commitBuffer();
		broadcastChange(true);
	}
	
	private void commitBuffer() {
		synchronized (mDataCache) {
			addToList(mDataCache, mBuffer);
			Collections.sort(mDataCache);
		}
		mBuffer.clear();
	}
	
	private ContactDataItem buildV1(Cursor cursor) {
		long id = cursor.getLong(0);
		String name = cursor.getString(1);
		int type = cursor.getInt(2);
		type = PhoneNumber.getType(type);
		String number = cursor.getString(3);
		
		if(id <= 0 || number == null || number.trim().length() == 0) {
			return null;
		}
		
		if(mMobileOnly && type != PhoneNumber.MOBILE) {
			return null;
		}
		
		number = number.replace("-", "").replace(" ", "");
		//TODO:过滤相同名字和号码的联系人选项 xwj
		if(mDataBuffer.containsKey(number)){
			if(name.equals(mDataBuffer.get(number)))
				return null;
		}
		mDataBuffer.put(number, name);
		//TODO:end
		
		ContactDataItem data = new ContactDataItem();
		data.setId(id);
		data.setName(name);
		// TODO: 检查一下这里
		if (null != mHanzi2Pinyin/* && mHanzi2Pinyin.getIsLoadOk()*/) {
			PinyinTools.converterToSpell(data);
		} else {
			data.setFirstLetters(PinyinTool.converterToFirstSpell(name));
			data.setPinyin(PinyinTool.converterToSpell(name, data));
		}
		PhoneNumber phone = new PhoneNumber();
		phone.number = number;
		phone.type = type;
		data.addPhone(phone);
		return data;
	}
	
	private ContactDataItem buildV2(Cursor cursor) {
		long id = cursor.getLong(0);
		String name = cursor.getString(1);
		int type = cursor.getInt(2);
		type = PhoneNumber.getType(type);
		String number = cursor.getString(3);
		
		if(id <= 0 || number == null || number.trim().length() == 0) {
			return null;
		}
		
		if(mMobileOnly && type != PhoneNumber.MOBILE) {
			return null;
		}
		
		number = number.replace("-", "").replace(" ", "");
		//TODO:过滤相同名字和号码的联系人选项 xwj
		if(mDataBuffer.containsKey(number)){
			if(name.equals(mDataBuffer.get(number)))
				return null;
		}
		mDataBuffer.put(number, name);
		//TODO:end
		ContactDataItem data = new ContactDataItem();
		data.setId(id);
		data.setName(name);
		// TODO: 检查一下这里
		if (null != mHanzi2Pinyin/* && mHanzi2Pinyin.getIsLoadOk()*/) {
			PinyinTools.converterToSpell(data);
		} else {
			data.setFirstLetters(PinyinTool.converterToFirstSpell(name));
			data.setPinyin(PinyinTool.converterToSpell(name, data));
		}
		PhoneNumber phone = new PhoneNumber();
		phone.number = number;
		phone.type = type;
		data.addPhone(phone);
		return data;
	}
	
	private void addToList(List<ContactDataItem> list, ContactDataItem contact) {
		ContactDataItem data = findContactInListById(list, contact.getId());
//		if(data != null) {
//			ArrayList<PhoneNumber> phones = data.getPhones();
//			ArrayList<PhoneNumber> phones2 = contact.getPhones();
//			for (int i = 0; i < phones2.size(); i++) {
//				PhoneNumber number = phones2.get(i);
//				if(isNumberInList(phones, number.number)) {
//					continue;
//				}
//				phones.add(number);
//			}	
//		} else {
//			list.add(contact);
//		}
		if (data == null) {
			list.add(contact);
		}
	}
	
	private boolean isNumberInList(ArrayList<PhoneNumber> phones, String number) {
		for(PhoneNumber phone : phones) {
			if(PhoneNumberUtils.compare(number, phone.number)) {
				return true;
			}
		}
		return false;
	}
	
	private void addToList(List<ContactDataItem> list, List<ContactDataItem> another) {
		for(ContactDataItem item : another) {
			addToList(list, item);
		}
	}
	
	private ContactDataItem findContactInListById(List<ContactDataItem> list, long id) {
		if(id <= 0) return null;
		for(ContactDataItem item : list) {
			if(item.getId() == id) {
				return item;
			}
		}
		return null;
	}
	
	private ContactDataItem findContactInListByNumber(List<ContactDataItem> list, String number) {
		if(number == null || number.trim().length() == 0) {
			return null;
		}
		for(ContactDataItem item : list) {
			List<PhoneNumber> phones = item.getPhones();
			for(PhoneNumber phone : phones) {
				if(PhoneNumberUtils.compare(number, phone.number)) {
					return item;
				}
			}
		}
		return null;
	}
	
	public ContactDataItem findContactInCacheById(long id) {
		ContactDataItem data = null;
		synchronized (mDataCache) {			
			data = findContactInListById(mDataCache, id);
		}
		return data;
	}
	
	public ContactDataItem findContactInCacheByNumber(String number) {
		ContactDataItem data = null;
		synchronized (mDataCache) {			
			data = findContactInListByNumber(mDataCache, number);
		}
		return data;
	}
	
	public static int getContactIndexByLetter(String letter, List<ContactDataItem> list) {
		int index = -1;
		if(letter == null || letter.length() <= 0) {
			return index;
		}
		
		char c = letter.charAt(0);
		char searchKey;
		if (c >= 44032 && c <= 55203) {
			searchKey = c;
		} else {
			StringBuilder b = new StringBuilder();
			b.append(c);
			searchKey = b.toString().toLowerCase().charAt(0);
		}
		
		for (int i = 0; i < list.size(); ++i) {
			if (list.get(i) == null || 
				list.get(i).getCanonicalPinyin() == null || 
				list.get(i).getCanonicalPinyin().length() <= 0) {
				continue;
			}
			if (list.get(i).getCanonicalPinyin().charAt(0) == searchKey) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	private class ContactsOberver extends ContentObserver {
		public ContactsOberver(Handler handler) {
			super(handler);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return false;
		}

		@Override
		public void onChange(boolean selfChange) {
			if(mIsStarted) {
				// 强制重新读取数据
				mIsDataReady = false;
        		startRead(true);
        	}
		}
	}
}
