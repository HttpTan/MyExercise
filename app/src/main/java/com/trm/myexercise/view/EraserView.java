package com.trm.myexercise.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.trm.myexercise.R;

public class EraserView extends View {

    private Bitmap mBitmap;
    private Bitmap mDstBmp;
    private Bitmap mSrcBmp;
    private Paint mPaint;
    private Path mPath;


    public EraserView(Context context) {
        this(context, null);
    }

    public EraserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EraserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        //静止硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.a001);
        mDstBmp = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mSrcBmp = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(mSrcBmp);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        Path path = new Path();
        path.addRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), Path.Direction.CW);
        canvas.drawPath(path, paint);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(60);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        //绘制最底层图片
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        //使用离屏绘制
        int saveId = canvas.saveLayer(0, 0, getWidth(), getHeight(), mPaint);

        //绘制路径到bitmap上
        Canvas dstCanvas = new Canvas(mDstBmp);
        dstCanvas.drawPath(mPath, mPaint);

        //绘制路径的bitmap
        canvas.drawBitmap(mDstBmp, 0,0,mPaint);

        //设置图层混合模式
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawBitmap(mSrcBmp, 0, 0, mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(saveId);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(event.getX(), event.getY());

                break;

            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
        }
        invalidate();
        return true;
    }
}
