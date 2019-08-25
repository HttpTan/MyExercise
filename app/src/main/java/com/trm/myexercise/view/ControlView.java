package com.trm.myexercise.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ControlView extends View implements View.OnClickListener {
    private Context mContext;

    private Paint mPaint;
    private Path mPath;

    private int mWidth;
    private int mHeight;

    private int cx, cy;
    private int crInner, crOuter;
    private Rect innerRect;
    private Rect outerRect;

    private int space = 6;

    private List<Region> regionList;
    //    private String[] strings = {"Center", "Top", "Right", "Bottom", "Left"};
    private String[] strings = {"确认", "频道+", "音量+", "频道-", "音量-"};

    public ControlView(Context context) {
        this(context, null);
    }

    public ControlView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);

        mPath = new Path();

        regionList = new ArrayList<>();

        setOnClickListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;

        cx = w / 2;
        cy = h / 2;
        crOuter = w / 2;
        crInner = crOuter / 2;

        innerRect = new Rect(cx - crInner, cy - crInner, cx + crInner, cy + crInner);
        outerRect = new Rect(cx - crOuter, cy - crOuter, cx + crOuter, cy + crOuter);

        Region region = new Region();
        Matrix matrix = new Matrix();
        matrix.setRotate(90, cx, cy);
        mPath.reset();

        //inner circle part
        mPath.addCircle(cx, cy, crInner - 2 * space, Path.Direction.CW);
        region.setPath(mPath, new Region(innerRect));
        regionList.add(region);

        mPath.reset();
        float innerDisAngle = (float) (space * 1.0 / (2 * Math.PI * crInner) * 360);
        float outerDisAngle = (float) (space * 0.5 / (2 * Math.PI * crOuter) * 360);
        float sweepInnerAngle = 90 - 2 * innerDisAngle;
        float sweepOuterAngle = 90 - 2 * outerDisAngle;

        //top 01
        mPath.arcTo(new RectF(innerRect), -135 + innerDisAngle, sweepInnerAngle);
        mPath.lineTo((float) (cx + crOuter * Math.sin(sweepOuterAngle / 2 * Math.PI / 180)), (float) (cy - crOuter * Math.cos(sweepOuterAngle / 2 * Math.PI / 180)));
        mPath.arcTo(new RectF(outerRect), -45 + outerDisAngle, -sweepOuterAngle);
        mPath.close();
        region = new Region();
        region.setPath(mPath, new Region(outerRect));
        regionList.add(region);

        for (int i = 0; i < 3; i++) {
            mPath.transform(matrix);
            region = new Region();
            region.setPath(mPath, new Region(outerRect));
            regionList.add(region);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(Color.GRAY);
        for (Region region : regionList) {
            canvas.drawPath(region.getBoundaryPath(), mPaint);
        }

        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(28);
        mPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i<regionList.size(); i++) {
            if (i == 0) {
                canvas.drawText(strings[i], regionList.get(i).getBounds().centerX(), regionList.get(i).getBounds().centerY() + 8, mPaint);
            } else if (i == 1) {
                canvas.drawText(strings[i], regionList.get(i).getBounds().centerX(), regionList.get(i).getBounds().centerY() + 8, mPaint);
            } else if (i == 2) {
                canvas.drawText(strings[i], regionList.get(i).getBounds().centerX() + 10, regionList.get(i).getBounds().centerY() + 8, mPaint);
            } else if (i == 3) {
                canvas.drawText(strings[i], regionList.get(i).getBounds().centerX(), regionList.get(i).getBounds().centerY() + 16, mPaint);
            } else if (i == 4) {
                canvas.drawText(strings[i], regionList.get(i).getBounds().centerX() - 10, regionList.get(i).getBounds().centerY() + 8, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        setTag(null);

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x < 0 || y < 0 || x > mWidth || y > mHeight) {
            return false;
        }

        for (int i = 0; i < regionList.size(); i++) {
            if (regionList.get(i).contains(x, y)) {
                setTag(i);
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() == null) return;

        Toast.makeText(mContext, strings[(int) v.getTag()], Toast.LENGTH_SHORT).show();

    }
}