package com.parttime.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.parttimejob.R;

public class EULA_Activity extends Activity {
	private Context mContext;
	
	/** 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private WebView EulaView;
	
	/*等待框部分*/
	private TextView WaitingText;
	private RelativeLayout WaitingDlg;
	
	//常量部分
	/** 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_COMMIT=1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.eula_layout);
		mContext = this;
		
		initViews();
		
	}
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		CenterTitle.setText("最终用户协议");
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		/* 等待对话框部分 */
		WaitingText = (TextView)findViewById(R.id.Waiting_text);
		WaitingDlg  =(RelativeLayout)findViewById(R.id.Waiting_dlg);

		EulaView = (WebView)findViewById(R.id.eula_web);
		
		 WebSettings wSet = EulaView.getSettings();     
	        wSet.setJavaScriptEnabled(true);     
	        EulaView.loadUrl("file:///android_asset/eula.html");    
	        //wView.loadUrl("content://com.android.htmlfileprovider/sdcard/index.html");  
//	        EulaView.loadUrl("http://wap.baidu.com");  
	        
	        
	        EulaView.setWebViewClient(new MyWebViewClient()); 
	    
	}
	 // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            HideWaiting();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            
            ShowWaiting("正在加载数据……");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }
    
    public void ShowWaiting(String text) {
		WaitingText.setText(text);
		WaitingDlg.setVisibility(View.VISIBLE);
	}

	public void HideWaiting() {
		WaitingDlg.setVisibility(View.GONE);
	}
	/**
	 * 自定义ClickListener管理Click事件
	 * */
	public class MyOnClickListener implements OnClickListener{
		private int mtype=-1;//默认为-1，什么也不做
		public MyOnClickListener(int type){
			mtype = type;
		}
		@Override
		public void onClick(View v) {
			ManageClick(mtype);
		}
		
	}
	
	/**
	 * 通过判断type，管理click事件
	 * @param mtype
	 */
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			// 返回按钮的监听
			finish();
			break;
		}
	}
}
