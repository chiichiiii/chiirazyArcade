package com.smu8.javautil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L06List {
    public static void main(String[] args) {
        //List 는 Array를 보완하기 위해 나온 컬렉션
        int [] numArr=new int[4]; //{0,0,0,0}
        int [] numArr2={11,22,33,44};
        System.out.println(numArr2[0]);
        System.out.println(numArr2[1]);
        System.out.println(numArr2[2]);
        System.out.println(numArr2[3]);
        //numArr2[4]=55;
        //Array는 가볍고 기본형이 가능하지만 길이 변경이 불가
        //=> 고정 길이 데이터에 적합
        List<Integer> numList=new ArrayList<>();
        numList.add(111);
        numList.add(222);
        numList.add(333);
        numList.add(444);
        numList.add(555);
        numList.add(2,666); // 2번째 위치에 666 추가하겠다
        System.out.println(numList);
        //[111, 222, 666, 333, 444, 555] : ArrayList가 toString을 재정의해서
        System.out.println(numList.size()); //6
        numArr2[2]=66; //2번째 위치에 66으로 바꾸겠다
        System.out.println(numArr2); //[I@b4c966a
        System.out.println(Arrays.toString(numArr2)); //[11, 22, 33, 44]
        System.out.println(numArr2.length); //4

        //List는 삭제가 가능
        numList.remove(Integer.valueOf(666)); //정수 666을 지우겠다
        //List 모든 요소를 자료형으로 갖는다.
        //numList.remove(2); //2번째 위치 지우기
        System.out.println(numList); //[111, 222, 333, 444, 555]
    }
}
