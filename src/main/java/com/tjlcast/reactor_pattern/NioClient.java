package com.tjlcast.reactor_pattern;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * Nio客户端
 * 用于向服务端发送数据
 */
public class NioClient {

    public static void main(String[] args) {
        SocketChannel clientChannel = null ;
        Selector selector = null ;

        try {
            clientChannel = SocketChannel.open() ;
            clientChannel.configureBlocking(false) ;

            selector = Selector.open() ;

            clientChannel.register(selector, SelectionKey.OP_ACCEPT) ;
            clientChannel.connect(new InetSocketAddress("127.0.0.1", 8082)) ;

            Set<SelectionKey> ops = null ;

            while(true) {
                try {
                    selector.select() ;
                    ops = selector.selectedKeys();

                    for(Iterator<SelectionKey> it = ops.iterator() ; it.hasNext(); ) {
                        SelectionKey selectionKey = it.next();
                        it.remove();

                        if (selectionKey.isAcceptable()) {
                            System.out.println("client connect") ;

                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            // 判断此通道上是否正在进行连接操作
                            // 完成套接字的连接过程
                            if (sc.isConnectionPending()) {
                                sc.finishConnect() ;
                                System.out.println("finish connections!") ;
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                buffer.put("Hello, Server".getBytes()) ;
                                buffer.flip() ;
                                sc.write(buffer) ;
                            }
                            sc.register(selector, SelectionKey.OP_READ) ;
                        } else if(selectionKey.isWritable()) {
                            System.out.println("client side is sending") ;

                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.put("hello server.".getBytes()) ;
                            buffer.flip() ;
                            sc.write(buffer) ;
                        } else if(selectionKey.isReadable()) {
                            System.out.println("客户端收到服务器的响应....");

                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int count = sc.read(buffer);

                            if (count > 0) {
                                buffer.flip() ;
                                byte[] response = new byte[buffer.remaining()];
                                buffer.get(response) ;
                                System.out.println(new String(response)) ;
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
