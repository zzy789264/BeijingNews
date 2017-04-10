package com.android.beijinnews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.android.beijinnews.activity.GuideActivity;
import com.android.beijinnews.activity.MainActivity;
import com.android.beijinnews.utils.CacheUtils;

public class WelcomeActivity extends Activity {

    public static final String START_MAIN = "start_main";
    private RelativeLayout activity_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        activity_welcome = (RelativeLayout) findViewById(R.id.activity_welcome);

        //渐变动画
        AlphaAnimation aa = new AlphaAnimation(0, 1);

        //缩放动画
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);

        //旋转动画
        RotateAnimation ra = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        //设置动画播放时间
        set.setDuration(2000);
        //添加三个动画效果,同时播放
        set.addAnimation(sa);
        set.addAnimation(aa);
        set.addAnimation(ra);

        activity_welcome.startAnimation(set);

        set.setAnimationListener(new MyAnimationListener());

    }


    class MyAnimationListener implements Animation.AnimationListener {

        /*
        * 当动画开始播放时
        * */
        @Override
        public void onAnimationStart(Animation animation) {

        }

        /*
        * 当动画停止播放时
        * */
        @Override
        public void onAnimationEnd(Animation animation) {

            Intent intent;
            //判断是否进入过主页面
            boolean isStartMasin = CacheUtils.getBoolean(WelcomeActivity.this, START_MAIN);
            if (isStartMasin) {
                //进入过主页面就直接进入主页面
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            } else {
                //没有进入过主页面就进入引导页面
                intent = new Intent(WelcomeActivity.this, GuideActivity.class);
            }
            startActivity(intent);
            finish();
        }

        /*
        * 当动画重新播放时
        * */
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
