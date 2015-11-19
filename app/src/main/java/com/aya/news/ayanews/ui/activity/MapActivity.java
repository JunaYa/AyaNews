package com.aya.news.ayanews.ui.activity;


import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.aya.news.ayanews.R;
import com.aya.news.ayanews.common.ToastUtils;
import com.aya.news.ayanews.config.Const;
import com.aya.news.ayanews.ui.base.BaseActivity;

import java.util.ArrayList;

/**
 * Created by aya on 2015/10/31.
 */
public class MapActivity extends BaseActivity implements View.OnClickListener, LocationSource, AMap.OnCameraChangeListener, AMapLocationListener, PoiSearch.OnPoiSearchListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel() - 2));
            aMap.setOnCameraChangeListener(this);
            aMap.setMyLocationEnabled(true);
            aMap.setLocationSource(this);

            aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听
            aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听
            aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        }

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

    private void addMarkers(String str) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.snippet(str);
        aMap.addMarker(markerOptions);
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

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
            //在定位结束后，在合适的生命周期调用destroy()方法
            //其中如果间隔时间为-1，则定位只定一次
            mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 10 * 1000, 10, this);
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        poiSearch(new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude));
    }

    /**
     * 当camera移动，根据屏幕地图中心的经纬度查找该位置
     *
     * @param latLonPoint
     */
    private void poiSearch(LatLonPoint latLonPoint) {
        PoiSearch.Query query = new PoiSearch.Query("", Const.AMAP_CTGR, "");//cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）
        query.setPageSize(20);//设置每页最多返回多少条poiitem
        query.setPageNum(0);//设置查第一页
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 100));//设置周边搜索的中心点以及区域
        poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器
        poiSearch.searchPOIAsyn();//开始搜索
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener.onLocationChanged(aMapLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int position) {
        if (poiResult != null) {
            for (int i = 0; i < poiResult.getPois().size(); i++) {
                addMarkers(poiResult.getPois().get(i).getSnippet());
            }
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        ToastUtils.show(this, marker.getTitle());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View windowView = LayoutInflater.from(this).inflate(R.layout.window_layout, null);
        TextView textView = (TextView) windowView.findViewById(R.id.text_View);
        String snippet = marker.getSnippet();
        if (snippet != null) {
            textView.setText(snippet);
        }
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
