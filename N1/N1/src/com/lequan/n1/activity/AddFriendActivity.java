package com.lequan.n1.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lequan.n1.adapter.holder.AddFriendViewHolder;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddFriendActivity extends BaseActivity {

	private static final int TYPE_SINGLE = 0;
	private static final int TYPE_MORE = 1;

	@ViewInject(R.id.et_add_friend_number)
	private EditText et_add_friend_number;

	@ViewInject(R.id.rlv_add_friend)
	private PullToRefreshListView rlv_add_friend;

	@ViewInject(R.id.Praise_List_back_img)
	private ImageView backImg;

	private ProgressDialog mDialog;

	// 数据类型：多个、单个
	private int data_type = TYPE_MORE;

	// 好友信息
	private JSONArray mData = new JSONArray();
	private AddFriendAdapter mAdapter;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_add_friend);
		ViewUtils.inject(this);
		mDialog = new ProgressDialog(this);
	}

	@Override
	protected void initData() {
		mAdapter = new AddFriendAdapter();
		this.rlv_add_friend.setAdapter(mAdapter);
		initRandomFriend();
	}

	private void initRandomFriend() {
		String id = SpUtils.getInstance(this).getString(Constants.USER_ID);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		data_type = TYPE_MORE;
		loadFriendInfos(Constants.Url.RECOMMEND_FRIENDS, params);
	}

	// 通过id或者昵称搜索好友信息
	private void loadFriendInfos(final String url,
			final Map<String, Object> params) {
		LogUtils.i(params.toString());
		// 加载数据
		UiUtils.showSimpleProcessDialog(mDialog, "数据加载中....");
		new Thread() {
			public void run() {
				try {
					SystemClock.sleep(2000);
					ResponseStream response = HttpRequestProxy.sendSyncPost(
							url, params);
					if (response.getStatusCode() == 200) {
						String result = response.readString();
						LogUtils.i(result);
						// 解析数据--->显示数据(处理根据id(单个数据)，多个数据的显示切换)
						final JSONObject data = new JSONObject(result);
						int code = data.optInt("code");
						// 获取数据成功
						if (code == 200) {
							// 解析数据
							UiUtils.runOnSafe(new Runnable() {

								@Override
								public void run() {
									poccessData(data);
								}
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					UiUtils.runOnSafe(new Runnable() {
						@Override
						public void run() {
							rlv_add_friend.onRefreshComplete();
							UiUtils.closeProcessDialog(mDialog);
						}
					});
				}
			};
		}.start();
	}

	// 处理数据--->根据当前查询的类型
	private void poccessData(JSONObject data) {
		if (data_type == TYPE_SINGLE) {
			mData = new JSONArray();
			JSONObject result = data.optJSONObject("data");
			if (result != null) {
				mData.put(result);
			}
		} else {
			mData = data.optJSONArray("data");
		}
		// 通知adapter刷新ui
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void initEvent() {

		// 返回
		backImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddFriendActivity.this.finish();
			}
		});

		// 下拉刷新监听
		this.rlv_add_friend
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase refreshView) {
						initRandomFriend();
					}
				});
		this.et_add_friend_number
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE
								|| actionId == EditorInfo.IME_ACTION_NEXT
								|| actionId == EditorInfo.IME_ACTION_GO) {
							String number = et_add_friend_number.getText()
									.toString();
							if (ValidateUtils.isValidate(number)) {
								Map<String, Object> params = new HashMap<String, Object>();
								String url = null;
								// 如果能装好成整形数据
								if (ValidateUtils.isNumbser(number)) {
									data_type = TYPE_SINGLE;
									url = Constants.Url.QUERYUSER_BY_ID;
									params.put("id", number);
								} else {
									data_type = TYPE_SINGLE;
									url = Constants.Url.QUERYUSER_BY_name;
									params.put("username", number);
								}
								loadFriendInfos(url, params);
							}
						}
						return true;
					}
				});

		this.rlv_add_friend.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			
					JSONObject selected = mData.optJSONObject(position-1);

					Intent intent = new Intent(AddFriendActivity.this,
							OthersActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("userid", selected.optInt("id"));
					intent.putExtra("key", bundle);
					startActivity(intent);
				
			}
		});

	}

	private final class AddFriendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mData != null) {
				return mData.length();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return mData.optJSONObject(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AddFriendViewHolder holder = null;
			if (convertView == null) {
				holder = new AddFriendViewHolder();
			} else {
				holder = (AddFriendViewHolder) convertView.getTag();
			}
			// 设置数据
			holder.setDataAndRefreshUi(mData.optJSONObject(position), position);
			return holder.getConveter();
		}

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

}
