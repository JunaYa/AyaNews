package com.aya.news.ayanews.config;

/**
 * Created by Single on 2015/10/22.
 */
public class Urls {
    public static final int pageSize = 10;
    public static final String VIDEO_URL = "http://c.m.163.com/nc/video/list/V9LG4B3A0/n/10-10.html";
    public static String URL_DONG_HUA_ZONG_HE = "http://www.bilibili.com/index/ding/1.json";

    public static String getUrl(String url, int pageNo) {
        return url.substring(0, url.lastIndexOf("/") + 1) + (pageNo - 1) * pageSize + "-" + pageSize + ".html";
    }
}
