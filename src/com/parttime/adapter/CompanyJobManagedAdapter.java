package com.parttime.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.JobManagedStruct;

/**
 * 
 * @author 灰色的寂寞
 * @function 职位管理
 * @date 2015-2-4
 * @time 17:54
 *
 */
public class CompanyJobManagedAdapter extends BaseAdapter implements OnCheckedChangeListener{
	public static final String TAG = CompanyJobManagedAdapter.class.getSimpleName();
	private Context mContext;
	private PartTimeDB mPartTimeDB = null;
	private ArrayList<JobManagedStruct> mJobManagedList=null;
	private Handler mHandler =null;
	
	/*布局部分*/
	private LayoutInflater layoutInflater = null;
	private JobManagedListItem JobManagedItemView=null;
	
	/*imageloader部分*/
	private DisplayImageOptions options;  
	private ImageLoader mImageLoader;
	
	
	public CompanyJobManagedAdapter(Context context,Handler mhandler, ArrayList<JobManagedStruct> list){
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		initDisplayOptions();
		
		mJobManagedList =list;
		
		mHandler = mhandler;
	}
	
	@Override
	public int getCount() {
			return mJobManagedList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		if (convertView == null) {
			JobManagedItemView = new JobManagedListItem();
			convertView = layoutInflater.inflate(R.layout.company_job_managed_item_layout,null);
			JobManagedItemView.JobManagedItemBlock = (LinearLayout) convertView.findViewById(R.id.job_managed_item_block);
			JobManagedItemView.mUserName = (TextView) convertView.findViewById(R.id.job_managed_username);
			JobManagedItemView.mDistance = (TextView) convertView.findViewById(R.id.job_managed_distance);
			JobManagedItemView.mTime = (TextView) convertView.findViewById(R.id.job_managed_time);
//			JobManagedItemView.mHeadImage = (ImageView) convertView.findViewById(R.id.job_managed_personal_headimage);
			
			JobManagedItemView.mCheckbox = (CheckBox) convertView.findViewById(R.id.job_managed_checked);
			JobManagedItemView.mCheckbox.setOnCheckedChangeListener(this);
			convertView.setTag(JobManagedItemView);
		}else {
			JobManagedItemView = (JobManagedListItem) convertView.getTag();
		}
		JobManagedStruct JobManaged_item =mJobManagedList.get(position);
		if(JobManaged_item!=null){
			JobManagedItemView.mUserName.setText("员工："+JobManaged_item.mUserName);
			if(JobManaged_item.mTime!=null&&!JobManaged_item.mTime.equals(""))
			JobManagedItemView.mTime.setText(JobManaged_item.mTime);
			else
				JobManagedItemView.mTime.setText("00:00:00");
			if(JobManaged_item.mAddressDistance!=null && !JobManaged_item.mAddressDistance.equals(""))
				if(TextUtils.isDigitsOnly(JobManaged_item.mAddressDistance))
					JobManagedItemView.mDistance.setText(JobManaged_item.mAddressDistance+"/米");
				else
					JobManagedItemView.mDistance.setText(JobManaged_item.mAddressDistance);
			else
				JobManagedItemView.mDistance.setText("../米");
//			mImageLoader.displayImage(JobManaged_item.mUserHeadImage, JobManagedItemView.mHeadImage, options);
			JobManagedItemView.mCheckbox.setTag(JobManaged_item);
		}
		return convertView;
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean state) {
		switch(buttonView.getId()){
		case R.id.job_managed_checked:
			JobManagedStruct jmstruct= (JobManagedStruct)JobManagedItemView.mCheckbox.getTag();
			if(state){
				Message msg = new Message();
				msg.what = 1000;
				msg.obj = jmstruct;
				mHandler.sendMessage(msg);
			}else{
				Message msg = new Message();
				msg.what = -1000;
				msg.obj = jmstruct;
				mHandler.sendMessage(msg);
			}
			break;
		}
	}
	/**
	 * imageloader 加载图片展示，的一些参数的设置
	 * avatar_user_default 为默认图片
	 */
	@SuppressWarnings("deprecation")
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading_default_img)	//设置正在加载图片
		//.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
		.showImageForEmptyUri(R.drawable.drop_down_box_bg)	
		.showImageOnFail(R.drawable.loading_failed)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisc(true)
		//.displayer(new RoundedBitmapDisplayer(20))	//设置图片角度,0为方形，360为圆角
		.build();
		
		mImageLoader = ImageLoader.getInstance();
	}
	
	/**
	 * fucntion:收藏列表中的字段：图片，工作标题，工作类型，信誉星级，公司名称，时间，单价，距离图片，距离
	 */
	public static class JobManagedListItem{
		private LinearLayout JobManagedItemBlock;
		private TextView mUserName,mDistance,mTime;
		private ImageView mHeadImage;
		private CheckBox mCheckbox;
		
	}





}
