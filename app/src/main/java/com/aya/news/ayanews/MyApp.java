package com.aya.news.ayanews;

import android.app.Application;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);//初始化，只一次
        JPushInterface.setAlias(this, "12345", new TagAliasCallback() {
                    @Override
                    public void gotResult(int arg0, String arg1, Set<String> arg2) {
                        // TODO Auto-generated method stub
                        if (arg0 == 0) {
                        }
                    }
                }
        );
    }
}
