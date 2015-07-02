package com.parttime.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.adapter.AskAnswer_Adapter;
import com.parttime.adapter.Volunteer_ListView_Adapter;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.listener.VolunteerPullfreshListener;
import com.parttime.struct.PartTimeStruct.AskAnswerStruct;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.DateUtil;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

public class Ask_Answer_Activity extends Activity{
	private Context mContext;
	private int mid =-1;
	private PartTimeDB DB ;
	
	private AskAnswer_Adapter askadapter;
	/**view 部分****************************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;

	private TextView AskMainTitle,AskContentText,AskTimeShow,AskMen,AskCall,AskAnswerBtn;
	private ImageView AskImageShow;
	private ListView AnswerContentList;      
	private EditText EditAnswerContent,GoodManContent,CallContent;
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_ANSWERBTN=1;
	private static final int CLICK_TYPE_SHOWLIST=2;
	
	private String AnswerContent="",GoodMan="",Call="";
	AskAnswerStruct struct = new AskAnswerStruct();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ask_answer_layout);
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

		AskAnswerBtn= (TextView)findViewById(R.id.answer_btn);
		EditAnswerContent= (EditText)findViewById(R.id.edit_answer_content);
		GoodManContent= (EditText)findViewById(R.id.goodman_content);
		CallContent= (EditText)findViewById(R.id.call_content);
		
		AnswerContentList= (ListView)findViewById(R.id.answer_content_list);
		AskAnswerBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ANSWERBTN));
		
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);
	
		CenterTitle.setText("求助回复");
		ShowAskDetailsBlock();
		
	}
	public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
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
//			AskInfoStruct struct = DB.getAskInfoByAskId(mContext, mid);
			break;
		case CLICK_TYPE_ANSWERBTN:
			AnswerContent = EditAnswerContent.getText().toString().trim();
			GoodMan =GoodManContent.getText().toString().trim();
			Call =CallContent.getText().toString().trim();
			if(AnswerContent.equals("")){
				Utils.ShowToast(mContext, "回复内容不能为空！");
			}else if(GoodMan.equals("")){
				Utils.ShowToast(mContext, "好心人不能为空！");
			}else if(Call.equals("")){
				Utils.ShowToast(mContext, "联系方式不能为空！");
			}else{
				struct.mAskid =mid;
				struct.mCall =Call;
				struct.mContent = AnswerContent;
				struct.mName = GoodMan;
				DateUtil data = new DateUtil();
				struct.mCreateTime = data.getDateString(System.currentTimeMillis());
				new SubmitAnswerAsynTask().execute(struct);
			}
			
			
			break;
		default:
			break;
		}
	}
	ArrayList<AskAnswerStruct> mlist= new ArrayList<AskAnswerStruct>();
	public Handler viewhandler = new Handler(){
		public void handleMessage(Message message) {
			switch (message.what) {
			case CLICK_TYPE_SHOWLIST:
				askadapter = new AskAnswer_Adapter(mContext,viewhandler,mlist);
				AnswerContentList.setAdapter(askadapter);
				break;
			}
			
		};
	};
	
	/**
	 * 获取求助列表
	 */
	public class GetAskListAsynTask extends AsyncTask<Integer, Void, Boolean>{
int errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在获取求助列表……");
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.GetAskAnswerTask(mContext, params[0]);
				
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
			if(result== false){
//					Utils.ShowToast(mContext, "获取列表失败，请重试！");
				return ;
			}
			mlist = DB.getAnswerList(mContext,mid);
			viewhandler.sendEmptyMessage(CLICK_TYPE_SHOWLIST);
		}
	}
	
	/**
	 * 显示求助部分实现对应的操作
	 */
	public void ShowAskDetailsBlock(){
		if(Submit.isNetworkAvailable(mContext))
			new GetAskListAsynTask().execute(mid);
		else
			Utils.ShowToast(mContext, "没有可用网络！");
	}
	
	/**
	 *提交回复内容
	 */
	public class SubmitAnswerAsynTask extends AsyncTask<AskAnswerStruct, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ShowWaiting("正在提交信息……");
		}
		@Override
		protected Boolean doInBackground(AskAnswerStruct... params) {
			try {
				errcode  = SvrOperation.AskAnswerTask(mContext,  params[0]);
				
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
			if(result== false){
					Utils.ShowToast(mContext, "回复失败，请重试！");
				return ;
			}
			EditAnswerContent.setText("");
			GoodManContent.setText("");
			CallContent.setText("");
			Utils.ShowToast(mContext, "提交成功！");
			ShowAskDetailsBlock();
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
