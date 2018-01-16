package com.tjlcast.reactor_pattern.mulitThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 */
public class NioServer {

    public static void main(String[] args) {
        new Thread(new Acceptor()).start();
    }

    /**
     * 连接线程模型， 反应对， 转发器 Acceptor
     */
    private static final class Acceptor implements Runnable {

        private NioReactorThreadGroup nioReactorThreadGroup ;

        public Acceptor() {
            nioReactorThreadGroup = new NioReactorThreadGroup() ;
        }

        @Override
        public void run() {
            System.out.println("服务端启动成功，等待客户端接入") ;
            ServerSocketChannel serverSocketChannel = null ;
            Selector selector = null ;

            try {
                serverSocketChannel = ServerSocketChannel.open() ;
                serverSocketChannel.configureBlocking(false) ;
                serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8082)) ;

                selector = Selector.open() ;
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT) ;

                Set<SelectionKey> ops = null ;
                while(true) {
                    // 阻塞获取事件
                    try {
                        selector.select() ;
                        ops = selector.selectedKeys() ;
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    // 处理相关事件
                    for(Iterator<SelectionKey> it = ops.iterator(); it.hasNext(); ) {
                        SelectionKey selectionKey = it.next();
                        it.remove();

                        try {
                            if (selectionKey.isAcceptable()) {
                                System.out.println("收到客户端的连接请求...") ;
                                ServerSocketChannel serverSc = (ServerSocketChannel) selectionKey.channel();

                                SocketChannel clientChannel = serverSc.accept();
                                clientChannel.configureBlocking(false) ;
                                nioReactorThreadGroup.dispatch(clientChannel);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
