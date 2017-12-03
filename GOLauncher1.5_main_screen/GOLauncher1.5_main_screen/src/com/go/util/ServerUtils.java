package com.go.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.go.util.device.Machine;
import com.jiubang.ggheart.launcher.LauncherEnv;

/**
 * 服务Url工具
 * @author yejijiong
 *
 */
public class ServerUtils {

	public static boolean isUseTestServer(String key) {
		boolean ret = false;
		if (LauncherEnv.SIT && Machine.isSDCardExist()) {
			File file = new File(LauncherEnv.Path.SERVER_CONFIG_PATH);
			Properties p = new Properties();
			if (file.exists()) {
				FileInputStream is = null;
				try {
					is = new FileInputStream(file);
					p.load(is);
					String value = p.getProperty(key, "false");
					ret = Boolean.parseBoolean(value);
				} catch (Exception e) {
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
			} else {
				file.getParentFile().mkdirs();
				FileOutputStream os = null;
				try {
					os = new FileOutputStream(file);
					p.put(LauncherEnv.Server.SIDE_ADVERT_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.MSG_CENTER_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.AUTO_VERSION_CHECK_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.GO_STORE_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.APP_CENTER_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.BETA_XML_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.ADVERT_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.STATISTICS_DATA_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.ADMOB_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.PAID_THEME_INFO_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.PROMOTION_XML_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.AD_HTTP_CONFIG_USE_TEST_SERVER, "false");
					p.put(LauncherEnv.Server.THEME_ICON_ISSUED_TEST_SERVER, "false");
					p.store(os, null);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
		return ret;
	}
	
}
