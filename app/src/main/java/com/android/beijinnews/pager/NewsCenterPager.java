package com.android.beijinnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.beijinnews.activity.MainActivity;
import com.android.beijinnews.base.BasePager;
import com.android.beijinnews.base.MenuDetailBasePager;
import com.android.beijinnews.domain.NewsCenterPagerBean;
import com.android.beijinnews.fragment.LeftmenuFragment;
import com.android.beijinnews.menudetailpager.InteracMenuDetailPager;
import com.android.beijinnews.menudetailpager.NewsMenuDetailPager;
import com.android.beijinnews.menudetailpager.PhotosMenuDetailPager;
import com.android.beijinnews.menudetailpager.TopicMenuDetailPager;
import com.android.beijinnews.utils.Constants;
import com.android.beijinnews.utils.LogUtil;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msi on 2017/4/10.
 */

public class NewsCenterPager extends BasePager {

    public NewsCenterPager(Context context) {
        super(context);
    }
    //详情页面集合
    private ArrayList<MenuDetailBasePager> detailBasePagers;

    private  List<NewsCenterPagerBean.DataBean> data;


    /*
   * 初始化数据，当孩子需要初始化数据或者绑定数据，联网请求并绑定数据的时候，重写该方法
   * */
    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻中心数据被初始化了");
        ib_menu.setVisibility(View.VISIBLE);
        //1.可以设置标题
        tv_title.setText("新闻中心");
        //2.联网请求得到数据，创建视图
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3.添加子视图到BasePager的FrameLayout中
        fl_content.addView(textView);
        //4.绑定数据
        textView.setText("新闻中心面内容");

        //联网请求数据
        getDataFromNet();
    }

    /*
    * 使用xUtils3联网请求数据
    * */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NEWSCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("使用xUtils3联网请求成功 == " + result);

                //设置适配器
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("使用xUtils3联网请求失败 == " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("使用xUtils3-onCancelled == " + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("使用xUtils3-onFinished == ");
            }
        });
    }

    /*
    * 解析JSON数据和显示数据
    * */
    private void processData(String json) {
        NewsCenterPagerBean bean = parsedJson(json);
        String title = bean.getData().get(0).getChildren().get(1).getTitle();
        LogUtil.e("使用Gson解析数据成功 == " + title);

        //给左侧菜单传递数据
        data = bean.getData();

        MainActivity mainActivity = (MainActivity) context;
        //得到左侧菜单
        LeftmenuFragment leftmenuFragment = mainActivity.getLeftMenuFragment();

        //添加新闻详情页面
        detailBasePagers = new ArrayList<>();
        detailBasePagers.add(new NewsMenuDetailPager(context));
        detailBasePagers.add(new TopicMenuDetailPager(context));
        detailBasePagers.add(new PhotosMenuDetailPager(context));
        detailBasePagers.add(new InteracMenuDetailPager(context));

        //把数据传递给左侧菜单
        leftmenuFragment.setData(data);

    }

    /*
    *
    * 使用第三方框架Gson解析Json数据
    * */
    private NewsCenterPagerBean parsedJson(String json){
//        Gson gson = new Gson();
//        NewsCenterPagerBean bean = gson.fromJson(json,NewsCenterPagerBean.class);
        return new Gson().fromJson(json,NewsCenterPagerBean.class);
    }

    /*
    * 根据位置切换详情页面
    * */
    public void swichPager(int position) {
        //1.设置详情页面标题
        tv_title.setText(data.get(position).getTitle());
        //2.移除之前的内容
        fl_content.removeAllViews();
        //3.添加新内容
        MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
        View rootView = detailBasePager.rootView;
        detailBasePager.initData();//初始化数据

        fl_content.addView(rootView);
    }
}
