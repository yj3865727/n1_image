package com.lequan.n1.util;

public class Constants {

	public static final long CACHE_TIME = 1000 * 60 * 60;

	// sp的名称
	public static final String N1_SP = "n1_sp";
	public static final String IS_SETTED = "IS_SETTED";
	public static final String TOKEN = "TOKEN";
	public static final String USER_ID = "USER_ID";
	public static final String IS_FIRST_HOME = "is_first_home";

	public static final String ALL_USERINFO = "all_userinfo";

	public static String USERCOUNT="usercount";
	public static String ATTENTIONCOUNT="attentionCount";
	public static String UWCOUNT="uwcount";
	public static String HEADIMG="img"; 
	public static String LOGINSN="loginsn";
	public static String PASSWORD = "password";
	// 用户信息
	public static boolean isLogin = false;
	public static boolean needConnected=true;
	public static boolean needReload=false;

	public static String defualt_token;

	public static final class Url {
		public static final String BASE_URL = "http://112.74.209.114:12562/fashion-portal";
		public static final String IMAGE_URL = "http://112.74.209.114:12561/cloud/uploadServlet";
		public static final String HOME_PAGE = BASE_URL + "/api/homePage/getInfo";
		public static final String USER_LOGIN = BASE_URL + "/api/appUser/login";
		public static final String USER_REGISTE = BASE_URL + "/api/appUser/regist";
		// 根据悬赏id查询其基本信息及首位作品列表
		public static final String FINDREWARDINFO = BASE_URL + "/api/reward/findRewardInfo";
		// 根据悬赏id分页查询首位作品列表
		public static final String FINDWITHSCOREBYPAGE = BASE_URL + "/api/reward/findWithScoreByPage";
		// 根据悬赏id分页查询时间作品列表
		public static final String FINDWITHTIMEBYPAGE = BASE_URL + "/api/reward/findWithTimeByPage";
		// 根据悬赏id查询我的作品列表
		public static final String FINDMYWORK = BASE_URL + "/api/reward/findMyWork";
		// 根据悬赏id分页查询获奖作品列表
		public static final String FINDWITHLOTTERYBYPAGE = BASE_URL + "/api/reward/findWithLotteryByPage";
		// 获取最新的点赞信息
		public static final String LAST_PRAISE = BASE_URL + "/api/news/getLastPraise";
		public static final String LAST_COMMENT = BASE_URL + "/api/news/getLastCommentAndReply";
		public static final String LAST_FANS = BASE_URL + "/api/news/getLastFans";
		public static final String LAST_NOTIFY_COUNT = BASE_URL + "/api/news/getLastNotifyCount";
		// 返回个人中心信息
		public static final String ImplementationNotes = BASE_URL + "/api/personalCenter/getPersonalInfo";
		// 返回粉丝列表
		public static final String FANS_USER = BASE_URL + "/api/personalCenter/getFansUser";
		// 分页返回个人作品信息
		public static final String USER_WORK = BASE_URL + "/api/personalCenter/getUserWork";
		// 分页返回关注的人信息
		public static final String ATTENTIONED_USER = BASE_URL + "/api/personalCenter/getAttentionedUser";
		// 获取推荐的好友
		public static final String RECOMMEND_FRIENDS = BASE_URL + "/api/appUser/recommendFriends";
		// 通过用户id查询用户信息
		public static final String QUERYUSER_BY_ID = BASE_URL + "/api/appUser/findById";
		// 通过用户名查询用户信息
		public static final String QUERYUSER_BY_name = BASE_URL+"/api/appUser/findByName";
		// 根据用户查出朋友的作品
		public static final String FRIEND_PIC = BASE_URL + "/api/userwork/findFriendsUserWork";
		// 对朋友作品点赞，也是对所有作品点赞
		public static final String FRIEND_PRAISE = BASE_URL + "/api/praise/save";
		// 通过WorkId查看作品详细信息
		public static final String PIC_DETAIL = BASE_URL + "/api/userwork/findUserWork";
		// 对图片进行评论
		public static final String PIC_DETAIL_SEND_COMMENT = BASE_URL + "/api/comment/save";
		// 查询用户的奖励,包含已经过期的
		public static final String GETMYREWARDDETAILS = BASE_URL + "/api/appUser/getMyRewardDetails";
		// 登录
		public static final String LOGIN = BASE_URL + "/api/appUser/login";
		// 发布作品
		public static final String USERWORK_PUBLISH = BASE_URL + "/api/userwork/publish";
		// 用户建议
		public static final String SUGGEST = BASE_URL + "/api/suggestion/suggest";		
		//修改个人信息
		public static final String UPDATE = BASE_URL+"/api/appUser/update";
		//关注用户
		public static final String ATTENTION_OTHER=BASE_URL+"/api/appUser/attention";
		//绑定第三方账号
		public static final String APPUSER_BIND = BASE_URL+"/api/appUser/bind"; 
		
		//用户当前所在城市排名信息
		public static final String USER_LOCALBY_CITY=BASE_URL+"/api/rank/localby";
		public static final String USER_NEARBY_CITY=BASE_URL+"/api/rank/nearby";
		//查询用户用户之间的关注关系
		public static final String USER_RELATION=BASE_URL+"/api/appUser/findRelationBetweenUsers";
		//取消关注用户
		public static final String UNATTENTION=BASE_URL+"/api/appUser/unAttention";
		//举报用户
		public static final String REPORT=BASE_URL+"/api/reportUserWork/saveReportInfo";
	}
}
