package com.smu8.oop;


class Person{
    String name;
    int age;
    double score;
    //전역에 선언된 변수, 전역변수, 필드 (객체의 상태), 속성

    public void test(){
        String name="테스터"; //test()가 호출되면 만들어지는 지역변수
        System.out.println("name: "+name);
        System.out.println("this name: "+this.name);
        //this 객체 자신을 접근
    }
}


public class L05Field {
    public static void main(String[] args) {
        Person p=new Person();
        System.out.println(p.name); //null
        System.out.println(p.age); //0
        System.out.println(p.score); //0.0
        p.name="태연";
        p.age=40;
        p.score=89.39;
        System.out.println(p.name);
        System.out.println(p.age);
        System.out.println(p.score);

        int a; //main의 지역변수
        //a가 생성되었지만 초기화되지는 않음 => Undefined(사용 금지)
        int b=10; //b가 생성되면서 10으로 초기화 됨
        //System.out.println(a); //컴파일 오류
        //++a;
        //p.test.name="뫄뫄"; 객체의 지역변수는 접근하거나 바꿀 수 없다.
        p.test(); //name: 테스터
        //this.name: 태연

    }
}
