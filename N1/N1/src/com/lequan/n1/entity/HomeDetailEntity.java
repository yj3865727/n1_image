package com.lequan.n1.entity;

import java.util.List;

public class HomeDetailEntity extends BaseEntity{

	public Data data;

	public class Data{

		public RewardInfo rewardInfo;
		public long currentDate;
		public UserworkInfo userworkInfo;

		public class RewardInfo {
			public int id;
			public String createdate;
			public String modifydate;
			public String rewardSubject;
			public String rewardAttachSubject;
			public int rewardUserType;
			public int rewardSn;
			public String rewardName;
			public int rewardStatus;
			public String rewardEndTime;
			public int rewardMoney;
			public String backgroundUrl;
			public String pic1Url;
			public String pic2Url;
			public String pic3Url;
			public String pic4Url;
			public String rewardSummary;
			public int contributePostNum;
			public int contributePersonNum;
			public ScoreRule scoreRule;
			public class ScoreRule {
				int id;
				String name;
			}
			public RewardDetail rewardDetail;
			public class RewardDetail {
				public int id;
				public String createdate;
				public String modifydate;
				public int money;
				public String startDate;
				public String endDate;
				public String businessSn;
				public RewardType rewardType;
				public class RewardType {
					int sn;
					String name;
				}
				public String description;
				public String name;
				public String pictureUrl;
				public String isOverTime;
				public String lottery;
				public String rewardId;
				public String rewardName;
				public String userWorkId;
				public String userWorkName;
			}
			public String userworks;
			public String countDown;
			public String imageW;
			public String imageH;
			public List<String> rewardPhotos;
			public int type;
		}

		public class UserworkInfo{
			public List<UserWorkInfoDetail> rows;
			public int total;

			public class UserWorkInfoDetail{
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
				public Reward reward;
				public AppUser appuser;

				public class Reward{
					public int id;
					public String createdate;
					public String modifydate;
					public String rewardSubject;
					public String rewardAttachSubject;
					public int rewardUserType;
					public String rewardSn;
					public String rewardName;
					public int rewardStatus;
					public String rewardEndTime;
					public int rewardMoney;
					public String backgroundUrl;
					public String pic1Url;
					public String pic2Url;
					public String pic3Url;
					public String pic4Url;
					public String rewardSummary;
					public int contributePostNum;
					public int contributePersonNum;
					public String scoreRule;
					public String rewardDetail;
					public String userworks;
					public String countDown;
					public String imageW;
					public String imageH;
					public String rewardPhotos;
					public int type;
				}

				
			}
		}
	}
}
