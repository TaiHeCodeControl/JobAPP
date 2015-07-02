package com.parttime.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.activity.Personal_Task_Activity;
import com.parttime.activity.Personal_Task_Portrait_Activity;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.JobManagedStruct;
/**
 * 
 * @author 灰色的寂寞
 * @function 用工管理
 * @date 2015-2-4
 * @time 17:54
 *
 */
public class CompanyEmplymentAdapter extends BaseAdapter implements OnClickListener{
	public static final String TAG = CompanyEmplymentAdapter.class.getSimpleName();
	private Context mContext;
	private PartTimeDB mPartTimeDB = null;
	private ArrayList<JobManagedStruct> mEmploymentList=null;
	private Handler mHandler =null;
	private String jobName;
	/*布局部分*/
	private LayoutInflater layoutInflater = null;
	private EmploymentListItem EmploymentItemView=null;
	
	/*imageloader部分*/
	private DisplayImageOptions options;  
	private ImageLoader mImageLoader;
	
	
	public CompanyEmplymentAdapter(Context context,Handler mhandler,ArrayList<JobManagedStruct> list,String jobname){
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		initDisplayOptions();
		
		mEmploymentList = list;
		jobName = jobname;
		mHandler = mhandler;
	}
	
	
	@Override
	public int getCount() {
			return mEmploymentList.size();
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
			EmploymentItemView = new EmploymentListItem();
			convertView = layoutInflater.inflate(R.layout.company_emplyment_item_layout,null);
			EmploymentItemView.EmploymentItemBlock = (LinearLayout) convertView.findViewById(R.id.employment_item_block);
			EmploymentItemView.mUserName = (TextView) convertView.findViewById(R.id.employment_username);
			EmploymentItemView.mJobNameType = (TextView) convertView.findViewById(R.id.employment_jobtype);
			EmploymentItemView.mDistance = (TextView) convertView.findViewById(R.id.employment_distance);
			EmploymentItemView.mTime = (TextView) convertView.findViewById(R.id.employment_time);
//			EmploymentItemView.mHeadImage = (ImageView) convertView.findViewById(R.id.employment_personal_headimage);
			
			EmploymentItemView.EmploymentItemBlock.setOnClickListener(CompanyEmplymentAdapter.this);
			convertView.setTag(EmploymentItemView);
		}else {
			EmploymentItemView = (EmploymentListItem) convertView.getTag();
		}
		JobManagedStruct Employment_item =mEmploymentList.get(position);
		if(Employment_item!=null){
			EmploymentItemView.mUserName.setText("员工："+Employment_item.mUserName);
			if(Employment_item.mJobNameType==null || Employment_item.mJobNameType.equals("null")){
				EmploymentItemView.mJobNameType.setTextColor(mContext.getResources().getColor(R.color.black));
				EmploymentItemView.mJobNameType.setVisibility(View.GONE);
			}else if(Employment_item.mJobNameType.equals("时")){
				EmploymentItemView.mJobNameType.setBackground(mContext.getResources().getDrawable(R.drawable.job_item_time_bg));
				EmploymentItemView.mJobNameType.setTextColor(mContext.getResources().getColor(R.color.red));
			}else if(Employment_item.mJobNameType.equals("天")){
				EmploymentItemView.mJobNameType.setBackground(mContext.getResources().getDrawable(R.drawable.job_item_time_green));
				EmploymentItemView.mJobNameType.setTextColor(mContext.getResources().getColor(R.color.green));
			}else if(Employment_item.mJobNameType.equals("周")){
				EmploymentItemView.mJobNameType.setBackground(mContext.getResources().getDrawable(R.drawable.job_item_time_blue));
				EmploymentItemView.mJobNameType.setTextColor(mContext.getResources().getColor(R.color.light_blue));
			} 
			EmploymentItemView.mJobNameType.setText(Employment_item.mJobNameType+"");
			if(Employment_item.mTime!=null &&!Employment_item.mTime.equals(""))
			EmploymentItemView.mTime.setText(Employment_item.mTime);
			else
				EmploymentItemView.mTime.setText("00:00:00");
			if(Employment_item.mAddressDistance!=null && !Employment_item.mAddressDistance.equals(""))
				if(TextUtils.isDigitsOnly(Employment_item.mAddressDistance))
					EmploymentItemView.mDistance.setText(Employment_item.mAddressDistance+"/米");
				else
					EmploymentItemView.mDistance.setText(Employment_item.mAddressDistance);
			else
				EmploymentItemView.mDistance.setText("../米");
//			mImageLoader.displayImage(Employment_item.mUserHeadImage, EmploymentItemView.mHeadImage, options);
			EmploymentItemView.EmploymentItemBlock.setTag(Employment_item);
		}
		return convertView;
	}
	
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.employment_item_block:
			
			JobManagedStruct struct = (JobManagedStruct)view.getTag();
			Intent intent = new Intent();
			intent.setClass(mContext, Personal_Task_Activity.class);
			intent.putExtra("hire_id", Integer.parseInt(struct.mHireid));
			intent.putExtra("hire_name",jobName );
			
			mContext.startActivity(intent);
			
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
	public static class EmploymentListItem{
		private LinearLayout EmploymentItemBlock;
		private TextView mUserName,mJobNameType,mDistance,mTime;
		private ImageView mHeadImage;
		
	}



}
