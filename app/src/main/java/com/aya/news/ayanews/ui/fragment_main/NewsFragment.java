package com.aya.news.ayanews.ui.fragment_main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.adapter.FragmentAdapter;
import com.aya.news.ayanews.config.Const;
import com.aya.news.ayanews.model.Channel;
import com.aya.news.ayanews.model.ChannelData;
import com.aya.news.ayanews.ui.base.BaseFragment;
import com.aya.news.ayanews.ui.fragment_sub.NormalNewsFragment;
import com.aya.news.ayanews.ui.fragment_sub.PhotoNewsFragment;

import java.util.ArrayList;

/**
 * Created by Single on 2015/10/22.
 */
public class NewsFragment extends BaseFragment {

    private HorizontalScrollView hsView;
    private RadioGroup channelTabGroup;
    private ViewPager viewPager;
    private FragmentAdapter mAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new FragmentAdapter(super.getActivity().getSupportFragmentManager(), fragments);
        initFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_news, container, false);

            hsView = (HorizontalScrollView)findViewById(R.id.hsView);
            channelTabGroup = (RadioGroup) findViewById(R.id.tab_channel);
            initChannelTab(inflater);
            viewPager = (ViewPager) findViewById(R.id.view_pager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setTab(position);
                    viewPager.setCurrentItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.setAdapter(mAdapter);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setCurrentItem(0);
        }
        return rootView;
    }

    private void initChannelTab(LayoutInflater inflater) {
        ArrayList<Channel> channels = (ArrayList<Channel>) ChannelData.getSelectedChannel();
        for (int i = 0; i < channels.size(); i++) {
            RadioButton radioButton = (RadioButton)inflater.inflate(R.layout.item_tab_channel,null);
            radioButton.setId(i);
            radioButton.setText(channels.get(i).getName());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            channelTabGroup.addView(radioButton, params);
        }
    }

    private void setTab(int idx){
        RadioButton radioButton = (RadioButton)channelTabGroup.getChildAt(idx);
        radioButton.setChecked(true);
        int left = radioButton.getLeft();
        int width = radioButton.getWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width/2 - screenWidth/2;
        hsView.smoothScrollTo(len,0);
    }
    private void initFragment() {
        ArrayList<Channel> channels = (ArrayList<Channel>) ChannelData.getSelectedChannel();
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getOrder() == 1) {
                PhotoNewsFragment photoNewsFragment = new PhotoNewsFragment();
                fragments.add(photoNewsFragment);
            } else {
                NormalNewsFragment normalNewsFragment = new NormalNewsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Const.ARG_CHANNEL_ID, channels.get(i).getId());
                bundle.putString(Const.ARG_CHANNEL_NAME, channels.get(i).getName());
                bundle.putString(Const.ARG_CHANNEL_ORDER, channels.get(i).getOrder() + "");
                bundle.putString(Const.ARG_CHANNEL_URL, channels.get(i).getWeburl());
                normalNewsFragment.setArguments(bundle);
                fragments.add(normalNewsFragment);
            }
        }
    }
}
