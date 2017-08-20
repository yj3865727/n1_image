package com.lequan.n1.activity;

import java.util.HashMap;

import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.sea_monster.common.Md5;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalSettingEmail extends BaseActivity implements OnClickListener{
	private ImageView email_back_person;
	private TextView email_save;
	private EditText email_new;
	private EditText email_pass;
	private CharSequence temp;  
	private String mUserId;
	private String password;
    private int editStart ;  
    private int editEnd ;
    private String newemail;
    private String pass;
    private TextView tip_1;
    private TextView tip_2;
	@Override
	protected void initView() {
		setContentView(R.layout.personal_setting_accountandsafety_emile);
		password = SpUtils.getInstance(this).getString(Constants.PASSWORD);
		mUserId = SpUtils.getInstance(this).getString(Constants.USER_ID);
		findView();
		
    }  
	public void findView(){
		email_back_person = (ImageView) findViewById(R.id.email_back_person);
		email_save = (TextView) findViewById(R.id.email_save);
		email_new = (EditText) findViewById(R.id.email_new);
		email_pass = (EditText) findViewById(R.id.email_pass);
		tip_1 = (TextView) findViewById(R.id.tip_1);
		tip_2 = (TextView) findViewById(R.id.tip_2);
	}
	@Override
	protected void initEvent() {

		setOnClick();
	}
	private void setOnClick() {
		email_back_person.setOnClickListener(this);
		email_save.setOnClickListener(this);
		email_new.addTextChangedListener(textWatcher); 
		email_pass.addTextChangedListener(textWatcher); 
		
	}

	 private TextWatcher textWatcher = new TextWatcher() {  
	         @Override    
	        public void onTextChanged(CharSequence s, int start, int before,     
	                int count) {   
	        	 temp = s;  
	 }

			@Override
			public void afterTextChanged(Editable s) {
				  editStart = email_new.getSelectionStart();  
		            editEnd = email_new.getSelectionEnd();
		            tip_1.setText("请输入新邮箱");
		            tip_2.setText("请输入登录密码");
		            
		        	
		            pass = Md5.encode(email_pass.getText().toString());
		            if (temp.length() >0) {
		            	tip_1.setText("");
		            }
		            if(pass.toString().equals(password.toString())){
		            	
		            	tip_2.setText("");
		            }
		            
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
	 };
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.email_back_person:
			finish();
			break;
		case R.id.email_save:
			if(pass.equals(password) && email_new.length()>0){
				final String url = Constants.Url.UPDATE;
				
				newemail = email_new.getText().toString();
	        	HashMap<String, Object> params = new HashMap<String, Object>();
	 			params.put("id", mUserId);
	 			
	 			params.put("email", newemail);
				try {
					HttpRequestProxy.sendAsyncPost(url, params, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							LogUtils.i("请求失败");
						}
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO 缓存数据
						}
						
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 			finish();
				
			}else{
				
			}
			break;
		default:
			break;
		}
	}
}

