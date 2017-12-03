package com.go.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.go.proxy.ApplicationProxy;
import com.go.util.log.LogConstants;
import com.jiubang.ggheart.apps.desks.Preferences.DeskSettingConstants;
import com.jiubang.ggheart.data.AppClassifyDatabaseHelper;
import com.jiubang.ggheart.data.statistics.StatisticsDataBaseHelper;
import com.jiubang.ggheart.launcher.LauncherEnv;
import com.jiubang.ggheart.net.form.HttpRequestStatus;
import com.jiubang.ggheart.net.form.Result;

/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  
 * @date  
 */
public class FileUtil {
	
	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 * @author  
	 * @date  
	 */
	public enum FileCategory {
		All, Music, Video, Picture, Theme, Doc, Zip, Apk, Custom, Other, Favorite
	}

	private static String sANDROID_SECURE = "/mnt/sdcard/.android_secure";
	public static final String ROOT_PATH = "/";

	public static final String SDCARD_PATH = ROOT_PATH + "sdcard";

	public static String sZipFileMimeType = "application/zip";

	public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
		{
			add("text/plain");
			add("text/plain");
			add("application/pdf");
			add("application/msword");
			add("application/vnd.ms-excel");
			add("application/vnd.ms-excel");
		}
	};

	/**
	 * 将图片数据存入sd卡文件中
	 * 
	 * @author huyong
	 * @param iconByte
	 *            待存入的文件路径
	 * @param iconType
	 *            图片类型，传入null，则默认以png为后缀
	 * @return 存入后的文件路径，存入失败，则返回null
	 */
	public static String saveIconToSDFile(final byte[] iconByte, String iconType) {
		Random random = new Random();
		int randomId = random.nextInt();
		String string1 = LauncherEnv.Path.SDCARD + LauncherEnv.Path.SPECIAL_ICON_PATH + "/";
		String string2 = String.valueOf(randomId);
		String string3 = iconType;
		if (string3 == null) {
			string3 = ".png";
		}
		String pathString = string1 + string2 + string3;
		boolean result = saveByteToSDFile(iconByte, pathString);
		if (result) {
			return pathString;
		} else {
			return null;
		}
	}

	/**
	 * 保存位图到通用图片库中
	 * 
	 * @author huyong
	 * @param bitmap
	 *            ：位图资源
	 * @param fileName
	 *            ：待保存文件名
	 * @param iconFormat
	 *            ：图片格式
	 * @return true for 保存成功，false for 保存失败。
	 */
	public static boolean saveBitmapToCommonIconSDFile(final Bitmap bitmap, final String fileName,
			CompressFormat iconFormat) {
		String filePathName = LauncherEnv.Path.SDCARD + LauncherEnv.Path.COMMON_ICON_PATH;
		filePathName += fileName;
		return saveBitmapToSDFile(bitmap, filePathName, iconFormat);

	}

	/**
	 * 保存位图到sd卡目录下
	 * 
	 * @author huyong
	 * @param bitmap
	 *            ：位图资源
	 * @param filePathName
	 *            ：待保存的文件完整路径名
	 * @param iconFormat
	 *            ：图片格式
	 * @return true for 保存成功，false for 保存失败。
	 */
	public static boolean saveBitmapToSDFile(final Bitmap bitmap, final String filePathName,
			CompressFormat iconFormat) {
		boolean result = false;
		try {
			createNewFile(filePathName, false);
			OutputStream outputStream = new FileOutputStream(filePathName);
			result = bitmap.compress(iconFormat, 100, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @author huyong
	 * @param byteData
	 * @param fileName
	 * @return
	 */
	public static boolean saveByteToCommonIconSDFile(final byte[] byteData, final String fileName) {
		String filePathName = LauncherEnv.Path.SDCARD + LauncherEnv.Path.COMMON_ICON_PATH;
		filePathName += fileName;
		return saveByteToSDFile(byteData, filePathName);
	}

	/**
	 * 保存数据到指定文件
	 * 
	 * @author huyong
	 * @param byteData
	 * @param filePathName
	 * @return true for save successful, false for save failed.
	 */
	public static boolean saveByteToSDFile(final byte[] byteData, final String filePathName) {
		boolean result = false;
		try {
			File newFile = createNewFile(filePathName, false);
			FileOutputStream fileOutputStream = new FileOutputStream(newFile);
			fileOutputStream.write(byteData);
			fileOutputStream.flush();
			fileOutputStream.close();
			result = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @author huyong
	 * @param path
	 *            ：文件路径
	 * @param append
	 *            ：若存在是否插入原文件
	 * @return
	 */
	public static File createNewFile(String path, boolean append) {
		File newFile = new File(path);
		if (!append) {
			if (newFile.exists()) {
				newFile.delete();
			} else {
				// 不存在，则删除带png后缀名的文件
				File prePngFile = new File(path + ".png");
				if (prePngFile != null && prePngFile.exists()) {
					prePngFile.delete();
				}
			}
		}
		if (!newFile.exists()) {
			try {
				File parent = newFile.getParentFile();
				if (parent != null && !parent.exists()) {
					parent.mkdirs();
				}
				newFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newFile;
	}
	
	/**
	 * <br>功能简述:创建文件
	 * <br>功能详细描述:
	 * <br>注意:1：如果不存在父文件夹，则新建文件夹；2：如果文件已存在，则直接返回
	 * @param destFileName
	 * @param replace 是否删除旧文件，生成新文件
	 * @return
	 */
	public static boolean createFile(String destFileName, boolean replace) {
		File file = new File(destFileName);
		if (file.exists()) {
			if (replace) {
				file.delete();
			} else {
				System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
				return false;
			}
		}
		if (destFileName.endsWith(File.separator)) {
			System.out.println("创建单个文件" + destFileName + "失败，目标不能是目录！");
			return false;
		}
		if (!file.getParentFile().exists()) {
			System.out.println("目标文件所在路径不存在，准备创建。。。");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("创建目录文件所在的目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				System.out.println("创建单个文件" + destFileName + "成功！");
				return true;
			} else {
				System.out.println("创建单个文件" + destFileName + "失败！");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("创建单个文件" + destFileName + "失败！");
			return false;
		}
	}

	/**
	 * sd卡是否可读写
	 * 
	 * @author huyong
	 * @return
	 */
	public static boolean isSDCardAvaiable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 指定路径文件是否存在
	 * 
	 * @author huyong
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		boolean result = false;
		try {
			File file = new File(filePath);
			result = file.exists();
			file = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	/**
	 * 在媒体库中隐藏文件夹内的媒体文件 1. 加入.nomedia文件，使媒体功能扫描不到，用户可以通过文件浏览器方便看到 2.
	 * 在文件夹前面加点，隐藏整个文件夹，用户需要对文件浏览器设置显示点文件才能看到
	 * 
	 * @param folder
	 *            文件夹
	 */
	public static void hideMedia(final String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(folder, ".nomedia");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				Log.i(LogConstants.HEART_TAG, "hideMediaInFolder error, folder = " + folder);
			}
		}
		file = null;
	}

	/**
	 * 创建文件夹（如果不存在）
	 * 
	 * @param dir
	 */
	public static void mkDir(final String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				Log.i(LogConstants.HEART_TAG, "mkDir error, folder = " + dir);
			}
		}
		file = null;
	}

	/**
	 * 在媒体库中显示文件夹内的媒体文件
	 * 
	 * @param folder
	 *            文件夹
	 */
	public static void showMediaInFolder(final String folder) {
		File file = new File(folder, ".nomedia");
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				Log.i(LogConstants.HEART_TAG, "showMediaInFolder error, folder = " + folder);
			}
		}
	}

	public static void copyFile(String srcStr, String decStr) {
		// 前提
		File srcFile = new File(srcStr);
		if (!srcFile.exists()) {
			return;
		}
		File decFile = new File(decStr);
		if (!decFile.exists()) {
			File parent = decFile.getParentFile();
			parent.mkdirs();

			try {
				decFile.createNewFile();

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(srcFile);
			output = new FileOutputStream(decFile);
			byte[] data = new byte[4 * 1024]; // 4k
			while (true) {
				int len = input.read(data);
				if (len <= 0) {
					break;
				}
				output.write(data, 0, len);
			}
		} catch (Exception e) {
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (Exception e2) {
				}
			}
			if (null != output) {
				try {
					output.flush();
					output.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	/**
	 * 根据给定路径参数删除单个文件的方法 私有方法，供内部其它方法调用
	 * 
	 * @param filePath
	 *            要删除的文件路径
	 * @return 成功返回true,失败返回false
	 */
	public static boolean deleteFile(String filePath) {
		// 定义返回结果
		boolean result = false;
		// //判断路径参数是否为空
		// if(filePath == null || "".equals(filePath)) {
		// //如果路径参数为空
		// System.out.println("文件路径不能为空~！");
		// } else {
		// //如果路径参数不为空
		// File file = new File(filePath);
		// //判断给定路径下要删除的文件是否存在
		// if( !file.exists() ) {
		// //如果文件不存在
		// System.out.println("指定路径下要删除的文件不存在~！");
		// } else {
		// //如果文件存在，就调用方法删除
		// result = file.delete();
		// }
		// }

		if (filePath != null && !"".equals(filePath.trim())) {
			File file = new File(filePath);
			if (file.exists()) {
				result = file.delete();
			}
		}
		return result;
	}

	/*
	 * @param path 要删除的文件夹路径
	 * 
	 * @return 是否成功
	 */
	public static boolean deleteCategory(String path) {
		if (path == null || "".equals(path)) {
			return false;
		}

		File file = new File(path);
		if (!file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteFile(f.getAbsolutePath());
			}
		}

		return file.delete();
	}

	public static boolean isNormalFile(String fullName) {
		return !fullName.equals(sANDROID_SECURE);
	}

	public static String getSdDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/*
	 * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过 appInfo.publicSourceDir =
	 * apkPath;来修正这个问题，详情参见:
	 * http://code.google.com/p/android/issues/detail?id=9151
	 */
	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getExtFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(dotPosition + 1, filename.length());
		}
		return "";
	}

	public static String getNameFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(0, dotPosition);
		}
		return "";
	}

	public static String getPathFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(0, pos);
		}
		return "";
	}

	public static String getNameFromFilepath(String filepath) {
		int pos = filepath.lastIndexOf('/');
		if (pos != -1) {
			return filepath.substring(pos + 1);
		}
		return "";
	}

	// storage, G M K B
	public static String convertStorage(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else {
			return String.format("%d B", size);
		}
	}

	/**
	 * 
	 * <br>类描述:
	 * <br>功能详细描述:
	 * 
	 */
	public static class SDCardInfo {
		public long total;

		public long free;
	}

	public static SDCardInfo getSDCardInfo() {
		String sDcString = android.os.Environment.getExternalStorageState();

		if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File pathFile = android.os.Environment.getExternalStorageDirectory();

			try {
				android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

				// 获取SDCard上BLOCK总数
				long nTotalBlocks = statfs.getBlockCount();

				// 获取SDCard上每个block的SIZE
				long nBlocSize = statfs.getBlockSize();

				// 获取可供程序使用的Block的数量
				long nAvailaBlock = statfs.getAvailableBlocks();

				// 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
				long nFreeBlock = statfs.getFreeBlocks();

				SDCardInfo info = new SDCardInfo();
				// 计算SDCard 总容量大小MB
				info.total = nTotalBlocks * nBlocSize;

				// 计算 SDCard 剩余大小MB
				info.free = nAvailaBlock * nBlocSize;

				return info;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String formatDateString(Context context, long time) {
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
		Date date = new Date(time);
		return dateFormat.format(date) + " " + timeFormat.format(date);
	}

	public static long getFileSize(String path) {
		long size = 0;
		if (path != null) {
			File file = new File(path);
			size = file.length();
		}
		return size;
	}
	public static byte[] getByteFromSDFile(final String filePathName) {
		byte[] bs = null;
		try {
			File newFile = new File(filePathName);
			FileInputStream fileInputStream = new FileInputStream(newFile);
			DataInputStream dataInputStream = new DataInputStream(fileInputStream);
			BufferedInputStream inPutStream = new BufferedInputStream(dataInputStream);
			bs = new byte[(int) newFile.length()];
			inPutStream.read(bs);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bs;
	}
	
	/**
	 * <br>功能简述:删除文件夹
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param sPath
	 * @return
	 */
	public static boolean deleteDirectory(String sPath) {   
	    //如果sPath不以文件分隔符结尾，自动添加文件分隔符   
	    if (!sPath.endsWith(File.separator)) {   
	        sPath = sPath + File.separator;   
	    }   
	    File dirFile = new File(sPath);   
	    //如果dir对应的文件不存在，或者不是一个目录，则退出   
	    if (!dirFile.exists() || !dirFile.isDirectory()) {   
	        return false;   
	    }   
	    boolean flag = true;   
	    //删除文件夹下的所有文件(包括子目录)   
	    File[] files = dirFile.listFiles();   
	    for (int i = 0; i < files.length; i++) {   
	        //删除子文件   
	        if (files[i].isFile()) {   
	            flag = deleteFile(files[i].getAbsolutePath());   
				if (!flag) {
					break;
				}
	        } //删除子目录   
	        else {   
	            flag = deleteDirectory(files[i].getAbsolutePath());   
	            if (!flag) {
	            	break;
	            }   
	        }   
	    }   
	    if (!flag) {
	    	return false;
	    }
	    //删除当前目录   
	    if (dirFile.delete()) {   
	        return true;   
	    } else {   
	        return false;   
	    }   
	}
	
	private static String createApkCachePath(Context context, String fileName) {
		//存放临时从assets目录中读取出来的dex文件的缓存目录
		final String apks_cache = "apks";
		String parentDir = context.getDir(apks_cache, Context.MODE_PRIVATE)
				.getAbsolutePath();
		String cacheName = null;
		File file = new File(parentDir);
		try {
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(parentDir + File.separator + fileName);
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (file != null) {
			cacheName = file.getAbsolutePath(); 
			Log.i("createPath", "============apk = " + cacheName);
		}
		return cacheName; 
	}
	
	public static String copyFileToApkCacheFromAsset(Context context, String fileName) {
		InputStream is = null;
		FileOutputStream fos = null;
		String apkFile = null;
		try {
			is = context.getAssets().open(fileName);
			apkFile = createApkCachePath(context, fileName);
			fos = new FileOutputStream(new File(apkFile));
			byte[] buffer = new byte[1024];
			while (true) {
				int len = is.read(buffer);
				if (len == -1) {
					break;
				}
				fos.write(buffer, 0, len);
			}
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return apkFile;
	}
	
	public static boolean checkFileInAssets(String toCheckFile) {
		AssetManager assetMgr = null;
		boolean isContainShellEngine = false;
		InputStream input = null;
		try {
			assetMgr = ApplicationProxy.getContext().getAssets();
			if (assetMgr != null) {
				input = assetMgr.open(toCheckFile);
			}
			isContainShellEngine = true;
		} catch (Exception e) {
			e.printStackTrace();
			isContainShellEngine = false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Log.i("shellfactory", "=====sContainEngine = " + toCheckFile);
		return isContainShellEngine;
	}
	
	/**
	 * 统一获取raw文件流中数据
	 * @param context
	 * @param rawId
	 * @return
	 */
	public static String getShortStrDataFromRaw(Context context, int rawId) {
		String strData = null;
		if (context == null) {
			return strData;
		}
		// 从资源获取流
		InputStream is = null;
		try {
			is = context.getResources().openRawResource(rawId);
			if (is != null) {
				byte[] buffer = new byte[128];
				int len = is.read(buffer); // 读取流内容
				if (len > 0) {
					strData = new String(buffer, 0, len).trim(); // 生成字符串
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return strData;
	}
	
	/**
	 *
	 * @param strPath
	 *            获取文件夹下文件的路径
	 * @return 文件夹下的文件列表
	 * @author zhangxi
	 * @date 2013-09-22
	 */
	public static ArrayList<String> getDirFiles(String strPath) {
		ArrayList<String> strFileList = new ArrayList<String>();
		try {
			File dirFile = new File(strPath);
			// 如果dir对应的文件不存在，或者不是一个目录，则退出
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				return null;
			}

			File[] files = dirFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				// 将文件夹下的文件返回，排除子文件夹
				if (files[i].isFile()) {
					strFileList.add(files[i].getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strFileList;
	}
	
	
	public static final String GOOGLE_ANALYTICS_FILENAME = "google_analytics";
	
	public static void copyBackupFile(String filepath, String backupfilepath) {
		File backupfile = new File(backupfilepath);
		File file = new File(filepath);
		if (file.exists()) {
			try {
				FileUtil.createFile(backupfilepath, true);
				DeskSettingConstants.copyInputFile(file, backupfile, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void copyFolder(String srcFolderPath, String destFolderpath, boolean encrypt,
			int encryptByte) throws IOException {
		if (srcFolderPath == null || destFolderpath == null) {
			return;
		}
		File srcFolder = new File(srcFolderPath);
		if (srcFolder == null || !srcFolder.exists() || !srcFolder.isDirectory()) {
			return;
		}

		if (encryptByte < 0) {
			encryptByte = 0;
		}

		// 构造目标文件夹
		File destFolder = new File(destFolderpath);
		destFolder.mkdirs();

		File[] srcFolderFiles = null; // 源文件夹
		srcFolderFiles = srcFolder.listFiles();
		if (srcFolderFiles == null) {
			return;
		}

		int count = srcFolderFiles.length;
		File srcFile = null;
		File destFile = null;
		String fileName = null;
		for (int i = 0; i < count; i++) {
			srcFile = srcFolderFiles[i];
			if (srcFile.isFile()) {
				// 开始拷贝
				fileName = srcFile.getName();
				if (fileName.contains(GOOGLE_ANALYTICS_FILENAME)) {
					continue;
				}
				if (fileName.contains(StatisticsDataBaseHelper.getDBName())) {
					// 统计数据库不用拷贝
					continue;
				}
				if (fileName.contains(AppClassifyDatabaseHelper.getDBName())) {
					// 应用分类数据库不用拷贝
					continue;
				}
				destFile = new File(destFolderpath + "/" + fileName);
				if (destFile.exists()) {
					destFile.delete();
				}
				destFile.createNewFile();
				if (encrypt) {
					DeskSettingConstants.copyOutPutFile(srcFile, destFile, encryptByte);
				} else {
					DeskSettingConstants.copyInputFile(srcFile, destFile, encryptByte);
				}
			}
		}
	}
	
	/**
	 * 输入流转为String.<BR/>String 编码采用流里面默认的编码
	 * @param is
	 * @return
	 */
	public static String transferInputStreamToString(InputStream is) {
		return transferInputStreamToString(is, null, null);
	}

	/**
	 * 输入流转为String.
	 * @param is 
	 * @param charset 文本编码格式
	 * @return
	 */
	public static String transferInputStreamToString(InputStream is, String charset, Result result) {
		StringBuilder builder = new StringBuilder();
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {
			if (charset == null) {
				inputStreamReader = new InputStreamReader(is);
			} else {
				inputStreamReader = new InputStreamReader(is, charset);
			}
			reader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (result != null) {
				result.setStatus(HttpRequestStatus.REQUEST_FAILED);
				result.setErrorType(HttpRequestStatus.REQUEST_IO_EXCEPTION);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}
	/**
	 * <br>功能简述: 读取输入流，转为字符串
	 * @param in
	 * @param charset 字符格式
	 * @return
	 * @throws IOException
	 */
	public static String readInputStream(InputStream in, String charset) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		final int bufferLength = 1024;
		byte[] buf = new byte[bufferLength];
		int len = 0;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		byte[] data = out.toByteArray();
		in.close();
		out.close();
		return new String(data, charset);
	}
}
