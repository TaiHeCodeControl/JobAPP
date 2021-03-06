package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parttime.adapter.Personal_Collection_ListAdapter;
import com.parttime.parttimejob.R;
/**
 * 
 * @author 灰色的寂寞
 * @function 个人中心收藏
 * @date 2015-1-19
 * @time 17:54
 *
 */
public class Personal_Collection_Activity extends Activity{
	
	private Context mContext;
	private Personal_Collection_ListAdapter CollectionAdapter=null;
	/**view 部分****************************************************/
	/* 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private ListView CollectionList;
	/**常量部分****************************************************/
	/* 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_SHOWLIST=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_collection_layout);
		mContext = this;
		initViews();
		
		
		
	}

	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("收藏");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
		CollectionList = (ListView) findViewById(R.id.collection_content_list);
		viewhandler.sendEmptyMessage(CLICK_TYPE_SHOWLIST);
		
		
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
		default:
			break;
		}
	}
	
	public Handler viewhandler = new Handler(){
		public void handleMessage(Message message) {
			switch (message.what) {
			case CLICK_TYPE_SHOWLIST:
				CollectionAdapter = new Personal_Collection_ListAdapter(mContext,viewhandler);
				CollectionList.setAdapter(CollectionAdapter);
			}
		}
	};
}
