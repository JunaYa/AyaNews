package com.aya.news.ayanews.https;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by qoo on 15/9/17.
 */
public class ResponseObject {
    public static final int CODE_SUCCESS = 1;

    public int code;
    public String msg;
    public String data;

    public static ResponseObject parse(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        ResponseObject object = new ResponseObject();
        object.code = jsonObject.getInt("code");
        object.msg = jsonObject.getString("msg");
        object.data = jsonObject.getString("data");
        return object;
    }
}
