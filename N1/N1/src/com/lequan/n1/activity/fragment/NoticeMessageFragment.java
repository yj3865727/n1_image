package com.lequan.n1.activity.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.lequan.n1.activity.MessageNoticeDetailActivity;
import com.lequan.n1.activity.R;
import com.lequan.n1.protocol.HttpRequestProxy;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoticeMessageFragment extends BaseFragment implements OnClickListener {

	@ViewInject(R.id.rl_message_notice_dianzan)
	private RelativeLayout notice_dianzan;
	
	@ViewInject(R.id.tv_message_notice_dianzan)
	private TextView tv_message_notice_dianzan;
	
	@ViewInject(R.id.tv_message_notice_dianzan_count)
	private TextView dianzan_count;

	@ViewInject(R.id.rl_message_notice_guanzhu)
	private RelativeLayout notice_guanzhu;
	
	@ViewInject(R.id.tv_message_notice_guanzhu)
	private TextView tv_message_notice_guanzhu;
	
	@ViewInject(R.id.tv_message_notice_guanzhu_count)
	private TextView guanzhu_count;


	@ViewInject(R.id.rl_message_notice_pinglun)
	private RelativeLayout notice_pinglun;
	
	@ViewInject(R.id.tv_message_notice_pinglun)
	private TextView tv_message_notice_pinglun;
	
	@ViewInject(R.id.tv_message_notice_pinglun_count)
	private TextView pinglun_count;
	
	@ViewInject(R.id.ll_message_notice_container)
	private LinearLayout notice_container;

	@Override
	protected View initView() {
		View view = View.inflate(mContext, R.layout.ll_fragment_message_notice, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	protected void initEvent() {
		this.notice_dianzan.setOnClickListener(this);
		this.notice_guanzhu.setOnClickListener(this);
		this.notice_pinglun.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		try {
			String userId = SpUtils.getInstance(mContext).getString(Constants.USER_ID);
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("id", userId);
			HttpRequestProxy.sendAsyncPost(Constants.Url.LAST_NOTIFY_COUNT, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;
					LogUtils.i(result);
					try {
						JSONObject data=new JSONObject(result);
						int code = data.optInt("code");
						if(code==200){
							JSONObject numbsers = data.optJSONObject("data");
							int praiseCount = numbsers.optInt("praiseCount");
							int attentionCount=numbsers.optInt("attentionCount");
							int commentAndReplyCount=numbsers.optInt("commentAndReplyCount");
							dianzan_count.setText(praiseCount>0?praiseCount+"":"");
							guanzhu_count.setText(attentionCount>0?attentionCount+"":"");
							pinglun_count.setText(commentAndReplyCount>0?commentAndReplyCount+"":"");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_message_notice_dianzan:
			this.dianzan_count.setText("");
			Intent intent = new Intent(mContext, MessageNoticeDetailActivity.class);
			intent.putExtra("title", "点赞");
			intent.putExtra("type", MessageNoticeDetailActivity.TYPE_PRAISE);
			mContext.startActivity(intent);
			break;
		case R.id.rl_message_notice_guanzhu:
			this.guanzhu_count.setText("");
			intent = new Intent(mContext, MessageNoticeDetailActivity.class);
			intent.putExtra("title", "关注");
			intent.putExtra("type", MessageNoticeDetailActivity.TYPE_ATTENTION);
			mContext.startActivity(intent);
			break;
		case R.id.rl_message_notice_pinglun:
			this.pinglun_count.setText("");
			intent = new Intent(mContext, MessageNoticeDetailActivity.class);
			intent.putExtra("title", "评论");
			intent.putExtra("type", MessageNoticeDetailActivity.TYPE_COMMENT);
			mContext.startActivity(intent);
			break;
		default:
			break;
		}
	}

}
