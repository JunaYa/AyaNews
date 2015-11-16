package com.aya.news.ayanews.ui.fragment_main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.adapter.FragmentAdapter;
import com.aya.news.ayanews.ui.base.BaseFragment;
import com.aya.news.ayanews.ui.fragment_sub.BroadCastingFragment;
import com.aya.news.ayanews.ui.fragment_sub.VideoFragment;

import java.util.ArrayList;

/**
 * Created by Single on 2015/10/23.
 */
public class VaFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private FragmentAdapter mAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private ViewPager viewPager;
    private RadioButton rbVideo, rbBroadcasting;

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VideoFragment videoFragment = new VideoFragment();
        BroadCastingFragment broadcastingFragment = new BroadCastingFragment();
        fragments.add(videoFragment);
        fragments.add(broadcastingFragment);
        mAdapter = new FragmentAdapter(super.getActivity().getSupportFragmentManager(), fragments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_va, container, false);

            rbVideo = (RadioButton) rootView.findViewById(R.id.video);
            rbBroadcasting = (RadioButton) rootView.findViewById(R.id.broadcasting);
            rbVideo.setOnClickListener(this);
            rbBroadcasting.setOnClickListener(this);
            viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
            viewPager.addOnPageChangeListener(this);
            viewPager.setAdapter(mAdapter);
            viewPager.setCurrentItem(0);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.broadcasting:
                viewPager.setCurrentItem(1, true);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            rbVideo.setChecked(true);
        } else {
            rbBroadcasting.setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
