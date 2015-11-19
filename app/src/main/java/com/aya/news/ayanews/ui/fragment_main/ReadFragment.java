package com.aya.news.ayanews.ui.fragment_main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.common.ToastUtils;
import com.aya.news.ayanews.config.Urls;
import com.aya.news.ayanews.model.Film;
import com.aya.news.ayanews.okhttp.OkHttpRequest;
import com.aya.news.ayanews.okhttp.ResultCallback;
import com.bumptech.glide.Glide;
import com.squareup.okhttp.Request;

import java.util.ArrayList;


/**
 * Created by Single on 2015/10/23.
 */
public class ReadFragment extends Fragment {
    private View rootView;
    private ArrayList<Film> filmList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FilmAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getVideoList();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_read, container, false);
            adapter = new FilmAdapter();
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    public abstract class MyResultCallback<T> extends ResultCallback<T> {
        @Override
        public void onError(Request request, Exception e) {
            ToastUtils.show(getActivity(), "loading...");
        }

        @Override
        public void onResponse(T response) {
            ToastUtils.show(getActivity(), "okhttp");
        }
    }

    public void getVideoList() {
        String url = Urls.URL_DONG_HUA_ZONG_HE;
        new OkHttpRequest.Builder()
                .url(url)
                .get(new MyResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        super.onError(request, e);
                    }

                    @Override
                    public void onResponse(String str) {
                        if (Film.getFilms(str) != null) {
                            filmList.addAll(Film.getFilms(str));
                        }
                        ToastUtils.show(getActivity(), filmList.get(0).getPic());
                    }
                });
    }

    private class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmHolder> {
        @Override
        public FilmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FilmHolder holder = new FilmHolder(LayoutInflater.from(getActivity()).inflate(R.layout.item_video, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(FilmHolder holder, int position) {
            Film film = filmList.get(position);
            Glide.with(getActivity()).load(film.getPic()).into(holder.film_img);
        }

        @Override
        public int getItemCount() {
            return filmList.size();
        }

        class FilmHolder extends RecyclerView.ViewHolder {
            ImageView film_img;

            public FilmHolder(View view) {
                super(view);
                film_img = (ImageView) view.findViewById(R.id.video_view);
            }
        }
    }
}
