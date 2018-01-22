package com.tjlcast.request_mvn;

/**
 * Created by tangjialiang on 2018/1/22.
 */
public class Factory {

    private static Factory factory = new Factory() ;

    private Factory() {

    }

    public static Factory getInstance() {
        return factory ;
    }

    public HttpRequest getObject() {
        return (HttpRequest) ThreadLocalTest.get() ;
    }

    public void setObject(HttpRequest request) {
        ThreadLocalTest.set(request) ;
    }
}
