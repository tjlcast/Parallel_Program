package com.tjlcast._8th;

import com.tjlcast._8th.Data.Element;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by tangjialiang on 2018/1/9.
 */
public class Example_Itera {

    // work
    void process(Element e) {
        return ;
    }

    // 将串行执行 process
    void processSequentially(List<Element> elements) {
        for(Element e : elements) {
            process(e) ;
        }
    }

    // 并行执行 process
    void processInParallel(Executor exec, List<Element> elemets) {
        for(final Element e : elemets) {
            exec.execute(new Runnable() {
                public void run() {
                    process(e);
                }
            });
        }
    }
}


