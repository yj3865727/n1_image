package com.lequan.n1.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lequan.n1.activity.AddFriendActivity;
import com.lequan.n1.activity.HomeDetailsActivity;
import com.lequan.n1.activity.OthersActivity;
import com.lequan.n1.activity.PicDetailActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.entity.BaseEntity;
import com.lequan.n1.entity.FriendEntity;
import com.lequan.n1.entity.FriendEntity.Data.Rows;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FriendFragment extends BaseFragment {

	private PullToRefreshListView listView;
	private ImageView addFriend;
	private ProgressBar mProgressBar;
	private Intent intent;
	private BaseEntity baseEntity;
	private FriendEntity friendEntity;
	private List<Rows> mList = new ArrayList<FriendEntity.Data.Rows>();

	private FriendPicAdapter adapter;
	private Toast toast;
	// TODO 此处会报空指针问题
	private String loginId;

	// 网络请求请求数据的参数
	private int dataNum = 6;
	private int pageNum = 1;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@Override
	protected View initView() {
		View view = View.inflate(mContext, R.layout.ll_fragment_friend, null);
		addFriend = (ImageView) view
				.findViewById(R.id.friend_title_addfriend_img);
		listView = (PullToRefreshListView) view
				.findViewById(R.id.friend_listview);
		listView.getRefreshableView().setDivider(null);
		listView.getRefreshableView().setSelector(android.R.color.transparent);

		mProgressBar = (ProgressBar) view
				.findViewById(R.id.friend_pic_progressbar);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				// // 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.logo_placeholder2x)
				// // 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.logo_placeholder2x)
				// // 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.logo_placeholder2x)
				.cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_8888)// 设置图片的解码类型
				.considerExifParams(true)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();

		return view;
	}

	@Override
	protected void initData() {

		this.loginId = SpUtils.getInstance(mContext).getString(
				Constants.USER_ID);

		loadDataByNet();// 通过网络请求加载数据到页面
	}

	@Override
	protected void initEvent() {
		addFriend(); // 点击进入添加朋友页面
		pullToRefresh();// 下拉刷新，上拉加载
	}

	private void pullToRefresh() {
		listView.setMode(Mode.BOTH);
		listView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载…");
		listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("放开加载…");
		listView.getLoadingLayoutProxy(false, true).setReleaseLabel("正在载入…");
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (listView.isHeaderShown()) {
					pageNum = 1;
					loadDataByNet(); // 下拉刷新数据，重新网络请求
				} else if (listView.isFooterShown()) {
					pageNum++;
					loadDataByNet();
				}
			}
		});
	}

	private void addFriend() {
		addFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AddFriendActivity.class);
				startActivity(intent);
			}
		});
	}

	// 异步请求
	private void loadDataByNet() {
		try {
			final String url = Constants.Url.FRIEND_PIC;
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("id", loginId); //
			params.put("page", pageNum);
			params.put("rows", dataNum);
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
							FriendEntity entity = parseData(data);
							handleData(entity);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 同步请求
	private void loadMoreBySync() {
		final String url = Constants.Url.FRIEND_PIC;
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("id", loginId);
		params.put("page", pageNum);
		params.put("rows", dataNum);
		try {
			ResponseStream response = HttpRequestProxy
					.sendSyncPost(url, params);
			if (response.getStatusCode() == 200) {
				String result = response.readString();
				FriendEntity entity = parseData(result);
				handleData(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 解析数据
	protected FriendEntity parseData(String result) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		friendEntity = gson.fromJson(result, FriendEntity.class);
		return friendEntity;
	}

	// 处理数据
	private void handleData(FriendEntity friendEntity) {
		mList = friendEntity.data.rows;

		if (adapter == null) {
			adapter = new FriendPicAdapter(mList, mContext);
			listView.setAdapter(adapter);
		} else { // 上拉和下拉

			if (listView.isHeaderShown()) {
				adapter.updateData(mList);
				adapter.notifyDataSetChanged();
			}
			if (listView.isFooterShown()) {
				if (mList.size() == 0) { // 所有数据加载完毕时
					listView.getLoadingLayoutProxy(false, true).setPullLabel(
							"没有更多数据了...");
					listView.getLoadingLayoutProxy(false, true)
							.setRefreshingLabel("没有更多数据了...");
					listView.getLoadingLayoutProxy(false, true)
							.setReleaseLabel("没有更多数据了...");
				} else {
					adapter.addData(mList);
					adapter.notifyDataSetChanged();
				}
			}
			listView.onRefreshComplete();
		}
	}

	class FriendPicAdapter extends BaseAdapter {
		private List<FriendEntity.Data.Rows> list = new ArrayList<FriendEntity.Data.Rows>();
		private Context mContext;

		public FriendPicAdapter(List<FriendEntity.Data.Rows> list,
				Context Context) {
			this.list = list;
			this.mContext = Context;
		}

		public void addData(List<FriendEntity.Data.Rows> mlist) {
			list.addAll(mlist);
		}

		public void updateData(List<FriendEntity.Data.Rows> mlist) {
			list = mlist;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FriendPicHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_friend_listview, parent, false);
				holder = new FriendPicHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (FriendPicHolder) convertView.getTag();
			}
			holder.setDataAndRefreshUi(list.get(position));
			holder.initListener(list.get(position));
			return convertView;
		}

		public class FriendPicHolder {
			// 普通数据
			ImageView timeImg;
			TextView timeTv;
			TextView userName;
			ImageView headImg;
			ImageView photoImg;
			TextView zhutiTv;
			ImageView commentImg;
			TextView commentTv;
			ImageView praiseImg;
			TextView praiseTv;
			LinearLayout praise;
			LinearLayout comment;
			LinearLayout theme;

			private void initListener(final Rows data) {
				/*
				 * 点赞事件
				 */
				praise.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mProgressBar.setVisibility(View.VISIBLE);
						sendPraise(data);
					}
				});

				/*
				 * * 进入图片详情
				 */
				comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						intent = new Intent(mContext, PicDetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("workid", data.id); // workid
						bundle.putInt("userid", data.userid);// 作者的id
						intent.putExtra("key", bundle);
						startActivity(intent);
					}
				});
				/*
				 * 进入主题
				 */
				theme.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						intent = new Intent(mContext, HomeDetailsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("themeid", data.rewardid);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
				/*
				 * 点击头像查看朋友信息
				 */
				headImg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						intent = new Intent(mContext, OthersActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("userid", data.userid);
						intent.putExtra("key", bundle);
						startActivity(intent);
					}
				});
			}

			public FriendPicHolder(View itemView) {
				timeImg = (ImageView) itemView
						.findViewById(R.id.friend_time_img);
				timeTv = (TextView) itemView.findViewById(R.id.friend_time_tv);
				userName = (TextView) itemView
						.findViewById(R.id.friend_username_tv);
				headImg = (ImageView) itemView
						.findViewById(R.id.friend_touxiang_img);
				photoImg = (ImageView) itemView
						.findViewById(R.id.friend_photo_img);
				zhutiTv = (TextView) itemView
						.findViewById(R.id.friend_zhuti_tv);
				commentTv = (TextView) itemView
						.findViewById(R.id.friend_pinglun_tv);
				commentImg = (ImageView) itemView
						.findViewById(R.id.friend_pinglun_img);
				praiseImg = (ImageView) itemView
						.findViewById(R.id.friend_zan_img);
				praiseTv = (TextView) itemView.findViewById(R.id.friend_zan_tv);
				praise = (LinearLayout) itemView
						.findViewById(R.id.friend_dianzan_ll);// 点赞
				comment = (LinearLayout) itemView
						.findViewById(R.id.friend_pinglun_ll);// 评论
				theme = (LinearLayout) itemView
						.findViewById(R.id.friend_zhuti_ll); // 主题

			}

			public void setDataAndRefreshUi(Rows data) {
				imageLoader.displayImage(data.appuser.headphoto,
						headImg, options);
				/*imageLoader.displayImage(data.workMainPic,
						photoImg, options);*/
		        String imageUrl = data.workMainPic;
		        ImageSize mImageSize = new ImageSize(data.imageH, data.imageW);  
		        ImageLoader.getInstance().loadImage(imageUrl, mImageSize, new SimpleImageLoadingListener(){  
		            public void onLoadingComplete(String imageUri, View view,  
		                    Bitmap loadedImage) {  
		                super.onLoadingComplete(imageUri, view, loadedImage);
		                float scal = (float)photoImg.getWidth()/(float)loadedImage.getWidth();
		                Matrix matrix = new Matrix();
		                matrix.setScale(scal, scal);
		                Bitmap newbit = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(),  
		                        matrix, true);
		                loadedImage.recycle();
		                photoImg.setImageBitmap(newbit);  
		            }  
		              
		        });  
				/*photoImg.setRelative(AutoScaleView.RELATIVE_WIDTH);
				
				ImageView img=new ImageView(mContext);
				BitmapUtil.display(img, data.workMainPic);
				photoImg.addView(img);*/
								
				timeTv.setText(StringUtils.timeFormat(data.publishTime));
				userName.setText(data.appuser.loginSn + "");
				zhutiTv.setText(data.reward.rewardSubject + "");
				commentTv.setText(data.commentCount + "");
				praiseTv.setText(data.praiseCount + "");
				photoImg.setTag(data.workMainPic);
				headImg.setTag(data.appuser.headphoto);
			}

			public void sendPraise(final Rows data) {

				final HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("id", 1);
				params.put("workid", data.id); // 被点赞作品的id
				params.put("userid", loginId); // 登录用户的id
				final String url = Constants.Url.FRIEND_PRAISE;
				try {
					HttpRequestProxy.sendAsyncPost(url, params,
							new RequestCallBack<String>() {
								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									LogUtils.i("请求失败");
									mProgressBar.setVisibility(View.GONE);
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									mProgressBar.setVisibility(View.GONE);
									String result = arg0.result;
									Gson gson = new Gson();
									/*
									 * 设置点赞提示
									 */
									baseEntity = gson.fromJson(result,
											BaseEntity.class);

									if (baseEntity.getDesc().equals("点赞成功")) {
										toast = Toast.makeText(mContext,
												baseEntity.getDesc() + "",
												Toast.LENGTH_SHORT);
										data.praiseCount++;
										praiseTv.setText(data.praiseCount + "");
									}
									if (baseEntity.getDesc()
											.equals("您以为此作品点过赞")) {
										toast = Toast.makeText(mContext,
												"你已经点过赞了", Toast.LENGTH_SHORT);
									}
									if (toast != null) {
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
									}
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
