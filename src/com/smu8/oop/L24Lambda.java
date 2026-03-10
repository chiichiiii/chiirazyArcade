package com.smu8.oop;

import java.awt.event.ActionListener;
import java.util.function.Function;

@FunctionalInterface
interface LambdaTest{
    void a();
    static void b(){}; //필드와는 관련이 없으니 가능
}
@FunctionalInterface
interface ParamTest{
    int sum(int a,int b);
}
public class L24Lambda {
    public static void main(String[] args) {
        LambdaTest l=new LambdaTest() {
            @Override
            public void a() {
            }
        };
        LambdaTest l2=()->{};
        //ParamTest p=()->{}; //ParamTest.sum(a,b)를 오버라이드 하는 중
        ParamTest p=(int a,int b)->{return a+b;};
        System.out.println(p.sum(10,30));
        ParamTest p2=(int a,int b)->a+b; //return, {} 생략 가능

        ActionListener a=(e)->{};
        Runnable run=()->{};
    }
}
