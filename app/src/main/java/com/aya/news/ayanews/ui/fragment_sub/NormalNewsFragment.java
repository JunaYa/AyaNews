package com.aya.news.ayanews.ui.fragment_sub;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.config.Const;
import com.aya.news.ayanews.model.News;
import com.aya.news.ayanews.ui.base.BaseFragment;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Single on 2015/10/23.
 */
public class NormalNewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private NormalNewsAdapter mAdapter;
    private ArrayList<News> newses = new ArrayList<>();

    private RequestQueue mRequestQueue;

    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = new Volley().newRequestQueue(getActivity());
        mAdapter = new NormalNewsAdapter(newses, getActivity());
        url = getArguments().getString(Const.ARG_CHANNEL_URL);
        Log.d("aya",url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.commom_list, container, false);
            refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(this);

            listView = (ListView) findViewById(R.id.list_view);
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
        return rootView;
    }

    @Override
    public void onRefresh() {

    }
}
