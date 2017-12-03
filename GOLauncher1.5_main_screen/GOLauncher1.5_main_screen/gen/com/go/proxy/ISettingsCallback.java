/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\ericziyiwu\\workspace\\GOLauncher1.5_main_screen\\src\\com\\go\\proxy\\ISettingsCallback.aidl
 */
package com.go.proxy;
public interface ISettingsCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.go.proxy.ISettingsCallback
{
private static final java.lang.String DESCRIPTOR = "com.go.proxy.ISettingsCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.go.proxy.ISettingsCallback interface,
 * generating a proxy if needed.
 */
public static com.go.proxy.ISettingsCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.go.proxy.ISettingsCallback))) {
return ((com.go.proxy.ISettingsCallback)iin);
}
return new com.go.proxy.ISettingsCallback.Stub.Proxy(obj);
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
case TRANSACTION_settingsChagedInt:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.settingsChagedInt(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_settingsChagedBoolean:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.settingsChagedBoolean(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_settingsChagedString:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.settingsChagedString(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_settingsChagedBundle:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.settingsChagedBundle(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_broadCastEffectChanged:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
com.jiubang.ggheart.data.info.EffectSettingInfo _arg2;
if ((0!=data.readInt())) {
_arg2 = com.jiubang.ggheart.data.info.EffectSettingInfo.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
this.broadCastEffectChanged(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.go.proxy.ISettingsCallback
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
@Override public void settingsChagedInt(int settingId, int value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(settingId);
_data.writeInt(value);
mRemote.transact(Stub.TRANSACTION_settingsChagedInt, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void settingsChagedBoolean(int settingId, boolean value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(settingId);
_data.writeInt(((value)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_settingsChagedBoolean, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void settingsChagedString(int settingId, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(settingId);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_settingsChagedString, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void settingsChagedBundle(int settingId, android.os.Bundle value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(settingId);
if ((value!=null)) {
_data.writeInt(1);
value.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_settingsChagedBundle, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void broadCastEffectChanged(int msgId, int param, com.jiubang.ggheart.data.info.EffectSettingInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(msgId);
_data.writeInt(param);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_broadCastEffectChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_settingsChagedInt = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_settingsChagedBoolean = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_settingsChagedString = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_settingsChagedBundle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_broadCastEffectChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void settingsChagedInt(int settingId, int value) throws android.os.RemoteException;
public void settingsChagedBoolean(int settingId, boolean value) throws android.os.RemoteException;
public void settingsChagedString(int settingId, java.lang.String value) throws android.os.RemoteException;
public void settingsChagedBundle(int settingId, android.os.Bundle value) throws android.os.RemoteException;
public void broadCastEffectChanged(int msgId, int param, com.jiubang.ggheart.data.info.EffectSettingInfo info) throws android.os.RemoteException;
}
