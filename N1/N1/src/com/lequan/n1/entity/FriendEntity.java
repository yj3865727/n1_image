package com.lequan.n1.entity;

import java.util.List;

public class FriendEntity extends BaseEntity {

	public Data data;

	// 节点数据
	public class Data {
		public List<Rows> rows;
		public int total;

		// 每个数据对象
		public class Rows { 

			public int id;
			public String name;
			public int commentCount;
			public int browseCount;
			public int praiseCount;

			public float score;
			public int picCount;
			public int rewardid;

			public int userid;
			public int isLottery;
			public String description;

			public String publishTime;
			public int commentMatchCount;
			public int browseMatchCount;
			public int praiseMatchCount;

			public float matchScore;
			public String workMainPic;
			public String workPhotos;

			public String comments;
			public String praises;
			public String browses;
			public String picUrls;
			public int imageH;
			public int imageW;

			public RewardEntity reward;  //引用Reward类
			public AppUser appuser;     //引用AppUser类	
		}

	}

}
