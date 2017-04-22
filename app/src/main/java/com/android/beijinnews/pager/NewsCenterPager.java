package com.android.beijinnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.beijinnews.activity.MainActivity;
import com.android.beijinnews.base.BasePager;
import com.android.beijinnews.base.MenuDetailBasePager;
import com.android.beijinnews.domain.NewsCenterPagerBean2;
import com.android.beijinnews.domain.PhotosMenuDetailPagerBean;
import com.android.beijinnews.fragment.LeftmenuFragment;
import com.android.beijinnews.menudetailpager.InteracMenuDetailPager;
import com.android.beijinnews.menudetailpager.NewsMenuDetailPager;
import com.android.beijinnews.menudetailpager.PhotosMenuDetailPager;
import com.android.beijinnews.menudetailpager.TopicMenuDetailPager;
import com.android.beijinnews.utils.CacheUtils;
import com.android.beijinnews.utils.Constants;
import com.android.beijinnews.utils.LogUtil;
import com.android.beijinnews.volley.VolleyManager;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msi on 2017/4/10.
 */

public class NewsCenterPager extends BasePager {

    //起始时间
    private long startTime;

    public NewsCenterPager(Context context) {
        super(context);
    }

    //详情页面集合
    private ArrayList<MenuDetailBasePager> detailBasePagers;

    private List<NewsCenterPagerBean2.DetailPagerData> data;

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

        //获取缓存数据
        String saveJson = CacheUtils.getString(context, Constants.NEWSCENTER_PAGER_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }

        startTime = SystemClock.uptimeMillis();

        //联网请求数据
        getDataFromNet();
        //getDataFromNetByVolley();

    }

    /*
    * 使用Volley联网请求数据
    * */
    private void getDataFromNetByVolley() {
        //请求队列
        RequestQueue queue = Volley.newRequestQueue(context);
        //String请求
        StringRequest request = new StringRequest(Request.Method.GET, Constants.NEWSCENTER_PAGER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime;
                LogUtil.e("Volley花费时间 == " + passTime);
                LogUtil.e("使用Volley联网请求成功 == " + result);
                //缓存数据
                CacheUtils.putString(context, Constants.NEWSCENTER_PAGER_URL, result);

                //设置适配器
                processData(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("使用Volley联网请求失败 == " + volleyError);
            }
        }) {
            //设置文字编码格式UTF-8
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };

        //添加到队列中
        VolleyManager.getRequestQueue().add(request);
    }

    /*
    * 使用xUtils3联网请求数据
    * */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NEWSCENTER_PAGER_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                long endTime = SystemClock.uptimeMillis();
                long passTime = endTime - startTime;
                LogUtil.e("xUtils3花费时间 == " + passTime);
                LogUtil.e("使用xUtils3联网请求成功 == " + result);

                //缓存数据
                CacheUtils.putString(context, Constants.NEWSCENTER_PAGER_URL, result);

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
        // NewsCenterPagerBean bean = parsedJson(json);
        NewsCenterPagerBean2 bean = parsedJson2(json);
        // String title = bean.getData().get(0).getChildren().get(1).getTitle();

        //  LogUtil.e("使用Gson解析数据成功-title == " + title);
        String title2 = bean.getData().get(0).getChildren().get(1).getTitle();
        LogUtil.e("使用Gson解析数据成功-title2 == " + title2);

        //给左侧菜单传递数据
        data = bean.getData();

        MainActivity mainActivity = (MainActivity) context;
        //得到左侧菜单
        LeftmenuFragment leftmenuFragment = mainActivity.getLeftMenuFragment();

        //添加新闻详情页面
        detailBasePagers = new ArrayList<>();
        detailBasePagers.add(new NewsMenuDetailPager(context, data.get(0)));
        detailBasePagers.add(new TopicMenuDetailPager(context, data.get(1)));
        detailBasePagers.add(new PhotosMenuDetailPager(context, data.get(2)));
        detailBasePagers.add(new InteracMenuDetailPager(context));

        //把数据传递给左侧菜单
        leftmenuFragment.setData(data);

    }

    /*使用Android自带API解析Json数据*/
    private NewsCenterPagerBean2 parsedJson2(String json) {
        NewsCenterPagerBean2 bean2 = new NewsCenterPagerBean2();
        try {
            JSONObject object = new JSONObject(json);

            //optInt防止使用getInt时当此字段不存在导致崩溃
            int retcode = object.optInt("retcode");
            bean2.setRetcode(retcode);//retcode字段解析成功
            JSONArray data = object.optJSONArray("data");
            //判断data数据是否存在
            if (data != null && data.length() > 0) {
                List<NewsCenterPagerBean2.DetailPagerData> detailPagerDatas = new ArrayList<>();
                //设置列表数据
                bean2.setData(detailPagerDatas);
                //for循环解析每条数据
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = (JSONObject) data.get(i);

                    NewsCenterPagerBean2.DetailPagerData detailPagerData = new NewsCenterPagerBean2.DetailPagerData();

                    //添加到集合中
                    detailPagerDatas.add(detailPagerData);

                    int id = jsonObject.optInt("id");
                    detailPagerData.setId(id);
                    int type = jsonObject.optInt("type");
                    detailPagerData.setType(type);
                    String title = jsonObject.optString("title");
                    detailPagerData.setTitle(title);
                    String url = jsonObject.optString("url");
                    detailPagerData.setUrl(url);
                    String url1 = jsonObject.optString("url1");
                    detailPagerData.setUrl(url1);
                    String dayurl = jsonObject.optString("dayurl");
                    detailPagerData.setDayurl(dayurl);
                    String excurl = jsonObject.optString("excurl");
                    detailPagerData.setExcurl(excurl);
                    String weekurl = jsonObject.optString("weekurl");
                    detailPagerData.setWeekurl(weekurl);

                    JSONArray children = jsonObject.optJSONArray("children");
                    //判断children数据是否存在
                    if (children != null && children.length() > 0) {

                        List<NewsCenterPagerBean2.DetailPagerData.ChildrenData> childrenDatas = new ArrayList<>();

                        //设置集合-存储childrenDatas
                        detailPagerData.setChildren(childrenDatas);
                        //存在
                        for (int j = 0; j < children.length(); j++) {
                            JSONObject childrenItem = (JSONObject) children.get(j);

                            NewsCenterPagerBean2.DetailPagerData.ChildrenData childrenData = new NewsCenterPagerBean2.DetailPagerData.ChildrenData();
                            //添加到集合中
                            childrenDatas.add(childrenData);

                            int childId = childrenItem.optInt("id");
                            childrenData.setId(childId);
                            String childTitle = childrenItem.optString("title");
                            String childUrl = childrenItem.optString("url");
                            childrenData.setUrl(childUrl);
                            childrenData.setTitle(childTitle);
                            int childType = childrenItem.optInt("type");
                            childrenData.setType(childType);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean2;
    }


    /*
    *
    * 使用第三方框架Gson解析Json数据
    * */
    private NewsCenterPagerBean2 parsedJson(String json) {
//        Gson gson = new Gson();
//        NewsCenterPagerBean bean = gson.fromJson(json,NewsCenterPagerBean.class);
        return new Gson().fromJson(json, NewsCenterPagerBean2.class);
    }

    /*
    * 根据位置切换详情页面
    * */
    public void switchPager(int position) {
        //1.设置详情页面标题
        tv_title.setText(data.get(position).getTitle());
        //2.移除之前的内容
        fl_content.removeAllViews();
        //3.添加新内容
        final MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
        View rootView = detailBasePager.rootView;
        detailBasePager.initData();//初始化数据

        fl_content.addView(rootView);

        if(position == 2){
            //图组详情页面
            ib_switch_list_grid.setVisibility(View.VISIBLE);
            //设置点击事件,切换图片显示方式
            ib_switch_list_grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.得到图组对象
                    PhotosMenuDetailPager detailPager = (PhotosMenuDetailPager) detailBasePagers.get(2);
                    //2.得到图组对象切换ListView和GridView的方法
                    detailPager.switchListAndGrid(ib_switch_list_grid);
                }
            });
        }else {
            //其他页面
            ib_switch_list_grid.setVisibility(View.GONE);
        }
    }
}
