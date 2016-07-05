package com.jym.floatmenuview.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Jimmy on 2016/7/5 0005.
 */
public class ControllableSlideViewPager extends ViewPager {

    private boolean isCanSlide = true;

    public ControllableSlideViewPager(Context context) {
        super(context);
    }

    public ControllableSlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanSlide) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanSlide) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void setCanSlide(boolean canSlide) {
        isCanSlide = canSlide;
    }

}
