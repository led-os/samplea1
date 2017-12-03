package com.jiubang.ggheart.apps.desks.diy.pref;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Environment;

/**
 * 
 * @author guoyiqing
 * 
 */
public class PrefMigrater {

	private static final String PREF_BOTTOM_DELIMITER = "</map>";
	private static final String PREF_PRE_DELIMITER = "<map>";
	private String mDstPref;
	private static String sPrefTopStr;
	private static String sPrefBottomStr;

	public PrefMigrater(String dstPref) {
		mDstPref = dstPref;
	}

	public void migrate(List<String> srcPrefs, Context context) {
		if (mDstPref == null || mDstPref.trim().equals("") || srcPrefs == null
				|| srcPrefs.isEmpty()) {
			return;
		}
		String dir = Environment.getDataDirectory() + "/data/"
				+ context.getPackageName() + "/shared_prefs";
		new File(dir).mkdir();
		StringBuilder sBuilder = new StringBuilder();
		int length = srcPrefs.size();
		String fileContent = null;
		for (int i = 0; i < length; i++) {
			String scrPath = dir + "/" + srcPrefs.get(i) + ".xml";
			File srcFile = new File(scrPath);
			if (!srcFile.exists()) {
				continue;
			}
			fileContent = getMigrateContent(scrPath);
			if (fileContent != null && !fileContent.trim().equals("")) {
				sBuilder.append(fileContent + "\n");
			}
		}
		if (sBuilder.length() <= 0) {
			return;
		}
		preventEmpty();
		String dstPath = dir + "/" + mDstPref + ".xml";
		BufferedWriter writer = null;
		String dstContent = getMigrateContent(dstPath);
		if (dstContent == null) {
			dstContent = "";
		}
		new File(dstPath).delete();
		try {
			writer = new BufferedWriter(new FileWriter(dstPath));
			writer.write(sPrefTopStr);
			writer.write(dstContent);
			writer.write(sBuilder.toString());
			writer.write(sPrefBottomStr);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void preventEmpty() {
		if (sPrefTopStr == null || sPrefTopStr.trim().equals("")) {
			sPrefTopStr = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>"
					+ "\n" + PREF_PRE_DELIMITER;
		}
		if (sPrefBottomStr == null || sPrefBottomStr.trim().equals("")) {
			sPrefBottomStr = PREF_BOTTOM_DELIMITER + "\n";
		}
	}

	private String getMigrateContent(String scrPath) {
		File file = new File(scrPath);
		if (!file.exists()) {
			return "";
		}
		StringBuilder sBuilder = new StringBuilder();
		BufferedReader reader = null;
		try {
			String line = null;
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				sBuilder.append(line);
			}
			String content = sBuilder.toString();
			return parseContent(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	private String parseContent(String content) {
		int startIndex = content.indexOf(PREF_PRE_DELIMITER);
		if (startIndex != -1) {
			if (sPrefTopStr == null) {
				sPrefTopStr = content.substring(0, startIndex) + "\n" + PREF_PRE_DELIMITER + "\n";
			}
			int endIndex = content.indexOf(PREF_BOTTOM_DELIMITER);
			if (endIndex != -1) {
				if (sPrefBottomStr == null) {
					sPrefBottomStr = content.substring(endIndex) + "\n";
				}
				content = content.substring(startIndex + PREF_PRE_DELIMITER.length(),
						endIndex);
				return content;
			}
		} else {
			if (sPrefTopStr == null) {
				startIndex = content.indexOf("<map />");
				if (startIndex != -1) {
					sPrefTopStr = content.substring(0, startIndex) + "\n";
					sPrefBottomStr = "";
				}
			}
		}
		return "";
	}

}
