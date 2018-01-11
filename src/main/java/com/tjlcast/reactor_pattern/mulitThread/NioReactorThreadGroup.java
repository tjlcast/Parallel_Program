package com.tjlcast.reactor_pattern.mulitThread;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * nio 线程组， 简易的NIO线程组
 */
public class NioReactorThreadGroup {

    private static final AtomicInteger requestCounter = new AtomicInteger() ; // 请求计数器

    private final int nioThreadCount ; // 线程池IO线程的数量
    private static final int DEFAULT_NIO_THREAD_COUNT ;
    private NioReactorThread[] nioThread ;

    static {
        DEFAULT_NIO_THREAD_COUNT = 4 ;
    }

    public NioReactorThreadGroup() {
        this(DEFAULT_NIO_THREAD_COUNT) ;
    }

    public NioReactorThreadGroup(int threadCount) {
        if (threadCount < 1) {
            threadCount = DEFAULT_NIO_THREAD_COUNT ;
        }
        this.nioThreadCount = threadCount ;
        this.nioThread = new NioReactorThread[threadCount] ;

        for(int i=0; i<threadCount; i++) {
            this.nioThread[i] = new NioReactorThread() ;
            this.nioThread[i].start(); // 构造方法中启动线程，由于nioThread不会对外暴露，故不会引起线程逃逸。
        }

        System.out.println("Nio 线程数量: " + this.nioThreadCount) ;
    }

    public void dispatch(SocketChannel socketChannel) {
        if (socketChannel != null) {
            next().register(socketChannel);
        }
    }

    protected NioReactorThread next() {
        return this.nioThread[ requestCounter.getAndIncrement() % nioThreadCount ] ;
    }
}
