package com.lequan.n1.activity.fragment;

import com.lequan.n1.util.LogUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	protected FragmentActivity mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext =getActivity();
		LogUtils.i("BaseFragment--->onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = initView();
		LogUtils.i("BaseFragment--->onCreateView");
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtils.i("BaseFragment--->onActivityCreated");
		initData();
		initEvent();
	}

	/**
	 * 初始化数据
	 */
	protected void initData() {
		
	}

	/**
	 * 设置事件
	 */
	protected void initEvent() {
		
	}

	/** 初始化界面
	 * @return
	 */
	protected abstract View initView() ;

}
