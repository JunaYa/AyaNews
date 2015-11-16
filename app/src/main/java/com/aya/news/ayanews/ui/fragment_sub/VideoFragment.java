package com.aya.news.ayanews.ui.fragment_sub;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.common.ToastUtils;
import com.aya.news.ayanews.config.Urls;
import com.aya.news.ayanews.model.Video;
import com.aya.news.ayanews.ui.base.BaseFragment;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Single on 2015/10/29.
 */
public class VideoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private RequestQueue mRequestQueue;

    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private VideoAdapter mAdapter;
    private ArrayList<Video> videoList = new ArrayList<>();
    private int pageNo = 1;
    private boolean isGetMore = false;
    private View listFooterView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new VideoAdapter(getActivity());
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

            refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(this);
            listView = (ListView) findViewById(R.id.list_view);
            listFooterView = inflater.inflate(R.layout.list_footer_load_more, listView, false);
            listView.addFooterView(listFooterView);
            listView.setAdapter(mAdapter);
            listView.removeFooterView(listFooterView);
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
            mAdapter.PlayOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.show(getActivity(),"play");
                }
            });
        }
        return rootView;
    }

    private void loadDate(String url) {
        refreshLayout.setRefreshing(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String body = response.getString("V9LG4B3A0");
                    Gson gson = new Gson();
                    ArrayList<Video> videos = gson.fromJson(body, new TypeToken<ArrayList<Video>>() {
                    }.getType());
                    videoList.addAll(videos);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isGetMore) {
                    isGetMore = false;
                    refreshLayout.setEnabled(true);
                    listView.removeFooterView(listFooterView);
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    private void onGetMore() {
        if (refreshLayout.isRefreshing() || isGetMore) return;
        isGetMore = true;
        refreshLayout.setEnabled(false);
        listView.addFooterView(listFooterView);
        pageNo += 1;
        String url = Urls.getUrl(Urls.VIDEO_URL, pageNo);
        loadDate(url);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        loadDate(Urls.getUrl(Urls.VIDEO_URL, pageNo));
    }

    private class VideoAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public VideoAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        private View.OnClickListener iconOnClickListener;

        public void PlayOnClickListener(View.OnClickListener onClickListener) {
            iconOnClickListener = onClickListener;
        }

        @Override
        public int getCount() {
            return videoList.size();
        }

        @Override
        public Video getItem(int position) {
            return videoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_video, null);
                holder.video = (ImageView) convertView.findViewById(R.id.video);
                holder.play_icon = (ImageView) convertView.findViewById(R.id.play_icon);
                holder.title = (TextView) convertView.findViewById(R.id.video_title);
                holder.describe = (TextView) convertView.findViewById(R.id.video_des);
                holder.playCount = (TextView) convertView.findViewById(R.id.video_play_count);
                holder.replyCount = (TextView) convertView.findViewById(R.id.video_reply_count);
                holder.videoLength = (TextView) convertView.findViewById(R.id.video_length);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Video video = videoList.get(position);
            Glide.with(getActivity()).load(video.getCover()).placeholder(R.mipmap.biz_pread_adapter_bg_default).into(holder.video);
            holder.play_icon.setOnClickListener(iconOnClickListener);
            holder.title.setText(video.getTitle());
            holder.describe.setText(video.getDescription());
            holder.playCount.setText(video.getPlayCount());
            holder.replyCount.setText(video.getReplyCount() +"跟帖");
            holder.videoLength.setText(video.getLength());
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView video;
        ImageView play_icon;
        TextView title;
        TextView describe;
        TextView playCount;
        TextView replyCount;
        TextView videoLength;
    }
}
