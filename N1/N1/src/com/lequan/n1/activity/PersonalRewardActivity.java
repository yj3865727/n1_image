package com.lequan.n1.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.lequan.n1.activity.PersonalRewardActivity.RewardAdapter.PersonRewardDetailViewHolder;
import com.lequan.n1.adapter.holder.BaseViewHolder;
import com.lequan.n1.entity.PersonRewardDetailEntity;
import com.lequan.n1.entity.PersonRewardDetailEntity.RewardDetail;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalRewardActivity extends Activity {
	private ImageView geren_jiangli_fanhui;
	// 无奖励显示图片
	private ImageView iv_person_reward;
	// 有奖励时显示ListView
	private ListView lv_person_reward;

	private PersonRewardDetailViewHolder viewHolder;
	private String mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_reward);
		geren_jiangli_fanhui = (ImageView) findViewById(R.id.geren_jiangli_fanhui);
		iv_person_reward = (ImageView) findViewById(R.id.iv_person_reward);
		lv_person_reward = (ListView) findViewById(R.id.lv_person_reward);

		mUserId = SpUtils.getInstance(this).getString(Constants.USER_ID);

		loadNet();
		geren_jiangli_fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// 根据USER_ID访问网络获取数据
	private void loadNet() {
		try {
			String url = Constants.Url.GETMYREWARDDETAILS;
			HashMap<String, String> map = new HashMap<String, String>();

			map.put("userId", mUserId);

			HttpRequestProxy.sendAsyncPost(url, map, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("请求失败");

				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;
					LogUtils.i(result);
					PersonRewardDetailEntity parseData = parseData(result);
					judge(parseData);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 解析Json数据
	private PersonRewardDetailEntity parseData(String jsonData) {
		Gson gson = new Gson();
		return gson.fromJson(jsonData, PersonRewardDetailEntity.class);
	}

	// 设置ListView里面的数据
	private void setDataForListView(PersonRewardDetailEntity personRewardDetailEntity) {
		lv_person_reward.setAdapter(new RewardAdapter(personRewardDetailEntity.data));
	}

	// 有奖励的适配器
	class RewardAdapter extends BaseAdapter {
		List<PersonRewardDetailEntity.RewardDetail> list = new ArrayList<PersonRewardDetailEntity.RewardDetail>();

		public RewardAdapter(List<PersonRewardDetailEntity.RewardDetail> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				viewHolder = new PersonRewardDetailViewHolder();
			} else {
				viewHolder = (PersonRewardDetailViewHolder) convertView.getTag();
			}

			viewHolder.setDataAndRefreshUi(list.get(position), position);

			return viewHolder.getConveter();
		}

		//ListView的ViewHolder
		class PersonRewardDetailViewHolder extends BaseViewHolder<RewardDetail> {

			@ViewInject(R.id.iv_personreward)
			private ImageView iv_personreward;
			@ViewInject(R.id.tv_personreward_photo)
			private TextView tv_personreward_photo;
			@ViewInject(R.id.tv_personreward_way)
			private TextView tv_personreward_way;
			@ViewInject(R.id.tv_personreward_money)
			private TextView tv_personreward_money;

			@Override
			protected View initView() {
				View view = View.inflate(UiUtils.getContext(), R.layout.list_personreward, null);
				ViewUtils.inject(this, view);
				return view;
			}

			@Override
			protected void refreshUi(RewardDetail t, int position) {

				// BitmapUtil.display(iv_personreward,
				// t.list.get(position).pictureUrl);

				tv_personreward_money.setText(t.name + "");
				tv_personreward_photo.setText(t.description + "");
				tv_personreward_way.setText(t.rewardName + "");
			}
		}
	}

	// 判断是否有返回数据
	public void judge(PersonRewardDetailEntity parseData) {
		if (ValidateUtils.isValidate(parseData.data)) {
			setDataForListView(parseData);
			iv_person_reward.setVisibility(View.GONE);
			lv_person_reward.setVisibility(View.VISIBLE);
		} else {
			iv_person_reward.setVisibility(View.VISIBLE);
			lv_person_reward.setVisibility(View.GONE);
		}
	}
}
