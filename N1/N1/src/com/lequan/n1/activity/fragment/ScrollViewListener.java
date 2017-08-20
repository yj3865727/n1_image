package com.lequan.n1.activity.fragment;

import com.lequan.n1.view.ObservableScrollView;
import com.lequan.n1.view.SetAlphaScrollView;

public interface ScrollViewListener {
	void onScrollChanged(SetAlphaScrollView setAlphaScrollView, int x, int y, int oldx, int oldy); 
}
