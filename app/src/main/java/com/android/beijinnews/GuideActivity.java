package com.android.beijinnews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.beijinnews.utils.CacheUtils;
import com.android.beijinnews.utils.DensityUtil;

import java.util.ArrayList;

public class GuideActivity extends Activity {

    private ViewPager viewPager;
    private Button btn_start_main;
    private LinearLayout ll_point_group;
    private ImageView iv_red_point;

    private ArrayList<ImageView> imageViews;

    //两点间距
    private int leftmax;

    private  int widthdpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        btn_start_main = (Button) findViewById(R.id.btn_start_main);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);
        //准备数据
        int[] ids = new int[]{
                R.drawable.guide1,
                R.drawable.guide2,
                R.drawable.guide3
        };

        widthdpi = DensityUtil.dip2px(this,10);

        imageViews = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            ImageView imageView = new ImageView(this);
            //设置背景
            imageView.setBackgroundResource(ids[i]);

            //添加到集合
            imageViews.add(imageView);

            //创建点
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);

            /*
            * 单位为像素
            * 将dp转为对应的像素
            * */
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthdpi, widthdpi);
            if (i != 0) {
                //不包括第零个，所有点距离左边有10个像素
                params.leftMargin = widthdpi;
            }
            point.setLayoutParams(params);
            //添加到线性布局
            ll_point_group.addView(point);
        }
        //设置ViewPager适配器
        viewPager.setAdapter(new MyPagerAdapter());

        //根据View的生命周期，当视图执行到绘制阶段onLayout或者OnDraw的时候，就已经具有高宽边距
        //通过视图树设置全局监听
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());
    }

    class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            iv_red_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            //间距 = 第1个点到左边的距离 - 第0个点到左边的距离
            leftmax = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0).getLeft();

            //得到屏幕滑动的百分比
            viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

            //设置按钮点击事件
            btn_start_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.保存进入过主页面的参数
                    CacheUtils.putBoolean(GuideActivity.this,WelcomeActivity.START_MAIN,true);
                    //2.跳转主页面
                    Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    //监听页面改变
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        /*
        * 页面滚动会回调此方法
        * positionOffset 滑动百分比
        * positionOffsetPixels 滑动的像素
        * */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //两点间移动距离 = 屏幕滑动百分比 * 间距 positionOffset * leftmax
            //两点间滑动距离对应的坐标 = 原始起始位置 + 两点见移动距离
            int leftMargin = position * leftmax + (int) (positionOffset * leftmax);
            //params.leftMargin = 两点间滑动距离对应最表
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();
            params.leftMargin = leftMargin;
            iv_red_point.setLayoutParams(params);
        }

        /*
        * 页面被选择会回调此方法
        * */
        @Override
        public void onPageSelected(int position) {
            if(position == imageViews.size()-1){
                //最后一个页面显示进入按钮
                btn_start_main.setVisibility(View.VISIBLE);
            }else{
                //其他界面隐藏按钮
                btn_start_main.setVisibility(View.GONE);
            }
        }

        /*
        * 页面状态改变会回调此方法
        * */
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    class MyPagerAdapter extends PagerAdapter {

        /*
        * 返回数据总个数
        * */
        @Override
        public int getCount() {
            return imageViews.size();
        }

        /*
        * 得到View
        * position 姚创建的页面的位置
        * return 返回和创建当前页面有关系的值
        * */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            //添加到容器中
            container.addView(imageView);
            return imageView;
        }

        /*
                * view 当前创建的视图
                * object instantiateItem返回的值
                * */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            //return view == imageViews.get(Integer.parseInt((String) object));
            return view == object;
        }

        /*
        * 销毁页面
        * container 容器
        * position 要销毁的页面的位置
        * object 要销毁的页面
        * */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
