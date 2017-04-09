package com.android.beijinnews.utils;

import android.content.Context;

/**
 * 单位转换工具
 */

public class DensityUtil {
    /*
    * 根据手机分辨率从dip的单位转化成px
    * */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /*
    * 根据手机分辨率从px的单位转化成dip
    * */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
