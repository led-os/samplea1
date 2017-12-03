/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\ericziyiwu\\workspace\\GOLauncher1.5_main_screen\\src\\com\\jiubang\\ggheart\\appgame\\download\\IDownloadService.aidl
 */
package com.jiubang.ggheart.appgame.download;
public interface IDownloadService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jiubang.ggheart.appgame.download.IDownloadService
{
private static final java.lang.String DESCRIPTOR = "com.jiubang.ggheart.appgame.download.IDownloadService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jiubang.ggheart.appgame.download.IDownloadService interface,
 * generating a proxy if needed.
 */
public static com.jiubang.ggheart.appgame.download.IDownloadService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jiubang.ggheart.appgame.download.IDownloadService))) {
return ((com.jiubang.ggheart.appgame.download.IDownloadService)iin);
}
return new com.jiubang.ggheart.appgame.download.IDownloadService.Stub.Proxy(obj);
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
case TRANSACTION_addDownloadTask:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
long _result = this.addDownloadTask(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_startDownload:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.startDownload(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopDownloadById:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.stopDownloadById(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_restartDownloadById:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.restartDownloadById(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDownloadConcurrentHashMap:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _result = this.getDownloadConcurrentHashMap();
reply.writeNoException();
reply.writeMap(_result);
return true;
}
case TRANSACTION_removeDownloadTaskById:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.removeDownloadTaskById(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeDownloadTasksById:
{
data.enforceInterface(DESCRIPTOR);
java.util.List _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readArrayList(cl);
this.removeDownloadTasksById(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDownloadTaskById:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
com.jiubang.ggheart.appgame.download.DownloadTask _result = this.getDownloadTaskById(_arg0);
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
case TRANSACTION_removeTaskIdFromDownloading:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.removeTaskIdFromDownloading(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeListener:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.removeListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addDownloadTaskListener:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
com.jiubang.ggheart.appgame.download.IAidlDownloadListener _arg1;
_arg1 = com.jiubang.ggheart.appgame.download.IAidlDownloadListener.Stub.asInterface(data.readStrongBinder());
long _result = this.addDownloadTaskListener(_arg0, _arg1);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_addDownloadTaskListenerByName:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
long _result = this.addDownloadTaskListenerByName(_arg0, _arg1);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_removeDownloadTaskListener:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
long _arg1;
_arg1 = data.readLong();
this.removeDownloadTaskListener(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_removAllDownloadTaskListeners:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.removAllDownloadTaskListeners(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getCompleteIdsByPkgName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.util.List _result = this.getCompleteIdsByPkgName(_arg0);
reply.writeNoException();
reply.writeList(_result);
return true;
}
case TRANSACTION_getDownloadCompleteList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List _result = this.getDownloadCompleteList();
reply.writeNoException();
reply.writeList(_result);
return true;
}
case TRANSACTION_removeDownloadCompleteItem:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.removeDownloadCompleteItem(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDownloadingTaskSortByTime:
{
data.enforceInterface(DESCRIPTOR);
java.util.List _result = this.getDownloadingTaskSortByTime();
reply.writeNoException();
reply.writeList(_result);
return true;
}
case TRANSACTION_addInstalledPackage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.addInstalledPackage(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getInstalledTaskList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List _result = this.getInstalledTaskList();
reply.writeNoException();
reply.writeList(_result);
return true;
}
case TRANSACTION_addRunningActivityClassName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.addRunningActivityClassName(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jiubang.ggheart.appgame.download.IDownloadService
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
@Override public long addDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((task!=null)) {
_data.writeInt(1);
task.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_addDownloadTask, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void startDownload(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_startDownload, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopDownloadById(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_stopDownloadById, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void restartDownloadById(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_restartDownloadById, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.Map getDownloadConcurrentHashMap() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDownloadConcurrentHashMap, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void removeDownloadTaskById(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_removeDownloadTaskById, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeDownloadTasksById(java.util.List list) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeList(list);
mRemote.transact(Stub.TRANSACTION_removeDownloadTasksById, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public com.jiubang.ggheart.appgame.download.DownloadTask getDownloadTaskById(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.jiubang.ggheart.appgame.download.DownloadTask _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_getDownloadTaskById, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(_reply);
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
@Override public void removeTaskIdFromDownloading(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_removeTaskIdFromDownloading, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeListener(long id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(id);
mRemote.transact(Stub.TRANSACTION_removeListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public long addDownloadTaskListener(long taskId, com.jiubang.ggheart.appgame.download.IAidlDownloadListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addDownloadTaskListener, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public long addDownloadTaskListenerByName(long taskId, java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_addDownloadTaskListenerByName, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void removeDownloadTaskListener(long taskId, long listenerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
_data.writeLong(listenerId);
mRemote.transact(Stub.TRANSACTION_removeDownloadTaskListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removAllDownloadTaskListeners(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_removAllDownloadTaskListeners, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List getCompleteIdsByPkgName(java.lang.String packageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
mRemote.transact(Stub.TRANSACTION_getCompleteIdsByPkgName, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readArrayList(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List getDownloadCompleteList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDownloadCompleteList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readArrayList(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void removeDownloadCompleteItem(long taskId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(taskId);
mRemote.transact(Stub.TRANSACTION_removeDownloadCompleteItem, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List getDownloadingTaskSortByTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDownloadingTaskSortByTime, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readArrayList(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addInstalledPackage(java.lang.String packageName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
mRemote.transact(Stub.TRANSACTION_addInstalledPackage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.util.List getInstalledTaskList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getInstalledTaskList, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readArrayList(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addRunningActivityClassName(java.lang.String className) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(className);
mRemote.transact(Stub.TRANSACTION_addRunningActivityClassName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_addDownloadTask = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopDownloadById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_restartDownloadById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getDownloadConcurrentHashMap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_removeDownloadTaskById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_removeDownloadTasksById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getDownloadTaskById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_removeTaskIdFromDownloading = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_removeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_addDownloadTaskListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_addDownloadTaskListenerByName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_removeDownloadTaskListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_removAllDownloadTaskListeners = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getCompleteIdsByPkgName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getDownloadCompleteList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_removeDownloadCompleteItem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getDownloadingTaskSortByTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_addInstalledPackage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_getInstalledTaskList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_addRunningActivityClassName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
}
public long addDownloadTask(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void startDownload(long taskId) throws android.os.RemoteException;
public void stopDownloadById(long taskId) throws android.os.RemoteException;
public void restartDownloadById(long taskId) throws android.os.RemoteException;
public java.util.Map getDownloadConcurrentHashMap() throws android.os.RemoteException;
public void removeDownloadTaskById(long taskId) throws android.os.RemoteException;
public void removeDownloadTasksById(java.util.List list) throws android.os.RemoteException;
public com.jiubang.ggheart.appgame.download.DownloadTask getDownloadTaskById(long taskId) throws android.os.RemoteException;
public void removeTaskIdFromDownloading(long taskId) throws android.os.RemoteException;
public void removeListener(long id) throws android.os.RemoteException;
public long addDownloadTaskListener(long taskId, com.jiubang.ggheart.appgame.download.IAidlDownloadListener listener) throws android.os.RemoteException;
public long addDownloadTaskListenerByName(long taskId, java.lang.String name) throws android.os.RemoteException;
public void removeDownloadTaskListener(long taskId, long listenerId) throws android.os.RemoteException;
public void removAllDownloadTaskListeners(long taskId) throws android.os.RemoteException;
public java.util.List getCompleteIdsByPkgName(java.lang.String packageName) throws android.os.RemoteException;
public java.util.List getDownloadCompleteList() throws android.os.RemoteException;
public void removeDownloadCompleteItem(long taskId) throws android.os.RemoteException;
public java.util.List getDownloadingTaskSortByTime() throws android.os.RemoteException;
public void addInstalledPackage(java.lang.String packageName) throws android.os.RemoteException;
public java.util.List getInstalledTaskList() throws android.os.RemoteException;
public void addRunningActivityClassName(java.lang.String className) throws android.os.RemoteException;
}
