package com.android.beijinnews.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.android.beijinnews.R;
import com.android.beijinnews.activity.MainActivity;
import com.android.beijinnews.adapter.ContentFragmentAdapter;
import com.android.beijinnews.base.BaseFragment;
import com.android.beijinnews.base.BasePager;
import com.android.beijinnews.pager.GovaffairPager;
import com.android.beijinnews.pager.HomePager;
import com.android.beijinnews.pager.NewsCenterPager;
import com.android.beijinnews.pager.SettingPager;
import com.android.beijinnews.pager.SmartServicePager;
import com.android.beijinnews.utils.LogUtil;
import com.android.beijinnews.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by msi on 2017/4/9.
 */

public class ContentFragment extends BaseFragment {

    @ViewInject(R.id.viewpager)
    private NoScrollViewPager viewPager;

    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;

    /*用来存放子页面*/
    private ArrayList<BasePager> basePagers;

    @Override
    public View initView() {
        LogUtil.e("正文Fragment视图被初始化");
        View view = View.inflate(context, R.layout.content_fragment, null);
        //viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //rg_main = (RadioGroup) view.findViewById(R.id.rg_main);
        //1.把视图注入到XUtil3框架中，让ContentFragment类与View关联
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("正文Fragment数据被初始化");

        //初始化五个子页面并放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(context));
        basePagers.add(new NewsCenterPager(context));
        basePagers.add(new SmartServicePager(context));
        basePagers.add(new GovaffairPager(context));
        basePagers.add(new SettingPager(context));

        //设置默认选中首页
        rg_main.check(R.id.rb_home);

        //设置ViewPager的适配器
        viewPager.setAdapter(new ContentFragmentAdapter(basePagers));

        //设置RadioGroup选中状态改变的监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //监听页面被选中，初始化对应的页面的数据
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        //预加载首页数据
        basePagers.get(0).initData();
        //设置模式SlidingMenu不可滑动
        isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /*
        * 页面被选择会回调此方法
        * position被选中页面的位置
        * 只对选中页面调用initData()方法获取数据
        * */
        @Override
        public void onPageSelected(int position) {
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        /*
        * checkedId 选中的RadioButton
        * */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_home:
                    viewPager.setCurrentItem(0,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_newscenter:
                    viewPager.setCurrentItem(1,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                case R.id.rb_smartservice:
                    viewPager.setCurrentItem(2,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_govafair:
                    viewPager.setCurrentItem(3,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
                case R.id.rb_setting:
                    viewPager.setCurrentItem(4,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
                    break;
            }
        }
    }
    /*
    * 根据传入的参数设置是否让SlidingMenu可以滑动
    * */
    private void isEnableSlidingMenu(int touchmodeFullscreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }
}
