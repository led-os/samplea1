package com.jiubang.ggheart.data.model;

import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;

import com.go.util.file.media.FileEngine;
import com.go.util.file.media.MediaDataProviderConstants;
import com.jiubang.ggheart.data.tables.MediaManagementHideTable;
import com.jiubang.ggheart.data.tables.MediaManagementPlayListFileTable;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2014-1-3]
 */
public class MediaFileDataModel extends DataModel {

	public MediaFileDataModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取多媒体隐藏数据（图片、音乐、视频）
	 * @param type
	 * @return
	 */
	public HashMap<String, String> getAllHideMediaDatas(int type) {
		HashMap<String, String> datas = new HashMap<String, String>();
		String select = MediaManagementHideTable.TYPE + "=" + type + " ";
		Cursor cursor = mContext.getContentResolver().query(MediaDataProviderConstants.HideData.CONTENT_DATA_URI, null, select,
				null, null);
		
		if (cursor != null) {
			try {
				String uri = null;
				final int uriIdx = cursor.getColumnIndex(MediaManagementHideTable.URI);
				if (cursor.moveToFirst()) {
					do {
						uri = cursor.getString(uriIdx);
						datas.put(uri, uri);
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}
		return datas;
	}

	/**
	 * 获取播放列表中隐藏音乐数据
	 * @return
	 */
	public HashSet<Integer> getAllHidePlayListAudioDatas() {
		HashSet<Integer> datas = new HashSet<Integer>();
		HashMap<String, String> hidePlaylistId = getAllHideMediaDatas(FileEngine.TYPE_PLAYLIST);
		for (String playListId : hidePlaylistId.values()) {
			HashMap<String, Long> hideFiles = getPlayListFiles(Long.parseLong(playListId));
			for (Long fileId : hideFiles.values()) {
				datas.add(fileId.intValue());
			}
		}
		return datas;
	}
	
	/**
	 * 根据播放列表id获取其包含的音乐文件
	 * @param playListId
	 * @return
	 */
	public HashMap<String, Long> getPlayListFiles(long playListId) {
		HashMap<String, Long> datas = new HashMap<String, Long>();
		Cursor cursor = mContext.getContentResolver().query(MediaDataProviderConstants.PlayListFile.CONTENT_DATA_URI, null,
				MediaManagementPlayListFileTable.PLAYLISTID + "=" + playListId, null, null);
		if (cursor != null) {
			try {
				final int idx = cursor.getColumnIndex(MediaManagementPlayListFileTable.FILEID);
				if (cursor.moveToFirst()) {
					do {
						long id = cursor.getLong(idx);
						datas.put(String.valueOf(id), id);
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
		}
		return datas;
	}

}
