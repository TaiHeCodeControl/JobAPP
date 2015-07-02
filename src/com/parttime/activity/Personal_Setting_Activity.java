package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.filedownload.DownLoadDialog;
import com.parttime.filedownload.DownloadService;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.VersionInfoStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author hiuse的寂寞
 * @function 个人中心的设置界面
 * @date 2015-1-20
 * @time 11：20
 * 
 */
public class Personal_Setting_Activity extends Activity {
	private Context mContext;
	/** view 部分 ****************************************************/
	/* 标题部分 */
	private TextView CenterTitle;
	private ImageView LeftBackBtn;

	private RelativeLayout AccountSafe, Update, About365, Feadback;

	/* 对话框部分 */
	private RelativeLayout DialogBlock;
	private TextView DialogTitle, DialogCancel, DialogEnsure, DialogContent;

	/** 常量部分 ****************************************************/
	/* 返回 */
	private static final int CLICK_TYPE_BACKBTN = 0;
	private static final int CLICK_TYPE_ACCOUNTSAGFE = 1;
	private static final int CLICK_TYPE_UPDATE = 2;
	private static final int CLICK_TYPE_ABOUT365 = 3;
	private static final int CLICK_TYPE_FEADBACK = 4;

	private static final int CLICK_TYPE_DIALOG_CANCEL = 5;
	private static final int CLICK_TYPE_DIALOG_ENSURE = 6;

	private int code = 1;
	private String apkpath = "http://www.xunlvshi.cn/apk/PartTimeJob.apk";
	private VersionInfoStruct struct = new VersionInfoStruct();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_setting_layout);
		mContext = this;
		code = Utils.getVersionCode(mContext);
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent();
		intent.setClass(mContext, DownloadService.class);
		mContext.startService(intent);

	}

	private void initViews() {
		/** 标题部分 */
		CenterTitle = (TextView) findViewById(R.id.title_text);
		LeftBackBtn = (ImageView) findViewById(R.id.home_back);
		CenterTitle.setText("设置");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));

		AccountSafe = (RelativeLayout) findViewById(R.id.rl_account_safe_block);
		Update = (RelativeLayout) findViewById(R.id.rl_update_block);
		About365 = (RelativeLayout) findViewById(R.id.rl_about_365_block);
		Feadback = (RelativeLayout) findViewById(R.id.rl_feadback_block);

		AccountSafe.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ACCOUNTSAGFE));
		Update.setOnClickListener(new MyOnClickListener(CLICK_TYPE_UPDATE));
		About365.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ABOUT365));
		Feadback.setOnClickListener(new MyOnClickListener(CLICK_TYPE_FEADBACK));

		DialogBlock = (RelativeLayout) findViewById(R.id.edit_dialog_layout);
		DialogTitle = (TextView) findViewById(R.id.edit_dialog_title);
		DialogCancel = (TextView) findViewById(R.id.edit_cancel);
		DialogEnsure = (TextView) findViewById(R.id.edit_ensure);
		DialogContent = (TextView) findViewById(R.id.dialog_msg_info);
		DialogContent.setVisibility(View.VISIBLE);

		DialogCancel.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_CANCEL));
		DialogEnsure.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_ENSURE));
	}

	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener {
		private int mtype = -1;// 默认为-1，什么也不做

		public MyOnClickListener(int type) {
			mtype = type;
		}

		@Override
		public void onClick(View v) {
			ManageClick(mtype);
		}

	}

	/**
	 * 通过判断type，管理click事件
	 * 
	 * @param mtype
	 */
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_ACCOUNTSAGFE:
			Utils.intent2Class(mContext, Setting_AccountSafe_Activity.class);
			break;
		case CLICK_TYPE_UPDATE:
			if (Submit.isNetworkAvailable(mContext)) {
				new GetAppNewVersionFromSvr().execute();
			} else {
				Utils.ShowToast(mContext, "没有可用网络!");
			}
			break;
		case CLICK_TYPE_ABOUT365:
			Utils.intent2Class(mContext, Setting_About365_Activity.class);
			break;
		case CLICK_TYPE_FEADBACK:
			Intent intent = new Intent();
			intent.setClass(mContext, Personal_FeadBack_Activity.class);
			intent.putExtra("type", "feadback");
			startActivity(intent);
			break;
		case CLICK_TYPE_DIALOG_CANCEL:
			DialogBlock.setVisibility(View.GONE);
			break;
		case CLICK_TYPE_DIALOG_ENSURE:
			Utils.ShowToast(mContext, "谢谢");
			break;
		default:
			break;
		}
	}

	/**
	 * @function 从服务器获取新的版本号，然后对比当前的版本code 大：更新 小：弹出提示为最新版本 Demo:{“errorcode”:
	 *           -9} //获取失败 Demo:{“errorcode”: -10} //不需要更新
	 */
	public class GetAppNewVersionFromSvr extends AsyncTask<Void, Void, Boolean> {
		long errcode = -1;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			if (struct == null)
				struct = new VersionInfoStruct();
			try {
				errcode = SvrOperation.GetAppNewVersion(mContext, code, struct);

				if (errcode != SvrInfo.SVR_RESULT_SUCCESS)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == false) {
				if (errcode == -999)
					Utils.ShowToast(mContext, "已是最新版本");
				return;
			}
			DownLoadDialog.DownLoadApkTips(mContext, apkpath, struct);
		}
	}

}
