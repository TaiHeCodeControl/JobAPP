package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.parttime.parttimejob.R;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 关于365界面
 * @date 2015-1-21
 * @time 17:54
 * 
 */
public class Setting_About365_Activity extends Activity {

	private Context mContext;
	/** view 部分 ****************************************************/
	/* 标题部分 */
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	private TextView VersionNumber, EulaTextView;
	/** 常量部分 ****************************************************/
	/* 返回 */
	private static final int CLICK_TYPE_BACKBTN = 0;
	private static final int CLICK_TYPE_EULA = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_about365_layout);
		mContext = this;
		initViews();
	}

	private void initViews() {
		/** 标题部分 */
		CenterTitle = (TextView) findViewById(R.id.title_text);
		LeftBackBtn = (ImageView) findViewById(R.id.home_back);
		CenterTitle.setText("关于蛋壳儿");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		EulaTextView = (TextView) findViewById(R.id.eula_text_show_in_aboutus);
		EulaTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		VersionNumber = (TextView) findViewById(R.id.about_version_number);
		String versionname = Utils.getVersionName(mContext);
		//显示蛋壳儿版本信息，位置重复，还未确定位置
//		VersionNumber.setText(String.format(getResources().getString(R.string.text_show_version_name), versionname));

	/*	EulaTextView.setOnClickListener(new
			 MyOnClickListener(CLICK_TYPE_EULA));*/
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
		case CLICK_TYPE_EULA:
			Utils.intent2Class(mContext, EULA_Activity.class);
			break;
		default:
			break;
		}
	}
}
