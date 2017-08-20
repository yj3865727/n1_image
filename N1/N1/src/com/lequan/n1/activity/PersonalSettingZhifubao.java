package com.lequan.n1.activity;

import java.util.HashMap;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class PersonalSettingZhifubao extends BaseActivity implements OnClickListener{
	@ViewInject(R.id.person_setting_zhifubao_back)
	private ImageView zhifubao_back;
	@ViewInject(R.id.person_setting_zhifubao_bangding)
	private ImageView zhifubao_bangding;
	@ViewInject(R.id.et_person_zhanghao)
	private EditText et_person_zhanghao;

	private String mUserId;
	private String paypalWalletAccount;
	private String userInfo; 

	@Override
	protected void initView() {
		setContentView(R.layout.person_setting_zhifubao);
		ViewUtils.inject(this);
	}

	@Override
	protected void initData() {
		mUserId = SpUtils.getInstance(this).getString(Constants.USER_ID);
		userInfo = SpUtils.getInstance(this).getString(Constants.ALL_USERINFO);
		Gson gson = new Gson();
		AppUser appuser = gson.fromJson(userInfo, AppUser.class);
		if(appuser.paypalWalletAccount!=null){
			et_person_zhanghao.setHint(appuser.paypalWalletAccount);
		}else{
			return;
		}
	}

	@Override
	@OnClick({R.id.person_setting_zhifubao_bangding,R.id.person_setting_zhifubao_back})
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.person_setting_zhifubao_back:
			finish();
			break;
		case R.id.person_setting_zhifubao_bangding:
			paypalWalletAccount = et_person_zhanghao.getText().toString();
			if(ValidateUtils.isValidate(paypalWalletAccount)){
				sendMessageNet(paypalWalletAccount);				
			}else{
				Toast.makeText(this, "内容不能为空", Toast.LENGTH_LONG).show();
				et_person_zhanghao.setText("");
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
			map.put("paypalWalletAccount", message);
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
						Toast.makeText(PersonalSettingZhifubao.this, "绑定成功", Toast.LENGTH_LONG).show();
						et_person_zhanghao.setHint(paypalWalletAccount);
						et_person_zhanghao.setText("");
						getUserInfo(paypalWalletAccount);					
					}else{
						Toast.makeText(PersonalSettingZhifubao.this, "绑定失败,请重新绑定", Toast.LENGTH_LONG).show();
						et_person_zhanghao.setText("");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//将数据存储到SP中
	public void getUserInfo(String paypalWalletAccount){
		userInfo = SpUtils.getInstance(this).getString(Constants.ALL_USERINFO);
		Gson gson = new Gson();
		AppUser appuser = gson.fromJson(userInfo, AppUser.class);
		appuser.paypalWalletAccount = paypalWalletAccount;		
		SpUtils.getInstance(this).setString(Constants.ALL_USERINFO, new Gson().toJson(appuser));
	}
}
