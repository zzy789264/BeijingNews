package com.android.beijinnews.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.beijinnews.R;
import com.android.beijinnews.base.BaseFragment;
import com.android.beijinnews.utils.LogUtil;

/**
 * Created by msi on 2017/4/9.
 */

public class ContentFragment extends BaseFragment {

    private ViewPager viewPager;
    private RadioGroup rg_main;

    @Override
    public View initView() {
        LogUtil.e("正文Fragment视图被初始化");
        View view = View.inflate(context, R.layout.content_fragment,null);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        rg_main = (RadioGroup) view.findViewById(R.id.rg_main);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("正文Fragment数据被初始化");

        //设置默认选中首页
        rg_main.check(R.id.rb_home);
    }
}
