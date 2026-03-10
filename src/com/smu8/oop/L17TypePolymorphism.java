package com.smu8.oop;

class TestA{
    int a=100;
    void printA(){
        System.out.println("TestA");
    }
}
class TestB extends TestA{
    int b=200;
    void printB(){
        System.out.println("TestB");
    }
}
class TestC extends TestB{
    int c=300;

    @Override
    void printA() {
        //super.printA();
        System.out.println("TestC에서 재정의한 printA()");
    }

    void printC(){
        System.out.println("TestC");
    }
}


public class L17TypePolymorphism {
    //다형성

    //오버로드
    void sum(){}
    void sum(int a){}
    void sum(int a,int b){}
    //오버라이드 (부모 자식 둘 다 같은 함수를 가짐)
    @Override
    public String toString() {
        return super.toString();
    }

    //타입의 다형성
    public static void main(String[] args) {
        TestC c=new TestC(); //일반
        TestB b=new TestC(); //부모 타입이 자식 객체를 참조
        TestA a=new TestC();
        Object obj=new TestC();
        //타입의 다형성: 부모타입이 객체를 참조
        //Instanceof : ㄴㅓ 어떤 타입이니?
        //getClass() : 너 어떤 타입이니 문자열로??
        //System.out.println(a.c);
        System.out.println(a.a);
        //필드는 참조하는 부분만 사용
        a.printA();//함수도 참조하는 부분만 사용 //TestC에서 재정의한 printA()
        //**오버라이드는 제외 => 부모의 함수를 재정의한 함수가 메서드 실행 테이블에 명시되어서

        //a.printC();
        System.out.println(obj.getClass()); //class com.smu8.oop.TestC
        System.out.println(obj instanceof TestC); //true

        L17TypePolymorphism t=new L17TypePolymorphism();
        //객체를 참조하는 변수는 객체와 동일한 타입으로 참조
        Object o=new L17TypePolymorphism();
        //객체의 부모 타입 변수도 참조 가능 (타입의 다형성)
        //다형성 == 오버로드,오버라이드,타입의다형성
        //오버로드: 함수 이름은 1개인데 매개변수에 따라 여러개 존재
    }
}
