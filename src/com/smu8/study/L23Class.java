package com.smu8.study;


import java.util.Date;

public class L23Class {
    public static void main(String[] args) {
        //데이터 : 기본형(수,원시형), 자료형(복합적,참조형)
        // 기본형 표기법 (8개) : 리터럴하게 표기, 소문자 타입명시
        int i=10; // byte, short, int, long
        double d=10.0; // float, double
        char c='c';
        boolean b=true;
        // 자료형 표기법 : new 연산자로 생성자 호출, 파스칼 규칙(class)
        Date date=new Date(); //new Date(); 객체(Object, Instance) => 객체의 타입이 자료형, 참조형,, 복수의 데이터를 하나로 묶는 것
        System.out.println(date.toLocaleString()); //2026. 1. 15. 오전 10:48:50
        //System.out.println(i.); //기본형은 수만 존재하는 데이터기 때문에 다른 자료를 참조하지 않는다.
        //참조형 : 여러 데이터를 참조하는 것
        //참조형 : 주소(식별자)만 존재
        new Student();
        //데이터 재사용 => 변수
        var s=new Student();
        s.hello();
        System.out.println(s); //com.smu8.study.Student@3f91beef (해당 객체의 타입@주소)
        // 객체에 대한 참조하는 데이터가 많아서 무엇인지 설명하기 곤란하여 주소만 나옴 (참조형)
        System.out.println(i); // == int i 10
        // 저장된 데이터가 수, 즉 원시 데이터이기 때문에 설명 가능하여 주소가 아닌 데이터가 출력 (기본형)
        Student s2=new Student();
        s2.name="태연";
        s2.hello();

    }
}
//main 실행될 때 main이 포함된 package를 모두 로딩 후 저장 (메소드영역)
// 이후에 main 코드를 하나씩 실행
class Student{// class는 파스칼 표기법
    //3가지 요소
    //필드 : 저장할 데이터 (성적,이름,id --)
    //함수 : 객체의 기능 (인사)
    //생성자 : 생성할 때 호출됨
        String grade="A";
        String name="김태연";
        String id="TY890309";
        public Student(){
            System.out.println("Student() 생성자 호출 됨!");
        } // 기본 생성자 *쓰든 안쓰든 존재함
        public void hello(){ //함수는 카멜표기법
            System.out.println(name+"이 인사합니다.(꾸벅)");
        }
}






