package com.jiubang.ggheart.components.gohandbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.gau.utils.net.operator.IHttpOperator;
import com.gau.utils.net.request.THttpRequest;
import com.gau.utils.net.response.BasicResponse;
import com.gau.utils.net.response.IResponse;

/**
 * 解析NET请求返回的数据,先用gzip解压再转换成字符串
 * @author zouguiquan
 *
 */
public class StringGzipOperator implements IHttpOperator {

	private final static int BYTE_SIZE = 1024;

	@Override
	public IResponse operateHttpResponse(THttpRequest request, HttpResponse response)
			throws IllegalStateException, IOException {

		String content = null;
		HttpEntity entity = response.getEntity();
		//进行解压
		InputStream is = new GZIPInputStream(entity.getContent());
		ByteArrayOutputStream baopt = new ByteArrayOutputStream();
		byte[] buff = new byte[BYTE_SIZE];
		int len = -1;

		while ((len = is.read(buff)) != -1) {
			baopt.write(buff, 0, len);
		}

		content = new String(baopt.toByteArray(), "utf-8");
		BasicResponse ret = new BasicResponse(IResponse.RESPONSE_TYPE_STRING, content);
		return ret;
	}
}
