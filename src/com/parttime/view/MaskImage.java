package com.parttime.view;
import com.parttime.parttimejob.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
/**
 * @author 灰色的寂寞
 * @function 自定义ImageView，使得ImageView为圆形显示
 * @tips：显示圆形，但是四周会留出一部分空隙
 */
public class MaskImage extends ImageView{
	private Bitmap mask = null;
	private Bitmap original = null;
	private int mBackgroundSource=0;
	
	public MaskImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskImage, 0, 0);
		int mImageSource = a.getResourceId(R.styleable.MaskImage_image, 0);
		int mMaskSource = a.getResourceId(R.styleable.MaskImage_mask, 0);
		mBackgroundSource = a.getResourceId(R.styleable.MaskImage_frame, 0);
		
		if (mImageSource != 0)
			original = BitmapFactory.decodeResource(getResources(), mImageSource);
		if (mMaskSource != 0)
			mask = BitmapFactory.decodeResource(getResources(), mMaskSource);
		
		setImageBitmap(original);
		setScaleType(ScaleType.CENTER);
		if (mBackgroundSource != 0)
			setBackgroundResource(mBackgroundSource);

		a.recycle();
	}
	
	@Override
	public void setImageBitmap(Bitmap bp)
	{
		original = bp;
		
		if (original != null) {
			Bitmap result = getClipImage(original, mask);
			super.setImageBitmap(result);
		}
	}

	public void setMaskBitmap(Bitmap source, Bitmap mmask, int FrameSId)
	{
		original = source;
		mask = mmask;
		mBackgroundSource = FrameSId;
		
		setImageBitmap(original);
		setScaleType(ScaleType.CENTER);
		if (mBackgroundSource != 0)
			setBackgroundResource(mBackgroundSource);
	}

	public void setMaskBitmap(Bitmap mmask)
	{
		mask = mmask;
		if (mask == null)
			return;
		if (original != null) {
			Bitmap result = getClipImage(original, mask);
			super.setImageBitmap(result);
		}
	}

	public void setFrameSource(int sid)
	{
		mBackgroundSource = sid;
		
		setScaleType(ScaleType.CENTER);
		if (mBackgroundSource != 0)
			setBackgroundResource(mBackgroundSource);
	}

	private Bitmap getClipImage(Bitmap Source, Bitmap mMask){
		Bitmap result = null;
		if (mMask != null)
			result = Bitmap.createBitmap(mMask.getWidth(), mMask.getHeight(), Config.ARGB_8888);
		else
			result = Bitmap.createBitmap(Source.getWidth(), Source.getHeight(), Config.ARGB_8888);
		Canvas mCanvas = new Canvas(result);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		Rect TargetR;
		if (mMask != null)
			TargetR = new Rect(0,0,mMask.getWidth(),mMask.getHeight());
		else 
			TargetR = new Rect(0,0,Source.getWidth(),Source.getHeight());
		Rect SouR = new Rect(0,0,Source.getWidth(),Source.getHeight());
		mCanvas.drawBitmap(Source, SouR, TargetR, null);
		if (mMask != null)
			mCanvas.drawBitmap(mMask, 0, 0, paint);
		paint.setXfermode(null); 
		
		return result;
	}
}
