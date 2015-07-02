package com.parttime.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.activity.AskDetails_Activity;
import com.parttime.activity.Volunteer_Detials_Activity;
import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.AskInfoStruct;
import com.parttime.struct.PartTimeStruct.VolunteerStruct;
import com.parttime.utils.Utils;

/**
 * 
 * @author 灰色的寂寞
 * @function 公益列表的适配
 */
public class Volunteer_ListView_Adapter extends BaseAdapter implements OnClickListener{
	public static final String TAG = Volunteer_ListView_Adapter.class.getSimpleName();
	private Context mContext;
	ArrayList<AskInfoStruct> masklist = new ArrayList<AskInfoStruct>();
	ArrayList<VolunteerStruct> mvolunteerlist = new ArrayList<VolunteerStruct>();
	long muserid = -1;

	long errorcode = -1;
	VolunteerListItem mVolunteerListItem = null;
	private LayoutInflater layoutInflater = null;

	private DisplayImageOptions options;
	private ImageLoader mImageLoader;

	
	private static final int CLICK_TYPE_ITEM=0;
	/*manage click */
	private static final int CLICK_TYPE_ASKHELP= 1;
	private static final int CLICK_TYPE_VOLUNTEER= 2;
	private static final int CLICK_TYPE_JOINIT= 3;
	private int mType;
	public Volunteer_ListView_Adapter(Context context,ArrayList<AskInfoStruct> asklist,ArrayList<VolunteerStruct> volunteerlist,int Type){
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		mType = Type;
		if(mType == CLICK_TYPE_ASKHELP){
			masklist =asklist;
			mvolunteerlist = null;
		}else if(mType == CLICK_TYPE_VOLUNTEER){
			masklist =null;
			mvolunteerlist = volunteerlist;
		}
		initDisplayOptions();

	}

	@Override
	public int getCount() {
		if(mType == CLICK_TYPE_ASKHELP){
			return masklist.size();
		}else {
			return mvolunteerlist.size();
		}
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mVolunteerListItem = new VolunteerListItem();
			convertView = layoutInflater.inflate(R.layout.volunteer_listview_item_layout, null);
			mVolunteerListItem.AskTitleBlock = (LinearLayout) convertView.findViewById(R.id.ask_title_block_item);
			mVolunteerListItem.AskTitle = (TextView) convertView.findViewById(R.id.ask_title_item);
			mVolunteerListItem.AskTime = (TextView) convertView.findViewById(R.id.ask_time_item);
			
			mVolunteerListItem.AskTitleBlock.setOnClickListener(Volunteer_ListView_Adapter.this);
			convertView.setTag(mVolunteerListItem);
		} else {
			mVolunteerListItem = (VolunteerListItem) convertView.getTag();
		}
		if(masklist!=null){
			AskInfoStruct askstruct = masklist.get(position);
			mVolunteerListItem.AskTitle.setText(askstruct.mTheme);
			if(askstruct.mCreateTime!=null)
			mVolunteerListItem.AskTime.setText(askstruct.mCreateTime);
			mVolunteerListItem.AskTitleBlock.setTag(askstruct);
		}else {
			VolunteerStruct volunteerStruct = mvolunteerlist.get(position);
			if(volunteerStruct.mCreateTime!=null)
				mVolunteerListItem.AskTime.setText(volunteerStruct.mCreateTime);
			mVolunteerListItem.AskTitle.setText(volunteerStruct.mTheme);
			mVolunteerListItem.AskTitleBlock.setTag(volunteerStruct);
		}
		return convertView;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.ask_title_block_item:
			
			if(masklist!=null){
				AskInfoStruct askstruct = (AskInfoStruct)view.getTag();
				Intent intent = new Intent();
				intent.setClass(mContext, AskDetails_Activity.class);
				intent.putExtra("mid", askstruct.mAskid);  
				mContext.startActivity(intent);
			}else if(mvolunteerlist!=null){
				VolunteerStruct volunteerstruct = (VolunteerStruct)view.getTag();
				Intent intent = new Intent();
				intent.setClass(mContext, Volunteer_Detials_Activity.class);
				intent.putExtra("mid", volunteerstruct.mVolunteerid);  
				mContext.startActivity(intent);
			}
			break;
		}
	}
	/**
	 * imageloader 加载图片展示，的一些参数的设置 avatar_user_default 为默认图片
	 */
	@SuppressWarnings("deprecation")
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.loading)
		// 设置正在加载图片
		// .showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
		.showImageForEmptyUri(R.drawable.load_failed)
		.showImageOnFail(R.drawable.loading_default_img)
		// 设置加载失败图片
		.cacheInMemory(true).cacheOnDisc(true)
		//.displayer(new RoundedBitmapDisplayer(20)) // 设置图片角度,0为方形，360为圆角
		.build();

		mImageLoader = ImageLoader.getInstance();
	}

	/**
	 * 图片和标题
	 */
	public static class VolunteerListItem {
		private LinearLayout AskTitleBlock;
		private TextView AskTitle,AskTime;
	}



}
