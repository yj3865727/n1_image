package com.lequan.n1.activity;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lequan.n1.adapter.PraiseListAdapter;
import com.lequan.n1.entity.PraisesEntity;
import com.lequan.n1.entity.UserWork;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class PraiseListActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private ImageView picBackImg;
	private ListView praiseLv;

	private UserWork userWork;
	private List<PraisesEntity> praiseList;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_praise_list);
		findViewId();
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		picBackImg.setOnClickListener(this);
		praiseLv.setOnItemClickListener(this);
	}

	@Override
	protected void initData() {

		super.initData();
		loadPraiseData();
	}

	private void findViewId() {
		picBackImg = (ImageView) findViewById(R.id.praise_list_back_img);
		praiseLv = (ListView) findViewById(R.id.praise_list_listview);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.praise_list_back_img:
			PraiseListActivity.this.finish();
			break;

		default:
			break;
		}
	}

	private void loadPraiseData() {
		try {
			final String url = Constants.Url.PIC_DETAIL;
			HashMap<String, Object> params = new HashMap<String, Object>();
			/*
			 * 取其他activity传来的数据对象
			 */
			Intent intent = getIntent();
			Bundle bundle = intent.getBundleExtra("key");
			params.put("id", bundle.get("workid")); // 4207 多张图片 workid,3704

			HttpRequestProxy.sendAsyncPost(url, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							LogUtils.i("请求失败");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO 缓存数据
							String data = arg0.result;
							praiseList = parseData(data);
							handleData(praiseList);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 解析数据
	protected List<PraisesEntity> parseData(String result) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		userWork = gson.fromJson(result, UserWork.class);
		return userWork.data.praises;
	}

	// 处理数据
	private void handleData(List<PraisesEntity> list) {

		PraiseListAdapter adapter = new PraiseListAdapter(
				PraiseListActivity.this, list);
		praiseLv.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String loginId = SpUtils.getInstance(PraiseListActivity.this)
				.getString(Constants.USER_ID);
		//区别点击本人和其他人
		if ((praiseList.get(position).appuser.id + "").equals(loginId)) {

		} else {
			Intent intent = new Intent(PraiseListActivity.this,
					OthersActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("userid", praiseList.get(position).appuser.id);
			intent.putExtra("key", bundle);
			startActivity(intent);
		}
	}
}
