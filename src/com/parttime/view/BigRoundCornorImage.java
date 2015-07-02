package com.parttime.view;

import com.parttime.parttimejob.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 
 * @author 灰色的寂寞
 * @function 自定义ImageView，使得ImageView显示图片为圆形图片
 * 
 * @tips： ImageView的宽高要和自定义中的roundWidth和roundHeight相等
 *
 */
@SuppressLint("Recycle")
public class BigRoundCornorImage extends ImageView {  
	  
    private Paint paint;  
    private int roundWidth = 60; //圆角的程度，此处的数据要与ImageView的宽高相同，才可以是圆角 
    private int roundHeight = 60;  
    private Paint paint2;  
  
    public BigRoundCornorImage(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init(context, attrs);  
    }  
  
    public BigRoundCornorImage(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context, attrs);  
    }  
  
    public BigRoundCornorImage(Context context) {  
        super(context);  
        init(context, null);  
    }  
      
    private void init(Context context, AttributeSet attrs) {  
          
        if(attrs != null) {     
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);   
            roundWidth= a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundWidth, roundWidth);  
            roundHeight= a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundHeight, roundHeight);  
        }else {  
            float density = context.getResources().getDisplayMetrics().density;  
            roundWidth = (int) (roundWidth*density);  
            roundHeight = (int) (roundHeight*density);  
        }   
          
        paint = new Paint();  
        paint.setColor(Color.WHITE);  
        paint.setAntiAlias(true);  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));  
          
        paint2 = new Paint();  
        paint2.setXfermode(null);  
    }  
      
    @Override  
    public void draw(Canvas canvas) {  
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);  
        Canvas canvas2 = new Canvas(bitmap);  
        super.draw(canvas2);  
        drawLiftUp(canvas2);  
        drawRightUp(canvas2);  
        drawLiftDown(canvas2);  
        drawRightDown(canvas2);  
        canvas.drawBitmap(bitmap, 0, 0, paint2);  
        bitmap.recycle();  
    }  
      
    private void drawLiftUp(Canvas canvas) {  
        Path path = new Path();  
        path.moveTo(0, roundHeight);  
        path.lineTo(0, 0);  
        path.lineTo(roundWidth, 0);  
        path.arcTo(new RectF(  
                0,   
                0,   
                roundWidth*2,   
                roundHeight*2),   
                -90,   
                -90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawLiftDown(Canvas canvas) {  
        Path path = new Path();  
        path.moveTo(0, getHeight()-roundHeight);  
        path.lineTo(0, getHeight());  
        path.lineTo(roundWidth, getHeight());  
        path.arcTo(new RectF(  
                0,   
                getHeight()-roundHeight*2,   
                0+roundWidth*2,   
                getHeight()),  
                90,   
                90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawRightDown(Canvas canvas) {  
        Path path = new Path();  
        path.moveTo(getWidth()-roundWidth, getHeight());  
        path.lineTo(getWidth(), getHeight());  
        path.lineTo(getWidth(), getHeight()-roundHeight);  
        path.arcTo(new RectF(  
                getWidth()-roundWidth*2,   
                getHeight()-roundHeight*2,   
                getWidth(),   
                getHeight()), 0, 90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawRightUp(Canvas canvas) {  
        Path path = new Path();  
        path.moveTo(getWidth(), roundHeight);  
        path.lineTo(getWidth(), 0);  
        path.lineTo(getWidth()-roundWidth, 0);  
        path.arcTo(new RectF(  
                getWidth()-roundWidth*2,   
                0,   
                getWidth(),   
                0+roundHeight*2),   
                -90,   
                90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
  
}  
