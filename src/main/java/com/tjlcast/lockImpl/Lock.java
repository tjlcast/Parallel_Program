package com.tjlcast.lockImpl;

/**
 * Created by tangjialiang on 2018/1/19.
 */
public class Lock {

    private boolean isLocked = false ;
    private Thread lockingThread = null ;

    /**
     * 这段代码中的进入lock() 并调用 wait()进行比较，
     * 会发现大部分时间用在等待进入锁和进行wait()的等待中，
     * 而不是被阻塞在试图进入lock()中
     * @throws InterruptedException
     */
    public synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }

        isLocked = true ;
        lockingThread = Thread.currentThread() ;
    }

    public synchronized void unlock() {
        if (this.lockingThread != Thread.currentThread()) {
            throw new IllegalMonitorStateException("Calling thread has not locked this lock") ;
        }

        isLocked = false ;
        lockingThread = null ;
        notify();
    }
}
