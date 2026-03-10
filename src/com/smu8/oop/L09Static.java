package com.smu8.oop;

//class는 data x (객체의 형(틀,type))
class StaticTest{
    int a=10;
    void call(){
        System.out.println("여보세요?");
    }
}
class StaticTest2{ //class는 data가 아님
    static int a=100; //static으로 선언된 필드나 함수는 class와는 별개로 data로 존재
    static void call(){
        System.out.println("여보시오");
    }
}

public class L09Static {
    // main : jvm 호출
    // public : jvm은 오픈
    // static : jvm은 이미 생성됨
    // void : 어플은 싱행이 반환
    // String[]args : 프로그램 실행 시 필요한 초기 조건
    public static void main(String[] args) {
        //System.out.println(StaticTest.a); //class는 데이터가 아니기 때문 오류
        //StaticTest.call(); //class는 데이터가 아니기 때문 오류
        StaticTest s=new StaticTest(); //class로 객체 생성(Data)
        System.out.println(s.a);
        s.call();
        System.out.println(StaticTest2.a);
        StaticTest2.call();
    }
}
