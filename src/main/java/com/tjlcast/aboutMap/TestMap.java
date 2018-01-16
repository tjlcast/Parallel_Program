package com.tjlcast.aboutMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by tangjialiang on 2018/1/12.
 */
public class TestMap {

    public static void main(String[] args) {

        HashMap<String, String> hashMap = new HashMap();

        hashMap.put("a", "a") ;
        hashMap.put("b", "b") ;
        hashMap.put("c", "c") ;
        hashMap.put("d", "d") ;
        hashMap.put("e", "e") ;
        hashMap.put("f", "f") ;
        hashMap.put("g", "g") ;

        int i = 0 ;
        for (Iterator<Map.Entry<String, String>> it = hashMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> next = it.next();

            if (i == 2)
                it.remove();

            System.out.println(next.getKey() + " : " + next.getValue()) ;
            i++ ;
        }

        Set<String> strings = hashMap.keySet();
        for(String k : strings) {
            System.out.println(k + " - " + hashMap.get(k)) ;
        }
    }
}
