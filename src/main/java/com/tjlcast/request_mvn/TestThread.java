package com.tjlcast.request_mvn;

/**
 * Created by tangjialiang on 2018/1/22.
 */
public class TestThread implements Runnable {

    private static HttpRequest request ; // 这里的request是代理类，实例类型并不是HttpRequestImpl

    public static HttpRequest getRequest() {
        return request ;
    }

    public static void setRequest(HttpRequest request) {
        TestThread.request = request ;
    }

    private void init() {
        HttpRequestImpl requestImpl = new HttpRequestImpl() ;
        requestImpl.d = Math.random();
        Factory.getInstance().setObject(requestImpl);
    }

    @Override
    public void run() {
        System.out.println("**************") ;
        init();
        request.service();
        System.out.println("**************") ;
    }
}
