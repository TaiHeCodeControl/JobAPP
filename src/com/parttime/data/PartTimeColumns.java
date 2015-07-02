package com.parttime.data;

import com.parttime.constant.Constant;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * @author 灰色的寂寞
 * @date 2015-1-18
 * @time 9:34
 * @function 对数据操作用的Column，比如数据库中的列
 */
public class PartTimeColumns {
	/**
	 *用户登录信息的columns 
	 */
	public static final class UserInfoColumns implements BaseColumns{
		public static final String TABLE_NAME = "user_info";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);

		public static String USERID = "userid";// 用户id
		public static String USERNAME = "username";// 用户名
		public static String EMAIL = "email";// 邮箱
		public static String AVATAR = "avatar";// 头像
		public static String TYPE = "type";//  0是个人/1是企业
		public static String REMEMBER = "remember";// 0是未记住密码，1是记住密码
	}
	/**
	 *用户登录信息的columns 
	 */
	public static final class PersonalInfoColumns implements BaseColumns{
		public static final String TABLE_NAME = "personal_info";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String USERID = "userid";// 用户id
		public static String AVATAR = "avatar";// 头像
		public static String USERNAME = "username";// 用户名
		public static String NIKENAME = "nickname";// 昵称
		public static String GENDER= "gender";// 性别
		public static String EMAIL = "email";//邮件
		public static String JOBEXP= "jobexp";//地区
		public static String ADDRESS = "address";//详细地址
		public static String CALL = "call";//电话
		public static String CONFIRM= "confirm";//认证图片
	}
	/**
	 *热门工作推荐表的columns 
	 */
	public static final class HotWorkColumns implements BaseColumns{
		public static final String TABLE_NAME = "hot_work_list";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static String Hot_Id = "hot_id";//id
		public static String NAME = "name";// 职位名
		public static String CHARGE= "charge";//佣金
		public static String TYPE = "type";// 类型
		public static String NUM = "num";// 招聘人数
		public static String CREATE_TIME = "create_time";//  发布时间
		public static String COMPANY_NAME = "company_name";// 公司名称
		public static String COMPANY_ADD = "company_add";// 公司地址
		public static String COMPANY_LEVEL = "company_level";// 公司信誉度
		public static String USERID = "company_id";// 公司id
		public static String POSITION = "position";// 公司位置
		public static String LOCATION = "location";// 公司经纬度
		
	}
	/**
	 *工作表的columns 
	 */
	public static final class JobColumns implements BaseColumns{
		public static final String TABLE_NAME = "job_list";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static String Hot_Id = "hot_id";//id
		public static String NAME = "name";// 职位名
		public static String CHARGE= "charge";//佣金
		public static String TYPE = "type";// 类型
		public static String NUM = "num";// 招聘人数
		public static String CREATE_TIME = "create_time";//  发布时间
		public static String COMPANY_NAME = "company_name";// 公司名称
		public static String COMPANY_ADD = "company_add";// 公司地址
		public static String COMPANY_LEVEL = "company_level";// 公司信誉度
		public static String COMPANY_POSITION = "company_position";// 公司经纬度
		public static String COMPANY_ID = "company_id";// 公司id
		public static String LOCATION = "location";// 距离
		
	}
	
	/**
	 *职位详情表的columns 
	 */
	public static final class JobInfoColumns implements BaseColumns{
		public static final String TABLE_NAME = "job_info_list";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String Hot_Id = "hot_id";//id
		public static String Company_Id = "company_id";//企业id
		public static String NAME = "name";// 职位名
		public static String CHARGE= "charge";//佣金
		public static String TYPE = "type";// 类型
		public static String NUM = "num";// 招聘人数
		public static String CREATE_TIME = "create_time";//  发布时间
		public static String Present="present";//职位介绍
		public static String Require="require";//职位要求
		public static String Condition="condition";//职位申请条件
		public static String Thumb="thumb";//图片示例
		public static String StartTime="starttime";//开始时间
		public static String EndTime="endtime";//结束时间
		public static String Company_Status="company_status";//公司状态
		public static String Company_Code="company_code"; //公司营业执照编码
		public static String COMPANY_NAME = "company_name";// 公司名称
		public static String COMPANY_ADD = "company_add";// 公司地址
		public static String COMPANY_LEVEL = "company_level";// 公司信誉度
	}
	
	/**
	 *职位列表的columns 
	 */
	public static final class AllResumeColumns implements BaseColumns{
		public static final String TABLE_NAME = "all_resume_list";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static String RID = "rid";//id
		public static String RJOB= "rjob";//期望职位 
		public static String RADD = "radd";//工作地区 
		public static String RSTARTTIME = "rstart_time";//开始时间 
		public static String RENDTIME= "rend_time";//结束时间 
		public static String RTYPE = "rtype";//类型 
		public static String RJOBTYPE = "rjob_type";//工作类型
		public static String RNAME = "rname";//姓名 
		public static String RSEX = "rsex";//性别 
		public static String RHEIGHT = "rheight";//身高 
		public static String RCALL = "rcall";//手机 
		public static String REXPERIENCE = "rexperience";//兼职经历 
		public static String RUSERID = "user_id";//用户id 
	}
	/**
	 *职位简历详情的columns 
	 */
	public static final class ResumeInfoColumns implements BaseColumns{
		public static final String TABLE_NAME = "resume_info";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String RID = "rid";//id
		public static String RJOB= "rjob";//期望职位 
		public static String RADD = "radd";//工作地区 
		public static String RSTARTTIME = "rstart_time";//开始时间 
		public static String RENDTIME= "rend_time";//结束时间 
		public static String RTYPE = "rtype";//类型 
		public static String RNAME = "rname";//姓名 
		public static String RSEX = "rsex";//性别 
		public static String RHEIGHT = "rheight";//身高 
		public static String RCALL = "rcall";//手机 
		public static String REXPERIENCE = "rexperience";//兼职经历 
		public static String RUSERID = "user_id";//用户id 
	}
	
	/**
	 *求职者推荐的columns 
	 */
	public static final class HotSeekerColumns implements BaseColumns{
		public static final String TABLE_NAME = "hot_seeker";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String RID = "rid";//id
		public static String RJOB= "rjob";//期望职位 
		public static String RADD = "radd";//工作地区 
		public static String RSTARTTIME = "rstart_time";//开始时间 
		public static String RENDTIME= "rend_time";//结束时间 
		public static String RTYPE = "rtype";//类型 
		public static String RNAME = "rname";//姓名 
		public static String RSEX = "rsex";//性别 
		public static String RHEIGHT = "rheight";//身高 
		public static String RCALL = "rcall";//手机 
		public static String REXPERIENCE = "rexperience";//兼职经历 
		public static String RCREATE_TIME = "create_time";//创建时间
		public static String RUSERID = "user_id";//用户id 
		public static String POSITION = "position";//求职者所在的经纬度
		public static String LOCATION = "location";//距离
	}
	
	
	
	/**
	 *求职信息的columns 
	 */
	public static final class PublicResumeColumns implements BaseColumns{
		public static final String TABLE_NAME = "public_resume_info";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String mRheight="rheight";//身高
		public static String mRtype="rtype";//类别
		public static String mRname="rname";//姓名
		public static String mRsex="rsex";//性别
		public static String mRstart_time="rstart_time";//工作開始時間
		public static String mRexperience="rexperience";//经验
		public static String mRadd="radd";//地址
		public static String mRjob="rjob";//工作
		public static String mRid="rid";//简历id
		public static String mRuser_id="ruser_id";//用户id
		public static String mRcall="rcall";//电话
		public static String mRpublic="rpublic";//是否公开
		public static String mRrend_time="rend_time";//结束时间
		public static String mRcreate_time="rcreate_time";//创建时间
		public static String mRposition="rposition";//求职者所填的经纬度
		public static String Location="rlocation";//距离
	}
	/**
	 *聊天联系人的columns 
	 */
	public static final class CommuncationUsersColumns implements BaseColumns{
		public static final String TABLE_NAME = "communcation_user_info";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String USERID = "userid";//用户id
		public static String COMM_USERID= "comm_userid";//聊过天人的userid
		public static String COMM_HEAD_IMAGE="comm_headimage";//聊过天人的用户头像
	}
	
	/**
	 *获取评论列表的columns 
	 */
	public static final class CommentColumns implements BaseColumns{
		public static final String TABLE_NAME = "comments";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static String CREATE_TIME = "create_time";//创建时间
		public static String BUSINESS_ID = "business_id";//企业id
		public static String ID = "id";//id
		public static String PRESTIGE = "prestige";//信誉度
		public static String COMMENT= "comment";//评论
		public static String PERSONAL_ID="personal_id";//个人id
		public static String NAME="name";//用户名
	}
	
	
	
	/**
	 *所有企业的坐标columns 
	 */
	public static final class CompanyLatLngColumns implements BaseColumns{
		public static final String TABLE_NAME = "company_latlng";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String company_id="company_id";//企业id
		public static String company_lat="company_lat";//经度
		public static String company_lng="company_lng";//纬度
	}
	/**
	 *所有个人用户的坐标columns 
	 */
	public static final class PersonalLatLngColumns implements BaseColumns{
		public static final String TABLE_NAME = "personal_latlng";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static String personal_id="personal_id";//企业id
		public static String personal_lat="personal_lat";//经度
		public static String personal_lng="personal_lng";//纬度
	}
	/**
	 *用户被雇佣的职位列表columns
	 */
	public static final class HiredListColumns implements BaseColumns{
		public static final String TABLE_NAME = "personal_hired_list";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);

		public static String hired_id="id";//雇佣id
		public static String job_id="job_id";//职位id
		public static String r_id="r_id";//签到id
		public static String engage = "engage";//签到状态
		public static String create_time = "create_time";//创建时间
		public static String hire_name = "hire_name";//工作标题
	}
	/**
	 *获取求助列表columns
	 */
	public static final class AskListColumns implements BaseColumns{
		public static final String TABLE_NAME = "ask_list";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static final String DEFAULT_SORT_ORDER = "_id DESC";
		public static String askid="id";//求助id
		public static String userid="user_id";//用户id
		public static String name="name";//发布人
		public static String theme = "theme";//发布主题
		public static String time = "time";//发布时间
		public static String content = "content";//求助内容
		public static String call = "call";//电话
		public static String image = "image";//图片
	}
	/**
	 *获取公益列表columns
	 */
	public static final class VolunteerColumns implements BaseColumns{
		public static final String TABLE_NAME = "volunteer_list";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static final String DEFAULT_SORT_ORDER = "_id DESC";
		public static String volunteerid="id";
		public static String volunteertheme="theme";
		public static String volunteercontent="content";
		public static String volunteerthumb = "thumb";
		public static String volunteercreatetime = "create_time";
	}
	/**
	 *获取公益列表columns
	 */
	public static final class CompanyColumns implements BaseColumns{
		public static final String TABLE_NAME = "company_info";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static String CompanyId="id";
		public static String CompanyName="company_name";
		public static String CompanyProperty="property";
		public static String CompanyConfirmState = "confirm_state";
		public static String CompanyPoiNum = "poi";
		public static String CompanyDetailsPoi = "details_poi";
		public static String CompanyConfirm = "confirm_number";
		public static String CompanyEmail = "company_email";
		public static String CompanyCall = "company_call";
		public static String CompanyIdentity = "company_identity";
		public static String userid = "userid";
	}
	/**
	 *工作职位列表columns
	 */
	public static final class CompanyJobListColumns implements BaseColumns{
		public static final String TABLE_NAME = "company_job_listinfo";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		
		public static final String DEFAULT_SORT_ORDER = "_id DESC";
		public static String JobId="job_id";
		public static String JobName="job_name";
		public static String JobCharge="job_charge";
		public static String JobType= "job_type";
		public static String JobEmployNum= "job_employnum";
		public static String JobCreateTime = "create_time";
	}
	/**
	 *回复columns
	 */
	public static final class AnswerListColumns implements BaseColumns{
		public static final String TABLE_NAME = "answer_listinfo";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ Constant.AUTHORITY + "/" + TABLE_NAME);
		public static final String DEFAULT_SORT_ORDER = "_id DESC";
		public static String mAskid="appeal_id";
		public static String mid="id";
		public static String mName="name";
		public static String mContent= "content";
		public static String mCall= "call";
		public static String mCreateTime = "create_time";
		public static String mImage = "thumb";
	}
}
