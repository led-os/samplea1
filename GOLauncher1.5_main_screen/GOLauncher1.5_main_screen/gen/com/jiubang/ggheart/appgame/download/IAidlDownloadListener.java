/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\ericziyiwu\\workspace\\GOLauncher1.5_main_screen\\src\\com\\jiubang\\ggheart\\appgame\\download\\IAidlDownloadListener.aidl
 */
package com.jiubang.ggheart.appgame.download;
public interface IAidlDownloadListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jiubang.ggheart.appgame.download.IAidlDownloadListener
{
private static final java.lang.String DESCRIPTOR = "com.jiubang.ggheart.appgame.download.IAidlDownloadListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jiubang.ggheart.appgame.download.IAidlDownloadListener interface,
 * generating a proxy if needed.
 */
public static com.jiubang.ggheart.appgame.download.IAidlDownloadListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jiubang.ggheart.appgame.download.IAidlDownloadListener))) {
return ((com.jiubang.ggheart.appgame.download.IAidlDownloadListener)iin);
}
return new com.jiubang.ggheart.appgame.download.IAidlDownloadListener.Stub.Proxy(obj);
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
case TRANSACTION_onStart:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onStart(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onWait:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onWait(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onUpdate:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onUpdate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onComplete:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onComplete(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onFail:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onFail(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onReset:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onReset(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onStop:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onStop(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onCancel:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onCancel(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onDestroy:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onDestroy(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onConnectionSuccess:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onConnectionSuccess(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onException:
{
data.enforceInterface(DESCRIPTOR);
com.jiubang.ggheart.appgame.download.DownloadTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.jiubang.ggheart.appgame.download.DownloadTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onException(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jiubang.ggheart.appgame.download.IAidlDownloadListener
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
@Override public void onStart(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onWait(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onWait, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onUpdate(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onComplete(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onComplete, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onFail(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onFail, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onReset(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onReset, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onStop(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onStop, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onCancel(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onCancel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onDestroy(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onDestroy, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onConnectionSuccess(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onConnectionSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onException(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException
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
mRemote.transact(Stub.TRANSACTION_onException, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onWait = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onComplete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onFail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onReset = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onStop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onCancel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onDestroy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onConnectionSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onException = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
}
public void onStart(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onWait(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onUpdate(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onComplete(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onFail(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onReset(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onStop(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onCancel(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onDestroy(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onConnectionSuccess(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
public void onException(com.jiubang.ggheart.appgame.download.DownloadTask task) throws android.os.RemoteException;
}
