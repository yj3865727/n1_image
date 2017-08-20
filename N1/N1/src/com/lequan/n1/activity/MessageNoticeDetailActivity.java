package com.lequan.n1.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lequan.n1.adapter.holder.MessageNoticeDetailViewHolder;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MessageNoticeDetailActivity extends BaseActivity {

	private static final int STATE_LOADING = 0;
	private static final int STATE_EMPTY = 1;
	private static final int STATE_SUCCESSS = 2;

	public static final int TYPE_ATTENTION = 3;// 关注
	public static final int TYPE_COMMENT = 4;// 评论
	public static final int TYPE_PRAISE = 5;// 赞

	@ViewInject(R.id.lv_message_notice_detail)
	private ListView lv_message_notice_detail;

	@ViewInject(R.id.pb_message_notice_detail_load)
	private ProgressBar pb_message_notice_load;

	@ViewInject(R.id.tv_message_notice_detail_hint)
	private TextView tv_message_notice_detail_hint;

	private String mTitle;
	private String mUrl;
	private String mUid;
	private int mType;
	private SpUtils mSpUtils;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_message_notice_detail);
		ViewUtils.inject(this);
	}

	@Override
	protected void initActionBar() {
		Intent intent = getIntent();
		if (intent != null) {
			mTitle = intent.getStringExtra("title");
			mType = intent.getIntExtra("type", -1);
			switch (mType) {
			case TYPE_ATTENTION:
				mUrl = Constants.Url.LAST_FANS;
				break;
			case TYPE_COMMENT:
				mUrl = Constants.Url.LAST_COMMENT;
				break;
			case TYPE_PRAISE:
				mUrl = Constants.Url.LAST_PRAISE;
				break;

			default:
				break;
			}
			if (ValidateUtils.isValidate(mTitle)) {
				ActionBar actionBar = getSupportActionBar();
				if (actionBar != null) {
					actionBar.setDisplayHomeAsUpEnabled(true);
					actionBar.setTitle(mTitle);
					this.tv_message_notice_detail_hint
							.setText(String.format(UiUtils.getString(R.string.message_notice_detail_hint), mTitle));
				}
			}
		}
	}

	@Override
	protected void initData() {
		mSpUtils = SpUtils.getInstance(this);
		mUid = mSpUtils.getString(Constants.USER_ID);
		// 初始化为加载
		refreshUi(STATE_LOADING);
		// 加载网络数据
		new Thread() {

			public void run() {
				try {
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("id", mUid);
					ResponseStream response = HttpRequestProxy.sendSyncPost(mUrl, params);
					// 请求成功
					if (response.getStatusCode() == 200) {
						String result = response.readString();
						LogUtils.i("通知详情返回数据："+result);
						final JSONObject data = new JSONObject(result);
						int code = data.getInt("code");
						if (code == 200) {// 获取数据成功
							UiUtils.runOnSafe(new Runnable() {

								@Override
								public void run() {
									processData(data);
								}

							});
							return;
						}
					}
				} catch (Exception e) {// 请求网络失败
					e.printStackTrace();
				}
				UiUtils.runOnSafe(new Runnable() {

					@Override
					public void run() {
						refreshUi(STATE_EMPTY);
					}
				});
			};

		}.start();
	}

	// 处理数据 data为原始的json数据
	private void processData(JSONObject data) {
		JSONArray praises = null;
		// 如果是关注则数据需要重新处理
		if (mType == TYPE_ATTENTION) {
			praises = data.optJSONObject("data").optJSONArray("userInfo");
			LogUtils.i("通知详情-->关注信息："+praises.toString());
		} else {
			praises = data.optJSONArray("data");
			LogUtils.i("通知详情-->信息："+praises.toString());
		}
		//处理本地数据和网络数据的结合
		String cacheData = mSpUtils.getString(mUid+"_notice_"+mType);
		if(ValidateUtils.isValidate(cacheData)){
			try {
				JSONArray cache=new JSONArray(cacheData);
				for(int i=0;i<cache.length();i++){
					praises.put(cache.optJSONObject(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// 此处判断解析之后数据是否有效
		if (praises != null && praises.length() > 0) {
			refreshUi(STATE_SUCCESSS);
			// TODO 此处通过数据库保存数据
			this.mSpUtils.setString(mUid+"_notice_"+mType, praises.toString());
			MessageNoticeDetailAdapter adapter = new MessageNoticeDetailAdapter(praises);
			this.lv_message_notice_detail.setAdapter(adapter);
		} else {
			refreshUi(STATE_EMPTY);
		}
	}

	private void refreshUi(int state) {
		this.lv_message_notice_detail.setVisibility(state == STATE_SUCCESSS ? View.VISIBLE : View.GONE);
		this.pb_message_notice_load.setVisibility(state == STATE_LOADING ? View.VISIBLE : View.GONE);
		this.tv_message_notice_detail_hint.setVisibility(state == STATE_EMPTY ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private final class MessageNoticeDetailAdapter extends BaseAdapter {

		private JSONArray data;

		public MessageNoticeDetailAdapter(JSONArray data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			if (data != null) {
				return data.length();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return data.opt(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MessageNoticeDetailViewHolder holder;
			if (convertView == null) {
				holder = new MessageNoticeDetailViewHolder();
			} else {
				holder = (MessageNoticeDetailViewHolder) convertView.getTag();
			}
			// 设置数据
			holder.setNoticeType(mType);
			holder.setDataAndRefreshUi(data.optJSONObject(position), position);
			return holder.getConveter();
		}

	}

}
