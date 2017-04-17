package com.android.beijinnews.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {
    /*
    * 得到缓存值
    * @param context 上下文
    * @param key
    * @return
    * */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("toutiao",Context.MODE_PRIVATE);
        return  sp.getBoolean(key,false);
    }

    /*
    * 保存软件参数
    * @param contexy
    * @param key
    * @param value
    * */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("toutiao",Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }

    /*
    * 缓存文本数据
    * */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("toutiao",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }

    /*
    * 获取缓存文本信息
    * */
    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("toutiao",Context.MODE_PRIVATE);
        return  sp.getString(key,"");
    }
}
