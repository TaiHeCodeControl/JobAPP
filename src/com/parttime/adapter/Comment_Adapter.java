package com.parttime.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parttime.parttimejob.R;
/**
 * 评论的adapter
 * @author Administrator
 *
 */
public class Comment_Adapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<HashMap<String,String>> arraylist=null;
	private LayoutInflater mInflater;
	
	public Comment_Adapter(Context context,ArrayList<HashMap<String,String>> list)
	{
		
		this.mContext=context;
		this.arraylist=list;
		mInflater = LayoutInflater.from(context);
	}

	public void refresh(ArrayList<HashMap<String,String>> list)
	{
		this.arraylist=list;
		this.notifyDataSetChanged();
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
			convertView=mInflater.inflate(R.layout.comment_listview_item, null);
			holder.comment_info=(TextView)convertView.findViewById(R.id.comment_info);
			holder.comment_content=(TextView)convertView.findViewById(R.id.comment_content);
			holder.comment_text=(TextView)convertView.findViewById(R.id.comment_text);
			holder.comment_rating=(ProgressBar)convertView.findViewById(R.id.comment_rating);
			holder.comment_time=(TextView)convertView.findViewById(R.id.comment_time);
			
			
		
			convertView.setTag(holder);
			convertView.setId(position);
		}else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		
		
//		map.put("business_id", business_id);
//		map.put("comment", comment);
//		map.put("id", id);
//		map.put("name", name);
//		map.put("personal_id", personal_id);
//		map.put("prestige", prestige);
//		map.put("create_time", create_time);
		
		String prestige=arraylist.get(position).get("prestige");
		if(prestige.equals("null")||prestige==null)
			prestige="0";
		int pre=Integer.valueOf(prestige);
		String comment_text="";
		if(pre>3)
		{
			comment_text="好评";
		}else if(pre==3)
		{
			comment_text="中评";
		}else if(pre<3)
		{
			comment_text="差评";
		}
		
		holder.comment_info.setText(String.valueOf(arraylist.get(position).get("name")));
		holder.comment_content.setText(String.valueOf(arraylist.get(position).get("comment")));
		holder.comment_text.setText(comment_text);
		holder.comment_rating.setProgress(pre);
		holder.comment_time.setText(String.valueOf(arraylist.get(position).get("create_time")));
		
		
		
		return convertView;
	}
	
	/**
	 * listview 所用的viewholder
	 * 
	 * @author huxixi
	 */
	public class ViewHolder {
		public TextView comment_info;
		public TextView comment_content;
		public TextView comment_text;
		public ProgressBar comment_rating;
		public TextView comment_time;

	}

	
}
