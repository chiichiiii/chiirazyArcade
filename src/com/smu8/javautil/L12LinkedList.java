package com.smu8.javautil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class L12LinkedList {
    public static void main(String[] args) {
        List<String> strList=new LinkedList<>();
        strList.add("안녕");
        strList.add("잘가");
        System.out.println(strList);
        //링크드리스트는 사용법이 다르지 않음!!
        List<Integer> numArrList=new ArrayList<>();
        List<Integer> numLinkedList=new LinkedList<>();
        long start=System.nanoTime(); // 1/1_000_000초 == 나노초
        for (int i=0; i<1_000_000;i++){
            numArrList.add(i);
            numLinkedList.add(i);
        }
        long end=System.nanoTime();
        System.out.println((end-start)/1_000_000_000.0+"초"); //127.5509초
        //System.out.println(numLinkedList);
        //System.out.println(numArrList);
        start=System.nanoTime();
        numArrList.remove(0);
        end=System.nanoTime();
        System.out.println((end-start)/1_000_000.0+"ms"); //0.4972ms

        start=System.nanoTime();
        numLinkedList.remove(0);
        end=System.nanoTime();
        System.out.println((end-start)/1_000_000.0+"ms"); //0.0081ms
    }
}
