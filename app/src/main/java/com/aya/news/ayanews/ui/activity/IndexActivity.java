package com.aya.news.ayanews.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.CircularPropagation;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by Single on 2015/10/22.
 */
public class IndexActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private IndexImagesAdapter mAdapter;
    //    private final int[] imgs = {
//            R.mipmap.biz_more_menu_t0,
//            R.mipmap.biz_more_menu_t1,
//            R.mipmap.biz_more_menu_t2,
//            R.mipmap.biz_more_menu_t3,
//            R.mipmap.biz_more_menu_t4,
//            R.mipmap.biz_more_menu_t5,
//            R.mipmap.biz_more_menu_t6,
//            R.mipmap.biz_more_menu_t7,
//            R.mipmap.biz_more_menu_t8,
//            R.mipmap.biz_more_menu_t9
//    };
    private String[] imageUrls = {"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
            "http://pic30.nipic.com/20130626/8174275_085522448172_2.jpg",
            "http://pic18.nipic.com/20111215/577405_080531548148_2.jpg",
            "http://pic15.nipic.com/20110722/2912365_092519919000_2.jpg",
            "http://pic.58pic.com/58pic/12/64/27/55U58PICrdX.jpg"};

    private TextView enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new IndexImagesAdapter();
        viewPager.setAdapter(mAdapter);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        enter = (TextView) findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class IndexImagesAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(IndexActivity.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Glide.with(IndexActivity.this).load(imageUrls[position]).placeholder(R.mipmap.biz_more_menu_t0).into(imageView);
            if (position == (imageUrls.length - 1)) {
                enter.setVisibility(View.VISIBLE);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
