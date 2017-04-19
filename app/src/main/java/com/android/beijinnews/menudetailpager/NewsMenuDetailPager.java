package com.android.beijinnews.menudetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.beijinnews.R;
import com.android.beijinnews.activity.MainActivity;
import com.android.beijinnews.base.MenuDetailBasePager;
import com.android.beijinnews.domain.NewsCenterPagerBean2;
import com.android.beijinnews.menudetailpager.tabdetailpager.TabDetailPager;
import com.android.beijinnews.utils.LogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻详情页面
 */

public class NewsMenuDetailPager extends MenuDetailBasePager {

    @ViewInject(R.id.tabPageIndicator)
    private TabPageIndicator tabPageIndicator;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ib_tab_next;

    //标签页面数据集合
    private List<NewsCenterPagerBean2.DetailPagerData.ChildrenData> children;
    //标签页面结集合
    private ArrayList<TabDetailPager> tabDetailPagers;

    public NewsMenuDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData detailPagerData) {
        super(context);
        children = detailPagerData.getChildren();
    }

    @Override
    public View initView() {
        final View view = View.inflate(context, R.layout.newsmenu_detail_pager, null);
        x.view().inject(this, view);

        //设置ib_tab_next点击事件
        ib_tab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("新闻详情页面数据被初始化了..");

        //准备新闻详情页面的数据
        tabDetailPagers = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            tabDetailPagers.add(new TabDetailPager(context, children.get(i)));
        }

        //设置ViewPager的适配器
        viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter());
        //ViewPager和TabPageIndicator关联
        tabPageIndicator.setViewPager(viewPager);

        //注意以后监听页面的变化 ，TabPageIndicator监听页面的变化
        tabPageIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setCurrentItem(tempPositon);
    }

    private int tempPositon = 0;

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                //设置当位置为0时SlidingMenu可以全屏滑动.
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
            } else {
                //设置当位置为0时SlidingMenu可以全屏滑动.
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /*
   * 根据传入的参数设置是否让SlidingMenu可以滑动
   * */
    private void isEnableSlidingMenu(int touchmodeFullscreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }

    class MyNewsMenuDetailPagerAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).getTitle();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager tabDetailPager  =tabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
            tabDetailPager.initData();//初始化数据
            container.addView(rootView);
            return rootView;
        }

        @Override
        public int getCount() {
            return tabDetailPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}