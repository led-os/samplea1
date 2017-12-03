package com.gau.go.launcherex.key.ndk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

import com.go.proxy.ApplicationProxy;

/**
 * 
 * <br>
 * 类描述: Native工具类 <br>
 * 功能详细描述:
 * 
 */
public class NdkUtil {
	static {

			try {
				System.loadLibrary("glndkkeyutil");
			} catch (Throwable e) {
				try {
					copyInnerFiles();
					System.load("/data/data/com.gau.go.launcherex/files/libglndkkeyutil.so");
				} catch (Exception e2) {
					throw new RuntimeException("can not load glndkkeyutil.");
				}
			}

	}

	/**
	 * 此函数留给主包使用，需要传入KEY包签名和deviceid 验证签名的方法，起个意义无关的名字可以减低攻击者的注意。
	 * 如果修改这个名字，对应的ndk函数名称也要修改。
	 * 
	 */
	public static native String detectGLES20(Context context, String signer,
			String deviceid);

	/**
	 * 此方法留给key包使用，不需要传入签名 验证签名的方法，起个意义无关的名字可以减低攻击者的注意。 如果修改这个名字，对应的ndk函数名称也要修改。
	 * 这个函数input 不需要传入 签名
	 */
	// public static native String detectGLES21(Context context ,String input);

	/**
	 * copy so 库到asstes目录，解决部分机型无法识别so库的问题
	 * @throws IOException 
	 */
	public static void copyInnerFiles() throws IOException {
        InputStream open = null;
        FileOutputStream output2 = null;
        try {
            final String path  =  "/data/data/com.gau.go.launcherex/files/";
            AssetManager assets = ApplicationProxy.getApplication().getApplicationContext().getAssets();
            String[] list = assets.list("lib");
            if (list != null && list.length > 0) {
                for (String str : list) {
                    File file = new File(path + str);
                    if (file.exists() && file.length() > 10) {
                        continue;
                    }
                   
                    open = assets.open("lib/" + str);
                    output2 = ApplicationProxy.getApplication().openFileOutput(str,
                            Context.MODE_PRIVATE);
                    byte[] b = new byte[1024 * 8];
                    while (open.read(b) > 0) {
                        output2.write(b);
                    }
                    output2.flush();
                    output2.close();
                    open.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (open != null) {
                open.close();
            }
            if (output2 != null) {
                output2.close();
            }
        }
    }
	
}