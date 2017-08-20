package com.lequan.n1.activity.fragment;

import com.lequan.n1.activity.R;
import com.lequan.n1.manager.ConversactionListDbManager;
import com.lequan.n1.util.Constants;
import com.lequan.n1.util.LogUtils;
import com.lequan.n1.util.SpUtils;
import com.lequan.n1.util.UiUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.ConversationListBehaviorListener;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.ArraysDialogFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class PersonalMessageFragment extends BaseFragment implements ConversationListBehaviorListener {

	private String mUId;

	@Override
	protected View initView() {
		View view = View.inflate(mContext, R.layout.ll_fragment_message_presonal, null);
		ConversationListFragment fragment = new ConversationListFragment();
		Uri uri = Uri.parse("rong://" + UiUtils.getPackageName()).buildUpon().appendPath("conversationlist")
				.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") // 设置私聊会话非聚合显示
				.build();
		fragment.setUri(uri);

		FragmentTransaction transaction = mContext.getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.rong_content, fragment);
		transaction.commit();
		return view;
	}

	@Override
	protected void initData() {
		mUId = SpUtils.getInstance(mContext).getString(Constants.USER_ID);
	}

	@Override
	protected void initEvent() {
		RongIM.setConversationListBehaviorListener(this);
	}

	@Override
	public boolean onConversationClick(Context arg0, View arg1, UIConversation arg2) {
		LogUtils.i("PersonalMessageFragment-->onConversationClick");
		return false;
	}

	@Override
	public boolean onConversationLongClick(Context arg0, View arg1, UIConversation arg2) {
		buildMultiDialog(arg2);
		return true;
	}

	@Override
	public boolean onConversationPortraitClick(Context arg0, ConversationType arg1, String arg2) {
		LogUtils.i("PersonalMessageFragment-->onConversationPortraitClick");
		return false;
	}

	@Override
	public boolean onConversationPortraitLongClick(Context arg0, ConversationType arg1, String arg2) {
		LogUtils.i("PersonalMessageFragment-->onConversationPortraitLongClick");
		return false;
	}

	private void buildMultiDialog(final UIConversation uiConversation) {
		String items[] = new String[2];
		if (uiConversation.isTop())
			items[0] = RongContext.getInstance()
					.getString(io.rong.imkit.R.string.rc_conversation_list_dialog_cancel_top);
		else
			items[0] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_set_top);
		items[1] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_remove);
		ArraysDialogFragment.newInstance(uiConversation.getUIConversationTitle(), items).setArraysDialogItemListener(
				new io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener() {

					public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
						if (which == 0)
							RongIM.getInstance().getRongIMClient().setConversationToTop(
									uiConversation.getConversationType(), uiConversation.getConversationTargetId(),
									!uiConversation.isTop(), new io.rong.imlib.RongIMClient.ResultCallback() {

										public void onSuccess(Boolean aBoolean) {
											if (uiConversation.isTop())
												Toast.makeText(RongContext.getInstance(),
														getString(
																io.rong.imkit.R.string.rc_conversation_list_popup_cancel_top),
														Toast.LENGTH_SHORT).show();
											else
												Toast.makeText(RongContext.getInstance(),
														getString(
																io.rong.imkit.R.string.rc_conversation_list_dialog_set_top),
														Toast.LENGTH_SHORT).show();
										}

										public void onError(io.rong.imlib.RongIMClient.ErrorCode errorcode) {
										}

										public synchronized void onSuccess(Object x0) {
											onSuccess((Boolean) x0);
										}

									});
						else if (which == 1) {// 删除
							RongIM.getInstance().getRongIMClient().removeConversation(
									uiConversation.getConversationType(), uiConversation.getConversationTargetId());
							// TODO 删除数据库中的数据
							WhereBuilder builder = WhereBuilder.b()//
									.expr("targetId", "=", uiConversation.getConversationTargetId())//
									.and("currentUid", "=", mUId);
							ConversactionListDbManager.getInstance().delete(builder);
						}
					}
				}).show(getFragmentManager());
	}

}
