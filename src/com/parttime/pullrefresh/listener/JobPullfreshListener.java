package com.parttime.pullrefresh.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.parttime.adapter.Job_ListView_Adapter1;
import com.parttime.adapter.Volunteer_ListView_Adapter;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullToRefreshLayout.OnRefreshListener;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.Utils;
/**
 * 针对工作页实现上拉下拉功能
 * @author huxixi
 *
 */
public class JobPullfreshListener implements OnRefreshListener {

	private ArrayList<HashMap<String,Object>> mList;
	private ArrayList<HashMap<String,String>> mList1;
	private ArrayList<HashMap<String, Object>> part_list;
	private Job_ListView_Adapter1 mAdapter;
	private Context mContext=null;
	private PartTimeDB parttimeDb;
	private HashMap<String,String> map=null;
	private int count=0;
	private Handler load_handler=null;
	private Handler refresh_handler=null;
	private String flag=null;
	private ArrayList<Integer> location_list=new ArrayList<Integer>();
	public JobPullfreshListener(Context context,ArrayList<HashMap<String,Object>> list,Job_ListView_Adapter1 adapter){
		mList = list;
		mAdapter = adapter;
		this.mContext=context;
		parttimeDb=PartTimeDB.getInstance(mContext);
	}
	
	
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		flag="refresh";
		count=parttimeDb.getJobInfoNum(mContext);
		map=new HashMap<String, String>();
		
		map.put("count", String.valueOf(count));
		map.put("refresh", "1");
         new Job_Task().execute(map);
		// 下拉刷新操作
         refresh_handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mAdapter.refresh(part_list);
				// 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
		};
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		
		flag="load";
		count=parttimeDb.getJobInfoNum(mContext);
		map=new HashMap<String, String>();
		
		map.put("count", String.valueOf(count));
         new Job_Task().execute(map);
		// 加载操作
         load_handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mAdapter.refresh(part_list);
				// 千万别忘了告诉控件加载完毕了哦！
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		};
	}
	
	
	/**
	 * 工作列表的异步
	 * @author huxixi
	 *
	 */
	public class Job_Task extends AsyncTask<HashMap<String,String>, Void, Boolean> {
		
		
		private int errcode;
		
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String,String>... params) {
			
			
			
			int iresult = SvrOperation.Job_List(mContext,params[0]);
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
			mList1=parttimeDb.getJobInfo(mContext);
			
			
//			for(int i=0;i<mList1.size();i++)
//			{
//				String company_addr=String.valueOf(mList1.get(i).get("position"));
//				if(company_addr!=null)
//				{
//				String[] lat_lng=company_addr.split(",");
//				if(lat_lng!=null&&lat_lng.length==2)
//				{
//				double lat=Double.parseDouble(lat_lng[0]);
//				double lgn=Double.parseDouble(lat_lng[1]);
//				LatLng ll =BaiduMap_GetLatLng_Utils.mLocal_LatLng;
//				LatLng la = new LatLng(lat, lgn);
//				double meter=DistanceUtil.getDistance(la, ll);
//				location_list.add((int)meter);
//				}else
//				{
//					location_list.add(0);
//				}
//				}
//				
//			}
			
			
			/**************/
//			ArrayList<Integer> location_list=new ArrayList<Integer>();
			location_list.clear();
			for(int i=0;i<mList1.size();i++)
			{
				String company_addr=mList1.get(i).get("position");
				String[] lat_lng=company_addr.split(",");
				if(lat_lng!=null&&lat_lng.length==2)
				{
				double lat=Double.parseDouble(lat_lng[0]);
				double lgn=Double.parseDouble(lat_lng[1]);
				LatLng ll =BaiduMap_GetLatLng_Utils.mLocal_LatLng;
				LatLng la = new LatLng(lat, lgn);
				double meter=DistanceUtil.getDistance(la, ll);
				location_list.add((int)meter);
				}else
				{
					location_list.add(0);
				}
				
			}
			for(int i=0;i<mList1.size();i++)
			{
				HashMap<String,String> map=new HashMap<String, String>();
				map.put("hot_id", mList1.get(i).get("hot_id"));
				map.put("name", mList1.get(i).get("name"));
				map.put("charge", mList1.get(i).get("charge"));
				map.put("type", mList1.get(i).get("type"));
				map.put("num", mList1.get(i).get("num"));
				map.put("create_time", mList1.get(i).get("create_time"));
				map.put("company_name", mList1.get(i).get("company_name"));
				map.put("company_add", mList1.get(i).get("company_add"));
				map.put("company_level", mList1.get(i).get("company_level"));
				map.put("position", mList1.get(i).get("position"));
				map.put("company_id", mList1.get(i).get("company_id"));
				if(parttimeDb.IsExistJobInfo(mList1.get(i).get("hot_id"), mContext))
				{
					parttimeDb.UpdateJobInfo1(mContext, map, location_list.get(i),mList1.get(i).get("hot_id") );
				}else
				{
				parttimeDb.AddJobInfo1(mContext, map, location_list.get(i));
				}
			}
			 part_list=parttimeDb.getJobInfo1(mContext);
			/**************/
			if(flag.equals("refresh"))
			{
				refresh_handler.sendEmptyMessage(0);
			}else if(flag.equals("load"))
			{
			load_handler.sendEmptyMessage(0);
			}
			return;
		}
		
	}
}
