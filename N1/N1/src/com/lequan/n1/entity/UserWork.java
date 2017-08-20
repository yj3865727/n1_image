package com.lequan.n1.entity;

import java.util.List;

import com.lequan.n1.entity.CommentsEntity.CommentData.CommentRows;

/*
 * 作品信息，包括图片所有信息，点赞列表，所有评论
 */
public class UserWork {
	public int code;
	public String desc;
	public UserWorkData data;

	public class UserWorkData {
		public int id;
		public String name;
		public int commentCount; // 评论数
		public int browseCount; // 浏览数
		public int praiseCount; // 点赞数
		public float score; // 分数
		public int picCount; // 图片数量
		public int rewardid;
		public RewardEntity reward; // 所属奖赏主题
		public int userid; // 所属用户id
		public AppUser appuser; // 所属用户详细信息
		public String isLottery;
		public String description; // 对作品描述
		public String publishTime; // 上传时间
		public int commentMatchCount;
		public int browseMatchCount;
		public int praiseMatchCount;
		public float matchScore;
		public String workMainPic; // 主图片的url
		public List<WorkPhotosEntity> workPhotos; // 该作品的所有图片
		public List<CommentRows> comments; // 所有评论
		public List<PraisesEntity> praises; // 点赞列表
		public List browses; // 浏览纪录
		public String picUrls;
		public int imageH;
		public int imageW;
	}
}
