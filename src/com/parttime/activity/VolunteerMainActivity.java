package com.parttime.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.adapter.Volunteer_ListView_Adapter;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.listener.VolunteerPullfreshListener;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.struct.PartTimeStruct.VolunteerStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @date 2015-1-12
 * @time 10：48
 * @function 公益页面<br>
 * 两部分：1、求助 2、志愿者
 * <br>求助：图片、求助主题
 * <br> 志愿者：图片、标题、发布时间、报名人数（已有人数、总共人数）
 */
public class VolunteerMainActivity extends Activity{
	private static  Context mContext=null;
	private PartTimeDB DB ;
	ArrayList<AskInfoStruct> asklist = new ArrayList<AskInfoStruct>();
	ArrayList<VolunteerStruct> volunteerlist = new ArrayList<VolunteerStruct>();
	
	private RelativeLayout PublishCardBlock;
	private LinearLayout AskHelpTitleBlock;
	private TextView AskHelpCard,VolunteerCard,PublishBtn;
	private ListView VolunteerContentList;//列表listview
	private Volunteer_ListView_Adapter volunteerPageAdapter;//志愿者adapter
	
	private PullToRefreshLayout refreshableView;
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	/*manage click */
	private static final int CLICK_TYPE_ASKHELP= 1;
	private static final int CLICK_TYPE_VOLUNTEER= 2;
	private static final int CLICK_TYPE_PUBLISH_HELP= 3;
	private static final int CLICK_TYPE_BACKBTN= 4;
	
	
	private long userid=-1;
	private SharedPreferences sharedPreferences;
	
	/**
	 * 标题卡背景
	 */
	private static final int CLICK_TYPE_BACK_RIGHT_PRESSED= R.drawable.ask_vol_btn_block_back_pressed;
	private static final int CLICK_TYPE_BACK_LEFT_NORMAL= R.drawable.ask_vol_btn_block_back_normal;
	private static final int CLICK_TYPE_BACK_LEFT_PRESSED= R.drawable.ask_vol_btn_block_back_pressed1;
	private static final int CLICK_TYPE_BACK_RIGHT_NORMAL= R.drawable.ask_vol_btn_block_back_normal1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_volunteer_layout);
		mContext = this;
		DB = PartTimeDB.getInstance(mContext);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid = sharedPreferences.getLong("userid", -1);
		
		initViews();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_PERSONAL);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(Submit.isNetworkAvailable(mContext))
			LoadData();
		else{
			Utils.ShowToast(mContext, "没有可用网络，请联网后刷新查看！");
		}
	}
	
	
	
	/**
	 * 初始化view
	 * @param view
	 */
	private void initViews() {
		
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("公益");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		PublishCardBlock = (RelativeLayout) findViewById(R.id.publish_card_block);
		AskHelpTitleBlock = (LinearLayout) findViewById(R.id.ask_help_content_card);
		AskHelpCard = (TextView) findViewById(R.id.left_card_ask_help);
		VolunteerCard = (TextView) findViewById(R.id.right_card_volunteer);
		PublishBtn = (TextView) findViewById(R.id.volunteer_btn);
		
		AskHelpCard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ASKHELP));
		VolunteerCard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_VOLUNTEER));
		
		PublishBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_PUBLISH_HELP));
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
		/**列表部分*/
		VolunteerContentList = (ListView) findViewById(R.id.volunteer_content_list);
		refreshableView = (PullToRefreshLayout) findViewById(R.id.volunteer_refreshable);
	    
		LoadData();
		
	}
	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
	/**
	 * 加载数据
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void LoadData(){
		int loadtype =sharedPreferences.getInt("ask_volunteer", 0);
		if(loadtype==0){
			AskHelpCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_LEFT_PRESSED));
			VolunteerCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_RIGHT_NORMAL));
			if(Submit.isNetworkAvailable(mContext)){
				new GetAskListAsynTask().execute(0);
			}else{
				asklist = DB.getAskInfoList(mContext);
				volunteerlist = DB.getVolunteerInfoList(mContext);
				if(asklist!=null)
					viewhandler.sendEmptyMessage(CLICK_TYPE_ASKHELP);
			}
		}else{
			AskHelpCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_LEFT_NORMAL));
			VolunteerCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_RIGHT_PRESSED));
			if(Submit.isNetworkAvailable(mContext)){
				new GetVolunteerInfoAsynTask().execute(0);
			}else{
				asklist = DB.getAskInfoList(mContext);
				volunteerlist = DB.getVolunteerInfoList(mContext);
				if(volunteerlist!=null)
					viewhandler.sendEmptyMessage(CLICK_TYPE_VOLUNTEER);
			}
		}
	}
	public Handler viewhandler = new Handler(){
		public void handleMessage(Message message) {
			switch (message.what) {
			
			case CLICK_TYPE_ASKHELP:
				volunteerPageAdapter = new Volunteer_ListView_Adapter(mContext,asklist,null,CLICK_TYPE_ASKHELP);
				VolunteerContentList.setAdapter(volunteerPageAdapter);
				refreshableView.setOnRefreshListener(new VolunteerPullfreshListener(userid,mContext,asklist,null,volunteerPageAdapter,CLICK_TYPE_ASKHELP,viewhandler));
				break;
			case CLICK_TYPE_VOLUNTEER:
				volunteerPageAdapter = new Volunteer_ListView_Adapter(mContext,null,volunteerlist,CLICK_TYPE_VOLUNTEER);
				VolunteerContentList.setAdapter(volunteerPageAdapter);
				refreshableView.setOnRefreshListener(new VolunteerPullfreshListener(userid,mContext,null,volunteerlist,volunteerPageAdapter,CLICK_TYPE_VOLUNTEER,viewhandler));
				break;
			case 20000://处理下拉刷新后，列表没有改变问题
				int type = message.arg1;
				ProcessCardData(type);
				break;
			}
			
		};
	};
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
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_PERSONAL);
			finish();
			break;
		case CLICK_TYPE_ASKHELP:
			sharedPreferences.edit().putInt("ask_volunteer", 0).commit();//0代表类型是求助
			PublishCardBlock.setVisibility(View.VISIBLE);
			PublishBtn.setText("发布求助");
			AskHelpCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_LEFT_PRESSED));
			VolunteerCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_RIGHT_NORMAL));
			if(Submit.isNetworkAvailable(mContext))
				new GetAskListAsynTask().execute(0);
			ProcessCardData(CLICK_TYPE_ASKHELP);
			break;
		case CLICK_TYPE_VOLUNTEER:
			sharedPreferences.edit().putInt("ask_volunteer", 1).commit();//1代表类型是志愿者
			PublishCardBlock.setVisibility(View.GONE);
			PublishBtn.setText("志愿者发布");
			AskHelpCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_LEFT_NORMAL));
			VolunteerCard.setBackgroundDrawable(getResources().getDrawable(CLICK_TYPE_BACK_RIGHT_PRESSED));
			if(Submit.isNetworkAvailable(mContext))
			   new GetVolunteerInfoAsynTask().execute(0);
			ProcessCardData(CLICK_TYPE_VOLUNTEER);
			
			break;
			
		case CLICK_TYPE_PUBLISH_HELP:
			Utils.intent2Class(mContext, Volunteer_PublishHelp_Activity.class);
			break;
		default:
			break;
		}
	}
	
	
	
	/**
	 * 获取求助列表
	 */
	public class GetAskListAsynTask extends AsyncTask<Integer, Void, Boolean>{

		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取求助列表……");
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.GetAskingListTask(mContext, params[0]);
				
				if (errcode!=SvrInfo.SVR_RESULT_SUCCESS)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			HideWaiting();
			ProcessCardData(CLICK_TYPE_ASKHELP);
			if(result== false){
				if(errcode==-9){
					Utils.ShowToast(mContext, "没有求助列表！");
				}else
				Utils.ShowToast(mContext, "获取列表失败，请重试！");
				return ;
			}
			
		}
	}
	/**
	 *获取志愿者信息
	 */
	public class GetVolunteerInfoAsynTask extends AsyncTask<Integer, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取志愿列表……");
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode  = SvrOperation.GetVolunteerListTask(mContext,  params[0]);
				
				if (errcode!=SvrInfo.SVR_RESULT_SUCCESS)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			HideWaiting();
			ProcessCardData(CLICK_TYPE_VOLUNTEER);
			if(result== false){
				if(errcode==-9){
					Utils.ShowToast(mContext, "没有志愿者数据！");
				}else
					Utils.ShowToast(mContext, "获取列表失败，请重试！");
				return ;
			}
		}
	}
	
	public void ProcessCardData(int type){
		if(type == CLICK_TYPE_ASKHELP){
			asklist = DB.getAskInfoList(mContext);
			if(asklist!=null)
			viewhandler.sendEmptyMessage(CLICK_TYPE_ASKHELP);
		}else if(type == CLICK_TYPE_VOLUNTEER){
			volunteerlist = DB.getVolunteerInfoList(mContext);
			if(volunteerlist!=null)
			viewhandler.sendEmptyMessage(CLICK_TYPE_VOLUNTEER);
		}
	}
	
	
}
