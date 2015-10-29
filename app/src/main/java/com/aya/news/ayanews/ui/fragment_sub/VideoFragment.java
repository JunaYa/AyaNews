package com.aya.news.ayanews.ui.fragment_sub;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.aya.news.ayanews.config.Urls;
import com.aya.news.ayanews.https.ResponseListener;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new VideoAdapter(getActivity());
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    private void loadDate() {
        refreshLayout.setRefreshing(false);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Urls.VIDEO_URL, null, new Response.Listener<JSONObject>() {
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        }
        return rootView;
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
        loadDate();
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
            holder.playCount.setText(video.getPlayCount());
            holder.replyCount.setText(video.getReplyCount());
            holder.videoLength.setText(video.getLength());
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView video;
        ImageView play_icon;
        TextView title;
        TextView playCount;
        TextView replyCount;
        TextView videoLength;
    }
}
