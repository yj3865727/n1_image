package com.lequan.n1.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lequan.n1.adapter.PicCommentsAdapter;
import com.lequan.n1.entity.PraisesEntity;
import com.lequan.n1.entity.UserWork;
import com.lequan.n1.entity.WorkPhotosEntity;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.AnimationUtils;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.ListViewForScrollUtil;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.PhotoUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.StringUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.AutoScaleView;
import com.lequan.n1.view.PicToReportPopupWindow;
import com.lequan.n1.view.ReboundScrollView;
import com.lequan.n1.view.ToReportPopupWindow;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PicDetailActivity extends BaseActivity implements OnClickListener
,	OnItemClickListener, OnTouchListener 
{
	private ImageView picBackImg; // 返回按钮
	private TextView reportTv; // 举报用户按钮
	private AutoScaleView mainImg;// 主图片
	private ImageView headImg;// 发布者头像
	private TextView userNameTv;// 发布者用户名
	private TextView descriptionTv;// 作品描述
	private TextView themeTv; // 所属主题
	private TextView commentCountTv;
	private TextView praiseNumTv;// 点赞数
	private LinearLayout praisesHeadLayout;// 点赞列表头像
	private HorizontalScrollView photosScoll;
	private LinearLayout photosLayout;// 设置点击事件进入点赞详情页面
	private ListView commentsListView;
	private ReboundScrollView detailScroll;
	private TextView praiseTv;
	private ImageView praiseImg1;
	private ImageView praiseImg2;
	private EditText commentEdit;
	private Button sendCommentBtn;
	private String mUserId;
	private UserWork userWork;
	private List<WorkPhotosEntity> photosList;
	private List<PraisesEntity> praiseList = new ArrayList<PraisesEntity>(0); // 点赞列表
	private Intent intent;
	private RelativeLayout pic_rl;
	private View pic_mViewMask;
	private String str;
	private PicCommentsAdapter adapter;
	private boolean isUpdate = false; // 是否更新评论列表
	private boolean isPraise = false; // 是否已经点赞
	private String parentId = null; //
	private String touserid = null;
	// 登录用户的id和头像地址
	private String loginId = SpUtils.getInstance(this).getString(
			Constants.USER_ID);
	private String loginHead = SpUtils.getInstance(this).getString(
			Constants.HEADIMG);
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@Override
	protected void initView() {
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

		mUserId = SpUtils.getInstance(this).getString(Constants.USER_ID);
		setContentView(R.layout.activity_pic_detail);
		findViewId();
		loadPicDetialByNet();
	}

	@Override
	protected void initEvent() {
		picBackImg.setOnClickListener(this); // 返回上一页面
		reportTv.setOnClickListener(this); // 举报用户
		praisesHeadLayout.setOnClickListener(this); // 进入点赞列表、
		sendCommentBtn.setOnClickListener(this); // 发送评论
		praiseTv.setOnClickListener(this);
		praiseImg1.setOnClickListener(this);
		headImg.setOnClickListener(this);
		commentsListView.setOnTouchListener(this);
		commentsListView.setOnItemClickListener(this);
	}

	private void findViewId() {
		picBackImg = (ImageView) findViewById(R.id.pic_detail_back_img);
		reportTv = (TextView) findViewById(R.id.pic_detail_report_tv);
		mainImg = (AutoScaleView) findViewById(R.id.pic_detail_photo_img);
		headImg = (ImageView) findViewById(R.id.pic_detail_head_img);
		userNameTv = (TextView) findViewById(R.id.pic_detail_username_tv);
		descriptionTv = (TextView) findViewById(R.id.pic_detail_photo_description_tv);
		themeTv = (TextView) findViewById(R.id.pic_detail_theme_tv);
		commentCountTv = (TextView) findViewById(R.id.pic_detail_commentcount_tv);
		praiseNumTv = (TextView) findViewById(R.id.pic_detail_praisenumber_tv);
		photosLayout = (LinearLayout) findViewById(R.id.pic_detail_photo_ll);
		praisesHeadLayout = (LinearLayout) findViewById(R.id.pic_detail_praisehead_ll);
		photosScoll = (HorizontalScrollView) findViewById(R.id.pic_detail_photo_horiScroll);
		commentsListView = (ListView) findViewById(R.id.pic_detail_comment_lv);
		detailScroll = (ReboundScrollView) findViewById(R.id.pic_detail_scroll);
		praiseImg1 = (ImageView) findViewById(R.id.pic_detail_praise_img1);
		praiseImg2 = (ImageView) findViewById(R.id.pic_detail_praise_img2);
		praiseTv = (TextView) findViewById(R.id.pic_detail_praise_tv);
		commentEdit = (EditText) findViewById(R.id.pic_comment_content_edit);
		sendCommentBtn = (Button) findViewById(R.id.pic_comment_send_btn);
		pic_rl = (RelativeLayout) findViewById(R.id.pic_rl);
		pic_mViewMask = findViewById(R.id.pic_viewMask);
	}

	// 图片详细信息，点赞列表，评论列表
	private void loadPicDetialByNet() {
		try {
			final String url = Constants.Url.PIC_DETAIL;

			HashMap<String, Object> params = new HashMap<String, Object>();
			/*
			 * 取其他activity传来的数据对象
			 */
			intent = getIntent();
			Bundle bundle = intent.getBundleExtra("key");
			params.put("id", bundle.getInt("workid")); // 4207 多张图片 workid,3704
			// 圈圈
			// params.put("id", 7971);
			params.put("page", 1);
			params.put("rows", 5);
			HttpRequestProxy.sendAsyncPost(url, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							LogUtils.i("请求失败");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							String data = arg0.result;
							UserWork userWork = parseData(data); // 解析数据
							if (!isUpdate) { // 第一次加载
								handleData(userWork);
							} else {
								updateData(userWork);
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 解析数据
	protected UserWork parseData(String result) {
		Gson gson = new Gson();
		LogUtils.e(result);
		userWork = gson.fromJson(result, UserWork.class);
		return userWork;
	}

	// 处理数据
	private void handleData(final UserWork userWork) {
		// 先判断是否已经点赞
		if (userWork.data.praiseCount > 0) {
			for (int i = 0; i < userWork.data.praiseCount; i++) {
				if ((userWork.data.praises.get(i).appuser.id + "")
						.equals(loginId)) {
					isPraise = true;
					praiseImg1
							.setImageResource(R.drawable.homepage_hongaixin2x);
					praiseImg2
							.setBackgroundResource(R.drawable.homepage_hongaixin2x);
					praiseTv.setTextColor(Color.RED); // 设置红色
				}
			}
		}
		// 加载主图
		mainImg.setRelative(AutoScaleView.RELATIVE_WIDTH);
		mainImg.setScale(userWork.data.imageW * 1f / userWork.data.imageH);
		ImageView img = new ImageView(PicDetailActivity.this);
		BitmapUtil.display(img, userWork.data.workMainPic);// 设置图片
		mainImg.addView(img);
		mainImg.setVisibility(View.VISIBLE);
		headImg.setVisibility(View.VISIBLE);
		userNameTv.setVisibility(View.VISIBLE);
		descriptionTv.setVisibility(View.VISIBLE);
		imageLoader.displayImage(userWork.data.appuser.headphoto,
				headImg, options);
		userNameTv.setText(userWork.data.appuser.loginSn + ""); // 设置发布者昵称
		Log.e("loginsn", userWork.data.appuser.loginSn + "");
		descriptionTv.setText(userWork.data.description + ""); // 设置作品描述
		themeTv.setText(userWork.data.reward.rewardSubject + ""); // 主题
		commentCountTv.setText("共有" + userWork.data.commentCount + "条评论"); // 评论数
		praiseNumTv.setText(userWork.data.praiseCount + "");// 点赞数
		setPraiseHead(userWork);// 点赞头像
		setPhotos(userWork); // 超过一张图片时，横向缩略图
		if (userWork.data.comments.size() > 0) {
			Log.e("list", userWork.data.comments.size() + "");
			commentsListView.setVisibility(View.VISIBLE);
			adapter = new PicCommentsAdapter(userWork.data.comments,
					PicDetailActivity.this);
			commentsListView.setAdapter(adapter);
			/*
			 * 解决scrollView里嵌套listview问题
			 */
			ListViewForScrollUtil
					.setListViewHeightBasedOnChildren(commentsListView);
		} else {
			commentsListView.setVisibility(View.GONE);
		}
	}

	/*
	 * 实现横向点赞列表头像
	 */
	private void setPraiseHead(UserWork userWork) {
		praiseList = userWork.data.praises;
		if (praiseList.size() > 0) { // 有人点赞
			praisesHeadLayout.setOrientation(LinearLayout.HORIZONTAL);
			for (int i = 0; i < praiseList.size(); i++) {
				ImageView praiseHead = new ImageView(this);
				BitmapUtil.display(praiseHead,
						praiseList.get(i).appuser.headphoto);// 设置图片
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						UiUtils.dip2px(30), UiUtils.dip2px(30));
				if (i != 0) {
					params.leftMargin = UiUtils.dip2px(5);
				}
				praiseHead.setLayoutParams(params);
				praisesHeadLayout.addView(praiseHead);
			}
		}
	}

	/*
	 * 设多图片时的展示
	 */
	private void setPhotos(UserWork userWork) {
		photosList = userWork.data.workPhotos;
		if (photosList.size() > 1) {
			photosScoll.setVisibility(View.VISIBLE);
			for (int i = 0; i < photosList.size(); i++) {
				AutoScaleView autoScaleView = new AutoScaleView(this);
				autoScaleView.setRelative(AutoScaleView.RELATIVE_HEIGHT);
				autoScaleView.setScale(photosList.get(i).imageW * 1f
						/ photosList.get(i).imageH);
				ImageView photos = new ImageView(this);
				photos.setImageResource(R.drawable.none_data_bg);
				BitmapUtil.display(photos, photosList.get(i).url);// 设置图片
				autoScaleView.addView(photos);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						UiUtils.dip2px(80));
				if (i != 0) {
					params.leftMargin = UiUtils.dip2px(5);
				}
				final int index=i;
				autoScaleView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent imagesView = new Intent(UiUtils.getContext(),CheckImageActivity.class);
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("imageUrl", StringUtils.list2StringArray(photosList));
						bundle.putInt("imageId",index+1);
						imagesView.putExtra("key",bundle);
						startActivity(imagesView);
					}
				});
				photosLayout.addView(autoScaleView, params);
			}
		}
	}

	ToReportPopupWindow toReportPopupWindow;
	PicToReportPopupWindow picToReportPopupWindow;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		detailScroll.requestDisallowInterceptTouchEvent(true);
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// 唤起软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(commentEdit, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
				InputMethodManager.HIDE_IMPLICIT_ONLY);
		// commentEdit.setHint("@"+userWork.data.comments.get(position).appuser.loginSn);
		parentId = userWork.data.comments.get(position).id + "";
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		Bundle bundle = null;
		switch (v.getId()) {
		/*
		 * 返回上页
		 */
		case R.id.pic_detail_back_img:
			PicDetailActivity.this.finish();
			break;
		/*
		 * 举报用户
		 */
		case R.id.pic_detail_report_tv:
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = 0.7f;
			getWindow().setAttributes(lp);
			toReportPopupWindow = PhotoUtils.getToReportPopupWindow(this, this,
					pic_rl);
			AnimationUtils.showAlpha(pic_mViewMask);
			break;
		/*
		 * 进入点赞详情
		 */
		case R.id.pic_detail_praisehead_ll:
			intent = new Intent(PicDetailActivity.this,
					PraiseListActivity.class);
			bundle = new Bundle();
			bundle.putInt("workid",
					getIntent().getBundleExtra("key").getInt("workid"));
			intent.putExtra("key", bundle);
			startActivity(intent);
			break;

		case R.id.pic_comment_send_btn:
			sendComment(touserid, parentId); // 发送评论
			break;
		case R.id.pic_detail_praise_img1:
		case R.id.pic_detail_praise_tv:
			addPraiseHead(userWork);
			break;
		case R.id.pic_detail_head_img:
			String id = userWork.data.appuser.id+"";
			if(mUserId.equals(id)){
				
			}else{
				intent = new Intent(PicDetailActivity.this, OthersActivity.class);
				bundle = new Bundle();
				bundle.putInt("userid",userWork.data.appuser.id);
				intent.putExtra("key", bundle);
				startActivity(intent);
				}
			break;
		case R.id.pic_toReport:
			picToReportPopupWindow = PhotoUtils.getPicToReportPopupWindow(this,
					this, pic_rl);
			AnimationUtils.showAlpha(pic_mViewMask);
			this.toReportPopupWindow.dismiss();
			break;
		case R.id.pic_cancel:
			WindowManager.LayoutParams lp1 = getWindow().getAttributes();
			lp1.alpha = 1.0f;
			getWindow().setAttributes(lp1);
			this.toReportPopupWindow.dismiss();
			break;
		case R.id.toReport1:
			str = "涉嫌抄袭";
			toReport(str);
			this.picToReportPopupWindow.dismiss();
			WindowManager.LayoutParams lp2 = getWindow().getAttributes();
			lp2.alpha = 1.0f;
			getWindow().setAttributes(lp2);
			break;
		case R.id.toReport2:
			str = "图片低俗";
			toReport(str);
			this.picToReportPopupWindow.dismiss();
			WindowManager.LayoutParams lp3 = getWindow().getAttributes();
			lp3.alpha = 1.0f;
			getWindow().setAttributes(lp3);
			break;
		case R.id.toReport3:
			str = "主题不符";
			toReport(str);
			this.picToReportPopupWindow.dismiss();
			WindowManager.LayoutParams lp4 = getWindow().getAttributes();
			lp4.alpha = 1.0f;
			getWindow().setAttributes(lp4);
			break;
		case R.id.toReport4:
			str = "其他";
			toReport(str);
			this.picToReportPopupWindow.dismiss();
			WindowManager.LayoutParams lp5 = getWindow().getAttributes();
			lp5.alpha = 1.0f;
			getWindow().setAttributes(lp5);
			break;
		case R.id.toReport_cancel:
			this.picToReportPopupWindow.dismiss();
			WindowManager.LayoutParams lp6 = getWindow().getAttributes();
			lp6.alpha = 1.0f;
			getWindow().setAttributes(lp6);
			break;
		}
	}

	private void toReport(String str) {
		try {
			final String url = Constants.Url.REPORT;
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("reportUserTo", userWork.data.appuser.id);
			params.put("reportUserFrom",
					SpUtils.getInstance(this).getString(Constants.USER_ID));
			params.put("reportDesc", str);
			params.put("reportWork", userWork.data.id);
			params.put("reportReward", userWork.data.reward.id);
			HttpRequestProxy.sendAsyncPost(url, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							LogUtils.i("请求失败");
						}

						@SuppressLint("ShowToast")
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							Toast.makeText(PicDetailActivity.this, "举报成功",
									Toast.LENGTH_SHORT).show();
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发表评论
	private void sendComment(String touserid, String parentId) {
		if (ValidateUtils.isValidate(commentEdit.getText().toString())) {
			try {
				final String url = Constants.Url.PIC_DETAIL_SEND_COMMENT;
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("id", 1);
				params.put("content", commentEdit.getText().toString());
				params.put("workid",
						getIntent().getBundleExtra("key").getInt("workid")); // 7971
				params.put("userid", loginId);
				params.put("touserid", touserid); // 被回复的人的id
				params.put("parentid", Integer.getInteger(parentId)); // 具体哪条评论的id
				parentId = null;
				touserid=null;
				/*
				 * 收起软键盘
				 */
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				Toast.makeText(PicDetailActivity.this, "发送中...",
						Toast.LENGTH_LONG).show();
				HttpRequestProxy.sendAsyncPost(url, params,
						new RequestCallBack<String>() {
							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								LogUtils.i("请求失败");
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								String data = arg0.result;
								UserWork userWork = parseData(data); // 解析数据
								if (userWork.code == 200) {
									commentEdit.setText("");
									isUpdate = true;
									loadPicDetialByNet(); // 更新页面
									Toast.makeText(PicDetailActivity.this,
											userWork.desc, Toast.LENGTH_SHORT)
											.show();
								} else {
									Toast.makeText(PicDetailActivity.this,
											userWork.desc, Toast.LENGTH_SHORT)
											.show();
								}
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(PicDetailActivity.this, "发送不能为空...",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void updateData(UserWork userWork) {
		commentCountTv.setText("共有" + userWork.data.commentCount + "条评论"); // 评论数
		// 该作品没有评论时，更新
		if (adapter == null) {
			commentsListView.setVisibility(View.VISIBLE);
			adapter = new PicCommentsAdapter(userWork.data.comments,
					PicDetailActivity.this);
			commentsListView.setAdapter(adapter);
		}
		// 作品已经有过评论，刷新数据
		else {
			adapter.setItemList(userWork.data.comments);
			adapter.notifyDataSetChanged();
		}

		ListViewForScrollUtil
				.setListViewHeightBasedOnChildren(commentsListView);
		// 添加布局监听器
		this.detailScroll.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						detailScroll.fullScroll(View.FOCUS_DOWN);
						detailScroll.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
	}

	private void addPraiseHead(UserWork userWork) {
		if (isPraise) {
			Toast.makeText(PicDetailActivity.this, "你已经点过赞了",
					Toast.LENGTH_SHORT).show();
		} else {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					UiUtils.dip2px(45), UiUtils.dip2px(45));
			params.setMargins(0, 0, 15, 0);
			ImageView praiseImg = new ImageView(getApplicationContext());
			BitmapUtil.display(praiseImg, loginHead);
			praisesHeadLayout.addView(praiseImg, params);
			praiseImg1.setImageResource(R.drawable.homepage_hongaixin2x);
			praiseImg2.setBackgroundResource(R.drawable.homepage_hongaixin2x);
			praiseTv.setTextColor(Color.RED); // 设置红色
			userWork.data.praiseCount++;
			praiseNumTv.setText(userWork.data.praiseCount + "");
			isPraise = true;
			Toast.makeText(PicDetailActivity.this, "点赞成功", Toast.LENGTH_SHORT)
					.show();
			// 点赞网络请求
			addPraiseByNet(userWork);
		}
	}

	private void addPraiseByNet(UserWork userWork) {
		final HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("id", 1);
		params.put("workid", getIntent().getBundleExtra("key").getInt("workid")); // 被点赞作品的id
		params.put("userid", loginId); // 登录用户的id
		final String url = Constants.Url.FRIEND_PRAISE;
		try {
			HttpRequestProxy.sendAsyncPost(url, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							LogUtils.i("请求失败");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							String result = arg0.result;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
