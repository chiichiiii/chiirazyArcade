package com.smu8.homework.other;
//com.smu8.homework.H10AccessModifier : 다른 패키지의 class를 호출

import com.smu8.homework.H10AccessModifier;

class ProtectedTest extends H10AccessModifier{
    public ProtectedTest(){
        H10AccessModifier m=new H10AccessModifier();
        m.a=111;
        //m.b=222;
        //m.c=333;
        //m.d=444;
        //객체를 만들지 않아도 상속받은 부모가 객체로 존재
        //이때 부모 객체를 참조하는 것은 super로 참조
        super.a=1111;
        //super.b=2222;
        super.c=3333; //protected
        //super.d=4444;
    }
}


public class H11AccessTest {
    public static void main(String[] args) {
        H10AccessModifier m=new H10AccessModifier();
        m.a=11; //public : 어디서든 접근 가능
        //m.b=22; //default : 다른 패키지로 오류
        //m.c=33; //protected : 상속관계가 아니라 오류
        //m.d=44; //private : 다른 클래스로 오류
    }
}
