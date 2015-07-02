package com.parttime.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.parttime.constant.Constant;
import com.parttime.data.PartTimeColumns.AllResumeColumns;
import com.parttime.data.PartTimeColumns.AnswerListColumns;
import com.parttime.data.PartTimeColumns.AskListColumns;
import com.parttime.data.PartTimeColumns.CommentColumns;
import com.parttime.data.PartTimeColumns.CommuncationUsersColumns;
import com.parttime.data.PartTimeColumns.CompanyColumns;
import com.parttime.data.PartTimeColumns.CompanyJobListColumns;
import com.parttime.data.PartTimeColumns.CompanyLatLngColumns;
import com.parttime.data.PartTimeColumns.HiredListColumns;
import com.parttime.data.PartTimeColumns.HotSeekerColumns;
import com.parttime.data.PartTimeColumns.HotWorkColumns;
import com.parttime.data.PartTimeColumns.JobColumns;
import com.parttime.data.PartTimeColumns.JobInfoColumns;
import com.parttime.data.PartTimeColumns.PersonalInfoColumns;
import com.parttime.data.PartTimeColumns.PersonalLatLngColumns;
import com.parttime.data.PartTimeColumns.PublicResumeColumns;
import com.parttime.data.PartTimeColumns.ResumeInfoColumns;
import com.parttime.data.PartTimeColumns.UserInfoColumns;
import com.parttime.data.PartTimeColumns.VolunteerColumns;

/**
 * @author 灰色的寂寞
 * @date 2015-1-18
 * @time 13：00
 * @function 继承ContentProvider实现数据的增删改查
 */
public class PartTimeProvider extends ContentProvider {
	
	private DatabaseHelper mOpenHelper;//数据库对象
	private static final int TYPE_USERINFO = 1002;
	private static final int TYPE_COMPANY_LATLNG = 1003;
	private static final int TYPE_PERSONAL_LATLNG = 1004;
	private static final int TYPE_PERSONAL_INFO = 1005;
	private static final int TYPE_HOTWORK_LATLNG = 1006;
	private static final int TYPE_JOB_LIST_LATLNG = 1007;
	private static final int TYPE_JOB_INFO_LIST_LATLNG = 1008;
	private static final int TYPE_ALL_RESUME_LIST_LATLNG = 1009;
	private static final int TYPE_RESUME_INFO_LATLNG = 1010;
	private static final int TYPE_COMMUNICATION_USER_LATLNG = 1011;
	private static final int TYPE_PUBLIC_RESUME_LATLNG = 1012;
	private static final int TYPE_HIREDJOB_LIST = 1013;
	private static final int TYPE_HOTSEEKER_LIST = 1014;
	private static final int TYPE_ASK_LIST = 1015;
	private static final int TYPE_VOLUNTEER_LIST = 1016;
	private static final int TYPE_COMPANY_INFO_LIST = 1017;
	private static final int TYPE_COMPANY_PJPB_LIST = 1018;
	private static final int TYPE_COMMENT_LIST = 1019;
	private static final int TYPE_ANSWER_LIST = 1020;

	
	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Constant.AUTHORITY, UserInfoColumns.TABLE_NAME,TYPE_USERINFO);
		sUriMatcher.addURI(Constant.AUTHORITY, CompanyLatLngColumns.TABLE_NAME,TYPE_COMPANY_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, PersonalLatLngColumns.TABLE_NAME,TYPE_PERSONAL_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, PersonalInfoColumns.TABLE_NAME,TYPE_PERSONAL_INFO);
		sUriMatcher.addURI(Constant.AUTHORITY, HotWorkColumns.TABLE_NAME,TYPE_HOTWORK_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, JobColumns.TABLE_NAME,TYPE_JOB_LIST_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, JobInfoColumns.TABLE_NAME,TYPE_JOB_INFO_LIST_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, AllResumeColumns.TABLE_NAME,TYPE_ALL_RESUME_LIST_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, ResumeInfoColumns.TABLE_NAME,TYPE_RESUME_INFO_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, CommuncationUsersColumns.TABLE_NAME,TYPE_COMMUNICATION_USER_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, PublicResumeColumns.TABLE_NAME,TYPE_PUBLIC_RESUME_LATLNG);
		sUriMatcher.addURI(Constant.AUTHORITY, HiredListColumns.TABLE_NAME,TYPE_HIREDJOB_LIST);
		sUriMatcher.addURI(Constant.AUTHORITY, HotSeekerColumns.TABLE_NAME,TYPE_HOTSEEKER_LIST);
		sUriMatcher.addURI(Constant.AUTHORITY, AskListColumns.TABLE_NAME,TYPE_ASK_LIST);
		sUriMatcher.addURI(Constant.AUTHORITY, VolunteerColumns.TABLE_NAME,TYPE_VOLUNTEER_LIST);
		sUriMatcher.addURI(Constant.AUTHORITY, CompanyColumns.TABLE_NAME,TYPE_COMPANY_INFO_LIST);
		sUriMatcher.addURI(Constant.AUTHORITY, CompanyJobListColumns.TABLE_NAME,TYPE_COMPANY_PJPB_LIST);
		sUriMatcher.addURI(Constant.AUTHORITY, CommentColumns.TABLE_NAME,TYPE_COMMENT_LIST);
		sUriMatcher.addURI(Constant.AUTHORITY, AnswerListColumns.TABLE_NAME,TYPE_ANSWER_LIST);
	}
	
	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "ParTtime_Job.db";
		private static final int DATABASE_VERSION = 2;

		DatabaseHelper(Context context, String db_name) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			/**
			 * 登录表
			 */
			 String sqlstr = "CREATE TABLE " + UserInfoColumns.TABLE_NAME + " ("

					+ UserInfoColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ UserInfoColumns.USERID + " TEXT,"
					+ UserInfoColumns.USERNAME + " TEXT,"
					+ UserInfoColumns.AVATAR + " TEXT,"
					+ UserInfoColumns.TYPE + " TEXT,"
					+ UserInfoColumns.REMEMBER + " TEXT,"
					+ UserInfoColumns.EMAIL + " TEXT"
					+ ");";
			db.execSQL(sqlstr);
			/**
			 * 个人信息表
			 */
			sqlstr = "CREATE TABLE " + PersonalInfoColumns.TABLE_NAME + " ("
					
					+ PersonalInfoColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ PersonalInfoColumns.USERID + " LONG,"
					+ PersonalInfoColumns.AVATAR + " TEXT,"
					+ PersonalInfoColumns.USERNAME + " TEXT,"
					+ PersonalInfoColumns.NIKENAME + " TEXT,"
					+ PersonalInfoColumns.GENDER + " TEXT,"
					+ PersonalInfoColumns.EMAIL + " TEXT,"
					+ PersonalInfoColumns.JOBEXP + " TEXT,"
					+ PersonalInfoColumns.ADDRESS + " TEXT,"
					+ PersonalInfoColumns.CALL + " TEXT,"
					+ PersonalInfoColumns.CONFIRM + " TEXT"
					+ ");";
			db.execSQL(sqlstr);
			
			
			/**
			 * 热门工作推荐表
			 */
			 sqlstr = "CREATE TABLE " + HotWorkColumns.TABLE_NAME + " ("
					
					+ HotWorkColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ HotWorkColumns.Hot_Id + " TEXT,"
					+ HotWorkColumns.NAME + " TEXT,"
					+ HotWorkColumns.CHARGE + " TEXT,"
					+ HotWorkColumns.NUM + " TEXT,"
					+ HotWorkColumns.TYPE + " TEXT,"
					+ HotWorkColumns.CREATE_TIME + " TEXT,"
					+ HotWorkColumns.COMPANY_NAME + " TEXT,"
					+ HotWorkColumns.COMPANY_ADD + " TEXT,"
					+ HotWorkColumns.USERID + " TEXT,"
					+ HotWorkColumns.POSITION + " TEXT,"
					+ HotWorkColumns.LOCATION + " INTEGER,"
					+ HotWorkColumns.COMPANY_LEVEL + " TEXT,"
					+"UNIQUE ( "+HotWorkColumns.Hot_Id + ")"
					+ ");";
			db.execSQL(sqlstr);
			/**
			 * 工作表
			 */
			sqlstr = "CREATE TABLE " + JobColumns.TABLE_NAME + " ("
					
					+ JobColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ JobColumns.Hot_Id + " TEXT,"
					+ JobColumns.NAME + " TEXT,"
					+ JobColumns.CHARGE + " TEXT,"
					+ JobColumns.NUM + " TEXT,"
					+ JobColumns.TYPE + " TEXT,"
					+ JobColumns.CREATE_TIME + " TEXT,"
					+ JobColumns.COMPANY_NAME + " TEXT,"
					+ JobColumns.COMPANY_ADD + " TEXT,"
					+ JobColumns.COMPANY_ID + " TEXT,"
					+ JobColumns.COMPANY_POSITION + " TEXT,"
					+ JobColumns.LOCATION + " FLOAT,"
					+ JobColumns.COMPANY_LEVEL + " TEXT,"
					+"UNIQUE ( "+JobColumns.Hot_Id + ")"
					+ ");";
			db.execSQL(sqlstr);
			/**
			 * 职位详情表
			 */
			sqlstr = "CREATE TABLE " + JobInfoColumns.TABLE_NAME + " ("
					
					+ JobColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					
					+ JobInfoColumns.Hot_Id + " TEXT,"
					+ JobInfoColumns.Company_Id + " TEXT,"
					+ JobInfoColumns.NAME + " TEXT,"
					+ JobInfoColumns.CHARGE + " TEXT,"
					+ JobInfoColumns.NUM + " TEXT,"
					+ JobInfoColumns.TYPE + " TEXT,"
					+ JobInfoColumns.CREATE_TIME + " TEXT,"
					+ JobInfoColumns.Present + " TEXT,"
					+ JobInfoColumns.Require + " TEXT,"
					+ JobInfoColumns.Condition + " TEXT,"
					+ JobInfoColumns.Thumb + " TEXT,"
					+ JobInfoColumns.StartTime + " TEXT,"
					+ JobInfoColumns.EndTime + " TEXT,"
					+ JobInfoColumns.Company_Status + " TEXT,"
					+ JobInfoColumns.Company_Code + " TEXT,"
					+ JobInfoColumns.COMPANY_NAME + " TEXT,"
					+ JobInfoColumns.COMPANY_ADD + " TEXT,"
					+ JobInfoColumns.COMPANY_LEVEL + " TEXT"
					+ ");";
			db.execSQL(sqlstr);
/**
 * 职位简历列表
 */
			sqlstr = "CREATE TABLE " + AllResumeColumns.TABLE_NAME + " ("
					+ AllResumeColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ AllResumeColumns.RID+ " TEXT,"
					+ AllResumeColumns.RJOB+ " TEXT,"
					+ AllResumeColumns.RADD+ " TEXT,"
					+ AllResumeColumns.RSTARTTIME+ " TEXT,"
					+ AllResumeColumns.RENDTIME+ " TEXT,"
					+ AllResumeColumns.RTYPE+ " TEXT,"
					+ AllResumeColumns.RJOBTYPE+ " TEXT,"
					+ AllResumeColumns.RNAME+ " TEXT,"
					+ AllResumeColumns.RSEX+ " TEXT,"
					+ AllResumeColumns.RHEIGHT+ " TEXT,"
					+ AllResumeColumns.RCALL+ " TEXT,"
					+ AllResumeColumns.REXPERIENCE+ " TEXT,"
					+ AllResumeColumns.RUSERID+ " TEXT,"
					+"UNIQUE ( "+AllResumeColumns.RID + ")"
				
					+ ");";
			db.execSQL(sqlstr);
			/**
			 * 职位简历详情表
			 */
			sqlstr = "CREATE TABLE " + ResumeInfoColumns.TABLE_NAME + " ("
					+ ResumeInfoColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ ResumeInfoColumns.RID+ " TEXT,"
					+ ResumeInfoColumns.RJOB+ " TEXT,"
					+ ResumeInfoColumns.RADD+ " TEXT,"
					+ ResumeInfoColumns.RSTARTTIME+ " TEXT,"
					+ ResumeInfoColumns.RENDTIME+ " TEXT,"
					+ ResumeInfoColumns.RTYPE+ " TEXT,"
					+ ResumeInfoColumns.RNAME+ " TEXT,"
					+ ResumeInfoColumns.RSEX+ " TEXT,"
					+ ResumeInfoColumns.RHEIGHT+ " TEXT,"
					+ ResumeInfoColumns.RCALL+ " TEXT,"
					+ ResumeInfoColumns.REXPERIENCE+ " TEXT,"
					+ ResumeInfoColumns.RUSERID+ " TEXT,"
					+"UNIQUE ( "+ResumeInfoColumns.RID + ")"
					
					+ ");";
			db.execSQL(sqlstr);
			/**
			 * 求职者推荐表
			 */
			sqlstr = "CREATE TABLE " + HotSeekerColumns.TABLE_NAME + " ("
					+ HotSeekerColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ HotSeekerColumns.RID+ " TEXT,"
					+ HotSeekerColumns.RJOB+ " TEXT,"
					+ HotSeekerColumns.RADD+ " TEXT,"
					+ HotSeekerColumns.RSTARTTIME+ " TEXT,"
					+ HotSeekerColumns.RENDTIME+ " TEXT,"
					+ HotSeekerColumns.RTYPE+ " TEXT,"
					+ HotSeekerColumns.RNAME+ " TEXT,"
					+ HotSeekerColumns.RSEX+ " TEXT,"
					+ HotSeekerColumns.RHEIGHT+ " TEXT,"
					+ HotSeekerColumns.RCALL+ " TEXT,"
					+ HotSeekerColumns.POSITION+ " TEXT,"
					+ HotSeekerColumns.REXPERIENCE+ " TEXT,"
					+ HotSeekerColumns.RCREATE_TIME+ " TEXT,"
					+ HotSeekerColumns.LOCATION+ " INTEGER,"
					+ HotSeekerColumns.RUSERID+ " TEXT,"
					+"UNIQUE ( "+PublicResumeColumns.mRid + ")"
					
					+ ");";
			db.execSQL(sqlstr);
			/**
			 * 求职信息表
			 */
			sqlstr = "CREATE TABLE " + PublicResumeColumns.TABLE_NAME + " ("
					+ PublicResumeColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ PublicResumeColumns.mRheight+ " TEXT,"
					+ PublicResumeColumns.mRtype+ " TEXT,"
					+ PublicResumeColumns.mRname+ " TEXT,"
					+ PublicResumeColumns.mRsex+ " TEXT,"
					+ PublicResumeColumns.mRstart_time+ " TEXT,"
					+ PublicResumeColumns.mRexperience+ " TEXT,"
					+ PublicResumeColumns.mRadd+ " TEXT,"
					+ PublicResumeColumns.mRjob+ " TEXT,"
					+ PublicResumeColumns.mRid+ " TEXT,"
					+ PublicResumeColumns.mRuser_id+ " TEXT,"
					+ PublicResumeColumns.mRcall+ " TEXT,"
					+ PublicResumeColumns.mRpublic+ " TEXT,"
					+ PublicResumeColumns.mRposition+ " TEXT,"
					+ PublicResumeColumns.mRrend_time+ " TEXT,"
					+ PublicResumeColumns.Location+ " INTEGER,"
					+ PublicResumeColumns.mRcreate_time+ " TEXT,"
					+"UNIQUE ( "+PublicResumeColumns.mRid + ")"
					
					+ ");";
			db.execSQL(sqlstr);
			
			/**
			 * 评论表
			 */
			sqlstr = "CREATE TABLE " + CommentColumns.TABLE_NAME + " ("
					+ CommentColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ CommentColumns.CREATE_TIME+ " TEXT,"
					+ CommentColumns.BUSINESS_ID+ " TEXT,"
					+ CommentColumns.PRESTIGE+ " TEXT,"
					+ CommentColumns.COMMENT+ " TEXT,"
					+ CommentColumns.PERSONAL_ID+ " TEXT,"
					+ CommentColumns.NAME+ " TEXT,"
					+ CommentColumns.ID+ " TEXT,"
					+"UNIQUE ( "+CommentColumns.ID + ")"
					
					+ ");";
			db.execSQL(sqlstr);
			/**
			 * 通讯表
			 */
			sqlstr = "CREATE TABLE " + CommuncationUsersColumns.TABLE_NAME + " ("
					+ CommuncationUsersColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ CommuncationUsersColumns.USERID+ " TEXT,"
					+ CommuncationUsersColumns.COMM_USERID+ " TEXT,"
					+ CommuncationUsersColumns.COMM_HEAD_IMAGE+ " TEXT"
					
					+ ");";
			db.execSQL(sqlstr);
			
			sqlstr = "CREATE TABLE " + CompanyLatLngColumns.TABLE_NAME + " ("
					+ CompanyLatLngColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ CompanyLatLngColumns.company_id+ " LONG,"
					+ CompanyLatLngColumns.company_lat + " DOUBLE,"
					+ CompanyLatLngColumns.company_lng + " DOUBLE"
					+ ");";
			db.execSQL(sqlstr);
			sqlstr = "CREATE TABLE " + PersonalLatLngColumns.TABLE_NAME + " ("
					+ PersonalLatLngColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ PersonalLatLngColumns.personal_id+ " LONG,"
					+ PersonalLatLngColumns.personal_lat + " DOUBLE,"
					+ PersonalLatLngColumns.personal_lng + " DOUBLE"
					+ ");";
			db.execSQL(sqlstr);
			
			sqlstr = "CREATE TABLE " + HiredListColumns.TABLE_NAME + " ("
					+ HiredListColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ HiredListColumns.hired_id+" INTEGER,"
					+ HiredListColumns.job_id+" INTEGER,"
					+ HiredListColumns.r_id+" INTEGER,"
					+ HiredListColumns.engage+" TEXT,"
					+ HiredListColumns.create_time+" TEXT,"
					+ HiredListColumns.hire_name+" TEXT"
					+ ");";
			db.execSQL(sqlstr);
			//求助信息
			sqlstr = "CREATE TABLE " + AskListColumns.TABLE_NAME + " ("
					+ AskListColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ AskListColumns.askid+" INTEGER,"
					+ AskListColumns.userid+" INTEGER,"
					+ AskListColumns.name+" TEXT,"
					+ AskListColumns.content+" TEXT,"
					+ AskListColumns.call+" TEXT,"
					+ AskListColumns.time+" TEXT,"
					+ AskListColumns.theme+" TEXT,"
					+ AskListColumns.image+" TEXT"
					+ ");";
			db.execSQL(sqlstr);
			//公益信息
			sqlstr = "CREATE TABLE " + VolunteerColumns.TABLE_NAME + " ("
					+ VolunteerColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ VolunteerColumns.volunteerid+" INTEGER,"
					+ VolunteerColumns.volunteertheme+" TEXT,"
					+ VolunteerColumns.volunteerthumb+" TEXT,"
					+ VolunteerColumns.volunteercontent+" TEXT,"
					+ VolunteerColumns.volunteercreatetime+" TEXT"
					+ ");";
			db.execSQL(sqlstr);
			//企业的信息
			sqlstr = "CREATE TABLE " + CompanyColumns.TABLE_NAME + " ("
					+ CompanyColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ CompanyColumns.CompanyId+" INTEGER,"
					+ CompanyColumns.userid+" INTEGER,"
					+ CompanyColumns.CompanyName+" TEXT,"
					+ CompanyColumns.CompanyProperty+" TEXT,"
					+ CompanyColumns.CompanyConfirmState+" TEXT,"
					+ CompanyColumns.CompanyConfirm+" TEXT,"
					+ CompanyColumns.CompanyEmail+" TEXT,"
					+ CompanyColumns.CompanyCall+" TEXT,"
					+ CompanyColumns.CompanyIdentity+" TEXT,"
					+ CompanyColumns.CompanyPoiNum+" TEXT,"
					+ CompanyColumns.CompanyDetailsPoi+" TEXT"
					+ ");";
			db.execSQL(sqlstr);
			//企业发布的职位信息
			sqlstr = "CREATE TABLE " + CompanyJobListColumns.TABLE_NAME + " ("
					+ CompanyJobListColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ CompanyJobListColumns.JobId+" INTEGER,"
					+ CompanyJobListColumns.JobName+" TEXT,"
					+ CompanyJobListColumns.JobCharge+" TEXT,"
					+ CompanyJobListColumns.JobType+" TEXT,"
					+ CompanyJobListColumns.JobCreateTime+" TEXT,"
					+ CompanyJobListColumns.JobEmployNum+" TEXT"
					+ ");";
			db.execSQL(sqlstr);
			sqlstr = "CREATE TABLE " + AnswerListColumns.TABLE_NAME + " ("
					+ AnswerListColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ AnswerListColumns.mAskid+" INTEGER,"
					+ AnswerListColumns.mid+" INTEGER,"
					+ AnswerListColumns.mCall+" TEXT,"
					+ AnswerListColumns.mContent+" TEXT,"
					+ AnswerListColumns.mCreateTime+" TEXT,"
					+ AnswerListColumns.mName+" TEXT,"
					+ AnswerListColumns.mImage+" TEXT"
					+ ");";
			db.execSQL(sqlstr);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			switch (DATABASE_VERSION) {
			case 2:

				break;
			default:
				break;
			}
		}

	}
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TYPE_USERINFO:
			count = db.delete(UserInfoColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_PERSONAL_INFO:
			count = db.delete(PersonalInfoColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_COMPANY_LATLNG:
			count = db.delete(CompanyLatLngColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_PERSONAL_LATLNG:
			count = db.delete(PersonalLatLngColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_HOTWORK_LATLNG:
			count = db.delete(HotWorkColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_JOB_LIST_LATLNG:
			count = db.delete(JobColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_JOB_INFO_LIST_LATLNG:
			count = db.delete(JobInfoColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_ALL_RESUME_LIST_LATLNG:
			count = db.delete(AllResumeColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_RESUME_INFO_LATLNG:
			count = db.delete(ResumeInfoColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_COMMUNICATION_USER_LATLNG:
			count = db.delete(CommuncationUsersColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_PUBLIC_RESUME_LATLNG:
			count = db.delete(PublicResumeColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_HIREDJOB_LIST:
			count = db.delete(HiredListColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_HOTSEEKER_LIST:
			count = db.delete(HotSeekerColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_ASK_LIST:
			count = db.delete(AskListColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_VOLUNTEER_LIST:
			count = db.delete(VolunteerColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_COMPANY_INFO_LIST:
			count = db.delete(CompanyColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_COMPANY_PJPB_LIST:
			count = db.delete(CompanyJobListColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_COMMENT_LIST:
			count = db.delete(CommentColumns.TABLE_NAME, where, whereArgs);
			break;
		case TYPE_ANSWER_LIST:
			count = db.delete(AnswerListColumns.TABLE_NAME, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO 
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values = null;
		Uri rtUri = null;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			return null;
		}
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = 0;
		switch (sUriMatcher.match(uri)) {

		case TYPE_USERINFO:
			rowId = db.insert(UserInfoColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(UserInfoColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_PERSONAL_INFO:
			rowId = db.insert(PersonalInfoColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(PersonalInfoColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_COMPANY_LATLNG:
			rowId = db.insert(CompanyLatLngColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(CompanyLatLngColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_PERSONAL_LATLNG:
			rowId = db.insert(PersonalLatLngColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(PersonalLatLngColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_HOTWORK_LATLNG:
			rowId = db.insert(HotWorkColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(HotWorkColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
		case TYPE_JOB_LIST_LATLNG:
			rowId = db.insert(JobColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(JobColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
		case TYPE_JOB_INFO_LIST_LATLNG:
			rowId = db.insert(JobInfoColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(JobInfoColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_ALL_RESUME_LIST_LATLNG:
			rowId = db.insert(AllResumeColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(JobInfoColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_RESUME_INFO_LATLNG:
			rowId = db.insert(ResumeInfoColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(ResumeInfoColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_COMMUNICATION_USER_LATLNG:
			rowId = db.insert(CommuncationUsersColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(CommuncationUsersColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_PUBLIC_RESUME_LATLNG:
			rowId = db.insert(PublicResumeColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(PublicResumeColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_HIREDJOB_LIST:
			rowId = db.insert(HiredListColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(HiredListColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_HOTSEEKER_LIST:
			rowId = db.insert(HotSeekerColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(HotSeekerColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_ASK_LIST:
			rowId = db.insert(AskListColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(AskListColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_VOLUNTEER_LIST:
			rowId = db.insert(VolunteerColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(VolunteerColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_COMPANY_INFO_LIST:
			rowId = db.insert(CompanyColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(CompanyColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_COMPANY_PJPB_LIST:
			rowId = db.insert(CompanyJobListColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(CompanyJobListColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_COMMENT_LIST:
			rowId = db.insert(CommentColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(CommentColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		case TYPE_ANSWER_LIST:
			rowId = db.insert(AnswerListColumns.TABLE_NAME, null, values);
			if (rowId > 0) {
				rtUri = ContentUris.withAppendedId(AnswerListColumns.CONTENT_URI,rowId);
				getContext().getContentResolver().notifyChange(rtUri, null);
				return rtUri;
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return rtUri;
	}

	@Override
	public boolean onCreate() {
		InitDB(Constant.CURRENT_USER_DB_NAME);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case TYPE_USERINFO:
			qb.setTables(UserInfoColumns.TABLE_NAME);
			break;
		case TYPE_PERSONAL_INFO:
			qb.setTables(PersonalInfoColumns.TABLE_NAME);
			break;
		case TYPE_COMPANY_LATLNG:
			qb.setTables(CompanyLatLngColumns.TABLE_NAME);
			break;
		case TYPE_PERSONAL_LATLNG:
			qb.setTables(PersonalLatLngColumns.TABLE_NAME);
			break;
		case TYPE_HOTWORK_LATLNG:
			qb.setTables(HotWorkColumns.TABLE_NAME);
			break;
		case TYPE_JOB_LIST_LATLNG:
			qb.setTables(JobColumns.TABLE_NAME);
			break;
		case TYPE_JOB_INFO_LIST_LATLNG:
			qb.setTables(JobInfoColumns.TABLE_NAME);
			break;
		case TYPE_ALL_RESUME_LIST_LATLNG:
			qb.setTables(AllResumeColumns.TABLE_NAME);
			break;
		case TYPE_RESUME_INFO_LATLNG:
			qb.setTables(ResumeInfoColumns.TABLE_NAME);
			break;
		case TYPE_COMMUNICATION_USER_LATLNG:
			qb.setTables(CommuncationUsersColumns.TABLE_NAME);
			break;
		case TYPE_PUBLIC_RESUME_LATLNG:
			qb.setTables(PublicResumeColumns.TABLE_NAME);
			break;
		case TYPE_HIREDJOB_LIST:
			qb.setTables(HiredListColumns.TABLE_NAME);
			break;
		case TYPE_HOTSEEKER_LIST:
			qb.setTables(HotSeekerColumns.TABLE_NAME);
			break;
		case TYPE_ASK_LIST:
			qb.setTables(AskListColumns.TABLE_NAME);
			break;
		case TYPE_VOLUNTEER_LIST:
			qb.setTables(VolunteerColumns.TABLE_NAME);
			break;
		case TYPE_COMPANY_INFO_LIST:
			qb.setTables(CompanyColumns.TABLE_NAME);
			break;
		case TYPE_COMPANY_PJPB_LIST:
			qb.setTables(CompanyJobListColumns.TABLE_NAME);
			break;
		case TYPE_COMMENT_LIST:
			qb.setTables(CommentColumns.TABLE_NAME);
			break;
		case TYPE_ANSWER_LIST:
			qb.setTables(AnswerListColumns.TABLE_NAME);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		qb.setDistinct(true);
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case TYPE_USERINFO:
			count = db.update(UserInfoColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_PERSONAL_INFO:
			count = db.update(PersonalInfoColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_COMPANY_LATLNG:
			count = db.update(CompanyLatLngColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_PERSONAL_LATLNG:
			count = db.update(PersonalLatLngColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_HOTWORK_LATLNG:
			count = db.update(HotWorkColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_JOB_LIST_LATLNG:
			count = db.update(JobColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_JOB_INFO_LIST_LATLNG:
			count = db.update(JobInfoColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_ALL_RESUME_LIST_LATLNG:
			count = db.update(AllResumeColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_RESUME_INFO_LATLNG:
			count = db.update(ResumeInfoColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_COMMUNICATION_USER_LATLNG:
			count = db.update(CommuncationUsersColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_PUBLIC_RESUME_LATLNG:
			count = db.update(PublicResumeColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_HIREDJOB_LIST:
			count = db.update(HiredListColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_HOTSEEKER_LIST:
			count = db.update(HotSeekerColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_ASK_LIST:
			count = db.update(AskListColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_VOLUNTEER_LIST:
			count = db.update(VolunteerColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_COMPANY_INFO_LIST:
			count = db.update(CompanyColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_COMPANY_PJPB_LIST:
			count = db.update(CompanyJobListColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_COMMENT_LIST:
			count = db.update(CommentColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case TYPE_ANSWER_LIST:
			count = db.update(AnswerListColumns.TABLE_NAME, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	// 用一个给定的数据库名，初始化数据库helper
	public void InitDB(String DBname) {
		mOpenHelper = new DatabaseHelper(getContext(), DBname);
	}
}
