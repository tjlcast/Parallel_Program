package com.tjlcast.lockImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangjialiang on 2018/1/19.
 */
public class FairLock {

    private boolean isLocked = false ;
    private Thread lockingThread = null ;
    private List<QueueObject> waitingThreads = new ArrayList<QueueObject>() ;

    public void lock() throws InterruptedException {
        QueueObject queueObject = new QueueObject() ;
        boolean isLockedForThisThread = true ;

        synchronized (this) {
            waitingThreads.add(queueObject) ;
        }

        while(isLockedForThisThread) {
            synchronized (this) {
                isLockedForThisThread = isLocked || waitingThreads.get(0) != queueObject ;
                if (!isLockedForThisThread) {
                    isLocked = true ;
                    waitingThreads.remove(queueObject) ;
                    lockingThread = Thread.currentThread() ;
                    return ;
                }
            }
        }

        try {
            queueObject.doWait();
        } catch (InterruptedException e) {
            synchronized (this) {
                waitingThreads.remove(queueObject) ;
            }
            throw e ;
        }
    }

    public synchronized void unlock() {}
}


class QueueObject {
    private boolean isNotified = false ;

    public synchronized void doWait() throws InterruptedException {
        while (! isNotified) {
            this.wait();
        }
        this.isNotified = false ;
    }

    public synchronized void doNotify() {
        this.isNotified = true ;
        this.notify();
    }

    public boolean equals(Object o) {
        return this == o ;
    }
}
