package com.smu8.oop;

class Outer{
    int o=20;
    class Inner{
        int i=10;
        void print(){
            System.out.println(this.i);//this==Inner
            System.out.println(Outer.this.o); //Outer가 객체일때 == Outer.this
        }
    }
    Inner i=new Inner();
    public Outer(){
        i.print();
        System.out.println("////////////////");
        Inner i2=new Inner();
        i2.print();
    }
}


public class L21InnerClass {
    //내부클래스
    public static void main(String[] args) {
        Outer outer=new Outer();
        System.out.println("----------------");
        //Inner class는 외부에서 사용하지 않지만 수업이니까 생성해봄
        //Outer.Inner inner=new Outer.Inner(); //Outer.Inner는 독립적으로 객체로 존재 불가
        Outer.Inner inner=new Outer().new Inner();
        System.out.println("----------------");
        inner.print();
    }
}
