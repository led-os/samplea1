/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\ericziyiwu\\workspace\\GOLauncher1.5_main_screen\\src\\com\\jiubang\\ggheart\\appgame\\download\\IAidlDownloadManagerListener.aidl
 */
package com.jiubang.ggheart.appgame.download;
public interface IAidlDownloadManagerListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener
{
private static final java.lang.String DESCRIPTOR = "com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener interface,
 * generating a proxy if needed.
 */
public static com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener))) {
return ((com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener)iin);
}
return new com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener.Stub.Proxy(obj);
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
case TRANSACTION_onStartDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onStartDownloadTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onRemoveDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onRemoveDownloadTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onRestartDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onRestartDownloadTask(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onFailDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onFailDownloadTask(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jiubang.ggheart.appgame.download.IAidlDownloadManagerListener
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
@Override public void onStartDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((task!=null)) {
_data.writeInt(1);
task.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onStartDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onRemoveDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((task!=null)) {
_data.writeInt(1);
task.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onRemoveDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onRestartDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((task!=null)) {
_data.writeInt(1);
task.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onRestartDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onFailDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((task!=null)) {
_data.writeInt(1);
task.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onFailDownloadTask, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onStartDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onRemoveDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onRestartDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onFailDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void onStartDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onRemoveDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onRestartDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onFailDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
}
