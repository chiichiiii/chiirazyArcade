package com.smu8.oop;

public class L07AccessModifier {
    public int a; //public : 누구나 접근가능
    int b; //default : 같은 패키지(com.smu8.oop)에 있는 클래스가 접근가능
    protected int c; //protected : 상속 관계에서 접근 가능(상속시간)
    private int d; //private : 같은 클래스에서 접근 가능

    //같은 클래스에서 접근 가능한가? : 같은 클래스에서는 모두모두 접근 가능!
    public void set(){
        this.a=100;
        this.b=200;
        this.c=300;
        this.d=400;
    }
}
class AccessModifierMain{
    //com.smu8.oop.AccessModifierMain 에서 같은 패키에 있는 L07AccessModifier 의 필드 중
    //어떤것을 접근할 수 있나??
    public static void main(String[] args) {
        L07AccessModifier m=new L07AccessModifier();
        m.a=10;
        m.b=20;
        m.c=30;
        // m.d=40; //컴파일 오류
    }
}