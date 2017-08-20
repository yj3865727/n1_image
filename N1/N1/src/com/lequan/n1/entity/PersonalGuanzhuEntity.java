package com.lequan.n1.entity;

import java.util.List;
public class PersonalGuanzhuEntity extends BaseEntity{
	public Data data;

	// 节点数据
	public class Data {
		public List<AppUser> rows;
		public int total;
	}
}