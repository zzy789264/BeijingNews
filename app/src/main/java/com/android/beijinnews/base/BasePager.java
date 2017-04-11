package com.android.beijinnews.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.beijinnews.R;
import com.android.beijinnews.activity.MainActivity;

/**
 * 基类，用于继承
 */

public class BasePager {

    //上下文MainActivity
    public final Context context;

    //代表各个不同的页面
    public View rootView;
    //显示标题
    public TextView tv_title;
    //点击侧滑
    public ImageButton ib_menu;
    //帧布局，用以加载各个子页面
    public FrameLayout fl_content;

    public BasePager(Context context) {
        this.context = context;
        //构造方法一执行，视图初始化
        rootView = initView();
    }

    /*
    * 用于初始化公共部分视图，并且初始化加载子视图的FrameLayout
    * */
    private View initView() {
        //基类页面
        View view = View.inflate(context, R.layout.base_pager, null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//设置页面开--关
            }
        });
        return view;
    }

    /*
    * 初始化数据，当孩子需要初始化数据或者绑定数据，联网请求并绑定数据的时候，重写该方法
    * */
    public void initData() {

    }

}
