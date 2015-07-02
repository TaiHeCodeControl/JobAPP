package com.parttime.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;

import com.parttime.data.PartTimeColumns.AllResumeColumns;
import com.parttime.data.PartTimeColumns.AnswerListColumns;
import com.parttime.data.PartTimeColumns.AskListColumns;
import com.parttime.data.PartTimeColumns.CommentColumns;
import com.parttime.data.PartTimeColumns.CommuncationUsersColumns;
import com.parttime.data.PartTimeColumns.CompanyColumns;
import com.parttime.data.PartTimeColumns.CompanyJobListColumns;
import com.parttime.data.PartTimeColumns.HiredListColumns;
import com.parttime.data.PartTimeColumns.HotSeekerColumns;
import com.parttime.data.PartTimeColumns.HotWorkColumns;
import com.parttime.data.PartTimeColumns.JobColumns;
import com.parttime.data.PartTimeColumns.JobInfoColumns;
import com.parttime.data.PartTimeColumns.PersonalInfoColumns;
import com.parttime.data.PartTimeColumns.PublicResumeColumns;
import com.parttime.data.PartTimeColumns.ResumeInfoColumns;
import com.parttime.data.PartTimeColumns.UserInfoColumns;
import com.parttime.data.PartTimeColumns.VolunteerColumns;
import com.parttime.struct.PartTimeStruct.AllResumeStruct;
import com.parttime.struct.PartTimeStruct.AskAnswerStruct;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.struct.PartTimeStruct.CommentStruct;
import com.parttime.struct.PartTimeStruct.Communication_UserStruct;
import com.parttime.struct.PartTimeStruct.CompanyDetailsStruct;
import com.parttime.struct.PartTimeStruct.CompanyPJobListStruct;
import com.parttime.struct.PartTimeStruct.HiredStruct;
import com.parttime.struct.PartTimeStruct.HotSeekerStruct;
import com.parttime.struct.PartTimeStruct.HotWorkStruct;
import com.parttime.struct.PartTimeStruct.JobInfoStruct;
import com.parttime.struct.PartTimeStruct.LoginStruct;
import com.parttime.struct.PartTimeStruct.PersonalDetailsStruct;
import com.parttime.struct.PartTimeStruct.PublicResumeStruct;
import com.parttime.struct.PartTimeStruct.VolunteerStruct;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 实现对书库的管理，增删改查
 * @date 2015-1-18
 * @time 14:36
 */
public class PartTimeDB {
	private Context context;
	private static final String TAG = PartTimeDB.class.getSimpleName();
	private static PartTimeDB mPartTimeDB = null;

	/** 使用外部数据库 **/
	public static final String DB_NAME = "city.db"; // 保存的数据库文件名
	public static final String PACKAGE_NAME = "com.parttime.parttimejob";
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/" + DB_NAME; // 在手机里存放数据库的位置

	/*****/
	public PartTimeDB(Context mContext) {
		this.context = mContext;
	}

	public static PartTimeDB getInstance(Context context) {
		if (mPartTimeDB == null) {
			mPartTimeDB = new PartTimeDB(context);
		}
		return mPartTimeDB;
	}

	/**
	 * 向登录表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddLoginInfo(Context context, LoginStruct struct) {
		ContentValues values = new ContentValues();

		values.put(UserInfoColumns.USERID, struct.mUserId);
		values.put(UserInfoColumns.USERNAME, struct.mName);
		values.put(UserInfoColumns.EMAIL, struct.mEmail);
		values.put(UserInfoColumns.AVATAR, struct.mHead);
		values.put(UserInfoColumns.TYPE, struct.mType);
		values.put(UserInfoColumns.REMEMBER, struct.mRemember);

		Uri Evturi = context.getContentResolver().insert(UserInfoColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 向个人信息表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddPersonalInfo(Context context, PersonalDetailsStruct struct) {
		ContentValues values = new ContentValues();
		values.put(PersonalInfoColumns.USERID, struct.mPersonalId);
		values.put(PersonalInfoColumns.AVATAR, struct.mPersonalHeadImage);
		values.put(PersonalInfoColumns.USERNAME, struct.mPersonalName);
		values.put(PersonalInfoColumns.NIKENAME, struct.mPersonalLinkman);
		values.put(PersonalInfoColumns.GENDER, struct.mPersonalGender);
		values.put(PersonalInfoColumns.EMAIL, struct.mPersonalEmail);
		values.put(PersonalInfoColumns.JOBEXP, struct.mPersonalJobExp);
		values.put(PersonalInfoColumns.ADDRESS, struct.mPersonalAddress);
		values.put(PersonalInfoColumns.CALL, struct.mPersonalCall);
		values.put(PersonalInfoColumns.CONFIRM, struct.mPersonalCardConfirm);

		Uri Evturi = context.getContentResolver().insert(PersonalInfoColumns.CONTENT_URI, values);
		long etnid = ContentUris.parseId(Evturi);
		if (etnid < 0) {
			return -1;
		}
		return etnid;
	}

	/**
	 * 通过userid获取对应的详细信息
	 * 
	 * @param mContext
	 * @param puerid
	 * @return
	 */
	public PersonalDetailsStruct GetPersonalDetailsInfo(Context mContext, long puerid) {
		Cursor curtmp = null;
		PersonalDetailsStruct struct = new PersonalDetailsStruct();
		try {
			String selection = PersonalInfoColumns.USERID + "= '" + puerid + "'";
			curtmp = mContext.getContentResolver().query(PersonalInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				curtmp.moveToNext();
				struct.mPersonalId = curtmp.getLong(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.USERID));
				struct.mPersonalHeadImage = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.AVATAR));
				struct.mPersonalName = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.USERNAME));
				struct.mPersonalLinkman = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.NIKENAME));
				struct.mPersonalGender = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.GENDER));
				struct.mPersonalEmail = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.EMAIL));
				struct.mPersonalJobExp = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.JOBEXP));
				struct.mPersonalAddress = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.ADDRESS));
				struct.mPersonalCall = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.CALL));
				struct.mPersonalCardConfirm = curtmp.getString(curtmp.getColumnIndexOrThrow(PersonalInfoColumns.CONFIRM));

			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return struct;
	}

	/**
	 * 清除个人信息
	 * 
	 * @param mContext
	 * @param userid
	 * @return
	 */
	public long ClearPersonalInfo(Context mContext, long userid) {
		String selection = PersonalInfoColumns.USERID + "= '" + userid + "'";
		long count = mContext.getContentResolver().delete(UserInfoColumns.CONTENT_URI, selection, null);
		return count;
	}

	/**
	 * 更新个人信息
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdatePersonalInfo(Context context, PersonalDetailsStruct struct) {
		int Evturi = 0;
		String selection = PersonalInfoColumns.USERID + "= '" + struct.mPersonalId + "'";
		ContentValues values = new ContentValues();
		values.put(PersonalInfoColumns.USERID, struct.mPersonalId);
		values.put(PersonalInfoColumns.AVATAR, struct.mPersonalHeadImage);
		values.put(PersonalInfoColumns.USERNAME, struct.mPersonalName);
		values.put(PersonalInfoColumns.NIKENAME, struct.mPersonalLinkman);
		values.put(PersonalInfoColumns.GENDER, struct.mPersonalGender);
		values.put(PersonalInfoColumns.EMAIL, struct.mPersonalEmail);
		values.put(PersonalInfoColumns.JOBEXP, struct.mPersonalJobExp);
		values.put(PersonalInfoColumns.ADDRESS, struct.mPersonalAddress);
		values.put(PersonalInfoColumns.CALL, struct.mPersonalCall);
		values.put(PersonalInfoColumns.CONFIRM, struct.mPersonalCardConfirm);

		Evturi = context.getContentResolver().update(PersonalInfoColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 判断个人信息是否存在
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistPersonalInfo(long userid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = PersonalInfoColumns.USERID + "= '" + userid + "'";
			curtmp = context.getContentResolver().query(PersonalInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 用户信息表中是否存在某条数据
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistLoginInfo(String name, Context context) {
		Cursor curtmp = null;
		try {
			String selection = UserInfoColumns.USERNAME + "= '" + name + "'";
			curtmp = context.getContentResolver().query(UserInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 更新登录表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateLoginInfo(Context context, LoginStruct struct, String name) {
		int Evturi = 0;
		String selection = UserInfoColumns.USERNAME + "= '" + name + "'";
		ContentValues values = new ContentValues();
		values.put(UserInfoColumns.USERID, struct.mUserId);
		values.put(UserInfoColumns.USERNAME, struct.mName);
		values.put(UserInfoColumns.EMAIL, struct.mEmail);
		values.put(UserInfoColumns.AVATAR, struct.mHead);
		values.put(UserInfoColumns.TYPE, struct.mType);
		values.put(UserInfoColumns.REMEMBER, struct.mRemember);

		Evturi = context.getContentResolver().update(UserInfoColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找数据库中的登录信息
	 * 
	 * @param context
	 * @param name
	 *            用户名
	 * @return
	 */
	public HashMap<String, String> getLoginInfo(Context context, String name) {
		Cursor curtmp = null;

		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String selection = UserInfoColumns.USERNAME + "= '" + name + "'";
			curtmp = context.getContentResolver().query(UserInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				curtmp.moveToNext();
				String login_name = curtmp.getString(curtmp.getColumnIndexOrThrow(UserInfoColumns.USERNAME));
				String login_email = curtmp.getString(curtmp.getColumnIndexOrThrow(UserInfoColumns.EMAIL));
				String login_avatar = curtmp.getString(curtmp.getColumnIndexOrThrow(UserInfoColumns.AVATAR));
				String type = curtmp.getString(curtmp.getColumnIndexOrThrow(UserInfoColumns.TYPE));
				String remember = curtmp.getString(curtmp.getColumnIndexOrThrow(UserInfoColumns.REMEMBER));
				map.put("name", login_name);
				map.put("remember", remember);
				map.put("type", type);
				map.put("email", login_email);
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return map;
	}

	/**
	 * 向热门工作推荐表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddHotWorkInfo(Context context, HotWorkStruct struct) {
		ContentValues values = new ContentValues();

		values.put(HotWorkColumns.Hot_Id, struct.mId);
		values.put(HotWorkColumns.CHARGE, struct.mCharge);
		values.put(HotWorkColumns.COMPANY_ADD, struct.mCompany_Add);
		values.put(HotWorkColumns.COMPANY_NAME, struct.mCompany_Name);
		values.put(HotWorkColumns.CREATE_TIME, struct.mCreate_Time);
		values.put(HotWorkColumns.NAME, struct.mName);
		values.put(HotWorkColumns.NUM, struct.mNum);
		values.put(HotWorkColumns.TYPE, struct.mType);
		values.put(HotWorkColumns.USERID, struct.mUserId);
		values.put(HotWorkColumns.POSITION, struct.mCompany_Position);
		values.put(HotWorkColumns.COMPANY_LEVEL, struct.mCompany_Level);

		Uri Evturi = context.getContentResolver().insert(HotWorkColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 向热门工作推荐表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddHotWorkInfo1(Context context, HashMap<String, String> map, int distance) {
		ContentValues values = new ContentValues();

		values.put(HotWorkColumns.Hot_Id, map.get("hot_id"));
		values.put(HotWorkColumns.CHARGE, map.get("charge"));
		values.put(HotWorkColumns.COMPANY_ADD, map.get("company_add"));
		values.put(HotWorkColumns.COMPANY_NAME, map.get("company_name"));
		values.put(HotWorkColumns.CREATE_TIME, map.get("create_time"));
		values.put(HotWorkColumns.NAME, map.get("name"));
		values.put(HotWorkColumns.NUM, map.get("num"));
		values.put(HotWorkColumns.TYPE, map.get("type"));
		values.put(HotWorkColumns.USERID, map.get("company_id"));
		values.put(HotWorkColumns.POSITION, map.get("position"));
		values.put(HotWorkColumns.COMPANY_LEVEL, map.get("company_level"));
		values.put(HotWorkColumns.LOCATION, distance);

		Uri Evturi = context.getContentResolver().insert(HotWorkColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 热门工作推荐表中是否存在某条数据
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistHotWorkInfo(String hotId, Context context) {
		Cursor curtmp = null;
		try {
			String selection = HotWorkColumns.Hot_Id + "= '" + hotId + "'";
			curtmp = context.getContentResolver().query(HotWorkColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 更新热门工作推荐表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateHotWorkInfo(Context context, HotWorkStruct struct, String HotId) {
		int Evturi = 0;
		String selection = HotWorkColumns.Hot_Id + "= '" + HotId + "'";
		ContentValues values = new ContentValues();
		values.put(HotWorkColumns.Hot_Id, struct.mId);
		values.put(HotWorkColumns.CHARGE, struct.mCharge);
		values.put(HotWorkColumns.COMPANY_ADD, struct.mCompany_Add);
		values.put(HotWorkColumns.COMPANY_NAME, struct.mCompany_Name);
		values.put(HotWorkColumns.CREATE_TIME, struct.mCreate_Time);
		values.put(HotWorkColumns.NAME, struct.mName);
		values.put(HotWorkColumns.NUM, struct.mNum);
		values.put(HotWorkColumns.TYPE, struct.mType);
		values.put(HotWorkColumns.USERID, struct.mUserId);
		values.put(HotWorkColumns.POSITION, struct.mCompany_Position);
		values.put(HotWorkColumns.COMPANY_LEVEL, struct.mCompany_Level);
		Evturi = context.getContentResolver().update(HotWorkColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 更新热门工作推荐表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateHotWorkInfo1(Context context, HashMap<String, String> map, int distance, String HotId) {
		int Evturi = 0;
		String selection = HotWorkColumns.Hot_Id + "= '" + HotId + "'";
		ContentValues values = new ContentValues();
		values.put(HotWorkColumns.Hot_Id, map.get("hot_id"));
		values.put(HotWorkColumns.CHARGE, map.get("charge"));
		values.put(HotWorkColumns.COMPANY_ADD, map.get("company_add"));
		values.put(HotWorkColumns.COMPANY_NAME, map.get("company_name"));
		values.put(HotWorkColumns.CREATE_TIME, map.get("create_time"));
		values.put(HotWorkColumns.NAME, map.get("name"));
		values.put(HotWorkColumns.NUM, map.get("num"));
		values.put(HotWorkColumns.TYPE, map.get("type"));
		values.put(HotWorkColumns.USERID, map.get("company_id"));
		values.put(HotWorkColumns.POSITION, map.get("position"));
		values.put(HotWorkColumns.COMPANY_LEVEL, map.get("company_level"));
		values.put(HotWorkColumns.LOCATION, distance);
		Evturi = context.getContentResolver().update(HotWorkColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找热门工作推荐表中的数据
	 */
	public ArrayList<HashMap<String, String>> getHotWorkInfo(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		try {
			// String selection = HotWorkColumns.Hot_Id + "= '"+ HotId + "'";
			curtmp = context.getContentResolver().query(HotWorkColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, String> map = new HashMap<String, String>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.Hot_Id));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.NAME));
					String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.CHARGE));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.TYPE));
					String num = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.NUM));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.CREATE_TIME));
					String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_NAME));
					String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_ADD));
					String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_LEVEL));
					String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.USERID));
					String position = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.POSITION));

					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("charge", charge);
					map.put("type", type);
					map.put("num", num);
					map.put("create_time", create_time);
					map.put("company_name", company_name);
					map.put("company_add", company_add);
					map.put("company_level", company_level);
					map.put("company_id", company_id);
					map.put("position", position);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 查找热门工作推荐表中的数据
	 */
	public ArrayList<HashMap<String, Object>> getHotWorkInfo1(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		try {
			String order = HotWorkColumns.LOCATION + " asc";
			// String selection = HotWorkColumns.Hot_Id + "= '"+ HotId + "'";
			curtmp = context.getContentResolver().query(HotWorkColumns.CONTENT_URI, null, null, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.Hot_Id));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.NAME));
					String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.CHARGE));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.TYPE));
					String num = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.NUM));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.CREATE_TIME));
					String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_NAME));
					String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_ADD));
					String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_LEVEL));
					String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.USERID));
					String position = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.POSITION));
					int distance = curtmp.getInt(curtmp.getColumnIndexOrThrow(HotWorkColumns.LOCATION));

					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("charge", charge);
					map.put("type", type);
					map.put("num", num);
					map.put("create_time", create_time);
					map.put("company_name", company_name);
					map.put("company_add", company_add);
					map.put("company_level", company_level);
					map.put("company_id", company_id);
					map.put("position", position);
					map.put("location", distance);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 清空热门工作推荐表
	 */
	public int ClearHotWorkInfo() {
		int count = context.getContentResolver().delete(HotWorkColumns.CONTENT_URI, null, null);
		return count;
	}

	/**
	 * 查找热门工作推荐表中的数据
	 */
	public HashMap<String, String> getHotWorkInfo(Context context, String HotId) {
		Cursor curtmp = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String selection = HotWorkColumns.Hot_Id + "= '" + HotId + "'";
			curtmp = context.getContentResolver().query(HotWorkColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.Hot_Id));
				String name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.NAME));
				String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.CHARGE));
				String type = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.TYPE));
				String num = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.NUM));
				String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.CREATE_TIME));
				String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_NAME));
				String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_ADD));
				String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(HotWorkColumns.COMPANY_LEVEL));

				map.put("hot_id", hot_id);
				map.put("name", name);
				map.put("charge", charge);
				map.put("type", type);
				map.put("num", num);
				map.put("create_time", create_time);
				map.put("company_name", company_name);
				map.put("company_add", company_add);
				map.put("company_level", company_level);
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return map;
	}

	/**
	 * 向工作表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddJobInfo(Context context, HotWorkStruct struct) {
		ContentValues values = new ContentValues();

		values.put(JobColumns.Hot_Id, struct.mId);
		values.put(JobColumns.CHARGE, struct.mCharge);
		values.put(JobColumns.COMPANY_ADD, struct.mCompany_Add);
		values.put(JobColumns.COMPANY_NAME, struct.mCompany_Name);
		values.put(JobColumns.CREATE_TIME, struct.mCreate_Time);
		values.put(JobColumns.NAME, struct.mName);
		values.put(JobColumns.NUM, struct.mNum);
		values.put(JobColumns.TYPE, struct.mType);
		values.put(JobColumns.COMPANY_ID, struct.mUserId);
		values.put(JobColumns.COMPANY_POSITION, struct.mCompany_Position);
		values.put(JobColumns.COMPANY_LEVEL, struct.mCompany_Level);

		Uri Evturi = context.getContentResolver().insert(JobColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 向工作表中添加数据
	 * 
	 * @param context
	 * @param struct
	 * @return
	 */
	public long AddJobInfo1(Context context, HashMap<String, String> map, int location_list) {
		ContentValues values = new ContentValues();

		// map.put("hot_id", data_list.get(i).get("hot_id"));
		// map.put("name", data_list.get(i).get("name"));
		// map.put("charge", data_list.get(i).get("charge"));
		// map.put("type", data_list.get(i).get("type"));
		// map.put("num", data_list.get(i).get("num"));
		// map.put("create_time", data_list.get(i).get("create_time"));
		// map.put("company_name", data_list.get(i).get("company_name"));
		// map.put("company_add", data_list.get(i).get("company_add"));
		// map.put("company_level", data_list.get(i).get("company_level"));
		// map.put("position", data_list.get(i).get("position"));
		// map.put("company_id", data_list.get(i).get("company_id"));

		values.put(JobColumns.Hot_Id, map.get("hot_id"));
		values.put(JobColumns.CHARGE, map.get("charge"));
		values.put(JobColumns.COMPANY_ADD, map.get("company_add"));
		values.put(JobColumns.COMPANY_NAME, map.get("company_name"));
		values.put(JobColumns.CREATE_TIME, map.get("create_time"));
		values.put(JobColumns.NAME, map.get("name"));
		values.put(JobColumns.NUM, map.get("num"));
		values.put(JobColumns.TYPE, map.get("type"));
		values.put(JobColumns.COMPANY_ID, map.get("company_id"));
		values.put(JobColumns.COMPANY_POSITION, map.get("position"));
		values.put(JobColumns.COMPANY_LEVEL, map.get("company_level"));
		values.put(JobColumns.LOCATION, location_list);

		Uri Evturi = context.getContentResolver().insert(JobColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 工作表中是否存在某条数据
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistJobInfo(String hotId, Context context) {
		Cursor curtmp = null;
		try {
			String selection = JobColumns.Hot_Id + "= '" + hotId + "'";
			curtmp = context.getContentResolver().query(JobColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 查看工作表中一个几条数据
	 * 
	 * @param context
	 * @return
	 */
	public int getJobInfoNum(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(JobColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 更新工作表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateJobInfo(Context context, HotWorkStruct struct, String HotId) {
		int Evturi = 0;
		String selection = JobColumns.Hot_Id + "= '" + HotId + "'";
		ContentValues values = new ContentValues();
		values.put(JobColumns.Hot_Id, struct.mId);
		values.put(JobColumns.CHARGE, struct.mCharge);
		values.put(JobColumns.COMPANY_ADD, struct.mCompany_Add);
		values.put(JobColumns.COMPANY_NAME, struct.mCompany_Name);
		values.put(JobColumns.CREATE_TIME, struct.mCreate_Time);
		values.put(JobColumns.NAME, struct.mName);
		values.put(JobColumns.NUM, struct.mNum);
		values.put(JobColumns.TYPE, struct.mType);
		values.put(JobColumns.COMPANY_ID, struct.mUserId);
		values.put(JobColumns.COMPANY_POSITION, struct.mCompany_Position);
		values.put(JobColumns.COMPANY_LEVEL, struct.mCompany_Level);
		Evturi = context.getContentResolver().update(JobColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 更新工作表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateJobInfo1(Context context, HashMap<String, String> map, int location_list, String HotId) {
		int Evturi = 0;
		String selection = JobColumns.Hot_Id + "= '" + HotId + "'";
		ContentValues values = new ContentValues();
		values.put(JobColumns.Hot_Id, map.get("hot_id"));
		values.put(JobColumns.CHARGE, map.get("charge"));
		values.put(JobColumns.COMPANY_ADD, map.get("company_add"));
		values.put(JobColumns.COMPANY_NAME, map.get("company_name"));
		values.put(JobColumns.CREATE_TIME, map.get("create_time"));
		values.put(JobColumns.NAME, map.get("name"));
		values.put(JobColumns.NUM, map.get("num"));
		values.put(JobColumns.TYPE, map.get("type"));
		values.put(JobColumns.COMPANY_ID, map.get("company_id"));
		values.put(JobColumns.COMPANY_POSITION, map.get("position"));
		values.put(JobColumns.COMPANY_LEVEL, map.get("company_level"));
		values.put(JobColumns.LOCATION, location_list);
		Evturi = context.getContentResolver().update(JobColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找工作列表中的数据
	 */
	public ArrayList<HashMap<String, String>> getJobInfo(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		try {
			// String selection = HotWorkColumns.Hot_Id + "= '"+ HotId + "'";
			curtmp = context.getContentResolver().query(JobColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, String> map = new HashMap<String, String>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.Hot_Id));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NAME));
					String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CHARGE));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.TYPE));
					String num = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NUM));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CREATE_TIME));
					String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_NAME));
					String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ADD));
					String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_LEVEL));
					String company_position = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_POSITION));
					String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ID));

					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("charge", charge);
					map.put("type", type);
					map.put("num", num);
					map.put("create_time", create_time);
					map.put("company_name", company_name);
					map.put("company_add", company_add);
					map.put("company_level", company_level);
					map.put("position", company_position);
					map.put("company_id", company_id);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 查找工作列表中的数据
	 */
	public ArrayList<HashMap<String, Object>> getJobInfo1(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		try {
			String order = JobColumns.LOCATION + " asc";
			// String selection = HotWorkColumns.Hot_Id + "= '"+ HotId + "'";
			curtmp = context.getContentResolver().query(JobColumns.CONTENT_URI, null, null, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.Hot_Id));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NAME));
					String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CHARGE));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.TYPE));
					String num = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NUM));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CREATE_TIME));
					String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_NAME));
					String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ADD));
					String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_LEVEL));
					String company_position = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_POSITION));
					String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ID));
					int location = curtmp.getInt(curtmp.getColumnIndexOrThrow(JobColumns.LOCATION));

					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("charge", charge);
					map.put("type", type);
					map.put("num", num);
					map.put("create_time", create_time);
					map.put("company_name", company_name);
					map.put("company_add", company_add);
					map.put("company_level", company_level);
					map.put("position", company_position);
					map.put("company_id", company_id);
					map.put("location", location);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 筛选工作表中的数据(通过距离)
	 */
	public ArrayList<HashMap<String, Object>> filterJobInfo(Context context, int kilometre, String flag) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String selection = null;
		try {
			String order = JobColumns.LOCATION + " asc";
			if (flag.equals("rest")) {
				selection = JobColumns.LOCATION + ">" + kilometre;
			} else {
				selection = JobColumns.LOCATION + "<" + kilometre;
				// selection = JobColumns.LOCATION + " < 16000";
			}
			// selection = JobColumns.LOCATION + " < 16000";
			curtmp = context.getContentResolver().query(JobColumns.CONTENT_URI, null, selection, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.Hot_Id));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NAME));
					String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CHARGE));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.TYPE));
					String num = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NUM));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CREATE_TIME));
					String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_NAME));
					String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ADD));
					String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_LEVEL));
					String company_position = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_POSITION));
					String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ID));
					int location = curtmp.getInt(curtmp.getColumnIndexOrThrow(JobColumns.LOCATION));

					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("charge", charge);
					map.put("type", type);
					map.put("num", num);
					map.put("create_time", create_time);
					map.put("company_name", company_name);
					map.put("company_add", company_add);
					map.put("company_level", company_level);
					map.put("position", company_position);
					map.put("company_id", company_id);
					map.put("location", location);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 筛选工作表中的数据(通过职位)
	 */
	public ArrayList<HashMap<String, Object>> filterJobInfobyposition(Context context, String position) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String selection = null;
		try {
			String order = JobColumns.LOCATION + " asc";

			selection = JobColumns.NAME + " = '" + position + "'";
			curtmp = context.getContentResolver().query(JobColumns.CONTENT_URI, null, selection, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.Hot_Id));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NAME));
					String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CHARGE));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.TYPE));
					String num = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NUM));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CREATE_TIME));
					String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_NAME));
					String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ADD));
					String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_LEVEL));
					String company_position = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_POSITION));
					String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ID));
					int location = curtmp.getInt(curtmp.getColumnIndexOrThrow(JobColumns.LOCATION));

					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("charge", charge);
					map.put("type", type);
					map.put("num", num);
					map.put("create_time", create_time);
					map.put("company_name", company_name);
					map.put("company_add", company_add);
					map.put("company_level", company_level);
					map.put("position", company_position);
					map.put("company_id", company_id);
					map.put("location", location);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 筛选工作表中的数据(通过时间)
	 */
	public ArrayList<HashMap<String, Object>> filterJobInfobytime(Context context, String time_flag) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String selection = null;
		try {
			String order = JobColumns.LOCATION + " asc";

			String current_time = Utils.getCurrentTime();

			String starttime = null;
			Date date = new Date();// 取时间
			Calendar calendar = new GregorianCalendar();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (time_flag.equals("1")) {
				calendar.setTime(date);
				calendar.add(calendar.DATE, -1);// -1表示往前推1天 （如：今天是2015-3-27
												// 那么退三天是2015-3-26）
				date = calendar.getTime(); // 这个时间就是前三天
				starttime = formatter.format(date);

			} else if (time_flag.equals("3")) {
				calendar.setTime(date);
				calendar.add(calendar.DATE, -3);// -3表示往前推3天 （如：今天是2015-3-27
												// 那么退三天是2015-3-24）
				date = calendar.getTime(); // 这个时间就是前三天
				starttime = formatter.format(date);

			} else if (time_flag.equals("7")) {
				calendar.setTime(date);
				calendar.add(calendar.DATE, -7);// -3表示往前推3天 （如：今天是2015-3-27
												// 那么退三天是2015-3-24）
				date = calendar.getTime(); // 这个时间就是前三天
				starttime = formatter.format(date);
			}

			selection = JobColumns.CREATE_TIME + " >'" + starttime + "' and " + JobColumns.CREATE_TIME + " <'" + current_time + "'";
			curtmp = context.getContentResolver().query(JobColumns.CONTENT_URI, null, selection, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.Hot_Id));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NAME));
					String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CHARGE));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.TYPE));
					String num = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.NUM));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.CREATE_TIME));
					String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_NAME));
					String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ADD));
					String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_LEVEL));
					String company_position = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_POSITION));
					String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobColumns.COMPANY_ID));
					int location = curtmp.getInt(curtmp.getColumnIndexOrThrow(JobColumns.LOCATION));

					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("charge", charge);
					map.put("type", type);
					map.put("num", num);
					map.put("create_time", create_time);
					map.put("company_name", company_name);
					map.put("company_add", company_add);
					map.put("company_level", company_level);
					map.put("position", company_position);
					map.put("company_id", company_id);
					map.put("location", location);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 清空工作列表
	 */
	public int ClearJobInfo() {
		int count = context.getContentResolver().delete(JobColumns.CONTENT_URI, null, null);
		return count;
	}

	/**
	 * 向职位简历列表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddAllResumeList(Context context, AllResumeStruct struct) {
		ContentValues values = new ContentValues();
		values.put(AllResumeColumns.RID, struct.mRid);
		values.put(AllResumeColumns.RJOB, struct.mRjob);
		values.put(AllResumeColumns.RADD, struct.mRadd);
		values.put(AllResumeColumns.RSTARTTIME, struct.mRstart_Time);
		values.put(AllResumeColumns.RENDTIME, struct.mRend_Time);
		values.put(AllResumeColumns.RTYPE, struct.mRtype);
		values.put(AllResumeColumns.RJOBTYPE, struct.mRJobType);
		values.put(AllResumeColumns.RNAME, struct.mRname);
		values.put(AllResumeColumns.RSEX, struct.mRsex);
		values.put(AllResumeColumns.RHEIGHT, struct.mRheight);
		values.put(AllResumeColumns.RCALL, struct.mRcall);
		values.put(AllResumeColumns.REXPERIENCE, struct.mRexperience);
		values.put(AllResumeColumns.RUSERID, struct.mUserid);

		Uri Evturi = context.getContentResolver().insert(AllResumeColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 职位简历列表中是否存在某条数据
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistAllResumeList(String Rid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = AllResumeColumns.RID + "= '" + Rid + "'";
			curtmp = context.getContentResolver().query(AllResumeColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 获取企业发布职位信息列表
	 */
	public AllResumeStruct getAllResumeInfoListByrid(Context context, int rid) {
		Cursor curtmp = null;
		AllResumeStruct struct = new AllResumeStruct();
		try {
			String selection = AllResumeColumns.RID + "= '" + rid + "'";
			curtmp = context.getContentResolver().query(AllResumeColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					struct.mRid = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RID));
					struct.mRjob = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RJOB));
					struct.mRadd = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RADD));
					struct.mRstart_Time = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RSTARTTIME));
					struct.mRend_Time = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RENDTIME));
					struct.mRtype = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RTYPE));
					struct.mRJobType = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RJOBTYPE));
					struct.mRname = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RNAME));
					struct.mRsex = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RSEX));
					struct.mRheight = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RHEIGHT));
					struct.mRcall = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RCALL));
					struct.mRexperience = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.REXPERIENCE));
					struct.mUserid = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RUSERID));
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return struct;
	}

	/**
	 * 更新职位简历列表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateAllResumeList(Context context, AllResumeStruct struct, String RId) {
		int Evturi = 0;
		String selection = AllResumeColumns.RID + "= '" + RId + "'";
		ContentValues values = new ContentValues();
		values.put(AllResumeColumns.RID, struct.mRid);
		values.put(AllResumeColumns.RJOB, struct.mRjob);
		values.put(AllResumeColumns.RADD, struct.mRadd);
		values.put(AllResumeColumns.RSTARTTIME, struct.mRstart_Time);
		values.put(AllResumeColumns.RENDTIME, struct.mRend_Time);
		values.put(AllResumeColumns.RTYPE, struct.mRtype);
		values.put(AllResumeColumns.RJOBTYPE, struct.mRJobType);
		values.put(AllResumeColumns.RNAME, struct.mRname);
		values.put(AllResumeColumns.RSEX, struct.mRsex);
		values.put(AllResumeColumns.RHEIGHT, struct.mRheight);
		values.put(AllResumeColumns.RCALL, struct.mRcall);
		values.put(AllResumeColumns.REXPERIENCE, struct.mRexperience);
		values.put(AllResumeColumns.RUSERID, struct.mUserid);
		Evturi = context.getContentResolver().update(AllResumeColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找职位简历列表中的简洁数据
	 */

	public ArrayList<HashMap<String, String>> getAllResumeList(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		try {
			curtmp = context.getContentResolver().query(AllResumeColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, String> map = new HashMap<String, String>();
					String rid = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RID));
					String rjob = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RJOB));
					String rstarttime = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RSTARTTIME));
					String rendtime = curtmp.getString(curtmp.getColumnIndexOrThrow(AllResumeColumns.RENDTIME));

					map.put("rid", rid);
					map.put("rjob", rjob);
					map.put("rstarttime", rstarttime);
					map.put("rendtime", rendtime);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 清空简历列表
	 */
	public int ClearAllResumeInfo() {
		int count = context.getContentResolver().delete(AllResumeColumns.CONTENT_URI, null, null);
		return count;
	}

	/**
	 * 向职位简历详情表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddResumeInfo(Context context, AllResumeStruct struct) {
		ContentValues values = new ContentValues();
		values.put(ResumeInfoColumns.RID, struct.mRid);
		values.put(ResumeInfoColumns.RJOB, struct.mRjob);
		values.put(ResumeInfoColumns.RADD, struct.mRadd);
		values.put(ResumeInfoColumns.RSTARTTIME, struct.mRstart_Time);
		values.put(ResumeInfoColumns.RENDTIME, struct.mRend_Time);
		values.put(ResumeInfoColumns.RTYPE, struct.mRtype);
		values.put(ResumeInfoColumns.RNAME, struct.mRname);
		values.put(ResumeInfoColumns.RSEX, struct.mRsex);
		values.put(ResumeInfoColumns.RHEIGHT, struct.mRheight);
		values.put(ResumeInfoColumns.RCALL, struct.mRcall);
		values.put(ResumeInfoColumns.REXPERIENCE, struct.mRexperience);
		values.put(ResumeInfoColumns.RUSERID, struct.mUserid);

		Uri Evturi = context.getContentResolver().insert(ResumeInfoColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 判断共用关系的工作是否已经存在
	 * 
	 * @param jobid
	 * @param context
	 * @return
	 */
	public boolean IsExistHiredList(int id, Context context) {
		Cursor curtmp = null;
		try {
			String selection = HiredListColumns.hired_id + "= '" + id + "'";
			curtmp = context.getContentResolver().query(HiredListColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 清空被雇佣的列表
	 * 
	 * @return
	 */
	public int ClearHiredJobList() {
		int count = context.getContentResolver().delete(HiredListColumns.CONTENT_URI, null, null);
		return count;
	}

	/**
	 * 向雇佣关系表中增加数据
	 * 
	 * @param context
	 * @param HiredStruct
	 * @return
	 */
	public long AddHiredInfo(Context context, HiredStruct struct) {
		ContentValues values = new ContentValues();
		values.put(HiredListColumns.hired_id, struct.id);
		values.put(HiredListColumns.job_id, struct.job_id);
		values.put(HiredListColumns.r_id, struct.r_id);
		values.put(HiredListColumns.engage, struct.engage);
		values.put(HiredListColumns.create_time, struct.create_time);
		values.put(HiredListColumns.hire_name, struct.hire_name);
		Uri Evturi = context.getContentResolver().insert(HiredListColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 更新职位简历详情表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateHiredInfo(Context context, HiredStruct struct, String RId) {
		int Evturi = 0;
		String selection = HiredListColumns.hired_id + "= '" + RId + "'";
		ContentValues values = new ContentValues();
		values.put(HiredListColumns.hired_id, struct.id);
		values.put(HiredListColumns.job_id, struct.job_id);
		values.put(HiredListColumns.r_id, struct.r_id);
		values.put(HiredListColumns.engage, struct.engage);
		values.put(HiredListColumns.create_time, struct.create_time);
		values.put(HiredListColumns.hire_name, struct.hire_name);
		Evturi = context.getContentResolver().update(HiredListColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找雇佣工作列表
	 */
	public ArrayList<HiredStruct> getHiredJobList(Context context, int type) {
		Cursor curtmp = null;
		ArrayList<HiredStruct> list = new ArrayList<HiredStruct>();
		HiredStruct struct;
		try {
			switch (type) {
			case -1:
				curtmp = context.getContentResolver().query(HiredListColumns.CONTENT_URI, null, null, null, null);
				break;
			case 1:
				String selection = HiredListColumns.engage + " = '已申请'";
				curtmp = context.getContentResolver().query(HiredListColumns.CONTENT_URI, null, selection, null, null);
				break;
			case 2:
				selection = HiredListColumns.engage + " = '已雇佣'";
				curtmp = context.getContentResolver().query(HiredListColumns.CONTENT_URI, null, selection, null, null);
				break;
			case 3:
				selection = HiredListColumns.engage + " = '进行中'";
				curtmp = context.getContentResolver().query(HiredListColumns.CONTENT_URI, null, selection, null, null);
				break;
			case 4:
				selection = HiredListColumns.engage + " = '已完成'";
				curtmp = context.getContentResolver().query(HiredListColumns.CONTENT_URI, null, selection, null, null);
				break;

			}
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					struct = new HiredStruct();
					struct.id = curtmp.getInt(curtmp.getColumnIndexOrThrow(HiredListColumns.hired_id));
					struct.job_id = curtmp.getInt(curtmp.getColumnIndexOrThrow(HiredListColumns.job_id));
					struct.r_id = curtmp.getInt(curtmp.getColumnIndexOrThrow(HiredListColumns.r_id));
					struct.engage = curtmp.getString(curtmp.getColumnIndexOrThrow(HiredListColumns.engage));
					struct.create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HiredListColumns.create_time));
					struct.hire_name = curtmp.getString(curtmp.getColumnIndexOrThrow(HiredListColumns.hire_name));
					list.add(struct);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 职位简历详情表中是否存在某条数据
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistResumeInfo(String Rid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = ResumeInfoColumns.RID + "= '" + Rid + "'";
			curtmp = context.getContentResolver().query(ResumeInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 更新职位简历详情表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateResumeInfo(Context context, AllResumeStruct struct, String RId) {
		int Evturi = 0;
		String selection = ResumeInfoColumns.RID + "= '" + RId + "'";
		ContentValues values = new ContentValues();
		values.put(ResumeInfoColumns.RID, struct.mRid);
		values.put(ResumeInfoColumns.RJOB, struct.mRjob);
		values.put(ResumeInfoColumns.RADD, struct.mRadd);
		values.put(ResumeInfoColumns.RSTARTTIME, struct.mRstart_Time);
		values.put(ResumeInfoColumns.RENDTIME, struct.mRend_Time);
		values.put(ResumeInfoColumns.RTYPE, struct.mRtype);
		values.put(ResumeInfoColumns.RNAME, struct.mRname);
		values.put(ResumeInfoColumns.RSEX, struct.mRsex);
		values.put(ResumeInfoColumns.RHEIGHT, struct.mRheight);
		values.put(ResumeInfoColumns.RCALL, struct.mRcall);
		values.put(ResumeInfoColumns.REXPERIENCE, struct.mRexperience);
		values.put(ResumeInfoColumns.RUSERID, struct.mUserid);
		Evturi = context.getContentResolver().update(ResumeInfoColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找职位详情表中的数据
	 */

	public HashMap<String, String> getResumeInfo(Context context, String rid) {
		Cursor curtmp = null;

		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String selection = ResumeInfoColumns.RID + "= '" + rid + "'";
			curtmp = context.getContentResolver().query(ResumeInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {

					String rjob = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RJOB));
					String rstarttime = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RSTARTTIME));
					String rendtime = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RENDTIME));
					String radd = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RADD));
					String rtype = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RTYPE));
					String rname = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RNAME));
					String rsex = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RSEX));
					String rheight = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RHEIGHT));
					String rcall = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RCALL));
					String rexperence = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.REXPERIENCE));
					String ruserid = curtmp.getString(curtmp.getColumnIndexOrThrow(ResumeInfoColumns.RUSERID));

					map.put("rid", rid);
					map.put("rjob", rjob);
					map.put("rstarttime", rstarttime);
					map.put("rendtime", rendtime);
					map.put("radd", radd);
					map.put("rtype", rtype);
					map.put("rname", rname);
					map.put("rsex", rsex);
					map.put("rheight", rheight);
					map.put("rcall", rcall);
					map.put("rexperence", rexperence);
					map.put("ruserid", ruserid);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return map;
	}

	/**
	 * 删除一条简历
	 * 
	 * @param rid
	 * @return
	 */
	public int removeOneResume(int rid) {
		String selection = AllResumeColumns.RID + "= '" + rid + "'";
		int count = context.getContentResolver().delete(AllResumeColumns.CONTENT_URI, selection, null);
		return count;
	}

	/**
	 * 向职位详情表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddJobDetailInfo(Context context, JobInfoStruct struct) {
		ContentValues values = new ContentValues();

		values.put(JobInfoColumns.Hot_Id, struct.mId);
		values.put(JobInfoColumns.Company_Id, struct.mUserId);
		values.put(JobInfoColumns.CHARGE, struct.mCharge);
		values.put(JobInfoColumns.COMPANY_ADD, struct.mCompany_Add);
		values.put(JobInfoColumns.COMPANY_NAME, struct.mCompany_Name);
		values.put(JobInfoColumns.CREATE_TIME, struct.mCreate_time);
		values.put(JobInfoColumns.NAME, struct.mName);
		values.put(JobInfoColumns.NUM, struct.mNum);
		values.put(JobInfoColumns.TYPE, struct.mType);
		values.put(JobInfoColumns.COMPANY_LEVEL, struct.mCompany_Level);
		values.put(JobInfoColumns.Present, struct.mPresent);
		values.put(JobInfoColumns.Require, struct.mRequire);
		values.put(JobInfoColumns.Condition, struct.mCondition);
		values.put(JobInfoColumns.Thumb, struct.mThumb);
		values.put(JobInfoColumns.StartTime, struct.mStartTime);
		values.put(JobInfoColumns.EndTime, struct.mEndTime);
		values.put(JobInfoColumns.Company_Status, struct.mCompany_status);
		values.put(JobInfoColumns.Company_Code, struct.mCompany_Code);

		Uri Evturi = context.getContentResolver().insert(JobInfoColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 职位详情表中是否存在某条数据
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistJobDetailInfo(String hotId, Context context) {
		Cursor curtmp = null;
		try {
			String selection = JobInfoColumns.Hot_Id + "= '" + hotId + "'";
			curtmp = context.getContentResolver().query(JobInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 查看职位详情表中一共几条数据
	 * 
	 * @param context
	 * @return
	 */
	public int getJobDeatilInfoNum(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(JobInfoColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 更新职位详情表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateJobDetailInfo(Context context, JobInfoStruct struct, String HotId) {
		int Evturi = 0;
		String selection = JobInfoColumns.Hot_Id + "= '" + HotId + "'";
		ContentValues values = new ContentValues();
		values.put(JobInfoColumns.Hot_Id, struct.mId);
		values.put(JobInfoColumns.Company_Id, struct.mUserId);
		values.put(JobInfoColumns.CHARGE, struct.mCharge);
		values.put(JobInfoColumns.COMPANY_ADD, struct.mCompany_Add);
		values.put(JobInfoColumns.COMPANY_NAME, struct.mCompany_Name);
		values.put(JobInfoColumns.CREATE_TIME, struct.mCreate_time);
		values.put(JobInfoColumns.NAME, struct.mName);
		values.put(JobInfoColumns.NUM, struct.mNum);
		values.put(JobInfoColumns.TYPE, struct.mType);
		values.put(JobInfoColumns.COMPANY_LEVEL, struct.mCompany_Level);
		values.put(JobInfoColumns.Present, struct.mPresent);
		values.put(JobInfoColumns.Require, struct.mRequire);
		values.put(JobInfoColumns.Condition, struct.mCondition);
		values.put(JobInfoColumns.Thumb, struct.mThumb);
		values.put(JobInfoColumns.StartTime, struct.mStartTime);
		values.put(JobInfoColumns.EndTime, struct.mEndTime);
		values.put(JobInfoColumns.Company_Status, struct.mCompany_status);
		values.put(JobInfoColumns.Company_Code, struct.mCompany_Code);
		Evturi = context.getContentResolver().update(JobInfoColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找职位详情表中的单条数据
	 */
	public HashMap<String, String> getJobDetailInfo(Context context, String id) {
		Cursor curtmp = null;
		HashMap<String, String> map = new HashMap<String, String>();

		try {
			String selection = JobInfoColumns.Hot_Id + "= '" + id + "'";
			curtmp = context.getContentResolver().query(JobInfoColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				curtmp.moveToNext();
				String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Hot_Id));
				String company_id = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Company_Id));
				String name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.NAME));
				String charge = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.CHARGE));
				String type = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.TYPE));
				String num = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.NUM));
				String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.CREATE_TIME));
				String present = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Present));
				String require = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Require));
				String condition = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Condition));
				String thumb = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Thumb));
				String starttime = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.StartTime));
				String endtime = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.EndTime));
				String company_status = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Company_Status));
				String company_code = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.Company_Code));
				String company_name = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.COMPANY_NAME));
				String company_add = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.COMPANY_ADD));
				String company_level = curtmp.getString(curtmp.getColumnIndexOrThrow(JobInfoColumns.COMPANY_LEVEL));

				map.put("hot_id", hot_id);
				map.put("company_id", company_id);
				map.put("name", name);
				map.put("charge", charge);
				map.put("type", type);
				map.put("num", num);
				map.put("create_time", create_time);
				map.put("present", present);
				map.put("require", require);
				map.put("condition", condition);
				map.put("thumb", thumb);
				map.put("starttime", starttime);
				map.put("endtime", endtime);
				map.put("company_status", company_status);
				map.put("company_code", company_code);
				map.put("company_name", company_name);
				map.put("company_add", company_add);
				map.put("company_level", company_level);
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return map;
	}

	/**
	 * 获取职位详情中的图片示例的url
	 */
	public ArrayList<String> getImages(String path) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			JSONArray array = new JSONArray(path);
			for (int i = 0; i < array.length(); i++) {
				list.add(array.getString(i));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 向评论表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddCommentInfo(Context context, CommentStruct struct) {
		ContentValues values = new ContentValues();
		values.put(CommentColumns.ID, struct.mId);
		values.put(CommentColumns.CREATE_TIME, struct.mCreateTime);
		values.put(CommentColumns.BUSINESS_ID, struct.mBusiness_Id);
		values.put(CommentColumns.PRESTIGE, struct.mPrestige);
		values.put(CommentColumns.COMMENT, struct.mComment);
		values.put(CommentColumns.PERSONAL_ID, struct.mPersonal_Id);
		values.put(CommentColumns.NAME, struct.mName);

		Uri Evturi = context.getContentResolver().insert(CommentColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 评论表中是否存在某条数据
	 * 
	 * @param name
	 * @param context
	 * @return
	 */
	public boolean IsExistCommentInfo(String Id, Context context) {
		Cursor curtmp = null;
		try {
			String selection = CommentColumns.ID + "= '" + Id + "'";
			curtmp = context.getContentResolver().query(CommentColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 更新评论表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateComment(Context context, CommentStruct struct, String Id) {
		int Evturi = 0;
		String selection = CommentColumns.ID + "= '" + Id + "'";
		ContentValues values = new ContentValues();
		values.put(CommentColumns.ID, struct.mId);
		values.put(CommentColumns.CREATE_TIME, struct.mCreateTime);
		values.put(CommentColumns.BUSINESS_ID, struct.mBusiness_Id);
		values.put(CommentColumns.PRESTIGE, struct.mPrestige);
		values.put(CommentColumns.COMMENT, struct.mComment);
		values.put(CommentColumns.PERSONAL_ID, struct.mPersonal_Id);
		values.put(CommentColumns.NAME, struct.mName);
		Evturi = context.getContentResolver().update(CommentColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查看评论表中一共几条数据
	 * 
	 * @param context
	 * @return
	 */
	public int getCommentNum(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(CommentColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 查找评论表中的所有数据
	 */
	public ArrayList<HashMap<String, String>> getComments(Context context, String personal_id1, String flag) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			String selection = "";
			if (flag.equals("company"))

				selection = CommentColumns.PERSONAL_ID + " = '" + personal_id1 + "'";
			else {
				selection = CommentColumns.BUSINESS_ID + " = '" + personal_id1 + "'";

			}
			curtmp = context.getContentResolver().query(CommentColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {

					HashMap<String, String> map = new HashMap<String, String>();
					String business_id = curtmp.getString(curtmp.getColumnIndexOrThrow(CommentColumns.BUSINESS_ID));
					String comment = curtmp.getString(curtmp.getColumnIndexOrThrow(CommentColumns.COMMENT));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(CommentColumns.CREATE_TIME));
					String id = curtmp.getString(curtmp.getColumnIndexOrThrow(CommentColumns.ID));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(CommentColumns.NAME));
					String personal_id = curtmp.getString(curtmp.getColumnIndexOrThrow(CommentColumns.PERSONAL_ID));
					String prestige = curtmp.getString(curtmp.getColumnIndexOrThrow(CommentColumns.PRESTIGE));

					map.put("business_id", business_id);
					map.put("comment", comment);
					map.put("id", id);
					map.put("name", name);
					map.put("personal_id", personal_id);
					map.put("prestige", prestige);
					map.put("create_time", create_time);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/*********************/
	/**
	 * 向求职信息表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddPublicCustomerInfo(Context context, PublicResumeStruct struct) {
		ContentValues values = new ContentValues();
		values.put(PublicResumeColumns.mRheight, struct.mRheight);
		values.put(PublicResumeColumns.mRtype, struct.mRtype);
		values.put(PublicResumeColumns.mRname, struct.mRname);
		values.put(PublicResumeColumns.mRsex, struct.mRsex);
		values.put(PublicResumeColumns.mRstart_time, struct.mRstart_time);
		values.put(PublicResumeColumns.mRexperience, struct.mRexperience);
		values.put(PublicResumeColumns.mRadd, struct.mRadd);
		values.put(PublicResumeColumns.mRjob, struct.mRjob);
		values.put(PublicResumeColumns.mRid, struct.mRid);
		values.put(PublicResumeColumns.mRuser_id, struct.mRuser_id);
		values.put(PublicResumeColumns.mRcall, struct.mRcall);
		values.put(PublicResumeColumns.mRpublic, struct.mRpublic);
		values.put(PublicResumeColumns.mRposition, struct.mPosition);
		values.put(PublicResumeColumns.mRrend_time, struct.mRrend_time);
		values.put(PublicResumeColumns.mRcreate_time, struct.mRcreate_time);

		Uri Evturi = context.getContentResolver().insert(PublicResumeColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 向求职信息表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddPublicCustomerInfo1(Context context, HashMap<String, String> map, int distance) {
		ContentValues values = new ContentValues();
		values.put(PublicResumeColumns.mRheight, map.get("height"));
		values.put(PublicResumeColumns.mRtype, map.get("type"));
		values.put(PublicResumeColumns.mRname, map.get("name"));
		values.put(PublicResumeColumns.mRsex, map.get("sex"));
		values.put(PublicResumeColumns.mRstart_time, map.get("start_time"));
		values.put(PublicResumeColumns.mRexperience, map.get("experence"));
		values.put(PublicResumeColumns.mRadd, map.get("addr"));
		values.put(PublicResumeColumns.mRjob, map.get("job"));
		values.put(PublicResumeColumns.mRid, map.get("rid"));
		values.put(PublicResumeColumns.mRuser_id, map.get("user_id"));
		values.put(PublicResumeColumns.mRcall, map.get("call"));
		values.put(PublicResumeColumns.mRpublic, map.get("public"));
		values.put(PublicResumeColumns.mRrend_time, map.get("end_time"));
		values.put(PublicResumeColumns.mRposition, map.get("position"));
		values.put(PublicResumeColumns.mRcreate_time, map.get("create_time"));
		values.put(PublicResumeColumns.Location, distance);

		Uri Evturi = context.getContentResolver().insert(PublicResumeColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 求职信息表中是否存在某条数据
	 * 
	 * @param com_userid
	 *            简历id
	 * @param context
	 * @return
	 */
	public boolean IsExistPublicResumeInfo(String rid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = PublicResumeColumns.mRid + "= '" + rid + "'";
			curtmp = context.getContentResolver().query(PublicResumeColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 更新求职信息表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdatePublicResumeInfo(Context context, PublicResumeStruct struct, String rid) {
		int Evturi = 0;
		String selection = PublicResumeColumns.mRid + "= '" + rid + "'";
		ContentValues values = new ContentValues();
		values.put(PublicResumeColumns.mRheight, struct.mRheight);
		values.put(PublicResumeColumns.mRtype, struct.mRtype);
		values.put(PublicResumeColumns.mRname, struct.mRname);
		values.put(PublicResumeColumns.mRsex, struct.mRsex);
		values.put(PublicResumeColumns.mRstart_time, struct.mRstart_time);
		values.put(PublicResumeColumns.mRexperience, struct.mRexperience);
		values.put(PublicResumeColumns.mRadd, struct.mRadd);
		values.put(PublicResumeColumns.mRjob, struct.mRjob);
		values.put(PublicResumeColumns.mRid, struct.mRid);
		values.put(PublicResumeColumns.mRuser_id, struct.mRuser_id);
		values.put(PublicResumeColumns.mRcall, struct.mRcall);
		values.put(PublicResumeColumns.mRpublic, struct.mRpublic);
		values.put(PublicResumeColumns.mRrend_time, struct.mRrend_time);
		values.put(PublicResumeColumns.mRposition, struct.mPosition);
		values.put(PublicResumeColumns.mRcreate_time, struct.mRcreate_time);
		Evturi = context.getContentResolver().update(PublicResumeColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 更新求职信息表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdatePublicResumeInfo1(Context context, HashMap<String, String> map, int distance, String rid) {
		int Evturi = 0;
		String selection = PublicResumeColumns.mRid + "= '" + rid + "'";
		ContentValues values = new ContentValues();

		values.put(PublicResumeColumns.mRheight, map.get("height"));
		values.put(PublicResumeColumns.mRtype, map.get("type"));
		values.put(PublicResumeColumns.mRname, map.get("name"));
		values.put(PublicResumeColumns.mRsex, map.get("sex"));
		values.put(PublicResumeColumns.mRstart_time, map.get("start_time"));
		values.put(PublicResumeColumns.mRexperience, map.get("experence"));
		values.put(PublicResumeColumns.mRadd, map.get("addr"));
		values.put(PublicResumeColumns.mRjob, map.get("job"));
		values.put(PublicResumeColumns.mRid, map.get("rid"));
		values.put(PublicResumeColumns.mRuser_id, map.get("user_id"));
		values.put(PublicResumeColumns.mRcall, map.get("call"));
		values.put(PublicResumeColumns.mRpublic, map.get("public"));
		values.put(PublicResumeColumns.mRrend_time, map.get("end_time"));
		values.put(PublicResumeColumns.mRposition, map.get("position"));
		values.put(PublicResumeColumns.mRcreate_time, map.get("create_time"));
		values.put(PublicResumeColumns.Location, distance);
		Evturi = context.getContentResolver().update(PublicResumeColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查看工作表中一共几条数据
	 * 
	 * @param context
	 * @return
	 */
	public int getPublicResumeCount(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(PublicResumeColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 查找求职信息表中的所有数据
	 */
	public ArrayList<HashMap<String, String>> getPublicReusmeInfo(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			curtmp = context.getContentResolver().query(PublicResumeColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {

					HashMap<String, String> map = new HashMap<String, String>();
					String height = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRheight));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRtype));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRname));
					String sex = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRsex));
					String start_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRstart_time));
					String experience = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRexperience));
					String addr = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRadd));
					String job = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRjob));
					String rid = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRid));
					String user_id = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRuser_id));
					String call = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRcall));
					String mpublic = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRpublic));
					String mend_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRrend_time));
					String mcreate_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRcreate_time));
					String mposition = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRposition));
					map.put("height", height);
					map.put("type", type);
					map.put("name", name);
					map.put("sex", sex);
					map.put("start_time", start_time);
					map.put("experence", experience);
					map.put("addr", addr);
					map.put("job", job);
					map.put("rid", rid);
					map.put("user_id", user_id);
					map.put("call", call);
					map.put("public", mpublic);
					map.put("end_time", mend_time);
					map.put("create_time", mcreate_time);
					map.put("position", mposition);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 查找求职信息表中的所有数据
	 */
	public ArrayList<HashMap<String, Object>> getPublicReusmeInfo1(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try {
			String order = PublicResumeColumns.Location + " asc";
			curtmp = context.getContentResolver().query(PublicResumeColumns.CONTENT_URI, null, null, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {

					HashMap<String, Object> map = new HashMap<String, Object>();
					String height = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRheight));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRtype));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRname));
					String sex = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRsex));
					String start_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRstart_time));
					String experience = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRexperience));
					String addr = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRadd));
					String job = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRjob));
					String rid = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRid));
					String user_id = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRuser_id));
					String call = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRcall));
					String mpublic = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRpublic));
					String mend_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRrend_time));
					String mcreate_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRcreate_time));
					String mposition = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRposition));
					int location = curtmp.getInt(curtmp.getColumnIndexOrThrow(PublicResumeColumns.Location));
					map.put("height", height);
					map.put("type", type);
					map.put("name", name);
					map.put("sex", sex);
					map.put("start_time", start_time);
					map.put("experence", experience);
					map.put("addr", addr);
					map.put("job", job);
					map.put("rid", rid);
					map.put("user_id", user_id);
					map.put("call", call);
					map.put("public", mpublic);
					map.put("end_time", mend_time);
					map.put("create_time", mcreate_time);
					map.put("position", mposition);
					map.put("location", location);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 查找求职信息表中的数据（按照距离查找）
	 */
	public ArrayList<HashMap<String, Object>> getPublicReusmeInfobylocation(Context context, int kilometre, String flag) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		String selection = null;
		try {
			String order = PublicResumeColumns.Location + " asc";
			if (flag.equals("rest")) {
				selection = PublicResumeColumns.Location + ">" + kilometre;
			} else {
				selection = PublicResumeColumns.Location + "<" + kilometre;
			}
			curtmp = context.getContentResolver().query(PublicResumeColumns.CONTENT_URI, null, selection, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {

					HashMap<String, Object> map = new HashMap<String, Object>();
					String height = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRheight));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRtype));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRname));
					String sex = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRsex));
					String start_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRstart_time));
					String experience = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRexperience));
					String addr = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRadd));
					String job = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRjob));
					String rid = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRid));
					String user_id = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRuser_id));
					String call = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRcall));
					String mpublic = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRpublic));
					String mend_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRrend_time));
					String mcreate_time = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRcreate_time));
					String mposition = curtmp.getString(curtmp.getColumnIndexOrThrow(PublicResumeColumns.mRposition));
					int location = curtmp.getInt(curtmp.getColumnIndexOrThrow(PublicResumeColumns.Location));
					map.put("height", height);
					map.put("type", type);
					map.put("name", name);
					map.put("sex", sex);
					map.put("start_time", start_time);
					map.put("experence", experience);
					map.put("addr", addr);
					map.put("job", job);
					map.put("rid", rid);
					map.put("user_id", user_id);
					map.put("call", call);
					map.put("public", mpublic);
					map.put("end_time", mend_time);
					map.put("create_time", mcreate_time);
					map.put("position", mposition);
					map.put("location", location);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 清空求职信息列表
	 * 
	 * @return
	 */
	public int ClearPublicResumeList() {
		int count = context.getContentResolver().delete(PublicResumeColumns.CONTENT_URI, null, null);
		return count;
	}

	/*********************/

	/**
	 * 向求职者推荐表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddHotSeekerInfo(Context context, HotSeekerStruct struct) {
		ContentValues values = new ContentValues();
		values.put(HotSeekerColumns.RHEIGHT, struct.mRheight);
		values.put(HotSeekerColumns.RTYPE, struct.mRtype);
		values.put(HotSeekerColumns.RNAME, struct.mRname);
		values.put(HotSeekerColumns.RSEX, struct.mRsex);
		values.put(HotSeekerColumns.RSTARTTIME, struct.mRstart_time);
		values.put(HotSeekerColumns.REXPERIENCE, struct.mRexperience);
		values.put(HotSeekerColumns.RADD, struct.mRadd);
		values.put(HotSeekerColumns.RJOB, struct.mRjob);
		values.put(HotSeekerColumns.RID, struct.mRid);
		values.put(HotSeekerColumns.POSITION, struct.mPosition);
		values.put(HotSeekerColumns.RUSERID, struct.mUser_id);
		values.put(HotSeekerColumns.RCALL, struct.mRcall);
		values.put(HotSeekerColumns.RENDTIME, struct.mEnd_time);
		values.put(HotSeekerColumns.RCREATE_TIME, struct.mRcreate_time);

		Uri Evturi = context.getContentResolver().insert(HotSeekerColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 向求职者推荐表中增加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddHotSeekerInfo1(Context context, HashMap<String, String> map, int distance) {

		ContentValues values = new ContentValues();
		values.put(HotSeekerColumns.RHEIGHT, map.get("height"));
		values.put(HotSeekerColumns.RTYPE, map.get("type"));
		values.put(HotSeekerColumns.RNAME, map.get("name"));
		values.put(HotSeekerColumns.RSEX, map.get("sex"));
		values.put(HotSeekerColumns.RSTARTTIME, map.get("start_time"));
		values.put(HotSeekerColumns.REXPERIENCE, map.get("experence"));
		values.put(HotSeekerColumns.RADD, map.get("addr"));
		values.put(HotSeekerColumns.RJOB, map.get("job"));
		values.put(HotSeekerColumns.RID, map.get("rid"));
		values.put(HotSeekerColumns.POSITION, map.get("position"));
		values.put(HotSeekerColumns.RUSERID, map.get("user_id"));
		values.put(HotSeekerColumns.RCALL, map.get("call"));
		values.put(HotSeekerColumns.RENDTIME, map.get("end_time"));
		values.put(HotSeekerColumns.RCREATE_TIME, map.get("create_time"));
		values.put(HotSeekerColumns.LOCATION, distance);

		Uri Evturi = context.getContentResolver().insert(HotSeekerColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 求职信息表中是否存在某条数据
	 * 
	 * @param com_userid
	 *            简历id
	 * @param context
	 * @return
	 */
	public boolean IsExistHotSeekerInfo(String rid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = HotSeekerColumns.RID + "= '" + rid + "'";
			curtmp = context.getContentResolver().query(HotSeekerColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 更新求职信息表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateHotSeekerInfo(Context context, HotSeekerStruct struct, String rid) {
		int Evturi = 0;
		String selection = HotSeekerColumns.RID + "= '" + rid + "'";
		ContentValues values = new ContentValues();
		values.put(HotSeekerColumns.RHEIGHT, struct.mRheight);
		values.put(HotSeekerColumns.RTYPE, struct.mRtype);
		values.put(HotSeekerColumns.RNAME, struct.mRname);
		values.put(HotSeekerColumns.RSEX, struct.mRsex);
		values.put(HotSeekerColumns.RSTARTTIME, struct.mRstart_time);
		values.put(HotSeekerColumns.REXPERIENCE, struct.mRexperience);
		values.put(HotSeekerColumns.RADD, struct.mRadd);
		values.put(HotSeekerColumns.RJOB, struct.mRjob);
		values.put(HotSeekerColumns.RID, struct.mRid);
		values.put(HotSeekerColumns.RUSERID, struct.mUser_id);
		values.put(HotSeekerColumns.RCALL, struct.mRcall);
		values.put(HotSeekerColumns.RENDTIME, struct.mEnd_time);
		values.put(HotSeekerColumns.RCREATE_TIME, struct.mRcreate_time);
		Evturi = context.getContentResolver().update(HotSeekerColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 更新求职信息表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateHotSeekerInfo1(Context context, HashMap<String, String> map, int distance, String rid) {
		int Evturi = 0;
		String selection = HotSeekerColumns.RID + "= '" + rid + "'";
		ContentValues values = new ContentValues();
		values.put(HotSeekerColumns.RHEIGHT, map.get("height"));
		values.put(HotSeekerColumns.RTYPE, map.get("type"));
		values.put(HotSeekerColumns.RNAME, map.get("name"));
		values.put(HotSeekerColumns.RSEX, map.get("sex"));
		values.put(HotSeekerColumns.RSTARTTIME, map.get("start_time"));
		values.put(HotSeekerColumns.REXPERIENCE, map.get("experence"));
		values.put(HotSeekerColumns.RADD, map.get("addr"));
		values.put(HotSeekerColumns.RJOB, map.get("job"));
		values.put(HotSeekerColumns.RID, map.get("rid"));
		values.put(HotSeekerColumns.POSITION, map.get("position"));
		values.put(HotSeekerColumns.RUSERID, map.get("user_id"));
		values.put(HotSeekerColumns.RCALL, map.get("call"));
		values.put(HotSeekerColumns.RENDTIME, map.get("end_time"));
		values.put(HotSeekerColumns.RCREATE_TIME, map.get("create_time"));
		values.put(HotSeekerColumns.LOCATION, distance);
		Evturi = context.getContentResolver().update(HotSeekerColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找求职信息表中的所有数据
	 */
	public ArrayList<HashMap<String, String>> getHotWeekerInfo(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			curtmp = context.getContentResolver().query(HotSeekerColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, String> map = new HashMap<String, String>();
					String rid = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RID));
					String job = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RJOB));
					String addr = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RADD));
					String start_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RSTARTTIME));
					String end_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RENDTIME));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RTYPE));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RNAME));
					String sex = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RSEX));
					String height = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RHEIGHT));
					String call = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RCALL));
					String experience = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.REXPERIENCE));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RCREATE_TIME));
					String userid = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RUSERID));
					String position = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.POSITION));
					map.put("height", height);
					map.put("type", type);
					map.put("name", name);
					map.put("sex", sex);
					map.put("start_time", start_time);
					map.put("experence", experience);
					map.put("addr", addr);
					map.put("job", job);
					map.put("position", position);
					map.put("rid", rid);
					map.put("user_id", userid);
					map.put("call", call);
					map.put("end_time", end_time);
					map.put("create_time", create_time);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 查找求职信息表中的所有数据
	 */
	public ArrayList<HashMap<String, Object>> getHotWeekerInfo1(Context context) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		try {
			String order = HotSeekerColumns.LOCATION + " asc";
			curtmp = context.getContentResolver().query(HotSeekerColumns.CONTENT_URI, null, null, null, order);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String rid = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RID));
					String job = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RJOB));
					String addr = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RADD));
					String start_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RSTARTTIME));
					String end_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RENDTIME));
					String type = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RTYPE));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RNAME));
					String sex = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RSEX));
					String height = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RHEIGHT));
					String call = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RCALL));
					String experience = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.REXPERIENCE));
					String create_time = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RCREATE_TIME));
					String userid = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.RUSERID));
					String position = curtmp.getString(curtmp.getColumnIndexOrThrow(HotSeekerColumns.POSITION));
					int location = curtmp.getInt(curtmp.getColumnIndexOrThrow(HotSeekerColumns.LOCATION));
					map.put("height", height);
					map.put("type", type);
					map.put("name", name);
					map.put("sex", sex);
					map.put("start_time", start_time);
					map.put("experence", experience);
					map.put("addr", addr);
					map.put("job", job);
					map.put("position", position);
					map.put("rid", rid);
					map.put("user_id", userid);
					map.put("call", call);
					map.put("end_time", end_time);
					map.put("create_time", create_time);
					map.put("location", location);
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 向通讯表中添加数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddCommunicationInfo(Context context, Communication_UserStruct struct) {
		ContentValues values = new ContentValues();
		values.put(CommuncationUsersColumns.USERID, struct.userid);
		values.put(CommuncationUsersColumns.COMM_USERID, struct.comm_userid);
		values.put(CommuncationUsersColumns.COMM_HEAD_IMAGE, struct.comm_head_image);

		Uri Evturi = context.getContentResolver().insert(CommuncationUsersColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 判断通讯表中是否存在某条数据
	 * 
	 * @param com_userid
	 *            简历id
	 * @param context
	 * @return
	 */
	public boolean IsExistCommunication(String userid, String comm_userid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = CommuncationUsersColumns.USERID + "= '" + userid + "'" + " and " + CommuncationUsersColumns.COMM_USERID + "= '" + comm_userid + "'";
			curtmp = context.getContentResolver().query(CommuncationUsersColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 更新通讯表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateCommunicationInfo(Context context, Communication_UserStruct struct, String userid, String comm_userid) {
		int Evturi = 0;
		String selection = CommuncationUsersColumns.USERID + "= '" + userid + "'" + " and " + CommuncationUsersColumns.COMM_USERID + " = '" + comm_userid + "'";
		ContentValues values = new ContentValues();
		values.put(CommuncationUsersColumns.USERID, struct.userid);
		values.put(CommuncationUsersColumns.COMM_USERID, struct.comm_userid);
		values.put(CommuncationUsersColumns.COMM_HEAD_IMAGE, struct.comm_head_image);
		Evturi = context.getContentResolver().update(CommuncationUsersColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;

		}
		return Evturi;
	}

	/**
	 * 查找通讯表中用户id所对应的数据
	 */
	public ArrayList<HashMap<String, String>> getCommUserInfo(Context context, String userid) {
		Cursor curtmp = null;
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			String selection = CommuncationUsersColumns.USERID + "= '" + userid + "'";
			curtmp = context.getContentResolver().query(CommuncationUsersColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					HashMap<String, String> map = new HashMap<String, String>();
					String hot_id = curtmp.getString(curtmp.getColumnIndexOrThrow(CommuncationUsersColumns.USERID));
					String name = curtmp.getString(curtmp.getColumnIndexOrThrow(CommuncationUsersColumns.COMM_USERID));
					String avatar = curtmp.getString(curtmp.getColumnIndexOrThrow(CommuncationUsersColumns.COMM_HEAD_IMAGE));
					map.put("hot_id", hot_id);
					map.put("name", name);
					map.put("avatar", avatar);
					// www.xun
					list.add(map);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 从city.db查找省市区的方法 (level 为1表示省，level 为2表示市，level为3表示区)
	 */
	public ArrayList<String> getCity(int level, String city) {
		ArrayList<String> citys = new ArrayList<String>();
		Cursor cursor = null;
		String sql = null;
		String id = null;
		try {
			SQLiteDatabase cityDb = SQLiteDatabase.openOrCreateDatabase(DB_PATH, null);

			if (level == 1) {
				sql = "select city_Name from city where level = " + level;
			} else if (level == 2) {
				sql = "select Id from city where city_Name = '" + city + "'" + "and level = 1";
				cursor = cityDb.rawQuery(sql, null);
				cursor.moveToFirst();
				id = cursor.getString(cursor.getColumnIndex("Id"));
				sql = "select city_Name from city where level = " + level + " and Id like '%" + id + "%'";
			} else if (level == 3) {
				sql = "select Id from city where city_Name = '" + city + "'" + " and level = 2";
				cursor = cityDb.rawQuery(sql, null);
				cursor.moveToFirst();
				id = cursor.getString(cursor.getColumnIndex("Id"));
				sql = "select city_Name from city where level = " + level + " and  Id like '%" + id + "%'";
			}
			cursor = cityDb.rawQuery(sql, null);
			citys.add("请选择");
			while (cursor.moveToNext()) {
				citys.add(cursor.getString(cursor.getColumnIndex("city_Name")));
			}
			return citys;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

	}

	/**
	 * 添加求助数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddAskInfo(Context context, AskInfoStruct struct) {
		ContentValues values = new ContentValues();
		values.put(AskListColumns.askid, struct.mAskid);
		values.put(AskListColumns.userid, struct.mUserid);
		values.put(AskListColumns.name, struct.mName);
		values.put(AskListColumns.call, struct.mCall);
		values.put(AskListColumns.content, struct.mContent);
		values.put(AskListColumns.theme, struct.mTheme);
		values.put(AskListColumns.time, struct.mCreateTime);
		values.put(AskListColumns.image, struct.mImage);
		Uri Evturi = context.getContentResolver().insert(AskListColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 更新职位详情表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateAskInfo(Context context, AskInfoStruct struct) {
		int Evturi = 0;
		String selection = AskListColumns.askid + "= '" + struct.mAskid + "'";
		ContentValues values = new ContentValues();
		values.put(AskListColumns.askid, struct.mAskid);
		values.put(AskListColumns.userid, struct.mUserid);
		values.put(AskListColumns.name, struct.mName);
		values.put(AskListColumns.content, struct.mContent);
		values.put(AskListColumns.theme, struct.mTheme);
		values.put(AskListColumns.time, struct.mCreateTime);
		values.put(AskListColumns.call, struct.mCall);
		values.put(AskListColumns.image, struct.mImage);
		Evturi = context.getContentResolver().update(AskListColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;
		}
		return Evturi;
	}

	/**
	 * 获取求助信息列表
	 */
	public ArrayList<AskInfoStruct> getAskInfoList(Context context) {
		Cursor curtmp = null;
		ArrayList<AskInfoStruct> list = new ArrayList<AskInfoStruct>();
		try {
			curtmp = context.getContentResolver().query(AskListColumns.CONTENT_URI, null, null, null, AskListColumns.DEFAULT_SORT_ORDER);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					AskInfoStruct struct = new AskInfoStruct();
					struct.mAskid = curtmp.getInt(curtmp.getColumnIndexOrThrow(AskListColumns.askid));
					struct.mUserid = curtmp.getInt(curtmp.getColumnIndexOrThrow(AskListColumns.userid));
					struct.mName = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.name));
					struct.mTheme = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.theme));
					struct.mCreateTime = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.time));
					struct.mContent = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.content));
					struct.mCall = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.call));
					struct.mImage = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.image));
					list.add(struct);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 获取求助信息
	 */
	public AskInfoStruct getAskInfoByAskId(Context context, int askid) {
		Cursor curtmp = null;
		AskInfoStruct struct = new AskInfoStruct();
		try {
			String selection = AskListColumns.askid + "= '" + askid + "'";
			curtmp = context.getContentResolver().query(AskListColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					struct.mAskid = curtmp.getInt(curtmp.getColumnIndexOrThrow(AskListColumns.askid));
					struct.mUserid = curtmp.getInt(curtmp.getColumnIndexOrThrow(AskListColumns.userid));
					struct.mName = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.name));
					struct.mTheme = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.theme));
					struct.mCreateTime = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.time));
					struct.mContent = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.content));
					struct.mCall = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.call));
					struct.mImage = curtmp.getString(curtmp.getColumnIndexOrThrow(AskListColumns.image));
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return struct;
	}

	/**
	 * 清空数据库
	 * 
	 * @param mContext
	 * @return
	 */
	public long ClearAskInfo(Context mContext) {
		long count = mContext.getContentResolver().delete(AskListColumns.CONTENT_URI, null, null);
		return count;
	}

	/**
	 * 获取求助信息列表
	 */
	public int getAskInfoCount(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(AskListColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 判断是否存在一条求助信息
	 * 
	 * @param context
	 * @return
	 */
	public boolean IsExistAskInfo(int askid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = AskListColumns.askid + "= '" + askid + "'";
			curtmp = context.getContentResolver().query(AskListColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 添加公益数据
	 * 
	 * @param context
	 * @param map
	 * @return
	 */
	public long AddVolunteerInfo(Context context, VolunteerStruct struct) {
		ContentValues values = new ContentValues();
		values.put(VolunteerColumns.volunteerid, struct.mVolunteerid);
		values.put(VolunteerColumns.volunteertheme, struct.mTheme);
		values.put(VolunteerColumns.volunteercontent, struct.mContent);
		values.put(VolunteerColumns.volunteerthumb, struct.mThumb);
		values.put(VolunteerColumns.volunteercreatetime, struct.mCreateTime);
		Uri Evturi = context.getContentResolver().insert(VolunteerColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 更新公益表表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateVolunteerInfo(Context context, VolunteerStruct struct) {
		int Evturi = 0;
		String selection = VolunteerColumns.volunteerid + "= '" + struct.mVolunteerid + "'";
		ContentValues values = new ContentValues();
		values.put(VolunteerColumns.volunteerid, struct.mVolunteerid);
		values.put(VolunteerColumns.volunteertheme, struct.mTheme);
		values.put(VolunteerColumns.volunteercontent, struct.mContent);
		values.put(VolunteerColumns.volunteerthumb, struct.mThumb);
		values.put(VolunteerColumns.volunteercreatetime, struct.mCreateTime);
		Evturi = context.getContentResolver().update(VolunteerColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;
		}
		return Evturi;
	}

	/**
	 * 清空数据库
	 * 
	 * @param mContext
	 * @return
	 */
	public long ClearVolunteerInfo(Context mContext) {
		long count = mContext.getContentResolver().delete(VolunteerColumns.CONTENT_URI, null, null);
		return count;
	}

	/**
	 * 获取求助信息列表
	 */
	public ArrayList<VolunteerStruct> getVolunteerInfoList(Context context) {
		Cursor curtmp = null;
		ArrayList<VolunteerStruct> list = new ArrayList<VolunteerStruct>();
		try {
			curtmp = context.getContentResolver().query(VolunteerColumns.CONTENT_URI, null, null, null, VolunteerColumns.DEFAULT_SORT_ORDER);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					VolunteerStruct struct = new VolunteerStruct();
					struct.mVolunteerid = curtmp.getInt(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteerid));
					struct.mTheme = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteertheme));
					struct.mThumb = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteerthumb));
					struct.mContent = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteercontent));
					struct.mCreateTime = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteercreatetime));
					list.add(struct);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 获取求助信息列表
	 */
	public VolunteerStruct getVolunteerInfoByVolunteerid(Context context, int vid) {
		Cursor curtmp = null;
		VolunteerStruct struct = new VolunteerStruct();
		try {
			String selection = VolunteerColumns.volunteerid + "= '" + vid + "'";
			curtmp = context.getContentResolver().query(VolunteerColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					struct.mVolunteerid = curtmp.getInt(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteerid));
					struct.mTheme = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteertheme));
					struct.mThumb = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteerthumb));
					struct.mContent = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteercontent));
					struct.mCreateTime = curtmp.getString(curtmp.getColumnIndexOrThrow(VolunteerColumns.volunteercreatetime));
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return struct;
	}

	/**
	 * 获取求助信息列表数量
	 */
	public int getVolunteerInfoCount(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(VolunteerColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 判断是否存在一条公益信息
	 * 
	 * @param context
	 * @return
	 */
	public boolean IsExistVolunteerInfo(int volunteerid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = VolunteerColumns.volunteerid + "= '" + volunteerid + "'";
			curtmp = context.getContentResolver().query(VolunteerColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 添加企业信息
	 * 
	 * @param context
	 * @param struct
	 * @return
	 */
	public long AddCompanyInfo(Context context, CompanyDetailsStruct struct) {
		ContentValues values = new ContentValues();
		values.put(CompanyColumns.CompanyId, struct.mCompanyId);
		values.put(CompanyColumns.userid, struct.muserid);
		values.put(CompanyColumns.CompanyName, struct.mCompanyName);
		values.put(CompanyColumns.CompanyProperty, struct.mModifyProperty);
		values.put(CompanyColumns.CompanyConfirm, struct.mCompanyConfirm);
		values.put(CompanyColumns.CompanyConfirmState, struct.mCompanyConfirmState);
		values.put(CompanyColumns.CompanyPoiNum, struct.mPOI);
		values.put(CompanyColumns.CompanyDetailsPoi, struct.mDetailsPosition);
		values.put(CompanyColumns.CompanyEmail, struct.mCompanyEmail);
		values.put(CompanyColumns.CompanyCall, struct.mCompanyContact);
		values.put(CompanyColumns.CompanyIdentity, struct.mCompanyIdentity);
		Uri Evturi = context.getContentResolver().insert(CompanyColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 更新公益表表
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateCompanyInfo(Context context, CompanyDetailsStruct struct) {
		int Evturi = 0;
		String selection = CompanyColumns.CompanyId + "= '" + struct.mCompanyId + "'";
		ContentValues values = new ContentValues();
		values.put(CompanyColumns.CompanyId, struct.mCompanyId);
		values.put(CompanyColumns.userid, struct.muserid);
		values.put(CompanyColumns.CompanyName, struct.mCompanyName);
		values.put(CompanyColumns.CompanyProperty, struct.mModifyProperty);
		values.put(CompanyColumns.CompanyConfirm, struct.mCompanyConfirm);
		values.put(CompanyColumns.CompanyConfirmState, struct.mCompanyConfirmState);
		values.put(CompanyColumns.CompanyPoiNum, struct.mPOI);
		values.put(CompanyColumns.CompanyDetailsPoi, struct.mDetailsPosition);
		values.put(CompanyColumns.CompanyEmail, struct.mCompanyEmail);
		values.put(CompanyColumns.CompanyCall, struct.mCompanyContact);
		values.put(CompanyColumns.CompanyIdentity, struct.mCompanyIdentity);
		Evturi = context.getContentResolver().update(CompanyColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;
		}
		return Evturi;
	}

	/**
	 * 获取求助信息列表
	 */
	public CompanyDetailsStruct getCompanyInfoList(Context context, int userid) {
		Cursor curtmp = null;
		CompanyDetailsStruct struct = new CompanyDetailsStruct();
		try {
			String selection = CompanyColumns.userid + "= '" + userid + "'";
			curtmp = context.getContentResolver().query(CompanyColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					struct.mCompanyId = curtmp.getInt(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyId));
					struct.muserid = curtmp.getInt(curtmp.getColumnIndexOrThrow(CompanyColumns.userid));
					struct.mCompanyName = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyName));
					struct.mModifyProperty = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyProperty));
					struct.mCompanyConfirm = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyConfirm));
					struct.mPOI = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyPoiNum));
					struct.mDetailsPosition = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyDetailsPoi));
					struct.mModifyProperty = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyProperty));
					struct.mCompanyEmail = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyEmail));
					struct.mCompanyContact = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyCall));
					struct.mCompanyIdentity = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyColumns.CompanyIdentity));

				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return struct;
	}

	/**
	 * 判断是否存在一条公益信息
	 * 
	 * @param context
	 * @return
	 */
	public boolean IsExistCompanyInfo(int userid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = CompanyColumns.userid + "= '" + userid + "'";
			curtmp = context.getContentResolver().query(CompanyColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 添加企业发布职位
	 * 
	 * @param context
	 * @param struct
	 * @return
	 */
	public long AddCompanyPJobListInfo(Context context, CompanyPJobListStruct struct) {
		ContentValues values = new ContentValues();
		values.put(CompanyJobListColumns.JobId, struct.mJobId);
		values.put(CompanyJobListColumns.JobName, struct.mJobName);
		values.put(CompanyJobListColumns.JobCharge, struct.mJobCharge);
		values.put(CompanyJobListColumns.JobType, struct.mJobType);
		values.put(CompanyJobListColumns.JobEmployNum, struct.mJobEmployNum);
		values.put(CompanyJobListColumns.JobCreateTime, struct.mCreateTime);
		Uri Evturi = context.getContentResolver().insert(CompanyJobListColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 更新获取企业发布职位
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateCompanyPJobListInfo(Context context, CompanyPJobListStruct struct) {
		int Evturi = 0;
		String selection = CompanyJobListColumns.JobId + "= '" + struct.mJobId + "'";
		ContentValues values = new ContentValues();
		values.put(CompanyJobListColumns.JobCharge, struct.mJobCharge);
		values.put(CompanyJobListColumns.JobName, struct.mJobName);
		values.put(CompanyJobListColumns.JobType, struct.mJobType);
		values.put(CompanyJobListColumns.JobEmployNum, struct.mJobEmployNum);
		values.put(CompanyJobListColumns.JobCreateTime, struct.mCreateTime);
		values.put(CompanyJobListColumns.JobId, struct.mJobId);
		Evturi = context.getContentResolver().update(CompanyJobListColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;
		}
		return Evturi;
	}

	/**
	 * 获取企业发布职位信息列表
	 */
	public ArrayList<CompanyPJobListStruct> getCompanyPJobInfoList(Context context) {
		Cursor curtmp = null;
		ArrayList<CompanyPJobListStruct> list = new ArrayList<CompanyPJobListStruct>();
		try {
			curtmp = context.getContentResolver().query(CompanyJobListColumns.CONTENT_URI, null, null, null, CompanyJobListColumns.DEFAULT_SORT_ORDER);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					CompanyPJobListStruct struct = new CompanyPJobListStruct();
					struct.mJobId = curtmp.getInt(curtmp.getColumnIndexOrThrow(CompanyJobListColumns.JobId));
					struct.mJobCharge = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyJobListColumns.JobCharge));
					struct.mJobType = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyJobListColumns.JobType));
					struct.mJobName = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyJobListColumns.JobName));
					struct.mJobEmployNum = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyJobListColumns.JobEmployNum));
					struct.mCreateTime = curtmp.getString(curtmp.getColumnIndexOrThrow(CompanyJobListColumns.JobCreateTime));
					list.add(struct);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 获取企业发布职位信息个数
	 */
	public int getCompanyPJobInfoNum(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(CompanyJobListColumns.CONTENT_URI, null, null, null, CompanyJobListColumns.DEFAULT_SORT_ORDER);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 判断是否存在一条企业职位列表信息
	 * 
	 * @param context
	 * @return
	 */
	public boolean IsExistCompanyPJobInfo(int userid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = CompanyJobListColumns.JobId + "= '" + userid + "'";
			curtmp = context.getContentResolver().query(CompanyJobListColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}

	/**
	 * 删除一条发布职位数据
	 * 
	 * @param mContext
	 * @param userid
	 * @return
	 */
	public long DeletePJobInfo(Context mContext, int jobid) {
		String selection = CompanyJobListColumns.JobId + "= '" + jobid + "'";
		long count = mContext.getContentResolver().delete(CompanyJobListColumns.CONTENT_URI, selection, null);
		return count;
	}

	/**
	 * 获取企业职位列表的个数
	 * 
	 * @param context
	 * @return
	 */
	public int getPjobListInfoCount(Context context) {
		Cursor curtmp = null;
		try {
			curtmp = context.getContentResolver().query(CompanyJobListColumns.CONTENT_URI, null, null, null, null);
			if (curtmp == null)
				return 0;
			if (curtmp.getCount() != 0) {
				return curtmp.getCount();
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return 0;
	}

	/**
	 * 添加企业发布职位
	 * 
	 * @param context
	 * @param struct
	 * @return
	 */
	public long AddAnswerListInfo(Context context, AskAnswerStruct struct) {
		ContentValues values = new ContentValues();
		values.put(AnswerListColumns.mAskid, struct.mAskid);
		values.put(AnswerListColumns.mid, struct.mid);
		values.put(AnswerListColumns.mCall, struct.mCall);
		values.put(AnswerListColumns.mContent, struct.mContent);
		values.put(AnswerListColumns.mCreateTime, struct.mCreateTime);
		values.put(AnswerListColumns.mName, struct.mName);
		values.put(AnswerListColumns.mImage, struct.mImage);
		Uri Evturi = context.getContentResolver().insert(AnswerListColumns.CONTENT_URI, values);
		if (Evturi != null) {
			long etnid = ContentUris.parseId(Evturi);
			if (etnid < 0) {
				return -1;
			}
			return etnid;
		} else {
			return -1;
		}
	}

	/**
	 * 更新获取企业发布职位
	 * 
	 * @param context
	 * @param struct
	 * @param name
	 * @return
	 */
	public int UpdateAnswerListInfo(Context context, AskAnswerStruct struct) {
		int Evturi = 0;
		String selection = AnswerListColumns.mid + "= '" + struct.mid + "'";
		ContentValues values = new ContentValues();
		values.put(AnswerListColumns.mAskid, struct.mAskid);
		values.put(AnswerListColumns.mid, struct.mid);
		values.put(AnswerListColumns.mCall, struct.mCall);
		values.put(AnswerListColumns.mContent, struct.mContent);
		values.put(AnswerListColumns.mCreateTime, struct.mCreateTime);
		values.put(AnswerListColumns.mName, struct.mName);
		values.put(AnswerListColumns.mImage, struct.mImage);
		Evturi = context.getContentResolver().update(AnswerListColumns.CONTENT_URI, values, selection, null);
		if (Evturi < 0) {
			return -1;
		}
		return Evturi;
	}

	/**
	 * 获取企业发布职位信息列表
	 */
	public ArrayList<AskAnswerStruct> getAnswerList(Context context, int id) {
		Cursor curtmp = null;
		ArrayList<AskAnswerStruct> list = new ArrayList<AskAnswerStruct>();
		try {
			String selection = AnswerListColumns.mAskid + "='" + id + "'";
			curtmp = context.getContentResolver().query(AnswerListColumns.CONTENT_URI, null, selection, null, AnswerListColumns.DEFAULT_SORT_ORDER);
			if (curtmp == null)
				return null;
			if (curtmp.getCount() != 0) {
				while (curtmp.moveToNext()) {
					AskAnswerStruct struct = new AskAnswerStruct();
					struct.mAskid = curtmp.getInt(curtmp.getColumnIndexOrThrow(AnswerListColumns.mAskid));
					struct.mid = curtmp.getInt(curtmp.getColumnIndexOrThrow(AnswerListColumns.mid));
					struct.mCall = curtmp.getString(curtmp.getColumnIndexOrThrow(AnswerListColumns.mCall));
					struct.mContent = curtmp.getString(curtmp.getColumnIndexOrThrow(AnswerListColumns.mContent));
					struct.mCreateTime = curtmp.getString(curtmp.getColumnIndexOrThrow(AnswerListColumns.mCreateTime));
					struct.mName = curtmp.getString(curtmp.getColumnIndexOrThrow(AnswerListColumns.mName));
					struct.mImage = curtmp.getString(curtmp.getColumnIndexOrThrow(AnswerListColumns.mImage));
					list.add(struct);
				}
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return list;
	}

	/**
	 * 判断是否存在一条企业职位列表信息
	 * 
	 * @param context
	 * @return
	 */
	public boolean IsExistAnswerInfo(int userid, Context context) {
		Cursor curtmp = null;
		try {
			String selection = AnswerListColumns.mid + "= '" + userid + "'";
			curtmp = context.getContentResolver().query(AnswerListColumns.CONTENT_URI, null, selection, null, null);
			if (curtmp == null)
				return false;
			if (curtmp.getCount() != 0) {
				return true;
			}
		} finally {
			if (curtmp != null && !curtmp.isClosed())
				curtmp.close();
		}
		return false;
	}
}
