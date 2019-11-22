package com.trm.myexercise.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.trm.myexercise.R;

public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout_TAG";
    private int horizontalSpace;
    private int verticalSpace;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        horizontalSpace = a.getDimensionPixelSize(R.styleable.FlowLayout_horitental_space, 20);
        verticalSpace = a.getDimensionPixelSize(R.styleable.FlowLayout_vertical_space, 20);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure");
        int mWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int mHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int mHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //must measure child to get child size
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int[] measureSizes = getMeasuredSize(mWidthSize - getPaddingRight());

        //If mode is EXACTLY keep current size
        if (mWidthMode == MeasureSpec.AT_MOST) {
            mWidthSize = measureSizes[0];
        }

        if (mHeightMode == MeasureSpec.AT_MOST) {
            mHeightSize = measureSizes[1];
        }

        setMeasuredDimension(mWidthSize, mHeightSize);
    }

    /**
     * parent width size contains paddingleft not contians paddingRight
     * @param parentWidthSize
     * @return
     */
    private int[] getMeasuredSize(int parentWidthSize) {
        int[] measuredSize = new int[2];

        int lineWidth = getPaddingLeft();
        int allLineHeight = getPaddingTop();
        int maxLineWidth = 0;

        View child;
        MarginLayoutParams childLP;
        int childWidth;
        int childHeight = 0;

        int childCount = getChildCount();
        Log.d(TAG, "getMeasuredSize childCount: " + childCount);
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            childLP = (MarginLayoutParams) child.getLayoutParams();
            childWidth = child.getMeasuredWidth() + childLP.leftMargin + childLP.rightMargin;
            childHeight = child.getMeasuredHeight() + childLP.topMargin + childLP.bottomMargin;

            //if need other line
            if (i == 0) {
                lineWidth += childWidth;
                allLineHeight += childHeight;
            } else if ((lineWidth + childWidth + horizontalSpace) > parentWidthSize) {
                lineWidth = getPaddingLeft() + childWidth;
                allLineHeight = allLineHeight + childHeight + verticalSpace;
            } else {
                lineWidth = lineWidth + childWidth + horizontalSpace;
            }

            maxLineWidth = Math.max(maxLineWidth, lineWidth);

            child.setTag(new Rect(lineWidth - childWidth + childLP.leftMargin, allLineHeight - childHeight + childLP.topMargin,
                    lineWidth - childLP.rightMargin, allLineHeight - childLP.bottomMargin));
        }

        measuredSize[0] = maxLineWidth + getPaddingRight();
        measuredSize[1] = allLineHeight + getPaddingBottom();

        return measuredSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout");
        int childCount = getChildCount();
        View child;
        Rect rect;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            rect = (Rect) child.getTag();
            child.layout(rect.left, rect.top, rect.right, rect.bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
