package com.tjlcast.reactor_pattern.masterMulitThread;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * Reactor 主从模式的Reactor
 */
public class NioServer {

    private static final int DEFAULT_PORT = 8082 ;

    public static void main(String[] args) {
        new Thread(new Acceptor()).start();
    }

    /**
     * Acceptor 可以理解为启动类
     */
    private static final class Acceptor implements Runnable {

        private static ExecutorService minReactor = Executors.newSingleThreadExecutor() ;

        @Override
        public void run() {
            ServerSocketChannel ssc = null ;

            try {
                ssc = ServerSocketChannel.open() ;
                ssc.configureBlocking(false) ;
                ssc.bind(new InetSocketAddress("127.0.0.1", DEFAULT_PORT)) ;

                dispatch(ssc);
            } catch (Throwable e) {
                e.printStackTrace() ;
            }
        }

        private void dispatch(ServerSocketChannel ssc) {
            minReactor.execute(new MainReactor(ssc));
        }
    }
}
