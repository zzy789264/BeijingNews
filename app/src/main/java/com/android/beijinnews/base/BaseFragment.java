package com.android.beijinnews.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment基类
 */

public abstract class BaseFragment extends Fragment{

    public Context context;//MainActivity

    /*
    * 当Fragment创建时回调此方法
    * */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    /*
    * 当View被创建时回调此方法
    * */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    /**
     * 让孩子视图实现自己，达到自身效果
     */
    public abstract View initView();

    /*
    * 当Activity被创建时候会回调此方法
    * */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /*
    * 如果子页面没有数据，联网请求数据，并且绑定到initView上
    * */
    public  void initData(){

    }
}
