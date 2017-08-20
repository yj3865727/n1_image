package com.lequan.n1.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class PersonalSettingAboutus extends BaseActivity implements OnClickListener{
	private ImageView about_back;
	@Override
	protected void initView() {
		setContentView(R.layout.personal_setting_aboutus);
		findView();
		
    }  
	public void findView(){
		about_back = (ImageView) findViewById(R.id.about_back);
	}
	@Override
	protected void initEvent() {

		setOnClick();
	}
	private void setOnClick() {
		about_back.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_back:
			finish();
			break;
		default:
			break;
		}
	}
}

