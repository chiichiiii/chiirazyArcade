package com.smu8.javautil;

import java.util.*;

public class L10ListMethod {
    public static void main(String[] args) {
        List<String> strList=new ArrayList<>();
        strList.add("A");
        strList.add("B");
        strList.add("C");
        strList.add("D");
        strList.add("E");
        System.out.println(strList); //[A, B, C, D, E]
        strList.add(2,"Z");
        System.out.println(strList); //[A, B, Z, C, D, E]
        strList.set(2,"ㄱ");
        System.out.println(strList); //[A, B, ㄱ, C, D, E]
        //add: 데이터를 추가 => 길이 변경
        //set: 데이터를 변경
        //strList.remove(2);
        strList.remove("ㄱ");
        System.out.println(strList); //[A, B, C, D, E]

        System.out.println(strList.get(3)); //D
        System.out.println(strList.contains("D")); //true
        System.out.println(strList.contains("F")); //false
        System.out.println(strList.indexOf("D")); //3
        System.out.println(strList.indexOf("F")); //-1
        System.out.println(strList.lastIndexOf("D")); //3 뒤에서 요소 찾기(요소가 많을 때 뒤에 있는 요소가 찾는게 유리함)
        strList.clear();
        System.out.println(strList); //[]
        strList=new ArrayList<>(Arrays.asList("F","G","Abc","D","C","B","Acc"));
        strList.add("Bac");
        strList.sort(Collections.reverseOrder()); //역순 정렬 DESC
        System.out.println(strList); //[G, F, D, C, B, A]
        Collections.sort(strList); //정렬 ASC
        System.out.println(strList); //[A, B, C, D, F, G]

        List<Integer>numList=new ArrayList<>(Arrays.asList(111,0,-45,39,89,21,12));
        System.out.println(numList); //[111, 0, -45, 39, 89, 21, 12]
        numList.sort(Comparator.naturalOrder());
        System.out.println(numList); //[-45, 0, 12, 21, 39, 89, 111] ASC
        numList.sort(Comparator.reverseOrder());
        System.out.println(numList); //[111, 89, 39, 21, 12, 0, -45] DESC


        //Arrays: 길이불가 상태로 생성
        //strList= Arrays.asList("A","B","C","D","E","F");
        //System.out.println(strList);

        //strList.add(2,"Z"); //.UnsupportedOperationException
        //strList.set(2,"Z");
        //System.out.println(strList); //[A, B, Z, D, E, F]
    }
}
