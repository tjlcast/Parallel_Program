package com.tjlcast.lockImpl;

/**
 * Created by tangjialiang on 2018/1/19.
 */
public class Lock {

    private boolean isLocked = false ;
    private Thread lockingThread = null ;

    /**
     * 线程进入由 sychronized 和 wait 进行控制
     * 这段代码中的进入lock() 并调用 wait()进行比较，
     * 会发现大部分时间用在等待进入锁和进行wait()的等待中，
     * 而不是被阻塞在试图进入lock()中
     *
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

class LockExample {
    Lock lock = new Lock() ;

    /**
     * 将在临界区内运行很长一段时间，
     *
     * @throws InterruptedException
     */
    public void doSynchronized() throws InterruptedException {
        this.lock.lock();

        // critical section, do a lot of work which takes a long time.

        this.lock.unlock();
    }
}

