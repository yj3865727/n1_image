package com.lequan.n1.entity;

import java.util.HashMap;
import java.util.List;


public class PersonalFansEntity extends BaseEntity{
	public Data data;
	// 节点数据
	public class Data {
		public HashMap<String, String> attentionEachother;
		public Page page;
		public class Page{
			public List<AppUser> rows;
		}
	}
}
