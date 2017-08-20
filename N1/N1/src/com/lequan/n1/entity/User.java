package com.lequan.n1.entity;

public class User extends BaseEntity {

	public Data data;
	
	public class Data{
		public int usercount;
		public int attentionCount;
		public int uwcount;
		
		public AppUser appUserInfo;
	}
}
