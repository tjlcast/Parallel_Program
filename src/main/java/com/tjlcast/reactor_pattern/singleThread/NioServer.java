package com.tjlcast.reactor_pattern.singleThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * 使用单一线程运行
 *
 * Nio服务器
 * 该例子主要用于增加对Reactor线程模型的理解，不考虑半包等网络问题.
 *
 * 例子程序的功能：服务器接受客户端的请求数据，然后在后面追加（hello， 服务器收到了你的信息）
 */

public class NioServer {

    public static void main(String[] args) {
        (new Thread(new Reactor())).start();
    }

    private static final class Reactor implements Runnable {
        byte[] b = "hello, 服务器收到了你的信息. ".getBytes() ;

        @Override
        public void run() {
            System.out.println("the server side is running... waiting for ") ;

            ServerSocketChannel ssc = null ;
            Selector selector = null ;

            try {
                ssc = ServerSocketChannel.open() ;
                ssc.configureBlocking(false) ;
                ssc.bind(new InetSocketAddress("127.0.0.1", 8082)) ;

                selector = Selector.open() ;
                ssc.register(selector, SelectionKey.OP_ACCEPT) ;

                Set<SelectionKey> ops = null ;
                while (true) {
                    try {
                        selector.select() ; // 如果没有感兴趣的事件到达，阻塞等待
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        break ;
                    }

                    // 处理相关事件
                    for(Iterator<SelectionKey> it = ops.iterator(); it.hasNext(); ) {
                        SelectionKey selectionKey = it.next();
                        it.remove();

                        try {
                            if (selectionKey.isAcceptable()) { // 客户端建立连接
                                SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                                clientChannel.configureBlocking(false) ;

                                // 向选择器注册事件，客户端向服务端发送数据准备好后，再处理。
                                clientChannel.register(selector, SelectionKey.OP_READ) ;
                                System.out.println("received the request of building connection") ;
                            } else if (selectionKey.isWritable()) { // 向客户端发送请求
                                SocketChannel clientChannel = (SocketChannel) selectionKey.channel() ;
                                ByteBuffer buf = (ByteBuffer) selectionKey.attachment();
                                buf.flip() ;
                                clientChannel.write(buf) ;

                                // 重新注册读事件
                                clientChannel.register(selector, SelectionKey.OP_READ) ;
                            } else if (selectionKey.isReadable()) { // 处理端发送的请求
                                System.out.println("server side receive msg from client...") ;

                                SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                System.out.println(buffer.capacity()) ;
                                clientChannel.read(buffer) ;
                                clientChannel.register(selector, SelectionKey.OP_WRITE, buffer) ;
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            System.out.println("client side disconnected...") ;
                            ssc.register(selector, SelectionKey.OP_ACCEPT) ;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace() ;
            }
        }
    }
}
