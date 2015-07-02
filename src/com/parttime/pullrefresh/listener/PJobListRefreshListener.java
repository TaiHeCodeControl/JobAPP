package com.parttime.pullrefresh.listener;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.parttime.adapter.PublishJobListAdapter;
import com.parttime.data.PartTimeDB;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullToRefreshLayout.OnRefreshListener;
import com.parttime.struct.PartTimeStruct.CompanyPJobListStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
/**
 * 
 * @author 灰色的寂寞
 * @function 发布职位列表，实现上拉下拉功能
 * @date 2015-1-22
 * @time 16:30
 *
 */
public class PJobListRefreshListener implements OnRefreshListener {

	private List<CompanyPJobListStruct> mJobListtmp = new ArrayList<CompanyPJobListStruct>();;
	private List<CompanyPJobListStruct> mJobList = new ArrayList<CompanyPJobListStruct>();
	private PublishJobListAdapter mAdapter;
	private Handler mHandler;
	private Context mContext;
	private PartTimeDB DB;
	private long muserid;

	public PJobListRefreshListener(long userid,Context context,List<CompanyPJobListStruct> mlist,PublishJobListAdapter adapter,Handler handler){
		mContext = context;
		mHandler = handler;
		DB = PartTimeDB.getInstance(mContext);
		muserid =userid;
		mJobList = mlist;
		mAdapter = adapter;
	}
	
	
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
					new GetPJobListAsynTask().execute(0,(int)muserid);
				
				mAdapter.notifyDataSetChanged();
				// 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
		}.sendEmptyMessageDelayed(0, 2000);
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				int vcount = DB.getPjobListInfoCount(mContext);
				new GetJobListAsynTask().execute(vcount,(int)muserid);
				
				mAdapter.notifyDataSetChanged();
				// 千万别忘了告诉控件加载完毕了哦！
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 2000);
	}
	
	
	
	
	
	
	/**
	 * 获取求助列表
	 */
	public class GetPJobListAsynTask extends AsyncTask<Integer, Void, Boolean>{

		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode = SvrOperation.GetCompanyPJobInforeFreshTask(mContext, params[0], params[1]);				
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
			ProcessRefreshData();
			if(result== false){
				
				return ;
			}
			
		}
	}
	
	public void ProcessRefreshData(){
		mJobList = DB.getCompanyPJobInfoList(mContext);
		//因为下拉刷新后mAdapter.notifyDataSetChanged(); 没有起到作用，所以又重新掉了一遍从新执行
		Message msg = new Message();
		msg.what = 30000;
		mHandler.sendMessage(msg);
	}
	/*****************************************************************************************/
	/**
	 * 获取求助列表
	 */
	public class GetJobListAsynTask extends AsyncTask<Integer, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				mJobListtmp = SvrOperation.GetCompanyPJobInfoLoadTask(mContext, params[0],params[1]);
				
				if (mJobListtmp==null)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ProcessCardData();
			if(result== false){
				return ;
			}
			
		}
	}
	
	public void ProcessCardData(){
			if(mJobListtmp!=null && mJobListtmp.size()>0)
			mJobList.addAll(mJobListtmp);
	}
}
