package com.parttime.struct;
/**
 * @function 数据的显示结构
 * @author 灰色的寂寞
 *
 */
public class PartTimeStruct {
	
	/**发布求助结构 求助struct*/
	public static class AskInfoStruct{
		public int mAskid;
		public int mUserid;
		public String mName;//姓名
		public String mTheme;//求助标题
		public String mContent;//内容
		public String mCall;//电话
		public String mCreateTime;//创建时间
		public String mImage;//详情图片
	}
	public static class AskAnswerStruct{
		public int mAskid;
		public int mid;
		public String mName;//姓名
		public String mContent;//内容
		public String mCall;//电话
		public String mCreateTime;//创建时间
		public String mImage;//详情图片
	}
	/**志愿者struct*/
	public static class VolunteerStruct{
		public int mVolunteerid;
		public String mTheme;
		public String mContent;
		public String mThumb;
		public String mCreateTime;
	}
	
	/**
	 * 志愿者报名struct
	 */
	public static class VolunteerSignInfoStruct{
		public int userid;
		public String name;
		public String papers;
		public String content;
		public String call;
		public int volunteerid;
		public String image;
	}
	/**
	 * 版本信息返回
	 */
	public static class VersionInfoStruct{
		public String mNewContent;
		public int mNewVersionCode;
		public String mNewVersionName;
		public String mVersionNewTime;
	}
	
	/**
	 * 任务列表内容的Struct 
	 * 
	 */
	public static class PersonalTaskStruct{
		public String mTilte;//工作标题
		public String mImageString;//时间/图片列表
		public String mJobId;//工作id 
		
	}
	/**
	 * 工作页中的工作详情的Struct
	 */
	public static class JobCollectionStruct{
		public String mJobSign;//工作标记
		public int mJobCredibility;//工作信誉度
		public String mJobNameType;//工作名称
		public String mPrice;//工作单价
		public int mDistance;//工作地址多少米
		public String mCompaneyName;//工作联系人
		public String mTime;//工作发布时间
	}
	/**
	 * 职位管理的Struct
	 */
	public static class JobManagedStruct{
		public String mHireid;//雇佣id
		public String mUserName;//员工名称
		public String mTime;//工作发布时间
		public String mJobNameType;//工作名称
		public String mAddressDistance;//工作地址，多少米
		public String mUserHeadImage;//头像
	}

	/**
	 * 登录返回的数据
	 * @author huxixi
	 *
	 */
	public static class LoginStruct{
		public String mUserId;
		public String mName;
		public String mEmail;
		public String mHead;
		public String mType;
		public String mRemember;
	}
	/**
	 * 热门工作推荐返回的数据
	 * @author huxixi
	 */
	public static class HotWorkStruct {
		public String mId;//id
		public String mName ;//职位名
		public String mCharge;//佣金
		public String mType;//类型
		public String mNum;//招聘人数
		public String mCreate_Time;//发布时间
		public String mCompany_Name;//公司名称
		public String mCompany_Add;//公司地址
		public String mCompany_Level;//公司信誉度
		public String mCompany_Position;//公司经纬度
		public String mUserId;//公司Id
		
	}
	/**
	 * 求职者推荐返回的数据
	 * @author huxixi
	 */
	public static class HotSeekerStruct{
	     public String mRid;  //简历的id
	     public String mRjob; //职位
	     public String mRadd;//公司地址
	     public String mRstart_time; //开始时间
	     public String mEnd_time;//结束时间
	     public String mRtype; //类别
	     public String mRname;//姓名
	     public String mRsex;//性别
	     public String mRheight;//身高
	     public String mRcall;//电话
	     public String mRexperience;//工作经历
	     public String mRcreate_time;//创建时间
	     public String mUser_id;//用户id
	     public String mPosition;//求职的经纬度
	}
	/**
	 * 职位详情返回的数据
	 */
	public static class JobInfoStruct{
		public String mId;
		public String mName; //职位名称
		public String mCharge; //职位佣金
		public String mType;//职位类别
		public String mNum;//招聘人数
		public String mCreate_time;//创建时间
		public String mPresent;//职位介绍
		public String mRequire;//职位要求
		public String mCondition;//职位申请条件
		public String mThumb;//图片示例
		public String mStartTime;//开始时间
		public String mEndTime;//结束时间
		public String mCompany_Name;//公司名称
		public String mCompany_status;//工作状态
		public String mCompany_Code;//公司营业执照编码
		public String mCompany_Add;//公司地址
		public String mCompany_Level;//公司信誉度
		public String mUserId;//公司id
	}
	
	/**
	 * 评论表中的数据
	 */
	public static class CommentStruct{
		
		public String mCreateTime;//创建时间
		public String mBusiness_Id;// 公司id
		public String mId;// id
		public String mPrestige;// 公司的信誉度
		public String mPersonal_Id;// 个人id
		public String mName;// 用户名
		public String mComment;// 评论
		
		
	}
	/**
	 * 求职简历列表的数据
	 */
	public static class AllResumeStruct{
		public String mRid;//
		public String mRjob;//期望职位 
		public String mRadd; //工作地区 
		public String mRstart_Time;//开始时间 
		public String mRend_Time;//结束时间 
		public String mRtype;//类型 
		public String mRJobType;//已申请或已雇佣
		public String mRname;//姓名 
		public String mRsex;//性别 
		public String mRheight;//身高 
		public String mRcall;//手机 
		public String mRexperience;//兼职经历
		public String mUserid;//用户id
		
	}
	/**
	 * 获取求职信息的数据
	 */
	public static class PublicResumeStruct{
		
	public String mRheight;//身高
	public String mRtype;//类别
	public String mRname;//姓名
	public String mRsex;//性别
	public String mRstart_time;//工作開始時間
	public String mRexperience;//经验
	public String mRadd;//地址
	public String mRjob;//工作
	public String mRid;//简历id
	public String mRuser_id;//用户id
	public String mRcall;//电话
	public String mRpublic;//是否公开
	public String mRrend_time;//结束时间
	public String mPosition;//经纬度
	public String mRcreate_time;//创建时间
		
		
	}
	/**
	 * 通讯表的数据
	 */
	public static class Communication_UserStruct{
		public String userid;//用户id
		public String comm_userid;//聊过天人的id
		public String comm_head_image;//聊过天人的头像
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**************************************灰色的寂寞********************************************************/
	
	
	public static class CompanyLatLngStruct {
		public long company_id;
		public double company_lat;
		public double company_lng;
	}
	public static class PersonalLatLngStruct {
		public long personal_id;
		public double personal_lat;
		public double personal_lng;
		
	}
	

	/**
	 * 个人详情的struct 
	 */
	public static class PersonalDetailsStruct{
		public long mPersonalId;//id
		public String mPersonalHeadImage;//头像
		public String mPersonalName;//姓名
		public String mPersonalLinkman;//昵称
		public String mPersonalGender;//性别
		public String mPersonalEmail;//邮件
		public String mPersonalJobExp;//工作经验
		public String mPersonalAddress;//详细地址
		public String mPersonalCall;//手机
		public String mPersonalCardConfirm;//认证
	}
	
	/**
	 * 签到结构 
	 */
	public static class SignStruct{
		public int hire_id;
		public int day;
		public int type;
		public String image;
	}
	/**
	 *用户被雇佣的工作列表 
	 */
	public static class HiredStruct{
		public int id;//hire_id
		public int job_id;
		public int r_id;//简历id
		public String engage;//雇佣状态
		public String create_time;
		public String hire_name;
	}
	/**
	 * 企业详情的Struct
	 */
	public static class CompanyDetailsStruct{
		public int mCompanyId;//id
		public String mModifyHeadImage;//头像
		public String mModifyProperty;//修改性质
		public String mCompanyName;//公司名称
		public String mCompanyConfirmState;//公司的认证状态
		public String mDetailsPosition;//详细地址
		public String mPOI;//详细地址坐标
		public String mCompanyConfirm;//企业认证
		public String mCompanyEmail;//邮箱
		public String mCompanyContact;//联系方式
		public String mCompanyIdentity;//认证图片
		public int muserid;
	}
	/**
	 * 企业发布的职位列表
	 */
	public static class CompanyPJobListStruct{
		public int mJobId;//id
		public String mJobName;//工作名称
		public String mJobCharge;//职位的佣金
		public String mJobType;//工作类型
		public String mJobEmployNum;//招聘人数
		public String mCreateTime;//发布时间
	}
	
}
