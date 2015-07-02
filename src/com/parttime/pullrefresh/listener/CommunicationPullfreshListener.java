package com.parttime.pullrefresh.listener;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.parttime.adapter.ApplyJob_ListView_Adapter;
import com.parttime.adapter.Communication_ListView_Adapter;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.pullrefresh.PullToRefreshLayout.OnRefreshListener;

public class CommunicationPullfreshListener implements OnRefreshListener {

	private ArrayList<HashMap<String,String>> mList;
	private Communication_ListView_Adapter mAdapter;
	public CommunicationPullfreshListener(Context context,ArrayList<HashMap<String,String>> list,Communication_ListView_Adapter adapter){
		mList = list;
		mAdapter = adapter;
	}
	
	
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				for(int i=0;i<10;i++)
				{
					
					
				HashMap<String,String> map=new HashMap<String, String>();
				map.put("name", "小康 "+i);
				map.put("last_info", "嗯"+i); 
				map.put("last_time","11:30");
				mList.add(map);
				}
				mAdapter.notifyDataSetChanged();
				// 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
		}.sendEmptyMessageDelayed(0, 2000);
	}


	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
//		// 加载操作
//		new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				for(int i=0;i<10;i++)
//				{
//					
//					
//				HashMap<String,Object> map=new HashMap<String, Object>();
//				map.put("job_seeker_name", "小康 "+i);
//				map.put("job_position", "服务员"+i); 
//				map.put("job_time","2015-01-30");
//				mList.add(map);
//				}
//				mAdapter.notifyDataSetChanged();
//				// 千万别忘了告诉控件加载完毕了哦！
//				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//			}
//		}.sendEmptyMessageDelayed(0, 2000);
//	}
}
