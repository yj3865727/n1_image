package com.lequan.n1.protocol;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;

import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HttpRequestProxy {

	private static final String TAG = "HttpRequestProxy";
	public static final int ImagePathTypeUserHeadImage = 0;
	public static final int ImagePathTypeUserWorks = 1;

	/**
	 * 发送异步post请求
	 * 
	 * @param url
	 *            地址
	 * @param queryParams
	 *            参数
	 * @param callBack
	 *            回调函数
	 */
	public static void sendAsyncPost(String url, Map<?, ?> queryParams, RequestCallBack<?> callBack) throws Exception {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configTimeout(5000);
		RequestParams params = new RequestParams();
		params.addHeader("content-type", "application/json");
		params.addHeader("Accept", "*/*");
		Gson gson = new Gson();
		String s = gson.toJson(queryParams);
		LogUtils.i(TAG, s);
		params.setBodyEntity(new StringEntity(s, "UTF-8"));
		httpUtils.send(HttpMethod.POST, url, params, callBack);
	}

	/**
	 * 发送同步的post请求
	 * 
	 * @param url
	 * @param queryParams
	 * @param callBack
	 * @return
	 * @throws Exception
	 */
	public static ResponseStream sendSyncPost(String url, Map<?, ?> queryParams) throws Exception {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configTimeout(5000);
		RequestParams params = new RequestParams();
		params.addHeader("content-type", "application/json");
		params.addHeader("Accept", "*/*");
		Gson gson = new Gson();
		String s = gson.toJson(queryParams);
		LogUtils.i(TAG, s);
		params.setBodyEntity(new StringEntity(s, "UTF-8"));
		ResponseStream responseStream = httpUtils.sendSync(HttpMethod.POST, url, params);
		return responseStream;
	}

	/**
	 * 异步上传文件 TODO 处理大图片
	 */
	public static void uploadFileAsync(int uploadType, List<File> files, RequestCallBack<String> callBack) {
		if(files!=null&&files.size()>0){
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inJustDecodeBounds=true;
			BitmapFactory.decodeFile(files.get(0).getAbsolutePath(),options);
			int width = options.outWidth;
			int height = options.outHeight;
			String url = Constants.Url.IMAGE_URL + "?tofile=" + getSpecialPath(uploadType)
			+ "&bucketName=fashionshowimage&condition=@w"+width+"_h"+height+"m";
			//上传图片
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.configTimeout(5*60*1000);
			RequestParams params = new RequestParams();
			params.addHeader("User-Agent", "fashionshow");
			for (int i = 0; i < files.size(); i++) {
				params.addBodyParameter("files" + i, files.get(i));
			}
			httpUtils.send(HttpMethod.POST, url, params, callBack);
		}
	}

	private static String getSpecialPath(int uploadType) {
		String path = null;
		switch (uploadType) {
		case ImagePathTypeUserHeadImage:
			path = "/fashion/photos/userHear/";
			break;
		case ImagePathTypeUserWorks:
			path = "/fashion/photos/works/";
			break;
		}
		return path;
	}

}
