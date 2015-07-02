package com.parttime.activity;

import com.parttime.parttimejob.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
public class Home_Job_Apply extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_job_apply);
	}
}
