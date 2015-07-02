package com.parttime.msg;

import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;

/**
 * 
 * @author 灰色的寂寞
 * @function 通过判断从服务器返回来的错误码，返回对应的错误信息
 * @date 2015-1-18
 * @time 13:52
 * 
 */
public class ErrorMsgSvr {
	public static int ErrorMsg(int errorcode) {
		// int errmsg = R.string.unknownerror;
		int errmsg = 0;
		switch (errorcode) {
		// 注册错误信息，处理
		case SvrInfo.SVR_REGISTER_USER_EXIST:
			errmsg = R.string.SVR_REGISTER_USER_EXIST;
			break;
		case SvrInfo.SVR_REGISTER_AVATAR_FAILED:
			errmsg = R.string.SVR_REGISTER_AVATAR_FAILED;
			break;
		case SvrInfo.SVR_REGISTER_DATA_FAILED:
			errmsg = R.string.SVR_REGISTER_DATA_FAILED;
			break;
		case SvrInfo.SVR_LOGIN_FAILED:
			errmsg = R.string.SVR_LOGIN_FAILED;
			break;
		case SvrInfo.SVR_EMAIL_FORMAT_IS_NOT_CORRECT:
			errmsg = R.string.SVR_EMAIL_FORMAT_IS_NOT_CORRECT;
			break;
		case SvrInfo.SVR_DATA_LOOK_FAILED:
			errmsg = R.string.SVR_DATA_LOOK_FAILED;
			break;
		case SvrInfo.SVR_OLDPWD_ERROR:
			errmsg = R.string.SVR_OLDPWD_ERROR;
			break;
		case SvrInfo.SVR_OLDPWD_NEWPWD_SAME:
			errmsg = R.string.SVR_OLDPWD_NEWPWD_SAME;
			break;
		default:
			errmsg = R.string.unknownerror;
			break;

		}
		return errmsg;
	}
}
