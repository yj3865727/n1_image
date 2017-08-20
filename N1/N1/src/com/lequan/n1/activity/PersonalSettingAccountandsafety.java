package com.lequan.n1.activity;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.lequan.n1.util.ValidateUtils;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PersonalSettingAccountandsafety extends BaseActivity implements OnClickListener{
	private LinearLayout setPass;
	private LinearLayout setEmail;
	private LinearLayout setWeibo;
	private LinearLayout setWeixin;
	private LinearLayout setQq;
	private LinearLayout setZhifubao;
	private LinearLayout setWeixinPay;
	private ImageView back;
	@Override
	protected void initView() {
		setContentView(R.layout.personal_setting_accountandsafety);
		findView();

	}  
	public void findView(){
		back = (ImageView) findViewById(R.id.accountandsafety_back_person);
		setPass = (LinearLayout) findViewById(R.id.set_pass);
		setEmail = (LinearLayout) findViewById(R.id.set_Email);
		setWeibo = (LinearLayout) findViewById(R.id.set_weibo);
		setWeixin = (LinearLayout) findViewById(R.id.set_weixin);
		setQq = (LinearLayout) findViewById(R.id.set_qq);
		setZhifubao = (LinearLayout) findViewById(R.id.set_zhifubao);
		setWeixinPay = (LinearLayout) findViewById(R.id.set_weixinPay);
	}
	@Override
	protected void initEvent() {

		setOnClick();
	}
	private void setOnClick() {
		setPass.setOnClickListener(this);
		setEmail.setOnClickListener(this);
		setWeibo.setOnClickListener(this);
		setWeixin.setOnClickListener(this);
		setQq.setOnClickListener(this);
		setZhifubao.setOnClickListener(this);
		setWeixinPay.setOnClickListener(this);
		back.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accountandsafety_back_person:
			finish();
			break;
		case R.id.set_pass:
			startActivity(new Intent(this,PersonalSettingPassword.class));
			break;
		case R.id.set_Email:
			startActivity(new Intent(this,PersonalSettingEmail.class));
			break;
		case R.id.set_weibo:
			shareLogin(SinaWeibo.NAME);
			break;
		case R.id.set_weixin:
			shareLogin(Wechat.NAME);
			break;
		case R.id.set_qq:
			shareLogin(QQ.NAME);
			break;
		case R.id.set_zhifubao:
			startActivity(new Intent(this,PersonalSettingZhifubao.class));
			break;
		case R.id.set_weixinPay:
			startActivity(new Intent(this,PersonalSettingWeixinPay.class));
			break;
		default:
			break;
		}
	}

	// 第三方登录
	private void shareLogin(String platformName) {
		if (!ValidateUtils.isValidate(platformName)) {
			return;
		}
		ShareSDK.initSDK(this);
		Platform plat = ShareSDK.getPlatform(platformName);
		if (plat == null) {
			return;
		}

		if (plat.isAuthValid()) {
			plat.removeAccount(true);
			return;
		}

		// 使用SSO授权，通过客户单授权
		plat.SSOSetting(false);
		plat.setPlatformActionListener(new PlatformActionListener() {
			public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
				if (action == Platform.ACTION_USER_INFOR) {
					PlatformDb platDB = plat.getDb();// 获取数平台数据DB
					//TODO 绑定账号
					String number = platDB.getUserId();// id
					String paltName = plat.getName();// 平台名称
					if (QQ.NAME.equals(paltName)) {
						
					} else if (SinaWeibo.NAME.equals(paltName)) {
						
					} else if (Wechat.NAME.equals(paltName)) {
						
					}
				}
				System.out.println("完成");
			}

			public void onError(Platform plat, int action, Throwable t) {
				System.out.println("失败");
			}

			public void onCancel(Platform plat, int action) {
				System.out.println("取消");
			}
		});
		plat.showUser(null);
	}
}

