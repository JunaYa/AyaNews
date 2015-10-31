package com.aya.news.ayanews.ui.activity;


import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.aya.news.ayanews.MyApp;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.common.ExampleUtil;
import com.aya.news.ayanews.ui.base.BaseActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Single on 2015/10/22.
 */
public class FirstActivity extends BaseActivity {
    private MyApp myApp;
    public static boolean isForeground = false;
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        myApp = (MyApp) getApplication();
        setContentView(R.layout.activity_wellcome);
        next();
        //JPushInterface.init(getApplicationContext());
        registerMessageReceiver();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //极光推送
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //极光推送
        JPushInterface.onPause(this);
        super.onPause();

    }

    private void next() {
        sp = super.getSharedPreferences("app_param", Context.MODE_PRIVATE);
        int user = sp.getInt("user", 0);
        if (user == 0) {
            goIndex();
        } else {
            goHome();
        }
    }

    private void goIndex() {
        Intent intent = new Intent(FirstActivity.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    private void goHome() {
        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }
}
