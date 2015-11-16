package com.aya.news.ayanews.ui.fragment_main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.adapter.FragmentAdapter;
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
public class NewsFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private HorizontalScrollView hsView;
    private RadioGroup channelTabGroup;
    private ViewPager viewPager;
    private FragmentAdapter mAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private View rootView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
        mAdapter = new FragmentAdapter(super.getActivity().getSupportFragmentManager(), fragments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_news, container, false);

            hsView = (HorizontalScrollView) rootView.findViewById(R.id.hsView);
            channelTabGroup = (RadioGroup) rootView.findViewById(R.id.tab_channel);
            channelTabGroup.setOnCheckedChangeListener(this);
            initChannelTab(inflater);
            viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    setTab(position);
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            viewPager.setAdapter(mAdapter);
            viewPager.setOffscreenPageLimit(2);
            viewPager.setCurrentItem(0);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null){
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void initChannelTab(LayoutInflater inflater) {
        ArrayList<Channel> channels = (ArrayList<Channel>) ChannelData.getSelectedChannel();
        for (int i = 0; i < channels.size(); i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_tab_channel, null);
            radioButton.setId(i);
            radioButton.setText(channels.get(i).getName());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            channelTabGroup.addView(radioButton, params);
        }
        ((RadioButton) channelTabGroup.getChildAt(0)).setChecked(true);
    }

    private void setTab(int idx) {
        RadioButton radioButton = (RadioButton) channelTabGroup.getChildAt(idx);
        radioButton.setChecked(true);
        int left = radioButton.getLeft();
        int width = radioButton.getWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width / 2 - screenWidth / 2;
        hsView.smoothScrollTo(len, 0);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        viewPager.setCurrentItem(checkedId);
    }
}
