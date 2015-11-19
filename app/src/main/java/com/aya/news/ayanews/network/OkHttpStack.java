package com.aya.news.ayanews.network;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Single on 2015/11/19.
 */
public class OkHttpStack extends HurlStack {
    private OkUrlFactory mFactory;

    public OkHttpStack() {
       this(new OkHttpClient());
    }
    public OkHttpStack(OkHttpClient client){
        if (client == null){
            throw new NullPointerException("Client must not be null");
        }
    }
    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return mFactory.open(url);
    }
}
