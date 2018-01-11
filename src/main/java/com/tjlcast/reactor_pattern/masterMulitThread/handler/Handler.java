package com.tjlcast.reactor_pattern.masterMulitThread.handler;

import com.tjlcast.reactor_pattern.masterMulitThread.SubReactorThread;
import com.tjlcast.reactor_pattern.masterMulitThread.task.NioTask;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by tangjialiang on 2018/1/11.
 *
 * 业务类 的包装类
 */
public class Handler implements Runnable {

    private static final byte[] msg = "hello, 服务器收到了你的消息".getBytes() ;

    private SocketChannel sc ;
    private ByteBuffer reqBuffer ;
    private SubReactorThread parent ;

    public Handler(SocketChannel sc, ByteBuffer reqBuffer, SubReactorThread parent) {
        super() ;
        this.sc = sc ;
        this.reqBuffer = reqBuffer ;
        this.parent = parent ;
    }

    @Override
    public void run() {
        System.out.println("业务在handler中开始执行...") ;

        // 相关业务处理
        reqBuffer.put(msg) ;
        parent.resgister(new NioTask(sc, SelectionKey.OP_WRITE, reqBuffer)) ;
        System.out.println("业务在handler中执行结束...") ;
    }
}
