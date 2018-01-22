package com.tjlcast.request_mvn;

import java.lang.reflect.Proxy;

/**
 * Created by tangjialiang on 2018/1/22.
 */
public class ProxyUtils {

    public static HttpRequest getRequest() {
        HttpRequest request = new HttpRequestImpl() ;
        Delegate delegate = new Delegate();
        delegate.setFactory(Factory.getInstance());
        HttpRequest proxy = (HttpRequest)Proxy.newProxyInstance(request.getClass().getClassLoader(), request.getClass().getInterfaces(), delegate);
        return proxy ;
    }
}
