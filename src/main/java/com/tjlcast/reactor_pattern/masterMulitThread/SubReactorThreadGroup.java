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
            this.nioThreads[i].start() ;
        }

        System.out.println("the size of subReactorThreadGroup is: " + nioThreadCount) ;
    }

    public void dispatch(SocketChannel clientChannel) {
        this.next().resgister(new NioTask(clientChannel, SelectionKey.OP_READ));
    }

    public SubReactorThread next() {
        return this.nioThreads[ requestCount.getAndIncrement() % nioThreadCount ] ;
    }
}
