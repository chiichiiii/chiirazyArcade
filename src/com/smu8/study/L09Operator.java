package com.smu8.study;

public class L09Operator {
    public static void main(String[] args) {
        //연산자 : 프로그래밍에서 어떤 일이 발생하게 하면 연산자
        int a=10; //'=' 대입 연산자
        int b=3;
        //정수와 실수를 연산하는 수학적 연산자
        System.out.println(a+b); //13
        System.out.println(a-b); //7
        System.out.println(a*b); //30
        System.out.println(a/b); //3
        System.out.println(a%b); //1
        System.out.println(a);

        //증감연산자
        System.out.println(++a); // '++' 하나를 증가
        System.out.println((--b)); //'--' 하나를 감소
        //++a -> 기존 데이터 10을 11로 바꾸는 것이 아니다.
        //10은 그대로 있고 10+1의 결과인 11이 만들어진다.
        //a가 11을 참조한다.
        System.out.println(a);

        //대입연산자 (+= -= *= /= %=)
        a=a+3;
        System.out.println(a); // 14
        a+=3; //증감대입연산자
        System.out.println(a); // 17
        a+=1; //== ++a // 18
        System.out.println(a);
        //a를 10 만들기
        a-=8;
        System.out.println(a);
        //*=으로 50 만들기
        // 후에 /=으로 1 만들기
        // 후에 %=으로 0 만들기
        a*=5;
        System.out.println(a);
        a/=50;
        System.out.println(a);
        a%=1;
        System.out.println(a);

        //증감연산자의 순서
        //증감연산자가 앞에(++a) 있으면 먼저 증가
        //ex) a=10, (++a*2)=22
        //증감연산자가 뒤에(a++) 있으면 다른 연산을 먼저하고 증가
        //ex) a=10, (a++*2)=20, a>11
        System.out.println(a++); //println()도 연산
        System.out.println(a);//println 먼저 하고 a를 0 > 1 증가시킴

        a=1;
        int result=++a*2; //4
        System.out.println(result);

        a=1;
        result=a++*2;
        System.out.println(result); //2
        System.out.println(a); //2


    }
}
