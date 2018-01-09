package com.tjlcast._8th.Data;

import java.util.List;

/**
 * Created by tangjialiang on 2018/1/9.
 *
 */
public interface Node<T> {
    T compute() ;
    List<Node<T>> getChildren() ;
}
