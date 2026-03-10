package com.smu8.javautil;

import java.util.HashSet;
import java.util.Set;

public class L13SetMethod {
    public static void main(String[] args) {
        Set<String> fruitSet=new HashSet<>();
        fruitSet.add("바나나");
        fruitSet.add("바나나");
        fruitSet.add("사과");
        fruitSet.add("망고");
        fruitSet.add("딸기");
        fruitSet.add("애플망고");
        System.out.println(fruitSet); //[애플망고, 망고, 사과, 바나나, 딸기]
        //fruitSet.get() //set은 찾아서 가져오는 것이 힘들다
        boolean isMango=fruitSet.contains("망고");
        System.out.println("망고 있니? "+isMango); // 망고 있니? true
        System.out.println(fruitSet.size()); // size==길이 //5
    }
}
