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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.struct.PartTimeStruct.AskAnswerStruct;

public class AskAnswer_Adapter  extends BaseAdapter implements OnClickListener{
	public static final String TAG = AskAnswer_Adapter.class.getSimpleName();
	private Context mContext;
	private PartTimeDB mPartTimeDB = null;
	private Handler mHandler =null;
	
	/*布局部分*/
	private LayoutInflater layoutInflater = null;
	private askanswerListItem askanswerItemView=null;
	
	/*imageloader部分*/
	private DisplayImageOptions options;  
	private ImageLoader mImageLoader;
	
	ArrayList<AskAnswerStruct> mlist= new ArrayList<AskAnswerStruct>();
	public AskAnswer_Adapter(Context context,Handler mhandler,ArrayList<AskAnswerStruct> list){
		mContext = context;
		layoutInflater = LayoutInflater.from(context);
		mPartTimeDB = PartTimeDB.getInstance(mContext);
		initDisplayOptions();
		mlist = list;
		
		mHandler = mhandler;
	}
	
	@Override
	public int getCount() {
			return mlist.size();
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
			askanswerItemView = new askanswerListItem();
			convertView = layoutInflater.inflate(R.layout.ask_answer_list_item,null);
			askanswerItemView.askanswerItemBlock = (LinearLayout) convertView.findViewById(R.id.ask_answer_list_item);

			askanswerItemView.mContent = (TextView) convertView.findViewById(R.id.askanswer_item_content);
			askanswerItemView.mTime = (TextView) convertView.findViewById(R.id.askanswer_item_time);
			askanswerItemView.mName= (TextView) convertView.findViewById(R.id.askanswer_item_name);
			askanswerItemView.mCall= (TextView) convertView.findViewById(R.id.askanswer_item_call);
			
			askanswerItemView.askanswerItemBlock.setOnClickListener(AskAnswer_Adapter.this);
			convertView.setTag(askanswerItemView);
		}else {
			askanswerItemView = (askanswerListItem) convertView.getTag();
		}
		AskAnswerStruct askanswer_item =mlist.get(position);
		if(askanswer_item!=null){
			askanswerItemView.mContent.setText("回复："+askanswer_item.mContent);
			askanswerItemView.mTime.setText(""+askanswer_item.mCreateTime);
			askanswerItemView.mName.setText("好心人："+askanswer_item.mName);
			askanswerItemView.mCall.setText("电话："+askanswer_item.mCall);
			askanswerItemView.askanswerItemBlock.setTag(position);
		}
		return convertView;
	}
	
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.ask_answer_list_item:
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
	public static class askanswerListItem{
		private LinearLayout askanswerItemBlock;
		private TextView mContent,mName,mTime,mCall;
		private ImageView mImage;
		
	}




}
