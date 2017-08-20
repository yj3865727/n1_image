package com.lequan.n1.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.lequan.n1.activity.fragment.FragmentGeRen_FenSi;
import com.lequan.n1.activity.fragment.FragmentGeRen_GuanZhu;
import com.lequan.n1.activity.fragment.FragmentGeRen_XuanShang;
import com.lequan.n1.activity.fragment.ScrollViewListener;
import com.lequan.n1.adapter.GeRenMypagerAdapter;
import com.lequan.n1.entity.AppUser;
import com.lequan.n1.entity.FindRelationBetweenUsersEntity;
import com.lequan.n1.entity.FriendEntity;
import com.lequan.n1.entity.FriendEntity.Data.Rows;
import com.lequan.n1.manager.ConversactionListDbManager;
import com.lequan.n1.manager.ConversactionListDbManager.Friend;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lequan.n1.view.SetAlphaScrollView;
import com.lequan.n1.view.XCRoundRectImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation.ConversationType;

public class OthersActivity extends BaseActivity
		implements OnClickListener, OnCheckedChangeListener, ScrollViewListener {
	@ViewInject(R.id.others_back)
	private ImageView others_back;

	@ViewInject(R.id.others_id)
	private TextView others_id;
	@ViewInject(R.id.others_rg_daohang)
	private LinearLayout others_rg_daohang;
	@ViewInject(R.id.others_rbt_fensi)
	private LinearLayout others_rbt_fensi;
	@ViewInject(R.id.others_scrollView)
	private SetAlphaScrollView scrollView;
	@ViewInject(R.id.others_title)
	private LinearLayout title;
	@ViewInject(R.id.others_dot_1)
	private View dot_1;
	@ViewInject(R.id.others_dot_2)
	private View dot_2;
	@ViewInject(R.id.others_imvi_tupian)
	private XCRoundRectImageView others_imvi_tupian;
	private TextView lv;
	private TextView score;
	private TextView id;
	private Button guanzhu_btn;
	@ViewInject(R.id.others_xuanshang1)
	private TextView others_xuanshang1;
	@ViewInject(R.id.others_xuanshang2)
	private TextView others_xuanshang2;
	@ViewInject(R.id.others_guanzhu2)
	private TextView others_guanzhu2;
	@ViewInject(R.id.others_fensi2)
	private TextView others_fensi2;
	@ViewInject(R.id.others_guanzhu1)
	private TextView others_guanzhu1;
	@ViewInject(R.id.others_fensi1)
	private TextView others_fensi1;
	@ViewInject(R.id.others_Mask)
	private ImageView others_Mask;
	private ViewPager others_home_pic;

	private Intent intent;
	private FindRelationBetweenUsersEntity findRelationBetweenUsersEntity;
	private List<Rows> list = new ArrayList<FriendEntity.Data.Rows>();
	private RecyclerView rv;
	private FragmentManager fm; // 管理fragment的方法
	private SetAlphaScrollView scrollView1 = null;
	private List<View> viewlist;
	private ArrayList<View> dots;
	private int oldPosition = 0;// 记录上一次点的位置
	private int currentItem; // 当前页面
	public String userIds;
	private FragmentTransaction transaction;
	private Bundle bundle;
	private int i;// 记录关注状态
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@SuppressWarnings("deprecation")
	@Override
	protected void initView() {
		intent = getIntent();
		bundle = intent.getBundleExtra("key");
		userIds = bundle.getInt("userid") + "";
		View views = View.inflate(getBaseContext(), R.layout.others_home, null);
		viewlist = new ArrayList<View>();

		View view1 = View.inflate(getBaseContext(), R.layout.others_viewpager1, null);
		View view2 = View.inflate(getBaseContext(), R.layout.others_viewpager2, null);
		id = (TextView) view2.findViewById(R.id.others_vp_id);
		score = (TextView) view2.findViewById(R.id.others_vp_score);
		lv = (TextView) view1.findViewById(R.id.others_vp_lv);
		guanzhu_btn = (Button) view1.findViewById(R.id.btn_other_person_add_notice);
		view1.findViewById(R.id.btn_other_person_add_notice).setOnClickListener(this);
		view1.findViewById(R.id.ibt_other_person_talk).setOnClickListener(this);
		others_home_pic = (ViewPager) views.findViewById(R.id.others_home_pic);

		viewlist.add(view1);
		viewlist.add(view2);
		ViewUtils.inject(this, views);

		fm = getSupportFragmentManager();
		others_rg_daohang.setOnClickListener(this);
		changeFragment(new FragmentGeRen_GuanZhu(), false);

		// 通过view对象作为viewpager的数据源
		// 显示的点
		dots = new ArrayList<View>();
		dots.add(dot_1);
		dots.add(dot_2);
		// 创建PagerAdapter适配器
		GeRenMypagerAdapter adapter = new GeRenMypagerAdapter(viewlist);
		// viewpager加载适配器
		others_home_pic.setAdapter(adapter);
		scrollView1 = scrollView;
		scrollView1.setScrollViewListener(this);

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				// // 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.logo_placeholder2x)
				// // 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.logo_placeholder2x)
				// // 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.logo_placeholder2x).cacheInMemory(true)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.ARGB_8888)// 设置图片的解码类型
				.considerExifParams(true).resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();

		dots.get(1).setBackgroundResource(R.drawable.dot_normal);
		others_home_pic.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub

				dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
				dots.get(1 - oldPosition).setBackgroundResource(R.drawable.dot_focused);

				oldPosition = position;
				currentItem = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		setContentView(views);
	}

	// 切换不同的fragment
	public void changeFragment(Fragment fragment, boolean isiton) {
		// 开启事务
		transaction = fm.beginTransaction();
		transaction.replace(R.id.others_framelayout, fragment);
		Bundle bundle = new Bundle();
		bundle = intent.getBundleExtra("key");
		others_xuanshang1.setTextColor(getResources().getColor(R.color.gray));
		others_xuanshang2.setTextColor(getResources().getColor(R.color.gray));
		others_fensi2.setTextColor(getResources().getColor(R.color.gray));
		others_fensi1.setTextColor(getResources().getColor(R.color.gray));
		others_guanzhu1.setTextColor(getResources().getColor(R.color.white));
		others_guanzhu2.setTextColor(getResources().getColor(R.color.white));
		bundle.putString("userid", bundle.getInt("userid") + "");
		fragment.setArguments(bundle);
		if (!isiton) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	@OnClick({ R.id.others_back, R.id.others_rbt_fensi, R.id.others_rbt_guanzhu, R.id.others_rbt_xuanshang })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.others_rbt_fensi:
			others_xuanshang1.setTextColor(getResources().getColor(R.color.gray));
			others_xuanshang2.setTextColor(getResources().getColor(R.color.gray));
			others_fensi2.setTextColor(getResources().getColor(R.color.white));
			others_fensi1.setTextColor(getResources().getColor(R.color.white));
			others_guanzhu1.setTextColor(getResources().getColor(R.color.gray));
			others_guanzhu2.setTextColor(getResources().getColor(R.color.gray));
			FragmentGeRen_FenSi fragment3 = new FragmentGeRen_FenSi();
			fragment3.setArguments(intent.getBundleExtra("key"));
			// 如果transaction commit（）过 那么我们要重新得到transaction
			transaction = fm.beginTransaction();
			transaction.replace(R.id.others_framelayout, fragment3);
			transaction.commit();
			break;
		case R.id.others_rbt_guanzhu:
			others_xuanshang1.setTextColor(getResources().getColor(R.color.gray));
			others_xuanshang2.setTextColor(getResources().getColor(R.color.gray));
			others_fensi2.setTextColor(getResources().getColor(R.color.gray));
			others_fensi1.setTextColor(getResources().getColor(R.color.gray));
			others_guanzhu1.setTextColor(getResources().getColor(R.color.white));
			others_guanzhu2.setTextColor(getResources().getColor(R.color.white));
			FragmentGeRen_GuanZhu fragment1 = new FragmentGeRen_GuanZhu();
			fragment1.setArguments(intent.getBundleExtra("key"));
			// 如果transaction commit（）过 那么我们要重新得到transaction
			transaction = fm.beginTransaction();
			transaction.replace(R.id.others_framelayout, fragment1);
			transaction.commit();

			break;
		case R.id.others_rbt_xuanshang:

			others_xuanshang1.setTextColor(getResources().getColor(R.color.white));
			others_xuanshang2.setTextColor(getResources().getColor(R.color.white));
			others_fensi2.setTextColor(getResources().getColor(R.color.gray));
			others_fensi1.setTextColor(getResources().getColor(R.color.gray));
			others_guanzhu1.setTextColor(getResources().getColor(R.color.gray));
			others_guanzhu2.setTextColor(getResources().getColor(R.color.gray));
			FragmentGeRen_XuanShang fragment2 = new FragmentGeRen_XuanShang();
			fragment2.setArguments(intent.getBundleExtra("key"));
			// 如果transaction commit（）过 那么我们要重新得到transaction
			transaction = fm.beginTransaction();
			transaction.replace(R.id.others_framelayout, fragment2);
			transaction.commit();
			break;
		case R.id.btn_other_person_add_notice:
			if (i > 0) {
				unAttention();
			} else {
				hanleAttention();
			}
			break;
		case R.id.ibt_other_person_talk:
			startConversation();
			break;
		case R.id.others_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollChanged(SetAlphaScrollView setAlphaScrollView, int x, int y, int oldx, int oldy) {
		if (y < 450) {
			title.setAlpha((y / 450f));
		}
		if (y >= 450) {
			title.setAlpha(1.0f);
		}

	}

	@Override
	protected void initData() {
		try {
			Map<String, Long> param = new HashMap<String, Long>();
			param.put("touser", Long.parseLong(userIds));
			long fromuser = Long.parseLong(SpUtils.getInstance(this).getString(Constants.USER_ID));
			param.put("fromuser", fromuser);
			HttpRequestProxy.sendAsyncPost(Constants.Url.USER_RELATION, param, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.i("请求失败");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO 缓存数据
					String result = arg0.result;
					FindRelationBetweenUsersEntity findRelationBetweenUsersEntity = parseData(result);
					LogUtils.e(result + "");
					handleData(findRelationBetweenUsersEntity);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected FindRelationBetweenUsersEntity parseData(String result) {
		Gson gson = new Gson();
		findRelationBetweenUsersEntity = gson.fromJson(result, FindRelationBetweenUsersEntity.class);
		return findRelationBetweenUsersEntity;
	}

	private void handleData(FindRelationBetweenUsersEntity findRelationBetweenUsersEntity) {
		if (findRelationBetweenUsersEntity.data.isAttention > 0) {
			guanzhu_btn.setBackgroundResource(R.drawable.geren_btn_shape);
			guanzhu_btn.setText("已关注");// 改变button关注的状态
			i = 1;
		} else {
			i = 0;
		}
		others_id.setText(findRelationBetweenUsersEntity.data.touser.loginSn + ""); // 加载昵称
		imageLoader.displayImage(findRelationBetweenUsersEntity.data.touser.headphoto, others_imvi_tupian, options);
		id.setText(findRelationBetweenUsersEntity.data.touser.id + ""); // id
		score.setText(findRelationBetweenUsersEntity.data.touser.score + ""); // 经验值
		others_xuanshang1.setText(findRelationBetweenUsersEntity.data.uwcount + ""); // 悬赏总数
		others_guanzhu1.setText(findRelationBetweenUsersEntity.data.attentionCount + ""); // 关注总数
		others_fensi1.setText(findRelationBetweenUsersEntity.data.usercount + ""); // 粉丝总数
		if (findRelationBetweenUsersEntity.data.touser.address == null
				|| findRelationBetweenUsersEntity.data.touser.address == "") {
			lv.setText("Lv " + findRelationBetweenUsersEntity.data.touser.gradeUser.grade + "");
		} else {
			lv.setText("Lv " + findRelationBetweenUsersEntity.data.touser.gradeUser.grade + "　"
					+ findRelationBetweenUsersEntity.data.touser.address);
		}
		LogUtils.e(findRelationBetweenUsersEntity.data.touser.headphoto + "10010");
		if (findRelationBetweenUsersEntity.data.touser.headphoto.equals(
				"http://fashionshowimage.oss-cn-shenzhen.aliyuncs.com/appeaser_default_header/default_header.jpg")) {
		} else {
			BitmapUtil.display(others_Mask, findRelationBetweenUsersEntity.data.touser.headphoto);// 将头像设为背景
		}

		// setPraiseHead(personalEntity);
		// 关注用户的id
		this.toUser = Long.parseLong(findRelationBetweenUsersEntity.data.touser.id + "");
		this.loginSn = findRelationBetweenUsersEntity.data.touser.loginSn;
		this.headPic = findRelationBetweenUsersEntity.data.touser.headphoto;
	}

	private long toUser;
	private String loginSn;
	private String headPic;

	private ProgressDialog mDialog;

	private void hanleAttention() {
		if (!ValidateUtils.isValidate(toUser + "")) {
			return;
		}
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("touser", toUser);
		long fromuser = Long.parseLong(SpUtils.getInstance(this).getString(Constants.USER_ID));
		params.put("fromuser", fromuser);
		if (mDialog == null) {
			mDialog = new ProgressDialog(this);
		}
		guanzhu_btn.setBackgroundResource(R.drawable.geren_btn_shape);
		guanzhu_btn.setText("关注");
		UiUtils.showSimpleProcessDialog(mDialog, "关注用户，请稍后....");
		try {
			HttpRequestProxy.sendAsyncPost(Constants.Url.ATTENTION_OTHER, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					UiUtils.closeProcessDialog(mDialog);
					Toast.makeText(OthersActivity.this, "关注失败,请稍好再试!", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					UiUtils.closeProcessDialog(mDialog);
					// 修改用户关注的本地数量
					SpUtils spUtils = SpUtils.getInstance(OthersActivity.this);
					String userInfo = spUtils.getString(Constants.ALL_USERINFO);
					AppUser user = new Gson().fromJson(userInfo, AppUser.class);
					user.attentionCount++;
					guanzhu_btn.setText("已关注");
					i = 1;
					spUtils.setString(Constants.ALL_USERINFO, new Gson().toJson(user));
					spUtils.setString(Constants.ATTENTIONCOUNT,
							(Integer.parseInt(spUtils.getString(Constants.ATTENTIONCOUNT)) + 1) + "");
					try {
						JSONObject data = new JSONObject(arg0.result);
						Toast.makeText(OthersActivity.this, data.optString("desc"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unAttention() {
		if (!ValidateUtils.isValidate(toUser + "")) {
			return;
		}
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("touser", toUser);
		long fromuser = Long.parseLong(SpUtils.getInstance(this).getString(Constants.USER_ID));
		params.put("fromuser", fromuser);
		if (mDialog == null) {
			mDialog = new ProgressDialog(this);
		}
		guanzhu_btn.setBackgroundResource(R.drawable.geren_shape);
		guanzhu_btn.setText("关注");
		UiUtils.showSimpleProcessDialog(mDialog, "取消关注，请稍后....");
		try {
			HttpRequestProxy.sendAsyncPost(Constants.Url.UNATTENTION, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					UiUtils.closeProcessDialog(mDialog);
					Toast.makeText(OthersActivity.this, "取消关注失败,请稍好再试!", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					UiUtils.closeProcessDialog(mDialog);
					// 修改用户关注的本地数量
					SpUtils spUtils = SpUtils.getInstance(OthersActivity.this);
					String userInfo = spUtils.getString(Constants.ALL_USERINFO);
					AppUser user = new Gson().fromJson(userInfo, AppUser.class);
					--user.attentionCount;
					guanzhu_btn.setBackgroundResource(R.drawable.geren_shape);
					guanzhu_btn.setText("+关注");
					i = 0;
					spUtils.setString(Constants.ALL_USERINFO, new Gson().toJson(user));
					spUtils.setString(Constants.ATTENTIONCOUNT,
							(Integer.parseInt(spUtils.getString(Constants.ATTENTIONCOUNT)) - 1) + "");
					try {
						JSONObject data = new JSONObject(arg0.result);
						Toast.makeText(OthersActivity.this, data.optString("desc"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 开始聊天
	private void startConversation() {
		if (ValidateUtils.isValidate(loginSn) && ValidateUtils.isValidate(toUser + "")) {
			// TODO 考虑什么时候链接融云服务器
			if (RongIM.getInstance() != null) {
				//保存到数据库
				long currentId = Long.parseLong(SpUtils.getInstance(this).getString(Constants.USER_ID));
				Friend friend = new Friend(currentId, toUser, loginSn, headPic);
				ConversactionListDbManager.getInstance().save(friend);
				
				RongIM.getInstance().startConversation(this, ConversationType.PRIVATE, toUser + "", loginSn);
			}
		}
	}

}
