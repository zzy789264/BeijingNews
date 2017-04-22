package com.android.beijinnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.beijinnews.R;
import com.android.beijinnews.activity.NewsDetailActivity;
import com.android.beijinnews.base.MenuDetailBasePager;
import com.android.beijinnews.domain.NewsCenterPagerBean2;
import com.android.beijinnews.domain.TabDetailPagerBean;
import com.android.beijinnews.utils.CacheUtils;
import com.android.beijinnews.utils.Constants;
import com.android.beijinnews.utils.LogUtil;
import com.android.beijinnews.view.HorizontalScrollViewPager;
import com.android.beijinnews.view.RefreshListview;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * 标签详情页面
 */

public class TabDetailPager extends MenuDetailBasePager {

    public static final String READ_ARRAY_ID = "read_array_id";
    private HorizontalScrollViewPager viewpager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private RefreshListview listview;
    private TabDetailPagerListAdapter adapter;
    private ImageOptions imageOptions;
    private List<TabDetailPagerBean.DataBean.NewsData> news;

    //之前红点的位置
    private int prePosition;

    private final NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData;
    private String url;
    //头条新闻轮播集合
    private List<TabDetailPagerBean.DataBean.TopnewsData> topnews;
    //加载更多链接
    private String moreUrl;
    //是否加载更多
    private boolean isLoadMore = false;
    private InternalHandler internalHandler;


    public TabDetailPager(Context context, NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.home_scroll_default)
                .setFailureDrawableId(R.drawable.home_scroll_default)
                .build();
    }


    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        listview = (RefreshListview) view.findViewById(R.id.listview);

        View topNewsView = View.inflate(context, R.layout.topnews, null);
        viewpager = (HorizontalScrollViewPager) topNewsView.findViewById(R.id.viewpager);
        tv_title = (TextView) topNewsView.findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) topNewsView.findViewById(R.id.ll_point_group);

        //把顶部轮播图，以表头的形式添加到ListView中
        //listview.addHeaderView(topNewsView);
        listview.addTopNewsView(topNewsView);

        //设置监听刷新
        listview.setOnRefreshListener(new MyOnRefreshListener());

        //设置ListView点击监听
        listview.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int realposition = position - 1;
            TabDetailPagerBean.DataBean.NewsData newsData = news.get(realposition);
            //Toast.makeText(context, "newsData==id==" + newsData.getId() + "newsData_title==" + newsData.getTitle(), Toast.LENGTH_SHORT).show();
            LogUtil.e("newsData==id==" + newsData.getId() + ",newsData_title==" + newsData.getTitle() + ",url==" + newsData.getUrl());
            //1.取出保存的id集合
            String idArray = CacheUtils.getString(context, READ_ARRAY_ID);
            //2.判断是否存在，如果不存在就保存且刷新适配器
            if (idArray.contains(newsData.getId() + "")) {
                CacheUtils.putString(context, READ_ARRAY_ID, idArray + newsData.getId() + ",");
                //刷新适配器
                adapter.notifyDataSetChanged();
            }

            //跳转到新闻浏览页面
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("url", Constants.BASE_URL + newsData.getUrl());
            context.startActivity(intent);
        }
    }

    class MyOnRefreshListener implements RefreshListview.OnRefreshListener {

        @Override
        public void onPullDownRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
            if (TextUtils.isEmpty(moreUrl)) {
                Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                listview.onRefreshFinish(false);
            } else {
                getMoreDataFromNet();
            }
        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("加载更多联网请求成功 ==  " + result);
                listview.onRefreshFinish(false);
                isLoadMore = true;

                //解析数据
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多联网请求失败 ==  " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("加载更多联网请求onCancelled == " + cex.getMessage());

            }

            @Override
            public void onFinished() {
                LogUtil.e("加载更多联网请求onFinished");

            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        //取出缓存数据
        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            //解析和处理显示数据
            processData(saveJson);
        }
        //LogUtil.e(childrenData.getTitle() + "的联网地址 == " + url);
        //联网请求数据
        getDataFromNet();
    }

    private void getDataFromNet() {
        prePosition = 0;
        LogUtil.e("url地址 == " + url);
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "页面数据请求成功 == " + result);
                //解析和处理显示数据
                processData(result);

                //隐藏下拉刷新控件--更新时间
                listview.onRefreshFinish(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "页面数据请求失败 == " + ex.getMessage());
                //隐藏下拉刷新控件--不更新时间
                listview.onRefreshFinish(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e(childrenData.getTitle() + "页面数据请求onCancelled == " + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e(childrenData.getTitle() + "页面数据请求onFinished");
            }
        });
    }

    private void processData(String json) {
        TabDetailPagerBean bean = parsedJson(json);
        LogUtil.e(childrenData.getTitle() + "解析成功 == " + bean.getData().getNews().get(0).getTitle());

        moreUrl = "";
        if (TextUtils.isEmpty(bean.getData().getMore())) {
            moreUrl = "";
        } else {
            moreUrl = Constants.BASE_URL + bean.getData().getMore();
        }

        LogUtil.e("加载更多的地址 == " + moreUrl);
        //默认和加载更多
        if (!isLoadMore) {
            //默认
            //头条轮播数据
            topnews = bean.getData().getTopnews();

            //设置ViewPager的适配器
            viewpager.setAdapter(new TabDetailPagerTopNewsAdapter());

            //添加红点
            addPoint();

            //监听页面改变，设置红点和文本变化
            viewpager.addOnPageChangeListener(new MyOnPageChangeListener());
            //默认显示第一个新闻
            tv_title.setText(topnews.get(prePosition).getTitle());

            //准备ListView对应的集合数据
            news = bean.getData().getNews();
            //设置ListView的适配器
            adapter = new TabDetailPagerListAdapter();
            listview.setAdapter(adapter);
        } else {
            //加载更多
            isLoadMore = false;
            //添加更多到原新闻集合中
            news.addAll(bean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();
        }

        //发送消息每隔4000切换一次ViewPager页面
        if (internalHandler == null) {
            internalHandler = new InternalHandler();
        }

        //把消息队列的所有消息和回调移除
        internalHandler.removeCallbacksAndMessages(null);
        //4S后执行MyRunnable()中的run()方法
        internalHandler.postDelayed(new MyRunnable(), 4000);
    }

    class InternalHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //切换ViewPager的下一个页面
            int item = (viewpager.getCurrentItem() + 1) % topnews.size();
            viewpager.setCurrentItem(item);
            internalHandler.sendEmptyMessageDelayed(0, 4000);
        }
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            internalHandler.sendEmptyMessage(0);
        }
    }

    class TabDetailPagerListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_tabdetail_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //根据位置得到数据
            TabDetailPagerBean.DataBean.NewsData newsData = news.get(position);
            String imageUrl = Constants.BASE_URL + newsData.getListimage();
            //使用XUtil3请求图片
            // x.image().bind(viewHolder.iv_icon,imageUrl,imageOptions);

            //使用Glide请求图片
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.iv_icon);

            //设置标题
            viewHolder.tv_title.setText(newsData.getTitle());

            //设置时间
            viewHolder.tv_time.setText(newsData.getPubdate());

            String idArray = CacheUtils.getString(context, READ_ARRAY_ID);
            if (idArray.contains(newsData.getId() + "")) {
                //设置为灰色
                viewHolder.tv_title.setTextColor(Color.GRAY);
            } else {
                //设置为黑色
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

    private void addPoint() {
        //移除所有红点
        ll_point_group.removeAllViews();

        for (int i = 0; i < topnews.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.point_selector);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(org.xutils.common.util.DensityUtil.dip2px(5), org.xutils.common.util.DensityUtil.dip2px(5));


            if (i == 0) {
                imageView.setEnabled(true);
            } else {
                imageView.setEnabled(false);
                params.leftMargin = org.xutils.common.util.DensityUtil.dip2px(8);
            }
            imageView.setLayoutParams(params);
            ll_point_group.addView(imageView);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            //1设置文本
            tv_title.setText(topnews.get(position).getTitle());
            //2对应图片的点变红，
            // 没被选中的变灰
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            //设置当前选中为红
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition = position;
        }

        private boolean isDragging = false;

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {//拖拽
                isDragging = true;
                //拖拽时移除消息
                internalHandler.removeCallbacksAndMessages(null);
            } else if (state == ViewPager.SCROLL_STATE_SETTLING) {//惯性
                //发消息
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(), 4000);
            } else if (state == ViewPager.SCROLL_STATE_IDLE) {//静止
                //发消息
                isDragging = false;
                internalHandler.removeCallbacksAndMessages(null);
                internalHandler.postDelayed(new MyRunnable(), 4000);
            }
        }
    }

    class TabDetailPagerTopNewsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            //设置头条默认背景图片
            imageView.setBackgroundResource(R.drawable.home_scroll_default);
            //设置X和Y拉伸
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //把图片添加容器中
            container.addView(imageView);

            TabDetailPagerBean.DataBean.TopnewsData topnewsData = topnews.get(position);
            //图片请求地址
            String imageUrl = Constants.BASE_URL + topnewsData.getTopimage();

            //联网请求图片
            x.image().bind(imageView, imageUrl);

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            LogUtil.e("按下");
                            //把消息队列的所有消息和回调移除
                            internalHandler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            LogUtil.e("放开");
                            //把消息队列的所有消息和回调移除
                            internalHandler.removeCallbacksAndMessages(null);
                            //4S后执行MyRunnable()中的run()方法
                            internalHandler.postDelayed(new MyRunnable(), 4000);
                            break;
                    }
                    return true;
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    private TabDetailPagerBean parsedJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
