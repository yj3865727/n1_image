package com.lequan.n1.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initActionBar();
		initData();
		initEvent();
	}

	/**
	 * 初始化界面
	 */
	protected abstract void initView();

	/**
	 * 初始化ActionBar
	 */
	protected void initActionBar() {

	}

	/**
	 * 初始化数据
	 */
	protected void initData() {

	}

	/**
	 * 处理事件
	 */
	protected void initEvent() {

	}

}
