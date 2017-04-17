package com.android.beijinnews.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义不可滑动的ViewPager
 */

public class NoScrollViewPager extends ViewPager {

    /*
    * 通常在代码中实例化的时候使用该方法
    * */
    public NoScrollViewPager(Context context) {
        super(context);
    }

    /*
    * 在布局文件中使用该类的时候，实例化该类用该构造方法，不可缺少
    * */
    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
    * 重写触摸事件
    * */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
