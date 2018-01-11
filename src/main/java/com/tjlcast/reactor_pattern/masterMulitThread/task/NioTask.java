package com.tjlcast.reactor_pattern.masterMulitThread.task;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

/**
 * Created by tangjialiang on 2018/1/11.
 * Nio task
 */

public class NioTask implements Serializable {

    private SocketChannel clientChannel ;
    private int op ;
    private Object data ;

    public NioTask(SocketChannel clientChannel, int op) {
        this.clientChannel = clientChannel ;
        this.op = op ;
    }

    public NioTask(SocketChannel clientChannel, int op, Object data) {
        this(clientChannel, op);
        this.data = data ;
    }

    public SocketChannel getClientChannel() {
        return clientChannel;
    }

    public void setClientChannel(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
