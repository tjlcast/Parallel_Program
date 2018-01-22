package com.tjlcast.request_mvn;

/**
 * Created by tangjialiang on 2018/1/22.
 */
public class HttpRequestImpl implements HttpRequest {

    public double d ;

    @Override
    public void service() {
        System.out.println("do some servier, random value is " + d) ;
    }
}
