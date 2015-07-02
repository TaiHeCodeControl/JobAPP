package com.parttime.pullrefresh.listener;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.parttime.adapter.PersonalManageAdapter;
import com.parttime.data.PartTimeDB;
import com.parttime.fragment.Fragment_PersonalManage_Page;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullToRefreshLayout.OnRefreshListener;
import com.parttime.struct.PartTimeStruct.HiredStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @fucntion 个人管理
 *
 */
public class PJEngagePullfreshListener implements OnRefreshListener {

	private List<HiredStruct> mHireList = new ArrayList<HiredStruct>();;
	private PersonalManageAdapter mAdapter;
	private Handler mHandler;
	private int mType;
	private Context mContext;
	private PartTimeDB DB;
	private long muserid;
	/*manage click */
	private static final int CLICK_TYPE_APPLIED= 1;
	private static final int CLICK_TYPE_HIRED= 2;
	private static final int CLICK_TYPE_DOING= 3;
	private static final int CLICK_TYPE_FINISHED= 4;
	public PJEngagePullfreshListener(long userid,Context context,List<HiredStruct> hirelist,PersonalManageAdapter adapter,int Type,Handler handler){
		mContext = context;
		mType = Type;
		mHandler = handler;
		DB = PartTimeDB.getInstance(mContext);
		mHireList = hirelist;
		muserid =userid;
		mAdapter = adapter;
	}
	
	
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(Submit.isNetworkAvailable(mContext)){
//					Fragment_PersonalManage_Page.ProcessCardUnEnable();
//					new GetHiredListInfoAsynTask().execute();
				}else{
					Utils.ShowToast(mContext, "没有可用网络！");
				}
				
				mAdapter.notifyDataSetChanged();
				if(Submit.isNetworkAvailable(mContext)){
					// 千万别忘了告诉控件加载完毕了哦！
					pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}else{
					Utils.ShowToast(mContext, "没有可用网络！");
					pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				}
			}
			
		}.sendEmptyMessageDelayed(0, 2000);
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
//				Fragment_PersonalManage_Page.ProcessCardUnEnable();
//				new GetHiredListInfoAsynTask().execute();
				mAdapter.notifyDataSetChanged();
				if(Submit.isNetworkAvailable(mContext)){
					// 千万别忘了告诉控件加载完毕了哦！
					pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
				}else{
					Utils.ShowToast(mContext, "没有可用网络！");
					pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
				}
			}
		}.sendEmptyMessageDelayed(0, 2000);
	}
	
	
	
	/**
	 * 获取雇佣关系列表存储到数据库
	 */
	public class GetHiredListInfoAsynTask extends AsyncTask<Long, Void, Boolean>{
		int errorcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(Long... params) {
			try {
				
				errorcode = SvrOperation.getTaskJobList(mContext);
				
				if (errorcode != SvrInfo.SVR_RESULT_SUCCESS)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result== false){
				return ;
			}
			
			mHandler.sendEmptyMessage(50000);
		}
	}
	
	
}
