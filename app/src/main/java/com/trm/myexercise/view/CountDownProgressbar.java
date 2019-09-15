package com.trm.myexercise.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RemoteViews;

import com.trm.myexercise.R;

/**
 * roundProgressBar
 *
 * @author trm
 */

@RemoteViews.RemoteView
public class CountDownProgressbar extends View {

    //center bitmap
    private Bitmap mBitmap;

    //internal space
    private int internalSpace;

    //arc StrokeWidth
    private int strokeWidth;

    //countDown Arc StrokeColor
    private int strokeColor;

    //outsideWrapper color
    private int outsideWrapperColor;

    //countDown millis default is 8000ms
    private int countDownTimeMillis;


    //paint for center img
    private Paint bitmapPaint;

    //arc
    private Paint arcPaint;
    private RectF arcRect;


    //progress
    private int progress;

    //value animator
    private ValueAnimator mAnimator;

    //save current time
    private long currentTime;

    //progress change listener
    private ProgressChangeListener mProgressChangeListener;
    private ClickListener mClickListener;


    private int mWidth;
    private int mHeight;

    //for click region
    private Region mRegion;


    public CountDownProgressbar(Context context) {
        this(context, null);
    }

    public CountDownProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownProgressbar);
        int imageId = a.getResourceId(R.styleable.CountDownProgressbar_cdpb_center_img, R.mipmap.ic_accept_ecall);
        internalSpace = a.getDimensionPixelSize(R.styleable.CountDownProgressbar_cdpb_internal_space, 9);
        strokeWidth = a.getDimensionPixelSize(R.styleable.CountDownProgressbar_cdpb_sweepStrokeWidth, 6);
        strokeColor = a.getColor(R.styleable.CountDownProgressbar_cdpb_sweepStrokeColor, Color.BLACK);
        outsideWrapperColor = a.getColor(R.styleable.CountDownProgressbar_cdpb_outsideWrapperColor, Color.parseColor("#E8E8E8"));
        countDownTimeMillis = a.getInteger(R.styleable.CountDownProgressbar_cdpb_countDownTimeInMillis, 8 * 1000);
        a.recycle();

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bitmapPaint.setStyle(Paint.Style.STROKE);

        mBitmap = BitmapFactory.decodeResource(context.getResources(), imageId);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        if (mBitmap == null) return;

        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();

        Log.d("rongmin", "bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);

        arcRect = new RectF((w - bitmapWidth) / 2 - internalSpace, (h - bitmapHeight) / 2 - internalSpace, (w + bitmapWidth) / 2 + internalSpace, (h + bitmapHeight) / 2 + internalSpace);
        mRegion = new Region();
        Path path = new Path();
        path.addCircle(w / 2, h / 2, bitmapWidth / 2, Path.Direction.CW);

        mRegion.setPath(path, new Region(new Rect(0, 0, w, h)));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClickListener == null) return false;

        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x < 0 || y < 0 || x > mWidth || y > mHeight) {
            return false;
        }

        if (mRegion.contains(x, y)) {
            stop();
            setProgress(0);
            mClickListener.onClick();
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, mRegion.getBounds().left, mRegion.getBounds().top, bitmapPaint);
        //canvas.drawBitmap(mBitmap, mWidth/2-mBitmap.getWidth()/2, mHeight/2-mBitmap.getHeight()/2, bitmapPaint);
        drawOutsideWrapper(canvas);
        drawArc(canvas);
    }

    /**
     * draw outside arc wrapper if needed
     * @param canvas
     */
    private void drawOutsideWrapper(Canvas canvas) {
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setColor(outsideWrapperColor);
        canvas.drawArc(arcRect, 0, 360, false, arcPaint);
    }

    /**
     * draw sweep arc
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        arcPaint.setColor(strokeColor);
        canvas.drawArc(arcRect, -90, progress, false, arcPaint);
    }

    public void start() {
        initAnimator(countDownTimeMillis);
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.removeAllListeners();
            mAnimator.cancel();
        }
    }

    public void pause() {
        if (mAnimator != null) {
            currentTime = mAnimator.getCurrentPlayTime();
            mAnimator.cancel();
        }
    }

    /**
     * resume
     */
    public void resume() {
        if (mAnimator != null) {
            mAnimator.setCurrentPlayTime(currentTime);
            mAnimator.start();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    /**
     * init Animator
     * @param duration  duration
     */
    private void initAnimator(int duration) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        int start = 360;
        int end = 0;

        mAnimator = ValueAnimator.ofInt(start, end).setDuration(duration);
        mAnimator.setRepeatCount(0);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.start();
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue();
                if (mProgressChangeListener != null) {
                    mProgressChangeListener.onProgressChanged((int) (progress / 3.6));

                    mProgressChangeListener.onCountTime((int)((animation.getDuration() - animation.getCurrentPlayTime()))/1000);
                }
                invalidate();
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mProgressChangeListener != null) {
                    mProgressChangeListener.onFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**
     * set sweep arc strokeWidth
     *
     * @param strokeWidth strokeWidth
     */
    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth > 0) {
            this.strokeWidth = strokeWidth;
        }
    }

    /**
     * set sweep arc strokeColor
     *
     * @param strokeColor strokeColor
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * set countDown millis
     *
     * @param countDownTimeMillis countDownTimeMillis
     */
    public void setCountDownTimeMillis(int countDownTimeMillis) {
        this.countDownTimeMillis = countDownTimeMillis;
    }

    /**
     * set progressChange listener
     * @param progressChangeListener progressChangeListener
     */
    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        mProgressChangeListener = progressChangeListener;
    }

    public void setClickListener(ClickListener onClickListener) {
        mClickListener = onClickListener;
    }

    /**
     * set progress by your self
     * @param progress progress 0-360
     */
    public void setProgress(int progress) {
        if (progress > 360) {
            progress = 360;
        } else if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;
        invalidate();
    }

    /**
     * set progress percent
     * @param progressPercent 0-100
     */
    public void setProgressPercent(int progressPercent) {
        if (progressPercent > 100) {
            progressPercent = 100;
        } else if (progressPercent < 0) {
            progressPercent = 0;
        }
        this.progress = (int) (progressPercent * 3.6);
        invalidate();
    }

    public void setOutsideWrapperColor(int outsideWrapperColor) {
        this.outsideWrapperColor = outsideWrapperColor;
    }

    public interface ProgressChangeListener {
        void onFinish();
        void onProgressChanged(int progress);
        void onCountTime(int leftTime);

    }

    public interface ClickListener {
        void onClick();
    }

}