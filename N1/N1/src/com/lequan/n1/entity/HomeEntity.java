package com.lequan.n1.entity;

import java.util.List;

public class HomeEntity extends BaseEntity {

	public Data data;

	//节点数据
	public class Data {
		public Home_Data_Data whPage;
		public Home_Data_Data xsPage;
		public List<String> hotListData;

		//每个数据对象
		public class Home_Data_Data {
			public int total;
			public List<Home_Data_Data_Row> rows;

			//每行的数据
			public class Home_Data_Data_Row {
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
				public String userworks;
				public int countDown;
				public int imageW;
				public int imageH;
				public String rewardPhotos;
				public int type;

				public ScoreRule scoreRule;
				public RewardDetail rewardDetail;

				public class ScoreRule {
					public int id;
					public String name;
				}

				public class RewardDetail {
					public int id;
					public String createdate;
					public String modifydate;
					public int money;
					public String startDate;
					public String endDate;
					public String businessSn;
					public String description;
					public String name;
					public String pictureUrl;
					public boolean isOverTime;
					public String lottery;
					public String rewardId;
					public String rewardName;
					public int userWorkId;
					public String userWorkName;
					public RewardType rewardType;

					public class RewardType {
						public String sn;
						public String name;
					}

				}
			}
		}

	}

}
