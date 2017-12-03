package com.jiubang.ggheart.apps.desks.backup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Environment;

/**
 * 
 * @author guoyiqing
 *
 */
public class BackupRestoreParser {
	private static final String FILENAME = "backup_config.xml";
	private static final String FILE_ENCODE = "utf-8";
	private static BackupRestoreParser sParser;
	private List<BackupRestoreItem> mItems;
	
	public BackupRestoreParser() {
		mItems = new ArrayList<BackupRestoreItem>();
	}
	
	public static synchronized BackupRestoreParser getParser() {
		if (sParser == null) {
			sParser = new BackupRestoreParser();
		}
		return sParser;
	}
	
	public List<BackupRestoreItem> getBackupFileMap(Context context) {
		if (mItems.size() <= 0) {
			parseXml(context);
			prepareItems(context);
		}
		return mItems;
	}

	private void prepareItems(Context context) {
		String backup_data_path = Environment.getDataDirectory() + "/data/"
				+ context.getPackageName();
		String backup_sdcard_path = Environment.getExternalStorageDirectory().getPath();
		for (BackupRestoreItem item : mItems) {
			if (item.mIsDataToExternal) {
				item.mBackupPath = backup_data_path + item.mBackupPath;
			} else {
				item.mBackupPath = backup_sdcard_path + item.mBackupPath;
			}
			item.mRestorePath = backup_sdcard_path + item.mRestorePath;
		}
	}
	
	private void parseXml(Context context) {
		if (context == null) {
			return;
		}
		InputStream inputStream = null;
		XmlPullParser xmlPullParser = null;
		try {
			xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
			inputStream = context.getAssets().open(FILENAME);
			xmlPullParser.setInput(inputStream, FILE_ENCODE);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlPullParser == null) {
			return;
		}
		String attributeValue = null;
		try {
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String tagName = xmlPullParser.getName();
					if (tagName.equals(BackupConfigTAG.ITEM)) {
						BackupRestoreItem item = new BackupRestoreItem();
						attributeValue = xmlPullParser.getAttributeValue(null,
								BackupConfigTAG.BACKUPPATH);
						item.mBackupPath = attributeValue;
						attributeValue = xmlPullParser.getAttributeValue(null,
								BackupConfigTAG.RESTOREPATH);
						item.mRestorePath = attributeValue;
						attributeValue = xmlPullParser.getAttributeValue(null,
								BackupConfigTAG.ISDATATOEXTERNAL);
						item.mIsDataToExternal = stringToBoolean(attributeValue);
						attributeValue = xmlPullParser.getAttributeValue(null,
								BackupConfigTAG.ISFOLDER);
						item.mIsFolder = stringToBoolean(attributeValue);;
						mItems.add(item);
					}
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean stringToBoolean(String value) {
		if (null == value) {
			return false;
		}
		if (value.equals("1") || value.equals("true") || value.equals("TRUE")) {
			return true;
		}
		return false;
	}
	
}