package com.parttime.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parttime.activity.Setting_AccountSafe_Activity.MyOnClickListener;
import com.parttime.constant.Constant;
import com.parttime.parttimejob.R;
import com.parttime.utils.Utils;
/**
 * 
 * @author 灰色的寂寞
 * @date 2014-1-13
 * @time 14:54
 * @function 个人中心界面中的钱包部分
 *
 */
public class Personal_Wallet_Activity extends Activity {
	
	private Context mContext;
	
	/** 标题部分*/
	private TextView CenterTitle;
	private ImageView LeftBackBtn;
	
	private TextView 	AccountRemaind,BankCard,Alipay;
	private RelativeLayout WalletAccountRemaind,WalletBankcard,WalletAlipay;
	//常量部分
	/** 返回*/
	private static final int CLICK_TYPE_BACKBTN=0;
	private static final int CLICK_TYPE_ACCOUNTREMAIND=1;
	private static final int CLICK_TYPE_BANKCARD=2;
	private static final int CLICK_TYPE_ALIPAY=3;
	
	private static final int CLICK_TYPE_DIALOG_CANCEL=7;
	private static final int CLICK_TYPE_DIALOG_ENSURE=8;
	/*编辑对话框部分*/
	private RelativeLayout EditDialogBlock;
	private TextView DialogTitle,DialogCancelBtn,DialogEnsureBtn,ErrorTips;
	private EditText DialogContent;
	SharedPreferences shared ;
	String bank = "";
	String apply = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_wallet_layout);
		mContext = this;
		shared =  PreferenceManager.getDefaultSharedPreferences(mContext);
		bank = shared.getString("wallet_bank", "空");
		apply = shared.getString("wallet_apply", "空");
		initViews();
		
	}
	
	
	private void initViews() {
		/** 标题部分*/
		CenterTitle = (TextView)findViewById(R.id.title_text);
		LeftBackBtn = (ImageView)findViewById(R.id.home_back);
		CenterTitle.setText("钱包");
		LeftBackBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BACKBTN));
		
	
		/**
		 * 内容部分
		 */
		AccountRemaind = (TextView)findViewById(R.id.account_remaind);
		BankCard = (TextView)findViewById(R.id.bankcard);
		Alipay = (TextView)findViewById(R.id.alipay);
		
		AccountRemaind.setText("00.0");
		BankCard.setText(bank);
		Alipay.setText(apply);
		
		WalletAccountRemaind= (RelativeLayout)findViewById(R.id.rl_wallet_count_remiand_block);
		WalletBankcard= (RelativeLayout)findViewById(R.id.rl_wallet_bankcard_block);
		WalletAlipay= (RelativeLayout)findViewById(R.id.rl_wallet_alipay_block);
		
		WalletAccountRemaind.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ACCOUNTREMAIND));
		WalletBankcard.setOnClickListener(new MyOnClickListener(CLICK_TYPE_BANKCARD));
		WalletAlipay.setOnClickListener(new MyOnClickListener(CLICK_TYPE_ALIPAY));
		
		
		EditDialogBlock = (RelativeLayout) findViewById(R.id.edit_dialog_layout);
		DialogTitle = (TextView)findViewById(R.id.edit_dialog_title);
		DialogCancelBtn = (TextView)findViewById(R.id.edit_cancel);
		DialogEnsureBtn = (TextView)findViewById(R.id.edit_ensure);
		ErrorTips = (TextView)findViewById(R.id.edit_error_tips);
		DialogContent = (EditText)findViewById(R.id.edit_dialog_content);
		DialogContent.setVisibility(View.VISIBLE);
		DialogCancelBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_CANCEL));
		DialogEnsureBtn.setOnClickListener(new MyOnClickListener(CLICK_TYPE_DIALOG_ENSURE));
		
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
	private int EnsureType=-1;
	private void ManageClick(int mtype) {
		switch (mtype) {
		case CLICK_TYPE_BACKBTN:
			Utils.CommitPageFlagToShared(mContext, Constant.FINISH_ACTIVITY_FLAG_COMPANY);
			// 返回按钮的监听
			finish();
			break;
		case CLICK_TYPE_ACCOUNTREMAIND:
			break;
		case CLICK_TYPE_BANKCARD:
			EnsureType = CLICK_TYPE_BANKCARD; 
			ProcessDialogInfo("银行卡号");
			break;
		case CLICK_TYPE_ALIPAY:
			EnsureType = CLICK_TYPE_ALIPAY; 
			ProcessDialogInfo("支付宝账号");
			break;
		case CLICK_TYPE_DIALOG_CANCEL:
			EditDialogBlock.setVisibility(View.GONE);
			break;
		case CLICK_TYPE_DIALOG_ENSURE:
			ProcessDialogEnsure(EnsureType);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 处理对话框的标题和显示
	 */
	public void ProcessDialogInfo(String title){
		EditDialogBlock.setVisibility(View.VISIBLE);
		DialogTitle.setText(title);
		DialogContent.setText("");
	}
	/**
	 * 处理对应的对话框确定值
	 * @param ensuretype
	 */
	public void ProcessDialogEnsure(int ensuretype){
		String content = DialogContent.getText().toString().trim();
		DialogContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			@Override
			public void afterTextChanged(Editable s) {
		            if(temp.length()>0){
		            	ErrorTips.setVisibility(View.GONE);
		            }
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				temp = s;  				
			}
			
		});
		if(content.equals("")){
			ErrorTips.setVisibility(View.VISIBLE);
			ErrorTips.setText("内容不能为空！");
			return;
		}else{
			ErrorTips.setVisibility(View.GONE);
		}
		switch(ensuretype){
		case CLICK_TYPE_BANKCARD:
			shared.edit().putString("wallet_bank", content).commit();
			BankCard.setText(content);
			break;
		case CLICK_TYPE_ALIPAY:
			shared.edit().putString("wallet_apply", content).commit();
			Alipay.setText(content);
			break;
		}
		
		EditDialogBlock.setVisibility(View.GONE);
	}
}
