package com.lequan.n1.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lequan.n1.adapter.holder.HomeDetailsRecyclerHolder;
import com.lequan.n1.entity.FriendEntity;
import com.lequan.n1.entity.FriendEntity.Data.Rows;
import com.lequan.n1.entity.HomeDetailEntity;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.HomeDetailsPopupWindow;
import com.lequan.n1.view.LoadMoreRecyclerView;
import com.lequan.n1.view.ObservableScrollView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeDetailsActivity extends BaseActivity implements
OnClickListener {
	// 切换页
	@ViewInject(R.id.rv_homedetails)
	private LoadMoreRecyclerView rv_homedetails;

	// @ViewInject(R.id.rv_homedetails_header)
	// private RecyclerViewHeader rv_homedetails_header;
	// 轮播图
	@ViewInject(R.id.vp_homedetails)
	private ViewPager vp_homedetails;
	private PictureChangeTask mPictureChangeTask;

	// 底部点
	@ViewInject(R.id.ll_homedetails_points)
	private LinearLayout ll_homedetails_points;

	// 信息项
	@ViewInject(R.id.tv_homedetails_personnum)
	private TextView personNum;
	@ViewInject(R.id.tv_homedetails_imagenum)
	private TextView imageNum;
	@ViewInject(R.id.tv_homedetails_money)
	private TextView money;
	@ViewInject(R.id.tv_homedetails_days)
	private TextView days;
	@ViewInject(R.id.tv_homedetails_taskname)
	private TextView taskName;

	// 选项卡切换数据
	@ViewInject(R.id.tv_home_homedetails)
	private TextView DataHome;
	@ViewInject(R.id.tv_time_homedetails)
	private TextView DataTime;
	@ViewInject(R.id.tv_prize_homedetails)
	private TextView DataPrize;
	@ViewInject(R.id.tv_contribute_homedetails)
	private TextView DataContribute;

	// 查看详情
	@ViewInject(R.id.bt_viewdetails)
	private Button bt_Viewdetails;
	@ViewInject(R.id.tv_longlonglong)
	private TextView longlong;

	// 投稿按钮
	@ViewInject(R.id.btn_publisword_activity)
	private Button btn_publisword;

	@ViewInject(R.id.rl_detail_root)
	private RelativeLayout rl_detail_root;

	// 设置开头
	private ActionBar mActionbar;
	private TextView tvTitle;

	@ViewInject(R.id.slv_home_detail_header)
	private ObservableScrollView slv_home_detail_header;


	private HomeDetailEntity homeDetailEntity;
	private FriendEntity friendEntity;
	private List<String> views;
	private RelativeLayout actionbar;
	private HomeDetailsPopupWindow mPopup;
	private recyclerViewAdapter mRyAdapter;
	private String userId;
	private String homeUrl;
	private View headerView;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_homedetails);
		headerView = View.inflate(this, R.layout.home_detail_header, null);
		ViewUtils.inject(this);
		ViewUtils.inject(this, headerView);
	}

	// 设置标题栏
	@Override
	protected void initActionBar() {
		mActionbar = getSupportActionBar();
		if (mActionbar == null) {
			return;
		}
		mActionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mActionbar.setDisplayUseLogoEnabled(true);
		mActionbar.setCustomView(R.layout.homedetails_actionbar);
		tvTitle = (TextView) mActionbar.getCustomView().findViewById(R.id.tv_homedetails_actionbar);
		actionbar = (RelativeLayout) mActionbar.getCustomView().findViewById(R.id.home_actionbar);
		mActionbar.getCustomView().findViewById(R.id.iv_homedetails_actionbar)
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void initData() {
		// 跳转该页面接收int类型id
		Bundle bundle = getIntent().getExtras();
		DetailId = bundle.getInt("themeid");
		loadHomeDetailsNet(DetailId);
		userId = SpUtils.getInstance(this).getString(Constants.USER_ID);

		// TODO 这里需要优化
		this.pages = 1;
		this.currentUrl = Constants.Url.FINDWITHSCOREBYPAGE;
		this.reyParams = new HashMap<String, Object>();
		reyParams.put("id", DetailId);
		loadNetForId(reyParams, currentUrl);

		DataHome.setOnClickListener(this);
		DataTime.setOnClickListener(this);
		DataPrize.setOnClickListener(this);
		DataContribute.setOnClickListener(this);
		bt_Viewdetails.setOnClickListener(this);
		btn_publisword.setOnClickListener(this);
	}

	// 加载recycleview的数据 TODO 嵌套显示问题
	private void loadNetForId(Map<String, Object> map, String url) {
		map.put("page", pages);
		map.put("rows", rows);
		try {
			HttpRequestProxy.sendAsyncPost(url, map,new RequestCallBack<String>() {
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("请求失败");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;
					LogUtils.i(result);
						processRecycleViewData(parseRecycleViewData(result));
					/*	loderMore(parseRecycleViewData(result));*/
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// 加载网络数据
	private void loadHomeDetailsNet(int DetailId) {
		try {
			final String url = Constants.Url.FINDREWARDINFO;
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", DetailId);
			HttpRequestProxy.sendAsyncPost(url, map,new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("请求失败");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;
					Log.d("aaaa", result);
					homeDetailEntity = parseData(result);
					processData(homeDetailEntity);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 解析json数据
	private HomeDetailEntity parseData(String jsonData) {
		Gson gson = new Gson();
		return gson.fromJson(jsonData, HomeDetailEntity.class);
	}

	// 解析轮播的json数据
	private FriendEntity parseRecycleViewData(String jsonData) {
		Gson gson = new Gson();
		return gson.fromJson(jsonData, FriendEntity.class);
	}

	private void processData(HomeDetailEntity homeDetail) {
		homeDetailEntity = homeDetail;
		// 设置轮播图数据
		views = getUrlImage(homeDetail);
		addPoint(views);
		vp_homedetails.setAdapter(new TitleAdapter());
		// 开始调度
		if (mPictureChangeTask == null) {
			mPictureChangeTask = new PictureChangeTask();
		}
		mPictureChangeTask.startTask();
		// 设置开头数据
		tvTitle.setText(homeDetailEntity.data.rewardInfo.rewardSubject + "");
		// 设置中间体数据
		personNum.setText(homeDetailEntity.data.rewardInfo.contributePersonNum + "");
		imageNum.setText(homeDetailEntity.data.rewardInfo.contributePostNum + "");
		money.setText("￥" + homeDetailEntity.data.rewardInfo.rewardMoney + "");
		taskName.setText(homeDetailEntity.data.rewardInfo.rewardSubject + "");
		String str = homeDetailEntity.data.rewardInfo.rewardSummary;
		longlong.setText(str.substring(0, str.indexOf("\r\n")) + "...");
		days.setText(UiUtils.calculateEndTime(homeDetailEntity.data.rewardInfo.rewardEndTime)+ "天");
	}

	@Override
	protected void initEvent() {
		this.vp_homedetails.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				refreshPoints(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// this.rv_homedetails.addOnScrollListener(new OnScrollListener() {
		// @Override
		// public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
		// // TODO Auto-generated method stub
		// super.onScrolled(recyclerView, dx, dy);
		// }
		// });
	}

	private recyclerViewAdapter mAdapter;
	private List<FriendEntity.Data.Rows> mData;
	// recycleView的请求参数
	private int pages = 1;
	private int rows = 6;
	private int DetailId;
	private Map<String, Object> reyParams;
	private String currentUrl;
/*	private void loderMore(FriendEntity friendEntity){
		// 加载更多
		if (ValidateUtils.isValidate(friendEntity.data.rows)) {
			mData.addAll(friendEntity.data.rows);
			rv_homedetails.setLoadingMore(false);
			rv_homedetails.notifyMoreFinish(true);
			rv_homedetails.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
				@Override
				public void onGlobalLayout() {
					rv_homedetails.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
		} else {
			//mAdapter.setLoadingNoMore();
			rv_homedetails.setLoadingMore(false);
			rv_homedetails.notifyMoreFinish(false);
		}
	}*/

	private void processRecycleViewData(FriendEntity friendEntity) {
		// 第一次加载recycleView的数据
		if (mAdapter == null) {
			// 初始化recycleView
			StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
			rv_homedetails.setLayoutManager(layoutManager);
			mData = friendEntity.data.rows;
			mAdapter = new recyclerViewAdapter(mData);
			rv_homedetails.setAdapter(mAdapter);
			rv_homedetails.setHeaderEnable(true);
			rv_homedetails.addHeaderView(headerView);
			slv_home_detail_header.setViewPager(vp_homedetails);
			rv_homedetails.setAutoLoadMoreEnable(true);
			rv_homedetails.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
				@Override
				public void onLoadMore() {
					pages++;
					rv_homedetails.setLoadingMore(true);
					// 加载更多
					LogUtils.i("加载更多数据");
					loadNetForId(reyParams, currentUrl);
				}
			});
		}else {
			// 加载更多
			if (ValidateUtils.isValidate(friendEntity.data.rows)) {
				mData.addAll(friendEntity.data.rows);
				rv_homedetails.setLoadingMore(false);
				rv_homedetails.notifyMoreFinish(true);
				rv_homedetails.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
					@Override
					public void onGlobalLayout() {
						rv_homedetails.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				});
			} else {
				//mAdapter.setLoadingNoMore();
				rv_homedetails.setLoadingMore(false);
				rv_homedetails.notifyMoreFinish(false);
			}
		}
	}

	// 获取网络图片地址到list集合
	private List<String> getUrlImage(HomeDetailEntity homeDetail) {
		List<String> list = new ArrayList<String>();
		if (ValidateUtils.isValidate(homeDetail.data.rewardInfo.backgroundUrl)) {
			list.add(homeDetail.data.rewardInfo.backgroundUrl);
		}
		if (ValidateUtils.isValidate(homeDetail.data.rewardInfo.pic1Url)) {
			list.add(homeDetail.data.rewardInfo.pic1Url);
		}
		if (ValidateUtils.isValidate(homeDetail.data.rewardInfo.pic2Url)) {
			list.add(homeDetail.data.rewardInfo.pic2Url);
		}
		if (ValidateUtils.isValidate(homeDetail.data.rewardInfo.pic3Url)) {
			list.add(homeDetail.data.rewardInfo.pic3Url);
		}
		if (ValidateUtils.isValidate(homeDetail.data.rewardInfo.pic4Url)) {
			list.add(homeDetail.data.rewardInfo.pic4Url);
		}
		return list;
	}

	// 顶部轮播适配器
	private class TitleAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(UiUtils.getContext());
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(R.drawable.backgroudimage2x);
			BitmapUtil.display(iv, views.get(position));
			container.addView(iv);
			return iv;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_home_homedetails:
			DataHome.setTextColor(getResources().getColor(R.color.black));
			DataPrize.setTextColor(getResources().getColor(R.color.gray));
			DataTime.setTextColor(getResources().getColor(R.color.gray));
			DataContribute.setTextColor(getResources().getColor(R.color.gray));
			mData.clear();
			reyParams = new HashMap<String, Object>();
			reyParams.put("id", DetailId);
			this.pages = 1;
			this.currentUrl = Constants.Url.FINDWITHSCOREBYPAGE;
			loadNetForId(reyParams, Constants.Url.FINDWITHSCOREBYPAGE);
			break;
		case R.id.tv_time_homedetails:
			DataHome.setTextColor(getResources().getColor(R.color.gray));
			DataPrize.setTextColor(getResources().getColor(R.color.gray));
			DataTime.setTextColor(getResources().getColor(R.color.black));
			DataContribute.setTextColor(getResources().getColor(R.color.gray));
			mData.clear();
			reyParams = new HashMap<String, Object>();
			reyParams.put("id", DetailId);
			this.pages = 1;
			this.currentUrl = Constants.Url.FINDWITHTIMEBYPAGE;
			loadNetForId(reyParams, Constants.Url.FINDWITHTIMEBYPAGE);
			break;
		case R.id.tv_prize_homedetails:
			DataHome.setTextColor(getResources().getColor(R.color.gray));
			DataPrize.setTextColor(getResources().getColor(R.color.black));
			DataTime.setTextColor(getResources().getColor(R.color.gray));
			DataContribute.setTextColor(getResources().getColor(R.color.gray));
			mData.clear();
			reyParams = new HashMap<String, Object>();
			reyParams.put("id", DetailId);
			this.pages = 1;
			this.currentUrl = Constants.Url.FINDWITHLOTTERYBYPAGE;
			loadNetForId(reyParams, currentUrl);
			break;
		case R.id.tv_contribute_homedetails:
			DataHome.setTextColor(getResources().getColor(R.color.gray));
			DataPrize.setTextColor(getResources().getColor(R.color.gray));
			DataTime.setTextColor(getResources().getColor(R.color.gray));
			DataContribute.setTextColor(getResources().getColor(R.color.black));
			mData.clear();
			reyParams = new HashMap<String, Object>();
			reyParams.put("rewardid", DetailId);
			reyParams.put("userid", Integer.parseInt(userId));
			this.pages = 1;
			this.currentUrl = Constants.Url.FINDMYWORK;
			loadNetForId(reyParams, currentUrl);
			break;
		case R.id.bt_viewdetails:
			showDitailInfo();
			break;
		case R.id.btn_publisword_activity:
			LoginOrChange();
			break;
		default:
			break;
		}
	}

	private void showDitailInfo() {
		if (mPopup == null) {
			mPopup = new HomeDetailsPopupWindow(this, homeDetailEntity);
		}
		mPopup.showAtLocation(rl_detail_root, Gravity.BOTTOM, 0, 0);
	}

	// 瀑布流
	class recyclerViewAdapter extends
	RecyclerView.Adapter<HomeDetailsRecyclerHolder> {

		private List<FriendEntity.Data.Rows> list;

		public recyclerViewAdapter(List<FriendEntity.Data.Rows> list) {
			this.list = list;
		}

		public void setData(List<Rows> data) {
			this.list = data;
		}

		@Override
		public int getItemCount() {
			return list != null ? list.size() : 0;
		}

		// 绑定viewholder
		@Override
		public void onBindViewHolder(HomeDetailsRecyclerHolder holder, int pos) {
			holder.setDataAndRefreshUi(list.get(pos - 1));
		}

		// 设置viewholder数据
		@Override
		public HomeDetailsRecyclerHolder onCreateViewHolder(ViewGroup view,final int arg1) {
			LogUtils.i("arg1" + arg1);
			View v = View.inflate(UiUtils.getContext(),R.layout.fragment_homedetails_image, null);
			HomeDetailsRecyclerHolder hdrHolder = new HomeDetailsRecyclerHolder(v);
			return hdrHolder;
		}

		@Override
		public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
			if(observer != null){
				super.unregisterAdapterDataObserver(observer);
			}
		}
	}

	// 判断点击是否是登录状态
	public void LoginOrChange() {
		if (Constants.isLogin) {
			Intent intent = new Intent(this, PublishWorkActivity.class);
			intent.putExtra("rewardid", DetailId);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}

	// 设置自动轮播
	public class PictureChangeTask extends Handler implements Runnable {
		public void startTask() {
			stopTask();
			postDelayed(this, 2000);
		}

		public void stopTask() {
			removeCallbacks(this);
		}

		@Override
		public void run() {
			vp_homedetails.setCurrentItem((vp_homedetails.getCurrentItem() + 1) % vp_homedetails.getAdapter().getCount());
			postDelayed(this, 4000);
		}
	}

	// 设置点
	public void addPoint(List<String> views) {
		for (int i = 0; i < views.size(); i++) {
			View point = new View(UiUtils.getContext());
			point.setBackgroundResource(R.drawable.guide__point_bg);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UiUtils.dip2px(8), UiUtils.dip2px(8));
			params.bottomMargin = UiUtils.dip2px(5);
			if (i != 0) {
				params.leftMargin = UiUtils.dip2px(5);
			}
			point.setLayoutParams(params);
			ll_homedetails_points.addView(point);
		}
		refreshPoints(0);
	}

	// 刷新点
	private void refreshPoints(int index) {
		for (int i = 0; i < views.size(); i++) {
			ll_homedetails_points.getChildAt(i).setEnabled(i == index);
		}
	}
}
