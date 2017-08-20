package com.lequan.n1.activity;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeTitleActivity2 extends BaseActivity{

	@ViewInject(R.id.iv_tit2)
	private LinearLayout iv_tit2;

	private Long time1,time2;
	@Override
	protected void initView() {
		setContentView(R.layout.hometitle_activity2);
		ViewUtils.inject(this);
		iv_tit2.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					time1 = System.currentTimeMillis();
					break;
				case MotionEvent.ACTION_UP:
					time2 = System.currentTimeMillis();
					if(time2-time1 < 1000){
						finish();
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
}
