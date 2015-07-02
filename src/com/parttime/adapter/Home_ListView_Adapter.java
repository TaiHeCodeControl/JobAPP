package com.parttime.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parttime.parttimejob.R;

public class Home_ListView_Adapter extends BaseAdapter{
	private Context context;
	private ArrayList<HashMap<String,Object>> arraylist=null;
	private LayoutInflater mInflater;
	
	public Home_ListView_Adapter(Context context,ArrayList<HashMap<String,Object>> arraylist)
	{
		this.context=context;
		this.arraylist=arraylist;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return arraylist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int  position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null)
		{
			holder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.home_listview_item, null);
			holder.home_list_image=(ImageView)convertView.findViewById(R.id.home_list_image);
			holder.home_list_time=(TextView)convertView.findViewById(R.id.home_list_time);
			holder.home_list_title=(TextView)convertView.findViewById(R.id.home_list_title);
//			holder.home_ratingBar=(RatingBar)convertView.findViewById(R.id.home_ratingBar);
			holder.invite_text=(TextView)convertView.findViewById(R.id.invite_text);
		
			convertView.setTag(holder);
			convertView.setId(position);
		}else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		
		holder.home_list_time.setText(String.valueOf(arraylist.get(position).get("time")));
//		holder.home_ratingBar.setRating(Integer.parseInt((String)arraylist.get(position).get("percent")));
		holder.invite_text.setText(String.valueOf(arraylist.get(position).get("employee")));
		
		return convertView;
	}
	/**
	 * listview 所用的viewholder
	 * 
	 * @author huxixi
	 */
	public class ViewHolder {
		public ImageView home_list_image;
		public TextView home_list_title;
		public TextView home_list_time;
		public RatingBar home_ratingBar;
		public TextView invite_text;

	}


}
