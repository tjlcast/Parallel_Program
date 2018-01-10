package com.tjlcast.test;

/**
 * Created by tangjialiang on 2018/1/10.
 */
public class aboutFinally {

    public static void main(String[] args) {

        try {
            System.out.println("this is try") ;

            return ;
        } catch (Exception e) {
            System.out.println("this is catch") ;
        } finally {
            System.out.println("this is finally") ;
        }
    }
}
