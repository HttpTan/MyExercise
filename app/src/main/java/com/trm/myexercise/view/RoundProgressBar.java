package com.trm.myexercise.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.trm.myexercise.R;
/**
 * roundProgressBar
 *
 * @author trm
 */

public class RoundProgressBar extends View {
    //arc
    private Paint arcPaint;
    private RectF arcRect;

    // textPaint
    private Paint textPaint;

    //arc StrokeWidth
    private int strokeWidth;

    //countDown Arc StrokeColor
    private int strokeColor;

    //progress
    private int progress;

    //countDown millis default is 8000ms

    private int countDownTimeMillis;

    //center background paint
    private Paint centerBgPaint;

    //center background
    private int centerBackground;

    //center textColor
    private int centerTextColor;

    //center textSize
    private float centerTextSize;

    //progress change listener
    private ProgressChangeListener mProgressChangeListener;
    private ClickListener mClickListener;

    private Paint.FontMetrics fontMetrics;

    //value animator
    private ValueAnimator animator;

    //if true draw outsideWrapper false otherwise
    private boolean shouldDrawOutsideWrapper;

    //outsideWrapper color
    private int outsideWrapperColor;

    //default space between view to bound
    private int defaultSpace;

    private long currentTime;

    private int mWidth;
    private int mHeight;

    //for click region
    private Region mRegion;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        strokeWidth = a.getDimensionPixelSize(R.styleable.RoundProgressBar_rpb_sweepStrokeWidth, (int) dp2px(2));
        strokeColor = a.getColor(R.styleable.RoundProgressBar_rpb_sweepStrokeColor, Color.BLACK);
        centerTextSize = a.getDimension(R.styleable.RoundProgressBar_rpb_centerTextSize, sp2px(12));
        centerTextColor = a.getColor(R.styleable.RoundProgressBar_rpb_centerTextColor, Color.WHITE);
        centerBackground = a.getColor(R.styleable.RoundProgressBar_rpb_centerBackgroundColor, Color.parseColor("#808080"));
        countDownTimeMillis = a.getInteger(R.styleable.RoundProgressBar_rpb_countDownTimeInMillis, 3 * 1000);
        shouldDrawOutsideWrapper = a.getBoolean(R.styleable.RoundProgressBar_rpb_drawOutsideWrapper, false);
        outsideWrapperColor = a.getColor(R.styleable.RoundProgressBar_rpb_outsideWrapperColor, Color.parseColor("#E8E8E8"));
        a.recycle();
        defaultSpace = strokeWidth * 2;
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(centerTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);

        centerBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        centerBgPaint.setStyle(Paint.Style.FILL);

        arcRect = new RectF();
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

        arcRect.left = defaultSpace >> 1;
        arcRect.top = defaultSpace >> 1;
        arcRect.right = w - (defaultSpace >> 1);
        arcRect.bottom = h - (defaultSpace >> 1);

        //if (mRegion != null) {
        mRegion = new Region();
        //}

        Path path = new Path();
        path.addCircle(w / 2, h / 2, (arcRect.width() - (defaultSpace >> 2)) / 2, Path.Direction.CW);

        mRegion.setPath(path, new Region(new Rect(0, 0, w, h)));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCenterBackground(canvas);
        if (shouldDrawOutsideWrapper) {
            drawOutsideWrapper(canvas);
        }
        drawArc(canvas);
        drawCenterText(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClickListener == null) return false;

        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        setTag(null);

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (x < 0 || y < 0 || x > mWidth || y > mHeight) {
            return false;
        }

        if (mRegion.contains(x, y)) {
            mClickListener.onClick();
        }

        return super.onTouchEvent(event);
    }

    /**
     * draw center background circle
     *
     * @param canvas
     */
    private void drawCenterBackground(Canvas canvas) {
        centerBgPaint.setColor(centerBackground);
        canvas.drawCircle(arcRect.centerX(), arcRect.centerY(), (arcRect.width() - (defaultSpace >> 2)) / 2, centerBgPaint);
    }

    /**
     * draw outside arc wrapper if needed
     *
     * @param canvas
     */
    private void drawOutsideWrapper(Canvas canvas) {
        arcPaint.setColor(outsideWrapperColor);
        canvas.drawArc(arcRect, 0, 360, false, arcPaint);
    }

    /**
     * draw sweep arc
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        arcPaint.setStrokeWidth(strokeWidth);
        arcPaint.setColor(strokeColor);
        canvas.drawArc(arcRect, -90, progress, false, arcPaint);
    }

    /**
     * draw centerText
     * @param canvas
     */
    private void drawCenterText(Canvas canvas) {
        textPaint.setColor(centerTextColor);
        fontMetrics = textPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) /2;

        canvas.drawText("立即", arcRect.centerX(), arcRect.centerY() - 6 , textPaint);
        canvas.drawText("接听", arcRect.centerX(), arcRect.centerY() + 6 + distance , textPaint);
    }

    public void start() {
        initAnimator(countDownTimeMillis);
    }

    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
    }

    public void pause() {
        if (animator != null) {
            currentTime = animator.getCurrentPlayTime();
            animator.cancel();
        }
    }

    /**
     * resume
     */
    public void resume() {
        if (animator != null) {
            animator.setCurrentPlayTime(currentTime);
            animator.start();
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
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        int start = 360;
        int end = 0;
        animator = ValueAnimator.ofInt(start, end).setDuration(duration);
        animator.setRepeatCount(0);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue();
                if (mProgressChangeListener != null) {
                    mProgressChangeListener.onProgressChanged((int) (progress / 3.6));
                }
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
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
     * set center background (color)
     *
     * @param centerBackground centerBackground
     */
    public void setCenterBackground(int centerBackground) {
        this.centerBackground = centerBackground;
    }

    /**
     * set center textColor
     * @param centerTextColor centerTextColor
     */
    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
    }

    /**
     * set center textSize
     *
     * @param centerTextSize centerTextSize
     */
    public void setCenterTextSize(float centerTextSize) {
        this.centerTextSize = centerTextSize;
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

    public void setShouldDrawOutsideWrapper(boolean shouldDrawOutsideWrapper) {
        this.shouldDrawOutsideWrapper = shouldDrawOutsideWrapper;
    }

    public void setOutsideWrapperColor(int outsideWrapperColor) {
        this.outsideWrapperColor = outsideWrapperColor;
    }

    private float sp2px(float inParam) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, inParam, getContext().getResources().getDisplayMetrics());
    }

    private float dp2px(float dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return dp < 0 ? dp : Math.round(dp * displayMetrics.density);
    }

    public interface ProgressChangeListener {
        void onFinish();
        void onProgressChanged(int progress);
    }

    public interface ClickListener {
        void onClick();
    }


}