package com.test2019.tyapp.longhuan.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleS1ider extends View {

    private float mCx;
    private float mCy;
    private float mRadius;

    private Paint mMainCirclePaint;
    private int mMainCircleColor = 0xFF4d5157;
    private float mMainCircleWidth;
    private static final float MAIN_CIRCLE_WIDTH = 30f;

    private Paint mOutCirclePaint;
    private int mOutCircleColor = 0xFF2f343b;
    private float mOutCircleWidth;
    private static final float OUT_CIRCLE_WIDTH = 16f;

    private Paint mMiddleCirclePaint;
    private int mMiddleCircleColor = 0xFF181d24;
    private float mMiddleCircleWidth;
    private static final float MIDDLE_CIRCLE_WIDTH = 8f;
    private Paint mMiddleCircleEndPaint;

    private Paint mShowPaint;
    private Paint mShowEndPaint;
    private int mShowColor = 0xFFffd64c;

    private Paint mInnerCirclePaint;

    private Paint mTouchButtonPaint;
    private int mTouchButtonColor = 0xFF454951;
    private float mTouchButtonRadius;
    private static final float TOUCH_BUTTON_RADIUS = 8f;

    private Paint mTouchButtonOutPaint;
    private int mTouchButtonOutColor = 0xFF646868;
    private float mTouchButtonOutRadius;
    private static final float TOUCH_BUTTON_OUT_RADIUS = 10f;

    private float mTextSize;
    private static final float TEXT_SIZE = 30;
    private Paint mTextPaint;
    private int mTextColor = 0xFFFFFFFF;

    private static double START_ANGLE = Math.PI * 3 / 4;
    private static double SWEEP_ANGLE = Math.PI * 3 / 2;

    private double mPreRadian;
    private double mCurrentRadian;
    private boolean bInTouchButton = false;
    private float mIsTouchRadius;
    private static final float IS_TOUCH_RADIUS = 15;

    //==== interface ========//
    public interface IOnDimmerMeterChangeListener{
        void onChange(int current);
        void onChangeStop(int current);
    }
    public IOnDimmerMeterChangeListener mOnDimmerChangeListener;

    public void setOnDimmerChangeListener(IOnDimmerMeterChangeListener listener){
        this.mOnDimmerChangeListener = listener;
    }

    public CircleS1ider(Context context) {
        this (context, null);
    }

    public CircleS1ider(Context context, @Nullable AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public CircleS1ider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    private void initialize() {
        mMainCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAIN_CIRCLE_WIDTH,
                getContext().getResources().getDisplayMetrics());
        mOutCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, OUT_CIRCLE_WIDTH,
                getContext().getResources().getDisplayMetrics());
        mMiddleCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MIDDLE_CIRCLE_WIDTH,
                getContext().getResources().getDisplayMetrics());
        mTouchButtonRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TOUCH_BUTTON_RADIUS,
                getContext().getResources().getDisplayMetrics());
        mTouchButtonOutRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TOUCH_BUTTON_OUT_RADIUS,
                getContext().getResources().getDisplayMetrics());
        mIsTouchRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, IS_TOUCH_RADIUS,
                getContext().getResources().getDisplayMetrics());
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE,
                getContext().getResources().getDisplayMetrics());

        //======= main circle =======//
        mMainCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMainCirclePaint.setColor(mMainCircleColor);
        mMainCirclePaint.setStyle(Paint.Style.STROKE);
        mMainCirclePaint.setStrokeWidth(mMainCircleWidth);

        //======== out circle =====//
        mOutCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutCirclePaint.setColor(mOutCircleColor);
        mOutCirclePaint.setStyle(Paint.Style.STROKE);
        mOutCirclePaint.setStrokeWidth(mOutCircleWidth);

        //======= middle circle ========//
        mMiddleCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleCirclePaint.setColor(mMiddleCircleColor);
        mMiddleCirclePaint.setStyle(Paint.Style.STROKE);
        mMiddleCirclePaint.setStrokeWidth(mMiddleCircleWidth);

        mMiddleCircleEndPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleCircleEndPaint.setColor(mMiddleCircleColor);
        mMiddleCircleEndPaint.setAntiAlias(true);
        mMiddleCircleEndPaint.setStyle(Paint.Style.FILL);

        //======== show circle ==========//
        mShowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShowPaint.setColor(mShowColor);
        mShowPaint.setStyle(Paint.Style.STROKE);
        mShowPaint.setStrokeWidth(mMiddleCircleWidth);

        mShowEndPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShowEndPaint.setColor(mShowColor);
        mShowEndPaint.setAntiAlias(true);
        mShowEndPaint.setStyle(Paint.Style.FILL);

        //======= inner circle ==========//
        mInnerCirclePaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        mInnerCirclePaint.setColor(mMiddleCircleColor);
        mInnerCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setStyle(Paint.Style.FILL);

        //==== touch button point =============//
        mTouchButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTouchButtonPaint.setColor(mTouchButtonColor);
        mTouchButtonPaint.setAntiAlias(true);
        mTouchButtonPaint.setStyle(Paint.Style.FILL);

        mTouchButtonOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTouchButtonOutPaint.setColor(mTouchButtonOutColor);
        mTouchButtonOutPaint.setAntiAlias(true);
        mTouchButtonOutPaint.setStyle(Paint.Style.FILL);

        //====== text paint ==============//
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mCurrentRadian = START_ANGLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        // draw out circle
        canvas.drawCircle(mCx, mCy, mRadius + mMainCircleWidth + mOutCircleWidth / 2, mOutCirclePaint);

        // draw main circle
        canvas.drawCircle(mCx, mCy, mRadius + mMainCircleWidth / 2, mMainCirclePaint);

        // draw inner circle
        canvas.drawCircle(mCx, mCy, mRadius, mInnerCirclePaint);

        // draw middle arc
        RectF rect = new RectF(
                mCx - mRadius - mMainCircleWidth - mOutCircleWidth / 2,
                mCy - mRadius - mMainCircleWidth - mOutCircleWidth / 2,
                mCx + mRadius + mMainCircleWidth + mOutCircleWidth / 2,
                mCy + mRadius + mMainCircleWidth + mOutCircleWidth / 2);
        canvas.drawArc(rect, (float) Math.toDegrees(START_ANGLE), (float) Math.toDegrees(SWEEP_ANGLE), false, mMiddleCirclePaint);

        // draw middle arc end point
        canvas.rotate((float) Math.toDegrees(START_ANGLE), mCx, mCy);
        canvas.drawCircle(mCx + mRadius + mMainCircleWidth + mOutCircleWidth / 2, mCy, mMiddleCircleWidth / 2, mMiddleCircleEndPaint);

        canvas.rotate((float) Math.toDegrees(SWEEP_ANGLE), mCx, mCy);
        canvas.drawCircle(mCx + mRadius + mMainCircleWidth + mOutCircleWidth / 2, mCy, mMiddleCircleWidth / 2, mMiddleCircleEndPaint);
        canvas.restore();   // refresh canvas

        // draw touch button
        canvas.save();
        canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
        canvas.drawCircle(mCx + mRadius + mMainCircleWidth / 2, mCy, mTouchButtonOutRadius, mTouchButtonOutPaint);
        canvas.drawCircle(mCx + mRadius + mMainCircleWidth / 2, mCy, mTouchButtonRadius, mTouchButtonPaint);
        canvas.restore();

        // draw show arc
        canvas.save();
        canvas.drawArc(rect, (float) Math.toDegrees(START_ANGLE), (float) Math.toDegrees(mCurrentRadian - START_ANGLE), false, mShowPaint);
        canvas.restore();

        if (mCurrentRadian > START_ANGLE) {
            // draw show arc end point
            canvas.save();
            canvas.rotate((float) Math.toDegrees(START_ANGLE), mCx, mCy);
            canvas.drawCircle(mCx + mRadius + mMainCircleWidth + mOutCircleWidth / 2, mCy, mMiddleCircleWidth / 2, mShowEndPaint);
            canvas.restore();

            canvas.save();
            canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
            canvas.drawCircle(mCx + mRadius + mMainCircleWidth + mOutCircleWidth / 2, mCy, mMiddleCircleWidth / 2, mShowEndPaint);
            canvas.restore();
        }

        int nCurrentPercent = (int) Math.round((mCurrentRadian - START_ANGLE) * 100 / SWEEP_ANGLE);
        canvas.drawText(nCurrentPercent + "%", mCx, mCy + getFontHeight(mTextPaint) / 2, mTextPaint);

        mOnDimmerChangeListener.onChange(nCurrentPercent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // if the point in the circle button
                if (isInTouchButton(event.getX(), event.getY())) {
                    bInTouchButton = true;
                    mPreRadian = getRadian(event.getX(), event.getY());
                }
                else {
                    callOnClick();          // call View.onclick function when it is not touch button.
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (bInTouchButton && isEnabled()) {
                    double temp = getRadian(event.getX(), event.getY());
                    if (mPreRadian > Math.toRadians(270) && temp < Math.toRadians(90)) {
                        mPreRadian -= 2 * Math.PI;
                    } else if (mPreRadian < Math.toRadians(90) && temp > Math.toRadians(270)) {
                        mPreRadian = (float) (temp + (temp - 2 * Math.PI) - mPreRadian);
                    }
                    mCurrentRadian += (temp - mPreRadian);
                    mPreRadian = temp;

                    if (mCurrentRadian > START_ANGLE + SWEEP_ANGLE) {
                        mCurrentRadian = START_ANGLE + SWEEP_ANGLE;
                    }
                    else if (mCurrentRadian < START_ANGLE) {
                        mCurrentRadian = START_ANGLE;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (bInTouchButton && isEnabled()) {
                    bInTouchButton = false;
                    int nCurrentPercent = (int) Math.round((mCurrentRadian - START_ANGLE) * 100 / SWEEP_ANGLE);
                    mOnDimmerChangeListener.onChangeStop(nCurrentPercent);
                }
                break;
        }
//        return super.onTouchEvent(event);
        return true;
    }

    private boolean isInTouchButton(float x, float y){
        float r = mRadius + mMainCircleWidth / 2;
        float x2 = (float) (mCx + r * Math.cos(mCurrentRadian));
        float y2 = (float) (mCy + r * Math.sin(mCurrentRadian));

        if (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) <= mIsTouchRadius) {
            return true;
        }
        return false;
    }

    private double getRadian(float x, float y) {
        double radian = Math.atan((y - mCy) / (x - mCx));

        if (x > mCx && y < mCy){
            radian = 2 * Math.PI + radian;  // radian is negative
        }
        else if (x < mCx && y > mCy) {
            radian = Math.PI + radian;  // radian is negative
        }
        else if (x < mCx && y < mCy) {
            radian = Math.PI + radian;  //radian is positive
        }
        return radian;
    }

    private float getFontHeight(Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        this.mCx = width / 2;
        this.mCy = height / 2;

        this.mRadius = width / 2 - mOutCircleWidth - mMainCircleWidth;
    }

    public void setCurrent(int current){
        if (Math.abs((mCurrentRadian - START_ANGLE) * 100 / SWEEP_ANGLE - (double) current) < 0.5) return;

        if (current > 100) current = 100;
        else if (current < 0) current = 0;

        mCurrentRadian = current * SWEEP_ANGLE / 100 + START_ANGLE;
        invalidate();

//        final double dStep = 0.01;
//        if (mCurrentRadian < targetRadian) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (mCurrentRadian <= targetRadian) {
//                        invalidate();
//                        try {
//                            Thread.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        mCurrentRadian += dStep;
//                    }
//                }
//            }).start();
//        }
//        else if (mCurrentRadian > targetRadian) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (mCurrentRadian >= targetRadian - dStep) {
//                        invalidate();
//                        try {
//                            Thread.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        mCurrentRadian -= dStep;
//                    }
//                }
//            }).start();
//        }
    }

}
