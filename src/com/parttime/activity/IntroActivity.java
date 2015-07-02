package com.parttime.activity;

import java.util.ArrayList;
import java.util.List;

import com.parttime.adapter.IntroAdapter;
import com.parttime.constant.Constant;
import com.parttime.fragment.MainFragment;
import com.parttime.parttimejob.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
/**
 * 
 * @author 灰色的寂寞
 *
 *@function the software's introduce
 */
public class IntroActivity extends Activity implements
android.view.View.OnClickListener, OnPageChangeListener {

	public static final String TAG = "IntroActivity";
	private ViewPager mViewPager;

	private IntroAdapter mPageAdapter;
	/* 根据背景图，用inflater获取对应的layout view */
	private List<View> mListViews;
	/* 介绍的几张图片，数组管理 */
	private final static int viewBackground[] = { R.drawable.guide_1,
		R.drawable.guide_2, R.drawable.guide_3,R.drawable.guide_4,R.drawable.guide_5};
	/* 管理滑动时的点，管理几个imageview */
	private ImageView[] mImageViews;

	private int mViewCount;

	private int mCurSel;

	boolean mLeft = false;
	boolean mRight = false;
	private boolean mIsScrolling = false;
	private int mLastValue = -1;
	private boolean mExit;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.intro_activity_layout);

		initViewPage();

		initView();
		mExit = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	private void initViewPage() {
		mListViews = new ArrayList<View>();
		LayoutInflater mLayoutInflater = getLayoutInflater();

		mViewCount = viewBackground.length;
		for (int i = 0; i < mViewCount; i++) {
			View view = mLayoutInflater.inflate(R.layout.intro_framelayout, null);
			view.setBackgroundResource(viewBackground[i]);
			mListViews.add(view);
		}

		mPageAdapter = new IntroAdapter(mListViews);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setOnPageChangeListener(this);
	}
	/*
	 * @function  init the dot of viewpagger 
	 */
	private void initView() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llayout);

		mImageViews = new ImageView[mViewCount];

		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (ImageView) linearLayout.getChildAt(i);
			//			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}

		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);

	}

	private void setCurView(int pos) {
		if (pos < 0 || pos >= mViewCount) {
			return;
		}

		mViewPager.setCurrentItem(pos);
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}

		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);

		mCurSel = index;
	}

	@Override
	public void onClick(View v) {
		int pos = (Integer) v.getTag();
		setCurView(pos);
		setCurPoint(pos);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (arg0 == 1) {
			mIsScrolling = true;
		} else {
			mIsScrolling = false;
		}

		if (arg0 == 2) {
			mRight = mLeft = false;
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mIsScrolling) {
			if (mLastValue > arg2) {
				mRight = true;
				mLeft = false;
			} else if (mLastValue < arg2) {
				mRight = false;
				mLeft = true;
			} else if (mLastValue == arg2) {
				mRight = mLeft = false;
				if(arg0 == mViewCount - 1){
					if(mLastValue == 0 && arg2 == 0){
						if(!mExit){
							mExit = true;
							SharedPreferences mPrefs = getSharedPreferences("first_in", 0) ;
							mPrefs.edit().putBoolean(Constant.PREF_FIRST_RUN, false).commit();

							Intent intent = new Intent(IntroActivity.this, MainFragment.class);
							IntroActivity.this.startActivity(intent);
							finish();
						}
					}
				}
			}
		}

		mLastValue = arg2;
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurPoint(arg0);
	}

}
