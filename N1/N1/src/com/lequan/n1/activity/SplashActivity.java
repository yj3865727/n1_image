package com.lequan.n1.activity;

import com.lequan.n1.util.Constants;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Thread() {
			
			public void run() {
				SystemClock.sleep(3000);
				UiUtils.runOnSafe(new Runnable() {
					
					@Override
					public void run() {
						SpUtils spUtils = SpUtils.getInstance(SplashActivity.this);
						boolean flag = spUtils.getBoolean(Constants.IS_SETTED);
						if(flag){
							//主界面
							Intent intent=new Intent(SplashActivity.this,MainActivity.class);
							startActivity(intent);
						}else{
							//向导界面
							Intent intent=new Intent(SplashActivity.this,GuideActivity.class);
							startActivity(intent);
						}
						finish();
					}
				});
			};
			
		}.start();
	}

}
