package com.aya.news.ayanews.model;

/**
 * Created by Single on 2015/10/23.
 */
public class Channel {
    private String id;
    private String name;
    private int order;
    private String weburl;
    private String hweburl;

    public Channel(String id, String name, int order, String weburl, String hweburl) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.weburl = weburl;
        this.hweburl = hweburl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    public void setHweburl(String hweburl) {
        this.hweburl = hweburl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getWeburl() {
        return weburl;
    }

    public String getHweburl() {
        return hweburl;
    }
}
