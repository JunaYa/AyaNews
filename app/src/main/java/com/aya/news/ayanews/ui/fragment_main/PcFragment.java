package com.aya.news.ayanews.ui.fragment_main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.activity.MapActivity;
import com.aya.news.ayanews.ui.activity.location.LocationGPSActivity;
import com.aya.news.ayanews.ui.activity.location.LocationNetworkActivity;
import com.aya.news.ayanews.ui.activity.location.LocationSensorSourceActivity;
import com.aya.news.ayanews.ui.activity.location.LocationSourceActivity;
import com.aya.news.ayanews.ui.base.BaseFragment;


/**
 * Created by Single on 2015/10/23.
 */
public class PcFragment extends Fragment implements View.OnClickListener {

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_pc, container, false);

            /*显示基本地图*/
            rootView.findViewById(R.id.my_map).setOnClickListener(this);
            rootView.findViewById(R.id.map_gps).setOnClickListener(this);
            rootView.findViewById(R.id.map_network).setOnClickListener(this);
            rootView.findViewById(R.id.map_sensor_source).setOnClickListener(this);
            rootView.findViewById(R.id.map_source).setOnClickListener(this);

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
            case R.id.my_map:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
            case R.id.map_gps:
                startActivity(new Intent(getActivity(), LocationGPSActivity.class));
                break;
            case R.id.map_network:
                startActivity(new Intent(getActivity(), LocationNetworkActivity.class));
                break;
            case R.id.map_sensor_source:
                startActivity(new Intent(getActivity(), LocationSensorSourceActivity.class));
                break;
            case R.id.map_source:
                startActivity(new Intent(getActivity(), LocationSourceActivity.class));
                break;

        }
    }


}
