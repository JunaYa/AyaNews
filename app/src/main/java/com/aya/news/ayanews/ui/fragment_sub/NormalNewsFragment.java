package com.aya.news.ayanews.ui.fragment_sub;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.config.Urls;
import com.aya.news.ayanews.ui.adapter.NormalNewsAdapter;
import com.aya.news.ayanews.common.ToastUtils;
import com.aya.news.ayanews.config.Const;
import com.aya.news.ayanews.model.News;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Single on 2015/10/23.
 */
public class NormalNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View rootView;
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private NormalNewsAdapter mAdapter;
    private ArrayList<News> newses = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private String url;
    private String news_id;
    private int pageNo = 1;
    private boolean isGetMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(Const.ARG_CHANNEL_URL);
        news_id = getArguments().getString(Const.ARG_CHANNEL_ID);

        mAdapter = new NormalNewsAdapter(newses, getActivity());
        mRequestQueue = Volley.newRequestQueue(getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.commom_list, container, false);
            refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(this);

            listView = (ListView) rootView.findViewById(R.id.list_view);
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0) {
                        onGetMore();
                    }
                }
            });

            Log.d("aya", "getScrollY() = " + listView.getScrollY() + "getScrollX() = " + listView.getScrollX());
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
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

    //获取数据
    private void loadData() {
        refreshLayout.setRefreshing(false);
        if (pageNo == 1) newses.clear();
        String urlStr = Urls.getUrl(url, pageNo);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String body = response.getString(news_id);
                            Gson gson = new Gson();
                            ArrayList<News> newsList = gson.fromJson(body, new TypeToken<ArrayList<News>>() {
                            }.getType());
                            newses.clear();
                            newses.addAll(newsList);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isGetMore) {
                            isGetMore = false;
                            refreshLayout.setEnabled(true);
                        } else {
                            refreshLayout.setRefreshing(false);
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

    private void onGetMore() {
        if (refreshLayout.isRefreshing() || isGetMore) return;
        isGetMore = true;
        refreshLayout.setEnabled(false);
        pageNo = pageNo + 1;
        loadData();
    }
}
