package com.trm.myexercise.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ChromeView extends View implements View.OnClickListener {
    private Context mContext;

    private int mWith;
    private int mHeight;
    private int cx, cy;
    private int crInner, crOuter;
    private RectF innerRectF;
    private RectF outerRectF;

    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;

    private int[] colors = {0xFFD21D22, 0xFFFBD109, 0xFF4BB748, 0xFF398ED5, Color.WHITE};
    private int space = 12;


    public ChromeView(Context context) {
        this(context, null);
    }

    public ChromeView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ChromeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPath = new Path();

        setOnClickListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWith = w;
        mHeight = h;
        cx = w / 2;
        cy = h / 2;
        crOuter = w / 2;
        crInner = crOuter / 2;
        innerRectF = new RectF(cx - crInner, cy - crInner, cx + crInner, cy + crInner);
        outerRectF = new RectF(cx - crOuter, cy - crOuter, cx + crOuter, cy + crOuter);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Canvas mCanvas = new Canvas(mBitmap);

        Matrix matrix = new Matrix();
        matrix.setRotate(120, cx, cy);

        mPath.reset();
        //red part
        mPaint.setColor(colors[0]);
        mPath.arcTo(innerRectF, 150, 120);
        mPath.lineTo((float) (cx + crOuter * Math.sqrt(3) / 2), cy - crInner);
        mPath.arcTo(outerRectF, -30, -120);
        mPath.close();
        mCanvas.drawPath(mPath, mPaint);

        //yellow part
        mPaint.setColor(colors[1]);
        mPath.transform(matrix);
        mCanvas.drawPath(mPath, mPaint);

        //green part
        mPaint.setColor(colors[2]);
        mPath.transform(matrix);
        mCanvas.drawPath(mPath, mPaint);

        //circle part
        mPaint.setColor(colors[4]);
        mCanvas.drawCircle(cx, cy, crInner, mPaint);
        mPaint.setColor(colors[3]);
        mCanvas.drawCircle(cx, cy, crInner - space, mPaint);

        canvas.drawBitmap(mBitmap, 0 ,0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x <= 0 || y <= 0 || x >= mWith || y >= mHeight) {
            return false;
        }

        int color = mBitmap.getPixel(x, y);
        for (int i = 0; i < colors.length - 1; i++) {
            if (color == colors[i]) {
                setTag(i);
                return super.onTouchEvent(event);
            }
        }

        return false;
    }


    @Override
    public void onClick(View v) {
        if (v.getTag() == null) return;

        Toast.makeText(mContext, String.valueOf(v.getTag()), Toast.LENGTH_SHORT).show();
    }
}