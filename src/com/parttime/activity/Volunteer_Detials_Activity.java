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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.struct.PartTimeStruct.VolunteerStruct;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 求助的详情页面
 * @date 2015-1-19
 * @time 17:54
 *
 */
public class Volunteer_Detials_Activity extends Activity{
	private Context mContext;
	private int type =-1;
	private int mid =-1;
	private PartTimeDB DB ;
	/**view 部分****************************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private TextView VolunteerMainTitle,VolunteerContentText,VolunteerTimeShow,SignUpBtn;
	private ImageView VolunteerImageShow,VolunteerSchoolImageFirst,VolunteerSchoolImageSecond,VolunteerSchoolImageThird,VolunteerSchoolImageFourth;
	private RelativeLayout SignUpBlock,VolunteerSchoolImagesBlock;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_SIGNUP=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.volunteer_details_layout);
		mContext = this;
		DB =PartTimeDB.getInstance(mContext); 
		Intent intent = getIntent();
		mid = intent.getIntExtra("mid", -1);
		
		initDisplayOptions();
		initViews();
		
	}
	
	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("志愿者详情");
		
		
		VolunteerMainTitle = (TextView)findViewById(R.id.volunteer_maintitle);
		VolunteerContentText = (TextView)findViewById(R.id.volunteer_content_text);
		VolunteerTimeShow = (TextView)findViewById(R.id.volunteer_time_show);
		SignUpBtn = (TextView)findViewById(R.id.sign_up_btn);
		
		VolunteerImageShow = (ImageView)findViewById(R.id.volunteer_image_show);
		VolunteerSchoolImageFirst = (ImageView)findViewById(R.id.volunteer_schoolimage_first_show);
		VolunteerSchoolImageSecond = (ImageView)findViewById(R.id.volunteer_schoolimage_second_show);
		VolunteerSchoolImageThird = (ImageView)findViewById(R.id.volunteer_schoolimage_third_show);
		VolunteerSchoolImageFourth = (ImageView)findViewById(R.id.volunteer_schoolimage_fourth_show);
		VolunteerSchoolImagesBlock = (RelativeLayout)findViewById(R.id.rl_volunteer_school_images_block);
		VolunteerSchoolImagesBlock = (RelativeLayout)findViewById(R.id.rl_volunteer_school_images_block);
		SignUpBlock = (RelativeLayout)findViewById(R.id.rl_sign_up_block);

		
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		SignUpBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_SIGNUP));
		ShowVolunteerDetailsBlock();
		
		
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
		case CLICK_TYPE_SIGNUP:
				Intent intent = new Intent();
				intent.setClass(mContext, VolunteerSignUpActivity.class);
				intent.putExtra("mid",mid);//志愿者id
				intent.putExtra("flag",0);//0:不用记住fragment的flag
				startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 显示志愿者部分实现对应的操作
	 */
	public void ShowVolunteerDetailsBlock(){
		SignUpBlock.setVisibility(View.VISIBLE);
		VolunteerStruct mStruct = DB.getVolunteerInfoByVolunteerid(mContext, mid);
		if(mStruct!=null){
			VolunteerMainTitle.setText(mStruct.mTheme);
			VolunteerContentText.setText("简介："+mStruct.mContent);
			mImageLoader.displayImage(mStruct.mThumb, VolunteerImageShow, options);
		}
		
	}
	
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	/**
	 * imageloader 加载图片展示，的一些参数的设置 avatar_user_default 为默认图片
	 */
	@SuppressWarnings("deprecation")
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading_1)
		// 设置正在加载图片
		// .showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
		.showImageForEmptyUri(R.drawable.load_failing)
		.showImageOnFail(R.drawable.load_failing)
		// 设置加载失败图片
		.cacheInMemory(true).cacheOnDisc(true)
		//.displayer(new RoundedBitmapDisplayer(20)) // 设置图片角度,0为方形，360为圆角
		.build();

		mImageLoader = ImageLoader.getInstance();
	}
}
