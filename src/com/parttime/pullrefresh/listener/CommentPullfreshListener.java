package com.parttime.pullrefresh.listener;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.parttime.adapter.Comment_Adapter;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullToRefreshLayout.OnRefreshListener;
import com.parttime.pullrefresh.listener.JobPullfreshListener.Job_Task;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Utils;

public class CommentPullfreshListener implements OnRefreshListener {

	private ArrayList<HashMap<String, String>> mList;
	private Comment_Adapter mAdapter;
	private Context mContext = null;
	private PartTimeDB parttimeDb;
	private HashMap<String, String> map = null;
	private int count = 0;
	private Handler load_handler = null;
	private Handler refresh_handler = null;
	private String flag = null;
	private String business_id;
	private String cp_flag = null; // 公司和个人的标志

	public CommentPullfreshListener(Context context,
			ArrayList<HashMap<String, String>> list, Comment_Adapter adapter,
			String business_id, String flag) {
		mList = list;
		this.mContext = context;
		mAdapter = adapter;
		this.business_id = business_id;
		this.cp_flag = flag;
		parttimeDb = PartTimeDB.getInstance(mContext);
	}

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {

		flag = "refresh";
		count = parttimeDb.getCommentNum(mContext);
		map = new HashMap<String, String>();
		if (this.cp_flag.equals("company"))
			map.put("personal_id", business_id);
		else if (this.cp_flag.equals("personal"))
			map.put("business_id", business_id);
		map.put("count", String.valueOf(count));
		map.put("refresh", "1");
		new Get_Comment_Task().execute(map);
		// 下拉刷新操作
		refresh_handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (mAdapter != null)
					mAdapter.refresh(mList);
				// 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}

		};
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		flag = "load";
		count = parttimeDb.getCommentNum(mContext);
		map = new HashMap<String, String>();
		if (this.cp_flag.equals("company"))
			map.put("personal_id", business_id);
		else if (this.cp_flag.equals("personal"))
			map.put("business_id", business_id);

		map.put("count", String.valueOf(count));
		new Get_Comment_Task().execute(map);
		// 加载操作
		load_handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (mAdapter != null)
					mAdapter.refresh(mList);
				// 千万别忘了告诉控件加载完毕了哦！
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		};
	}

	/**
	 * 工作列表的异步
	 * 
	 * @author huxixi
	 * 
	 */
	public class Get_Comment_Task extends
			AsyncTask<HashMap<String, String>, Void, Boolean> {

		private int errcode;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String, String>... params) {

			int iresult = SvrOperation.GetComments(mContext, params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
				errcode = iresult;
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
			if (cp_flag.equals("company"))
				mList = parttimeDb.getComments(mContext,business_id,"company");
			else if (cp_flag.equals("personal"))
				mList = parttimeDb.getComments(mContext,business_id,"personal");
			if (flag.equals("refresh")) {
				refresh_handler.sendEmptyMessage(0);
			} else if (flag.equals("load")) {
				load_handler.sendEmptyMessage(0);
			}
			return;
		}

	}
}
