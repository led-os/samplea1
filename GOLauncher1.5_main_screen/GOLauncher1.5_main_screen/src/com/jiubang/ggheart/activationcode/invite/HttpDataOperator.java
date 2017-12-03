/**
 * 
 */
package com.jiubang.ggheart.activationcode.invite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gau.utils.net.operator.IHttpOperator;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.BasicResponse;
import com.gau.utils.net.response.IResponse;
import com.jiubang.ggheart.appgame.appcenter.help.RecommAppFileUtil;
import com.jiubang.ggheart.appgame.base.data.AppGameNetLogControll;
import com.jiubang.ggheart.appgame.base.data.ClassificationExceptionRecord;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.ConstValue;
import com.jiubang.ggheart.apps.desks.diy.messagecenter.Beans.MessageListBean;

/**
 * 将服务端返回的数据转换为Json后字符串格式
 * @author caoyaming
 *
 */
public class HttpDataOperator implements IHttpOperator {
	private boolean mIsZipData = false;
	public HttpDataOperator(boolean isZipData) {
		mIsZipData = isZipData;
	}
	@Override
	public IResponse operateHttpResponse(THttpRequest request, HttpResponse response)
			throws IllegalStateException, IOException {
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		Object object = parseMsgListStreamData(is, mIsZipData);
		BasicResponse iResponse = new BasicResponse(object != null && object instanceof JSONObject ? IResponse.RESPONSE_TYPE_JSONOBJECT : IResponse.RESPONSE_TYPE_STRING, object);
		return iResponse;
	}
	/**
	 * 将服务端返回的数据转换为Json或字符串格式
	 * @param in
	 * @param isZipData
	 * @return
	 */
	public static Object parseMsgListStreamData(final InputStream in, final boolean isZipData) {
		if (in == null) {
			return null;
		}
		try {
			String jsonString = null;
			if (isZipData) {
				jsonString = RecommAppFileUtil.unzipDataAndLog(in);
			} else {
				long time = System.currentTimeMillis();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				int len = -1;
				try {
					while ((len = in.read(buff)) != -1) {
						buffer.write(buff, 0, len);
					}
				} catch (IOException e) {
					e.printStackTrace();
					ClassificationExceptionRecord.getInstance().record(e);
					// 记录异常信息，同时保存网络信息
					AppGameNetLogControll.getInstance().setExceptionCode(AppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE, e);
					return null;
				}

				byte[] data = buffer.toByteArray();
				// 统计下载速度 old_bytes/time2
				long time2 = System.currentTimeMillis() - time;
				if (time2 > 0) {
					String speed = String.valueOf(data.length / time2);
					AppGameNetLogControll.getInstance().setDownloadSpeed(
							AppGameNetLogControll.DEFAULT_CURRENT_THREAD_CODE, speed);
				}
				jsonString = new String(data);
			}
			if (jsonString != null) {
				// Log.d(TAG, "list:" + jsonString);
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(jsonString);
				} catch (Exception e) {
					return jsonString;
				}
				JSONObject result = jsonObject.getJSONObject(MessageListBean.TAG_RESULT);
				int status = result.getInt(MessageListBean.TAG_STATUS);
				if (status == ConstValue.STATTUS_OK) {
					// 解析数据
					return jsonObject;
				} else {
					Log.i("DownloadUtil", "parseMsgListStreamData result = " + result);
					// 记录错误信息
					ClassificationExceptionRecord.getInstance().record("服务器数据异常：" + result.toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ClassificationExceptionRecord.getInstance().record(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}
}
