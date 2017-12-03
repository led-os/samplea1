package com.go.proxy;

import android.os.Bundle;

import com.jiubang.ggheart.data.info.EffectSettingInfo;

/**
 * 
 * @author liuheng
 *
 */
public interface SettingsCallback {
	public void settingsChagedInt(int settingId, int value);
	public void settingsChagedBoolean(int settingId, boolean value);
	public void settingsChagedString(int settingId, String value);
	public void settingsChagedBundle(int settingId, Bundle value);
	public void broadCastEffectChanged(int msgId, int param, EffectSettingInfo info);
}
