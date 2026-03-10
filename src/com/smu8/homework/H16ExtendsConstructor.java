package com.smu8.homework;

class A{
    int a;
    public A(int a){ // A객체를 생성하려면 a를 무조건 초기화
        this.a=a;
    }
    void sum(){
        System.out.println("더하기");
    }
}
class B extends A{
    public B(){
        //super() == A super=new A();
        super(10); //== A super=new A(10);
        System.out.println("A.a 접근: "+super.a);
        System.out.println("A.a 접근: "+this.a);
        //부모의 것은 자식의 것이 되었기때문에 this로도 접근 가능
        //this로 찾았는데 없으면 자동으로 super로 접근
        super.sum();
        this.sum();
    }
}
public class H16ExtendsConstructor {
    public static void main(String[] args) {
        B b=new B();
    }
}
