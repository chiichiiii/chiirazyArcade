package com.smu8.javautil;

import java.util.*;

public class L07Set {
    public static void main(String[] args) {
        Set<Integer>numSet=new HashSet<>();
        numSet.add(789);
        numSet.add(123);
        numSet.add(456);
        numSet.add(789);
        System.out.println(numSet); //[789, 456, 123]
        //Set 중복된 데이터를 확인 후 저장(==, .equals)
        //Set, HashSet 순서가 없다
        System.out.println(789==789);
        System.out.println("안녕".equals("안녕"));
        Set<Integer>numSet2=new LinkedHashSet<>();
        numSet2.add(111);
        numSet2.add(222);
        numSet2.add(333);
        numSet2.add(444);
        numSet2.add(555);
        numSet2.add(555);
        System.out.println(numSet2); //[111, 222, 333, 444, 555]
        //순서는 보장되지만 인덱스 기반으로 데이터를 찾을 수 없다
        List<Integer> numList=new ArrayList<>();
        numList.add(111);
        numList.add(222);
        numList.add(333);
        numList.add(444);
        numList.add(555);
        numList.add(555);
        System.out.println(numList); //[111, 222, 333, 444, 555, 555]
        System.out.println(numList.get(2)); //333


        Set<Integer>numTree=new TreeSet<>();
        numTree.add(13);
        numTree.add(4);
        numTree.add(20);
        numTree.add(3);
        numTree.add(1);
        numTree.add(5);
        numTree.add(7);
        numTree.add(1);
        System.out.println(numTree); //[1, 3, 4, 5, 7, 13, 20]

        //자료형은 equals가 구현된 타입만 set 으로 중복 제거됨
        Set<String> strSet=new HashSet<>();
        strSet.add("경민");
        strSet.add("경만");
        strSet.add("경민코딩");
        strSet.add("경민");
        strSet.add("경민의코딩입문소");
        strSet.add("경만");
        System.out.println(strSet); //[경민코딩, 경민의코딩입문소, 경민, 경만]
        // String은 equals가 구현되기 때문에 중복 제거

    }
}
