package com.aya.news.ayanews.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aya.news.ayanews.R;

/**
 * Created by Single on 2015/10/22.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void setHeaderTitle(String title){
        TextView titleView = (TextView)findViewById(R.id.page_title);
        titleView.setText(title);
    }

    protected void showBackBtn(){
        ImageButton backBtn = (ImageButton)findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void showRightBtn(String str, View.OnClickListener onClickListener){
        Button right = (Button)findViewById(R.id.btn_right_text);
        right.setText(str);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(onClickListener);
    }
}
