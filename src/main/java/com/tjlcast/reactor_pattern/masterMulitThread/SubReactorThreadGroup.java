package com.tjlcast.reactor_pattern.masterMulitThread;

import com.tjlcast.reactor_pattern.masterMulitThread.task.NioTask;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * nio 线程组， 简易的nio线程组
 *
 *  * Sub Reactor,目前没有使用jdk的并发池，这里用的SubReactorThreadGroup,其实现是数组，当然这里也可以使用jdk线程池，
 * SubReactor的每一个线程都是IO线程，用来处理读，写事件。所有的IO线程公用一个业务线程池（基于juc）实现，用来处理业务逻辑，
 * 也就是运行Handel的地方。
 */
public class SubReactorThreadGroup {

    private static final int DEFAULT_NIO_THREAD_COUNT = 4 ;

    private static final AtomicInteger requestCount = new AtomicInteger() ; // 请求的计数器 （无上限）

    private final int nioThreadCount ;
    private SubReactorThread[] nioThreads ;
    private ExecutorService businessExecutorPool ;

    public SubReactorThreadGroup() {
        this(DEFAULT_NIO_THREAD_COUNT) ;
    }

    public SubReactorThreadGroup(int nioThreadCount) {
        if (nioThreadCount < 1) {
            this.nioThreadCount = DEFAULT_NIO_THREAD_COUNT;
        } else {
            this.nioThreadCount = nioThreadCount ;
        }

        this.businessExecutorPool = Executors.newFixedThreadPool(nioThreadCount) ;

        nioThreads = new SubReactorThread[this.nioThreadCount] ;
        for(int i=0; i<this.nioThreadCount; i++) {
            this.nioThreads[i] = new SubReactorThread(businessExecutorPool) ;
            this.nioThreads[i].start() ; // 构造方法中启动线程，由于nioThreads不会对外暴露，故不会引起线程逃逸
        }

        System.out.println("the size of subReactorThreadGroup is: " + nioThreadCount) ;
    }

    /**
     * 把事件Channel分配到某个线程。
     * @param clientChannel
     */
    public void dispatch(SocketChannel clientChannel) {
        this.next().resgister(new NioTask(clientChannel, SelectionKey.OP_READ));
    }

    public SubReactorThread next() {
        return this.nioThreads[ requestCount.getAndIncrement() % nioThreadCount ] ;
    }
}
