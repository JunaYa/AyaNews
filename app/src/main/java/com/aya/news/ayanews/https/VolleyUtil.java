package com.aya.news.ayanews.https;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.aya.news.ayanews.https.Requests.GetObjectRequest;
import com.aya.news.ayanews.https.Requests.PostObjectRequest;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by qoo on 15/9/17.
 */
public class VolleyUtil {
    private static RequestQueue mRequestQueue;

    public static void initialize(Context context) {
        if (mRequestQueue == null) {
            synchronized (VolleyUtil.class) {
                if (mRequestQueue == null) {
                    mRequestQueue = Volley.newRequestQueue(context);
                }
            }
        }
        mRequestQueue.start();
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            throw new RuntimeException("请先初始化mRequestQueue");
        return mRequestQueue;
    }

    public static void getObject(String baseUrl, Map<String, String> params, Type type, ResponseListener listener) {
        String url = baseUrl + (baseUrl.contains("?") ? "&" : "?") + VolleyUtil.transMapToString(params);
        GetObjectRequest request = new GetObjectRequest(url, type, listener);
        VolleyUtil.getRequestQueue().add(request);
    }

    public static void getObject(String baseUrl,ResponseListener listener){
        GetObjectRequest request = new GetObjectRequest(baseUrl ,listener);
        VolleyUtil.getRequestQueue().add(request);
    }

    public static void postObject(String url, Map<String, String> params, Type type, ResponseListener listener) {
        PostObjectRequest request = new PostObjectRequest(url, params, type, listener);
        VolleyUtil.getRequestQueue().add(request);
    }

    public static void postObject(String baseUrl, Map<String, String> getParams, Map<String, String> params, Type type, ResponseListener listener) {
        String url = baseUrl + (baseUrl.contains("?") ? "&" : "?") + VolleyUtil.transMapToString(params);
        VolleyUtil.postObject(url, params, type, listener);
    }

    /**
     * 方法名称:transMapToString
     * 传入参数:map
     * 返回值:String 形如 username=chenziwen&password=1234
     */
    private static String transMapToString(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }
}
