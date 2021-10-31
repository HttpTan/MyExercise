package com.trm.myexercise.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.trm.myexercise.R;

public class ScaleView extends View {
    private static final String TAG = "ScaleView_TAG";

    /**
     * 高低刻度线的高度、
     * 尺寸线的宽度、
     * 尺寸线的颜色
     * 尺寸线的间距
     */
    private int lowSize;
    private int highSize;
    private int scaleWidth;
    @ColorInt
    private int scaleColor;
    private int defaultScaleColor = ContextCompat.getColor(getContext(), R.color.blue);
    private int scaleSpace;

    /**
     * 中间指针的高度尺寸、宽度、颜色
     */
    private int pointerSize;
    private int pointerWidth;
    @ColorInt
    private int pointerColor;

    /**
     * 刻度的起始数值
     */
    private float startIndex;
    private float endIndex;

    /**
     * ScaleView的宽高,中心线
     */
    private int mWidth;
    private int mHeight;
    private Point mCenterPoint = new Point();

    private Paint mPaint = new Paint();
    private Canvas mCanvas;
    /**
     * 存储刻度线起始位置的变量
     * startX, startY, stopX, stopY
     */
    private float[] pathValue = new float[4];

    /**
     * 当前刻度值
     */
    private float mPointerValue;
    /**
     * 每个刻度的值
     */
    private float perValue = 0.5f;
    /**
     * 每个刻度与实际值的倍数，如滑动一个刻度值，对应mPointerValue的实际变化倍数
     */
    private int timesPerValue = 1;

    /**
     * 刻度线的类型，高低和中心刻度线
     */
    private enum ScaleType {
        LOW, HIGH, pointer
    }

    private Listener mListener;

    public ScaleView(Context context) {
        super(context);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 从布局或者主题中获取各属性的值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        lowSize = typedArray.getDimensionPixelSize(R.styleable.ScaleView_lowSize, 1);
        highSize = typedArray.getDimensionPixelSize(R.styleable.ScaleView_highSize, 1);
        scaleWidth = typedArray.getDimensionPixelSize(R.styleable.ScaleView_scaleWidth, 1);
        scaleColor = typedArray.getColor(R.styleable.ScaleView_scaleColor, defaultScaleColor);
        scaleSpace = typedArray.getDimensionPixelSize(R.styleable.ScaleView_scaleSpace, 20);

        pointerSize = typedArray.getDimensionPixelSize(R.styleable.ScaleView_pointerSize, 1);
        pointerWidth = typedArray.getDimensionPixelSize(R.styleable.ScaleView_pointerWidth, 1);
        pointerColor = typedArray.getColor(R.styleable.ScaleView_pointerColor, defaultScaleColor);

        startIndex = typedArray.getFloat(R.styleable.ScaleView_startIndex, 0);
        endIndex = typedArray.getFloat(R.styleable.ScaleView_endIndex, 100);

        typedArray.recycle();

        init();
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.FILL); 默认FILL
        // 线帽，即画的线条两端是否带有圆角，SQUARE，矩形
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }


    public float getPointerValue() {
        return mPointerValue;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        mCenterPoint.x = mWidth / 2;
        mCenterPoint.y = mHeight / 2;
        mPointerValue = startIndex;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        drawScale();
        drawPointer();
    }

    /**
     * 绘制高低刻度线, 从中心线开始绘制
     * 绘制分左右2部分
     *
     * 算出当前刻度值mPointerValue 是长刻度线还是短刻度线
     * 当前刻度值mPointerValue，绘制在中心位置，与指针重合
     */
    private void drawScale() {
        Log.d(TAG, "drawScale mPointerValue： " + mPointerValue);
        mPaint.setColor(scaleColor);
        mPaint.setStrokeWidth(scaleWidth);

        // 绘制右刻度线
        ScaleType centerType = getScaleType(mPointerValue);
//        Log.d(TAG, "drawScale mPointerValue: " + mPointerValue + ", centerType： " + centerType);

        float startX = mCenterPoint.x;
        float leftSize = (endIndex - mPointerValue) / (perValue * timesPerValue) * scaleSpace;

        while (startX < mWidth && startX <= mCenterPoint.x + leftSize) {
            mCanvas.drawLines(getPathValue(startX, centerType), mPaint);
            if (centerType == ScaleType.HIGH) {
                centerType = ScaleType.LOW;
            } else if (centerType == ScaleType.LOW) {
                centerType = ScaleType.HIGH;
            }
            startX += scaleSpace;
        }

        // 绘制左刻度线
        centerType = getScaleType(mPointerValue);
        startX = mCenterPoint.x - scaleSpace;
        leftSize = (mPointerValue - startIndex) / (perValue * timesPerValue) * scaleSpace;
        while (startX > 0 && startX >= mCenterPoint.x - leftSize) {
            if (centerType == ScaleType.HIGH) {
                centerType = ScaleType.LOW;
            } else if (centerType == ScaleType.LOW) {
                centerType = ScaleType.HIGH;
            }
            mCanvas.drawLines(getPathValue(startX, centerType), mPaint);
            startX -= scaleSpace;
        }
    }

    /**
     * 绘制中心指针
     */
    private void drawPointer() {
        mPaint.setColor(pointerColor);
        mPaint.setStrokeWidth(pointerWidth);
        mCanvas.drawLines(getPathValue(mCenterPoint.x, ScaleType.pointer), mPaint);
    }

    /**
     * 刻度尺的起点为高刻度线
     * @param value 刻度值
     * @return 刻度类型，长or短
     */
    private ScaleType getScaleType(float value) {
        if (value < startIndex || value > endIndex) {
            Log.d(TAG, "invalid value: " + value + "," + startIndex + ", " + endIndex);
            return null;
        }

        int num = (int) ((value - startIndex) / perValue);

        Log.d(TAG, "getScaleType value: " + value + ",startIndex: " + startIndex + ", num: " + num);
        if (num % 2 == 0) {
            return ScaleType.HIGH;
        }

        return ScaleType.LOW;
    }


    /**
     * 通过X坐标获取刻度线段参数
     * @param x 刻度值
     * @param type 小刻度 Or 大刻度
     * @return
     */
    private float[] getPathValue(float x, ScaleType type) {
        pathValue[0] = x;
        pathValue[2] = x;
        float pathHeight;
        switch (type) {
            case LOW:
                pathHeight = lowSize;
                break;
            case HIGH:
                pathHeight = highSize;
                break;
            case pointer:
                pathHeight = pointerSize;
                break;
            default:
                return null;
        }
        pathValue[1] = mCenterPoint.y - pathHeight / 2;
        pathValue[3] = mCenterPoint.y + pathHeight / 2;

        return pathValue;
    }


    /**
     * 当前触摸位置
     */
    private float currentX, currentY;

    /**
     * 滑动刻度的数量
     */
    private int screwNum;

    /**
     * 滑动的方向
     */
//    private Direction direction;

    /**
     * 滑动的方向，左 or 右
     * 右滑刻度向右移动，刻度值变小
     * 左滑刻度向左移动，刻度值变大
     */
    /*private enum Direction {
        LEFT, RIGHT
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = event.getX();
                currentY = event.getX();
                screwNum = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - currentX) < scaleSpace) {
                    break;
                }

                screwNum = (int) (Math.abs(event.getX() - currentX) / scaleSpace);
                if (event.getX() - currentX > 0 && mPointerValue > startIndex) {
                    mPointerValue -= screwNum * perValue * timesPerValue;
                    mPointerValue = mPointerValue < startIndex ? startIndex : mPointerValue;
                } else if (event.getX() - currentX < 0 && mPointerValue < endIndex) {
//                    direction = Direction.LEFT;
                    mPointerValue += screwNum * perValue * timesPerValue;
                    mPointerValue = mPointerValue > endIndex ? endIndex : mPointerValue;
                } else {
                    break;
                }
//                Log.d(TAG, "ACTION_MOVE screwNum: " + screwNum + ", " + mPointerValue);
                if (mListener != null) {
                    mListener.onChanged(mPointerValue);
                }
                invalidate();

                currentX = event.getX();
                currentY = event.getX();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                screwNum = 0;
                break;
        }

        return true;
    }

    public interface Listener {
        void onChanged(float value);
    }

}
