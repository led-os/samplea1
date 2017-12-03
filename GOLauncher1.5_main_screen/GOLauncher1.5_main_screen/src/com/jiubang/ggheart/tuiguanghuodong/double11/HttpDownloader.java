package com.jiubang.ggheart.tuiguanghuodong.double11;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.go.util.file.FileUtil;

/**
 * 文件下载器
 * 
 * @author chenshihang
 * 
 */
public class HttpDownloader {

	private HttpURLConnection mUrlConn;
	
	private boolean mIsOverrided = false;

	public HttpDownloader() {
		
	}
	
	/**
	 * 判断文件是否要覆盖
	 * @param isOverrided
	 */
	public void setIsOverrided(boolean isOverrided) {
		mIsOverrided = isOverrided;
	}

	public boolean downfile(String urlStr, String path, String fileName) {
		boolean result = false;
		if (FileUtil.isFileExist(path + "/" + fileName)) {
			if (!mIsOverrided) {
				// 不需要覆盖，直接返回为true
				return true;
			}
		}

		try {
			InputStream input = null;
			input = getInputStream(urlStr);
			File resultFile = write2SDFromInput(path, fileName, input);
			if (resultFile != null) {
				result = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (mUrlConn != null) {
				mUrlConn.disconnect();
			}
		}
		return result;
	}

	public InputStream getInputStream(String urlStr) throws IOException {
		InputStream is = null;
		try {
			URL url = new URL(urlStr);
			mUrlConn = (HttpURLConnection) url
					.openConnection();
			mUrlConn.setReadTimeout(30000);
			mUrlConn.setConnectTimeout(30000);
			is = mUrlConn.getInputStream();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return is;
	}

	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			file = new File(path, fileName);
			File parentFile = new File(path);
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			
			int size = 0;
			int fileSize = 0;
			if (mUrlConn != null) {
				fileSize = mUrlConn.getContentLength();
			}
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			
			int len;
			while ((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
				size += len;
			}
			output.flush();
			//数据未写完，删除不完整的文件
			if (size != fileSize) {
				file.delete();
				file = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
