package com.android.beijinnews.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.beijinnews.R;
import com.android.beijinnews.activity.MainActivity;
import com.android.beijinnews.base.BaseFragment;
import com.android.beijinnews.domain.NewsCenterPagerBean;
import com.android.beijinnews.pager.NewsCenterPager;
import com.android.beijinnews.utils.DensityUtil;
import com.android.beijinnews.utils.LogUtil;

import java.util.List;

/**
 * 左侧菜单Fragment
 */

public class LeftmenuFragment extends BaseFragment {

    private List<NewsCenterPagerBean.DataBean> data;

    private LeftMenuFragmentAdapter adapter;

    private ListView listView;

    //点击的位置
    private int prePosition;


    @Override
    public View initView() {
        LogUtil.e("左侧菜单视图被初始化");
        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context, 40), 0, 0);
        listView.setDividerHeight(0);//设置分割线高度为0
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setSelector(android.R.color.transparent);//设置按下Item不变色

        //设置Item点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //1.记录点击的位置，点击变红
                prePosition = position;
                adapter.notifyDataSetChanged();//刷新适配器getCount()-->getView

                //2.把左侧菜单关闭
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//设置页面开--关

                //3.切换到对应的详情页面
                switchPager(prePosition);

            }
        });

        return listView;
    }

    /*
    * 根据位置切换不同详情页面
    * */
    private void switchPager(int position) {
        MainActivity mainActivity = (MainActivity) context;
        ContentFragment contentFragment = mainActivity.getContentFragment();
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        newsCenterPager.swichPager(position);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("左侧菜单数据被初始化");
    }

    /*
    * 接收数据
    * */
    public void setData(List<NewsCenterPagerBean.DataBean> data) {
        this.data = data;
        for (int i = 0; i < data.size(); i++) {
            LogUtil.e("title == " + data.get(i).getTitle());
        }

        //设置适配器
        adapter = new LeftMenuFragmentAdapter();
        listView.setAdapter(adapter);

        /*
        * 设置默认页面
        * */
        switchPager(prePosition);
    }

    class LeftMenuFragmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu, null);
            textView.setText(data.get(position).getTitle());
//            if (position == prePosition) {
//                textView.setEnabled(true);
//            } else {
//                textView.setEnabled(false);
//            }

            textView.setEnabled(position == prePosition);
            return textView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
