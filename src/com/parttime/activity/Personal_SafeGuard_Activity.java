package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.parttime.parttimejob.R;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-13
 * @time 16:32
 * @function 个人中心的保障部分
 *
 */
public class Personal_SafeGuard_Activity extends Activity  implements OnGetGeoCoderResultListener{
	private Context mContext;
	/** view 部分****************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private RelativeLayout PersonalAppeal,FirstPayed,BuyedSafeGuard;
	/** 数据部分****************************************/

	
	
	/** 常量部分****************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_PERSONAL_APPEAL=1;
	private static final int CLICK_TYPE_FIRSTPAYED=2;
	private static final int CLICK_TYPE_BUYEDSAFEGUARD=3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_safeguard_layout);
		
		mContext = this;
		
		initViews();
		initData();
	}
	


	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("保障");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		
		PersonalAppeal = (RelativeLayout)findViewById(R.id.rl_personal_appeal_block);
		FirstPayed = (RelativeLayout)findViewById(R.id.rl_first_payed_block);
		BuyedSafeGuard = (RelativeLayout)findViewById(R.id.rl_buyed_safeguard_block);
		
		PersonalAppeal.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PERSONAL_APPEAL));
		FirstPayed.setOnClickListener(new MyOnClickListener(CLICK_TYPE_FIRSTPAYED));
		BuyedSafeGuard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BUYEDSAFEGUARD));
	}
	private void initData() {
		
	}
	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener{
		private int mtype=-1;//默认为-1，什么也不做
		public MyOnClickListener(int type){
			mtype = type;
		}
		@Override
		public void onClick(View v) {
			ManageClick(mtype);
		}
		
	}
	
	/**
	 * 通过判断type，管理click事件
	 * @param mtype
	 */
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_PERSONAL_APPEAL:
	
			Intent intent = new Intent();
			intent.setClass(mContext, Personal_FeadBack_Activity.class);
			intent.putExtra("type", "appeal");
			startActivity(intent);
			break;
		case CLICK_TYPE_FIRSTPAYED:
			Utils.ShowToast(mContext, "谢谢，暂无此功能");
			// 初始化搜索模块，注册事件监听
//			GeoCoder mSearch = GeoCoder.newInstance();
//			mSearch.setOnGetGeoCodeResultListener(this);
//			mSearch.geocode(new GeoCodeOption().city("北京市").address("北京市昌平区北清路1号"));
			break;
		case CLICK_TYPE_BUYEDSAFEGUARD:
			Utils.ShowToast(mContext, "谢谢，暂无此功能");
			break;
		default:
			break;
		}

	}



	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Utils.ShowToast(mContext, "抱歉，未能找到结果");
			return;
		}
		
		Intent intent = new Intent();
		intent.setClass(mContext,  BaiduMap_Activity.class);
		intent.putExtra("lat", result.getLocation().latitude);
		intent.putExtra("lng", result.getLocation().longitude);
		startActivity(intent);
	}

	 @Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Utils.ShowToast(mContext,"抱歉，未能找到结果");
			return;
		}
	}
}
