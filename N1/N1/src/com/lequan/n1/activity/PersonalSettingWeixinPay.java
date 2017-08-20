package com.lequan.n1.activity;

import java.util.HashMap;

import com.google.gson.Gson;
import com.lequan.n1.entity.AppUser;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PersonalSettingWeixinPay extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.person_setting_weixin_back)
	private ImageView zhifubao_back;
	@ViewInject(R.id.person_setting_weixin_bangding)
	private ImageView zhifubao_bangding;
	@ViewInject(R.id.et_person_weixinzhanghao)
	private EditText et_person_weixinzhanghao;

	private String mUserId;
	private String wechatWalletAccount;
	private String userInfo;
	@Override
	protected void initView() {
		setContentView(R.layout.person_setting_weixinbangding);
		ViewUtils.inject(this);
	}


	@Override
	protected void initData() {
		mUserId = SpUtils.getInstance(this).getString(Constants.USER_ID);
		userInfo = SpUtils.getInstance(this).getString(Constants.ALL_USERINFO);
		Gson gson = new Gson();
		AppUser appuser = gson.fromJson(userInfo, AppUser.class);
		if(appuser.wechatWalletAccount!=null){
			et_person_weixinzhanghao.setHint(appuser.wechatWalletAccount);
		}else{
			return;
		}
	}

	@Override
	@OnClick({R.id.person_setting_weixin_back,R.id.person_setting_weixin_bangding})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.person_setting_weixin_back:
			finish();
			break;
		case R.id.person_setting_weixin_bangding:
			wechatWalletAccount = et_person_weixinzhanghao.getText().toString();
			if(ValidateUtils.isValidate(wechatWalletAccount)){
				sendMessageNet(wechatWalletAccount);				
			}else{
				Toast.makeText(this, "内容不能为空", Toast.LENGTH_LONG).show();
				et_person_weixinzhanghao.setText("");
			}
			break;
		default:
			break;
		}
	}

	// 访问网络发送数据
	public void sendMessageNet(String message) {
		try {
			final String url = Constants.Url.APPUSER_BIND;
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("userid", mUserId);
			map.put("wechatWalletAccount", message);
			HttpRequestProxy.sendAsyncPost(url, map,
					new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("失败");
				}
				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					int coke = arg0.statusCode;		
					if(coke == 200){								
						Toast.makeText(PersonalSettingWeixinPay.this, "绑定成功", Toast.LENGTH_LONG).show();
						et_person_weixinzhanghao.setHint(wechatWalletAccount);
						et_person_weixinzhanghao.setText("");
						getUserInfo(wechatWalletAccount);				
					}else{
						Toast.makeText(PersonalSettingWeixinPay.this, "绑定失败,请重新绑定", Toast.LENGTH_LONG).show();
						et_person_weixinzhanghao.setText("");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	//将数据存储到SP中
	public void getUserInfo(String wechatWalletAccount){
		userInfo = SpUtils.getInstance(this).getString(Constants.ALL_USERINFO);
		Gson gson = new Gson();
		AppUser appuser = gson.fromJson(userInfo, AppUser.class);
		appuser.wechatWalletAccount = wechatWalletAccount;
		SpUtils.getInstance(this).setString(Constants.ALL_USERINFO, new Gson().toJson(appuser));
	}
}

