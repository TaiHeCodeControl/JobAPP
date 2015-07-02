package com.parttime.pullrefresh.listener;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.parttime.adapter.ApplyJob_ListView_Adapter;
import com.parttime.data.PartTimeDB;
import com.parttime.msg.ErrorMsgSvr;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullToRefreshLayout.OnRefreshListener;
import com.parttime.svr.SvrInfo;
import com.parttime.svr.SvrOperation;
import com.parttime.utils.BaiduMap_GetLatLng_Utils;
import com.parttime.utils.Utils;
/**
 * @function： 实现求职列表的上拉下拉刷新功能
 * @author huxixi
 *
 */
public class ApplyJobPullfreshListener implements OnRefreshListener {

	private ArrayList<HashMap<String,Object>> mList;
	private ArrayList<HashMap<String,String>> data_list;
	private ApplyJob_ListView_Adapter mAdapter;
	private Context mContext=null;
	private PartTimeDB parttimeDb;
	private HashMap<String,String> map=null;
	private int count=0;
	private Handler load_handler=null;
	private Handler refresh_handler=null;
	private String flag=null;
	private ArrayList<Integer> location_list=new ArrayList<Integer>();
	public ApplyJobPullfreshListener(Context context,ArrayList<HashMap<String,Object>> list,ApplyJob_ListView_Adapter adapter){
		mList = list;
		mAdapter = adapter;
		this.mContext=context;
		parttimeDb=PartTimeDB.getInstance(mContext);
	}
	
	
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		flag="refresh";
		count=parttimeDb.getPublicResumeCount(mContext);
		map=new HashMap<String, String>();
		
		map.put("count", String.valueOf(count));
		map.put("refresh", "1");
         new GetPublicResume_Task().execute(map);
		// 下拉刷新操作
         refresh_handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mAdapter.refresh(mList);
				// 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
		};
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		flag="load";
		count=parttimeDb.getPublicResumeCount(mContext);
		map=new HashMap<String, String>();
		
		map.put("count", String.valueOf(count));
         new GetPublicResume_Task().execute(map);
		// 加载操作
         load_handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mAdapter.refresh(mList);
				// 千万别忘了告诉控件加载完毕了哦！
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		};
	}
	
	
	/**
	 * 获取求职信息列表的异步
	 * @author huxixi
	 *
	 */
	public class GetPublicResume_Task extends AsyncTask<HashMap<String,String>, Void, Boolean> {
		
		
		private int errcode;
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(HashMap<String,String>... params) {
			int iresult = SvrOperation.getPublicResume(mContext,params[0]);
			if (iresult != SvrInfo.SVR_RESULT_SUCCESS) {
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
			data_list=parttimeDb.getPublicReusmeInfo(mContext);
			
//			for(int i=0;i<mList.size();i++)
//			{
//				String company_addr=String.valueOf(mList.get(i).get("position"));
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
			
			/***************/
			location_list.clear();
//			ArrayList<Integer> location_list=new ArrayList<Integer>();
			for(int i=0;i<data_list.size();i++)
			{
				String company_addr=data_list.get(i).get("position");
				if(company_addr!=null)
				{
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
				}else
				{
					location_list.add(0);
					
				}
				HashMap<String,String> map=new HashMap<String, String>();
				map.put("height", data_list.get(i).get("height"));
				map.put("type",  data_list.get(i).get("type"));
				map.put("name",  data_list.get(i).get("name"));
				map.put("sex",  data_list.get(i).get("sex"));
				map.put("start_time",  data_list.get(i).get("start_time"));
				map.put("experence",  data_list.get(i).get("experence"));
				map.put("addr",  data_list.get(i).get("addr"));
				map.put("job", data_list.get(i).get("job") );
				map.put("rid",  data_list.get(i).get("rid"));
				map.put("user_id",  data_list.get(i).get("user_id"));
				map.put("call",  data_list.get(i).get("call"));
				map.put("public",  data_list.get(i).get("public"));
				map.put("end_time",  data_list.get(i).get("end_time"));
				map.put("create_time",  data_list.get(i).get("create_time"));
				map.put("position",  data_list.get(i).get("position"));
				if(parttimeDb.IsExistPublicResumeInfo(data_list.get(i).get("rid"), mContext))
				{
					parttimeDb.UpdatePublicResumeInfo1(mContext, map, location_list.get(i), data_list.get(i).get("rid"));
				}else{
					parttimeDb.AddPublicCustomerInfo1(mContext, map, location_list.get(i));
				}
				
				
			}
			 mList=	parttimeDb.getPublicReusmeInfo1(mContext);
			/***************/
			
			
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
