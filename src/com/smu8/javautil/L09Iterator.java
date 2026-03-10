package com.smu8.javautil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class L09Iterator {
    public static void main(String[] args) {
        List<String>strList=new ArrayList<>();

        strList.add("자바");
        strList.add("컬렉션");
        strList.add("리스트");
        strList.add("셋");
        strList.add("맵");
        strList.add("이터러블");
        //반복탐색 : 요소의 값을 변경하거나 검사하기 위해
        for (int i=0;i<strList.size();i++){
            System.out.print(strList.get(i)+","); //자바,컬렉션,리스트,셋,맵,이터러블,
        }
        //Iterator: 반복자는 직접 이동하면서 값을 탐색
        System.out.println("\n반복자 출력!");
        Iterator<String> it=strList.iterator();
        System.out.println(it.next());
        System.out.println(it.next());
        System.out.println(it.next());
        System.out.println(it.next());
        System.out.println(it.next());
        System.out.println(it.hasNext()); //다음 요소가 있니??
        System.out.println(it.next());
        System.out.println(it.hasNext()); //마지막은 false
        //System.out.println(it.next()); //.NoSuchElementException
        Iterator<String>it2= strList.iterator();
        System.out.println("while iterator 반복문");
        while (it2.hasNext()){
            System.out.print(it2.next()+","); //자바,컬렉션,리스트,셋,맵,이터러블,
        }
        System.out.println("\n자바가 제공하는 이터레이터 반복문 (향상된 반복문)");
        //자바가 제공하는 이터레이터 반복문 (향상된 반복문)
        for(String str:strList){
            System.out.print(str+","); //자바,컬렉션,리스트,셋,맵,이터러블,
        }

    }
}
