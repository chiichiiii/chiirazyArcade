package com.smu8.oop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

class StaticObjectTest{
    int a; //==this에 소속(=> 객체에 존재)
    static int count; // this와 무관

    void setA(){
        a++;
        this.a++;
        //static이 아닌 필드와 메소드는 같은 객체에 존재하기 때문에 접근 가능
    }

    static void set(){
        //a++;
        //this.a++;
        count++;
        //this.count++;
        //static 으로 선언된 필드와 메소드는 실행될 때 데이터로 생성됨 == class member
        //static이 아닌 필드와 메소드는 객체가 될 때만 데이터로 존재 (객체==this)
    }
}

public class L10StaticObject {
    int a;
    static int b;

    public static void main(String[] args) {
        //a와 b를 하나씩 증가
        //static 끼리는 같은 시점에 존재하기 때문에 접근 가능
        b++;
        L10StaticObject.b++;
        System.out.println(b); //2
        //a++;
        L10StaticObject obj=new L10StaticObject();
        obj.a++; //this == obj
        System.out.println(obj.a); //1
        L10StaticObject obj2=new L10StaticObject();
        obj2.a++; //this == obj2
        System.out.println(obj2.a); //1
        //객체는 서로 필드를 공유하지 않는다.(객체는 서로 관련이 없다.)
        System.out.println(obj.b); //2
        //static은 객체로 접근하는 것은 문법적 오류
        //하지만 컴파일러가 L10StaticObject.b 클래스 변수 접근으로 바꾼다.

        //System.out : static 필드는 자주 사용하는 필드나 메서드에 사용
        //"11"=>11
        System.out.println(Integer.parseInt("11")*11);
        //System.out.println(new Integer(11).parseInt("11")*11);

        int[] nums={111,222,333,444};
        System.out.println(nums);
        // [I@b4c966a (자료형 참조형은 참조하는 데이터가 많아서 식별자(주소)로 표현)
        System.out.println(Arrays.toString(nums));
        //System.out.println(new Arrays().toString(nums));
        Date now=new Date();
        System.out.println(now.toLocaleString());
        System.out.println(LocalDate.now());
        System.out.println(LocalDateTime.now());
        //2026-01-26T12:48:29.562287500 : 국제표준 날짜 표기법
        System.out.println(Math.PI);


    }
}
