package com.parttime.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.parttime.adapter.ChatAllHistoryAdapter;
import com.parttime.adapter.Communication_ListView_Adapter;
import com.parttime.constant.Constant;
import com.parttime.data.PartTimeDB;
import com.parttime.parttimejob.R;
import com.parttime.pullrefresh.PullToRefreshLayout;
import com.parttime.utils.Utils;
import com.parttime.view.ClearEditText;

public class Fragment_CommunicationPage extends Fragment  {
private static Context mContext=null;


/**
 * communication页中的listview
 */
private ListView communication_list;//列表listview
private Communication_ListView_Adapter communication_listview_adapter=null;
private ArrayList<HashMap<String,String>> list=null;

private PullToRefreshLayout refreshableView;

private ClearEditText searchbox;
private PartTimeDB parttimeDb;
private SharedPreferences sharedPreferences;
private ChatAllHistoryAdapter adapter;
private long userid;
private List<EMConversation> conversationList = new ArrayList<EMConversation>();


private boolean hidden;

	
	static Fragment_CommunicationPage newInstance(Context context) {
		Fragment_CommunicationPage framgment1 = new Fragment_CommunicationPage();
		mContext = context;
		
		return framgment1;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_commnicationpager, container,false);
		parttimeDb = PartTimeDB.getInstance(mContext);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		userid=sharedPreferences.getLong("userid",0 );
		initView(view);
		return view;
	}
	@Override
	public void onPause() {
		super.onPause();
		Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_COMMUNICATION);
	}
	
	private void initView(View v)
	{
		
		
		searchbox=(ClearEditText)v.findViewById(R.id.searchbox);
		
		/**列表部分*/
		communication_list = (ListView) v.findViewById(R.id.communication_listview);
		
		
		searchbox.addTextChangedListener(new TextWatcher(){

			private CharSequence temp;

			@Override
			public void afterTextChanged(Editable s) {
				if (temp.length() >= 0) {
					Log.e("hu", "temp   "+temp.toString());
//					refersh(list,temp.toString());
				} 
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
				adapter.getFilter().filter(s);
			}

			
		});
		
		
	    
		
		list=new ArrayList<HashMap<String,String>>();
		String user=String.valueOf(userid);
		list=parttimeDb.getCommUserInfo(mContext, user);
//		communication_listview_adapter=new Communication_ListView_Adapter(mContext, list);
//		communication_list.setAdapter(communication_listview_adapter);
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
	
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		communication_list.setAdapter(adapter);
		/*************/
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
			refresh();
	}
	
	@Override
		public void onHiddenChanged(boolean hidden) {
			super.onHiddenChanged(hidden);
			this.hidden = hidden;
			if (!hidden) {
				refresh();
			}
		}
	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if(adapter != null)
		    adapter.notifyDataSetChanged();
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		List<EMConversation> list = new ArrayList<EMConversation>();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0)
				list.add(conversation);
		}
		// 排序
		sortConversationByLastChatTime(list);
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1, final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}


}
