package com.android.beijinnews.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 水平方向滑动的ViewPager
 */

public class HorizontalScrollViewPager extends ViewPager {
    public HorizontalScrollViewPager(Context context) {
        super(context);
    }

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
    * 起始坐标
    * */
    private float startX;
    private float startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //请求父层视图不拦截当前控件的事件
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);//把事件全部传给当前控件
                //1.记录起始坐标
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //2.来到新坐标
                float endX = ev.getX();
                float endY = ev.getY();
                //3.计算偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                //4.判断滑动方向
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    //水平方向滑动,当滑动ViewPager的第0个界面，且从左向右滑动
                    if (getCurrentItem() == 0 && distanceX > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        //当滑动ViewPager的最后一个界面，且从右向左滑动
                    } else if ((getCurrentItem() == (getAdapter().getCount() - 1)) && distanceX < 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                } else {
                    //中间部分
                    //竖直方向滑动
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
