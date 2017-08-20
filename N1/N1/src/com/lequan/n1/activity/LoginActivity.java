package com.lequan.n1.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import com.google.gson.Gson;
import com.lequan.n1.entity.AppUser;
import com.lequan.n1.entity.User;
import com.lequan.n1.manager.ConversactionListDbManager;
import com.lequan.n1.manager.ConversactionListDbManager.Friend;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sea_monster.common.Md5;

public class LoginActivity extends BaseActivity implements OnClickListener, PlatformActionListener {
	private static final int MSG_SMSSDK_CALLBACK = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	
	private String smssdkAppkey;
	private String smssdkAppSecret;
	private Handler handler;




	//执行授权,获取用户信息
	//文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat) {
		if (plat == null) {
			return;
		}
		
		plat.setPlatformActionListener(this);
		//关闭SSO授权
		plat.SSOSetting(false);
		plat.showUser(null);
	}
	
	
	
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
		/*	Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] {platform.getName(), res};
			handler.sendMessage(msg);*/
			PlatformDb platDB = platform.getDb();// 获取数平台数据DB
			// 通过DB获取各种数据
			Map<String, String> params = new HashMap<String, String>();
			
			params.put("sex", platDB.getUserGender());// 性别
			params.put("headphoto", platDB.getUserIcon());// 头像
			params.put("username", platDB.getUserName());// 名称

			String number = platDB.getUserId();// id
			String paltName = platform.getName();// 平台名称
			LogUtils.e("119900",paltName+"");
			if (paltName.equals("QZone")) {
				params.put("userQq", number);
			} else if (paltName.equals("SinaWeibo")) {
				params.put("userBlog", number);
			} else if (paltName.equals("Wechat")) {
				params.put("userWechat", number);
			}
			LogUtils.e("19910",params+"");
			processLogin(params);
		}
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				//取消授权
			} break;
			case MSG_AUTH_ERROR: {
				//授权失败
			} break;
			case MSG_AUTH_COMPLETE: {
				//授权成功
				/*Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];*/
				PlatformDb platDB = (PlatformDb) msg.obj;// 获取数平台数据DB
				// 通过DB获取各种数据
				/*Map<String, String> params = new HashMap<String, String>();
				params.put("sex", platDB.getUserGender());// 性别
				params.put("headphoto", platDB.getUserIcon());// 头像
				params.put("username", platDB.getUserName());// 名称

				String number = platDB.getUserId();// id
				String paltName = platDB.exportData();// 平台名称
				LogUtils.e("119900",paltName+"");
				if (paltName.equals("qq")) {
					params.put("userQq", number);
				} else if (paltName.equals("sina")) {
					params.put("userBlog", number);
				} else if (paltName.equals("wechat")) {
					params.put("userWechat", number);
				}
				Toast.makeText(LoginActivity.this, params+"", Toast.LENGTH_SHORT).show();
				processLogin(params);*/
			} break;
		}
		return false;
	}
	
	public void show(Context context) {
		initSDK(context);
		//super.show(context, null);
	}
	
	private void initSDK(Context context) {
		//初始化sharesdk,具体集成步骤请看文档：
		//http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
		ShareSDK.initSDK(context);
		
	}

	@ViewInject(R.id.iv_login_exit)
	private ImageView iv_login_exit;

	@ViewInject(R.id.et_login_username)
	private EditText et_login_username;

	@ViewInject(R.id.et_login_password)
	private EditText et_login_password;

	@ViewInject(R.id.tv_forget_password)
	private TextView tv_forget_password;

	@ViewInject(R.id.tv_login_registe)
	private TextView tv_login_registe;

	@ViewInject(R.id.iv_longin_weixin)
	private ImageView iv_longin_weixin;

	@ViewInject(R.id.iv_longin_weibo)
	private ImageView iv_longin_weibo;

	@ViewInject(R.id.iv_longin_qq)
	private ImageView iv_longin_qq;

	@ViewInject(R.id.btn_login)
	private Button btn_login;

	private ProgressDialog mDialog;

	private SpUtils mSpUtils;
	private int checked;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
	}

	@Override
	protected void initData() {
		mSpUtils = SpUtils.getInstance(this);
		checked = getIntent().getIntExtra("index", 0);
	}

	@Override
	protected void initEvent() {
		this.btn_login.setOnClickListener(this);
		this.iv_login_exit.setOnClickListener(this);
		this.iv_longin_qq.setOnClickListener(this);
		this.iv_longin_weibo.setOnClickListener(this);
		this.iv_longin_weixin.setOnClickListener(this);
		this.tv_forget_password.setOnClickListener(this);
		this.tv_login_registe.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_login_exit:
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("index", MainActivity.HOME);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_login:
			login();
			break;
		case R.id.tv_login_registe:// 用户注册
			intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.tv_forget_password:
			Toast.makeText(this, "忘记密码", Toast.LENGTH_LONG).show();
			break;
		case R.id.iv_longin_qq:
			ShareSDK.initSDK(this);
			Platform qq = ShareSDK.getPlatform(QZone.NAME);
			authorize(qq);
			//shareLogin(QQ.NAME);
			break;
		case R.id.iv_longin_weibo:
			ShareSDK.initSDK(this);
			Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(sina);
			//shareLogin(SinaWeibo.NAME);
			break;
		case R.id.iv_longin_weixin:
			ShareSDK.initSDK(this);
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			authorize(wechat);
			//shareLogin(Wechat.NAME);
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
			PlatformDb platDB = plat.getDb();// 获取数平台数据DB
			// 通过DB获取各种数据
			Map<String, String> params = new HashMap<String, String>();
			params.put("sex", platDB.getUserGender());// 性别
			params.put("headphoto", platDB.getUserIcon());// 头像
			params.put("username", platDB.getUserName());// 名称

			String number = platDB.getUserId();// id
			String paltName = plat.getName();// 平台名称
			if (QQ.NAME.equals(paltName)) {
				params.put("userQq", number);
			} else if (SinaWeibo.NAME.equals(paltName)) {
				params.put("userBlog", number);
			} else if (Wechat.NAME.equals(paltName)) {
				params.put("userWechat", number);
			}
			processLogin(params);
			return;
		}

		// 使用SSO授权，通过客户单授权
		plat.SSOSetting(true);
		plat.setPlatformActionListener(new PlatformActionListener() {
			public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
				LogUtils.i("授权成功!");
				//处理登录
				if (action == Platform.ACTION_USER_INFOR) {
					PlatformDb platDB = plat.getDb();// 获取数平台数据DB
					// 通过DB获取各种数据
					Map<String, String> params = new HashMap<String, String>();
					params.put("sex", platDB.getUserGender());// 性别
					params.put("headphoto", platDB.getUserIcon());// 头像
					params.put("username", platDB.getUserName());// 名称

					String number = platDB.getUserId();// id
					String paltName = plat.getName();// 平台名称
					if (QQ.NAME.equals(paltName)) {
						params.put("userQq", number);
					} else if (SinaWeibo.NAME.equals(paltName)) {
						params.put("userBlog", number);
					} else if (Wechat.NAME.equals(paltName)) {
						params.put("userWechat", number);
					}
					processLogin(params);
				}
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

	private void login() {
		String username = this.et_login_username.getText().toString();
		if (!ValidateUtils.isValidate(username)) {
			et_login_username.setError("用户名不能为空!");
			return;
		}
		String password = this.et_login_password.getText().toString();
		if (!ValidateUtils.isValidate(password)) {
			et_login_password.setError("密码不能为空!");
			return;
		}
		final Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		password = Md5.encode(password);
		params.put("password", password);// 显示对话框
		if (mDialog == null) {
			initLoadingDialog();
		}
		mDialog.show();
		new Thread() {

			public void run() {
				processLogin(params);
			}

		}.start();
	}

	protected void processLogin(Map<String, String> params) {
		try {
			ResponseStream response = HttpRequestProxy.sendSyncPost(Constants.Url.USER_LOGIN, params);
			UiUtils.runOnSafe(new Runnable() {

				@Override
				public void run() {
					// 关闭对话框
					if(mDialog != null){
						
						mDialog.dismiss();
					}
				}
			});
			if (response.getStatusCode() == 200) {
				String result = response.readString();
				Gson gson = new Gson();
				final User user = gson.fromJson(result, User.class);
				LogUtils.i(result);
				// 注册成功
				if (user.getCode() == 200) {
					// 缓存用户数据-->id token
					Constants.defualt_token = user.data.appUserInfo.rctoken;
					Constants.isLogin = true;
					Constants.needConnected = true;
					// 缓存用户信息到sp中
					mSpUtils.setString(Constants.LOGINSN, user.data.appUserInfo.loginSn);
					mSpUtils.setString(Constants.TOKEN, user.data.appUserInfo.rctoken);
					mSpUtils.setString(Constants.USER_ID, user.data.appUserInfo.id + "");
					mSpUtils.setString(Constants.PASSWORD, user.data.appUserInfo.password + "");
					mSpUtils.setString(Constants.ATTENTIONCOUNT, user.data.attentionCount + "");
					mSpUtils.setString(Constants.USERCOUNT, user.data.usercount + "");
					mSpUtils.setString(Constants.UWCOUNT, user.data.uwcount + "");
					mSpUtils.setString(Constants.HEADIMG, user.data.appUserInfo.headphoto + "");

					//数据库存储容云需要的数据
					Friend friend=new Friend(user.data.appUserInfo.id, user.data.appUserInfo.id, user.data.appUserInfo.loginSn, user.data.appUserInfo.headphoto);
					ConversactionListDbManager.getInstance().save(friend);

					// 缓存用户的所有信息
					AppUser userInfo = user.data.appUserInfo;
					userInfo.attentionCount = user.data.attentionCount;
					userInfo.usercount = user.data.usercount;
					userInfo.uwcount = user.data.uwcount;
					mSpUtils.setString(Constants.ALL_USERINFO, gson.toJson(userInfo));
					// 返回个人中心
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					intent.putExtra("index", checked);
					startActivity(intent);
					finish();
				} else {
					UiUtils.runOnSafe(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(LoginActivity.this, user.getDesc(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			UiUtils.runOnSafe(new Runnable() {

				@Override
				public void run() {
					// 关闭对话框
					if(mDialog != null){
						mDialog.dismiss();
					}
					Toast.makeText(LoginActivity.this, "当前网络不给力，请稍后再试！", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	// 显示加载对话框
	private void initLoadingDialog() {
		mDialog = new ProgressDialog(this);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
		mDialog.setMessage("登录中，请稍后......");
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("index", MainActivity.HOME);
		startActivity(intent);
		finish();
	}

}
