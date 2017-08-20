package com.lequan.n1.manager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lequan.n1.manager.ConversactionListDbManager.Friend;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class UserInfoManger {

	public static void getUserInfoById(final String senderId, final String targetId) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", senderId);
			HttpRequestProxy.sendAsyncPost(Constants.Url.QUERYUSER_BY_ID, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {

				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// 保存到数据库中
					try {
						LogUtils.i("查询用户信息："+arg0.result);
						JSONObject result = new JSONObject(arg0.result);
						if (result.optInt("code") == 200) {
							JSONObject userInfo = result.optJSONObject("data");
							Friend friend = new Friend(Long.parseLong(targetId), Long.parseLong(senderId),
									userInfo.optString("loginSn"), userInfo.optString("headphoto"));
							ConversactionListDbManager.getInstance().save(friend);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
