package com.android.beijinnews.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

import com.android.beijinnews.R;
import com.android.beijinnews.fragment.ContentFragment;
import com.android.beijinnews.fragment.LeftmenuFragment;
import com.android.beijinnews.utils.DensityUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    public static final String MAIN_CONTENT_TAG = "main_content_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
        super.onCreate(savedInstanceState);
        //设置滑动
        intiSlidingMenu();

        //初始化Fragment
        initFragment();
    }

    private void intiSlidingMenu() {
        //1.设置主页面
        setContentView(R.layout.activity_main);

        //2.设置左侧菜单
        setBehindContentView(R.layout.activity_leftmenu);

        //2.1.设置右侧菜单
        SlidingMenu slidingMenu = getSlidingMenu();
        //slidingMenu.setSecondaryMenu(R.layout.activity_rightmenu);

        //3.设置显示模式：左侧菜单+主页+右侧菜单
        slidingMenu.setMode(SlidingMenu.LEFT);

        //4.设置滑动模式：滑动边缘，全屏滑动，不可滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        //5.设置主页占据的宽度
        slidingMenu.setBehindOffset(DensityUtil.dip2px(MainActivity.this, 200));
    }

    private void initFragment() {
        //1.得到FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        //2.开启事物
        FragmentTransaction ft = fm.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main_content, new ContentFragment(), MAIN_CONTENT_TAG);//主页
        ft.replace(R.id.fl_leftmenu, new LeftmenuFragment(), LEFTMENU_TAG);//左侧菜单
        //4.提交
        ft.commit();

    }

    /*
    * 得到左侧菜单的Fragment
    * */
    public LeftmenuFragment getLeftMenuFragment() {
//        FragmentManager fm = getSupportFragmentManager();
//        LeftmenuFragment leftmenuFragment = (LeftmenuFragment) fm.findFragmentByTag(LEFTMENU_TAG);//根据标签得到左侧菜单
        return (LeftmenuFragment) getSupportFragmentManager().findFragmentByTag(LEFTMENU_TAG);
    }

    /*
    * 得到正文Fragment
    * */
    public ContentFragment getContentFragment() {
        return (ContentFragment) getSupportFragmentManager().findFragmentByTag(MAIN_CONTENT_TAG);
    }
}