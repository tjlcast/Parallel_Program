package com.tjlcast.test;

/**
 * Created by tangjialiang on 2018/1/10.
 *
 */
public class aboutFinally {

    public static void main(String[] args) {

        try {
            System.out.println("this is try") ;
            int i = 1 / 0;
        } catch (Exception e) {
            System.out.println("this is catch") ;
            return ;
        } finally {
            System.out.println("this is finally") ;
        }
    }
}
