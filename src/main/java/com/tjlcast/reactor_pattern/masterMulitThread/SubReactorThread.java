package com.tjlcast.reactor_pattern.masterMulitThread;

import com.tjlcast.reactor_pattern.masterMulitThread.handler.Handler;
import com.tjlcast.reactor_pattern.masterMulitThread.task.NioTask;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * Nio 线程， 专门负责IO 的 read， write
 *
 */
public class SubReactorThread extends Thread {

    private ExecutorService businessExecutorPool ;

    private Selector selector ;
    private List<NioTask> taskList = new ArrayList<NioTask>(512) ;
    private ReentrantLock taskMainLock = new ReentrantLock() ;

    /**
     * 业务线程池
     * @param businessExecutorPool
     */
    public SubReactorThread(ExecutorService businessExecutorPool) {
        try {
            this.businessExecutorPool = businessExecutorPool ;
            this.selector = Selector.open() ;
        } catch (Throwable e) {
            e.printStackTrace() ;
        }
    }

    /**
     * socket channel.
     *
     * @param task
     */
    public void resgister(NioTask task) {
        if (task != null) {
            try {
                taskMainLock.lock();
                taskList.add(task);
            } finally {
                taskMainLock.unlock();
            }
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Set<SelectionKey> ops = null ;
            try {
                selector.select(1000) ;
                ops = selector.selectedKeys() ;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            // 处理相关事件
            for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                SelectionKey selectionKey = it.next();
                it.remove();

                try {
                    if (selectionKey.isWritable()) {        // 向客户端发送请求
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buf = (ByteBuffer) selectionKey.attachment();
                        buf.flip() ;
                        clientChannel.write(buf) ;
                        System.out.println("服务端向客户端发送数据。。。") ;
                        // 重新注册读事件
                        clientChannel.register(selector, SelectionKey.OP_READ) ;
                    } else if (selectionKey.isReadable()) { // 接受客户端请求
                        System.out.println("服务端接收客户端连接请求...") ;
                        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        System.out.println(buf.capacity()) ;
                        clientChannel.read(buf);
                        dispatch(clientChannel, buf);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.out.println("客户端主动断开连接...") ;
                }
            }

            // 注册事件
            if (!taskList.isEmpty()) {
                try {
                    taskMainLock.lock();
                    for(Iterator<NioTask> it = taskList.iterator(); it.hasNext();) {
                        NioTask task = it.next();
                        try {
                            SocketChannel clientChannel = task.getClientChannel();
                            if (task.getData() != null) {
                                clientChannel.register(selector, task.getOp(), task.getData()) ;
                            } else {
                                clientChannel.register(selector, task.getOp()) ;
                            }
                        } catch (Throwable e) {
                            e.printStackTrace(); // ignore.
                        }
                        it.remove();
                    }
                } finally {
                    taskMainLock.unlock();
                }
            }
        }
    }

    /**
     * 此处的reqBuffer处于可写状态
     * @param sc
     * @param reqBuffer
     */
    private void dispatch(SocketChannel sc, ByteBuffer reqBuffer) {
        businessExecutorPool.submit(new Handler(sc, reqBuffer, this));
    }
}
