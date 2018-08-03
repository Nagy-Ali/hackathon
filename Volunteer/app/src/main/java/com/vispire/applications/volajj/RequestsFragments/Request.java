package com.vispire.applications.volajj.RequestsFragments;

import java.lang.ref.SoftReference;

/**
 * Created by Abo3li on 8/1/2018.
 */

public class Request {
    public String title;
    public String subTitle;
    public String uri;
    public String longitude;
    public String latitude;
    public String user;

    public Request(String title, String subTitle, String uri, String longitude,
                   String latitude, String user ) {
        this.title = title;
        this.subTitle = subTitle;
        this.uri = uri;
        this.longitude = longitude;
        this.latitude = latitude;
        this.user = user;
    }
}