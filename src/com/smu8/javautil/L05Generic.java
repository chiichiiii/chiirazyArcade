package com.smu8.javautil;

import java.time.LocalDate;

class GenericTest<T>{
    public T o;
}

public class L05Generic {
    public static void main(String[] args) {
        GenericTest g = new GenericTest(); // <?> : T가 Object가 된다
        g.o="안녕"; //String
        g.o=13; // Integer
        g.o=true; //Boolean
        g.o= LocalDate.now(); //LocalDate
        //타입의 다형성
        // - 장점 : 어떤 타입의 객체든 부모 타입의 변수로 참조 가능
        // - 단점 : 어떤 객체를 참조하고 있는지 파악하기 힘들다
        // Generic : 타입의 다형성의 단점 보완
        // Generic은 기본형이 될 수 없고 랩퍼 클래스만 작성 가능
        GenericTest<Integer> g2=new GenericTest();
        //g2.o="안녕";
        //g2.o=LocalDate.now();
        g2.o=13;
        //g2.o=true;
        g2.o=(int)13.0; //다운캐스팅
        System.out.println(g2.o);

    }
}
