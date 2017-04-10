package com.android.beijinnews.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.android.beijinnews.base.BasePager;

import java.util.ArrayList;

/**
 * Created by msi on 2017/4/10.
 */

public class ContentFragmentAdapter extends PagerAdapter {

    private final ArrayList<BasePager> basePagers;

    public ContentFragmentAdapter(ArrayList<BasePager> basePagers){
        this.basePagers = basePagers;
    }

    @Override
    public int getCount() {
        return basePagers.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePager basePager = basePagers.get(position);//得到各个页面的实例0，1,2,3,4
        View rootView = basePager.rootView;//各个子页面
        //调用各个页面的initData()方法
        //basePager.initData();//初始化数据
        container.addView(rootView);
        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /*
    * object 要销毁的页面
    * */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}