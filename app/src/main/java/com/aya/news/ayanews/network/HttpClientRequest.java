package com.aya.news.ayanews.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.aya.news.ayanews.https.ResponseListener;

import java.util.HashMap;

/**
 * Created by Single on 2015/11/19.
 */
public class HttpClientRequest {
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private HttpClientRequest() {
        mRequestQueue = getRequestQueue();
    }

    public static HttpClientRequest getInstance(Context context) {
        mContext = context;
        return ClientHolder.CLIENT_REQUEST;
    }

    private static class ClientHolder {
        private static final HttpClientRequest CLIENT_REQUEST = new HttpClientRequest();
    }

    public <T> void addRequest(Request<T> request) {
        getRequestQueue().add(request);
    }

    public <T> void addRequest(Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public void cancelAllRequest(String tag) {
        if (getRequestQueue() != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new OkHttpStack());
        }
        return mRequestQueue;
    }

    public void getData(String url, HashMap<String, String> params, Response.Listener listener, Response.ErrorListener errorListener, String tag) {

    }

    private String transMapString(HashMap<String, String> maps) {
        StringBuffer result = new StringBuffer();
        for (HashMap.Entry<String, String> entry : maps.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }
}
