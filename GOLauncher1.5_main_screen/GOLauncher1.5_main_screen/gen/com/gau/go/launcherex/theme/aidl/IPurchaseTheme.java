/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\ericziyiwu\\workspace\\GOLauncher1.5_main_screen\\src\\com\\gau\\go\\launcherex\\theme\\aidl\\IPurchaseTheme.aidl
 */
package com.gau.go.launcherex.theme.aidl;
/**
 * 
 * <br>类描述:getjar主题AIDL接口
 * <br>功能详细描述:
 * 
 * @author  rongjinsong
 * @date  [2014年3月20日]
 */
public interface IPurchaseTheme extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.gau.go.launcherex.theme.aidl.IPurchaseTheme
{
private static final java.lang.String DESCRIPTOR = "com.gau.go.launcherex.theme.aidl.IPurchaseTheme";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.gau.go.launcherex.theme.aidl.IPurchaseTheme interface,
 * generating a proxy if needed.
 */
public static com.gau.go.launcherex.theme.aidl.IPurchaseTheme asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.gau.go.launcherex.theme.aidl.IPurchaseTheme))) {
return ((com.gau.go.launcherex.theme.aidl.IPurchaseTheme)iin);
}
return new com.gau.go.launcherex.theme.aidl.IPurchaseTheme.Stub.Proxy(obj);
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
case TRANSACTION_getPurchaseStatus:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPurchaseStatus();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startPurchase:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.startPurchase();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.gau.go.launcherex.theme.aidl.IPurchaseTheme
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
@Override public int getPurchaseStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPurchaseStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String startPurchase() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startPurchase, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getPurchaseStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startPurchase = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public int getPurchaseStatus() throws android.os.RemoteException;
public java.lang.String startPurchase() throws android.os.RemoteException;
}
