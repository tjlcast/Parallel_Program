package com.tjlcast.request_mvn;

/**
 * Created by tangjialiang on 2018/1/22.
 */
public class ThreadLocalTest {

    public static ThreadLocal<HttpRequest> local = new ThreadLocal<HttpRequest>() ;

    public static HttpRequest get() {
        return local.get() ;
    }

    public static void set(HttpRequest httpRequest) {
        if (get() != null) {
            return ;
        }

        System.out.println("ThreadLocal is null") ;
        local.set(httpRequest);
    }
}
