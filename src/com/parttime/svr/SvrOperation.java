package com.parttime.svr;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parttime.data.PartTimeDB;
import com.parttime.encode.Encode;
import com.parttime.struct.PartTimeStruct.AllResumeStruct;
import com.parttime.struct.PartTimeStruct.AskAnswerStruct;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.struct.PartTimeStruct.CommentStruct;
import com.parttime.struct.PartTimeStruct.CompanyDetailsStruct;
import com.parttime.struct.PartTimeStruct.CompanyPJobListStruct;
import com.parttime.struct.PartTimeStruct.HiredStruct;
import com.parttime.struct.PartTimeStruct.HotSeekerStruct;
import com.parttime.struct.PartTimeStruct.HotWorkStruct;
import com.parttime.struct.PartTimeStruct.JobInfoStruct;
import com.parttime.struct.PartTimeStruct.LoginStruct;
import com.parttime.struct.PartTimeStruct.PersonalDetailsStruct;
import com.parttime.struct.PartTimeStruct.PublicResumeStruct;
import com.parttime.struct.PartTimeStruct.SignStruct;
import com.parttime.struct.PartTimeStruct.VersionInfoStruct;
import com.parttime.struct.PartTimeStruct.VolunteerSignInfoStruct;
import com.parttime.struct.PartTimeStruct.VolunteerStruct;
import com.parttime.utils.File2Server;
import com.parttime.utils.MD5Encrypt;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 跟服务器交互
 * @date 2015-1-18
 * @time 13:55
 */
public class SvrOperation {

	private final static String TAG = SvrOperation.class.getSimpleName();
	static SharedPreferences sharedPreferences;
	private static PartTimeDB parttimeDb = null;
	private static String JZ365 = MD5Encrypt.encryption("jz365");

	private static String volunteerUri = "http://101.200.176.136/jianzhi365/Uploads/";

	/**
	 * @function 从服务器下载图片
	 * 
	 * @param mContext
	 * @param filePath
	 *            文件要存储的路径 <br>
	 *            根目录 （Constant.PARTTIMEJOB_PATH）、图片目录（
	 *            Constant.PARTTIMEJOB_IMAGE）<br>
	 *            下载目录（ Constant.PARTTIMEJOB_DOWNLOAD） 、临时目录（
	 *            Constant.PARTTIMEJOB_TEMP_PATH）
	 * @param headName
	 *            存储文件的名称
	 * @return
	 */
	@SuppressLint("NewApi")
	public static int getFileFromSvr(Context mContext, String linkpathname, String filePath) {

		if (linkpathname == null || linkpathname.isEmpty()) {
			return SvrInfo.SVR_RESULT_NO_FILENAME_SET;
		}
		String filename = linkpathname.substring(linkpathname.lastIndexOf("/") + 1);
		File2Server f2s = new File2Server();
		int result = f2s.DownFile(linkpathname, filePath, filename);
		if (result == File2Server.RESULT_FAILED)
			return SvrInfo.SVR_RESULT_DOWNLOAD_FILE_FAILED;
		return SvrInfo.SVR_RESULT_SUCCESS;
	}

	/**
	 * 上传图片到服务器
	 * 
	 * @param mContext
	 * @param auserid
	 * @param fpath
	 * @param fname
	 * @return Demo:{“errorcode”: -3} //头像存储失败 Demo:{“errorcode”: -4} //数据存储失败
	 */
	public static int uploadImage(Context mContext, long auserid, String fname) {
		return uploadFile(mContext, auserid, 0, fname);
	}

	// =========================================uploadfile========================================================
	public static int uploadFile(Context mContext, long auserid, int type, String fname) {
		int iret;
		JSONObject object = new JSONObject();
		try {
			String datad = Encode.FileBase64String(fname);
			if (datad == null)
				return SvrInfo.SVR_RESULT_FILE_CONTENT_ERR;
			object.put("check_code", JZ365);
			object.put("user_id", auserid);
			object.put("avatar", datad);
			JSONObject result = Submit.Request(SvrInfo.MODIFY_HEADIMAGE_API, object);
			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;

		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 上传认证图片
	 * 
	 * @param mContext
	 * @param auserid
	 * @param type
	 * @param fname
	 * @return
	 */
	public static int uploadConfirmImage(Context mContext, long auserid, String type, String fname) {
		int iret;
		JSONObject object = new JSONObject();
		try {
			String datad = Encode.FileBase64String(fname);
			if (datad == null)
				return SvrInfo.SVR_RESULT_FILE_CONTENT_ERR;
			object.put("check_code", JZ365);
			object.put("user_id", auserid);
			object.put("type", type);
			object.put("thumb", datad);
			JSONObject result = Submit.Request(SvrInfo.SET_SUBMIT_CONFIRMIMAGE_API, object);
			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;

		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * get new version from svr , and to judge it is new or not
	 * 
	 * @param mContext
	 * @param newVersion
	 *            0:update it , -999:do not update
	 */
	public static int GetAppNewVersion(Context mContext, int newVersionCode, VersionInfoStruct vis) {
		JSONObject object = new JSONObject();
		try {

			object.put("version_code", newVersionCode);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_NEW_VERSIONINFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			if (status == 0) {
				try {
					JSONObject data = result.getJSONObject("data");
					vis.mNewContent = data.getString("version_content");
					vis.mNewVersionCode = data.getInt("version_code");
					vis.mNewVersionName = data.getString("version_name");
					vis.mVersionNewTime = data.getString("version_time");

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 登录
	 */
	public static int Login(Context mContext, HashMap<String, String> map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			object.put("username", map.get("username"));
			object.put("password", map.get("pwd"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));

			JSONObject result = Submit.Request(SvrInfo.LOGIN_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}

			// System.out.println("login>>>>>>:" + result.toString());

			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			/**** 用户userid，名称，头像 （灰色的寂寞） ***********************************************************************/
			// SharedPreferences.Editor shared =
			// mContext.getSharedPreferences(Constant.USERINFO_SHARED,
			// 0).edit();

			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			SharedPreferences.Editor shared = sharedPreferences.edit();
			shared.putLong("userid", Long.parseLong(result.getString("userid"))).commit();
			shared.putString("username", result.getString("username")).commit();
			shared.putString("email", result.getString("email")).commit();
			shared.putString("call", result.getString("call")).commit();
			shared.putString("avatar", "http://101.200.176.136/jianzhi365/Uploads/avatar/" + result.getString("avatar")).commit();
			/**** 用户userid，名称，头像 （灰色的寂寞） ***********************************************************************/

			LoginStruct loginStruct = new LoginStruct();
			loginStruct.mUserId = result.getString("userid");
			loginStruct.mName = result.getString("username");
			loginStruct.mEmail = result.getString("email");
			loginStruct.mHead = result.getString("avatar");
			loginStruct.mType = result.getString("type");

			boolean is_exist_flag = parttimeDb.IsExistLoginInfo(loginStruct.mName, mContext);
			if (!is_exist_flag) {
				loginStruct.mRemember = map.get("remember");
				parttimeDb.AddLoginInfo(mContext, loginStruct);
			} else {
				parttimeDb.UpdateLoginInfo(mContext, loginStruct, loginStruct.mName);
			}
			return SvrInfo.SVR_RESULT_SUCCESS;

		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 注册
	 */
	public static int Register(Context mContext, HashMap<String, String> map) {
		JSONObject object = new JSONObject();
		try {

			object.put("check_code", MD5Encrypt.encryption("jz365"));
			object.put("username", map.get("username"));
			object.put("password", map.get("password"));
			// object.put("email", map.get("email"));
			object.put("call", map.get("call"));
			object.put("type", map.get("type"));
			//object.put("avatar", Utils.FileBase64String(map.get("avatar")));

			JSONObject result = Submit.Request(SvrInfo.REGITSTER_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}

			return SvrInfo.SVR_RESULT_SUCCESS;

		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 发送短信验证码
	 * 
	 * @param mContext
	 * @return
	 */
	public static int SendVcode(Context mContext, HashMap<String, String> map) {
		JSONObject object = new JSONObject();
		try {
			object.put("phone", map.get("phone"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.SENDPHONE_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}

			// System.out.println("========================");
			// System.out.println(result.toString());
			// System.out.println("========================");

			Utils.BaseMessage = result.getString("message");
			int status = result.getInt("status");
			if (status == 200) {
				Utils.Vcode = result.getString("code");
				return SvrInfo.SVR_RESULT_SUCCESS;
			} else {
				return status;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 忘记密码
	 * 
	 * @param mContext
	 * @return
	 */
	public static int ForgetPass(Context mContext, HashMap<String, String> map) {
		JSONObject object = new JSONObject();

		try {
			object.put("new_pass", map.get("new_pass"));
			object.put("username", map.get("username"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.FORGET_PASS_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}

			return SvrInfo.SVR_RESULT_SUCCESS;

		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 热门工作推荐
	 * 
	 * @param mContext
	 * @return
	 */
	public static int Hot_Work(Context mContext) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();

		try {
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_INDEX_LIST_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}

			// System.out.println("===============================");
			// System.out.println(result.toString());
			// System.out.println("===============================");

			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			if (status == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {

						JSONObject jsonObject = (JSONObject) data.opt(i);
						HotWorkStruct hotWorkStruct = new HotWorkStruct();
						hotWorkStruct.mId = jsonObject.getString("id");
						hotWorkStruct.mName = jsonObject.getString("name");
						hotWorkStruct.mCharge = jsonObject.getString("charge");
						hotWorkStruct.mType = jsonObject.getString("type");
						hotWorkStruct.mNum = jsonObject.getString("num");
						hotWorkStruct.mCreate_Time = jsonObject.getString("create_time");
						hotWorkStruct.mCompany_Name = jsonObject.getString("company_name");
						hotWorkStruct.mCompany_Add = jsonObject.getString("company_add");
						hotWorkStruct.mUserId = jsonObject.getString("user_id");
						hotWorkStruct.mCompany_Position = jsonObject.getString("position");

						boolean is_exist_flag = parttimeDb.IsExistHotWorkInfo(hotWorkStruct.mId, mContext);
						if (is_exist_flag) {
							parttimeDb.UpdateHotWorkInfo(mContext, hotWorkStruct, hotWorkStruct.mId);
						} else {
							parttimeDb.AddHotWorkInfo(mContext, hotWorkStruct);
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;
	}

	/**
	 * 求职者推荐
	 * 
	 * @param mContext
	 * @return
	 */

	public static int Hot_Seeker(Context mContext) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();

		try {
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_INDEX2_LIST_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			if (status == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {

						JSONObject jsonObject = (JSONObject) data.opt(i);
						HotSeekerStruct struct = new HotSeekerStruct();
						struct.mRid = jsonObject.getString("rid");
						struct.mRjob = jsonObject.getString("rjob");
						struct.mRadd = jsonObject.getString("radd");
						struct.mRstart_time = jsonObject.getString("rstart_time");
						struct.mEnd_time = jsonObject.getString("rend_time");
						struct.mRtype = jsonObject.getString("rtype");
						struct.mRname = jsonObject.getString("rname");
						struct.mRsex = jsonObject.getString("rsex");
						struct.mRheight = jsonObject.getString("rheight");
						struct.mRcall = jsonObject.getString("rcall");
						struct.mRexperience = jsonObject.getString("rexperience");
						struct.mRcreate_time = jsonObject.getString("rcreate_time");
						struct.mUser_id = jsonObject.getString("user_id");
						struct.mPosition = jsonObject.getString("position");

						boolean is_exist_flag = parttimeDb.IsExistHotSeekerInfo(struct.mRid, mContext);
						if (is_exist_flag) {
							parttimeDb.UpdateHotSeekerInfo(mContext, struct, struct.mRid);
						} else {
							parttimeDb.AddHotSeekerInfo(mContext, struct);
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 工作列表
	 * 
	 * @param mContext
	 * @return
	 */
	public static int Job_List(Context mContext, HashMap<String, String> map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();

		try {
			object.put("count", map.get("count"));
			if (map.get("refresh") != null)
				object.put("refresh", map.get("refresh"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_JOB_LIST_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}

			if (status == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						HotWorkStruct hotWorkStruct = new HotWorkStruct();
						hotWorkStruct.mId = jsonObject.getString("id");
						hotWorkStruct.mName = jsonObject.getString("name");
						hotWorkStruct.mCharge = jsonObject.getString("charge");
						hotWorkStruct.mType = jsonObject.getString("type");
						hotWorkStruct.mNum = jsonObject.getString("num");
						hotWorkStruct.mCreate_Time = jsonObject.getString("create_time");
						hotWorkStruct.mCompany_Name = jsonObject.getString("company_name");
						hotWorkStruct.mCompany_Add = jsonObject.getString("company_add");
						hotWorkStruct.mCompany_Position = jsonObject.getString("position");
						hotWorkStruct.mUserId = jsonObject.getString("user_id");

						boolean is_exist_flag = parttimeDb.IsExistJobInfo(hotWorkStruct.mId, mContext);
						if (is_exist_flag) {
							parttimeDb.UpdateJobInfo(mContext, hotWorkStruct, hotWorkStruct.mId);
						} else {
							parttimeDb.AddJobInfo(mContext, hotWorkStruct);
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 获取求职信息列表
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getPublicResume(Context mContext, HashMap<String, String> map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();

		try {
			object.put("count", map.get("count"));
			if (map.get("refresh") != null)
				object.put("refresh", map.get("refresh"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_PUBLIC_RESUME_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}

			if (status == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						PublicResumeStruct resumestruct = new PublicResumeStruct();
						resumestruct.mRheight = jsonObject.getString("rheight");
						resumestruct.mRtype = jsonObject.getString("rtype");
						resumestruct.mRname = jsonObject.getString("rname");
						resumestruct.mRsex = jsonObject.getString("rsex");
						resumestruct.mRstart_time = jsonObject.getString("rstart_time");
						resumestruct.mRexperience = jsonObject.getString("rexperience");
						resumestruct.mRadd = jsonObject.getString("radd");
						resumestruct.mRjob = jsonObject.getString("rjob");
						resumestruct.mRid = jsonObject.getString("rid");
						resumestruct.mRuser_id = jsonObject.getString("user_id");
						resumestruct.mRcall = jsonObject.getString("rcall");
						resumestruct.mRpublic = jsonObject.getString("public");

						resumestruct.mRrend_time = jsonObject.getString("rend_time");

						resumestruct.mRcreate_time = jsonObject.getString("rcreate_time");
						resumestruct.mPosition = jsonObject.getString("position");

						boolean is_exist_flag = parttimeDb.IsExistPublicResumeInfo(resumestruct.mRid, mContext);
						if (is_exist_flag) {
							parttimeDb.UpdatePublicResumeInfo(mContext, resumestruct, resumestruct.mRid);
						} else {
							parttimeDb.AddPublicCustomerInfo(mContext, resumestruct);
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 获取职位信息详情
	 * 
	 * @param mContext
	 * @return
	 */
	public static int Job_Info(Context mContext, HashMap<String, String> map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();

		try {
			object.put("job_id", map.get("job_id"));
			object.put("hire_name", map.get("job_name"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_JOB_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}

			if (status == 0) {
				try {
					JSONObject jsonObject = result.getJSONObject("data");
					JobInfoStruct jobInfoStruct = new JobInfoStruct();
					jobInfoStruct.mId = jsonObject.getString("id");
					jobInfoStruct.mName = jsonObject.getString("name");
					jobInfoStruct.mCharge = jsonObject.getString("charge");
					jobInfoStruct.mType = jsonObject.getString("type");
					jobInfoStruct.mNum = jsonObject.getString("num");
					jobInfoStruct.mCreate_time = jsonObject.getString("create_time");

					jobInfoStruct.mPresent = jsonObject.getString("present");
					jobInfoStruct.mRequire = jsonObject.getString("require");
					jobInfoStruct.mCondition = jsonObject.getString("condition");
					jobInfoStruct.mThumb = jsonObject.getString("thumb");
					jobInfoStruct.mStartTime = jsonObject.getString("start_time");
					jobInfoStruct.mEndTime = jsonObject.getString("end_time");
					jobInfoStruct.mCompany_Code = jsonObject.getString("company_code");
					jobInfoStruct.mCompany_status = jsonObject.getString("company_status");

					jobInfoStruct.mCompany_Name = jsonObject.getString("company_name");
					jobInfoStruct.mCompany_Add = jsonObject.getString("company_add");
					// jobInfoStruct.mCompany_Level =
					// jsonObject.getString("company_level");
					jobInfoStruct.mUserId = jsonObject.getString("user_id");

					boolean is_exist_flag = parttimeDb.IsExistJobDetailInfo(jobInfoStruct.mId, mContext);
					if (is_exist_flag) {
						parttimeDb.UpdateJobDetailInfo(mContext, jobInfoStruct, jobInfoStruct.mId);
					} else {
						parttimeDb.AddJobDetailInfo(mContext, jobInfoStruct);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 新建求职简历
	 * 
	 * @param mContext
	 * @return
	 */
	public static int New_Resume(Context mContext, HashMap<String, String> map) {
		JSONObject object = new JSONObject();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		long userid = sharedPreferences.getLong("userid", 0);
		try {
			object.put("rjob", map.get("rjob"));
			object.put("radd", map.get("radd"));
			object.put("rstart_time", map.get("rstart_time"));
			object.put("rend_time", map.get("rend_time"));
			object.put("rtype", map.get("rtype"));
			object.put("rname", map.get("rname"));
			object.put("rsex", map.get("rsex"));
			object.put("rheight", map.get("rheight"));
			object.put("rcall", map.get("rcall"));
			object.put("position", map.get("position"));
			object.put("rexperience", map.get("rexperience"));
			object.put("rcreate_time", map.get("rcreate_time"));
			object.put("user_id", userid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_ADD_RESUME_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 修改求职简历
	 * 
	 * @param mContext
	 * @return
	 */
	public static int Modify_Resume(Context mContext, AllResumeStruct struct) {
		JSONObject object = new JSONObject();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		long userid = sharedPreferences.getLong("userid", 0);
		try {
			object.put("rid", struct.mRid);
			object.put("rjob", struct.mRjob);
			object.put("radd", struct.mRadd);
			object.put("rstart_time", struct.mRstart_Time);
			object.put("rend_time", struct.mRend_Time);
			object.put("rtype", struct.mRtype);
			object.put("rname", struct.mRname);
			object.put("rsex", struct.mRsex);
			object.put("rheight", struct.mRheight);
			object.put("rcall", struct.mRcall);
			object.put("rexperience", struct.mRexperience);
			object.put("user_id", userid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));

			JSONObject result = Submit.Request(SvrInfo.UP_RESUME_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			return SvrInfo.SVR_RESULT_SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}

	}

	/**
	 * 所有求职简历
	 * 
	 * @param mContext
	 * @return
	 */
	public static int AllResume(Context mContext) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		long userid = sharedPreferences.getLong("userid", 0);
		try {
			object.put("user_id", userid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_RESUME_LIST_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						AllResumeStruct allResumeStruct = new AllResumeStruct();

						allResumeStruct.mRid = jsonObject.getString("rid");
						allResumeStruct.mRjob = jsonObject.getString("rjob");
						allResumeStruct.mRadd = jsonObject.getString("radd");
						allResumeStruct.mRstart_Time = jsonObject.getString("rstart_time");
						allResumeStruct.mRend_Time = jsonObject.getString("rend_time");
						allResumeStruct.mRtype = jsonObject.getString("rtype");
						allResumeStruct.mRname = jsonObject.getString("rname");
						allResumeStruct.mRsex = jsonObject.getString("rsex");
						allResumeStruct.mRheight = jsonObject.getString("rheight");
						allResumeStruct.mRcall = jsonObject.getString("rcall");
						allResumeStruct.mRexperience = jsonObject.getString("rexperience");
						allResumeStruct.mUserid = jsonObject.getString("user_id");

						boolean is_exist_flag = parttimeDb.IsExistAllResumeList(allResumeStruct.mRid, mContext);
						if (is_exist_flag) {
							parttimeDb.UpdateAllResumeList(mContext, allResumeStruct, allResumeStruct.mRid);
						} else {
							parttimeDb.AddAllResumeList(mContext, allResumeStruct);
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 新建雇佣关系
	 * 
	 * @param mContext
	 * @return
	 */
	public static int AddHire(Context mContext, HashMap<String, String> map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		long userid = sharedPreferences.getLong("userid", 0);
		try {
			object.put("user_id", userid);
			object.put("job_id", map.get("job_id"));
			object.put("r_id", map.get("r_id"));
			object.put("hire_name", map.get("hire_name"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.ADD_HIRE_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 获取信誉度
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getCompanyLevel(Context mContext, HashMap<String, String> input_map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		String business_id1 = "";
		try {
			if (input_map.get("business_id") != null)
				object.put("business_id", input_map.get("business_id"));
			if (input_map.get("personal_id") != null)
				object.put("personal_id", input_map.get("personal_id"));
			object.put("type", input_map.get("type"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_COMPANYLEVEL_API, object);

			if (result == null) {
				return null;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return null;
			}
			if (state == 0) {
				// {"status":0,"avg":"3.3333"}
				business_id1 = result.getString("avg");
				// business_id1=business_id;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return business_id1;

	}

	/**
	 * 发表评论
	 * 
	 * @param mContext
	 * @return
	 */
	public static int AddComments(Context mContext, HashMap<String, String> map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			object.put("name", map.get("name"));
			object.put("comment", map.get("comment"));
			object.put("prestige", map.get("prestige"));
			object.put("personal_id", map.get("personal_id"));
			object.put("business_id", map.get("business_id"));
			if (map.get("type") != null)
				object.put("type", map.get("type"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.ADD_COMMENT_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 获取评论
	 * 
	 * @param mContext
	 * @return
	 */
	public static int GetComments(Context mContext, HashMap<String, String> map) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			if (map.get("personal_id") != null)
				object.put("personal_id", map.get("personal_id"));
			if (map.get("business_id") != null)
				object.put("business_id", map.get("business_id"));
			object.put("count", map.get("count"));
			if (map.get("refresh") != null)
				object.put("refresh", map.get("refresh"));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_COMMENT_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						CommentStruct struct = new CommentStruct();
						struct.mCreateTime = jsonObject.getString("create_time");
						struct.mBusiness_Id = jsonObject.getString("business_id");
						struct.mId = jsonObject.getString("id");
						struct.mPrestige = jsonObject.getString("prestige");
						struct.mComment = jsonObject.getString("comment");
						struct.mPersonal_Id = jsonObject.getString("personal_id");
						struct.mName = jsonObject.getString("name");

						boolean is_exist_flag = parttimeDb.IsExistCommentInfo(struct.mId, mContext);
						if (is_exist_flag) {
							parttimeDb.UpdateComment(mContext, struct, struct.mId);
						} else {
							parttimeDb.AddCommentInfo(mContext, struct);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 发布求职信息
	 * 
	 * @param mContext
	 * @return
	 */
	public static int PublicResume(Context mContext, String rid) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		try {
			object.put("rid", rid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.PUBLIC_RESUME_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 职位简历详情
	 * 
	 * @param mContext
	 * @return
	 */
	public static int Resume_Info(Context mContext, String rid) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			object.put("rid", rid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.GET_RESUME_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				try {
					JSONObject jsonObject = result.getJSONObject("data");
					AllResumeStruct allResumeStruct = new AllResumeStruct();

					allResumeStruct.mRid = jsonObject.getString("rid");
					allResumeStruct.mRjob = jsonObject.getString("rjob");
					allResumeStruct.mRadd = jsonObject.getString("radd");
					allResumeStruct.mRstart_Time = jsonObject.getString("rstart_time");
					allResumeStruct.mRend_Time = jsonObject.getString("rend_time");
					allResumeStruct.mRtype = jsonObject.getString("rtype");
					allResumeStruct.mRname = jsonObject.getString("rname");
					allResumeStruct.mRsex = jsonObject.getString("rsex");
					allResumeStruct.mRheight = jsonObject.getString("rheight");
					allResumeStruct.mRcall = jsonObject.getString("rcall");
					allResumeStruct.mRexperience = jsonObject.getString("rexperience");
					allResumeStruct.mUserid = jsonObject.getString("user_id");

					boolean is_exist_flag = parttimeDb.IsExistResumeInfo(allResumeStruct.mRid, mContext);
					if (is_exist_flag) {
						parttimeDb.UpdateResumeInfo(mContext, allResumeStruct, allResumeStruct.mRid);
					} else {
						parttimeDb.AddResumeInfo(mContext, allResumeStruct);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 删除求职简历
	 * 
	 * @param mContext
	 * @return
	 */
	public static int DelResume(Context mContext, int rid) {
		parttimeDb = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			object.put("rid", rid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.DEL_RESUME_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}

	}

	/**
	 * 职位发布
	 * 
	 * @param mContext
	 * @return
	 */
	public static int Job_Issue(Context mContext, HashMap<String, String> map) {
		int iret;
		JSONObject object = new JSONObject();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		long userid = sharedPreferences.getLong("userid", 0);
		try {
			object.put("user_id", userid);
			// Demo:{"user_id":"企业id", "name": "职位名","charge": "佣金","type":
			// "类型","num": "招聘人数","present": "介绍",
			// "require": "要求","condition": "条件","start_time":
			// "开始时间","end_time": "结束时间","thumb":"base64()"}

			object.put("name", map.get("name"));
			object.put("charge", map.get("charge"));
			object.put("type", map.get("type"));
			object.put("num", map.get("num"));
			object.put("present", map.get("present"));
			object.put("require", map.get("require"));
			object.put("condition", map.get("condition"));
			object.put("start_time", map.get("start_time"));
			object.put("end_time", map.get("end_time"));
			object.put("position", map.get("position"));
			object.put("thumb", Utils.FileBase64String(map.get("avatar")));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.ADD_JOB_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;

		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 获取所有企业位置坐标的接口
	 * 
	 * @param mContext
	 *            type(0:个人 1:企业)
	 * @return
	 */
	public static int GetCompanyLatLngFromSvr(Context mContext) {
		JSONObject object = new JSONObject();
		try {
			object.put("type", 1);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request("注册用户的接口", object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}

			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 获取所有个人位置坐标的接口
	 * 
	 * @param mContext
	 *            type(0:个人 1:企业)
	 * @return
	 */
	public static int GetPersonalLatLngFromSvr(Context mContext) {
		JSONObject object = new JSONObject();
		try {
			object.put("type", 0);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request("注册用户的接口", object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}

			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 从服务器获取个人信息的数据
	 * 
	 * @param mContext
	 * @param p_userid
	 * @return
	 */
	public static int GetPersonalInfo(Context mContext, long p_userid) {
		PartTimeDB db = PartTimeDB.getInstance(mContext);
		PersonalDetailsStruct struct = new PersonalDetailsStruct();
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", p_userid);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_PERSONAL_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			db.ClearPersonalInfo(mContext, p_userid);
			struct.mPersonalId = p_userid;
			struct.mPersonalHeadImage = result.getString("avatar");
			struct.mPersonalName = result.getString("username");
			struct.mPersonalLinkman = result.getString("linkman");
			struct.mPersonalGender = result.getString("sex");
			struct.mPersonalEmail = result.getString("email");
			struct.mPersonalJobExp = result.getString("experience");
			struct.mPersonalAddress = result.getString("QQ");
			struct.mPersonalCall = result.getString("call");
			struct.mPersonalCardConfirm = "http://www.xunlvshi.cn/project/jianzhi365/Uploads/images/identity/" + result.getString("identity");
			if (db.IsExistPersonalInfo(p_userid, mContext)) {
				db.UpdatePersonalInfo(mContext, struct);
			} else {
				db.AddPersonalInfo(mContext, struct);
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 从服务器获取个人信息的数据
	 * 
	 * @param mContext
	 * @param p_userid
	 * @return
	 */
	public static PersonalDetailsStruct GetPersonalInfo1(Context mContext, long p_userid) {
		PartTimeDB db = PartTimeDB.getInstance(mContext);
		PersonalDetailsStruct struct = new PersonalDetailsStruct();
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", p_userid);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_PERSONAL_INFO_API, object);

			if (result == null) {
				return null;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return null;
			}
			db.ClearPersonalInfo(mContext, p_userid);
			struct.mPersonalId = p_userid;
			struct.mPersonalHeadImage = result.getString("avatar");
			struct.mPersonalName = result.getString("username");
			struct.mPersonalLinkman = result.getString("linkman");
			struct.mPersonalGender = result.getString("sex");
			struct.mPersonalEmail = result.getString("email");
			struct.mPersonalJobExp = result.getString("experience");
			struct.mPersonalAddress = result.getString("QQ");
			struct.mPersonalCall = result.getString("call");
			db.AddPersonalInfo(mContext, struct);
			// return struct;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return struct;
	}

	/**
	 * 通过企业id从服务器获取企业注册时的用户名
	 * 
	 * @param mContext
	 * @param p_userid
	 * @return
	 */
	public static HashMap<String, String> GetUserName(Context mContext, long p_userid) {
		HashMap<String, String> map = new HashMap<String, String>();
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", p_userid);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_USERNAME_API, object);

			if (result == null) {
				return null;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return null;
			}
			String username = result.getString("username");
			String avatar = result.getString("avatar");
			map.put("username", username);
			map.put("avatar", avatar);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 提交个人信息修噶后数据
	 * 
	 * @param mContext
	 * @param struct
	 * @return
	 */
	public static long UploadPersonalInfo(Context mContext, PersonalDetailsStruct struct) {
		JSONObject object = new JSONObject();
		try {
			object.put("check_code", JZ365);
			object.put("user_id", struct.mPersonalId);
			object.put("linkman", struct.mPersonalLinkman);
			object.put("sex", struct.mPersonalGender);

			object.put("email", struct.mPersonalEmail);
			object.put("experience", struct.mPersonalJobExp);
			object.put("QQ", struct.mPersonalAddress);
			object.put("call", struct.mPersonalCall);
			JSONObject result = Submit.Request(SvrInfo.SUBMIT_PERSONAL_INFO_API, object);
			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int status = result.getInt("status");
			if (status != 0) {
				return status;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (JSONException e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
		}
	}

	/**
	 * 获取雇佣信息列表
	 * 
	 * @param mContext
	 * @return "id":123 ,"r_id":123 ,"job_id": "123","engage": "进行中",
	 *         "create_time": "2010-10-10 10:10:10"
	 */
	public static int getTaskJobList(Context mContext) {

		PartTimeDB DB = PartTimeDB.getInstance(mContext);

		JSONObject object = new JSONObject();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		long userid = sharedPreferences.getLong("userid", 0);
		try {
			object.put("user_id", userid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.TASK_GET_INFO_LIST_API, object);
			// {"data":[{"create_time":"2015-03-16 16:15:03","id":"32","user_id":"89","hire_name":"打字员","engage":"","r_id":"47","job_id":"68"},
			// {"create_time":"2015-03-16 16:29:53","id":"33","user_id":"89","hire_name":null,"engage":"","r_id":"47","job_id":"69"},
			// {"create_time":"2015-03-17 16:38:58","id":"47","user_id":"89","hire_name":"111111","engage":"已申请","r_id":"64","job_id":"69"},
			// {"create_time":"2015-03-17 16:39:11","id":"48","user_id":"89","hire_name":"泥瓦匠","engage":"","r_id":"64","job_id":"82"},
			// {"create_time":"2015-03-17 16:53:07","id":"49","user_id":"89","hire_name":"111111","engage":"已申请","r_id":"64","job_id":"69"},
			// {"create_time":"2015-03-23 09:47:05","id":"66","user_id":"89","hire_name":"打字员","engage":"已申请","r_id":"91","job_id":"58"},
			// {"create_time":"2015-03-23 09:51:39","id":"67","user_id":"89","hire_name":"发单员","engage":"已申请","r_id":"91","job_id":"63"},
			// {"create_time":"2015-03-26 16:45:19","id":"68","user_id":"89","hire_name":"11","engage":"","r_id":"91","job_id":"94"}],"status":0}
			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				DB.ClearHiredJobList();// 每次添加之前清空下数据库
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						HiredStruct hiredStruct = new HiredStruct();

						hiredStruct.id = jsonObject.getInt("id");
						hiredStruct.job_id = jsonObject.getInt("job_id");
						hiredStruct.r_id = jsonObject.getInt("r_id");
						hiredStruct.engage = jsonObject.getString("engage");
						hiredStruct.create_time = jsonObject.getString("create_time");
						hiredStruct.hire_name = jsonObject.getString("hire_name");

						if (!DB.IsExistHiredList(hiredStruct.id, mContext)) {
							DB.AddHiredInfo(mContext, hiredStruct);
						} else {
							DB.UpdateHiredInfo(mContext, hiredStruct, hiredStruct.id + "");
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 获取雇佣信息列表
	 * 
	 * @param mContext
	 * @return "id":123 ,"r_id":123 ,"job_id": "123","engage": "进行中",
	 *         "create_time": "2010-10-10 10:10:10"
	 */
	public static int getTaskJobList(Context mContext, int jobid) {

		PartTimeDB DB = PartTimeDB.getInstance(mContext);

		JSONObject object = new JSONObject();
		try {
			object.put("job_id", jobid);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.TASK_GET_INFO_LIST_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				DB.ClearHiredJobList();// 每次添加之前清空下数据库
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						HiredStruct hiredStruct = new HiredStruct();

						hiredStruct.id = jsonObject.getInt("id");
						hiredStruct.job_id = jsonObject.getInt("job_id");
						hiredStruct.r_id = jsonObject.getInt("r_id");
						hiredStruct.engage = jsonObject.getString("engage");
						hiredStruct.create_time = jsonObject.getString("create_time");
						hiredStruct.hire_name = jsonObject.getString("hire_name");

						if (!DB.IsExistHiredList(hiredStruct.job_id, mContext))
							DB.AddHiredInfo(mContext, hiredStruct);
						else
							DB.UpdateHiredInfo(mContext, hiredStruct, hiredStruct.r_id + "");

					}
				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}
		return SvrInfo.SVR_RESULT_SUCCESS;

	}

	/**
	 * 签到签退
	 * 
	 * @param mContext
	 * @return //type 1 = 签到,2 = 签离 Demo:{"hire_id":123, "day":
	 *         3,"type":"1/2","image": "base64('123.jpg')"}
	 */
	public static int SignOnOffTask(Context mContext, SignStruct struct) {
		JSONObject object = new JSONObject();
		try {
			object.put("hire_id", struct.hire_id);
			if (struct.day > 9) {
				switch (struct.day) {
				case 10:
					object.put("day", 1);
					break;
				case 11:
					object.put("day", 2);
					break;
				case 12:
					object.put("day", 3);
					break;
				case 13:
					object.put("day", 4);
					break;
				case 14:
					object.put("day", 5);
					break;
				case 15:
					object.put("day", 6);
					break;
				case 16:
					object.put("day", 7);
					break;
				case 17:
					object.put("day", 8);
					break;
				case 18:
					object.put("day", 9);
					break;
				}
			} else {
				object.put("day", struct.day);
			}
			object.put("type", struct.type);
			object.put("image", Utils.FileBase64String(struct.image));
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.TASK_SIGNON_OFF_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取签到信息
	 * 
	 * @param mContext
	 * @return //type 1 = 签到,2 = 签离 Demo:{"hire_id":123, "day": 3,"type":"1/2"}
	 */
	public static String GetSignInfoTask(Context mContext, SignStruct struct) {
		JSONObject object = new JSONObject();
		String imageUri = null;
		try {
			object.put("hire_id", struct.hire_id);
			object.put("day", struct.day);
			object.put("type", struct.type);
			object.put("check_code", MD5Encrypt.encryption("jz365"));
			JSONObject result = Submit.Request(SvrInfo.TASK_GETSIGN_INFO_API, object);

			if (result == null) {
				return null;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return null;
			}

			if (state == 0) {
				try {
					JSONObject data = result.getJSONObject("data");
					imageUri = "http://www.xunlvshi.cn/project/jianzhi365/Uploads/images/signin/" + data.getString("pic");
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return imageUri;
	}

	/**
	 * 修改密码 { "userid": "12", "old_pass": "DSFdsfsdf", "new_pass": "123456" }
	 * Demo:{ "status": 0} //修改成功 Demo:{"status": -7} //原密码不正确 Demo:{"status":
	 * -8} //新密码与原密码一致 Demo:{"status": -3} //数据存储失败
	 */
	public static int ModifyPwdTask(Context mContext, Long userid, String oldpwd, String newpwd) {
		JSONObject object = new JSONObject();
		try {
			object.put("userid", userid);
			object.put("old_pass", MD5Encrypt.encryption(oldpwd));
			object.put("new_pass", MD5Encrypt.encryption(newpwd));
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.CHANGE_PASS_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取求助信息列表(刷新)
	 */
	public static int GetAskingListTask(Context mContext, int count) {
		PartTimeDB DB = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			object.put("refresh", 1);
			object.put("count", count);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_PUBLISH_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			if (state == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						AskInfoStruct askStruct = new AskInfoStruct();
						askStruct.mAskid = jsonObject.getInt("id");
						askStruct.mUserid = jsonObject.getInt("user_id");
						askStruct.mName = jsonObject.getString("name");
						askStruct.mTheme = jsonObject.getString("theme");
						askStruct.mCreateTime = jsonObject.getString("create_time");
						askStruct.mContent = jsonObject.getString("content");
						askStruct.mCall = jsonObject.getString("call");
						askStruct.mImage = volunteerUri + jsonObject.getString("thumb");

						if (!DB.IsExistAskInfo(askStruct.mAskid, mContext)) {
							DB.AddAskInfo(mContext, askStruct);
						} else {
							DB.UpdateAskInfo(mContext, askStruct);
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取求助信息列表(加载 count条)
	 */
	public static ArrayList<AskInfoStruct> GetAskingListTask(Context mContext, String count) {
		ArrayList<AskInfoStruct> list = new ArrayList<AskInfoStruct>();

		JSONObject object = new JSONObject();
		try {

			object.put("count", count);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_PUBLISH_INFO_API, object);

			if (result == null) {
				return null;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return null;
			}
			if (state == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						AskInfoStruct askStruct = new AskInfoStruct();
						askStruct.mAskid = jsonObject.getInt("id");
						askStruct.mUserid = jsonObject.getInt("user_id");
						askStruct.mName = jsonObject.getString("name");
						askStruct.mTheme = jsonObject.getString("theme");
						askStruct.mCreateTime = jsonObject.getString("create_time");
						askStruct.mContent = jsonObject.getString("content");
						askStruct.mCall = jsonObject.getString("call");
						askStruct.mImage = volunteerUri + jsonObject.getString("thumb");
						list.add(askStruct);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 获取志愿者信息列表(刷新)
	 */
	public static int GetVolunteerListTask(Context mContext, int count) {
		PartTimeDB DB = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			object.put("refresh", 1);
			object.put("count", count);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_VOLUNTEER_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			if (state == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						VolunteerStruct askStruct = new VolunteerStruct();
						askStruct.mVolunteerid = jsonObject.getInt("id");
						askStruct.mTheme = jsonObject.getString("theme");
						askStruct.mContent = jsonObject.getString("content");
						askStruct.mThumb = volunteerUri + jsonObject.getString("thumb");
						askStruct.mCreateTime = jsonObject.getString("create_time");

						if (!DB.IsExistVolunteerInfo(askStruct.mVolunteerid, mContext)) {
							DB.AddVolunteerInfo(mContext, askStruct);
						} else {
							DB.UpdateVolunteerInfo(mContext, askStruct);
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_UNKNOWN_ERR;
				}
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取志愿者信息列表(加载 count条)
	 */
	public static ArrayList<VolunteerStruct> GetVolunteerListTask(Context mContext, String count) {
		ArrayList<VolunteerStruct> list = new ArrayList<VolunteerStruct>();
		JSONObject object = new JSONObject();
		try {

			object.put("count", count);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_VOLUNTEER_INFO_API, object);

			if (result == null) {
				return null;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return null;
			}
			if (state == 0) {
				try {
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						VolunteerStruct askStruct = new VolunteerStruct();
						askStruct.mVolunteerid = jsonObject.getInt("id");
						askStruct.mTheme = jsonObject.getString("theme");
						askStruct.mContent = jsonObject.getString("content");
						askStruct.mThumb = volunteerUri + jsonObject.getString("thumb");
						askStruct.mCreateTime = jsonObject.getString("create_time");

						list.add(askStruct);

					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 发布求助信息 Demo:{"status": 0} //成功 Demo:{"status": -2} //图片存储失败
	 * Demo:{"status": -3} //数据存储失败
	 */
	public static int PublishAskTask(Context mContext, AskInfoStruct struct) {
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", struct.mUserid);
			object.put("name", struct.mName);
			object.put("theme", struct.mTheme);
			object.put("content", struct.mContent);
			object.put("call", struct.mCall);
			object.put("image", Utils.FileBase64String(struct.mImage));
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.PUBLISH_ASKING_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 报名异步 Demo:{"status": 0} //成功 Demo:{"status": -2} //图片存储失败 Demo:{"status":
	 * -3} //数据存储失败
	 */
	public static int VolunteerSignTask(Context mContext, VolunteerSignInfoStruct struct) {
		JSONObject object = new JSONObject();
		try {
			object.put("publicgood_id", struct.volunteerid);
			object.put("user_id", struct.userid);
			object.put("name", struct.name);
			object.put("papers", struct.papers);
			object.put("content", struct.content);
			object.put("call", struct.call);
			// if(struct.image!=null)
			// object.put("image", Utils.FileBase64String(struct.image));
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_SIGNUP_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取企业信息
	 */
	public static int GetCompanyInfoTask(Context mContext, int userid) {
		PartTimeDB DB = PartTimeDB.getInstance(mContext);
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", userid);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_COMPANY_INFO_API, object);
			// {"company_position":"40.096621,116.288819","company_code":null,"call":"null","username":"13261116343","status":0,
			// "company_name":"null","email":"323492",
			// "company_type":"null","company_add":"北京市昌平区生命科学园","company_status":null,"avatar":"2_avatar.jpg"}
			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				try {
					CompanyDetailsStruct struct = new CompanyDetailsStruct();
					struct.mCompanyId = userid;
					struct.muserid = userid;
					struct.mCompanyName = result.getString("username");
					struct.mModifyProperty = result.getString("company_type");
					struct.mCompanyConfirmState = result.getString("company_status");
					struct.mCompanyConfirm = result.getString("company_code");// @@@@@@@@@@@@@@@@@@@@
					struct.mPOI = result.getString("company_position");
					struct.mDetailsPosition = result.getString("company_add");
					struct.mModifyHeadImage = result.getString("avatar");
					struct.mCompanyEmail = result.getString("email");
					struct.mCompanyContact = result.getString("call");
					struct.mCompanyIdentity = "http://www.xunlvshi.cn/project/jianzhi365/Uploads/images/identity/" + result.getString("company_code");// @@@@@@@@@@@@@@@@@@@@
					if (!DB.IsExistCompanyInfo(userid, mContext))
						DB.AddCompanyInfo(mContext, struct);
					else
						DB.UpdateCompanyInfo(mContext, struct);
				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_NULL_RESULT;
				}
			}

			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取企业发布职位信息
	 */
	public static int GetCompanyPJobInforeFreshTask(Context mContext, int count, int userid) {
		PartTimeDB DB = PartTimeDB.getInstance(mContext);
		CompanyPJobListStruct struct;
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", userid);
			object.put("refresh", "1");
			object.put("count", count);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_COMPANY_PJOB_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				try {
					struct = new CompanyPJobListStruct();
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						struct.mJobId = jsonObject.getInt("id");
						struct.mJobName = jsonObject.getString("name");
						struct.mJobCharge = jsonObject.getString("charge");
						struct.mJobType = jsonObject.getString("type");
						struct.mJobEmployNum = jsonObject.getString("num");
						struct.mCreateTime = jsonObject.getString("create_time");
						if (!DB.IsExistCompanyPJobInfo(struct.mJobId, mContext))
							DB.AddCompanyPJobListInfo(mContext, struct);
						else
							DB.UpdateCompanyPJobListInfo(mContext, struct);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_NULL_RESULT;
				}
			}

			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取企业发布职位信息(上拉加载)
	 */
	public static ArrayList<CompanyPJobListStruct> GetCompanyPJobInfoLoadTask(Context mContext, int count, int userid) {
		PartTimeDB DB = PartTimeDB.getInstance(mContext);
		ArrayList<CompanyPJobListStruct> list = new ArrayList<CompanyPJobListStruct>();
		CompanyPJobListStruct struct;
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", userid);
			object.put("count", count);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.GET_COMPANY_PJOB_INFO_API, object);
			if (result == null) {
				return null;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return null;
			}

			if (state == 0) {
				try {
					struct = new CompanyPJobListStruct();
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						struct.mJobId = jsonObject.getInt("id");
						struct.mJobName = jsonObject.getString("name");
						struct.mJobCharge = jsonObject.getString("charge");
						struct.mJobType = jsonObject.getString("type");
						struct.mJobEmployNum = jsonObject.getString("num");
						struct.mCreateTime = jsonObject.getString("create_time");
						list.add(struct);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 删除发布的职位
	 * 
	 * @param mContext
	 * @param jobid
	 * @return
	 */
	public static int DelPJobTask(Context mContext, int jobid) {
		JSONObject object = new JSONObject();
		try {
			object.put("job_id", jobid);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.DELETE_JOB_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 修改企业信息
	 * 
	 * @param mContext
	 * @param jobid
	 * @return
	 */
	public static int ModifyCompanyInfoTask(Context mContext, CompanyDetailsStruct struct) {
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", struct.muserid);
			object.put("company_name", struct.mCompanyName);
			object.put("company_type", struct.mModifyProperty);
			object.put("company_status", struct.mCompanyConfirmState);
			object.put("company_code", struct.mCompanyConfirm);
			object.put("company_add", struct.mDetailsPosition);
			if (struct.mPOI == null)
				object.put("company_position", "");
			else
				object.put("company_position", struct.mPOI);
			object.put("call", struct.mCompanyContact);
			object.put("email", struct.mCompanyEmail);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.UP_COMPANY_INFO_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 企业雇佣员工,改变员工的对每个工作的工作状态
	 * 
	 * @param mContext
	 * @param jobid
	 * @return
	 */
	public static int HiredFunctionTask(Context mContext, String hireid, String engage) {
		JSONObject object = new JSONObject();
		try {
			object.put("hire_id", hireid);
			object.put("engage", engage);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.SET_HIRER_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 发布求助回复
	 * 
	 * @param mContext
	 * @param struct
	 * @return
	 */
	public static int AskAnswerTask(Context mContext, AskAnswerStruct struct) {
		JSONObject object = new JSONObject();
		try {
			object.put("appeal_id", struct.mAskid);
			object.put("name", struct.mName);
			object.put("content", struct.mContent);
			object.put("call", struct.mCall);
			object.put("create_time", struct.mCreateTime);
			// if(struct.image!=null)
			// object.put("image", Utils.FileBase64String(struct.image));
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.SET_ADDREPLY_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 获取回复列表
	 */
	public static int GetAskAnswerTask(Context mContext, int askid) {
		PartTimeDB DB = PartTimeDB.getInstance(mContext);
		ArrayList<AskAnswerStruct> list = new ArrayList<AskAnswerStruct>();
		AskAnswerStruct struct;
		JSONObject object = new JSONObject();
		try {
			object.put("appeal_id", askid);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.SET_GETREPLY_API, object);
			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}

			if (state == 0) {
				try {
					struct = new AskAnswerStruct();
					JSONArray data = result.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = (JSONObject) data.opt(i);
						struct.mAskid = jsonObject.getInt("appeal_id");
						struct.mid = jsonObject.getInt("id");
						struct.mName = jsonObject.getString("name");
						struct.mCall = jsonObject.getString("call");
						struct.mContent = jsonObject.getString("content");
						struct.mImage = jsonObject.getString("thumb");
						struct.mCreateTime = jsonObject.getString("create_time");

						if (DB.IsExistAnswerInfo(struct.mid, mContext)) {
							DB.UpdateAnswerListInfo(mContext, struct);
						} else {
							DB.AddAnswerListInfo(mContext, struct);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return SvrInfo.SVR_RESULT_NULL_RESULT;
				}
			}

			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}

	/**
	 * 用户回复
	 * 
	 * @param mContext
	 * @return
	 */
	public static int FeedbackTask(Context mContext, String userid, String username, String content) {
		JSONObject object = new JSONObject();
		try {
			object.put("user_id", userid);
			object.put("username", username);
			object.put("content", content);
			object.put("check_code", JZ365);
			JSONObject result = Submit.Request(SvrInfo.SET_FEEDBACK_API, object);

			if (result == null) {
				return SvrInfo.SVR_RESULT_NULL_RESULT;
			}
			int state = result.getInt("status");
			if (state != 0) {
				return state;
			}
			return SvrInfo.SVR_RESULT_SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return SvrInfo.SVR_RESULT_NULL_RESULT;
		}

	}
}
