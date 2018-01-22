package com.tjlcast.request_mvn;

/**
 * Created by tangjialiang on 2018/1/22.
 */
public class main {

    public static void main(String[] args) {
        HttpRequest request = ProxyUtils.getRequest();

        TestThread thread1 = new TestThread() ;
        thread1.setRequest(request);

        TestThread thread2 = new TestThread() ;
        thread2.setRequest(request);

        new Thread(thread1).start();
        new Thread(thread2).start();
    }
}
