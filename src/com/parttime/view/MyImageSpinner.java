package com.parttime.view;

import com.parttime.parttimejob.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Spinner;
/**
 * 
 * @author 灰色的寂寞
 * @date 2014-11-20
 * @time 10：45
 * @function 自定义spinner在spinner右边显示一张图片
 *
 */
public class MyImageSpinner extends Spinner {

	private Bitmap bitmap=null;
	private Paint mPaint=null;
	
	public MyImageSpinner(Context context) {
		super(context);
	}

	public MyImageSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyImageSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public synchronized void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mPaint =new Paint();
		mPaint.setStrokeWidth(1.0f);
		mPaint.setColor(getResources().getColor(R.color.caption_blue));
		 
		
		/*画出右边的按钮图片*/
		Drawable draw = getResources().getDrawable(R.drawable.drop_down_btn_image);
		BitmapDrawable drawable = (BitmapDrawable)draw;
		bitmap = drawable.getBitmap();

		Rect drect = new Rect();
		drect.left=getWidth()-getHeight();
		drect.top=0;
		drect.bottom=getHeight();
		drect.right=getWidth();
		canvas.drawBitmap(bitmap, null,drect, mPaint);
		
	}
}
