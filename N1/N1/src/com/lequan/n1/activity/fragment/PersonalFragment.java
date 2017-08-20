package com.lequan.n1.activity.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Config;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lequan.n1.activity.PersonalRewardActivity;
import com.lequan.n1.activity.PersonalSettingActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.adapter.GeRenMypagerAdapter;
import com.lequan.n1.entity.AppUser;
import com.lequan.n1.entity.FindByIDEntity;
import com.lequan.n1.entity.FriendEntity;
import com.lequan.n1.entity.FriendEntity.Data.Rows;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.BitmapUtil;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.view.ObservableScrollView;
import com.lequan.n1.view.RoundImageView;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class PersonalFragment extends BaseFragment implements OnClickListener,
		OnCheckedChangeListener, ScrollViewListener {
	@ViewInject(R.id.geren_imbtn_qianbao)
	private ImageView geren_imbtn_qianbao;
	@ViewInject(R.id.gr_home_pic)
	private ViewPager gr_home_pic;
	@ViewInject(R.id.geren_imbtn_shezhi)
	private ImageView geren_imbtn_shezhi;
	@ViewInject(R.id.geren_id)
	private TextView geren_id;
	@ViewInject(R.id.geren_rg_daohang)
	private LinearLayout geren_rg_daohang;
	@ViewInject(R.id.geren_rbt_fensi)
	private LinearLayout geren_rbt_fensi;
	@ViewInject(R.id.scrollView)
	private SetAlphaScrollView scrollView;
	@ViewInject(R.id.title)
	private LinearLayout title;
	@ViewInject(R.id.dot_1)
	private View dot_1;
	@ViewInject(R.id.dot_2)
	private View dot_2;
	@ViewInject(R.id.set_Mask)
	private ImageView set_Mask;
	@ViewInject(R.id.geren_imvi_tupian)
	private XCRoundRectImageView geren_imvi_tupian;
	private TextView lv;
	private TextView score;
	private TextView id;
	@ViewInject(R.id.xuanshang1)
	private TextView xuanshang1;
	@ViewInject(R.id.guanzhu1)
	private TextView guanzhu1;
	@ViewInject(R.id.fensi1)
	private TextView fensi1;
	@ViewInject(R.id.xuanshang2)
	private TextView xuanshang2;
	@ViewInject(R.id.guanzhu2)
	private TextView guanzhu2;
	@ViewInject(R.id.fensi2)
	private TextView fensi2;

	private String mUserId;
	private String mUserName;
	private String usercount;
	private String attentionCount;
	private String uwcount;
	private FindByIDEntity findByIdEntity;
	private List<Rows> list = new ArrayList<FriendEntity.Data.Rows>();
	private RecyclerView rv;
	private FragmentManager fm; // 管理fragment的方法
	private SetAlphaScrollView scrollView1 = null;
	private List<View> viewlist;
	private int titleHeight;
	private ArrayList<View> dots;
	private int oldPosition = 0;// 记录上一次点的位置
	private int currentItem; // 当前页面
	public String userIds;
	private FragmentTransaction transaction;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@SuppressWarnings("deprecation")
	@Override
	protected View initView() {

		View views = View.inflate(mContext, R.layout.gr_fragment_home, null);
		viewlist = new ArrayList<View>();
		View view1 = View.inflate(mContext, R.layout.personal_viewpager1, null);
		View view2 = View.inflate(mContext, R.layout.personal_viewpager2, null);
		id = (TextView) view2.findViewById(R.id.personal_vp_id);
		score = (TextView) view2.findViewById(R.id.personal_vp_score);
		lv = (TextView) view1.findViewById(R.id.personal_vp_lv);
		viewlist.add(view1);
		viewlist.add(view2);
		ViewUtils.inject(this, views);
		
		mUserId = SpUtils.getInstance(getContext())
				.getString(Constants.USER_ID);
		userIds = mUserId;
		usercount = SpUtils.getInstance(getContext()).getString(
				Constants.USERCOUNT);
		attentionCount = SpUtils.getInstance(getContext()).getString(
				Constants.ATTENTIONCOUNT);
		uwcount = SpUtils.getInstance(getContext())
				.getString(Constants.UWCOUNT);
		fm = getChildFragmentManager();
		geren_rg_daohang.setOnClickListener(this);
		changeFragment(new FragmentGeRen_GuanZhu(), false);

		// 通过view对象作为viewpager的数据源
		// 显示的点
		dots = new ArrayList<View>();
		dots.add(dot_1);
		dots.add(dot_2);
		// 创建PagerAdapter适配器
		GeRenMypagerAdapter adapter = new GeRenMypagerAdapter(viewlist);
		// viewpager加载适配器
		gr_home_pic.setAdapter(adapter);
		scrollView1 = scrollView;
		scrollView1.setScrollViewListener(this);
		dots.get(1).setBackgroundResource(R.drawable.dot_normal);
		gr_home_pic.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub

				dots.get(oldPosition).setBackgroundResource(
						R.drawable.dot_normal);
				dots.get(1 - oldPosition).setBackgroundResource(
						R.drawable.dot_focused);

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

		return views;

	}

	// 切换不同的fragment
	public void changeFragment(Fragment fragment, boolean isiton) {
		// 开启事务
		FragmentTransaction treansaction = fm.beginTransaction();
		treansaction.replace(R.id.geren_framelayout, fragment);
		Bundle bundle = new Bundle();
		bundle.putString("userid", userIds);
		xuanshang1.setTextColor(getResources().getColor(R.color.gray));
		xuanshang2.setTextColor(getResources().getColor(R.color.gray));
		fensi2.setTextColor(getResources().getColor(R.color.gray));
		fensi1.setTextColor(getResources().getColor(R.color.gray));
		guanzhu1.setTextColor(getResources().getColor(R.color.white));
		guanzhu2.setTextColor(getResources().getColor(R.color.white));
		fragment.setArguments(bundle);
		if (!isiton) {
			treansaction.addToBackStack(null);
		}
		treansaction.commit();
	}

	@Override
	@OnClick({ R.id.geren_imbtn_qianbao, R.id.geren_imbtn_shezhi,
			R.id.geren_rbt_fensi, R.id.geren_rbt_guanzhu,
			R.id.geren_rbt_xuanshang })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.geren_imbtn_qianbao:
			startActivityForResult(new Intent(getActivity(),
					PersonalRewardActivity.class), 0);
			break;
		case R.id.geren_imbtn_shezhi:
			startActivity(new Intent(getActivity(),
					PersonalSettingActivity.class));
			break;
		case R.id.geren_rbt_fensi:
			xuanshang1.setTextColor(getResources().getColor(R.color.gray));
			xuanshang2.setTextColor(getResources().getColor(R.color.gray));
			fensi2.setTextColor(getResources().getColor(R.color.white));
			fensi1.setTextColor(getResources().getColor(R.color.white));
			guanzhu1.setTextColor(getResources().getColor(R.color.gray));
			guanzhu2.setTextColor(getResources().getColor(R.color.gray));
			FragmentGeRen_FenSi fragment3 = new FragmentGeRen_FenSi();
			Bundle bundle3 = new Bundle();
			bundle3.putString("userid", userIds);
			fragment3.setArguments(bundle3);
			// 如果transaction commit（）过 那么我们要重新得到transaction
			transaction = fm.beginTransaction();
			transaction.replace(R.id.geren_framelayout, fragment3);
			transaction.commit();
			break;
		case R.id.geren_rbt_guanzhu:
			xuanshang1.setTextColor(getResources().getColor(R.color.gray));
			xuanshang2.setTextColor(getResources().getColor(R.color.gray));
			fensi2.setTextColor(getResources().getColor(R.color.gray));
			fensi1.setTextColor(getResources().getColor(R.color.gray));
			guanzhu1.setTextColor(getResources().getColor(R.color.white));
			guanzhu2.setTextColor(getResources().getColor(R.color.white));
			FragmentGeRen_GuanZhu fragment1 = new FragmentGeRen_GuanZhu();
			Bundle bundle1 = new Bundle();
			bundle1.putString("userid", userIds);
			fragment1.setArguments(bundle1);
			// 如果transaction commit（）过 那么我们要重新得到transaction
			transaction = fm.beginTransaction();
			transaction.replace(R.id.geren_framelayout, fragment1);
			transaction.commit();
			break;
		case R.id.geren_rbt_xuanshang:
			xuanshang1.setTextColor(getResources().getColor(R.color.white));
			xuanshang2.setTextColor(getResources().getColor(R.color.white));
			fensi2.setTextColor(getResources().getColor(R.color.gray));
			fensi1.setTextColor(getResources().getColor(R.color.gray));
			guanzhu1.setTextColor(getResources().getColor(R.color.gray));
			guanzhu2.setTextColor(getResources().getColor(R.color.gray));
			FragmentGeRen_XuanShang fragment2 = new FragmentGeRen_XuanShang();
			Bundle bundle2 = new Bundle();
			bundle2.putString("userid", userIds);
			fragment2.setArguments(bundle2);
			// 如果transaction commit（）过 那么我们要重新得到transaction
			transaction = fm.beginTransaction();
			transaction.replace(R.id.geren_framelayout, fragment2);
			transaction.commit();
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
	public void onScrollChanged(SetAlphaScrollView setAlphaScrollView, int x,
			int y, int oldx, int oldy) {
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

			// final String userId = Constants.USER_ID ;
			// LogUtils.d(userId+"222222222222222222");
			final String url = Constants.Url.QUERYUSER_BY_ID;
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("id", mUserId);
			HttpRequestProxy.sendAsyncPost(url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							LogUtils.i("请求失败");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO 缓存数据
							String result = arg0.result;
							FindByIDEntity findByIdEntity = parseData(result);
							LogUtils.e(result + "");
							handleData(findByIdEntity);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected FindByIDEntity parseData(String result) {
		Gson gson = new Gson();
		findByIdEntity = gson.fromJson(result, FindByIDEntity.class);
		LogUtils.e(findByIdEntity + "1111111111111");
		return findByIdEntity;
	}

	private void handleData(FindByIDEntity findByIdEntity) {
		mUserName = SpUtils.getInstance(getContext())
				.getString(Constants.LOGINSN);
		geren_id.setText(mUserName); // 加载昵称
		if (!TextUtils.isEmpty(SpUtils.getInstance(getContext())
				.getString(Constants.HEADIMG))) {
			// BitmapUtil
			// .display(geren_imvi_tupian, findByIdEntity.data.headphoto); //
			// 加载头像
			imageLoader.displayImage(SpUtils.getInstance(getContext())
					.getString(Constants.HEADIMG),
					geren_imvi_tupian, options);
		}
		id.setText(findByIdEntity.data.id + ""); // id
		score.setText(findByIdEntity.data.score + ""); // 经验值
		xuanshang1.setText(uwcount + ""); // 悬赏总数
		guanzhu1.setText(attentionCount + ""); // 关注总数
		fensi1.setText(usercount + ""); // 粉丝总数
		if (findByIdEntity.data.address == null
				|| findByIdEntity.data.address == "") {
			lv.setText("Lv " + findByIdEntity.data.gradeUser.grade + "");
		} else {
			lv.setText("Lv " + findByIdEntity.data.gradeUser.grade + "　"
					+ findByIdEntity.data.address);
		}
		// if(findByIdEntity.data.headphoto.equals("http://fashionshowimage.oss-cn-shenzhen.aliyuncs.com/appeaser_default_header/default_header.jpg")){
		// }else{
		// BitmapUtil.display(set_Mask, findByIdEntity.data.headphoto);//将头像设为背景
		// }
		// setPraiseHead(personalEntity);
	}
	@Override
	public void onResume() {
		geren_id.setText(SpUtils.getInstance(mContext).getString(Constants.LOGINSN) + ""); 
		imageLoader.displayImage(SpUtils.getInstance(getContext())
				.getString(Constants.HEADIMG),geren_imvi_tupian, options);
		super.onResume();
	}


}
