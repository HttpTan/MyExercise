package com.trm.myexercise.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.trm.myexercise.R;

public class SplashView extends View {

    private int[] mCircleColors;

    private Paint mPaint;
    private Paint mHolePaint;

    private int mCenterX, mCenterY;

    //6个小球的半径
    private int mCircleRadius = 18;
    //旋转大圆的半径
    private int mRotateRadius = 90;
    //空心圆的半径
    private float mCurrentHoleRadis = 0;

    private float mDistance;

    private float mCurrentRotateAngle = 0;
    private float mCurrentRotateRadius = mRotateRadius;

    private ValueAnimator mValueAnimator;
    private int mRotateDuration = 1000;

    private SplashState mSplashState;

    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCircleColors = context.getResources().getIntArray(R.array.splash_circle_colors);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mHolePaint = new Paint();
        mHolePaint.setAntiAlias(true);
        mHolePaint.setColor(getResources().getColor(R.color.splash_bg));
        mHolePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;

        //对角线的一半长度
        mDistance = (float) Math.hypot(w, h) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSplashState == null) {
            mSplashState = new RotateState();
        }
        mSplashState.drawState(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if (mCurrentHoleRadis > 0) {
            mHolePaint.setStrokeWidth(mDistance - mCurrentHoleRadis);
            canvas.drawCircle(mCenterX, mCenterY, (mDistance - mCurrentHoleRadis) / 2 + mCurrentHoleRadis, mHolePaint);

        } else {
            canvas.drawColor(getResources().getColor(R.color.splash_bg));
        }
    }

    private void drawCircles(Canvas canvas) {
        float rotateAngle = (float) (Math.PI * 2 / 6);
        //各个小圆的圆心坐标
        float centerX, centerY;
        for (int i = 0; i < mCircleColors.length; i++) {
            centerX = (float) (mCenterX + Math.cos(rotateAngle * i + mCurrentRotateAngle) * mCurrentRotateRadius);
            centerY = (float) (mCenterY + Math.sin(rotateAngle * i + mCurrentRotateAngle) * mCurrentRotateRadius);

            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(centerX, centerY, mCircleRadius, mPaint);
        }
    }

    private abstract class SplashState {
        abstract void drawState(Canvas canvas);
    }

    //3-1: 旋转
    private class RotateState extends SplashState {
        private RotateState() {
            mValueAnimator = ValueAnimator.ofFloat(0, (float) (Math.PI * 2));
            mValueAnimator.setDuration(mRotateDuration);
            mValueAnimator.setRepeatCount(2);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });

            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSplashState = new MeginState();
                }
            });

            mValueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }
    }

    //3-2: 扩散聚合
    private class MeginState extends SplashState {

        private MeginState() {
            mValueAnimator = ValueAnimator.ofFloat(mCircleRadius, mRotateRadius);
            mValueAnimator.setDuration(mRotateDuration);
            mValueAnimator.setInterpolator(new OvershootInterpolator(10f));
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateRadius = (float) animation.getAnimatedValue();

                    invalidate();
                }
            });

            mValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSplashState = new ExpandState();
                }
            });

            mValueAnimator.reverse();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
            drawCircles(canvas);
        }
    }

    //3-3: 水波纹
    private class ExpandState extends SplashState {
        private ExpandState() {
            mValueAnimator = ValueAnimator.ofFloat(mCircleRadius, mDistance);
            mValueAnimator.setDuration(mRotateDuration);
            mValueAnimator.setInterpolator(new LinearInterpolator());

            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentHoleRadis = (float) animation.getAnimatedValue();

                    invalidate();
                }
            });

            mValueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackground(canvas);
        }
    }
}
