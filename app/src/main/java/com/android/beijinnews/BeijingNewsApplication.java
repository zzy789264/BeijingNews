package com.android.beijinnews;

import android.app.Application;

import org.xutils.x;

/**
 * Created by msi on 2017/4/10.
 */

public class BeijingNewsApplication extends Application{

    /*
    * 在所有主键被创建键之前执行
    * */
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.setDebug(true);
        x.Ext.init(this);
    }
}
