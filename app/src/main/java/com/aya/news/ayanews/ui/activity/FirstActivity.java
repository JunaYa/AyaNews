package com.aya.news.ayanews.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.base.BaseActivity;

/**
 * Created by Single on 2015/10/22.
 */
public class FirstActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);
        next();
    }


    private SharedPreferences sp;

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
}
