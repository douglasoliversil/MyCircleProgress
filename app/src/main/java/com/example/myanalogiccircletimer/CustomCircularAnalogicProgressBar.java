package com.example.myanalogiccircletimer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class CustomCircularAnalogicProgressBar extends ConstraintLayout {

    private final float mStartAngle = -90;
    private Paint mPaint;
    private float mPad;
    private int mViewWidth;
    private int mViewHeight;
    private float mSweepAngle = 0;
    private float mMaxSweepAngle = 360;
    private float mStrokeWidth = 22;
    private int mAnimationDuration = 400;
    private int mProgressColor;
    private int mRemainingColor;
    private ImageView mPointer;

    private Float auxCount = 0f;

    public CustomCircularAnalogicProgressBar(Context context) {
        super(context);
    }

    public CustomCircularAnalogicProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomCircularAnalogicProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setProgress(final int progress, TimeCallBack timeCallBack) {
        if (progress > 100) {
            timeCallBack.timeOut();
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(mAnimationDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (progress == 100 ? 0 : (float) animation.getAnimatedValue());
                invalidate();
                mPointer.setRotation(mSweepAngle);
            }
        });
        animator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.custom_circular_analogic_progress_bar, this);
        setWillNotDraw(false);
        mPointer = findViewById(R.id.pointer);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomCircularProgressBar,
                0, 0);

        try {
            mProgressColor = a.getColor(R.styleable.CustomCircularProgressBar_timeProgressColor, getResources().getColor(R.color.colorAccent));
            mRemainingColor = a.getColor(R.styleable.CustomCircularProgressBar_timeRemainingColor, getResources().getColor(R.color.colorPrimaryDark));
            mStrokeWidth = a.getDimension(R.styleable.CustomCircularProgressBar_strokeWidth, 22);
            mPad = a.getFloat(R.styleable.CustomCircularProgressBar_pad, 2.0f);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initMeasurments();
        drawTime(canvas);
    }

    private void initMeasurments() {
        mViewWidth = getWidth();
        mViewHeight = getHeight();
    }

    private void drawTime(Canvas canvas) {

        final int diameter = Math.min(mViewWidth, mViewHeight);
        final float pad = mStrokeWidth / mPad;
        final RectF outerOval = new RectF(pad, pad, diameter - pad, diameter - pad);

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);

        // Painting remaining time
        mPaint.setColor(mRemainingColor);
        canvas.drawArc(outerOval, mSweepAngle, mMaxSweepAngle, true, mPaint);

        // Painting current time
        mPaint.setColor(mProgressColor);
        canvas.drawArc(outerOval, mStartAngle, mSweepAngle, true, mPaint);
    }

    private float calcSweepAngleFromProgress(int progress) {
        return (mMaxSweepAngle / 100) * progress;
    }

}

