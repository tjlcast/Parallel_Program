package com.tjlcast._3th;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by tangjialiang on 2017/11/12.
 *
 * 如果共享和发布对象，从而是他们能够安全地由多个线程同时访问。
 */
public class _3th_coding {
}


class NoVisibility {
    private static boolean ready ;
    private static int number ;

    private static class ReaderThread extends Thread {
        public void run() {
            while(!ready) {
                Thread.yield();
            }
            System.out.println(number) ;
        }
    }

    private static void main(String[] args) {
        new ReaderThread().start() ;
        number = 42 ;
        ready = true ;
    }

    /**
     * 因为"重排序"等原因，子线程可能不能检查到写入到ready的数值。
     * 编译器、处理器可能对操作的执行顺序进行一些意想不到的调整。
     * 失效数据。 当读线程查看ready变量时，可能会得到一个已经失效的值。除非在
     * 每次访问变量时都使用同步，否则很可能获得该变量的一个失效值。
     */
}


class MutableInteger {
    private int value ;

    public int get() { return value; }
    public void set(int value) {this.value = value ; }

    /**
     * get和set没有进行同步。
     */
}


@ThreadSafe
class SynchronizedInteger {
    @GuardedBy("this") private int value ;

    public synchronized int get() { return value ; }
    public synchronized void set(int value) { this.value = value ; }
    /**
     * volatile变量的读写行为与该模式的变量相同，在底层中没有加锁。
     */
}

/**
 * 加锁机制既可以确保可见性又可以确保原子性，而volatile变量只能确保可见性。
 */


/**
 * 一般来说，如果一个已经发布的对象能够通过非私有的变量引用和方法调用到达其他的对象，
 * 那么这些对象也都会被发布。
 *
 * 发布，使对象能够在当前作用域之外的代码中使用。
 *
 * tips：在构造器中启动线程时，无论是显示创建（通过将它传给构造函数）还是隐式创建（由于
 * Thread或Runnable是该对象的一个内部类），this引用都会被新穿件的线程共享。在该对象尚未完全
 * 构造之前，新个线程就可以看见它。
 */


/**
 * 使用工厂方法来防止this引用在构造过程中溢出
 */
public class SafeListener {
    private final EventListener listener ;

    private SafaListener() {
        listener = new EventListenser() {
            public void onEvent(Event e) {
                doSomething(e) ;
            }
        }
    }

    public static SafeListener newInstance(EventSource source) {
        SafeListener safe = new SafeListener() ;
        source.registerListener(safe.listener) ;
        return safe ;
    }
}


/**
 * 隐式地使this引用溢出(don't do this)
 */
public class ThisEscap {
    public ThisEscap(EventSource source) {
        source.registerListener (
                new EventListener() {
                    public void onEvent(Event e) {
                        doSomething(e) ;
                    }
                }
        )
        // SafeListener.newInstance
    }
}

/**
 * ThreadLocal对象通常用于防止对可变的单例变量或全局变量进行共享。
 *
 *
 * 例如： 由于JDBC的连接对象不一定是线程安全的，因此，当多线程应用程序在没有
 * 协同的情况下使用全局变量时，就不是线程安全的。通过将JDBC的连接保存到ThreadLocal对象
 * 中，每个线程都会拥有属于自己的连接。
 */

public class _4JDBC {
    private static ThreadLocal<Connection> connectionHolder =
            new ThreadLocal<Connection>() {
                public Connection inititalValue() {
                    return DriverManager.getConnection(DB_URL) ;
                }
            } ;

    public static Connection getConnection() {
        return connectionHolder.get() ;
    }
    /**
     * 当某个线程初次调用ThreadLocal.get方法时，就会调用initialValue来获取初始值。
     * 对与ThreadLocal，可以将ThreadLocal<T>视为包含了Map<Thread, T>对象，其中
     * 保存了特定于该线程的值。
     *
     * 单线程应用程序移植到多线程环境中，通过将共享的全局变量转换为ThreadLocal对象（如果
     * 全局变量语义允许），可以维持线程安全性。
     */
}



/**
 * 每当需要对一组相关数据以原子方式执行某个操作时，就可以考虑创建一个不可变的类来
 * 包含这些数据。
 * 对于在访问和更新多个相关变量时出现的竞争条件问题，可以通过将这些变量全部保存在
 * 一个不可变对象中来消除。
 */
@Immutable
class OneValueCache {
    private final BigInteger lastNumber ;
    private final BigInteger[] lastFactors ;

    public OneValueCache(BigInteger i,
                         BigInteger[] factors) {
        lastNumber = i ;
        lastFactors = Arrays.copyOf(factors, factors.length) ;
    }

    public BigInteger[] getLastFactors(BigInteger i) {
        if (lastFactors == null || !lastNumber.equals(i))
            return null ;
        else
            return Arrays.copyOf(lastFactors, lastFactors.length) ;
    }
}
@ThreadSafe
public class VolatileCacheFactorizer implements Servlet {
    private volatile  OneValueCache cache =
            new OneValueCache(null, null) ;

    public void service(ServletRequest req, ServletResposne resp) {
        BigInteger i = extractFromRequest(req) ;
        BigInteger[] factors = cache.getFactors(i) ;
        if (factors == null) {
            factors = factors(i) ;
            cache = new OneValueCache(i, factors) ; // 对变量引用进行赋值，而非修改变量
        }
        encodeIntoResposne(resp, factors) ;
    }
}
