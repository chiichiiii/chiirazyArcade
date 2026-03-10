package com.smu8.homework;

import java.util.Date;

public class H19Exception {
    class A{}
    class B extends A{}
    class C extends A{}
    void castTest(){
        A b= new B();
        A c= new C();
        C castingC=(C)b; //ClassCastException
    }


    public static void main(String[] args) {
        //배열,파싱(parsing/형변환),casting
        H19Exception ex=new H19Exception();
        //ex.castTest();
        System.out.println("오류 발생시 jvm 종료");
        Object o="문자열";
        //오류인데 if로 처리할 수 있는 것: 개발자가 처리할 수 있는 오류 (unchecked)
        if(o instanceof Date) {
            Date d = (Date) o; //.ClassCastException
        }
    }
}
