package com.parttime.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.parttime.parttimejob.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Communication_ListView_Adapter extends BaseAdapter{

	//www.xunlvshi.cn/project/jianzhi365/Uploads/images/avatar/头像图片名称
	public static final String PIC_PATH="http://www.xunlvshi.cn/project/jianzhi365/Uploads/avatar/";
	private Context context;
	private ArrayList<HashMap<String,String>> arraylist=null;
	private LayoutInflater mInflater;
	
	/*imageloader部分*/
	private DisplayImageOptions options;  
	private ImageLoader mImageLoader;
	
	public Communication_ListView_Adapter(Context context,ArrayList<HashMap<String,String>> list)
	{
		this.context=context;
		this.arraylist=list;
		initDisplayOptions();
		mInflater = LayoutInflater.from(context);
	}

	public void refersh()
	{
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
			convertView=mInflater.inflate(R.layout.commnuication_listview_item, null);
			holder.head_image=(ImageView)convertView.findViewById(R.id.communication_head_image);
			holder.commnuication_name=(TextView)convertView.findViewById(R.id.communication_name);
			holder.commnuication_info_count=(TextView)convertView.findViewById(R.id.communication_info_count);
			holder.commnuication_last_info=(TextView)convertView.findViewById(R.id.communication_info);
			holder.commnuication_time=(TextView)convertView.findViewById(R.id.communication_time);
		
			convertView.setTag(holder);
			convertView.setId(position);
		}else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		
//		holder.job_name.setText(String.valueOf(arraylist.get(position).get("job_name")));
////	holder.home_ratingBar.setRating(Integer.parseInt((String)arraylist.get(position).get("percent")));
		holder.commnuication_name.setText(String.valueOf(arraylist.get(position).get("name")));
//		holder.commnuication_last_info.setText(String.valueOf(arraylist.get(position).get("last_info")));
//		holder.commnuication_time.setText(String.valueOf(arraylist.get(position).get("last_time")));
//		holder.job_distance.setText(String.valueOf(arraylist.get(position).get("job_distance")));
		String pic_path=PIC_PATH+String.valueOf(arraylist.get(position).get("avatar"));
		
		mImageLoader.displayImage(pic_path, holder.head_image, options);  
		return convertView;
	}
	
	
	/**
	 * listview 所用的viewholder
	 * 
	 * @author huxixi
	 */
	public class ViewHolder {
		public ImageView head_image;
		public TextView commnuication_name;
		public TextView commnuication_last_info;
		public TextView commnuication_time;
		public TextView commnuication_info_count;

	}
	
	/**
	 * imageloader 加载图片展示，的一些参数的设置
	 * avatar_user_default 为默认图片
	 */
	@SuppressWarnings("deprecation")
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_launcher)	//设置正在加载图片
		//.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
		.showImageForEmptyUri(R.drawable.qq_icon)	
		.showImageOnFail(R.drawable.qq_icon)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(20))	//设置图片角度,0为方形，360为圆角
		.build();
		
		mImageLoader = ImageLoader.getInstance();
	}

}
