package com.parttime.msg;

import com.parttime.parttimejob.R;
import com.parttime.svr.SvrInfo;

/**
 * 判断短信验证码所反返回的错误码
 * @author huxixi
 *
 */
public class ErrorMsgSvrMsg {
	public static int ErrorMsg(int errorcode) {
//		int errmsg = R.string.unknownerror;
		int errmsg=0;
		switch (errorcode) {
		//注册错误信息，处理
		case SvrInfo.SVR_MSG_ACCOUNT_PWD_IN_NULL:
			errmsg = R.string.SVR_MSG_ACCOUNT_PWD_IN_NULL;
			break;	
		case SvrInfo.SVR_MSG_PHONE_NULL:
			errmsg = R.string.SVR_MSG_PHONE_NULL;
			break;	
		case SvrInfo.SVR_MSG_CONTENT_IN_NULL:
			errmsg = R.string.SVR_MSG_CONTENT_IN_NULL;
			break;	
		case SvrInfo.SVR_MSG_CONTENT_IS_LONG:
			errmsg = R.string.SVR_MSG_CONTENT_IS_LONG;
			break;	
		case SvrInfo.SVR_MSG_PHONE_IS_LONG:
			errmsg = R.string.SVR_MSG_PHONE_IS_LONG;
			break;	
		case SvrInfo.SVR_MSG_SYSTEM_ERROR:
			errmsg = R.string.SVR_MSG_SYSTEM_ERROR;
			break;	
		}
		return errmsg;
		}

}
