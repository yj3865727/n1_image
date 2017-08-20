package com.lequan.n1.entity;

import java.util.List;

public class CommentsEntity {
	public CommentData data;
	public String desc;
	public int code;

	public class CommentData {
		public List<CommentRows> rows;
		public int total;

		public class CommentRows {
			public int id;    
			/**
			 * 评论内容
			 */
			public String content;
			public String time;
			
			public int workid;

			public UserWork userwork;
			/**
			 *	评论作品id
			 */
			public int userid;

			public AppUser appuser;
			/**
			 * 被回复者id
			 */
			public int touserid;
			public ToappUser toappuser;

			public String isRead;
			public String toIsRead;
			/** 回复具体的哪一条*/
			public String parentid;
		}
	}
}
