package com.jiubang.ggheart.apps.appfunc.controler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.gau.go.launcherex.R;
import com.go.proxy.MsgMgrProxy;
import com.go.proxy.SettingProxy;
import com.go.util.file.media.AudioFile;
import com.go.util.file.media.Category;
import com.go.util.file.media.FileEngine;
import com.go.util.file.media.FileEngine.FileObserver;
import com.go.util.file.media.FileInfo;
import com.go.util.file.media.ImageFile;
import com.go.util.file.media.MediaDataProviderConstants;
import com.go.util.file.media.MediaDbUtil;
import com.go.util.file.media.MediaFileUtil;
import com.go.util.file.media.ThumbnailManager;
import com.go.util.file.media.VideoFile;
import com.golauncher.message.IDiyFrameIds;
import com.golauncher.message.IMediaManagementMsgId;
import com.jiubang.ggheart.apps.appfunc.setting.FunAppSetting;
import com.jiubang.ggheart.components.DeskToast;
import com.jiubang.ggheart.data.DataProvider;
import com.jiubang.ggheart.data.model.MediaFileDataModel;
import com.jiubang.ggheart.data.tables.MediaManagementHideTable;
import com.jiubang.ggheart.data.tables.MediaManagementPlayListFileTable;
import com.jiubang.ggheart.data.tables.MediaManagementPlayListTable;
import com.jiubang.ggheart.plugin.mediamanagement.MediaManagementFrame;
import com.jiubang.ggheart.plugin.mediamanagement.MediaManagementOpenChooser;
import com.jiubang.ggheart.plugin.mediamanagement.MediaPluginFactory;
import com.jiubang.ggheart.plugin.mediamanagement.inf.AppFuncContentTypes;
import com.jiubang.ggheart.plugin.mediamanagement.inf.IMediaManager;

/**
 * 其实没必要单例，静态也行
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  licanhui
 * @date  [2014-1-3]
 */
public class MediaFileSuperVisor {

	/**
	 * 从搜索打开多媒体文件
	 */
	public static final int MEDIA_FILE_OPEN_BY_SEARCH = 1;
	/**
	 * 从插件包打开多媒体文件
	 */
	public static final int MEDIA_FILE_OPEN_BY_PLUGIN = 2;

	// 持久化操作
	private MediaFileDataModel mMediaFileDataModel = null;

	private FileEngine mFileEngine;
	
	private Context mContext;
	
	private FileEngineDataRefreshListener mFileEngineDataRefreshListener;

	private static MediaFileSuperVisor sInstance;
	
	public static MediaFileSuperVisor getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new MediaFileSuperVisor(context);
		}
		return sInstance;
	}

	private MediaFileSuperVisor(Context context) {
		super();
		mContext = context;
		mMediaFileDataModel = new MediaFileDataModel(mContext);
	}

	public void scanSDCard() {
		if (mFileEngine != null) {
			mFileEngine.scanSDCard();
		}
	}

	/**
	 * 获取所有音频文件,可以滤掉被隐藏的专辑和播放列表内的文件
	 * 
	 * @param filterCategoryHide
	 *            是否过滤掉隐藏专辑内的文件
	 * @param filterCategoryHide
	 *            是否过滤掉隐藏播放列表内的文件
	 * @author yangbing
	 * */
	public ArrayList<FileInfo> getAllAudio(boolean filterCategoryHide,
			boolean filterPlaylistHide) {
		ArrayList<FileInfo> files = null;
		if (mFileEngine != null) {
			files = mFileEngine.getAllAudio();
		}
		if (filterCategoryHide) {
			HashMap<String, String> hideDatas = mMediaFileDataModel
					.getAllHideMediaDatas(FileEngine.TYPE_AUDIO);
			filterHideAudioFiles(files, hideDatas);
		}
		if (filterPlaylistHide) {
			HashSet<Integer> hideDatas = mMediaFileDataModel
					.getAllHidePlayListAudioDatas();
			filterHidePlaylistAudioFiles(files, hideDatas);
		}

		return files;
	}

	public void registerFileObserver(FileObserver observer) {
		if (mFileEngine != null) {
			mFileEngine.setFileObserver(observer);
		}
	}

	public void destroyFileEngine() {
		if (mFileEngine != null) {
			mFileEngine.destroy();
			mFileEngine = null;
		}
	}

	public void buildFileEngine() {
		if (null == mFileEngine) {
			mFileEngine = new FileEngine(mContext);
			if (MediaPluginFactory.isMediaPluginExist(mContext)) { 
				IMediaManager mediaManager = MediaPluginFactory.getMediaManager();
				if (mediaManager != null) {
					mediaManager.setFileEngine(mFileEngine);
				}
			}
		}

	}

	private boolean mediaInHidePool(String uri, HashMap<String, String> pool) {
		if (uri == null || pool == null) {
			return false;
		}
		String temp = pool.get(uri);
		if (temp != null && uri.equals(temp)) {
			return true;
		}
		return false;
	}

	/**
	 * 过滤掉隐藏专辑中的音乐文件
	 * 
	 * @param files
	 * @param hideDatas
	 */
	private void filterHideAudioFiles(ArrayList<FileInfo> files,
			HashMap<String, String> hideDatas) {
		if (files == null || hideDatas == null) {
			return;
		}

		Iterator<FileInfo> it = files.iterator();
		while (it.hasNext()) {
			AudioFile info = (AudioFile) it.next();
			if (mediaInHidePool(info.album, hideDatas)) {
				it.remove();
			}
		}
	}

	/**
	 * 过滤掉隐藏播放列表中的音乐文件
	 * 
	 * @param files
	 * @param hideDatas
	 */
	private void filterHidePlaylistAudioFiles(ArrayList<FileInfo> files,
			HashSet<Integer> hideDatas) {
		if (files == null || hideDatas == null) {
			return;
		}

		Iterator<FileInfo> it = files.iterator();
		while (it.hasNext()) {
			FileInfo info = it.next();
			if (hideDatas.contains(info.dbId)) {
				it.remove();
			}
		}
	}

	/**
	 * 
	 * 加载所有多媒体数据，并过滤后发消息返回（用于功能表搜索多媒体数据）
	 */
	public void refreshAllMediaData() {
		new Thread(new Runnable() { // 启动异步线程加载多媒体数据
					@Override
					public void run() {
						android.os.Process
								.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
						boolean isFilter = false;
						if (MediaPluginFactory.isMediaPluginExist(mContext)) { // 存在插件包，需要从plugin包中取出隐藏数据进行过滤
							isFilter = true;
						}
						if (mFileEngine != null) {
							try {
								if (!mFileEngine.isMediaDataInited(FileEngine.TYPE_IMAGE)) {
									mFileEngine.setInitingData(FileEngine.TYPE_IMAGE, true);
									// 加载图片数据
									mFileEngine.initImage();
									mFileEngine.setInitingData(FileEngine.TYPE_IMAGE, false);
								}
								ArrayList<FileInfo> imageData = getAllImage(isFilter);
								if (mFileEngineDataRefreshListener != null) {
									mFileEngineDataRefreshListener.dataRefreshFinish(
											FileEngine.TYPE_IMAGE, imageData);
								}

								if (!mFileEngine.isMediaDataInited(FileEngine.TYPE_AUDIO)) {
									mFileEngine.setInitingData(FileEngine.TYPE_AUDIO, true);
									// 加载音乐数据
									mFileEngine.initAudio();
									mFileEngine.setInitingData(FileEngine.TYPE_AUDIO, false);
								}
								ArrayList<FileInfo> audioData = getAllAudio(isFilter, isFilter);
								if (audioData == null) {
									audioData = new ArrayList<FileInfo>();
								}
								if (mFileEngineDataRefreshListener != null) {
									mFileEngineDataRefreshListener.dataRefreshFinish(
											FileEngine.TYPE_AUDIO, audioData);
								}

								if (!mFileEngine.isMediaDataInited(FileEngine.TYPE_VIDEO)) {
									mFileEngine.setInitingData(FileEngine.TYPE_VIDEO, true);
									// 加载视频数据
									mFileEngine.initVideo();
									mFileEngine.setInitingData(FileEngine.TYPE_VIDEO, false);
								}
								ArrayList<FileInfo> videoData = getAllVideo(isFilter);
								if (mFileEngineDataRefreshListener != null) {
									mFileEngineDataRefreshListener.dataRefreshFinish(
											FileEngine.TYPE_VIDEO, videoData);
								}
							} catch (NullPointerException ex) {
								// 如果在跑这段方法的时候执行退出搜索层的动作，外界会把引擎销毁，导致空指针，在这里进行捕捉，
								// 所带来的后果为本次搜索所请求的多媒体数据无法返回，导致搜索中无多媒体数据
							}
						}
					}
				}).start();
	}

	/**
	 * 
	 * 获取所有视频文件信息
	 * 
	 * @param filterCategoryHide
	 *            是否过滤隐藏视频文件
	 * @return
	 */
	@SuppressWarnings("null")
	public ArrayList<FileInfo> getAllVideo(boolean filterCategoryHide) {
		ArrayList<FileInfo> temp = new ArrayList<FileInfo>();
		if (mFileEngine != null) {
			ArrayList<FileInfo> videoInfoList = mFileEngine.getAllVideo();
			if (videoInfoList != null) {
				HashMap<String, String> hideDatas = mMediaFileDataModel
						.getAllHideMediaDatas(FileEngine.TYPE_VIDEO);
				for (FileInfo fileInfo : videoInfoList) {
					if (filterCategoryHide && mediaInHidePool(fileInfo.uri, hideDatas)) {
						continue;
					}
					fileInfo.needHide = false;
					temp.add(fileInfo);
				}
			}
		}
		return temp;
	}

	/**
	 * 
	 * 获取所有视频图片信息
	 * 
	 * @param filterCategoryHide
	 *            是否过滤隐藏图片文件
	 * @return
	 */
	public ArrayList<FileInfo> getAllImage(boolean filterCategoryHide) {
		ArrayList<FileInfo> temp = new ArrayList<FileInfo>();
		if (mFileEngine != null) {
			ArrayList<Category> imageCategoryInfoList = mFileEngine
					.getImagePaths();
			if (imageCategoryInfoList != null) {
				HashMap<String, String> hideDatas = mMediaFileDataModel
						.getAllHideMediaDatas(FileEngine.TYPE_IMAGE);
				for (Category imageCategoryInfo : imageCategoryInfoList) {
					if (imageCategoryInfo.files.isEmpty()
							|| (filterCategoryHide && mediaInHidePool(imageCategoryInfo.uri, hideDatas))) {
						continue;
					}
					for (FileInfo fileInfo : imageCategoryInfo.files) {
						if (fileInfo != null) {
							temp.add(fileInfo);
						}
					}
				}
			}
		}
		return temp;
	}

	/**
	 * 
	 * 打开多媒体文件（音乐、图片、视频）
	 */
	public void openMediaFile(FileInfo info, int openType, List<?> objs) {
		if (info instanceof AudioFile) {
			if (openType == MEDIA_FILE_OPEN_BY_SEARCH) {
				openAudioFile((AudioFile) info, true);
			} else {
				openAudioFile((AudioFile) info, false);
			}
		} else if (info instanceof ImageFile) {
			if (openType == MEDIA_FILE_OPEN_BY_SEARCH) {
				ArrayList<FileInfo> itemInfos = new ArrayList<FileInfo>();
				itemInfos.add(info);
				Bitmap b = ThumbnailManager.getInstance(mContext).getThumbnail(
						null, ThumbnailManager.TYPE_IMAGE, info.thumbnailId,
						info.thumbnailPath);
				if (b == null) { // 如果图片为空，则获取默认图片
					return;
				}
//				GoLauncher.sendMessage(this, IDiyFrameIds.MEDIA_CONTROLER,
//						IDiyMsgIds.SET_IMAGE_BROWSER_DATA, MEDIA_FILE_OPEN_BY_SEARCH, b, itemInfos);
				openImageFile(info, b, itemInfos);
			} else {
				openImageFile(info, null, null);
			}
		} else if (info instanceof VideoFile) {
			openVideoFile(info);
		}
	}
	
	/**
	 * 打开音频文件
	 * 
	 * @param fileInfo
	 * @param contentType
	 */
	public void openAudioFile(AudioFile info, boolean isOpenBySearch) {
		if (checkFile(mContext, info.fullFilePath)) {
			FunAppSetting setting = SettingProxy.getFunAppSetting();
			String uri = setting.getMediaOpenWay(FileEngine.TYPE_AUDIO);
			if (MediaManagementOpenChooser.APP_NONE.equals(uri)) {
				MediaManagementOpenChooser.getInstance(mContext).openChooser(
						info, info.mimeType,
						FileEngine.TYPE_AUDIO, info, isOpenBySearch);
			} else if (MediaManagementOpenChooser.APP_GO_MUSIC_PLAYER
					.equals(uri)) {
				if (MediaPluginFactory.isMediaPluginExist(mContext)) { // 存在插件包才打开播放器
					// 回到播放界面
					SwitchControler.getInstance(mContext).showMediaManagementFrame(-1, false);
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
							IMediaManagementMsgId.SWITCH_MEDIA_MANAGEMENT_CONTENT, -1, new Object[] {
									AppFuncContentTypes.MUSIC_PLAYER, (long) info.dbId, info.album,
									isOpenBySearch, info }, null);
				} else {
					MediaManagementOpenChooser.getInstance(mContext).openChooser(
							info, info.mimeType,
							FileEngine.TYPE_AUDIO, info, isOpenBySearch);
				}
			} else {
				try {
					// 使用系统播放器打开
					Intent intent = Intent.getIntent(uri);
					intent.setDataAndType(
							Uri.parse("file://" + info.fullFilePath),
							info.mimeType);
					mContext.startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					DeskToast.makeText(mContext, R.string.no_way_to_open_file,
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					DeskToast.makeText(mContext, R.string.no_way_to_open_file,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public boolean checkFile(Context context, String path) {
		if (MediaFileUtil.isFileExist(path) && MediaFileUtil.getFileSize(path) > 0) {
			return true;
		} else {
			showFileNotFoundDialog(context);
			return false;
		}
	}

	private void showFileNotFoundDialog(Context context) {
		new AlertDialog.Builder(context).setTitle(R.string.media_file_not_found_title)
				.setMessage(R.string.media_file_not_found_message)
				.setPositiveButton(R.string.refresh, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						scanSDCard();
					}
				}).setNegativeButton(R.string.cancel, null).create().show();
	}
	
	/**
	 * 
	 * 打开图片文件
	 * 
	 * @param info
	 * @param itemInfos
	 * @param b
	 */
	public void openImageFile(FileInfo info, Bitmap b, ArrayList<FileInfo> itemInfos) {
		if (checkFile(mContext, info.fullFilePath)) {
			String mimeType = "image/*";
			FunAppSetting setting = SettingProxy.getFunAppSetting();
			String uri = setting.getMediaOpenWay(FileEngine.TYPE_IMAGE);
			if (MediaManagementOpenChooser.APP_NONE.equals(uri)) {
				MediaManagementOpenChooser.getInstance(mContext).openChooser(info, mimeType,
						FileEngine.TYPE_IMAGE, b, itemInfos);
			} else if (MediaManagementOpenChooser.APP_GO_PIC_VIEWER.equals(uri)) {
				// 使用go图片浏览器打开
				if (MediaPluginFactory.isMediaPluginExist(mContext)) {
					if (!MediaManagementFrame.getMediaManagementIsInited()) {
						SwitchControler.getInstance(mContext).showMediaManagementFrame(-1, false);
					}
					MsgMgrProxy.sendMessage(this, IDiyFrameIds.MEDIA_MANAGEMENT_FRAME,
							IMediaManagementMsgId.OPEN_IMAGE_BROWSER, -1, new Object[]{b, itemInfos}, null);
				} else {
					MediaManagementOpenChooser.getInstance(mContext).openChooser(info, mimeType,
							FileEngine.TYPE_IMAGE, new Object[]{b, itemInfos});
				}
			} else {
				try {
					// 使用系统播放器打开
					Intent intent = Intent.getIntent(uri);
					intent.setDataAndType(
							Uri.parse("file://" + info.fullFilePath), mimeType);
					mContext.startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					DeskToast.makeText(mContext, R.string.no_way_to_open_file,
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					DeskToast.makeText(mContext, R.string.no_way_to_open_file,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 
	 * 打开视频文件
	 * 
	 * @param info
	 */
	public void openVideoFile(FileInfo info) {
		if (checkFile(mContext, info.fullFilePath)) {
			String mimeType = "video/*";
			if (mimeType != null) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setType(mimeType);
				intent.setDataAndType(Uri.parse("file://" + info.uri), mimeType);
				try {
					mContext.startActivity(intent);
				} catch (ActivityNotFoundException ex) {
					DeskToast.makeText(mContext, R.string.no_way_to_open_file,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 获取引擎
	 * 
	 * @return
	 */
	public FileEngine getFileEngine() {
		return mFileEngine;
	}

	/**
	 * 复制所有资源管理数据至插件包
	 */
	public boolean copyAllMediaData() {
		ArrayList<Cursor> cursorList = DataProvider.getInstance(mContext)
				.getAllMediaData();
		boolean ret = false;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		Cursor playListCursor = cursorList.get(0); // 播放列表
		Cursor playListFileCursor = cursorList.get(1); // 播放列表文件
		Cursor hideFileCursor = cursorList.get(2); // 隐藏文件表
		try {
			if (playListCursor != null && playListCursor.moveToFirst()) { // 播放列表
				do {
					ContentValues values = new ContentValues();
					values.put(MediaManagementPlayListTable.ID, MediaDbUtil
							.getLong(playListCursor,
									MediaManagementPlayListTable.ID));
					values.put(MediaManagementPlayListTable.NAME, MediaDbUtil
							.getString(playListCursor,
									MediaManagementPlayListTable.NAME));
					values.put(MediaManagementPlayListTable.TYPE, MediaDbUtil
							.getInt(playListCursor,
									MediaManagementPlayListTable.TYPE));
					values.put(MediaManagementPlayListTable.UDATE, MediaDbUtil
							.getLong(playListCursor,
									MediaManagementPlayListTable.UDATE));
					ops.add(ContentProviderOperation
							.newInsert(
									MediaDataProviderConstants.PlayList.CONTENT_DATA_URI)
							.withValues(values).build());
				} while (playListCursor.moveToNext());
			}

			if (playListFileCursor != null && playListFileCursor.moveToFirst()) { // 播放列表文件
				do {
					ContentValues values = new ContentValues();
					values.put(
							MediaManagementPlayListFileTable.PLAYLISTID,
							MediaDbUtil
									.getLong(
											playListFileCursor,
											MediaManagementPlayListFileTable.PLAYLISTID));
					values.put(MediaManagementPlayListFileTable.FILEID,
							MediaDbUtil.getLong(playListFileCursor,
									MediaManagementPlayListFileTable.FILEID));
					values.put(MediaManagementPlayListFileTable.DATE,
							MediaDbUtil.getLong(playListFileCursor,
									MediaManagementPlayListFileTable.DATE));
					ops.add(ContentProviderOperation
							.newInsert(
									MediaDataProviderConstants.PlayListFile.CONTENT_DATA_URI)
							.withValues(values).build());
				} while (playListFileCursor.moveToNext());
			}

			if (hideFileCursor != null && hideFileCursor.moveToFirst()) { // 隐藏文件表
				do {
					ContentValues values = new ContentValues();
					values.put(MediaManagementHideTable.TYPE, MediaDbUtil
							.getInt(hideFileCursor,
									MediaManagementHideTable.TYPE));
					values.put(MediaManagementHideTable.URI, MediaDbUtil
							.getString(hideFileCursor,
									MediaManagementHideTable.URI));
					ops.add(ContentProviderOperation
							.newInsert(
									MediaDataProviderConstants.HideData.CONTENT_DATA_URI)
							.withValues(values).build());
				} while (hideFileCursor.moveToNext());
			}
			mContext.getContentResolver().applyBatch(
					MediaDataProviderConstants.AUTHORITY, ops);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (playListCursor != null) {
				playListCursor.close();
			}
			if (playListFileCursor != null) {
				playListFileCursor.close();
			}
			if (hideFileCursor != null) {
				hideFileCursor.close();
			}
		}
		return ret;
	}

	/**
	 * 删除桌面所有资源管理数据
	 */
	public boolean deleteAllMediaData() {
		return DataProvider.getInstance(mContext).deleteAllMediaData();
	}
	
	public void setFileEngineDataRefreshListener(FileEngineDataRefreshListener fileEngineDataRefreshListener) {
		mFileEngineDataRefreshListener = fileEngineDataRefreshListener;
	}
	
	/**
	 * 
	 * @author dingzijian
	 *
	 */
	public interface FileEngineDataRefreshListener {
		public void dataRefreshFinish(int type, ArrayList<FileInfo> data);
	}

}
