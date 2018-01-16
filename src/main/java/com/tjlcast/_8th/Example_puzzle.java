package com.tjlcast._8th;

import com.tjlcast._8th.Data.ValueLatch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by tangjialiang on 2018/1/9.
 *
 */
public class Example_puzzle {


}


/**
 * 始终并行的方法执行
 * @param <P>
 * @param <M>
 */
class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle ;
    private final ExecutorService exec ;
    private final ConcurrentMap<P, Boolean> seen ;
    private ValueLatch<Node<P, M>> soluction = new ValueLatch<Node<P, M>>() ;

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle, ExecutorService exec, ConcurrentMap<P, Boolean> seen) {
        this.puzzle = puzzle;
        this.exec = exec;
        this.seen = seen;
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            exec.execute(newTask(p, null, null));

            Node<P, M> resultNode = soluction.getValue();
            return resultNode.asMoveList() ;
        } finally {
            exec.shutdown() ;
        }
    }

    protected Runnable newTask(P p, M m, Node<P, M> n)  {
        return new SolverTask(p, m, n) ;
    }

    class SolverTask extends Node<P, M> implements Runnable {
        SolverTask(P pos, M move, Node pre) {
            super(pos, move, pre);
        }

        public void run() {
            if (soluction.isSet()
                    || seen.putIfAbsent(pos, true) != null)
                return ;
            if (puzzle.isGoal(pos)) {
                soluction.setValue(this);
            } else {
                for(M m : puzzle.legalMoves(this.pos)) {
                    newTask(puzzle.move(this.pos, m), m, this);
                }
            }
        }
    }
}

/**
 * 使用串行的方法执行
 * @param <P>
 * @param <M>
 */
class SequentialPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle ;
    private final Set<P> seen = new HashSet<P>() ;

    SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        List<M> searchRes = this.search(new Node<P, M>(pos, null, null));
        return searchRes ;
    }

    public List<M> search(Node<P, M> node) {
        if(!seen.contains(node)) {
            seen.add(node.pos) ;
            if (puzzle.isGoal(node.pos)) {
                return node.asMoveList() ;
            }

            for(M canMove : puzzle.legalMoves(node.pos)) {
                P pos = puzzle.move(node.pos, canMove);
                Node<P, M> child = new Node<P, M>(pos, canMove, node) ;
                List<M> result = search(node);
                if (result != null)
                    return result ;
            }
        }
        return null ;
    }
}

/**
 * 游戏的env
 * @param <P>
 * @param <M>
 */
interface  Puzzle<P, M> {
    P initialPosition() ;
    boolean isGoal(P position) ;
    Set<M> legalMoves(P position) ;
    P move(P position, M move) ;
}

/**
 * 状态节点
 * @param <P>
 * @param <M>
 */
class Node<P, M> {
    final P pos ;
    final M move ;
    final Node<P, M> pre ;

    Node(P pos, M move, Node<P, M> pre) {
        this.pos = pos;
        this.move = move;
        this.pre = pre;
    }

    List<M> asMoveList() {
        List<M> solution = new LinkedList<M>() ;
        for(Node<P, M> n=this; n!=null; n=n.pre) {
            solution.add(0, n.move) ;
        }
        return solution ;
    }
}



