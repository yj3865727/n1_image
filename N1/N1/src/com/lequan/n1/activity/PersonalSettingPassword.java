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

public class PersonalSettingPassword extends BaseActivity implements OnClickListener{
	private ImageView password_back_person;
	private TextView pass_save;
	private EditText set_oldpass;
	private EditText set_newpass1;
	private EditText set_newpass2;
	private String mUserId;
	private String newpass;
	private TextView pass_prompt1;
	private TextView pass_prompt2;
	private TextView pass_prompt3;
	private CharSequence temp;  
    private int editStart ;  
    private int editEnd ;
    private String oldpasses;
    private String password;  //当前密码

	@Override
	protected void initView() {
		
		setContentView(R.layout.personal_setting_accountandsafety_password);
		password = SpUtils.getInstance(this).getString(Constants.PASSWORD);
		
		mUserId = SpUtils.getInstance(this).getString(Constants.USER_ID);
		findView();
    }  
	public void findView(){
		password_back_person = (ImageView) findViewById(R.id.password_back_person);
		pass_save = (TextView) findViewById(R.id.pase_save);
		set_oldpass = (EditText) findViewById(R.id.set_oldpass);
		set_newpass1 = (EditText) findViewById(R.id.set_newpass1);
		set_newpass2 = (EditText) findViewById(R.id.set_newpass2);
		pass_prompt1 =  (TextView) findViewById(R.id.pass_prompt1);
		pass_prompt2 =  (TextView) findViewById(R.id.pass_prompt2);
		pass_prompt3 =  (TextView) findViewById(R.id.pass_prompt3);
	}
	@Override
	protected void initEvent() {
		setOnClick();
	}
	private void setOnClick() {
		password_back_person.setOnClickListener(this);
		pass_save.setOnClickListener(this);
		set_oldpass.addTextChangedListener(textWatcher); 
		set_newpass1.addTextChangedListener(textWatcher); 
		set_newpass2.addTextChangedListener(textWatcher);  
	}
	 private TextWatcher textWatcher = new TextWatcher() {  
	         @Override    
	        public void onTextChanged(CharSequence s, int start, int before,     
	                int count) {   
	        	 temp = s;  
	 }

			@Override
			public void afterTextChanged(Editable s) {
				  editStart = set_newpass1.getSelectionStart();  
		            editEnd = set_newpass1.getSelectionEnd();
		            pass_prompt1.setText("密码错误");
		            pass_prompt2.setText("6-16字符");
		            
		        	
		            oldpasses = Md5.encode(set_oldpass.getText().toString());
		            if (temp.length() > 6 && temp.length() <16) {
		            	pass_prompt2.setText("");
		            }
		            if(set_newpass1.getText().toString().equals(set_newpass2.getText().toString()) ){
		            	pass_prompt3.setText("");
		            }
		            if(oldpasses.toString().equals(password.toString())){
		            	
		            	pass_prompt1.setText("");
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
		case R.id.pase_save:
			if(oldpasses.equals(password) && set_newpass1.getText().toString().equals(set_newpass2.getText().toString())){
				final String url = Constants.Url.UPDATE;
				
				newpass = set_newpass1.getText().toString();
				newpass=Md5.encode(newpass);
	        	HashMap<String, Object> params = new HashMap<String, Object>();
	 			params.put("id", mUserId);
	 			
	 			params.put("password", newpass);
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
		case R.id.password_back_person:
			finish();
		default:
			break;
		}
	}
	 }




