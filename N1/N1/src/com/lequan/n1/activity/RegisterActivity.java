package com.lequan.n1.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.InputHintView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sea_monster.common.Md5;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {

	@ViewInject(R.id.iht_registe_username)
	private InputHintView et_registe_username;

	@ViewInject(R.id.iht_registe_password)
	private InputHintView iht_registe_password;

	@ViewInject(R.id.iht_registe_repwd)
	private InputHintView iht_registe_repwd;

	@ViewInject(R.id.iht_registe_nikename)
	private InputHintView et_registe_nikename;

	@ViewInject(R.id.btn_registe)
	private Button btn_registe;

	@ViewInject(R.id.iv_registe_exit)
	private ImageView iv_registe_exit;

	private ProgressDialog mDialog;

	private SpUtils mSpUtils;

	private AlertDialog mCountDownDialog;

	private TextView mTv_registe_count;

	private Button mBtn_registe_success;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_register);
		ViewUtils.inject(this);

		this.iht_registe_repwd.setCompareAndHint(iht_registe_password, "两次密码不一致!");
		initLoadingDialog();
		initCountDownDialog();
	}

	@Override
	protected void initData() {
		mSpUtils = SpUtils.getInstance(this);
		this.totalCount = 6;
		mTimer = new Timer();
		this.mTv_registe_count.setText(totalCount + "s");
	}

	@Override
	protected void initEvent() {
		this.iv_registe_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		this.mBtn_registe_success.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				enterPersonalCenter();
			}
		});

		this.btn_registe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = et_registe_username.getTextValue();
				if (!ValidateUtils.isValidate(username)) {
					Toast.makeText(RegisterActivity.this, "用户名不能为空!", Toast.LENGTH_LONG).show();
					return;
				}
				String password = iht_registe_password.getTextValue();
				if (!ValidateUtils.isValidate(password)) {
					Toast.makeText(RegisterActivity.this, "密码不能为空!", Toast.LENGTH_LONG).show();
					return;
				}
				String repwd = iht_registe_repwd.getTextValue();
				if (!(ValidateUtils.isValidate(repwd) && repwd.equals(password))) {
					Toast.makeText(RegisterActivity.this, "两次密码不一致!", Toast.LENGTH_LONG).show();
					return;
				}
				String nikename =et_registe_nikename.getTextValue();
				final Map<String, String> params = new HashMap<String, String>();
				params.put("username", username);
				password = Md5.encode(password);
				params.put("password", password);
				params.put("loginSn", nikename);
				// 显示对话框
				if (mDialog == null) {
					initLoadingDialog();
				}
				mDialog.show();
				new Thread() {

					public void run() {
						processRegiste(params);
					}

				}.start();
			}
		});
	}

	// 显示加载对话框
	private void initLoadingDialog() {
		mDialog = new ProgressDialog(this);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setMessage("数据加载中....");
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
	}

	// 用户注册
	private void processRegiste(final Map<String, String> params) {
		try {
			ResponseStream response = HttpRequestProxy.sendSyncPost(Constants.Url.USER_REGISTE, params);
			UiUtils.runOnSafe(new Runnable() {

				@Override
				public void run() {
					// 关闭对话框
					mDialog.dismiss();
				}
			});
			if (response.getStatusCode() == 200) {
				String result = response.readString();
				LogUtils.i(result);
				final JSONObject data = new JSONObject(result);
				int code = data.getInt("code");
				// 注册成功
				if (code == 200) {
					// 显示倒计时界面
					UiUtils.runOnSafe(new Runnable() {

						@Override
						public void run() {
							mCountDownDialog.show();
							JSONObject userInfo = data.optJSONObject("data");
							// 缓存用户数据-->id token
							Constants.defualt_token = userInfo.optString("rctoken");
							Constants.isLogin = true;
							Constants.needConnected=true;
							mSpUtils.setString(Constants.TOKEN, userInfo.optString("rctoken"));
							mSpUtils.setString(Constants.USER_ID, userInfo.optInt("id") + "");
							mSpUtils.setString(Constants.PASSWORD,userInfo.optString("password"));
							mSpUtils.setString(Constants.ATTENTIONCOUNT, userInfo.optString("attentionCount"));
							mSpUtils.setString(Constants.USERCOUNT, userInfo.optString("usercount"));
							mSpUtils.setString(Constants.UWCOUNT, userInfo.optString("uwcount"));
							//处理缓存用户的所有信息
							mSpUtils.setString(Constants.ALL_USERINFO, userInfo.toString());
							//开始倒计时
							mTimer.schedule(mTimerTask, 1000, 1000);
						}
					});
				} else if (code == 300) {// 已经注册
					UiUtils.runOnSafe(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(RegisterActivity.this, data.optString("desc"), Toast.LENGTH_LONG).show();
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
					mDialog.dismiss();
					Toast.makeText(RegisterActivity.this, "当前网络不给力，请稍后再试！", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	// 初始化倒计时对话框
	private void initCountDownDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.rl_count_down, null);
		mTv_registe_count = (TextView) view.findViewById(R.id.tv_registe_count);
		mBtn_registe_success = (Button) view.findViewById(R.id.btn_registe_success);
		builder.setView(view);
		builder.setCancelable(false);
		mCountDownDialog = builder.create();

	}

	private int totalCount;

	private Timer mTimer;

	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			if (--totalCount < 0) {
				// 跳转到个人中心
				enterPersonalCenter();
			} else {
				UiUtils.runOnSafe(new Runnable() {

					@Override
					public void run() {
						mTv_registe_count.setText(totalCount + "s");
					}
				});
			}
		}

	};

	// 进入个人中心
	private void enterPersonalCenter() {
		UiUtils.runOnSafe(new Runnable() {

			@Override
			public void run() {
				if (mTimer != null) {
					mTimer.cancel();
				}
				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				intent.putExtra("index", MainActivity.PERSONAL);
				startActivity(intent);
				finish();
			}
		});

	}

}
