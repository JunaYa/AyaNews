package com.aya.news.ayanews.ui.fragment_sub;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.adapter.NormalNewsAdapter;
import com.aya.news.ayanews.common.ToastUtils;
import com.aya.news.ayanews.config.Const;
import com.aya.news.ayanews.model.News;
import com.aya.news.ayanews.ui.base.BaseFragment;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
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
    private String news_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(Const.ARG_CHANNEL_URL);
        news_id = getArguments().getString(Const.ARG_CHANNEL_ID);

        mAdapter = new NormalNewsAdapter(newses, getActivity());
        mRequestQueue = Volley.newRequestQueue(getActivity());


    }

    //获取数据
    private void loadData() {
        refreshLayout.setRefreshing(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String body = response.getString(news_id);
                            Gson gson = new Gson();
                            ArrayList<News> newsList = gson.fromJson(body, new TypeToken<ArrayList<News>>() {
                            }.getType());
                            newses.addAll(newsList);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.show(getActivity(), error.getMessage());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
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
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (newses.size() == 0) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                    onRefresh();
                }
            });
        }
    }
}
