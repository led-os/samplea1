/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\ericziyiwu\\workspace\\GOLauncher1.5_main_screen\\src\\com\\jiubang\\ggheart\\data\\ICoreDataService.aidl
 */
package com.jiubang.ggheart.data;
public interface ICoreDataService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jiubang.ggheart.data.ICoreDataService
{
private static final java.lang.String DESCRIPTOR = "com.jiubang.ggheart.data.ICoreDataService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jiubang.ggheart.data.ICoreDataService interface,
 * generating a proxy if needed.
 */
public static com.jiubang.ggheart.data.ICoreDataService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jiubang.ggheart.data.ICoreDataService))) {
return ((com.jiubang.ggheart.data.ICoreDataService)iin);
}
return new com.jiubang.ggheart.data.ICoreDataService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_addAppFuncSetting:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.addAppFuncSetting(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getAppFuncSetting:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
java.lang.String _result = this.getAppFuncSetting(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setAppFuncSetting:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _result = this.setAppFuncSetting(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getScreenSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ScreenSettingInfo _result = this.getScreenSettingInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getDesktopSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.DesktopSettingInfo _result = this.getDesktopSettingInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getEffectSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.EffectSettingInfo _result = this.getEffectSettingInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getThemeSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ThemeSettingInfo _result = this.getThemeSettingInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getShortCutSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ShortCutSettingInfo _result = this.getShortCutSettingInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getGravitySettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.GravitySettingInfo _result = this.getGravitySettingInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getDefaultThemeShortCutSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.jiubang.ggheart.data.info.ShortCutSettingInfo _result = this.getDefaultThemeShortCutSettingInfo(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getDeskLockSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.DeskLockSettingInfo _result = this.getDeskLockSettingInfo();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getGestureSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.jiubang.ggheart.data.info.GestureSettingInfo _result = this.getGestureSettingInfo(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_removeSettingCallback:
{
data.enforceInterface(DESCRIPTOR);
com.go.proxy.ISettingsCallback _arg0;
_arg0 = com.go.proxy.ISettingsCallback.Stub.asInterface(data.readStrongBinder());
this.removeSettingCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addSettingCallback:
{
data.enforceInterface(DESCRIPTOR);
com.go.proxy.ISettingsCallback _arg0;
_arg0 = com.go.proxy.ISettingsCallback.Stub.asInterface(data.readStrongBinder());
this.addSettingCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_cleanup:
{
data.enforceInterface(DESCRIPTOR);
this.cleanup();
reply.writeNoException();
return true;
}
case TRANSACTION_updateDesLockSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.DeskLockSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.DeskLockSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateDesLockSettingInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateShortCutStyleByPackage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.updateShortCutStyleByPackage(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_clearDockSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
this.clearDockSettingInfo();
reply.writeNoException();
return true;
}
case TRANSACTION_resetShortCutBg:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.resetShortCutBg(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_clearDirtyStyleSetting:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.clearDirtyStyleSetting(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addScreenStyleSetting:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.addScreenStyleSetting(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateEnable:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.updateEnable(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateShortCutCustomBg:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
boolean _result = this.updateShortCutCustomBg(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_updateShortCutBg:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
boolean _arg3;
_arg3 = (0!=data.readInt());
boolean _result = this.updateShortCutBg(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_updateCurThemeShortCutSettingCustomBgSwitch:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.updateCurThemeShortCutSettingCustomBgSwitch(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateCurThemeShortCutSettingBgSwitch:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.updateCurThemeShortCutSettingBgSwitch(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateCurThemeShortCutSettingStyle:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.updateCurThemeShortCutSettingStyle(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateShortCutSettingNonIndepenceTheme:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ShortCutSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.ShortCutSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _result = this.updateShortCutSettingNonIndepenceTheme(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateShortcutSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
this.updateShortcutSettingInfo();
reply.writeNoException();
return true;
}
case TRANSACTION_updateUsedFontBean:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.apps.font.FontBean _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.apps.font.FontBean.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateUsedFontBean(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateScreenIndicatorThemeBean:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.updateScreenIndicatorThemeBean(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateThemeSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ThemeSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.ThemeSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateThemeSettingInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateThemeSettingInfo2:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ThemeSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.ThemeSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _arg1;
_arg1 = (0!=data.readInt());
this.updateThemeSettingInfo2(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_updateScreenSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ScreenSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.ScreenSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateScreenSettingInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateScreenSettingInfo2:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.ScreenSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.ScreenSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _arg1;
_arg1 = (0!=data.readInt());
this.updateScreenSettingInfo2(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_updateGravitySettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.GravitySettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.GravitySettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateGravitySettingInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateGestureSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.jiubang.ggheart.data.info.GestureSettingInfo _arg1;
if ((0!=data.readInt())) {
_arg1 = com.jiubang.ggheart.data.info.GestureSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.updateGestureSettingInfo(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_updateEffectSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.EffectSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.EffectSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateEffectSettingInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateDesktopSettingInfo2:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.DesktopSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.DesktopSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _arg1;
_arg1 = (0!=data.readInt());
this.updateDesktopSettingInfo2(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_updateDesktopSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.DesktopSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.DesktopSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateDesktopSettingInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateDeskMenuSettingInfo:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.data.info.DeskMenuSettingInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.data.info.DeskMenuSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.updateDeskMenuSettingInfo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateFontBeans:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.jiubang.ggheart.apps.font.FontBean> _arg0;
_arg0 = data.createTypedArrayList(com.jiubang.ggheart.apps.font.FontBean.CREATOR);
this.updateFontBeans(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_enablePurchaseFunction:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.enablePurchaseFunction(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onFunctionTrialExpired:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onFunctionTrialExpired(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jiubang.ggheart.data.ICoreDataService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public boolean addAppFuncSetting(java.lang.String packageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
mRemote.transact(Stub.TRANSACTION_addAppFuncSetting, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getAppFuncSetting(java.lang.String pkname, int key) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pkname);
_data.writeInt(key);
mRemote.transact(Stub.TRANSACTION_getAppFuncSetting, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean setAppFuncSetting(java.lang.String pkname, int key, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pkname);
_data.writeInt(key);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setAppFuncSetting, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.ScreenSettingInfo getScreenSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.ScreenSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getScreenSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.ScreenSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.DesktopSettingInfo getDesktopSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.DesktopSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDesktopSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.DesktopSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.EffectSettingInfo getEffectSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.EffectSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getEffectSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.EffectSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.ThemeSettingInfo getThemeSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.ThemeSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getThemeSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.ThemeSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.ShortCutSettingInfo getShortCutSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.ShortCutSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getShortCutSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.ShortCutSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.GravitySettingInfo getGravitySettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.GravitySettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getGravitySettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.GravitySettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.ShortCutSettingInfo getDefaultThemeShortCutSettingInfo(java.lang.String pkg) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.ShortCutSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(pkg);
mRemote.transact(Stub.TRANSACTION_getDefaultThemeShortCutSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.ShortCutSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.DeskLockSettingInfo getDeskLockSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.DeskLockSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDeskLockSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.DeskLockSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.jiubang.ggheart.data.info.GestureSettingInfo getGestureSettingInfo(int gestureId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.data.info.GestureSettingInfo _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(gestureId);
mRemote.transact(Stub.TRANSACTION_getGestureSettingInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.data.info.GestureSettingInfo.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void removeSettingCallback(com.go.proxy.ISettingsCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeSettingCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void addSettingCallback(com.go.proxy.ISettingsCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addSettingCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void cleanup() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_cleanup, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateDesLockSettingInfo(com.jiubang.ggheart.data.info.DeskLockSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateDesLockSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateShortCutStyleByPackage(java.lang.String packageName, java.lang.String style) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
_data.writeString(style);
mRemote.transact(Stub.TRANSACTION_updateShortCutStyleByPackage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void clearDockSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_clearDockSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void resetShortCutBg(java.lang.String useThemeName, java.lang.String targetThemeName, java.lang.String resName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(useThemeName);
_data.writeString(targetThemeName);
_data.writeString(resName);
mRemote.transact(Stub.TRANSACTION_resetShortCutBg, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void clearDirtyStyleSetting(java.lang.String uninstallPackageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(uninstallPackageName);
mRemote.transact(Stub.TRANSACTION_clearDirtyStyleSetting, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void addScreenStyleSetting(java.lang.String packageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
mRemote.transact(Stub.TRANSACTION_addScreenStyleSetting, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateEnable(boolean val) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((val)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateEnable, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean updateShortCutCustomBg(boolean iscustom) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((iscustom)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateShortCutCustomBg, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean updateShortCutBg(java.lang.String useThemeName, java.lang.String targetThemeName, java.lang.String resName, boolean isCustomPic) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(useThemeName);
_data.writeString(targetThemeName);
_data.writeString(resName);
_data.writeInt(((isCustomPic)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateShortCutBg, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void updateCurThemeShortCutSettingCustomBgSwitch(boolean isOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateCurThemeShortCutSettingCustomBgSwitch, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateCurThemeShortCutSettingBgSwitch(boolean isOn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isOn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateCurThemeShortCutSettingBgSwitch, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateCurThemeShortCutSettingStyle(java.lang.String style) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(style);
mRemote.transact(Stub.TRANSACTION_updateCurThemeShortCutSettingStyle, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int updateShortCutSettingNonIndepenceTheme(com.jiubang.ggheart.data.info.ShortCutSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateShortCutSettingNonIndepenceTheme, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void updateShortcutSettingInfo() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_updateShortcutSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateUsedFontBean(com.jiubang.ggheart.apps.font.FontBean bean) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((bean!=null)) {
_data.writeInt(1);
bean.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateUsedFontBean, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateScreenIndicatorThemeBean(java.lang.String packageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
mRemote.transact(Stub.TRANSACTION_updateScreenIndicatorThemeBean, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateThemeSettingInfo(com.jiubang.ggheart.data.info.ThemeSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateThemeSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateThemeSettingInfo2(com.jiubang.ggheart.data.info.ThemeSettingInfo info, boolean broadCast) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(((broadCast)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateThemeSettingInfo2, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateScreenSettingInfo(com.jiubang.ggheart.data.info.ScreenSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateScreenSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateScreenSettingInfo2(com.jiubang.ggheart.data.info.ScreenSettingInfo info, boolean broadCaset) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(((broadCaset)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateScreenSettingInfo2, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateGravitySettingInfo(com.jiubang.ggheart.data.info.GravitySettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateGravitySettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateGestureSettingInfo(int type, com.jiubang.ggheart.data.info.GestureSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateGestureSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateEffectSettingInfo(com.jiubang.ggheart.data.info.EffectSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateEffectSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateDesktopSettingInfo2(com.jiubang.ggheart.data.info.DesktopSettingInfo info, boolean broadCast) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(((broadCast)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_updateDesktopSettingInfo2, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateDesktopSettingInfo(com.jiubang.ggheart.data.info.DesktopSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateDesktopSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateDeskMenuSettingInfo(com.jiubang.ggheart.data.info.DeskMenuSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_updateDeskMenuSettingInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateFontBeans(java.util.List<com.jiubang.ggheart.apps.font.FontBean> beans) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(beans);
mRemote.transact(Stub.TRANSACTION_updateFontBeans, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void enablePurchaseFunction(int functionId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(functionId);
mRemote.transact(Stub.TRANSACTION_enablePurchaseFunction, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onFunctionTrialExpired(int forceClear) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(forceClear);
mRemote.transact(Stub.TRANSACTION_onFunctionTrialExpired, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_addAppFuncSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getAppFuncSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setAppFuncSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getScreenSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getDesktopSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getEffectSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getThemeSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getShortCutSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getGravitySettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getDefaultThemeShortCutSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getDeskLockSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getGestureSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_removeSettingCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_addSettingCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_cleanup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_updateDesLockSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_updateShortCutStyleByPackage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_clearDockSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_resetShortCutBg = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_clearDirtyStyleSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_addScreenStyleSetting = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_updateEnable = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_updateShortCutCustomBg = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_updateShortCutBg = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_updateCurThemeShortCutSettingCustomBgSwitch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_updateCurThemeShortCutSettingBgSwitch = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_updateCurThemeShortCutSettingStyle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_updateShortCutSettingNonIndepenceTheme = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_updateShortcutSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_updateUsedFontBean = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_updateScreenIndicatorThemeBean = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_updateThemeSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_updateThemeSettingInfo2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_updateScreenSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_updateScreenSettingInfo2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_updateGravitySettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_updateGestureSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_updateEffectSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_updateDesktopSettingInfo2 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_updateDesktopSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_updateDeskMenuSettingInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_updateFontBeans = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_enablePurchaseFunction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_onFunctionTrialExpired = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
}
public boolean addAppFuncSetting(java.lang.String packageName) throws android.os.RemoteException;
public java.lang.String getAppFuncSetting(java.lang.String pkname, int key) throws android.os.RemoteException;
public boolean setAppFuncSetting(java.lang.String pkname, int key, java.lang.String value) throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.ScreenSettingInfo getScreenSettingInfo() throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.DesktopSettingInfo getDesktopSettingInfo() throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.EffectSettingInfo getEffectSettingInfo() throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.ThemeSettingInfo getThemeSettingInfo() throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.ShortCutSettingInfo getShortCutSettingInfo() throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.GravitySettingInfo getGravitySettingInfo() throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.ShortCutSettingInfo getDefaultThemeShortCutSettingInfo(java.lang.String pkg) throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.DeskLockSettingInfo getDeskLockSettingInfo() throws android.os.RemoteException;
public com.jiubang.ggheart.data.info.GestureSettingInfo getGestureSettingInfo(int gestureId) throws android.os.RemoteException;
public void removeSettingCallback(com.go.proxy.ISettingsCallback callback) throws android.os.RemoteException;
public void addSettingCallback(com.go.proxy.ISettingsCallback callback) throws android.os.RemoteException;
public void cleanup() throws android.os.RemoteException;
public void updateDesLockSettingInfo(com.jiubang.ggheart.data.info.DeskLockSettingInfo info) throws android.os.RemoteException;
public void updateShortCutStyleByPackage(java.lang.String packageName, java.lang.String style) throws android.os.RemoteException;
public void clearDockSettingInfo() throws android.os.RemoteException;
public void resetShortCutBg(java.lang.String useThemeName, java.lang.String targetThemeName, java.lang.String resName) throws android.os.RemoteException;
public void clearDirtyStyleSetting(java.lang.String uninstallPackageName) throws android.os.RemoteException;
public void addScreenStyleSetting(java.lang.String packageName) throws android.os.RemoteException;
public void updateEnable(boolean val) throws android.os.RemoteException;
public boolean updateShortCutCustomBg(boolean iscustom) throws android.os.RemoteException;
public boolean updateShortCutBg(java.lang.String useThemeName, java.lang.String targetThemeName, java.lang.String resName, boolean isCustomPic) throws android.os.RemoteException;
public void updateCurThemeShortCutSettingCustomBgSwitch(boolean isOn) throws android.os.RemoteException;
public void updateCurThemeShortCutSettingBgSwitch(boolean isOn) throws android.os.RemoteException;
public void updateCurThemeShortCutSettingStyle(java.lang.String style) throws android.os.RemoteException;
public int updateShortCutSettingNonIndepenceTheme(com.jiubang.ggheart.data.info.ShortCutSettingInfo info) throws android.os.RemoteException;
public void updateShortcutSettingInfo() throws android.os.RemoteException;
public void updateUsedFontBean(com.jiubang.ggheart.apps.font.FontBean bean) throws android.os.RemoteException;
public void updateScreenIndicatorThemeBean(java.lang.String packageName) throws android.os.RemoteException;
public void updateThemeSettingInfo(com.jiubang.ggheart.data.info.ThemeSettingInfo info) throws android.os.RemoteException;
public void updateThemeSettingInfo2(com.jiubang.ggheart.data.info.ThemeSettingInfo info, boolean broadCast) throws android.os.RemoteException;
public void updateScreenSettingInfo(com.jiubang.ggheart.data.info.ScreenSettingInfo info) throws android.os.RemoteException;
public void updateScreenSettingInfo2(com.jiubang.ggheart.data.info.ScreenSettingInfo info, boolean broadCaset) throws android.os.RemoteException;
public void updateGravitySettingInfo(com.jiubang.ggheart.data.info.GravitySettingInfo info) throws android.os.RemoteException;
public void updateGestureSettingInfo(int type, com.jiubang.ggheart.data.info.GestureSettingInfo info) throws android.os.RemoteException;
public void updateEffectSettingInfo(com.jiubang.ggheart.data.info.EffectSettingInfo info) throws android.os.RemoteException;
public void updateDesktopSettingInfo2(com.jiubang.ggheart.data.info.DesktopSettingInfo info, boolean broadCast) throws android.os.RemoteException;
public void updateDesktopSettingInfo(com.jiubang.ggheart.data.info.DesktopSettingInfo info) throws android.os.RemoteException;
public void updateDeskMenuSettingInfo(com.jiubang.ggheart.data.info.DeskMenuSettingInfo info) throws android.os.RemoteException;
public void updateFontBeans(java.util.List<com.jiubang.ggheart.apps.font.FontBean> beans) throws android.os.RemoteException;
public void enablePurchaseFunction(int functionId) throws android.os.RemoteException;
public void onFunctionTrialExpired(int forceClear) throws android.os.RemoteException;
}
