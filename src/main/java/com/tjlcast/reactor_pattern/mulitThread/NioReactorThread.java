package com.tjlcast.reactor_pattern.mulitThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * Nio线程，专门负责nio read,write
 * 本类是实例代码，不对nio，断线重连，写半包等场景进行处理，主要对Reactor理解（多线程版本）.
 *
 * 每一个NioReactorThread内部有：
 *  等待处理的连接队列 和 一个处理线程
 *
 *  一个selector负责一组SocketChannel的读写
 */
public class NioReactorThread extends Thread {

    private static final byte[] b = "hello, 服务器收到了你的信息。".getBytes() ; // 服务器端发送给客户端的消息

    private Selector selector ;
    private List<SocketChannel> waitRegisterList = new ArrayList<SocketChannel>(512) ;
    private ReentrantLock registerLock = new ReentrantLock() ;

    public NioReactorThread() {
        try {
            this.selector = Selector.open() ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(SocketChannel socketChannel) {
        if (socketChannel != null) {
            try {
                registerLock.lock() ;
                waitRegisterList.add(socketChannel) ;
            } finally {
                registerLock.unlock() ;
            }
        }
    }

    @Override
    public void run() {
        while(true) {
            Set<SelectionKey> ops = null ;
            try {
                selector.select(1000) ;
                Set<SelectionKey> selectionKeys = selector.selectedKeys() ;
            } catch (IOException e) {
                e.printStackTrace() ;
                continue ;
            }

            // 处理相关事件
            for(Iterator<SelectionKey> it = ops.iterator(); it.hasNext(); ) {
                SelectionKey selectionKey = it.next();
                it.remove();

                try {
                    if(selectionKey.isWritable()) {     // 向客户端发送请求
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buf = (ByteBuffer) selectionKey.attachment();
                        buf.flip() ;
                        clientChannel.write(buf) ;
                        System.out.println("服务端向客户端发送数据...") ;
                        // 重新注册读事件
                        clientChannel.register(selector, SelectionKey.OP_READ) ;
                    } else if (selectionKey.isReadable()) { // 接受客户端的请求
                        System.out.println("服务端接受客户端连接请求...") ;
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        System.out.println(buf.capacity());
                        clientChannel.read(buf);//
                        buf.put(b);
                        clientChannel.register(selector, SelectionKey.OP_WRITE, buf);//注册写事件
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.out.println("客户端主动断开连接。。。。。。。");
                }
            }

            // 注册事件
            if(!waitRegisterList.isEmpty()) {
                try {
                    registerLock.lock() ;

                    for(Iterator<SocketChannel> it = waitRegisterList.iterator(); it.hasNext(); ) {
                        SocketChannel socketChannel = it.next();
                        try {
                            socketChannel.register(selector, SelectionKey.OP_READ) ;
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        it.remove();
                    }
                } finally {
                    registerLock.unlock();
                }
            }
        }
    }
}
