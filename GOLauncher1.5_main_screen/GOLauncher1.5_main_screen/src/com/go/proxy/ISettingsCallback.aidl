package com.go.proxy;

import com.jiubang.ggheart.data.info.EffectSettingInfo;

interface ISettingsCallback {
	void settingsChagedInt(int settingId, int value);
	void settingsChagedBoolean(int settingId, boolean value);
	void settingsChagedString(int settingId, String value);
	void settingsChagedBundle(int settingId, in Bundle value);
	void broadCastEffectChanged(int msgId, int param, in EffectSettingInfo info);
}