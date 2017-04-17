package com.android.beijinnews.menudetailpager.tabdetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.beijinnews.R;
import com.android.beijinnews.base.MenuDetailBasePager;
import com.android.beijinnews.domain.NewsCenterPagerBean2;
import com.android.beijinnews.domain.TabDetailPagerBean;
import com.android.beijinnews.utils.CacheUtils;
import com.android.beijinnews.utils.Constants;
import com.android.beijinnews.utils.LogUtil;
import com.android.beijinnews.view.HorizontalScrollViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * 标签详情页面
 */

public class TabDetailPager extends MenuDetailBasePager {

    private HorizontalScrollViewPager viewpager;
    private TextView tv_title;
    private LinearLayout ll_point_group;
    private ListView listview;
    private TabDetailPagerListAdapter adapter;
    private ImageOptions imageOptions;
    private List<TabDetailPagerBean.DataBean.NewsData> news;

    //之前红点的位置
    private int prePosition;

    private final NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData;
    private String url;
    //头条新闻轮播集合
    private List<TabDetailPagerBean.DataBean.TopnewsData> topnews;


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
        listview = (ListView) view.findViewById(R.id.listview);

        View topNewsView = View.inflate(context, R.layout.topnews, null);
        viewpager = (HorizontalScrollViewPager) topNewsView.findViewById(R.id.viewpager);
        tv_title = (TextView) topNewsView.findViewById(R.id.tv_title);
        ll_point_group = (LinearLayout) topNewsView.findViewById(R.id.ll_point_group);

        //把顶部轮播图，以表头的形式添加到ListView中
        listview.addHeaderView(topNewsView);

        return view;
    }

    @Override
    public void initData() {
        url = Constants.BASE_URL + childrenData.getUrl();
        String saveJson = CacheUtils.getString(context, url);
        //取出缓存数据
        if (!TextUtils.isEmpty(saveJson)) {
            //解析和处理显示数据
            processData(saveJson);
        }
        //LogUtil.e(childrenData.getTitle() + "的联网地址 == " + url);
        //联网请求数据
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                //缓存数据
                CacheUtils.putString(context, url, result);
                LogUtil.e(childrenData.getTitle() + "页面数据请求成功 == " + result);
                //解析和处理显示数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(childrenData.getTitle() + "页面数据请求失败 == " + ex.getMessage());
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

        @Override
        public void onPageScrollStateChanged(int state) {

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
