package com.lequan.n1.view;

import com.lequan.n1.activity.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InputHintView extends RelativeLayout implements TextWatcher {

	private String hint;
	private String hintAfter;
	private String hintBefore;
	private Drawable drawableLeft;
	private int inputType;

	private EditText mEt_inputhint_value;
	private TextView mTv_inputhint_hint;

	public InputHintView(Context context) {
		this(context, null);
	}

	public InputHintView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public InputHintView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View.inflate(context, R.layout.rl_input_hint, this);
		mEt_inputhint_value = (EditText) findViewById(R.id.et_inputhint_value);
		mTv_inputhint_hint = (TextView) findViewById(R.id.tv_inputhint_hint);

		// 获取属性
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputHintView);
		hint = a.getString(R.styleable.InputHintView_hint);
		hintAfter = a.getString(R.styleable.InputHintView_hintAfter);
		hintBefore = a.getString(R.styleable.InputHintView_hintBefore);
		drawableLeft = a.getDrawable(R.styleable.InputHintView_drawableLeft);
		inputType=a.getInt(R.styleable.InputHintView_android_inputType, EditorInfo.TYPE_NULL);
		a.recycle();

		// 添加监听
		this.mEt_inputhint_value.addTextChangedListener(this);
	}

	@Override
	protected void onFinishInflate() {
		if (drawableLeft != null) {
			drawableLeft.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
		}
		this.mEt_inputhint_value.setCompoundDrawables(drawableLeft, null, null, null);
		this.mEt_inputhint_value.setHint(hint);
		this.mTv_inputhint_hint.setText(hintBefore);
		this.mEt_inputhint_value.setInputType(inputType);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.toString().length() > 0) {
			//比较值是否相等
			if(compare!=null){
				String thisValue = s.toString();
				String compareValue = compare.getTextValue();
				if(!thisValue.equals(compareValue)){
					this.mTv_inputhint_hint.setText(hintAfter);
				}else{
					this.mTv_inputhint_hint.setText("");
				}
			}else{
				this.mTv_inputhint_hint.setText(hintAfter);
			}
		} else {
			this.mTv_inputhint_hint.setText(hintBefore);
		}
	}

	/**
	 * 设置输入框提示信息
	 */
	public void setHint(String hint) {
		this.mEt_inputhint_value.setHint(hint);
	}

	/**
	 * 设置输入框数据变化后的提示
	 */
	public void setHintAfter(String hintAfter) {
		this.hintAfter = hintAfter;
	}

	/**
	 * 设置输入框数据变化前的提示
	 */
	public void setHintBefore(String hintBefore) {
		this.hintBefore = hintBefore;
		this.mTv_inputhint_hint.setText(hintBefore);
	}

	/**
	 * 设置输入框左边的图标
	 */
	public void setDrawableLeft(Drawable drawableLeft) {
		if (drawableLeft != null) {
			drawableLeft.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
			this.mEt_inputhint_value.setCompoundDrawables(drawableLeft, null, null, null);
		}
	}

	/**
	 * 返回输入框的值
	 */
	public String getTextValue() {
		return this.mEt_inputhint_value.getText().toString();
	}

	//比较对象
	private InputHintView compare;
	
	/**
	 * 设置比较的对象及不一致的提示信息
	 */
	public void setCompareAndHint(InputHintView compare,String hint){
		this.compare=compare;
		this.hintAfter=hint;
	}
	
}
