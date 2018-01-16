package com.tjlcast.reactor_pattern.masterMulitThread;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * 主要作为连接请求处理的反应堆
 *
 * Main Reactor,监听客户端连接的反应堆，这里使用jdk并发中的Executors.newSingleThreadExecutor线程池来实现，监听客户端的连接事件(OP_ACCEPT)
 */
public class MainReactor implements Runnable {

    private Selector selector ;
    private SubReactorThreadGroup subReactorThreadGroup ;

    public MainReactor(ServerSocketChannel serverSocketChannel) {
        try {
            selector = Selector.open() ;
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT) ;
        } catch (Throwable e) {
            e.printStackTrace() ;
        }

        subReactorThreadGroup = new SubReactorThreadGroup(4) ;
    }

    @Override
    public void run() {
        System.out.println("the main reactor is running") ;

        while(!Thread.interrupted()) {
            Set<SelectionKey> ops = null ;
            try {
                selector.select() ;
                ops = selector.selectedKeys() ;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 处理相关事件
            for(Iterator<SelectionKey> it = ops.iterator(); it.hasNext(); ) {
                SelectionKey selectionKey = it.next();
                it.remove();

                try {
                    if (selectionKey.isAcceptable()) {
                        System.out.println("main reactor received 'accept'") ;
                        ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();

                        SocketChannel clientChannel = channel.accept();
                        clientChannel.configureBlocking(false) ;
                        subReactorThreadGroup.dispatch(clientChannel);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.out.println("client side is connected!") ;
                }
            }
        }
    }
}
