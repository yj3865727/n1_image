package com.lequan.n1.activity;

import java.util.HashMap;

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
import android.widget.TextView;
import android.widget.Toast;

public class PersonalSettingFeedback extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.iv_feedback_back)
	private ImageView iv_feedback_back;
	@ViewInject(R.id.tv_feedback_send)
	private TextView tv_feedback_send;
	@ViewInject(R.id.et_feedback_message)
	private EditText et_feedback_message;
	@ViewInject(R.id.et_feedback_tellphone)
	private EditText et_feedback_tellphone;
	// 用户id
	private String mUserId;
	// 发送消息的内容
	private String message;
	// 发送消息的号码
	private String phone;
	// 接收到用户提交的字符串
	private String userMessage;

	@Override
	protected void initView() {
		setContentView(R.layout.personal_settint_feedback);
		ViewUtils.inject(this);
	}

	@Override
	protected void initData() {
		mUserId = SpUtils.getInstance(this).getString(Constants.USER_ID);
	}

	@Override
	@OnClick({ R.id.iv_feedback_back, R.id.tv_feedback_send })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_feedback_back:
			finish();
			break;
		case R.id.tv_feedback_send:
			userMessage = getMessage();
			if((ValidateUtils.isValidate(userMessage))){
				sendMessageNet(userMessage);
			}else{
				et_feedback_message.setText("");
				et_feedback_tellphone.setText("");
				Toast.makeText(this, "请输入正确的内容和号码", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}

	// 访问网络发送数据
	public void sendMessageNet(String message) {
		try {
			final String url = Constants.Url.SUGGEST;
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("content", message);
			map.put("userid", mUserId);
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
								Toast.makeText(PersonalSettingFeedback.this, "感谢你的反馈", Toast.LENGTH_LONG).show();
								finish();
							}else{
								Toast.makeText(PersonalSettingFeedback.this, "提交失败请重新提交", Toast.LENGTH_LONG).show();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//获取用户提交内容
	public String getMessage() {
		message = et_feedback_message.getText().toString();
		phone = et_feedback_tellphone.getText().toString();

		if (ValidateUtils.isValidate(message)
				&& ValidateUtils.isValidate(phone)) {
			return "反馈内容" + message + "反馈号码" + phone;
		} else {
			return null;
		}
	}
}
