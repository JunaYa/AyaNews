package com.aya.news.ayanews.ui.fragment_main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.base.BaseFragment;

/**
 * Created by Single on 2015/10/23.
 */
public class VaFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_va,container,false);
        }
        return rootView;
    }
}
