package com.lequan.n1.activity;

import java.util.Locale;

import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.UiUtils;
import com.lequan.n1.util.ValidateUtils;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

public class ConversationActivity extends BaseActivity {

	// 对方的id：及发送消息的id
	private String mTargetId;
	private String mTitle;

	// 当前会话的类型
	private Conversation.ConversationType mConversationType;

	private ActionBar mActionBar;

	@Override
	protected void initView() {
		LogUtils.i("ConversationActivity--->开始聊天");
		setContentView(R.layout.activity_conversation);
	}

	@Override
	protected void initActionBar() {
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		mTargetId = intent.getData().getQueryParameter("targetId");
		mConversationType = Conversation.ConversationType
				.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
		mTitle = intent.getData().getQueryParameter("title");
		if (ValidateUtils.isValidate(mTitle)) {
			mActionBar.setTitle(mTitle);
		}
		enterFragment(mConversationType, mTargetId);
	}

	private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {
		ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fl_personal_message_conversation);

		Uri uri = Uri.parse("rong://" + UiUtils.getPackageName()).buildUpon().appendPath("conversation")
				.appendPath(mConversationType.getName().toLowerCase()).appendQueryParameter("targetId", mTargetId)
				.build();

		fragment.setUri(uri);
	}

}
