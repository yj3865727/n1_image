package com.lequan.n1.view;

import com.lequan.n1.activity.fragment.ScrollViewListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class SetAlphaScrollView extends ScrollView {  
	  
    private ScrollViewListener scrollViewListener = null;  
  
    public SetAlphaScrollView(Context context) {  
        super(context);  
    }  
  
    public SetAlphaScrollView(Context context, AttributeSet attrs,  
            int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public SetAlphaScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {  
        this.scrollViewListener = scrollViewListener;  
    }  
  
    @Override  
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {  
        super.onScrollChanged(x, y, oldx, oldy);  
        if (scrollViewListener != null) {  
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);  
        }  
    }  
}

