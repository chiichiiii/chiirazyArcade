package com.smu8.study;

public class L11BooleanOperator {
    public static void main(String[] args) {
        //논리 연산자
        // 같다 : ==
        // 같지않다 : !=
        // 크기를 비교하는 연산자 (값을 비교=>수[기본형 data]만 가능)
        // 크다(초과) : >
        // 작다(미만) : <
        // 크거나 같다(이상) : >=
        // 작거나 같다(이하) : <=
        // 반대 : !

        int a=0;
        double d=0.0;
        String s="0";
        // System.out.println(s>a); // 크기를 비교하는 연산은 기본형만 가능
        // System.out.println(a==s); // == 자료형도 비교 가능한데 기본형이랑 자료형 비교는 당연히 false이기 때문에 오류
        System.out.println(s=="0");
        //System.out.println(s>"1"); // 불가
        System.out.println(a==d);
        // 기본형의 비교는 값(크기)을 비교한다. (데이터를 비교하지 않는다.)
        System.out.println(8==8.0);
        System.out.println("----------------------");

        boolean b=10>3;
        System.out.println(b);
        System.out.println(!b);
        System.out.println("----------------------");
        System.out.println("1"=="1"); // true
        a=10;
        d=9.0;
        System.out.println(a==d); // false
        System.out.println(a>d); // true
        System.out.println(a!=d); // true
        d=10.0;
        System.out.println(a==d); // true
        System.out.println(a!=d); // false
        // java는 논리형의 수학적 연산을 하지 못하게 한다.
        // true : 1
        // false : 0
        // System.out.println(true+true); // 2 or ture
        System.out.println("----------------------");
        // &&(곱) : 하나라도 flase가 있으면 flase
        System.out.println(true&&true); // 1*1 >> 1 true
        System.out.println(false&&true); // 0*1 >> 0 flase
        System.out.println(false&&false); // 0*0 >> 0 false
        // ||(합) : 하나라도 true가 있으면 true
        System.out.println(true||true); // 1+1 >> 1 true
        System.out.println(true||false); // 1+0 >> 1 true
        System.out.println(false||false); // 0+0 >> 0 false

        System.out.println(false||false||false||false||true); // 0+0+0+0+1 >> 1 true
        System.out.println(true&&true&&true&&true&&false); // 1*1*1*1*0 >> 0 flase

        System.out.println(false||false||true||false||false); // 0+0+1+0+0 >> 1 true
        a=0;
        System.out.println(++a>10||++a>10||++a>1||++a>0||++a>0);// 1>10 || 2>10 || 3>1 --------
        // 결과와 a는 몇일까요? >> true , a=3
        System.out.println("a=" + a);



    }
}
