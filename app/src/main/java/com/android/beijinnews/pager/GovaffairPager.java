package com.android.beijinnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.beijinnews.base.BasePager;
import com.android.beijinnews.utils.LogUtil;

/**
 * Created by msi on 2017/4/10.
 */

public class GovaffairPager extends BasePager {

    public GovaffairPager(Context context) {
        super(context);
    }

    /*
   * 初始化数据，当孩子需要初始化数据或者绑定数据，联网请求并绑定数据的时候，重写该方法
   * */
    @Override
    public void initData() {
        super.initData();
        LogUtil.e("政要页面数据被初始化了");
        ib_menu.setVisibility(View.GONE);
        //1.可以设置标题
        tv_title.setText("政要");
        //2.联网请求得到数据，创建视图
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3.添加子视图到BasePager的FrameLayout中
        fl_content.addView(textView);
        //4.绑定数据
        textView.setText("政要面内容");
    }
}
