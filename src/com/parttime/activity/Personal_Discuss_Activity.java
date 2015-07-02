package com.parttime.activity;

import com.parttime.parttimejob.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
/**
 * 
 * @author 灰色的寂寞
 * @function 实现个人中心的讨论功能
 * @date 2015-1-19
 * @time 10:33
 *
 */
public class Personal_Discuss_Activity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_discuss_layout);
	}
}
