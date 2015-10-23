package com.aya.news.ayanews.ui.fragment_sub;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.base.BaseFragment;

/**
 * Created by Single on 2015/10/23.
 */
public class NormalNewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.commom_list, container, false);
            refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
            refreshLayout.setOnRefreshListener(this);
        }
        return rootView;
    }

    @Override
    public void onRefresh() {

    }
}
