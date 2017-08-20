package com.lequan.n1.activity.fragment;

import com.lequan.n1.activity.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MessageFragment extends BaseFragment {

	private static final int MESSAGE_PERSONAL = 0;
	private static final int MESSAGE_NOTICE = 1;
	private int currentPosition = -1;

	@ViewInject(R.id.rg_message)
	private RadioGroup rg_message;

	@Override
	protected View initView() {
		View view = View.inflate(mContext, R.layout.ll_fragment_message, null);
		ViewUtils.inject(this, view);
		return view;
	}

	private static SparseArray<BaseFragment> cache = new SparseArray<BaseFragment>();

	@Override
	protected void initData() {
		cache.put(0, new PersonalMessageFragment());
		cache.put(1, new NoticeMessageFragment());

		// 首页
		changeFragment(MESSAGE_PERSONAL);
	}

	@Override
	protected void initEvent() {
		rg_message.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rbt_message_personal:
					changeFragment(MESSAGE_PERSONAL);
					break;
				case R.id.rbt_message_notice:
					changeFragment(MESSAGE_NOTICE);
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
			from = cache.get(MESSAGE_PERSONAL);
			//只能使用孩子的管理器
			FragmentManager manager = getChildFragmentManager();
			FragmentTransaction tx = manager.beginTransaction();
			tx.add(R.id.fl_message_content, from);
			tx.commit();
		} else {
			BaseFragment to = cache.get(position);
			// 如果是同一界面
			if (from != to) {
				FragmentManager manager = getChildFragmentManager();
				FragmentTransaction tx = manager.beginTransaction();
				// 如果没有添加
				if (!to.isAdded()) {
					tx.hide(from).add(R.id.fl_message_content, to);
				} else {
					tx.hide(from).show(to);
				}
				tx.commit();
			}
		}
		// 更新当前的位置
		currentPosition = position;
	}
	
	//用户处理用户切换之后-->重写加载数据
	public void reloadData(){
		for(int i=0;i<cache.size();i++){
			BaseFragment baseFragment = cache.get(i);
			if(baseFragment.isAdded()){
				FragmentManager fragmentManager = getChildFragmentManager();
				FragmentTransaction tx = fragmentManager.beginTransaction();
				tx.remove(baseFragment);
				tx.commit();
			}
			this.currentPosition=-1;
		}
	}

}
