package com.lequan.n1.activity.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.lequan.n1.activity.HomeDetailsActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.adapter.holder.BaseViewHolder;
import com.lequan.n1.adapter.holder.HomePicViewHolder;
import com.lequan.n1.adapter.holder.HomeTitleViewHolder;
import com.lequan.n1.adapter.holder.HotViewHolder;
import com.lequan.n1.adapter.holder.TopicRewardViewHolder;
import com.lequan.n1.entity.HomeEntity;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.FileUtils;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.PinnedSectionListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.IOUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeFragment extends BaseFragment {

	@ViewInject(R.id.pslv_home)
	private PinnedSectionListView pstl_main;

	@ViewInject(R.id.srl_home)
	private SwipeRefreshLayout srl_home;

	@ViewInject(R.id.rl_home_guide_container)
	private RelativeLayout rl_home_guide_container;

	@ViewInject(R.id.iv_guide_wait_yout_get)
	private ImageView iv_guide_wait_yout_get;

	@ViewInject(R.id.iv_guide_reward_total)
	private ImageView iv_guide_reward_total;

	@ViewInject(R.id.iv_guide_residue_time)
	private ImageView iv_guide_residue_time;

	private HomeEntity mData;

	private PstlAdapter mAdapter;

	private HomePicViewHolder mHeader;

	private LoadMoreHolder mLoadMoreHolder;

	private SpUtils mSpUtils;


	@Override
	protected View initView() {
		View view = View.inflate(mContext, R.layout.ll_fragment_home, null);
		ViewUtils.inject(this, view);
		
		return view;
	}

	@Override
	protected void initData() {
		mSpUtils = SpUtils.getInstance(mContext);
		boolean flag = mSpUtils.getBoolean(Constants.IS_FIRST_HOME);
		if(flag){
			this.rl_home_guide_container.setVisibility(View.GONE);
		}else{
			this.rl_home_guide_container.setVisibility(View.VISIBLE);
		}

		//初始化数据
		mHeader = new HomePicViewHolder();
		this.pstl_main.addHeaderView(mHeader.getConveter());
		mAdapter = new PstlAdapter();
		this.pstl_main.setAdapter(mAdapter);
		// 读取本地数据
		File dir = FileUtils.getCacheFile();
		File cacheFile = new File(dir, "home");
		BufferedReader reader = null;
		try {
			if (cacheFile.exists()) {
				reader = new BufferedReader(new FileReader(cacheFile));
				String lastTime = reader.readLine();
				if (System.currentTimeMillis() - Long.parseLong(lastTime) < Constants.CACHE_TIME) {
					String data = reader.readLine();
					if (ValidateUtils.isValidate(data)) {
						HomeEntity entity = parseData(data);
						processData(entity);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		// 加载网络数据
		loadDataFromNet();
	}

	// 加载网络数据
	private void loadDataFromNet() {
		try {
			final String url = Constants.Url.HOME_PAGE;
			HttpRequestProxy.sendAsyncPost(url, new HashMap<String, String>(), new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("请求失败");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;
					BufferedWriter writer = null;
					try {
						File dir = FileUtils.getCacheFile();
						File cache = new File(dir, "home");
						writer = new BufferedWriter(new FileWriter(cache));
						writer.write(System.currentTimeMillis() + "");
						writer.newLine();
						writer.write(result);
						writer.flush();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						IOUtils.closeQuietly(writer);
					}
					// 解析数据
					HomeEntity data = parseData(result);
					// 处理数据
					processData(data);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 处理数据
	private void processData(HomeEntity data) {
		mData = data;
		// 设置轮播图数据
		mHeader.setDataAndRefreshUi(data, 0);
		// 刷新ui
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void initEvent() {
		//向导设置点击事件
		this.iv_guide_wait_yout_get.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_guide_wait_yout_get.setVisibility(View.INVISIBLE);
				iv_guide_residue_time.setVisibility(View.VISIBLE);
				iv_guide_reward_total.setVisibility(View.VISIBLE);
				pstl_main.smoothScrollToPosition(4);
			}
		});

		this.iv_guide_residue_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_guide_residue_time.setVisibility(View.INVISIBLE);
				if(iv_guide_reward_total.getVisibility()==View.INVISIBLE){
					rl_home_guide_container.setVisibility(View.GONE);
					//保存数据
					mSpUtils.putBoolean(Constants.IS_FIRST_HOME, true);
				}
			}
		});

		this.iv_guide_reward_total.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_guide_reward_total.setVisibility(View.INVISIBLE);
				if(iv_guide_residue_time.getVisibility()==View.INVISIBLE){
					rl_home_guide_container.setVisibility(View.GONE);
					//保存数据
					mSpUtils.putBoolean(Constants.IS_FIRST_HOME, true);
				}
			}
		});

		this.srl_home.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (srl_home.isRefreshing()) {
					new Thread() {

						public void run() {
							SystemClock.sleep(2000);
							UiUtils.runOnSafe(new Runnable() {

								@Override
								public void run() {
									srl_home.setRefreshing(false);
									// 重新访问网络获取数据
									loadDataFromNet();
								}
							});
						};

					}.start();
				}
			}
		});

		this.pstl_main.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position < 4){
					return;
				}else{
					Intent intent = new Intent(mContext, HomeDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("themeid", mData.data.xsPage.rows.get(position-4).id);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	// 解析数据
	private HomeEntity parseData(String jsonData) {
		Gson gson = new Gson();
		return gson.fromJson(jsonData, HomeEntity.class);
	}

	private final class PstlAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {

		private final int NORMAL = 0;
		private final int PINNED = 1;
		private final int HOTTOPIC = 2;
		private final int LOADMORE = 3;

		@Override
		public int getCount() {
			if (mData != null) {
				return mData.data.xsPage.rows.size() + 2 + 1 + 1;//包括加载更多
			}
			return 0;
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
		public int getItemViewType(int position) {
			if (position == 0 || position == 2) {
				return PINNED;
			} else if (position == 1) {
				return HOTTOPIC;
			} else if (getCount() - 1 == position) {
				return LOADMORE;
			} else {
				return NORMAL;
			}
		}

		@Override
		public int getViewTypeCount() {
			return 3 + 1;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BaseViewHolder<HomeEntity> baseViewHolder = null;
			if (convertView == null) {
				if (position == 0 || position == 2) {
					baseViewHolder = new HomeTitleViewHolder();
				} else if (position == 1) {
					baseViewHolder = new HotViewHolder();
				} else if (getItemViewType(position) == LOADMORE) {
					baseViewHolder = getLoadMoreHolder();
				} else {
					baseViewHolder = new TopicRewardViewHolder();
				}
			} else {
				baseViewHolder = (BaseViewHolder<HomeEntity>) convertView.getTag();
			}
			if (getItemViewType(position) == LOADMORE) {
				// 处理加载更多
				processLoadMore();
			} else {
				// 设置数据刷新ui
				baseViewHolder.setDataAndRefreshUi(mData, position);
			}
			return baseViewHolder.getConveter();
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return PINNED == viewType;
		}
	}

	// 加载更多数据
	private final class LoadMoreHolder extends BaseViewHolder<HomeEntity> {
		private static final int none = 0;
		private static final int loading = 1;
		private static final int retry = 2;

		private int currentState = -1;

		private LinearLayout mLoading;
		private LinearLayout mRetry;

		@Override
		public View initView() {
			View view = View.inflate(UiUtils.getContext(), R.layout.item_load_more, null);
			mLoading = (LinearLayout) view.findViewById(R.id.item_loadmore_container_loading);
			mRetry = (LinearLayout) view.findViewById(R.id.item_loadmore_container_retry);
			return view;
		}

		// 根据不同的状态刷新界面:控制视图的切换
		@Override
		public void refreshUi(HomeEntity data, int state) {
			// 记录当前的状态
			currentState = state;
			mLoading.setVisibility(View.GONE);
			mRetry.setVisibility(View.GONE);
			switch (state) {
			case loading:
				mLoading.setVisibility(View.VISIBLE);
				break;
			case retry:
				mRetry.setVisibility(View.VISIBLE);
				break;
			case none:
				break;
			default:
				break;
			}
		}

	}

	public void processLoadMore() {
		//TODO 加载更多
		mLoadMoreHolder.setDataAndRefreshUi(null,LoadMoreHolder.loading);
		//开启线程加载数据
		new Thread(){

			public void run() {
				SystemClock.sleep(2000);
				UiUtils.runOnSafe(new Runnable() {

					@Override
					public void run() {
						mLoadMoreHolder.setDataAndRefreshUi(null, LoadMoreHolder.none);
					}
				});
			};

		}.start();
	}

	private BaseViewHolder<HomeEntity> getLoadMoreHolder() {
		if (mLoadMoreHolder == null) {
			mLoadMoreHolder = new LoadMoreHolder();
		}
		return mLoadMoreHolder;
	}

}
