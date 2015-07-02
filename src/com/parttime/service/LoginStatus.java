package com.parttime.service;

import com.parttime.activity.LoginActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

public class LoginStatus {
	private static LoginStatus mLoginStatus = null;

	private boolean isLogin = false;

	private LoginStatus() {
	}

	public static LoginStatus getLoginStatus() {
		if (mLoginStatus == null) {
			mLoginStatus = new LoginStatus();
		}
		return mLoginStatus;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public void showDialog(final Activity mActivity) {
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setMessage("查看更多内容,请先登录！");
		builder.setTitle("提示");
		builder.setPositiveButton("幸福登录", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(mActivity, LoginActivity.class);
				mActivity.startActivity(intent);
//				mActivity.finish();
			}
		});

		builder.setNegativeButton("残忍拒绝", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		builder.create().show();
	}
}
