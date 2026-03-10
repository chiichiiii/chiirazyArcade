package com.smu8.javautil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

interface CallBack{
    void test();
}
//CallBack 인터페이스를 객체로 사용하는 3가지 방법
//1.class를 만들어서 구현
//2.익명클래스
//3.CallBack에 추상메서드가 1개일 때 람다식

public class L16FunctionalInterface {
    //1
    static class CallBackFunc implements CallBack{
        @Override
        public void test() {
            System.out.println("class로 구현된 메서드");
        }
    }
    //class 1 implements CallBack{...}
    public static void main(String[] args) {
        CallBack c=new CallBackFunc();
        //2
        CallBack c2=new CallBack(){
            @Override
            public void test() {
                System.out.println("익명 클래스로 구현된 메서드");
            }
        };
        //3
        CallBack c3=()->{
            System.out.println("람다식으로 구현된 메서드");
        }; //test()->{};
        c.test();
        c2.test();
        c3.test();
        //java에서 사용하기 위해 미리 만들어놓은 함수형 인터페이
        //Consumer 소비자 accepts(T)->{}
        Consumer consumer=(Object o)->{};
        Consumer<String> consumer2=(String s)->{};
        Consumer<Integer>consumer3=(i)->{};
        //Collection.forEach(Consumer)
        List<Integer>numList=new ArrayList<>(Arrays.asList(10,2,-33,50,70));
        //음수가 있는지 검사하세요
        numList.forEach((num)->{
            if(num<0)
            System.out.println(num+",");
        });
        Optional<Integer>intOpt=Optional.of(13);
        intOpt.ifPresent((num)->{
            System.out.println(num+"이 존재합니다.");
        });
        Runnable r=()->{}; //run
        Optional<String>strOpt=Optional.empty();
        strOpt.ifPresentOrElse((s)->{},()->{
            System.out.println("데이터가 없음");
        });
        Function<String,Integer>fun=(str)->{return str.length();};
        Function<String,Integer>fun2=(str)->str.length();
        Function<String,Integer>fun3=String::length; //메서드 참조


        Predicate<String> predicate=(str)->{return str.length()>0;};
        Predicate<String> predicate2=(str)->str.length()>0;
        Supplier<String>supplier=()->"공급자";
    }
}
