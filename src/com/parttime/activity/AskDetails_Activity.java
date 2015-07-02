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
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 求助的详情页面
 * @date 2015-1-19
 * @time 17:54
 *
 */
public class AskDetails_Activity extends Activity{
	private Context mContext;
	private int type =-1;
	private int mid =-1;
	private PartTimeDB DB ;
	/**view 部分****************************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;

	private TextView AskMainTitle,AskContentText,AskTimeShow,AskMen,AskCall,AskAnswerBtn;
	private ImageView AskImageShow;
	private RelativeLayout AskSchoolImagesBlock;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_ANSWERBTN=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.askdetails_layout);
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
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));

		AskMainTitle = (TextView)findViewById(R.id.ask_maintitle);
		AskContentText = (TextView)findViewById(R.id.ask_content_text);
		AskTimeShow = (TextView)findViewById(R.id.ask_time_show);
		AskMen = (TextView)findViewById(R.id.ask_men);
		AskCall = (TextView)findViewById(R.id.ask_call);
		AskAnswerBtn= (TextView)findViewById(R.id.ask_details_answer_btn);
        
		AskAnswerBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ANSWERBTN));
        
		AskImageShow = (ImageView)findViewById(R.id.ask_image_show);
		AskSchoolImagesBlock = (RelativeLayout)findViewById(R.id.rl_ask_school_images_block);

		CenterTitle.setText("求助详情");
		ShowAskDetailsBlock();
		
		
		
		
		
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
			Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_MANAGER);
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_ANSWERBTN:
			Intent intent = new Intent();
			intent.setClass(mContext, Ask_Answer_Activity.class);
			intent.putExtra("mid", mid);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 显示求助部分实现对应的操作
	 */
	public void ShowAskDetailsBlock(){
		AskInfoStruct struct = DB.getAskInfoByAskId(mContext, mid);
		if(struct!=null){
			AskMainTitle.setText(struct.mTheme);
			AskMen.setText("求助人："+struct.mName);
			AskCall.setText("联系方式："+struct.mCall);
			AskTimeShow.setText("时间："+struct.mCreateTime);
			AskContentText.setText("   求助内容："+struct.mContent);
			mImageLoader.displayImage(struct.mImage, AskImageShow, options);
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
