package com.tjlcast.lockImpl;

import jdk.nashorn.internal.ir.IdentNode;

/**
 * Created by tangjialiang on 2018/1/19.
 */
public class LockExample {
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
