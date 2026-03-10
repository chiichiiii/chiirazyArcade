package com.smu8.homework;

public class H10AccessModifier {
    public int a; // 누구나 다 접근 가능
    int b; // default : 같은 패키지(package com.smu8.homework;) 내부에서 접근 가능
    protected int c; //상속 관계에서 접근 가능
    private int d; // 같은 class 내부에서 접근 가능

    //같은 class에서 접근 가능한가? : 같은 class에서는 모두 접근 가능
    public void set(){
        this.a=100;
        this.b=200;
        this.c=300;
        this.d=400;
    }
}
class AccessModifierMain{
    //com.smu8.homework.AccessModifierMain 에서 같은 패키지에 있는
    // H10AccessModifier 의 필드 중 어떤 것을 접근할 수 있나??
    public static void main(String[] args) {
        H10AccessModifier m=new H10AccessModifier();
        m.a=10;
        m.b=20;
        m.c=30;
        //m.d=40; //컴파일 오류, private는 외부에서 접근 불가능
    }
}
