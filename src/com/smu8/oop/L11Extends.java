package com.smu8.oop;

class A{
    int x;
    private int p; //extends 해도 B에서 접근 불가
    public void call(){
        System.out.println(this.x);
    }
}

class B extends A{}


public class L11Extends {
    public static void main(String[] args) {
        A a=new A();
        a.x=100;
        a.call();

        B b=new B();
        b.x=200;
        b.call();
        //b.p=20;

        A c=new B(); //객체는 부모타입의 변수가 참조 가능 (객체가 여러 타입의 변수로 참조, 타입의 다형성)
    }
}
