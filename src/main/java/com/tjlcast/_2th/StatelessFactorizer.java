package com.tjlcast._2th;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tangjialiang on 2017/11/10.
 *
 * 如何通过同步来避免多个线程在同一时刻访问相同的数据。
 */


public class StatelessFactorizer {
    /**
     * StatelessFactorizer是无状态的，不包含任何域，也不包含任何对其他类中域的引用。
     * 计算过程的临时状态仅存在与线程栈上的局部变量中，并且只能由正在执行的线程访问。
     *
     * 访问StatelessFactorizer的线程不会影响另一个访问同一个StatelessFactorizer的线程的计算结果，因为这两个线程并没有共享状态，它们访问不同的实例。
     */

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req) ;
        BigInteger[] factors = factor(i) ;
        encodeIntoResponse(resp, factors) ;

        // 大多数Servlet都是无状态的，从而极大地降低了在实现Servlet线程安全行时的复杂性。
        // 只有当Servlet在处理请求时需要保存一些信息，线程安全性才会成为一个问题。

    }
}


@NotThreadSafe
public class UnsafeCountingFactorizer implements Servlet {
    private long count = 0 ;

    public long getCount() { return count ; }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req) ;
        BigInteger[] factors = facotr(i) ;
        count++ ;
        encodeIntoResponse(resp, factors) ;
    }

    /**
     * 在本类中存在多个竞态条件，从而使结果变得不可靠。当某个计算的正确性取决于多个线程的
     * 交替执行时序时，那么就会发生竞态条件。(Race Condition)
     */
}


@NotThreadSafe
public class LazyInitRace {
    private ExpensiveObject instance = null ;

    public ExpensiveObject getInstance() {
        if (instance == null) {
            instance = new ExpensiveObject() ;
        }
        return instance ;
    }

    /**
     * 使用"先检查后执行"的一种常见情况就是延迟初始化。
     */
}


@ThreadSafe
public class CountingFactorizer implements Servlet {
    private final AtomicLong count = new AtomicLong(0) ;

    public long getCount() { return count.get(); }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req) ;
        BigInteger[] factors = factor(i) ;
        count.incrementAndGet() ;
        encodeIntoResponse(resp, factors) ;
    }

    /**
     * 在java.util.concurrent.atomic中包含了一些原则变量类，用于实现在数值和对象引用上的原子状态转换。
     * 所有对count的操作都是原子的。
     */
}


// 内置锁： 同步代码块。
// 内置锁将会关联一个获取计数值和一个所有者线程。
// 类方法，锁加载到实例对象。
// 静态方法，锁加载到Class对象。
@TreadSafe
public class SynchronizedFactorizer implements Servlet {
    @GuardedBy("this") private BigInteger lastNumber ;
    @GuardedBy("this") private BigInteger[] lastFactors ;

    public synchronized void service(ServletRequest req,
                                     ServletResponse resp) {
        BigInteger i = extractFromRequest(req) ;

        if (i.equals(lastNumber)) {
            encodeIntoResponse(resp, lastFactors) ;
        } else {
            BigInteger[] facotors = factor(i) ;
            lastNumber = i ;
            lastFactors = facotors ;
            encodingIntoResponse(resp, factors) ;
        }
    }
}

class Widget {
    public synchronized void doSomething() {
        System.out.println("....") ;
    }
}
class LoggingWidge extends Widget {
    /**
     * 如果内置锁不是可以重入的，那么该类的doSomething获取到锁后，将调用父类的doSomething方法。由于锁加载到widget对象，所以不能进入父类的方法，进而阻塞。
     */
    public synchronized void doSomething() {
        System.out.println(toString() + ": calling doSomething") ;
        super.doSomething() ;
    }
}

/**
 * 约定：将所有的可变状态都封装在对象内部，并通过对象的内置锁对所有访问可变状态的代码路径进行同步。
 */

@ThreadSafe
public class CachedFactorizer implements Servlet {
    @GuardedBy("this") private BigInteger lastNumber ;
    @GuardedBy("this") private BigInteger[] lastFactors ;
    @GuardedBy("this") private long hits ;
    @GuardedBy("this") private long cacheHits ;

    public synchronized long getHits() { return hits; }
    public synchronized double getCacheHitRatio() {
        return (double) cacheHits / (double) hits ;
    }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest(req) ;
        BigInteger[] factors = null ;

        synchronized (this) {
            ++hits ;
            if (i.equals(lastNumber)) {
                ++cacheHits ;
                factors = lastFactors.clone() ;
            }
        }

        if (factors == null) {
            factors = factors(i) ;
            synchronized (this) {
                lastNumber = i ;
                lastFactors = factors.clone() ;
            }
        }

        encodeIntoResponse(resp, factors) ;
    }
}

/**
 * 目前看来java的同步方法：
 *      1、原子变量： atomicInteger
 *      2、同步代码块：synchronized
 */

/**
 * tips: 当执行时间较长的计算或者可能无法快速完成的操作时（例如，网络I\O后控制台I/O），一定不要持有锁。
 */


