package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.parttime.parttimejob.R;

/**
 * 
 * @author 灰色的寂寞
 * @function 联系客服
 * @date 2015-1-20
 * @time 11-19
 * 
 */
public class Personal_SpecialCustomer_Activity extends Activity {

	private Context mContext;
	/** view 部分 ****************************************************/
	/* 标题部分 */
	private TextView CenterTitle, SpecialTips;
	private ImageView LeftBackBtn;

	/** 常量部分 ****************************************************/
	/* 返回 */
	private static final int CLICK_TYPE_BACKBTN = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_specialcustomer_activity);
		mContext = this;
		initViews();
	}

	private void initViews() {
		/** 标题部分 */
		CenterTitle = (TextView) findViewById(R.id.title_text);
		LeftBackBtn = (ImageView) findViewById(R.id.home_back);
		CenterTitle.setText("特惠商户");
		SpecialTips = (TextView) findViewById(R.id.special_tips);
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));

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
		default:
			break;
		}
	}
}
