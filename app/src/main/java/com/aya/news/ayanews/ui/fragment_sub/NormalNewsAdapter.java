package com.aya.news.ayanews.ui.fragment_sub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.model.News;

import java.util.ArrayList;

/**
 * Created by aya on 2015/10/24.
 */
public class NormalNewsAdapter extends BaseAdapter {
    private ArrayList<News> newses;
    private Context context;
    private LayoutInflater inflater;
    public NormalNewsAdapter(ArrayList<News> newses, Context context) {
        this.newses = newses;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return newses.size();
    }

    @Override
    public News getItem(int position) {
        return newses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_news,null);
            holder.img = (ImageView)convertView.findViewById(R.id.iv_left);
            holder.title = (TextView)convertView.findViewById(R.id.tv_title);
            holder.des = (TextView)convertView.findViewById(R.id.tv_des);
        }
        return convertView;
    }

    private class ViewHolder{
        ImageView img ;
        TextView title;
        TextView des;
    }
}
