package com.parttime.constant;

import android.os.Environment;

/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-18
 * @time 14:00
 * @function 本app中所有需要的信息类和 静态变量
 */
public class Constant {
	public static final String AUTHORITY = "com.parttime.data.PartTimeProvider.job";// 软件名称+provider+公司性质（金融）
	public static String CURRENT_USER_DB_NAME;

	public static final String USER_TYPE = "personal";

	/****** 环信用到的 *******/
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";

	public static final String ACCOUNT_REMOVED = "account_removed";

	/******* 百度的key ******/
	public static String BAIDU_AK = "MWFFbMn4clOH08mQ8GpflFwc";
	public static String BAIDU_SK = "pZV1BP7lIftt2GktieLYWat9o1ZBQ4gv";
	public static String BAIDU_SN = "48:07:CE:F2:31:8E:BD:ED:F3:44:6E:2E:03:AF:62:FE:83:04:70:D8;com.parttime.parttimejob";

	/**
	 * =================================== 本地sd卡中建包
	 * =======================================
	 */
	public static String PARTTIMEJOB_PATH = "/parttime/";
	public static String PARTTIMEJOB_TEMP_PATH = PARTTIMEJOB_PATH + "temp/";
	public static String PARTTIMEJOB_HEAD = PARTTIMEJOB_PATH + "head/";
	public static String PARTTIMEJOB_IMAGE = PARTTIMEJOB_PATH + "images/";
	public static String PARTTIMEJOB_DOWNLOAD = PARTTIMEJOB_PATH + "downloads/";

	public static String PARTTIMEJOB_SAVE_IMAGE_DIR = Environment.getExternalStorageDirectory() + PARTTIMEJOB_IMAGE;

	/**
	 * ======================================
	 * preferences中存储用户的信息===========================
	 */
	public static final String PREF_FIRST_RUN = "pref_key_first_run";
	public static final String FINISH_ACTIVITY_SHARED = "finish_activity_shared";
	public static final String FINISH_ACTIVITY_FLAG = "finish_activity_flag";
	public static final String USERINFO_SHARED = "userinfo_flag";
	public static final int FINISH_ACTIVITY_FLAG_HOMEPAGE = 1;
	public static final int FINISH_ACTIVITY_FLAG_JOB = 2;
	public static final int FINISH_ACTIVITY_FLAG_MANAGER = 3;
	public static final int FINISH_ACTIVITY_FLAG_COMMUNICATION = 4;
	public static final int FINISH_ACTIVITY_FLAG_PERSONAL = 5;
	public static final int FINISH_ACTIVITY_FLAG_COMPANY = 6;

	/**
	 * ======================================
	 * 调用系统的剪切图片result的信息===========================
	 */
	public final static int MESSENGE_REQUEST_CODE_ATTACH_PHOTO = 141;
	public final static int MESSENGE_REQUEST_CODE_TAKE_PHOTO = 142;
	public final static int MESSENGE_REQUEST_CODE_CROP_PHOTO = 143;

	public final static int MESSENGE_REQUEST_CODE_CROP_BIG_PHOTO_RESULT = 144;
	public final static int MESSENGE_REQUEST_CODE_CROP_BIG_PHOTO_CAMERA_RESULT = 145;

	public final static int MESSENGE_REQUEST_CODE_ATTACH_VIDEO = 146;
	public final static int MESSENGE_REQUEST_CODE_TAKE_VIDEO = 147;

	public final static int MESSENGE_REQUEST_CODE_ATTACH_SOUND = 148;
	public final static int MESSENGE_REQUEST_CODE_TAKE_SOUND = 149;

	public final static int MESSENGE_REQUEST_CODE_TAKE_PICTURE = 150;
	public final static int MESSENGE_REQUEST_CODE_ATTACH_PICTURE = 151;
}
