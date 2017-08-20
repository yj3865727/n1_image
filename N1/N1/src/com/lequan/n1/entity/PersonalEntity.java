package com.lequan.n1.entity;

public class PersonalEntity extends BaseEntity{
	public Data data;

	// 节点数据
	public class Data {
		public int usercount;
		public int attentionCount;
		public int uwcount;
		public AppUser appUserInfo;
	}
}
