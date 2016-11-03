package com.android.leo.orderbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action2;


/**
 * 仿照[饿了么]下单按钮
 * leo linxiaotao1993@vip.qq.com
 * Created on 16-11-2 上午9:12
 */

public class OrderButton extends View {

    private Paint mPaintBg, mPaintNum, mPaintText;
    private int mWidth, mHeight;
    private float mRadius;
    private float mDegrees;
    private int mChange;
    private int mChangeValue;
    private int mType = STEP_NORMAL;
    private boolean mIsRun = false;
    private static final String TEXT = "加入购物车";
    private int mNum = 0;
    private static int mMaxNum = Integer.MAX_VALUE;
    private List<Animator> mAnimatorList;
    private Action2<Integer, Integer> mCallback;

    //默认状态
    public static final int STEP_NORMAL = 0;
    //文字收缩
    public static final int STEP_TEXT_SHRINK = 1;
    //按钮展开
    public static final int STEP_BTN_EXPAND = 2;
    //按钮收缩
    public static final int STEP_BTN_SHRINK = 3;
    //文字展开
    public static final int STEP_TEXT_EXPAND = 4;
    //数量变化
    public static final int STEP_TEXT_CHANGE = 5;

    public OrderButton(Context context) {
        this(context, null);
    }

    public OrderButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrderButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mType) {
            case STEP_NORMAL:
            case STEP_TEXT_SHRINK:
            case STEP_TEXT_EXPAND: {
                int cx = (int) (mWidth - mRadius);
                int cy = (int) (mHeight - mRadius);
                canvas.drawCircle(cx, cy, mRadius, mPaintBg);
                float left = cx - mChange;
                float right = cx;
                float top = cy - mRadius;
                float bottom = cy + mRadius;
                canvas.drawRect(left, top, right, bottom, mPaintBg);
                cx = (int) left;
                canvas.drawCircle(cx, cy, mRadius, mPaintBg);
                if (mType == STEP_NORMAL) {
                    mPaintText.setTextSize(mPaintText.getTextSize() / 2);
                    canvas.drawText(TEXT, mWidth / 2, mHeight / 2 + getTextHeight(TEXT, mPaintText) / 2f, mPaintText);
                    mPaintText.setTextSize(mPaintText.getTextSize() * 2);
                }
            }
            break;
            case STEP_TEXT_CHANGE:
            case STEP_BTN_EXPAND:
            case STEP_BTN_SHRINK: {
                canvas.save();
                int cx = (int) (mWidth - mRadius);
                int cy = (int) (mHeight - mRadius);
                canvas.translate(-mChange, 0);
                if (mType != STEP_TEXT_CHANGE)
                    canvas.rotate(mDegrees, cx, cy);
                mPaintBg.setStrokeWidth(10);
                mPaintBg.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(cx, cy, mRadius - 10, mPaintBg);
                mPaintBg.setStyle(Paint.Style.FILL);
                //绘制减号
                canvas.drawLine(cx + mRadius / 3, cy, cx - mRadius / 3, cy, mPaintBg);
                mPaintBg.setStrokeWidth(0);
                canvas.restore();
                canvas.save();
                mChange = mChange / 2;
                canvas.translate(-mChange, 0);
                mChange = mChange * 2;
                canvas.drawText(String.valueOf(mNum), cx, cy + 15, mPaintNum);
                canvas.restore();
                canvas.drawCircle(cx, cy, mRadius, mPaintBg);
                if (mType == STEP_BTN_EXPAND || mType == STEP_TEXT_CHANGE) {
                    //绘制加号
                    mPaintText.setStrokeWidth(10);
                    canvas.drawLine(cx + mRadius / 3, cy, cx - mRadius / 3, cy, mPaintText);
                    canvas.drawLine(cx, cy - mRadius / 3, cx, cy + mRadius / 3, mPaintText);
                    mPaintText.setStrokeWidth(0);
                }
            }
            break;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mType == STEP_NORMAL && !mIsRun) {
                    mNum = 1;
                    mType = STEP_TEXT_SHRINK;
                    if (mCallback != null) {
                        mCallback.call(mType, mNum);
                    }
                    mIsRun = true;
                    step1();
                } else if ((mType == STEP_BTN_EXPAND || mType == STEP_TEXT_CHANGE) && !mIsRun) {
                    PointF eventP = new PointF(event.getX(), event.getY());
                    if (isPointInCircle(eventP, new PointF(mWidth - mChangeValue - mRadius, mHeight / 2), mRadius)) {
                        if (mChange != mChangeValue)
                            mChange = mChangeValue;
                        mType = STEP_TEXT_CHANGE;
                        mNum--;
                        if (mNum == 0) {
                            mType = STEP_BTN_SHRINK;
                            mIsRun = true;
                            setp3();
                            if (mCallback != null) {
                                mCallback.call(mType, mNum);
                            }
                        } else {
                            mType = STEP_TEXT_CHANGE;
                            invalidate();
                            if (mCallback != null) {
                                mCallback.call(mType, mNum);
                            }
                        }
                    } else if (isPointInCircle(eventP, new PointF(mWidth - mRadius, mHeight / 2), mRadius)) {
                        mType = STEP_TEXT_CHANGE;
                        if ((mNum + 1) <= mMaxNum) {
                            mNum++;
                            invalidate();

                        } else {
                            //达到最大数量
                        }
                        if (mChange != mChangeValue)
                            mChange = mChangeValue;
                        if (mCallback != null) {
                            mCallback.call(mType, mNum);
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimator();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        clear();
    }

    public void clear() {
        if (mAnimatorList != null) {
            for (Animator animator : mAnimatorList)
                animator.cancel();
            mAnimatorList.clear();
        }
        if (mChange != mChangeValue)
            mChange = mChangeValue;
        mDegrees = 360f;
        mIsRun = false;
        if (mType == STEP_TEXT_SHRINK)
            mType = STEP_BTN_EXPAND;
        else if (mType == STEP_BTN_SHRINK || mType == STEP_TEXT_EXPAND)
            mType = STEP_NORMAL;
    }

    public OrderButton setNum(int num) {
        mNum = num;
        return this;
    }

    public int getNum() {
        return mNum;
    }

    public OrderButton setMaxNum(int maxNum) {
        mMaxNum = maxNum;
        return this;
    }

    public static int getmMaxNum() {
        return mMaxNum;
    }

    public OrderButton setCallback(Action2<Integer, Integer> callback) {
        mCallback = callback;
        return this;
    }

    public OrderButton setType(int type) {
        if (mType == type)
            return this;
        mType = type;
        if (mIsRun)
            mIsRun = false;
        if (mChange != mChangeValue)
            mChange = mChangeValue;
        if (mType == STEP_NORMAL)
            mDegrees = 0f;
        else
            mDegrees = 360f;
//        Logger.d("type = %d,change = %d,degrees = %s",mType,mChange,String.valueOf(mDegrees));
        return this;
    }

    public int getType() {
        return mType;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    // private method
    //
    ///////////////////////////////////////////////////////////////////////////

    private void init() {

        mPaintBg = new Paint();
        mPaintBg.setAntiAlias(true);
        mPaintBg.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getContext().getTheme()));

        mPaintNum = new Paint();
        mPaintNum.setAntiAlias(true);
        mPaintNum.setTextAlign(Paint.Align.CENTER);
        mPaintNum.setTextSize(sp2px(16));
        mPaintNum.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(sp2px(16));
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setColor(ResourcesCompat.getColor(getResources(), android.R.color.white, getContext().getTheme()));

        mWidth = getTextWidth(mPaintText, TEXT) / 5 * 6;
        mHeight = sp2px(16) * 2;
        if (mWidth / (float) mHeight < 3.5) {
            mWidth = (int) (mHeight * 3.5);
        }

        mRadius = mHeight / 2;
        mChangeValue = mChange = (int) (mWidth - (mRadius * 2));
    }

    private void clearAnimator() {
        if (mAnimatorList != null) {
            for (Animator animator : mAnimatorList)
                animator.cancel();
            mAnimatorList.clear();
            mAnimatorList = null;
        }
    }

    private void step1() {
        if (mType == STEP_TEXT_SHRINK) {
            ValueAnimator animator = ValueAnimator.ofInt(mChangeValue, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mIsRun) {
                        mChange = (int) animation.getAnimatedValue();
                        invalidate();
                    } else {
                        animation.cancel();
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mIsRun = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mIsRun = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mType = STEP_BTN_EXPAND;
                    if (mCallback != null) {
                        mCallback.call(mType, mNum);
                    }
                    setp2();
                }
            });
            animator.setDuration(500);
            animator.start();
            animators().add(animator);
        }
    }

    private void setp2() {
        if (mType == STEP_BTN_EXPAND && mIsRun) {
            ValueAnimator animator = ValueAnimator.ofInt(0, mChangeValue);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mIsRun) {
                        mChange = (int) animation.getAnimatedValue();
                        mDegrees = mChange / (float) mChangeValue * 360f;
                        invalidate();
                    } else {
                        animation.cancel();
                    }

                }
            });
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mIsRun = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsRun = false;
                }
            });
            animator.start();
            animators().add(animator);
        }
    }

    private void setp3() {
        if (mType == STEP_BTN_SHRINK) {
            ValueAnimator animator = ValueAnimator.ofInt(mChangeValue, 0);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mIsRun) {
                        mChange = (int) animation.getAnimatedValue();
                        mDegrees = mChange / (float) mChangeValue * 360f;
                        invalidate();
                    } else {
                        animation.cancel();
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mIsRun = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mIsRun = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mType = STEP_TEXT_EXPAND;
                    if (mCallback != null) {
                        mCallback.call(mType, mNum);
                    }
                    setp4();
                }
            });
            animator.start();
            animators().add(animator);
        }
    }

    private void setp4() {
        if (mType == STEP_TEXT_EXPAND && mIsRun) {
            ValueAnimator animator = ValueAnimator.ofInt(0, mChangeValue);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (mIsRun) {
                        mChange = (int) animation.getAnimatedValue();
                        invalidate();
                    } else {
                        animation.cancel();
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mType = STEP_NORMAL;
                    if (mCallback != null) {
                        mCallback.call(mType, mNum);
                    }
                    mIsRun = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mIsRun = false;
                }
            });
            animator.setDuration(500);
            animator.start();
            animators().add(animator);
        }
    }

    private List<Animator> animators() {
        if (mAnimatorList == null)
            mAnimatorList = new ArrayList<>();
        return mAnimatorList;
    }

    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //获取Text高度
    private int getTextHeight(String str, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return (int) (rect.height() / 33f * 29);
    }

    //获取Text宽度
    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * 判断点是否在圆内
     *
     * @param pointF 待确定点
     * @param circle 圆心
     * @param radius 半径
     * @return true在圆内
     */
    private boolean isPointInCircle(PointF pointF, PointF circle, float radius) {
        return Math.pow((pointF.x - circle.x), 2) + Math.pow((pointF.y - circle.y), 2) <= Math.pow(radius, 2);
    }

}
