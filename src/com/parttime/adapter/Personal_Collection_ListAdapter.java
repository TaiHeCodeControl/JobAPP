package com.parttime.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.JobCollectionStruct;
import com.parttime.struct.PartTimeStruct.PersonalTaskStruct;
/**
 * 
 * @author 灰色的寂寞
 * @function 个人中心收藏
 * @date 2015-1-19
 * @time 17:54
 *
 */
public class Personal_Collection_ListAdapter extends BaseAdapter implements OnClickListener{
	public static final String TAG = Personal_Collection_ListAdapter.class.getSimpleName();
	private Context mContext;
	private PartTimeDB mPartTimeDB = null;
	private ArrayList<JobCollectionStruct> mCollectionList=null;
	private Handler mHandler =null;
	
	/*布局部分*/
	private LayoutInflater layoutInflater = null;
	private CollectionListItem collectionItemView=null;
	
	/*imageloader部分*/
	private DisplayImageOptions options;  
	private ImageLoader mImageLoader;
	
	
	public Personal_Collection_ListAdapter(Context context,Handler mhandler){
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		initDisplayOptions();
		
		//mtaskList = new ArrayList<PersonalTaskStruct>();
		mCollectionList = getList();
		
		mHandler = mhandler;
	}
	
	public ArrayList<JobCollectionStruct> getList(){
		JobCollectionStruct struct ;
		ArrayList<JobCollectionStruct> list = new ArrayList<JobCollectionStruct>();
		for(int i=0;i<10;i++){
			 struct = new JobCollectionStruct();
			struct.mJobSign="时";
			struct.mJobCredibility = 3;
			struct.mJobNameType = "小时工";
			struct.mPrice="1000元/天";
			struct.mDistance = 350;
			struct.mCompaneyName = "食为天饭店";
			struct.mTime = "2015-1-20";
			list.add(struct);
		}
		return list;
	}
	
	@Override
	public int getCount() {
			return mCollectionList.size();
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
			collectionItemView = new CollectionListItem();
			convertView = layoutInflater.inflate(R.layout.collection_list_item_layout,null);
			collectionItemView.CollectionItemBlock = (LinearLayout) convertView.findViewById(R.id.collection_item_block);

			collectionItemView.mJobSign = (TextView) convertView.findViewById(R.id.collection_job_sign);
			collectionItemView.mRatingStars = (RatingBar) convertView.findViewById(R.id.collection_home_ratingBar);
			collectionItemView.mDeleteBtn = (TextView) convertView.findViewById(R.id.collection_job_delete_btn);
			collectionItemView.mJobNameType = (TextView) convertView.findViewById(R.id.collection_job_name);
			collectionItemView.mPrice = (TextView) convertView.findViewById(R.id.collection_job_price);
			collectionItemView.mDistance = (TextView) convertView.findViewById(R.id.collection_job_distance);
			collectionItemView.mCompaneyName = (TextView) convertView.findViewById(R.id.collection_company_name);
			collectionItemView.mTime = (TextView) convertView.findViewById(R.id.collection_job_time);
			
			collectionItemView.mDeleteBtn.setOnClickListener(this);
			convertView.setTag(collectionItemView);
		}else {
			collectionItemView = (CollectionListItem) convertView.getTag();
		}
		JobCollectionStruct collection_item =mCollectionList.get(position);
		if(collection_item!=null){
			collectionItemView.mJobSign.setText(collection_item.mJobSign);
			collectionItemView.mRatingStars.setProgress(collection_item.mJobCredibility);
			collectionItemView.mJobNameType.setText(collection_item.mJobNameType);
			collectionItemView.mPrice.setText(collection_item.mPrice);
			collectionItemView.mDistance.setText(collection_item.mDistance+"米");
			collectionItemView.mCompaneyName.setText(collection_item.mCompaneyName);
			collectionItemView.mTime.setText(collection_item.mTime);
			
			collectionItemView.mDeleteBtn.setTag(position);
		}
		return convertView;
	}
	
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.collection_job_delete_btn:
			int pos = (Integer)collectionItemView.mDeleteBtn.getTag();
			mCollectionList.remove(pos);
			notifyDataSetChanged();
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
	public static class CollectionListItem{
		private LinearLayout CollectionItemBlock;
		private TextView mJobSign,mDeleteBtn,mJobNameType,mPrice,mDistance,mCompaneyName,mTime;
		private RatingBar mRatingStars;
		
	}


}