package com.android.beijinnews.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.beijinnews.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义下拉刷新ListView
 */

public class RefreshListview extends ListView {

    //包含下拉刷新
    private LinearLayout headerView;

    //下拉刷新控件
    private View ll_pull_down_refresh;
    private ImageView iv_arrow;
    private ProgressBar pb_status;
    private TextView tv_status;
    private TextView tv_time;
    //下来刷新控件的高
    private int pullDownRefreshHeight;
    //下拉刷新
    public static final int PULL_DOWN_REFRESH = 0;
    //手松刷新
    public static final int RELEASE_REFRESH = 1;
    //正在刷新
    public static final int REFRESHING = 2;
    //当前状态
    private int currentStatus = PULL_DOWN_REFRESH;

    private Animation upAnimation;
    private Animation downAnimation;
    //加载更多的控件
    private View footerView;
    private int footerViewHeight;
    //是否已经加载更多
    private boolean isLoadMore = false;
    //顶部轮播图
    private View topNewsView;
    //listView在Y轴上的坐标
    private int listViewOnScreenY = -1;

    public RefreshListview(Context context) {
        this(context, null);
    }

    public RefreshListview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView(context);
        initAnimation();
        initFooterView(context);
    }

    private void initFooterView(Context context) {
        footerView = View.inflate(context, R.layout.refresh_footer, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();

        footerView.setPadding(0, -footerViewHeight, 0, 0);

        //ListView添加foot
        addFooterView(footerView);

        //ListView滚动监听
        setOnScrollListener(new MyOnScrollListener());
    }

    /*
    * 添加顶部轮播图
    * */
    public void addTopNewsView(View topNewsView) {
        if (topNewsView != null) {
            this.topNewsView = topNewsView;
            headerView.addView(topNewsView);
        }
    }

    class MyOnScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //当静止或惯性滚动
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_FLING) {
                //且最后一条为可见
                if (getLastVisiblePosition() >= getCount() - 1) {
                    //1.显示加载更多布局
                    footerView.setPadding(8, 8, 8, 8);
                    //2.改变状态
                    isLoadMore = true;
                    //3.回调接口
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

    private void initAnimation() {
        upAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(-180, -360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);
    }


    private void initHeaderView(Context context) {
        headerView = (LinearLayout) View.inflate(context, R.layout.refresh_header, null);

        ll_pull_down_refresh = headerView.findViewById(R.id.ll_pull_down_refresh);
        iv_arrow = (ImageView) headerView.findViewById(R.id.iv_arrow);
        pb_status = (ProgressBar) headerView.findViewById(R.id.pb_status);
        tv_status = (TextView) headerView.findViewById(R.id.tv_status);
        tv_time = (TextView) headerView.findViewById(R.id.tv_time);

        //测量父控件
        ll_pull_down_refresh.measure(0, 0);
        pullDownRefreshHeight = ll_pull_down_refresh.getMeasuredHeight();

        //设置默认隐藏下拉刷新控件
        ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);

        //添加ListView的HeaderView
        addHeaderView(headerView);
    }

    private float startY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                //1.记录起始坐标
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {
                    startY = ev.getY();
                }

                //判断顶部轮播图是否完全显示，只有完全显示才有下拉刷新
                boolean isDisplayTopNews = isDisplayTopNews();
                if (!isDisplayTopNews) {
                    //加载更多
                    break;
                }

                //如果正在刷新则无法刷新
                if (currentStatus == REFRESHING) {
                    break;
                }
                //2.来到新的坐标
                float endY = ev.getY();
                //3.记录滑动的距离
                float distanceY = endY - startY;
                if (distanceY > 0) {//下拉
                    int paddingTop = (int) (-pullDownRefreshHeight + distanceY);

                    if (paddingTop < 0 && currentStatus != PULL_DOWN_REFRESH) {
                        //下拉刷新状态
                        currentStatus = PULL_DOWN_REFRESH;
                        //更新状态
                        refreshViewState();
                    } else if (paddingTop > 0 && currentStatus != RELEASE_REFRESH) {
                        //手松刷新状态
                        currentStatus = RELEASE_REFRESH;
                        //更新状态
                        refreshViewState();
                    }
                    ll_pull_down_refresh.setPadding(0, paddingTop, 0, 0);//动态显示下拉控件
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (currentStatus == PULL_DOWN_REFRESH) {
                    // View.setPadding(0,-控件高，0,0);//完全隐藏
                    ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);
                } else if (currentStatus == RELEASE_REFRESH) {
                    //设置状态为正在刷新
                    currentStatus = REFRESHING;
                    refreshViewState();
                    //View.setPadding(0,0，0,0);//完全显示
                    ll_pull_down_refresh.setPadding(0, 0, 0, 0);
                    //回调接口
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onPullDownRefresh();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /*
    * 判断是否完整显示顶部轮播图
    * */
    private boolean isDisplayTopNews() {
        if (topNewsView != null) {

            //1.得到ListView在屏幕上的坐标
            int[] location = new int[2];
            if (listViewOnScreenY == -1) {
                getLocationOnScreen(location);
                listViewOnScreenY = location[1];
            }
            //2.得到顶部轮播图在屏幕上的坐标
            topNewsView.getLocationOnScreen(location);
            int topNewsViewOnScreenY = location[1];
//
//        if (listViewOnScreenY <= topNewsViewOnScreenY) {
//            return true;
//        } else {
//            return false;
//        }

            return listViewOnScreenY <= topNewsViewOnScreenY;
        } else {
            return true;
        }
    }

    private void refreshViewState() {
        switch (currentStatus) {
            case PULL_DOWN_REFRESH://下拉刷新状态
                iv_arrow.startAnimation(downAnimation);
                tv_status.setText("下拉刷新...");
                break;
            case RELEASE_REFRESH://释放刷新状态
                iv_arrow.startAnimation(upAnimation);
                tv_status.setText("释放刷新...");
                break;
            case REFRESHING://正在刷新状态
                tv_status.setText("正在刷新...");
                pb_status.setVisibility(VISIBLE);
                iv_arrow.clearAnimation();
                iv_arrow.setVisibility(GONE);
                break;
        }
    }

    /*
    * 当联网请求和失败时回调此方法
    * */
    public void onRefreshFinish(boolean success) {
        if (isLoadMore) {
            //加载更多
            isLoadMore = false;
            //隐藏加载更多控件
            footerView.setPadding(0, -footerViewHeight, 0, 0);
        } else {
            //下拉刷新
            tv_status.setText("下拉刷新...");
            currentStatus = PULL_DOWN_REFRESH;
            iv_arrow.clearAnimation();
            pb_status.setVisibility(GONE);
            iv_arrow.setVisibility(VISIBLE);
            //隐藏下拉刷新控件
            ll_pull_down_refresh.setPadding(0, -pullDownRefreshHeight, 0, 0);
            if (success) {
                //设置更新时间
                tv_time.setText("上次更新时间：" + getSystemTime());
            }
        }
    }


    /*
    * 获取手机当前时间
    * */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }


    /**
     * 监听控件的刷新
     */
    public interface OnRefreshListener {

        /**
         * 当下拉刷新的时候回调这个方法
         */
        public void onPullDownRefresh();

        /**
         * 当加载更多的时候回调这个方法
         */
        public void onLoadMore();

    }

    private OnRefreshListener mOnRefreshListener;

    /**
     * 设置监听刷新，由外界设置
     */
    public void setOnRefreshListener(OnRefreshListener l) {
        this.mOnRefreshListener = l;

    }
}
