package com.parttime.pullrefresh.listener;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.parttime.adapter.Volunteer_ListView_Adapter;
import com.parttime.data.PartTimeDB;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullToRefreshLayout.OnRefreshListener;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.struct.PartTimeStruct.VolunteerStruct;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.Submit;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 针对志愿者列表，实现上拉下拉功能
 * @date 2015-1-22
 * @time 16:30
 *
 */
public class VolunteerPullfreshListener implements OnRefreshListener {

	private List<AskInfoStruct> maskListtmp = new ArrayList<AskInfoStruct>();
	private List<VolunteerStruct> mvolunteerListtmp = new ArrayList<VolunteerStruct>();;
	private List<AskInfoStruct> maskList = new ArrayList<AskInfoStruct>();
	private List<VolunteerStruct> mvolunteerList = new ArrayList<VolunteerStruct>();;
	private Volunteer_ListView_Adapter mAdapter;
	private Handler mHandler;
	private int mType;
	private Context mContext;
	private PartTimeDB DB;
	private long muserid;
	/*manage click */
	private static final int CLICK_TYPE_ASKHELP= 1;
	private static final int CLICK_TYPE_VOLUNTEER= 2;
	public VolunteerPullfreshListener(long userid,Context context,List<AskInfoStruct> asklist,List<VolunteerStruct> volunteerlist,Volunteer_ListView_Adapter adapter,int Type,Handler handler){
		mContext = context;
		mType = Type;
		mHandler = handler;
		DB = PartTimeDB.getInstance(mContext);
		if(mType == CLICK_TYPE_ASKHELP){
			maskList =asklist;
			mvolunteerList = null;
		}else if(mType == CLICK_TYPE_VOLUNTEER){
			maskList =null;
			mvolunteerList = volunteerlist;
		}
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
				switch(mType){
				case CLICK_TYPE_ASKHELP:
					new GetAskListRefreshAsynTask().execute(0);
					break;
				case CLICK_TYPE_VOLUNTEER:
					new GetVolunteerInfoRefreshAsynTask().execute(0);
					break;
				}
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
				
				switch(mType){
				case CLICK_TYPE_ASKHELP:
					LoadData(CLICK_TYPE_ASKHELP);
					break;
				case CLICK_TYPE_VOLUNTEER:
					LoadData(CLICK_TYPE_VOLUNTEER);
					break;
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
	
	
	
	
	
	
	/**
	 * 获取求助列表
	 */
	public class GetAskListRefreshAsynTask extends AsyncTask<Integer, Void, Boolean>{

		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
			ProcessRefreshData(CLICK_TYPE_ASKHELP);
			if(result== false){
				return ;
			}
			
		}
	}
	/**
	 *获取志愿者信息
	 */
	public class GetVolunteerInfoRefreshAsynTask extends AsyncTask<Integer, Void, Boolean>{
		long errcode = -1;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				errcode  = SvrOperation.GetVolunteerListTask(mContext, params[0]);
				
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
			ProcessRefreshData(CLICK_TYPE_VOLUNTEER);
			if(result== false){
				return ;
			}
		}
	}
	
	public void ProcessRefreshData(int type){
		if(type == CLICK_TYPE_ASKHELP){
			maskList = DB.getAskInfoList(mContext);
		}else if(type == CLICK_TYPE_VOLUNTEER){
			mvolunteerList = DB.getVolunteerInfoList(mContext);
		}
		//因为下拉刷新后mAdapter.notifyDataSetChanged(); 没有起到作用，所以又重新掉了一遍从新执行
		Message msg = new Message();
		msg.what = 20000;
		msg.arg1 = type;
		mHandler.sendMessage(msg);
	}
	/*****************************************************************************************/
	/**
	 * 上拉加载数据
	 * @param type
	 */
	public void LoadData(int type){
		if(type == CLICK_TYPE_ASKHELP){
			int count = DB.getAskInfoCount(mContext);
			new GetAskListAsynTask().execute(count+"");
		}else if(type == CLICK_TYPE_VOLUNTEER){
			int vcount = DB.getVolunteerInfoCount(mContext);
			new GetVolunteerInfoAsynTask().execute(vcount+"");
		}
	}
	/**
	 * 获取求助列表
	 */
	public class GetAskListAsynTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				maskListtmp = SvrOperation.GetAskingListTask(mContext, params[0]);
				
				if (maskListtmp==null)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ProcessCardData(CLICK_TYPE_ASKHELP);
			if(result== false){
				return ;
			}
			
		}
	}
	/**
	 *获取志愿者信息
	 */
	public class GetVolunteerInfoAsynTask extends AsyncTask<String, Void, Boolean>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mvolunteerListtmp  = SvrOperation.GetVolunteerListTask(mContext,  params[0]);
				
				if (mvolunteerListtmp==null)
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			ProcessCardData(CLICK_TYPE_VOLUNTEER);
			if(result== false){
				return ;
			}
		}
	}
	
	public void ProcessCardData(int type){
		if(type == CLICK_TYPE_ASKHELP){
			if(maskListtmp!=null && maskListtmp.size()>0)
			maskList.addAll(maskListtmp);
		}else if(type == CLICK_TYPE_VOLUNTEER){
			if(mvolunteerListtmp!=null && mvolunteerListtmp.size()>0)
			mvolunteerList.addAll(mvolunteerListtmp);
		}
	}
}
