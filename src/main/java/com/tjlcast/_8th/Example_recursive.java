package com.tjlcast._8th;

import com.tjlcast._8th.Data.Node;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by tangjialiang on 2018/1/9.
 *
 */
public class Example_recursive {

    // 串行执行 递归
    public <T> void sequentialRecursive(List<Node<T>> nodes,
                                        Collection<T> results) {
        for(Node<T> node : nodes) {
            results.add(node.compute()) ;
            sequentialRecursive(node.getChildren(), results);
        }
        return ;
    }

    // 并行执行 递归 (递归 之间联系 较为简单)
    public <T> void parallelRecursive(final Executor exec,
                                      List<Node<T>> nodes,
                                      final Collection<T> results) {
        for(final Node<T> node : nodes) {
            exec.execute(new Runnable() {
                public void run() {
                    results.add(node.compute()) ;
                }
            });
            parallelRecursive(exec, node.getChildren(), results);
        }
        return ;
    }

    // 等待通过并行放松计算的处理结果
    public<T> Collection<T> getParallelResults(List<Node<T>> nodes) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        Queue<T> results = new ConcurrentLinkedQueue<T>() ;
        parallelRecursive(exec, nodes, results);
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS) ;
        return results ;
    }
}
