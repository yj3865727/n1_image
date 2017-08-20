package com.lequan.n1.activity;

import com.lequan.n1.activity.fragment.BaseFragment;
import com.lequan.n1.activity.fragment.FriendFragment;
import com.lequan.n1.activity.fragment.HomeFragment;
import com.lequan.n1.activity.fragment.MessageFragment;
import com.lequan.n1.activity.fragment.PersonalFragment;
import com.lequan.n1.manager.ConversactionListDbManager;
import com.lequan.n1.manager.UserInfoManger;
import com.lequan.n1.manager.ConversactionListDbManager.Friend;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.UserInfoProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends BaseActivity implements UserInfoProvider, OnReceiveMessageListener {

	public static final int HOME = 0;
	public static final int FRIEND = 1;
	public static final int MESSAGE = 2;
	public static final int PERSONAL = 3;

	@ViewInject(R.id.rg_main_tab)
	private RadioGroup rg_main_tab;

	@ViewInject(R.id.fl_main)
	private FrameLayout fl_main;

	private int currentPosition = -1;
	private long lastTime;

	private static SparseArray<BaseFragment> cache = new SparseArray<BaseFragment>();
	private SpUtils mSpUtils;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
	}

	@Override
	protected void initData() {
		// 初始化界面
		cache.put(HOME, new HomeFragment());
		cache.put(FRIEND, new FriendFragment());
		cache.put(MESSAGE, new MessageFragment());
		cache.put(PERSONAL, new PersonalFragment());

		mSpUtils = SpUtils.getInstance(this);
		String uId = mSpUtils.getString(Constants.USER_ID);
		// 如果缓存用用户的信息
		if (ValidateUtils.isValidate(uId)) {
			Constants.isLogin = true;
			Constants.defualt_token = mSpUtils.getString(Constants.TOKEN);
		}

		// 初始化为首页
		changeFragment(HOME);
	}

	@Override
	protected void onResume() {
		// 处理切换用户之后：数据重新加载
		if (Constants.needReload) {
			for (int i = 0; i < cache.size(); i++) {
				BaseFragment baseFragment = cache.get(i);
				if (baseFragment.isAdded()) {
					if (baseFragment instanceof MessageFragment) {
						((MessageFragment) baseFragment).reloadData();
					}
					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction tx = fragmentManager.beginTransaction();
					tx.remove(baseFragment);
					tx.commit();
				}
			}
			cache.put(HOME, new HomeFragment());
			cache.put(FRIEND, new FriendFragment());
			cache.put(MESSAGE, new MessageFragment());
			cache.put(PERSONAL, new PersonalFragment());
			Constants.needReload = false;
			// 标记为初始进入
			this.currentPosition = -1;
			// 重新设置为首页
			changeFragment(HOME);
		}
		// 处理链接和切换用户重新连接
		if (Constants.needConnected && ValidateUtils.isValidate(Constants.defualt_token)) {
			if (UiUtils.getPackageName().equals(UiUtils.getCurProcessName())) {
				RongIM.connect(Constants.defualt_token, new RongIMClient.ConnectCallback() {

					@Override
					public void onTokenIncorrect() {
					}

					@Override
					public void onSuccess(String userid) {
						LogUtils.i("链接融云服务器：当前用户id为：" + userid);
						Constants.needConnected = false;
						// 链接成功之后上传用户数据--->设置用户信息提供者
						RongIM.setUserInfoProvider(MainActivity.this, true);
						RongIM.setOnReceiveMessageListener(MainActivity.this);
					}

					@Override
					public void onError(RongIMClient.ErrorCode errorCode) {
						LogUtils.i("onError：" + errorCode);
					}
				});
			}
		}
		if (!Constants.isLogin) {
			changeFragment(HOME);
		}
		super.onResume();
	}

	@Override
	protected void initEvent() {
		this.rg_main_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_main_home:
					changeFragment(HOME);
					break;
				case R.id.rb_main_friend:
					changeFragment(FRIEND);
					break;
				case R.id.rb_main_xiaoxi:
					changeFragment(MESSAGE);
					break;
				case R.id.rb_main_geren:
					changeFragment(PERSONAL);
					break;
				default:
					break;
				}
			}
		});
	}

	public void changeFragment(int position) {
		BaseFragment from = cache.get(currentPosition);
		// 第一次
		if (from == null) {
			from = cache.get(HOME);
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction tx = manager.beginTransaction();
			tx.add(R.id.fl_main, from);
			tx.commit();
		} else {// 切换
			// 如果点击的不是首页，并且没有登录
			if (position != HOME && !Constants.isLogin) {
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra("index", position);
				startActivity(intent);
				overridePendingTransition(0, android.R.anim.fade_in);
				return;
			}
			BaseFragment to = cache.get(position);
			// 如果是同一界面
			if (from != to) {
				FragmentManager manager = getSupportFragmentManager();
				FragmentTransaction tx = manager.beginTransaction();
				// 如果没有添加
				if (!to.isAdded()) {
					tx.hide(from).add(R.id.fl_main, to);
				} else {
					tx.hide(from).show(to);
				}
				// 可以在activity保存状态之后提交
				tx.commitAllowingStateLoss();
			}
		}
		// 更新当前的位置
		currentPosition = position;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		LogUtils.i("MainActivity--->onNewIntent");
		if (intent != null) {
			int changge = intent.getIntExtra("index", HOME);
			LogUtils.i("位置:" + changge);
			switch (changge) {
			case HOME:
				this.rg_main_tab.check(R.id.rb_main_home);
				break;
			case FRIEND:
				this.rg_main_tab.check(R.id.rb_main_friend);
				break;
			case MESSAGE:
				this.rg_main_tab.check(R.id.rb_main_xiaoxi);
				break;
			case PERSONAL:
				this.rg_main_tab.check(R.id.rb_main_geren);
				break;
			default:
				break;
			}
			changeFragment(changge);
		}
		super.onNewIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTimeMillis = System.currentTimeMillis();
			if (currentTimeMillis - lastTime > 2000) {
				Toast.makeText(this, "再按一次退出悬赏相册", Toast.LENGTH_SHORT).show();
				lastTime = currentTimeMillis;
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public UserInfo getUserInfo(String uId) {
		Friend userInfo = ConversactionListDbManager.getInstance().findUserInfo(uId);
		if(userInfo!=null){
			return new UserInfo(userInfo.getTargetId()+"", userInfo.getName(), Uri.parse(userInfo.getHeadPic()));
		}
		return null;
	}
	
	@Override
	public boolean onReceived(Message message, int arg1) {
		String senderUserId = message.getSenderUserId();
		String targetId = message.getTargetId();
		LogUtils.i("接受到信息：senderId"+senderUserId+"发给："+targetId);
		UserInfoManger.getUserInfoById(senderUserId, targetId);
		return false;
	}

}
