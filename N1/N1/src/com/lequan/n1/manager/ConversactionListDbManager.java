package com.lequan.n1.manager;

import java.util.List;

import com.lequan.n1.entity.AppUser;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import android.content.Context;

public class ConversactionListDbManager {

	private final static String dbName = "conversation_list.db";

	private static ConversactionListDbManager mDbManager;

	private static DbUtils mDbUtils;

	private ConversactionListDbManager() {
	}

	public static void init(Context context){
		mDbUtils=DbUtils.create(context, dbName);
	}
	
	public static ConversactionListDbManager getInstance() {
		if (mDbManager == null) {
			synchronized (ConversactionListDbManager.class) {
				if (mDbManager == null) {
					mDbManager = new ConversactionListDbManager();
				}
			}
		}
		return mDbManager;
	}

	public void save(Friend friend) {
		try {
			List<Friend> inDb = mDbUtils.findAll(Selector.from(Friend.class).where("targetId", "=", friend.targetId));
			if(inDb==null||inDb.size()==0)
				mDbUtils.save(friend);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(WhereBuilder whereBuilder) {
		try {
			mDbUtils.delete(Friend.class, whereBuilder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateUserInfo(AppUser user){
		try {
			WhereBuilder buidler = WhereBuilder.b().expr("currentUid", "=", user.id).and("targetId", "=", user.id);
			Friend friend=new Friend(user.id, user.id, user.loginSn, user.headphoto);
			mDbUtils.update(friend, buidler, "headPic");
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	public List<Friend> findFriends(long id){
		List<Friend> list=null;
		try {
			list = mDbUtils.findAll(Selector.from(Friend.class).expr("currentUid", "=", id));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Friend findUserInfo(String uid){
		try {
			return mDbUtils.findFirst(Selector.from(Friend.class).expr("targetId", "=", uid));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Table
	public static final class Friend {
		@Id
		private int id;
		@Column
		private long currentUid;// 当前用户的id
		@Column
		private long targetId;// 聊天对象的id
		@Column
		private String name;// 聊天对象的名称
		@Column
		private String headPic;// 头像

		public Friend() {
		}
		
		public Friend(long currentUid, long targetId, String name, String headPic) {
			this.currentUid = currentUid;
			this.targetId = targetId;
			this.name = name;
			this.headPic = headPic;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public long getCurrentUid() {
			return currentUid;
		}

		public void setCurrentUid(long currentUid) {
			this.currentUid = currentUid;
		}

		public long getTargetId() {
			return targetId;
		}

		public void setTargetId(long targetId) {
			this.targetId = targetId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHeadPic() {
			return headPic;
		}

		public void setHeadPic(String headPic) {
			this.headPic = headPic;
		}

	}

}
