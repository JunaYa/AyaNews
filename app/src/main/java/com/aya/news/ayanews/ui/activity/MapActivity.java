package com.aya.news.ayanews.ui.activity;


import android.os.Bundle;

import android.view.View;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.base.BaseActivity;

/**
 * Created by aya on 2015/10/31.
 */
public class MapActivity extends BaseActivity implements View.OnClickListener {
    private MapView mapView;
    private AMap aMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        findViewById(R.id.normal_map).setOnClickListener(this);
        findViewById(R.id.satellite_map).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void setLayer(String layerName) {
        if (layerName.equals(getString(R.string.normal))) {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
        } else if (layerName.equals(getString(R.string.satellite))) {
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.normal_map:
                setLayer("Normal");
                break;
            case R.id.satellite_map:
                setLayer("Satellite");
                break;
        }
    }

}
