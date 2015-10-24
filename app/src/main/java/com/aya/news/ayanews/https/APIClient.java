package com.aya.news.ayanews.https;

import com.aya.news.ayanews.model.News;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by qoo on 15/9/16.
 */
public class APIClient {

    private static HashMap getBaseParams() {
        HashMap<String, String> params = new HashMap<>();
//        params.put("imei", DeviceInfo.getInstance().getImei());
//        params.put("mac", DeviceInfo.getInstance().getMacAddress());
//        params.put("osver", DeviceInfo.getInstance().getOsVersion());
//        params.put("src", Const.APISrc);
        return params;
    }

    public static void getConfigs(String cityId, ResponseListener listener) {
        HashMap<String, String> params = getBaseParams();
        params.put("city_id", cityId);
        VolleyUtil.getObject(Const.GET_CONFIGS_URL, params, new TypeToken<News>() {}.getType(), listener);
    }

    public static void getJokeList(String classId, int page, ResponseListener listener){
        HashMap<String, String> params = getBaseParams();
        params.put("class_id", String.valueOf(classId));
        params.put("page", String.valueOf(page));
//        VolleyUtil.getObject(Const.GET_JOKES_URL,params, new TypeToken<ArrayList<Joke>>(){}.getType(), listener);
    }
}
