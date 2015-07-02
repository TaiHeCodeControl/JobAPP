package com.parttime.activity;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.parttime.activity.Comment_Activity.AddComment_Task;
import com.parttime.activity.Comment_Activity.GetComment_Task;
import com.parttime.activity.Comment_Activity.MyClickListener;
import com.parttime.adapter.Comment_Adapter;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.listener.CommentPullfreshListener;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;

public class Comment_Activity_Company extends Activity {

	private static final int BACK_INT = 1;
	private static final int PUBLISH_COMMENT_INT = 2;

	/**
	 * 标题
	 */
	private ImageView back_image = null;
	private TextView job_title = null;
	private View job_title_view = null;
	private Context mContext = null;
	private EditText comment_edit;
	private Button publish_comment;
	private RatingBar app_ratingbar;

	private Comment_Adapter comment_Adapter = null;
	private ArrayList<HashMap<String, Object>> list = null;

	private ListView comment_listview;// 列表listview
	
	
	private HashMap<String,String> input_map=null;
	private HashMap<String,String> get_comment_map=null;
	
	private ArrayList<HashMap<String, String>> data_list;

	private PullToRefreshLayout refreshableView;
	/* manage click */
	private static final int CLICK_TYPE_ASKHELP = 1;
	private static final int CLICK_TYPE_VOLUNTEER = 2;
	private static final int TYPE_OPERATION = CLICK_TYPE_ASKHELP;
	
	
	private SharedPreferences sharedPreferences;
	private String username="";
	private long personal_id;
	private String business_id="";
	private PartTimeDB parttimeDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment);
		mContext = this;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		parttimeDb=PartTimeDB.getInstance(mContext);
		business_id=getIntent().getStringExtra("business_id");
		input_map=new HashMap<String, String>();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		username=sharedPreferences.getString("login_username", "");
		personal_id=sharedPreferences.getLong("userid", 0);
		input_map.put("name", username);
		input_map.put("personal_id", business_id);
		input_map.put("business_id", String.valueOf(personal_id));
		initView();
		get_comment_map=new HashMap<String, String>();
		get_comment_map.put("count", "0");
		get_comment_map.put("personal_id", business_id);
		new GetComment_Task().execute(get_comment_map);
	}

	private void initView() {
		
		
		app_ratingbar=(RatingBar)findViewById(R.id.app_ratingbar);
		job_title_view = (View) findViewById(R.id.comment_title_view);
		back_image = (ImageView) job_title_view.findViewById(R.id.home_back);
		job_title = (TextView) job_title_view.findViewById(R.id.title_text);
		back_image.setVisibility(View.VISIBLE);
		job_title.setText(getResources().getString(R.string.comment));

		back_image.setOnClickListener(new MyClickListener(BACK_INT));

		/** 列表部分 */
		comment_listview = (ListView) findViewById(R.id.comment_listview);
		refreshableView = (PullToRefreshLayout) findViewById(R.id.comment_refreshable);

		viewhandler.sendEmptyMessage(TYPE_OPERATION);

		comment_edit = (EditText) findViewById(R.id.comment_edit);
		publish_comment = (Button) findViewById(R.id.publish_comment);
		publish_comment.setOnClickListener(new MyClickListener(
				PUBLISH_COMMENT_INT));

		
		app_ratingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					   boolean fromUser) {
				 final int numStars = ratingBar.getNumStars();
				 input_map.put("prestige", String.valueOf(rating));
			}
			
		});

	}


	public Handler viewhandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case CLICK_TYPE_ASKHELP:
				if(data_list!=null&&data_list.size()>0)
				{
				comment_Adapter = new Comment_Adapter(mContext, data_list);
				comment_listview.setAdapter(comment_Adapter);
				}

				break;
			case CLICK_TYPE_VOLUNTEER:
				break;
			}

			refreshableView.setOnRefreshListener(new CommentPullfreshListener(
					mContext, data_list, comment_Adapter,business_id,"company"));
			comment_listview.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					return true;
				}
			});

		};
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	/**
	 * 点击事件的类
	 * 
	 * @author huxixi
	 * 
	 */
	public class MyClickListener implements View.OnClickListener {
		int index;

		public MyClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View arg0) {
			switch (index) {
			case BACK_INT:
				Comment_Activity_Company.this.finish();
				break;
			case PUBLISH_COMMENT_INT:
				// TODO 发表评论的点击事件
				String comment=comment_edit.getText().toString();
				input_map.put("comment",comment);
				input_map.put("type", "1");
				new AddComment_Task().execute(input_map);
				break;
			}
		}

	}
	
	/**
	 * 发表评论的异步
	 * @author huxixi
	 *
	 */
	public class AddComment_Task extends AsyncTask<HashMap<String,String>, Void, Boolean> {
		
		
		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String,String>... params) {
			
			
			
			int iresult = SvrOperation.AddComments(mContext,params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode=iresult;
					return false;
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == false) {
				int errmsg = ErrorMsgSvr.ErrorMsg(errcode);
				Utils.ShowToast(mContext, errmsg);
				return;
			}
			Utils.ShowToast(mContext, "评论成功");
			comment_edit.setText("");
			app_ratingbar.setRating(0);
			new GetComment_Task().execute(get_comment_map);
			return;
		}
		
	}
	/**
	 * 获取评论列表的异步
	 * @author huxixi
	 *
	 */
	public class GetComment_Task extends AsyncTask<HashMap<String,String>, Void, Boolean> {
		
		
		private int errcode;
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(HashMap<String,String>... params) {
			
			
			
			int iresult = SvrOperation.GetComments(mContext,params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode=iresult;
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result == false) {
				int errmsg = ErrorMsgSvr.ErrorMsg(errcode);
				Utils.ShowToast(mContext, errmsg);
				return;
			}
			data_list=parttimeDb.getComments(mContext,business_id,"company");
			viewhandler.sendEmptyMessage(TYPE_OPERATION);
			return;
		}
		
	}

}
