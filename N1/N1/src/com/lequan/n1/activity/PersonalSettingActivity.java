package com.lequan.n1.activity;

import java.io.File;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lequan.n1.util.Constants;
import com.lequan.n1.util.FileUtils;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.StringUtils;
import com.lequan.n1.util.UiUtils;


public class PersonalSettingActivity extends BaseActivity implements OnClickListener{
	

	private LinearLayout set_info;
	private LinearLayout set_account;
	private LinearLayout set_clean;
	private LinearLayout set_about_us;
	private LinearLayout set_problem;
	private LinearLayout set_exit;
	private ImageView back_person;
	private TextView tv_personal_setting_cache_size;

	@Override
	protected void initView() {
		setContentView(R.layout.personal_setting);
		findView();

	}

	public void findView() {
		set_info = (LinearLayout) findViewById(R.id.set_info);
		set_account = (LinearLayout) findViewById(R.id.set_account);
		set_clean = (LinearLayout) findViewById(R.id.set_clean);
		set_about_us = (LinearLayout) findViewById(R.id.set_about_us);
		set_problem = (LinearLayout) findViewById(R.id.set_problem);
		set_exit = (LinearLayout) findViewById(R.id.set_exit);
		back_person = (ImageView) findViewById(R.id.back_person);
		tv_personal_setting_cache_size = (TextView) findViewById(R.id.tv_personal_setting_cache_size);
	}

	@Override
	protected void initEvent() {

		setOnClick();
	}

	@Override
	protected void initData() {
		// 设置缓存的大小
		new Thread() {

			public void run() {
				long cacheSize = getCacheSize(FileUtils.getCacheDir());
				cacheSize += getCacheSize(FileUtils.getImageCache());
				LogUtils.i("缓存大小：" + StringUtils.formatFileSize(cacheSize, false));
				final long temp = cacheSize;
				UiUtils.runOnSafe(new Runnable() {

					@Override
					public void run() {
						tv_personal_setting_cache_size.setText(StringUtils.formatFileSize(temp, false));
					}
				});
			};

		}.start();
	}

	private void setOnClick() {
		set_info.setOnClickListener(this);
		back_person.setOnClickListener(this);
		set_account.setOnClickListener(this);
		set_clean.setOnClickListener(this);
		set_about_us.setOnClickListener(this);
		set_problem.setOnClickListener(this);
		set_exit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_info:
			startActivity(new Intent(this, PersonalSettingDataActivity.class));
			break;
		case R.id.back_person:
			finish();
			break;
		case R.id.set_account:
			startActivity(new Intent(this, PersonalSettingAccountandsafety.class));
			break;
		case R.id.set_clean:
			clearCache();
			break;
		case R.id.set_about_us:
			startActivity(new Intent(this, PersonalSettingAboutus.class));
			break;
		case R.id.set_problem:
			startActivity(new Intent(this,PersonalSettingFeedback.class));
			break;
		case R.id.set_exit:
			logout();
			break;
		default:
			break;
		}
	}

	// 清除缓存数据
	private void clearCache() {
		new Thread() {

			public void run() {
				try {
					// 清除应用缓存目录中的缓存数据
					deleteCache(FileUtils.getCacheDir());
					// 清除sdcard中的缓存的图片
					deleteCache(FileUtils.getImageCache());
					UiUtils.runOnSafe(new Runnable() {
						
						@Override
						public void run() {
							tv_personal_setting_cache_size.setText("");
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			};

		}.start();
	}

	// 递归删除
	private void deleteCache(String path) {
		File dir = new File(path);
		if (dir.exists()) {
			// 如果是个文件夹
			if (dir.isDirectory()) {
				for (File file : dir.listFiles()) {
					deleteCache(file.getAbsolutePath());
				}
			} else {
				dir.delete();
			}
		}
	}

	// 递归获取缓存的大小
	private long getCacheSize(String path) {
		long cacheSize = 0;
		File dir = new File(path);
		if (dir.exists()) {
			// 如果是个文件夹
			if (dir.isDirectory()) {
				for (File file : dir.listFiles()) {
					cacheSize += getCacheSize(file.getAbsolutePath());
				}
			} else {
				cacheSize = dir.length();
			}
		}
		return cacheSize;
	}

	// 退出登录
	private void logout() {
		// 清除用户数据--->id,token
		LogUtils.i("退出登录");
		SpUtils sp = SpUtils.getInstance(this);
		sp.setString(Constants.USER_ID, "");
		sp.setString(Constants.TOKEN, "");
		// TODO 清除该用户的所有的信息
		sp.setString(Constants.PASSWORD,"");
		sp.setString(Constants.ATTENTIONCOUNT, "");
		sp.setString(Constants.USERCOUNT, "");
		sp.setString(Constants.UWCOUNT, "");
		sp.setString(Constants.ALL_USERINFO, "");
		//清空常量中的数据
		Constants.isLogin=false;
		Constants.defualt_token="";
		Constants.needConnected=true;
		Constants.needReload=true;
		// 登录界面
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}
