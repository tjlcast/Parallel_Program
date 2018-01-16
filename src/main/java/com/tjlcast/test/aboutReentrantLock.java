package com.tjlcast.test;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tangjialiang on 2018/1/14.
 *
 * 测试ReentrantLock的相关功能.
 */
public class aboutReentrantLock {

    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();

        reentrantLock.tryLock() ;
        try {

        } finally {
            reentrantLock.unlock();
        }
    }
}
