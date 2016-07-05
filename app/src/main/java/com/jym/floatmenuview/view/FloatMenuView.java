package com.jym.floatmenuview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.jym.floatmenuview.R;

public class FloatMenuView extends FrameLayout {

    private OnControllerClickListener mListener; // 控制器点击监听
    private View mControllerView; // 控制器
    private View mControlledView; // 被控制块
    private boolean isMove = false; // 是否可移动
    private boolean isMoving = false; // 是否正在移动
    private boolean isVisibility = false; // mControlledView是否可见
    private int mControllerViewWidth; // 控制器宽度
    private int mControllerViewHeight; // 控制器高度
    private int mControlledViewWidth; // 被控制块宽度
    private int mControlledViewHeight; // 被控制块高度
    private int mWidth; // 屏幕宽度
    private int mHeight; // 屏幕高度
    private int mSpeed = 1; // 速度
    private int downX = 0; // 触摸下X坐标
    private int downY = 0; // 触摸下Y坐标
    private int minControllerTopDistance = 0; // 控制器距离顶部最小距离
    private int minControllerBottomDistance = 0; // 控制器距离底部最小距离

    public FloatMenuView(Context context) {
        this(context, null);
    }

    public FloatMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.FloatMenuView);
        minControllerTopDistance = array.getInteger
                (R.styleable.FloatMenuView_minTopDistance, 0);
        minControllerBottomDistance = array.getInteger
                (R.styleable.FloatMenuView_minBottomDistance, 0);
        mSpeed = array.getInteger(R.styleable.FloatMenuView_slideBlockSpeed, 1);
        if (mSpeed < 1) {
            throw new IllegalStateException("滑动速度必须大于等于1");
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 2) {
            mControllerView = getChildAt(0);
            mControlledView = getChildAt(1);
            mControllerViewWidth = mControllerView.getLayoutParams().width;
            mControllerViewHeight = mControllerView.getLayoutParams().height;
            mControlledViewWidth = mControlledView.getLayoutParams().width;
            mControlledViewHeight = mControlledView.getLayoutParams().height;
        } else {
            throw new IllegalArgumentException("FloatMenuView有且仅有2个子控件");
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mControllerView.layout(0, minControllerTopDistance, mControllerViewWidth,
                mControllerViewHeight + minControllerTopDistance);
        mControlledView.layout(mWidth, 0, mWidth
                + mControlledViewWidth, mControlledViewHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                if ((int) event.getX() > mControllerView.getLeft()
                        && (int) event.getX() < mControllerView.getRight()
                        && (int) event.getY() > mControllerView.getTop()
                        && (int) event.getY() < mControllerView.getBottom()) {
                    if (mListener != null) {
                        mListener.onControllerDown();
                    }
                    return true;
                } else {
                    super.onTouchEvent(event);
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                int detalX = Math.abs(downX - (int) event.getX());
                int detalY = Math.abs(downY - (int) event.getY());
                if (detalX > 20 || detalY > 20) {
                    if (!isMove) {
                        isMove = true;
                    }
                }
                isMoving = true;
                if (isMove) {
                    moveController((int) event.getX(), (int) event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    moveController((int) event.getX(), (int) event.getY());
                } else {
                    int detalUpX = Math.abs(downX - (int) event.getX());
                    int detalUpY = Math.abs(downY - (int) event.getY());
                    if (detalUpX < mControllerViewWidth && detalUpY < mControllerViewHeight) {
                        mListener.onControllerClick();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void moveController(int x, int y) {
        if (isVisibility) {
            isMove = false;
            return;
        }
        int l = x - mControllerViewWidth / 2;
        int t = y - mControllerViewHeight / 2;
        int r = x + mControllerViewWidth / 2;
        int b = y + mControllerViewHeight / 2;
        if (l < 0) {
            l = 0;
            r = mControllerViewWidth;
        }
        if (r > mWidth) {
            r = mWidth;
            l = r - mControllerViewWidth;
        }
        if (t < minControllerTopDistance) {
            t = minControllerTopDistance;
            b = t + mControllerViewHeight;
        }
        if (b > mHeight - minControllerBottomDistance) {
            b = mHeight - minControllerBottomDistance;
            t = b - mControllerViewHeight;
        }
        if (isMoving) {
            mControllerView.layout(l, t, r, b);
            if (isVisibility) {
                if (x < mWidth / 2) {
                    mControlledView.layout(mWidth - mControlledViewWidth,
                            (t + b) / 2 - mControlledViewHeight / 2,
                            mWidth,
                            (t + b) / 2 + mControlledViewHeight / 2);
                } else {
                    mControlledView.layout(0,
                            (t + b) / 2 - mControlledViewHeight / 2,
                            mControlledViewWidth,
                            (t + b) / 2 + mControlledViewHeight / 2);
                }
            } else {
                if (x < mWidth / 2) {
                    mControlledView.layout(mWidth,
                            (t + b) / 2 - mControlledViewHeight / 2,
                            mWidth + mControlledViewWidth,
                            (t + b) / 2 + mControlledViewHeight / 2);
                } else {
                    mControlledView.layout(-mControlledViewWidth,
                            (t + b) / 2 - mControlledViewHeight / 2,
                            0,
                            (t + b) / 2 + mControlledViewHeight / 2);
                }
            }
            isMoving = false;
        } else {
            LayoutAnimation animation;
            if (x < mWidth / 2) {
                animation = new LayoutAnimation(mControllerView, 0);
                mControllerView.layout(0, t, mControllerViewWidth, b);
            } else {
                animation = new LayoutAnimation(mControllerView, mWidth - mControllerViewWidth);
                mControllerView.layout(mWidth - mControllerViewWidth, t, mWidth, b);
            }
            startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mListener != null) {
                        mListener.onControllerMoveFinished();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            isMove = false;
        }
    }

    private class LayoutAnimation extends Animation {

        private View view;
        private int startLeft;
        private int startTop;
        private int startBottom;
        private int totalLeft;

        public LayoutAnimation(View view, int targetLeft) {
            this.view = view;
            startLeft = view.getLeft();
            startTop = view.getTop();
            startBottom = view.getBottom();
            totalLeft = targetLeft - startLeft;
            int time = Math.abs(totalLeft / mSpeed);
            setDuration(time);
        }

        protected void applyTransformation(float interpolatedTime,
                                           Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            int currentLeft = (int) (startLeft + totalLeft * interpolatedTime);
            view.layout(currentLeft, startTop, currentLeft + view.getWidth(),
                    startBottom);
        }
    }

    /**
     * 设置滑动速度，默认值为1
     *
     * @param speed (speed>=1)
     */
    public void setSlideSpeed(int speed) {
        if (speed < 1) {
            throw new IllegalStateException("滑动速度必须大于等于1");
        }
        mSpeed = speed;
    }

    public void setOnControllerClickListener(OnControllerClickListener listener) {
        mListener = listener;
    }

    public interface OnControllerClickListener {
        void onControllerDown();

        void onControllerMoveFinished();

        void onControllerClick();

    }

    public void setControlledVisibility(boolean visibility) {
        isVisibility = visibility;
        if (visibility) {
            if (mControllerView.getLeft() < mWidth / 2) {
                mControlledView.layout(mWidth - mControlledViewWidth,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 - mControlledViewHeight / 2,
                        mWidth,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 + mControlledViewHeight / 2);
            } else {
                mControlledView.layout(0,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 - mControlledViewHeight / 2,
                        mControlledViewWidth,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 + mControlledViewHeight / 2);
            }
        } else {
            if (mControllerView.getLeft() < mWidth / 2) {
                mControlledView.layout(mWidth,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 - mControlledViewHeight / 2,
                        mWidth + mControlledViewWidth,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 + mControlledViewHeight / 2);
            } else {
                mControlledView.layout(-mControlledViewWidth,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 - mControlledViewHeight / 2,
                        0,
                        (mControllerView.getTop() + mControllerView.getBottom()) / 2 + mControlledViewHeight / 2);
            }
        }
    }

    public boolean getControlledVisibility() {
        return isVisibility;
    }

    // 设置最小距离顶部高度
    public void setMinControllerTopDistance(int distance) {
        minControllerTopDistance = distance;
    }

    // 设置最小距离底部高度
    public void setMinControllerBottomDistance(int distance) {
        minControllerBottomDistance = distance;
    }

}
