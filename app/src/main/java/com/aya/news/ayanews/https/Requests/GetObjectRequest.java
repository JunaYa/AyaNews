package com.aya.news.ayanews.https.Requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.aya.news.ayanews.https.ResponseListener;
import com.aya.news.ayanews.https.ResponseObject;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by qoo on 15/9/17.
 */
public class GetObjectRequest<T> extends Request<T> {
    /*正确数据的时候回掉用*/
    private ResponseListener mListener;
    /*用来解析 json 用的*/
    private Gson mGson;
    /*在用 gson 解析 json 数据的时候，需要用到这个参数*/
    private Type mClazz;

    public GetObjectRequest(String url, Type type, ResponseListener listener) {
        super(Method.GET, url, listener);
        this.mListener = listener;
        mGson = new Gson();
        mClazz = type;
    }

    public GetObjectRequest(String url,ResponseListener listener){
        super(Method.GET,url,listener);
        this.mListener = listener;
        mGson = new Gson();
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            T result;
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            ResponseObject responseObject = ResponseObject.parse(jsonString);
            if (responseObject.code == ResponseObject.CODE_SUCCESS) {
                result = mGson.fromJson(responseObject.data, mClazz);
                return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.error(new VolleyError("服务器异常,请稍后再试"));
            }
        } catch (Exception e) {
            return Response.error(new VolleyError("服务器异常,请稍后再试"));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return new VolleyError("网络异常,请稍后再试");
    }
}
