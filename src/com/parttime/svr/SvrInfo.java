package com.parttime.svr;

/**
 * 
 * @author 灰色的寂寞
 * @function 本app中所有需要的信息类和 静态变量（关于服务器中的静态变量）
 * @date 2015-1-18
 * @time 14:00
 */
public class SvrInfo {

	/**
	 * ==================================服务器地址和信息==============================
	 * =======
	 */
	public static String SERVER_ADDR = "101.200.176.136/jianzhi365/api.php/Home/Api/";
	public final static int SYSTEM_USER_ID = 1;
	public static String SERVER_API_PATH = "http://" + SERVER_ADDR;

	/**
	 * ==================================用户的api接口调用============================
	 * =========
	 */

	public static String LOGIN_API = SERVER_API_PATH + "login";// 账户登录接口
	public static String REGITSTER_API = SERVER_API_PATH + "register";// 注册接口
	public static String FORGET_PASS_API = SERVER_API_PATH + "forgotPass";// 忘记密码接口
	public static String CHANGE_PASS_API = SERVER_API_PATH + "changePass";// 修改密码接口
	public static String SENDMAIL_API = SERVER_API_PATH + "sendMail";// 邮箱验证接口
	public static String SENDPHONE_API = SERVER_API_PATH + "sendMessage";// 短信验证接口
	public static String GET_INDEX_LIST_API = SERVER_API_PATH + "getIndexList";// 热门工作推荐接口
	public static String GET_JOB_LIST_API = SERVER_API_PATH + "getJobList";// 工作列表接口
	public static String GET_JOB_INFO_API = SERVER_API_PATH + "getJobInfo";// 获取职位信息详情
	public static String GET_ADD_RESUME_API = SERVER_API_PATH + "addResume";// 新建简历的接口
	public static String GET_RESUME_LIST_API = SERVER_API_PATH + "getResumeList";// 获取简历列表
	public static String GET_RESUME_INFO_API = SERVER_API_PATH + "getResumeInfo";// 获取简历详情
	public static String DEL_RESUME_API = SERVER_API_PATH + "delResume";// 删除简历
	public static String UP_RESUME_API = SERVER_API_PATH + "upResume";// 修改简历
	public static String ADD_HIRE_API = SERVER_API_PATH + "addHire";// 新建雇佣关系
	public static String PUBLIC_RESUME_API = SERVER_API_PATH + "publicResume";// 发布求职信息

	public static String TASK_GET_INFO_LIST_API = SERVER_API_PATH + "getHire";// 获取任务信息

	public static String TASK_SIGNON_OFF_API = SERVER_API_PATH + "signIn";// 签到签退接口
	public static String TASK_GETSIGN_INFO_API = SERVER_API_PATH + "getSignIn";// 获取签到签退信息接口

	public static String GET_PERSONAL_INFO_API = SERVER_API_PATH + "getUserInfo";// 获取个人信息
	public static String SUBMIT_PERSONAL_INFO_API = SERVER_API_PATH + "upUserInfo";// 提交个人信息
	public static String MODIFY_HEADIMAGE_API = SERVER_API_PATH + "upAvatar";// 头像接口

	public static String PUBLISH_ASKING_API = SERVER_API_PATH + "addAppeal";// 发布求助信息
	public static String GET_PUBLISH_INFO_API = SERVER_API_PATH + "getAppealList";// 获取求助信息列表
	public static String GET_VOLUNTEER_INFO_API = SERVER_API_PATH + "getPgList";// 获取公益信息列表

	public static String GET_SIGNUP_INFO_API = SERVER_API_PATH + "signUp";// 公益报名
	public static String GET_USERNAME_API = SERVER_API_PATH + "getUsername";// 获取用户名
	public static String GET_NEW_VERSIONINFO_API = SERVER_API_PATH + "getVersion";//
	/***** 以下是企业接口 ********/
	public static String GET_PUBLIC_RESUME_API = SERVER_API_PATH + "getPublicResume";// 获取求职信息
	public static String GET_COMPANY_INFO_API = SERVER_API_PATH + "getCompanyInfo";// 获取企业信息
	public static String GET_COMPANY_PJOB_INFO_API = SERVER_API_PATH + "getCompanyJob";// 获取企业发布职位信息
	public static String GET_INDEX2_LIST_API = SERVER_API_PATH + "getIndex2List";// 获取个人推荐列表
	public static String ADD_JOB_API = SERVER_API_PATH + "addJob";// 发布职位
	public static String DELETE_JOB_API = SERVER_API_PATH + "delJob";// 删除职位
	public static String UP_COMPANY_INFO_API = SERVER_API_PATH + "upCompanyInfo";// 修改企业信息
	public static String ADD_COMMENT_API = SERVER_API_PATH + "addComment";// 发表评论
	public static String GET_COMMENT_API = SERVER_API_PATH + "getComment";// 获取评论列表
	public static String GET_COMPANYLEVEL_API = SERVER_API_PATH + "getCredit";// 获取信誉度
	public static String SET_HIRER_API = SERVER_API_PATH + "setHire";// 设置雇佣人
	public static String SET_ADDREPLY_API = SERVER_API_PATH + "addReply";// 设置回复
	public static String SET_GETREPLY_API = SERVER_API_PATH + "getReply";// 获取回复
	public static String SET_FEEDBACK_API = SERVER_API_PATH + "feedback";// 个人回复

	public static String SET_SUBMIT_CONFIRMIMAGE_API = SERVER_API_PATH + "identity";// 上传认证图片
	/**
	 * ================================== 服务器的返回结果 错误等
	 * ErrorMsgSvr=================================
	 */

	// 账户相关
	public final static int SVR_RESULT_SUCCESS = 0;// 接口正常数据正常返回
	public final static int SVR_SERVICE_CENTER_ID_NULL = 1;// 服务中心id为空
	public final static int SVR_USER_NAME_NULL = 2;// 用户名为空
	public final static int SVR_USER_PASSWORD_NULL = 3;// 用户名密码为空
	public final static int SVR_VCODE_NULL = 4;// 验证码为空
	public final static int SVR_UNIQUE_ID_NULL = 5;// UNIQUE_ID为空
	public final static int SVR_LOGIN_TOKEN_NULL = 6;// 登录令牌为空时
	public final static int SVR_ADMIN_ID_NULL = 7;// admin_id为空时
	public final static int SVR_PRODUCT_ID_NULL = 8;// product_id为空时
	public final static int SVR_LOCK_PARAMS_NULL = 9;// 项目锁定操作时没有参数
	public final static int SVR_PHONE_NULL = 10;// 手机号码为空时
	public final static int SVR_SUBBRACH_BANK_NULL = 11; // 支行id没有
	public final static int SVR_SEARCH_CONTENT_NULL = 12; // 搜索的内容为空时
	public final static int SVR_SEARCH_MODE_NULL = 13;// 搜索的方式为空
	public final static int SVR_SEARCH_AMOUNT_NULL = 14;// 投资金额为空时
	public final static int SVR_CONTRANTNO_NULL = 15;// 合同编号为空时
	public final static int SVR_PAYSTATE_NOCHOICE = 16;// 支付状态没有选择(支付状态没有选择)
	public final static int SVR_PURCHASER_NULL = 17; // 购买方为空
	public final static int SVR_PAYSTATE_NOCHOICE_ERROR = 18;// 支付状态没有选择(支付状态没有选择)
	public final static int SVR_PROJECT_ID_NULL = 19;// 项目id为空
	public final static int SVR_MESSAGE_TYPE_NOT_EMPTY = 20;// 短信类型不能为空
	public final static int SVR_MESSAGE_NOT_EXIST = 21; // 短信类型不存在
	public final static int SVR_MESSAGE_AUTH_NOT_EMPTY = 22; // 短信验证码不能为空
	public final static int SVR_LOGIN_FAIL = 1000;// 登陆失败
	public final static int SVR_ACCOUNT_PASSWORD_ERROR = 1001; // 账户密码错误
	public final static int SVR_USER_NOT_EXIST = 1002; // 用户不存在
	public final static int SVR_VERIFICATION_CODE_ERROR = 1003; // 验证码错误
	public final static int SVR_ACCOUNT_LOCKED = 1004;// 账户已被锁定
	public final static int SVR_LOGIN_FAIL_CANCLE = 1005; // 账户已被锁定(账户注销失败)
	public final static int SVR_ERROR_N_TIME = 1006;// 错误一次到N次(密码错误，您还有N-1次登录机会)
	public final static int SVR_ADMIN_LOGIN = 1007; // 超级管理员登录
	public final static int SVR_ADMIN_GROUP_NOT_EXIST_DISABLE = 1008;// 此管理员分组已禁用或不存在
	public final static int SVR_DATA_ABNORMAL = 1009;// 数据异常，服务中心用户缺少父级编号
	public final static int SVR_INVESTOR_CALL_REGISTER = 1102; // 投资人电话已经注册
	public final static int SVR_INVESTOR_MAIL_REGISTER = 1103; // 投资人邮箱已经被注册
	public final static int SVR_TOKEN_VALIDATION_FAIL = 1200; // TOKEN验证失败
	public final static int SVR_MESSAGE_AUTH_CODE_FAIL = 1201; // 验证短信验证码失败
	public final static int SVR_NOT_ALLOW_PAD_LOGININ = 1010; // 该用户不允许在pad服务中心登录

	// 项目相关
	public final static int SVR_CREATE_PROJECT_FAIL = 2000; // 创建项目失败
	public final static int SVR_MODIFY_PROJECT_DATA_FAIL = 2001;// 项目数据修改失败
	public final static int SVR_PROJECT_LOCK_FAIL = 2002;// 项目锁定失败
	public final static int SVR_PROJECT_UNLOCK_FAIL = 2003;// 项目解锁失败
	public final static int SVR_OPERATING_DATA_NO_PERMISSION = 2004;// 无权限操作此数据
	public final static int SVR_PROJECT_NOT_LOCK = 2005;// 该项目不能被锁定
	public final static int SVR_USER_LOCK_NOT_OPERATE = 2006; // 用户已经被锁定，不能进行操作
	public final static int SVR_USER_HAS_LOCK_PROJECT_NOT_LOCK = 2007;// 用户已经有锁定项目，不能在锁定项目
	public final static int SVR_INVESTMENT_FAIL = 2100; // 投资失败
	public final static int SVR_SUBSCRIPTION_AMOUNT_MORE_REMAIN_AMOUNT = 2101;// 认购金额超过项目剩余可认购金额

	// 短信相关
	public final static int SVR_MESSAGE_SEND_FAIL = 3000; // 短信发送失败
	public final static int SVR_MESSAGE_SEND_TOO_OPTEN = 3001; // 发送过于频繁
	public final static int SVR_SMS_VALIDATION_FAIL = 3002; // 短信验证失败
	public final static int SVR_INVESTOR_ACCOUNT_PHONE_NOT_NULL = 3003; // 投资人账号和手机号不能同时为空
	public final static int SVR_NOT_FIND_USER_PHONENUM = 3004;// 没找到用户的手机号
	public final static int SVR_USER_PHONE_FORMAT_ERROR = 3005;// 用户手机格式不正确
	public final static int SVR_MESSAGE_AUTH_TIME_OUT = 3100;// 短信验证码超时
	public final static int SVR_MSGC_DATA_NOT_NORMAL = 3200; // MSGC返回数据不正常
	// 投资人相关
	public final static int SVR_ADD_INVESTOR_FAIL = 4000;// 添加投资人失败
	public final static int SVR_MODIFY_INVESTOR_INFO_FAIL = 4001;// 修改投资人信息失败
	public final static int SVR_INVESTOR_INFO_NOT_EXITEST = 4002; // 修改投资人信息失败
	public final static int SVR_OFFLINE_INVESTMENT_CONFIRMED_NOT_CHANGE = 4003; // 线下投资已经确认不允许修改
	public final static int SVR_PHONENUM_IS_ACCUPY = 4004; // 项目剩余可投金额不足,请修改金额

	// 文件上传
	public final static int SVR_FILE_UPLOAD_FAIL = 4000;// 文件上传失败
	public final static int SVR_FILE_FORMAT_ERROR = 4001; // 文件格式不正确
	public final static int SVR_FILE_SIZE_LARGE = 4002;// 文件大小过大

	// 其他
	public final static int SVR_GET_DATA_FAIL = 9000; // 获取数据失败
	public final static int SVR_UNKNOWN_PARAMS = 9001; // 未知的输入参数

	public final static int SVR_RESULT_UNKNOWN_ERR = -1000;
	public final static int SVR_RESULT_FAILED = -1001;
	public final static int SVR_RESULT_NULL_RESULT = -24;
	public final static int SVR_RESULT_FILE_CONTENT_ERR = -26;
	public final static int SVR_RESULT_NO_FILENAME_SET = -28;
	public final static int SVR_RESULT_DOWNLOAD_FILE_FAILED = -27;
	public final static int SVR_DATA_ITEM_IS_APPROVED_DONT_UPLOAD_FILE = 5001;
	// Project Buffer
	public final static int SVR_NOT_PROJECT_BUFFER = 2009;
	public final static int SVR_NOT_FIND_PRODUCT_INFO = 2008;
	public final static int SVR_CUSTOMER_NOT_NULL = 32;
	public final static int SVR_RESPON_INFO_NOT_NULL = 30;
	public final static int SVR_REAL_NAME_NOT_NULL = 35;
	public final static int SVR_IDCARD_NOT_NULL = 36;

	/*****************/
	public final static int SVR_REGISTER_USER_EXIST = -1;// 用户名已被注册
	public final static int SVR_REGISTER_AVATAR_FAILED = -2;// 头像存储失败
	public final static int SVR_REGISTER_DATA_FAILED = -3;// 数据存储失败
	public final static int SVR_LOGIN_FAILED = -4;// 登录失败
	public final static int SVR_EMAIL_FORMAT_IS_NOT_CORRECT = -6;// 邮箱格式不正确
	public final static int SVR_DATA_LOOK_FAILED = -9;// 数据查询失败
	public final static int SVR_OLDPWD_ERROR = -7; // 原密码不正确
	public final static int SVR_OLDPWD_NEWPWD_SAME = -8; // 新密码与原密码一致
	public final static int SVR_MSG_ACCOUNT_PWD_IN_NULL = -1; // 帐号或密码为空
	public final static int SVR_MSG_PHONE_NULL = -2; // 下发号码为空
	public final static int SVR_MSG_CONTENT_IN_NULL = -3; // 下发内容为空
	public final static int SVR_MSG_CONTENT_IS_LONG = -4; // 内容超长
	public final static int SVR_MSG_PHONE_IS_LONG = -5; // 下发号码超长
	public final static int SVR_MSG_SYSTEM_ERROR = -99; // 系统异常

}
