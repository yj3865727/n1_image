package com.lequan.n1.entity;

import java.util.List;

/**
 * 个人-------奖励详情
 * 
 * @return 
 */
public class PersonRewardDetailEntity extends BaseEntity{

	public List<RewardDetail> data;

	public class RewardDetail{
		public int id;
		public String createdate;
		public String modifydate;
		public int money;
		public String startDate;
		public String endDate;
		public String description;
		public String name;
		public String pictureUrl;
		public int isOverTime;
		public int lottery;
		public int rewardId;
		public String rewardName;
		public int userWorkId;
		public String userWorkName;
		public RewardType  rewardType;
		
		public class RewardType{
			public int sn;
			public String name;
		}
	}
}
